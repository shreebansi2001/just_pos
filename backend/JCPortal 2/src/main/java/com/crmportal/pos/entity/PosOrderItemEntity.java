package com.crmportal.pos.entity;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosOrderItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private PosOrderEntity order;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_key", nullable = false, length = 80)
    private String itemKey;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(name = "price", nullable = false)
    private Double price = 0.0;

    @Column(name = "qty", nullable = false)
    private Integer qty = 0;

    @Column(name = "sent_qty", nullable = false)
    private Integer sentQty = 0;
}
