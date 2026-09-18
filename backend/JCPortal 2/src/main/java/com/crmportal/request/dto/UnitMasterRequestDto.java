package com.crmportal.request.dto;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;

import com.crmportal.enums.ERangeType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitMasterRequestDto {

	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String symbolEnglish;
	private String symbolHindi;
	private String symbolGujarati;
	private Long userId;
	private Boolean isParentUnit = Boolean.FALSE;
	private Long parent_unit_id;
	private Integer decimalLimit;
	private Double equivalentValue;
	private ERangeType rangeType;
	private Double stepValue;
	 private List<UnitRangeRequestDto> ranges; 
	
}
