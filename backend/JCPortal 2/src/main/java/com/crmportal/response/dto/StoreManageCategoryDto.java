package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class StoreManageCategoryDto {
    private Long   catId;
    private String catName;
    private List<StoreManageItemResponseDto> items;
}