package com.assistudy.userservice.oauth.service;

import com.assistudy.userservice.converter.UserConverter;
import com.assistudy.userservice.entity.User;
import com.assistudy.userservice.exception.AuthErrorCode;
import com.assistudy.userservice.exception.AuthException;
import com.assistudy.userservice.oauth.PrincipalDetails;
import com.assistudy.userservice.oauth.userInfo.KakaoUserInfo;
import com.assistudy.userservice.oauth.userInfo.NaverUserInfo;
import com.assistudy.userservice.oauth.userInfo.OAuth2UserInfo;
import com.assistudy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // 소셜로그인 유저 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo = getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        // 기존 정보가 있으면 로그인, 없는 경우 임시 회원가입
        User user = saveOrUpdateUser(userInfo);

        // OAuth2User 반환
        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        try {
            return switch (provider) {
                case "kakao" -> new KakaoUserInfo(attributes);
                case "naver" -> new NaverUserInfo((Map<String, Object>) attributes.get("response"));
                default -> throw new AuthException(AuthErrorCode.INVALID_PROVIDER);
            };
        } catch (AuthException e) {
            log.error("OAuth 사용자 정보 처리 중 AuthException 발생: {}", e.getMessage());

            OAuth2Error error = new OAuth2Error(
                e.getCode().getCode(),
                e.getCode().getMessage(),
                null
            );
            throw new OAuth2AuthenticationException(error, e.getCode().getMessage(), e);
        }
    }

    private User saveOrUpdateUser(OAuth2UserInfo userInfo) {
        String provider = userInfo.getProvider();
        String providerId = userInfo.getProviderId();
        String email = userInfo.getEmail();

        try {
            Optional<User> existingUserByEmail = userRepository.findByEmail(email);
            if (existingUserByEmail.isPresent()) {
                User existingUser = existingUserByEmail.get();
                if (existingUser.getProvider() == null || !existingUser.getProvider().equals(provider)) {
                    throw new AuthException(AuthErrorCode.DIFFERENT_PROVIDER_EXISTS);
                }
            }

            Optional<User> optionalUser = userRepository.findByProviderAndProviderId(provider, providerId);

            if (optionalUser.isEmpty()) {
                String password = UUID.randomUUID().toString();
                Integer randomProfileImg = (int)(Math.random() * 5) + 1;
                User user = UserConverter.toTempSoicalUser(provider, providerId, email, password, randomProfileImg);
                return userRepository.save(user);
            }

            return optionalUser.get();
        } catch (AuthException e) {
            log.error("OAuth 사용자 처리 중 AuthException 발생: {}", e.getMessage());

            OAuth2Error error = new OAuth2Error(
                e.getCode().getCode(),
                e.getCode().getMessage(),
                null
            );
            throw new OAuth2AuthenticationException(error, e.getCode().getMessage(), e);
        } catch (Exception e) {
            log.error("OAuth 사용자 처리 중 예상치 못한 예외 발생: {}", e.getMessage());

            OAuth2Error error = new OAuth2Error(
                "oauth_error",
                "OAuth 처리 중 오류가 발생했습니다.",
                null
            );
            throw new OAuth2AuthenticationException(error, "OAuth 처리 중 오류가 발생했습니다.", e);
        }
    }


}
