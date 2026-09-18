package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.PriorityType;
import com.crmportal.enums.TaskType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "manager_task")
@AllArgsConstructor
@NoArgsConstructor
public class ManagerTaskMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "manager_task_id")
	private Long id;

	@Column(name = "name", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String name;

	@Column(name = "description", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String description;

	@Column(name = "user_id")
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "priority")
	private PriorityType priority;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private TaskType type;

	@Column(name = "sequence")
	private Integer sequence;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
