package com.jamesaworo.stocky.core.commandrunner.seeders;

import com.jamesaworo.stocky.features.product.data.repository.ProductStatusRepository;
import com.jamesaworo.stocky.features.product.domain.entity.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jamesaworo.stocky.core.commandrunner.seeds.ProductStatusSeeds.DEFAULT_PRODUCT_STATUSES;
import static com.jamesaworo.stocky.core.commandrunner.seeds.ProductStatusSeeds.LOW_STOCK_STATUS_TITLE;

@Component
@RequiredArgsConstructor
public class ProductStatusSeeder {

    private final ProductStatusRepository productStatusRepository;

    public void run() {
        this.seedDefaultProductStatuses();
    }

    private void seedDefaultProductStatuses() {
        DEFAULT_PRODUCT_STATUSES.forEach(this::seedStatusIfNotExist);
    }

    private void seedStatusIfNotExist(ProductStatus status) {
        Optional<ProductStatus> existing = this.productStatusRepository.findByTitleEqualsIgnoreCase(status.getTitle());
        if (existing.isEmpty()) {
            this.productStatusRepository.save(status);
            System.out.println("----- seed product status: " + status.getTitle() + " -----");
        }
    }

    public Optional<ProductStatus> getLowStockStatus() {
        return this.productStatusRepository.findByTitleEqualsIgnoreCase(LOW_STOCK_STATUS_TITLE);
    }
}
