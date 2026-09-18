package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.response.dto.AccountLedgerDashboardResponseDto;
import com.crmportal.response.dto.AccountLedgerResponseDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.IncomeListResponseDto;
import com.crmportal.service.AccountLedgerReportService;
import com.crmportal.service.AccountLedgerService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
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
public class AccountLedgerReportServiceImpl implements AccountLedgerReportService {

	@Autowired
	AccountLedgerService accountLedgerService;
	
	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	Environment environment;
	
	@Autowired
	ChefRequisitionServiceImpl chefRequisitionServiceImpl;
	
	@Autowired
	CashAccountRepository cashAccountRepository;
	
	@Autowired
	BankDetailsRepository bankDetailsRepository;
	
	@Override
	public String generateAccountLedgerReport(String startDate, String endDate, AccountType accountType,
			PaymentMode paymentMode, Long cashAccountId, Long bankAccountId, Long userId, HttpServletRequest req) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			CompanyDetailsResponseDto companyDetails = chefRequisitionServiceImpl.getCompanyDetails(userId);
			
			if(companyDetails == null) {
				return "User id not found.";
			}
			
			AccountLedgerDashboardResponseDto data = accountLedgerService.getAccountLedger(startDate, endDate, accountType,
					paymentMode, cashAccountId, bankAccountId, userId);

			if (data == null) {
				return "";
			}
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			
			Date now = new Date();
			String rootPath = req.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/ACCOUNT_REPORT/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(startDate, endDate, "account ledger report") + ".pdf");

			DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			
	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf);
	
	        String companyName = companyDetails.getCompanyName() != null ? companyDetails.getCompanyName() : "";
	        String companyEmail = companyDetails.getCompanyEmail() != null ? companyDetails.getCompanyEmail() : "";
	        
	        Cell cell;
	        
	        Color primaryLabelBgColor = new DeviceRgb(242, 235, 223);
			Color labelColor = new DeviceRgb(115, 99, 67);
			Color whiteColor = new DeviceRgb(255, 255, 255);
			Color blackColor = new DeviceRgb(0, 0, 0);
			
			boolean hasCash = cashAccountId != null;
			boolean hasBank = bankAccountId != null;

			if (hasCash && hasBank) {
				throw new RuntimeException("Only one of Cash or Bank should be provided.");
			}

			if (!hasCash && !hasBank) {
				throw new RuntimeException("Either Cash or Bank detail is required.");
			}
			
			String accountName = "";
			
			if (hasCash && cashAccountId != -1) {
				CashAccountEntity cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(cashAccountId)
						.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + cashAccountId));
				accountName = cashAccountEntity.getAccountName();
			}
			if (hasBank && bankAccountId != -1) {
				BankDetailsEntity bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(bankAccountId)
						.orElseThrow(() -> new RuntimeException("Bank account not found with id : " + bankAccountId));
				accountName = bankDetailsEntity.getBankName();
			}
	        
