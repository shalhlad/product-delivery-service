package com.shalhlad.product_delivery_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class AccessTokenResponse {

  private String token;

}
