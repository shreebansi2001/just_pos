package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class BankTransactionReportDTO {
    private String     bankName;
    private String     branchName;
    private String     accountNo;
    private String     accountHolderName;
    private String     fromDate;
    private String     toDate;
    private BigDecimal totalCredit;
    private BigDecimal totalDebit;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private List<BankTransactionDTO> transactions;
}