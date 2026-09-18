package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuAllocationRequestDto {

	private Long id;
    private Boolean chefLabour;
    private Boolean outside;
    private Boolean inside;
    private Integer personCount;
    private String place;
    private String instructions;
    private String instructionsGujarati;
    private String instructionsHindi;

    private Long eventId;
    private Long eventFunctionId;
    private Long menuItemId;
    private Long menuCategoryId;
    private Long userId;
    private Boolean isPaxChange;
    private Integer menuCategorySortOrder;
    private Integer itemSortOrder;
    private List<EventFunctionMenuAllocationOrderRequestDto> menuAllocationOrders;
    
    private List<EventFunctionMenuItemRawMaterialRequestDto> menuItemRawMaterials;
}
