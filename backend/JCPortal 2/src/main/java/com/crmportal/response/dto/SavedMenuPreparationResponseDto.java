package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedMenuPreparationResponseDto {

	private Long menuPreparationDetailsId;
	
	private Long menuItemId;
	
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
}
