package pe.com.lacunza.technix.domain.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.lacunza.technix.domain.entities.jpa.AlertConfiguration;

import java.util.Optional;

@Repository
public interface AlertConfigurationRepository extends JpaRepository<AlertConfiguration, Long> {
    Optional<AlertConfiguration> findByProductId(Long productId);
}
