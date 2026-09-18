package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitHierarchyDto {

	private Long unitId;
    private String nameEnglish;
    private String nameHindi;
    private String nameGujarati;
    private String symbolEnglish;
	private String symbolHindi;
	private String symbolGujarati;
    private List<UnitChildDto> children;
}
