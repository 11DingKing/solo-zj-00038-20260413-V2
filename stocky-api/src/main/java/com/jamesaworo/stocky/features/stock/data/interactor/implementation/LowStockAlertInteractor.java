package com.jamesaworo.stocky.features.stock.data.interactor.implementation;

import com.jamesaworo.stocky.core.annotations.Interactor;
import com.jamesaworo.stocky.features.notification.domain.service.StockAlertService;
import com.jamesaworo.stocky.features.product.data.repository.ProductRepository;
import com.jamesaworo.stocky.features.product.domain.entity.Product;
import com.jamesaworo.stocky.features.product.domain.entity.ProductBasic;
import com.jamesaworo.stocky.features.product.domain.usecase.IProductBasicUsecase;
import com.jamesaworo.stocky.features.product.domain.usecase.IProductUsecase;
import com.jamesaworo.stocky.features.stock.data.interactor.contract.ILowStockAlertInteractor;
import com.jamesaworo.stocky.features.stock.data.request.BatchReplenishRequest;
import com.jamesaworo.stocky.features.stock.data.request.BatchSetLowStockPointRequest;
import com.jamesaworo.stocky.features.stock.data.request.LowStockAlertRequest;
import com.jamesaworo.stocky.features.stock.data.request.ReplenishItemRequest;
import com.jamesaworo.stocky.features.stock.domain.entity.StockTransactionLog;
import com.jamesaworo.stocky.features.stock.domain.enums.StockTransactionType;
import com.jamesaworo.stocky.features.stock.domain.usecase.IStockTransactionLogUsecase;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.http.ResponseEntity.ok;

@Interactor
@RequiredArgsConstructor
public class LowStockAlertInteractor implements ILowStockAlertInteractor {

    private final ProductRepository productRepository;
    private final IProductUsecase productUsecase;
    private final IProductBasicUsecase productBasicUsecase;
    private final IStockTransactionLogUsecase transactionLogUsecase;
    private final StockAlertService stockAlertService;

    @Override
    public ResponseEntity<List<LowStockAlertRequest>> getLowStockAlerts() {
        List<Product> lowStockProducts = this.productRepository.findLowStockProducts();
        List<LowStockAlertRequest> alerts = lowStockProducts.stream()
                .map(this::toLowStockAlertRequest)
                .collect(Collectors.toList());
        return ok().body(alerts);
    }

    @Override
    public ResponseEntity<Long> getLowStockCount() {
        Long count = this.productRepository.countLowStockProducts();
        return ok().body(count);
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> batchReplenish(BatchReplenishRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ok().body(false);
        }

        String currentUser = getCurrentUsername();

        for (ReplenishItemRequest item : request.getItems()) {
            if (item.getProductId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }

            Optional<Product> optionalProduct = this.productUsecase.findById(item.getProductId());
            optionalProduct.ifPresent(product -> {
                ProductBasic basic = product.getBasic();
                Integer quantityBefore = basic.getQuantity();
                Integer quantityAfter = quantityBefore + item.getQuantity();

                Optional<ProductBasic> updatedBasic = this.productBasicUsecase.updateProductQuantity(
                        basic.getId(),
                        item.getQuantity(),
                        com.jamesaworo.stocky.features.product.domain.enums.ProductQuantityUpdateType.INCREMENT
                );

                updatedBasic.ifPresent(this.stockAlertService::checkAndHandleLowStockAlert);

                StockTransactionLog log = StockTransactionLog.builder()
                        .product(product)
                        .transactionType(StockTransactionType.INCREMENT)
                        .quantityBefore(quantityBefore)
                        .quantityAfter(quantityAfter)
                        .changeAmount(item.getQuantity())
                        .remark(request.getRemark() != null ? request.getRemark() : "Batch replenish")
                        .operatorName(currentUser)
                        .build();

                this.transactionLogUsecase.save(log);
            });
        }

        return ok().body(true);
    }

    @Override
    @Transactional
    public ResponseEntity<Integer> batchSetLowStockPoint(BatchSetLowStockPointRequest request) {
        if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty() || request.getLowStockPoint() == null) {
            return ok().body(0);
        }

        int updatedCount = this.productRepository.updateLowStockPointByCategoryIds(
                request.getCategoryIds(),
                request.getLowStockPoint()
        );

        List<Product> products = this.productRepository.findByCategoryIds(request.getCategoryIds());
        for (Product product : products) {
            Optional<ProductBasic> optionalBasic = this.productBasicUsecase.findById(product.getBasic().getId());
            optionalBasic.ifPresent(this.stockAlertService::checkAndHandleLowStockAlert);
        }

        return ok().body(updatedCount);
    }

    @Override
    public ResponseEntity<Resource> exportLowStockAlert() {
        List<Product> lowStockProducts = this.productRepository.findLowStockProducts();
        List<LowStockAlertRequest> alerts = lowStockProducts.stream()
                .map(this::toLowStockAlertRequest)
                .collect(Collectors.toList());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Low Stock Alerts");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Product Name");
            headerRow.createCell(1).setCellValue("Brand");
            headerRow.createCell(2).setCellValue("SKU");
            headerRow.createCell(3).setCellValue("Barcode");
            headerRow.createCell(4).setCellValue("Category");
            headerRow.createCell(5).setCellValue("Current Quantity");
            headerRow.createCell(6).setCellValue("Low Stock Point");
            headerRow.createCell(7).setCellValue("Shortage");

            int rowNum = 1;
            for (LowStockAlertRequest alert : alerts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(alert.getProductName() != null ? alert.getProductName() : "");
                row.createCell(1).setCellValue(alert.getBrandName() != null ? alert.getBrandName() : "");
                row.createCell(2).setCellValue(alert.getSku() != null ? alert.getSku() : "");
                row.createCell(3).setCellValue(alert.getBarcode() != null ? alert.getBarcode() : "");
                row.createCell(4).setCellValue(alert.getCategoryName() != null ? alert.getCategoryName() : "");
                row.createCell(5).setCellValue(alert.getCurrentQuantity() != null ? alert.getCurrentQuantity() : 0);
                row.createCell(6).setCellValue(alert.getLowStockPoint() != null ? alert.getLowStockPoint() : 0);
                row.createCell(7).setCellValue(alert.getShortageQuantity() != null ? alert.getShortageQuantity() : 0);
            }

            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=low-stock-alerts.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Failed to export low stock alerts", e);
        }
    }

    private LowStockAlertRequest toLowStockAlertRequest(Product product) {
        ProductBasic basic = product.getBasic();
        LowStockAlertRequest request = new LowStockAlertRequest();
        request.setProductId(product.getId());
        request.setProductName(basic.getProductName());
        request.setBrandName(basic.getBrandName());
        request.setSku(basic.getSku());
        request.setBarcode(basic.getBarcode());
        request.setCurrentQuantity(basic.getQuantity());
        request.setLowStockPoint(basic.getLowStockPoint());

        if (basic.getLowStockPoint() != null && basic.getQuantity() != null) {
            request.setShortageQuantity(basic.getLowStockPoint() - basic.getQuantity());
        }

        if (basic.getProductCategory() != null) {
            request.setCategoryName(basic.getProductCategory().getTitle());
        }

        return request;
    }

    private String getCurrentUsername() {
        if (getContext().getAuthentication() != null) {
            return getContext().getAuthentication().getName();
        }
        return "System";
    }
}
