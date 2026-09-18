package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.crmportal.request.dto.EventFunctionQuotationPaymentResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionQuotationResponseDto {

	private Long id;
	private BigDecimal totalAmount;
	private BigDecimal cashPayment;
	private BigDecimal chequePayment;
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
	private String quotationCode;
	private String createdAt;
	private String gstnumber;
	private String billingname;
	private String duedate;
	private String quotationdate;
	private EventMasterResponseDto event;
	private UserMasterResponseDto user;
	private BigInteger overallTotalAmnt;
	private BigInteger overAllReceivableAmnt;
	private BigInteger overAllRemainingAmnt;
	private String poojaRooms;
	private String ironService;
	private String venueRemark;
	private BigInteger venueTotal;
	private Boolean isExtraFunction;
	private Boolean isDecore;
	private List<EventFunctionQuotationPaymentResponseDto> eventFunctionQuotationPayments;
	private List<EventFunctionQuotationItemsResponseDto> functionQuotationItems;
	private List<EventQuotationSecurityDepositResponseDto> eventQuotationSecurityDeposit;
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
	private String discountPct;
	private Boolean isDiscountPercent;
}
