package com.oleg.educationalplatform.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors()
                .disable()
                .csrf()
                .disable()
                .authorizeRequests()
                //.authorizeHttpRequests()
                /*.anyRequest()
                .permitAll()*/
                .requestMatchers("/api/v1/auth/check-token", "/api/v1/auth/authenticate", "/pages/**", "/websocket/**", "/webjars/**", "/login", "/abc")
                .permitAll()
                .requestMatchers("/api/v1/auth/register/teacher")
                .hasAuthority("ADMIN")
                .requestMatchers("/api/v1/auth/register/student")
                .hasAnyAuthority("ADMIN", "TEACHER")
                .anyRequest()
                .authenticated()
                .and()
                .requiresChannel().requestMatchers("").requiresSecure()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
    }
}
