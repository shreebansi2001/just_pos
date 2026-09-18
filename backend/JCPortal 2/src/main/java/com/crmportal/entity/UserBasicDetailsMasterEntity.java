package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_basic_details")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserBasicDetailsMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_basic_id")
	private Long id;

	@Column(name = "company_name")
	private String companyName;

	@Column(name = "company_email")
	private String companyEmail;

	@Column(name = "office_no")
	private String officeNo;

	@Column(name = "country_code")
	private String countryCode;

	@Column(name = "address")
	private String address;

	@Column(name = "is_task_access", nullable = false)
	private Boolean isTaskAccess = false;

	@Column(name = "is_attendance_leave_access", nullable = false)
	private Boolean isAttendanceLeaveAccess = false;

	@Column(name = "reporting_manager_id", nullable = false)
	private Long reportingManagerId = 0l;

	@Column(name = "member_type")
	private String memberType;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserMasterEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private CountryMasterEntity country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "state_id")
	private StateMasterEntity state;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id")
	private CityMasterEntity city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private RoleMasterEntity role;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "profile", nullable = true)
	private String profile;

	@Column(name = "sales_id", nullable = false)
	private Long salesId = 0l;

	@Column(name = "manager_id", nullable = false)
	private Long managerId = 0l;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(name = "sales_req", columnDefinition = "TEXT")
	private String salesReq;

	@Column(name = "manager_req", columnDefinition = "TEXT")
	private String managerReq;

	@Column(name = "overall_remarks", columnDefinition = "TEXT")
	private String overAllRemarks;

	@Column(name = "type")
	private String type;

	@Column(name = "services")
	private String services;

	@Column(name = "lang")
	private String lang;

	@Column(name = "theme_color")
	private String themeColor;

	@Column(name = "soft_type", nullable = false)
	private String softType;

	@Column(name = "gst_number")
	private String gstNumber;

	@Column(name = "pan_number")
	private String panNumber;

	@Column(name = "cin_number")
	private String cinNumber;

	@Column(name = "fda_lincense")
	private String fdaLincense;

	@Column(name = "fssai_number")
	private String fssaiNumber;

	@Column(name = "hsn_number")
	private String hsnNumber;

	@Column(name = "followup_day")
	private Integer followupDay;

}
