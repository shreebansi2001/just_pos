package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.AllocationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eventground_task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventGroundTaskMasterEntity {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	@Column(name = "task_id")
	private Long id;
	
	@Column(name = "resource_type")
	private AllocationType resourceType;
	
	@Column(name = "name_english",columnDefinition = "TEXT")
	private String nameEnglish;
	
	@Column(name = "name_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "is_true", nullable = false)
	private Boolean isTrue = true;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private UserMasterEntity user;
}
