package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "decore_main_category_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoreMainCategoryItemMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "decore_item_id")
	private Long id;

	@Column(name = "name_english")
	private String nameEnglish;

	@Column(name = "name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;

	@Column(name = "instruction_english", columnDefinition = "TEXT")
	private String instructionEnglish;

	@Column(name = "instruction_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionHindi;

	@Column(name = "instruction_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionGujarati;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "is_published")
	private Boolean isPublished = true;

	@Column(name = "slogan")
	private String slogan;

	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;

	@Column(name = "sequence")
	private Integer sequence;

	@ManyToOne
	@JoinColumn(name = "decore_main_category_id")
	private DecoreMainCategoryMasterEntity decoreMainCategory;

	@Column(name = "url")
	private String url;

	@Column(name = "remarks")
	private String remarks;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserMasterEntity user;

	@Column(name = "is_delete")
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "uuid")
	private String uuid;

}