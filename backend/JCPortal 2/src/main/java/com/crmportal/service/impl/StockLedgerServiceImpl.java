package com.crmportal.service.impl;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.StockLedgerService;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.time.LocalDate;
@Service
@Transactional
public class StockLedgerServiceImpl implements StockLedgerService {

	@Autowired
	private RawMaterialMasterRepository rawMaterialRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseDetailRepository;

	@Autowired
	private PurchaseOrderReturnDetailRepository purchaseReturnDetailRepository;

	@Autowired
	private PurchaseOrderStoreDetailRepository storeIssueDetailRepository;

	@Autowired
	private StoreIssueReturnDetailRepository storeIssueReturnDetailRepository;

	@Autowired
	private StockLedgerRepository stockLedgerRepository;
	
	@Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;
	
	@Autowired
	Environment environment;
	
	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	private UserMasterRepository userRepository;


	private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Autowired
	private UnitMasterRepository unitMasterRepository;

	@Override
	public StockLedgerResponseDto getStockLedger(Long rawMaterialId, String fromDate, String toDate, Long userId) {

		LocalDate from = LocalDate.parse(fromDate, inputFormatter);
		LocalDate to = LocalDate.parse(toDate, inputFormatter);

		RawMaterialMasterEntity rawMaterial = rawMaterialRepository.findById(rawMaterialId)
				.orElseThrow(() -> new RuntimeException("Raw material not found"));

		//BigDecimal opb = rawMaterial.getOpbStock() != null ? rawMaterial.getOpbStock() : BigDecimal.ZERO;

		LocalDate openingDate = from.minusDays(1);

        double purchaseBefore =
                stockLedgerRepository.sumQtyInByRawMaterialAndDateLessThanAndRefType(
                        rawMaterialId, from, "PURCHASE");
        
        double purchaseReturnBefore =
                stockLedgerRepository.sumQtyOutByRawMaterialAndDateLessThanAndRefType(
                		rawMaterialId, from, "PURCHASE_RETURN");

        double sellBefore =
                stockLedgerRepository.sumSellQtyBeforeDate(
                		rawMaterialId, from);

        double sellReturnBefore =
                stockLedgerRepository.sumSellReturnBeforeDate(
                		rawMaterialId, from);

        double increaseBefore =
                stockLedgerRepository.sumQtyInByRawMaterialAndDateLessThanAndRefType(
                		rawMaterialId, from, "INCREASE");

        double wastageBefore =
                stockLedgerRepository.sumQtyOutByRawMaterialAndDateLessThanAndRefType(
                		rawMaterialId, from, "WASTAGE");
        
        double openingStock = rawMaterial.getOpbStock() != null
                ? rawMaterial.getOpbStock().doubleValue()
                : 0.0;

        double opb = openingStock
                + purchaseBefore
                - purchaseReturnBefore
                - sellBefore
                + sellReturnBefore
                + increaseBefore
                - wastageBefore;
        
        
		String unitName = rawMaterial.getUnit() != null ? rawMaterial.getUnit().getNameEnglish() : "-";

		// ── Convert OPB to parent unit if applicable ──────────────────────────
		Double opbFinal = opb;
		String displayUnitName = unitName;

		if (rawMaterial.getUnit() != null) {
			Optional<UnitMasterEntity> rmUnitOp = unitMasterRepository
					.findByIdAndIsParentUnitFalse(rawMaterial.getUnit().getId());

			if (rmUnitOp.isPresent() && rmUnitOp.get().getParentUnit() != null
					&& rmUnitOp.get().getEquivalentValue() != null && rmUnitOp.get().getEquivalentValue() != 0.0) {

				opbFinal = convertUnitQtyToParentQty(rmUnitOp.get(), opbFinal);
				displayUnitName = rmUnitOp.get().getParentUnit().getNameEnglish();
			}
		}

		// ── OPB Row ───────────────────────────────────────────────────────────
		StockLedgerRowDto opbRow = new StockLedgerRowDto();
		opbRow.setDate(from.format(outputFormatter));
		opbRow.setVno("-");
		opbRow.setSupplierName("OPB");
		opbRow.setBillNo("-");
		opbRow.setPurchase(0);
		opbRow.setPurchaseReturn(0);
		opbRow.setSale(0);
		opbRow.setSaleReturn(0);
		opbRow.setBalance(opbFinal);
		opbRow.setUnitName(displayUnitName);
		opbRow.setTransactionType("OPB");

		// ── Fetch from stock_ledger table ─────────────────────────────────────
		List<StockLedgerEntity> ledgerEntries = stockLedgerRepository.findByRawMaterialAndDateRange(rawMaterialId, from,
				to);

		List<StockLedgerRowDto> transactionRows = new ArrayList<>();

		for (StockLedgerEntity entry : ledgerEntries) {

			// ── Unit conversion per row ───────────────────────────────────────
			Double finalQtyIn = entry.getQtyIn();
			Double finalQtyOut = entry.getQtyOut();
			String finalUnitName = entry.getUnit() != null ? entry.getUnit().getNameEnglish() : displayUnitName;

			if (entry.getUnit() != null) {
				Optional<UnitMasterEntity> unitOp = unitMasterRepository
						.findByIdAndIsParentUnitFalse(entry.getUnit().getId());

				if (unitOp.isPresent() && unitOp.get().getParentUnit() != null
						&& unitOp.get().getEquivalentValue() != null && unitOp.get().getEquivalentValue() != 0.0) {

					if (finalQtyIn > 0)
						finalQtyIn = convertUnitQtyToParentQty(unitOp.get(), finalQtyIn);
					if (finalQtyOut > 0)
						finalQtyOut = convertUnitQtyToParentQty(unitOp.get(), finalQtyOut);
					finalUnitName = unitOp.get().getParentUnit().getNameEnglish();
				}
			}
			// ─────────────────────────────────────────────────────────────────

			StockLedgerRowDto row = new StockLedgerRowDto();
			row.setDate(entry.getTransactionDate().format(outputFormatter));
			row.setVno(entry.getVoucher() != null ? entry.getVoucher() : "-");
			row.setSupplierName(entry.getParty() != null ? entry.getParty().getNameEnglish() : "-");
			row.setBillNo(entry.getBillNo() != null ? entry.getBillNo() : "-");
			row.setTransactionType(entry.getRefType());
			row.setUnitName(finalUnitName);

			System.out.println("Type:-" + entry.getRefType());
			switch (entry.getRefType()) {
			case "PURCHASE":
				row.setPurchase(finalQtyIn);
				row.setPurchaseReturn(0);
				row.setSale(0);
				row.setSaleReturn(0);
				row.setWastage(0);
				row.setIncrease(0);
				break;
			case "PURCHASE_RETURN":
				row.setPurchase(0);
				row.setPurchaseReturn(finalQtyOut);
				row.setSale(0);
				row.setSaleReturn(0);
				row.setWastage(0);
				row.setIncrease(0);
				break;
			case "STORE_ISSUE":
			case "CHEF_REQUISITION":
			case "SOT_STORE_ISSUE":
				row.setPurchase(0);
				row.setPurchaseReturn(0);
				row.setSale(finalQtyOut);
				row.setSaleReturn(0);
				row.setWastage(0);
				row.setIncrease(0);
				break;
			case "STORE_ISSUE_RETURN":
			case "SOT_STORE_RETURN":
				row.setPurchase(0);
				row.setPurchaseReturn(0);
				row.setSale(0);
				row.setSaleReturn(finalQtyIn);
				row.setWastage(0);
				row.setIncrease(0);
				break;
			case "INCREASE":
				row.setPurchase(0);
				row.setPurchaseReturn(0);
				row.setSale(0);
				row.setSaleReturn(0);
				row.setWastage(0);
				System.out.println("finalQtyIn:- "+finalQtyIn);
				row.setIncrease(finalQtyIn);
				break;
			case "WASTAGE":
				row.setPurchase(0);
				row.setPurchaseReturn(0);
				row.setSale(0);
				row.setSaleReturn(0);
				System.out.println("finalQtyOut:- "+finalQtyOut);
				row.setWastage(finalQtyOut);
				row.setIncrease(0);
				break;
			default:
				row.setPurchase(0);
				row.setPurchaseReturn(0);
				row.setSale(0);
				row.setSaleReturn(0);
				row.setWastage(0);
				row.setIncrease(0);
				break;
			}

			row.setBalance(0);
			transactionRows.add(row);
		}
		// ── Recalculate balance starting from converted OPB ───────────────────
		double balance = opbFinal;
		for (StockLedgerRowDto row : transactionRows) {
			balance += row.getPurchase();
			balance -= row.getPurchaseReturn();
			balance -= row.getSale();
			balance += row.getSaleReturn();
			balance += row.getIncrease();
			balance -= row.getWastage();
			balance = BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
			row.setBalance(balance);
		}

		List<StockLedgerRowDto> finalRows = new ArrayList<>();
		finalRows.add(opbRow);
		finalRows.addAll(transactionRows);

		// ── Build Response ────────────────────────────────────────────────────
		StockLedgerResponseDto response = new StockLedgerResponseDto();
		response.setItemName(rawMaterial.getNameEnglish());
		response.setUnitName(displayUnitName);
		response.setFromDate(fromDate);
		response.setToDate(toDate);
		response.setOpb(BigDecimal.valueOf(opbFinal));
		response.setSupplierRate(
				rawMaterial.getSupplierRate() != null ? rawMaterial.getSupplierRate() : BigDecimal.ZERO);
		response.setClosingStock(BigDecimal.valueOf(balance));
		response.setRows(finalRows);

		return response;
	}
	
