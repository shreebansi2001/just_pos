package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_terms_and_condition")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTermsAndConditionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "terms_id")
	private Long id;
	
	@Column(name = "name_english")
	private String nameEnglish;
	
	@Column(name = "name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;
	
	@Column(name = "event_id")
	private Long eventId;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserMasterEntity user;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = Boolean.FALSE;
	
}
