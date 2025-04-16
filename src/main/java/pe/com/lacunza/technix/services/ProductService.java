package pe.com.lacunza.technix.services;

import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.api.models.response.ProductLowStockRes;
import pe.com.lacunza.technix.api.models.response.ProductResponse;
import pe.com.lacunza.technix.domain.entities.jpa.Product;
import pe.com.lacunza.technix.dtos.product.ProductDto;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponse> findAllProducts();

    Optional<ProductResponse> findProductById(Long id);

    @Transactional
    ProductResponse saveProduct(ProductDto product);

    @Transactional
    ProductResponse updateProduct(Long id, ProductDto productDetails);

    @Transactional
    void deleteProduct(Long id);

    List<ProductResponse> searchProducts(String query);

    List<ProductResponse> findProductsByCategory(String categoryName);

    List<ProductLowStockRes> findLowStockProducts();
}
