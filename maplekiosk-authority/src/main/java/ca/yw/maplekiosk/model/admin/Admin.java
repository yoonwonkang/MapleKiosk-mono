package ca.yw.maplekiosk.model.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "admin")
@Getter
@Builder
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long adminId;

  private String adminName;

  private String password;

  private String role;

  public void setPassword(String newPassword) {
    this.password = newPassword;
  }

}