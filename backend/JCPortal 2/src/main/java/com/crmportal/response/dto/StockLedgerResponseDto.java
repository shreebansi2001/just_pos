package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class StockLedgerResponseDto {
    private String itemName;
    private String unitName;
    private String fromDate;
    private String toDate;
    private BigDecimal  opb;           // opening balance
    private BigDecimal  closingStock;  // final balance
    private BigDecimal supplierRate;
    private List<StockLedgerRowDto> rows;
    
}