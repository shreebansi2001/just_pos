package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWiseSotDetailsResponseDto {

	private Long sotId;

	private Long eventId;
	
	private String eventName;
	
	private Long sotDetailId;
	
	private Long unitId;
	
    private String unitName;
    
    private double qty;
    
    private double acceptedQty;
    
    private double returnQty;
    
}
