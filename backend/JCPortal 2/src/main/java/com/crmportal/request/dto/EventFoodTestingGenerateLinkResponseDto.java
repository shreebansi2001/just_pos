package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingGenerateLinkResponseDto {
    
	private Long id;
    
	private String token;
    
	private String shareUrl;
    
	private String accessCode;
    
	private String expiryDate;
    
	private Long eventId;
    
	private String eventName;
    
	private Long eventFunctionId;
    
	private String functionName;
    
	private String functionDate;
    
	private Long testerId;
    
	private String testerName;
    
	private String testerContactNo;
    
	private Boolean isActive;
    
	private String eventNo;
    
	private Long userId;
	
	private Integer members;
}
