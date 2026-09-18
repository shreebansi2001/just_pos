package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationCatWiseResponseDto {

	private Long menuCategoryId;
    private String menuCategoryName;
    private String menuCategoryNameHindi;
    private String menuCategoryNameGujarati;
    private Integer menuSortOrder;
    
    private List<MenuItemAllocationResponseDto> items;
}
