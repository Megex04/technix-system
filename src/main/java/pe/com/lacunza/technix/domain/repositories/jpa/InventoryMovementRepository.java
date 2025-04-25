package pe.com.lacunza.technix.domain.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.com.lacunza.technix.domain.entities.jpa.InventoryMovement;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByProductId(Long productId);
    List<InventoryMovement> findByUserId(String userId);
    List<InventoryMovement> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<InventoryMovement> findByCreatedBy(String createdBy);

    @Query("SELECT im FROM InventoryMovement im " +
            "JOIN im.product p WHERE LOWER(p.name) " +
            "LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<InventoryMovement> findByProductNameContainingIgnoreCase(@Param("productName") String productName);
}
