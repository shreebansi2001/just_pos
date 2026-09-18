package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AITemplateResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String instructionEnglish;
	private String instructionHindi;
	private String instructionGujarati;
	private String ruleEnglish;
	private String ruleHindi;
	private String ruleGujarati;
	private String queryInstruction;
	private Boolean isActive;
	private String createdAt;
	private BigDecimal price;
	private String billingCycle;
	private String aiModel;
}
