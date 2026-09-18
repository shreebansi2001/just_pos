package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuSharedMenuItemsResponseDto {
	private Long categoryId;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	List<MenuPreparationItemResponseDto> items;
}
