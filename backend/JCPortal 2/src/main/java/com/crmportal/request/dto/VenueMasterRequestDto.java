package com.crmportal.request.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueMasterRequestDto {
	
	private Long id;
	private String nameEnglish;
	private String nameGujarati;
	private String nameHindi;
	private Long userId;
}
