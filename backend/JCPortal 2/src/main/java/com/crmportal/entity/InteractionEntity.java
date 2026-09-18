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
@Table(name = "interaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "interaction_id")
	private Long id;

	@Column(name = "interactionname", columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String interactionname;

	@Column(name = "interactiontype", columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String interactiontype;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "is_active")
	private Boolean isActive = true;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

}
