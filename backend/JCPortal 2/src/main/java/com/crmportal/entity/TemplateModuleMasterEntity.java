package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_module_mst")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateModuleMasterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "template_module_id")
	private Long id;
	
	@Column(name = "name_english")
	private String nameEnglish;
	
	@Column(name = "name_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci")
	private String nameHindi;
	
	@Column(name = "name_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci")
	private String nameGujarati;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name= "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
	
	@OneToMany(mappedBy = "templateModuleMaster", cascade = CascadeType.ALL)
	private List<TemplateMasterEntity> templates;
	
	@OneToMany(mappedBy = "templateModuleMaster", cascade = CascadeType.ALL)
	private List<AdminTemplateModuleEntity> adminTemplateModuleEntity;
}
