package com.crmportal.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cat_bg_selection")
public class CatBgSelectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "is_cat_img", columnDefinition = "TINYINT(1)")
    private Boolean isCatImg;

    @Column(name = "is_delete", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @Column(name = "user_id")
    private Long userId;
}