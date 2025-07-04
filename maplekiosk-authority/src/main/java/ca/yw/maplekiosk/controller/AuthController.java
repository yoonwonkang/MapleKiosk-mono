package ca.yw.maplekiosk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.service.AuthIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthIntegrationService authService;

  @PostMapping("/login/{type}")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, @PathVariable String type) {
    return ResponseEntity.ok(authService.login(type, loginRequest));
  }
}
