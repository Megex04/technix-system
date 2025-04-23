package pe.com.lacunza.technix.domain.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.com.lacunza.technix.domain.entities.jpa.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    List<Product> findByCategoryName(String name);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByName(String categoryName);

    @Query("SELECT p FROM Product p JOIN AlertConfiguration ac ON ac.product = p " +
            "WHERE p.stockQuantity <= ac.minimumStock")
    List<Product> findLowStockProducts();

    @Query("SELECT CASE WHEN p.stockQuantity <= ac.minimumStock THEN TRUE ELSE FALSE END " +
            "FROM Product p JOIN AlertConfiguration ac ON ac.product = p " +
            "WHERE p.id = :productId")
    Boolean isStockLow(@Param("productId") Long productId);
}
