package ca.yw.maplekiosk.exception;

import ca.yw.maplekiosk.enums.ErrorCode;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
  }
}
