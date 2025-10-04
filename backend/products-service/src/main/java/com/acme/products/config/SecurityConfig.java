package com.acme.products.config;

import com.acme.products.security.ApiKeyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/swagger-ui.html",
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/actuator/health"
        ).permitAll()
        .anyRequest().authenticated()
      )
      // nuestro filtro de API Key antes del auth filter por defecto
      .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
