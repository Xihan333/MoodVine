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
        if(token == null){
            return null; // No token provided, return null user
        }
        token = jwtUtil.extractTokenFromHeader(token);
        
        String identifier = jwtUtil.getSubjectFromToken(token);
        if (identifier == null) {
            return null; // Invalid token or no subject
        }

        // Try to find user by email first (for regular users)
        User user = userMapper.findByEmail(identifier);
        if (user == null) {
            // If not found by email, try by open_id (for WeChat users)
            user = userMapper.findByOpenId(identifier);
        }
        
        return user;
    }
}
