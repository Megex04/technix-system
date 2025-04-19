package pe.com.lacunza.technix.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LowStockResponse {
    private Boolean isStockLow;
    private String message;
}
