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
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "userrights_module")
public class UserRightsModuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userrights_module_id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "is_admin_module")
	private Boolean isAdminModule;
	
	@Column(name = "is_delete",nullable = false)
	private Boolean isDelete = Boolean.FALSE;
	
	@Column(name = "is_active")
	private Boolean isActive = Boolean.TRUE;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
}
