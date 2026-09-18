package com.crmportal.entity;

import java.math.BigDecimal;
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
@Table(name = "user_notification_config")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserNotificationConfigEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unc_id")
	private Long id;

	@Column(name = "upgrade_module_id")
	private Long upgradeModuleId;

	//appKey
	@Column(name = "key1",columnDefinition = "TEXT")
	private String key1;
	
	//authKey
	@Column(name = "key2",columnDefinition = "TEXT")
	private String key2;
	
	@Column(name = "url",columnDefinition = "TEXT")
	private String url;
	
	@Column(name = "userId")
	private Long userId;
	
	@Column(name = "is_active",nullable = true)
	private Boolean isActive = Boolean.TRUE;
	
	@Column(name = "is_delete",nullable = true)
	private Boolean isDelete = Boolean.FALSE;
	
	@Column(name = "is_pay_done", nullable = false)
	private Boolean isPayDone = Boolean.FALSE;
	
	@Column(name = "pay_amount")
	private BigDecimal payAmount = BigDecimal.ZERO;
	
	@Column(name = "start_date", columnDefinition = "DATETIME")
	private LocalDateTime startDate;

	@Column(name = "end_date", columnDefinition = "DATETIME")
	private LocalDateTime endDate;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
