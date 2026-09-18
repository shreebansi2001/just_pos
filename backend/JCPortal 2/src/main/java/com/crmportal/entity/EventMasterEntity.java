package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long id;

	@Column(name = "event_no")
	private String eventNo;

	@Column(name = "inquiry_date")
	private LocalDate inquiryDate;

	@Column(name = "event_start_date_time",columnDefinition = "DATETIME")
	private LocalDateTime eventStartDateTime;

	@Column(name = "event_end_date_time",columnDefinition = "DATETIME")
	private LocalDateTime eventEndDateTime;

	@Column(name = "status")
	private Integer status;
	
	@Column(name = "is_r_menu", nullable = false)
	private Boolean isRMenu = true;

	@Column(name = "address")
	private String address;

	@Column(name = "mobileno")
	private String mobileno;

	@Column(name = "is_high_priority")
	private String isHighPriority;

	@Column(name = "reference")
	private String reference;

	@Column(name = "meal_notes")
	private String meal_notes;

	@Column(name = "meal_notes_hindi")
	private String meal_notes_hindi;
	
	@Column(name = "meal_notes_gujarati")
	private String meal_notes_gujarati;
	
	@Column(name = "service")
	private String service;
	
	@Column(name = "service_hindi")
	private String serviceHindi;
	
	@Column(name = "service_gujarati")
	private String serviceGujarati;

	@Column(name = "theme")
	private String theme;
	
	@Column(name = "theme_hindi")
	private String themeHindi;
	
	@Column(name = "theme_gujarati")
	private String themeGujarati;

	@Column(name = "remark")
	private String remark;
	
	@Column(name = "remarks_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarksHindi;

	@Column(name = "remarks_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarksGujarati;
	
	@Column(name = "groom_name")
	private String groomName;

	@Column(name = "groom_insta_link")
	private String groomInstaLink;

	@Column(name = "groom_birth_date")
	private LocalDate groomBirthDate;

	@Column(name = "groom_community")
	private String groom_community;

	@Column(name = "groom_mobileno")
	private String groomMobileno;

	@Column(name = "bride_name")
	private String brideName;

	@Column(name = "bride_insta_link")
	private String brideInstaLink;

	@Column(name = "bride_birth_date")
	private LocalDate brideBirthDate;

	@Column(name = "bride_community")
	private String bride_community;

	@Column(name = "bride_mobileno")
	private String brideMobileno;

	@Column(name = "prefix")
	private String prefix;

	@Column(name = "billing_name_english")
	private String billingNameEnglish;
	
	@Column(name = "billing_name_hindi")
	private String billingNameHindi;
	
	@Column(name = "billing_name_gujarati")
	private String billingNameGujarati;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id")
	private UserMasterEntity manager;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_type_id")
	private EventTypeMasterEntity eventType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venue_id")
	private VenueMasterEntity venue;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "banquet_hall_id")
	private BanquetHallMasterEntity banquetHall;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity party;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meal_type_id")
	private MealTypeMasterEntity mealType;

//	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<EventFunctionMasterEntity> eventFunctions = new ArrayList<>();
//	
//	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<EventFunctionQuotationEntity> quotation = new ArrayList<>();

	@Column(name = "menu_prep_status")
	private String menuPreparationStatus;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "cordinator_person_name_english")
	private String cordinatorPersonNameEnglish;
	
	@Column(name = "cordinator_person_name_hindi")
	private String cordinatorPersonNameHindi;
	
	@Column(name = "cordinator_person_name_gujarati")
	private String cordinatorPersonNameGujarati;
	
	@Column(name = "cordinator_person_contact_no")
	private String cordinatorPersonContactNo;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "childuserid", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private Long childuserid = 0l;
	
	@Column(name = "permissable_item", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String permissable_item;
	
	@Column(name = "not_permissable_item", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String not_permissable_item;
	
	@Column(name = "rate_discussion", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String rate_discussion;
	
	@Column(name = "internal_staff_discussion", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String internal_staff_discussion;
	
}
