package com.shalhlad.product_delivery_service.security;

import com.shalhlad.product_delivery_service.security.filter.AuthenticationFilter;
import com.shalhlad.product_delivery_service.security.filter.AuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      AuthenticationManager authenticationManager) throws Exception {

    AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
    authenticationFilter.setFilterProcessesUrl(SecurityConstants.LOGIN_URL);

    AuthorizationFilter authorizationFilter = new AuthorizationFilter(authenticationManager);

    http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
        .antMatchers(HttpMethod.GET, "/products/**, /departments/**, /categories/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilter(authenticationFilter)
        .addFilter(authorizationFilter)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
