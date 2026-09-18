package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLedgerSummaryDto {

    private BigDecimal totalCredit;
    private BigDecimal totalDebit;
    private BigDecimal totalBalance;

}
