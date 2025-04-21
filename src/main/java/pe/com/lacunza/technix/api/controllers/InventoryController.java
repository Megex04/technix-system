package pe.com.lacunza.technix.api.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.com.lacunza.technix.api.models.request.StockAdjustRequest;
import pe.com.lacunza.technix.api.models.request.StockInRequest;
import pe.com.lacunza.technix.api.models.request.StockOutRequest;
import pe.com.lacunza.technix.dtos.InventoryMovementDto;
import pe.com.lacunza.technix.services.InventoryService;
import pe.com.lacunza.technix.util.SortType;

import java.util.Objects;

@RestController
@RequestMapping("/api/inventory")
@AllArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/movements")
    public ResponseEntity<Page<InventoryMovementDto>> getAllMovements(@RequestParam Integer page,
                                                                      @RequestParam Integer size,
                                                                      @RequestParam(required = false) SortType sortType,
                                                                      @RequestParam(required = false) String fieldSorted) {
        if(Objects.isNull(sortType)) sortType = SortType.NONE;
        if(Objects.isNull(fieldSorted)) fieldSorted = "createdAt";
        Page<InventoryMovementDto> movements = inventoryService.getAllMovements(page, size, sortType, fieldSorted);
        return movements.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(movements);
    }

    @PostMapping("/in")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<InventoryMovementDto> registerStockIn(@RequestHeader("Authorization") String token, @Valid @RequestBody StockInRequest request) {
        String jwt = token.substring(7); // Remove "Bearer " prefijo
        InventoryMovementDto movement = inventoryService.registerStockIn(jwt, request);
        return new ResponseEntity<>(movement, HttpStatus.CREATED);
    }

    @PostMapping("/out")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<InventoryMovementDto> registerStockOut(@RequestHeader("Authorization") String token, @Valid @RequestBody StockOutRequest request) {
        String jwt = token.substring(7);
        InventoryMovementDto movement = inventoryService.registerStockOut(jwt, request);
        return new ResponseEntity<>(movement, HttpStatus.CREATED);
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<InventoryMovementDto> adjustInventory(@RequestHeader("Authorization") String token, @Valid @RequestBody StockAdjustRequest request) {
        String jwt = token.substring(7);
        InventoryMovementDto movement = inventoryService.adjustInventory(jwt, request);
        return new ResponseEntity<>(movement, HttpStatus.CREATED);
    }
}
