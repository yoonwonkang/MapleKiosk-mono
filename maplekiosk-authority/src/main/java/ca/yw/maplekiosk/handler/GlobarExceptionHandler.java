package ca.yw.maplekiosk.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ca.yw.maplekiosk.dto.common.ErrorResponse;
import ca.yw.maplekiosk.exception.JwtTokenException;
import ca.yw.maplekiosk.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobarExceptionHandler {

  private static final Logger log = LogManager.getLogger(GlobarExceptionHandler.class);

  @ExceptionHandler(JwtTokenException.class)
  public ResponseEntity<?> handleJwtTokenException(JwtTokenException e) {
    log.error("JWT Exception: {}", e.getMessage());
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
              ErrorResponse.builder()
              .code(e.getErrorCode().name())
              .message(e.getMessage())
              .build()
            );
  }
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
    log.error("User not found Exception: {}", e.getMessage());
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
              ErrorResponse.builder()
              .code(":USER_NOT_FOUND")
              .message(e.getMessage())
              .build()
            );
  }
}
