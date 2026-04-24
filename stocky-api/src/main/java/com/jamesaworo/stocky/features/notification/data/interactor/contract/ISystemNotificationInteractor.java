package com.jamesaworo.stocky.features.notification.data.interactor.contract;

import com.jamesaworo.stocky.features.notification.data.request.SystemNotificationRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISystemNotificationInteractor {

    ResponseEntity<List<SystemNotificationRequest>> findAll();

    ResponseEntity<List<SystemNotificationRequest>> findUnread();

    ResponseEntity<Long> countUnread();

    ResponseEntity<Boolean> markAsRead(Long id);

    ResponseEntity<Boolean> markAllAsRead();
}
