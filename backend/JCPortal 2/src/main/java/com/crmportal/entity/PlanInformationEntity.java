package com.crmportal.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanInformationEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "plan_name")
	private String planName;

	@Column(name = "plan_start_date", columnDefinition = "DATETIME")
	private LocalDateTime planStartDate;

	@Column(name = "plan_end_date", columnDefinition = "DATETIME")
	private LocalDateTime planEndDate;

	@Column(name = "amount", precision = 15, scale = 2)
	private BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id", nullable = false)
	private InvoiceEntity invoice;
}