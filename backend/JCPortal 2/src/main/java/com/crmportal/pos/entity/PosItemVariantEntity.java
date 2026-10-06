package com.crmportal.pos.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_item_variant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosItemVariantEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId = 1L; // Admin/Owner account

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private PosItemEntity item;

    @Column(name = "group_name", nullable = false, length = 60)
    private String groupName = "Size"; // 'Size', 'Portion', 'Crust', 'Add-on', 'Preparation'

    @Column(name = "variant_name", nullable = false, length = 80)
    private String variantName; // 'Half', 'Full', 'Extra Cheese', 'Jain'

    @Column(name = "price", nullable = false)
    private Double price = 0.0; // Variant price or delta/addon price

    @Column(name = "price_type", length = 20)
    private String priceType = "fixed"; // 'fixed' (overrides base price) or 'addon' (added to base price)

    @Column(name = "selection_type", length = 20)
    private String selectionType = "single"; // 'single' (exclusive radio) or 'multiple' (checkbox addons)

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 1;

    @Column(name = "is_active")
    private Boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
