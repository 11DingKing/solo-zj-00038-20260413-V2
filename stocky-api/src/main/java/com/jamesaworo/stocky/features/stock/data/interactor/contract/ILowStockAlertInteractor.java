package com.jamesaworo.stocky.features.stock.data.interactor.contract;

import com.jamesaworo.stocky.features.stock.data.request.BatchReplenishRequest;
import com.jamesaworo.stocky.features.stock.data.request.BatchSetLowStockPointRequest;
import com.jamesaworo.stocky.features.stock.data.request.LowStockAlertRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ILowStockAlertInteractor {
    ResponseEntity<List<LowStockAlertRequest>> getLowStockAlerts();
    ResponseEntity<Long> getLowStockCount();
    ResponseEntity<Boolean> batchReplenish(BatchReplenishRequest request);
    ResponseEntity<Integer> batchSetLowStockPoint(BatchSetLowStockPointRequest request);
    ResponseEntity<Resource> exportLowStockAlert();
}
