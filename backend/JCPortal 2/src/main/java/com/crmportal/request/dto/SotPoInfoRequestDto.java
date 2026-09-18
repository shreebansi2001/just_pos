package com.crmportal.request.dto;

import lombok.Data;

@Data
public class SotPoInfoRequestDto {

    private Long   sotPoId;
    private String challanNo;
    private String eventName;
    private String deliveryVenue;
    private String deliveryTime;
    private String remarks;
}