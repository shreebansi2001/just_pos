package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_otp")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "is_used",nullable = false)
    private Boolean isUsed = false;
    
    @Column(name = "expiry_time", columnDefinition = "DATETIME")
    private LocalDateTime expiryTime;

}
