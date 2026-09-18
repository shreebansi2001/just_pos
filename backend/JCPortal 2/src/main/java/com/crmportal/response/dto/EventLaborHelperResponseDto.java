package com.crmportal.response.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventLaborHelperResponseDto {
    private Long id;
    
    // Event Details
    private Long eventId;
    private Long eventFunctionId;
    
    // Contact / Party Details
    private Long partyId;
    private String partyName;
    
    // Labor Helper Details
    private Long laborHelperId;
    private String name;
    private String phonenumber;
    private String aadharcard;
    private String pancard;
    private String aadharcarddocpathfront;
    private String aadharcarddocpathback;
    private String pancarddocpath;
    private String photo;
    private String drivinglicense;
    
    // Contact Category Details from Labor Helper
    private Long contactCategoryId;
    private String contactCategoryName;
    
    private LocalDateTime createdAt;
}