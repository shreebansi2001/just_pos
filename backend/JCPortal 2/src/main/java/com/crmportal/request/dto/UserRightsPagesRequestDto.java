package com.crmportal.request.dto;

import lombok.Data;

@Data
public class UserRightsPagesRequestDto {
    private String pagename;
    private Boolean isActive;
    private Long moduleId;
}
