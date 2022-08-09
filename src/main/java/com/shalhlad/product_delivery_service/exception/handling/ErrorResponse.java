package com.shalhlad.product_delivery_service.exception.handling;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class ErrorResponse {

  private int code;
  private String status;
  private String message;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date timestamp;
}
