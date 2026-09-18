package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.enums.TaxType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequestDTO {

	@NotNull(message = "Customer ID is required")
	private Long customerId;
	
	@NotBlank(message = "Billing address is required") 
	private String billingAddress;
	
	private String shippingAddress;
	
	@NotBlank(message = "Billing name is required") 
	private String billingName;
	
	private String gstNumber;
	
	@NotBlank(message = "Invoice code is required")
	private String invoiceCode;
	
	@NotNull(message = "Invoice date is required")
	private String invoice_date;
	
	@NotNull(message = "Terms is required.")
	@Size(max = 50, message = "Terms must not exceed 50 characters")
	private String terms;
	
	@NotNull(message = "Due date is required")
	private String due_date;
	
	@NotNull(message = "Sales Person ID is required")
	private Long salesPersonId;
	
	@Size(max = 1000, message = "Customer notes must not exceed 1000 characters")
	private String customerNotes;
	
	@NotNull(message = "Sub total is required") 
	@DecimalMin(value = "0.0", inclusive = true, message = "Sub total must be >= 0")
	private BigDecimal subTotal;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "Discount % must be >= 0") 
	@DecimalMax(value = "100.0", message = "Discount % cannot exceed 100")
	private BigDecimal discountPer;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be >= 0")
	private BigDecimal discountAmount;
	
	@NotNull(message = "Tax type is required (TDS or TCS)")
	private TaxType taxType;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "GST % must be >= 0") 
	@DecimalMax(value = "100.0", message = "GST % cannot exceed 100")
	private BigDecimal gstPercent;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "GST amount must be >= 0")
	private BigDecimal gstAmount;
	
	@NotNull(message = "Adjust amount is required")
	private BigDecimal adjust_amount;
	
	@NotNull(message = "Total amount is required") 
	@DecimalMin(value = "0.0", inclusive = true, message = "Total amount must be >= 0")
	private BigDecimal totalAmount;
	
	private String tnc;
	
	@NotEmpty(message = "Invoice items are required")
	@Valid
	private List<InvoiceItemsRequestDto> invoiceItems;
	
	private MultipartFile doc;
	
}
