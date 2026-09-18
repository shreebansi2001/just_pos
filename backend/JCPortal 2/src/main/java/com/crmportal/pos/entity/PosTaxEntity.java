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
@Table(name = "pos_tax")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosTaxEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tax_name", nullable = false, length = 100)
    private String taxName; // e.g. CGST, SGST, IGST, Service Tax

    @Column(name = "percentage", nullable = false)
    private Double percentage = 0.0;

    @Column(name = "status", length = 30)
    private String status = "active"; // active or inactive

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
