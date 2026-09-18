package com.crmportal.entity;	

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
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
@Table(name = "LeadMaster")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_id")
	private Long id;
	
	@Column(name = "lead_code")
	private String leadCode;
	
	@Column(name = "company_name")
	private String companyName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id")
	private PlansEntity plan;
	
	@Column(name = "client_name")
	private String clientName;
	
	@Column(name = "contact_number")
	private String contactNumber;
	
	@Column(name = "alternate_number")
	private String alternateNumber;
	
	@Column(name = "email_id")
	private String emailId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id")
	private CityMasterEntity city;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "state_id")
	private StateMasterEntity state;
	
	@Column(name = "lead_remark")
	private String leadRemark;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "lead_status_id")
    private LeadStatusEntity leadStatus;
	
	@Column(name = "lead_Type")
	private String leadType;
	
	@Column(name = "lead_priority")
	private String leadPriority;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "lead_source_id")
    private LeadSourceEntity leadSource;
	
	@Column(name = "medium")
	private String medium;
	
	@Column(name = "campaign")
	private String campaign;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_assign_id")
	private UserMasterEntity leadAssign;
	
	@Column(name = "deal_value")
	private String dealValue;
	
	@Column(name = "closeDate")
	private LocalDate closeDate;
	
	@Column(name = "event_type_id") 
	private Long eventTypeId;
	
	@Column(name = "inquiry_date") 
	private LocalDate inquiryDate;
	
	@Column(name = "min_pax")
	private Integer minPax;

	@Column(name = "max_pax")
	private Integer maxPax;
	
	@Column(name = "any_function_with_us")
	private Boolean anyFunctionWithUs;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private UserMasterEntity user;
	
	@Column(name = "description") 
	private String description;
	/*
	 * @Column(name = "address") private String address;
	 * 
	 * @Column(name = "lead_title") private String leadTitle;
	 * 
	 * @Column(name = "lead_status") private String leadStatus;
	 * 
	 * @Column(name = "lead_source") private String leadSource;
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	 * 
	 * @JoinColumn(name = "lead_subsource_id") private LeadSubSourceEntity
	 * leadSubSource;
	 * 
	 * @Column(name = "event_type_id") private Long eventTypeId;
	 * 
	 * 
	 * 
	 * 
	 * @Column(name = "estimate_amount") private BigDecimal estimateAmount;
	 * 
	 * @Column(name = "lead_follow_up_date", columnDefinition = "DATETIME") private
	 * LocalDateTime leadFollowUpDate;
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "pipeline_id") private PipelineEntity pipeline;
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "close_stage_id") private PipelineCloseStageEntity
	 * closeStage;
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "open_stage_id") private PipelineOpenStageEntity
	 * openStage;
	 * 
	 * @Column(name = "select_prefix") private String selectPrefix;
	 * 
	 * 
	 * 
	 * @Column(name = "pin_code") private String pinCode;
	 * 
	 * @Column(name = "overall_remark") private String overallRemark;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @Column(name = "actual_close_date", columnDefinition = "DATETIME") private
	 * LocalDateTime actualCloseDate;
	 * 
	 * @Column(name = "function_id") private Long functionId;
	 * 
	 * 
	 * 
	 * @Column(name = "referral_source") private String referralSource;
	 * 
	 * @Column(name = "inquiry_date") private LocalDate inquiryDate;
	 */
}
