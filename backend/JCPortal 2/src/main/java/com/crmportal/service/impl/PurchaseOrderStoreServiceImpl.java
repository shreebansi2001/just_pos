package com.crmportal.service.impl;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.ChefRequisitionEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PurchaseOrderEntity;
import com.crmportal.entity.PurchaseOrderStoreDetailEntity;
import com.crmportal.entity.PurchaseOrderStoreEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.ChefRequisitionDetailRepository;
import com.crmportal.repository.ChefRequisitionRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PurchaseOrderDetailRepository;
import com.crmportal.repository.PurchaseOrderRepository;
import com.crmportal.repository.PurchaseOrderStoreDetailRepository;
import com.crmportal.repository.PurchaseOrderStoreRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.StockLedgerRepository;
import com.crmportal.repository.StockTypeRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.PdfWithPriceRequestDto;
import com.crmportal.request.dto.PriceOverrideDto;
import com.crmportal.request.dto.PurchaseOrderStoreDetailRequestDto;
import com.crmportal.request.dto.PurchaseOrderStoreRequestDto;
import com.crmportal.response.dto.ChefRequisitionDetailResponseDto;
import com.crmportal.response.dto.ChefRequisitionResponseDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.CrcodeResponseDto;
import com.crmportal.response.dto.DatewisePurchaseReportDataResponseDto;
import com.crmportal.response.dto.DatewisePurchaseReportResponseDto;
import com.crmportal.response.dto.DatewiseStoreIssueDateResponseDto;
import com.crmportal.response.dto.DatewiseStoreIssueDetailsReportResponseDto;
import com.crmportal.response.dto.DatewiseStoreIssueReportResponseDto;
import com.crmportal.response.dto.EventPartiesResponseDto;
import com.crmportal.response.dto.PriceItemResponseDto;
import com.crmportal.response.dto.PurchaseOrderDetailResponseDto;
import com.crmportal.response.dto.PurchaseOrderResponseDto;
import com.crmportal.response.dto.PurchaseOrderStoreDetailResponseDto;
import com.crmportal.response.dto.PurchaseOrderStoreResponseDto;
import com.crmportal.service.PurchaseOrderStoreService;
import com.crmportal.service.StockLedgerEntryService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;

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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import org.springframework.core.env.Environment;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@Transactional
public class PurchaseOrderStoreServiceImpl implements PurchaseOrderStoreService {

	@Autowired
	private PurchaseOrderStoreRepository poRepository;

	@Autowired
	private PurchaseOrderStoreDetailRepository detailRepository;

	@Autowired
	private PartyMasterRepository partyRepository;

	@Autowired
	private StockTypeRepository stockTypeRepository;

	@Autowired
	private RawMaterialMasterRepository rawMaterialRepository;

	@Autowired
	private RawMaterialCategoryMasterRepository rawMaterialCatRepository;

	@Autowired
	private UnitMasterRepository unitRepository;

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

	@Autowired
	private ChefRequisitionRepository crRepository;

	@Autowired
	private ChefRequisitionDetailRepository crDetailRepository;

	@Autowired
	private StockLedgerRepository stockLedgerRepository;

	@Autowired
	private UnitMasterRepository unitMasterRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	RawMaterialReportServiceImpl rawMaterialReportServiceImpl;

	@Override
	public PurchaseOrderStoreResponseDto addOrUpdate(PurchaseOrderStoreRequestDto request) {

		if (request.getEventId() != null && request.getEventId() > 0) {
			eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
					.orElseThrow(() -> new RuntimeException("Event not found with id : " + request.getEventId()));
		}

		PurchaseOrderStoreEntity entity;

		if (request.getId() == null || request.getId() == 0 || request.getId() == -1) {
			entity = new PurchaseOrderStoreEntity();
			Long userId = request.getUserId();
			entity.setPocode(generatePoCode(userId));
		} else {
			entity = poRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("PO not found"));
			detailRepository.deleteByPoId(entity.getId());
		}

//        entity.setParty(
//                request.getPartyId() != null
//                        ? partyRepository.findById(request.getPartyId()).orElse(null)
//                        : null
//        );

		if (request.getEventId() != null && request.getPartyId() > 0) {
			entity.setEventId(request.getEventId());
		} else {
			entity.setEventId(null);
		}

		if (request.getPartyId() != null && request.getPartyId() > 0) {
			entity.setParty(partyRepository.findById(request.getPartyId()).orElse(null));

		} else {
			entity.setParty(null);
		}

		entity.setVoucher(request.getVoucher());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		entity.setPodate(LocalDate.parse(request.getPodate(), formatter));

		entity.setInvoicetype(request.getInvoicetype());
		if (request.getStockTypeId() != null) {
			entity.setStocktype(stockTypeRepository.findById(request.getStockTypeId())
					.orElseThrow(() -> new RuntimeException("Stock type not found")));
		} else {
			entity.setStocktype(null);
		}
		if (request.getKitchenTypeId() != null) {

			entity.setKitchentype(stockTypeRepository.findById(request.getKitchenTypeId())
					.orElseThrow(() -> new RuntimeException("Kitchen type not found")));
		} else {
			entity.setKitchentype(null);
		}

		// ── Chef Requisition (nullable) ───────────────────────────────────────
		if (request.getCrId() != null && request.getCrId() > 0) {
			ChefRequisitionEntity cr = crRepository.findById(request.getCrId()).orElseThrow(
					() -> new RuntimeException("Chef Requisition not found with id: " + request.getCrId()));
			entity.setChefRequisition(cr);
			entity.setCrcode(cr.getCrcode()); // ← pulled from ChefRequisitionEntity
		} else {
			entity.setChefRequisition(null);
			entity.setCrcode(null); // ← default null if no CR selected
		}

		entity.setRemarks(request.getRemarks());

		entity.setUser(
				userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));

		if (request.getStatus() != null && !request.getStatus().isEmpty()) {
			entity.setStatus(request.getStatus());
		} else {
			if (entity.getStatus() == null)
				entity.setStatus("PENDING");
		}

		entity = poRepository.save(entity);

		// Only save to stock_ledger if status is COMPLETED

		List<PurchaseOrderStoreDetailEntity> detailEntities = new ArrayList<>();

		for (PurchaseOrderStoreDetailRequestDto d : request.getDetails()) {

			PurchaseOrderStoreDetailEntity detail = new PurchaseOrderStoreDetailEntity();

			detail.setPo(entity);
			RawMaterialMasterEntity rm = rawMaterialRepository.findById(d.getRawMaterialId()).orElse(null);

			detail.setRawMaterial(rm);
			detail.setRawMaterialCat(rm != null ? rm.getRawMaterialCat() : null);
			detail.setUnit(rm != null ? rm.getUnit() : null);
			detail.setQty(d.getQty());
			detail.setUserid(request.getUserId());
			detail.setIsAddInStock(d.getIsAddInStock());
			if (rm != null) {
				Double closingStock = calculateClosingStock(rm);
				detail.setClosingStock(closingStock);
			}

			detailEntities.add(detail);
		}

		detailRepository.saveAll(detailEntities);
		detailRepository.flush();

		if ("COMPLETED".equalsIgnoreCase(entity.getStatus())) {
			detailRepository.flush();
			stockLedgerEntryService.saveStoreIssue(entity);
		}
		return convertToResponse(entity);
	}

