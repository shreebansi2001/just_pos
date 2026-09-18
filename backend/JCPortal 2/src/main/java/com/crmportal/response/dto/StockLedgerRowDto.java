package com.crmportal.response.dto;

import lombok.Data;

@Data
public class StockLedgerRowDto {
    private String date;
    private String vno;           // voucher no
    private String supplierName;
    private String billNo;
    private double purchase;
    private double purchaseReturn;
    private double sale;          // storeIssue
    private double saleReturn;    // storeIssueReturn
    private double balance;
    private String unitName;
    private double wastage;
    private double increase;
    private String transactionType; // PURCHASE, PURCHASE_RETURN, STORE_ISSUE, STORE_ISSUE_RETURN
}