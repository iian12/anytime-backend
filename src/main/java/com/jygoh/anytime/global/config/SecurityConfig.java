package com.jygoh.anytime.global.config;

import com.jygoh.anytime.global.handler.CustomAccessDeniedHandler;
import com.jygoh.anytime.global.handler.JwtAuthenticationEntryPoint;
import com.jygoh.anytime.global.handler.CustomOAuth2SuccessHandler;
import com.jygoh.anytime.global.security.auth.service.CustomOAuth2UserService;
import com.jygoh.anytime.global.security.auth.service.CustomUserDetailsService;
import com.jygoh.anytime.global.security.jwt.filter.JwtTokenFilter;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;


    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
        CustomUserDetailsService userDetailsService, CustomOAuth2UserService customOAuth2UserService,
        CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(
                authorizeRequests -> authorizeRequests.requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/member/register").permitAll()
                    .requestMatchers("/api/ws/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                    .requestMatchers("/rss/**").permitAll().requestMatchers("/login/**").permitAll()
                    .anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2.successHandler(customOAuth2SuccessHandler)
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
            .exceptionHandling(configurer -> {
                configurer.authenticationEntryPoint(new JwtAuthenticationEntryPoint()); // 추가
                configurer.accessDeniedHandler(new CustomAccessDeniedHandler());       // 추가
            })
            .addFilterAfter(jwtTokenFilter(), OAuth2LoginAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider, userDetailsService);
    }
}
