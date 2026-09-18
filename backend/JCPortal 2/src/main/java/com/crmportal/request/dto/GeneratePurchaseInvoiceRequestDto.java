package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class GeneratePurchaseInvoiceRequestDto {

    private Long sotPoId;
    private Long userId;

    // ── PO Header fields (from the form) ─────────────────────────────────
    private String podate;       // "dd/MM/yyyy"
    private String billno;
    private String invoicetype;
    private String remarks;
    private String grnNumber;

    // ── Amount fields ─────────────────────────────────────────────────────
    private float subamount;
    private float discountper;
    private float discountval;
    private float adjustamount;
    private float finalamount;

    // ── Details with price info ───────────────────────────────────────────
    private List<PurchaseInvoiceDetailRequestDto> details;
}