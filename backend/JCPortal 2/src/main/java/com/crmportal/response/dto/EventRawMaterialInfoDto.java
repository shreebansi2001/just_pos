package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialInfoDto {

    private Long eventRawMaterialId;

    private Long rawMaterialId;
    private String rawMaterialNameEnglish;
    private String rawMaterialNameHindi;
    private String rawMaterialNameGujarati;

    private Long supplierId;
    private String supplierName;

    private Long unitId;
    private String unitName;

    private Double qty;
    private Double finalQty;
    private String place;
    private Double totalPrice;
    
    private Integer sequence;
}
