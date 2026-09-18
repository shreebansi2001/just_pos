package com.crmportal.response.dto;


import lombok.Data;

@Data
public class StockTypeResponseDto {

    private Long id;

    private String nameEnglish;

    private String nameHindi;

    private String nameGujarati;

    private Boolean isActive;

    private String createdAt;

    private String updatedAt;

    private Long userId;
    
    private Integer mainType;
}