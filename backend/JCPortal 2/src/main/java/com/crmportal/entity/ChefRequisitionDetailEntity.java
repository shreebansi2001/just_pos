package com.crmportal.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "chefrequisitiondetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChefRequisitionDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crdetail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cr_id")
    private ChefRequisitionEntity chefRequisition;

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
    private double qty = 0.0;

    @Column(name = "userid")
    private Long userid;
}