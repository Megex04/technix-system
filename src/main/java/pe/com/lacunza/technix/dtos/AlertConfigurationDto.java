package pe.com.lacunza.technix.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AlertConfigurationDto {

    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo debe ser mayor o igual a 0")
    private Integer minimumStock;
}