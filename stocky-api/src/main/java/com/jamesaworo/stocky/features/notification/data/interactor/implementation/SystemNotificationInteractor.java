package com.jamesaworo.stocky.features.notification.data.interactor.implementation;

import com.jamesaworo.stocky.core.annotations.Interactor;
import com.jamesaworo.stocky.features.notification.data.interactor.contract.ISystemNotificationInteractor;
import com.jamesaworo.stocky.features.notification.data.request.SystemNotificationRequest;
import com.jamesaworo.stocky.features.notification.domain.entity.SystemNotification;
import com.jamesaworo.stocky.features.notification.domain.usecase.ISystemNotificationUsecase;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@Interactor
@RequiredArgsConstructor
public class SystemNotificationInteractor implements ISystemNotificationInteractor {

    private final ISystemNotificationUsecase usecase;
    private final ModelMapper mapper;

    @Override
    public ResponseEntity<List<SystemNotificationRequest>> findAll() {
        List<SystemNotification> notifications = this.usecase.findAll();
        List<SystemNotificationRequest> requests = notifications.stream()
                .map(this::toRequest)
                .collect(Collectors.toList());
        return ok().body(requests);
    }

    @Override
    public ResponseEntity<List<SystemNotificationRequest>> findUnread() {
        List<SystemNotification> notifications = this.usecase.findUnread();
        List<SystemNotificationRequest> requests = notifications.stream()
                .map(this::toRequest)
                .collect(Collectors.toList());
        return ok().body(requests);
    }

    @Override
    public ResponseEntity<Long> countUnread() {
        Long count = this.usecase.countUnread();
        return ok().body(count);
    }

    @Override
    public ResponseEntity<Boolean> markAsRead(Long id) {
        return ok().body(this.usecase.markAsRead(id).orElse(false));
    }

    @Override
    public ResponseEntity<Boolean> markAllAsRead() {
        return ok().body(this.usecase.markAllAsRead().orElse(false));
    }

    private SystemNotificationRequest toRequest(SystemNotification model) {
        return this.mapper.map(model, SystemNotificationRequest.class);
    }
}
