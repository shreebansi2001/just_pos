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
public class EventFunctionQuotationRequestDto {

	private Long eventId;
	private BigDecimal totalAmount;
	private BigDecimal cashPayment = BigDecimal.ZERO;
	private BigDecimal chequePayment = BigDecimal.ZERO;
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
	private String duedate;
	private String quotationdate;
	private Long userId;
	private String poojaRooms;
	private String ironService;
	private String venueRemark;
	private BigInteger venueTotal;
	private Boolean isExtraFunction;
	private Boolean isDecore = false;
	private List<EventFunctionQuotationPaymentRequestDto> eventFunctionQuotationPayments;
	private List<EventFunctionQuotationItemsRequestDto> functionQuotationItems;
	private Boolean isLocked;
	private BigInteger transportation;
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
