package com.crmportal.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GstSalesReportDTO {

    private int srNo;
    private LocalDate date;
    private String invNo;
    private String name;
    private String gstNumber;
    private BigDecimal basicAmount;
    private BigDecimal discount;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
    private BigDecimal totalgst;
    private BigInteger total;
}