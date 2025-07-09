package ca.yw.maplekiosk.service.login;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.RoleType;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.model.kiosk.Kiosk;
import ca.yw.maplekiosk.model.kiosk.KioskRepository;
import ca.yw.maplekiosk.provider.JwtTokenProvider;

@Service
public class KioskLoginService implements LoginService {

  private final KioskRepository kioskRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public KioskLoginService(KioskRepository kioskRepository, JwtTokenProvider jwtTokenProvider) {
    this.kioskRepository = kioskRepository;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    Kiosk kiosk = kioskRepository.findByKioskName(request.getUsername()).
      orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

    if (!kiosk.getPassword().equals(request.getPassword()))
      throw new AuthException(HttpStatus.NOT_FOUND, ErrorCode.INVALID_PASSWORD);

    String accessToken = jwtTokenProvider.generateAccessToken(kiosk.getKioskId(), request.getUsername(), RoleType.KIOSK);
    String refreshToken = jwtTokenProvider.generateRefreshToken(kiosk.getKioskId(), request.getUsername(), RoleType.KIOSK);

    return LoginResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .build();
  }

  @Override
  public boolean supports(String type) {
    return RoleType.KIOSK.name().equalsIgnoreCase(type);
  }

}
