package ca.yw.maplekiosk.dto.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Request {

  @Getter
  @Setter
  @AllArgsConstructor
  public class LoginRequest {
    private String username;
    private String password;
  }
}
