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
import ca.yw.maplekiosk.model.shop.Shop;
import ca.yw.maplekiosk.model.shop.ShopRepository;
import ca.yw.maplekiosk.provider.JwtTokenProvider;

public class ShopLoginServiceTest {

  private ShopRepository shopRepository;
  private JwtTokenProvider jwtTokenProvider;
  private ShopLoginService shopLoginService;

  @BeforeEach
  void setUp() {
    shopRepository = mock(ShopRepository.class);
    jwtTokenProvider = mock(JwtTokenProvider.class);
    shopLoginService = new ShopLoginService(shopRepository, jwtTokenProvider);
  }

  @Test
  @DisplayName("fail_login_when_user_not_found")
  void login_fail_when_user_not_found() {
    //Given wrong user name
    String wrongUserName = "unknown";
    LoginRequest request = new LoginRequest(wrongUserName, "test");

    when(shopRepository.findByShopName(wrongUserName)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> shopLoginService.login(request))
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

    Shop shop = Shop.createShop(null, username, wrongPassword);

    when(shopRepository.findByShopName(username)).thenReturn(Optional.of(shop));

    assertThatThrownBy(() -> shopLoginService.login(request))
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
    Shop shop = Shop.createShop(null, username, password);

    when(shopRepository.findByShopName(username)).thenReturn(Optional.of(shop));
    when(jwtTokenProvider.generateAccessToken(username, TokenType.KIOSK)).thenReturn(accessToken);
    when(jwtTokenProvider.generateRefreshToken(username, TokenType.KIOSK)).thenReturn(refreshToken);

    // When
    LoginResponse response = shopLoginService.login(request);

    // Then
    assertThat(response.getAccessToken()).isEqualTo(accessToken);
    assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

    verify(shopRepository, times(1)).findByShopName(username);
    verify(jwtTokenProvider, times(1)).generateAccessToken(username, TokenType.KIOSK);
    verify(jwtTokenProvider, times(1)).generateRefreshToken(username, TokenType.KIOSK);
  }

}