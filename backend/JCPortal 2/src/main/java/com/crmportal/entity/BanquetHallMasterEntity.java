package com.crmportal.entity;

import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "banquet_hall_master")
public class BanquetHallMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hall_name")
    private String hallName;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "morning_price")
    private Float morningPrice;

    @Column(name = "evening_price")
    private Float eveningPrice;

    @Column(name = "full_day_price")
    private Float fullDayPrice;

    @Column(name = "exhibition_price")
    private Float exhibitionPrice;

    @Column(name = "corporate_price")
    private Float corporatePrice;

    @Column(name = "extra_charges_per_hr")
    private Float extraChargesPerHr;

    @Column(name = "password")
    private String password;  // stored encrypted

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;
    

    @OneToMany(mappedBy = "hall", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BanquetHallImageEntity> images;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
}