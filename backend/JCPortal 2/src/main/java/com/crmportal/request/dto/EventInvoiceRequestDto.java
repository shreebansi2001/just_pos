package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInvoiceRequestDto {

	private Long eventId;
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
	private String gstnumber;
	private String billingname;
	private String billingaddress;
	private String shipname;
	private String shipaddress;
	private String duedate;
	private Long userId;
	private String invoiceCode;
	private BigDecimal cashPayment = BigDecimal.ZERO;
	private BigDecimal chequePayment = BigDecimal.ZERO;
	private List<EventInvoiceFunctionPaymentRequestDto> eventInvoiceFunctionPayments;
	private List<EventInvoiceFunctionItemsRequestDto> invoiceFunctionItems;
	private String foodTax;
	private  BigInteger foodTaxAmount;
	private  BigInteger foodTaxTotalAmount;
	private String serviceTax;
	private  BigInteger serviceTaxAmount;
	private  BigInteger serviceTaxTotalAmount;
	private String vatTax;
	private  BigInteger vatTaxAmount;
	private  BigInteger vatTaxTotalAmount;
	private String discountPct = "";
	private Boolean isDiscountPercent;
}
