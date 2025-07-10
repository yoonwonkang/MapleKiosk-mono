package ca.yw.maplekiosk.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.yw.maplekiosk.config.SpringSecurityConfig;
import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.dto.auth.response.RefreshResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.enums.RoleType;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.provider.JwtTokenProvider;
import ca.yw.maplekiosk.service.AuthIntegrationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(AuthController.class)
@Import(SpringSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class AuthControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthIntegrationService authService;

  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  private static final String USER_NAME = "shop01";
  private static final String USER_PASS = "shop-pass";
  private static final String TEST_ACCESS_TOKEN = "access-token";
  private static final String TEST_REFRESH_TOKEN = "refresh-token";

  private final ObjectMapper objectMapper = new ObjectMapper();

  MockMvc setUpNoFilterMockMvc() {
    return MockMvcBuilders.webAppContextSetup(context)
    .addFilters() // without filter
    .build();
  }

  @Test
  @DisplayName("fail_login_when_user_not_found")
  void login_fail_user_not_found() throws Exception {
    MockMvc withoutFilterMockMvc = setUpNoFilterMockMvc();
    // Give correct type
    String type = "KIOSK";
    // Given wrong user name
    LoginRequest loginRequest = new LoginRequest("invalidUser", "password");

    when(authService.login(eq(type), any(LoginRequest.class)))
        .thenThrow(new AuthException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

    // When & Then
    withoutFilterMockMvc.perform(post("/api/v1/auth/login/"+type)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isNotFound());
        // .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        // .andExpect(jsonPath("$.message").value("User not found"));

    verify(authService, times(1)).login(eq(type), any(LoginRequest.class));
  }

  @Test
  @DisplayName("fail_login_when_user_type_invalid")
  void login_fail_user_type_invalid() throws Exception {
    MockMvc withoutFilterMockMvc = setUpNoFilterMockMvc();
    // Give wrong type
    String wrongType = "WRONG";
    LoginRequest loginRequest = new LoginRequest("user", "password");

    when(authService.login(eq(wrongType), any(LoginRequest.class)))
        .thenThrow(new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_USER_TYPE));

    // When & Then
    withoutFilterMockMvc.perform(post("/api/v1/auth/login/"+wrongType)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest());
        // .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
        // .andExpect(jsonPath("$.message").value("User not found"));

    verify(authService, times(1)).login(eq(wrongType), any(LoginRequest.class));
  }

  @Test
  @DisplayName("fail_login_when_password_invalid")
  void login_fail_password_invalid() throws Exception {
    MockMvc withoutFilterMockMvc = setUpNoFilterMockMvc();
    // Given
    String type = "KIOSK";
    LoginRequest loginRequest = new LoginRequest("user", "wrongPassword");

    when(authService.login(eq(type), any(LoginRequest.class)))
    .thenThrow(new AuthException(HttpStatus.NOT_FOUND, ErrorCode.INVALID_PASSWORD));

    // When & Then
    withoutFilterMockMvc.perform(post("/api/v1/auth/login/"+type)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
    .andExpect(status().isNotFound());
    // .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
    // .andExpect(jsonPath("$.message").value("User not found"));

    verify(authService, times(1)).login(eq(type), any(LoginRequest.class));
  }

  @Test
  @DisplayName("success_login")
  void login_success() throws Exception {
    MockMvc withoutFilterMockMvc = setUpNoFilterMockMvc();
      // Given Valid Data
    String type = "KIOSK";
    LoginRequest loginRequest = new LoginRequest(USER_NAME, USER_PASS);
    LoginResponse loginResponse = LoginResponse.builder()
      .accessToken(TEST_ACCESS_TOKEN).refreshToken(TEST_REFRESH_TOKEN)
    .build();

    when(authService.login(eq(type), any(LoginRequest.class)))
        .thenReturn(loginResponse);

    // When expect success
    withoutFilterMockMvc.perform(post("/api/v1/auth/login/"+type)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.accessToken").value(TEST_ACCESS_TOKEN))
      .andExpect(jsonPath("$.refreshToken").value(TEST_REFRESH_TOKEN));

    verify(authService, times(1)).login(eq(type), any(LoginRequest.class));
  }

  @Test
  @DisplayName("fail_login_when_request_is_invalid")
  void login_fail_when_request_is_invalid() throws Exception {
    MockMvc withoutFilterMockMvc = setUpNoFilterMockMvc();
    // Given: empty user name;
    String type = "KIOSK";
    LoginRequest loginRequest = new LoginRequest("", USER_PASS);

    // When & Then: 요청 실패를 기대
    withoutFilterMockMvc.perform(post("/api/v1/auth/login/" + type)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))  // 글로벌 핸들러에서 적절히 설정
      .andExpect(jsonPath("$.message").exists());              // 에러 메시지가 있는지만 확인

    verify(authService, times(0)).login(any(), any());  // 아예 서비스 호출도 안 해야 함
  }

  @Test
  @DisplayName("logout_success_when_token_is_valid")
  void logout_success_when_token_is_valid() throws Exception {
    String token = "valid-token";

    when(jwtTokenProvider.resolveToken(any(HttpServletRequest.class))).thenReturn(token);

    doNothing().when(authService).logout(any(HttpServletRequest.class)); // optional

    mockMvc.perform(post("/api/v1/auth/logout")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk());

    verify(authService, times(1)).logout(any(HttpServletRequest.class));
  }

  @Test
  @DisplayName("logout_fail_when_token_is_invalid")
  void logout_fail_when_token_is_invalid() throws Exception {
    String invalidToken = "invalid-token";
    // given: resolveToken은 토큰을 추출해줌
    when(jwtTokenProvider.resolveToken(any(HttpServletRequest.class)))
      .thenReturn(invalidToken);

    // given: validateToken에서 예외 발생 (유효하지 않은 토큰)
    doThrow(new AuthException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN))
      .when(jwtTokenProvider).validateToken(invalidToken);

    mockMvc.perform(post("/api/v1/auth/logout")
        .header("Authorization", "Bearer " + invalidToken))
    .andExpect(status().isUnauthorized());
    // .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));

    verify(jwtTokenProvider, times(1)).validateToken(invalidToken);
    verify(authService, times(0)).logout(any()); // ✅ 서비스 호출은 없어야 함
  }
  @Test
  @DisplayName("success_when_refresh_token_is_valid")
  void success_when_refresh_token_is_valid() throws Exception {
    String refreshToken = "valid-refresh-token";
    String newAccessToken = "new-access-token";
    String id = "123";

    // given: resolveToken 은 요청에서 토큰 추출
    when(jwtTokenProvider.resolveToken(any(HttpServletRequest.class)))
      .thenReturn(refreshToken);

    // given: validateToken은 예외 없이 성공
    doNothing().when(jwtTokenProvider).validateToken(refreshToken);

       // given: resolveToken 은 요청에서 토큰 추출
    when(authService.refresh(any(HttpServletRequest.class)))
    .thenReturn(RefreshResponse.builder().accessToken(newAccessToken).build());

    // given: getClaims는 유효한 Refresh Token의 Claims 반환
    Claims claims = Jwts.claims().setSubject("testUser");
    claims.setExpiration(Date.from(Instant.now().plusSeconds(3600)));
    claims.setId(id);
    claims.put("tokenType", "REFRESH");
    claims.put("role", "KIOSK");

    when(jwtTokenProvider.getClaims(refreshToken)).thenReturn(claims);

    // given: 새로운 Access Token 생성
    when(jwtTokenProvider.generateAccessToken(Long.valueOf(id), "testUser", RoleType.KIOSK))
        .thenReturn(newAccessToken);

    // when & then
    mockMvc.perform(post("/api/v1/auth/refresh")
            .header("Authorization", "Bearer " + refreshToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(newAccessToken));

    verify(jwtTokenProvider, times(1)).validateToken(refreshToken);
    verify(jwtTokenProvider, times(1)).getClaims(refreshToken);
  }
}
