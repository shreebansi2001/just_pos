// VerifyLinkRequestDto.java
package com.crmportal.request.dto;

import lombok.Data;

@Data
public class VerifyLinkRequestDto {
    private String token;
    private String accessCode;
}