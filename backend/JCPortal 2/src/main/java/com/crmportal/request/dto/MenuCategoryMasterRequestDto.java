package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryMasterRequestDto {

	 @NotBlank(message = "Name English is required")
	    private String nameEnglish;

	    private String nameGujarati;

	    private String nameHindi;

	    private String menuSlogan;

	    private BigDecimal price;

	    private Integer sequence = 1;

	    @NotNull(message = "User Id is required")
	    private Long userId;
	    
		private String reportNameEnglish;
		
		private String reportNameHindi;
		
		private String reportNameGujarati;
	    
}