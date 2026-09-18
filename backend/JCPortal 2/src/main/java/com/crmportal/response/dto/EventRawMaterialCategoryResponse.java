package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialCategoryResponse {

    private Long categoryId;
    private String categoryNameEnglish;
    private String categoryNameHindi;
    private String categoryNameGujarati;

    private List<EventRawMaterialInfoDto> rawMaterials;
}
