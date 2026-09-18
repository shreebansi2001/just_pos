package com.crmportal.service.impl;

import java.math.BigDecimal;


import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.PurchaseOrderDetailEntity;
import com.crmportal.entity.PurchaseOrderEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PurchaseOrderDetailRepository;
import com.crmportal.repository.PurchaseOrderRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.StockTypeRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.PurchaseOrderDetailRequestDto;
import com.crmportal.request.dto.PurchaseOrderRequestDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.PurchaseOrderDetailResponseDto;
import com.crmportal.response.dto.PurchaseOrderRawMaterialDetailsResponseDto;
import com.crmportal.response.dto.PurchaseOrderResponseDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.service.PurchaseOrderService;
import com.crmportal.service.StockLedgerEntryService;
import com.crmportal.service.UnitMasterService;

import java.io.ByteArrayOutputStream;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.Border;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

	@Autowired
    private PurchaseOrderRepository poRepository;
	
	@Autowired
    private PurchaseOrderDetailRepository detailRepository;
	
	@Autowired
    private PartyMasterRepository partyRepository;
	
	@Autowired
    private RawMaterialMasterRepository rawRepository;
	
	@Autowired
	private UserMasterRepository userRepository;
	
	@Autowired
	private StockLedgerEntryService stockLedgerEntryService;

	@Autowired
	private UnitMasterRepository unitMasterRepository;
	
	@Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;
	
	@Autowired
	private StockTypeRepository stockTypeRepository;
	
	@Autowired
	Environment environment;
	
	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	UnitMasterService unitMasterService;
	
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void addOrUpdate(PurchaseOrderRequestDto dto, int potype) {

		UserMasterEntity user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + dto.getUserId()));
    	
		Optional<PurchaseOrderEntity> op = poRepository.findByGrnNumberAndUserAndIsDeleteFalse(dto.getGrnNumber(), user);
		
        PurchaseOrderEntity po;
        LocalDate podate = LocalDate.parse(dto.getPodate(), formatter);
        if (dto.getId() != null && dto.getId() != 0 && dto.getId() != -1) {

            po = poRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

            if(op.isPresent()) {
            	PurchaseOrderEntity po1 = op.get();
            	
            	if(po1.getId() != po.getId()) {
            		throw new RuntimeException("Grn Number is exist.");
            	}
            }
            
            List<PurchaseOrderDetailEntity> oldDetails = 
                    detailRepository.findByPoId(po.getId());

            if (!oldDetails.isEmpty()) {
                detailRepository.deleteAll(oldDetails);
            }

        } else {
        	if(op.isPresent()) {
        		throw new RuntimeException("Grn Number is exist.");
        	}

        	po = new PurchaseOrderEntity();

            Long userId = dto.getUserId();
            String poCode = generatePoCode(userId, "PO");

            po.setPocode(poCode);
            po.setPotype(0); // default 0 for new
        }

        // Set PO fields
        po.setSupplier(partyRepository.findById(dto.getSupplierId()).orElse(null));
        po.setVoucher(dto.getVoucher());
        po.setPodate(podate);
        po.setBillno(dto.getBillno());
        po.setInvoicetype(dto.getInvoicetype());
        po.setRemarks(dto.getRemarks());
        po.setSubamount(dto.getSubamount());
        po.setDiscountper(dto.getDiscountper());
        po.setDiscountval(dto.getDiscountval());
        po.setAdjustamount(dto.getAdjustamount());
        po.setFinalamount(dto.getFinalamount());
        po.setPotype(potype);
        po.setPriceUpdateMaster(dto.getPriceUpdateMaster());
        po.setUser(user);
        po.setGrnNumber(dto.getGrnNumber());

        // ── Stock Type ────────────────────────────────────────────────────────
        if (dto.getStockTypeId() != null && dto.getStockTypeId() > 0) {
            po.setStocktype(stockTypeRepository.findById(dto.getStockTypeId())
                    .orElse(null));
        } else {
            po.setStocktype(null);
        }
        // ─────────────────────────────────────────────────────────────────────

        po = poRepository.save(po);

        System.out.println("======================================");
        System.out.println("PO saved - ID     : " + po.getId());
        System.out.println("PO saved - CODE   : " + po.getPocode());
        System.out.println("dto.getDetails()  : " + dto.getDetails());
        System.out.println("details null?     : " + (dto.getDetails() == null));
        System.out.println("details empty?    : " + (dto.getDetails() != null && dto.getDetails().isEmpty()));
        System.out.println("details size      : " + (dto.getDetails() != null ? dto.getDetails().size() : "NULL"));
        System.out.println("======================================");
     // Save New Details
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {

            List<PurchaseOrderDetailEntity> detailList = new ArrayList<>();
            List<RawMaterialMasterEntity> rawMatList = new ArrayList<>();
            
            for (PurchaseOrderDetailRequestDto d : dto.getDetails()) {

                PurchaseOrderDetailEntity detail = new PurchaseOrderDetailEntity();
                detail.setPo(po);
                RawMaterialMasterEntity rm = rawRepository.findById(d.getRawMaterialId()).orElse(null);
                detail.setPo(po);
                detail.setRawMaterial(rm);
                detail.setRawMaterialCat(rm != null ? rm.getRawMaterialCat() : null);
                detail.setUnit(rm.getUnit());
                detail.setHsccode(d.getHsccode());
                detail.setCgst(d.getCgst());
                detail.setSgst(d.getSgst());
                detail.setIgst(d.getIgst());
                detail.setCess(d.getCess());
                detail.setQty(d.getQty());
                detail.setPrice(d.getPrice());
                detail.setOthercharge(d.getOthercharge());
                detail.setTotal(d.getTotal());
                detail.setUserid(dto.getUserId());
                detail.setIsAddInStock(d.getIsAddInStock());
                detailList.add(detail);
                
                if(dto.getPriceUpdateMaster()) {
                	if((d.getPrice() != d.getOldPrice()) && (rm != null)) {
                		rm.setSupplierRate(BigDecimal.valueOf(d.getPrice()));
                		
                		rawMatList.add(rm);
                	}
                };
            }

            detailRepository.saveAll(detailList);
            if(rawMatList != null && !rawMatList.isEmpty()) {
            	rawRepository.saveAll(rawMatList);
            }
            
            detailRepository.flush();

            System.out.println("======================================");
            System.out.println("PO ID         : " + po.getId());
            System.out.println("PO CODE       : " + po.getPocode());
            System.out.println("details saved : " + detailList.size());

            // Check if details are actually in DB
            List<PurchaseOrderDetailEntity> savedDetails = detailRepository.findByPoId(po.getId());
            System.out.println("details in DB : " + savedDetails.size());

            System.out.println("Calling stockLedgerEntryService.savePurchase...");
            try {
                stockLedgerEntryService.savePurchase(po);
                System.out.println("stockLedgerEntryService.savePurchase SUCCESS");
            } catch (Exception e) {
                System.out.println("stockLedgerEntryService.savePurchase FAILED");
                e.printStackTrace();
            }
            System.out.println("======================================");
        }
    }
    
    @Override
    public String generatePoCode(Long userId, String codeType) {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        long count = poRepository.countByUserAndCreatedAtBetween(
                userId,
                startOfDay,
                endOfDay
        );

        long nextNumber = count + 1;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMM");
        String formattedDate = today.format(formatter);

        String type = null;
        if(codeType.equalsIgnoreCase("GRN")) {
        	type = "GRN-";
        }else {
        	type = "PO-";
        }
        return type + userId + "-" + formattedDate + "-" + nextNumber;
    }
    
    @Override
    public List<PurchaseOrderResponseDto> getByUser(Long userId) {
        return poRepository.getByUserDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PurchaseOrderResponseDto> getByUserAndPotype(Long userId, int potype) {
        return poRepository.findByUserIdAndPotypeAndIsDeleteFalse(userId, potype)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderResponseDto> getByPoId(Long poId) {
        return poRepository.findByIdAndIsDeleteFalse(poId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderResponseDto> getByPoType(int potype) {
        return poRepository.findByPotypeAndIsDeleteFalse(potype)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PurchaseOrderResponseDto convertToDto(PurchaseOrderEntity po) {

        PurchaseOrderResponseDto dto = new PurchaseOrderResponseDto();

        dto.setId(po.getId());
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
        dto.setPocode(po.getPocode());
        dto.setPriceUpdateMaster(po.getPriceUpdateMaster());
        dto.setGrnNumber(po.getGrnNumber());
        
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
                detailRepository.findByPoId(po.getId())
                        .stream()
                        .map(d -> {
                            PurchaseOrderDetailResponseDto rd = new PurchaseOrderDetailResponseDto();
                            rd.setId(d.getId());
                            rd.setRawMaterialId(d.getRawMaterial().getId());
                            rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
                            rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
                            rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
                            rd.setIsAddInStock(d.getIsAddInStock());
                            // ── Unit conversion logic ─────────────────────────────
                            String displayUnitName = d.getUnit().getNameEnglish();
                            Long displayUnitId = d.getUnit().getId();
                            Double qty = d.getQty();

                            java.util.Optional<UnitMasterEntity> rmUnitOp =
                                    unitMasterRepository.findByIdAndIsParentUnitFalse(d.getUnit().getId());

                            if (rmUnitOp.isPresent()
                                    && rmUnitOp.get().getParentUnit() != null
                                    && rmUnitOp.get().getEquivalentValue() != null
                                    && rmUnitOp.get().getEquivalentValue() != 0.0) {

                                UnitMasterEntity unitForConversion = rmUnitOp.get();
                                qty = convertUnitQtyToParentQty(unitForConversion, qty);
                                displayUnitName = unitForConversion.getParentUnit().getNameEnglish();
                                displayUnitId   = unitForConversion.getParentUnit().getId();
                            }
                            // ─────────────────────────────────────────────────────

                            rd.setUnitId(displayUnitId);
                            rd.setUnitName(displayUnitName);
                            rd.setQty(qty);
                            rd.setHsccode(d.getHsccode());
                            rd.setCgst(d.getCgst());
                            rd.setSgst(d.getSgst());
                            rd.setIgst(d.getIgst());
                            rd.setCess(d.getCess());
                            rd.setPrice(d.getPrice());
                            rd.setOthercharge(d.getOthercharge());
                            rd.setTotal(d.getTotal());
                            return rd;
                        }).collect(Collectors.toList());

        dto.setDetails(detailDtos);

        return dto;
    }

    @Override
    public void deleteByPoId(Long poId) {
        PurchaseOrderEntity po = poRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException(
                        "Purchase Order not found with ID: " + poId));

        stockLedgerEntryService.reversePurchase(poId); 
        
        po.setIsDelete(true);
        poRepository.save(po);
    }
    
    @Override
    public byte[] generatePdfReport(Long poId, Long userId, Integer isCompanyDetails, Integer isPrice) {
        try {
        	PdfFont basicFont = null;
            List<PurchaseOrderResponseDto> list = getByPoId(poId);
            if (list == null || list.isEmpty()) {
                throw new RuntimeException("Purchase Order not found with ID: " + poId);
            }
            PurchaseOrderResponseDto po = list.get(0);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);
            
            CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId); 
            PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // ── Colors matching the reference image ───────────────────────────
            Color blackColor    = new DeviceRgb(0, 0, 0);
            Color darkBlue    = new DeviceRgb(13,  71,  116);   // header / final amount bg
            Color lightBlueBg = new DeviceRgb(235, 245, 255);   // vendor info band bg
            Color tableHeader = new DeviceRgb(26,  99,  153);   // items table header
            Color altRow      = new DeviceRgb(245, 248, 252);   // alternate row
            Color borderGray  = new DeviceRgb(200, 210, 220);   // subtle border
            Color labelGray   = new DeviceRgb(100, 120, 140);   // small labels
            Color remarksBg   = new DeviceRgb(255, 253, 235);   // remarks yellow tint
            Color remarksBdr  = new DeviceRgb(220, 200, 100);   // remarks border
         
            // ══════════════════════════════════════════════════════════════════
            // SECTION 0 — COMPANY DETAILS
            // ══════════════════════════════════════════════════════════════════
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
            // SECTION 1 — TOP HEADER (Title left, PO code + date right)
            // ══════════════════════════════════════════════════════════════════
            float[] topWidths = {60f, 40f};
            Table topHeader = new Table(UnitValue.createPercentArray(topWidths));
            topHeader.setWidth(UnitValue.createPercentValue(100));

            // Left: "Purchase Receipt" title + printed-on line
            String printedOn = "Printed on: " +
                    java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

            Cell titleCell = new Cell()
                    .add(new Paragraph("Purchase Report")
                            .setFont(boldFont).setFontSize(20)
                            .setFontColor(darkBlue)
                            .setMarginBottom(2))
                    .add(new Paragraph(printedOn)
                            .setFont(regularFont).setFontSize(8)
                            .setFontColor(labelGray))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingBottom(6);
            topHeader.addCell(titleCell);

            // Right: PO code bold + date below
            String poDateStr = po.getPodate() != null
                    ? po.getPodate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";

            Cell poCodeCell = new Cell()
                    .add(new Paragraph(nvl(po.getPocode()))
                            .setFont(boldFont).setFontSize(13)
                            .setFontColor(darkBlue)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setMarginBottom(2))
                    .add(new Paragraph("Date: " + poDateStr)
                            .setFont(regularFont).setFontSize(8)
                            .setFontColor(labelGray)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingBottom(6);
            topHeader.addCell(poCodeCell);

            document.add(topHeader);

            // ── Divider line ──────────────────────────────────────────────────
            Table divider = new Table(UnitValue.createPercentArray(new float[]{100f}));
            divider.setWidth(UnitValue.createPercentValue(100));
            divider.addCell(new Cell()
                    .setHeight(2f)
                    .setBackgroundColor(darkBlue)
                    .setBorder(Border.NO_BORDER));
            document.add(divider);
            document.add(new Paragraph("").setMarginBottom(8));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 2 — VENDOR INFO BAND
            // ══════════════════════════════════════════════════════════════════
            float[] vendorWidths = {22f, 18f, 18f, 14f, 12f, 16f};
            Table vendorTable = new Table(UnitValue.createPercentArray(vendorWidths));
            vendorTable.setWidth(UnitValue.createPercentValue(100));

            // helper: add a vendor info cell (label on top, value below)
            addVendorCell(vendorTable, "VENDOR",       nvl(po.getSupplierName()), lightBlueBg, labelGray, boldFont, regularFont, true,  false);
            addVendorCell(vendorTable, "BILL NO.",      nvl(po.getBillno()),       lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addVendorCell(vendorTable, "GRN NO.",      nvl(po.getGrnNumber()),       lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addVendorCell(vendorTable, "INVOICE TYPE",  nvl(po.getInvoicetype()),  lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addVendorCell(vendorTable, "DATE",  poDateStr,                 lightBlueBg, labelGray, boldFont, regularFont, false, true);
            addVendorCell(vendorTable, "STOCK TYPE", nvl(po.getStockTypeName()),   lightBlueBg, labelGray, boldFont, regularFont, false, true);

            document.add(vendorTable);
            document.add(new Paragraph("").setMarginBottom(12));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 3 — ITEMS TABLE
            // ══════════════════════════════════════════════════════════════════
         // ── Define columns based on isPrice flag ─────────────────────────────────
            float[] colWidths = isPrice == 1
                    ? new float[]{3f, 17f, 13f, 4f, 7f, 8f, 8f, 8f, 8f, 8f, 8f, 8f}
                    : new float[]{8f, 40f, 30f, 12f, 10f};

            Table itemTable = new Table(UnitValue.createPercentArray(colWidths));
            itemTable.setWidth(UnitValue.createPercentValue(100));

            String[] headers = isPrice == 1
                    ? new String[]{"#", "Item Name", "Category", "Unit", "Qty",
                                    "Price", "O. Charge", "CGST%", "SGST%", "IGST%", "CESS%", "Total"}
                    : new String[]{"#", "Item Name", "Category", "Qty", "Unit"};

            TextAlignment[] aligns = isPrice == 1
                    ? new TextAlignment[]{
                        TextAlignment.CENTER, TextAlignment.LEFT,  TextAlignment.LEFT,
                        TextAlignment.CENTER, TextAlignment.RIGHT, TextAlignment.RIGHT,
                        TextAlignment.RIGHT,  TextAlignment.RIGHT, TextAlignment.RIGHT,
                        TextAlignment.RIGHT,  TextAlignment.RIGHT,  TextAlignment.RIGHT}
                    : new TextAlignment[]{
                        TextAlignment.CENTER, TextAlignment.LEFT, TextAlignment.LEFT,
                        TextAlignment.CENTER, TextAlignment.CENTER};

            for (int i = 0; i < headers.length; i++) {
                itemTable.addHeaderCell(new Cell()
                        .add(new Paragraph(headers[i])
                                .setFont(boldFont).setFontSize(9)
                                .setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(tableHeader)
                        .setTextAlignment(aligns[i])
                        .setPaddingTop(6).setPaddingBottom(6)
                        .setBorder(Border.NO_BORDER));
            }

            boolean alternate = false;
            int srNo = 1;

            for (PurchaseOrderDetailResponseDto d : po.getDetails()) {
                Color bg = alternate ? altRow : ColorConstants.WHITE;
                alternate = !alternate;

                // ── Always printed columns ────────────────────────────────────────
                itemTable.addCell(iCell(String.valueOf(srNo++),         bg, regularFont, TextAlignment.CENTER));
                itemTable.addCell(iCell(nvl(d.getRawMaterialName()),    bg, regularFont, TextAlignment.LEFT));
                itemTable.addCell(iCell(nvl(d.getRawMaterialCatName()), bg, regularFont, TextAlignment.LEFT));
                itemTable.addCell(iCell(fmt1(d.getQty()),               bg, regularFont, TextAlignment.CENTER));
                itemTable.addCell(iCell(nvl(d.getUnitName()),           bg, regularFont, TextAlignment.CENTER));
                

                // ── Price columns — only when isPrice == 1 ────────────────────────
                if (isPrice == 1) {
                    itemTable.addCell(iCell(fmt1(d.getPrice()),              bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(iCell(fmt1(d.getOthercharge()),        bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(iCell(fmt1(d.getCgst()) + "%",        bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(iCell(fmt1(d.getSgst()) + "%",        bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(iCell(fmt1(d.getIgst()) + "%",        bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(iCell(fmt1(d.getCess()) + "%",        bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(iCell(fmt1(d.getTotal()),              bg, regularFont, TextAlignment.RIGHT));
                }
            }

            document.add(itemTable);
            document.add(new Paragraph("").setMarginBottom(8));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 4 — SUMMARY (right-aligned box, ~35% width)
            // ══════════════════════════════════════════════════════════════════
            float[] outerW = {65f, 35f};
            Table outerSummary = new Table(UnitValue.createPercentArray(outerW));
            outerSummary.setWidth(UnitValue.createPercentValue(100));
            outerSummary.addCell(new Cell().setBorder(Border.NO_BORDER)); // empty left
            
         //  Single loop for all summary calculations
            float totalCgstAmt        = 0f;
            float totalSgstAmt        = 0f;
            float totalIgstAmt        = 0f;
            float calculatedSubAmount = 0f;

            for (PurchaseOrderDetailResponseDto d : po.getDetails()) {
                float base = (float) d.getQty() * d.getPrice();
                calculatedSubAmount += base + d.getOthercharge();
                totalCgstAmt        += base * (d.getCgst() / 100f);
                totalSgstAmt        += base * (d.getSgst() / 100f);
                totalIgstAmt        += base * (d.getIgst() / 100f);
            }

            float calculatedFinalAmount = calculatedSubAmount
                    + totalCgstAmt
                    + totalSgstAmt
                    + totalIgstAmt
                    - po.getDiscountval()
                    + po.getAdjustamount();
           

            // Build the inner summary box
            float[] summaryW = {60f, 40f};
            Table summaryBox = new Table(UnitValue.createPercentArray(summaryW));
            summaryBox.setWidth(UnitValue.createPercentValue(100));
            summaryBox.setKeepTogether(true);

            if (isPrice == 1) {
            addSummaryLine(summaryBox, "Base Amount",
                    fmt1(calculatedSubAmount),
                    ColorConstants.WHITE, boldFont, regularFont, borderGray, false);     

            addSummaryLine(summaryBox, "Adjust Amount",
                    fmt1(po.getAdjustamount()),
                    ColorConstants.WHITE, regularFont, regularFont, borderGray, false);
            
            addSummaryLine(summaryBox, "Total CGST",
                    fmt1(totalCgstAmt),
                    ColorConstants.WHITE, regularFont, regularFont, borderGray, false);

            addSummaryLine(summaryBox, "Total SGST",
                    fmt1(totalSgstAmt),
                    ColorConstants.WHITE, regularFont, regularFont, borderGray, false);

            addSummaryLine(summaryBox, "Total IGST",
                    fmt1(totalIgstAmt),
                    ColorConstants.WHITE, regularFont, regularFont, borderGray, false);
            
            String discLabel = "Discount (" + fmt1(po.getDiscountper()) + "%)";
            addSummaryLine(summaryBox, discLabel,
                    fmt1(po.getDiscountval()),
                    ColorConstants.WHITE, regularFont, regularFont, borderGray, false);

            // Final Amount — dark blue row
            addSummaryLine(summaryBox, "Final Amount",
                    fmt1(calculatedFinalAmount),
                    darkBlue, boldFont, boldFont, darkBlue, true);

            outerSummary.addCell(new Cell().add(summaryBox).setBorder(Border.NO_BORDER).setPadding(0));
            document.add(outerSummary);
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 5 — REMARKS (only if present)
            // ══════════════════════════════════════════════════════════════════
            if (po.getRemarks() != null && !po.getRemarks().trim().isEmpty()) {
                document.add(new Paragraph("").setMarginBottom(10));

                Table remarksTable = new Table(UnitValue.createPercentArray(new float[]{100f}));
                remarksTable.setWidth(UnitValue.createPercentValue(100));

                Cell remarksCell = new Cell()
                        .add(new Paragraph("REMARKS")
                                .setFont(boldFont).setFontSize(8)
                                .setFontColor(new DeviceRgb(160, 120, 0))
                                .setMarginBottom(3))
                        .add(new Paragraph(po.getRemarks())
                                .setFont(regularFont).setFontSize(9)
                                .setFontColor(ColorConstants.BLACK))
                        .setBackgroundColor(remarksBg)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(remarksBdr, 1f))
                        .setPadding(10);
                remarksTable.addCell(remarksCell);
                document.add(remarksTable);
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 6 — FOOTER
            // ══════════════════════════════════════════════════════════════════
            document.add(new Paragraph("").setMarginBottom(16));

            Table footer = new Table(UnitValue.createPercentArray(new float[]{50f, 50f}));
            footer.setWidth(UnitValue.createPercentValue(100));
            footer.addCell(new Cell()
                    .add(new Paragraph(nvl(po.getPocode()) + "  |  " + nvl(po.getInvoicetype()))
                            .setFont(regularFont).setFontSize(8)
                            .setFontColor(labelGray))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f))
                    .setBorderLeft(Border.NO_BORDER)
                    .setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setPaddingTop(6));
            footer.addCell(new Cell()
                    .add(new Paragraph("Total Items: " + po.getDetails().size())
                            .setFont(regularFont).setFontSize(8)
                            .setFontColor(labelGray)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f))
                    .setBorderLeft(Border.NO_BORDER)
                    .setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER)
                    .setPaddingTop(6));
            document.add(footer);
            
            /* ================= FOOTER ================= */
			int totalPages = pdf.getNumberOfPages();

			PdfPage page = pdf.getPage(totalPages);
			Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

			canvas.showTextAligned("SIGN __________________________", page.getPageSize().getLeft() + 36, 22,
					TextAlignment.LEFT);

			canvas.showTextAligned(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")),
					page.getPageSize().getRight() - 36, // Right margin
					22, TextAlignment.RIGHT);
			
			canvas.close();

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

 // ── Vendor info band cell (label small/gray on top, value bold below) ─────
    private void addVendorCell(Table table, String label, String value,
            Color bg, Color labelColor,
            PdfFont boldFont, PdfFont regularFont,
            boolean isFirst, boolean isLast) {

        com.itextpdf.layout.borders.Border border =
                new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(180, 200, 220), 0.5f);

        Cell cell = new Cell()
                .add(new Paragraph(label)
                        .setFont(regularFont).setFontSize(7)
                        .setFontColor(labelColor).setMarginBottom(2))
                .add(new Paragraph(value)
                        .setFont(boldFont).setFontSize(10)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(bg)
                .setPadding(10)
                .setBorder(border)
                .setBorderLeft( isFirst ? border : Border.NO_BORDER)
                .setBorderRight(isLast  ? border : border);
        table.addCell(cell);
    }

    // ── Summary row ───────────────────────────────────────────────────────────
    private void addSummaryLine(Table table,
            String label, String value,
            Color bg, PdfFont labelFont, PdfFont valueFont,
            Color borderColor, boolean isTotal) {

        com.itextpdf.layout.borders.Border bdr =
                new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f);

        Color fontColor = isTotal ? ColorConstants.WHITE : ColorConstants.BLACK;

        table.addCell(new Cell()
                .add(new Paragraph(label).setFont(labelFont).setFontSize(9).setFontColor(fontColor))
                .setBackgroundColor(bg)
                .setBorder(bdr)
                .setPaddingTop(5).setPaddingBottom(5).setPaddingLeft(8));

        table.addCell(new Cell()
                .add(new Paragraph(value).setFont(valueFont).setFontSize(9).setFontColor(fontColor))
                .setBackgroundColor(bg)
                .setBorder(bdr)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(5).setPaddingBottom(5).setPaddingRight(8));
    }

    // ── Item table cell ───────────────────────────────────────────────────────
    private Cell iCell(String text, Color bg, PdfFont font, TextAlignment align) {
        Color borderColor = new DeviceRgb(220, 228, 235);
        return new Cell()
                .add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setTextAlignment(align)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.3f))
                .setPaddingTop(5).setPaddingBottom(5);
    }

    // ── Formatters ────────────────────────────────────────────────────────────
    private String fmtD(Double val) {
        return val != null ? String.format("%.2f", val) : "0.00";
    }

    private String fmtB(BigDecimal val) {
        return val != null ? val.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
    }

    private String fmtB(Float val) {
        return val != null ? String.format("%.2f", val) : "0.00";
    }

    private String fmtB(float val) {
        return String.format("%.2f", val);
    }

 // decimal place formatter (replaces fmtD and fmtB everywhere)
    private String fmt1(double val) {
        return String.format("%.1f", val);
    }

    private String fmt1(float val) {
        return String.format("%.1f", val);
    }

    private String fmt1(Double val) {
        return val != null ? String.format("%.1f", val) : "0.0";
    }

    private String fmt1(BigDecimal val) {
        return val != null ? String.format("%.1f", val.doubleValue()) : "0.0";
    }

    private String fmt1(Float val) {
        return val != null ? String.format("%.1f", val) : "0.0";
    }
    
    private String nvl(String val) {
        return val != null && !val.isEmpty() ? val : "-";
    }
    
    private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {
        if (qty == null || qty == 0.0) return 0.0;
        return BigDecimal.valueOf(qty)
                .divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
                .doubleValue();
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
    public byte[] generateExcelReport(Long poId, Long userId) {

        try {

            List<PurchaseOrderResponseDto> list = getByPoId(poId);

            if (list == null || list.isEmpty()) {
                throw new RuntimeException("Purchase Order not found");
            }

            PurchaseOrderResponseDto po = list.get(0);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Purchase Order");

            int rowNum = 0;

            // ================= STYLES =================

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
        //    titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font whiteFont = workbook.createFont();
            whiteFont.setBold(true);
            whiteFont.setColor(IndexedColors.WHITE.getIndex());

            headerStyle.setFont(whiteFont);

            // ================= TITLE =================

            Row titleRow = sheet.createRow(rowNum++);

            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Purchase Order Report");
            titleCell.setCellStyle(titleStyle);

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

            rowNum++;

            // ================= PO DETAILS =================

            Row infoRow1 = sheet.createRow(rowNum++);
    //        infoRow1.createCell(0).setCellValue("PO Code");
            infoRow1.createCell(0).setCellValue(po.getPocode());

            infoRow1.createCell(3).setCellValue("Vendor : ");
            infoRow1.createCell(4).setCellValue(po.getSupplierName());

            Row infoRow2 = sheet.createRow(rowNum++);
        //    infoRow2.createCell(0).setCellValue("PO Date");
            infoRow2.createCell(0).setCellValue(
                    po.getPodate() != null ? po.getPodate().toString() : "-"
            );

            infoRow2.createCell(3).setCellValue("Invoice Type : ");
            infoRow2.createCell(4).setCellValue(po.getInvoicetype());

            rowNum++;

            // ================= TABLE HEADER =================

            String[] headers = {
                    "#", "Item Name", "Category", "Unit",
                    "Qty", "Price", "Other Charge",
                    "CGST%", "SGST%", "IGST%", "Total"
            };

            Row headerRow = sheet.createRow(rowNum++);

            for (int i = 0; i < headers.length; i++) {

            	org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ================= ITEM DATA =================

            int sr = 1;

            double subAmount = 0;
            double totalCgst = 0;
            double totalSgst = 0;
            double totalIgst = 0;

            for (PurchaseOrderDetailResponseDto d : po.getDetails()) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(sr++);
                row.createCell(1).setCellValue(d.getRawMaterialName());
                row.createCell(2).setCellValue(d.getRawMaterialCatName());
                row.createCell(3).setCellValue(d.getUnitName());
                row.createCell(4).setCellValue(d.getQty());
                row.createCell(5).setCellValue(d.getPrice());
                row.createCell(6).setCellValue(d.getOthercharge());
                row.createCell(7).setCellValue(d.getCgst());
                row.createCell(8).setCellValue(d.getSgst());
                row.createCell(9).setCellValue(d.getIgst());
                row.createCell(10).setCellValue(d.getTotal());

                double base = d.getQty() * d.getPrice();

                subAmount += base + d.getOthercharge();
                totalCgst += base * (d.getCgst() / 100);
                totalSgst += base * (d.getSgst() / 100);
                totalIgst += base * (d.getIgst() / 100);
            }

            // ================= SUMMARY =================

            rowNum++;

            Row subRow = sheet.createRow(rowNum++);
            subRow.createCell(8).setCellValue("Base Amount");
            subRow.createCell(9).setCellValue(subAmount);

            Row cgstRow = sheet.createRow(rowNum++);
            cgstRow.createCell(8).setCellValue("Total CGST");
            cgstRow.createCell(9).setCellValue(totalCgst);

            Row sgstRow = sheet.createRow(rowNum++);
            sgstRow.createCell(8).setCellValue("Total SGST");
            sgstRow.createCell(9).setCellValue(totalSgst);

            Row igstRow = sheet.createRow(rowNum++);
            igstRow.createCell(8).setCellValue("Total IGST");
            igstRow.createCell(9).setCellValue(totalIgst);

            Row discountRow = sheet.createRow(rowNum++);
            discountRow.createCell(8).setCellValue("Discount");
            discountRow.createCell(9).setCellValue(po.getDiscountval());

            double finalAmount =
                    subAmount
                    + totalCgst
                    + totalSgst
                    + totalIgst
                    - po.getDiscountval()
                    + po.getAdjustamount();

            Row finalRow = sheet.createRow(rowNum++);
            finalRow.createCell(8).setCellValue("Final Amount");
            finalRow.createCell(9).setCellValue(finalAmount);

            // ================= REMARKS =================

//            if (po.getRemarks() != null) {
//
//                rowNum++;
//
//                Row remarksRow = sheet.createRow(rowNum++);
//                remarksRow.createCell(0).setCellValue("Remarks");
//
//                Row remarksValueRow = sheet.createRow(rowNum++);
//                remarksValueRow.createCell(0).setCellValue(po.getRemarks());
//            }

            // ================= AUTO SIZE =================

            for (int i = 0; i < 11; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
	@Override
	public PurchaseOrderRawMaterialDetailsResponseDto getRawMaterialPrice(Long supplierId, Long rawMaterialId,
			Long userId) {
		userRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		partyRepository.findByIdAndIsDeleteFalse(supplierId)
				.orElseThrow(() -> new RuntimeException("Party not found with id : " + supplierId));

		RawMaterialMasterEntity rawMaterial = rawRepository.findByIdAndIsDeleteFalse(rawMaterialId);

		if (rawMaterial == null) {
			throw new RuntimeException("Raw material not found with id : " + rawMaterialId);
		}

		BigDecimal price = poRepository.getLatestRawMaterialPriceSupplierWise(userId, supplierId, rawMaterialId);

		PurchaseOrderRawMaterialDetailsResponseDto response = new PurchaseOrderRawMaterialDetailsResponseDto();
		response.setRawMaterialId(rawMaterialId);
		response.setRawMaterialName(rawMaterial.getNameEnglish());
		response.setPrice(
				price != null && price.compareTo(BigDecimal.ZERO) != 0 ? price : rawMaterial.getSupplierRate());
		response.setUnitId(rawMaterial.getUnit().getId());
		response.setUnitName(rawMaterial.getUnit().getNameEnglish());
		response.setUnitSymbole(rawMaterial.getUnit().getSymbolEnglish());

		UnitHierarchyDto unitHierarchyDto = unitMasterService
				.getParentUnitsWithChildren(rawMaterial.getUnit() == null ? null : rawMaterial.getUnit().getId());

		response.setUnitHierarchy(unitHierarchyDto);
		
		return response;
	}
}