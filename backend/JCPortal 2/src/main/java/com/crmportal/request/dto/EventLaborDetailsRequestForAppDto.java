package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLaborDetailsRequestForAppDto {
	
	
	private Long id;
	private Long eventId;
	private Long eventFunctionId;
	private Long labortypeid;
	private Long contactid;
	private String laborshift;
	private String labordatetime;
	private Double price=0.0;
	private Double qty=0.0;
	private Double totalprice=0.0;
	private String place;
	private String notesEnglish;
	private String notesHindi;
	private String notesGujarati;
	private BigDecimal shiftTransPrice;
	private Integer sortOrder;
}
