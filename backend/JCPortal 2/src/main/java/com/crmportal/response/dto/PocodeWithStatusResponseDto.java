package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PocodeWithStatusResponseDto {
    private Long id;
    private String pocode;
    private boolean isReturned; // true = already has a return, false = no return yet
}