//	        ImageData logo = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
	        ImageData logo = menuPreparationServiceImpl.loadImageFromResource(environment.getProperty("app.image.url") + companyDetails.getLogo());

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

			cell = new Cell(1, 2).add(new Paragraph().add(new Text(companyName.toUpperCase())).setFont(boldFont)
					.setFontSize(14f).setFixedLeading(14f)).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell(1, 2).add(
					new Paragraph().add(new Text(companyEmail)).setFont(basicFont).setFontSize(13f).setFixedLeading(13f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell(1, 2).setBorder(Border.NO_BORDER);
			table.addCell(cell);

			cell = new Cell(1, 3)
					.add(new Paragraph("ACCOUNT LEDGER REPORT").setFont(boldFont).setFontSize(18)
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
			
			/* Account Details */
			Table accountDetailsTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			accountDetailsTable.setFixedLayout();
			accountDetailsTable.setWidth(UnitValue.createPercentValue(100f));
			accountDetailsTable.setMarginTop(5f);

			cell = addTableHeaderCell("ACCOUNT DETAILS", boldFont, labelColor, primaryLabelBgColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			accountDetailsTable.addCell(cell);

			cell = addTableHeaderCell("Account Name", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountDetailsTable.addCell(cell);

			cell = addTableHeaderCell(accountName.toUpperCase(), basicFont, blackColor, whiteColor, labelColor,
					1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountDetailsTable.addCell(cell);

			cell = addTableHeaderCell("Account Type", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountDetailsTable.addCell(cell);

			cell = addTableHeaderCell(accountType.name().toUpperCase(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountDetailsTable.addCell(cell);

			cell = addTableHeaderCell("Currency", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountDetailsTable.addCell(cell);

			cell = addTableHeaderCell("INR", basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			accountDetailsTable.addCell(cell);

			document.add(accountDetailsTable);
	        
			/* Summary */
			Table summaryTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			summaryTable.setFixedLayout();
			summaryTable.setWidth(UnitValue.createPercentValue(100f));
			summaryTable.setMarginTop(10f);

			cell = addTableHeaderCell("SUMMARY", boldFont, labelColor, primaryLabelBgColor, labelColor, 2,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			summaryTable.addCell(cell);
			
			cell = addTableHeaderCell("Opening Balance", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);

			cell = addTableHeaderCell(data.getOpeningBalance().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);

			cell = addTableHeaderCell("Total Debit", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);
			
			cell = addTableHeaderCell(data.getDebit().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);
			
			cell = addTableHeaderCell("Total Credit", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);
			
			cell = addTableHeaderCell(data.getCredit().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);
			
			cell = addTableHeaderCell("Closing Balance", basicFont, blackColor, whiteColor, labelColor, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);
			
			cell = addTableHeaderCell(data.getTotalBalance().toString(), basicFont, blackColor, whiteColor,
					labelColor, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f);
			summaryTable.addCell(cell);
			
			document.add(summaryTable);
			
			/* Payment Mode Summary */
			Table transactionTable = new Table(UnitValue.createPercentArray(new float[] {14f, 18f, 23f, 15f, 15f, 15f }));
			transactionTable.setFixedLayout();
			transactionTable.setWidth(UnitValue.createPercentValue(100f));
			transactionTable.setMarginTop(10f);

			cell = addTableHeaderCell("DETAILED LIST", boldFont, labelColor, primaryLabelBgColor, labelColor, 6,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 7f);
			transactionTable.addCell(cell);
			
			cell = addTableHeaderCell("DATE", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			transactionTable.addCell(cell);
			
			cell = addTableHeaderCell("VOUCHER NO", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			transactionTable.addCell(cell);
			
			cell = addTableHeaderCell("PARTICULARS", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			transactionTable.addCell(cell);
			
			cell = addTableHeaderCell("DEBIT", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			transactionTable.addCell(cell);
			
			cell = addTableHeaderCell("CREDIT", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			transactionTable.addCell(cell);
			
			cell = addTableHeaderCell("BALANCE", boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			transactionTable.addCell(cell);
			
			for(AccountLedgerResponseDto entry : data.getTransactions()) {
				String val = "";
				cell = addTableHeaderCell(entry.getDate(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				transactionTable.addCell(cell);
				
				cell = addTableHeaderCell(entry.getVoucherNo(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				transactionTable.addCell(cell);
				
				cell = addTableHeaderCell(entry.getParticular(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				transactionTable.addCell(cell);
				
				cell = addTableHeaderCell(entry.getDebit().toString(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				transactionTable.addCell(cell);
				
				cell = addTableHeaderCell(entry.getCredit().toString(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				transactionTable.addCell(cell);
				
				cell = addTableHeaderCell(entry.getBalance().toString(), basicFont, blackColor, whiteColor, labelColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
				transactionTable.addCell(cell);
			}
			
			cell = addTableHeaderCell("TOTAL", boldFont, labelColor, whiteColor, labelColor, 5,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, 13f, 5f);
			transactionTable.addCell(cell);
			
			cell = addTableHeaderCell(data.getTotalBalance().toString(), boldFont, labelColor, whiteColor, labelColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 13f, 5f);
			transactionTable.addCell(cell);
			
			document.add(transactionTable);
			
	        document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/ACCOUNT_REPORT/"
					+ getReportName(startDate, endDate, "account ledger report")
					+ ".pdf";
			return fullUrl;
	
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}
	
	private static String getReportName(String startDate, String endDate, String type) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		LocalDate stdate = startDate != null ? LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				: null;
		LocalDate edate = endDate != null ? LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				: null;

		String stDate = stdate != null ? stdate.format(formatter) : "NA";
		String eDate = edate != null ? edate.format(formatter) : "NA";

		String safeType = type != null ? type.replaceAll("[^a-zA-Z0-9]", "") : "";

		return stDate + "_TO_" + eDate + "_" + safeType;
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
	
}
