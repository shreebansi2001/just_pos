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
@Table(name = "eventfunction_manager_assign")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionManagerAssignEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "efma_id")
	private Long id;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "eventfunction_id")
	private Long eventFunctionId;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "manager_id")
	private Long managerId;
	
	@Column(name = "user_id")
	private Long userId;
	
}
