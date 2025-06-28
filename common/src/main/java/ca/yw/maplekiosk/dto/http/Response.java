package ca.yw.maplekiosk.dto.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Response {

  @Getter
  @AllArgsConstructor
  public
  static class ErrorResponse {
    private String code;
    private String message;
  }
}
