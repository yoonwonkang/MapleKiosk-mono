package ca.yw.maplekiosk.exception;

import org.springframework.http.HttpStatus;

import ca.yw.maplekiosk.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends MapleKioskException {
  private final HttpStatus status;

  public AuthException(HttpStatus status, ErrorCode errorCode, Exception e) {
    super(errorCode, e);
    this.status = status;
  }

  public AuthException(HttpStatus status, ErrorCode errorCode) {
    super(errorCode);
    this.status = status;
  }
}
