package com.crmportal.request.dto;

import java.util.List;

import com.crmportal.request.NameplateRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionNameplateRequestDto {

	private Long eventId;
	
	private Long eventFunctionId;
	
	private Integer itemFontSize;
	
	private Integer categoryFontSize;
	
	private Long userId;
	
	private Integer isTableMenuItem;

	private Integer isStandyItem;
	
	private Integer isCounterItem;
	
	private List<NameplateRequestDto> namePlateRequests;
	
	private String headerNotesEnglish;
	
	private String headerNotesHindi;
	
	private String headerNotesGujarati;
	
	private String footerNotesEnglish;
	
	private String footerNotesHindi;
	
	private String footerNotesGujarati;
}
