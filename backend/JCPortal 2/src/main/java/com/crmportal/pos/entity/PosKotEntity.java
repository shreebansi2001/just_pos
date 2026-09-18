package com.crmportal.pos.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_kot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosKotEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kot_code", nullable = false, length = 40)
    private String kotCode;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_code", length = 40)
    private String orderCode;

    @Column(name = "table_label", length = 40)
    private String tableLabel;

    @Column(name = "order_type", length = 30)
    private String orderType = "dine-in";

    @Column(name = "status", length = 30)
    private String status = "new"; // new, preparing, ready, served

    @OneToMany(mappedBy = "kot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PosKotItemEntity> items = new ArrayList<PosKotItemEntity>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