	@Override
	public byte[] generatePdfReport(Long rawMaterialId, String fromDate, String toDate, Long userId, Integer isCompanyDetails) {
	    try {
	        StockLedgerResponseDto data = getStockLedger(rawMaterialId, fromDate, toDate, userId);

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(baos);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf, PageSize.A4.rotate());
	        document.setMargins(30, 30, 30, 30);

	        PdfFont boldFont    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

	        // ── Colors ────────────────────────────────────────────────────────
	        Color darkBlue   = new DeviceRgb(13,  71,  116);
	        Color blackColor   = new DeviceRgb(0, 0, 0);
	        Color tableBlue  = new DeviceRgb(26,  99,  153);
	        Color borderGray = new DeviceRgb(200, 210, 220);
	        Color labelGray  = new DeviceRgb(100, 120, 140);
	        Color altRow     = new DeviceRgb(245, 248, 252);
	        Color cardBg     = new DeviceRgb(245, 248, 252);
	        Color redColor   = new DeviceRgb(220, 50,  50);

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
	                logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
	                headerTable.addCell(new Cell(3, 2).add(logo)
	                        .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
	                        .setBorder(Border.NO_BORDER).setPaddingBottom(10f));
	            } catch (Exception e) {
	                headerTable.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
	            }

	            headerTable.addCell(new Cell()
	                    .add(new Paragraph(cmpDto.getCompanyName())
	                            .setFont(boldFont).setFontSize(14).setFontColor(blackColor))
	                    .setBorder(Border.NO_BORDER).setPaddingLeft(15f));

