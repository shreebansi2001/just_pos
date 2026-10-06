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
@Table(name = "pos_order_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosOrderActivityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // Staff member who performed the action

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "role_name", length = 40)
    private String roleName;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // created, kot_sent, delivery_assigned, out_for_delivery, delivered, settled, cancelled

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
