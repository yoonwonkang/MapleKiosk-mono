package ca.yw.maplekiosk.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.service.login.LoginService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthIntegrationService {
private final List<LoginService> loginServices;

  public LoginResponse login(String type, LoginRequest request) {
    LoginService service = loginServices.stream()
        .filter(s -> s.supports(type))
        .findFirst()
        .orElseThrow(() -> new AuthException(HttpStatus.BAD_REQUEST ,ErrorCode.INVALID_USER_TYPE));

    return service.login(request);
  }
}
