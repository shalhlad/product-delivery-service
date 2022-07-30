package com.shalhlad.product_delivery_service.exception.handling;

import java.util.Date;
import lombok.Data;

@Data
public class ErrorResponse {

  private int code;
  private String status;
  private String message;
  private Date timestamp;
}
