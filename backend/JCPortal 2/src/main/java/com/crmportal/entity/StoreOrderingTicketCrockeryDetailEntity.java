package com.crmportal.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_ordering_ticket_crockery_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreOrderingTicketCrockeryDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sot_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sot_id")
    private StoreOrderingTicketCrockeryEntity sot;

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

    // Qty accepted in Accept SOT step
    @Column(name = "accepted_qty")
    private double acceptedQty = 0.0;

    // Return qty in Return SOT step
    @Column(name = "return_qty")
    private double returnQty = 0.0;

    @Column(name = "available_stock")
    private double availableStock = 0.0;
}