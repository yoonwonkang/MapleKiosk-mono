package ca.yw.maplekiosk.provider;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import ca.yw.maplekiosk.config.JwtConfig;
import ca.yw.maplekiosk.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final Logger log = LogManager.getLogger(JwtTokenProvider.class);

  private final JwtConfig jwtConfig;

  private final Integer milliSeconds = 1000;

  private final Key key;

  public JwtTokenProvider(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
    this.key = initKey();
  }

  private Key initKey() {
    return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String createAccessToken(String username, TokenType tokenType) {
    return Jwts.builder()
      .setSubject(username)
      .claim("role", tokenType)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpirationSeconds() * milliSeconds))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public String createRefreshToken(String username, TokenType tokenType) {
    return Jwts.builder()
      .setSubject(username)
      .claim("role", tokenType)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpirationSeconds() * milliSeconds))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public boolean validateToken(String token) {
    try {

        Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token);
        return true;
    } catch (io.jsonwebtoken.security.SignatureException e) {
      // 서명이 잘못된 경우
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      // 토큰이 만료된 경우
      log.error("Expired JWT token: {}", e.getMessage());
    } catch (io.jsonwebtoken.MalformedJwtException e) {
      // 토큰 형식이 잘못된 경우
      log.error("Invalid JWT token format: {}", e.getMessage());
    } catch (io.jsonwebtoken.UnsupportedJwtException e) {
      // 지원하지 않는 토큰
      log.error("Unsupported JWT token: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      // Claims가 비어있는 경우
      log.error("JWT claims string is empty: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error while validating JWT token: {}", e.getMessage());
    }
    return false;
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(key).build()
      .parseClaimsJws(token).getBody();
  }
}
