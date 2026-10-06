package com.crmportal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pos_kot_item")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PosKotItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId = 1L; // Admin/Owner account

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kot_id", nullable = false)
    @JsonIgnore
    private PosKotEntity kot;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(name = "unit_mode", length = 20)
    private String unitMode = "portion";

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "variant_label", length = 150)
    private String variantLabel;

    @Column(name = "qty", nullable = false)
    private Integer qty = 1;
}
