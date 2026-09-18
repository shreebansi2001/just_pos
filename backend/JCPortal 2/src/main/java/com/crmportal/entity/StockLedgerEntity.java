package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "stock_ledger")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockLedgerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_ledger_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id")
    private RawMaterialMasterEntity rawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_cat_id")
    private RawMaterialCategoryMasterEntity rawMaterialCat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private UnitMasterEntity unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private PartyMasterEntity party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_type_id")
    private StockTypeEntity stockType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMasterEntity user;

    @Column(name = "ref_id")
    private Long refId;

    @Column(name = "ref_code", length = 50)
    private String refCode;

    @Column(name = "ref_type", length = 30)
    private String refType;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "voucher", length = 50)
    private String voucher;

    @Column(name = "bill_no", length = 30)
    private String billNo;

    @Column(name = "qty")
    private Double qty = 0.0;

    @Column(name = "qty_in")
    private Double qtyIn = 0.0;

    @Column(name = "qty_out")
    private Double qtyOut = 0.0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_type_id")
    private StockTypeEntity kichenType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "is_delete", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDelete = false;
}