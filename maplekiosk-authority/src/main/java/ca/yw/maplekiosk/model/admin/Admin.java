package ca.yw.maplekiosk.model.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "admin")
@Getter
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

  private Admin(String adminName, String password, String role) {
    this.adminName = adminName;
    this.password = password;
    this.role = role;
  }

  public static Admin createAdmin(String adminName, String password, String role) {
    // can add validation
    if(role == null) {
      role = "ROLE_ADMIN";
    }
    return new Admin(adminName, password, role);
  }
}