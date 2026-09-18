package com.crmportal.entity;

import java.time.LocalDate;
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
@Table(name = "tap_inquiry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TapInquiryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tap_inquiry_id")
	private Long id;

	@Column(name = "company_name")
	private String companyName;

	@Column(name = "country_code")
	private String countryCode;

	@Column(name = "contact_no")
	private String contactNo;

	@Column(name = "function_name")
	private String functionName;

	@Column(name = "venue_address")
	private String venueAddress;

	@Column(name = "event_date")
	private String eventDate;

	@Column(name = "event_time")
	private String eventTime;

	@Column(name = "total_tab")
	private Integer totalTab;

	@Column(name = "time_slot")
	private String timeSlot;

	@Column(name = "city_id")
	private Long cityId;

	@Column(name = "state_id")
	private Long stateId;

	@Column(name = "estimated_budget")
	private Double estimatedBudget;

	@Column(name = "is_delete")
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
