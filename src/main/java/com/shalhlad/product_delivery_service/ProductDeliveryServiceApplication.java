package com.shalhlad.product_delivery_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:my_constants.properties")
public class ProductDeliveryServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductDeliveryServiceApplication.class, args);
  }

}
