package com.crmportal.pos.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosTableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "shortcode", nullable = false, length = 20)
    private String shortcode;

    @Column(name = "capacity", nullable = false)
    private Integer capacity = 4;

    @Column(name = "floor_id")
    private Long floorId;

    @Column(name = "floor_code", length = 30)
    private String floorCode;

    @Column(name = "status", length = 30)
    private String status = "available"; // available, occupied, billed, cleaning

    @Column(name = "current_order_id")
    private Long currentOrderId;

    @Column(name = "current_order_code", length = 40)
    private String currentOrderCode;

    @Column(name = "is_active")
    private Boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
