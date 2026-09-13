package com.technerd.finsight.security.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final String[] PUBLIC_ROUTE = {"/auth/**", "/error"};

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http){
      return  http.authorizeHttpRequests(requests -> requests

                .requestMatchers(PUBLIC_ROUTE).permitAll()
                .anyRequest().authenticated())

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable).build();
    }
}
