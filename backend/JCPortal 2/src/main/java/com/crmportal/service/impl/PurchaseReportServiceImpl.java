package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventTermsAndConditionEntity;
import com.crmportal.entity.EventTermsAndConditionFeaturesEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PurchaseOrderDetailEntity;
import com.crmportal.entity.PurchaseOrderEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.repository.PurchaseOrderDetailRepository;
import com.crmportal.repository.PurchaseOrderRepository;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.DatewisePurchaseReportDataResponseDto;
import com.crmportal.response.dto.DatewisePurchaseReportDateResponseDto;
import com.crmportal.response.dto.DatewisePurchaseReportResponseDto;
import com.crmportal.response.dto.EventFunctionReportResponseDto;
import com.crmportal.response.dto.EventReportResponseDto;
import com.crmportal.response.dto.ExtraChargesResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;
import com.crmportal.response.dto.PurchaseOrderDetailResponseDto;
import com.crmportal.response.dto.PurchaseOrderResponseDto;
import com.crmportal.service.PurchaseOrderService;
import com.crmportal.service.PurchaseReportService;
import com.crmportal.utility.BackgroundEventHandler;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.splitting.ISplitCharacters;

@Service
public class PurchaseReportServiceImpl implements PurchaseReportService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private MenuPreparationServiceImpl menuPreparationServiceImpl;

    @Autowired
    private Environment environment;
    
    @Autowired
    PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    RawMaterialReportServiceImpl rawMaterialReportServiceImpl;
    
    @Override
    public String generatePurchaseOrderReport(Long poId, int lang, Long userId, HttpServletRequest re) {
        try {
            menuPreparationServiceImpl.loadLicense();

            // ── Font Loading ──────────────────────────────────────────────────────────────
            PdfFont basicFont;
            PdfFont boldFont;

            if (lang == 1) {
                // Hindi
                basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
                boldFont  = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
            } else if (lang == 2) {
                // Gujarati
                basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
                boldFont  = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
            } else {
                // English (default)
                basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                boldFont  = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            }

            // ── Fetch Data ────────────────────────────────────────────────────────────────
            PurchaseOrderEntity poEntity = purchaseOrderRepository.findById(poId)
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + poId));

            List<PurchaseOrderResponseDto> poList = purchaseOrderService.getByPoId(poId);
            if (poList == null || poList.isEmpty()) {
                throw new RuntimeException("No Purchase Order data found for ID: " + poId);
            }
            PurchaseOrderResponseDto po = poList.get(0);

            // ── Supplier / Company Info ───────────────────────────────────────────────────
            String supplierName    = safe(po.getSupplierName());
            String billno          = safe(po.getBillno());
            String voucher         = safe(po.getVoucher());
            String remarks         = safe(po.getRemarks());
            String poCode          = safe(po.getPocode());
            String invoiceType     = safe(po.getInvoicetype());

            String poDateStr = po.getPodate() != null
                    ? po.getPodate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    : "";

            // ── Company details via user's company (same pattern as quotation) ────────────
            // Company info is fetched from the user's company profile via MenuPreparationServiceImpl
            // For Purchase reports we load it via the existing common utility
            String companyName    = "";
            String companyAddress = "";
            String companyGst     = "";
            String companyPan     = "";

            try {
                // Reuse existing company-info loader if your project has one,
                // e.g. menuPreparationServiceImpl.getCompanyInfo(userId)
                // Below is a safe fallback — replace with your actual call:
                companyName    = environment.getProperty("app.company.name",    "");
                companyAddress = environment.getProperty("app.company.address", "");
                companyGst     = environment.getProperty("app.company.gst",     "");
                companyPan     = environment.getProperty("app.company.pan",     "");
            } catch (Exception ignored) {}

            // ── Amounts ───────────────────────────────────────────────────────────────────
            float subAmount    = po.getSubamount();
            float discountPer  = po.getDiscountper();
            float discountVal  = po.getDiscountval();
            float adjustAmount = po.getAdjustamount();
            float finalAmount  = po.getFinalamount();

            // ── Colors ────────────────────────────────────────────────────────────────────
            Color headerBg   = new DeviceRgb(51, 51, 51);   // dark gray — matches screenshot
            Color white      = new DeviceRgb(255, 255, 255);
            Color borderClr  = new DeviceRgb(200, 200, 200);
            Color grayText   = new DeviceRgb(80, 80, 80);

            // ── File Setup ────────────────────────────────────────────────────────────────
            String rootPath        = re.getSession().getServletContext().getRealPath("/");
            String safePo          = fileSafe(poCode);
            File   outputDir       = new File(rootPath + "resources/tempDownload/PO/" + safePo);
            
            System.out.println("root path = "+ rootPath);
            System.out.println("safePo = "+ safePo);
            System.out.println("outputDir = "+ outputDir);

            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String fileName = "PurchaseOrder_" + safePo + ".pdf";
            File   pdfFile  = new File(outputDir, fileName);

            // ── Build PDF ────────────────────────────────────────────────────────────────
            PdfWriter   writer      = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document    document    = new Document(pdfDocument);
            document.setMargins(40, 40, 50, 40);

            // ════════════════════════════════════════════════════════════
            // SECTION 1 — Company Header (centered)
            // ════════════════════════════════════════════════════════════
            Paragraph companyNamePara = new Paragraph()
                    .add(new Text(companyName).setFont(boldFont).setFontSize(16))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2f);
            document.add(companyNamePara);

            Paragraph companyAddrPara = new Paragraph()
                    .add(new Text(companyAddress).setFont(basicFont).setFontSize(9).setFontColor(grayText))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(1f);
            document.add(companyAddrPara);

            Paragraph gstPara = new Paragraph()
                    .add(new Text("GST No - " + companyGst).setFont(basicFont).setFontSize(9).setFontColor(grayText))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(1f);
            document.add(gstPara);

            Paragraph panPara = new Paragraph()
                    .add(new Text("PAN No - " + companyPan).setFont(basicFont).setFontSize(9).setFontColor(grayText))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4f);
            document.add(panPara);

            // ════════════════════════════════════════════════════════════
            // SECTION 2 — Supplier Name (centered, bold, large)
            // ════════════════════════════════════════════════════════════
            Paragraph supplierNamePara = new Paragraph()
                    .add(new Text(supplierName).setFont(boldFont).setFontSize(14))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(1f);
            document.add(supplierNamePara);

            // Supplier address / city (if available — extend PurchaseOrderResponseDto if needed)
            // For now we skip if not present; add supplierAddress field to DTO if required
            // document.add(new Paragraph(supplierAddress)...);

            // ── Separator line ────────────────────────────────────────────────────────────
            SolidLine solidLine = new SolidLine(0.5f);
            solidLine.setColor(borderClr);
            LineSeparator separator = new LineSeparator(solidLine);
            separator.setWidth(UnitValue.createPercentValue(100));
            separator.setMarginTop(6f);
            separator.setMarginBottom(8f);
            document.add(separator);

            // ════════════════════════════════════════════════════════════
            // SECTION 3 — Bill No / Date / Voucher  (right-aligned block)
            // ════════════════════════════════════════════════════════════
            float[] metaWidths = {70f, 30f};
            Table metaTable = new Table(UnitValue.createPercentArray(metaWidths));
            metaTable.setWidth(UnitValue.createPercentValue(100));

            // empty left cell
            metaTable.addCell(new Cell().setBorder(Border.NO_BORDER));

            // right cell — Bill No / Date / Voucher
            Table innerMeta = new Table(UnitValue.createPercentArray(new float[]{40f, 60f}));
            innerMeta.setWidth(UnitValue.createPercentValue(100));

            addMetaRow(innerMeta, "Bill No", billno, boldFont, basicFont, grayText);
            addMetaRow(innerMeta, "Date",    poDateStr, boldFont, basicFont, grayText);
            addMetaRow(innerMeta, "Voucher No", voucher, boldFont, basicFont, grayText);

            metaTable.addCell(new Cell().add(innerMeta).setBorder(Border.NO_BORDER));
            document.add(metaTable);

            // ════════════════════════════════════════════════════════════
            // SECTION 4 — NOTE / Remarks
            // ════════════════════════════════════════════════════════════
            if (remarks != null && !remarks.isEmpty()) {
                Paragraph notePara = new Paragraph()
                        .add(new Text("NOTE  ").setFont(boldFont).setFontSize(9))
                        .add(new Text(remarks).setFont(basicFont).setFontSize(9).setFontColor(grayText))
                        .setMarginTop(8f)
                        .setMarginBottom(6f);
                document.add(notePara);
            }

            // ════════════════════════════════════════════════════════════
            // SECTION 5 — Items Table
            // ════════════════════════════════════════════════════════════
            float[] colWidths = {5f, 22f, 10f, 8f, 8f, 10f, 10f, 9f, 9f, 9f};
            Table itemTable = new Table(UnitValue.createPercentArray(colWidths));
            itemTable.setWidth(UnitValue.createPercentValue(100));

            // Header row
            String[] headers = {"#", "Item Name", "SAC", "Qty", "Unit", "Price", "Total", "CGST", "SGST", "IGST"};
            for (String h : headers) {
                Cell hCell = new Cell()
                        .add(new Paragraph().add(new Text(h).setFont(boldFont).setFontSize(9).setFontColor(white)))
                        .setBackgroundColor(headerBg)
                        .setBorder(Border.NO_BORDER)
                        .setPaddingTop(5f)
                        .setPaddingBottom(5f)
                        .setTextAlignment(h.equals("#") || h.equals("Qty") || h.equals("Price")
                                || h.equals("Total") || h.equals("CGST") || h.equals("SGST") || h.equals("IGST")
                                ? TextAlignment.CENTER : TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
                itemTable.addCell(hCell);
            }

            // Data rows
            List<PurchaseOrderDetailResponseDto> details = po.getDetails();
            if (details != null && !details.isEmpty()) {
                int idx = 1;
                for (PurchaseOrderDetailResponseDto d : details) {
                    boolean isEven = idx % 2 == 0;
                    Color rowBg = isEven ? new DeviceRgb(245, 245, 245) : white;

                    addItemCell(itemTable, String.valueOf(idx), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, safe(d.getRawMaterialName()), basicFont, TextAlignment.LEFT, borderClr, rowBg);
                    addItemCell(itemTable, safe(d.getHsccode()), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, formatFloat(d.getQty()), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, safe(d.getUnitName()), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, formatFloat(d.getPrice()), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, formatFloat(d.getTotal()), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, formatFloat(d.getCgst()), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, formatFloat(d.getSgst()), basicFont, TextAlignment.CENTER, borderClr, rowBg);
                    addItemCell(itemTable, formatFloat(d.getIgst()), basicFont, TextAlignment.CENTER, borderClr, rowBg);

                    idx++;
                }
            }

            document.add(itemTable);

            // ── Separator ─────────────────────────────────────────────────────────────────
            LineSeparator sep2 = new LineSeparator(new SolidLine(0.5f));
            sep2.setWidth(UnitValue.createPercentValue(100));
            sep2.setMarginTop(10f);
            sep2.setMarginBottom(6f);
            document.add(sep2);

            // ════════════════════════════════════════════════════════════
            // SECTION 6 — Amount Summary (right-aligned)
            // ════════════════════════════════════════════════════════════
            float[] summaryWidths = {60f, 40f};
            Table summaryTable = new Table(UnitValue.createPercentArray(summaryWidths));
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            // empty left
            summaryTable.addCell(new Cell().setBorder(Border.NO_BORDER));

            Table amtInner = new Table(UnitValue.createPercentArray(new float[]{55f, 45f}));
            amtInner.setWidth(UnitValue.createPercentValue(100));

            addAmountRow(amtInner, "Sub Amount",    formatFloat(subAmount),    basicFont, boldFont, grayText, false);
            if (discountPer > 0 || discountVal > 0) {
                addAmountRow(amtInner, "Discount (" + formatFloat(discountPer) + "%)",
                        "- " + formatFloat(discountVal), basicFont, boldFont, grayText, false);
            }
            if (adjustAmount != 0) {
                addAmountRow(amtInner, "Adjust Amount", formatFloat(adjustAmount), basicFont, boldFont, grayText, false);
            }
            addAmountRow(amtInner, "Final Amount", "Rs. " + formatFloat(finalAmount), basicFont, boldFont, grayText, true);

            summaryTable.addCell(new Cell().add(amtInner).setBorder(Border.NO_BORDER));
            document.add(summaryTable);

            // ════════════════════════════════════════════════════════════
            // SECTION 7 — Authorized Signatory
            // ════════════════════════════════════════════════════════════
            LineSeparator sep3 = new LineSeparator(new SolidLine(0.5f));
            sep3.setWidth(UnitValue.createPercentValue(25));
            sep3.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            sep3.setMarginTop(50f);
            document.add(sep3);

            Paragraph signPara = new Paragraph()
                    .add(new Text("Authorized Signatory").setFont(basicFont).setFontSize(9))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(4f);
            document.add(signPara);

            document.close();

            // ── Return URL ────────────────────────────────────────────────────────────────
            String fullUrl = environment.getProperty("ws_image_path")
                    + "/api/download/pdf/PO/" + safePo + "/" + fileName;

            return fullUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Purchase Order Report Generation Failed: " + e.getMessage(), e);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // Helper Methods
    // ══════════════════════════════════════════════════════════════════

    /** Null-safe string */
    private String safe(String val) {
        return val == null ? "" : val.trim();
    }

    /** Remove chars unsafe for filenames */
    private String fileSafe(String data) {
        return data == null ? "UNKNOWN" : data.replaceAll("[^\\p{L}\\p{N}_\\-]", "_");
    }

    /** Format float to 2 decimal places */
    private String formatFloat(double d) {
        return String.format("%.2f", d);
    }

    /** Format int qty as float string */
    private String formatFloat(int val) {
        return String.format("%.2f", (float) val);
    }

    /** Add a two-column meta row (label | value) */
    private void addMetaRow(Table table, String label, String value,
                             PdfFont boldFont, PdfFont basicFont, Color grayText) {
        table.addCell(new Cell()
                .add(new Paragraph().add(new Text(label).setFont(boldFont).setFontSize(9)))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(2f));
        table.addCell(new Cell()
                .add(new Paragraph().add(new Text(value).setFont(basicFont).setFontSize(9).setFontColor(grayText)))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(2f));
    }

    /** Add a data cell to the items table */
    private void addItemCell(Table table, String text, PdfFont font,
                              TextAlignment align, Color borderClr, Color bgColor) {
        table.addCell(new Cell()
                .add(new Paragraph().add(new Text(text).setFont(font).setFontSize(9)))
                .setTextAlignment(align)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(borderClr, 0.5f))
                .setBackgroundColor(bgColor)
                .setPaddingTop(4f)
                .setPaddingBottom(4f)
                .setPaddingLeft(3f)
                .setPaddingRight(3f));
    }

    /** Add a two-column amount summary row */
    private void addAmountRow(Table table, String label, String value,
                               PdfFont basicFont, PdfFont boldFont, Color grayText, boolean isFinal) {
        PdfFont lFont = isFinal ? boldFont : basicFont;
        PdfFont vFont = isFinal ? boldFont : basicFont;
        float   size  = isFinal ? 10f : 9f;

        table.addCell(new Cell()
                .add(new Paragraph().add(new Text(label + " :").setFont(lFont).setFontSize(size).setFontColor(grayText)))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(3f));
        table.addCell(new Cell()
                .add(new Paragraph().add(new Text(value).setFont(vFont).setFontSize(size)))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(3f));
    }
    
    @Override
    public String generateDatewisePurchaseReport(Long userId, String startDate, String endDate,
    		HttpServletRequest re, Integer isCompanyDetails, Integer isWithPrice) {
    	try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;

			menuPreparationServiceImpl.loadLicense();

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			UserBasicDetailsMasterEntity cmpData = rawMaterialReportServiceImpl.getCompanyData(userId);
			
			if(cmpData == null) {
				return "Company data not found.";
			}
			
			List<DatewisePurchaseReportResponseDto> data = getDatewisePurchaseReportData(userId,
					LocalDate.parse(startDate, dateFormatter), LocalDate.parse(endDate, dateFormatter));
			
			if(data == null || data.isEmpty()) {
				return "Data not found.";
			}
			
			// Define colors
			Color blackColor = new DeviceRgb(0, 0, 0);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/purchasereport/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/Datewise_purchase_report_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(30, 20, 30, 20);

			String cmpName = cmpData.getCompanyName() != null ? cmpData.getCompanyName() : "";
			String cmpEmail = cmpData.getCompanyEmail() != null ? cmpData.getCompanyEmail() : "";
			String cmpMobileNo = cmpData.getOfficeNo() != null ? cmpData.getOfficeNo() : "";
			String cmpAddress = cmpData.getAddress() != null ? cmpData.getAddress() : "";
			
			if(isCompanyDetails == 1) {
				Table cmpTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }));
				cmpTable.setWidth(UnitValue.createPercentValue(100f));
				cmpTable.setBorder(Border.NO_BORDER);
				cmpTable.setMarginTop(10f);
				
				ImageData logoData = null;
				logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpData.getUser().getLogo());
	//			logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
	
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
					new Cell(1, 2).add(new Paragraph("DATEWISE PURCHASE REPORT").setFont(boldFont).setFontSize(16f))
					.setBorder(Border.NO_BORDER)
					.setTextAlignment(TextAlignment.CENTER));
			
			detailTable.addCell(
					new Cell().add(new Paragraph("FROM : " + startDate).setFont(boldFont).setFontSize(14f))
					.setBorder(Border.NO_BORDER)
					.setTextAlignment(TextAlignment.CENTER));
			
			detailTable.addCell(
					new Cell().add(new Paragraph("TO : " + endDate).setFont(boldFont).setFontSize(14f))
					.setBorder(Border.NO_BORDER)
					.setTextAlignment(TextAlignment.CENTER));

			document.add(detailTable);
			
			float[] columnWidths = isWithPrice == 1 ? new float[] { 37f, 9f, 10f, 9f, 9f, 9f, 9f, 9f, 9f }
					: new float[] { 70f, 30f, 30f };
			Table purchaseTable = new Table(UnitValue.createPercentArray(columnWidths));
			purchaseTable.setWidth(UnitValue.createPercentValue(100f));
			purchaseTable.setBorder(Border.NO_BORDER);
			purchaseTable.setMarginTop(10f);

			String[] labels = isWithPrice == 1
					? new String[] { "Item Name", "Qty", "Unit", "Price", "Cgst", "Sgst", "Igst", "Cess", "Value" }
					: new String[] { "Item Name", "Qty", "Unit" };
			
			for(String label : labels) {
				purchaseTable.addHeaderCell(new Cell().add(new Paragraph(label).setFont(boldFont).setFontSize(14f)
						.setTextAlignment(TextAlignment.CENTER)));
			}

			BigDecimal finalTotal = BigDecimal.ZERO;

			for (DatewisePurchaseReportResponseDto supplier : data) {

			    Cell supplierCell = isWithPrice == 1 ? new Cell(1, 9) : new Cell(1, 3);

			    supplierCell.add(new Paragraph(supplier.getSupplierName())
			            .setFontSize(12f).simulateBold()).setPaddingLeft(5f);

			    purchaseTable.addCell(supplierCell);

			    for (DatewisePurchaseReportDateResponseDto dateDto : supplier.getDates()) {

			        Cell dateCell = isWithPrice == 1 ? new Cell(1, 9) : new Cell(1, 3);

			        String dateText = dateDto.getPurchaseDate() != null
			                ? dateDto.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
			                : "";

			        dateCell.add(new Paragraph("Date : " + dateText)
			                .setFont(boldFont).setFontSize(11f)
			                .setTextAlignment(TextAlignment.LEFT));

			        purchaseTable.addCell(dateCell);

			        for (DatewisePurchaseReportDataResponseDto details : dateDto.getDetails()) {

			            if (isWithPrice == 1) {
			                addRow(purchaseTable, details.getRawMaterialName(), details.getQty(),
			                        details.getPrice(), details.getCgst(), details.getSgst(),
			                        details.getIgst(), details.getTotal(), details.getUnitName(),
			                        details.getCess());
			            } else {
			                addWithoutPriceRow(purchaseTable, details.getRawMaterialName(),
			                        details.getQty(), details.getUnitName());
			            }
			        }
			    }

			    if (isWithPrice == 1) {
			        purchaseTable.addCell(new Cell(1, 8)
			                .add(new Paragraph("Total")
			                        .setTextAlignment(TextAlignment.RIGHT)
			                        .setFontSize(12f).simulateBold()));

			        purchaseTable.addCell(new Cell()
			                .add(new Paragraph(String.valueOf(supplier.getTotalAmount().intValue()))
			                        .setTextAlignment(TextAlignment.CENTER)
			                        .setFontSize(12f).simulateBold()));
			    }

			    finalTotal = finalTotal.add(supplier.getTotalAmount());
			}

			if (isWithPrice == 1) {
			    purchaseTable.addCell(new Cell(1, 8)
			            .add(new Paragraph("Final Total")
			                    .setTextAlignment(TextAlignment.RIGHT)
			                    .setFontSize(12f).simulateBold()));

			    purchaseTable.addCell(new Cell()
			            .add(new Paragraph(String.valueOf(finalTotal.intValue()))
			                    .setTextAlignment(TextAlignment.CENTER)
			                    .setFontSize(12f).simulateBold()));
			}

			document.add(purchaseTable);
			
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
					+ "/api/download/pdf/purchasereport/Datewise_purchase_report_" + datetimeforfile + ".pdf";
			
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}

    }
    
	private void addRow(Table table, String itemName, Double qty, Float price, Float cgst, Float sgst,
			Float igst, Float value, String unitName, Float cess) {
		Cell cell = new Cell().add(new Paragraph(itemName))
				.setTextAlignment(TextAlignment.LEFT)
				.setFontSize(10f)
				.setPaddingLeft(5f);
		table.addCell(cell);

		double roundedQty = BigDecimal.valueOf(qty)
		        .setScale(2, RoundingMode.HALF_UP)
		        .doubleValue();
		
		cell = new Cell().add(new Paragraph(String.valueOf(roundedQty)))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);
		
		cell = new Cell().add(new Paragraph(unitName))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(String.valueOf(price.intValue())))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(cgst.toString()))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(sgst.toString()))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(igst.toString()))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);
		
		cell = new Cell().add(new Paragraph(cess.toString()))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(String.valueOf(value.intValue())))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);
	}
	
	private void addWithoutPriceRow(Table table, String itemName, Double qty, String unitName) {
		Cell cell = new Cell().add(new Paragraph(itemName))
				.setTextAlignment(TextAlignment.LEFT)
				.setPaddingLeft(5f)
				.setFontSize(10f);
		table.addCell(cell);

		cell = new Cell().add(new Paragraph(qty.toString()))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);
		
		cell = new Cell().add(new Paragraph(unitName))
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(10f);
		table.addCell(cell);
	}
    
	/*private List<DatewisePurchaseReportResponseDto> getDatewisePurchaseReportData(
	        Long userId,
	        LocalDate startDate,
	        LocalDate endDate) {

	    List<PurchaseOrderDetailEntity> purchaseDetails =
	            purchaseOrderDetailRepository.findPurchaseReportData(userId, startDate, endDate);

	    Map<Long, DatewisePurchaseReportResponseDto> supplierMap = new LinkedHashMap<>();
	    Map<Long, Map<Long, DatewisePurchaseReportDataResponseDto>> supplierRawMaterialMap =
	            new LinkedHashMap<>();

	    for (PurchaseOrderDetailEntity detail : purchaseDetails) {

	        PurchaseOrderEntity po = detail.getPo();
	        PartyMasterEntity supplier = po.getSupplier();

	        // Supplier DTO
	        DatewisePurchaseReportResponseDto supplierDto =
	                supplierMap.computeIfAbsent(supplier.getId(), id -> {

	                    DatewisePurchaseReportResponseDto dto =
	                            new DatewisePurchaseReportResponseDto();

	                    dto.setSupplierId(supplier.getId());
	                    dto.setSupplierName(supplier.getNameEnglish());
	                    dto.setTotalAmount(BigDecimal.ZERO);
	                    dto.setDetails(new ArrayList<>());

	                    return dto;
	                });

	        // Raw material map for supplier
	        Map<Long, DatewisePurchaseReportDataResponseDto> rawMaterialMap =
	                supplierRawMaterialMap.computeIfAbsent(
	                        supplier.getId(),
	                        k -> new LinkedHashMap<>());

	        Long rawMaterialId = detail.getRawMaterial().getId();

	        // Raw material DTO
	        DatewisePurchaseReportDataResponseDto itemDto =
	                rawMaterialMap.computeIfAbsent(rawMaterialId, id -> {

	                    DatewisePurchaseReportDataResponseDto dto =
	                            new DatewisePurchaseReportDataResponseDto();

	                    dto.setRawMaterialId(detail.getRawMaterial().getId());
	                    dto.setRawMaterialName(detail.getRawMaterial().getNameEnglish());

	                    dto.setRawMaterialCatId(detail.getRawMaterialCat().getId());
	                    dto.setRawMaterialCatName(detail.getRawMaterialCat().getNameEnglish());

	                    dto.setUnitId(detail.getUnit().getId());
	                    dto.setUnitName(detail.getUnit().getNameEnglish());

	                    dto.setHsccode(detail.getHsccode());

	                    // These will store GST AMOUNT (₹), not percentage
	                    dto.setCgst(0f);
	                    dto.setSgst(0f);
	                    dto.setIgst(0f);
	                    dto.setCess(0f);

	                    dto.setQty(0d);
	                    dto.setPrice(detail.getPrice()); // Keep first purchase price
	                    dto.setOthercharge(0f);
	                    dto.setTotal(0f);

	                    supplierDto.getDetails().add(dto);

	                    return dto;
	                });

	        // Aggregate quantity
	        itemDto.setQty(itemDto.getQty() + detail.getQty());

	        // Aggregate other charge
	        itemDto.setOthercharge(itemDto.getOthercharge() + detail.getOthercharge());

	        // Aggregate total
	        itemDto.setTotal(itemDto.getTotal() + detail.getTotal());

	        // Calculate GST amount for THIS purchase transaction
	        float taxableAmount = (float) (detail.getQty() * detail.getPrice());

	        float cgstAmount = taxableAmount * detail.getCgst() / 100f;
	        float sgstAmount = taxableAmount * detail.getSgst() / 100f;
	        float igstAmount = taxableAmount * detail.getIgst() / 100f;
	        float cessAmount = taxableAmount * detail.getCess() / 100f;

	        // Add GST amounts
	        itemDto.setCgst(itemDto.getCgst() + cgstAmount);
	        itemDto.setSgst(itemDto.getSgst() + sgstAmount);
	        itemDto.setIgst(itemDto.getIgst() + igstAmount);
	        itemDto.setCess(itemDto.getCess() + cessAmount);

	        // Supplier total
	        supplierDto.setTotalAmount(
	                supplierDto.getTotalAmount().add(BigDecimal.valueOf(detail.getTotal())));
	    }

	    return new ArrayList<>(supplierMap.values());
	}*/
	
	private List<DatewisePurchaseReportResponseDto> getDatewisePurchaseReportData(
	        Long userId,
	        LocalDate startDate,
	        LocalDate endDate) {

	    List<PurchaseOrderDetailEntity> purchaseDetails =
	            purchaseOrderDetailRepository.findPurchaseReportData(
	                    userId, startDate, endDate);

	    Map<Long, DatewisePurchaseReportResponseDto> supplierMap =
	            new LinkedHashMap<>();

	    Map<Long, Map<LocalDate, DatewisePurchaseReportDateResponseDto>> supplierDateMap =
	            new LinkedHashMap<>();

	    for (PurchaseOrderDetailEntity detail : purchaseDetails) {

	        PurchaseOrderEntity po = detail.getPo();
	        PartyMasterEntity supplier = po.getSupplier();

	        Long supplierId = supplier.getId();
	        LocalDate purchaseDate = po.getPodate();

	        // =========================
	        // SUPPLIER
	        // =========================

	        DatewisePurchaseReportResponseDto supplierDto =
	                supplierMap.computeIfAbsent(supplierId, id -> {

	                    DatewisePurchaseReportResponseDto dto =
	                            new DatewisePurchaseReportResponseDto();

	                    dto.setSupplierId(supplierId);
	                    dto.setSupplierName(supplier.getNameEnglish());
	                    dto.setTotalAmount(BigDecimal.ZERO);
	                    dto.setDates(new ArrayList<>());

	                    return dto;
	                });

	        // =========================
	        // DATE
	        // =========================

	        Map<LocalDate, DatewisePurchaseReportDateResponseDto> dateMap =
	                supplierDateMap.computeIfAbsent(
	                        supplierId,
	                        k -> new LinkedHashMap<>()
	                );

	        DatewisePurchaseReportDateResponseDto dateDto =
	                dateMap.computeIfAbsent(purchaseDate, date -> {

	                    DatewisePurchaseReportDateResponseDto dto =
	                            new DatewisePurchaseReportDateResponseDto();

	                    dto.setPurchaseDate(date);
	                    dto.setDetails(new ArrayList<>());

	                    supplierDto.getDates().add(dto);

	                    return dto;
	                });

	        // =========================
	        // RAW MATERIAL
	        // =========================

	        DatewisePurchaseReportDataResponseDto itemDto = null;

	        for (DatewisePurchaseReportDataResponseDto existing :
	                dateDto.getDetails()) {

	            if (existing.getRawMaterialId()
	                    .equals(detail.getRawMaterial().getId())) {

	                itemDto = existing;
	                break;
	            }
	        }

	        if (itemDto == null) {

	            itemDto = new DatewisePurchaseReportDataResponseDto();

	            itemDto.setRawMaterialId(
	                    detail.getRawMaterial().getId());

	            itemDto.setRawMaterialName(
	                    detail.getRawMaterial().getNameEnglish());

	            itemDto.setRawMaterialCatId(
	                    detail.getRawMaterialCat().getId());

	            itemDto.setRawMaterialCatName(
	                    detail.getRawMaterialCat().getNameEnglish());

	            itemDto.setUnitId(
	                    detail.getUnit().getId());

	            itemDto.setUnitName(
	                    detail.getUnit().getNameEnglish());

	            itemDto.setHsccode(detail.getHsccode());

	            itemDto.setCgst(0f);
	            itemDto.setSgst(0f);
	            itemDto.setIgst(0f);
	            itemDto.setCess(0f);

	            itemDto.setQty(0d);

	            // First purchase price
	            itemDto.setPrice(detail.getPrice());

	            itemDto.setOthercharge(0f);
	            itemDto.setTotal(0f);

	            dateDto.getDetails().add(itemDto);
	        }

	        // =========================
	        // QTY
	        // =========================

	        itemDto.setQty(
	                itemDto.getQty() + detail.getQty()
	        );

	        // =========================
	        // OTHER CHARGE
	        // =========================

	        itemDto.setOthercharge(
	                itemDto.getOthercharge()
	                        + detail.getOthercharge()
	        );

	        // =========================
	        // TOTAL
	        // =========================

	        itemDto.setTotal(
	                itemDto.getTotal()
	                        + detail.getTotal()
	        );

	        // =========================
	        // GST
	        // =========================

	        float taxableAmount =
	                (float) (detail.getQty() * detail.getPrice());

	        float cgstAmount =
	                taxableAmount * detail.getCgst() / 100f;

	        float sgstAmount =
	                taxableAmount * detail.getSgst() / 100f;

	        float igstAmount =
	                taxableAmount * detail.getIgst() / 100f;

	        float cessAmount =
	                taxableAmount * detail.getCess() / 100f;

	        itemDto.setCgst(
	                itemDto.getCgst() + cgstAmount
	        );

	        itemDto.setSgst(
	                itemDto.getSgst() + sgstAmount
	        );

	        itemDto.setIgst(
	                itemDto.getIgst() + igstAmount
	        );

	        itemDto.setCess(
	                itemDto.getCess() + cessAmount
	        );

	        // =========================
	        // SUPPLIER TOTAL
	        // =========================

	        supplierDto.setTotalAmount(
	                supplierDto.getTotalAmount().add(
	                        BigDecimal.valueOf(detail.getTotal())
	                )
	        );
	    }

	    return new ArrayList<>(supplierMap.values());
	}
}