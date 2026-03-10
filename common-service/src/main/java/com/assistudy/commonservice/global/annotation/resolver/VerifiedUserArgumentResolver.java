package com.assistudy.commonservice.global.annotation.resolver;

import com.assistudy.commonservice.global.annotation.VerifiedUser;
import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.GeneralErrorCode;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class VerifiedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectProvider<UserServiceClient> userServiceClientProvider;

    public VerifiedUserArgumentResolver(ObjectProvider<UserServiceClient> userServiceClientProvider) {
        this.userServiceClientProvider = userServiceClientProvider;
    }

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String AUTH_HEADER = "X-Access-Token";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(VerifiedUser.class)
            && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) throws Exception {

        String userIdHeader = webRequest.getHeader(USER_ID_HEADER);
        String token = webRequest.getHeader(AUTH_HEADER);

        if (userIdHeader == null || token == null) {
            throw new MissingRequestHeaderException(USER_ID_HEADER + " or Authorization", parameter);
        }

        // User-Service 에 블랙리스트 확인 요청
        boolean isBlacklisted = userServiceClientProvider.getObject().checkUserToken(token).getResult();
        if (isBlacklisted) {
            throw new CustomException(GeneralErrorCode.INVALID_USER_AUTH);
        }

        return Long.parseLong(userIdHeader);
    }
}