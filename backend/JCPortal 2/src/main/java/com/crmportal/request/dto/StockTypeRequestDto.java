package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class StockTypeRequestDto {

    @NotBlank(message = "Stock type name (English) is required")
    private String nameEnglish;

    private String nameHindi;

    private String nameGujarati;

    @NotNull(message = "User Id is required")
    private Long userId;
    
    private Integer mainType;
}