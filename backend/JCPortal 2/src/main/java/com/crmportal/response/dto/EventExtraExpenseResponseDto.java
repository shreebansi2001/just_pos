package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventExtraExpenseResponseDto {
	private Long id;
	private Long eventId;
	private Long eventFunctionId;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Double qty=0.0;
	private Double price=0.0;
	private Double totalprice=0.0;
	private Long userId;
}