//    @Override
//    public PurchaseOrderStoreResponseDto updateStatus(Long poId, String status) {
//
//        PurchaseOrderStoreEntity entity = poRepository.findById(poId)
//                .orElseThrow(() -> new RuntimeException("PO not found with id: " + poId));
//
//        String oldStatus = entity.getStatus();
//
//        entity.setStatus(status.toUpperCase());
//
//        poRepository.save(entity);
//        poRepository.flush();
//
//        // Only trigger once
//        if ("COMPLETED".equalsIgnoreCase(status)
//                && !"COMPLETED".equalsIgnoreCase(oldStatus)) {
//
//            detailRepository.flush();
//            stockLedgerEntryService.saveStoreIssue(entity);
//        }
//
//        return convertToResponse(entity);
//    }

	@Override
	@Transactional
	public PurchaseOrderStoreResponseDto updateStatus(Long poId, String status) {

		PurchaseOrderStoreEntity entity = poRepository.findById(poId)
				.orElseThrow(() -> new RuntimeException("PO not found with id: " + poId));

		String oldStatus = entity.getStatus() != null ? entity.getStatus().trim().toUpperCase() : "";

		String newStatus = status != null ? status.trim().toUpperCase() : "";

		entity.setStatus(newStatus);

		poRepository.saveAndFlush(entity);

		if ("COMPLETED".equals(newStatus) && !"COMPLETED".equals(oldStatus)) {

			// VERY IMPORTANT
			poRepository.flush();

			// OPTIONAL EXTRA SAFETY
			poRepository.flush();

			stockLedgerEntryService.saveStoreIssue(entity);
		}

		return convertToResponse(entity);
	}

	@Override
	public List<PurchaseOrderStoreResponseDto> getByUser(Long userId) {

		return poRepository.getByUserDesc(userId).stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Override
	public PurchaseOrderStoreResponseDto getByPoId(Long poId) {

		PurchaseOrderStoreEntity entity = poRepository.findById(poId)
				.orElseThrow(() -> new RuntimeException("PO not found"));

		return convertToResponse(entity);
	}

	private String generatePoCode(Long userId) {

		LocalDate today = LocalDate.now();

		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

		long count = poRepository.countByUserAndCreatedAtBetween(userId, startOfDay, endOfDay);

		long nextNumber = count + 1;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMM");

		String formattedDate = today.format(formatter);

		return "SI-" + userId + "-" + formattedDate + "-" + nextNumber;
	}

	private PurchaseOrderStoreResponseDto convertToResponse(PurchaseOrderStoreEntity entity) {

		PurchaseOrderStoreResponseDto dto = new PurchaseOrderStoreResponseDto();

		dto.setId(entity.getId());

		dto.setPocode(entity.getPocode());

		if (entity.getEventId() != null) {
			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(entity.getEventId())
					.orElseThrow(() -> new RuntimeException("Event not found with id : " + entity.getEventId()));
			dto.setEventId(entity.getEventId());
			dto.setEventName(event.getEventType().getNameEnglish());
			dto.setEventDate(event.getEventStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			;
		} else {
			dto.setEventId(null);
			dto.setEventDate(null);
		}

		// ── Party nullable ─────────────────────────────────────────────
		if (entity.getParty() != null) {

			dto.setPartyId(entity.getParty().getId());

			dto.setPartyName(entity.getParty().getNameEnglish());

		} else {

			dto.setPartyId(null);

			dto.setPartyName(null);
		}

		dto.setVoucher(entity.getVoucher());

		dto.setPodate(entity.getPodate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		dto.setInvoicetype(entity.getInvoicetype());

		// ── Stock Type ────────────────────────────────────────────────
		if (entity.getStocktype() != null) {

			dto.setStockTypeId(entity.getStocktype().getId());

			dto.setStockTypeName(entity.getStocktype().getNameEnglish());
		}

		if (entity.getKitchentype() != null) {

			dto.setKitchenTypeId(entity.getKitchentype().getId());

			dto.setKitchenTypeName(entity.getKitchentype().getNameEnglish());
		}

		dto.setRemarks(entity.getRemarks());

		dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);

		// ── User ──────────────────────────────────────────────────────
		if (entity.getUser() != null) {

			dto.setUserId(entity.getUser().getId());
		}

		dto.setStatus(entity.getStatus());

		// ── Chef Requisition nullable ─────────────────────────────────
		if (entity.getChefRequisition() != null) {

			dto.setCrId(entity.getChefRequisition().getId());

			dto.setCrcode(entity.getChefRequisition().getCrcode());

		} else {

			dto.setCrId(null);

			dto.setCrcode(entity.getCrcode());
		}

		List<PurchaseOrderStoreDetailEntity> details = detailRepository.findByPoId(entity.getId());

		List<PurchaseOrderStoreDetailResponseDto> detailDtos = details.stream().map(d -> {

			PurchaseOrderStoreDetailResponseDto rd = new PurchaseOrderStoreDetailResponseDto();

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

			rd.setQty(d.getQty());
			rd.setQty(d.getQty());
			rd.setClosingStock(d.getClosingStock());
			rd.setIsAddInStock(d.getIsAddInStock());
			return rd;

		}).collect(Collectors.toList());

		dto.setDetails(detailDtos);

		return dto;
	}

	@Override
	public byte[] generatePdfReport(Long poId, Long userId, Integer isCompanyDetails) {
		try {
			PurchaseOrderStoreResponseDto po = getByPoId(poId);
			if (po == null) {
				throw new RuntimeException("Store Issue not found with ID: " + poId);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf, PageSize.A4);
			document.setMargins(30, 30, 30, 30);

			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

			// ── Colors ────────────────────────────────────────────────────────
			Color darkBlue = new DeviceRgb(13, 71, 116);
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color tableBlue = new DeviceRgb(26, 99, 153);
			Color lightBlueBg = new DeviceRgb(235, 245, 255);
			Color cardBg = new DeviceRgb(245, 248, 252);
			Color borderGray = new DeviceRgb(200, 210, 220);
			Color labelGray = new DeviceRgb(100, 120, 140);
			Color altRow = new DeviceRgb(245, 248, 252);
			Color badgeBg = new DeviceRgb(210, 230, 255);

			// ══════════════════════════════════════════════════════════════════
			// SECTION 0 — COMPANY HEADER
			// ══════════════════════════════════════════════════════════════════
			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId);

			if (isCompanyDetails == 1) {

				float[] companyWidths = { 20f, 2f, 78f };
				Table headerTable = new Table(UnitValue.createPercentArray(companyWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));

				// Logo (spans 3 rows × 2 cols — exact match to reference)
				try {
					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());

					Image logo = new Image(logoData);
					logo.setWidth(100f);
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

					headerTable.addCell(new Cell(3, 2).add(logo)
							.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
							.setBorder(Border.NO_BORDER).setPaddingBottom(10f));

				} catch (Exception e) {
					headerTable.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
				}

				// Company Name
				headerTable.addCell(new Cell()
						.add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPaddingLeft(15f));

				// Phone
				headerTable
						.addCell(new Cell()
								.add(new Paragraph()
										.add(new Text("Phone : ").setFont(boldFont).setFontSize(14)
												.setFontColor(blackColor))
										.add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getOfficeNo() : "")
												.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(15f)
								.setPaddingBottom(10f));

				// Email
				headerTable
						.addCell(new Cell()
								.add(new Paragraph()
										.add(new Text("Email : ").setFont(boldFont).setFontSize(14)
												.setFontColor(blackColor))
										.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
												.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(15f)
								.setPaddingBottom(10f));

				// ── Empty filler cell (reference has this exact line) ─────────
				headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));

				document.add(headerTable);

				// Dark blue divider
				Table companyDivider = new Table(UnitValue.createPercentArray(new float[] { 100f }));
				companyDivider.setWidth(UnitValue.createPercentValue(100));
				companyDivider
						.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
				document.add(companyDivider);
				document.add(new Paragraph("").setMarginBottom(8));
			}

			// ══════════════════════════════════════════════════════════════════
			// SECTION 1 — TOP HEADER
			// ══════════════════════════════════════════════════════════════════
			String printedOn = "Printed on: "
					+ java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + " at "
					+ java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
							.toLowerCase();

			float[] topW = { 60f, 40f };
			Table topTable = new Table(UnitValue.createPercentArray(topW));
			topTable.setWidth(UnitValue.createPercentValue(100));

			// Left: title + badge + printed-on
			Cell titleCell = new Cell()
					.add(new Paragraph("Store Issue Report").setFont(boldFont).setFontSize(22).setFontColor(darkBlue)
							.setMarginBottom(3))
					.add(new Paragraph(printedOn).setFont(regularFont).setFontSize(8).setFontColor(labelGray))
					.setBorder(Border.NO_BORDER).setPaddingBottom(6);
			topTable.addCell(titleCell);

			// Right: SI code + issue date
			Cell codeCell = new Cell()
					.add(new Paragraph(nvl(po.getPocode())).setFont(boldFont).setFontSize(14).setFontColor(darkBlue)
							.setTextAlignment(TextAlignment.RIGHT).setMarginBottom(2))
					.setBorder(Border.NO_BORDER).setPaddingBottom(6);
			topTable.addCell(codeCell);

			document.add(topTable);

			// ── Divider ───────────────────────────────────────────────────────
			Table divider = new Table(UnitValue.createPercentArray(new float[] { 100f }));
			divider.setWidth(UnitValue.createPercentValue(100));
			divider.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
			document.add(divider);
			document.add(new Paragraph("").setMarginBottom(10));

			// ══════════════════════════════════════════════════════════════════
			// SECTION 2 — INFO BAND (Party | Stock Type | Invoice Type | Date)
			// ══════════════════════════════════════════════════════════════════
			float[] bandW = { 33f, 33f, 33f };
			Table bandTable = new Table(UnitValue.createPercentArray(bandW));
			bandTable.setWidth(UnitValue.createPercentValue(100));

			addBandCell(bandTable, "PARTY NAME", nvl(po.getPartyName()), lightBlueBg, labelGray, boldFont, regularFont,
					borderGray);
			addBandCell(bandTable, "STOCK TYPE", nvl(po.getStockTypeName()), lightBlueBg, labelGray, boldFont,
					regularFont, borderGray);
			addBandCell(bandTable, "ISSUE DATE", nvl(po.getPodate()), lightBlueBg, labelGray, boldFont, regularFont,
					borderGray);

			document.add(bandTable);
			document.add(new Paragraph("").setMarginBottom(12));

			// ══════════════════════════════════════════════════════════════════
			// SECTION 3 — ITEMS TABLE
			// ══════════════════════════════════════════════════════════════════
			// # | Item Name | Category | Unit | Issued Qty | Returned Qty | Remaining Qty
			float[] colW = { 4f, 38f, 28f, 14f, 16f };
			Table itemTable = new Table(UnitValue.createPercentArray(colW));
			itemTable.setWidth(UnitValue.createPercentValue(100));

			String[] headers = { "#", "Item Name", "Category", "Qty", "Unit" };
			TextAlignment[] aligns = { TextAlignment.CENTER, TextAlignment.LEFT, TextAlignment.LEFT,
					TextAlignment.RIGHT, TextAlignment.CENTER };

			for (int i = 0; i < headers.length; i++) {
				itemTable.addHeaderCell(new Cell()
						.add(new Paragraph(headers[i]).setFont(boldFont).setFontSize(9)
								.setFontColor(ColorConstants.WHITE))
						.setBackgroundColor(tableBlue).setTextAlignment(aligns[i]).setPaddingTop(7).setPaddingBottom(7)
						.setBorder(Border.NO_BORDER));
			}

			// ── Totals accumulators ───────────────────────────────────────────
			double totalIssuedQty = 0;
			double totalReturnedQty = 0;

			boolean alternate = false;
			int srNo = 1;

			for (PurchaseOrderStoreDetailResponseDto d : po.getDetails()) {
				Color bg = alternate ? altRow : ColorConstants.WHITE;
				alternate = !alternate;

				double issuedQty = d.getQty();
				double returnedQty = d.getReturnedQty();
				double remainingQty = d.getRemainingQty();

				totalIssuedQty += issuedQty;
				totalReturnedQty += returnedQty;

				itemTable.addCell(siCell(String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER));
				itemTable.addCell(siCell(nvl(d.getRawMaterialName()), bg, regularFont, TextAlignment.LEFT));
				itemTable.addCell(siCell(nvl(d.getRawMaterialCatName()), bg, regularFont, TextAlignment.LEFT));
				itemTable.addCell(siCell(fmtQty(issuedQty), bg, boldFont, TextAlignment.RIGHT));
				itemTable.addCell(siCell(nvl(d.getUnitName()), bg, regularFont, TextAlignment.CENTER));
//                itemTable.addCell(siCell(fmtQty(returnedQty),                  bg, regularFont, TextAlignment.RIGHT));
//                itemTable.addCell(siCell(fmtQty(remainingQty),                  bg, regularFont, TextAlignment.RIGHT));
			}

			document.add(itemTable);
			document.add(new Paragraph("").setMarginBottom(12));

			// ══════════════════════════════════════════════════════════════════
			// SECTION 4 — SUMMARY CARDS (Total Items | Total Issued | Total Returned)
			// ══════════════════════════════════════════════════════════════════
//            float[] cardW = {32f, 2f, 32f, 2f, 32f};
//            Table cardTable = new Table(UnitValue.createPercentArray(cardW));
//            cardTable.setWidth(UnitValue.createPercentValue(100));
//
//            cardTable.addCell(buildSummaryCard(
//                    "TOTAL ITEMS",
//                    String.valueOf(po.getDetails().size()),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            cardTable.addCell(buildSummaryCard(
//                    "TOTAL ISSUED QTY",
//                    fmtQty(totalIssuedQty),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            cardTable.addCell(buildSummaryCard(
//                    "TOTAL RETURNED QTY",
//                    fmtQty(totalReturnedQty),
//                    cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray));
//
//            document.add(cardTable);

			// ══════════════════════════════════════════════════════════════════
			// SECTION 5 — REMARKS
			// ══════════════════════════════════════════════════════════════════
			if (po.getRemarks() != null && !po.getRemarks().trim().isEmpty()) {
				document.add(new Paragraph("").setMarginBottom(10));
				Table rt = new Table(UnitValue.createPercentArray(new float[] { 100f }));
				rt.setWidth(UnitValue.createPercentValue(100));
				rt.addCell(new Cell()
						.add(new Paragraph("REMARKS").setFont(boldFont).setFontSize(8)
								.setFontColor(new DeviceRgb(160, 120, 0)).setMarginBottom(3))
						.add(new Paragraph(po.getRemarks()).setFont(regularFont).setFontSize(9))
						.setBackgroundColor(new DeviceRgb(255, 253, 235))
						.setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(220, 200, 100), 1f))
						.setPadding(10));
				document.add(rt);
			}

			// ══════════════════════════════════════════════════════════════════
			// SECTION 6 — FOOTER
			// ══════════════════════════════════════════════════════════════════
			document.add(new Paragraph("").setMarginBottom(16));

			com.itextpdf.layout.borders.Border topBorder = new com.itextpdf.layout.borders.SolidBorder(borderGray,
					0.5f);

			Table footer = new Table(UnitValue.createPercentArray(new float[] { 60f, 40f }));
			footer.setWidth(UnitValue.createPercentValue(100));

			footer.addCell(new Cell()
					.add(new Paragraph(
							nvl(po.getPocode()) + "  |  Store Issue Document  |  Party: " + nvl(po.getPartyName()))
							.setFont(regularFont).setFontSize(8).setFontColor(labelGray))
					.setBorder(topBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
					.setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

			footer.addCell(new Cell()
					.add(new Paragraph("Total Items: " + po.getDetails().size()).setFont(regularFont).setFontSize(8)
							.setFontColor(labelGray).setTextAlignment(TextAlignment.RIGHT))
					.setBorder(topBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
					.setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

			document.add(footer);
			document.close();

			return baos.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// ── Info band cell ────────────────────────────────────────────────────────
	private void addBandCell(Table table, String label, String value, Color bg, Color labelColor, PdfFont boldFont,
			PdfFont regularFont, Color borderGray) {

		com.itextpdf.layout.borders.Border bdr = new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f);

		table.addCell(new Cell()
				.add(new Paragraph(label).setFont(regularFont).setFontSize(7).setFontColor(labelColor)
						.setMarginBottom(3))
				.add(new Paragraph(value).setFont(boldFont).setFontSize(11).setFontColor(ColorConstants.BLACK))
				.setBackgroundColor(bg).setBorder(bdr).setPadding(10));
	}

	// ── Summary card (Total Items / Issued Qty / Returned Qty) ───────────────
//    private Cell buildSummaryCard(String label, String value,
//            Color cardBg, Color darkBlue, Color labelGray,
//            PdfFont boldFont, PdfFont regularFont, Color borderGray) {
//
//        com.itextpdf.layout.borders.Border bdr =
//                new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.8f);
//
//        return new Cell()
//                .add(new Paragraph(label)
//                        .setFont(regularFont).setFontSize(7)
//                        .setFontColor(labelGray).setMarginBottom(4))
//                .add(new Paragraph(value)
//                        .setFont(boldFont).setFontSize(20)
//                        .setFontColor(darkBlue))
//                .setBackgroundColor(cardBg)
//                .setBorder(bdr)
//                .setPadding(12);
//    }

	// ── Item table cell ───────────────────────────────────────────────────────
	private Cell siCell(String text, Color bg, PdfFont font, TextAlignment align) {
		return new Cell().add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(9))
				.setBackgroundColor(bg).setTextAlignment(align)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(220, 228, 235), 0.3f))
				.setPaddingTop(5).setPaddingBottom(5);
	}

	// ── Qty formatter (removes trailing .0 for whole numbers) ────────────────
	private String fmtQty(double val) {
		if (val == Math.floor(val) && !Double.isInfinite(val)) {
			return String.valueOf((long) val);
		}
		return String.format("%.3f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
	}

	private String nvl(String val) {
		return (val != null && !val.isEmpty()) ? val : "\u2014"; // em dash for empty
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
	public void deleteByPoId(Long poId) {
		PurchaseOrderStoreEntity entity = poRepository.findById(poId)
				.orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + poId));

		stockLedgerEntryService.reverseStoreIssue(poId);

		entity.setIsDelete(true);
		poRepository.save(entity);
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

	public ChefRequisitionResponseDto getByCrcode(String crcode, Long userId) {

		ChefRequisitionEntity entity = crRepository.findByCrcodeAndUserIdAndIsDeleteFalse(crcode, userId)
				.orElseThrow(() -> new RuntimeException("Chef Requisition not found with crcode: " + crcode));

		return convertCrToDto(entity);
	}

	// ── CR converter ──────────────────────────────────────────────────────────
	private ChefRequisitionResponseDto convertCrToDto(ChefRequisitionEntity entity) {

		ChefRequisitionResponseDto dto = new ChefRequisitionResponseDto();
		dto.setId(entity.getId());
		dto.setCrcode(entity.getCrcode());
		dto.setPartyId(entity.getParty() != null ? entity.getParty().getId() : null);
		dto.setPartyName(entity.getParty() != null ? entity.getParty().getNameEnglish() : null);
		dto.setVoucher(entity.getVoucher());
		dto.setCrdate(entity.getCrdate() != null ? entity.getCrdate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				: null);
		dto.setInvoicetype(entity.getInvoicetype());
		dto.setRemarks(entity.getRemarks());
		dto.setStatus(entity.getStatus());
		dto.setCreatedAt(entity.getCreatedAt().toString());
		dto.setUserId(entity.getUser().getId());

		if (entity.getStocktype() != null) {
			dto.setStockTypeId(entity.getStocktype().getId());
			dto.setStockTypeName(entity.getStocktype().getNameEnglish());
		}

		List<ChefRequisitionDetailResponseDto> detailDtos = crDetailRepository.findByChefRequisitionId(entity.getId())
				.stream().map(d -> {
					ChefRequisitionDetailResponseDto rd = new ChefRequisitionDetailResponseDto();
					rd.setId(d.getId());
					if (d.getRawMaterial() != null) {
						rd.setRawMaterialId(d.getRawMaterial().getId());
						rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
						rd.setRawMaterialNameGujarati(d.getRawMaterial().getNameGujarati());
						rd.setRawMaterialNameHindi(d.getRawMaterial().getNameHindi());
					}
					if (d.getRawMaterialCat() != null) {
						rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
						rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
						rd.setRawMaterialCatNameGujarati(d.getRawMaterial().getNameGujarati());
						rd.setRawMaterialCatNameHindi(d.getRawMaterial().getNameHindi());
					}
					if (d.getUnit() != null) {
						rd.setUnitId(d.getUnit().getId());
						rd.setUnitName(d.getUnit().getNameEnglish());
						rd.setUnitNameHindi(d.getUnit().getNameHindi());
						rd.setUnitNameGujarati(d.getUnit().getNameGujarati());
					}
					rd.setQty(d.getQty());
					return rd;
				}).collect(Collectors.toList());

		dto.setDetails(detailDtos);
		return dto;
	}

	@Override
	public byte[] generateExcelReport(Long poId, Long userId) {

		try {

			PurchaseOrderStoreResponseDto po = getByPoId(poId);

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Store Issue");

			int rowNum = 0;

			Font boldFont = workbook.createFont();
			boldFont.setBold(true);

			CellStyle titleStyle = workbook.createCellStyle();
			titleStyle.setFont(boldFont);
			titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(boldFont);
			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Font whiteFont = workbook.createFont();
			whiteFont.setBold(true);
			whiteFont.setColor(IndexedColors.WHITE.getIndex());

			headerStyle.setFont(whiteFont);

			// TITLE

			Row titleRow = sheet.createRow(rowNum++);

			org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);

			titleCell.setCellValue("STORE ISSUE REPORT");
			titleCell.setCellStyle(titleStyle);

			sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

			rowNum++;

			// INFO

			Row infoRow1 = sheet.createRow(rowNum++);
			infoRow1.createCell(0).setCellValue("PO Code");
			infoRow1.createCell(1).setCellValue(po.getPocode());

			infoRow1.createCell(2).setCellValue("Party");
			infoRow1.createCell(3).setCellValue(po.getPartyName());

			Row infoRow2 = sheet.createRow(rowNum++);
			infoRow2.createCell(0).setCellValue("Issue Date");
			infoRow2.createCell(1).setCellValue(po.getPodate());

			infoRow2.createCell(2).setCellValue("Stock Type");
			infoRow2.createCell(3).setCellValue(po.getStockTypeName());

			rowNum++;

			// HEADER

			String[] headers = { "Sr No", "Item Name", "Category", "Qty", "Unit" };

			Row headerRow = sheet.createRow(rowNum++);

			for (int i = 0; i < headers.length; i++) {

				org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);

				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// DATA

			int sr = 1;

			for (PurchaseOrderStoreDetailResponseDto d : po.getDetails()) {

				Row row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(sr++);
				row.createCell(1).setCellValue(nvl(d.getRawMaterialName()));
				row.createCell(2).setCellValue(nvl(d.getRawMaterialCatName()));
				row.createCell(3).setCellValue(d.getQty());
				row.createCell(4).setCellValue(nvl(d.getUnitName()));
			}

			// AUTO SIZE

			for (int i = 0; i < 5; i++) {
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

	public Double calculateClosingStock(RawMaterialMasterEntity rm) {
		try {
			LocalDate from = LocalDate.of(2000, 1, 1);
			LocalDate to = LocalDate.now();
			Long rmId = rm.getId();

			// ── Resolve unit conversion ───────────────────────────────────
			boolean hasParentUnit = false;
			UnitMasterEntity unitForConv = null;

			if (rm.getUnit() != null) {
				Optional<UnitMasterEntity> rmUnitOp = unitMasterRepository
						.findByIdAndIsParentUnitFalse(rm.getUnit().getId());

				if (rmUnitOp.isPresent() && rmUnitOp.get().getParentUnit() != null
						&& rmUnitOp.get().getEquivalentValue() != null && rmUnitOp.get().getEquivalentValue() != 0.0) {
					hasParentUnit = true;
					unitForConv = rmUnitOp.get();
				}
			}

			// ── OPB ───────────────────────────────────────────────────────
			double opb = rm.getOpbStock() != null ? rm.getOpbStock().doubleValue() : 0.0;
			if (hasParentUnit)
				opb = convertStockQty(unitForConv, opb);

			// ── Purchase ──────────────────────────────────────────────────
			double purchase = stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to,
					"PURCHASE");
			if (hasParentUnit)
				purchase = convertStockQty(unitForConv, purchase);

			// ── Purchase Return ───────────────────────────────────────────
			double purchaseReturn = stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to,
					"PURCHASE_RETURN");
			if (hasParentUnit)
				purchaseReturn = convertStockQty(unitForConv, purchaseReturn);

			// ── Sell ──────────────────────────────────────────────────────
			double sell = stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to,
					"STORE_ISSUE")
					+ stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to,
							"CHEF_REQUISITION")
					+ stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to,
							"SOT_STORE_ISSUE");
			if (hasParentUnit)
				sell = convertStockQty(unitForConv, sell);

			// ── Sell Return ───────────────────────────────────────────────
			double sellReturn = stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to,
					"STORE_ISSUE_RETURN")
					+ stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to,
							"SOT_STORE_RETURN");
			if (hasParentUnit)
				sellReturn = convertStockQty(unitForConv, sellReturn);

			// ── Final ─────────────────────────────────────────────────────
			double closing = opb + purchase - purchaseReturn - sell + sellReturn;
			return BigDecimal.valueOf(closing).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();

		} catch (Exception e) {
			return 0.0;
		}
	}

	private double convertStockQty(UnitMasterEntity unit, double qty) {
		if (qty == 0.0)
			return 0.0;
		return BigDecimal.valueOf(qty)
				.divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, java.math.RoundingMode.HALF_UP).doubleValue();
	}

	@Override
	public List<PriceItemResponseDto> getPricesForPo(Long poId, Long userId) {

		poRepository.findById(poId).orElseThrow(() -> new RuntimeException("Store PO not found with id: " + poId));

		List<PurchaseOrderStoreDetailEntity> details = detailRepository.findByPoId(poId);

		List<PriceItemResponseDto> result = new ArrayList<>();

		for (PurchaseOrderStoreDetailEntity detail : details) {

			RawMaterialMasterEntity rm = detail.getRawMaterial();

			if (rm == null) {
				continue;
			}

			PriceItemResponseDto dto = new PriceItemResponseDto();

			dto.setRawMaterialId(rm.getId());
			dto.setRawMaterialName(rm.getNameEnglish());
			dto.setQty(detail.getQty());

			if (detail.getUnit() != null) {

				dto.setUnitId(detail.getUnit().getId());
				dto.setUnitName(detail.getUnit().getNameEnglish());
			}

			// ── 1. Master Price ── supplier_rate from rawmaterial table
			dto.setMasterPrice(rm.getSupplierRate() != null ? rm.getSupplierRate().doubleValue() : 0.0);

			// ── 2. Last Price ── user-wise latest purchase price
			Double lastPrice = purchaseOrderDetailRepository.findLatestPriceByRawMaterialIdAndUserId(rm.getId(),
					userId);

			dto.setLastPrice(lastPrice != null ? lastPrice : 0.0);

			// ── 3. Avg Price ── user-wise average purchase price
			Double sumPurchase = purchaseOrderDetailRepository.sumPriceByRawMaterialIdAndUserId(rm.getId(), userId);

			Long purchaseCount = purchaseOrderDetailRepository
					.countByRawMaterialIdAndUserIdAndPriceGreaterThanZero(rm.getId(), userId);

			double masterP = dto.getMasterPrice();

			double totalSum = (sumPurchase != null ? sumPurchase : 0.0) + masterP;

			long totalCount = (purchaseCount != null ? purchaseCount : 0L) + 1;

			double avg = totalCount > 0 ? totalSum / totalCount : masterP;

			dto.setAvgPrice(BigDecimal.valueOf(avg).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue());

			result.add(dto);
		}

		return result;
	}

	@Override
	public byte[] generatePdfReportWithPrice(PdfWithPriceRequestDto request, Long userId) {

		try {

			PurchaseOrderStoreResponseDto po = getByPoId(request.getPoId());

			if (po == null) {
				throw new RuntimeException("Store Issue not found with ID: " + request.getPoId());
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);

			Document document = new Document(pdf, PageSize.A4);

			document.setMargins(30, 30, 30, 30);

			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

			Color darkBlue = new DeviceRgb(13, 71, 116);
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color tableBlue = new DeviceRgb(26, 99, 153);
			Color lightBlueBg = new DeviceRgb(235, 245, 255);
			Color borderGray = new DeviceRgb(200, 210, 220);
			Color labelGray = new DeviceRgb(100, 120, 140);
			Color altRow = new DeviceRgb(245, 248, 252);

			// ─────────────────────────────────────────────────────────────
			// COMPANY DETAILS
			// ─────────────────────────────────────────────────────────────

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId);

			if (request.getIsCompanyDetails() != null && request.getIsCompanyDetails() == 1) {

				float[] companyWidths = { 20f, 2f, 78f };

				Table headerTable = new Table(UnitValue.createPercentArray(companyWidths));

				headerTable.setWidth(UnitValue.createPercentValue(100));

				try {

					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());

					Image logo = new Image(logoData);

					logo.setWidth(100f);

					headerTable.addCell(new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER));

				} catch (Exception e) {

					headerTable.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
				}

				headerTable.addCell(new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont)
						.setFontSize(14).setFontColor(blackColor)).setBorder(Border.NO_BORDER));

				headerTable.addCell(new Cell()
						.add(new Paragraph("Phone : " + nvl(cmpDto.getOfficeNo())).setFont(regularFont).setFontSize(12))
						.setBorder(Border.NO_BORDER));

				headerTable.addCell(new Cell().add(
						new Paragraph("Email : " + nvl(cmpDto.getCompanyEmail())).setFont(regularFont).setFontSize(12))
						.setBorder(Border.NO_BORDER));

				document.add(headerTable);

				document.add(new Paragraph(""));
			}

			// ─────────────────────────────────────────────────────────────
			// PRICE LABEL
			// ─────────────────────────────────────────────────────────────

			String priceLabel = "";

			if ("MASTER".equalsIgnoreCase(request.getPriceType())) {
				priceLabel = "Master Price";
			} else if ("LAST".equalsIgnoreCase(request.getPriceType())) {
				priceLabel = "Last Price";
			} else if ("AVG".equalsIgnoreCase(request.getPriceType())) {
				priceLabel = "Average Price";
			}

			// ─────────────────────────────────────────────────────────────
			// HEADER
			// ─────────────────────────────────────────────────────────────

			document.add(new Paragraph("Store Issue Report").setFont(boldFont).setFontSize(22).setFontColor(darkBlue));

			Cell codeCell = new Cell()
					.add(new Paragraph(nvl(po.getPocode())).setFont(boldFont).setFontSize(14).setFontColor(darkBlue)
							.setTextAlignment(TextAlignment.LEFT).setMarginBottom(2))
					.setBorder(Border.NO_BORDER).setPaddingBottom(6);
			document.add(codeCell);

			document.add(new Paragraph(""));

			// =========================================================
			// TABLE WIDTHS
			// =========================================================

			float[] colW;

			String[] headers;

			if (request.getIsRate() != null && request.getIsRate() == 1) {

				colW = new float[] { 4f, 30f, 22f, 10f, 11f, 12f, 11f };

				headers = new String[] { "#", "Item Name", "Category", "Qty", "Unit", "Unit Price", "Amount" };

			} else {

				colW = new float[] { 5f, 40f, 25f, 15f, 15f };

				headers = new String[] { "#", "Item Name", "Category", "Qty", "Unit" };
			}

			Table itemTable = new Table(UnitValue.createPercentArray(colW));

			itemTable.setWidth(UnitValue.createPercentValue(100));

			// =========================================================
			// HEADER
			// =========================================================

			for (String h : headers) {

				itemTable.addHeaderCell(
						new Cell().add(new Paragraph(h).setFont(boldFont).setFontColor(ColorConstants.WHITE))
								.setBackgroundColor(tableBlue).setTextAlignment(TextAlignment.CENTER));
			}

			double grandTotal = 0.0;

			int srNo = 1;

			boolean alternate = false;

			// =========================================================
			// ROWS
			// =========================================================

			for (PurchaseOrderStoreDetailResponseDto d : po.getDetails()) {

				Color bg = alternate ? altRow : ColorConstants.WHITE;

				alternate = !alternate;

				double unitPrice = 0.0;

				// =========================================================
				// GET MASTER PRICE
				// =========================================================

				RawMaterialMasterEntity rm = rawMaterialRepository.findById(d.getRawMaterialId()).orElse(null);

				double masterPrice = 0.0;

				if (rm != null && rm.getSupplierRate() != null) {

					masterPrice = rm.getSupplierRate().doubleValue();
				}

				// =========================================================
				// GET LAST PRICE
				// =========================================================

				Double lp = purchaseOrderDetailRepository.findLatestPriceByRawMaterialIdAndUserId(d.getRawMaterialId(),
						userId);

				double lastPrice = lp != null ? lp : 0.0;

				// =========================================================
				// MASTER PRICE TYPE
				// =========================================================

				if ("masterPrice".equalsIgnoreCase(request.getPriceType())) {

					unitPrice = masterPrice > 0 ? masterPrice : lastPrice;
				}

				// =========================================================
				// LAST PRICE TYPE
				// =========================================================

				else if ("lastPrice".equalsIgnoreCase(request.getPriceType())) {

					unitPrice = lastPrice > 0 ? lastPrice : masterPrice;
				}

				// =========================================================
				// AVG PRICE TYPE
				// =========================================================

				else if ("avgPrice".equalsIgnoreCase(request.getPriceType())) {

					double total = 0.0;

					int count = 0;

					if (masterPrice > 0) {

						total += masterPrice;
						count++;
					}

					if (lastPrice > 0) {

						total += lastPrice;
						count++;
					}

					unitPrice = count > 0 ? total / count : 0.0;
				}

				// =========================================================
				// ROUNDING
				// =========================================================

				unitPrice = BigDecimal.valueOf(unitPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();

				double qty = d.getQty();

				double amount = qty * unitPrice;

				grandTotal += amount;

				// =========================================================
				// COMMON CELLS
				// =========================================================

				itemTable.addCell(siCell(String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER));

				itemTable.addCell(siCell(nvl(d.getRawMaterialName()), bg, regularFont, TextAlignment.LEFT));

				itemTable.addCell(siCell(nvl(d.getRawMaterialCatName()), bg, regularFont, TextAlignment.LEFT));

				itemTable.addCell(siCell(fmtQty(qty), bg, boldFont, TextAlignment.RIGHT));

				itemTable.addCell(siCell(nvl(d.getUnitName()), bg, regularFont, TextAlignment.CENTER));

				// =========================================================
				// RATE COLUMNS
				// =========================================================

				if (request.getIsRate() != null && request.getIsRate() == 1) {

					itemTable.addCell(siCell(fmtPrice(unitPrice), bg, regularFont, TextAlignment.RIGHT));

					itemTable.addCell(siCell(fmtPrice(amount), bg, boldFont, TextAlignment.RIGHT));
				}
			}

			// =========================================================
			// GRAND TOTAL
			// =========================================================

			if (request.getIsRate() != null && request.getIsRate() == 1) {

				itemTable.addCell(new Cell(1, 6)
						.add(new Paragraph("GRAND TOTAL").setFont(boldFont).setFontColor(ColorConstants.WHITE))
						.setBackgroundColor(tableBlue).setTextAlignment(TextAlignment.RIGHT));

				itemTable.addCell(new Cell()
						.add(new Paragraph(fmtPrice(grandTotal)).setFont(boldFont).setFontColor(ColorConstants.WHITE))
						.setBackgroundColor(tableBlue).setTextAlignment(TextAlignment.RIGHT));
			}

			document.add(itemTable);

			document.close();

			return baos.toByteArray();

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException(e);
		}
	}

	// Add this helper alongside your existing fmtQty/nvl helpers:
	private String fmtPrice(Double val) {
		if (val == null)
			return "0.00";
		return String.format("%.2f", val);
	}

	@Override
	public List<EventPartiesResponseDto> getAllPartiesWithEvent(Long userId) {
		return eventMasterRepository.getAllPartiesWithEvent(userId);
	}

	public PurchaseOrderResponseDto getByPocode(String pocode, Long userId) {

		PurchaseOrderEntity entity = purchaseOrderRepository.findByPocodeAndUserIdAndIsDeleteFalse(pocode, userId)
				.orElseThrow(() -> new RuntimeException("Purchase Order not found with code : " + pocode));

		return convertPoToDto(entity);
	}

	private PurchaseOrderResponseDto convertPoToDto(PurchaseOrderEntity entity) {

		PurchaseOrderResponseDto dto = new PurchaseOrderResponseDto();

		dto.setId(entity.getId());
		dto.setPocode(entity.getPocode());

		if (entity.getSupplier() != null) {
			dto.setSupplierId(entity.getSupplier().getId());
			dto.setSupplierName(entity.getSupplier().getNameEnglish());
		}

		dto.setVoucher(entity.getVoucher());

		dto.setBillno(entity.getBillno());
		dto.setInvoicetype(entity.getInvoicetype());
		dto.setRemarks(entity.getRemarks());

		dto.setSubamount(entity.getSubamount());
		dto.setDiscountper(entity.getDiscountper());
		dto.setDiscountval(entity.getDiscountval());
		dto.setAdjustamount(entity.getAdjustamount());
		dto.setFinalamount(entity.getFinalamount());

		dto.setPotype(entity.getPotype());
		dto.setGrnNumber(entity.getGrnNumber());

		if (entity.getStocktype() != null) {
			dto.setStockTypeId(entity.getStocktype().getId());
			dto.setStockTypeName(entity.getStocktype().getNameEnglish());
		}

		List<PurchaseOrderDetailResponseDto> detailDtos = purchaseOrderDetailRepository.findByPoId(entity.getId())
				.stream().map(d -> {

					PurchaseOrderDetailResponseDto rd = new PurchaseOrderDetailResponseDto();

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

					rd.setHsccode(d.getHsccode());

					rd.setQty(d.getQty());
					rd.setPrice(d.getPrice());

					rd.setCgst(d.getCgst());
					rd.setSgst(d.getSgst());
					rd.setIgst(d.getIgst());
					rd.setCess(d.getCess());

					rd.setOthercharge(d.getOthercharge());
					rd.setTotal(d.getTotal());

					return rd;

				}).collect(Collectors.toList());

		dto.setDetails(detailDtos);

		return dto;
	}

	@Override
	public String generateDatewiseStoreIssueReport(String startDate, String endDate, Long userId,
			Integer isCompanyDetails, Integer isWithPrice, String priceType, HttpServletRequest re, Long kitchenTypeId) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;

			menuPreparationServiceImpl.loadLicense();

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			UserBasicDetailsMasterEntity cmpData = rawMaterialReportServiceImpl.getCompanyData(userId);

			if (cmpData == null) {
				return "Company data not found.";
			}

			List<DatewiseStoreIssueDateResponseDto> data = getDatewiseStoreIssueReportData(userId,
					LocalDate.parse(startDate, dateFormatter), LocalDate.parse(endDate, dateFormatter), priceType, kitchenTypeId);

			if (data == null || data.isEmpty()) {
				return "Data not found.";
			}

			// Define colors
			Color blackColor = new DeviceRgb(0, 0, 0);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/storeissuereport/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/Datewise_store_issue_report_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(30, 20, 30, 20);

			String cmpName = cmpData.getCompanyName() != null ? cmpData.getCompanyName() : "";
			String cmpEmail = cmpData.getCompanyEmail() != null ? cmpData.getCompanyEmail() : "";
			String cmpMobileNo = cmpData.getOfficeNo() != null ? cmpData.getOfficeNo() : "";
			String cmpAddress = cmpData.getAddress() != null ? cmpData.getAddress() : "";

			if (isCompanyDetails == 1) {
				Table cmpTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }));
				cmpTable.setWidth(UnitValue.createPercentValue(100f));
				cmpTable.setBorder(Border.NO_BORDER);
				cmpTable.setMarginTop(10f);

				ImageData logoData = null;
				logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpData.getUser().getLogo());
				// logoData =
				// menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

				Image logo = new Image(logoData);

				// Resize & align
				logo.scaleToFit(100, 100);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				Cell cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setBorder(Border.NO_BORDER).setPadding(3f);

				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpMobileNo))
								.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
								.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
				cmpTable.addCell(cell);

				cmpTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
				cmpTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

				document.add(cmpTable);
			}

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			detailTable.setWidth(UnitValue.createPercentValue(100f));
			detailTable.setBorder(Border.NO_BORDER);

			detailTable.addCell(
					new Cell(1, 2).add(new Paragraph("DATEWISE STORE ISSUE REPORT").setFont(boldFont).setFontSize(16f))
							.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

			detailTable.addCell(new Cell().add(new Paragraph("FROM : " + startDate).setFont(boldFont).setFontSize(14f))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

			detailTable.addCell(new Cell().add(new Paragraph("TO : " + endDate).setFont(boldFont).setFontSize(14f))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

			document.add(detailTable);

			float[] columnWidths = isWithPrice == 1 ? new float[] { 3f, 37f, 15f, 15f, 15f, 15f }
					: new float[] { 3f, 67f, 30f, 30f };

			String[] labels = isWithPrice == 1 ? new String[] { "No.", "Item Name", "Qty", "Unit", "Price", "Value" }
					: new String[] { "No.", "Item Name", "Qty", "Unit" };
			
			BigDecimal finalTotal = BigDecimal.ZERO;
			for (DatewiseStoreIssueDateResponseDto d : data) {

				Table purchaseTable = new Table(UnitValue.createPercentArray(columnWidths));
				purchaseTable.setWidth(UnitValue.createPercentValue(100f));
				purchaseTable.setBorder(Border.NO_BORDER);
				purchaseTable.setMarginTop(20f);

				for (String label : labels) {
					purchaseTable.addHeaderCell(new Cell().add(new Paragraph(label).setFont(boldFont).setFontSize(14f)
							.setTextAlignment(TextAlignment.CENTER)));
				}

		
			    Cell categoryCell = isWithPrice == 1
			            ? new Cell(1, 6)
			            : new Cell(1, 4);

			    categoryCell.add(new Paragraph(d.getIssueDate() != null ? "Date : " + d.getIssueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "")
			            .setFont(boldFont)
			            .setFontSize(12f))
			            .setPaddingLeft(5f);

			    purchaseTable.addCell(categoryCell);

			    Integer index = 0;

			    for (DatewiseStoreIssueReportResponseDto cat : d.getCategories()) {

			        Cell dateCell = isWithPrice == 1
			                ? new Cell(1, 6)
			                : new Cell(1, 4);


			        dateCell.add(new Paragraph(cat.getRawMaterialCatName())
			                .setFont(boldFont)
			                .setFontSize(11f)
			                .setTextAlignment(TextAlignment.LEFT)
			                .setPaddingLeft(5f));

			        purchaseTable.addCell(dateCell);

			        for (DatewiseStoreIssueDetailsReportResponseDto details : cat.getDetails()) {

			            if (isWithPrice == 1) {

			                addRow(purchaseTable,
			                        details.getRawMaterialName(),
			                        details.getQty(),
			                        details.getPrice(),
			                        details.getTotal(),
			                        details.getUnitName(),
			                        ++index);

			            } else {

			                addWithoutPriceRow(purchaseTable,
			                        details.getRawMaterialName(),
			                        details.getQty(),
			                        details.getUnitName(),
			                        ++index);
			            }
			        }
			        
			        if (isWithPrice == 1) {

				        purchaseTable.addCell(new Cell(1, 5)
				                .add(new Paragraph("Total")
				                        .setTextAlignment(TextAlignment.RIGHT)
				                        .setFontSize(12f)
				                        .simulateBold()));

				        purchaseTable.addCell(new Cell()
				                .add(new Paragraph(String.valueOf(cat.getTotalAmount().intValue()))
				                        .setTextAlignment(TextAlignment.CENTER)
				                        .setFontSize(12f)
				                        .simulateBold()));
				    }
			        finalTotal = finalTotal.add(cat.getTotalAmount());
			    }
			    
			    document.add(purchaseTable);
			}

			if (isWithPrice == 1) {
				Table finalAmountTable = new Table(UnitValue.createPercentArray(columnWidths));
				finalAmountTable.setWidth(UnitValue.createPercentValue(100f));
				finalAmountTable.setBorder(Border.NO_BORDER);
				finalAmountTable.setMarginTop(10f);
				
				finalAmountTable.addCell(new Cell(1, 5)
			            .add(new Paragraph("Final Total")
			                    .setTextAlignment(TextAlignment.RIGHT)
			                    .setFontSize(12f)
			                    .simulateBold()));

				finalAmountTable.addCell(new Cell()
			            .add(new Paragraph(String.valueOf(finalTotal.intValue()))
			                    .setTextAlignment(TextAlignment.CENTER)
			                    .setFontSize(12f)
			                    .simulateBold()));
				
			    document.add(finalAmountTable);
			}

			/* ================= FOOTER ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			PdfPage page = pdfDocument.getPage(totalPages);
			Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

			canvas.showTextAligned("SIGN __________________________", page.getPageSize().getLeft() + 36, 22,
					TextAlignment.LEFT);

			canvas.showTextAligned(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")),
					page.getPageSize().getRight() - 36, // Right margin
					22, TextAlignment.RIGHT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path")
					+ "/api/download/pdf/storeissuereport/Datewise_store_issue_report_" + datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private void addRow(Table table, String itemName, Double qty, Float price, Float value, String unitName,
			Integer index) {
		Cell cell = new Cell().add(new Paragraph(index.toString())).setTextAlignment(TextAlignment.LEFT)
				.setFontSize(10f).setPaddingLeft(5f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(itemName)).setTextAlignment(TextAlignment.LEFT).setFontSize(10f)
				.setPaddingLeft(5f);
		table.addCell(cell);

		double roundedQty = BigDecimal.valueOf(qty).setScale(2, RoundingMode.HALF_UP).doubleValue();

		cell = new Cell().add(new Paragraph(String.valueOf(roundedQty))).setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(unitName)).setTextAlignment(TextAlignment.CENTER).setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(String.valueOf(price.intValue()))).setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(String.valueOf(value.intValue()))).setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);
	}

	private void addWithoutPriceRow(Table table, String itemName, Double qty, String unitName, Integer index) {
		Cell cell = new Cell().add(new Paragraph(index.toString())).setTextAlignment(TextAlignment.LEFT)
				.setPaddingLeft(5f).setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(itemName)).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5f)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(qty.toString())).setTextAlignment(TextAlignment.CENTER).setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(unitName)).setTextAlignment(TextAlignment.CENTER).setFontSize(10f);
		table.addCell(cell);
	}

	public List<DatewiseStoreIssueDateResponseDto> getDatewiseStoreIssueReportData(Long userId, LocalDate startDate,
			LocalDate endDate, String type, Long kitchenTypeId) {

		Map<LocalDate, DatewiseStoreIssueDateResponseDto> dateMap = new LinkedHashMap<>();

		List<Object[]> list = poRepository.getDatewiseStoreIssueReportData(userId, startDate, endDate, type,
				kitchenTypeId);

		for (Object[] row : list) {

			// ---------------------------------------------------------
			// Issue Date
			// ---------------------------------------------------------
			LocalDate issueDate;

			if (row[0] instanceof java.sql.Date) {

				issueDate = ((java.sql.Date) row[0]).toLocalDate();

			} else if (row[0] instanceof java.sql.Timestamp) {

				issueDate = ((java.sql.Timestamp) row[0]).toLocalDateTime().toLocalDate();

			} else if (row[0] instanceof LocalDate) {

				issueDate = (LocalDate) row[0];

			} else {

				continue;
			}

			// ---------------------------------------------------------
			// Category
			// ---------------------------------------------------------
			Long catId = ((Number) row[2]).longValue();

			String catName = row[3] != null ? (String) row[3] : "";

			// ---------------------------------------------------------
			// Get/Create Date
			// ---------------------------------------------------------
			DatewiseStoreIssueDateResponseDto dateDto = dateMap.computeIfAbsent(issueDate, k -> {

				DatewiseStoreIssueDateResponseDto dto = new DatewiseStoreIssueDateResponseDto();

				dto.setIssueDate(issueDate);
				dto.setCategories(new ArrayList<>());

				return dto;
			});

			// ---------------------------------------------------------
			// Get/Create Category inside Date
			// ---------------------------------------------------------
			DatewiseStoreIssueReportResponseDto categoryDto = null;

			for (DatewiseStoreIssueReportResponseDto existing : dateDto.getCategories()) {

				if (existing.getRawMaterialCatId().equals(catId)) {

					categoryDto = existing;
					break;
				}
			}

			if (categoryDto == null) {

				categoryDto = new DatewiseStoreIssueReportResponseDto();

				categoryDto.setRawMaterialCatId(catId);
				categoryDto.setRawMaterialCatName(catName);
				categoryDto.setDetails(new ArrayList<>());
				categoryDto.setTotalAmount(BigDecimal.ZERO);

				dateDto.getCategories().add(categoryDto);
			}

			// ---------------------------------------------------------
			// Detail
			// ---------------------------------------------------------
			DatewiseStoreIssueDetailsReportResponseDto detail = new DatewiseStoreIssueDetailsReportResponseDto();

			detail.setRawMaterialId(((Number) row[4]).longValue());

			detail.setRawMaterialName(row[5] != null ? (String) row[5] : "");

			detail.setUnitId(((Number) row[6]).longValue());

			detail.setUnitName(row[7] != null ? (String) row[7] : "");

			double qty = row[8] != null ? ((Number) row[8]).doubleValue() : 0.0;

			float price = row[9] != null ? ((Number) row[9]).floatValue() : 0.0f;

			float total = (float) (qty * price);

			detail.setQty(qty);
			detail.setPrice(price);
			detail.setTotal(total);

			categoryDto.getDetails().add(detail);

			// ---------------------------------------------------------
			// Category Total
			// ---------------------------------------------------------
			categoryDto.setTotalAmount(categoryDto.getTotalAmount().add(BigDecimal.valueOf(total)));
		}

		return new ArrayList<>(dateMap.values());
	}

}