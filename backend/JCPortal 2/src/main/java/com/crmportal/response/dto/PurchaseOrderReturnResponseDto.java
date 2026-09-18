package com.crmportal.response.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class PurchaseOrderReturnResponseDto {

    private Long id;
    private String porcode;
    private Long poId;
    private String pocode;        // original PO code
    private Long supplierId;
    private String supplierName;
    private String voucher;
    private LocalDate returndate;
    private String billno;
    private String invoicetype;
    private String remarks;
    private float subamount;
    private float discountper;
    private float discountval;
    private float adjustamount;
    private float finalamount;
    private Long   stockTypeId;
    private String stockTypeName;
    private LocalDateTime createdAt;
   

    private List<PurchaseOrderReturnDetailResponseDto> details;
}