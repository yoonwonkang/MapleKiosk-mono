package ca.yw.maplekiosk.service.login;

import org.springframework.http.HttpStatus;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.RoleType;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.model.shop.Shop;
import ca.yw.maplekiosk.model.shop.ShopRepository;
import ca.yw.maplekiosk.provider.JwtTokenProvider;

public class ShopLoginService implements LoginService {
  
  private final ShopRepository shopRepository;
  private final JwtTokenProvider jwtTokenProvider;

    public ShopLoginService(ShopRepository shopRepository, JwtTokenProvider jwtTokenProvider) {
    this.shopRepository = shopRepository;
    this.jwtTokenProvider = jwtTokenProvider;
  }

		@Override
		public LoginResponse login(LoginRequest request) {
    Shop shop = shopRepository.findByShopName(request.getUsername()).
      orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

    if (!shop.getPassword().equals(request.getPassword()))
      throw new AuthException(HttpStatus.NOT_FOUND, ErrorCode.INVALID_PASSWORD);

    String accessToken = jwtTokenProvider.generateAccessToken(shop.getShopId(), request.getUsername(), RoleType.SHOP);
    String refreshToken = jwtTokenProvider.generateRefreshToken(shop.getShopId(), request.getUsername(), RoleType.SHOP);

    return LoginResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .build();
		}

		@Override
		public boolean supports(String type) {
      return RoleType.SHOP.name().equalsIgnoreCase(type);
		}
}
