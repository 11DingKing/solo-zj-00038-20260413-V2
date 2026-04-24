package com.jamesaworo.stocky.features.notification.endpoint;

import com.jamesaworo.stocky.features.notification.data.interactor.contract.ISystemNotificationInteractor;
import com.jamesaworo.stocky.features.notification.data.request.SystemNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.jamesaworo.stocky.core.constants.Global.API_PREFIX;

@RestController
@RequestMapping(value = API_PREFIX + "/notifications")
@RequiredArgsConstructor
public class SystemNotificationEndpoint {

    private final ISystemNotificationInteractor interactor;

    @GetMapping("/all")
    public ResponseEntity<List<SystemNotificationRequest>> findAll() {
        return this.interactor.findAll();
    }

    @GetMapping("/unread")
    public ResponseEntity<List<SystemNotificationRequest>> findUnread() {
        return this.interactor.findUnread();
    }

    @GetMapping("/count-unread")
    public ResponseEntity<Long> countUnread() {
        return this.interactor.countUnread();
    }

    @PostMapping("/mark-as-read/{id}")
    public ResponseEntity<Boolean> markAsRead(@PathVariable Long id) {
        return this.interactor.markAsRead(id);
    }

    @PostMapping("/mark-all-as-read")
    public ResponseEntity<Boolean> markAllAsRead() {
        return this.interactor.markAllAsRead();
    }
}
