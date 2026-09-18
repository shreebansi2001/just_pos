// MenuShareDataResponseDto.java
package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class MenuShareDataResponseDto {
	private Long eventId;
    private Long eventFunctionId;
    private Long userId;
    private String expiryDate;
    private MenuShareLinkMenuPreparationDto menuPreparation;
	private List<MenuSharedMenuItemsResponseDto> MenuPreparationItems;
	private List<MenuPreparationSelectedItemDetailsResponseDto> selectedMenuPreparationItems;
}