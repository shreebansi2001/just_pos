package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemAllocationResponseDto {

	private Long menuItemId;
    private String menuItemName;
    private String menuItemNameHindi;
    private String menuItemNameGujarati;
    private String agencyType;
    private Integer pax;
    private Integer itemSortOrder;
    
    private MenuItemAgencyAlloResponseDto agencies;
}
