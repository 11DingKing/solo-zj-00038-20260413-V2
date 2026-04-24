package com.jamesaworo.stocky.features.notification.data.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jamesaworo.stocky.features.notification.domain.enums.SystemNotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemNotificationRequest {
    private Long id;
    private SystemNotificationType type;
    private String title;
    private String message;
    private Long relatedEntityId;
    private String relatedEntityType;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
