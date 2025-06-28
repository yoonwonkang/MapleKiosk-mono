package ca.yw.maplekiosk.service;

import org.springframework.stereotype.Service;

import ca.yw.maplekiosk.dto.http.Response;
import ca.yw.maplekiosk.dto.http.Request.LoginRequest;
import ca.yw.maplekiosk.dto.http.Response.LoginResponse;
import ca.yw.maplekiosk.enums.TokenType;
import ca.yw.maplekiosk.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {
    // dummy validate check
    if (!loginRequest.getUsername().equals("shop01") || !loginRequest.getPassword().equals("password")) {
      throw new IllegalArgumentException("Invalid username or password");
    }

    String accessToken = jwtTokenProvider.generateAccessToken(loginRequest.getUsername(), TokenType.SHOP);
    String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getUsername(), TokenType.SHOP);

    return new Response.LoginResponse(accessToken, refreshToken);
  }

}
