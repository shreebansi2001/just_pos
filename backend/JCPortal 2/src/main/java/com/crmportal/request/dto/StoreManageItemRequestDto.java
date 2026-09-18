package com.crmportal.request.dto;

import lombok.Data;

@Data
public class StoreManageItemRequestDto {
    private Long   rawMaterialId;
    private Double storeQty;
    private Double increaseQty;
    private Double wastageQty;
    private Double closingStock;
    private Long rawCatId;
    private Long unitId;
    private String remarks;
}