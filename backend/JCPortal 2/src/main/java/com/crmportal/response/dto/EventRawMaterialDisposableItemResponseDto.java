package com.crmportal.response.dto;

import lombok.Data;

@Data
public class EventRawMaterialDisposableItemResponseDto {
    private Long id;          // null if from master
    private Long rawMaterialId;
    private String rawMaterialNameEnglish;
    private String rawMaterialNameHindi;
    private String rawMaterialNameGujarati;
    private Long rawMaterialCatId;
    private String categoryNameEnglish;
    private String categoryNameHindi;
    private String categoryNameGujarati;
    private Double supRate;
    private Double totalRate;
    private Double qty;
    private Long unitId;
    private String unitNameEnglish;
    private String unitNameHindi;
    private String unitNameGujarati;
    private String imageUrl;
}