package ca.yw.maplekiosk.model.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "shop")
@Getter
public class Shop {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long shopId;

  private String shopName;

  private String password;

  private String apiKey;

  private Shop(String shopName, String password, String apiKey) {
    this.shopName = shopName;
    this.password = password;
    this.apiKey = apiKey;
  }

  public static Shop createShop(String shopName, String password, String apiKey) {
    return new Shop(shopName, password, apiKey);
  }
}
