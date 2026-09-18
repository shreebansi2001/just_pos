package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "purchaseorderreturn")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderReturnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "por_id")
    private Long id;

    @Column(name = "porcode", unique = true, nullable = false, length = 30)
    private String porcode;

    // Reference to original PO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id")
    private PurchaseOrderEntity purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private PartyMasterEntity supplier;

    @Column(name = "voucher", columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String voucher;

    @Column(name = "returndate")
    private LocalDate returndate;

    @Column(name = "billno", columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String billno;

    @Column(name = "invoicetype", columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String invoicetype;

    @Column(name = "remarks", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String remarks;

    @Column(name = "subamount")
    private float subamount = 0.0f;

    @Column(name = "discountper")
    private float discountper = 0.0f;

    @Column(name = "discountval")
    private float discountval = 0.0f;

    @Column(name = "adjustamount")
    private float adjustamount = 0.0f;

    @Column(name = "finalamount")
    private float finalamount = 0.0f;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMasterEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_type_id", nullable = true)
    private StockTypeEntity stocktype;
}