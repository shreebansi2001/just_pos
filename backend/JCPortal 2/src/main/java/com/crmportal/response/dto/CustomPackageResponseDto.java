package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPackageResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameGujarati;
	private String nameHindi;
	private BigDecimal price;
	private Integer sequence;
	private Boolean isActive;
	private Long userId;
	private List<CustomPackageDetailsResponseDto> customPackageDetails;
}
