package com.crmportal.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.WorkingReportResponseDto;
import com.crmportal.service.EventMasterService;
import com.crmportal.service.WorkReportService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class WorkReportServiceImpl implements WorkReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventMasterService eventMasterService;

	@Autowired
	Environment environment;

	@Autowired
	EventFunctionMenuAllocationServiceImpl eventFunctionMenuAllocationServiceImpl;

	@Override
	public String generateWorkReport(Long eventId, Long userId, HttpServletRequest request, int lang) {

		try {

			menuPreparationServiceImpl.loadLicense();

			// ============================================================
			// FONT
			// ============================================================

			PdfFont basicFont;
			PdfFont boldFont;

			if (lang == 1) {
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (lang == 2) {
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			}

			List<Object[]> result = eventMasterRepository.getEventData(eventId);
			
			if (result == null || result.isEmpty()) {
				throw new RuntimeException("Event data not found for eventId : " + eventId);
			}

			WorkingReportResponseDto data = mapEventData(result.get(0));
			

			// ============================================================
			// OUTPUT PATH
			// ============================================================

			String rootPath = request.getSession().getServletContext().getRealPath("/");

			File outputPath = new File(rootPath + "resources/tempDownload/" + data.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			File pdfFile = new File(outputPath, "work_report.pdf");

			// ============================================================
			// PDF
			// ============================================================

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);

			Document document = new Document(pdfDocument, PageSize.A4);

			document.setMargins(20, 20, 20, 20);

			Border NONE = Border.NO_BORDER;

			Color black = new DeviceRgb(0, 0, 0);
			Color gray = new DeviceRgb(100, 100, 100);

			// ============================================================
			// COMPANY DETAILS
			// ============================================================

			CompanyDetailsResponseDto companyDetails = eventFunctionMenuAllocationServiceImpl.getCompanyDetails(userId);

			// ============================================================
			// HEADER
			// ============================================================

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 80f }));

			headerTable.setWidth(UnitValue.createPercentValue(100));
			headerTable.setBorder(NONE);
			headerTable.setMarginBottom(3f);

			// ------------------------------------------------------------
			// LOGO
			// ------------------------------------------------------------

			Cell logoCell = new Cell();
			logoCell.setBorder(NONE);
			logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
			logoCell.setPadding(2f);

			try {

				if (companyDetails != null && companyDetails.getLogo() != null
						&& !companyDetails.getLogo().trim().isEmpty()) {

					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + companyDetails.getLogo());

					Image logo = new Image(logoData);

					logo.setWidth(UnitValue.createPercentValue(90));
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

					logoCell.add(logo);
				}

			} catch (Exception ex) {
				// If logo is not available, don't fail PDF generation
				ex.printStackTrace();
			}

			headerTable.addCell(logoCell);

			// ------------------------------------------------------------
			// COMPANY INFORMATION
			// ------------------------------------------------------------

			Cell companyCell = new Cell();
			companyCell.setBorder(NONE);
			companyCell.setPaddingLeft(5f);
			companyCell.setPaddingTop(0);
			companyCell.setPaddingBottom(0);

			Paragraph companyName = new Paragraph(companyDetails != null && companyDetails.getCompanyName() != null
					? companyDetails.getCompanyName().toUpperCase()
					: "");

			companyName.setFont(boldFont);
			companyName.setFontSize(18);
			companyName.setFontColor(black);
			companyName.setMargin(0);
			companyName.setTextAlignment(TextAlignment.CENTER);

			companyCell.add(companyName);

			Paragraph address = new Paragraph(
					companyDetails != null && companyDetails.getAddress() != null ? companyDetails.getAddress() : "");

			address.setFont(basicFont);
			address.setFontSize(9);
			address.setFontColor(black);
			address.setMargin(0);
			address.setTextAlignment(TextAlignment.CENTER);

			companyCell.add(address);

			Paragraph contact = new Paragraph();

			if (companyDetails != null) {

				if (companyDetails.getOfficeNo() != null) {
					contact.add(new Text("Mob : ").setFont(boldFont));

					contact.add(new Text(companyDetails.getOfficeNo()));
				}

				if (companyDetails.getCompanyEmail() != null) {

					contact.add(new Text("    Email : ").setFont(boldFont));

					contact.add(new Text(companyDetails.getCompanyEmail()));
				}
			}

			contact.setFont(basicFont);
			contact.setFontSize(8.5f);
			contact.setFontColor(black);
			contact.setMargin(0);
			contact.setTextAlignment(TextAlignment.CENTER);

			companyCell.add(contact);

			headerTable.addCell(companyCell);

			document.add(headerTable);

			// ============================================================
			// ORDER FORM TITLE
			// ============================================================

			Table titleTable = new Table(UnitValue.createPercentArray(new float[] { 75f, 25f }));

			titleTable.setWidth(UnitValue.createPercentValue(100));
			titleTable.setBorder(NONE);
			titleTable.setMarginTop(3f);
			titleTable.setMarginBottom(5f);

			Cell titleCell = new Cell();
			titleCell.setBorder(NONE);

			Paragraph title = new Paragraph("ORDER FORM");

			title.setFont(boldFont);
			title.setFontSize(14);
			title.setFontColor(black);
			title.setTextAlignment(TextAlignment.CENTER);
			title.setMargin(0);

			titleCell.add(title);

			titleTable.addCell(titleCell);

			// Booking date
			Cell bookingDateCell = new Cell();
			bookingDateCell.setBorder(NONE);

			Paragraph bookingDate = new Paragraph();

			bookingDate.add(new Text("Booking Date : ").setFont(boldFont));

			bookingDate.add(new Text(formatDate(data.getInquiryDate())).setFont(basicFont));

			bookingDate.setFontSize(8.5f);
			bookingDate.setMargin(0);
			bookingDate.setTextAlignment(TextAlignment.RIGHT);

			bookingDateCell.add(bookingDate);

			titleTable.addCell(bookingDateCell);

			document.add(titleTable);

			// ============================================================
			// HORIZONTAL LINE
			// ============================================================

			Paragraph line = new Paragraph();
			line.setBorderBottom(new SolidBorder(0.8f));
			line.setMarginTop(0);
			line.setMarginBottom(5f);

			document.add(line);

			// ============================================================
			// PARTY NAME + MOBILE
			// ============================================================

			Table partyTable = new Table(UnitValue.createPercentArray(new float[] { 15f, 55f, 12f, 18f }));

			partyTable.setWidth(UnitValue.createPercentValue(100));
			partyTable.setBorder(NONE);

			addLabelCell(partyTable, "Party Name", boldFont, black);
			addValueCell(partyTable, safe(data.getPartyName()), basicFont, black);
			addLabelCell(partyTable, "Mob.", boldFont, black);
			addValueCell(partyTable, safe(data.getMobileNo()), basicFont, black);

			document.add(partyTable);

			// ============================================================
			// ADDRESS
			// ============================================================

			Table addressTable = new Table(UnitValue.createPercentArray(new float[] { 15f, 85f }));

			addressTable.setWidth(UnitValue.createPercentValue(100));
			addressTable.setBorder(NONE);
			addLabelCell(addressTable, "Address", boldFont, black);
			addValueCell(addressTable, safe(data.getPartyAddress()), basicFont, black);

			document.add(addressTable);

			// ============================================================
			// VENUE
			// ============================================================

			Table venueTable = new Table(UnitValue.createPercentArray(new float[] { 15f, 85f }));

			venueTable.setWidth(UnitValue.createPercentValue(100));
			venueTable.setBorder(NONE);

			addLabelCell(venueTable, "Venue", boldFont, black);
			addValueCell(venueTable, safe(data.getEventVenue()), basicFont, black);

			document.add(venueTable);

			// ============================================================
			// PROGRAM DATE + TIME
			// ============================================================

			Table programTable = new Table(UnitValue.createPercentArray(new float[] { 15f, 40f, 18f, 27f }));

			programTable.setWidth(UnitValue.createPercentValue(100));
			programTable.setBorder(NONE);

			addLabelCell(programTable, "Program Date", boldFont, black);
			addValueCell(programTable, data.getEventStartDate(), basicFont, black);
			addLabelCell(programTable, "Program Time", boldFont, black);
			addValueCell(programTable, safe(data.getEventStartTime()), basicFont, black);

			document.add(programTable);

			// ============================================================
			// EVENT DETAILS
			// ============================================================

			Table eventDetailsTable = new Table(UnitValue.createPercentArray(new float[] { 15f, 85f }));

			eventDetailsTable.setWidth(UnitValue.createPercentValue(100));
			eventDetailsTable.setBorder(NONE);

			addLabelCell(eventDetailsTable, "Event Details", boldFont, black);
			addValueCell(eventDetailsTable, safe(data.getEventName()), basicFont, black);

			document.add(eventDetailsTable);

			// ============================================================
			// SPACE
			// ============================================================

			document.add(new Paragraph(" ").setFontSize(4));

			// ============================================================
			// RATE / MIN PERSON / TOTAL
			// ============================================================

			Table amountTable = new Table(UnitValue.createPercentArray(new float[] { 10f, 20f, 18f, 18f, 15f, 19f }));

			amountTable.setWidth(UnitValue.createPercentValue(100));
			amountTable.setBorder(NONE);

			addLabelCell(amountTable, "Rate", boldFont, black);
			addValueCell(amountTable, safe(""), basicFont, black);
			addLabelCell(amountTable, "Min Granted Person", boldFont, black);
			addValueCell(amountTable, safe(""), basicFont, black);
			addLabelCell(amountTable, "Total Amount", boldFont, black);
			addValueCell(amountTable, safe(""), boldFont, black);

			document.add(amountTable);

			// ============================================================
			// BOOKING AMOUNT / ADVANCE AMOUNT
			// ============================================================

			Table paymentTable = new Table(UnitValue.createPercentArray(new float[] { 18f, 32f, 18f, 32f }));

			paymentTable.setWidth(UnitValue.createPercentValue(100));
			paymentTable.setBorder(NONE);

			addLabelCell(paymentTable, "Booking Amount", boldFont, black);
			addValueCell(paymentTable, safe(""), basicFont, black);
			addLabelCell(paymentTable, "Advance Amount", boldFont, black);
			addValueCell(paymentTable, safe(""), basicFont, black);

			document.add(paymentTable);

			// ============================================================
			// OTHERS
			// ============================================================

			Table othersTable = new Table(UnitValue.createPercentArray(new float[] { 15f, 85f }));

			othersTable.setWidth(UnitValue.createPercentValue(100));
			othersTable.setBorder(NONE);

			addLabelCell(othersTable, "Others", boldFont, black);
			addValueCell(othersTable, safe(""), basicFont, black);

			document.add(othersTable);

			// ============================================================
			// PAYMENT INSTRUCTION
			// ============================================================

			document.add(new Paragraph("Payment Instruction :").setFont(boldFont).setFontSize(9).setFontColor(black)
					.setMarginTop(10).setMarginBottom(3));

			Div instructionDiv = new Div();

			instructionDiv.setBorder(NONE);
			instructionDiv.setMarginLeft(3f);

			addInstruction(instructionDiv, "1. 50% of work order time as booking amount.", basicFont);
			addInstruction(instructionDiv, "2. 50% of work order before one week or programme date.", basicFont);
			addInstruction(instructionDiv,
					"3. There will be no any adjustment in amount for less than minimum granted person.", basicFont);
			addInstruction(instructionDiv, "4. Extra amount will be charged for increasing persons.", basicFont);
			addInstruction(instructionDiv,
					"5. Advance payment on booking cancellation will not be refunded in any condition.", basicFont);
			addInstruction(instructionDiv, "6. GST would have to be paid as per applicable.", basicFont);

			document.add(instructionDiv);

			// ============================================================
			// SIGNATURE SECTION
			// ============================================================

			document.add(new Paragraph(" ").setFontSize(18));

			Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));

			signatureTable.setWidth(UnitValue.createPercentValue(100));
			signatureTable.setBorder(NONE);

			// Customer signature
			Cell customerSignature = new Cell();
			customerSignature.setBorder(NONE);
			customerSignature.setTextAlignment(TextAlignment.LEFT);

			Paragraph customerLine = new Paragraph("________________________");

			customerLine.setFontSize(9);
			customerLine.setMarginBottom(0);

			customerSignature.add(customerLine);

			Paragraph customerText = new Paragraph("Customer's Signature");

			customerText.setFont(boldFont);
			customerText.setFontSize(8.5f);
			customerText.setMarginTop(0);

			customerSignature.add(customerText);

			signatureTable.addCell(customerSignature);

			// Authorised signature
			Cell authorisedSignature = new Cell();
			authorisedSignature.setBorder(NONE);
			authorisedSignature.setTextAlignment(TextAlignment.RIGHT);

			Paragraph authorisedLine = new Paragraph("________________________");

			authorisedLine.setFontSize(9);
			authorisedLine.setMarginBottom(0);

			authorisedSignature.add(authorisedLine);

			Paragraph authorisedText = new Paragraph("Authorised Signature");

			authorisedText.setFont(boldFont);
			authorisedText.setFontSize(8.5f);
			authorisedText.setMarginTop(0);

			authorisedSignature.add(authorisedText);

			signatureTable.addCell(authorisedSignature);

			document.add(signatureTable);

			// ============================================================
			// FOOTER
			// ============================================================

			Paragraph footer = new Paragraph("Customer Satisfaction is our motto");

			footer.setFont(boldFont);
			footer.setFontSize(11);
