package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AITemplateRequestDto {

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
	private BigDecimal price;
	private String billingCycle;
	private String aiModel;
}
