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
@Table(name = "report_configuration")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportConfigurationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "is_category_slogan")
	private Integer isCategorySlogan;

	@Column(name = "is_category_instruction")
	private Integer isCategoryInstruction;

	@Column(name = "is_category_image")
	private Long isCategoryImage;

	@Column(name = "is_item_slogan")
	private Integer isItemSlogan;

	@Column(name = "is_item_instruction")
	private Integer isItemInstruction;

	@Column(name = "is_item_image")
	private Integer isItemImage;
	
	@Column(name = "is_company_logo")
	private Integer isCompanyLogo;
	
	@Column(name = "is_company_details")	
	private Integer isCompanyDetails;
	
	@Column(name = "is_party_details")		
	private Integer isPartyDetails;
	
	@Column(name = "is_active")
	private Boolean isActive = false;
	
	@Column(name = "is_withqty")
	private Integer isWithQty;

	@Column(name = "size1")
	private String size1;

	@Column(name = "size2")
	private String size2;
	
	@Column(name = "size3")
	private String size3;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "is_agency")
	private Integer isAgency;
	
	@Column(name = "is_status")
	private Integer isStatus;
	
	@Column(name = "is_raw_material_cat")
	private Integer isRawMaterialCat;
	
	@Column(name = "is_withprice")
	private Integer isWithPrice;
	
	@Column(name = "is_item")
	private Integer isItem;
	
	@Column(name = "is_item_page")
	private Integer isItemPage;
	
	@Column(name = "is_item_column")
	private Integer isItemColumn;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "is_combo")
	private Integer isCombo;
	
	@Column(name = "is_one_page")
	private Integer isOnePage
	;
	@Column(name = "is_qrcode")
	private Integer isQrCode;
	
	@Column(name = "is_termscond")
	private Integer isTermsCond;

	@Column(name = "is_advancedpay")
	private Integer isAdvancedPay;
	
	@Column(name = "is_doc")
	private Integer isDoc;
	
	@Column(name = "is_half_pax")
	private Integer isHalfPax;
	
	@Column(name = "is_3column")
	private Integer is3Column;

	@Column(name = "is_function_next_page")
	private Integer isFunctionNextPage;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updateAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_mapping_id")
	private TemplateMappingEntity templateMapping;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_module_id")
	private TemplateModuleMasterEntity templateModule;
	
	private Integer isExtraCharges;
	
	@Column(name = "is_add_decoration")
	private Integer isAddDecoration;
	
	@Column(name = "is_decore")
	private Boolean isDecore;
	
	@Column(name = "is_show_event_remarks")
	private Integer isShowEventRemarks;
	
	@Column(name = "show_additional")
	private Integer showAdditional;
	
	@Column(name = "is_agency_next_page")
	private Integer isAgencyNextPage;
	
	@Column(name = "store_issue_wise")
	private Integer storeIssueWise;
	
	@Column(name = "is_contact_no_visible")
	private Integer isContactNoVisible;
	
	@Column(name = "is_signature_visible")
	private Integer isSignatureVisible;
	
	@Column(name = "is_excel")
	private Integer isExcel;
	
	@Column(name = "is_5column")
	private Integer is5Column;
	
	@Column(name = "is_add_store_issue")
	private Integer isAddStoreIssue;
	
	@Column(name = "is_add_menu")
	private Integer isAddMenu;
	
	@Column(name = "is_advance_payment")
	private Integer isAdvancePayment;
	
	@Column(name = "is_notes")
	private Integer isNotes;
	
	@Column(name = "show_last_page")
	private Integer showLastPage;
	
	@Column(name = "show_add_on_label")
	private Integer showAddOnLabel;
	
	@Column(name = "with_out_bg")	
	private Integer withOutBg;
	
	@Column(name = "with_vendor")	
	private Integer withVendor;
	
	@Column(name = "is_all_item_together")
	private Integer isAllItemTogether;
	
	@Column(name = "is_start_date")
	private Integer isStartDate;
	
	@Column(name = "is_end_date")
	private Integer isEndDate;
}
