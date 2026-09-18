package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

import org.aspectj.weaver.tools.Trace;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_remark")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRemarkMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_remark_id")
	private Long id;
	
	@Column(name = "name_english",nullable = false, columnDefinition = "text")
	private String nameEnglish;
	
	@Column(name = "name_hindi", columnDefinition = "text CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "text CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;
	
	@Column(name = "isOdc", nullable = true)
	private Boolean isOdc = true;
	
	@Column(name = "type",nullable = false)
	private String type;
	
	@Column(name = "is_active")
    private Boolean isActive = true;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private UserMasterEntity user;
//	
//	@OneToMany(mappedBy = "eventType", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<EventMasterEntity> events = new ArrayList<>();
//	
}
