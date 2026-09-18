package com.crmportal.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "storeissuereturndetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueReturnDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sirdetail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sir_id")
    private StoreIssueReturnEntity storeIssueReturn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id")
    private RawMaterialMasterEntity rawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_cat_id")
    private RawMaterialCategoryMasterEntity rawMaterialCat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private UnitMasterEntity unit;

    @Column(name = "qty")
    private double qty = 0.0f;

    @Column(name = "userid")
    private Long userid;
}