package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePlateTableMenuRequestDto {

	private List<NamePlateTableMenuWithBgRequestDto> items;
	private Long eventId;
	private Long eventFunctionId;
	private Long userId;
	private Integer categoryFontSize;
	private Integer itemFontSize;
	
	private String headerNotesEnglish;
	private String headerNotesHindi;
	private String headerNotesGujarati;
	
	private String footerNotesEnglish;
	private String footerNotesHindi;
	private String footerNotesGujarati;
}
