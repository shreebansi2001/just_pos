package com.crmportal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosInvoiceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId = 1L; // Admin/Owner account

    @Column(name = "invoice_code", nullable = false, length = 40)
    private String invoiceCode;

    @Column(name = "outlet_code", length = 30)
    private String outletCode = "MAIN";

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_code", length = 40)
    private String orderCode;

    @Column(name = "order_type", length = 30)
    private String orderType = "dine-in";

    @Column(name = "table_label", length = 40)
    private String tableLabel;

    @Column(name = "customer_name", length = 120)
    private String customerName;

    @Column(name = "customer_phone", length = 30)
    private String customerPhone;

    @Column(name = "subtotal")
    private Double subtotal = 0.0;

    @Column(name = "discount")
    private Double discount = 0.0;

    @Column(name = "cgst")
    private Double cgst = 0.0;

    @Column(name = "sgst")
    private Double sgst = 0.0;

    @Column(name = "total")
    private Double total = 0.0;

    @Column(name = "status", length = 30)
    private String status = "unpaid"; // unpaid, paid, voided

    @Column(name = "payment_mode", length = 40)
    private String paymentMode; // Cash, Card, UPI

    @Column(name = "cashier_id")
    private Long cashierId; // Cashier who collected payment

    @Column(name = "created_by_user_id")
    private Long createdByUserId; // Staff who created invoice

    @Lob
    @Column(name = "items_json")
    private String itemsJson;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",columnDefinition = "DATETIME")
    private Date createdAt = new Date();
}
