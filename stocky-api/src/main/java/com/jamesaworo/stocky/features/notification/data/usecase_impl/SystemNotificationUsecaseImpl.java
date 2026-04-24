package com.jamesaworo.stocky.features.notification.data.usecase_impl;

import com.jamesaworo.stocky.core.annotations.Usecase;
import com.jamesaworo.stocky.features.notification.data.repository.SystemNotificationRepository;
import com.jamesaworo.stocky.features.notification.domain.entity.SystemNotification;
import com.jamesaworo.stocky.features.notification.domain.usecase.ISystemNotificationUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Usecase
@RequiredArgsConstructor
@Slf4j
public class SystemNotificationUsecaseImpl implements ISystemNotificationUsecase {

    private final SystemNotificationRepository repository;

    @Override
    public SystemNotification save(SystemNotification notification) {
        return this.repository.save(notification);
    }

    @Override
    public Optional<SystemNotification> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public List<SystemNotification> findAll() {
        return this.repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<SystemNotification> findUnread() {
        return this.repository.findByIsReadOrderByCreatedAtDesc(false);
    }

    @Override
    public Long countUnread() {
        return this.repository.countUnread();
    }

    @Override
    public Optional<Boolean> markAsRead(Long id) {
        Optional<SystemNotification> optional = this.findById(id);
        return optional.map(notification -> {
            notification.setIsRead(true);
            this.repository.save(notification);
            return true;
        });
    }

    @Override
    public Optional<Boolean> markAllAsRead() {
        List<SystemNotification> unread = this.findUnread();
        unread.forEach(notification -> notification.setIsRead(true));
        this.repository.saveAll(unread);
        return Optional.of(true);
    }
}
