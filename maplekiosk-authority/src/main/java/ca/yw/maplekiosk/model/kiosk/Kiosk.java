package ca.yw.maplekiosk.model.kiosk;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "kiosk")
@Getter
public class Kiosk {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long kioskId;

  private Long shopId;

  private String kioskName;

  private String password;

  private Kiosk(Long shopId, String kioskName, String password) {
    this.shopId = shopId;
    this.kioskName = kioskName;
    this.password = password;
  }

  public static Kiosk createKiosk(Long shopId, String kioskName, String password) {
    return new Kiosk(shopId, kioskName, password);
  }
}
