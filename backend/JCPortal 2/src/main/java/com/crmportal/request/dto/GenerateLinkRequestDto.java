// GenerateLinkRequestDto.java
package com.crmportal.request.dto;

import lombok.Data;

@Data
public class GenerateLinkRequestDto {
	private Long eventId;
    private Long eventFunctionId;
    private Long packageId;
    private Long userId;
}