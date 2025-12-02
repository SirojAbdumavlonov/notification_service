package uzumtech.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uzumtech.notification.entity.Price;

import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Optional<Price> findByIsActiveTrue();
}
