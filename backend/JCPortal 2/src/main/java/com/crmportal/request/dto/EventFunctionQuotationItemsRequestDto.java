package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionQuotationItemsRequestDto {

	private Long id;
	private String functionName;
	private Long eventFunctionId;
	private String functionDate;
	private Integer pax;
	private Integer extraPax;
	private BigDecimal ratePerPlate;
	private BigDecimal offeredRate;
	private BigDecimal amount;
	private Boolean isEventFunction;
	private Long defaultFunctionId;	
	private Boolean isAddons;	
	private Long menuCatId;	
	private Long itemId;	
	private BigDecimal extraTax;
	private BigDecimal taxRate;
	private String options;
	private Long       customPackageId;
	private String     customPackageName;
	private BigDecimal customPackagePrice;
	private Boolean isLocked;
	private Long extraChargesId = null;
	private Boolean isExtraCharges = false;

}
