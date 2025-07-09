package ca.yw.maplekiosk.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.dto.auth.response.RefreshResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.RoleType;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.model.token.TokenBlackList;
import ca.yw.maplekiosk.model.token.TokenBlackListRepository;
import ca.yw.maplekiosk.provider.JwtTokenProvider;
import ca.yw.maplekiosk.service.login.LoginService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthIntegrationService {

  private static final Logger log = LogManager.getLogger(AuthIntegrationService.class);

  private final List<LoginService> loginServices;
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenBlackListRepository tokenBlackListRepository;

  public LoginResponse login(String type, LoginRequest request) {
    LoginService service = loginServices.stream()
        .filter(s -> s.supports(type))
        .findFirst()
        .orElseThrow(() -> new AuthException(HttpStatus.BAD_REQUEST ,ErrorCode.INVALID_USER_TYPE));

    return service.login(request);
  }

  public void logout(HttpServletRequest request) {
    String token = jwtTokenProvider.resolveToken(request);
    jwtTokenProvider.validateToken(token); // 유효성 검사
    Claims claims = jwtTokenProvider.getClaims(token);

    Long userId = Optional.ofNullable(claims.getId())
    .map(Long::valueOf)
    .orElseThrow(() -> new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TOKEN));

    tokenBlackListRepository.save(
    TokenBlackList.createTokenBlackList(
      token,
      claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
      LocalDateTime.now(), null, RoleType.fromString(claims.get("role").toString()), userId));
  }

  public RefreshResponse refresh(HttpServletRequest request) {
    String token = jwtTokenProvider.resolveToken(request);
    jwtTokenProvider.validateToken(token);
    Claims claims = jwtTokenProvider.getClaims(token);

    // 토큰 타입 확인
    if (!claims.get("tokenType").equals("REFRESH"))
        throw new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TOKEN);

    // 블랙리스트 확인 생략 or 적용

    String username = claims.getSubject();
    String type = claims.get("role").toString();
    Long userId = Optional.ofNullable(claims.getId())
      .map(Long::valueOf)
      .orElseThrow(() -> new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TOKEN));

    String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, RoleType.fromString(type));

    return new RefreshResponse(newAccessToken);
  }
}
