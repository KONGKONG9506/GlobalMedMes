package com.globalmed.mes.mes_api.config;

import com.globalmed.mes.mes_api.user.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf->csrf.disable() )
                .headers(headers->headers.frameOptions(
                        frame->frame.disable()
                ))
                .sessionManagement(
                        sess->sess.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )
                .authorizeHttpRequests(
                        auth->auth
                                .requestMatchers(
                                        "/swagger-ui/**","/v3/api-docs/**",
                                        //위의 2개는 swagger 관련
                                        "/auth/**",
                                        "/h2-console", "/h2-console/**"
                                ).permitAll().anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler) // 403을 401로 변환
                )
                .addFilterBefore(
                        new JwtLoginFilter(jwtProvider),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
}