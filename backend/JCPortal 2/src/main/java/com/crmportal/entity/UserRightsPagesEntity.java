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
@Table(name = "userrights_pages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRightsPagesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userrights_pages_id")
	private Long id;

	@Column(name = "pagename", columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String pagename;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "is_active")
	private Boolean isActive = true;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "module_id")
	private Long moduleId;

}