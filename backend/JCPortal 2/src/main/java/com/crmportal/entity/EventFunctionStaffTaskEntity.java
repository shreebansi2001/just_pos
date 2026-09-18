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
@Table(name = "eventfunction_staff_task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionStaffTaskEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "eventfunction_staff_task_id")
	private Long id;

	@Column(name = "task_id", nullable = true)
	private Long taskId;
	
	@Column(name = "task_name",columnDefinition = "TEXT")
	private String taskName;
	
	@Column(name = "task_name_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String taskNameHindi;

	@Column(name = "task_name_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String taskNameGujarati;

	@Column(name = "remarks", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarks;
	
	@Column(name = "assignment_id")
	private Long assignmentId;

	@Column(name = "is_completed", nullable = false)
	private Boolean isCompleted = Boolean.FALSE;

	@Column(name = "is_common_task", nullable = false)
	private Boolean isCommonTask = Boolean.TRUE;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "completedAt", columnDefinition = "DATETIME")
	private LocalDateTime completedAt;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
