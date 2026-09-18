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
@Table(name = "event_food_testing_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingDetailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_food_testing_detail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_food_testing_id")
	private EventFoodTestingEntity eventFoodTesting;
	
	@Column(name = "menu_cat_id")
	private Long menuCatId;
	
	@Column(name = "menu_cat_name_english")
	private String menuCatNameEnglish;
	
	@Column(name = "menu_cat_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuCatNameHindi;
	
	@Column(name = "menu_cat_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuCatNameGujarati;
	
	@Column(name = "cat_notes_english")
	private String catNotesEnglish;
	
	@Column(name = "cat_notes_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String catNotesHindi;
	
	@Column(name = "cat_notes_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String catNotesGujarati;
	
	@Column(name = "menu_item_id")
	private Long menuItemId;
	
	@Column(name = "menu_item_name_english")
	private String menuItemNameEnglish;
	
	@Column(name = "menu_item_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuItemNameHindi;
	
	@Column(name = "menu_item_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuItemNameGujarati;
	
	@Column(name = "item_notes_english")
	private String itemNotesEnglish;
	
	@Column(name = "item_notes_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNotesHindi;
	
	@Column(name = "item_notes_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNotesGujarati;
	
	@Column(name = "item_slogan")
	private String itemSlogan;

	@Column(name = "cat_sort_order")
	private Integer catSortOrder;
	
	@Column(name = "item_sort_order")
	private Integer itemSortOrder;
	
	@Column(name = "client_notes")
	private String clientNotes;
	
	@Column(name = "client_notes_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String clientNotesHindi;
	
	@Column(name = "client_notes_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String clientNotesGujarati;
	
	@Column(name = "review")
	private String review;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "is_delete")
	private Boolean isDelete = false;
}
