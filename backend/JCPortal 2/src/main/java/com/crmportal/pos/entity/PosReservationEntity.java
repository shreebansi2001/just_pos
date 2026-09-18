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
@Table(name = "pos_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "res_code", nullable = false, length = 40)
    private String resCode;

    @Column(name = "guest_name", nullable = false, length = 120)
    private String guestName;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "pax", nullable = false)
    private Integer pax = 2;

    @Column(name = "duration")
    private Integer duration = 90;

    @Column(name = "res_date", nullable = false, length = 20)
    private String resDate; // YYYY-MM-DD

    @Column(name = "res_time", nullable = false, length = 20)
    private String resTime; // HH:mm

    @Column(name = "floor_id")
    private Long floorId;

    @Column(name = "table_id")
    private Long tableId;

    @Column(name = "notes", length = 300)
    private String notes;

    @Column(name = "status", length = 30)
    private String status = "upcoming"; // upcoming, seated, completed, cancelled, no-show

    @Column(name = "order_id")
    private Long orderId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
