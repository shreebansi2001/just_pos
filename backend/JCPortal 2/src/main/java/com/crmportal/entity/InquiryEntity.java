package com.crmportal.entity;

import java.time.LocalDateTime;

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
@Table(name = "inquiry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "inquiry_date", columnDefinition = "DATETIME")
	private LocalDateTime inquiryDate;

	@Column(name = "guest_name")
	private String guestName;
	
	@Column(name = "guest_address")
	private String guestAddress;

	@Column(name = "mobile_no")
	private String mobileNo;

	@Column(name = "tentative_date")
	private String tentativeDate;

	@Column(name = "referral_source")
	private String referralSource;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "is_delete")
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "function_name")
	private String functionName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;
}
