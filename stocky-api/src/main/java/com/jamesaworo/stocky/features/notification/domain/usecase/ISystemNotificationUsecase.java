package com.jamesaworo.stocky.features.notification.domain.usecase;

import com.jamesaworo.stocky.features.notification.domain.entity.SystemNotification;

import java.util.List;
import java.util.Optional;

public interface ISystemNotificationUsecase {

    SystemNotification save(SystemNotification notification);

    Optional<SystemNotification> findById(Long id);

    List<SystemNotification> findAll();

    List<SystemNotification> findUnread();

    Long countUnread();

    Optional<Boolean> markAsRead(Long id);

    Optional<Boolean> markAllAsRead();
}
