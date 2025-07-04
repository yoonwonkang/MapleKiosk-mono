package ca.yw.maplekiosk.provider;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import ca.yw.maplekiosk.config.JwtConfig;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.TokenType;
import ca.yw.maplekiosk.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {

  private static final Logger log = LogManager.getLogger(JwtTokenProvider.class);

  private final JwtConfig jwtConfig;

  private static final Integer MILLISECONDS_PER_SECOND = 1000;

  private final Key key;

  public JwtTokenProvider(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
    this.key = initKey();
  }

  private Key initKey() {
    return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String username, TokenType tokenType, long expirationSeconds) {
    return Jwts.builder()
        .setSubject(username)
        .claim("role", tokenType.name())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * MILLISECONDS_PER_SECOND))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
}

  public String generateAccessToken(String username, TokenType tokenType) {
    return generateToken(username, tokenType, jwtConfig.getAccessTokenExpirationSeconds());
  }

  public String generateRefreshToken(String username, TokenType tokenType) {
    return generateToken(username, tokenType, jwtConfig.getRefreshTokenExpirationSeconds());
  }

  public void validateToken(String token) {
    try {
      Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token);
    } catch (ExpiredJwtException e) {
        throw new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.TOKEN_EXPIRED, e);
    } catch (MalformedJwtException | SignatureException e) {
      throw new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TOKEN, e);
    } catch (Exception e) {
      throw new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.UNKNOWN_ERROR, e);    }
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(key).build()
      .parseClaimsJws(token).getBody();
  }
}
