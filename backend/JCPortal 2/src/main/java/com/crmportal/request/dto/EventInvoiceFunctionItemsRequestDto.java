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
public class EventInvoiceFunctionItemsRequestDto {

	private Long id;
	private String functionName;
	private String functionDate;
	private Integer pax;
	private Integer extraPax;
	private BigDecimal extraTax;
	private BigDecimal taxRate;
	private BigDecimal ratePerPlate;
	private BigDecimal offeredRate;
	private BigDecimal amount;
	private Boolean isEventFunction;
	private Long extraChargesId = null;
	private Boolean isExtraCharges = false;
}
