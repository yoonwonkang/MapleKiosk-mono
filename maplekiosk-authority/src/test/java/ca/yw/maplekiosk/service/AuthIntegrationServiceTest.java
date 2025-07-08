package ca.yw.maplekiosk.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.service.login.LoginService;

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
}