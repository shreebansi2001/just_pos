package com.crmportal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GstPurchaseReportDTO {

    private int srNo;
    private LocalDate date;
    private String billNo;
    private String name;          
    private String productName;   
    private double qty;
    private float basicBillAmt;   
    private float cgst;          
    private float sgst;           
    private float igst;           
    private float cgstAmt;        
    private float sgstAmt;       
    private float igstAmt;        
    private float gstAmt;   
    private float discount;    
    private float total;         
}