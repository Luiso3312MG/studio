package com.kbseed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // archivos públicos
                .requestMatchers(
                        "/",
                        "/login.html",
                        "/favicon.ico",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                ).permitAll()

                // permitir LOGIN específicamente por POST
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // permitir preflight de CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // todo lo demás requiere sesión
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
