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

import com.crmportal.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "eventfunction_managertask")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EventFunctionManagerTaskEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "eventfunction_managertask_id")
	private Long id;

	@Column(name = "manager_task_id")
	private Long managerTaskId;

	@Column(name = "manager_id")
	private Long managerId;

	@Column(name = "eventfunction_id")
	private Long eventFunctionId;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "remarks", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarks;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "completed_at", columnDefinition = "DATETIME")
	private LocalDateTime completedAt;

	@Column(name = "latitude")
	private String latitude;
	
	@Column(name = "longitude")
	private String longitude;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private StatusType status;
}
