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
@Table(name = "pos_kot_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosKotItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kot_id", nullable = false)
    private PosKotEntity kot;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(name = "qty", nullable = false)
    private Integer qty = 1;
}
