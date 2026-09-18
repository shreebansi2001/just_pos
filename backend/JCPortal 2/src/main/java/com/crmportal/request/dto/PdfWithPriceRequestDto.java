package com.crmportal.request.dto;

import lombok.Data;

@Data
public class PdfWithPriceRequestDto {

    private Long poId;

    private Integer isCompanyDetails;

    // MASTER | LAST | AVG
    private String priceType;
    
    private Integer isRate;
}