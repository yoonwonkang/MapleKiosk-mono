package ca.yw.maplekiosk.model.token;

import java.time.LocalDateTime;

import ca.yw.maplekiosk.enums.RoleType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "token_black_list")
@Getter
public class TokenBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiredAt;

    private LocalDateTime createdAt;

    private String reason; // "logout", "manual", "compromised"

    @Enumerated(EnumType.STRING)
    private RoleType tokenType; // ACCESS, REFRESH

    private Long ownerId; // Optional: admin_id, shop_id 등

    private TokenBlackList(String token, LocalDateTime expiredAt, LocalDateTime createdAt, String reason, RoleType tokenType, Long ownerId) {
      this.token = token;
      this.expiredAt = expiredAt;
      this.createdAt = createdAt;
      this.reason = reason;
      this.tokenType = tokenType;
      this.ownerId = ownerId;
    }

    public static TokenBlackList createTokenBlackList(String token, LocalDateTime expiredAt, LocalDateTime createdAt, String reason, RoleType tokenType, Long ownerId) {
      return new TokenBlackList(token, expiredAt, createdAt, reason, tokenType, ownerId);
    }
}
