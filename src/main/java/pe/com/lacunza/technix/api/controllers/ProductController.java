package pe.com.lacunza.technix.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Obtain all list of products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtain a product by id")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    @Operation(summary = "Create a new product")
    @ApiResponse(
            responseCode = "400",
            description = "When the request and his field contains a invalid data to save new product"
    )
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductDto product) {
        ProductResponse savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product by data validation to change")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDetails) {
        ProductResponse updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by id")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name or description")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String query) {
        List<ProductResponse> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}")
    @Operation(summary = "Get products by category name")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductResponse> products = productService.findProductsByCategory(categoryName);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/all-low-stock")
    @Operation(summary = "Get all products with low stock and send email if necessary")
    public ResponseEntity<List<ProductLowStockRes>> getLowStockProducts() {
        List<ProductLowStockRes> products = productService.findLowStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}/stock")
    @Operation(summary = "Get a product by id and check if it has low stock, if low stock send email to alert this")
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
