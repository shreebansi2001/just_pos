package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SotListResponseDto {

	private Long id;
	
    private String sotNo;
    
    private Long eventId;
    
    private String eventName;
    
    private String eventDate;
    
    private String status;
    
    private String createdAt;
    
    private Long userId;
}
