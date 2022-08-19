package com.shalhlad.product_delivery_service.security;

import com.shalhlad.product_delivery_service.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig {

  private final JwtFilter jwtFilter;
  private final AuthenticationEntryPoint authenticationEntryPoint;
  private final AccessDeniedHandler accessDeniedHandler;

  @Value("${apiPrefix}")
  private String apiPrefix;

  @Autowired
  public WebSecurityConfig(JwtFilter jwtFilter, AuthenticationEntryPoint authenticationEntryPoint,
      AccessDeniedHandler accessDeniedHandler) {
    this.jwtFilter = jwtFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.accessDeniedHandler = accessDeniedHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .csrf().disable()
        .authorizeRequests(authz -> authz
            .antMatchers(HttpMethod.POST,
                apiPrefix + "/auth/sign-in",
                apiPrefix + "/auth/sign-up").permitAll()
            .antMatchers(HttpMethod.GET,
                apiPrefix + "/products/**",
                apiPrefix + "/departments/**",
                apiPrefix + "/categories/**").permitAll()
            .antMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(handling -> handling
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
