package com.crmportal.entity;

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
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactCategoryMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contact_category_id")
	private Long id;

	@Column(name = "name_english", nullable = false)
	private String nameEnglish;

	@Column(name = "name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@Column(name = "sequence")
	private Integer sequence;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
//
//	@OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
//	private List<PartyMasterEntity> parties;
//	
//	@OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
//	private List<MenuItemAllocationConfigEntity> menuItemAllocationConfigs;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_type_id")
	private ContactTypeMasterEntity contactType;

}
