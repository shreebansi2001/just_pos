package com.crmportal.service.impl;

import java.time.LocalDate;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.PurchaseOrderReturnService;
import com.crmportal.service.StockLedgerEntryService;

import java.io.ByteArrayOutputStream;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.core.env.Environment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
@Service
@Transactional
public class PurchaseOrderReturnServiceImpl implements PurchaseOrderReturnService {

    @Autowired
    private PurchaseOrderReturnRepository porRepository;

    @Autowired
    private PurchaseOrderReturnDetailRepository porDetailRepository;

    @Autowired
    private PurchaseOrderRepository poRepository;

    @Autowired
    private PurchaseOrderDetailRepository poDetailRepository;

    @Autowired
    private PartyMasterRepository partyRepository;

    @Autowired
    private RawMaterialMasterRepository rawRepository;

    @Autowired
    private UserMasterRepository userRepository;
    
    @Autowired
    private StockLedgerEntryService stockLedgerEntryService;
    
    @Autowired
    private StockTypeRepository stockTypeRepository;
    
    @Autowired
    private Environment environment;

    @Autowired
    private MenuPreparationServiceImpl menuPreparationServiceImpl;
    
    @Autowired
   	EventFunctionMenuAllocationRepository menuAllocationRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── Add or Update ───────────────────────────────────────────────────────────

    @Override
    public void addOrUpdate(PurchaseOrderReturnRequestDto dto) {

        PurchaseOrderReturnEntity por;
        LocalDate returndate = LocalDate.parse(dto.getReturndate(), formatter);

        if (dto.getId() != null && dto.getId() != 0 && dto.getId() != -1) {

            por = porRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Purchase Order Return not found"));

            List<PurchaseOrderReturnDetailEntity> oldDetails =
                    porDetailRepository.findByPurchaseOrderReturnId(por.getId());

            if (!oldDetails.isEmpty()) {
                porDetailRepository.deleteAll(oldDetails);
            }

        } else {
            por = new PurchaseOrderReturnEntity();
            Long userId = dto.getUserId();
            por.setPorcode(generatePorCode(userId));
        }

        // Link original PO
        PurchaseOrderEntity po = poRepository.findById(dto.getPoId())
                .orElseThrow(() -> new RuntimeException("Original Purchase Order not found"));

        por.setPurchaseOrder(po);
        por.setSupplier(partyRepository.findById(dto.getSupplierId()).orElse(null));
        por.setVoucher(dto.getVoucher());
        por.setReturndate(returndate);
        por.setBillno(dto.getBillno());
        por.setInvoicetype(dto.getInvoicetype());
        por.setRemarks(dto.getRemarks());
        por.setSubamount(dto.getSubamount());
        por.setDiscountper(dto.getDiscountper());
        por.setDiscountval(dto.getDiscountval());
        por.setAdjustamount(dto.getAdjustamount());
        por.setFinalamount(dto.getFinalamount());
        por.setUser(userRepository.findById(dto.getUserId()).orElse(null));
     // ── Stock Type ────────────────────────────────────────────────────────
        if (dto.getStockTypeId() != null && dto.getStockTypeId() > 0) {
            por.setStocktype(stockTypeRepository.findById(dto.getStockTypeId())
                    .orElse(null));
        } else {
            por.setStocktype(null);
        }
        // ─────────────────────────────────────────────────────────────────────

        por = porRepository.save(por);

        // Save details
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {

            List<PurchaseOrderReturnDetailEntity> detailList = new ArrayList<>();

            for (PurchaseOrderReturnDetailRequestDto d : dto.getDetails()) {

                PurchaseOrderReturnDetailEntity detail = new PurchaseOrderReturnDetailEntity();
                RawMaterialMasterEntity rm = rawRepository.findById(d.getRawMaterialId()).orElse(null);

                detail.setPurchaseOrderReturn(por);
                detail.setRawMaterial(rm);
                detail.setRawMaterialCat(rm != null ? rm.getRawMaterialCat() : null);
                detail.setUnit(rm != null ? rm.getUnit() : null);
                detail.setHsccode(d.getHsccode());
                detail.setCgst(d.getCgst());
                detail.setSgst(d.getSgst());
                detail.setIgst(d.getIgst());
                detail.setQty(d.getQty());
                detail.setPrice(d.getPrice());
                detail.setOthercharge(d.getOthercharge());
                detail.setTotal(d.getTotal());
                detail.setUserid(dto.getUserId());

                detailList.add(detail);
            }

            porDetailRepository.saveAll(detailList);
            porDetailRepository.flush();
            stockLedgerEntryService.savePurchaseReturn(por);
        }
    }

    // ─── Get PO details by pocode (for auto-fill when user types PO number) ──────

