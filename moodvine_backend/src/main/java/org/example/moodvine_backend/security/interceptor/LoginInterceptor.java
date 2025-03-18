package org.example.moodvine_backend.security.interceptor;


import org.example.moodvine_backend.mapper.UserMapper;
import org.example.moodvine_backend.model.PO.UserType;
import org.example.moodvine_backend.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    UserMapper userMapper;

    @Autowired
    JwtUtil jwtUtil;

    //仅管理员能访问的请求
    private final String[] FOR_ADMIN = {
            "/admin"
    };


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        System.out.println("进入preHandle！！！！！！！！！！！！！！！！！");
        String requestURI = request.getRequestURI();
        System.out.println(request.getMethod().toString());

        if (requestURI.contains("/login")) {
            //System.out.println("login request! pass");
            return true;
        }

        // 获取请求中的Token
        String token = request.getHeader("Authorization");
        //去掉"Bearer "
        if(token == null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        token = jwtUtil.extractTokenFromHeader(token);
        System.out.println("Authorization:   "+ token);
        // 模拟Token验证，这里假设Token合法性校验方法为isValidToken
        if (!jwtUtil.validateToken(token)) {
            // Token验证失败，重定向到登录页面或其他错误页面
            // 当你使用 response.sendRedirect("/api/auth/login") 方法时，它会向客户端浏览器发送一个临时重定向响应，
            // 指示浏览器向指定的 URL 发起一个新的请求。这个新的请求将会是一个 GET 请求。
            System.out.println("false!!!!!");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        if(Arrays.stream(FOR_ADMIN).anyMatch(requestURI::startsWith)) {
            System.out.println(jwtUtil.getUserTypeFromToken(token) );
            if (jwtUtil.getUserTypeFromToken(token) != UserType.ADMIN) {
                System.out.println("false!!!!!");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        //Token验证通过，继续处理请求
        System.out.println("Token验证通过,已经成功通过两层过滤器!");
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在处理请求后执行的操作，可留空
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在完成请求后执行的操作，可留空
    }
}


