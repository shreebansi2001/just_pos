package com.crmportal.response.dto;

import java.math.BigDecimal;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuAllocationOrderResponseDto {

	private Long id;
    
	private Long partyId;
    private String partyName;
    
    private Long menuAllocationId;

    // Outside Order
    private BigDecimal price;
    private BigDecimal quantity;
    
    private Long unitId;
    private String unitName;
    // Agency Order
    private String serviceType;
    private Integer counterQuantity;
    private Integer helperQuantity;
    private BigDecimal counterPrice;
    private BigDecimal helperPrice;
    private BigDecimal totalPrice;
    
    private Boolean isOutside;
    
    //inside
    private String number;
    private String remarks;
    private Integer pax;

    private String partyNameHindi;
    private String partyNameGujarati;
    
    private BigDecimal shiftTransPrice;
    private String reportingTime;
}
