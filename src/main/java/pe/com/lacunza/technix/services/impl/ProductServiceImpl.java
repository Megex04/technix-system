package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.api.models.response.ProductLowStockRes;
import pe.com.lacunza.technix.api.models.response.ProductResponse;
import pe.com.lacunza.technix.domain.entities.jpa.Category;
import pe.com.lacunza.technix.domain.entities.jpa.Product;
import pe.com.lacunza.technix.domain.entities.jpa.Supplier;
import pe.com.lacunza.technix.domain.repositories.jpa.AlertConfigurationRepository;
import pe.com.lacunza.technix.domain.repositories.jpa.CategoryRepository;
import pe.com.lacunza.technix.domain.repositories.jpa.ProductRepository;
import pe.com.lacunza.technix.domain.repositories.jpa.SupplierRepository;
import pe.com.lacunza.technix.dtos.ProductDto;
import pe.com.lacunza.technix.services.ProductService;
import pe.com.lacunza.technix.api.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final AlertConfigurationRepository alertConfigurationRepository;

    // @Value("${product.lowstock.threshold:10}")
    //private int lowStockThreshold = 10;

    @Override
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductServiceImpl::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse findProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductServiceImpl::toProductResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductResponse saveProduct(ProductDto product) {
        Category categoryToAdd = categoryRepository.findById(Long.valueOf(product.getCategoryId()))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + product.getCategoryId()));

        Supplier supplierToAdd = supplierRepository.findById(Long.valueOf(product.getSupplierId()))
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + product.getSupplierId()));

        Product productToSave = fromDtotoProduct(product);
        productToSave.setCategory(categoryToAdd);
        productToSave.setSupplier(supplierToAdd);
        productToSave.setCreatedAt(LocalDateTime.now());
        productToSave.setUpdatedAt(null);
        Product savedProduct = productRepository.save(productToSave);
        return toProductResponse(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductDto productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(productDetails.getName());
        if(Objects.nonNull(productDetails.getDescription())) {
            product.setDescription(productDetails.getDescription());
        }
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());

        Category categoryToUpdate = categoryRepository.findById(Long.valueOf(productDetails.getCategoryId()))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDetails.getCategoryId()));

        Supplier supplierToUpdate = supplierRepository.findById(Long.valueOf(productDetails.getSupplierId()))
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + productDetails.getSupplierId()));

        product.setCategory(categoryToUpdate);
        product.setSupplier(supplierToUpdate);
        product.setUpdatedAt(LocalDateTime.now());

        Product productUpdated = productRepository.save(product);
        return toProductResponse(productUpdated);
    }


    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
                .stream()
                .map(ProductServiceImpl::toProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> findProductsByCategory(String categoryName) {
        return productRepository.findByNameContainingIgnoreCase(categoryName).stream()
                .map(ProductServiceImpl::toProductResponse)
                .toList();
    }

    @Override
    public List<ProductLowStockRes> findLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(ProductServiceImpl::toProductLowStock)
                .toList();
    }

    @Override
    public Boolean findLowStockProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if(productOptional.isPresent()) {
            alertConfigurationRepository.findByProductId(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Alert configuration not found for product with id: " + id));
            return productRepository.isStockLow(id);
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
    }

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id (product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory().getName())
                .supplierName(product.getSupplier().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
    public static Product fromDtotoProduct(ProductDto productDto) {
        return Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stockQuantity(productDto.getStockQuantity())
                .build();
    }
    public static ProductLowStockRes toProductLowStock(Product product) {
        return ProductLowStockRes.builder()
                .id(product.getId())
                .name(product.getName())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
