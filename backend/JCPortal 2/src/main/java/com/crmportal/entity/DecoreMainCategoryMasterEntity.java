package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "decore_main_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoreMainCategoryMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decore_main_category_id")
    private Long id;

    @Column(name = "name_english")
    private String nameEnglish;

    @Column(name = "name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String nameHindi;

    @Column(name = "name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String nameGujarati;

    @Column(name = "category_slogan")
    private String categorySlogan;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "sequence")
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserMasterEntity user;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @Column(name = "uuid", nullable = false)
    private String uuid;
}