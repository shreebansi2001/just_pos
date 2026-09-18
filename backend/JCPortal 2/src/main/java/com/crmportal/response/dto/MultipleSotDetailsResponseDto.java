package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleSotDetailsResponseDto {

    private Long rawMaterialId;
    private String rawMaterialName;
    private Long rawMaterialCatId;
    private String rawMaterialCatName;
    private Long unitId;
    private String unitName;
    private Long partyId;
    private String partyName;
    private double qty;
    private double acceptedQty;
    private double returnQty;
    private double availableStock;
    private Long availableStockUnitId;
    private String availableStockUnitName;
    
    private List<EventWiseSotDetailsResponseDto> eventWiseSotDetails;
}
