package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLabourCheckListResponseDto {

	private String shiftName;
	private List<EventLabourCheckListDataResponse> eventLabourCheckLists;
}
