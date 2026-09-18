package com.crmportal.response.dto;

import lombok.Data;

@Data
public class UserDashboardResponse {

	private Long userId;
    private String fullName;
    private String email;
    private String mobile;
    private Boolean isActive;
    private Boolean isApproved;
    private String userCode;
    private String preFix;
    private String createdAt;  // formatted string

    private String companyName;
    private String companyEmail;
    private String memberType;
}