package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuAllocationOrderRequestDto {

	 	private Long id;
	    private Long partyId;
	    private BigDecimal totalPrice;

	    // Outside Order
	    private BigDecimal price;
	    private BigDecimal quantity;
	    private Long unitId;

	    // Agency Order
	    private String serviceType;
	    private Integer counterQuantity;
	    private Integer helperQuantity;
	    private BigDecimal counterPrice;
	    private BigDecimal helperPrice;
	    
	    private Boolean isOutside;
	    
	    private String remarks;
	    private String number;
	    
	    private BigDecimal shiftTransPrice;
	    
	    private String reportingTime;
}
