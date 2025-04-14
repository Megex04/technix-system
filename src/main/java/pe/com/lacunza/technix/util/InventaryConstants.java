package pe.com.lacunza.technix.util;

import java.util.List;

public class InventaryConstants {
    public static final List<String> newPermissionsAdmin = List.of(
            "ALL_PERMISSIONS"
    );

    public static final List<String> newPermissionsSupervisor = List.of(
            "CATEGORY_CREATE",
            "CATEGORY_READ",
            "CATEGORY_UPDATE",
            "CATEGORY_DELETE",
            "SUPPLIER_READ",
            "SUPPLIER_UPDATE",
            "ORDER_CREATE",
            "ORDER_READ",
            "ORDER_UPDATE",
            "ORDER_DELETE",
            "PRODUCT_APPROVE",
            "REPORT_INVENTORY",
            "REPORT_SALES",
            "REPORT_PURCHASE",
            "USER_READ"
    );

    public static final List<String> newPermissionsEmployee = List.of(
            "PRODUCT_READ",
            "PRODUCT_UPDATE",
            "INVENTORY_READ",
            "INVENTORY_UPDATE",
            "CATEGORY_READ",
            "SUPPLIER_READ",
            "ORDER_CREATE",
            "ORDER_READ",
            "ORDER_UPDATE",
            "REPORT_INVENTORY"
    );
}
