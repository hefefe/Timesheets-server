package com.mw.timesheets.configuration;

import com.mw.timesheets.commons.jwt.JwtFilter;
import com.mw.timesheets.domain.security.JwtService;
import com.mw.timesheets.domain.security.TokenBlacklistService;
import com.mw.timesheets.domain.security.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtService jwtService;
    private final UserDetailService userDetailService;
    private final TokenBlacklistService tokenBlacklistService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/v3/**", "/swagger-ui/**").permitAll())
                .addFilterBefore(new JwtFilter(jwtService, userDetailService, tokenBlacklistService), BasicAuthenticationFilter.class)
                .build();
    }
}
