package pe.com.lacunza.technix.api.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "New quantity is required")
    @Min(value = 0, message = "New quantity must not be negative")
    private Integer newQuantity;

    @NotNull(message = "Reason is required")
    private String reason;

}
