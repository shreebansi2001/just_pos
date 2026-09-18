package com.crmportal.response.dto;

import com.itextpdf.io.image.ImageData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryImagePageInfo {

	private int startPage;
	
	private int endPage;
	
	private ImageData imageData;
	
	private boolean imageOnRight;
}
