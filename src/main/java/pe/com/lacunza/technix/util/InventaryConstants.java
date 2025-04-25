package pe.com.lacunza.technix.util;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InventaryConstants {

    public static final String headerXlsx = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static final List<String> newPermissionsAdmin = List.of(
            "ALL_PERMISSIONS"
    );

    public static final List<String> newPermissionsSupervisor = List.of(
            "CATEGORY_READ",
            "CATEGORY_CREATE",
            "CATEGORY_UPDATE",
            "CATEGORY_DELETE",
            "SUPPLIER_READ",
            "SUPPLIER_UPDATE",
            "INVENTORY_READ",
            "INVENTORY_ADJUST",
            "PRODUCT_CREATE",
            "REPORT_INVENTORY",
            "USER_READ",
            "ALERT_READ",
            "ALERT_CREATE",
            "ALERT_UPDATE",
            "ALERT_DELETE"
    );

    public static final List<String> newPermissionsEmployee = List.of(
            "PRODUCT_READ",
            "PRODUCT_UPDATE",
            "PRODUCT_DELETE",
            "CATEGORY_READ",
            "SUPPLIER_READ",
            "INVENTORY_READ",
            "INVENTORY_IN",
            "INVENTORY_OUT",
            "ALERT_READ"
    );
}
