package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.PriorityType;
import com.crmportal.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "special_notes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialNotesEntity {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "special_notes_id")
	private Long id;

	@Column(name = "name",columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String name;
	
	@Column(name = "description",columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String description;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "manager_id")
	private Long managerId;
	
	@Column(name = "remarks",columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarks;
	
	@Column(name = "priority")
	private PriorityType priority;
	
	@Column(name = "status")
	private StatusType status;
	
	@Column(name = "event_id")
	private Long eventId;
	
	@Column(name = "sequence")
	private Integer sequence;
	
	@Column(name = "event_function_id")
	private Long eventFunctionId;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	
}
