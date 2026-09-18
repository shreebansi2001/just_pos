package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.crmportal.entity.CaptainReceipeMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemCaptainReceipeResponseDto {

	private Long id;
	
	private BigDecimal weight;
	
	private Long unitId;
	
	private String unitName;
	
	private UnitHierarchyDto unitHierarchy;
	
	private BigDecimal rate;
	
	private CaptainReceipeMasterResponseDto captainReceipeMaster;
	
	private String venue;
	
	private Long menuItemId;
	
	private String menuItemName;
	
	private Boolean isDelete;
	
	private Boolean isActive;
	
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private Long userId;

	private String uuid;
}
