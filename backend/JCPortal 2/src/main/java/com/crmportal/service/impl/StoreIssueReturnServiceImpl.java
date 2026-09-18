package com.crmportal.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PurchaseOrderStoreEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.StoreIssueReturnDetailEntity;
import com.crmportal.entity.StoreIssueReturnEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PurchaseOrderStoreDetailRepository;
import com.crmportal.repository.PurchaseOrderStoreRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.StockTypeRepository;
import com.crmportal.repository.StoreIssueReturnDetailRepository;
import com.crmportal.repository.StoreIssueReturnRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.StoreIssueReturnDetailRequestDto;
import com.crmportal.request.dto.StoreIssueReturnRequestDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.PocodeWithStatusResponseDto;
import com.crmportal.response.dto.PurchaseOrderStoreDetailResponseDto;
import com.crmportal.response.dto.PurchaseOrderStoreResponseDto;
import com.crmportal.response.dto.StoreIssueReturnDetailResponseDto;
import com.crmportal.response.dto.StoreIssueReturnResponseDto;
import com.crmportal.service.StockLedgerEntryService;
import com.crmportal.service.StoreIssueReturnService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
@Transactional
public class StoreIssueReturnServiceImpl implements StoreIssueReturnService {

    @Autowired
    private StoreIssueReturnRepository sirRepository;

    @Autowired
    private StoreIssueReturnDetailRepository sirDetailRepository;

    @Autowired
    private PurchaseOrderStoreRepository storePoRepository;

    @Autowired
    private PurchaseOrderStoreDetailRepository storePoDetailRepository;

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
	EventFunctionMenuAllocationRepository menuAllocationRepository;

    @Autowired
    private MenuPreparationServiceImpl menuPreparationServiceImpl;

    @Autowired
    EventMasterRepository eventMasterRepository;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── Add or Update ────────────────────────────────────────────────────────

