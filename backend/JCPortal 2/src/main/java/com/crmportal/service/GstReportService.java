package com.crmportal.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.dto.GstPurchaseReportDTO;
import com.crmportal.dto.GstReportResponseDTO;
import com.crmportal.dto.GstSalesReportDTO;
import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventInvoiceEntity;
import com.crmportal.entity.PurchaseOrderDetailEntity;
import com.crmportal.repository.GstInvoiceReportRepository;
import com.crmportal.repository.GstPurchaseReportRepository;
import com.crmportal.repository.GstSalesReportRepository;

@Service
public class GstReportService {

    @Autowired
    private GstSalesReportRepository salesRepository;

    @Autowired
    private GstPurchaseReportRepository purchaseRepository;
    
    @Autowired
    private GstInvoiceReportRepository invoiceRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── Sales Report ──────────────────────────────────────────────────────────

    public GstReportResponseDTO getSalesReport(LocalDate fromDate, LocalDate toDate, Long userId, String gstType) {
        
    	 LocalDateTime fromDT = fromDate.atStartOfDay();                // e.g. 2026-02-01 00:00:00
         LocalDateTime toDT   = toDate.atTime(LocalTime.MAX);
         
        List<GstSalesReportDTO> result = new ArrayList<>();
 
        int srNo = 1;
        
        List<EventInvoiceEntity> invoices = invoiceRepository.findInvoicesWithinDateRange(fromDT, toDT, userId, gstType);
        for (EventInvoiceEntity inv : invoices) {
            GstSalesReportDTO dto = new GstSalesReportDTO();
            dto.setSrNo(srNo++);
            // Use the related quotation date for consistency; fall back to createdAt date
//            dto.setDate(inv.getDuedate() != null ? inv.getDuedate() : 
//                        (inv.getCreatedAt() != null ? inv.getCreatedAt().toLocalDate() : null));
            dto.setDate(inv.getCreatedAt().toLocalDate());
            dto.setInvNo(inv.getInvoiceCode());
            String invName = (inv.getBillingname() != null && !inv.getBillingname().trim().isEmpty())
                    ? inv.getBillingname()
                    : (inv.getEvent() != null && inv.getEvent().getParty() != null
                            ? inv.getEvent().getParty().getNameEnglish() : "");
            dto.setName(invName);
            dto.setGstNumber(inv.getGstnumber());
 
            BigDecimal subTotal = inv.getSubTotal() != null ? new BigDecimal(inv.getSubTotal()) : BigDecimal.ZERO;
            BigDecimal discount = inv.getDiscount() != null ? inv.getDiscount() : BigDecimal.ZERO;
            BigDecimal basicAmount = subTotal.subtract(discount);
            dto.setDiscount(inv.getDiscount());
            dto.setBasicAmount(basicAmount);
            
            BigDecimal cgstAmnt = inv.getCgstAmnt()  != null ? inv.getCgstAmnt()  : BigDecimal.ZERO;
            BigDecimal sgstAmnt = inv.getSgstAmnt()  != null ? inv.getSgstAmnt()  : BigDecimal.ZERO;
            BigDecimal igstAmnt = inv.getIgstAmnt()  != null ? inv.getIgstAmnt()  : BigDecimal.ZERO;
            BigInteger grandTotal = inv.getGrandTotal()  != null ? inv.getGrandTotal()  : BigInteger.ZERO;
 
            dto.setCgst(cgstAmnt);
            dto.setSgst(sgstAmnt);
            dto.setIgst(igstAmnt);
            dto.setTotalgst(cgstAmnt.add(sgstAmnt).add(igstAmnt));
            dto.setTotal(grandTotal);
            result.add(dto);
        }
        
        
        List<EventFunctionQuotationEntity> quotations = salesRepository.findQuotationsWithoutInvoice(fromDT, toDT, userId, gstType);
        for (EventFunctionQuotationEntity q : quotations) {
            GstSalesReportDTO dto = new GstSalesReportDTO();
            dto.setSrNo(srNo++);
            dto.setDate(q.getQuotationdate());
            dto.setInvNo(q.getQuotationCode());
            String qName = (q.getBillingname() != null && !q.getBillingname().trim().isEmpty())
                    ? q.getBillingname()
                    : (q.getEvent() != null && q.getEvent().getParty() != null
                            ? q.getEvent().getParty().getNameEnglish() : "");
            dto.setName(qName);
            dto.setGstNumber(q.getGstnumber());
            
            BigDecimal subTotal = q.getSubTotal() != null ? new BigDecimal(q.getSubTotal()) : BigDecimal.ZERO;
            BigDecimal discount = q.getDiscount() != null ? q.getDiscount() : BigDecimal.ZERO;
            BigDecimal basicAmount = subTotal.subtract(discount);
            dto.setDiscount(q.getDiscount());
            dto.setBasicAmount(basicAmount);
            
            BigDecimal cgstAmnt = q.getCgstAmnt()  != null ? q.getCgstAmnt()  : BigDecimal.ZERO;
            BigDecimal sgstAmnt = q.getSgstAmnt()  != null ? q.getSgstAmnt()  : BigDecimal.ZERO;
            BigDecimal igstAmnt = q.getIgstAmnt()  != null ? q.getIgstAmnt()  : BigDecimal.ZERO;
            BigInteger grandTotal = q.getGrandTotal()  != null ? q.getGrandTotal()  : BigInteger.ZERO;
 
            dto.setCgst(cgstAmnt);
            dto.setSgst(sgstAmnt);
            dto.setIgst(igstAmnt);
            dto.setTotalgst(cgstAmnt.add(sgstAmnt).add(igstAmnt));
            dto.setTotal(grandTotal);
            result.add(dto);
        }
 
        return new GstReportResponseDTO(
                "SALES",
                fromDate.format(DATE_FORMATTER),
                toDate.format(DATE_FORMATTER),
                result
        );
    }

