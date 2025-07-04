package ca.yw.maplekiosk.service.login;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.TokenType;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.model.kiosk.Kiosk;
import ca.yw.maplekiosk.model.kiosk.KioskRepository;
import ca.yw.maplekiosk.provider.JwtTokenProvider;

public class KioskLoginServiceTest {

  private KioskRepository kioskRepository;
  private JwtTokenProvider jwtTokenProvider;
  private KioskLoginService kioskLoginService;

  @BeforeEach
  void setUp() {
    kioskRepository = mock(KioskRepository.class);
    jwtTokenProvider = mock(JwtTokenProvider.class);
    kioskLoginService = new KioskLoginService(kioskRepository, jwtTokenProvider);
  }

  @Test
  @DisplayName("fail_login_when_user_not_found")
  void login_fail_when_user_not_found() {
    //Given wrong user name
    String wrongUserName = "unknown";
    LoginRequest request = new LoginRequest(wrongUserName, "test");

    when(kioskRepository.findByKioskName(wrongUserName)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> kioskLoginService.login(request))
    .isInstanceOf(AuthException.class)
    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("fail_login_when_invalid_password")
  void login_fail_when_invalid_password() {
    //Given
    String username = "user";
    String wrongPassword = "wrongPass";
    LoginRequest request = new LoginRequest(username, "pass");

    Kiosk kiosk = Kiosk.createKiosk(null, username, wrongPassword);

    when(kioskRepository.findByKioskName(username)).thenReturn(Optional.of(kiosk));

    assertThatThrownBy(() -> kioskLoginService.login(request))
    .isInstanceOf(AuthException.class)
    .hasMessageContaining(ErrorCode.INVALID_PASSWORD.getMessage());
  }

  @Test
  @DisplayName("success_login")
  void success_login() {
    String username = "user";
    String password = "password";
    String accessToken = "access-token";
    String refreshToken = "refresh-token";

    LoginRequest request = new LoginRequest(username, password);
    Kiosk kiosk = Kiosk.createKiosk(null, username, password);

    when(kioskRepository.findByKioskName(username)).thenReturn(Optional.of(kiosk));
    when(jwtTokenProvider.generateAccessToken(username, TokenType.KIOSK)).thenReturn(accessToken);
    when(jwtTokenProvider.generateRefreshToken(username, TokenType.KIOSK)).thenReturn(refreshToken);

    // When
    LoginResponse response = kioskLoginService.login(request);

    // Then
    assertThat(response.getAccessToken()).isEqualTo(accessToken);
    assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

    verify(kioskRepository, times(1)).findByKioskName(username);
    verify(jwtTokenProvider, times(1)).generateAccessToken(username, TokenType.KIOSK);
    verify(jwtTokenProvider, times(1)).generateRefreshToken(username, TokenType.KIOSK);
  }

}
