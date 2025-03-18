package org.example.moodvine_backend.Resolver;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.mapper.UserMapper;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
                parameter.getParameterType().isAssignableFrom(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                  NativeWebRequest request, WebDataBinderFactory factory) {
        // header中获取用户token

        // 获取请求中的Token
        String token = request.getHeader("Authorization");
        //去掉"Bearer "
        token = jwtUtil.extractTokenFromHeader(token);

        String email = jwtUtil.getEmailFromToken(token);
        return userMapper.findByEmail(email);
    }
}
