package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_template")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AITemplateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ai_template_id")
	private Long id;

	@Column(name = "name_english", columnDefinition = "TEXT")
	private String nameEnglish;

	@Column(name = "name_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;

	@Column(name = "instruction_english", columnDefinition = "TEXT")
	private String instructionEnglish;

	@Column(name = "instruction_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionHindi;

	@Column(name = "instruction_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionGujarati;

	@Column(name = "rule_english", columnDefinition = "TEXT")
	private String ruleEnglish;

	@Column(name = "rule_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String ruleHindi;

	@Column(name = "rule_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String ruleGujarati;

	@Column(name = "query_instruction", columnDefinition = "TEXT")
	private String queryInstruction;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "price")
	private BigDecimal price;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "billing_cycle")
	private String billingCycle;
	
	@Column(name = "ai_model")
	private String aiModel;
}
