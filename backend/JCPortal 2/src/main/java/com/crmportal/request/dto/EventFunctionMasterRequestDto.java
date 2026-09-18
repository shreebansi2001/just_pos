package com.crmportal.request.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMasterRequestDto {

	private Long eventFuncId; // can be null when creating, required for update

	@NotNull(message = "Function Id is required")
	private Long functionId;

	@NotBlank(message = "Function Start Date & Time is required")
	private String functionStartDateTime;

	@NotBlank(message = "Function End Date & Time is required")
	private String functionEndDateTime;

	@NotNull(message = "Pax is required")
	@Min(value = 1, message = "Pax must be at least 1")
	private Integer pax;

	private Double rate;

	private String foodType;
	
	private String mainFunction;
	
	private String ratePostFix;
	
	private String function_venue;
	
	private String function_venue_hindi;
	
	private String function_venue_gujarati;

	private String notesEnglish;

	private String notesHindi;

	private String notesGujarati;

	private Integer sortorder;
	
	private Long customPackageId;
	
	private String banquentNotes;
	
	private List<BanquetHallShiftRequestDto> banquetHallShifts;

}
