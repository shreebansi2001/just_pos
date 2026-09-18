package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeExpensePayoutResponseDto {

	private Long id;
	
	private OfficeExpenseResponseDto officeExpense;
	
	private BankDetailsResponseDto bankAccount;
	
	private LocalDate paymentDate;
	
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
