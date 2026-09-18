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
@Table(name = "close_date")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseDateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "close_date_id")
	private Long closeDateId;
	
	@Column(name = "start_date", columnDefinition = "DATETIME")
	private LocalDateTime startDate;
	
	@Column(name = "close_date", columnDefinition = "DATETIME")
	private LocalDateTime closeDate;
	
	@Column(name = "year")
    private Integer year;
    
    @Column(name = "month")
    private Integer month;
    
    @Column(name = "user_id")
    private Long userId;
    
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
}
