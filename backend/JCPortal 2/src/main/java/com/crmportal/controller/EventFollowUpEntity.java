package com.crmportal.controller;

import java.time.LocalDate;
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
@Table(name = "event_followup")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFollowUpEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_followup_id")
	private Long id;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "manager_id")
	private Long managerId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "followup_date", nullable = false)
	private LocalDate followUpDate;

	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@Column(name = "description",columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String description;

	@Column(name = "is_done",nullable = false)
	private Boolean isDone = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
