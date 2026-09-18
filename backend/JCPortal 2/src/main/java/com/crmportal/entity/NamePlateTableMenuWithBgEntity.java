package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.annotation.Generated;
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
@Table(name = "name_plate_table_menu_with_bg")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePlateTableMenuWithBgEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "name_plate_id")
	private Long id;
	
	@Column(name = "item_name_english")
	private String itemNameEnglish;
	
	@Column(name = "item_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNameHindi;
	
	@Column(name = "item_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNameGujarati;
	
	@Column(name = "menu_item_id")
	private Long menuItemId;
	
	@Column(name = "event_function_id")
	private Long eventFunctionId;
	
	@Column(name = "sequence")
	private BigDecimal sequence;
	
	@Column(name = "item_count")
	private BigDecimal itemCount;
	
	@Column(name = "is_checked")
	private Boolean isChecked;
	
	@Column(name = "cat_font_size")
	private Integer catFontSize;
	
	@Column(name = "item_font_size")
	private Integer itemFontSize;
	
	@Column(name = "header_notes_english")
	private String headerNotesEnglish;
	
	@Column(name = "header_notes_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String headerNotesHindi;
	
	@Column(name = "header_notes_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String headerNotesGujarati;
	
	@Column(name = "footer_notes_english")
	private String footerNotesEnglish;
	
	@Column(name = "footer_notes_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String footerNotesHindi;
	
	@Column(name = "footer_notes_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String footerNotesGujarati;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;

}
