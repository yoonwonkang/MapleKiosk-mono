package ca.yw.maplekiosk.service.login;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;

public interface LoginService {
  LoginResponse login(LoginRequest request);
  boolean supports(String type); // who can support
}