package pe.com.lacunza.technix.dtos.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDto {
    @NotBlank(message = "Name product is required")
    @Size(min = 3, max = 12, message = "Name must be at least 12 characters")
    private String name;

    private String description;

    @NotBlank(message = "Price is required")
    private BigDecimal price;

    @NotBlank(message = "Stock is required")
    private Integer stockQuantity;

    @NotBlank(message = "Category is required")
    private Integer categoryId;

    @NotBlank(message = "Category is required")
    private Integer supplierId;
}
