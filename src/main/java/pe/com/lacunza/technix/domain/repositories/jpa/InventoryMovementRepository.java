package pe.com.lacunza.technix.domain.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.lacunza.technix.domain.entities.jpa.InventoryMovement;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByProductId(Long productId);
    List<InventoryMovement> findByUserId(String userId);
}
