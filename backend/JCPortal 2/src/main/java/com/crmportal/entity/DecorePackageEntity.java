package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "decore_package")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decore_package_id")
    private Long id;

    @Column(name = "name_english")
    private String nameEnglish;

    @Column(name = "name_hindi")
    private String nameHindi;

    @Column(name = "name_gujarati")
    private String nameGujarati;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_published")
    private Boolean isPublished = true;

    @Column(name = "sequence")
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserMasterEntity user;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}