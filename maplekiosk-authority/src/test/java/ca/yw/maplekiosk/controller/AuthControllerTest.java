package ca.yw.maplekiosk.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.yw.maplekiosk.dto.auth.request.LoginRequest;
import ca.yw.maplekiosk.dto.auth.response.LoginResponse;
import ca.yw.maplekiosk.enums.ErrorCode;
import ca.yw.maplekiosk.exception.AuthException;
import ca.yw.maplekiosk.provider.JwtTokenProvider;
import ca.yw.maplekiosk.service.AuthIntegrationService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
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

  @Test
  @DisplayName("fail_login_when_user_not_found")
  void login_fail_user_not_found() throws Exception {
    // Give correct type
    String type = "KIOSK";
    // Given wrong user name
    LoginRequest loginRequest = new LoginRequest("invalidUser", "password");

    when(authService.login(eq(type), any(LoginRequest.class)))
        .thenThrow(new AuthException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

    // When & Then
    mockMvc.perform(post("/api/v1/auth/login/"+type)
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
    // Give wrong type
    String wrongType = "WRONG";
    LoginRequest loginRequest = new LoginRequest("user", "password");

    when(authService.login(eq(wrongType), any(LoginRequest.class)))
        .thenThrow(new AuthException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_USER_TYPE));

    // When & Then
    mockMvc.perform(post("/api/v1/auth/login/"+wrongType)
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
    // Given
    String type = "KIOSK";
    LoginRequest loginRequest = new LoginRequest("user", "wrongPassword");

    when(authService.login(eq(type), any(LoginRequest.class)))
    .thenThrow(new AuthException(HttpStatus.NOT_FOUND, ErrorCode.INVALID_PASSWORD));

    // When & Then
    mockMvc.perform(post("/api/v1/auth/login/"+type)
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
      // Given Valid Data
      String type = "KIOSK";
      LoginRequest loginRequest = new LoginRequest(USER_NAME, USER_PASS);
      LoginResponse loginResponse = LoginResponse.builder()
        .accessToken(TEST_ACCESS_TOKEN).refreshToken(TEST_REFRESH_TOKEN)
      .build();

      when(authService.login(eq(type), any(LoginRequest.class)))
          .thenReturn(loginResponse);

      // When expect success
      mockMvc.perform(post("/api/v1/auth/login/"+type)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(loginRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.accessToken").value(TEST_ACCESS_TOKEN))
              .andExpect(jsonPath("$.refreshToken").value(TEST_REFRESH_TOKEN));

      verify(authService, times(1)).login(eq(type), any(LoginRequest.class));
  }
}
