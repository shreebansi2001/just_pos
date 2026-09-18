package com.crmportal.response.dto;

import lombok.Data;

@Data
public class StoreManageItemResponseDto {
    private Long   rawMaterialId;
    private String rawMaterialName;
    private Long   catId;
    private String catName;
    private Long   unitId;
    private String unitName;
    private Double closingStock;   // qty — not editable on frontend
    private Double storeQty;
    private Double increaseQty;
    private Double wastageQty;
    private String remarks;
}