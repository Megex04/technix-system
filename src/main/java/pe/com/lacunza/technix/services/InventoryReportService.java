package pe.com.lacunza.technix.services;

import java.time.LocalDateTime;

public interface InventoryReportService {
    byte[] generateReportByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    byte[] generateReportByCreator(String createdBy);

    byte[] generateReportByProductName(String productName);
}
