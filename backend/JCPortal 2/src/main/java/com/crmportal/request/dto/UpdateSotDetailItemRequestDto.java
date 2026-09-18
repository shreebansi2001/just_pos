package com.crmportal.request.dto;

import lombok.Data;

@Data
public class UpdateSotDetailItemRequestDto {
    private Long sotDetailId;
    private double qty;       
    private Long partyId;    
    private Long unitId;     
}