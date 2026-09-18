package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Entity
@Table(name = "userpaymentinfo")
public class PaymentInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;
	
	@Column(name = "userpaymentid")
	private String userpaymentid;
	
	@Column(name = "payid")
	private String payid;
	
	@Column(name = "paysignature")
	private String paysignature;
	
	@Column(name = "paymentresponse", length = 15000)
	private String paymentresponse;
	
	@Column(name = "amount")
	private Float amount;
	
	@Column(name = "odId")
	private String odId;
	
	@Column(name = "internalorderid")
	private String internalorderid;
	
	@Column(name = "paidamount")
	private Float paidamount;
	
	@Column(name = "surcharge")
	private Float surcharge;
	
	@Column(name = "paymentdone")
	private Boolean paymentdone;
	
	@Column(name = "payment_type")
	private String paymentType;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "doc_path")
	private String docPath;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "paymentdonetimestamp", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime paymentdonetimestamp;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_plan_Id")
	private UserPlansHistoryEntity userPlanHist;
	
	@Column(name = "user_upgrademodule_id")
	private Long userUpgradedModuleId;
	
	@Column(name = "user_notification_id")
	private Long userNotificationId;
	
	@Column(name = "user_ai_template_id")
	private Long userAiTemplateId;
	
	
}
