package pe.com.lacunza.technix.util;

import org.apache.poi.ss.usermodel.*;
import pe.com.lacunza.technix.domain.entities.jpa.InventoryMovement;

import java.util.List;

public class ExcelUtils {

    public static void createSheetForMovementType(Workbook workbook, List<InventoryMovement> movements, String sheetName, String reportTitle) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);

        // Título del reporte
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(reportTitle);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 9));

        // Crear encabezados
        Row headerRow = sheet.createRow(2);
        String[] headers = {"ID", "Producto", "Cantidad", "Stock Anterior", "Stock Actual",
                "Proveedor", "Fecha de Creación", "Creado por", "Referencia", "Comentario"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 3;
        for (InventoryMovement movement : movements) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(movement.getId());
            row.createCell(1).setCellValue(movement.getProduct().getName());
            row.createCell(2).setCellValue(movement.getQuantity());
            row.createCell(3).setCellValue(movement.getPreviousStock());
            row.createCell(4).setCellValue(movement.getCurrentStock());
            row.createCell(5).setCellValue(movement.getSupplier() != null ? movement.getSupplier().getName() : "N/A");
            row.createCell(6).setCellValue(movement.getCreatedAt().format(InventaryConstants.DATE_FORMATTER));
            row.createCell(7).setCellValue(movement.getCreatedBy());
            row.createCell(8).setCellValue(movement.getReferenceNumber() != null ? movement.getReferenceNumber() : "None");
            row.createCell(9).setCellValue(movement.getComment() != null ? movement.getComment() : "");
        }

        // Ajustar ancho de columnas automáticamente
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
