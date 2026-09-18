package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripExpensePayoutHistoryResponseDto {

	private Long id;
	
	private Long tripExpenseId;
	
	private BankDetailsResponseDto bankAccount;
	
	private String paymentDate;
	
	private String transactionId;
	
	private String chequeNo;

	private BigDecimal amount;
	
	private BigDecimal dueAmount;
	
	private PaymentMode paymentMode;
	
	private CashOpbResponseDto cashType;
	
	private String status;
	
	private String description;
	
	private Boolean isDelete;
	
    private String createdAt;

    private String updatedAt;
}
