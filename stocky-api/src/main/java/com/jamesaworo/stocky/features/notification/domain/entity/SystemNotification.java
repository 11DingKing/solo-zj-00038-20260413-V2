package com.jamesaworo.stocky.features.notification.domain.entity;

import com.jamesaworo.stocky.core.base.BaseModel;
import com.jamesaworo.stocky.features.notification.domain.enums.SystemNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.jamesaworo.stocky.core.constants.Table.PREFIX;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = PREFIX + "SYSTEM_NOTIFICATIONS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemNotification extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemNotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String message;

    @Column
    private Long relatedEntityId;

    @Column
    private String relatedEntityType;

    @Column
    private Boolean isRead = false;

    @Column
    private String recipientRoles;

    @Column
    private String recipientUsers;
}
