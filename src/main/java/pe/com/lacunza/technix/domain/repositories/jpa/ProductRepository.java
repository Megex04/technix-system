package pe.com.lacunza.technix.domain.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.com.lacunza.technix.domain.entities.jpa.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByName(String categoryName);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= " +
            "(SELECT ac.minimumStock FROM AlertConfiguration ac WHERE ac.product = p)")
    List<Product> findLowStockProducts();
}
