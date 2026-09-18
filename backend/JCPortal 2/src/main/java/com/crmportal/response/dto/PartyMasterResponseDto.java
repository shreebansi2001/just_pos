package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyMasterResponseDto {

	private Long id;

	private String nameEnglish;

	private String nameHindi;

	private String nameGujarati;

	private String addressEnglish;

	private String addressHindi;

	private String addressGujarati;

	private ContactCategoryMasterResponseDto contact;

	private String email;

	private String mobileno;

	private String altMobileno;

	private String gst;

	private String birthDate;

	private String document;

	private String docPath;

	private String createdAt;

	private String partyCode;

	private Long userId;

	private String token;

	private String tokenType = "Bearer";

	private Long expiresIn;

	private BigDecimal opb;

	private String opbDate;

	private String type;
	
	private String pan;
	
	private BigDecimal price;
	
	private BigDecimal helperPrice;
    
    private BigDecimal counterPrice;
}
