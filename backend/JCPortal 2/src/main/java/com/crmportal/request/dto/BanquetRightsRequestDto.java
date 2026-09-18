package com.crmportal.request.dto;

import lombok.Data;

@Data
public class BanquetRightsRequestDto {

    private Long banquetHallId;

    private Boolean isAllow;
}