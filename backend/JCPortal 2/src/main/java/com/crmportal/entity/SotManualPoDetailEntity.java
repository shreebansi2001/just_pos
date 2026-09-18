package com.crmportal.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "sot_manual_po_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SotManualPoDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sot_po_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sot_po_id")
    private SotManualPoEntity sotManualPo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id")
    private RawMaterialMasterEntity rawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_cat_id")
    private RawMaterialCategoryMasterEntity rawMaterialCat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private UnitMasterEntity unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private PartyMasterEntity party;

    @Column(name = "qty")
    private double qty = 0.0;
    
 // Add these fields to SotManualPoDetailEntity.java

    @Column(name = "hsccode", columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String hsccode;

    @Column(name = "cgst")
    private float cgst = 0.0f;

    @Column(name = "sgst")
    private float sgst = 0.0f;

    @Column(name = "igst")
    private float igst = 0.0f;

    @Column(name = "price")
    private float price = 0.0f;

    @Column(name = "othercharge")
    private float othercharge = 0.0f;

    @Column(name = "total")
    private float total = 0.0f;
}