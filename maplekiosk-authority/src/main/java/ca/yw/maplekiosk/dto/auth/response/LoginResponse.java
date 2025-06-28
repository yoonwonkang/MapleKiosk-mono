package ca.yw.maplekiosk.dto.auth.response;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
}
