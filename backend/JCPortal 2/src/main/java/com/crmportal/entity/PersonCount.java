package com.crmportal.entity;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "person_count")
@Data
public class PersonCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personcount_id")
    private Long id;

    @Column(name = "count_in_value")
    private Integer countIn;

    @Column(name = "count_out_value")
    private Integer countOut;

    @Column(name = "total_in")
    private Integer totalIn;

    @Column(name = "total_out")
    private Integer totalOut;

    private Integer occupancy;

    @CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
    
}