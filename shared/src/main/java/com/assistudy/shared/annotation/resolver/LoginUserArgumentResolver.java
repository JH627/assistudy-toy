package com.assistudy.shared.annotation.resolver;

import com.assistudy.shared.annotation.LoginUser;
import com.assistudy.shared.constants.HeaderConstants;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        String headerValue = webRequest.getHeader(HeaderConstants.USER_ID_HEADER);

        if (headerValue == null) {
            throw new MissingRequestHeaderException(HeaderConstants.USER_ID_HEADER, parameter);
        }

        return Long.parseLong(headerValue);
    }
}
