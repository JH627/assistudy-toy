package com.assistudy.userservice.oauth.service;

import com.assistudy.userservice.entity.User;
import com.assistudy.userservice.exception.UserErrorCode;
import com.assistudy.userservice.exception.UserException;
import com.assistudy.userservice.oauth.PrincipalDetails;
import com.assistudy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 일반 유저 로그인 처리 (이메일)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() ->
                new UserException(UserErrorCode.USER_NOT_FOUND));
        return new PrincipalDetails(user);
    }
}
