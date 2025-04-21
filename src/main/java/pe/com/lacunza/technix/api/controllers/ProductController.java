package pe.com.lacunza.technix.api.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.com.lacunza.technix.api.models.response.LowStockResponse;
import pe.com.lacunza.technix.api.models.response.ProductLowStockRes;
import pe.com.lacunza.technix.api.models.response.ProductResponse;
import pe.com.lacunza.technix.dtos.ProductDto;
import pe.com.lacunza.technix.services.ProductService;
import pe.com.lacunza.technix.api.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductDto product) {
        ProductResponse savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDetails) {
        ProductResponse updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    // probar de aqui para abajo estos endpoints (desde /search para abajo)
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String query) {
        List<ProductResponse> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductResponse> products = productService.findProductsByCategory(categoryName);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/all-low-stock")
    public ResponseEntity<List<ProductLowStockRes>> getLowStockProducts() {
        List<ProductLowStockRes> products = productService.findLowStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<LowStockResponse> getLowStockProductById(@PathVariable Long id) {
        Boolean lowStock = productService.findLowStockProductById(id);
        LowStockResponse validarStock;
        if(Boolean.TRUE.equals(lowStock)) {
            validarStock = new LowStockResponse(true, "El producto con id: "+ id +" tiene bajo stock (alerta!!)");
        } else {
            validarStock = new LowStockResponse(false, "El producto con id: "+ id +" tiene stock suficiente");
        }
        return new ResponseEntity<>(validarStock, HttpStatus.OK);
    }
}
