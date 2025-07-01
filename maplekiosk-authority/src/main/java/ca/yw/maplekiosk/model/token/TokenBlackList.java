package ca.yw.maplekiosk.model.token;

import java.time.LocalDateTime;

import ca.yw.maplekiosk.enums.TokenType;
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
@Builder
public class TokenBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiredAt;

    private LocalDateTime createdAt;

    private String reason; // "logout", "manual", "compromised"

    @Enumerated(EnumType.STRING)
    private TokenType tokenType; // ACCESS, REFRESH

    private Long ownerId; // Optional: admin_id, shop_id 등
}
