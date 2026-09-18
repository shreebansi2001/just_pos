package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userrights")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRightsMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userrights_id")
	private Long id;

	@Column(name = "addaccess")
	private Boolean addaccess;

	@Column(name = "editaccess")
	private Boolean editaccess;
	
	@Column(name = "deleteaccess")
	private Boolean deleteaccess;
	
	@Column(name = "viewaccess")
	private Boolean viewaccess;
	
	@Column(name = "pageid")
	private Long pageid;
	
	@Column(name = "roleid")
	private Long roleid;
	
	@Column(name = "module_id")
	private Long moduleId;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
}
