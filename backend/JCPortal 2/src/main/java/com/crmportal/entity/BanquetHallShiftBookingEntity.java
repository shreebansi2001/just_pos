package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Data;

@Data
@Entity
@Table(name = "banquet_hall_shift_booking")
public class BanquetHallShiftBookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private BanquetHallMasterEntity hall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private BanquetShiftMasterEntity shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventMasterEntity event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_function_id", nullable = true)
    private EventFunctionMasterEntity eventFunction;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserMasterEntity user;
}