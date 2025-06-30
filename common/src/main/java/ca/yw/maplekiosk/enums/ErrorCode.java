package ca.yw.maplekiosk.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND("AUTH", "001", "사용자를 찾을 수 없습니다."),
  INVALID_PASSWORD("AUTH", "002", "비밀번호가 일치하지 않습니다."),
  TOKEN_EXPIRED("AUTH", "003", "토큰이 만료되었습니다."),
  INVALID_TOKEN("AUTH", "004", "토큰이 유효하지 않습니다."),
  UNKNOWN_ERROR("AUTH", "005", "확인이 되지 않는 에러입니다.");

  private final String service;
  private final String code;
  private final String message;

  ErrorCode(String service, String code, String message) {
    this.service = service;
    this.code = code;
    this.message = message;
  }
  public String getFullCode() {
      return service + "-" + code;
  }
}
