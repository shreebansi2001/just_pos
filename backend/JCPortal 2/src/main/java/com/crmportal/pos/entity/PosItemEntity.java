package com.crmportal.pos.entity;

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_code", length = 30)
    private String categoryCode;

    @Column(name = "is_veg")
    private Boolean veg = true;

    @Column(name = "price")
    private Double price = 0.0;

    @Column(name = "tag", length = 200)
    private String tag;

    @Column(name = "station", length = 60)
    private String station = "Kitchen";

    @Column(name = "is_active")
    private Boolean active = true;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PosItemVariantEntity> variants = new ArrayList<PosItemVariantEntity>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
