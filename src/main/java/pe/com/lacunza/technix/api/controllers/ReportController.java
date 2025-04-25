package pe.com.lacunza.technix.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.lacunza.technix.services.InventoryReportService;
import pe.com.lacunza.technix.util.InventaryConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
@Slf4j
@Tag(name = "Inventory Reports", description = "Endpoints that generate reports to inventary movements")
public class ReportController {

    private final InventoryReportService inventoryReportService;

    @Operation(summary = "Generate report by date range")
    @GetMapping("/by-date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateReportByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        byte[] reportContent = inventoryReportService.generateReportByDateRange(startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory-report-by-date.xlsx")
                .contentType(MediaType.parseMediaType(InventaryConstants.headerXlsx))
                .body(reportContent);
    }

    @Operation(summary = "Generate report by user creator")
    @GetMapping("/by-creator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateReportByCreator(
            @RequestParam String createdBy) {

        byte[] reportContent = inventoryReportService.generateReportByCreator(createdBy);

        String encodedFileName = "inventory-report-by-" + createdBy + ".xlsx";
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
        String encodedFileNameForUrl = URLEncoder.encode(encodedFileName, StandardCharsets.UTF_8)
                .replace("+", "%20"); // reemplaza el + por %20 para representar espacios correctamente
        contentDisposition += "; filename*=UTF-8''" + encodedFileNameForUrl;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.parseMediaType(InventaryConstants.headerXlsx))
                .body(reportContent);
    }

    @Operation(summary = "Generate report by product mencionated")
    @GetMapping("/by-product")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<byte[]> generateReportByProductName(
            @RequestParam String productName) {

        byte[] reportContent = inventoryReportService.generateReportByProductName(productName);

        String encodedFileName = "inventory-report-by-" + productName + ".xlsx";
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
        String encodedFileNameForUrl = URLEncoder.encode(encodedFileName, StandardCharsets.UTF_8)
                .replace("+", "%20"); // reemplaza el + por %20 para representar espacios correctamente
        contentDisposition += "; filename*=UTF-8''" + encodedFileNameForUrl;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.parseMediaType(InventaryConstants.headerXlsx))
                .body(reportContent);
    }
}
