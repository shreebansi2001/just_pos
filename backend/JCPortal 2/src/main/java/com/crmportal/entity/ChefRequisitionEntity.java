package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "chefrequisition")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChefRequisitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cr_id")
    private Long id;

    @Column(name = "crcode", unique = true, nullable = false, length = 30)
    private String crcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = true)
    private PartyMasterEntity party;

    @Column(name = "voucher", columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String voucher;

    @Column(name = "crdate")
    private LocalDate crdate;

    @Column(name = "invoicetype", columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String invoicetype;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_type_id")
    private StockTypeEntity stocktype;

    @Column(name = "remarks", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String remarks;

    // Status: PENDING, RUNNING, COMPLETED
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status = "PENDING";

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "isDelete", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDelete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMasterEntity user;
}