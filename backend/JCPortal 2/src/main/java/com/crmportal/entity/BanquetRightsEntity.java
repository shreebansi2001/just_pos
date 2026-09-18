package com.crmportal.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "banquet_rights")
public class BanquetRightsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banquet_hall_id")
    private BanquetHallMasterEntity banquetHall;

    @Column(name = "is_allow")
    private Boolean isAllow = false;

    @Column(name = "is_delete")
    private Boolean isDelete = false;
}