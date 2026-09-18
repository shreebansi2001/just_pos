package com.crmportal.entity;

import java.math.BigDecimal;
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
@Table(name = "hall_package_rate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HallPackageRateEntity {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "hall_package_id")
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "hall_id")
	    private BanquetHallMasterEntity hall;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "custom_package_id")
	    private CustomPackageEntity customPackage;

	    @Column(name = "package_sequence")
	    private Integer packageSequence;

	    @Column(name = "tier_label")
	    private String tierLabel;

	    @Column(name = "min_guests")
	    private Integer minGuests;

	    @Column(name = "tier_sequence")
	    private Integer tierSequence;

	    @Column(name = "price", precision = 10, scale = 2)
	    private BigDecimal price = BigDecimal.ZERO;

	    @Column(name = "is_delete", nullable = false)
	    private Boolean isDelete = false;

	    @Column(name = "is_active", nullable = false)
	    private Boolean isActive = true;

	    @CreationTimestamp
	    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at", columnDefinition = "DATETIME")
	    private LocalDateTime updatedAt;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id")
	    private UserMasterEntity user;
}
