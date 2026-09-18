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
@Table(name = "eventfunction_menuallocation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuAllocationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_allocation_id")
	private Long id;

	@Column(name = "chef_labour")
	private Boolean chefLabour = false;

	@Column(name = "outside")
	private Boolean outside = false;

	@Column(name = "inside")
	private Boolean inside = false;

	@Column(name = "person_count", nullable = false)
	private Integer personCount;

	@Column(name = "place")
	private String place = "At venue";

	@Column(name = "instructions", columnDefinition = "TEXT")
	private String instructions;
	
	@Column(name = "instructions_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionsHindi;
	
	@Column(name = "instructions_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String instructionsGujarati;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventMasterEntity event;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventfunction_id", nullable = false)
    private EventFunctionMasterEntity eventFunction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemMasterEntity menuItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_category_id", nullable = false)
    private MenuCategoryMasterEntity menuCategory;
    
    @Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "menu_category_sort_order")
	private Integer menuCategorySortOrder;
	
	@Column(name = "menu_item_sort_order")
	private Integer menuitemSortOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

	@Column(name = "is_pax_change")
    private Boolean isPaxChange = Boolean.FALSE;

}
