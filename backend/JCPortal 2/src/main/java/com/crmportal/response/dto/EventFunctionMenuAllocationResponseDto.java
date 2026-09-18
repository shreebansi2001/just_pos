package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.entity.EventMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuAllocationResponseDto {

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
    private String eventName;
    
    private Long eventFunctionId;
    private String eventFunctionName;
    
    private Long menuCategoryId;
    private String menuCategoryName;
    private String menuCategoryNameHindi;
    private String menuCategoryNameGujarati;
    
    private Long menuItemId;
    private String menuItemName;
    private String menuItemNameHindi;
    private String menuItemNameGujarati;
    private Boolean isPaxChange;
    private Long userId;
    private Integer menuSortorder;
    private Integer itemSortorder;
    
    private List<EventFunctionMenuAllocationOrderResponseDto> eventFunctionMenuAllocations;
}
