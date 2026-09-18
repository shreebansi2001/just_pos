package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.crmportal.response.dto.MenuItemCaptainReceipeRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemMasterRequestDto {

	@NotBlank(message = "Name (English) name is required")
    private String nameEnglish;

    private String nameGujarati;

    private String nameHindi;
    
	private String instructionEnglish;
	private String instructionHindi;
	private String instructionGujarati;

    private String slogan;

    private BigDecimal price;

    private Integer sequence = 1;

    @NotNull(message = "Menu category is required")
    private Long menuCategoryId;

    private Long menuSubCategoryId;

    @NotNull(message = "User ID is required")
    private Long userId;
    
    
    private List<MenuItemRawMaterialRequestDto> menuItemRawMaterials; 
    
    private List<MenuItemCaptainReceipeRequestDto> captainReceipes;
    
	private MenuItemAllocationConfigRequestDto menuItemAllocationConfigRequest;

	private BigDecimal totalRate;
	
	private BigDecimal dishCosting;
	
	private String url;

	private String remarks;
	
}
