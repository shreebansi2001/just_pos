package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuReportResponseDto {
	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String imagePath;
	private String slogan;
	private String menuNotes;
	private String subCat;
	private String subCatHindi;
	private String subCatGujarati;
	private Integer catItemSpace;
	private String categoryStatus;
	private Boolean isAddOnCat;
	private String catHeadingEnglish;
	private String catHeadingHindi;
	private String catHeadingGujarati;
	List<MenuItemForReportResponseDto> menuItems;
}