//			footer.setItalic();
			footer.setTextAlignment(TextAlignment.CENTER);
			footer.setMarginTop(8);

			document.add(footer);

			// ============================================================
			// CLOSE
			// ============================================================
			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + data.getEventNo()
					+ "/work_report.pdf";

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("Failed to generate Work Report", e);
		}
	}

	private void addLabelCell(Table table, String label, PdfFont font, Color color) {

		Cell cell = new Cell();

		cell.setBorder(Border.NO_BORDER);
		cell.setPaddingTop(2f);
		cell.setPaddingBottom(3f);
		cell.setPaddingLeft(2f);
		cell.setPaddingRight(2f);

		Paragraph paragraph = new Paragraph(label);

		paragraph.setFont(font);
		paragraph.setFontSize(10f);
		paragraph.setFontColor(color);
		paragraph.setMargin(0);

		cell.add(paragraph);

		table.addCell(cell);
	}

	private void addValueCell(Table table, String value, PdfFont font, Color color) {

		Cell cell = new Cell();

		cell.setBorder(Border.NO_BORDER);

		cell.setBorderBottom(new DottedBorder(new DeviceRgb(100, 100, 100), 0.6f));

		cell.setPaddingTop(2f);
		cell.setPaddingBottom(1f);
		cell.setPaddingLeft(2f);
		cell.setPaddingRight(2f);

		Paragraph paragraph = new Paragraph(value == null ? "" : value);

		paragraph.setFont(font);
		paragraph.setFontSize(11f);
		paragraph.setFontColor(color);
		paragraph.setMargin(0);

		cell.add(paragraph);

		table.addCell(cell);
	}

	private String safe(Object value) {

		if (value == null) {
			return "";
		}

		return String.valueOf(value);
	}

	private String formatDate(Object date) {

		if (date == null) {
			return "";
		}

		if (date instanceof LocalDate) {
			return ((LocalDate) date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}

		if (date instanceof LocalDateTime) {
			return ((LocalDateTime) date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}

		return String.valueOf(date);
	}

	private String formatOnlyDate(Object value) {

		if (value == null) {
			return "";
		}

		if (value instanceof java.util.Date) {
			return new SimpleDateFormat("dd/MM/yyyy")
					.format((java.util.Date) value);
		}

		if (value instanceof LocalDateTime) {
			return ((LocalDateTime) value)
					.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}

		return value.toString();
	}
	
	private String formatOnlyTime(Object value) {

		if (value == null) {
			return "";
		}

		if (value instanceof java.util.Date) {
			return new SimpleDateFormat("hh:mm a")
					.format((java.util.Date) value);
		}

		if (value instanceof LocalDateTime) {
			return ((LocalDateTime) value)
					.format(DateTimeFormatter.ofPattern("hh:mm a"));
		}

		return value.toString();
	}
	
	private void addInstruction(Div div, String text, PdfFont font) {

		Paragraph paragraph = new Paragraph(text);

		paragraph.setFont(font);
		paragraph.setFontSize(8f);
		paragraph.setMarginTop(0);
		paragraph.setMarginBottom(2f);
		paragraph.setMarginLeft(0);

		div.add(paragraph);
	}
	
	private WorkingReportResponseDto mapEventData(Object[] data) {

		if (data == null || data.length == 0) {
			return null;
		}

		WorkingReportResponseDto dto = new WorkingReportResponseDto();

		dto.setInquiryDate(formatDate(data[0]));
		dto.setPartyName(safe(data[1]));
		dto.setMobileNo(safe(data[2]));
		dto.setPartyAddress(safe(data[3]));
		dto.setEventVenue(safe(data[4]));
		dto.setEventStartDate(formatOnlyDate(data[5]));
		dto.setEventStartTime(formatOnlyTime(data[5]));
		dto.setEventEndDateTime(formatDate(data[6]));
		dto.setEventName(safe(data[7]));
		dto.setEventNo(safe(data[8]));

		return dto;
	}
}
