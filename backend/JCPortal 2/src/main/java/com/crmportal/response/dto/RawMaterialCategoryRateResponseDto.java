package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RawMaterialCategoryRateResponseDto {

    private Long categoryId;
    private String categoryNameEng;
    private String categoryNameHindi;
    private String categoryNameGujarati;
    private BigDecimal totalRate;

    public RawMaterialCategoryRateResponseDto(
            Long categoryId,
            String categoryNameEng,
            String categoryNameHindi,
            String categoryNameGujarati,
            BigDecimal totalRate) {

        this.categoryId = categoryId;
        this.categoryNameEng = categoryNameEng;
        this.categoryNameHindi = categoryNameHindi;
        this.categoryNameGujarati = categoryNameGujarati;
        this.totalRate = totalRate;
    }

    public RawMaterialCategoryRateResponseDto(
            Long categoryId,
            String categoryNameEng,
            String categoryNameHindi,
            String categoryNameGujarati,
            Double totalRate) {

        this.categoryId = categoryId;
        this.categoryNameEng = categoryNameEng;
        this.categoryNameHindi = categoryNameHindi;
        this.categoryNameGujarati = categoryNameGujarati;
        this.totalRate = totalRate != null
                ? BigDecimal.valueOf(totalRate)
                : BigDecimal.ZERO;
    }
}