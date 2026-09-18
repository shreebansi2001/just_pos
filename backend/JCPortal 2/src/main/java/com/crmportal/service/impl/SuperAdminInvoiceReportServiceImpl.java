package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.TaxType;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.InvoicePaymentHistoryRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.SuperAdminInvoiceItemsResponseDto;
import com.crmportal.response.dto.SuperAdminInvoiceResponseDto;
import com.crmportal.service.InvoiceService;
import com.crmportal.service.SuperAdminInvoiceReportService;
import com.crmportal.utility.NumberToWordConverter;
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
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.ClearPropertyValue;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class SuperAdminInvoiceReportServiceImpl implements SuperAdminInvoiceReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	BankDetailsRepository bankDetailsRepository;
	
	@Autowired
	InvoicePaymentHistoryRepository invoicePaymentHistoryRepository;

	public static String getReportName(String partyName, String invoiceDate, String type) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		LocalDate date = invoiceDate != null ? LocalDate.parse(invoiceDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				: null;

		String inDate = date != null ? date.format(formatter) : "NA";
		String safeUserName = partyName != null ? partyName.replaceAll("[^a-zA-Z0-9]", "") : "All";
		String safeType = type != null ? type.replaceAll("[^a-zA-Z0-9]", "") : "";

		return safeUserName + "_" + inDate + "_" + safeType;
	}

	@Override
	public String generateInvoiceReport(Long invoiceId, HttpServletRequest re) {
		try {
			PdfFont basicFont = null, boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			System.out.println("English font loaded successfully");

			SuperAdminInvoiceResponseDto data = invoiceService.getAdminInvoiceById(invoiceId);

			if (data == null) {
				return "Data not found.";
			}

			Color headerBg = new DeviceRgb(60, 61, 58);
			Color whiteColor = new DeviceRgb(255, 255, 255);
			Color borderColor = new DeviceRgb(173, 173, 173);
			Color balanceBg = new DeviceRgb(245, 244, 242);
			Color grayColor = new DeviceRgb(51, 51, 51);
			Color darkGrayColor = ColorConstants.DARK_GRAY;

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/invoices");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(data.getCustomerName(), data.getInvoiceDate(), "Invoice") + ".pdf");

			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(Long.valueOf(1))
					.orElseThrow(() -> new RuntimeException("User not found with id : 1 "));

			UserBasicDetailsMasterEntity userBasicDetails = userBasicDetailsRepository.findByUserAndIsDeleteFalse(user)
					.orElseThrow(() -> new RuntimeException("User details not found for user id : " + user.getId()));

			BankDetailsEntity bankDetails = bankDetailsRepository.findByUserAndIsPrimaryTrueAndIsDeleteFalse(user)
					.orElseThrow(() -> new RuntimeException("Bank details not found with user id : " + user.getId()));

			// super admin company details
			String companyName = userBasicDetails.getCompanyName() != null
					? userBasicDetails.getCompanyName().toUpperCase()
					: "";
			String companyAddress = userBasicDetails.getAddress() != null ? userBasicDetails.getAddress() : "";

			// super admin bank details
			String bankName = bankDetails.getBankName() != null ? bankDetails.getBankName() : "";
			String accountNo = bankDetails.getAccountNo() != null ? bankDetails.getAccountNo() : "";
			String ifscCode = bankDetails.getIfscCode() != null ? bankDetails.getIfscCode() : "";
			String branchName = bankDetails.getBranchName() != null ? bankDetails.getBranchName() : "";
			String accountHolderName = bankDetails.getAccountHolderName() != null ? bankDetails.getAccountHolderName() : "";

			// invoice details
			String invoiceCode = data.getInvoiceCode() != null ? data.getInvoiceCode() : "";
			BigDecimal grandTotal = data.getTotalAmount() != null ? data.getTotalAmount() : BigDecimal.ZERO;
			String partyName = data.getCustomerName() != null ? data.getCustomerName() : "";
			String billingName = data.getBillingName() != null ? data.getBillingName() : "";
			String partyAddress = data.getBillingAddress() != null ? data.getBillingAddress() : "";
			String shippingAddress = data.getShippingAddress() != null ? data.getShippingAddress() : "";
			String partyGst = data.getGstNumber() != null ? data.getGstNumber() : "";
			String invoiceDate = data.getInvoiceDate() != null ? data.getInvoiceDate() : "";
			String terms = data.getTerms() != null ? data.getTerms() : "";
			String dueDate = data.getDueDate() != null ? data.getDueDate() : "";
			String subTotal = data.getSubTotal() != null ? data.getSubTotal().toString() : "";
			String gstPercent = data.getGstPercent() != null ? data.getGstPercent().toString() : "0.00";
			String gstAmount = data.getGstAmount() != null ? data.getGstAmount().toString() : "0.00";
			String customerNotes = data.getCustomerNotes() != null ? data.getCustomerNotes() : "0.00";
			String discountPercent = data.getDiscountPer() != null ? data.getDiscountPer().toString() : "0.00";
			String discountAmount = data.getDiscountAmount() != null ? data.getDiscountAmount().toString() : "0.00";
			String adjustAmount = data.getAdjust_amount() != null ? data.getAdjust_amount().toString() : "0.00";
			TaxType taxType = data.getTaxType() != null ? data.getTaxType() : TaxType.GST;
			BigDecimal totalPaidAmount = invoicePaymentHistoryRepository.getTotalPaidAmountByInvoiceId(invoiceId);
			BigDecimal dueAmount = grandTotal.subtract(totalPaidAmount);
			
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(40, 35, 40, 35);

			// ===== HEADER SECTION (Company Info + TAX INVOICE) =====
			float[] headerWidths = { 50f, 50f };
			Table headerTable = new Table(UnitValue.createPercentArray(headerWidths));
			headerTable.setWidth(UnitValue.createPercentValue(100));
			headerTable.setMarginBottom(20);

			// Left: Company details
			Paragraph address = new Paragraph().setFont(basicFont).setFontSize(10).setMarginTop(0);
			address.add(new Text(companyName + "\n").setFont(boldFont).setFontColor(grayColor).setFontSize(11));
			address.add(companyAddress + "\n");
//			address.add("GSTIN " + partyGst);

			Cell leftHeaderCell = new Cell().add(address).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.TOP);

			// Right: Invoice title
			Paragraph invoiceBlock = new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMarginTop(0);
			invoiceBlock.add(new Text("TAX INVOICE\n").setFont(boldFont).setFontSize(26));
			invoiceBlock.add(new Text("#" + invoiceCode + "\n").setFont(boldFont).setFontSize(14));
			invoiceBlock.add(new Text("\nBalance Due\n").setFont(boldFont).setFontColor(grayColor).setFontSize(9));
			invoiceBlock.add(new Text("RS. " + dueAmount).setFont(boldFont).setFontColor(grayColor).setFontSize(14));

			Cell rightHeaderCell = new Cell().add(invoiceBlock).setBorder(Border.NO_BORDER)
					.setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.TOP);

			headerTable.addCell(leftHeaderCell);
			headerTable.addCell(rightHeaderCell);

			document.add(headerTable);

			// ===== BILL/SHIP + INVOICE INFO SECTION =====
			Table sectionTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			sectionTable.setWidth(UnitValue.createPercentValue(100));

			// Left: Bill To / Ship To
			Paragraph billShip = new Paragraph().setFont(basicFont).setFontSize(10);
			billShip.add(new Text("Bill To\n").setFont(boldFont));
			billShip.add(new Text(billingName + "\n").setFont(boldFont).setFontColor(grayColor));
			billShip.add(partyAddress + "\n").setFontColor(grayColor);
			billShip.add("GSTIN " + partyGst + "\n\n").setFontColor(grayColor);
			billShip.add(new Text("Ship To\n").setFont(boldFont));
			billShip.add(shippingAddress + "\n").setFontColor(grayColor);
