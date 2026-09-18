package com.crmportal.response.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DateWiseStockRowDto {
    private int srNo;
    private String categoryName;
    private String itemName;
    private double opb;
    private double purchase;
    private double purchaseReturn;
    private double sell;          // store issue
    private double sellReturn;    // store issue return
    private double finalTotal;    // opb + purchase - purchaseReturn - sell + sellReturn
    private String unit;
    private BigDecimal rate;      // supplier_rate
    private double increase;
    private double wastage;
    private BigDecimal amount;    // finalTotal * rate
    private String remarks;
}