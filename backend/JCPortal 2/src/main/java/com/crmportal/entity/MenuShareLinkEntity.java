package com.crmportal.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "menu_share_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuShareLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // encrypted token stored in DB
    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    // static access code user must enter to view menu
    @Column(name = "access_code", nullable = false, length = 100)
    private String accessCode;

    @Column(name = "event_function_id", nullable = false)
    private Long eventFunctionId;
    
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "package_id")
    private Long packageId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expiry_date", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}