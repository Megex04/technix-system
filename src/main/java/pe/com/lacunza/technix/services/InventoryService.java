package pe.com.lacunza.technix.services;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.api.models.request.StockAdjustRequest;
import pe.com.lacunza.technix.api.models.request.StockInRequest;
import pe.com.lacunza.technix.api.models.request.StockOutRequest;
import pe.com.lacunza.technix.dtos.InventoryMovementDto;
import pe.com.lacunza.technix.util.SortType;

public interface InventoryService {
    Page<InventoryMovementDto> getAllMovements(Integer page, Integer size, SortType sortType, String fieldSorted);

    @Transactional
    InventoryMovementDto registerStockIn(String jwtUser, StockInRequest request);

    @Transactional
    InventoryMovementDto registerStockOut(String jwtUser, StockOutRequest request);

    @Transactional
    InventoryMovementDto adjustInventory(String jwtUser, StockAdjustRequest request);
}
