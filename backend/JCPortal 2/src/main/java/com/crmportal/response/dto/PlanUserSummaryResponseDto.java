package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanUserSummaryResponseDto {

	private Long totalUser;

    private Double totalAmount;
    private Double totalPaidAmount;
    private Double totalUnPaidAmount;
	List<UserMasterResponseDto> users;
}
