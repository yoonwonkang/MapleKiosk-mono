package ca.yw.maplekiosk.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.TokenType;
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
      LocalDateTime.now(), null, TokenType.fromString(claims.get("role").toString()), userId));
  }
}