    @Override
    public PurchaseOrderResponseDto getPoDetailsByPocode(String pocode, Long userId) {

        PurchaseOrderEntity po = poRepository.findByPocodeAndUserIdAndIsDeleteFalse(pocode, userId)
                .orElseThrow(() -> new RuntimeException("No Purchase Order found with code: " + pocode));

        return convertPoToDto(po);
    }

    // ─── Getters ──────────────────────────────────────────────────────────────────

    @Override
    public List<PurchaseOrderReturnResponseDto> getByUser(Long userId) {
        return porRepository.getByUserDesc(userId)
                .stream()
                .map(por -> {
                    PurchaseOrderReturnResponseDto dto = convertToDto(por);
                    dto.setDetails(null); // ← remove details
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderReturnResponseDto> getByPorId(Long porId) {
        return porRepository.findByIdAndIsDeleteFalse(porId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderReturnResponseDto> getByPoId(Long poId) {
        return porRepository.findByPurchaseOrderIdAndIsDeleteFalse(poId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteByPorId(Long porId) {
        PurchaseOrderReturnEntity por = porRepository.findById(porId)
                .orElseThrow(() -> new RuntimeException(
                        "Purchase Order Return not found with ID: " + porId));

        stockLedgerEntryService.reversePurchaseReturn(porId); 

        por.setIsDelete(true);
        porRepository.save(por);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────────

    private String generatePorCode(Long userId) {

        LocalDate today = LocalDate.now();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMM");

        String prefix = "POR-" + userId + "-" + today.format(dtf) + "-";

        String lastCode = porRepository.findMaxPorcodeByPrefix(userId, prefix);

        long nextNumber = 1;

        if (lastCode != null) {

            try {

                String[] parts = lastCode.split("-");

                nextNumber = Long.parseLong(parts[parts.length - 1]) + 1;

            } catch (NumberFormatException e) {

                nextNumber = 1;
            }
        }

        return prefix + String.format("%03d", nextNumber);
    }

    private PurchaseOrderReturnResponseDto convertToDto(PurchaseOrderReturnEntity por) {

        PurchaseOrderReturnResponseDto dto = new PurchaseOrderReturnResponseDto();

        dto.setId(por.getId());
        dto.setPorcode(por.getPorcode());
        dto.setPoId(por.getPurchaseOrder().getId());
        dto.setPocode(por.getPurchaseOrder().getPocode());
        dto.setSupplierId(por.getSupplier().getId());
        dto.setSupplierName(por.getSupplier().getNameEnglish());
        dto.setVoucher(por.getVoucher());
        dto.setReturndate(por.getReturndate());
        dto.setBillno(por.getBillno());
        dto.setInvoicetype(por.getInvoicetype());
        dto.setRemarks(por.getRemarks());
        dto.setSubamount(por.getSubamount());
        dto.setDiscountper(por.getDiscountper());
        dto.setDiscountval(por.getDiscountval());
        dto.setAdjustamount(por.getAdjustamount());
        dto.setFinalamount(por.getFinalamount());
        dto.setCreatedAt(por.getCreatedAt());

        Long poId = por.getPurchaseOrder().getId();

        List<PurchaseOrderReturnDetailResponseDto> detailDtos =
                porDetailRepository.findByPurchaseOrderReturnId(por.getId())
                        .stream()
                        .map(d -> {
                            PurchaseOrderReturnDetailResponseDto rd = new PurchaseOrderReturnDetailResponseDto();

                            rd.setId(d.getId());
                            rd.setRawMaterialId(d.getRawMaterial().getId());
                            rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
                            rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
                            rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
                            rd.setUnitId(d.getUnit().getId());
                            rd.setUnitName(d.getUnit().getNameEnglish());
                            rd.setHsccode(d.getHsccode());
                            rd.setCgst(d.getCgst());
                            rd.setSgst(d.getSgst());
                            rd.setIgst(d.getIgst());
                            rd.setPrice(d.getPrice());
                            rd.setOthercharge(d.getOthercharge());
                            rd.setTotal(d.getTotal());

                            // qty saved in this return record
                            double thisReturnQty = d.getQty();

                            // original qty from original PO
                            double originalQty = poDetailRepository.findByPoId(poId)
                                    .stream()
                                    .filter(od -> od.getRawMaterial().getId()
                                            .equals(d.getRawMaterial().getId()))
                                    .mapToDouble(od -> od.getQty())
                                    .findFirst()
                                    .orElse(0);

                            // total returned across ALL returns (including this one)
                            double totalReturnedQty = porDetailRepository
                                    .sumReturnedQtyByPoAndRawMaterial(
                                            poId,
                                            d.getRawMaterial().getId()
                                    );

                            // same formula as getpodetails
                            double remainingQty = Math.max(originalQty - totalReturnedQty, 0);

                            rd.setQty(originalQty);            // qty in this return
                            rd.setReturnedQty(totalReturnedQty); // total returned so far
                            rd.setRemainingQty(remainingQty);    // still left to return

                            return rd;
                        }).collect(Collectors.toList());

        dto.setDetails(detailDtos);
        return dto;
    }

    // Reuse existing PO→DTO conversion (for auto-fill response)
    private PurchaseOrderResponseDto convertPoToDto(PurchaseOrderEntity po) {

        PurchaseOrderResponseDto dto = new PurchaseOrderResponseDto();
        dto.setId(po.getId());
        dto.setPocode(po.getPocode());
        dto.setSupplierId(po.getSupplier().getId());
        dto.setSupplierName(po.getSupplier().getNameEnglish());
        dto.setVoucher(po.getVoucher());
        dto.setPodate(po.getPodate());
        dto.setBillno(po.getBillno());
        dto.setInvoicetype(po.getInvoicetype());
        dto.setRemarks(po.getRemarks());
        dto.setSubamount(po.getSubamount());
        dto.setDiscountper(po.getDiscountper());
        dto.setDiscountval(po.getDiscountval());
        dto.setAdjustamount(po.getAdjustamount());
        dto.setFinalamount(po.getFinalamount());
        dto.setPotype(po.getPotype());
        dto.setCreatedAt(po.getCreatedAt());
     // ── Stock Type ────────────────────────────────────────────────────────
        if (po.getStocktype() != null) {
            dto.setStockTypeId(po.getStocktype().getId());
            dto.setStockTypeName(po.getStocktype().getNameEnglish());
        } else {
            dto.setStockTypeId(null);
            dto.setStockTypeName(null);
        }
        // ─────────────────────────────────────────────────────────────────────

        List<PurchaseOrderDetailResponseDto> detailDtos =
                poDetailRepository.findByPoId(po.getId())
                        .stream()
                        .map(d -> {
                            PurchaseOrderDetailResponseDto rd = new PurchaseOrderDetailResponseDto();
                            rd.setId(d.getId());
                            rd.setRawMaterialId(d.getRawMaterial().getId());
                            rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
                            rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
                            rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
                            rd.setUnitId(d.getUnit().getId());
                            rd.setUnitName(d.getUnit().getNameEnglish());
                            rd.setHsccode(d.getHsccode());
                            rd.setCgst(d.getCgst());
                            rd.setSgst(d.getSgst());
                            rd.setIgst(d.getIgst());

                            double originalQty = d.getQty();

                            // Calculate already returned qty for this item
                            double returnedQty = porDetailRepository
                                    .sumReturnedQtyByPoAndRawMaterial(
                                            po.getId(),
                                            d.getRawMaterial().getId()
                                    );

                            double remainingQty = originalQty - returnedQty;

                            rd.setQty(originalQty);
                            rd.setReturnedQty(returnedQty);
                            rd.setRemainingQty(remainingQty);
                            rd.setPrice(d.getPrice());
                            rd.setOthercharge(d.getOthercharge());
                            rd.setTotal(d.getTotal());

                            return rd;
                        })
                        // Only return items that still have remaining qty
                        .filter(rd -> rd.getRemainingQty() > 0)
                        .collect(Collectors.toList());

        dto.setDetails(detailDtos);
        return dto;
    }
    
    @Override
    public byte[] generatePdfReport(Long porId, Long userId, Integer isCompanyDetails, Integer isPrice) {
        try {
            List<PurchaseOrderReturnResponseDto> list = getByPorId(porId);
            if (list == null || list.isEmpty()) {
                throw new RuntimeException("Purchase Order Return not found with ID: " + porId);
            }
            PurchaseOrderReturnResponseDto por = list.get(0);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // ── Colors ────────────────────────────────────────────────────────
            Color blackColor   = new DeviceRgb(0, 0, 0);
            Color darkBlue   = new DeviceRgb(13,  71,  116);
            Color tableBlue  = new DeviceRgb(26,  99,  153);
            Color lightBlueBg= new DeviceRgb(235, 245, 255);
            Color borderGray = new DeviceRgb(200, 210, 220);
            Color labelGray  = new DeviceRgb(100, 120, 140);
            Color altRow     = new DeviceRgb(245, 248, 252);
            Color remarksBg  = new DeviceRgb(255, 253, 235);
            Color remarksBdr = new DeviceRgb(220, 200, 100);

            // ══════════════════════════════════════════════════════════════════
            // SECTION 0 — COMPANY HEADER
            // ══════════════════════════════════════════════════════════════════
            CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId);

            if (isCompanyDetails == 1 && cmpDto != null) {
                float[] companyWidths = {20f, 2f, 78f};
                Table headerTable = new Table(UnitValue.createPercentArray(companyWidths));
                headerTable.setWidth(UnitValue.createPercentValue(100));

                try {
                    ImageData logoData = menuPreparationServiceImpl
                            .loadImageFromResource(
                                    environment.getProperty("app.image.url") + cmpDto.getLogo());
                    Image logo = new Image(logoData);
                    logo.setWidth(100f);
                    logo.setAutoScale(false);
                    logo.setHorizontalAlignment(
                            com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                    headerTable.addCell(new Cell(3, 2).add(logo)
                            .setVerticalAlignment(
                                    com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                            .setBorder(Border.NO_BORDER).setPaddingBottom(10f));
                } catch (Exception e) {
                    headerTable.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
                }

            //  Company Name 
                headerTable.addCell(new Cell()
                        .add(new Paragraph(cmpDto.getCompanyName())
                                .setFont(boldFont).setFontSize(14)
                                .setFontColor(blackColor)
                                .setTextAlignment(TextAlignment.LEFT))
                        .setBorder(Border.NO_BORDER)
                        .setPaddingLeft(15f));

                //  Phone
                headerTable.addCell(new Cell()
                        .add(new Paragraph()
                                .add(new Text("Phone : ")
                                        .setFont(boldFont).setFontSize(14)
                                        .setFontColor(blackColor))
                                .add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getOfficeNo()
                                        : "")
                                        .setFont(regularFont).setFontSize(14)
                                        .setFontColor(blackColor)))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setBorder(Border.NO_BORDER)
                        .setPaddingLeft(15f)
                        .setPaddingBottom(10f));
                
                //  Email
                headerTable.addCell(new Cell()
                        .add(new Paragraph()
                                .add(new Text("Email : ")
                                        .setFont(boldFont).setFontSize(14)
                                        .setFontColor(blackColor))
                                .add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
                                        .setFont(regularFont).setFontSize(14)
                                        .setFontColor(blackColor)))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setBorder(Border.NO_BORDER)
                        .setPaddingLeft(15f)
                        .setPaddingBottom(10f));
                

                headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                document.add(headerTable);

                Table companyDivider = new Table(UnitValue.createPercentArray(new float[]{100f}));
                companyDivider.setWidth(UnitValue.createPercentValue(100));
                companyDivider.addCell(new Cell().setHeight(2f)
                        .setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
                document.add(companyDivider);
                document.add(new Paragraph("").setMarginBottom(8));
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 1 — TOP HEADER
            // ══════════════════════════════════════════════════════════════════
            String printedOn = "Printed on: " +
                    java.time.LocalDate.now()
                            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

            String returnDateStr = por.getReturndate() != null
                    ? por.getReturndate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";

            float[] topW = {60f, 40f};
            Table topTable = new Table(UnitValue.createPercentArray(topW));
            topTable.setWidth(UnitValue.createPercentValue(100));

            topTable.addCell(new Cell()
                    .add(new Paragraph("Purchase Return Receipt")
                            .setFont(boldFont).setFontSize(20)
                            .setFontColor(darkBlue).setMarginBottom(2))
                    .add(new Paragraph(printedOn)
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(6));

            topTable.addCell(new Cell()
                    .add(new Paragraph(nvl(por.getPorcode()))
                            .setFont(boldFont).setFontSize(13)
                            .setFontColor(darkBlue)
                            .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(2))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(6));

            document.add(topTable);

            // ── Divider ───────────────────────────────────────────────────────
            Table divider = new Table(UnitValue.createPercentArray(new float[]{100f}));
            divider.setWidth(UnitValue.createPercentValue(100));
            divider.addCell(new Cell().setHeight(2f)
                    .setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
            document.add(divider);
            document.add(new Paragraph("").setMarginBottom(8));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 2 — INFO BAND
            // ══════════════════════════════════════════════════════════════════
            float[] vendorWidths = {24f, 16f, 18f, 18f, 24f};
            Table vendorTable = new Table(UnitValue.createPercentArray(vendorWidths));
            vendorTable.setWidth(UnitValue.createPercentValue(100));

            addVendorCell(vendorTable, "VENDOR",        nvl(por.getSupplierName()), lightBlueBg, labelGray, boldFont, regularFont, true,  false);
            addVendorCell(vendorTable, "GRN NO.",        nvl(por.getBillno()),       lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addVendorCell(vendorTable, "INVOICE TYPE",   nvl(por.getInvoicetype()),  lightBlueBg, labelGray, boldFont, regularFont, false, false);
         //   addVendorCell(vendorTable, "ORIGINAL PO",    nvl(por.getPocode()),       lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addVendorCell(vendorTable, "RETURN DATE",    returnDateStr,              lightBlueBg, labelGray, boldFont, regularFont, false, true);
            addVendorCell(vendorTable, "STOCK TYPE", nvl(por.getStockTypeName()),    lightBlueBg, labelGray, boldFont, regularFont, false, true);

            document.add(vendorTable);
            document.add(new Paragraph("").setMarginBottom(12));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 3 — ITEMS TABLE
            // ══════════════════════════════════════════════════════════════════
            // Columns: # | Item | Category | Unit | Orig Qty | Return Qty | Remaining | Price | O.Charge | CGST% | SGST% | IGST% | Total
         // ── Define columns based on isPrice flag ─────────────────────────────────
            float[] colWidths = isPrice == 1
                    ? new float[]{3f, 24f, 17f, 7f, 8f, 8f, 8f, 12f, 13f}
                    : new float[]{5f, 30f, 22f, 9f, 9f, 10f, 15f};

            Table itemTable = new Table(UnitValue.createPercentArray(colWidths));
            itemTable.setWidth(UnitValue.createPercentValue(100));

            String[] headers = isPrice == 1
                    ? new String[]{"#", "Item Name", "Category", "Unit", "Pur.Qty", "Ret.Qty", "Rem.Qty", "Price", "Total"}
                    : new String[]{"#", "Item Name", "Category", "Unit", "Pur.Qty", "Ret.Qty", "Remaining Qty"};

            TextAlignment[] aligns = isPrice == 1
                    ? new TextAlignment[]{
                        TextAlignment.CENTER, TextAlignment.LEFT,  TextAlignment.LEFT,  TextAlignment.CENTER,
                        TextAlignment.RIGHT,  TextAlignment.RIGHT, TextAlignment.RIGHT,
                        TextAlignment.RIGHT,  TextAlignment.RIGHT}
                    : new TextAlignment[]{
                        TextAlignment.CENTER, TextAlignment.LEFT, TextAlignment.LEFT, TextAlignment.CENTER,
                        TextAlignment.RIGHT,  TextAlignment.RIGHT, TextAlignment.RIGHT};

            for (int i = 0; i < headers.length; i++) {
                itemTable.addHeaderCell(new Cell()
                        .add(new Paragraph(headers[i])
                                .setFont(boldFont).setFontSize(10)
                                .setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(tableBlue)
                        .setTextAlignment(aligns[i])
                        .setPaddingTop(6).setPaddingBottom(6)
                        .setBorder(Border.NO_BORDER));
            }

            float calculatedSubAmount = 0f;
            boolean alternate = false;
            int srNo = 1;

            for (PurchaseOrderReturnDetailResponseDto d : por.getDetails()) {
                Color bg = alternate ? altRow : ColorConstants.WHITE;
                alternate = !alternate;

                // ── Calculate remaining ───────────────────────────────────────────
                double remainingQty = d.getQty() - d.getReturnedQty();

                if (isPrice == 1) {
                    float base = (float) d.getReturnedQty() * d.getPrice();
                    calculatedSubAmount += base;
                }

                // ── Always printed columns ────────────────────────────────────────
                itemTable.addCell(pCell(String.valueOf(srNo++),         bg, regularFont, TextAlignment.CENTER));
                itemTable.addCell(pCell(nvl(d.getRawMaterialName()),    bg, regularFont, TextAlignment.LEFT));
                itemTable.addCell(pCell(nvl(d.getRawMaterialCatName()), bg, regularFont, TextAlignment.LEFT));
                itemTable.addCell(pCell(nvl(d.getUnitName()),           bg, regularFont, TextAlignment.CENTER));
                itemTable.addCell(pCell(fmt1(d.getQty()),               bg, regularFont, TextAlignment.RIGHT));
                itemTable.addCell(pCell(fmt1(d.getReturnedQty()),       bg, regularFont, TextAlignment.RIGHT));
                itemTable.addCell(pCell(fmt1(remainingQty),             bg, regularFont, TextAlignment.RIGHT)); // Remaining Qty

                // ── Price columns — only when isPrice == 1 ────────────────────────
                if (isPrice == 1) {
                    itemTable.addCell(pCell(fmt1(d.getPrice()), bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(pCell(fmt1(d.getTotal()), bg, regularFont, TextAlignment.RIGHT));
                }
            }

            document.add(itemTable);
            document.add(new Paragraph("").setMarginBottom(8));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 4 — SUMMARY BOX
            // ══════════════════════════════════════════════════════════════════
            if (isPrice == 1) {
            float calculatedFinalAmount = calculatedSubAmount;

            float[] outerW = {65f, 35f};
            Table outerSummary = new Table(UnitValue.createPercentArray(outerW));
            outerSummary.setWidth(UnitValue.createPercentValue(100));
            outerSummary.addCell(new Cell().setBorder(Border.NO_BORDER));

            float[] summaryW = {60f, 40f};
            Table summaryBox = new Table(UnitValue.createPercentArray(summaryW));
            summaryBox.setWidth(UnitValue.createPercentValue(100));

            addSummaryLine(summaryBox, "Final Amount",
                    fmt1(calculatedFinalAmount),
                    darkBlue, boldFont, boldFont, darkBlue, true);

            outerSummary.addCell(new Cell().add(summaryBox)
                    .setBorder(Border.NO_BORDER).setPadding(0));
            document.add(outerSummary);
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 5 — REMARKS
            // ══════════════════════════════════════════════════════════════════
            if (por.getRemarks() != null && !por.getRemarks().trim().isEmpty()) {
                document.add(new Paragraph("").setMarginBottom(10));

                Table remarksTable = new Table(UnitValue.createPercentArray(new float[]{100f}));
                remarksTable.setWidth(UnitValue.createPercentValue(100));
                remarksTable.addCell(new Cell()
                        .add(new Paragraph("REMARKS")
                                .setFont(boldFont).setFontSize(8)
                                .setFontColor(new DeviceRgb(160, 120, 0)).setMarginBottom(3))
                        .add(new Paragraph(por.getRemarks())
                                .setFont(regularFont).setFontSize(9)
                                .setFontColor(ColorConstants.BLACK))
                        .setBackgroundColor(remarksBg)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(remarksBdr, 1f))
                        .setPadding(10));
                document.add(remarksTable);
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 6 — FOOTER
            // ══════════════════════════════════════════════════════════════════
            document.add(new Paragraph("").setMarginBottom(16));

            com.itextpdf.layout.borders.Border topBorder =
                    new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f);

            Table footer = new Table(UnitValue.createPercentArray(new float[]{50f, 50f}));
            footer.setWidth(UnitValue.createPercentValue(100));

            footer.addCell(new Cell()
                    .add(new Paragraph(nvl(por.getPorcode()) + "  |  " + nvl(por.getInvoicetype()))
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
                    .setBorder(topBorder)
                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

            footer.addCell(new Cell()
                    .add(new Paragraph("Total Items: " + por.getDetails().size())
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(topBorder)
                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

            document.add(footer);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    private void addVendorCell(Table table, String label, String value,
            Color bg, Color labelColor, PdfFont boldFont, PdfFont regularFont,
            boolean isFirst, boolean isLast) {

        com.itextpdf.layout.borders.Border border =
                new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(180, 200, 220), 0.5f);

        table.addCell(new Cell()
                .add(new Paragraph(label)
                        .setFont(regularFont).setFontSize(7).setFontColor(labelColor).setMarginBottom(2))
                .add(new Paragraph(value)
                        .setFont(boldFont).setFontSize(10).setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(bg).setPadding(10)
                .setBorder(border)
                .setBorderLeft(isFirst ? border : Border.NO_BORDER)
                .setBorderRight(isLast  ? border : border));
    }

    private void addSummaryLine(Table table, String label, String value,
            Color bg, PdfFont labelFont, PdfFont valueFont,
            Color borderColor, boolean isTotal) {

        com.itextpdf.layout.borders.Border bdr =
                new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f);
        Color fontColor = isTotal ? ColorConstants.WHITE : ColorConstants.BLACK;

        table.addCell(new Cell()
                .add(new Paragraph(label).setFont(labelFont).setFontSize(9).setFontColor(fontColor))
                .setBackgroundColor(bg).setBorder(bdr)
                .setPaddingTop(5).setPaddingBottom(5).setPaddingLeft(8));

        table.addCell(new Cell()
                .add(new Paragraph(value).setFont(valueFont).setFontSize(9).setFontColor(fontColor))
                .setBackgroundColor(bg).setBorder(bdr)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(5).setPaddingBottom(5).setPaddingRight(8));
    }

    private Cell pCell(String text, Color bg, PdfFont font, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(8))
                .setBackgroundColor(bg).setTextAlignment(align)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                        new DeviceRgb(220, 228, 235), 0.3f))
                .setPaddingTop(4).setPaddingBottom(4);
    }

    private String fmt1(double val) { return String.format("%.1f", val); }
    private String fmt1(float val)  { return String.format("%.1f", val); }
    private String fmt1(Double val) { return val != null ? String.format("%.1f", val) : "0.0"; }
    private String fmt1(Float val)  { return val != null ? String.format("%.1f", val) : "0.0"; }

    private String nvl(String val)  { return val != null && !val.isEmpty() ? val : "-"; }
    
    public CompanyDetailsResponseDto getCompanyDetails(Long userId) {
		UserMasterEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		Optional<Object[]> op = menuAllocationRepository.getCompanyDetailsByUserId(userId);

		if (!op.isPresent()) {
			return new CompanyDetailsResponseDto();
		} else {
			Object[] row = op.get();

			if (row.length == 1 && row[0] instanceof Object[]) {
				row = (Object[]) row[0];
			}

			CompanyDetailsResponseDto dto = new CompanyDetailsResponseDto();
			dto.setCompanyName(row[0] != null ? row[0].toString() : "");
			dto.setCountryCode(row[1] != null ? row[1].toString() : "");
			dto.setCompanyEmail(row[2] != null ? row[2].toString() : "");
			dto.setOfficeNo(row[3] != null ? row[3].toString() : "");
			dto.setAddress(row[4] != null ? row[4].toString() : "");
			dto.setLogo(row[5] != null ? row[5].toString() : "");
			return dto;
		}
	}

    @Override
    public List<PocodeWithStatusResponseDto> getAllPocodes(Long userId) {

        List<PocodeWithStatusResponseDto> pocodes = poRepository.findAllPocodes(userId);

        return pocodes.stream().map(p -> {

            boolean isReturned = porRepository
                    .existsByPurchaseOrderIdAndIsDeleteFalse(p.getId());

            p.setReturned(isReturned);

            return p;

        }).collect(Collectors.toList());
    }
    
    @Override
    public byte[] generateExcelReport(Long porId, Long userId) {
        try {
            List<PurchaseOrderReturnResponseDto> list = getByPorId(porId);
            if (list == null || list.isEmpty()) {
                throw new RuntimeException("Purchase Order Return not found with ID: " + porId);
            }
            PurchaseOrderReturnResponseDto por = list.get(0);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet    sheet    = workbook.createSheet("Purchase Return");

            // ── Cell styles ───────────────────────────────────────────────────
            // Title style
            XSSFCellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Header style (dark blue bg, white text)
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)26, (byte)99, (byte)153}, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
            headerFont.setFontHeightInPoints((short) 10);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Info label style (bold)
            XSSFCellStyle labelStyle = workbook.createCellStyle();
            XSSFFont labelFont = workbook.createFont();
            labelFont.setBold(true);
            labelFont.setFontHeightInPoints((short) 10);
            labelStyle.setFont(labelFont);
            labelStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)235, (byte)245, (byte)255}, null));
            labelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            labelStyle.setBorderBottom(BorderStyle.THIN);
            labelStyle.setBorderTop(BorderStyle.THIN);
            labelStyle.setBorderLeft(BorderStyle.THIN);
            labelStyle.setBorderRight(BorderStyle.THIN);

            // Info value style
            XSSFCellStyle valueStyle = workbook.createCellStyle();
            XSSFFont valueFont = workbook.createFont();
            valueFont.setFontHeightInPoints((short) 10);
            valueStyle.setFont(valueFont);
            valueStyle.setBorderBottom(BorderStyle.THIN);
            valueStyle.setBorderTop(BorderStyle.THIN);
            valueStyle.setBorderLeft(BorderStyle.THIN);
            valueStyle.setBorderRight(BorderStyle.THIN);

            // Alt row style
            XSSFCellStyle altStyle = workbook.createCellStyle();
            altStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)245, (byte)248, (byte)252}, null));
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            altStyle.setFont(valueFont);
            altStyle.setBorderBottom(BorderStyle.THIN);
            altStyle.setBorderTop(BorderStyle.THIN);
            altStyle.setBorderLeft(BorderStyle.THIN);
            altStyle.setBorderRight(BorderStyle.THIN);

            // Normal row style
            XSSFCellStyle normalStyle = workbook.createCellStyle();
            normalStyle.setFont(valueFont);
            normalStyle.setBorderBottom(BorderStyle.THIN);
            normalStyle.setBorderTop(BorderStyle.THIN);
            normalStyle.setBorderLeft(BorderStyle.THIN);
            normalStyle.setBorderRight(BorderStyle.THIN);

            // Total row style (dark blue)
            XSSFCellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)13, (byte)71, (byte)116}, null));
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalFont.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
            totalFont.setFontHeightInPoints((short) 10);
            totalStyle.setFont(totalFont);
            totalStyle.setBorderBottom(BorderStyle.THIN);
            totalStyle.setBorderTop(BorderStyle.THIN);
            totalStyle.setBorderLeft(BorderStyle.THIN);
            totalStyle.setBorderRight(BorderStyle.THIN);

            int rowNum = 0;

            // ══════════════════════════════════════════════════════════════════
            // ROW 0 — TITLE
            // ══════════════════════════════════════════════════════════════════
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(22);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Purchase Return Receipt");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            rowNum++; // blank row

            // ══════════════════════════════════════════════════════════════════
            // ROWS 2-5 — INFO BAND (2 columns per row)
            // ══════════════════════════════════════════════════════════════════
            String returnDateStr = por.getReturndate() != null
                    ? por.getReturndate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";

            String[][] infoRows = {
                {"POR Code",     nvl(por.getPorcode()),       "Vendor",        nvl(por.getSupplierName())},
                {"GRN No.",      nvl(por.getBillno()),        "Invoice Type",  nvl(por.getInvoicetype())},
                {"Original PO",  nvl(por.getPocode()),        "Return Date",   returnDateStr},
                {"Voucher No.",  nvl(por.getVoucher()),        "Remarks",       nvl(por.getRemarks())}
            };

            for (String[] info : infoRows) {
                Row infoRow = sheet.createRow(rowNum++);
                infoRow.setHeightInPoints(18);

                // Label 1
                org.apache.poi.ss.usermodel.Cell c = infoRow.createCell(0);
                c.setCellValue(info[0]); c.setCellStyle(labelStyle);
                // Value 1 (spans 2 cols)
                c = infoRow.createCell(1);
                c.setCellValue(info[1]); c.setCellStyle(valueStyle);
                infoRow.createCell(2).setCellStyle(valueStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 1, 2));

                // spacer
                infoRow.createCell(3).setCellStyle(normalStyle);

                // Label 2
                c = infoRow.createCell(4);
                c.setCellValue(info[2]); c.setCellStyle(labelStyle);
                // Value 2 (spans 3 cols)
                c = infoRow.createCell(5);
                c.setCellValue(info[3]); c.setCellStyle(valueStyle);
                infoRow.createCell(6).setCellStyle(valueStyle);
                infoRow.createCell(7).setCellStyle(valueStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 5, 7));
            }

            rowNum++; // blank row

            // ══════════════════════════════════════════════════════════════════
            // HEADER ROW
            // ══════════════════════════════════════════════════════════════════
            String[] headers = {
                "#", "Item Name", "Category", "Unit",
                "Pur. Qty", "Return Qty", "Price", "Total"
            };

            Row headerRow = sheet.createRow(rowNum++);
            headerRow.setHeightInPoints(18);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell hc = headerRow.createCell(i);
                hc.setCellValue(headers[i]);
                hc.setCellStyle(headerStyle);
            }

            // ══════════════════════════════════════════════════════════════════
            // DATA ROWS
            // ══════════════════════════════════════════════════════════════════
            int srNo = 1;
            boolean alternate = false;
            float calculatedFinalAmount = 0f;

            for (PurchaseOrderReturnDetailResponseDto d : por.getDetails()) {
                XSSFCellStyle rowStyle = alternate ? altStyle : normalStyle;
                alternate = !alternate;

                double returnedQty = d.getReturnedQty();
                float  price       = d.getPrice()       != 0    ? d.getPrice()       : 0f;
                float  total       = d.getTotal()        != 0   ? d.getTotal()        : 0f;
                calculatedFinalAmount += (float)(returnedQty * price);

                Row dataRow = sheet.createRow(rowNum++);
                dataRow.setHeightInPoints(16);

                createCell(dataRow, 0, String.valueOf(srNo++),                           rowStyle);
                createCell(dataRow, 1, d.getRawMaterialName()    != null
                        ? d.getRawMaterialName()    : "-",                               rowStyle);
                createCell(dataRow, 2, d.getRawMaterialCatName() != null
                        ? d.getRawMaterialCatName() : "-",                               rowStyle);
                createCell(dataRow, 3, d.getUnitName()           != null
                        ? d.getUnitName()           : "-",                               rowStyle);
                createNumericCell(dataRow, 4, d.getQty(), rowStyle);
                createNumericCell(dataRow, 5, returnedQty,                               rowStyle);
                createNumericCell(dataRow, 6, price,                                     rowStyle);
                createNumericCell(dataRow, 7, total,                                     rowStyle);
            }

            // ══════════════════════════════════════════════════════════════════
            // TOTAL ROW
            // ══════════════════════════════════════════════════════════════════
            Row totalRow = sheet.createRow(rowNum++);
            totalRow.setHeightInPoints(18);

            for (int i = 0; i < 7; i++) {
                org.apache.poi.ss.usermodel.Cell tc = totalRow.createCell(i);
                if (i == 6) tc.setCellValue("Final Amount");
                tc.setCellStyle(totalStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 6));

            org.apache.poi.ss.usermodel.Cell totalAmtCell = totalRow.createCell(7);
            totalAmtCell.setCellValue(calculatedFinalAmount);
            totalAmtCell.setCellStyle(totalStyle);

            // ══════════════════════════════════════════════════════════════════
            // COLUMN WIDTHS
            // ══════════════════════════════════════════════════════════════════
            sheet.setColumnWidth(0, 1500);    // #
            sheet.setColumnWidth(1, 8000);    // Item Name
            sheet.setColumnWidth(2, 6000);    // Category
            sheet.setColumnWidth(3, 3000);    // Unit
            sheet.setColumnWidth(4, 3000);    // Pur. Qty
            sheet.setColumnWidth(5, 3500);    // Return Qty
            sheet.setColumnWidth(6, 4000);    // Price
            sheet.setColumnWidth(7, 4000);    // Total

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── Excel cell helpers ────────────────────────────────────────────────────
    private void createCell(Row row, int col, String value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int col, double value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}