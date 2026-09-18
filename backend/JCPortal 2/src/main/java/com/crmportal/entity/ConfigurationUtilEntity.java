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
@Table(name = "user_utility")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationUtilEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "module_id")
	private Long id;

	@Column(name = "user", nullable = false)
	private Long user;

	@Column(name = "counter_name_plate", nullable = true)
	private String counterNamePlate;
	
	@Column(name = "date_format", nullable = true)
	private String dateFormat;

	@Column(name = "time_zone", nullable = true)
	private String timeZone;
	
	@Column(name = "time_format", nullable = true)
	private String timeFormat;
	
	@Column(name = "page_size", nullable = true)
	private String pageSize;

	@Column(name = "two_language_default", nullable = true)
	private String twoLanguageDefault;

	@Column(name = "two_language_preferred", nullable = true)
	private String twoLanguagePreferred;

	@Column(name = "choice_of_menu", nullable = true)
	private String choiceOfMenu;

	@Column(name = "direct_share", nullable = true)
	private String directShare;

	@Column(name = "sac_number", nullable = true)
	private String sacNumber;

	@Column(name = "display_max_person")
	private Boolean displayMaxPerson = true;

	@Column(name = "display_auto_time")
	private Boolean displayAutoTime = true;

	@Column(name = "total_raw_material_report")
	private Boolean totalRawMaterialReport = true;

	@Column(name = "edit_rawmaterial_quantity_before_gen_report")
	private Boolean editRawmaterialQuantityBeforeGenReport = true;

	@Column(name = "font_color", nullable = true)
	private String fontColor;

	@Column(name = "bg_color", nullable = true)
	private String bgColor;

	@Column(name = "combine_report_configuration", nullable = true)
	private String combineReportConfiguration;

	@Column(name = "bg_image", nullable = true)
	private String bgImage;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@Column(name = "catFontId")
	private Long catFontId;
	
	@Column(name = "itemFontId")
	private Long itemFontId;
	
	@Column(name = "sloganFontId")
	private Long sloganFontId;
	
	@Column(name = "catFontSize")
	private Integer catFontSize;
	
	@Column(name = "itemFontSize")
	private Integer itemFontSize;
	
	@Column(name = "sloganFontSize")
	private Integer sloganFontSize;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
