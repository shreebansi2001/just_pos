package com.crmportal.service.impl;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.*;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.repository.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.StockLedgerEntryService;
import com.crmportal.service.StoreOrderingTicketCrockeryService;
import com.crmportal.service.StoreOrderingTicketService;
import java.io.ByteArrayOutputStream;
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
public class StoreOrderingTicketCrockeryServiceImpl implements StoreOrderingTicketCrockeryService{

	@Autowired
	private StoreOrderingTicketCrockeryRepository sotRepository;

	@Autowired
	private StoreOrderingTicketCrockeryDetailRepository sotDetailRepository;

	@Autowired
	private EventRawMaterialDisposableRepository eventRawMaterialDisposableRepository;

	@Autowired
	private EventMasterRepository eventMasterRepository;

	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private StockLedgerRepository stockLedgerRepository;

	@Autowired
	private RawMaterialMasterRepository rawMaterialRepository;

	@Autowired
	private PartyMasterRepository partyRepository;

	@Autowired
	private UnitMasterRepository unitMasterRepository;

		@Autowired
	EventRawMaterialServiceImpl eventRawMaterialServiceImpl;
	
	@Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;
	
	@Autowired
	private Environment environment;

	@Autowired
	EventMasterMapper eventMasterMapper;
	
	@Autowired
	private MenuPreparationServiceImpl menuPreparationServiceImpl;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// ── Generate SOT ──────────────────────────────────────────────────────────
	@Override
	public SotResponseDto generateSot(GenerateSotRequestDto request) {

		// Check if SOT already exists for this event
		Optional<StoreOrderingTicketCrockeryEntity> existing = sotRepository
				.findByEventIdAndIsDeleteFalseAndStatusNot(request.getEventId(), "DELETED");

		if (existing.isPresent()) {
			throw new RuntimeException("SOT already exists for this event: " + existing.get().getSotNo());
		}

		EventMasterEntity event = eventMasterRepository.findById(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found"));

		// Create SOT
		StoreOrderingTicketCrockeryEntity sot = new StoreOrderingTicketCrockeryEntity();
		sot.setSotNo(generateSotNo(request.getUserId()));
		sot.setEvent(event);
		sot.setStatus("PENDING");
		sot.setUser(userRepository.findById(request.getUserId()).orElse(null));
		sot = sotRepository.save(sot);

		// Fetch all event raw materials for this event
		List<EventRawMaterialDisposableEntity> rawMaterials = eventRawMaterialDisposableRepository.findByEvent_Id(request.getEventId());

		List<StoreOrderingTicketCrockeryDetailEntity> details = new ArrayList<>();

		for (EventRawMaterialDisposableEntity erm : rawMaterials) {

			if (erm.getRawMaterial() == null)
				continue;

			// Calculate available stock from stock_ledger
			double availableStock = calculateAvailableStock(erm.getRawMaterial().getId());

			StoreOrderingTicketCrockeryDetailEntity detail = new StoreOrderingTicketCrockeryDetailEntity();
			detail.setSot(sot);
			detail.setRawMaterial(erm.getRawMaterial());
			detail.setRawMaterialCat(erm.getRawMaterialCat());
			detail.setUnit(erm.getUnit());
			detail.setParty(null);
			detail.setQty(erm.getQty() != null ? erm.getQty() : 0.0);
			detail.setAcceptedQty(0.0);
			detail.setReturnQty(0.0);
			detail.setAvailableStock(availableStock);
			details.add(detail);
		}

		sotDetailRepository.saveAll(details);

		return convertToDto(sot);
	}

	// ── Accept SOT ────────────────────────────────────────────────────────────
	@Override
	public SotResponseDto acceptSot(AcceptSotRequestDto request) {

		StoreOrderingTicketCrockeryEntity sot = sotRepository.findById(request.getSotId())
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		// Update accepted qty for each detail
		for (AcceptSotDetailRequestDto d : request.getDetails()) {

			StoreOrderingTicketCrockeryDetailEntity detail = sotDetailRepository.findById(d.getSotDetailId())
					.orElseThrow(() -> new RuntimeException("SOT detail not found: " + d.getSotDetailId()));

			detail.setAcceptedQty(d.getAcceptedQty());
			sotDetailRepository.save(detail);
		}

		sot.setStatus("ACCEPTED");
		sot.setUser(userRepository.findById(request.getUserId()).orElse(null));
		sot = sotRepository.save(sot);

		// NO stock ledger entry here — only status update

		return convertToDto(sot);
	}

	

	// ── Return SOT ────────────────────────────────────────────────────────────
	@Override
	public SotResponseDto returnSot(ReturnSotRequestDto request) {

		StoreOrderingTicketCrockeryEntity sot = sotRepository.findById(request.getSotId())
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		for (ReturnSotDetailRequestDto d : request.getDetails()) {

			StoreOrderingTicketCrockeryDetailEntity detail = sotDetailRepository.findById(d.getSotDetailId())
					.orElseThrow(() -> new RuntimeException("SOT detail not found: " + d.getSotDetailId()));

			detail.setReturnQty(d.getReturnQty());
			sotDetailRepository.save(detail);
		}

		sotRepository.flush();
		sotDetailRepository.flush();

		// Save return qty to stock_ledger as STORE_ISSUE_RETURN
		saveReturnSotToStockLedger(sot, request.getUserId());

		return convertToDto(sot);
	}

	// ── Getters ───────────────────────────────────────────────────────────────

	@Override
	public List<SotResponseDto> getAll(Long userId) {
		return sotRepository.findByUserIdAndIsDeleteFalseOrderByCreatedAtDesc(userId).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	public SotResponseDto getById(Long sotId) {
		StoreOrderingTicketCrockeryEntity sot = sotRepository.findById(sotId)
				.orElseThrow(() -> new RuntimeException("SOT not found"));
		return convertToDto(sot);
	}

	@Override
	public List<SotResponseDto> getByEvent(Long eventId) {
		return sotRepository.findByEventIdAndIsDeleteFalse(eventId).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}


	@Override
	public void deleteSot(Long sotId) {
		StoreOrderingTicketCrockeryEntity sot = sotRepository.findById(sotId)
				.orElseThrow(() -> new RuntimeException("SOT not found"));
		sot.setIsDelete(true);
		sotRepository.save(sot);
	}

	@Override
	public SotResponseDto getStoreReport(Long sotId) {
		return getById(sotId);
	}

	

	private void saveReturnSotToStockLedger(StoreOrderingTicketCrockeryEntity sot, Long userId) {

		// Soft delete old return entries
		stockLedgerRepository.softDeleteByRefIdAndRefType(sot.getId(), "SOT_STORE_RETURN");
		stockLedgerRepository.flush();

		List<StoreOrderingTicketCrockeryDetailEntity> details = sotDetailRepository.findBySotId(sot.getId());

		List<StockLedgerEntity> entries = new ArrayList<>();

		for (StoreOrderingTicketCrockeryDetailEntity d : details) {
			if (d.getReturnQty() <= 0)
				continue;

			double baseQty = d.getReturnQty();
			UnitMasterEntity unit = d.getUnit();
			if (d.getUnit().getParentUnit() != null && d.getUnit().getEquivalentValue() != null
					&& d.getUnit().getEquivalentValue() != 0.0) {
				unit = d.getUnit().getParentUnit();
				baseQty = convertUnitQtyToParentQty(d.getUnit(), baseQty);
			}
			StockLedgerEntity entry = new StockLedgerEntity();
			entry.setRawMaterial(d.getRawMaterial());
			entry.setRawMaterialCat(d.getRawMaterialCat());
			entry.setUnit(unit);
			entry.setParty(d.getParty());
			entry.setStockType(null);
			entry.setUser(userRepository.findById(userId).orElse(null));
			entry.setRefId(sot.getId());
			entry.setRefCode(sot.getSotNo());
			entry.setRefType("SOT_STORE_RETURN");
			entry.setTransactionDate(LocalDate.now());
			entry.setVoucher(sot.getSotNo());
			entry.setBillNo(null);
			entry.setQty(baseQty);
			entry.setQtyIn(baseQty);
			entry.setQtyOut(0.0);
			entry.setIsDelete(false);
			entries.add(entry);
		}

		stockLedgerRepository.saveAll(entries);
		stockLedgerRepository.flush();
	}

	private double calculateAvailableStock(Long rawMaterialId) {
	    try {
	        // OPB from raw material master
	        double opb = rawMaterialRepository.findById(rawMaterialId)
	                .map(r -> r.getOpbStock() != null
	                        ? r.getOpbStock().doubleValue() : 0.0)
	                .orElse(0.0);

	        //  Purchase — qty_in
	        double purchase = stockLedgerRepository
	                .sumQtyInByRawMaterialAndDateRangeAndRefType(
	                        rawMaterialId,
	                        LocalDate.of(2000, 1, 1),
	                        LocalDate.now(), "PURCHASE");

	        //  Purchase Return — qty_out
	        double purchaseReturn = stockLedgerRepository
	                .sumQtyOutByRawMaterialAndDateRangeAndRefType(
	                        rawMaterialId,
	                        LocalDate.of(2000, 1, 1),
	                        LocalDate.now(), "PURCHASE_RETURN");

	        //  Store Issue — qty_out
	        double storeIssue = stockLedgerRepository
	                .sumQtyOutByRawMaterialAndDateRangeAndRefType(
	                        rawMaterialId,
	                        LocalDate.of(2000, 1, 1),
	                        LocalDate.now(), "STORE_ISSUE");

	        //  Store Issue Return — qty_in
	        double storeReturn = stockLedgerRepository
	                .sumQtyInByRawMaterialAndDateRangeAndRefType(
	                        rawMaterialId,
	                        LocalDate.of(2000, 1, 1),
	                        LocalDate.now(), "STORE_ISSUE_RETURN");

	        //  Chef Requisition — qty_out (same as store issue)
	        double chefRequisition = stockLedgerRepository
	                .sumQtyOutByRawMaterialAndDateRangeAndRefType(
	                        rawMaterialId,
	                        LocalDate.of(2000, 1, 1),
	                        LocalDate.now(), "CHEF_REQUISITION");

	        //  SOT Store Issue — qty_out
	        double sotStoreIssue = stockLedgerRepository
	                .sumQtyOutByRawMaterialAndDateRangeAndRefType(
	                        rawMaterialId,
	                        LocalDate.of(2000, 1, 1),
	                        LocalDate.now(), "SOT_STORE_ISSUE");

	        //  SOT Store Return — qty_in
	        double sotStoreReturn = stockLedgerRepository
	                .sumQtyInByRawMaterialAndDateRangeAndRefType(
	                        rawMaterialId,
	                        LocalDate.of(2000, 1, 1),
	                        LocalDate.now(), "SOT_STORE_RETURN");

	        //  Same formula as stock ledger closing stock
	        return opb
	                + purchase
	                - purchaseReturn
	                - storeIssue
	                - chefRequisition
	                - sotStoreIssue
	                + storeReturn
	                + sotStoreReturn;

	    } catch (Exception e) {
	        return 0.0;
	    }
	}

	// ── Code Generators ───────────────────────────────────────────────────────

	/*
	 * private String generateSotNo() { String prefix = "SOT"; String lastCode =
	 * sotRepository.findMaxSotNo(prefix); long next = 1; if (lastCode != null) {
	 * try { next = Long.parseLong(lastCode.replace(prefix, "")) + 1; } catch
	 * (NumberFormatException e) { next = 1; } } return prefix +
	 * String.format("%05d", next); // e.g. SOT00001 }
	 */
	
	private String generateSotNo(Long userId) {

	    StoreOrderingTicketCrockeryEntity lastSot =
	            sotRepository.findTopByUserIdOrderByIdDesc(userId);

	    int nextNumber = 1;

	    if (lastSot != null && lastSot.getSotNo() != null) {
	        String lastNo = lastSot.getSotNo(); // Example: SOT00025
	        nextNumber = Integer.parseInt(lastNo.replace("SOT", "")) + 1;
	    }

	    return String.format("SOT%05d", nextNumber);
	}

	

	// ── Converters ────────────────────────────────────────────────────────────

	private SotResponseDto convertToDto(StoreOrderingTicketCrockeryEntity sot) {
		SotResponseDto dto = new SotResponseDto();
		dto.setId(sot.getId());
		dto.setSotNo(sot.getSotNo());
		dto.setEventId(sot.getEvent().getId());
		dto.setEventName(sot.getEvent().getParty() != null ? sot.getEvent().getParty().getNameEnglish() : "");
		dto.setStatus(sot.getStatus());
		dto.setCreatedAt(sot.getCreatedAt().toString());
		dto.setUserId(sot.getUser() != null ? sot.getUser().getId() : null);

		List<SotDetailResponseDto> details = sotDetailRepository.findBySotId(sot.getId()).stream().map(d -> {
			SotDetailResponseDto rd = new SotDetailResponseDto();
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
			if (d.getParty() != null) {
				rd.setPartyId(d.getParty().getId());
				rd.setPartyName(d.getParty().getNameEnglish());
			}
			rd.setQty(d.getQty());
			rd.setAcceptedQty(d.getAcceptedQty());
			rd.setReturnQty(d.getReturnQty());
			rd.setAvailableStock(d.getAvailableStock());
			return rd;
		}).collect(Collectors.toList());

		dto.setDetails(details);
		return dto;
	}

	
	
	@Override
	public byte[] generatePdfReport(Long sotId, Long userId, Integer isCompanyDetails) {
	    try {
	        SotResponseDto sot = getById(sotId);
	        if (sot == null) {
	            throw new RuntimeException("SOT not found with ID: " + sotId);
	        }

			EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(sot.getEventId())
					.orElseThrow(() -> new RuntimeException("Event not found."));
	        
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter pdfWriter  = new PdfWriter(baos);
	        PdfDocument pdfDoc   = new PdfDocument(pdfWriter);
	        Document document    = new Document(pdfDoc, PageSize.A4.rotate());
	        document.setMargins(25, 25, 40, 25); // bottom margin bigger for footer

	        PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

	        // ── Colors (matching sample: dark header, white text, gray alt) ──
	        Color darkHeader  = new DeviceRgb(50,  50,  50);   // near-black header
	        Color blackColor    = new DeviceRgb(0, 0, 0);
	        Color altRow      = new DeviceRgb(245, 245, 245);  // light gray alt
	        Color totalRowBg  = new DeviceRgb(200, 200, 200);  // gray total row
	        Color borderColor = new DeviceRgb(150, 150, 150);  // grid border
	        Color darkBlue    = new DeviceRgb(13,  71,  116);
	        Color labelGray   = new DeviceRgb(100, 120, 140);
	        Color lightBlueBg = new DeviceRgb(235, 245, 255);

	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 0 — COMPANY HEADER
	        // ══════════════════════════════════════════════════════════════════
	        CompanyDetailsResponseDto cmpDto = null;
	        try {
	            cmpDto = getCompanyDetails(userId);
	        } catch (Exception ignored) {}

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
	        // SECTION 1 — TOP HEADER (SOT No + Event + Status)
	        // ══════════════════════════════════════════════════════════════════
	        float[] topW = {50f, 50f};
	        Table topTable = new Table(UnitValue.createPercentArray(topW));
	        topTable.setWidth(UnitValue.createPercentValue(100));

	        topTable.addCell(new Cell(1, 2)
	                .add(new Paragraph("Store Report (Crockery SOT)")
	                        .setFont(boldFont).setFontSize(16).setFontColor(darkBlue))
	                .add(new Paragraph("SOT No: " + sotNvl(sot.getSotNo())
	                		+ "   |   Event Date: " + sotNvl(eventMaster.getEventStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
	                        + "   |   Party Name: " + sotNvl(eventMaster.getParty().getNameEnglish())
	                        + "   |   Event Name: " + sotNvl(eventMaster.getEventType().getNameEnglish()))
	                        .setFont(regularFont).setFontSize(9).setFontColor(labelGray))
	                .setBorder(Border.NO_BORDER).setPaddingBottom(4));

	        document.add(topTable);
	        document.add(new Paragraph("").setMarginBottom(6));


	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 2 — MAIN TABLE
	        // Columns: Item Name | Category | Transfer | Return Qty | Qty |
	        //          Unit | Supp Rate | Main Unit | Total
	        // ══════════════════════════════════════════════════════════════════
	        float[] colW = {22f, 13f, 8f, 9f, 7f, 8f, 9f, 8f, 10f};
	        Table mainTable = new Table(UnitValue.createPercentArray(colW));
	        mainTable.setWidth(UnitValue.createPercentValue(100));

	        String[] headers = {
	            "ITEM NAME", "CATEGORY NAME", "TRANSFER", "RETURN QTY",
	            "QTY", "UNIT", "SUPP RATE", "MAIN UNIT", "TOTAL"
	        };
	        TextAlignment[] hAligns = {
	            TextAlignment.LEFT,   TextAlignment.LEFT,   TextAlignment.RIGHT,
	            TextAlignment.RIGHT,  TextAlignment.RIGHT,  TextAlignment.CENTER,
	            TextAlignment.RIGHT,  TextAlignment.CENTER, TextAlignment.RIGHT
	        };

	        for (int i = 0; i < headers.length; i++) {
	            mainTable.addHeaderCell(new Cell()
	                    .add(new Paragraph(headers[i])
	                            .setFont(boldFont).setFontSize(8)
	                            .setFontColor(ColorConstants.WHITE))
	                    .setBackgroundColor(darkHeader)
	                    .setTextAlignment(hAligns[i])
	                    .setPaddingTop(6).setPaddingBottom(6).setPaddingLeft(4).setPaddingRight(4)
	                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f)));
	        }

	        // ── Data rows + totals ────────────────────────────────────────────
	        boolean alternate = false;
	        double grandTotal = 0;

	        for (SotDetailResponseDto d : sot.getDetails()) {
	            Color bg = alternate ? altRow : ColorConstants.WHITE;
	            alternate = !alternate;

	            double transfer   = d.getAcceptedQty();
	            double returnQty  = d.getReturnQty();
	            double qty        = transfer - returnQty; // net qty
	            double suppRate   = 0.0;

	            // Get supplier rate from raw material master
	            try {
	                suppRate = rawMaterialRepository.findById(d.getRawMaterialId())
	                        .map(r -> r.getSupplierRate() != null
	                                ? r.getSupplierRate().doubleValue() : 0.0)
	                        .orElse(0.0);
	            } catch (Exception ignored) {}

	            double total = qty * suppRate;
	            grandTotal += total;

	            String unitName     = sotNvl(d.getUnitName());
	            String mainUnitName = unitName; // default same

	            // Resolve parent unit for main unit column
	            try {
	                final Long rawMatId = d.getRawMaterialId();
	                Long unitId = d.getUnitId();
	                if (unitId != null) {
	                    com.itextpdf.kernel.colors.Color ignored2 = null; // just to scope
	                    UnitMasterEntity unitEnt = unitMasterRepository.findById(unitId).orElse(null);
	                    if (unitEnt != null && unitEnt.getParentUnit() != null) {
	                        mainUnitName = unitEnt.getParentUnit().getNameEnglish();
	                    }
	                }
	            } catch (Exception ignored) {}

	            mainTable.addCell(sotCell(sotNvl(d.getRawMaterialName()),    bg, regularFont, TextAlignment.LEFT,   borderColor));
	            mainTable.addCell(sotCell(sotNvl(d.getRawMaterialCatName()), bg, regularFont, TextAlignment.LEFT,   borderColor));
	            mainTable.addCell(sotCell(sotFmt(transfer),                   bg, regularFont, TextAlignment.RIGHT,  borderColor));
	            mainTable.addCell(sotCell(sotFmt(returnQty),                  bg, regularFont, TextAlignment.RIGHT,  borderColor));
	            mainTable.addCell(sotCell(sotFmt(qty),                        bg, regularFont, TextAlignment.RIGHT,  borderColor));
	            mainTable.addCell(sotCell(unitName,                           bg, regularFont, TextAlignment.CENTER, borderColor));
	            mainTable.addCell(sotCell(sotFmt(suppRate),                   bg, regularFont, TextAlignment.RIGHT,  borderColor));
	            mainTable.addCell(sotCell(mainUnitName,                       bg, regularFont, TextAlignment.CENTER, borderColor));
	            mainTable.addCell(sotCell(sotFmt(total),                      bg, regularFont, TextAlignment.RIGHT,  borderColor));
	        }

	        // ── AMOUNT total footer row ───────────────────────────────────────
	        mainTable.addCell(new Cell(1, 8)
	                .add(new Paragraph("AMOUNT")
	                        .setFont(boldFont).setFontSize(9)
	                        .setFontColor(ColorConstants.BLACK))
	                .setTextAlignment(TextAlignment.RIGHT)
	                .setBackgroundColor(totalRowBg)
	                .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f))
	                .setPaddingTop(5).setPaddingBottom(5).setPaddingRight(6));

	        mainTable.addCell(new Cell()
	                .add(new Paragraph(sotFmt(grandTotal))
	                        .setFont(boldFont).setFontSize(9))
	                .setTextAlignment(TextAlignment.RIGHT)
	                .setBackgroundColor(totalRowBg)
	                .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f))
	                .setPaddingTop(5).setPaddingBottom(5).setPaddingRight(4));

	        document.add(mainTable);

	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 3 — FOOTER (Sign | Page No | Timestamp)
	        // ══════════════════════════════════════════════════════════════════
	        document.add(new Paragraph("").setMarginBottom(10));

	        String timestamp = java.time.LocalDate.now()
	                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
	                " " +
	                java.time.LocalTime.now()
	                        .format(java.time.format.DateTimeFormatter.ofPattern("h.mm a"));

	        float[] footW = {33f, 34f, 33f};
	        Table footerTable = new Table(UnitValue.createPercentArray(footW));
	        footerTable.setWidth(UnitValue.createPercentValue(100));

	        // Left: Sign
	        footerTable.addCell(new Cell()
	                .add(new Paragraph("Sign :  ___________________")
	                        .setFont(regularFont).setFontSize(9)
	                        .setFontColor(ColorConstants.BLACK))
	                .setBorder(Border.NO_BORDER));

	        // Center: page number placeholder (static 1 — for multi-page use event handler)
	        footerTable.addCell(new Cell()
	                .add(new Paragraph("1")
	                        .setFont(regularFont).setFontSize(9)
	                        .setTextAlignment(TextAlignment.CENTER))
	                .setBorder(Border.NO_BORDER));

	        document.add(footerTable);
	        document.close();

	        return baos.toByteArray();

	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	private Cell sotCell(String text, Color bg, PdfFont font,
	        TextAlignment align, Color borderColor) {
	    return new Cell()
	            .add(new Paragraph(text != null ? text : "")
	                    .setFont(font).setFontSize(8))
	            .setBackgroundColor(bg)
	            .setTextAlignment(align)
	            .setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.3f))
	            .setPaddingTop(4).setPaddingBottom(4)
	            .setPaddingLeft(4).setPaddingRight(4);
	}

	private String sotFmt(double val) {
	    if (val == Math.floor(val) && !Double.isInfinite(val)) {
	        return String.format("%.2f", val);
	    }
	    return String.format("%.2f", val);
	}

	private String sotNvl(String val) {
	    return (val != null && !val.isEmpty()) ? val : "";
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
	public SotResponseDto updateSotDetails(UpdateSotDetailsRequestDto request) {

		StoreOrderingTicketCrockeryEntity sot = sotRepository.findById(request.getSotId())
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		for (UpdateSotDetailItemRequestDto d : request.getDetails()) {

			StoreOrderingTicketCrockeryDetailEntity detail = sotDetailRepository.findById(d.getSotDetailId())
					.orElseThrow(() -> new RuntimeException("SOT detail not found: " + d.getSotDetailId()));

			// Update qty
			detail.setAcceptedQty(d.getQty());

			// Update supplier if changed
			if (d.getPartyId() != null && d.getPartyId() != 0) {
				detail.setParty(partyRepository.findById(d.getPartyId()).orElse(null));
			}

			// Update unit if changed
			if (d.getUnitId() != null) {
				detail.setUnit(unitMasterRepository.findById(d.getUnitId()).orElse(null));
			}

			sotDetailRepository.save(detail);
		}

		sotDetailRepository.flush();
		return convertToDto(sot);
	}
	
	    
        // ── Private helpers ───────────────────────────────────────────────────────
 
   
	private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {

		return BigDecimal.valueOf(qty).divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
				.doubleValue();
	}
	
	@Override
	public boolean checkSotExists(Long eventId, Long userId) {
	    return sotRepository.existsByEventIdAndUserIdAndIsDeleteFalse(eventId, userId);
	}
	
	// ── Generate Manual PO agency wise ────────────────────────────────────────
	@Override
	public List<SotManualPoResponseDto> generateManualPo(Long sotId, Long userId) {

		StoreOrderingTicketCrockeryEntity sot = sotRepository.findById(sotId)
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		// Check if SOT is accepted first
		if (!"ACCEPTED".equalsIgnoreCase(sot.getStatus())) {
			throw new RuntimeException(
					"Cannot generate PO. SOT must be ACCEPTED first. Current status: " + sot.getStatus());
		}

		List<StoreOrderingTicketCrockeryDetailEntity> details = sotDetailRepository.findBySotId(sotId);

		
		// Update SOT status
		sot.setStatus("PO_GENERATED");
		sotRepository.save(sot);
		sotRepository.flush();
		sotDetailRepository.flush();

		// Save accepted qty to stock_ledger as STORE_ISSUE here
		saveAcceptedSotToStockLedger(sot);

		return Collections.emptyList();
	}
	
	// ── Stock Ledger Helpers ──────────────────────────────────────────────────

		private void saveAcceptedSotToStockLedger(StoreOrderingTicketCrockeryEntity sot) {

			// Soft delete old entries
			stockLedgerRepository.softDeleteByRefIdAndRefType(sot.getId(), "SOT_STORE_ISSUE");
			stockLedgerRepository.flush();

			List<StoreOrderingTicketCrockeryDetailEntity> details = sotDetailRepository.findBySotId(sot.getId());

			List<StockLedgerEntity> entries = new ArrayList<>();

			for (StoreOrderingTicketCrockeryDetailEntity d : details) {
				if (d.getAcceptedQty() <= 0)
					continue;

				double baseQty = d.getAcceptedQty();
				UnitMasterEntity unit = d.getUnit();
				if (d.getUnit().getParentUnit() != null && d.getUnit().getEquivalentValue() != null
						&& d.getUnit().getEquivalentValue() != 0.0) {
					unit = d.getUnit().getParentUnit();
					baseQty = convertUnitQtyToParentQty(d.getUnit(), baseQty);
				}

				StockLedgerEntity entry = new StockLedgerEntity();
				entry.setRawMaterial(d.getRawMaterial());
				entry.setRawMaterialCat(d.getRawMaterialCat());
				entry.setUnit(unit);
				entry.setParty(d.getParty());
				entry.setStockType(null);
				entry.setUser(sot.getUser());
				entry.setRefId(sot.getId());
				entry.setRefCode(sot.getSotNo());
				entry.setRefType("SOT_STORE_ISSUE");
				entry.setTransactionDate(LocalDate.now());
				entry.setVoucher(sot.getSotNo());
				entry.setBillNo(null);
				entry.setQty(baseQty);
				entry.setQtyIn(0.0);
				entry.setQtyOut(baseQty);
				entry.setIsDelete(false);
				entries.add(entry);
			}

			stockLedgerRepository.saveAll(entries);
			stockLedgerRepository.flush();
		}
}