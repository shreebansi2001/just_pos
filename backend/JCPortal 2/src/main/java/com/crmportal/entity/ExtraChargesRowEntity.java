package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;

@Entity
@Table(name = "extra_charges_row")
public class ExtraChargesRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heading_id", nullable = false)
    private ExtraChargesHeadingEntity heading;

    @Column(name = "charge_date")
    private LocalDate chargeDate;

    @Column(name = "charge_start_time")
    private LocalTime chargeStartTime;

    @Column(name = "charge_end_time")
    private LocalTime chargeEndTime;

    @Column(name = "session", length = 100)
    private String session;

    @Column(name = "person_item")
    private Integer personItem;

    @Column(name = "rate", precision = 10, scale = 2)
    private BigDecimal rate = BigDecimal.ZERO;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "is_delete", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDelete = false;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExtraChargesHeadingEntity getHeading() { return heading; }
    public void setHeading(ExtraChargesHeadingEntity heading) { this.heading = heading; }

    public LocalDate getChargeDate() { return chargeDate; }
    public void setChargeDate(LocalDate chargeDate) { this.chargeDate = chargeDate; }

    public LocalTime getChargeStartTime() { return chargeStartTime; }
    public void setChargeStartTime(LocalTime chargeStartTime) { this.chargeStartTime = chargeStartTime; }

    public LocalTime getChargeEndTime() { return chargeEndTime; }
    public void setChargeEndTime(LocalTime chargeEndTime) { this.chargeEndTime = chargeEndTime; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public Integer getPersonItem() { return personItem; }
    public void setPersonItem(Integer personItem) { this.personItem = personItem; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Boolean getIsDelete() { return isDelete; }
    public void setIsDelete(Boolean isDelete) { this.isDelete = isDelete; }
}