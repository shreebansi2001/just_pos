package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoreReportResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String imagePath;
	private String slogan;
	private String decoreNotes;
	private String subCat;
	private String subCatHindi;
	private String subCatGujarati;
	private BigDecimal price; 
	List<DecoreItemReportResponseDto> decoreItems;
}
