package ca.yw.maplekiosk.model.kiosk;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KioskRepository extends JpaRepository<Kiosk, Long> {

  Optional<Kiosk> findByKioskName(String kioskName);

}
