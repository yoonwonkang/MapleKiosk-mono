package ca.yw.maplekiosk.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.dto.auth.response.RefreshResponse;
import ca.yw.maplekiosk.service.AuthIntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LogManager.getLogger(AuthController.class);

  private final AuthIntegrationService authService;

  @PostMapping("/login/{type}")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, @PathVariable String type) {
    return ResponseEntity.ok(authService.login(type, loginRequest));
  }

  @PostMapping("logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    authService.logout(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<RefreshResponse> refreshToken(HttpServletRequest request) {
    return ResponseEntity.ok(authService.refresh(request));
  }
}
