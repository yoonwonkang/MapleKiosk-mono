package ca.yw.maplekiosk.model.admin;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class AdminRepositoryTest {
  @Autowired
  private AdminRepository adminRepository;

  @Test
  @DisplayName("admin save and find")
  void saveAndFindByAdminName() {
    String name = "test";
    String password = "test";

    Admin admin = Admin.builder().adminName(name).password(password).build();
    adminRepository.save(admin);

    Optional<Admin> foundOne = adminRepository.findByAdminName(name);

    assertThat(foundOne).isPresent();
    assertThat(foundOne.get().getAdminName()).isEqualTo(name);
    assertThat(foundOne.get().getPassword()).isEqualTo(password);
  }

  @Test
  @DisplayName("find fail admin - empty admin")
  void findByAdminName_shouldReturnEmptyWhenNotFound() {
    String notExistAdminName = "never";
    Optional<Admin> foundOne = adminRepository.findByAdminName(notExistAdminName);
    assertThat(foundOne).isEmpty();
  }

  @Test
  @DisplayName("admin update test")
  void updateAdminPassword() {
    String adminName = "test";
    String oldPassword = "old";
    String newPassword = "new";

    Admin admin = adminRepository.save(Admin.builder().adminName(adminName).password(oldPassword).build());

    admin.setPassword(newPassword);
    adminRepository.save(admin);

    Optional<Admin> foundOne = adminRepository.findByAdminName(adminName);
    assertThat(foundOne).isPresent();
    assertThat(foundOne.get().getPassword()).isEqualTo(newPassword);
  }
}
