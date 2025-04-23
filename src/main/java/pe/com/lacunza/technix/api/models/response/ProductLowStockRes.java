package pe.com.lacunza.technix.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductLowStockRes {
    private Long id;

    private String name;

    private Integer stockQuantity;

    private String categoryName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
