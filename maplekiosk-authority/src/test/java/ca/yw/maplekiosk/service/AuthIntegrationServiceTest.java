package ca.yw.maplekiosk.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.model.token.TokenBlackList;
import ca.yw.maplekiosk.model.token.TokenBlackListRepository;
import ca.yw.maplekiosk.provider.JwtTokenProvider;
import ca.yw.maplekiosk.service.login.LoginService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public class AuthIntegrationServiceTest {

  @Test
  @DisplayName("success_login_with_correct_service")
  void login_success_with_correct_service() {
    // Given
    String type = "KIOSK";
    LoginRequest loginRequest = new LoginRequest("user", "pass");

    // Mock KioskLoginService
    LoginService kioskLoginService = mock(LoginService.class);
    when(kioskLoginService.supports(type)).thenReturn(true);

    AuthIntegrationService authIntegrationService = new AuthIntegrationService(List.of(kioskLoginService), null, null);

    // When
    authIntegrationService.login(type, loginRequest);

    // Then
    verify(kioskLoginService, times(1)).login(loginRequest);
  }

  @Test
  @DisplayName("fail_login_with_invalid_user_type")
  void login_fail_with_invalid_user_type() {
    // Given
    String invalidType = "INVALID";
    LoginRequest loginRequest = new LoginRequest("user", "pass");

    // Mock KioskLoginService
    LoginService kioskLoginService = mock(LoginService.class);
    when(kioskLoginService.supports(anyString())).thenReturn(false);

    AuthIntegrationService authIntegrationService = new AuthIntegrationService(List.of(kioskLoginService), null, null);

    // Expect Exception
    assertThatThrownBy(() -> authIntegrationService.login(invalidType, loginRequest))
      .isInstanceOf(AuthException.class)
      .hasMessageContaining(ErrorCode.INVALID_USER_TYPE.getMessage());
  }

  @Test
  @DisplayName("logout_success")
  void logout_success() {
    JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    TokenBlackListRepository tokenBlackListRepository = mock(TokenBlackListRepository.class);
    AuthIntegrationService authIntegrationService = new AuthIntegrationService(null, jwtTokenProvider, tokenBlackListRepository);

    String token = "valid-token";
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    Claims claims = mock(Claims.class);

    when(jwtTokenProvider.resolveToken(mockRequest)).thenReturn(token);
    doNothing().when(jwtTokenProvider).validateToken(token);
    when(jwtTokenProvider.getClaims(token)).thenReturn(claims);

    when(claims.getExpiration()).thenReturn(Date.from(Instant.now().plusSeconds(3600)));
    when(claims.getId()).thenReturn("123");
    when(claims.get("role")).thenReturn("KIOSK");

    // When
    authIntegrationService.logout(mockRequest);

    // Then
    verify(jwtTokenProvider, times(1)).validateToken(token);
    verify(tokenBlackListRepository, times(1)).save(any(TokenBlackList.class));
  }


  @Test
  @DisplayName("logout_should_fail_when_token_is_invalid")
  void logout_should_fail_when_token_is_invalid() {
    JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    TokenBlackListRepository tokenBlackListRepository = mock(TokenBlackListRepository.class);
    AuthIntegrationService authIntegrationService = new AuthIntegrationService(null, jwtTokenProvider, tokenBlackListRepository);

    String token = "invalid-token";
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(jwtTokenProvider.resolveToken(request)).thenReturn(token);
    doThrow(new RuntimeException("Invalid Token")).when(jwtTokenProvider).validateToken(token);

    // Then
    assertThatThrownBy(() -> authIntegrationService.logout(request))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Invalid Token");

    verify(tokenBlackListRepository, never()).save(any());
  }
}