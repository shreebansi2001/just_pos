package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_payment_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePaymentHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invoice_payment_id")
	private Long invoicePaymentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id")
	private InvoiceEntity invoice;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_account_id")
	private BankDetailsEntity bankAccount;
	
	@Column(name = "payment_date")
	private LocalDate paymentDate;
	
	@Column(name = "transaction_id")
	private String transactionId;
	
	@Column(name = "cheque_no")
	private String chequeNo;

	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "due_amount")
	private BigDecimal dueAmount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode")
	private PaymentMode paymentMode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cash_type_id")
	private CashAccountEntity cashType;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
}
