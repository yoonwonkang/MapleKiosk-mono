package ca.yw.maplekiosk.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.yw.maplekiosk.dto.common.ErrorResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.provider.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LogManager.getLogger(JwtAuthenticationFilter.class);
  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
    System.out.println("Injected JwtTokenProvider: " + jwtTokenProvider);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = jwtTokenProvider.resolveToken(request);
    try{

      if (token != null) {
        jwtTokenProvider.validateToken(token);
        Claims claims = jwtTokenProvider.getClaims(token);

        UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
        return;
      }
      throw new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_TOKEN);
    } catch (AuthException e) {
      response.setStatus(e.getStatus().value());
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getWriter().write(new ObjectMapper().writeValueAsString(
          ErrorResponse.builder()
            .code(e.getErrorCode().name())
            .message(e.getErrorCode().getMessage())
            .build()
        ));
      response.getWriter().flush();
    }
  }
}