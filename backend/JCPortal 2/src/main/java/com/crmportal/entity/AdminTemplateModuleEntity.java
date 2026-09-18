package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.List;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_template_module")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminTemplateModuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_module_master_id", nullable = false)
	private TemplateModuleMasterEntity templateModuleMaster;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_master_id", nullable = false)
	private TemplateMasterEntity templateMaster;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_mapping_id", nullable = false)
	private TemplateMappingEntity templateMapping;
	
	@Column(name = "cat_font_id")
	private Long catFontId;
	
	@Column(name = "item_font_id")
	private Long itemFontId;
	
	@Column(name = "slogan_font_id")
	private Long sloganFontId;
	
	@Column(name = "cat_font_size")
	private Integer catFontSize;
	
	@Column(name = "item_font_size")
	private Integer itemFontSize;
	
	@Column(name = "slogan_font_size")
	private Integer sloganFontSize;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
}
