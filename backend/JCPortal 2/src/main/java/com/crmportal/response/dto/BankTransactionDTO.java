package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BankTransactionDTO {
    private Integer    srNo;
    private LocalDate  date;
    private String     source;        // "VENDOR_PAYMENT" or "ACCOUNT_ENTRY"
    private String     voucherNo;
    private String     contactName;
    private String     paymentMode;
    private String     notes;
    private BigDecimal credit;        // money IN  (received_amount / CREDIT entry)
    private BigDecimal debit;         // money OUT (pay_amount / DEBIT entry)
    private String     referenceNo;
}