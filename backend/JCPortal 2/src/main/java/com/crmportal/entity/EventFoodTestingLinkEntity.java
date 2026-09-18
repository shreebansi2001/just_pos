package com.crmportal.entity;

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
@Table(name = "event_food_testing_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingLinkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "event_id")
	private Long eventId;
	
	@Column(name = "event_function_id")
	private Long eventFunctionId;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "tester_id")
	private Long testerId;
	
	@Column(name = "token")
	private String token;
	 
	@Column(name = "access_code")
	private String accessCode;
	
	@Column(name = "expiry_date", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime expiryDate;
	
	@Column(name = "members")
	private Integer members = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;
	
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
    
}
