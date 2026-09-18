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
@Table(name = "eventfunction_revision_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionRevisionHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "history_id")
	private Long id;

	@Column(name = "revision_date", columnDefinition = "DATETIME")
	private LocalDateTime revisionDate;

	@Column(name = "approved_by") // user_id
	private Long approvedBy;

	@Column(name = "changes", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String changes;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