    @Override
    public StoreIssueReturnResponseDto addOrUpdate(StoreIssueReturnRequestDto request) {

    	if (request.getEventId() != null && request.getEventId() > 0) {
    	    eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
    	        .orElseThrow(() ->
    	            new RuntimeException("Event not found with id : " + request.getEventId()));
    	}

        StoreIssueReturnEntity entity;

        if (request.getId() == null || request.getId() == 0 || request.getId() == -1) {
            entity = new StoreIssueReturnEntity();
            Long userId = request.getUserId();
            entity.setSircode(generateSirCode(userId));
        } else {
            entity = sirRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Store Issue Return not found"));
            sirDetailRepository.deleteByStoreIssueReturnId(entity.getId());
        }

        // Link original store issue
        PurchaseOrderStoreEntity storeIssue = storePoRepository.findById(request.getStoreIssueId())
                .orElseThrow(() -> new RuntimeException("Store Issue not found"));

        entity.setStoreIssue(storeIssue);
        entity.setParty(request.getPartyId() == null ? null : partyRepository.findById(request.getPartyId()).orElse(null));
        entity.setVoucher(request.getVoucher());
        entity.setReturndate(LocalDate.parse(request.getReturndate(), formatter));
        entity.setInvoicetype(request.getInvoicetype());

        if(request.getEventId() != null && request.getPartyId() > 0) {
			entity.setEventId(request.getEventId());
		}else {
			entity.setEventId(null);
		}
        
        if (request.getStockTypeId() != null) {
            entity.setStocktype(stockTypeRepository.findById(request.getStockTypeId()).orElse(null));
        }
        
        if (request.getKitchenTypeId() != null) {
            entity.setKitchentype(stockTypeRepository.findById(request.getKitchenTypeId()).orElse(null));
        }

        entity.setRemarks(request.getRemarks());
        entity.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));

        entity = sirRepository.save(entity);

        // Save details
        if (request.getDetails() != null && !request.getDetails().isEmpty()) {

            List<StoreIssueReturnDetailEntity> detailList = new ArrayList<>();

            for (StoreIssueReturnDetailRequestDto d : request.getDetails()) {

                StoreIssueReturnDetailEntity detail = new StoreIssueReturnDetailEntity();
                RawMaterialMasterEntity rm = rawMaterialRepository.findById(d.getRawMaterialId()).orElse(null);

                detail.setStoreIssueReturn(entity);
                detail.setRawMaterial(rm);
                detail.setRawMaterialCat(rm != null ? rm.getRawMaterialCat() : null);
                detail.setUnit(rm != null ? rm.getUnit() : null);
                System.out.println("Qty:-"+d.getQty());
                detail.setQty(d.getQty());
                detail.setUserid(request.getUserId());

                detailList.add(detail);
            }

            sirDetailRepository.saveAll(detailList);
            sirDetailRepository.flush();
            stockLedgerEntryService.saveStoreIssueReturn(entity);
        }

        return convertToDto(entity);
    }

    // ─── Auto-fill by pocode ──────────────────────────────────────────────────

    @Override
    public PurchaseOrderStoreResponseDto getStoreIssueDetailsByPocode(String pocode, Long userId) {

        PurchaseOrderStoreEntity entity = storePoRepository.findByPocodeAndUserIdAndIsDeleteFalse(pocode, userId)
                .orElseThrow(() -> new RuntimeException("No Store Issue found with code: " + pocode));

        return convertStorePoToDto(entity);
    }

    // ─── All pocodes for dropdown ─────────────────────────────────────────────

    @Override
    public List<PocodeWithStatusResponseDto> getAllStorePocodes(Long userId) {

        List<PocodeWithStatusResponseDto> pocodes = storePoRepository.findAllPocodes(userId);

        return pocodes.stream().map(p -> {

            boolean isReturned = sirRepository
                    .existsByStoreIssueIdAndIsDeleteFalse(p.getId());

            p.setReturned(isReturned);

            return p;

        }).collect(Collectors.toList());
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    @Override
    public List<StoreIssueReturnResponseDto> getByUser(Long userId) {
        return sirRepository.getByUserDesc(userId)
                .stream()
                .map(entity -> {
                    StoreIssueReturnResponseDto dto = convertToDto(entity);
                    dto.setDetails(null); // ← remove details
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public StoreIssueReturnResponseDto getBySirId(Long sirId) {
        StoreIssueReturnEntity entity = sirRepository.findById(sirId)
                .orElseThrow(() -> new RuntimeException("Store Issue Return not found with ID: " + sirId));
        return convertToDto(entity);
    }

    @Override
    public List<StoreIssueReturnResponseDto> getByStoreIssueId(Long storeIssueId, Long userId) {
        return sirRepository.findByStoreIssueIdAndUserIdAndIsDeleteFalse(storeIssueId, userId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteBySirId(Long sirId) {
        StoreIssueReturnEntity entity = sirRepository.findById(sirId)
                .orElseThrow(() -> new RuntimeException(
                        "Store Issue Return not found with ID: " + sirId));

        stockLedgerEntryService.reverseStoreIssueReturn(sirId); 

        entity.setIsDelete(true);
        sirRepository.save(entity);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String generateSirCode(Long userId) {

        LocalDate today = LocalDate.now();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");

        String prefix = "SIR-" + userId + "-" + today.format(dtf) + "-";

        String lastCode = sirRepository.findMaxSircodeByPrefix(userId, prefix);

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

    private StoreIssueReturnResponseDto convertToDto(StoreIssueReturnEntity entity) {

        StoreIssueReturnResponseDto dto = new StoreIssueReturnResponseDto();

        dto.setId(entity.getId());
        dto.setSircode(entity.getSircode());
        dto.setStoreIssueId(entity.getStoreIssue().getId());
        dto.setStoreIssuePocode(entity.getStoreIssue().getPocode());
        dto.setPartyId(entity.getParty() != null ? entity.getParty().getId() : null);
        dto.setPartyName(entity.getParty() != null ? entity.getParty().getNameEnglish() : "");
        dto.setVoucher(entity.getVoucher());
        dto.setReturndate(entity.getReturndate().format(formatter));
        dto.setInvoicetype(entity.getInvoicetype());

        if(entity.getEventId() != null) {
			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(entity.getEventId())
					.orElseThrow(() -> new RuntimeException("Event not found with id : " + entity.getEventId()));
			dto.setEventId(entity.getEventId());
			dto.setEventName(event.getEventType().getNameEnglish());
			dto.setEventDate(event.getEventStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));;
		}else {
			dto.setEventId(null);
			dto.setEventName(null);
			dto.setEventDate(null);
		}
        
        if (entity.getStocktype() != null) {
            dto.setStockTypeId(entity.getStocktype().getId());
            dto.setStockTypeName(entity.getStocktype().getNameEnglish());
        }
        
        if (entity.getKitchentype() != null) {
            dto.setKitchenTypeId(entity.getKitchentype().getId());
            dto.setKitchenTypeName(entity.getKitchentype().getNameEnglish());
        }

        dto.setRemarks(entity.getRemarks());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setUserId(entity.getUser().getId());

        Long storeIssueId = entity.getStoreIssue().getId();

        List<StoreIssueReturnDetailResponseDto> detailDtos =
                sirDetailRepository.findByStoreIssueReturnId(entity.getId())
                        .stream()
                        .map(d -> {
                            StoreIssueReturnDetailResponseDto rd = new StoreIssueReturnDetailResponseDto();
                            rd.setId(d.getId());

                            if (d.getRawMaterial() != null) {
                                rd.setRawMaterialId(d.getRawMaterial().getId());
                                rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
                            }
                            if (d.getRawMaterialCat() != null) {
                                rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
                                rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
                            }
                            if (d.getUnit() != null) {
                                rd.setUnitId(d.getUnit().getId());
                                rd.setUnitName(d.getUnit().getNameEnglish());
                            }

                         // qty saved in this return record
                            double thisReturnQty = d.getQty();

                            // original qty from original store issue
                            double originalQty = storePoDetailRepository.findByPoId(storeIssueId)
                                    .stream()
                                    .filter(od -> od.getRawMaterial().getId()
                                            .equals(d.getRawMaterial().getId()))
                                    .mapToDouble(od -> od.getQty())
                                    .findFirst()
                                    .orElse(0.0f);

                            // total returned across ALL returns (including this one)
                            double totalReturnedQty = sirDetailRepository
                                    .sumReturnedQtyByStoreIssueAndRawMaterial(
                                            storeIssueId,
                                            d.getRawMaterial().getId()
                                    );

                            // same formula as getstoreissuedetails
                            double remainingQty = Math.max(originalQty - totalReturnedQty, 0.0f);

                            rd.setQty(originalQty);
                            rd.setReturnedQty(totalReturnedQty);
                            rd.setRemainingQty(remainingQty);

                            return rd;
                        }).collect(Collectors.toList());

        dto.setDetails(detailDtos);
        return dto;
    }

    // Reuse Store PO → DTO (for auto-fill response)
    private PurchaseOrderStoreResponseDto convertStorePoToDto(PurchaseOrderStoreEntity entity) {

        PurchaseOrderStoreResponseDto dto = new PurchaseOrderStoreResponseDto();

        dto.setId(entity.getId());
        dto.setPocode(entity.getPocode());
        dto.setPartyId(entity.getParty() != null ? entity.getParty().getId() : null);
        dto.setPartyName(entity.getParty() != null ? entity.getParty().getNameEnglish() : "");
        dto.setVoucher(entity.getVoucher());
        dto.setPodate(entity.getPodate().format(formatter));
        dto.setInvoicetype(entity.getInvoicetype());

        if(entity.getEventId() != null) {
			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(entity.getEventId())
					.orElseThrow(() -> new RuntimeException("Event not found with id : " + entity.getEventId()));
			dto.setEventId(entity.getEventId());
			dto.setEventName(event.getEventType().getNameEnglish());
			dto.setEventDate(event.getEventStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));;
		}else {
			dto.setEventId(null);
			dto.setEventDate(null);
		}
        
        if (entity.getStocktype() != null) {
            dto.setStockTypeId(entity.getStocktype().getId());
            dto.setStockTypeName(entity.getStocktype().getNameEnglish());
        }
        
        if (entity.getKitchentype() != null) {
            dto.setKitchenTypeId(entity.getKitchentype().getId());
            dto.setKitchenTypeName(entity.getKitchentype().getNameEnglish());
        }

        dto.setRemarks(entity.getRemarks());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setUserId(entity.getUser().getId());

        List<PurchaseOrderStoreDetailResponseDto> detailDtos =
                storePoDetailRepository.findByPoId(entity.getId())
                        .stream()
                        .map(d -> {
                            PurchaseOrderStoreDetailResponseDto rd = new PurchaseOrderStoreDetailResponseDto();
                            rd.setId(d.getId());
                            {
                                rd.setRawMaterialId(d.getRawMaterial().getId());
                                rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
                            }
                            if (d.getRawMaterialCat() != null) {
                                rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
                                rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
                            }
                            if (d.getUnit() != null) {
                                rd.setUnitId(d.getUnit().getId());
                                rd.setUnitName(d.getUnit().getNameEnglish());
                            }

                            double originalQty = d.getQty();

                            // Calculate already returned qty for this item
                            double returnedQty = sirDetailRepository
                                    .sumReturnedQtyByStoreIssueAndRawMaterial(
                                            entity.getId(),
                                            d.getRawMaterial().getId()
                                    );

                            double remainingQty = originalQty - returnedQty;

                            rd.setQty(originalQty);
                            rd.setReturnedQty(returnedQty);
                            rd.setRemainingQty(remainingQty);

                            return rd;
                        })
                        // Only return items that still have remaining qty
                        .filter(rd -> rd.getRemainingQty() > 0)
                        .collect(Collectors.toList());

        dto.setDetails(detailDtos);
        return dto;
    }
    
    @Override
    public byte[] generatePdfReport(Long sirId, Long userId, Integer isCompanyDetails) {
        try {
            StoreIssueReturnResponseDto sir = getBySirId(sirId);
            if (sir == null) {
                throw new RuntimeException("Store Issue Return not found with ID: " + sirId);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer  = new PdfWriter(baos);
            PdfDocument pdf   = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

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

            topTable.addCell(new Cell()
                    .add(new Paragraph("Store Issue Return")
                            .setFont(boldFont).setFontSize(22)
                            .setFontColor(darkBlue).setMarginBottom(3))
                    .add(new Paragraph(printedOn)
                            .setFont(regularFont).setFontSize(8)
                            .setFontColor(labelGray))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(6));

            topTable.addCell(new Cell()
                    .add(new Paragraph(nvlS(sir.getSircode()))
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
            // (Party | Stock Type | Invoice Type | Original SI | Return Date)
            // ══════════════════════════════════════════════════════════════════
            float[] bandW = {25f, 25f, 25f, 25f};
            Table bandTable = new Table(UnitValue.createPercentArray(bandW));
            bandTable.setWidth(UnitValue.createPercentValue(100));

            addSirBandCell(bandTable, "PARTY NAME",     nvlS(sir.getPartyName()),          lightBlueBg, labelGray, boldFont, regularFont, borderGray);
            addSirBandCell(bandTable, "STOCK TYPE",     nvlS(sir.getStockTypeName()),       lightBlueBg, labelGray, boldFont, regularFont, borderGray);
            addSirBandCell(bandTable, "STORE ISSUE ID",    nvlS(sir.getStoreIssuePocode()),    lightBlueBg, labelGray, boldFont, regularFont, borderGray);
            addSirBandCell(bandTable, "RETURN DATE",    nvlS(sir.getReturndate()),          lightBlueBg, labelGray, boldFont, regularFont, borderGray);

            document.add(bandTable);
            document.add(new Paragraph("").setMarginBottom(12));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 3 — ITEMS TABLE
            // # | Item Name | Category | Unit | Orig Qty | Return Qty | Remaining
            // ══════════════════════════════════════════════════════════════════
            float[] colW = {4f, 24f, 18f, 10f, 14f, 14f, 16f};

            Table itemTable = new Table(UnitValue.createPercentArray(colW));
            itemTable.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {
                "#", "Item Name", "Category", "Unit",
                "Issue Qty", "Return Qty", "Remaining Qty"
            };

            TextAlignment[] aligns = {
                TextAlignment.CENTER,
                TextAlignment.LEFT,
                TextAlignment.LEFT,
                TextAlignment.CENTER,
                TextAlignment.RIGHT,
                TextAlignment.RIGHT,
                TextAlignment.RIGHT
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

            // ── Totals ────────────────────────────────────────────────────────
            double totalOrigQty = 0;
            double totalReturnedQty = 0;
            double totalRemainingQty = 0;

            boolean alternate = false;
            int srNo = 1;

            for (StoreIssueReturnDetailResponseDto d : sir.getDetails()) {

                Color bg = alternate ? altRow : ColorConstants.WHITE;
                alternate = !alternate;

                double origQty = d.getQty();
                double returnedQty = d.getReturnedQty();

                // Remaining Qty
                double remainingQty = origQty - returnedQty;

                totalOrigQty += origQty;
                totalReturnedQty += returnedQty;
                totalRemainingQty += remainingQty;

                itemTable.addCell(sirCell(String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER));
                itemTable.addCell(sirCell(nvlS(d.getRawMaterialName()), bg, regularFont, TextAlignment.LEFT));
                itemTable.addCell(sirCell(nvlS(d.getRawMaterialCatName()), bg, regularFont, TextAlignment.LEFT));
                itemTable.addCell(sirCell(nvlS(d.getUnitName()), bg, regularFont, TextAlignment.CENTER));
                itemTable.addCell(sirCell(fmtSirQty(origQty), bg, regularFont, TextAlignment.RIGHT));
                itemTable.addCell(sirCell(fmtSirQty(returnedQty), bg, boldFont, TextAlignment.RIGHT));

                // Remaining Qty Column
                itemTable.addCell(sirCell(fmtSirQty(remainingQty), bg, regularFont, TextAlignment.RIGHT));
            }

            document.add(itemTable);
            document.add(new Paragraph("").setMarginBottom(12));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 4 — SUMMARY CARDS
            // Total Items | Total Orig Qty | Total Returned Qty
            // ══════════════════════════════════════════════════════════════════
//            float[] cardW = {32f, 2f, 32f, 2f, 32f};
//            Table cardTable = new Table(UnitValue.createPercentArray(cardW));
//            cardTable.setWidth(UnitValue.createPercentValue(100));
//
//            cardTable.addCell(buildSirCard(
//                    "TOTAL ITEMS",
//                    String.valueOf(sir.getDetails().size()),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            cardTable.addCell(buildSirCard(
//                    "TOTAL ORIG. QTY",
//                    fmtSirQty(totalOrigQty),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            cardTable.addCell(buildSirCard(
//                    "TOTAL RETURNED QTY",
//                    fmtSirQty(totalReturnedQty),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            document.add(cardTable);

            // ══════════════════════════════════════════════════════════════════
            // SECTION 5 — REMARKS
            // ══════════════════════════════════════════════════════════════════
            if (sir.getRemarks() != null && !sir.getRemarks().trim().isEmpty()) {
                document.add(new Paragraph("").setMarginBottom(10));
                Table rt = new Table(UnitValue.createPercentArray(new float[]{100f}));
                rt.setWidth(UnitValue.createPercentValue(100));
                rt.addCell(new Cell()
                        .add(new Paragraph("REMARKS")
                                .setFont(boldFont).setFontSize(8)
                                .setFontColor(new DeviceRgb(160, 120, 0)).setMarginBottom(3))
                        .add(new Paragraph(sir.getRemarks())
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
                            nvlS(sir.getSircode()) + "  |  Store Issue Return  |  Party: "
                            + nvlS(sir.getPartyName()))
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
                    .setBorder(topBorder)
                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

            footer.addCell(new Cell()
                    .add(new Paragraph("Total Items: " + sir.getDetails().size())
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
    
    private void addSirBandCell(Table table, String label, String value,
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

    private Cell buildSirCard(String label, String value,
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

    private Cell sirCell(String text, Color bg, PdfFont font, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "")
                        .setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setTextAlignment(align)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                        new DeviceRgb(220, 228, 235), 0.3f))
                .setPaddingTop(5).setPaddingBottom(5);
    }

    private String fmtSirQty(double val) {
        if (val == Math.floor(val) && !Double.isInfinite(val)) {
            return String.valueOf((long) val);
        }
        return String.format("%.3f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private String nvlS(String val) {
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
    public byte[] generatePdfReportByStorePoId(Long storePoId, Long userId, Integer isCompanyDetails) {
        try {
            // Fetch all SIRs for this storepo_id
            List<StoreIssueReturnResponseDto> sirList = getByStoreIssueId(storePoId, userId);

            if (sirList == null || sirList.isEmpty()) {
                throw new RuntimeException("No Store Issue Returns found for Store PO ID: " + storePoId);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer  = new PdfWriter(baos);
            PdfDocument pdf   = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            Color darkBlue    = new DeviceRgb(13,  71, 116);
            Color blackColor  = new DeviceRgb(0, 0, 0);
            Color tableBlue   = new DeviceRgb(26,  99, 153);
            Color lightBlueBg = new DeviceRgb(235, 245, 255);
            Color borderGray  = new DeviceRgb(200, 210, 220);
            Color labelGray   = new DeviceRgb(100, 120, 140);
            Color altRow      = new DeviceRgb(245, 248, 252);

            // ── Use first SIR for common header info ──────────────────────────
            StoreIssueReturnResponseDto firstSir = sirList.get(0);

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
                    java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

            float[] topW = {60f, 40f};
            Table topTable = new Table(UnitValue.createPercentArray(topW));
            topTable.setWidth(UnitValue.createPercentValue(100));

            topTable.addCell(new Cell()
                    .add(new Paragraph("Store Issue Return Report")
                            .setFont(boldFont).setFontSize(22).setFontColor(darkBlue).setMarginBottom(3))
                    .add(new Paragraph(printedOn)
                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(6));

            document.add(topTable);

            Table divider = new Table(UnitValue.createPercentArray(new float[]{100f}));
            divider.setWidth(UnitValue.createPercentValue(100));
            divider.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
            document.add(divider);
            document.add(new Paragraph("").setMarginBottom(10));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 2 — INFO BAND (common info from first SIR)
            // ══════════════════════════════════════════════════════════════════
            float[] bandW = {25f, 25f, 25f, 25f};
            Table bandTable = new Table(UnitValue.createPercentArray(bandW));
            bandTable.setWidth(UnitValue.createPercentValue(100));

            addSirBandCell(bandTable, "PARTY NAME",     nvlS(firstSir.getPartyName()),       lightBlueBg, labelGray, boldFont, regularFont, borderGray);
            addSirBandCell(bandTable, "STOCK TYPE",     nvlS(firstSir.getStockTypeName()),   lightBlueBg, labelGray, boldFont, regularFont, borderGray);
            addSirBandCell(bandTable, "STORE ISSUE PO", nvlS(firstSir.getStoreIssuePocode()),lightBlueBg, labelGray, boldFont, regularFont, borderGray);
            addSirBandCell(bandTable, "TOTAL RETURNS",  String.valueOf(sirList.size()),      lightBlueBg, labelGray, boldFont, regularFont, borderGray);

            document.add(bandTable);
            document.add(new Paragraph("").setMarginBottom(12));

            // ══════════════════════════════════════════════════════════════════
            // SECTION 3 — LOOP EACH SIR AS A SUB-SECTION
            // ══════════════════════════════════════════════════════════════════
            float[] colW = {4f, 22f, 16f, 10f, 12f, 12f, 12f};

            double grandTotalIssueQty    = 0;
            double grandTotalReturnedQty = 0;
            double grandTotalRemaining   = 0;

            for (StoreIssueReturnResponseDto sir : sirList) {

                // ── SIR sub-header ────────────────────────────────────────────
                float[] subHeaderW = {60f, 40f};
                Table subHeader = new Table(UnitValue.createPercentArray(subHeaderW));
                subHeader.setWidth(UnitValue.createPercentValue(100));

                subHeader.addCell(new Cell()
                        .add(new Paragraph(nvlS(sir.getSircode()))
                                .setFont(boldFont).setFontSize(11).setFontColor(darkBlue))
                        .setBorder(Border.NO_BORDER).setPaddingBottom(4));

                subHeader.addCell(new Cell()
                        .add(new Paragraph("Return Date: " + nvlS(sir.getReturndate()))
                                .setFont(regularFont).setFontSize(9).setFontColor(labelGray)
                                .setTextAlignment(TextAlignment.RIGHT))
                        .setBorder(Border.NO_BORDER).setPaddingBottom(4));

                document.add(subHeader);

                // ── Items table for this SIR ──────────────────────────────────
                Table itemTable = new Table(UnitValue.createPercentArray(colW));
                itemTable.setWidth(UnitValue.createPercentValue(100));

                String[] headers = {"#", "Item Name", "Category", "Unit",
                                     "Issue Qty", "Return Qty", "Remaining"};
                TextAlignment[] aligns = {
                    TextAlignment.CENTER, TextAlignment.LEFT,  TextAlignment.LEFT,
                    TextAlignment.CENTER, TextAlignment.RIGHT, TextAlignment.RIGHT, TextAlignment.RIGHT
                };

                for (int i = 0; i < headers.length; i++) {
                    itemTable.addHeaderCell(new Cell()
                            .add(new Paragraph(headers[i])
                                    .setFont(boldFont).setFontSize(9).setFontColor(ColorConstants.WHITE))
                            .setBackgroundColor(tableBlue)
                            .setTextAlignment(aligns[i])
                            .setPaddingTop(6).setPaddingBottom(6)
                            .setBorder(Border.NO_BORDER));
                }

                boolean alternate = false;
                int srNo = 1;
                double sirIssueQty = 0, sirReturnedQty = 0, sirRemaining = 0;

                for (StoreIssueReturnDetailResponseDto d : sir.getDetails()) {
                    Color bg = alternate ? altRow : ColorConstants.WHITE;
                    alternate = !alternate;

                    double origQty    = d.getQty();
                    double returnedQty = d.getReturnedQty();
                    double remaining  = origQty - returnedQty;

                    sirIssueQty    += origQty;
                    sirReturnedQty += returnedQty;
                    sirRemaining   += remaining;

                    itemTable.addCell(sirCell(String.valueOf(srNo++),           bg, regularFont, TextAlignment.CENTER));
                    itemTable.addCell(sirCell(nvlS(d.getRawMaterialName()),     bg, regularFont, TextAlignment.LEFT));
                    itemTable.addCell(sirCell(nvlS(d.getRawMaterialCatName()),  bg, regularFont, TextAlignment.LEFT));
                    itemTable.addCell(sirCell(nvlS(d.getUnitName()),            bg, regularFont, TextAlignment.CENTER));
                    itemTable.addCell(sirCell(fmtSirQty(origQty),              bg, regularFont, TextAlignment.RIGHT));
                    itemTable.addCell(sirCell(fmtSirQty(returnedQty),          bg, boldFont,    TextAlignment.RIGHT));
                    itemTable.addCell(sirCell(fmtSirQty(remaining),            bg, regularFont, TextAlignment.RIGHT));
                }

                document.add(itemTable);

                // ── Remarks for this SIR (if any) ─────────────────────────────
                if (sir.getRemarks() != null && !sir.getRemarks().trim().isEmpty()) {
                    Table rt = new Table(UnitValue.createPercentArray(new float[]{100f}));
                    rt.setWidth(UnitValue.createPercentValue(100));
                    rt.addCell(new Cell()
                            .add(new Paragraph("REMARKS: " + sir.getRemarks())
                                    .setFont(regularFont).setFontSize(9))
                            .setBackgroundColor(new DeviceRgb(255, 253, 235))
                            .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                                    new DeviceRgb(220, 200, 100), 1f))
                            .setPadding(8));
                    document.add(rt);
                }

                grandTotalIssueQty    += sirIssueQty;
                grandTotalReturnedQty += sirReturnedQty;
                grandTotalRemaining   += sirRemaining;

                document.add(new Paragraph("").setMarginBottom(14));
            }

            // ══════════════════════════════════════════════════════════════════
            // SECTION 4 — GRAND TOTAL SUMMARY ROW
            // ══════════════════════════════════════════════════════════════════
//            float[] summaryW = {60f, 40f};
//            Table summaryOuter = new Table(UnitValue.createPercentArray(summaryW));
//            summaryOuter.setWidth(UnitValue.createPercentValue(100));
//            summaryOuter.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            float[] innerW = {55f, 15f, 15f, 15f};
//            Table summaryBox = new Table(UnitValue.createPercentArray(innerW));
//            summaryBox.setWidth(UnitValue.createPercentValue(100));
//
//            // Header row
//            summaryBox.addCell(grandHeaderCell("",                              darkBlue, boldFont));
//            summaryBox.addCell(grandHeaderCell("Issue Qty",                     darkBlue, boldFont));
//            summaryBox.addCell(grandHeaderCell("Returned Qty",                  darkBlue, boldFont));
//            summaryBox.addCell(grandHeaderCell("Remaining",                     darkBlue, boldFont));
//
//            // Values row
//            summaryBox.addCell(grandValueCell("Grand Total",                    ColorConstants.WHITE, boldFont,    borderGray, true));
//            summaryBox.addCell(grandValueCell(fmtSirQty(grandTotalIssueQty),    ColorConstants.WHITE, regularFont, borderGray, false));
//            summaryBox.addCell(grandValueCell(fmtSirQty(grandTotalReturnedQty), ColorConstants.WHITE, boldFont,    borderGray, false));
//            summaryBox.addCell(grandValueCell(fmtSirQty(grandTotalRemaining),   ColorConstants.WHITE, regularFont, borderGray, false));
//
//            summaryOuter.addCell(new Cell().add(summaryBox).setBorder(Border.NO_BORDER).setPadding(0));
//            document.add(summaryOuter);

            // ══════════════════════════════════════════════════════════════════
            // SECTION 5 — FOOTER
            // ══════════════════════════════════════════════════════════════════
//            document.add(new Paragraph("").setMarginBottom(16));
//
//            Table footer = new Table(UnitValue.createPercentArray(new float[]{60f, 40f}));
//            footer.setWidth(UnitValue.createPercentValue(100));
//
//            com.itextpdf.layout.borders.Border topBorder =
//                    new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f);
//
//            footer.addCell(new Cell()
//                    .add(new Paragraph(nvlS(firstSir.getStoreIssuePocode())
//                            + "  |  Store Issue Return  |  Party: " + nvlS(firstSir.getPartyName()))
//                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
//                    .setBorder(topBorder)
//                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
//                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));
//
//            footer.addCell(new Cell()
//                    .add(new Paragraph("Total SIRs: " + sirList.size())
//                            .setFont(regularFont).setFontSize(8).setFontColor(labelGray)
//                            .setTextAlignment(TextAlignment.RIGHT))
//                    .setBorder(topBorder)
//                    .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
//                    .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));
//
//            document.add(footer);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── Grand total summary cell helpers ─────────────────────────────────────
    private Cell grandHeaderCell(String text, Color bg, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(bg)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(5).setPaddingBottom(5).setPaddingRight(8)
                .setBorder(Border.NO_BORDER);
    }

    private Cell grandValueCell(String text, Color bg, PdfFont font, Color borderColor, boolean isLabel) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9).setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(bg)
                .setTextAlignment(isLabel ? TextAlignment.LEFT : TextAlignment.RIGHT)
                .setPaddingTop(5).setPaddingBottom(5)
                .setPaddingLeft(isLabel ? 8 : 0).setPaddingRight(isLabel ? 0 : 8)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f));
    }
    
    @Override
    public byte[] generateExcelReport(
            Long sirId,
            Long userId) {

        try {

            StoreIssueReturnResponseDto sir = getBySirId(sirId);

            if (sir == null) {
                throw new RuntimeException(
                        "Store Issue Return not found"
                );
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Store Issue Return");

            // =========================
            // Styles
            // =========================

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);

            Font whiteFont = workbook.createFont();
            whiteFont.setBold(true);
            whiteFont.setColor(IndexedColors.WHITE.getIndex());

            headerStyle.setFont(whiteFont);

            CellStyle boldStyle = workbook.createCellStyle();

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            boldStyle.setFont(boldFont);

            // =========================
            // Company Details
            // =========================

            int rowNum = 0;


            // =========================
            // Title
            // =========================

            Row titleRow = sheet.createRow(rowNum++);

            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);

            titleCell.setCellValue("Store Issue Return");

            // Create style first
            CellStyle titleStyle = workbook.createCellStyle();

            Font titleFont = workbook.createFont();

            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            titleStyle.setFont(titleFont);

            // Then apply style
            titleCell.setCellStyle(titleStyle);

            ((org.apache.poi.ss.usermodel.Cell) titleCell).setCellStyle(titleStyle);

            rowNum++;

            // =========================
            // Basic Info
            // =========================

            Row info1 = sheet.createRow(rowNum++);
            info1.createCell(0).setCellValue("SIR Code");
            info1.createCell(1).setCellValue(nvlS(sir.getSircode()));

            Row info2 = sheet.createRow(rowNum++);
            info2.createCell(0).setCellValue("Party Name");
            info2.createCell(1).setCellValue(nvlS(sir.getPartyName()));

            Row info3 = sheet.createRow(rowNum++);
            info3.createCell(0).setCellValue("Stock Type");
            info3.createCell(1).setCellValue(nvlS(sir.getStockTypeName()));

            Row info4 = sheet.createRow(rowNum++);
            info4.createCell(0).setCellValue("Store Issue");
            info4.createCell(1).setCellValue(
                    nvlS(sir.getStoreIssuePocode()));

            Row info5 = sheet.createRow(rowNum++);
            info5.createCell(0).setCellValue("Return Date");
            info5.createCell(1).setCellValue(
                    nvlS(sir.getReturndate()));

            rowNum++;

            // =========================
            // Table Header
            // =========================

            Row headerRow = sheet.createRow(rowNum++);

            String[] headers = {
                    "#",
                    "Item Name",
                    "Category",
                    "Unit",
                    "Original Qty",
                    "Returned Qty",
                    "Remaining Qty"
            };

            for (int i = 0; i < headers.length; i++) {

            	org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);

            	cell.setCellValue(headers[i]);

            	cell.setCellStyle(headerStyle);
            }

            // =========================
            // Data Rows
            // =========================

            int srNo = 1;

            double totalOrigQty = 0;
            double totalReturnedQty = 0;

            for (StoreIssueReturnDetailResponseDto d
                    : sir.getDetails()) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(srNo++);
                row.createCell(1).setCellValue(
                        nvlS(d.getRawMaterialName()));
                row.createCell(2).setCellValue(
                        nvlS(d.getRawMaterialCatName()));
                row.createCell(3).setCellValue(
                        nvlS(d.getUnitName()));

                row.createCell(4).setCellValue(d.getQty());
                row.createCell(5).setCellValue(
                        d.getReturnedQty());
                row.createCell(6).setCellValue(
                        d.getRemainingQty());

                totalOrigQty += d.getQty();
                totalReturnedQty += d.getReturnedQty();
            }

            // =========================
            // Totals
            // =========================

            rowNum++;

            Row totalRow = sheet.createRow(rowNum++);

            org.apache.poi.ss.usermodel.Cell totalLabel = totalRow.createCell(3);

            totalLabel.setCellValue("Totals");

            totalLabel.setCellStyle(boldStyle);

            totalRow.createCell(4)
                    .setCellValue(totalOrigQty);

            totalRow.createCell(5)
                    .setCellValue(totalReturnedQty);

            // =========================
            // Remarks
            // =========================

            if (sir.getRemarks() != null
                    && !sir.getRemarks().trim().isEmpty()) {

                rowNum++;

                Row remarksRow = sheet.createRow(rowNum++);

                remarksRow.createCell(0)
                        .setCellValue(
                                "Remarks : " +
                                sir.getRemarks());
            }

            // =========================
            // Auto Size
            // =========================

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            workbook.write(baos);

            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}