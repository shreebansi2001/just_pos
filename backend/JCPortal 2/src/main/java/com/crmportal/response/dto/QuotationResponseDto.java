package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponseDto {

	private String companyName;
	private String countryCode;
	private String officeNo;
	private String companyEmail;
	private String companyAddress;
	private String companyLogo;

	private Long eventId;
	private String eventNo;

	private Long quotationId;
	private Long partyId;
	private String partyName;
	private String partyAddress;
	private String partyEmail;
	private String partyMobile;
	private String partyGst;
	private String quotationDueDate;
	private String quotationCode;
	private String eventDate;
	private Double subTotle;
	private Double cashPayment;
	private Double chequePayment;
	private String cgst;
	private Double cgstAmnt;
	private String sgst;
	private Double sgstAmnt;
	private String igst;
	private Double igstAmnt;
	private Double discount;
	private Double advancePayment;
	private Double remainingAmount;
	private Double grandTotal;
	private BigDecimal extraTax;
	private BigDecimal taxRate;

	private String functionName;
	private Integer functionPax;
	private Integer functionExtraPax;
	private String functionDate;
	private Double rate;
	private Double offeredRate;
	private Double amount;

	private String bankName;
	private String branchName;
	private String accountHolderName;
	private String accountNo;
	private String ifscCode;
	private String upiId;
	private String qrCodePath;

	private String notes;
	private String inquiryDate;

	private String options;
	private String functionTime;
	private Long banquet_hall_id;
	private Boolean is_event_function;
	private Long eventFunctionId;

	private String ironService;
	private String poojaRoom;
	private String venueRemark;
	private Integer venueTotal;

	private Boolean isQuotationLocked;
	private Boolean isItemLocked;

	private String eventName;
	private String venueName;

	private Boolean isAddons;

	private String cmpGstNumber;

	private LocalDateTime generatedDateTime;
	private String shipAddress;
	private String cmpPanNumber;
	private Integer transportation;

	private String pan;

	private String foodTax;
	private Integer foodTaxAmount;
	private Integer foodTaxTotalAmount;
	private String serviceTax;
	private Integer serviceTaxAmount;
	private Integer serviceTaxTotalAmount;
	private String vatTax;
	private Integer vatTaxAmount;
	private Integer vatTaxTotalAmount;

	private String fssaiNumber;
	private String hsnNumber;

	private String cinNumber;
	private String fdaLincense;
	
	private String discountPercent;

	private Long defaultFunctionId;
	
	private String prefix;
	
	private String eventVenue;
}
