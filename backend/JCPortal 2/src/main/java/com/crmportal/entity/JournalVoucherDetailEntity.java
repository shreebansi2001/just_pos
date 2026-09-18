package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "journal_voucher_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JournalVoucherDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private JournalVoucherEntity voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private PartyMasterEntity party;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "credit_debit", nullable = false, length = 2)
    private String creditDebit; // "CR" or "DR"

    @Column(name = "particular", length = 500)
    private String particular;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
    private LocalDateTime createdAt;
}