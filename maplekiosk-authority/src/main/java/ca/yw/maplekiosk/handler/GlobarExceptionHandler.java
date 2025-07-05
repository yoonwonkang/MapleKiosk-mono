package ca.yw.maplekiosk.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ca.yw.maplekiosk.dto.common.ErrorResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.exception.AuthException;


@RestControllerAdvice
public class GlobarExceptionHandler {

  private static final Logger log = LogManager.getLogger(GlobarExceptionHandler.class);

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<?> handleJwtTokenException(AuthException e) {
    log.error("JWT Exception: {}", e.getMessage());
    return ResponseEntity
      .status(e.getStatus())
      .body(
        ErrorResponse.builder()
        .code(e.getErrorCode().name())
        .message(e.getMessage())
        .build()
      );
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
      String message = ex.getMessage();
      return ResponseEntity.badRequest().body(ErrorResponse.builder().code(ErrorCode.INVALID_REQUEST.name()).message(message).build());
  }
}
