package com.cyf.tom.suit.middleware;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class XcxMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new XcxInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/admin/doLogin");
    }

}
