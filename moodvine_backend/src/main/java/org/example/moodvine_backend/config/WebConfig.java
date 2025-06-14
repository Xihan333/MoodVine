package org.example.moodvine_backend.config;


import org.example.moodvine_backend.Resolver.CustomMethodArgumentResolver;
import org.example.moodvine_backend.Resolver.LoginUserHandlerMethodArgumentResolver;
import org.example.moodvine_backend.security.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginInterceptor)
//                .addPathPatterns("/user/**","/pet/**","/like/**","/comment/**","/admin/**","/comment/**", "/good/**","/like/**", "/purchase-records/**") // 添加需要拦截的路径规则
//                .excludePathPatterns("/user/login","/user/register","/user/hello","/user/test","/user/sendEmail"); // 排除不需要拦截的路径
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/user/**") // 添加需要拦截的路径规则
                .excludePathPatterns("/user/login","/user/register","/user/hello","/user/test","/user/sendEmail","/user/diary/addDiary","/user/addScore","/chat/**"); // 排除不需要拦截的路径
    }


    @Resource
    private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomMethodArgumentResolver());
        resolvers.add(loginUserHandlerMethodArgumentResolver);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        //配置资源映射：设置虚拟路径，访问绝对路径下资源：访问 http://localhost:8080/view/images/xxx.png访问D:/view/Photos/下的资源
//        registry.addResourceHandler("/picture/**") //虚拟路径
//                .addResourceLocations("file:" + "D:/Files/Picture/pets/"); //绝对路径
//    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

}
