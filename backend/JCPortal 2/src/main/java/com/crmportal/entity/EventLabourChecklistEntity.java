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
@Table(name = "eventlabour_checklist")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLabourChecklistEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "eventlabour_checklist_id")
	private Long id;
	
	@Column(name = "total_qty")
	private Integer totalQty;
	
	@Column(name = "in_qty")
	private Integer inQty;
	
	@Column(name = "isStatus")
	private Boolean isStatus;
	
	@Column(name = "in_time")
	private String inTime;
	
	@Column(name = "out_time")
	private String outTime;
	
	@Column(name = "shift_id")
	private Long shiftId;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "is_editable",nullable = false)
	private Boolean isEditable = true; 
	
	@Column(name = "labordatetime",columnDefinition = "DATETIME")
	private LocalDateTime labordatetime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity contact;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_category_id")
	private ContactCategoryMasterEntity contactCategory;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
	
	@Column(name = "is_delete",nullable = false)
	private Boolean isDelete = false;
	
	@ManyToOne
    @JoinColumn(name = "event_function_id", nullable = false)
    private EventFunctionMasterEntity eventFunction;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
}
