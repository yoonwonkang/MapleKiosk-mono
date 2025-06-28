package ca.yw.maplekiosk.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
  TOKEN_EXPIRED("This token is an expired"),
  INVALID_TOKEN("This token is not valid"),
  UNKNOWN_ERROR("An unexpected error occured token verification ");

  private final String message;

  ErrorCode(String message) {
      this.message = message;
  }
}
