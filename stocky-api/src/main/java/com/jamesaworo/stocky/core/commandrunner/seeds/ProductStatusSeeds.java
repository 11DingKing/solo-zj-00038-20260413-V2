package com.jamesaworo.stocky.core.commandrunner.seeds;

import com.jamesaworo.stocky.features.product.domain.entity.ProductStatus;

import java.util.List;

public class ProductStatusSeeds {

    public static final String LOW_STOCK_STATUS_TITLE = "LOW_STOCK";
    public static final String LOW_STOCK_STATUS_DESCRIPTION = "Low stock alert status - automatically set when inventory falls below threshold";

    public static final List<ProductStatus> DEFAULT_PRODUCT_STATUSES = List.of(
            createLowStockStatus()
    );

    private static ProductStatus createLowStockStatus() {
        ProductStatus status = new ProductStatus();
        status.setTitle(LOW_STOCK_STATUS_TITLE);
        status.setDescription(LOW_STOCK_STATUS_DESCRIPTION);
        status.setIsActiveStatus(true);
        return status;
    }
}
