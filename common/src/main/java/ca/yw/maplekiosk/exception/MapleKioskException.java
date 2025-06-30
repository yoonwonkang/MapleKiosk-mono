package ca.yw.maplekiosk.exception;

import ca.yw.maplekiosk.enums.ErrorCode;

public class MapleKioskException extends RuntimeException {
  private final ErrorCode errorCode;

  public MapleKioskException(ErrorCode errorCode, Throwable cause) {
      super(errorCode.getMessage(), cause);
      this.errorCode = errorCode;
  }

  public MapleKioskException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }
}
