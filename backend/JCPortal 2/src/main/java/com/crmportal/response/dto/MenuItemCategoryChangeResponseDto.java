package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuSubCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemCategoryChangeResponseDto {

	private Long id;
	
	private String nameEnglish;

	private String nameHindi;
	
	private String nameGujarati;
	
    private Long menuCategoryId;
    
    private String menuCategoryNameEnglish;

    private String menuCategoryNameHindi;

    private String menuCategoryNameGujarati;
    
    private Long menuSubCategoryId;
    
    private String menuSubCategoryNameEnglish;

    private String menuSubCategoryNameHindi;

    private String menuSubCategoryNameGujarati;
	
    private Long menuItemAllocationId;
    
    private String allocationType;
    
    private BigDecimal base_price;
    
    private String counterNo;
    
    private String helperNo;
    
    private String godownLocation;
    
    private BigDecimal pricePerHelper;
    
    private BigDecimal pricePerLabour;
    
    private BigDecimal qtyPer100Person;
    
    private Boolean chefLabourAgency;

    private Boolean outsideAgency;

    private Boolean insideAgency;
    
    private Long supplierId;
    
    private String supplierNameEnglish;
    
    private String supplierNameHindi;
    
    private String supplierNameGujarati;
    
    private Long contactCategoryId;
    
    private String contactNameEnglish;
    
    private String contactNameHindi;
    
    private String contactNameGujarati;
    
    private Long unitId;
    
    private String unitNameEnglish;
    
    private String unitNameGujarati;
    
    private String unitNameHindi;
    
    private String symbolEnglish;
    
    private String symbolGujarati;
    
    private String symbolHindi;
    
    private Long user;
    
    private String number;
    
    private String remarks;

}
