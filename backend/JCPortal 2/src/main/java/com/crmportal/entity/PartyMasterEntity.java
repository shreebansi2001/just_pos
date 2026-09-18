package com.crmportal.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "partymaster")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyMasterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "party_id")
	private Long id;
	
	@Column(name = "name_english",nullable = false)
	private String nameEnglish;
	
	@Column(name = "name_hindi",columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;
	
	@Column(name = "name_gujarati",columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;
	
	@Column(name = "address_english",nullable = false)
	private String addressEnglish;
	
	@Column(name = "address_hindi",columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String addressHindi;

	@Column(name = "address_Gujarati",columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String addressGujarati;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "mobileno")
	private String mobileno;
	
	@Column(name = "alt_mobileno")
	private String altMobileno;
	
	@Column(name = "gst")
	private String gst;
	
	@Column(name = "pan")
	private String pan;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "birth_date", columnDefinition = "DATETIME")
	private Date  birthDate;
	
	@Column(name = "document")
	private String document;
	
	@Column(name = "doc_path")
	private String docPath;
	
	@Column(name = "party_code")
	private String partyCode;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "opb", nullable = false)
	private BigDecimal opb = BigDecimal.ZERO;

	@Column(name = "price", nullable = false)
	private BigDecimal price = BigDecimal.ZERO;
	
	@Column(name = "helper_price")
	private BigDecimal helperPrice = BigDecimal.ZERO;
	
	@Column(name = "counter_price")
	private BigDecimal counterPrice = BigDecimal.ZERO;

	
	@Column(name = "type")
	private String type;
	
	@Column(name = "opb_date")
	private LocalDate opbDate;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;


	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_category_id")
    private ContactCategoryMasterEntity contact;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private UserMasterEntity user;
	
	
}
