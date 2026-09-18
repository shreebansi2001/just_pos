package com.crmportal.response.dto;

import lombok.Data;

@Data
public class BanquetRightsResponseDto {

    private Long userId;

    private Long banquetHallId;

    private String banquetHallName;

    private Boolean isAllow;
}