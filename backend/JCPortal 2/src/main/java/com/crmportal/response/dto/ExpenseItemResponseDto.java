package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.enums.EExpense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseItemResponseDto {

	private Long expenseItemId;

	private String itemName;

	private BigDecimal amount;

	private String itemPurchaseDate;

	private String paymentType;

	private String remarks;

	private EExpense userType;

	private ExpenseManagementResponseDto expense;

	private Long userId;

	private Long eventId;

	private Long supplierId;
	private String supplierName;
}
