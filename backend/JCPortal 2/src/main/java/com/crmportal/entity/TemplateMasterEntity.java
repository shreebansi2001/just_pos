package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "template_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "template_id")
	private Long id;
	
	@Column(name = "template_name")
	private String name;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updateAt;
	
	@Column(name = "heading_font_color")
	private String headingFontColor;
	
	@Column(name = "content_font_color")
	private String contentFontColor;
	
	@Column(name = "description_font_color")
	private String descriptionFontColor;
	
	@Column(name = "front_page")
	private String frontPage;
	
	@Column(name = "second_front_page")
	private String secondFrontPage;
	
	@Column(name = "watermark")
	private String watermark;
	
	@Column(name = "last_main_page")
	private String lastMainPage;
	
	@Column(name = "is_nameplate")
	private Boolean isNamePlate;
	
	@Column(name = "nameplate_bg")
	private String namePlateBg;
	
	@Column(name = "nameplate_cover_bg")
	private String namePlateCoverBg;
	
	@Column(name = "cat_bg_page")
	private String catBgPage;
	
	@Column(name = "extra_page")
	private String extraPage;

	@Column(name = "description")
	private String description;
	
	@Column(name = "price")
	private BigDecimal price;

	@Column(name = "is_default")
	private Boolean isDefault;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_module_id")
	private TemplateModuleMasterEntity templateModuleMaster;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_mapping_id")
	private TemplateMappingEntity templateMapping;
	
	@Column(name = "dummy_pdf")
	private String dummyPdf;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
	
	@OneToMany(mappedBy = "templateMaster", cascade = CascadeType.ALL)
	private List<AdminTemplateModuleEntity> adminModuleTemplateEntity;
	
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
}
