package com.crmportal.service.impl;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.ChefRequisitionService;
import com.crmportal.service.StockLedgerEntryService;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.core.env.Environment;

@Service
@Transactional
public class ChefRequisitionServiceImpl implements ChefRequisitionService {

    @Autowired
    private ChefRequisitionRepository crRepository;

    @Autowired
    private ChefRequisitionDetailRepository crDetailRepository;

    @Autowired
    private PartyMasterRepository partyRepository;

    @Autowired
    private StockTypeRepository stockTypeRepository;

    @Autowired
    private RawMaterialMasterRepository rawMaterialRepository;

    @Autowired
    private UserMasterRepository userRepository;

    @Autowired
    private StockLedgerEntryService stockLedgerEntryService;
    
    @Autowired
    private Environment environment;

    @Autowired
    private MenuPreparationServiceImpl menuPreparationServiceImpl;
    
    @Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Add or Update ─────────────────────────────────────────────────────────
    @Override
    public ChefRequisitionResponseDto addOrUpdate(ChefRequisitionRequestDto request) {

        ChefRequisitionEntity entity;

        if (request.getId() == null || request.getId() == 0 || request.getId() == -1) {
            entity = new ChefRequisitionEntity();
            entity.setCrcode(generateCrCode());
            entity.setStatus("PENDING"); // default status
        } else {
            entity = crRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Chef Requisition not found"));
            crDetailRepository.deleteByChefRequisitionId(entity.getId());
        }

        //entity.setParty(partyRepository.findById(request.getPartyId()).orElse(null));
        entity.setParty(
        	    request.getPartyId() != null
        	        ? partyRepository.findById(request.getPartyId()).orElse(null)
        	        : null
        	);
        entity.setVoucher(request.getVoucher());
        entity.setCrdate(LocalDate.parse(request.getCrdate(), formatter));
        entity.setInvoicetype(request.getInvoicetype());

        if (request.getStockTypeId() != null) {
            entity.setStocktype(stockTypeRepository.findById(request.getStockTypeId()).orElse(null));
        }

        entity.setRemarks(request.getRemarks());
        entity.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));

        entity = crRepository.save(entity);

        // Save details
        if (request.getDetails() != null && !request.getDetails().isEmpty()) {

            List<ChefRequisitionDetailEntity> detailList = new ArrayList<>();

            for (ChefRequisitionDetailRequestDto d : request.getDetails()) {

                ChefRequisitionDetailEntity detail = new ChefRequisitionDetailEntity();
                RawMaterialMasterEntity rm = rawMaterialRepository
                        .findById(d.getRawMaterialId()).orElse(null);

                detail.setChefRequisition(entity);
                detail.setRawMaterial(rm);
                detail.setRawMaterialCat(rm != null ? rm.getRawMaterialCat() : null);
                detail.setUnit(rm != null ? rm.getUnit() : null);
                detail.setQty(d.getQty());
                detail.setUserid(request.getUserId());

                detailList.add(detail);
            }

            crDetailRepository.saveAll(detailList);
            //  NOT saving to stock_ledger here
        }

        return convertToDto(entity);
    }

    // ── Update Status ─────────────────────────────────────────────────────────
    @Override
    public ChefRequisitionResponseDto updateStatus(Long crId, String status) {

        ChefRequisitionEntity entity = crRepository.findById(crId)
                .orElseThrow(() -> new RuntimeException("Chef Requisition not found"));

        entity.setStatus(status.toUpperCase());
        entity = crRepository.save(entity);
        crRepository.flush(); //  flush CR save

        //  Only save to stock_ledger when status is COMPLETED
        if ("COMPLETED".equalsIgnoreCase(status)) {
            crDetailRepository.flush(); //  flush details so updated qty is visible
          //  stockLedgerEntryService.saveChefRequisition(entity);
        }

        return convertToDto(entity);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    @Override
    public List<ChefRequisitionResponseDto> getByUser(Long userId) {
        return crRepository.getByUserDesc(userId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public ChefRequisitionResponseDto getByCrId(Long crId) {
        ChefRequisitionEntity entity = crRepository.findById(crId)
                .orElseThrow(() -> new RuntimeException("Chef Requisition not found"));
        return convertToDto(entity);
    }

    @Override
    public void deleteByCrId(Long crId) {
        ChefRequisitionEntity entity = crRepository.findById(crId)
                .orElseThrow(() -> new RuntimeException("Chef Requisition not found"));
        entity.setIsDelete(true);
        crRepository.save(entity);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String generateCrCode() {

        LocalDate today = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        String prefix = "CR-" + today.format(dtf) + "-";

        String lastCode = crRepository.findMaxCrcodeByPrefix(prefix);
        long nextNumber = 1;

        if (lastCode != null) {
            try {
                String[] parts = lastCode.split("-");
                nextNumber = Long.parseLong(parts[parts.length - 1]) + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }

        return prefix + String.format("%03d", nextNumber); // e.g. CRJ-24032026-001
    }

    private ChefRequisitionResponseDto convertToDto(ChefRequisitionEntity entity) {

        ChefRequisitionResponseDto dto = new ChefRequisitionResponseDto();

        dto.setId(entity.getId());
        dto.setCrcode(entity.getCrcode());
        dto.setPartyId(entity.getParty() != null ? entity.getParty().getId() : null);
        dto.setPartyName(entity.getParty() != null ? entity.getParty().getNameEnglish() : null);
        dto.setPartyNameHindi(entity.getParty() != null ? entity.getParty().getNameHindi() : null);
        dto.setPartyNameGujarati(entity.getParty() != null ? entity.getParty().getNameGujarati() : null);
        dto.setVoucher(entity.getVoucher());
        dto.setCrdate(entity.getCrdate().format(formatter));
        dto.setInvoicetype(entity.getInvoicetype());

        if (entity.getStocktype() != null) {
            dto.setStockTypeId(entity.getStocktype().getId());
            dto.setStockTypeName(entity.getStocktype().getNameEnglish());
            dto.setStockTypeNameHindi(entity.getStocktype().getNameHindi());
            dto.setStockTypeNameGujarati(entity.getStocktype().getNameGujarati());
        }

        dto.setRemarks(entity.getRemarks());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setUserId(entity.getUser().getId());

        List<ChefRequisitionDetailResponseDto> detailDtos =
                crDetailRepository.findByChefRequisitionId(entity.getId())
                        .stream().map(d -> {
                            ChefRequisitionDetailResponseDto rd = new ChefRequisitionDetailResponseDto();
                            rd.setId(d.getId());
                            if (d.getRawMaterial() != null) {
                                rd.setRawMaterialId(d.getRawMaterial().getId());
                                rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
                                rd.setRawMaterialNameHindi(d.getRawMaterial().getNameHindi());
                                rd.setRawMaterialNameGujarati(d.getRawMaterial().getNameGujarati());
                            }
                            if (d.getRawMaterialCat() != null) {
                                rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
                                rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
                                rd.setRawMaterialCatNameHindi(d.getRawMaterialCat().getNameHindi());
                                rd.setRawMaterialCatNameGujarati(d.getRawMaterialCat().getNameGujarati());
                            }
                            if (d.getUnit() != null) {
                                rd.setUnitId(d.getUnit().getId());
                                rd.setUnitName(d.getUnit().getNameEnglish());
                                rd.setUnitNameHindi(d.getUnit().getNameHindi());
                                rd.setUnitNameGujarati(d.getUnit().getNameGujarati());
                            }
                            rd.setPrice(d.getRawMaterial().getSupplierRate());
                            rd.setQty(d.getQty());
                            return rd;
                        }).collect(Collectors.toList());

        dto.setDetails(detailDtos);
        return dto;
    }
    
    @Override
    public byte[] generatePdfReport(Long crId, Long userId, Integer isCompanyDetails, Integer lang) {
        try {
        	PdfFont boldFont = null;
        	PdfFont regularFont = null;
        	
        	PdfFont boldFont1 = null;
        	PdfFont regularFont1 = null;
        	
            boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            
            ChefRequisitionResponseDto cr = getByCrId(crId);
            if (cr == null) {
                throw new RuntimeException("Chef Requisition not found with ID: " + crId);
            }

            String partyName = "", stockType = "";
            if(lang == 1) {
        		regularFont1 = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
        		boldFont1 = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
        		partyName = cr.getPartyNameHindi() != null ? cr.getPartyNameHindi() : "";
        		stockType = cr.getStockTypeNameHindi() != null ? cr.getStockTypeNameHindi() : "";
            }else if(lang == 2) {
        		regularFont1 = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
        		boldFont1 = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
        		partyName = cr.getPartyNameGujarati() != null ? cr.getPartyNameGujarati() : "";
        		stockType = cr.getStockTypeNameGujarati() != null ? cr.getStockTypeNameGujarati() : "";
        	}else {
        		regularFont1 = menuPreparationServiceImpl.loadFont("/fonts/playfair-display-regular.ttf");
        		boldFont1 = menuPreparationServiceImpl.loadFont("/fonts/Playfair-display-bold.ttf");
                partyName = cr.getPartyName() != null ? cr.getPartyName() : "";
                stockType = cr.getStockTypeName() != null ? cr.getStockTypeName() : "";
        	}
        	
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer  = new PdfWriter(baos);
            PdfDocument pdf   = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            // ── Colors ────────────────────────────────────────────────────────
            Color darkBlue    = new DeviceRgb(13,  71,  116);
            Color blackColor    = new DeviceRgb(0, 0, 0);
            Color tableBlue   = new DeviceRgb(26,  99,  153);
            Color lightBlueBg = new DeviceRgb(235, 245, 255);
            Color cardBg      = new DeviceRgb(245, 248, 252);
            Color borderGray  = new DeviceRgb(200, 210, 220);
            Color labelGray   = new DeviceRgb(100, 120, 140);
            Color altRow      = new DeviceRgb(245, 248, 252);
            Color badgeBg     = new DeviceRgb(210, 230, 255);

            // Status badge color — green for COMPLETED, orange for PENDING
            boolean isCompleted = "COMPLETED".equalsIgnoreCase(cr.getStatus());
            Color statusColor = isCompleted
                    ? new DeviceRgb(0, 128, 0)
                    : new DeviceRgb(200, 100, 0);
            Color statusBg = isCompleted
                    ? new DeviceRgb(220, 255, 220)
                    : new DeviceRgb(255, 240, 210);

            // ══════════════════════════════════════════════════════════════════
            // SECTION 0 — COMPANY HEADER
            // ══════════════════════════════════════════════════════════════════
            CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId);

            if (isCompanyDetails == 1) {

                float[] companyWidths = {20f, 2f, 78f};
                Table headerTable = new Table(UnitValue.createPercentArray(companyWidths));
                headerTable.setWidth(UnitValue.createPercentValue(100));

                //  Logo (spans 3 rows × 2 cols — exact match to reference)
                try {
                    ImageData logoData = menuPreparationServiceImpl
                            .loadImageFromResource(
                                    environment.getProperty("app.image.url") + cmpDto.getLogo());

                    Image logo = new Image(logoData);
                    logo.setWidth(100f);
                    logo.setAutoScale(false);
                    logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

                    headerTable.addCell(new Cell(3, 2)
                            .add(logo)
                            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                            .setBorder(Border.NO_BORDER)
                            .setPaddingBottom(10f));

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
                

                // ── Empty filler cell (reference has this exact line) ─────────
                headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));

                document.add(headerTable);

                //  Dark blue divider 
                Table companyDivider = new Table(UnitValue.createPercentArray(new float[]{100f}));
                companyDivider.setWidth(UnitValue.createPercentValue(100));
                companyDivider.addCell(new Cell()
                        .setHeight(2f)
                        .setBackgroundColor(darkBlue)
                        .setBorder(Border.NO_BORDER));
                document.add(companyDivider);
                document.add(new Paragraph("").setMarginBottom(8));
            }	

            // ══════════════════════════════════════════════════════════════════
            // SECTION 1 — TOP HEADER
            // ══════════════════════════════════════════════════════════════════
            String printedOn = "Printed on: " +
                    java.time.LocalDate.now()
                            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) +
                    " at " +
                    java.time.LocalTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
                            .toLowerCase();

            float[] topW = {60f, 40f};
            Table topTable = new Table(UnitValue.createPercentArray(topW));
            topTable.setWidth(UnitValue.createPercentValue(100));

            // Left: title + badge + status badge + printed-on
            topTable.addCell(new Cell()
                    .add(new Paragraph("Chef Requisition")
                            .setFont(boldFont).setFontSize(22)
                            .setFontColor(darkBlue).setMarginBottom(3))	
                    .add(new Paragraph(printedOn)
                            .setFont(regularFont).setFontSize(8)
                            .setFontColor(labelGray))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(6));

            // Right: CR code + date
            topTable.addCell(new Cell()
                    .add(new Paragraph(crNvl(cr.getCrcode()))
                            .setFont(boldFont).setFontSize(14)
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
            document.add(new Paragraph("").setMarginBottom(10));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 2 — INFO BAND
            // Party | Stock Type | Invoice Type | Voucher | Date
            // ══════════════════════════════════════════════════════════════════
            float[] bandW = {34f, 33f, 33f};
            Table bandTable = new Table(UnitValue.createPercentArray(bandW));
            bandTable.setWidth(UnitValue.createPercentValue(100));

            addCrBandCell(bandTable, "PARTY NAME",   crNvl(partyName),    lightBlueBg, labelGray, boldFont1, regularFont, borderGray);
            addCrBandCell(bandTable, "STOCK TYPE",   crNvl(stockType),lightBlueBg, labelGray, boldFont1, regularFont, borderGray);
            addCrBandCell(bandTable, "REQ. DATE",    crNvl(cr.getCrdate()),       lightBlueBg, labelGray, boldFont1, regularFont, borderGray);

            document.add(bandTable);
            document.add(new Paragraph("").setMarginBottom(12));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 3 — ITEMS TABLE
            // # | Item Name | Category | Unit | Qty
            // ══════════════════════════════════════════════════════════════════
            float[] colW = {8f, 32f, 22f, 10f, 10f, 10f, 18f};
            Table itemTable = new Table(UnitValue.createPercentArray(colW));
            itemTable.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"Sr No.", "Item Name", "Category", "Qty", "Unit", "Unit Price", "Amount"};
            TextAlignment[] aligns = {
                TextAlignment.CENTER, TextAlignment.CENTER, TextAlignment.CENTER,
                TextAlignment.CENTER, TextAlignment.CENTER, TextAlignment.CENTER,
                TextAlignment.CENTER
            };

            for (int i = 0; i < headers.length; i++) {
                itemTable.addHeaderCell(new Cell()
                        .add(new Paragraph(headers[i])
                                .setFont(boldFont).setFontSize(9)
                                .setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(tableBlue)
                        .setTextAlignment(aligns[i])
                        .setPaddingTop(7).setPaddingBottom(7)
                        .setBorder(Border.NO_BORDER));
            }

            // ── Rows + total ──────────────────────────────────────────────────
            double totalQty = 0;
            boolean alternate = false;
            int srNo = 1;
            BigDecimal grandTotal = BigDecimal.ZERO;
            for (ChefRequisitionDetailResponseDto d : cr.getDetails()) {
                Color bg = alternate ? altRow : ColorConstants.WHITE;
                alternate = !alternate;

                double qty = d.getQty();
                totalQty += qty;

                BigDecimal price = d.getPrice();
                
                BigDecimal total = price.multiply(BigDecimal.valueOf(qty))
                        .setScale(2, RoundingMode.HALF_UP);
                
                grandTotal = grandTotal.add(total);
                
                String rawMaterialName = "";
                String rawCatName = "";
                String unitName = "";
                if(lang == 1) {
                	rawMaterialName = d.getRawMaterialNameHindi();
                	rawCatName = d.getRawMaterialCatNameHindi();
                	unitName = d.getUnitNameHindi();
                }else if(lang == 2) {
                	rawMaterialName = d.getRawMaterialNameGujarati();
                	rawCatName = d.getRawMaterialCatNameGujarati();
                	unitName = d.getUnitNameGujarati();	
                }else {
                	rawMaterialName = d.getRawMaterialName();
                	rawCatName = d.getRawMaterialCatName();
                	unitName = d.getUnitName();
                }
                itemTable.addCell(crCell(String.valueOf(srNo++),          bg, regularFont1, TextAlignment.CENTER));
                itemTable.addCell(crCell(crNvl(rawMaterialName),   bg, regularFont1, TextAlignment.LEFT));
                itemTable.addCell(crCell(crNvl(rawCatName),bg, regularFont1, TextAlignment.LEFT));
                itemTable.addCell(crCell(crFmtQty(qty),                   bg, boldFont1,    TextAlignment.CENTER));
                itemTable.addCell(crCell(crNvl(unitName),          bg, regularFont1, TextAlignment.CENTER));
                itemTable.addCell(crCell(crNvl(price.toString()),                   bg, boldFont1,    TextAlignment.CENTER));
                itemTable.addCell(crCell(crNvl(total.toString()),                   bg, boldFont1,    TextAlignment.CENTER));
            }

			itemTable.addCell(new Cell(1, 6)
					.add(new Paragraph("GRAND TOTAL").setFont(boldFont).setFontSize(10)
							.setFontColor(ColorConstants.WHITE))
					.setBackgroundColor(tableBlue).setTextAlignment(TextAlignment.RIGHT).setPaddingTop(7)
					.setPaddingBottom(7).setBorder(Border.NO_BORDER));
			
			itemTable.addCell(new Cell()
					.add(new Paragraph(grandTotal.toString()).setFont(boldFont).setFontSize(12)
							.setFontColor(ColorConstants.WHITE))
					.setBackgroundColor(tableBlue).setTextAlignment(TextAlignment.CENTER).setPaddingTop(7)
					.setPaddingBottom(7).setBorder(Border.NO_BORDER));
            
            document.add(itemTable);
            document.add(new Paragraph("").setMarginBottom(12));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 4 — SUMMARY CARDS
            // Total Items | Total Qty | Status
            // ══════════════════════════════════════════════════════════════════
//            float[] cardW = {32f, 2f, 32f, 2f, 32f};
//            Table cardTable = new Table(UnitValue.createPercentArray(cardW));
//            cardTable.setWidth(UnitValue.createPercentValue(100));
//
//            cardTable.addCell(buildCrCard(
//                    "TOTAL ITEMS",
//                    String.valueOf(cr.getDetails().size()),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            cardTable.addCell(buildCrCard(
//                    "TOTAL QTY",
//                    crFmtQty(totalQty),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            // Status card — colored bg
//            cardTable.addCell(new Cell()
//                    .add(new Paragraph("STATUS")
//                            .setFont(regularFont).setFontSize(7)
//                            .setFontColor(labelGray).setMarginBottom(4))
//                    .add(new Paragraph(cr.getStatus() != null
//                            ? cr.getStatus().toUpperCase() : "PENDING")
//                            .setFont(boldFont).setFontSize(18)
//                            .setFontColor(statusColor))
//                    .setBackgroundColor(statusBg)
//                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.8f))
//                    .setPadding(12));
//
//            document.add(cardTable);

            // ══════════════════════════════════════════════════════════════════
            // SECTION 5 — REMARKS
            // ══════════════════════════════════════════════════════════════════
            if (cr.getRemarks() != null && !cr.getRemarks().trim().isEmpty()) {
                document.add(new Paragraph("").setMarginBottom(10));
                Table rt = new Table(UnitValue.createPercentArray(new float[]{100f}));
                rt.setWidth(UnitValue.createPercentValue(100));
                rt.addCell(new Cell()
                        .add(new Paragraph("REMARKS")
                                .setFont(boldFont).setFontSize(8)
                                .setFontColor(new DeviceRgb(160, 120, 0)).setMarginBottom(3))
                        .add(new Paragraph(cr.getRemarks())
                                .setFont(regularFont).setFontSize(9))
                        .setBackgroundColor(new DeviceRgb(255, 253, 235))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                                new DeviceRgb(220, 200, 100), 1f))
                        .setPadding(10));
                document.add(rt);
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 6 — FOOTER
            // ══════════════════════════════════════════════════════════════════
            document.add(new Paragraph("").setMarginBottom(16));

            com.itextpdf.layout.borders.Border topBorder =
                    new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f);

            Table footer = new Table(UnitValue.createPercentArray(new float[]{60f, 40f}));
            footer.setWidth(UnitValue.createPercentValue(100));

            footer.addCell(new Cell()
                    .add(new Paragraph(
                            crNvl(cr.getCrcode()) + "  |  Chef Requisition  |  Party: "
                            + crNvl(cr.getPartyName()))
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
                    .setBorder(topBorder)
                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

            footer.addCell(new Cell()
                    .add(new Paragraph("Total Items: " + cr.getDetails().size())
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
    
    
    private void addCrBandCell(Table table, String label, String value,
            Color bg, Color labelColor, PdfFont boldFont, PdfFont regularFont,
            Color borderGray) {
        table.addCell(new Cell()
                .add(new Paragraph(label)
                        .setFont(regularFont).setFontSize(7)
                        .setFontColor(labelColor).setMarginBottom(3))
                .add(new Paragraph(value)
                        .setFont(boldFont).setFontSize(11)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(bg)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f))
                .setPadding(10));
    }

    private Cell buildCrCard(String label, String value,
            Color cardBg, Color darkBlue, Color labelGray,
            PdfFont boldFont, PdfFont regularFont, Color borderGray) {
        return new Cell()
                .add(new Paragraph(label)
                        .setFont(regularFont).setFontSize(7)
                        .setFontColor(labelGray).setMarginBottom(4))
                .add(new Paragraph(value)
                        .setFont(boldFont).setFontSize(20)
                        .setFontColor(darkBlue))
                .setBackgroundColor(cardBg)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.8f))
                .setPadding(12);
    }

    private Cell crCell(String text, Color bg, PdfFont font, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "")
                        .setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setTextAlignment(align)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                        new DeviceRgb(220, 228, 235), 0.3f))
                .setPaddingTop(5).setPaddingBottom(5);
    }

    private String crFmtQty(double val) {
        if (val == Math.floor(val) && !Double.isInfinite(val)) {
            return String.valueOf((long) val);
        }
        return String.format("%.3f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private String crNvl(String val) {
        return (val != null && !val.isEmpty()) ? val : "\u2014";
    }
    
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
    public List<CrcodeResponseDto> getAllCrcodes(Long userId) {
        List<Object[]> rows = crRepository.findAllCrcodesByUserId(userId);
        List<CrcodeResponseDto> list = new ArrayList<>();
        for (Object[] row : rows) {
            CrcodeResponseDto dto = new CrcodeResponseDto();
            dto.setId(((Number) row[0]).longValue());
            dto.setCrcode((String) row[1]);
            list.add(dto);
        }
        return list;
    }

    @Override
    public ChefRequisitionResponseDto getByCrcode(String crcode) {
    	ChefRequisitionEntity entity = crRepository.findByCrcodeAndIsDeleteFalse(crcode)
    	        .orElseThrow(() -> new RuntimeException(
    	                "Chef Requisition not found with crcode: " + crcode));
        return convertToDto(entity);
    }
}