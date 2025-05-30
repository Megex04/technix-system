package pe.com.lacunza.technix.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Size(min = 3, max = 20, message = "Name must be at least 12 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be at least 1.00 sol")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 3, message = "Stock must be at least 3")
    private Integer stockQuantity;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    @NotNull(message = "Category is required")
    private Integer supplierId;
}
