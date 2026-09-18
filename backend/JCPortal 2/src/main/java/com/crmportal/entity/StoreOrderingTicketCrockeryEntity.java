package com.crmportal.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "store_ordering_ticket_crockery")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreOrderingTicketCrockeryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sot_id")
    private Long id;

    @Column(name = "sot_no", nullable = false, length = 20)
    private String sotNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventMasterEntity event;

    // Status: PENDING, ACCEPTED, PO_GENERATED
    @Column(name = "status", length = 20)
    private String status = "PENDING";

    @Column(name = "isDelete", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMasterEntity user;
}