package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraRawItemDTO {

    private String rawMaterialName;
    private Double qty;
    private Double price;
    private String unitName;
}