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
@Table(name = "guest_signature")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestSignatureEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "guest_signature_id")
	private Long id;
	
	@Column(name = "event_id", nullable = false)
	private Long eventId;
	
	@Column(name = "event_function_id", nullable = false)
	private Long eventFunctionId;
	
	@Column(name = "particulars")
	private String particulars;
	
	@Column(name = "persons")
	private Integer persons;
	
	@Column(name = "extra")
	private Integer extra;

	@Column(name = "user_id")
	private Long userId;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
}
