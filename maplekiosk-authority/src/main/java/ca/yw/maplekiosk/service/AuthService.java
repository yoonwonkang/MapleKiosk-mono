package ca.yw.maplekiosk.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.TokenType;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {
    // dummy validate check
    if (!loginRequest.getUsername().equals("shop01") || !loginRequest.getPassword().equals("password")) {
      throw new AuthException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    String accessToken = jwtTokenProvider.generateAccessToken(loginRequest.getUsername(), TokenType.SHOP);
    String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getUsername(), TokenType.SHOP);

    return LoginResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .build();
  }

}
