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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.aspectj.weaver.tools.Trace;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "userBasicDetails")
public class UserMasterEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_Id")
	private Long id;
	
	@Column(name = "email",nullable = false, unique = true)
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "contact_no",nullable = false)
	private String contactNo;
	
	@Column(name = "otp")
	private String otp;
	
	@Column(name = "first_name",nullable = false)
	private String firstName;
	
	@Column(name = "last_name",nullable = false)
	private String lastName;
	
	@Column(name = "client_id")
	private Long clientId;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = false;
	
	@Column(name = "is_approve", nullable = false)
	private Boolean isApprove = false;	
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "isFirstTime", nullable = false)
	private Boolean isFirstTime = true;
	
	@Column(name = "isBlock", nullable = false)
	private Boolean isBlock = false;
	
	@Column(name = "usercode")
	private String userCode;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "pre_fix")
	private String preFix;
	
	@Column(name = "unique_code",nullable = false)
	private String uniqueCode;
	
	@Column(name = "logo")
	private String logo;
	
	@Column(name = "allowedip")
	private String allowedip = "";
	
	@Column(name = "ischilduser", nullable = false)
	private Boolean ischilduser = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	 @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	 private UserBasicDetailsMasterEntity userBasicDetails;
	 
	 @Column(name = "is_visible", nullable = false, columnDefinition = "BIT(1) DEFAULT b'1'")
		private Boolean isVisible = true;
	 
	 @Column(name = "is_inquiry_visible", nullable = false, columnDefinition = "BIT(1) DEFAULT b'1'")
		private Boolean isInquiryVisible = true;
	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuCategoryMasterEntity> menuCategories = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuSubCategoryMasterEntity> menuSubCategories = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuItemMasterEntity> menuItems = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<ContactTypeMasterEntity> contactTypes = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<RawMaterialCategoryTypeMasterEntity> rawMaterialCategoryTypes = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<RawMaterialSupplierEntity> rawMaterialSuppliers = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<UnitMasterEntity> units = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<CustomPackageEntity> packages = new ArrayList<>();
}
