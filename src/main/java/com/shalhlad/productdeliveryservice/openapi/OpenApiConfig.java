package com.shalhlad.productdeliveryservice.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  private static final String securitySchemeName = "Bearer Authentication";

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Product delivery service REST API")
            .description("REST API for product delivery service")
            .version("1.0")
            .contact(contact()))
        .components(new Components()
            .addSecuritySchemes(securitySchemeName, securityScheme()));
  }

  private Contact contact() {
    return new Contact()
        .email("shakhlad26@gmail.com")
        .name("Kiryl")
        .url("https://github.com/shalhlad");
  }

  private SecurityScheme securityScheme() {
    return new SecurityScheme()
        .name(securitySchemeName)
        .type(Type.HTTP)
        .bearerFormat("JWT")
        .scheme("bearer");
  }


}