	            headerTable.addCell(new Cell()
	                    .add(new Paragraph()
	                            .add(new Text("Phone : ").setFont(boldFont).setFontSize(11).setFontColor(blackColor))
	                            .add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getOfficeNo() : "")
	                                    .setFont(regularFont).setFontSize(11).setFontColor(blackColor)))
	                    .setBorder(Border.NO_BORDER).setPaddingLeft(15f).setPaddingBottom(6f));
	            
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

	            // divider
	            Table div = new Table(UnitValue.createPercentArray(new float[]{100f}));
	            div.setWidth(UnitValue.createPercentValue(100));
	            div.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
	            document.add(div);
	            document.add(new Paragraph("").setMarginBottom(8));
	        }

	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 1 — TITLE ROW  (title left, printed-on right)
	        // ══════════════════════════════════════════════════════════════════
	        String printedOn = "Printed on: " +
	                java.time.LocalDate.now()
	                        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) +
	                " at " +
	                java.time.LocalTime.now()
	                        .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

	        float[] topW = {60f, 40f};
	        Table topTable = new Table(UnitValue.createPercentArray(topW));
	        topTable.setWidth(UnitValue.createPercentValue(100));

	        topTable.addCell(new Cell()
	                .add(new Paragraph("General Stock Ledger")
	                        .setFont(boldFont).setFontSize(18).setFontColor(darkBlue).setMarginBottom(2))
	                .add(new Paragraph(printedOn)
	                        .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
	                .setBorder(Border.NO_BORDER).setPaddingBottom(4));

	        topTable.addCell(new Cell()
	                .setBorder(Border.NO_BORDER)); // empty right

	        document.add(topTable);

	        // ── thin divider ──────────────────────────────────────────────────
	        Table thinDiv = new Table(UnitValue.createPercentArray(new float[]{100f}));
	        thinDiv.setWidth(UnitValue.createPercentValue(100));
	        thinDiv.addCell(new Cell().setHeight(1.5f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
	        document.add(thinDiv);
	        document.add(new Paragraph("").setMarginBottom(10));

	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 2 — ITEM INFO BAND  (Item Name | Unit | From Date | To Date)
	        // ══════════════════════════════════════════════════════════════════
	        // Parse dates for display  e.g. "01 Apr 2026"
	        DateTimeFormatter inFmt  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

	        String fromDisplay = (fromDate != null && !fromDate.isEmpty())
	                ? LocalDate.parse(fromDate, inFmt).format(outFmt) : "-";
	        String toDisplay   = (toDate   != null && !toDate.isEmpty())
	                ? LocalDate.parse(toDate,   inFmt).format(outFmt) : "-";

	        float[] infoW = {30f, 12f, 20f, 20f};  // Item Name | Unit | From | To
	        Table infoTable = new Table(UnitValue.createPercentArray(infoW));
	        infoTable.setWidth(UnitValue.createPercentValue(100));

	        addInfoBandCell(infoTable, "ITEM NAME",  data.getItemName(),   darkBlue, labelGray, boldFont, regularFont, borderGray);
	        addInfoBandCell(infoTable, "UNIT",       data.getUnitName(),   darkBlue, labelGray, boldFont, regularFont, borderGray);
	        addInfoBandCell(infoTable, "FROM DATE",  fromDisplay,          darkBlue, labelGray, boldFont, regularFont, borderGray);
	        addInfoBandCell(infoTable, "TO DATE",    toDisplay,            darkBlue, labelGray, boldFont, regularFont, borderGray);

	        document.add(infoTable);
	        document.add(new Paragraph("").setMarginBottom(10));

	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 3 — OPB & CLOSING STOCK CARDS  (side by side)
	        // ══════════════════════════════════════════════════════════════════
	        double opbVal     = data.getOpb()          != null ? data.getOpb().doubleValue()          : 0;
	        double closingVal = data.getClosingStock()  != null ? data.getClosingStock().doubleValue()  : 0;

	        float[] cardW = {49f, 2f, 49f};
	        Table cardTable = new Table(UnitValue.createPercentArray(cardW));
	        cardTable.setWidth(UnitValue.createPercentValue(100));

	        // OPB card
	        cardTable.addCell(buildStockCard(
	                "OPENING BALANCE (OPB)", opbVal, data.getUnitName(),
	                cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray, false));

	        // spacer
	        cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));

	        // Closing card
	        cardTable.addCell(buildStockCard(
	                "FINAL CLOSING STOCK", closingVal, data.getUnitName(),
	                cardBg, darkBlue, labelGray, boldFont, regularFont, borderGray, closingVal < 0));

	        document.add(cardTable);
	        document.add(new Paragraph("").setMarginBottom(10));

	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 4 — LEDGER TABLE
	        // ══════════════════════════════════════════════════════════════════
	        float[] colW = {3f, 9f, 13f, 20f, 9f, 9f, 9f, 8f, 9f, 9f,9f,9f};
	        Table table = new Table(UnitValue.createPercentArray(colW));
	        table.setWidth(UnitValue.createPercentValue(100));

	        String[] headers = {"#", "Date", "V.No", "Supplier Name",
	                            "Bill No", "Purchase", "P. Return", "Sale", "S. Return","Increase","Wastage", "Balance"};
	        TextAlignment[] hAligns = {
	            TextAlignment.CENTER, TextAlignment.LEFT, TextAlignment.LEFT, TextAlignment.LEFT,
	            TextAlignment.LEFT,   TextAlignment.RIGHT, TextAlignment.RIGHT,
	            TextAlignment.RIGHT,  TextAlignment.RIGHT,  TextAlignment.RIGHT,TextAlignment.LEFT,TextAlignment.LEFT
	        };

	        for (int i = 0; i < headers.length; i++) {
	            table.addHeaderCell(new Cell()
	                    .add(new Paragraph(headers[i])
	                            .setFont(boldFont).setFontSize(9)
	                            .setFontColor(ColorConstants.WHITE))
	                    .setBackgroundColor(tableBlue)
	                    .setTextAlignment(hAligns[i])
	                    .setPaddingTop(6).setPaddingBottom(6)
	                    .setBorder(Border.NO_BORDER));
	        }

	        int srNo = 1;
	        boolean alternate = false;

	        for (StockLedgerRowDto row : data.getRows()) {
	            boolean isOpb = "OPB".equals(row.getTransactionType());
	            Color bg = alternate ? altRow : ColorConstants.WHITE;
	            if (!isOpb) alternate = !alternate;

	            double balance = row.getBalance();
	            boolean negBal = balance < 0;

	            // Sr No
	            table.addCell(lCell(isOpb ? "" : String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER));
	            table.addCell(lCell(row.getDate(),         bg, regularFont, TextAlignment.LEFT));
	            table.addCell(lCell(row.getVno(),          bg, regularFont, TextAlignment.LEFT));
	            table.addCell(lCell(row.getSupplierName(), bg, regularFont, TextAlignment.LEFT));
	            table.addCell(lCell(row.getBillNo(),       bg, regularFont, TextAlignment.LEFT));

	            // Purchase
	            table.addCell(lCell(
	                    row.getPurchase() > 0 ? fmtD(row.getPurchase()) : "0",
	                    bg, regularFont, TextAlignment.RIGHT));
	            // P. Return
	            table.addCell(lCell(
	                    row.getPurchaseReturn() > 0 ? fmtD(row.getPurchaseReturn()) : "0",
	                    bg, regularFont, TextAlignment.RIGHT));
	            // Sale
	            table.addCell(lCell(
	                    row.getSale() > 0 ? fmtD(row.getSale()) : "0",
	                    bg, regularFont, TextAlignment.RIGHT));
	            // S. Return
	            table.addCell(lCell(
	                    row.getSaleReturn() > 0 ? fmtD(row.getSaleReturn()) : "0",
	                    bg, regularFont, TextAlignment.RIGHT));
	            table.addCell(lCell(
	                    row.getIncrease() > 0 ? fmtD(row.getIncrease()) : "0",
	                    bg, regularFont, TextAlignment.RIGHT));
	            table.addCell(lCell(
	                    row.getWastage() > 0 ? fmtD(row.getWastage()) : "0",
	                    bg, regularFont, TextAlignment.RIGHT));

	            // Balance — blue normally, red if negative, bold if OPB
	            table.addCell(new Cell()
	                    .add(new Paragraph(fmtD(balance))
	                            .setFont(isOpb ? boldFont : boldFont)
	                            .setFontSize(9)
	                            .setFontColor(negBal ? redColor : darkBlue))
	                    .setBackgroundColor(bg)
	                    .setTextAlignment(TextAlignment.RIGHT)
	                    .setBorder(Border.NO_BORDER)
	                    .setPaddingTop(4).setPaddingBottom(4));
	        }

	        document.add(table);

	        // ══════════════════════════════════════════════════════════════════
	        // SECTION 5 — FOOTER
	        // ══════════════════════════════════════════════════════════════════
	        document.add(new Paragraph("").setMarginBottom(16));

	        Table footer = new Table(UnitValue.createPercentArray(new float[]{50f, 50f}));
	        footer.setWidth(UnitValue.createPercentValue(100));

	        com.itextpdf.layout.borders.Border topBorder =
	                new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f);

	        footer.addCell(new Cell()
	                .add(new Paragraph("General Stock Ledger Report")
	                        .setFont(regularFont).setFontSize(8).setFontColor(labelGray))
	                .setBorder(topBorder)
	                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
	                .setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

	        long totalEntries = data.getRows().stream()
	                .filter(r -> !"OPB".equals(r.getTransactionType())).count();

	        footer.addCell(new Cell()
	                .add(new Paragraph("Total entries: " + totalEntries)
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

	// ── PDF cell helpers (mirrors DateWiseStockReportServiceImpl) ─────────────
	private String fmtD(double val) {
	    return String.format("%.2f", val);
	}
	
	// Info band cell (small gray label on top, bold value below)
	private void addInfoBandCell(Table table, String label, String value,
	        Color darkBlue, Color labelGray,
	        PdfFont boldFont, PdfFont regularFont, Color borderGray) {

	    com.itextpdf.layout.borders.Border bdr =
	            new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.5f);

	    table.addCell(new Cell()
	            .add(new Paragraph(label)
	                    .setFont(regularFont).setFontSize(7).setFontColor(labelGray).setMarginBottom(2))
	            .add(new Paragraph(value != null ? value : "-")
	                    .setFont(boldFont).setFontSize(11).setFontColor(darkBlue))
	            .setBorder(Border.NO_BORDER)
	            .setBorderBottom(bdr)
	            .setPaddingBottom(6).setPaddingTop(2));
	}

	// OPB / Closing stock card 
	private Cell buildStockCard(String label, double value, String unit,
	        Color cardBg, Color darkBlue, Color labelGray,
	        PdfFont boldFont, PdfFont regularFont, Color borderGray, boolean isRed) {

	    com.itextpdf.layout.borders.Border bdr =
	            new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.8f);

	    Color valColor = isRed ? new DeviceRgb(220, 50, 50) : darkBlue;

	    Paragraph valuePara = new Paragraph()
	            .add(new Text(fmtD(value)).setFont(boldFont).setFontSize(22).setFontColor(valColor))
	            .add(new Text("  " + (unit != null ? unit : ""))
	                    .setFont(regularFont).setFontSize(10).setFontColor(labelGray));

	    return new Cell()
	            .add(new Paragraph(label)
	                    .setFont(regularFont).setFontSize(7).setFontColor(labelGray).setMarginBottom(4))
	            .add(valuePara)
	            .setBackgroundColor(cardBg)
	            .setBorder(bdr)
	            .setPadding(12);
	}

	// Ledger table cell 
	private Cell lCell(String text, Color bg, PdfFont font, TextAlignment align) {
	    return new Cell()
	            .add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(9))
	            .setBackgroundColor(bg)
	            .setTextAlignment(align)
	            .setBorder(Border.NO_BORDER)
	            .setPaddingTop(4).setPaddingBottom(4);
	}

	// ─ Shared conversion helper
	private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {
		return BigDecimal.valueOf(qty).divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
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
}