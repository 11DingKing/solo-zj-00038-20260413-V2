package com.jamesaworo.stocky.features.notification.domain.service;

import com.jamesaworo.stocky.core.commandrunner.seeders.ProductStatusSeeder;
import com.jamesaworo.stocky.features.notification.domain.entity.SystemNotification;
import com.jamesaworo.stocky.features.notification.domain.enums.SystemNotificationType;
import com.jamesaworo.stocky.features.notification.domain.usecase.ISystemNotificationUsecase;
import com.jamesaworo.stocky.features.product.domain.entity.ProductBasic;
import com.jamesaworo.stocky.features.product.domain.entity.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockAlertService {

    private final ISystemNotificationUsecase notificationUsecase;
    private final ProductStatusSeeder productStatusSeeder;

    public void checkAndHandleLowStockAlert(ProductBasic basic) {
        if (basic.getLowStockPoint() == null || basic.getQuantity() == null) {
            return;
        }

        boolean isLowStock = basic.getQuantity() <= basic.getLowStockPoint();
        Optional<ProductStatus> lowStockStatusOpt = productStatusSeeder.getLowStockStatus();

        if (lowStockStatusOpt.isEmpty()) {
            log.warn("LOW_STOCK status not found in database");
            return;
        }

        ProductStatus lowStockStatus = lowStockStatusOpt.get();
        boolean currentIsLowStock = basic.getStatus() != null && basic.getStatus().getId().equals(lowStockStatus.getId());

        if (isLowStock && !currentIsLowStock) {
            handleStockStatusChangeToLow(basic, lowStockStatus);
        } else if (!isLowStock && currentIsLowStock) {
            handleStockStatusRestore(basic, lowStockStatus);
        }
    }

    private void handleStockStatusChangeToLow(ProductBasic basic, ProductStatus lowStockStatus) {
        basic.setOriginalStatus(basic.getStatus());
        basic.setStatus(lowStockStatus);

        String title = "Low Stock Alert: " + basic.getProductName();
        String message = String.format(
                "Product '%s' (SKU: %s) has fallen below the low stock threshold. " +
                        "Current quantity: %d, Low stock point: %d, Shortage: %d",
                basic.getProductName(),
                basic.getSku() != null ? basic.getSku() : "N/A",
                basic.getQuantity(),
                basic.getLowStockPoint(),
                basic.getLowStockPoint() - basic.getQuantity()
        );

        createNotification(basic, SystemNotificationType.LOW_STOCK_ALERT, title, message);
        log.info("Low stock alert created for product: {}", basic.getProductName());
    }

    private void handleStockStatusRestore(ProductBasic basic, ProductStatus lowStockStatus) {
        if (basic.getOriginalStatus() != null) {
            basic.setStatus(basic.getOriginalStatus());
            basic.setOriginalStatus(null);
        } else {
            basic.setStatus(null);
        }

        String title = "Stock Restored: " + basic.getProductName();
        String message = String.format(
                "Product '%s' (SKU: %s) stock has been restored above the low stock threshold. " +
                        "Current quantity: %d, Low stock point: %d",
                basic.getProductName(),
                basic.getSku() != null ? basic.getSku() : "N/A",
                basic.getQuantity(),
                basic.getLowStockPoint()
        );

        createNotification(basic, SystemNotificationType.STOCK_RESTORED, title, message);
        log.info("Stock restored notification created for product: {}", basic.getProductName());
    }

    private void createNotification(ProductBasic basic, SystemNotificationType type, String title, String message) {
        SystemNotification notification = SystemNotification.builder()
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityId(basic.getId())
                .relatedEntityType("PRODUCT")
                .isRead(false)
                .build();

        notificationUsecase.save(notification);
    }
}
