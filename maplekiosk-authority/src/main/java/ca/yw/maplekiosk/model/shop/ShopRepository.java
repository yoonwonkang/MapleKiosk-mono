package ca.yw.maplekiosk.model.shop;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {

  Optional<Shop> findByShopName(String shopName);

}
