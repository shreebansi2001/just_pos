package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class AssignEventToChildRequestDto {

    private Long childUserId;

    private List<Long> eventIds;
    
    private BigDecimal paxPercentage;
}