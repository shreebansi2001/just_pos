package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLogsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private String user;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createAt;

	@Column(name = "ip_address", nullable = true)
	private String ipAddress;

	@Column(name = "description", nullable = true,columnDefinition = "TEXT")
	private String description;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@Column(name = "event_id", nullable = false)
	private Long eventId;
}
