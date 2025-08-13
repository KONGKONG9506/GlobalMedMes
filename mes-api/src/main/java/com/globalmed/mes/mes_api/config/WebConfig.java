package com.globalmed.mes.mes_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry
                        .addMapping("/**") //모든 API 경로 허용
                        .allowedOrigins("http://localhost:3000") // 이 Origin만 허용
                        .allowedMethods("*") // 모든 HTTP 메서드 허용 (GET, POST 등)
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(true); // 자격 증명 허용 (쿠키, Authorization 헤더 등)
            }
        };
    }
}