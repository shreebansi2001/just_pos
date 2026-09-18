package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLedgerResponseDto {

    private String date;
    
    private String voucherNo;
    
    private String particular;
    
    private BigDecimal debit;
    
    private BigDecimal credit;
    
    private BigDecimal balance;
}
