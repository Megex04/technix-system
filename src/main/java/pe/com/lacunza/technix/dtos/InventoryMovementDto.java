package pe.com.lacunza.technix.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovementDto {
    private Long id;
    private ProductDto product;
    private String movementType; // IN, OUT, ADJUSTMENT
    private Integer quantity;
    private Integer previousStock;
    private Integer currentStock;
    private String userId;
    private SupplierDto supplier; // Opcional, para movimientos de entrada
    private LocalDateTime createdAt;
    private String createdBy;
    private String referenceNumber; // Número de factura, orden, etc.
    private String reason;
}
