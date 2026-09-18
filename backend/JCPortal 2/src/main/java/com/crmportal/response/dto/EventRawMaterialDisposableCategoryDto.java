package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class EventRawMaterialDisposableCategoryDto {
    private Long catId;
    private String categoryNameEnglish;
    private String categoryNameHindi;
    private String categoryNameGujarati;
    private List<EventRawMaterialDisposableItemResponseDto> items;
}