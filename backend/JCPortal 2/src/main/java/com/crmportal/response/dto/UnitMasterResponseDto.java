package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.enums.ERangeType;
import com.crmportal.request.dto.UnitRangeRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String symbolEnglish;
	private String symbolHindi;
	private String symbolGujarati;
	private String createdAt;
	private Boolean isActive;
	private Long userId;
	private Boolean isParentUnit = Boolean.FALSE;
	private Integer decimalLimit;
	private Double equivalentValue;
	private ERangeType rangeType;
	private Long stepRangeId;
	private Double stepValue;
	private ParentUnitResponseDto parentUnit;
	private List<UnitRangeResponseDto> ranges; 
	
}
