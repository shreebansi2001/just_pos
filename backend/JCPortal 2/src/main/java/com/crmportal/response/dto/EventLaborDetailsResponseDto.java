package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLaborDetailsResponseDto {

	private Long id;
	private Long labortypeid;
	private String labortypename;
	private Long contactid;
	private String contactname;
	private String mobileNo;
	private Integer sortOrder;
	List<EventLaborShiftDetailsResponseDto> labourShift;
}
