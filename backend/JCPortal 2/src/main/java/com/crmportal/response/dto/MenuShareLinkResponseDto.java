// MenuShareLinkResponseDto.java
package com.crmportal.response.dto;

import lombok.Data;

@Data
public class MenuShareLinkResponseDto {
    private Long id;
    private String token;
    private String shareUrl;      // full clickable URL
    private String accessCode;
    private String expiryDate;
    private Long eventId;
    private String eventName;
    private Long eventFunctionId;
    private String functionName;
    private String functionDate;
    private Long packageId;
    private String packageName;
    private Long userId;
    private String userName;
    private String userMobileNo;
    private Boolean isActive;
    private String eventNo;
    private String guestName;
    private String guestMobileNo;
}