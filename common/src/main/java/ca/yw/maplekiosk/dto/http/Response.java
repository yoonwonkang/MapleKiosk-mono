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

  @Getter
  @AllArgsConstructor
  public class LoginResponse {
    private String accessToken;
    private String refreshToken;
  }
}
