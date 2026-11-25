package com.example.rescatevial.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor auth;
    private final String[] origenes;

    public WebConfig(AuthInterceptor auth, @Value("${app.cors.origenes}") String[] origenes) {
        this.auth = auth;
        this.origenes = origenes;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auth).addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/registro", "/api/health", "/api/health/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOriginPatterns(origenes)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*");
    }
}
