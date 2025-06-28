package ca.yw.maplekiosk.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ca.yw.maplekiosk.dto.http.Response;
import ca.yw.maplekiosk.exception.JwtTokenException;

@RestControllerAdvice
public class GlobarExceptionHandler {

  private static final Logger log = LogManager.getLogger(GlobarExceptionHandler.class);

  @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<?> handleJwtTokenException(JwtTokenException e) {
        log.error("JWT Exception: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new Response.ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }
}
