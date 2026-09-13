package com.technerd.finsight.security.securityconfig;

import com.technerd.finsight.security.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final String[] PUBLIC_ROUTE = {"/auth/**", "/error"};

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http){
      return  http.authorizeHttpRequests(requests -> requests

                .requestMatchers(PUBLIC_ROUTE).permitAll()
                .anyRequest().authenticated())

              .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable).build();
    }
}
