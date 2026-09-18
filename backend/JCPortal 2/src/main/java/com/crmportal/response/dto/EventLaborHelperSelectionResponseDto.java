package com.crmportal.response.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventLaborHelperSelectionResponseDto {
    private Long id;
    private Long partyId;
    private String partyName;
    private Long contactCategoryId;
    private String contactCategoryName;
    private String name;
    private String phonenumber;
    private String aadharcard;
    private String pancard;
    private String aadharcarddocpathfront;
    private String aadharcarddocpathback;
    private String pancarddocpath;
    private String photo;
    private String drivinglicense;
    private LocalDateTime createdAt;
    private Boolean isDelete;
    
    // Dynamic selection status flag
    private Boolean isSelected = false;
}