    // ─── Purchase Report ───────────────────────────────────────────────────────

    public GstReportResponseDTO getPurchaseReport(LocalDate fromDate, LocalDate toDate, String gstType) {
        List<PurchaseOrderDetailEntity> records = purchaseRepository.findPurchaseGstReport(fromDate, toDate, gstType);
        List<GstPurchaseReportDTO> result = new ArrayList<>();

        int srNo = 1;
        for (PurchaseOrderDetailEntity pod : records) {
            GstPurchaseReportDTO dto = new GstPurchaseReportDTO();
            dto.setSrNo(srNo++);
            dto.setDate(pod.getPo().getPodate());
            dto.setBillNo(pod.getPo().getBillno());
            dto.setName(pod.getPo().getSupplier() != null ? pod.getPo().getSupplier().getNameEnglish() : "");
            dto.setProductName(pod.getRawMaterial() != null ? pod.getRawMaterial().getNameEnglish() : "");
            dto.setQty(pod.getQty());

            float basicBillAmt = (float) (pod.getQty() * pod.getPrice());
            float discount = pod.getPo() != null ? pod.getPo().getDiscountval() : 0.0f;
            float basicAfterDiscount = basicBillAmt - discount;
            dto.setBasicBillAmt(basicAfterDiscount);
            
            

            float cgstAmt = basicAfterDiscount * pod.getCgst() / 100f;
            float sgstAmt = basicAfterDiscount * pod.getSgst() / 100f;
            float igstAmt = basicAfterDiscount * pod.getIgst() / 100f;
            float totalamt = cgstAmt +sgstAmt + igstAmt + basicAfterDiscount ;

            dto.setCgst(pod.getCgst());
            dto.setSgst(pod.getSgst());
            dto.setIgst(pod.getIgst());
            dto.setCgstAmt(round2(cgstAmt));
            dto.setSgstAmt(round2(sgstAmt));
            dto.setIgstAmt(round2(igstAmt));
            dto.setGstAmt(round2(cgstAmt + sgstAmt + igstAmt));
            dto.setTotal(round2(totalamt));
         //   dto.setTotal(pod.getTotal());
            result.add(dto);
        }

        return new GstReportResponseDTO(
                "PURCHASE",
                fromDate.format(DATE_FORMATTER),
                toDate.format(DATE_FORMATTER),
                result
        );
    }

    private float round2(float value) {
        return Math.round(value * 100f) / 100f;
    }
}