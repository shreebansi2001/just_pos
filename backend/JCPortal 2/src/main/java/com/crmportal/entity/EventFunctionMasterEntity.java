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
@Table(name = "event_function")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventFunctionMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_function_id")
	private Long id;

	@Column(name = "function_start_date_time",columnDefinition = "DATETIME")
	private LocalDateTime functionStartDateTime; // Change from String to LocalDateTime

	@Column(name = "function_end_date_time",columnDefinition = "DATETIME")
	private LocalDateTime functionEndDateTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "function_master_id")
	private FunctionMasterEntity function;

	@Column(name = "pax")
	private Integer pax;

	@Column(name = "rate")
	private Double rate;

	@Column(name = "function_venue")
	private String function_venue;
	
	@Column(name = "food_type")
	private String foodType;
	
	@Column(name = "main_function")
	private String mainFunction;
	
	@Column(name = "rate_post_fix")
	private String ratePostFix;
	
	@Column(name = "function_venue_hindi")
	private String function_venue_hindi;
	
	@Column(name = "function_venue_gujarati")
	private String function_venue_gujarati;

	@Column(name = "notes_english")
	private String notesEnglish;

	@Column(name = "notes_hindi")
	private String notesHindi;

	@Column(name = "notes_gujarati")
	private String notesGujarati;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "custom_package_id", nullable = true)
	private CustomPackageEntity customPackage;

	@Column(name = "sortorder",nullable = false)
	private Integer sortorder;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isUpdate", nullable = false)
	private Boolean isUpdate = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "banquent_notes", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci",nullable = true)
	private String banquentNotes;
	
	//	
//	@OneToMany(mappedBy = "eventFunction", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuPreparationEntity> menuPraparations = new ArrayList<>();

}
