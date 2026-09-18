package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
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

import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_entry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_no")
    private String voucherNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type")
    private EntryType entryType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
	private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_account_id")
    private CashAccountEntity cashType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id")
    private BankDetailsEntity bankDetails;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "income_expense_type_id")
    private IncomeExpenseTypeEntity incomeExpenseType;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "account_contact_name")
    private String accountContactName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @Column(name = "reference_no")
    private String referenceNo;
    
    @Column(name = "account_contact_id")
    private Long accountContactId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "is_delete")
    private Boolean isDelete = false;
    
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
    
}
