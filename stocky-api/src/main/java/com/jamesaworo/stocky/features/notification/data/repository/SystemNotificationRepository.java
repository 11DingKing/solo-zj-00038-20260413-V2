package com.jamesaworo.stocky.features.notification.data.repository;

import com.jamesaworo.stocky.features.notification.domain.entity.SystemNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemNotificationRepository extends JpaRepository<SystemNotification, Long> {

    List<SystemNotification> findByIsReadOrderByCreatedAtDesc(Boolean isRead);

    List<SystemNotification> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(n) FROM SystemNotification n WHERE n.isRead = false")
    Long countUnread();
}
