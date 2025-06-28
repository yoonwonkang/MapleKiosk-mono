package ca.yw.maplekiosk.exception;

import ca.yw.maplekiosk.enums.ErrorCode;
import lombok.Getter;


@Getter
public class JwtTokenException extends RuntimeException {
  private final ErrorCode errorCode;

  public JwtTokenException(ErrorCode errorCode, Throwable cause) {
      super(errorCode.getMessage(), cause);
      this.errorCode = errorCode;
  }
}