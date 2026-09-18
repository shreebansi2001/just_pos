package com.crmportal.request.dto;

import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingGenerateLinkRequestDto {

	private Long eventId;
	
    private Long eventFunctionId;
    
    private Long userId;
    
    private Long testerId;
    
    @PositiveOrZero(message = "Members should be zero or greater than zero")
    private Integer members = 0;
}
