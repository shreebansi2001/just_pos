package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import com.crmportal.response.dto.MenuPreparationSelectedItemDetailsResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPreparationRequestDto {
	
	private Long id;
	private Integer pax;
	private Integer sortorder;
	private BigDecimal price;
	private Long eventFunctionId;
	private BigDecimal defaultPrice;
	private Long packageId;
	private String packageName;
	private BigDecimal packagePrice;
	private Boolean isPackage;
	private List<MenuPreparationSelectedItemDetailsResponseDto> selectedMenuPreparation;
	private EventFunctionRawMaterialPermissionRequestDto permissionRawMaterials = null;
}
