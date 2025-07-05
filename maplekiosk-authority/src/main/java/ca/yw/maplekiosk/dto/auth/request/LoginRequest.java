package ca.yw.maplekiosk.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
  @NotBlank(message = "Username must not be empty")
  private String username;
  @NotBlank(message = "Password must not be empty")
  private String password;
}
