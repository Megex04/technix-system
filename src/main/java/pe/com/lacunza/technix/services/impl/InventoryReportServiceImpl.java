package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.domain.entities.jpa.InventoryMovement;
import pe.com.lacunza.technix.domain.repositories.jpa.InventoryMovementRepository;
import pe.com.lacunza.technix.services.InventoryReportService;
import pe.com.lacunza.technix.util.ExcelUtils;
import pe.com.lacunza.technix.util.InventaryConstants;
import pe.com.lacunza.technix.util.MovementType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryMovementRepository inventoryMovementRepository;

    @Override
    public byte[] generateReportByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generando reporte por rango de fechas: {} - {}", startDate, endDate);
        List<InventoryMovement> movements = inventoryMovementRepository.findByCreatedAtBetween(startDate, endDate);
        return generateExcelReport(movements, "Reporte por Fechas: " + startDate.format(InventaryConstants.DATE_FORMATTER) + " - " + endDate.format(InventaryConstants.DATE_FORMATTER));
    }

    @Override
    public byte[] generateReportByCreator(String createdBy) {
        log.info("Generando reporte por creador: {}", createdBy);
        List<InventoryMovement> movements = inventoryMovementRepository.findByCreatedBy(createdBy);
        return generateExcelReport(movements, "Reporte por Creador: " + createdBy);
    }

    @Override
    public byte[] generateReportByProductName(String productName) {
        log.info("Generando reporte por producto: {}", productName);
        List<InventoryMovement> movements = inventoryMovementRepository.findByProductNameContainingIgnoreCase(productName);
        return generateExcelReport(movements, "Reporte por Producto: " + productName);
    }

    private byte[] generateExcelReport(List<InventoryMovement> movements, String reportTitle) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Agrupar movimientos por tipo
            Map<MovementType, List<InventoryMovement>> movementsByType = movements.stream()
                    .collect(Collectors.groupingBy(InventoryMovement::getType));

            // Crear una pestaña para cada tipo de movimiento
            ExcelUtils.createSheetForMovementType(workbook, movementsByType.getOrDefault(MovementType.ENTRY, List.of()), "ENTRADA", reportTitle);
            ExcelUtils.createSheetForMovementType(workbook, movementsByType.getOrDefault(MovementType.EXIT, List.of()), "SALIDA", reportTitle);
            ExcelUtils.createSheetForMovementType(workbook, movementsByType.getOrDefault(MovementType.ADJUSTMENT, List.of()), "AJUSTE", reportTitle);

            // Convertir el libro a un array de bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error generando el reporte Excel", e);
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }
}
