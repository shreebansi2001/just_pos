package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.TaxType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invoice_id")
	private Long invoiceId;
	
	@Column(name = "customer_id")
	private Long customerId;
	
	@Column(name = "billing_address")
	private String billingAddress;
	
	@Column(name = "shipping_address")
	private String shippingAddress;
	
	@Column(name = "billing_name")
	private String billingName;
	
	@Column(name = "gst_number")
	private String gstNumber;
	
	@Column(name = "invoice_code")
	private String invoiceCode;
	
	@Column(name = "invoice_date")
	private LocalDate invoiceDate;
	
	@Column(name = "terms")
	private String terms;
	
	@Column(name = "due_date")
	private LocalDate dueDate;
	
	@Column(name = "sales_person_id")
	private Long salesPersonId;
	
	@Column(name = "customer_notes")
	private String customerNotes;

	@Column(name = "sub_total")
	private BigDecimal subTotal;
	
	@Column(name = "discount_per")
	private BigDecimal discountPer;
	
	@Column(name = "discount_amount")
	private BigDecimal discountAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "tax_type")
	private TaxType taxType;
	
	@Column(name = "gst_percent")
	private BigDecimal gstPercent;
	
	@Column(name = "gst_amount")
	private BigDecimal gstAmount;
	
	@Column(name = "adjust_amount")
	private BigDecimal adjust_amount;
	
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	
	@Column(name = "tnc")
	private String tnc;
	
	@Column(name = "doc_path")
	private String docPath;
	
//	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
//	private List<InvoiceItemsEntity> items;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
}
