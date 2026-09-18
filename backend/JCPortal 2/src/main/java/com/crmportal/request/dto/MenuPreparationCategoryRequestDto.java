package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuPreparationCategoryRequestDto {

	private Long menuCategoryId;
	private String menuCategoryName;
	private Integer menuSortOrder;
	private String menuSlogan;
	private String menuNotes;
}
