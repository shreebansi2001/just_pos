package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialCategoryChangeResponseDto {
	private Long id;
	
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
	private Long unit;

	private Long user;
	
	private Long rawMaterialCatId;

	private String rawMaterialCatNameEnglish;
	
	private String rawMaterialCatNameHindi;
	
	private String rawMaterialCatNameGujarati;
	
	private Long partyId;
	
	private String partyNameEnglish;
	
	private String partyNameHindi;
	
	private String partyNameGujarati;
}
