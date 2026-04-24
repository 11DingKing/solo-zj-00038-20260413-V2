package com.jamesaworo.stocky.features.notification.domain.enums;

public enum SystemNotificationType {
    LOW_STOCK_ALERT("Low Stock Alert"),
    STOCK_RESTORED("Stock Restored"),
    EXPIRY_ALERT("Expiry Alert"),
    GENERAL("General");

    private final String displayName;

    SystemNotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
