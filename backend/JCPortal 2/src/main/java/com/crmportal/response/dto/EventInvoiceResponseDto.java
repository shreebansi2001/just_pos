package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.crmportal.request.dto.EventInvoicePaymentResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInvoiceResponseDto {

	private Long id;
	private BigDecimal totalAmount;
	private String cgst;
	private BigDecimal cgstAmnt;
	private String sgst;
	private BigDecimal sgstAmnt;
	private String igst;
	private BigDecimal igstAmnt;
	private BigDecimal discount;
	private BigInteger roundOff;
	private BigInteger grandTotal;
	private BigInteger subTotal;
	private BigInteger remainingAmount;
	private String notes;
	private String invoiceCode;
	private String createdAt;
	private String gstnumber;
	private String billingname;
	private String duedate;
	private EventMasterResponseDto event;
	private UserMasterResponseDto user;
	private BigInteger overallTotalAmnt;
	private BigInteger overAllReceivableAmnt;
	private BigInteger overAllRemainingAmnt;
	private String billingaddress;
	private String shipname;
	private String shipaddress;
	private BigDecimal cashPayment = BigDecimal.ZERO;
	private BigDecimal chequePayment = BigDecimal.ZERO;
	private List<EventInvoicePaymentResponseDto> eventInvoicePayments;
	private List<EventInvoiceFunctionItemsResponseDto> invoiceFunctionItems;
	private Map<String, Object> salesInvoiceData;
	private String foodTax;
	private  BigInteger foodTaxAmount;
	private  BigInteger foodTaxTotalAmount;
	private String serviceTax;
	private  BigInteger serviceTaxAmount;
	private  BigInteger serviceTaxTotalAmount;
	private String vatTax;
	private  BigInteger vatTaxAmount;
	private  BigInteger vatTaxTotalAmount;
	private String discountPct;
	private Boolean isDiscountPercent;
	private Long extraChargesId;
	private Boolean isExtraCharges;
}
