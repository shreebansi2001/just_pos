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
@Table(name = "eventfunction_feedback")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionFeedbackEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "feedback_id")
	private Long id;

	@Column(name = "name", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String name;

	@Column(name = "mobileno")
	private String mobileno;

	@Column(name = "provided_services")
	private Integer providedServices;

	@Column(name = "satisfied_services")
	private Integer satisfiedServices;

	@Column(name = "expectation")
	private Integer expectation;

	@Column(name = "overall_experience")
	private Integer overallExperience;

	@Column(name = "recommend")
	private String recommend;

	@Column(name = "description", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String description;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "eventfunction_id")
	private Long eventFunctionId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "member_id")
	private Long memberId;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}