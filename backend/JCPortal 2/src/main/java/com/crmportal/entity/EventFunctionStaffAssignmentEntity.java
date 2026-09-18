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

import com.crmportal.enums.AllocationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eventfunction_staff_assignment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionStaffAssignmentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "assignment_id")
	private Long id;

	// common field
	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "eventfunction_id")
	private Long eventFunctionId;

	@Column(name = "vendor_id")
	private Long vendorId;
	
	@Column(name = "menu_item_id")
	private Long menuItemId;
	
	@Column(name = "instructions", columnDefinition = "TEXT")
	private String instructions;
	
	@Column(name = "instructions_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionsHindi;
	
	@Column(name = "instructions_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionsGujarati;

	@Column(name = "resource_type")
	private AllocationType resourceType;

	@Column(name = "staff_category")
	private String staffCategory;

	@Column(name = "reporting_time")
	private String reportingTime;

	@Column(name = "arrival_time")
	private String arrivalTime;

	@Column(name = "is_present")
	private Boolean isPresent;

	@Column(name = "status")
	private String status;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "user_id")
	private Long userId;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	// chef / outside
	@Column(name = "weight")
	private BigDecimal weight;

	@Column(name = "labour_qty")
	private Integer labourQty;

	@Column(name = "helpers_qty")
	private Integer helpersQty;

	@Column(name = "unit_id")
	private Long unitId;

	// labour field
	@Column(name = "shift_name")
	private String shiftName;

	@Column(name = "assigned_qty")
	private Integer assignedQty;

	@Column(name = "confirmed_qty")
	private Integer confirmedQty;
	
	@Column(name = "latitude")
	private String latitude;
	
	@Column(name = "longitude")
	private String longitude;

}
