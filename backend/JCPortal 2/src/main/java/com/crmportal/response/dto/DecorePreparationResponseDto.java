package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePreparationResponseDto {

    private Long id;
    private Integer pax;
    private Integer sortorder;
    private BigDecimal price;
    private Integer totalPage;
    private Long totalItem;
    private BigDecimal defaultPrice;

    private Long packageId;
    private String packageName;
    private BigDecimal packagePrice;
    private Boolean isPackage;

    private EventFunctionMenuPreparationResponseDto eventFunction;

    private List<DecorePreparationSelectedItemDetailsResponseDto> selectedDecorePreparation;
}