package com.crmportal.entity;

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
@Table(name = "event_terms_condition_features")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTermsAndConditionFeaturesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tcf_id")
	private Long id;

	@Column(name = "event_term_condition_id")
	private Long eventTermsConditionId;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "description_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String descriptionHindi;

	@Column(name = "description_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String descriptionGujarati;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = Boolean.FALSE;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