//			billShip.add("GSTIN " + partyGst + "\n\n").setFontColor(grayColor);

			Cell leftSectionCell = new Cell().add(billShip).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.TOP);

			Table innerTable = new Table(UnitValue.createPercentArray(new float[] { 40f, 60f }));
			innerTable.setWidth(UnitValue.createPercentValue(100f));
			innerTable.setFixedLayout();

			Cell cell = new Cell().add(new Paragraph("Invoice Date :").setFont(boldFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph(invoiceDate).setFont(basicFont).setFontColor(grayColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Terms :").setFont(boldFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph(terms).setFont(basicFont).setFontColor(grayColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Due Date :").setFont(boldFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph(dueDate).setFont(basicFont).setFontColor(grayColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			sectionTable.addCell(leftSectionCell);
			sectionTable.addCell(new Cell().add(innerTable).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.MIDDLE));

			document.add(sectionTable);

			Table itemTable = new Table(
					UnitValue.createPercentArray(new float[] { 5f, 30f, 10f, 8f, 13f, 10f, 10f, 14f }));
			itemTable.setWidth(UnitValue.createPercentValue(100f));
			itemTable.setFixedLayout();
			itemTable.setMarginTop(10f);

			cell = new Cell().add(new Paragraph("#").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addCell(cell);

			cell = new Cell(1, 4).add(new Paragraph("Items").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addCell(cell);

//			cell = new Cell().add(new Paragraph("HSN").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
//					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
//					.setPaddingTop(5f).setPaddingBottom(5f);
//			itemTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Qty").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Rate").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addCell(cell);

//			cell = new Cell().add(new Paragraph("Tax %").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
//					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
//					.setPaddingTop(5f).setPaddingBottom(5f);
//			itemTable.addCell(cell);
//
//			cell = new Cell().add(new Paragraph("Tax Amt.").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
//					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
//					.setPaddingTop(5f).setPaddingBottom(5f);
//			itemTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Amount").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f).setPaddingRight(10f);
			itemTable.addCell(cell);

			List<SuperAdminInvoiceItemsResponseDto> invoiceItems = data.getItems();

			Integer idx = 0;
			for (SuperAdminInvoiceItemsResponseDto item : invoiceItems) {
				String itemName = item.getItemName() != null ? item.getItemName() : "";
				String qty = item.getQty() != null ? item.getQty().toString() : "";
				String rate = item.getRate() != null ? item.getRate().toString() : "0.00";
				String amount = item.getAmount() != null ? item.getAmount().toString() : "0.00";
				String taxPercent = item.getTaxPercent() != null ? item.getTaxPercent().toString() : "0.00";
				String taxAmount = item.getTaxAmount() != null ? item.getTaxAmount().toString() : "0.00";
				String hsnCode = item.getHsnCode() != null && item.getHsnCode().trim().length() != 0 ? "\nHSN : " + item.getHsnCode() : "";
				String description = item.getDescription() != null && item.getDescription().trim().length() != 0
						? "\n" + item.getDescription()
						: "";

				cell = new Cell().add(new Paragraph((++idx).toString()).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				itemTable.addCell(cell);

				cell = new Cell(1, 4).add(new Paragraph(itemName + description + hsnCode).setFont(basicFont).setFontColor(grayColor))
						.setFontSize(10).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				itemTable.addCell(cell);

//				cell = new Cell().add(new Paragraph(hsnCode).setFont(basicFont)).setFontSize(10)
//						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
//						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
//				itemTable.addCell(cell);

				cell = new Cell().add(new Paragraph(qty).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				itemTable.addCell(cell);

				cell = new Cell().add(new Paragraph(rate).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				itemTable.addCell(cell);

//				cell = new Cell().add(new Paragraph(taxPercent).setFont(basicFont)).setFontSize(10)
//						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
//						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
//				itemTable.addCell(cell);
//
//				cell = new Cell().add(new Paragraph(taxAmount).setFont(basicFont)).setFontSize(10)
//						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
//						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
//				itemTable.addCell(cell);

				cell = new Cell().add(new Paragraph(amount).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f)
						.setPaddingRight(10f);
				itemTable.addCell(cell);
			}

			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell(1, 2).add(new Paragraph("Sub Total").setFont(basicFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f);
			itemTable.addCell(cell);

			cell = new Cell().add(new Paragraph(subTotal).setFont(basicFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f).setPaddingRight(10f);
			itemTable.addCell(cell);

			if(!discountAmount.equalsIgnoreCase("0.00")) {
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
	
				cell = new Cell(1, 2).add(new Paragraph("DISCOUNT (" + discountPercent + "%)").setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
						.setPaddingBottom(5f);
				itemTable.addCell(cell);
	
				cell = new Cell().add(new Paragraph(discountAmount).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
						.setPaddingBottom(5f).setPaddingRight(10f);
				itemTable.addCell(cell);
			}

			if(!gstAmount.equalsIgnoreCase("0.00")) {
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
	
				cell = new Cell(1, 2).add(new Paragraph(taxType +" (" + gstPercent + "%)").setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
						.setPaddingBottom(5f);
				itemTable.addCell(cell);
	
				cell = new Cell().add(new Paragraph(gstAmount).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
						.setPaddingBottom(5f).setPaddingRight(10f);
				itemTable.addCell(cell);
			}
			
			if(!adjustAmount.equalsIgnoreCase("0.00")) {
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
	
				cell = new Cell(1, 2).add(new Paragraph("Adjust").setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
						.setPaddingBottom(5f);
				itemTable.addCell(cell);
	
				cell = new Cell().add(new Paragraph(adjustAmount).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
						.setPaddingBottom(5f).setPaddingRight(10f);
				itemTable.addCell(cell);
			}
			
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell(1, 2).add(new Paragraph("Total").setFont(boldFont)).setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f);
			itemTable.addCell(cell);

			cell = new Cell().add(new Paragraph(grandTotal.toString()).setFont(boldFont)).setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f).setPaddingRight(10f);
			itemTable.addCell(cell);

			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell(1, 3).add(new Paragraph("Balance Due").setFont(boldFont)).setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f).setBackgroundColor(balanceBg);
			itemTable.addCell(cell);

			cell = new Cell().add(new Paragraph(dueAmount.toString()).setFont(boldFont)).setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f).setPaddingRight(10f).setBackgroundColor(balanceBg);
			itemTable.addCell(cell);

			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell(1, 2).add(new Paragraph("Total in words:").setFont(basicFont).setFontColor(grayColor))
					.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f);
			itemTable.addCell(cell);

			cell = new Cell(1, 3)
					.add(new Paragraph(NumberToWordConverter.convert(dueAmount.longValue()) + " Only")
							.setFont(boldFont).setFontColor(darkGrayColor))
					.setFontSize(10).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f).setPaddingRight(10f);
			itemTable.addCell(cell);

			document.add(itemTable);

			// ===== NOTES SECTION =====
			Paragraph notes = new Paragraph().setFont(basicFont).setFontSize(10).setMarginTop(15);

			notes.add(new Text("Notes\n").setFont(boldFont).setFontSize(11).setFontColor(ColorConstants.BLACK));
			notes.add(customerNotes).setFontColor(grayColor);

			document.add(notes);

			// ===== PAYMENT OPTIONS SECTION =====
//			Div paymentDiv = new Div();
//			paymentDiv.setMarginTop(11);
//			paymentDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
//			paymentDiv.setWidth(UnitValue.createPercentValue(100));
//
//			Paragraph paymentLabel = new Paragraph().setFont(boldFont).setFontSize(10).setMarginBottom(5);
//			paymentLabel.add("Payment Options");
//
//			Div paymentRow = new Div();
//			paymentRow.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
//			paymentRow.add(paymentLabel);
//
//			paymentDiv.add(paymentRow);
//			document.add(paymentDiv);

			// Clearfix
			Div clearfix3 = new Div();
			clearfix3.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
			document.add(clearfix3);

			// ===== TERMS & CONDITIONS SECTION =====
			Paragraph tnc = new Paragraph().setFont(basicFont).setFontSize(10).setMarginTop(10);

			tnc.add(new Text("Bank Details\n").setFont(boldFont).setFontSize(10)
					.setFontColor(ColorConstants.BLACK));
			tnc.add(bankName.toUpperCase() + "\n").setFontColor(grayColor); // HDFC BANK
			tnc.add("AC NO:-" + accountNo + "\n").setFontColor(grayColor); // 50200013422306
			tnc.add("BRANCH :- " + branchName.toUpperCase() + "\n").setFontColor(grayColor); // DARPAN SIX ROAD
			tnc.add("IFSC CODE :- " + ifscCode.toUpperCase() + "\n").setFontColor(grayColor); // HDFC0001678
			tnc.add("AC NAME :- " + accountHolderName.toUpperCase() + "\n").setFontColor(grayColor); // SHREE INFOTECH

			document.add(tnc);

			// ===== AUTHORIZED SIGNATURE =====
			Paragraph signature = new Paragraph().setFont(boldFont).setFontSize(10).setMarginTop(10);

			signature.add(new Text("Authorized Signature ").setFont(boldFont));
			signature.add(new Text("________________________________").setFont(basicFont));

			document.add(signature);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/invoices" + "/"
					+ getReportName(data.getCustomerName(), data.getInvoiceDate(), "Invoice") + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}

	}
}
