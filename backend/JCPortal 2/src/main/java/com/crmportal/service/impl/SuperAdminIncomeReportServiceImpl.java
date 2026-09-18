package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.math3.util.ResizableDoubleArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.response.dto.IncomeListResponseDto;
import com.crmportal.response.dto.IncomeResponseDto;
import com.crmportal.service.IncomeService;
import com.crmportal.service.SuperAdminIncomeReportService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class SuperAdminIncomeReportServiceImpl implements SuperAdminIncomeReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	IncomeService incomeService;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	public static String getReportName(String startDate, String endDate, String type) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		LocalDate start = startDate != null ? LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				: null;
		LocalDate end = endDate != null ? LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;

		String stDate = startDate != null ? start.format(formatter) : "NA";
		String eDate = endDate != null ? end.format(formatter) : "NA";
		String safeType = type != null ? type.replaceAll("[^a-zA-Z0-9]", "") : "IncomeReport";

		return stDate + "_to_" + eDate + "_" + safeType;
	}

	private Cell addTableHeaderCell(String label, PdfFont font, Color fontColor, Color bgColor, Color borderColor,
			Integer mergeCell, TextAlignment textAlignment, VerticalAlignment verticalAlignment, float fontSize,
			float padding) {
		return new Cell(1, mergeCell)
				.add(new Paragraph(label).setFont(font).setFontSize(fontSize).setFontColor(fontColor)
						.setFixedLeading(fontSize))
				.setBackgroundColor(bgColor).setPadding(padding).setBorder(new SolidBorder(borderColor, 0.5f))
				.setVerticalAlignment(verticalAlignment).setTextAlignment(textAlignment);
	}

	@Override
	public String generateIncomeReport(String startDate, String endDate, AccountType accountType, PaymentMode paymentMode,
			Long cashAccountId, Long bankAccountId, Long typeId, Long userId, HttpServletRequest re) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			System.out.println("English font loaded successfully");
			
			BankDetailsEntity bankDetails = null; 
			if(bankAccountId != null && bankAccountId != -1) {
				bankDetails = bankDetailsRepository.findByIdAndIsDeleteFalse(bankAccountId).orElseThrow(() -> new RuntimeException("Bank details not found with id : " + bankAccountId));
			}
			
			IncomeResponseDto data = incomeService.getIncome(startDate, endDate, accountType, paymentMode, 
					bankAccountId, cashAccountId, typeId, userId);

			if (data == null) {
				return "No data found.";
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/expense/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			File pdfFile = new File(outputPath + "/" + getReportName(startDate, endDate, "Income Report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 20, 20);

			Color primaryLabelBgColor = new DeviceRgb(242, 235, 223);
			Color labelColor = new DeviceRgb(115, 99, 67);
			Color whiteColor = new DeviceRgb(255, 255, 255);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

			Image logoData = new Image(logo);
			logoData.setWidth(110f);
			logoData.setAutoScale(false);
			logoData.setHorizontalAlignment(HorizontalAlignment.CENTER);

			/* ---------- HEADER TABLE ---------- */
			Table table = new Table(UnitValue.createPercentArray(new float[] { 24f, 46f, 30f }));
			table.setFixedLayout();
			table.setWidth(UnitValue.createPercentValue(100f));

			cell = new Cell(4, 1).add(logoData).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.MIDDLE);
			table.addCell(cell);

			cell = new Cell().setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell().add(new Paragraph().add(new Text("DATE : ").setFontSize(11).setFont(boldFont))
					.add(new Text(LocalDateTime.now().format(dateTimeFormat)).setFontSize(11).setFont(basicFont)))
					.setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell(1, 2).add(new Paragraph().add(new Text("WEB MINDS PVT. LTD.")).setFont(boldFont)
					.setFontSize(14f).setFixedLeading(14f)).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell(1, 2).add(
					new Paragraph().add(new Text("Ahmedabad")).setFont(basicFont).setFontSize(13f).setFixedLeading(13f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell(1, 2).setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell(1, 3)
					.add(new Paragraph("Income Report").setFont(boldFont).setFontSize(18)
							.setTextAlignment(TextAlignment.CENTER).setPadding(0).setFixedLeading(18f))
					.setBorder(Border.NO_BORDER);
			table.addCell(cell);

			document.add(table);

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			headerTable.setFixedLayout();
			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setMarginTop(5f);

			cell = new Cell()
					.add(new Paragraph().add(new Text("FROM : ").setFont(boldFont).setFontSize(12f))
							.add(new Text(startDate).setFont(basicFont).setFontSize(12f)))
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			headerTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text("TO : ").setFont(boldFont).setFontSize(12f))
							.add(new Text(endDate).setFont(basicFont).setFontSize(12f)))
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			headerTable.addCell(cell);

			document.add(headerTable);

			Table totalSummaryTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			totalSummaryTable.setFixedLayout();
			totalSummaryTable.setWidth(UnitValue.createPercentValue(100f));
			totalSummaryTable.setMarginTop(5f);

			cell = addTableHeaderCell("TOTAL SUMMARY", boldFont, labelColor, primaryLabelBgColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			totalSummaryTable.addCell(cell);

			cell = addTableHeaderCell("Total Revenue", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			totalSummaryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalRevenue().toString(), basicFont, blackColor, whiteColor, labelColor,
					1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			totalSummaryTable.addCell(cell);

			cell = addTableHeaderCell("Total Active Users", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			totalSummaryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalActiveUsers().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			totalSummaryTable.addCell(cell);

			cell = addTableHeaderCell("Total No Invoice Created", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			totalSummaryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalNoInvoiceCreated().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			totalSummaryTable.addCell(cell);
			
			document.add(totalSummaryTable);
			
			Table invoiceTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			invoiceTable.setFixedLayout();
			invoiceTable.setWidth(UnitValue.createPercentValue(100f));
			invoiceTable.setMarginTop(10f);
			
			cell = addTableHeaderCell("INVOICE SUMMARY", boldFont, labelColor, primaryLabelBgColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("THIS MONTH", boldFont, labelColor, whiteColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 13f, 7f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Invoice Created", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoiceCreatedThisMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Invoice Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoiceAmountThisMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Paid Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoicePaidAmountThisMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Unpaid Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoiceUnpaidAmountThisMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("PREV ALL MONTH", boldFont, labelColor, whiteColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 13f, 7f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Invoice Created", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoiceCreatedPrevAllMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Invoice Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoiceAmountPrevAllMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Paid Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoicePaidAmountPrevAllMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Unpaid Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalInvoiceUnpaidAmountPrevAllMonth().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			invoiceTable.addCell(cell);

			document.add(invoiceTable);

//			cell = addTableHeaderCell("Total Pending Amount", basicFont, blackColor, whiteColor, labelColor, 1,
//					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
//			totalSummaryTable.addCell(cell);

//			cell = addTableHeaderCell(data.getTotalPendingAmount().toString(), basicFont, blackColor, whiteColor,
//					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
//			totalSummaryTable.addCell(cell);

			Table accountEntryTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			accountEntryTable.setFixedLayout();
			accountEntryTable.setWidth(UnitValue.createPercentValue(100f));
			accountEntryTable.setMarginTop(10f);
			
			cell = addTableHeaderCell("ACCOUNT ENTRY", boldFont, labelColor, primaryLabelBgColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			invoiceTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Account Entry", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalAccountEntry().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Cash Entry", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalCashEntry().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Bank Entry", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalBankEntry().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalAccountEntryAmount().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Cash Amount", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalCashAmount().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Bank Entry", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);

			cell = addTableHeaderCell(data.getTotalBankAmount().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountEntryTable.addCell(cell);
			
			document.add(accountEntryTable);
			
			/* Payment Mode Summary */
			Table paymentModeSummaryTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			paymentModeSummaryTable.setFixedLayout();
			paymentModeSummaryTable.setWidth(UnitValue.createPercentValue(100f));
			paymentModeSummaryTable.setMarginTop(10f);

			cell = addTableHeaderCell("PAYMENT MODE SUMMARY", boldFont, labelColor, primaryLabelBgColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			paymentModeSummaryTable.addCell(cell);

			for (Map.Entry<PaymentMode, BigDecimal> entry : data.getPaymentModeAmount().entrySet()) {

				PaymentMode mode = entry.getKey();
				BigDecimal amount = entry.getValue();

				String val = "";
				if(mode.equals(PaymentMode.BANK_TRANSFER)) {
					val = "BANK TRANSFER";
				}else {
					val = mode.toString();
				}
				cell = addTableHeaderCell(val, basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
				paymentModeSummaryTable.addCell(cell);

				cell = addTableHeaderCell(amount.toString(), basicFont, blackColor, whiteColor,
						labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
				paymentModeSummaryTable.addCell(cell);
			}

			document.add(paymentModeSummaryTable);
			
			/* Payment Mode Summary */
			Table incomeListTable = new Table(UnitValue.createPercentArray(new float[] {17f, 30f, 23f, 15f, 15f }));
			incomeListTable.setFixedLayout();
			incomeListTable.setWidth(UnitValue.createPercentValue(100f));
			incomeListTable.setMarginTop(10f);

			String label = "";
			if(paymentMode != null) {
				if(paymentMode.equals(PaymentMode.BANK_TRANSFER)) {
					if(bankDetails != null) {
						label = bankDetails.getBankName().toUpperCase();
					}else {
						label = "BANK TRANSFER";
					}
				}else {
					label = paymentMode.toString();
				}
			}else {
				label = "ALL";
			}
			cell = addTableHeaderCell("DETAILED INCOME LIST ("+ label +")", boldFont, labelColor, primaryLabelBgColor, labelColor, 5,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			incomeListTable.addCell(cell);
			
			cell = addTableHeaderCell("INVOICE", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			incomeListTable.addCell(cell);
			
			cell = addTableHeaderCell("CUSTOMER NAME", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			incomeListTable.addCell(cell);
			
			cell = addTableHeaderCell("PAYMENT MODE", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			incomeListTable.addCell(cell);
			
			cell = addTableHeaderCell("DATE", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			incomeListTable.addCell(cell);
			
			cell = addTableHeaderCell("AMOUNT", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			incomeListTable.addCell(cell);
			
			for(IncomeListResponseDto invoice : data.getPayments()) {
				String val = "";
				if(invoice.getPaymentMode().equals(PaymentMode.BANK_TRANSFER)) {
					val = "BANK TRANSFER";
				}else {
					val = invoice.getPaymentMode().toString();
				}
				cell = addTableHeaderCell(invoice.getVoucherNo(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				incomeListTable.addCell(cell);
				
				cell = addTableHeaderCell(invoice.getClientName(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				incomeListTable.addCell(cell);
				
				cell = addTableHeaderCell(val, basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				incomeListTable.addCell(cell);
				
				cell = addTableHeaderCell(invoice.getDate(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				incomeListTable.addCell(cell);
				
				cell = addTableHeaderCell(invoice.getAmount().toString(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				incomeListTable.addCell(cell);
			}
			
			cell = addTableHeaderCell("TOTAL", boldFont, labelColor, whiteColor, labelColor, 4,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, 13f, 5f);
			incomeListTable.addCell(cell);
			
			cell = addTableHeaderCell(data.getTotalAmount().toString(), boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 13f, 5f);
			incomeListTable.addCell(cell);
			
			document.add(incomeListTable);
			
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/expense/"
					+ getReportName(startDate, endDate, "Income Report") + ".pdf";

			return fullUrl;
		}catch(

	Exception e)
	{
		e.printStackTrace();
		throw new RuntimeException("Failed to generate Event Menu Report", e);
	}
}}
