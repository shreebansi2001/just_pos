package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_mapping")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMappingEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name_english")
	private String nameEnglish;

	@Column(name = "name_hindi")
	private String nameHindi;

	@Column(name = "name_gujarati")
	private String nameGujarati;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "sortorder")
	private Integer sortorder;
	
	@Column(name = "name_plate_type")
	private String namePlateType;
	
	@Column(name = "is_date")
	private Integer isDate;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updateAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_module_id")
	private TemplateModuleMasterEntity templateModule;
}
