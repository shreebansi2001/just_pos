package com.crmportal.response.dto;

import lombok.Data;

@Data
public class SotPoInfoResponseDto {

    private Long   sotPoId;
    private String challanNo;
    private String eventName;
    private String deliveryVenue;
    private String deliveryTime;
    private String remarks;
}