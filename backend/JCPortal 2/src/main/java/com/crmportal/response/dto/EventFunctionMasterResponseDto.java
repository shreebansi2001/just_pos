package com.crmportal.response.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.crmportal.request.dto.FunctionMasterRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMasterResponseDto {

	private Long id;

	private FunctionMasterResponseDto function;

	private String functionStartDateTime;

	private String functionEndDateTime;

	private Integer pax;

	private Double rate;

	private String function_venue;

	private String function_venue_hindi;
	
	private String function_venue_gujarati;
	
	private String notesEnglish;

	private String notesHindi;

	private String notesGujarati;
	
	private Long eventId;
	
	private Integer sortorder;
	
	private String createdAt;
	
	private String foodType;
	
	private String mainFunction;
	
	private String ratePostFix;
	
	private Long   banquetHallId;
	private String banquetHallName;
	private Long   shiftId;
	private String shiftName;
	private String shiftStartTime;
	private String shiftEndTime;
	private String bookingDate;
	
	private Long   customPackageId;
	private String customPackageName;
	private String banquentNotes;
	private List<BanquetHallShiftInfoDto> banquetHallShifts;
}
