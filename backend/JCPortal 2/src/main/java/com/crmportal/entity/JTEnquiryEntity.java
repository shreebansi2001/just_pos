package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.JTEnquiryServiceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "just_tab_enquiry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JTEnquiryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "full_name")
	private String fullName;
	
	@Column(name = "mobile_no")
	private String mobileNo;
	
	@Column(name = "cmp_name")
	private String cmpName;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "notes")
	private String notes;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private JTEnquiryServiceType type;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
