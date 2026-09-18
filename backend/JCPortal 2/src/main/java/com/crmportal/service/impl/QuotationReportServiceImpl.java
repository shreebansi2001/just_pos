package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventRoomMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.TermsAndConditionFeaturesEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserTermsAndConditionEntity;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRoomMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.TermsAndConditionFeaturesRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserTermsAndConditionRepository;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.QuotationResponseDto;
import com.crmportal.service.AdminTemplateModuleService;
import com.crmportal.service.CommonService;
import com.crmportal.service.QuotationReportService;
import com.crmportal.utility.BackgroundEventHandler;
import com.crmportal.utility.NumberToWordConverter;
import com.crmportal.utility.PageBorderEventHandler;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
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
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.ClearPropertyValue;
import com.itextpdf.layout.properties.FlexDirectionPropertyValue;
import com.itextpdf.layout.properties.FlexWrapPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.splitting.ISplitCharacters;

@Service
public class QuotationReportServiceImpl implements QuotationReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	EventFunctionQuotationServiceImpl eventFunctionQuotationServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	UserTermsAndConditionRepository userTermsRepository;

	@Autowired
	TermsAndConditionFeaturesRepository termsFeaturesRepository;
//	@Autowired
//	EventFunctionMenuAllocationServiceImpl menuAllocationServiceImpl;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	@Autowired
	EventRoomMasterRepository eventRoomMasterRepository;

	@Autowired
	AdminTemplateModuleService adminTemplateModuleService;
	
	@Override
	public String getQuotationReport1(Long eventId, HttpServletRequest re, int lang, Long userid, Integer isInvoice,
			Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isCompanyDetails, Boolean isDecore,
			Integer isNotes, AdminTemplateModuleResponseDto exclusiveTheme, AdminTemplateModuleResponseDto backOffice, Integer showLastPage) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();
			// Hindi
			System.out.println("Loading Hindi font...");
			PdfFont basicFontHindi = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
			System.out.println("Hindi font loaded successfully");

			// Gujarati
			System.out.println("Loading Gujarati font...");
			PdfFont basicFontGujarati = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
			System.out.println("Gujarati font loaded successfully");

			// English
			System.out.println("Loading English font...");
			basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			System.out.println("English font loaded successfully");

			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userid, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String notes = eventData.getNotes() != null ? eventData.getNotes() : "";
			
			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer transportation = eventData.getTransportation() == null ? 0
					: (int) Math.round(eventData.getTransportation());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			Color boxBack = new DeviceRgb(250, 250, 250);
			Color linecolor = new DeviceRgb(210, 210, 210);
			Color dataHeading = new DeviceRgb(0, 91, 168);
			Color fontColor1 = ColorConstants.GRAY;
			Color fontColor2 = ColorConstants.DARK_GRAY;

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			String header = "ESTIMATE PROPOSAL";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "INVOICE";
				header = "TAX INVOICE";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(40, 35, 50, 20);

			float[] columnWidth1 = { 70f, 30f };
			Table cmpData = new Table(UnitValue.createPercentArray(columnWidth1));
			cmpData.setWidth(UnitValue.createPercentValue(100));
			Paragraph data = new Paragraph().add(new Text(companyName).setFontSize(22).setFont(boldFont)).setPadding(0f)
					.setMultipliedLeading(0.8f);
			Cell cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			cell = new Cell(3, 1).add(logo).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			data = new Paragraph()
					.add(new Text(companyAddress).setFontSize(12).setFont(basicFont).setFontColor(fontColor1))
					.setPadding(0).setMultipliedLeading(1);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			data = new Paragraph()
					.add(new Text("Contact: " + countryCode + " " + officeNo + "\nEmail: " + companyEmail
							+ " \nGST No: " + cmpGstNumber).setFontSize(12).setFont(basicFont).setFontColor(fontColor1))
					.setMultipliedLeading(1.2f);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			SolidLine solidLine = new SolidLine(1f);
			solidLine.setColor(linecolor);
			LineSeparator line = new LineSeparator(solidLine);
			line.setWidth(UnitValue.createPercentValue(100));
			line.setMarginTop(5f);
			line.setMarginBottom(10f);
			if (isCompanyDetails == 1) {
				document.add(cmpData);
			} else {
				data = new Paragraph()
						.add(new Text(header).setFont(boldFont).setFontSize(18).setFontColor(ColorConstants.WHITE));

				data.setTextAlignment(TextAlignment.CENTER);
				data.setBackgroundColor(dataHeading);
				data.setPadding(8);

				document.add(data);
			}
			document.add(line);

			Div divData1 = new Div();
			divData1.setBorder(new SolidBorder(linecolor, 1f));
			divData1.setWidth(UnitValue.createPercentValue(93));
			divData1.setPadding(5f);
			divData1.setBorderRadius(new BorderRadius(5f));
			divData1.setBackgroundColor(boxBack);
			divData1.setHorizontalAlignment(HorizontalAlignment.LEFT);

			data = new Paragraph().add(new Text("BILLING ADDRESS").setFont(boldFont).setFontSize(14))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1f).setPaddingLeft(10f);
			divData1.add(data);

			data = new Paragraph().add(new Text("Billed To: ").setFont(boldFont).setFontSize(10))
					.add(new Text(partyName).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.7f).setPadding(0f).setPaddingLeft(10f);
			divData1.add(data);

			data = new Paragraph().add(new Text("Contact: ").setFont(boldFont).setFontSize(10))
					.add(new Text(partyMobile + "\n").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.7f).setPadding(0f).setPaddingLeft(10f);
			divData1.add(data);

			data = new Paragraph().add(new Text("Address: ").setFont(boldFont).setFontSize(10))
					.add(new Text(partyAddress).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.7f).setPadding(0f).setPaddingLeft(10f);
			divData1.add(data);

			data = new Paragraph().add(new Text("GSTIN ").setFont(boldFont).setFontSize(10))
					.add(new Text(partyGst).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.7f).setPadding(0f).setPaddingLeft(10f);
			divData1.add(data);

			Div divData2 = new Div();
			divData2.setBorder(new SolidBorder(linecolor, 1f));
			divData2.setWidth(UnitValue.createPercentValue(93));
			divData2.setPadding(5f);
			divData2.setBackgroundColor(boxBack);
			divData2.setBorderRadius(new BorderRadius(5f));
			divData2.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			data = new Paragraph().add(new Text(reportLabel).setFont(boldFont).setFontSize(14))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.7f).setPadding(0f).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("PI No: ").setFont(boldFont).setFontSize(10))
					.add(new Text(quotationCode).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.7f).setPadding(0f).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("Date: ").setFont(boldFont).setFontSize(10))
					.add(new Text(quotationDueDate + "\n\n\n\n\n\n").setFont(basicFont).setFontSize(10)
							.setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(0.7f).setPadding(0f).setPaddingLeft(10f);
			divData2.add(data);

			float[] columnWidth2 = { 50f, 50f };
			Table partyTbl = new Table(UnitValue.createPercentArray(columnWidth2));
			partyTbl.setWidth(UnitValue.createPercentValue(100));

			cell = new Cell().add(divData1).setBorder(Border.NO_BORDER);
			partyTbl.addCell(cell);

			cell = new Cell().add(divData2).setBorder(Border.NO_BORDER);
			partyTbl.addCell(cell);

			document.add(partyTbl);

			data = new Paragraph().add(new Text("Event Date: ").setFont(boldFont).setFontSize(12))
					.add(new Text(eventDate).setFont(basicFont).setFontSize(12));
			data.setMarginTop(10f);
			data.setMarginBottom(10f);
			data.setTextAlignment(TextAlignment.CENTER);

			document.add(data);

			float[] columnWidth3 = { 5f, 20f, 10f, 10f, 10f, 10f, 10f,10f };
			float[] columnWidth6 = { 5f, 30f, 10f, 10f, 10f, 20f };

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));
			divData1.setBorder(new SolidBorder(linecolor, 1f));
			divData1.setBackgroundColor(dataHeading);
//			divData1.setBorderTopLeftRadius(new BorderRadius(10f));
//			divData1.setBorderTopRightRadius(new BorderRadius(10f));

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));

			data = new Paragraph()
					.add(new Text("No.").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Function").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Person").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

				data = new Paragraph()
						.add(new Text("Extra Person").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));
				cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
				funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Rate").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));

			if (isInvoice != 1 && !hasExtraTax) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice != 1) {
				if (hasExtraTax) {
					data = new Paragraph().add(
							new Text("Extra Tax").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}

				if (hasTaxRate) {
					data = new Paragraph().add(
							new Text("Tax Rate").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}
			}

			data = new Paragraph()
					.add(new Text("Total Price").setFontSize(10).setFont(boldFont).setFontColor(ColorConstants.WHITE));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			System.out.println("Data :- " + funData);
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";

				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();

//				if (Boolean.FALSE.equals(fun.getIs_event_function())) {
//
//					// Sr No
//					data = new Paragraph()
//							.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//					cell.setBorder(new SolidBorder(linecolor, 1f));
//					funTbl2.addCell(cell);
//
//					if(isInvoice == 1) {
//						cell = new Cell(1, 4);
//						data = new Paragraph()
//								.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//						cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//						cell.setBorder(new SolidBorder(linecolor, 1f));
//						funTbl2.addCell(cell);
//					}else {
//						cell = new Cell(1, 2);
//						data = new Paragraph()
//								.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//						cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//						cell.setBorder(new SolidBorder(linecolor, 1f));
//						funTbl2.addCell(cell);
//						
//						data = new Paragraph().add(
//								new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//						cell = new Cell(1, 2).add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//						cell.setBorder(new SolidBorder(linecolor, 1f));
//						funTbl2.addCell(cell);
//					}
//					
//					// Amount
//					data = new Paragraph()
//							.add(new Text("Rs. " + amount).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//					if (isInvoice != 1 && !hasExtraTax) {
//						cell = new Cell(1, 2);
//					} else {
//						cell = new Cell();
//					}
//					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//					cell.setBorder(new SolidBorder(linecolor, 1f));
//					funTbl2.addCell(cell);
//
//					i++;
//					continue;
//				}

				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(linecolor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(linecolor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(functionPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(linecolor, 1f));
				funTbl2.addCell(cell);

					data = new Paragraph().add(
							new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(new SolidBorder(linecolor, 1f));
					funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + rate.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(linecolor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice != 1) {
					if (hasExtraTax) {
						data = new Paragraph()
								.add(new Text(fun.getExtraTax() != null ? fun.getExtraTax().toString() : "")
										.setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(new SolidBorder(linecolor, 1f));
						funTbl2.addCell(cell);
					}

					if (hasTaxRate) {
						data = new Paragraph().add(new Text(fun.getTaxRate() != null ? fun.getTaxRate().toString() : "")
								.setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(new SolidBorder(linecolor, 1f));
						funTbl2.addCell(cell);
					}
				}

				data = new Paragraph().add(new Text("Rs. " + amount.toString()).setFontSize(10).setFont(basicFont)
						.setFontColor(fontColor2));

				if (isInvoice != 1 && !hasTaxRate) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(linecolor, 1f));
				funTbl2.addCell(cell);

				i++;
			}

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));

			divData1.add(funTbl2);
			document.add(divData1);

			data = new Paragraph().add(new Text("Total: Rs." + subTotle).setFont(boldFont).setFontSize(12));
			data.setWidth(UnitValue.createPercentValue(100));
			data.setTextAlignment(TextAlignment.RIGHT);
			data.setPaddingRight(20f);
			data.setMarginTop(10f);
			data.setMarginBottom(10f);
			data.setBorder(new SolidBorder(linecolor, 1f));
			data.setMultipliedLeading(2f);
//			data.setBorderRadius(new BorderRadius(5f));
			data.setBackgroundColor(boxBack);
			document.add(data);

			float[] columnWidth5 = { 50f, 50f };
			Table billTbl = new Table(UnitValue.createPercentArray(columnWidth5));
			billTbl.setWidth(UnitValue.createPercentValue(100));

			// Subtotal
			data = new Paragraph()
					.add(new Text("Subtotal: ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Rs. " + subTotle).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			// Transportation
			if (transportation != null && transportation != 0) {
				data = new Paragraph()
						.add(new Text("Transportation: ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				billTbl.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + transportation).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				billTbl.addCell(cell);
			}

			// Discount
			data = new Paragraph()
					.add(new Text("Discount: ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + discount).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			// Amount After Discount
			double amountAfterDiscount = subTotle - discount;

			data = new Paragraph().add(
					new Text("Amount After Discount: ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + amountAfterDiscount).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph()
					.add(new Text("Cash Amt : ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cashPay).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				billTbl.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt : ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cheqPay).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				billTbl.addCell(cell);
			}

			// SGST
			data = new Paragraph()
					.add(new Text("SGST (" + sgst + "): ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + sgstAmnt).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// CGST
			data = new Paragraph()
					.add(new Text("CGST (" + cgst + "): ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cgstAmnt).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// IGST
			data = new Paragraph()
					.add(new Text("IGST (" + igst + "): ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + igstAmnt).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(
						new Text("Cheque Amt(incl. GST): ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				billTbl.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + chequeAmtInclGst).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				billTbl.addCell(cell);

			}

			if (isAdvance == 1) {
				data = new Paragraph()
						.add(new Text("Advance Payment: ").setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					billTbl.addCell(cell);
				}

				data = new Paragraph().add(
						new Text("Rs. " + advancePayment).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					billTbl.addCell(cell);
				}
			}

			// ==========================================
			// Grand Total
			// ==========================================

			// grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total: ").setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.LEFT).setBorderTop(new SolidBorder(linecolor, 1f))
					.setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			data = new Paragraph().add(new Text("Rs. " + grandTotal).setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.LEFT).setBorderTop(new SolidBorder(linecolor, 1f))
					.setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			// In Words
			data = new Paragraph().add(new Text("In Words : ").setFont(boldFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert(grandTotal) + " Only.").setFont(basicFont)
							.setFontSize(10))
					.setTextAlignment(TextAlignment.LEFT).setBorderTop(new SolidBorder(linecolor, 1f))
					.setPaddingLeft(10f);

			cell = new Cell(1, 2).setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(95));
			divData1.setBorder(new SolidBorder(linecolor, 1f));
			divData1.setPadding(5f);
			divData1.setBackgroundColor(boxBack);
			divData1.setBorderRadius(new BorderRadius(5f));
			divData1.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			divData1.add(billTbl);

			divData2 = new Div();
			divData2.setWidth(UnitValue.createPercentValue(95));
			divData2.setBorder(new SolidBorder(linecolor, 1f));
			divData2.setPadding(5f);
			divData2.setBackgroundColor(boxBack);
			divData2.setBorderRadius(new BorderRadius(5f));
			divData2.setHorizontalAlignment(HorizontalAlignment.LEFT);

			data = new Paragraph().add(new Text("Bank Name: ").setFont(boldFont).setFontSize(10))
					.add(new Text(bankName).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("Acc Holder: ").setFont(boldFont).setFontSize(10))
					.add(new Text(accountHolderName).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("Account No: ").setFont(boldFont).setFontSize(10))
					.add(new Text(accountNo).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("IFSC Code: ").setFont(boldFont).setFontSize(10))
					.add(new Text(ifscCode).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("UPI ID: ").setFont(boldFont).setFontSize(10))
					.add(new Text(upiId).setFont(basicFont).setFontSize(10).setFontColor(fontColor1))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			if (isQrCode == 1) {
				ImageData qrCodeData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + qrCodePath);
				// ImageData qrCodeData =
				// menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/qr.png");

				Image qrCode = new Image(qrCodeData);
				qrCode.setWidth(80);
				qrCode.setHeight(80);
				qrCode.setAutoScale(false);
				qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);

				divData2.add(qrCode);
			}
			float[] columnWidth4 = { 50f, 50f };
			Table amtTbl = new Table(UnitValue.createPercentArray(columnWidth4));
			amtTbl.setWidth(UnitValue.createPercentValue(100));

			cell = new Cell().add(divData2).setBorder(Border.NO_BORDER);
			amtTbl.addCell(cell);

			cell = new Cell().add(divData1).setBorder(Border.NO_BORDER);
			amtTbl.addCell(cell);

			cell = new Cell(1, 2).add(new Paragraph("NOTES : \n" + notes)).setBorder(Border.NO_BORDER).setPaddingTop(10f);
			amtTbl.addCell(cell);

			document.add(amtTbl);
			
			
			solidLine = new SolidLine(1f);
			line = new LineSeparator(solidLine);
			line.setWidth(UnitValue.createPercentValue(25));
			line.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			line.setMarginTop(50f);
			document.add(line);

			data = new Paragraph().add(new Text("Authorized Signatory").setFont(basicFont).setFontSize(10))
					.setTextAlignment(TextAlignment.LEFT);
			data.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			data.setTextAlignment(TextAlignment.RIGHT);
			data.setWidth(UnitValue.createPercentValue(100));
			data.setMarginTop(6f);
			data.setPaddingRight(20f);
			document.add(data);

			if (isTermsCond == 1) {
				UserTermsAndConditionEntity terms = userTermsRepository
						.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(userid, reportType);

				if (terms == null) {
					// handle gracefully
					Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
							.setFontSize(12).setFontColor(ColorConstants.GRAY);

					document.add(noData);
				} else {

					List<TermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = termsFeaturesRepository.findByUserTermsConditionIdAndIsDeleteFalse(terms.getId());

					document.add(new AreaBreak());

					Paragraph termsTitle = new Paragraph("Special Instruction").setFont(boldFont).setFontSize(16)
							.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f);

					document.add(termsTitle);

					ISplitCharacters breakAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true; // break anywhere
						}
					};

					if (features == null || features.isEmpty()) {

						Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
								.setFontSize(12).setFontColor(ColorConstants.GRAY);

						document.add(noData);

					} else {

						int index = 1;

						for (TermsAndConditionFeaturesEntity feature : features) {

							String desc;

							if (lang == 1) {
								basicFont = basicFontHindi;
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								basicFont = basicFontGujarati;
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
									.setMarginBottom(5f).setSplitCharacters((text, glyphPos) -> true);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			Paragraph invisibleContent = null;
			
			Integer lastPageNum = pdfDocument.getNumberOfPages() + 1;
			
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			if(exclusiveTheme != null) {
				ImageData tncPage = null;
				if (exclusiveTheme.getTemplateMaster().getCatBgPage() != null
						&& exclusiveTheme.getTemplateMaster().getCatBgPage().trim().length() != 0) {
					System.out.println("2");
					tncPage = menuPreparationServiceImpl.loadImageFromResource(
							environment.getProperty("app.image.url") + exclusiveTheme.getTemplateMaster().getCatBgPage());
				}

				ImageData tncPage1 = null;
				if (exclusiveTheme.getTemplateMaster().getExtraPage() != null
						&& exclusiveTheme.getTemplateMaster().getExtraPage().trim().length() != 0) {
					System.out.println("4");
					tncPage1 = menuPreparationServiceImpl.loadImageFromResource(
							environment.getProperty("app.image.url") + exclusiveTheme.getTemplateMaster().getExtraPage());
				}

				ImageData lastBgData = null;
				if(exclusiveTheme.getTemplateMaster().getLastMainPage() != null
						&& exclusiveTheme.getTemplateMaster().getLastMainPage().trim().length() != 0) {
					lastBgData = menuPreparationServiceImpl.loadImageFromResource(
							environment.getProperty("app.image.url") + exclusiveTheme.getTemplateMaster().getLastMainPage());
				}

				// ============ Terms And Condition PAGE ============
				if (isNotes == 1 && tncPage != null) {
					bgHandler.setPageBackground(lastPageNum, tncPage);
	
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					invisibleContent = new Paragraph("\u00A0");
					invisibleContent.setFontSize(1);
	
					document.add(invisibleContent);
	
					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
	
				if (isNotes == 1 && tncPage1 != null) {
					bgHandler.setPageBackground(lastPageNum, tncPage1);
	
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					invisibleContent = new Paragraph("\u00A0");
					invisibleContent.setFontSize(1);
	
					document.add(invisibleContent);
					
					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
				
				if (showLastPage == 1 && lastBgData != null) {
					bgHandler.setPageBackground(lastPageNum, lastBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					invisibleContent = new Paragraph("\u00A0");
					invisibleContent.setFontSize(1);
					document.add(invisibleContent);
				}
			}else if(backOffice != null) {
				ImageData tncPage = null;
				if (backOffice.getTemplateMaster().getCatBgPage() != null
						&& backOffice.getTemplateMaster().getCatBgPage().trim().length() != 0) {
					tncPage = menuPreparationServiceImpl.loadImageFromResource(
							environment.getProperty("app.image.url") + backOffice.getTemplateMaster().getCatBgPage());
					
					// ============ Terms And Condition PAGE ============
					if (isNotes == 1 && tncPage != null) {
						bgHandler.setPageBackground(lastPageNum, tncPage);
		
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
						invisibleContent = new Paragraph("\u00A0");
						invisibleContent.setFontSize(1);
		
						document.add(invisibleContent);
		
						lastPageNum = pdfDocument.getNumberOfPages() + 1;
					}
				}
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}

	}

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}

	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + "-" + fileSafe(date.replace("/", "_") + " (" + type.toUpperCase() + ")");
	}

	public String formatDate(LocalDateTime dateTime) {
		return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
	}

	@Override
	public Boolean sendMailQuotationReport(Long eventId, HttpServletRequest re, int lang, Long userId, int i,
			Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isCompanyDetails, Boolean isDecore,
			Integer isNotes, Long exclusiveThemeId, Long backOfficeId, Integer showLastPage) {

		AdminTemplateModuleResponseDto exclusiveTheme = null;
		if(exclusiveThemeId != null && exclusiveThemeId != -1) {
			exclusiveTheme = adminTemplateModuleService.getAdminTemplateModuleById(exclusiveThemeId);	
		}
		
		AdminTemplateModuleResponseDto backOffice = null;
		if(backOfficeId != null && backOfficeId != -1) {
			backOffice = adminTemplateModuleService.getAdminTemplateModuleById(backOfficeId);	
		}
		String invoiceUrl = getQuotationReport1(eventId, re, lang, userId, i, isQrCode, isTermsCond, isAdvance,
				isCompanyDetails, isDecore, isNotes, exclusiveTheme, backOffice, showLastPage);

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		UserBasicDetailsMasterEntity basicDetailsMasterEntity = userBasicDetailsMasterRepository
				.findByUserAndIsDeleteFalse(userMasterEntity)
				.orElseThrow(() -> new RuntimeException("User Basic Details not found with id : " + userId));

		EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));

		PartyMasterEntity partyMasterEntity = partyMasterRepository
				.findByIdAndIsDeleteFalse(eventMasterEntity.getParty().getId()).orElseThrow(() -> new RuntimeException(
						"Party not found with id : " + eventMasterEntity.getParty().getId()));

		return true;
//		try {
//			commonService.sendMailInvoiceReport(partyMasterEntity, invoiceUrl, basicDetailsMasterEntity);
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}

//		return false;
	}

	@Override
	public String generateInvoiceType2(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore) {
		try {
			PdfFont basicFont = null, boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			// Hindi
			System.out.println("Loading Hindi font...");
			PdfFont basicFontHindi = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
			PdfFont boldFontHindi = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			System.out.println("Hindi font loaded successfully");
			// Gujarati
			System.out.println("Loading Gujarati font...");
			PdfFont basicFontGujarati = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
			PdfFont boldFontGujarati = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			System.out.println("Gujarati font loaded successfully");
			// English
			System.out.println("Loading English font...");
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, 1,
					isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			String notesData = eventData.getNotes() == null || eventData.getNotes().isEmpty() ? ""
					: eventData.getNotes();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			Color headerBg = new DeviceRgb(60, 61, 58);
			Color whiteColor = new DeviceRgb(255, 255, 255);
			Color borderColor = new DeviceRgb(173, 173, 173);
			Color balanceBg = new DeviceRgb(245, 244, 242);
			Color grayColor = new DeviceRgb(51, 51, 51);
			Color darkGrayColor = ColorConstants.DARK_GRAY;

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), "Invoice") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(20, 15, 20, 15);

			// ===== HEADER SECTION (Company Info + TAX INVOICE) =====
			float[] headerWidths = { 50f, 50f };
			Table headerTable = new Table(UnitValue.createPercentArray(headerWidths));
			headerTable.setWidth(UnitValue.createPercentValue(100));
			headerTable.setMarginBottom(5f);

			// Left: Company details
			Paragraph address = new Paragraph().setFont(basicFont).setFontSize(10).setMarginTop(0);
			address.add(new Text(companyName + "\n").setFont(boldFont).setFontColor(grayColor).setFontSize(11));
			address.add(companyAddress + "\n");
			address.add("GSTIN " + cmpGstNumber);

			Cell leftHeaderCell = new Cell().add(address).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.TOP);

			// Right: Invoice title
			Paragraph invoiceBlock = new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMarginTop(0);
			invoiceBlock.add(new Text("INVOICE\n").setFont(boldFont).setFontSize(26));
			invoiceBlock.add(new Text("#" + quotationCode + "\n").setFont(boldFont).setFontSize(14));
			invoiceBlock.add(new Text("\nBalance Due\n").setFont(boldFont).setFontColor(grayColor).setFontSize(9));
			invoiceBlock.add(new Text("RS. " + grandTotal).setFont(boldFont).setFontColor(grayColor).setFontSize(14));

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
			billShip.add(new Text(partyName + "\n").setFont(boldFont).setFontColor(grayColor));
			billShip.add(partyAddress + "\n").setFontColor(grayColor);
			billShip.add("GSTIN " + partyGst + "\n\n").setFontColor(grayColor);
			billShip.add(new Text("Ship To\n").setFont(boldFont));
			billShip.add("GSTIN " + partyGst).setFontColor(grayColor);
//			billShip.add("Place Of Supply: Gujarat (24)").setFontColor(grayColor);

			Cell leftSectionCell = new Cell().add(billShip).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.TOP);

			Table innerTable = new Table(UnitValue.createPercentArray(new float[] { 40f, 60f }));
			innerTable.setWidth(UnitValue.createPercentValue(100f));
			innerTable.setFixedLayout();

			Cell cell = new Cell().add(new Paragraph("Invoice Date :").setFont(boldFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
							.setFont(basicFont).setFontColor(grayColor))
					.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Terms :").setFont(boldFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Due on Receipt").setFont(basicFont).setFontColor(grayColor))
					.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Due Date :").setFont(boldFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph(quotationDueDate).setFont(basicFont).setFontColor(grayColor))
					.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			sectionTable.addCell(leftSectionCell);
			sectionTable.addCell(new Cell().add(innerTable).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.MIDDLE));

			document.add(sectionTable);

			Table fnTable = new Table(UnitValue.createPercentArray(new float[] { 5f, 40f, 10f, 10f, 15f, 20f }));
			fnTable.setWidth(UnitValue.createPercentValue(100f));
			fnTable.setFixedLayout();
			fnTable.setMarginTop(5f);

			cell = new Cell().add(new Paragraph("#").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Function").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Person").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Extra").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Rate").setFont(basicFont).setFontColor(whiteColor)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(headerBg)
					.setPaddingTop(5f).setPaddingBottom(5f);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph("Total Price").setFont(basicFont).setFontColor(whiteColor))
					.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
					.setBackgroundColor(headerBg).setPaddingTop(5f).setPaddingBottom(5f).setPaddingRight(10f);
			fnTable.addCell(cell);

			List<QuotationResponseDto> funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);

			Integer idx = 0;
			for (QuotationResponseDto function : funData) {
				cell = new Cell().add(new Paragraph((++idx).toString()).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(
								function.getFunctionName() != null ? function.getFunctionName().toUpperCase() : "")
								.setFont(basicFont).setFontColor(grayColor))
						.setFontSize(10).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(
								function.getFunctionPax() != null ? function.getFunctionPax().toString() : "")
								.setFont(basicFont))
						.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(
								function.getFunctionExtraPax() != null ? function.getFunctionExtraPax().toString() : "")
								.setFont(basicFont))
						.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(function.getRate() != null ? "Rs. " + function.getRate() : "")
								.setFont(basicFont))
						.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(function.getFunctionPax() != null && function.getRate() != null
								? "RS. " + function.getFunctionPax() * function.getRate()
								: "RS. 0.0").setFont(basicFont))
						.setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPaddingTop(5f).setPaddingBottom(5f)
						.setPaddingRight(10f);
				fnTable.addCell(cell);
			}

			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell().add(new Paragraph("Sub Total").setFont(basicFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(2f);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph(subTotle.toString()).setFont(basicFont)).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
					.setPaddingBottom(2f).setPaddingRight(10f);
			fnTable.addCell(cell);

			if (cgstAmnt > 0) {
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));

				cell = new Cell().add(new Paragraph("CGST (" + cgst + ")").setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
						.setPaddingBottom(2f);
				fnTable.addCell(cell);

				cell = new Cell().add(new Paragraph(cgstAmnt.toString()).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
						.setPaddingBottom(2f).setPaddingRight(10f);
				fnTable.addCell(cell);
			}

			if (sgstAmnt > 0) {
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));

				cell = new Cell().add(new Paragraph("SGST (" + sgst + ")").setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
						.setPaddingBottom(2f);
				fnTable.addCell(cell);

				cell = new Cell().add(new Paragraph(sgstAmnt.toString()).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
						.setPaddingBottom(2f).setPaddingRight(10f);
				fnTable.addCell(cell);
			}

			if (igstAmnt > 0) {
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));

				cell = new Cell().add(new Paragraph("IGST (" + igst + ")").setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
						.setPaddingBottom(2f);
				fnTable.addCell(cell);

				cell = new Cell().add(new Paragraph(igstAmnt.toString()).setFont(basicFont)).setFontSize(10)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
						.setPaddingBottom(2f).setPaddingRight(10f);
				fnTable.addCell(cell);
			}

			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell().add(new Paragraph("Total").setFont(boldFont)).setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
					.setPaddingBottom(2f);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph("RS. " + (subTotle + cgstAmnt + sgstAmnt + igstAmnt)).setFont(boldFont))
					.setFontSize(11).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(2f)
					.setPaddingBottom(2f).setPaddingRight(10f);
			fnTable.addCell(cell);

			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell(1, 2).add(new Paragraph("Balance Due").setFont(boldFont)).setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f).setBackgroundColor(balanceBg);
			fnTable.addCell(cell);

			cell = new Cell().add(new Paragraph("RS. " + (grandTotal - advancePayment)).setFont(boldFont))
					.setFontSize(11).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f).setPaddingRight(10f).setBackgroundColor(balanceBg);
			fnTable.addCell(cell);

			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			fnTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			cell = new Cell(1, 3)
					.add(new Paragraph().add(new Text("Total in words:").setFont(boldFont).setFontColor(grayColor))
							.add(NumberToWordConverter.convert(grandTotal - advancePayment) + " Only")
							.setFont(basicFont).setFontColor(darkGrayColor))
					.setFontSize(10).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
					.setPaddingBottom(5f);
			fnTable.addCell(cell);

//			cell = new Cell(1, 2)
//					.add(new Paragraph(NumberToWordConverter.convert(grandTotal) + " Only").setFont(basicFont)
//							.setFontColor(darkGrayColor))
//					.setFontSize(10).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingTop(5f)
//					.setPaddingBottom(5f).setPaddingRight(10f);
//			fnTable.addCell(cell);

			document.add(fnTable);

			// ===== NOTES SECTION =====
			Paragraph notes = new Paragraph().setFont(basicFont).setFontSize(10).setMarginTop(5f);

			notes.add(new Text("Notes\n").setFont(boldFont).setFontSize(11).setFontColor(ColorConstants.BLACK));
			notes.add(new Text(notesData).setTextAlignment(TextAlignment.JUSTIFIED)).setFontColor(grayColor);

			document.add(notes);

			// ===== PAYMENT OPTIONS SECTION =====
//			Div paymentDiv = new Div();
//			paymentDiv.setMarginTop(5f);
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
			Paragraph terms = new Paragraph().setFont(basicFont).setFontSize(10).setMarginTop(10);

			terms.add(new Text("Bank Details\n").setFont(boldFont).setFontSize(10).setFontColor(ColorConstants.BLACK));
			terms.add(bankName.toUpperCase() + "\n").setFontColor(grayColor); // HDFC BANK
			terms.add("AC NO:-" + accountNo + "\n").setFontColor(grayColor); // 50200013422306
			terms.add("BRANCH :- " + branchName.toUpperCase() + "\n").setFontColor(grayColor); // DARPAN SIX ROAD
			terms.add("IFSC CODE :- " + ifscCode.toUpperCase() + "\n").setFontColor(grayColor); // HDFC0001678
			terms.add("AC NAME :- " + accountHolderName.toUpperCase() + "\n").setFontColor(grayColor); // SHREE INFOTECH

			document.add(terms);

			// ===== AUTHORIZED SIGNATURE =====
			Paragraph signature = new Paragraph().setFont(boldFont).setFontSize(10).setMarginTop(10);

			signature.add(new Text("Authorized Signature ").setFont(boldFont));
			signature.add(new Text("________________________________").setFont(basicFont));

			document.add(signature);

			if (isTermsCond == 1) {
				UserTermsAndConditionEntity termsCond = userTermsRepository
						.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(userId, "Invoice");

				if (termsCond == null) {
					// handle gracefully
					Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
							.setFontSize(12).setFontColor(ColorConstants.GRAY);

					document.add(noData);
				} else {

					List<TermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = termsFeaturesRepository.findByUserTermsConditionIdAndIsDeleteFalse(termsCond.getId());

					document.add(new AreaBreak());

					Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(boldFont).setFontSize(16)
							.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f);

					document.add(termsTitle);

					ISplitCharacters breakAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true; // break anywhere
						}
					};

					if (features == null || features.isEmpty()) {

						Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
								.setFontSize(12).setFontColor(ColorConstants.GRAY);

						document.add(noData);

					} else {

						int index = 1;

						for (TermsAndConditionFeaturesEntity feature : features) {

							String desc;

							if (lang == 1) {
								basicFont = basicFontHindi;
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								basicFont = basicFontGujarati;
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
									.setMarginBottom(5f).setSplitCharacters((text, glyphPos) -> true);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}

				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), "Invoice")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}
	}

	@Override
	public String getQuotationReport2(Long eventId, HttpServletRequest re, int lang, Long userid, Integer isInvoice,
			Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isWithPrice, Boolean isDecore) {

		try {

			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();
			// Hindi
			System.out.println("Loading Hindi font...");
			PdfFont basicFontHindi = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
			System.out.println("Hindi font loaded successfully");

			// Gujarati
			System.out.println("Loading Gujarati font...");
			PdfFont basicFontGujarati = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
			System.out.println("Gujarati font loaded successfully");

			// English
			System.out.println("Loading English font...");
			basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			System.out.println("English font loaded successfully");

			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userid, 0,
					isInvoice, isDecore);

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String inquiryDate = eventData.getInquiryDate() == null || eventData.getInquiryDate().isEmpty() ? ""
					: eventData.getInquiryDate();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			Boolean isQuotationLocked = eventData.getIsQuotationLocked() == null ? false
					: eventData.getIsQuotationLocked();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer gTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());
			Integer grandTotal1 = eventData.getGrandTotal() == null ? 0
					: (int) Math.round(eventData.getGrandTotal()) - advancePayment;

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String partyGst = eventData.getPartyGst() != null ? eventData.getPartyGst() : "";

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			Color boxBack = new DeviceRgb(204, 204, 204);
			Color linecolor = new DeviceRgb(210, 210, 210);
			Color dataHeading = new DeviceRgb(0, 91, 168);
			Color fontColor1 = ColorConstants.GRAY;
			Color fontColor2 = ColorConstants.DARK_GRAY;
			Color fontColor3 = ColorConstants.RED;
			Color blackColor = ColorConstants.BLACK;
			Color fontColor4;
			Color whiteColor = ColorConstants.WHITE;

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "TAX INVOICE";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(40, 35, 50, 20);

			Paragraph title = new Paragraph("ESTIMATE - PROPOSAL").setFont(boldFont).setFontSize(18).setUnderline()
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(dataHeading).setFontColor(whiteColor)
					.setMarginBottom(15f);

			document.add(title);

			document.add(new LineSeparator(new SolidLine(0.8f)));

			float[] cols = { 47.5f, 5f, 47.5f };
			Table partyTbl = new Table(UnitValue.createPercentArray(cols));
			partyTbl.setWidth(UnitValue.createPercentValue(100));
//			partyTbl.setBorder(new SolidBorder(linecolor, 1f));
			partyTbl.setMarginTop(5);

			// LEFT CELL
			Cell leftCell = new Cell().setPadding(8).setBorder(new SolidBorder(linecolor, 1f))
					.setBorderRadius(new BorderRadius(15f));

			leftCell.add(new Paragraph().add(new Text("NAME:- ").setFontSize(16))
					.add(new Text(partyName).setFont(basicFont).setFontSize(16)));

			leftCell.add(new Paragraph().add(new Text("Contact No:- ").setFontSize(12))
					.add(new Text(partyMobile).setFontSize(12)));

			leftCell.add(new Paragraph().add(new Text("Email Id:- ").setFontSize(12))
					.add(new Text(partyEmail).setFontSize(12)));

			leftCell.add(new Paragraph().add(new Text("Gst Number:- ").setFontSize(12))
					.add(new Text(partyGst).setFontSize(12)));

			partyTbl.addCell(leftCell);

			// MIDDLE CELL
			Cell middleCell = new Cell().setPadding(8).setBorder(Border.NO_BORDER);
			partyTbl.addCell(middleCell);

			// RIGHT CELL
			Cell rightCell = new Cell().setPadding(8).setBorder(new SolidBorder(linecolor, 1f))
					.setBorderRadius(new BorderRadius(15f));

			rightCell.add(new Paragraph().add(new Text("Event date:- ").setFontSize(14))
					.add(new Text(eventDate).setFontSize(14)));

			rightCell.add(new Paragraph().add(new Text("Enquiry date:- ").setFontSize(14))
					.add(new Text(inquiryDate).setFontSize(14)));

			partyTbl.addCell(rightCell);

			document.add(partyTbl);

			title = new Paragraph("CATERING QUOTATION").setFont(boldFont).setFontSize(14)
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(dataHeading).setFontColor(whiteColor);

			document.add(title);

			float[] widths;

			if (isWithPrice == 1) {
				widths = new float[] { 12f, 22f, 10f, 16f, 13f, 11f, 12f };
			} else {
				widths = new float[] { 12f, 28f, 12f, 22f, 15f, 11f };
			}

			Table funTbl = new Table(UnitValue.createPercentArray(widths));
			funTbl.setWidth(UnitValue.createPercentValue(100));

			float[] width2;
			if (isWithPrice == 1) {
				width2 = new float[] { 12f, 27f, 10f, 14f, 13f, 12f, 12f };
			} else {
				width2 = new float[] { 12f, 28f, 12f, 22f, 15f, 11f };
			}
			Table lockedQuotationDataTable = new Table(UnitValue.createPercentArray(width2));
			lockedQuotationDataTable.setWidth(UnitValue.createPercentValue(100f));
			lockedQuotationDataTable.setFixedLayout();

			Table changesDataTable = new Table(UnitValue.createPercentArray(width2));
			changesDataTable.setWidth(UnitValue.createPercentValue(100f));
			changesDataTable.setFixedLayout();
			changesDataTable.setMarginBottom(15f);

			String[] headers = (isWithPrice == 1)
					? new String[] { "Date", "Particulars", "Time", "Options", "Members", "Rate", "Total" }
					: new String[] { "Date", "Particulars", "Time", "Options", "Members", "Rate" };

			for (String h : headers) {
				Cell c = new Cell().add(new Paragraph(h).setFont(boldFont).setFontSize(11))
						.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setHeight(25).setBorder(new SolidBorder(ColorConstants.BLACK, 1));

				funTbl.addHeaderCell(c);

				lockedQuotationDataTable.addHeaderCell(c);
				changesDataTable.addHeaderCell(c);
			}

			double grandTotal = 0;
			double finalQuotationGrandTotal = 0;
			double addOnItemGrandTotal = 0;
			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();

			List<QuotationResponseDto> funData = new ArrayList<>();
			funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			System.out.println("Data :- " + funData);
			for (QuotationResponseDto fun : funData) {
				Boolean isItemLocked = fun.getIsItemLocked() == null ? false : fun.getIsItemLocked();

				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";

				String funTime = fun.getFunctionTime() != null ? fun.getFunctionTime() : "";

				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();
				BigDecimal extraRate = fun.getTaxRate() == null ? BigDecimal.ZERO : fun.getTaxRate();

				String options = fun.getOptions() != null ? fun.getOptions() : "";
				String members = "";
				if (extraPax != 0) {
					members = functionPax + "+" + extraPax + "(Extra)";
				} else {
					members = functionPax.toString();
				}

				if (isQuotationLocked && !isItemLocked) {
					fontColor4 = fontColor3;
				} else {
					fontColor4 = blackColor;
				}
				int count = 0;
				if (Boolean.TRUE.equals(fun.getIs_event_function())) {
					Cell dateCell = new Cell().add(new Paragraph(funDate)).setBackgroundColor(dataHeading)
							.setFontColor(whiteColor).setTextAlignment(TextAlignment.CENTER)
							.setBorder(new SolidBorder(1));

					funTbl.addCell(dateCell);

					Cell funNameCell = new Cell().add(new Paragraph(functionName)).setBorder(new SolidBorder(1))
							.setTextAlignment(TextAlignment.LEFT);
					funTbl.addCell(funNameCell.setFontColor(fontColor4));

					Cell funTimeCell = new Cell().add(new Paragraph(funTime)).setBorder(new SolidBorder(1))
							.setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(funTimeCell.setFontColor(fontColor4));

					Cell optionCell = new Cell().add(new Paragraph(options)).setBorder(new SolidBorder(1))
							.setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(optionCell.setFontColor(fontColor4));

					Cell memberCell = new Cell().add(new Paragraph(members)).setBorder(new SolidBorder(1))
							.setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(memberCell.setFontColor(fontColor4));

					Cell rateCell = null;
					// if (isWithPrice == 1) {
					rateCell = new Cell().add(new Paragraph(String.valueOf(rate))).setBorder(new SolidBorder(1))
							.setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(rateCell.setFontColor(fontColor4));
					// }
					if (isWithPrice == 1) {
						funTbl.addCell(new Cell().add(new Paragraph(String.valueOf(amount)))
								.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));
					}

					if (isQuotationLocked) {
						if (isItemLocked) {
							lockedQuotationDataTable.addCell(dateCell);
							lockedQuotationDataTable.addCell(funNameCell);
							lockedQuotationDataTable.addCell(funTimeCell);
							lockedQuotationDataTable.addCell(optionCell);

							Cell paxCell = new Cell().add(new Paragraph(functionPax.toString()))
									.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER)
									.setFontColor(fontColor4);
							lockedQuotationDataTable.addCell(paxCell);

							// if (isWithPrice == 1) {
							lockedQuotationDataTable.addCell(rateCell);
							// }

							if (isWithPrice == 1) {
								int amount1 = (functionPax * rate) + extraRate.intValue();
								lockedQuotationDataTable.addCell(new Cell().add(new Paragraph(String.valueOf(amount1)))
										.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));

								finalQuotationGrandTotal += amount1;
							}

							if (extraPax != 0) {
								changesDataTable.addCell(dateCell);
								changesDataTable.addCell(funNameCell);
								changesDataTable.addCell(funTimeCell);
								changesDataTable.addCell(optionCell);

								Cell extraPaxCell1 = new Cell().add(new Paragraph(extraPax.toString()))
										.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER)
										.setFontColor(fontColor4);
								changesDataTable.addCell(extraPaxCell1);

								// if (isWithPrice == 1) {
								changesDataTable.addCell(rateCell);
								// }

								if (isWithPrice == 1) {
									int amount2 = (extraPax * rate) + extraRate.intValue();
									changesDataTable.addCell(new Cell()
											.add(new Paragraph(String.valueOf(String.valueOf(amount2))))
											.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));

									addOnItemGrandTotal += amount2;
								}
							}
						} else if (extraPax != 0) {
							changesDataTable.addCell(dateCell);
							changesDataTable.addCell(funNameCell);
							changesDataTable.addCell(funTimeCell);
							changesDataTable.addCell(optionCell);

							Cell extraPaxCell = new Cell().add(new Paragraph(extraPax.toString()))
									.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER)
									.setFontColor(fontColor4);
							changesDataTable.addCell(extraPaxCell);

							// if (isWithPrice == 1) {
							changesDataTable.addCell(rateCell);
							// }

							if (isWithPrice == 1) {
								int amount3 = (extraPax * rate) + extraRate.intValue();
								changesDataTable.addCell(new Cell().add(new Paragraph(String.valueOf(amount3)))
										.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));

								addOnItemGrandTotal += amount3;
							}
						}
					}

				} else {

					Cell totalTextCell = new Cell(1, (isWithPrice == 1) ? 6 : 5)
							.add(new Paragraph(String.valueOf(functionName)).setFont(boldFont).setFontSize(12))
							.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.LEFT);

					funTbl.addCell(totalTextCell.setFontColor(fontColor4));

					Cell totalAmountCell = new Cell()
							.add(new Paragraph(
									String.valueOf(fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue()))
									.setFont(boldFont).setFontSize(12))
							.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT);

					funTbl.addCell(totalAmountCell.setFontColor(fontColor4));

					if (isQuotationLocked) {
						Cell totalTextCell1 = new Cell(1, (isWithPrice == 1) ? 6 : 5)
								.add(new Paragraph(String.valueOf(functionName)).setFont(boldFont).setFontSize(12))
								.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.LEFT);

						int amount4 = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
						Cell totalAmountCell1 = new Cell()
								.add(new Paragraph(String.valueOf(amount4)).setFont(boldFont).setFontSize(12))
								.setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT);
						if (isItemLocked) {
							lockedQuotationDataTable.addCell(totalTextCell1);
							lockedQuotationDataTable.addCell(totalAmountCell1);

							finalQuotationGrandTotal += amount4;
						} else {
							changesDataTable.addCell(totalTextCell1);
							changesDataTable.addCell(totalAmountCell1);

							addOnItemGrandTotal += amount4;
						}
					}
				}

				grandTotal += amount;
			}

			if (isWithPrice == 1) {
				// Total
				Cell totalCell = new Cell(1, 6)
						.add(new Paragraph("Total \n (Taxes will be applicable as Extra)").setFont(boldFont)
								.setBackgroundColor(dataHeading).setFontColor(whiteColor).setFontSize(11).setPadding(5))
						.setTextAlignment(TextAlignment.LEFT).setBorder(new SolidBorder(dataHeading, 1)).setPadding(0);

				funTbl.addCell(totalCell);

				Cell totalAmountCell = new Cell().add(new Paragraph(String.valueOf((int) grandTotal)).setFont(boldFont))
						.setBackgroundColor(dataHeading).setFontColor(whiteColor).setFontSize(12)
						.setBorder(new SolidBorder(dataHeading, 1)).setTextAlignment(TextAlignment.RIGHT);

				funTbl.addCell(totalAmountCell);

				// Total
				Cell totalCell1 = new Cell(1, 6)
						.add(new Paragraph("Total").setFont(boldFont).setFontSize(11).setPadding(5))
						.setTextAlignment(TextAlignment.LEFT).setBorder(new SolidBorder(dataHeading, 1)).setPadding(0)
						.setBackgroundColor(dataHeading).setFontColor(whiteColor);

				lockedQuotationDataTable.addCell(totalCell1);

				Cell totalAmountCell1 = new Cell()
						.add(new Paragraph(String.valueOf((int) finalQuotationGrandTotal)).setFont(boldFont))
						.setBackgroundColor(dataHeading).setFontColor(whiteColor)
						.setBorder(new SolidBorder(dataHeading, 1)).setTextAlignment(TextAlignment.RIGHT);

				lockedQuotationDataTable.addCell(totalAmountCell1);

				if (changesDataTable.getNumberOfRows() > 0) {
					// Total
					Cell totalCell2 = new Cell(1, 6)
							.add(new Paragraph("Total").setFont(boldFont).setBackgroundColor(dataHeading)
									.setFontColor(whiteColor).setFontSize(11).setPadding(5))
							.setTextAlignment(TextAlignment.LEFT).setBorder(new SolidBorder(dataHeading, 1))
							.setPadding(0).setBackgroundColor(dataHeading).setFontColor(whiteColor);

					changesDataTable.addCell(totalCell2);

					Cell totalAmountCell2 = new Cell()
							.add(new Paragraph(String.valueOf((int) addOnItemGrandTotal)).setFont(boldFont))
							.setBackgroundColor(dataHeading).setFontColor(whiteColor)
							.setBorder(new SolidBorder(dataHeading, 1)).setTextAlignment(TextAlignment.RIGHT)
							.setPadding(0);

					changesDataTable.addCell(totalAmountCell2);
				}
			}

			Cell cell = null;
			if (eventData.getBanquet_hall_id() == 0) {
				funTbl.setMarginBottom(15f);
				document.add(funTbl);
			} else {
				if (!isQuotationLocked) {
					funTbl.setMarginBottom(15f);
					document.add(funTbl);
				} else {
					if (lockedQuotationDataTable.getNumberOfRows() > 0) {
						document.add(new Paragraph("Final Quotation : ").simulateBold());
						document.add(lockedQuotationDataTable);
					}

					if (changesDataTable.getNumberOfRows() > 0) {
						document.add(new Paragraph("Addon Items : ").simulateBold().setFontColor(fontColor3));
						document.add(changesDataTable);
					}

					Table bifurcationTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
					bifurcationTable.setWidth(UnitValue.createPercentValue(100f));
					bifurcationTable.setFixedLayout();
					bifurcationTable.setMarginBottom(15f);

					Map<String, String> bifurcationData = new LinkedHashMap<>();
					bifurcationData.put("FINAL AMOUNT", String.valueOf(finalQuotationGrandTotal));
					bifurcationData.put("ADDON AMOUNT", String.valueOf(addOnItemGrandTotal));
					bifurcationData.put("TOTAL AMOUNT", String.valueOf(finalQuotationGrandTotal + addOnItemGrandTotal));

					for (Entry<String, String> data : bifurcationData.entrySet()) {
						cell = new Cell().add(new Paragraph(data.getKey())).setBackgroundColor(dataHeading)
								.setFontColor(whiteColor).setTextAlignment(TextAlignment.CENTER)
								.setBorder(new SolidBorder(1));
						bifurcationTable.addCell(cell);

						cell = new Cell().add(new Paragraph(data.getValue())).setTextAlignment(TextAlignment.CENTER)
								.setBorder(new SolidBorder(1));
						bifurcationTable.addCell(cell);
					}

					if (isWithPrice == 1) {
						document.add(new Paragraph("Bifurcation : ").simulateBold());
						document.add(bifurcationTable);
					}
				}
			}

//			Div divData1 = new Div();
//			divData1.setWidth(UnitValue.createPercentValue(100));

//			divData1.add(funTbl2);
//			document.add(divData1);

			float[] columnWidth5 = { 50f, 50f };
			Table billTbl = new Table(UnitValue.createPercentArray(columnWidth5));
			billTbl.setWidth(UnitValue.createPercentValue(100));

			// Subtotal
			Paragraph data = new Paragraph()
					.add(new Text("Subtotal: ").setFont(basicFont).setFontSize(11).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Rs. " + subTotle).setFont(basicFont).setFontSize(11).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			// Discount
			data = new Paragraph()
					.add(new Text("Discount: ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + discount).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			// Amount After Discount
			double amountAfterDiscount = subTotle - discount;

			data = new Paragraph().add(
					new Text("Amount After Discount: ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + amountAfterDiscount).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph()
					.add(new Text("Cash Amt : ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cashPay).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				billTbl.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt : ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cheqPay).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				billTbl.addCell(cell);
			}

			// SGST
			data = new Paragraph()
					.add(new Text("SGST (" + sgst + "): ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + sgstAmnt).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// CGST
			data = new Paragraph()
					.add(new Text("CGST (" + cgst + "): ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cgstAmnt).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// IGST
			data = new Paragraph()
					.add(new Text("IGST (" + igst + "): ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + igstAmnt).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(
						new Text("Cheque Amt(incl. GST): ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				billTbl.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + chequeAmtInclGst).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				billTbl.addCell(cell);

			}

			if (isAdvance == 1) {
				data = new Paragraph()
						.add(new Text("Advance Payment: ").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					billTbl.addCell(cell);
				}

				data = new Paragraph().add(
						new Text("Rs. " + advancePayment).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					billTbl.addCell(cell);
				}
			}

			// ==========================================
			// Grand Total
			// ==========================================

			// grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total: ").setFont(boldFont).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT).setBorderTop(new SolidBorder(linecolor, 1f))
					.setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Rs. " + (isAdvance == 1 ? grandTotal1 : gTotal)).setFont(boldFont).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT).setBorderTop(new SolidBorder(linecolor, 1f))
					.setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			// In Words
			data = new Paragraph().add(new Text("In Words : ").setFont(boldFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert(isAdvance == 1 ? grandTotal1 : gTotal) + " Only.")
							.setFont(basicFont).setFontSize(10))
					.setTextAlignment(TextAlignment.LEFT).setBorderTop(new SolidBorder(linecolor, 1f))
					.setPaddingLeft(10f);

			cell = new Cell(1, 2).setBorder(Border.NO_BORDER).add(data);
			billTbl.addCell(cell);

			Div divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(95));
			divData1.setBorder(new SolidBorder(linecolor, 1f));
			divData1.setPadding(5f);
			divData1.setBackgroundColor(whiteColor);
//			divData1.setBorderRadius(new BorderRadius(5f));
			divData1.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			divData1.add(billTbl);

			Div divData2 = new Div();
			divData2.setWidth(UnitValue.createPercentValue(95));
			divData2.setBorder(new SolidBorder(linecolor, 1f));
			divData2.setPadding(5f);
			divData2.setBackgroundColor(whiteColor);
//			divData2.setBorderRadius(new BorderRadius(5f));
			divData2.setKeepTogether(true);
			divData2.setHorizontalAlignment(HorizontalAlignment.LEFT);

			data = new Paragraph().add(new Text("Bank Name: ").setFont(boldFont).setFontSize(12f))
					.add(new Text(bankName).setFont(basicFont).setFontSize(12f).setFontColor(blackColor))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("Acc Holder: ").setFont(boldFont).setFontSize(12f))
					.add(new Text(accountHolderName).setFont(basicFont).setFontSize(12f).setFontColor(blackColor))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("Account No: ").setFont(boldFont).setFontSize(12f))
					.add(new Text(accountNo).setFont(basicFont).setFontSize(12f).setFontColor(blackColor))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("IFSC Code: ").setFont(boldFont).setFontSize(12f))
					.add(new Text(ifscCode).setFont(basicFont).setFontSize(12f).setFontColor(blackColor))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("UPI ID: ").setFont(boldFont).setFontSize(12f))
					.add(new Text(upiId).setFont(basicFont).setFontSize(12f).setFontColor(blackColor))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			data = new Paragraph().add(new Text("GSTIN ").setFont(boldFont).setFontSize(12f))
					.add(new Text(cmpGstNumber).setFont(basicFont).setFontSize(12f).setFontColor(blackColor))
					.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
			divData2.add(data);

			if (isQrCode == 1) {
				ImageData qrCodeData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + qrCodePath);
				// ImageData qrCodeData =
				// menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/qr.png");

				Image qrCode = new Image(qrCodeData);
				qrCode.setWidth(80);
				qrCode.setHeight(80);
				qrCode.setAutoScale(false);
				qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);

				divData2.add(qrCode);
			}
			float[] columnWidth4 = { 50f, 50f };
			Table amtTbl = new Table(UnitValue.createPercentArray(columnWidth4));
			amtTbl.setWidth(UnitValue.createPercentValue(100));

			cell = new Cell().add(divData2).setBorder(Border.NO_BORDER);
			amtTbl.addCell(cell);

			if (isWithPrice == 1) {
				cell = new Cell().add(divData1).setBorder(Border.NO_BORDER);
				amtTbl.addCell(cell);
			}

			document.add(amtTbl);

			SolidLine solidLine = new SolidLine(1f);
			LineSeparator line = new LineSeparator(solidLine);
			line.setWidth(UnitValue.createPercentValue(25));
			line.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			line.setMarginTop(50f);
			document.add(line);

			data = new Paragraph().add(new Text("Authorized Signatory").setFont(basicFont).setFontSize(10))
					.setTextAlignment(TextAlignment.LEFT);
			data.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			data.setTextAlignment(TextAlignment.RIGHT);
			data.setWidth(UnitValue.createPercentValue(100));
			data.setMarginTop(6f);
			data.setPaddingRight(20f);
			document.add(data);

			/* Notes */
			document.add(
					new Paragraph("Notes : -").setFont(boldFont).setFontSize(12).setMarginTop(10).setMarginBottom(5));

			String notesText = eventData.getNotes(); // notes separated by \n

			String[] noteArray = notesText.split("\\n");

			for (int i = 0; i < noteArray.length; i++) {
				document.add(new Paragraph(noteArray[i]));
			}

			if (eventData.getBanquet_hall_id() != 0) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				title = new Paragraph("ESTIMATE - PROPOSAL").setFont(boldFont).setFontSize(14).setUnderline()
						.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(dataHeading)
						.setFontColor(whiteColor);

				document.add(title);

				document.add(new LineSeparator(new SolidLine(0.8f)));

				title = new Paragraph("VENUE QUOTATION").setFont(boldFont).setFontSize(14)
						.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(dataHeading)
						.setFontColor(whiteColor);

				document.add(title);

				float[] venueWidths = { 20f, 80f };

				Table venueTbl = new Table(UnitValue.createPercentArray(venueWidths));
				venueTbl.setWidth(UnitValue.createPercentValue(100));

				venueTbl.addHeaderCell(new Cell().add(new Paragraph("Product").setFont(boldFont))
						.setTextAlignment(TextAlignment.LEFT).setBackgroundColor(dataHeading).setFontColor(whiteColor));

				venueTbl.addHeaderCell(new Cell().add(new Paragraph("Details").setFont(boldFont))
						.setTextAlignment(TextAlignment.LEFT).setBackgroundColor(dataHeading).setFontColor(whiteColor));

				for (QuotationResponseDto fun : funData) {

					String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";

					String funTime = fun.getFunctionTime() != null ? fun.getFunctionTime() : "";

					String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
							: fun.getFunctionName();

					if (Boolean.TRUE.equals(fun.getIs_event_function())) {

						List<String> venueName = banquetHallShiftBookingRepository
								.findVenueNameByEventAndFunction(eventData.getEventId(), fun.getEventFunctionId());

						String venuesName = String.join(", ", venueName);

						venueTbl.addCell(
								getProductCell(String.valueOf(functionName), boldFont, dataHeading, whiteColor));

						venueTbl.addCell(new Cell().add(new Paragraph().add("Venue :- " + venuesName + "\n")
								.add("Date :- " + funDate + " - ").add("Time :- " + funTime)));
					}
				}

				if (userid != 299) {
					venueTbl.addCell(getProductCell("Pooja Room", boldFont, dataHeading, whiteColor));

					venueTbl.addCell(new Cell().add(new Paragraph(String.valueOf(eventData.getPoojaRoom()))));
				}
				venueTbl.addCell(getProductCell("Security", boldFont, dataHeading, whiteColor));

				venueTbl.addCell(
						new Cell().add(new Paragraph("Well Dressed Security on Entry Points & Parking Guards.\n"
								+ "Full Premises Under CCTV Surveillance.").simulateBold()));

				venueTbl.addCell(getProductCell("Grand Parking", boldFont, dataHeading, whiteColor));

				StringBuilder parkingDetails = new StringBuilder();

				parkingDetails.append("Ample Car Parking Space of 1,00,000 Sq.ft.");

				if (userid == 299) {
					parkingDetails.append(
							"\nPlease Note that valet parking at the event venue is not managed by the hotel, it need to be arranged directly.");
				}

				venueTbl.addCell(new Cell().add(new Paragraph(parkingDetails.toString()).simulateBold()));

				if (userid != 299) {
					venueTbl.addCell(getProductCell("Iron Service", boldFont, dataHeading, whiteColor));

					venueTbl.addCell(
							new Cell().add(new Paragraph(String.valueOf(eventData.getIronService())).simulateBold()));
				}

				venueTbl.addCell(getProductCell("Rooms", boldFont, dataHeading, whiteColor));

				List<EventRoomMasterEntity> rooms = eventRoomMasterRepository.findByEvent_Id(eventId);

				StringBuilder roomDetails = new StringBuilder();

				roomDetails.append("Rooms:-\n");
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				for (EventRoomMasterEntity room : rooms) {

					roomDetails.append(room.getQty()).append(" ROOMS ").append(room.getRoom().getNameEnglish())
							.append("\n");

					roomDetails.append("- Check In : ").append(dateFormatter.format(room.getBookingdate()))
							.append("  Time : 11:00 AM").append("\n");

					roomDetails.append("- Check Out : ").append(dateFormatter.format(room.getBookingcheckoutdate()))
							.append("  Time : ").append("\n\n");
				}

				if (userid == 299) {
					roomDetails.append("Room Amenities :\n")
							.append("- King Size Beds/Personal Balcony/Attached Living Room\n")
							.append("- Kitchen/Dinning Area/Private Garden/Plunge Pool\n")
							.append("- In Room Dining/internal Buggy Transphers\n")
							.append("- Personal Safe Lock/Air Conditioning/Firde/Hair Dryer/Television\n")
							.append("- Kimirica Herbal Bathroom Ameneties/Pillow Menu/On Call Service/King Size Beds/Twin Beds\n")
							.append("- In-Room Dining Menu (By Virtual Kitchen)\n")
							.append("- Herbal Bathroom Amenities\n").append("- Personal Safe Lock\n")
							.append("- Air Conditioning\n").append("- Refrigerator\n").append("- Television\n")
							.append("- Herbal Bathroom Amenities\n").append("- Extra Bed Facility (Resticted)\n")
							.append("- On Call Service\n\n").append("Facilies;\n")
							.append("- Eco Friendly Resort/Surrounded by scenic beauty of Hills & Nature\n")
							.append("- Wide Roads & Pathways for Morning walk/CCTV Security system\n")
							.append("- Security Guards/8-Vintage Cars (EV) for internal transportation of your guests\n")
							.append("- Electricity backup/Vallet Parking Only for In-House Guest At Lobby Area.\n\n")
							.append("Experiences:\n")
							.append("Hiking, Golf Garden, Chai Adda, Massage Chairs (Spa), Nature Walk, Swimming Pool, Lagoon Poll, Nature’s Library.")
							.append("Sports and recreation: Games Rooms/Kids Play Area/Pickle Ball Court/Badminton/Cricket Turf/Vally Ball, bicycle And walking paths.\n\n");
				} else {
					roomDetails.append("Room Amenities :\n").append("- King Size Beds/Twin Beds\n")
							.append("- In-Room Dining Menu (By Virtual Kitchen)\n").append("- Personal Safe Lock\n")
							.append("- Air Conditioning\n").append("- Refrigerator\n").append("- Television\n")
							.append("- Herbal Bathroom Amenities\n").append("- Extra Bed Facility (Restricted)\n")
							.append("- On call Service");
				}

				venueTbl.addCell(new Paragraph(roomDetails.toString()).simulateBold());

				venueTbl.addCell(new Cell().add(new Paragraph("OFFER").setFont(boldFont))
						.setBackgroundColor(dataHeading).setFontColor(whiteColor));

				venueTbl.addCell(new Cell().add(
						new Paragraph("Rs. " + eventData.getVenueTotal() + "/-\n(Taxes will be applicable as extra)")
								.setFont(boldFont))
						.setBackgroundColor(dataHeading).setFontColor(whiteColor));
				document.add(venueTbl);

				document.add(new Paragraph("\nVenue Notes :-").setFont(boldFont));

				document.add(new Paragraph(eventData.getVenueRemark()));
			}

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			if (eventData.getBanquet_hall_id() != 0) {
//				ImageData img1 = menuPreparationServiceImpl
//				.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
				ImageData img1 = menuPreparationServiceImpl
						.loadImageFromResource("/flipbook/pages/BhandariResort_proposal.png");
				document.add(new Image(img1));
			} else {
//				ImageData img1 = menuPreparationServiceImpl
//				.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
				ImageData img1 = menuPreparationServiceImpl
						.loadImageFromResource("/flipbook/pages/BhandariCatering_proposal.png");
				document.add(new Image(img1));
			}

			document.close();
			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}

	}

	private Cell getProductCell(String text, PdfFont boldFont, Color dataHeading, Color whiteColor) {
		return new Cell().add(new Paragraph(text).setFont(boldFont)).setBackgroundColor(dataHeading)
				.setFontColor(whiteColor).setTextAlignment(TextAlignment.LEFT);
	}

	private static final float[] HEADER_COLUMNS = { 25F, 25F, 25F, 25F };
	private static final float[] TABLE_COLUMNS = { 55F, 15F, 15F, 15F };
	private static final float[] TOTAL_COLUMNS = { 80F, 20F };
	private static final float[] SIGN_COLUMNS = { 50F, 50F };

	private static final String BUFFET_TITLE = "ESTIMATED BUFFET AMOUNT";
	private static final String DECOR_TITLE = "OTHER ESTIMATE AMOUNT";

	@Override
	public String getQuotationReport3(Long eventId, HttpServletRequest request, int lang, Long userId, int isInvoice,
			Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isWithPrice, Boolean isDecore,
			Integer isCombo, Integer isOnePage, Integer isCompanyDetails) {

		try {

			boolean combined = Integer.valueOf(1).equals(isCombo);
			boolean onePage = Integer.valueOf(1).equals(isOnePage);

			return generateQuotationPdf(eventId, request, userId, isInvoice, isDecore, combined, onePage, isQrCode,
					isTermsCond, isAdvance, isCompanyDetails);

		} catch (Exception ex) {
			throw new RuntimeException("Quotation Report Generation Failed", ex);
		}
	}

	private String generateQuotationPdf(Long eventId, HttpServletRequest request, Long userId, int isInvoice,
			Boolean isDecore, boolean isCombined, boolean onePage, Integer isQrCode, Integer isTermsCond,
			Integer isAdvance, Integer isCompanyDetails) throws Exception {

		EventMasterEntity eventMasterEntity = eventMasterRepository.findById(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found"));

		String eventNo = eventMasterEntity.getEventNo();

		File pdfFile = createPdfFile(request, eventNo, eventMasterEntity.getParty().getNameEnglish(),
				eventMasterEntity.getEventStartDateTime(), isInvoice);

		try (PdfWriter writer = new PdfWriter(pdfFile);
				PdfDocument pdfDocument = new PdfDocument(writer);
				Document document = new Document(pdfDocument, PageSize.A4)) {

			document.setMargins(20F, 20F, 20F, 20F);

			PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			QuotationResponseDto buffetDto = null;
			QuotationResponseDto decorDto = null;

			if (isCombined) {

				// SECTION A - BUFFET
				buffetDto = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, isInvoice, false);

				addCompanyOrTitleHeader(document, buffetDto, isCompanyDetails, BUFFET_TITLE, normalFont, boldFont);

				renderSection(document, buffetDto, isInvoice, false, BUFFET_TITLE, normalFont, boldFont, false);

				// ONLY BREAK PAGE WHEN REQUIRED
				if (!onePage) {
					document.add(new AreaBreak());
				} else {
					document.add(new Paragraph("\n\n"));
				}

				// SECTION B - DECOR
				decorDto = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, isInvoice, true);

				addCompanyOrTitleHeader(document, decorDto, isCompanyDetails, DECOR_TITLE, normalFont, boldFont);

				renderSection(document, decorDto, isInvoice, true, DECOR_TITLE, normalFont, boldFont, false);

				// GRAND TOTAL (A + B)
				addCombinedGrandTotal(document, buffetDto, decorDto, normalFont, boldFont);

			} else {

				boolean decor = Boolean.TRUE.equals(isDecore);

				QuotationResponseDto dto = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, isInvoice,
						decor);

				addCompanyOrTitleHeader(document, dto, isCompanyDetails, decor ? DECOR_TITLE : BUFFET_TITLE, normalFont,
						boldFont);

				renderSection(document, dto, isInvoice, decor, decor ? DECOR_TITLE : BUFFET_TITLE, normalFont, boldFont,
						false);

				buffetDto = dto;
			}

			addSignatureSection(document, normalFont);

			// QR CODE + BANK/PAYMENT DETAILS (once, at the end)
			if (Integer.valueOf(1).equals(isQrCode)) {
				addQrCodeSection(document, isCombined ? buffetDto : buffetDto, normalFont, boldFont);
			}

			// TERMS & CONDITIONS (once, at the end, English only)
			if (Integer.valueOf(1).equals(isTermsCond)) {
				addTermsAndConditions(document, userId, isInvoice, normalFont, boldFont);
			}
		}

		return buildDownloadUrl(eventNo, eventMasterEntity.getParty().getNameEnglish(),
				eventMasterEntity.getEventStartDateTime(), isInvoice);
	}

	// =========================================================
	// COMPANY DETAILS HEADER (mirrors getQuotationReport1's isCompanyDetails
	// toggle)
	// =========================================================
	private void addCompanyOrTitleHeader(Document document, QuotationResponseDto dto, Integer isCompanyDetails,
			String fallbackTitle, PdfFont normalFont, PdfFont boldFont) {

		Color linecolor = new DeviceRgb(210, 210, 210);
		Color dataHeading = new DeviceRgb(0, 91, 168);
		Color fontColor1 = ColorConstants.GRAY;

		if (Integer.valueOf(1).equals(isCompanyDetails)) {

			String companyName = dto.getCompanyName() == null ? "" : dto.getCompanyName();
			String companyAddress = dto.getCompanyAddress() == null ? "" : dto.getCompanyAddress();
			String countryCode = dto.getCountryCode() == null ? "" : dto.getCountryCode();
			String officeNo = dto.getOfficeNo() == null ? "" : dto.getOfficeNo();
			String companyEmail = dto.getCompanyEmail() == null ? "" : dto.getCompanyEmail();
			String companyLogo = dto.getCompanyLogo() == null ? "" : dto.getCompanyLogo();

			float[] columnWidth1 = { 70f, 30f };
			Table cmpData = new Table(UnitValue.createPercentArray(columnWidth1));
			cmpData.setWidth(UnitValue.createPercentValue(100));

			Paragraph data = new Paragraph().add(new Text(companyName).setFontSize(22).setFont(boldFont)).setPadding(0f)
					.setMultipliedLeading(0.8f);
			Cell cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			try {
				ImageData img = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
				Image logo = new Image(img);
				logo.setWidth(UnitValue.createPercentValue(100));
				logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
				cell = new Cell(3, 1).add(logo).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
						.setPaddingLeft(10f);
			} catch (Exception e) {
				cell = new Cell(3, 1).setBorder(Border.NO_BORDER);
			}
			cmpData.addCell(cell);

			data = new Paragraph()
					.add(new Text(companyAddress).setFontSize(12).setFont(normalFont).setFontColor(fontColor1))
					.setPadding(0).setMultipliedLeading(1);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			data = new Paragraph()
					.add(new Text("Contact: " + countryCode + " " + officeNo + " | Email: " + companyEmail)
							.setFontSize(12).setFont(normalFont).setFontColor(fontColor1))
					.setMultipliedLeading(0.8f);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			document.add(cmpData);

			SolidLine solidLine = new SolidLine(1f);
			solidLine.setColor(linecolor);
			LineSeparator line = new LineSeparator(solidLine);
			line.setWidth(UnitValue.createPercentValue(100));
			line.setMarginTop(5f);
			line.setMarginBottom(10f);
			document.add(line);

		} else {

			Paragraph data = new Paragraph()
					.add(new Text(fallbackTitle).setFont(boldFont).setFontSize(18).setFontColor(ColorConstants.WHITE));
			data.setTextAlignment(TextAlignment.CENTER);
			data.setBackgroundColor(dataHeading);
			data.setPadding(8);
			document.add(data);
		}
	}

	private void renderSection(Document document, QuotationResponseDto header, int isInvoice, boolean isDecor,
			String title, PdfFont normalFont, PdfFont boldFont, boolean printTitle) {

		List<QuotationResponseDto> items;
		if (isInvoice == 1) {
			items = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(header.getQuotationId());
		} else {
			items = eventFunctionQuotationServiceImpl.getFunctionData(header.getQuotationId());
		}

		addEventHeader(document, header, title, normalFont, boldFont, printTitle);

		addEstimateTable(document, items, header, isDecor, normalFont, boldFont);
	}

	private void addEventHeader(Document document, QuotationResponseDto dto, String title, PdfFont normal, PdfFont bold,
			boolean printTitle) {

		if (printTitle) {
			document.add(new Paragraph(title).setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER)
					.setMarginBottom(8));
		}

		Table table = new Table(UnitValue.createPercentArray(new float[] { 15, 35, 15, 35 })).useAllAvailableWidth();

		table.setFontSize(9);

		addHeaderInfoCell(table, "TYPE OF EVENT", bold);
		addValueCell(table, dto.getEventName(), normal);

		addHeaderInfoCell(table, "DATE", bold);
		addValueCell(table, dto.getEventDate(), normal);

		table.addCell(new Cell().add(new Paragraph("VENUE").setFont(bold).setFontSize(9)).setPadding(3)
				.setBorder(new SolidBorder(1)));

		table.addCell(new Cell(1, 3).add(
				new Paragraph(dto.getVenueName() == null ? "-" : dto.getVenueName()).setFont(normal).setFontSize(9))
				.setPadding(3).setBorder(new SolidBorder(1)));

		addHeaderInfoCell(table, "NAME", bold);
		addValueCell(table, dto.getPartyName(), normal);

		addHeaderInfoCell(table, "CONTACT NO", bold);
		addValueCell(table, dto.getPartyMobile(), normal);

		addHeaderInfoCell(table, "EMAIL ID", bold);
		addValueCell(table, dto.getPartyEmail(), normal);

		addHeaderInfoCell(table, "GST NO", bold);
		addValueCell(table, dto.getPartyGst(), normal);

		document.add(table);

		document.add(new Paragraph("\n"));
	}

	private void addHeaderInfoCell(Table table, String value, PdfFont font) {
		table.addCell(new Cell().add(new Paragraph(value == null ? "" : value).setFont(font).setFontSize(9))
				.setPadding(3).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.LEFT));
	}

	private void addValueCell(Table table, String value, PdfFont font) {
		table.addCell(new Cell().add(new Paragraph(value == null ? "-" : value).setFont(font).setFontSize(9))
				.setPadding(3).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.LEFT));
	}

	private void addEstimateTable(Document document, List<QuotationResponseDto> items, QuotationResponseDto dto,
			boolean isDecor, PdfFont normal, PdfFont bold) {

		Table table = new Table(UnitValue.createPercentArray(TABLE_COLUMNS)).useAllAvailableWidth();

		table.setFontSize(9);

		addHeaderCell(table, "PARTICULARS", bold);
		addHeaderCell(table, "QUANTITY", bold);
		addHeaderCell(table, "RATE", bold);
		addHeaderCell(table, "AMOUNT", bold);

		double total = 0D;

		for (QuotationResponseDto item : items) {
			String addOnLabel = "";
			if (item.getIsAddons() != null && item.getIsAddons()) {
				addOnLabel = " (Add On)";
			}
			table.addCell(createCell(item.getFunctionName().toUpperCase() + addOnLabel, normal));
			table.addCell(createRightCell(formatQty(item.getFunctionPax()), normal));
			table.addCell(createRightCell(formatAmount(item.getRate()), normal));
			table.addCell(createRightCell(formatAmount(item.getAmount()), normal));

			total += safe(item.getAmount());
		}

		addSummaryRows(table, dto, total, isDecor, normal, bold);

		document.add(table);

		Table remark = new Table(UnitValue.createPercentArray(new float[] { 15, 5, 80 })).useAllAvailableWidth();

		remark.addCell(new Cell().add(new Paragraph("REMARK")).setBorder(Border.NO_BORDER).setFontSize(9));
		remark.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER).setFontSize(9));
		remark.addCell(new Cell().add(new Paragraph(dto.getNotes())).setBorder(Border.NO_BORDER).setFontSize(9));
		document.add(remark);

		document.add(new Paragraph("\n"));
	}

	private void addSummaryRows(Table table, QuotationResponseDto dto, double total, boolean isDecor, PdfFont normal,
			PdfFont bold) {

		addSummaryRow(table, "TOTAL", formatAmount(total), normal);
		addSummaryRow(table, "DISCOUNT", formatAmount(dto.getDiscount()), normal);
		addSummaryRow(table, "FINAL TOTAL", formatAmount(dto.getSubTotle() - dto.getDiscount()), normal);

		addSummaryRow(table, isDecor ? "DECOR TAX" : "FOOD TAX",
				formatAmount(safe(dto.getCgstAmnt()) + safe(dto.getSgstAmnt()) + safe(dto.getIgstAmnt())), normal);

		addSummaryRow(table, "TOTAL AMOUNT", formatAmount(dto.getGrandTotal()), bold);

		double advance = safe(dto.getAdvancePayment());
		double balance = safe(dto.getGrandTotal()) - advance;

		if (advance > 0) {
			addSummaryRow(table, "ADVANCE PAYMENT", formatAmount(advance), normal);
			addSummaryRow(table, "BALANCE TOTAL", formatAmount(balance), normal);
		}
	}

	private void addSummaryRow(Table table, String label, String value, PdfFont font) {
		table.addCell(new Cell(1, 3).add(new Paragraph(label).setFont(font).setFontSize(9))
				.setTextAlignment(TextAlignment.RIGHT).setPadding(2));

		table.addCell(new Cell().add(new Paragraph(value).setFont(font).setFontSize(9))
				.setTextAlignment(TextAlignment.RIGHT).setPadding(2));
	}

	private void addCombinedGrandTotal(Document document, QuotationResponseDto buffet, QuotationResponseDto decor,
			PdfFont normalFont, PdfFont boldFont) {

		double total = safe(buffet.getGrandTotal()) + safe(decor.getGrandTotal());
		double advance = safe(buffet.getAdvancePayment()) + safe(decor.getAdvancePayment());
		double balance = total - advance;

		document.add(new Paragraph("\n"));

		Table table = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }))
				.setWidth(UnitValue.createPercentValue(40)).setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setKeepTogether(true);

		addCombinedRow(table, "TOTAL AMOUNT (A + B)", formatAmount(total), boldFont);
		addCombinedRow(table, "ADVANCE PAYMENT", formatAmount(advance), normalFont);
		addCombinedRow(table, "BALANCE TOTAL", formatAmount(balance), normalFont);

		document.add(table);
	}

	private void addCombinedRow(Table table, String label, String value, PdfFont font) {
		table.addCell(new Cell().add(new Paragraph(label).setFont(font).setFontSize(9)).setBorder(new SolidBorder(1))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2));

		table.addCell(new Cell().add(new Paragraph(value).setFont(font).setFontSize(9)).setBorder(new SolidBorder(1))
				.setTextAlignment(TextAlignment.RIGHT).setPadding(2));
	}

	// =========================================================
	// QR CODE SECTION (mirrors getQuotationReport1's bank/QR block)
	// =========================================================
	private void addQrCodeSection(Document document, QuotationResponseDto dto, PdfFont normalFont, PdfFont boldFont) {

		if (dto == null || dto.getQrCodePath() == null || dto.getQrCodePath().isEmpty()) {
			return;
		}

		Color linecolor = new DeviceRgb(210, 210, 210);
		Color boxBack = new DeviceRgb(250, 250, 250);
		Color fontColor1 = ColorConstants.GRAY;

		Div divData2 = new Div();
		divData2.setWidth(UnitValue.createPercentValue(50));
		divData2.setBorder(new SolidBorder(linecolor, 1f));
		divData2.setPadding(5f);
		divData2.setBackgroundColor(boxBack);
		divData2.setBorderRadius(new BorderRadius(5f));
		divData2.setHorizontalAlignment(HorizontalAlignment.LEFT);

		String bankName = dto.getBankName() == null ? "" : dto.getBankName();
		String accountHolderName = dto.getAccountHolderName() == null ? "" : dto.getAccountHolderName();
		String accountNo = dto.getAccountNo() == null ? "" : dto.getAccountNo();
		String ifscCode = dto.getIfscCode() == null ? "" : dto.getIfscCode();
		String upiId = dto.getUpiId() == null ? "" : dto.getUpiId();

		Paragraph data = new Paragraph().add(new Text("Bank Name: ").setFont(boldFont).setFontSize(10))
				.add(new Text(bankName).setFont(normalFont).setFontSize(10).setFontColor(fontColor1))
				.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
		divData2.add(data);

		data = new Paragraph().add(new Text("Acc Holder: ").setFont(boldFont).setFontSize(10))
				.add(new Text(accountHolderName).setFont(normalFont).setFontSize(10).setFontColor(fontColor1))
				.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
		divData2.add(data);

		data = new Paragraph().add(new Text("Account No: ").setFont(boldFont).setFontSize(10))
				.add(new Text(accountNo).setFont(normalFont).setFontSize(10).setFontColor(fontColor1))
				.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
		divData2.add(data);

		data = new Paragraph().add(new Text("IFSC Code: ").setFont(boldFont).setFontSize(10))
				.add(new Text(ifscCode).setFont(normalFont).setFontSize(10).setFontColor(fontColor1))
				.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
		divData2.add(data);

		data = new Paragraph().add(new Text("UPI ID: ").setFont(boldFont).setFontSize(10))
				.add(new Text(upiId).setFont(normalFont).setFontSize(10).setFontColor(fontColor1))
				.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
		divData2.add(data);

		try {
			ImageData qrCodeData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + dto.getQrCodePath());

			Image qrCode = new Image(qrCodeData);
			qrCode.setWidth(80);
			qrCode.setHeight(80);
			qrCode.setAutoScale(false);
			qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);

			divData2.add(qrCode);
		} catch (Exception e) {
			// QR image missing/unavailable — skip silently, rest of block still renders
		}

		document.add(new Paragraph("\n"));
		document.add(divData2);
	}

	// =========================================================
	// TERMS & CONDITIONS (English only)
	// =========================================================
	private void addTermsAndConditions(Document document, Long userId, int isInvoice, PdfFont normalFont,
			PdfFont boldFont) {

		String reportType = isInvoice == 1 ? "Invoice" : "Quotation";

		UserTermsAndConditionEntity terms = userTermsRepository
				.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(userId, reportType);

		document.add(new AreaBreak());

		Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(boldFont).setFontSize(16)
				.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f);
		document.add(termsTitle);

		if (terms == null) {
			document.add(new Paragraph("No Terms & Conditions available.").setFont(normalFont).setFontSize(12)
					.setFontColor(ColorConstants.GRAY));
			return;
		}

		List<TermsAndConditionFeaturesEntity> features = termsFeaturesRepository
				.findByUserTermsConditionIdAndIsDeleteFalse(terms.getId());

		if (features == null || features.isEmpty()) {
			document.add(new Paragraph("No Terms & Conditions available.").setFont(normalFont).setFontSize(12)
					.setFontColor(ColorConstants.GRAY));
			return;
		}

		ISplitCharacters breakAll = (text, glyphPos) -> true;

		int index = 1;
		for (TermsAndConditionFeaturesEntity feature : features) {
			Paragraph term = new Paragraph(index + ". " + feature.getDescription()).setFont(normalFont).setFontSize(12)
					.setMarginBottom(5f);
			term.setSplitCharacters(breakAll);
			document.add(term);
			index++;
		}
	}

	private void addSignatureSection(Document document, PdfFont normalFont) {

		document.add(new Paragraph("\n\n\n"));

		Table table = new Table(UnitValue.createPercentArray(SIGN_COLUMNS)).useAllAvailableWidth();

		table.addCell(new Cell()
				.add(new Paragraph("________________________\nCustomer's Signature").setFont(normalFont).setFontSize(9))
				.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

		table.addCell(new Cell()
				.add(new Paragraph("________________________\nAuthorised Signatory").setFont(normalFont).setFontSize(9))
				.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

		document.add(table);
	}

	private void addHeaderCell(Table table, String text, PdfFont font) {
		table.addHeaderCell(
				new Cell().add(new Paragraph(text).setFont(font).setFontSize(10)).setTextAlignment(TextAlignment.CENTER)
						.setBackgroundColor(new DeviceRgb(240, 236, 226)).setPadding(3).setBorder(new SolidBorder(1)));
	}

	private Cell createCell(String value, PdfFont font) {
		Paragraph paragraph = new Paragraph(value == null ? "" : value).setFont(font).setFontSize(9);
		return new Cell().add(paragraph).setPadding(2).setBorder(new SolidBorder(1));
	}

	private String formatQty(Number value) {
		if (value == null) {
			return "0";
		}
		return String.format("%.0f", value.doubleValue());
	}

	private Cell createRightCell(String value, PdfFont font) {
		return createCell(value, font).setTextAlignment(TextAlignment.RIGHT);
	}

	private double safe(Double value) {
		return value == null ? 0D : value;
	}

	private String formatAmount(Number value) {
		if (value == null) {
			return "0.00";
		}
		return String.format("%,.2f", value.doubleValue());
	}

	private File createPdfFile(HttpServletRequest request, String eventNo, String partyName, LocalDateTime eventDate,
			int isInvoice) {

		String rootPath = request.getSession().getServletContext().getRealPath("/");
		File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

		if (!outputPath.exists()) {
			if (outputPath.mkdirs()) {
				System.out.println("Directory Created!!!");
			} else {
				System.out.println("Directory Creation Failed!!!");
			}
		}

		String reportType = isInvoice == 1 ? "Invoice" : "Quotation";
		String fileName = getReportName(partyName, formatDate(eventDate), reportType) + ".pdf";

		return new File(outputPath, fileName);
	}

	private String buildDownloadUrl(String eventNo, String partyName, LocalDateTime eventDate, int isInvoice) {
		String reportType = isInvoice == 1 ? "Invoice" : "Quotation";
		return environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
				+ getReportName(partyName, formatDate(eventDate), reportType) + ".pdf";
	}

	@Override
	public String generateInvoiceType3(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");

			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			String fssaiNumber = eventData.getFssaiNumber() == null || eventData.getFssaiNumber().trim().isEmpty() ? ""
					: eventData.getFssaiNumber();
			String hsnNumber = eventData.getHsnNumber() == null || eventData.getHsnNumber().trim().isEmpty() ? ""
					: eventData.getHsnNumber();

			String cinNumber = eventData.getCinNumber() == null || eventData.getCinNumber().trim().isEmpty() ? ""
					: eventData.getCinNumber();
			String fdaLincense = eventData.getFdaLincense() == null || eventData.getFdaLincense().trim().isEmpty() ? ""
					: eventData.getFdaLincense();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();

			String shipAddress = eventData.getShipAddress() != null ? eventData.getShipAddress() : "";

			Color blackColor = new DeviceRgb(0, 0, 0);

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			String header = "ESTIMATE PROPOSAL";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "INVOICE";
				header = "TAX INVOICE";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 100, 20);

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new PageBorderEventHandler(1f, blackColor, 20f));

			float[] columnWidth1 = { 30f, 70f };
			Table cmpData = new Table(UnitValue.createPercentArray(columnWidth1));
			cmpData.setWidth(UnitValue.createPercentValue(100));
			cmpData.setMargin(10f);

			Paragraph data = new Paragraph().add(new Text(companyName).setFontSize(22).setFont(boldFont)).setPadding(0f)
					.setMultipliedLeading(0.8f);

			Cell cell = new Cell(3, 1).add(logo).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			data = new Paragraph()
					.add(new Text(companyAddress).setFontSize(12).setFont(basicFont).setFontColor(blackColor))
					.setPadding(0).setMultipliedLeading(1);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			data = new Paragraph().add(new Text("Contact Us: " + countryCode + " " + officeNo + "\n" + companyEmail)
					.setFontSize(12).setFont(boldFont).setFontColor(blackColor)).setMultipliedLeading(1f);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			Table headerData = new Table(UnitValue.createPercentArray(new float[] { 35f, 31.66f, 33.34f }));
			headerData.setWidth(UnitValue.createPercentValue(100f));
			headerData.setFixedLayout();

			cell = new Cell(1, 3).add(new Paragraph(header).setFontSize(16f).simulateBold())
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			headerData.addCell(cell);

			cell = new Cell().add(new Paragraph("Hotel GSTN #     : " + cmpGstNumber).setFontSize(10f))
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			headerData.addCell(cell);

			cell = new Cell().add(new Paragraph("SYMPOSIUM").simulateBold().setFontSize(12f))
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			headerData.addCell(cell);

			cell = new Cell().add(new Paragraph("Re-Printed").setFontSize(10f)).setTextAlignment(TextAlignment.CENTER)
					.setBorder(Border.NO_BORDER);
			headerData.addCell(cell);

			if (isCompanyDetails == 1) {
				document.add(cmpData);
			}

			document.add(headerData);

			Table detailsTable = new Table(
					UnitValue.createPercentArray(new float[] { 25f, 1.5f, 45f, 25f, 1.5f, 33f }));
			detailsTable.setWidth(UnitValue.createPercentValue(100f));
//			detailsTable.setFixedLayout();

			cell = new Cell()
					.add(new Paragraph("Name").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(partyName).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("FSSAI No").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f)).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(fssaiNumber).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Company Name").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setPadding(0);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(partyName).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("HSN No").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(hsnNumber).setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell(3, 1)
					.add(new Paragraph("Address").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell(3, 1).add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell(3, 1)
					.add(new Paragraph(isInvoice == 1 ? shipAddress : partyAddress).setFont(basicFont).setPadding(0)
							.setFontSize(10).setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Bill No").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(quotationCode).setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Bill Date & Time").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(generatedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
					.setFont(basicFont).setPadding(0).setFontSize(10).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("GSTN Bill #").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(cmpGstNumber).setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Guest GSTN #").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(partyGst).setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			document.add(detailsTable);

			float[] columnWidth3 = { 5f, 30f, 10f, 10f, 10f, 10f, 10f };
			float[] columnWidth6 = { 5f, 30f, 10f, 10f, 10f, 20f };

			Div divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));
			divData1.setBorderTop(new SolidBorder(blackColor, 1f));
			divData1.setBorderBottom(new SolidBorder(blackColor, 1f));

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));

			data = new Paragraph().add(new Text("No.").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Function").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Person").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice == 1) {
				data = new Paragraph().add(new Text("Extra").setFontSize(10).setFont(boldFont));
				cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
				funTbl.addCell(cell);
			}

			data = new Paragraph().add(new Text("Rate").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasExtraTax) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice != 1) {
				if (hasExtraTax) {
					data = new Paragraph().add(new Text("Extra Tax").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}

				if (hasTaxRate) {
					data = new Paragraph().add(new Text("Tax Rate").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}
			}

			data = new Paragraph().add(new Text("Total Price").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";
				System.out.println("fun name : " + fun.getFunctionName());
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();

				if (Boolean.FALSE.equals(fun.getIs_event_function())) {

					// Sr No
					data = new Paragraph()
							.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					cell = new Cell(1, 5);
					data = new Paragraph()
							.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					// Amount
					data = new Paragraph()
							.add(new Text("Rs. " + amount).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					if (isInvoice != 1 && !hasExtraTax) {
						cell = new Cell(1, 2);
					} else {
						cell = new Cell();
					}
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					i++;
					continue;
				}

				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(functionPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice == 1) {
					data = new Paragraph().add(
							new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);
				}

				data = new Paragraph().add(
						new Text("Rs. " + rate.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice != 1) {
					if (hasExtraTax) {
						data = new Paragraph()
								.add(new Text(fun.getExtraTax() != null ? fun.getExtraTax().toString() : "")
										.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(Border.NO_BORDER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}

					if (hasTaxRate) {
						data = new Paragraph().add(new Text(fun.getTaxRate() != null ? fun.getTaxRate().toString() : "")
								.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(Border.NO_BORDER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}
				}

				data = new Paragraph().add(new Text("Rs. " + amount.toString()).setFontSize(10).setFont(basicFont)
						.setFontColor(blackColor));

				if (isInvoice != 1 && !hasTaxRate) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				i++;
			}

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			data = new Paragraph()
					.add(new Text("Subtotal   :").setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPadding(0).setTextAlignment(TextAlignment.RIGHT);
			cell.setBorder(Border.NO_BORDER);
			funTbl2.addCell(cell);

			cell = new Cell();
			data = new Paragraph()
					.add(new Text(subTotle.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
			cell.setBorder(Border.NO_BORDER);
			funTbl2.addCell(cell);

			// Discount
			data = new Paragraph()
					.add(new Text("Discount   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(discount.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Amount After Discount   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}
			double amountAfterDiscount = subTotle - discount;
			data = new Paragraph()
					.add(new Text(String.valueOf((int) Math.round(amountAfterDiscount))).setFont(basicFont)
							.setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph()
					.add(new Text("Cash Amt   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cashPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cheqPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell(1, 5).setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			// SGST
			data = new Paragraph().add(
					new Text("SGST (" + sgst + ")   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(sgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// CGST
			data = new Paragraph().add(
					new Text("CGST (" + cgst + ")   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// IGST
			data = new Paragraph().add(
					new Text("IGST (" + igst + ")   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(igstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(new Text("Cheque Amt(incl. GST)   :").setFont(basicFont).setFontSize(10)
						.setFontColor(blackColor)).setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.setBorder(Border.NO_BORDER).add(data);
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + chequeAmtInclGst).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				funTbl2.addCell(cell);

			}

			if (isAdvance == 1) {
				data = new Paragraph().add(
						new Text("Advance Payment   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}

				data = new Paragraph().add(
						new Text("Rs. " + advancePayment).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}
			}

			// ==========================================
			// Grand Total
			// ==========================================

			// grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total   :").setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			funTbl2.addCell(cell);

			data = new Paragraph().add(new Text(grandTotal.toString()).setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setBorderTop(new SolidBorder(blackColor, 3f))
					.setBorderBottom(new SolidBorder(blackColor, 3f)).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			funTbl2.addCell(cell);

			// In Words
			data = new Paragraph().add(new Text("In Words : ").setFont(boldFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert(grandTotal) + " Only.").setFont(basicFont)
							.setFontSize(10))
					.setPaddingLeft(10f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);

			if (isInvoice == 1) {
				cell = new Cell(1, 6);
			} else {
				cell = new Cell(1, 7);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			funTbl2.addCell(cell);

			divData1.add(funTbl2);
			document.add(divData1);

			if (cgstAmnt != 0 || igstAmnt != 0 || sgstAmnt != 0) {
				Table taxSummaryTable = new Table(UnitValue.createPercentArray(new float[] { 40f, 30f, 30f }));
				taxSummaryTable.setWidth(UnitValue.createPercentValue(60f));
				taxSummaryTable.setMarginTop(10f);
				taxSummaryTable.setFixedLayout();

				cell = new Cell(1, 3)
						.add(new Paragraph("Tax Summary").setFont(basicFont).setFontSize(12).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				taxSummaryTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph("Tax Details").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(2).setPaddingTop(2).setBorder(Border.NO_BORDER)
						.setBorderTop(new SolidBorder(blackColor, 1f)).setBorderBottom(new SolidBorder(blackColor, 1f))
						.simulateBold();
				taxSummaryTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph("Taxable Amount").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(2).setPaddingTop(2).setBorder(Border.NO_BORDER)
						.setBorderTop(new SolidBorder(blackColor, 1f)).setBorderBottom(new SolidBorder(blackColor, 1f))
						.simulateBold();
				taxSummaryTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph("Tax Amount").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(2).setPaddingTop(2).setBorder(Border.NO_BORDER)
						.setBorderTop(new SolidBorder(blackColor, 1f)).setBorderBottom(new SolidBorder(blackColor, 1f))
						.simulateBold();
				taxSummaryTable.addCell(cell);

				if (cgstAmnt != 0) {
					cell = new Cell()
							.add(new Paragraph("Central GST @ " + cgst).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph("").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(cgstAmnt.toString()).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);
				}

				if (sgstAmnt != 0) {
					cell = new Cell()
							.add(new Paragraph("State GST @ " + cgst).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph("").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(sgstAmnt.toString()).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);
				}

				if (igstAmnt != 0) {
					cell = new Cell()
							.add(new Paragraph("Integrated GST @ " + igst).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph("").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(igstAmnt.toString()).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);
				}

				document.add(taxSummaryTable);
			}

			Table signTable = new Table(UnitValue.createPercentArray(new float[] { 33.33f, 33.33f, 33.34f }));
			signTable.setWidth(UnitValue.createPercentValue(100f));
			signTable.setMarginTop(50f);
			signTable.setFixedLayout();

			data = new Paragraph().add(new Text("Cashier Signature").setFont(basicFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER);
			signTable.addCell(new Cell().add(data).setBorder(Border.NO_BORDER));

			signTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			data = new Paragraph().add(new Text("Guest Signature").setFont(basicFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER);
			signTable.addCell(new Cell().add(data).setBorder(Border.NO_BORDER));

			document.add(signTable);

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int j = 1; j <= totalPages; j++) {

				PdfPage page = pdfDocument.getPage(j);
				Rectangle pageSize = page.getPageSize();

				PdfCanvas pdfCanvas = new PdfCanvas(page);

				// Draw top border line above footer
				pdfCanvas.saveState();
				pdfCanvas.setStrokeColor(blackColor);
				pdfCanvas.setLineWidth(1f);

				float leftMargin = 20f;
				float rightMargin = 20f;
				float lineY = 45f;

				pdfCanvas.moveTo(leftMargin, lineY);
				pdfCanvas.lineTo(pageSize.getWidth() - rightMargin, lineY);
				pdfCanvas.stroke();
				pdfCanvas.restoreState();

				// Add footer text
				Canvas canvas = new Canvas(pdfCanvas, pageSize);

				canvas.showTextAligned("** Thank You. Visit Again. **", pageSize.getWidth() / 2, 25,
						TextAlignment.CENTER);

				canvas.close();
			}

			if (isTermsCond == 1) {
				UserTermsAndConditionEntity terms = userTermsRepository
						.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(userId, reportType);

				if (terms == null) {
					// handle gracefully
					Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
							.setFontSize(12).setFontColor(ColorConstants.GRAY);

					document.add(noData);
				} else {

					List<TermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = termsFeaturesRepository.findByUserTermsConditionIdAndIsDeleteFalse(terms.getId());

					document.add(new AreaBreak());

					Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(boldFont).setFontSize(16)
							.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f);

					document.add(termsTitle);

					ISplitCharacters breakAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true; // break anywhere
						}
					};

					if (features == null || features.isEmpty()) {

						Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
								.setFontSize(12).setFontColor(ColorConstants.GRAY);

						document.add(noData);

					} else {

						int index = 1;

						for (TermsAndConditionFeaturesEntity feature : features) {

							String desc = feature.getDescription();

							Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
									.setMarginBottom(5f).setSplitCharacters((text, glyphPos) -> true);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}

	}

	@Override
	public String generateInvoiceType4(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");

			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();

			String shipAddress = eventData.getShipAddress() != null ? eventData.getShipAddress() : "";

			String cmpPanNumber = eventData.getCmpPanNumber() != null ? eventData.getCmpPanNumber() : "";

			Color blackColor = new DeviceRgb(0, 0, 0);

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			String header = "ESTIMATE PROPOSAL";
			String code = "Quotation code";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "INVOICE";
				header = "TAX INVOICE";
				code = "Invoice code";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 30, 20);

			Paragraph headerPara = new Paragraph().add(header).setFontSize(16f).simulateBold()
					.setTextAlignment(TextAlignment.CENTER);

			document.add(headerPara);

			Border border = new SolidBorder(ColorConstants.BLACK, 1);

			// Main Table (Left 53%, Right 47%)
			Table mainTable = new Table(UnitValue.createPercentArray(new float[] { 60, 40 }));
			mainTable.setWidth(UnitValue.createPercentValue(100));
			mainTable.setFixedLayout();

			Cell companyCell = new Cell(1, 2).setBorder(border).setPadding(5);

			companyCell.add(new Paragraph().add(new Text(companyName).simulateBold().setFontSize(14)).add(
					new Text("\n" + companyAddress + "\nGSTIN/UIN : " + cmpGstNumber + "\nE-Mail : " + companyEmail)
							.setFontSize(12)));

			mainTable.addCell(companyCell);

			Cell buyerCell = new Cell(2, 1).setBorder(border).setPadding(5);

			buyerCell.add(new Paragraph().add(new Text("Buyer (Bill To)\n").setFontSize(12))
					.add(new Text(partyName).simulateBold().setFontSize(14))
					.add(new Text("\n" + (isInvoice == 1 ? shipAddress : partyAddress) + "\nGSTIN/UIN : " + partyGst)
							.setFontSize(12)));

			mainTable.addCell(buyerCell);

			Cell invoiceNo = new Cell().setBorder(border).setPadding(5);
			invoiceNo.add(new Paragraph(code).setFontSize(12));
			invoiceNo.add(new Paragraph(quotationCode).simulateBold().setFontSize(12));
			mainTable.addCell(invoiceNo);

			Cell date = new Cell().setBorder(border).setPadding(5);
			date.add(new Paragraph("Dated").setFontSize(12));
			date.add(new Paragraph(generatedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).simulateBold()
					.setFontSize(12));
			mainTable.addCell(date);

			document.add(mainTable);

			float[] columnWidth3 = { 5f, 30f, 10f, 10f, 10f, 10f, 10f };
			float[] columnWidth6 = { 5f, 30f, 10f, 10f, 10f, 20f };

			Div divData1 = new Div();

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));
			funTbl.setMarginTop(0f);

			Paragraph data = new Paragraph().add(new Text("No.").setFontSize(10).setFont(boldFont));
			Cell cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Function").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Person").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice == 1) {
				data = new Paragraph().add(new Text("Extra").setFontSize(10).setFont(boldFont));
				cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setTextAlignment(TextAlignment.CENTER);
				funTbl.addCell(cell);
			}

			data = new Paragraph().add(new Text("Rate").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasExtraTax) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice != 1) {
				if (hasExtraTax) {
					data = new Paragraph().add(new Text("Extra Tax").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}

				if (hasTaxRate) {
					data = new Paragraph().add(new Text("Tax Rate").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}
			}

			data = new Paragraph().add(new Text("Total Price").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";
				System.out.println("fun name : " + fun.getFunctionName());
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();

				if (Boolean.FALSE.equals(fun.getIs_event_function())) {

					// Sr No
					data = new Paragraph()
							.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					cell = new Cell(1, 4);
					data = new Paragraph()
							.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					// Amount
					data = new Paragraph().add(
							new Text(amount.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					if (isInvoice != 1 && !hasExtraTax) {
						cell = new Cell(1, 2);
					} else {
						cell = new Cell();
					}
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					i++;
					continue;
				}

				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(functionPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice == 1) {
					data = new Paragraph().add(
							new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);
				}

				data = new Paragraph()
						.add(new Text(rate.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice != 1) {
					if (hasExtraTax) {
						data = new Paragraph()
								.add(new Text(fun.getExtraTax() != null ? fun.getExtraTax().toString() : "")
										.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}

					if (hasTaxRate) {
						data = new Paragraph().add(new Text(fun.getTaxRate() != null ? fun.getTaxRate().toString() : "")
								.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}
				}

				data = new Paragraph()
						.add(new Text(amount.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasTaxRate) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				i++;
			}

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			data = new Paragraph()
					.add(new Text("Subtotal").setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setTextAlignment(TextAlignment.RIGHT);
			funTbl2.addCell(cell);

			cell = new Cell();
			data = new Paragraph()
					.add(new Text(subTotle.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
			funTbl2.addCell(cell);

			// Discount
			data = new Paragraph().add(new Text("Discount").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(discount.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Amount After Discount").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}
			double amountAfterDiscount = subTotle - discount;
			data = new Paragraph()
					.add(new Text(String.valueOf((int) Math.round(amountAfterDiscount))).setFont(basicFont)
							.setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);

			cell = new Cell().add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph().add(new Text("Cash Amt").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cashPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cheqPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell(1, 5).add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			// SGST
			data = new Paragraph()
					.add(new Text("SGST (" + sgst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(sgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// CGST
			data = new Paragraph()
					.add(new Text("CGST (" + cgst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// IGST
			data = new Paragraph()
					.add(new Text("IGST (" + igst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(igstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(
						new Text("Cheque Amt(incl. GST)").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.add(data);
				funTbl2.addCell(cell);

				data = new Paragraph().add(new Text(String.valueOf((int) chequeAmtInclGst)).setFont(basicFont)
						.setFontSize(10).setFontColor(blackColor)).setTextAlignment(TextAlignment.CENTER)
						.setPaddingLeft(0f);

				cell = new Cell().add(data);
				funTbl2.addCell(cell);

			}

			if (isAdvance == 1) {
				data = new Paragraph()
						.add(new Text("Advance Payment").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}

				data = new Paragraph().add(
						new Text(advancePayment.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
				cell = new Cell().add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}
			}

			// ==========================================
			// Grand Total
			// ==========================================

			// grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total").setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			funTbl2.addCell(cell);

			data = new Paragraph().add(new Text(grandTotal.toString()).setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);

			cell = new Cell().add(data);
			funTbl2.addCell(cell);

			divData1.add(funTbl2);
			document.add(divData1);

			// ================== FOOTER TABLE ==================
			Table footerTable = new Table(UnitValue.createPercentArray(new float[] { 53, 47 }));
			footerTable.setWidth(UnitValue.createPercentValue(100));
			footerTable.setFixedLayout();
			footerTable.setBorder(new SolidBorder(ColorConstants.BLACK, 1));

			// ==================================================
			// Amount Chargeable (in words)
			// ==================================================
			Paragraph amountWord = new Paragraph()
					.add(new Text("Amount Chargeable (in words)\n").setFont(basicFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert(grandTotal) + " Only").setFont(boldFont)
							.setFontSize(10))
					.setMultipliedLeading(1f).setPaddingLeft(2);

			cell = new Cell(1, 2).add(amountWord).setPadding(4)
					.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1));

			footerTable.addCell(cell);

			double totalTaxAmount = cgstAmnt + sgstAmnt + igstAmnt;

			amountWord = new Paragraph().add(new Text("Tax Amount (in words)  :").setFont(basicFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert((long) totalTaxAmount) + " Only").setFont(boldFont)
							.setFontSize(10))
					.setMultipliedLeading(1f).setPaddingLeft(2);

			cell = new Cell(1, 2).add(amountWord).setPadding(4).setBorderBottom(Border.NO_BORDER);

			footerTable.addCell(cell);

			// ==================================================
			// Left Cell (PAN)
			// ==================================================
			Cell leftCell = new Cell();
			leftCell.setBorder(Border.NO_BORDER);
			leftCell.setPadding(5);
			leftCell.setMinHeight(120);

			Paragraph pan = new Paragraph().add(new Text("Company's PAN : ").setFont(basicFont).setFontSize(10))
					.add(new Text(cmpPanNumber).setFont(boldFont).setFontSize(10));

			leftCell.add(new Paragraph("\n\n"));
			leftCell.add(pan);

			footerTable.addCell(leftCell);

			// ==================================================
			// Right Cell (Bank Details + Signature)
			// ==================================================
			Cell rightCell = new Cell();
			rightCell.setBorderLeft(Border.NO_BORDER);
			rightCell.setBorderTop(Border.NO_BORDER);
			rightCell.setBorderRight(Border.NO_BORDER);
			rightCell.setBorderBottom(Border.NO_BORDER);
			rightCell.setPadding(0);

			// --------------- Bank Details ----------------
			Div bankDiv = new Div();
			bankDiv.setPaddings(0, 5, 5, 5);

			bankDiv.add(new Paragraph("Company's Bank Details").setFont(boldFont).setFontSize(11));

			Table bankTable = new Table(UnitValue.createPercentArray(new float[] { 44, 3, 53 }));
			bankTable.setWidth(UnitValue.createPercentValue(100));
			bankTable.setBorder(Border.NO_BORDER);

			bankTable.addCell(createBodyCell("A/c Holder's Name"));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell(companyName).setFont(boldFont));

			bankTable.addCell(createBodyCell("Bank Name"));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell(bankName).setFont(boldFont));

			bankTable.addCell(createBodyCell("Account No."));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell(accountNo).setFont(boldFont));

			bankTable.addCell(createBodyCell("Branch & IFSC Code"));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell(branchName + "&" + ifscCode).setFont(boldFont));

			bankDiv.add(bankTable);

			rightCell.add(bankDiv);

			// --------------- Signature ----------------
			Div signDiv = new Div();
			signDiv.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
			signDiv.setHeight(80);
			signDiv.setPaddings(0, 5, 0, 5);
			signDiv.setKeepTogether(true);

			signDiv.add(new Paragraph("for " + companyName).setFont(boldFont).setFontSize(12));

			signDiv.add(new Paragraph("Authorised Signatory").setFont(basicFont).setFontSize(12)
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(33f));

			rightCell.add(signDiv);

			footerTable.addCell(rightCell);

			// ==================================================
			document.add(footerTable);

			// ================= PAGE NUMBERS =================
			int totalPages = pdfDocument.getNumberOfPages();
			System.out.println("total pages : " + totalPages);
			for (int j = 1; j <= totalPages; j++) {
				PdfPage page = pdfDocument.getPage(j);

				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				Paragraph footer = new Paragraph().add("This is a Computer Generated Invoice").setFontSize(11f);

				canvas.showTextAligned(footer, page.getPageSize().getWidth() / 2, 22, TextAlignment.CENTER);
				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}

	}

	private Cell createBodyCell(String text) {
		return new Cell().add(new Paragraph(text).setFontSize(10).setMargin(0)).setBorder(Border.NO_BORDER)
				.setPadding(0);
	}

	@Override
	public String generateInvoiceType5(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice,
			AdminTemplateModuleResponseDto adminTemplate) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");

			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();

			String shipAddress = eventData.getShipAddress() != null ? eventData.getShipAddress() : "";

			String notes = eventData.getNotes() != null ? eventData.getNotes() : "";

			String cmpPanNumber = eventData.getCmpPanNumber() != null ? eventData.getCmpPanNumber() : "";

			String partyPan = eventData.getPan() != null ? eventData.getPan() : "";

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = (isInvoice == 1) ? "Invoice" : "Quotation";
			String reportLabel = (isInvoice == 1) ? "INVOICE" : "QUOTATION";
			String header = (isInvoice == 1) ? "TAX INVOICE" : "QUOTATION";

//			ImageData tncPage = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData tncPage1 = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishna_detail_4.png");

			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = menuPreparationServiceImpl.loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null
					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
				tncPage1 = menuPreparationServiceImpl.loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 20, 20);

			Color lightGray = ColorConstants.LIGHT_GRAY;
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color dataHeadingColor = new DeviceRgb(242, 235, 223);

			if (isCompanyDetails == 1) {
				Table cmpTable = new Table(UnitValue.createPercentArray(new float[] { 32f, 68f }));
				cmpTable.setWidth(UnitValue.createPercentValue(100));
				cmpTable.setBorder(Border.NO_BORDER);
				cmpTable.setBorderBottom(new SolidBorder(lightGray, 1f));
//				cmpTable.setMarginTop(10f);

				ImageData logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
				Image logo = new Image(logoData);
				logo.setAutoScale(false);
				logo.scaleToFit(100f, 100f);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				cmpTable.addCell(new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setBorder(Border.NO_BORDER).setPadding(3f));

				cmpTable.addCell(new Cell()
						.add(new Paragraph(companyName.toUpperCase()).setFont(basicFont).setFontSize(12)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.RIGHT))
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER));

				cmpTable.addCell(new Cell()
						.add(new Paragraph(companyAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.RIGHT))
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER));

				cmpTable.addCell(new Cell()
						.add(new Paragraph().add(new Text("Mobile No : ")).add(new Text(officeNo)).setFont(basicFont)
								.setFontSize(10).setFontColor(blackColor).setTextAlignment(TextAlignment.RIGHT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f));

				cmpTable.addCell(new Cell()
						.add(new Paragraph().add(new Text("Email : ")).add(new Text(companyEmail)).setFont(basicFont)
								.setFontSize(10).setFontColor(blackColor).setTextAlignment(TextAlignment.RIGHT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f));

				cmpTable.addCell(new Cell().setBorder(Border.NO_BORDER).setHeight(5f));
				cmpTable.addCell(new Cell().setBorder(Border.NO_BORDER).setHeight(5f));

				document.add(cmpTable);
			}

			document.add(new Paragraph(reportLabel).setFontSize(18f).setWidth(UnitValue.createPercentValue(100f))
					.setTextAlignment(TextAlignment.CENTER).setUnderline());

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 30f, 20f, 30f }));
			detailTable.setWidth(UnitValue.createPercentValue(100f));
			detailTable.setBorder(Border.NO_BORDER);
//			detailTable.setMarginTop(5f);

			String invDate = generatedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

			createHeaderCell(detailTable, isInvoice == 1 ? "INVOICE DATE" : "QUOTATION DATE", 11f, blackColor, null, 1,
					1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, invDate.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, isInvoice == 1 ? "INVOICE NO" : "QUOTATION NO", 11f, blackColor, null, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, quotationCode.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "CLIENT NAME", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, partyName.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "VENUE NAME", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, shipAddress.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "CLIENT CONTACT NO", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, partyMobile, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "EVENT DATE", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, eventDate, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "EMAIL", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, partyEmail, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "VENUE DURATION", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, "", 11f, blackColor, null, 1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE,
					basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "CLIENT GST", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, partyGst.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "CLIENT PAN", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, partyPan, 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			createHeaderCell(detailTable, "EVENT TYPE", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, eventMasterEntity.getEventType().getNameEnglish().toUpperCase(), 11f,
					blackColor, null, 1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, false, true,
					blackColor);

			createHeaderCell(detailTable, "HANDLED BY", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable,
					eventMasterEntity.getManager().getFirstName().toUpperCase() + " "
							+ eventMasterEntity.getManager().getLastName().toUpperCase(),
					11f, blackColor, null, 1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, basicFont, false, true,
					blackColor);

			createHeaderCell(detailTable, "ADDRESS", 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);
			createHeaderCell(detailTable, partyAddress.toUpperCase(), 11f, blackColor, null, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, basicFont, false, true, blackColor);

			document.add(detailTable);

			float[] columnWidth3 = { 10f, 70f, 20f };

			Div divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));
//			divData1.setBorder(new SolidBorder(blackColor, 1f));
			divData1.setBackgroundColor(dataHeadingColor);
			divData1.setMarginTop(10f);
//			divData1.setBorderTopLeftRadius(new BorderRadius(10f));
//			divData1.setBorderTopRightRadius(new BorderRadius(10f));

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			funData = funData.stream().filter(item -> Boolean.FALSE.equals(item.getIs_event_function()))
					.collect(Collectors.toList());

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			int summaryColSpan = 2;
			int remainingCol = 1;

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));

			Paragraph data = new Paragraph()
					.add(new Text("NO.").setFontSize(11).setFont(basicFont).setFontColor(blackColor));
			Cell cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("FUNCTION").setFontSize(11).setFont(basicFont).setFontColor(blackColor));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("TOTAL PRICE").setFontSize(11).setFont(basicFont).setFontColor(blackColor));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			System.out.println("funData size : " + funData.size());
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";

				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();

				// Sr No
				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				cell = new Cell();
				data = new Paragraph()
						.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text("Rs. " + amount).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				i++;
			}

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));

			cell = new Cell(1, summaryColSpan);
			data = new Paragraph()
					.add(new Text("Subtotal").setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPaddingRight(10f).setTextAlignment(TextAlignment.RIGHT);
			funTbl2.addCell(cell);

			cell = new Cell(1, remainingCol);
			data = new Paragraph().add(
					new Text("Rs. " + subTotle.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
			funTbl2.addCell(cell);

			// Discount
			data = new Paragraph().add(new Text("Discount").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);

			cell = new Cell(1, summaryColSpan);
			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + discount.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);

			cell = new Cell(1, remainingCol);
			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Amount After Discount").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);

			cell = new Cell(1, summaryColSpan);

			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}
			double amountAfterDiscount = subTotle - discount;
			data = new Paragraph()
					.add(new Text(String.valueOf("Rs. " + (int) Math.round(amountAfterDiscount))).setFont(basicFont)
							.setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);

			cell = new Cell(1, remainingCol).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph().add(new Text("Cash Amt").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);
			cell = new Cell(1, summaryColSpan);
			cell.add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + cashPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);
			cell = new Cell(1, remainingCol).add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);
			cell = new Cell(1, summaryColSpan);
			cell.add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + cheqPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);
			cell = new Cell(1, remainingCol).add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			// SGST
			data = new Paragraph()
					.add(new Text("SGST (" + sgst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);
			cell = new Cell(1, summaryColSpan);
			cell.add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + sgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);
			cell = new Cell(1, remainingCol).add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// CGST
			data = new Paragraph()
					.add(new Text("CGST (" + cgst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);
			cell = new Cell(1, summaryColSpan);
			cell.add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + cgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);
			cell = new Cell(1, remainingCol).add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// IGST
			data = new Paragraph()
					.add(new Text("IGST (" + igst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);
			cell = new Cell(1, summaryColSpan);
			cell.add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + igstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);
			cell = new Cell(1, remainingCol).add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(
						new Text("Cheque Amt(incl. GST)").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);

				cell = new Cell(1, summaryColSpan);
				cell.add(data);
				funTbl2.addCell(cell);

				data = new Paragraph().add(new Text("Rs. " + chequeAmtInclGst + "").setFont(basicFont).setFontSize(10)
						.setFontColor(blackColor)).setTextAlignment(TextAlignment.CENTER).setPadding(0f);

				cell = new Cell(1, remainingCol).add(data);
				funTbl2.addCell(cell);

			}
			if (isAdvance == 1) {
				data = new Paragraph()
						.add(new Text("Advance Payment").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);
				cell = new Cell(1, summaryColSpan);
				cell.add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}

				data = new Paragraph().add(new Text("Rs. " + advancePayment.toString()).setFont(basicFont)
						.setFontSize(10).setFontColor(blackColor)).setTextAlignment(TextAlignment.CENTER)
						.setPadding(0f);
				cell = new Cell(1, remainingCol).add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}
			}

			data = new Paragraph().add(new Text("Remaining Amount").setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);

			cell = new Cell(1, summaryColSpan);
			cell.add(data).setBackgroundColor(dataHeadingColor);
			funTbl2.addCell(cell);

			data = new Paragraph().add(new Text("Rs. " + remainingAmount.toString()).setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);

			cell = new Cell(1, remainingCol).add(data).setBackgroundColor(dataHeadingColor);
			funTbl2.addCell(cell);

			// ==========================================
			// Grand Total
			// ==========================================

			grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total").setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingRight(10f);

			cell = new Cell(1, summaryColSpan);
			cell.add(data).setBackgroundColor(dataHeadingColor);
			funTbl2.addCell(cell);

			Integer grandTotal1 = grandTotal;
			data = new Paragraph().add(new Text("Rs. " + grandTotal1.toString()).setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setPadding(0f);

			cell = new Cell(1, remainingCol).add(data).setBackgroundColor(dataHeadingColor);
			funTbl2.addCell(cell);

			divData1.add(funTbl2);
			document.add(divData1);

			if (notes.trim().length() != 0) {
				document.add(new Paragraph("REMARKS :- ").setFont(basicFont).simulateBold().setFontSize(10f));

				document.add(new Paragraph(notes).setFont(basicFont));
			}

			Table bankTable = new Table(UnitValue.createPercentArray(new float[] { 20, 2, 78 }));
			bankTable.setWidth(UnitValue.createPercentValue(100));
			bankTable.setBorder(Border.NO_BORDER);
			bankTable.setMarginTop(10f);

			bankTable.addCell(createBodyCell("A/c Holder's Name"));
			bankTable.addCell(createBodyCell(":-"));
			bankTable.addCell(createBodyCell(companyName));

			bankTable.addCell(createBodyCell("Bank Name"));
			bankTable.addCell(createBodyCell(":-"));
			bankTable.addCell(createBodyCell(bankName));

			bankTable.addCell(createBodyCell("Account No."));
			bankTable.addCell(createBodyCell(":-"));
			bankTable.addCell(createBodyCell(accountNo));

			bankTable.addCell(createBodyCell("Branch"));
			bankTable.addCell(createBodyCell(":-"));
			bankTable.addCell(createBodyCell(branchName));

			bankTable.addCell(createBodyCell("IFSC Code"));
			bankTable.addCell(createBodyCell(":-"));
			bankTable.addCell(createBodyCell(ifscCode));

			bankTable.addCell(createBodyCell("GST No"));
			bankTable.addCell(createBodyCell(":-"));
			bankTable.addCell(createBodyCell(cmpGstNumber));

			bankTable.addCell(createBodyCell("PAN Card"));
			bankTable.addCell(createBodyCell(":-"));
			bankTable.addCell(createBodyCell(cmpPanNumber));

			document.add(bankTable);

			// ================== FOOTER TABLE ==================
			Table footerTable = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
			footerTable.setWidth(UnitValue.createPercentValue(100));
			footerTable.setFixedLayout();
			footerTable.setMarginTop(60f);

			footerTable.addCell(createBodyCell("Customer's Signature").setTextAlignment(TextAlignment.CENTER));
			footerTable.addCell(createBodyCell("Authorised Signatory").setTextAlignment(TextAlignment.CENTER));

			document.add(footerTable);

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			Paragraph invisibleContent;

			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}
	}

	private void createHeaderCell(Table table, String label, float fontSize, Color fontColor, Color bgColor,
			Integer colSpan, Integer rowSpan, TextAlignment textAlignment, VerticalAlignment verticalAlignment,
			PdfFont font, Boolean isBold, Boolean isBorder, Color borderColor) {
		Cell cell = new Cell(rowSpan, colSpan).add(new Paragraph(label).setFont(font).setFontSize(fontSize)
				.setFontColor(fontColor).setTextAlignment(textAlignment).setVerticalAlignment(verticalAlignment));

		if (isBold) {
			cell.simulateBold();
		}

		if (bgColor != null) {
			cell.setBackgroundColor(bgColor);
		}

		if (isBorder && borderColor != null) {
			cell.setBorder(new SolidBorder(borderColor, 1f));
		} else {
			cell.setBorder(Border.NO_BORDER);
		}

		table.addCell(cell);
	}

	private static final Color SHADE = new DeviceRgb(248, 244, 235);

	@Override
	public String generateEstimateOrInvoiceType7(Long eventId, HttpServletRequest re, int lang, Long userId,
			Integer isTermsCond, Integer isAdvance, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice,
			String reportName) {

		try {
			// ---------- fonts / license ----------
			menuPreparationServiceImpl.loadLicense();
			PdfFont basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");

			// ---------- labels driven purely by isInvoice ----------
			Labels lb = Labels.forType(isInvoice);
			boolean invoice = isInvoice != null && isInvoice == 1;

			// ---------- data ----------
			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, lang,
					isInvoice, false);

			String eventName = nz(eventData.getEventName());
			String venueName = nz(eventData.getVenueName());
			String clientName = nz(eventData.getPartyName());

			String clientEmail = nz(eventData.getPartyEmail());
			String clientGst = nz(eventData.getPartyGst());

			String clientMobile = nz(eventData.getPartyMobile());
			String docCode = nz(eventData.getQuotationCode());
			String eventDate = nz(eventData.getEventDate());
			String invoiceDate = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
					: "";

			// company block (only fetched/rendered if isCompanyDetails == 1)
			String companyName = nz(eventData.getCompanyName());
			String companyAddress = nz(eventData.getCompanyAddress());
			String companyEmail = nz(eventData.getCompanyEmail());
			String cmpGstNumber = nz(eventData.getCmpGstNumber());
			String companyLogo = nz(eventData.getCompanyLogo());

			String remark = nz(eventData.getNotes());

			Integer total = round(eventData.getSubTotle());
			Integer discount = round(eventData.getDiscount());
			String cgst = eventData.getCgst();
			Integer cgstAmnt = round(eventData.getCgstAmnt());
			String sgst = eventData.getSgst();
			Integer sgstAmnt = round(eventData.getSgstAmnt());
			String igst = eventData.getIgst();
			Integer igstAmnt = round(eventData.getIgstAmnt());
			Integer totalAmount = round(eventData.getGrandTotal());
			Integer remainingPayment = round(eventData.getRemainingAmount());
			Integer receivedPayment = totalAmount - remainingPayment;
			Integer advancePayment = round(eventData.getAdvancePayment());
			Integer cashPay = round(eventData.getCashPayment());
			Integer chequePay = round(eventData.getChequePayment());

			String foodTax = eventData.getFoodTax();
			Integer foodTaxAmount = eventData.getFoodTaxAmount();
			Integer foodTaxTotalAmount = eventData.getFoodTaxTotalAmount();

			String serviceTax = eventData.getServiceTax();
			Integer serviceTaxAmount = eventData.getServiceTaxAmount();
			Integer serviceTaxTotalAmount = eventData.getServiceTaxTotalAmount();

			String VatTax = eventData.getVatTax();
			Integer vatTaxAmount = eventData.getVatTaxAmount();
			Integer vatTaxTotalAmount = eventData.getVatTaxTotalAmount();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String cmpPanNumber = eventData.getCmpPanNumber() != null ? eventData.getCmpPanNumber() : "";

			List<QuotationResponseDto> rows = new ArrayList<>();

			if (isInvoice == 1) {
				rows = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(eventData.getQuotationId());
			} else {
				rows = eventFunctionQuotationServiceImpl.getFunctionData(eventData.getQuotationId());
			}

			if (rows == null)
				rows = new ArrayList<>();

			// ---------- output file ----------
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventId);
			if (!outputPath.exists())
				outputPath.mkdirs();

			File pdfFile = new File(outputPath, lb.reportType + "_" + eventId + "_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4, false);
			document.setMargins(20, 20, 30, 20);

			// ---------- company details (conditional) ----------
			if (isCompanyDetails != null && isCompanyDetails == 1) {
				Table companyHeader = new Table(UnitValue.createPercentArray(new float[] { 30, 70 }));
				companyHeader.setWidth(UnitValue.createPercentValue(100));
				companyHeader.setMarginBottom(8);

				Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
				if (!companyLogo.isEmpty()) {
					ImageData img = null;
					if(reportName.equalsIgnoreCase("CCI Quotation Report")) {
						img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/cci_logo.png");
					}else {
						img = menuPreparationServiceImpl
								.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
					}
					Image logo = new Image(img);
					logo.setWidth(UnitValue.createPercentValue(90));
					logo.setHorizontalAlignment(HorizontalAlignment.LEFT);
					logoCell.add(logo);
				}
				companyHeader.addCell(logoCell);

				Paragraph companyInfo = new Paragraph()
						.add(new Text(companyName + "\n").setFont(boldFont).setFontSize(13))
						.add(new Text(companyAddress + "\n").setFont(basicFont).setFontSize(10))
						.add(new Text((cmpGstNumber.isEmpty() ? "" : "GSTIN/UIN : " + cmpGstNumber + "\n"))
								.setFont(basicFont).setFontSize(10))
//						.add(new Text(companyEmail.isEmpty() ? "" : "E-Mail : " + companyEmail).setFont(basicFont)
//								.setFontSize(10))
						.setTextAlignment(TextAlignment.RIGHT);

				Cell infoCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.add(companyInfo);
				companyHeader.addCell(infoCell);

				document.add(companyHeader);
			}

			// ---------- top-right: Event Date / (Invoice Date) / Code ----------
			Paragraph topRight = new Paragraph()
					.add(new Text(lb.eventDateLabel + " " + eventDate).setFont(basicFont).setFontSize(11))
					.setTextAlignment(TextAlignment.RIGHT).setMarginBottom(1);
			document.add(topRight);

			if (invoice) {
				Paragraph invDate = new Paragraph()
						.add(new Text(lb.invoiceDateLabel + " " + invoiceDate).setFont(basicFont).setFontSize(11))
						.setTextAlignment(TextAlignment.RIGHT).setMarginBottom(1);
				document.add(invDate);
			}

			Paragraph codeLine = new Paragraph()
					.add(new Text(lb.codeLabel + " " + docCode).setFont(basicFont).setFontSize(11))
					.setTextAlignment(TextAlignment.RIGHT).setMarginBottom(6);
			document.add(codeLine);

			// ---------- bordered client-info box (single column, 4 rows) ----------
			Table infoTable = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
			infoTable.setWidth(UnitValue.createPercentValue(100));

			addInfoRow(infoTable, lb.billingLabel, clientName, basicFont);
			addInfoRow(infoTable, lb.clientNoLabel, clientMobile, basicFont);
			addInfoRow(infoTable, lb.clientEmailLabel, clientEmail, basicFont);
			addInfoRow(infoTable, lb.clientGstLabel, clientGst, basicFont);
			addInfoRow(infoTable, lb.eventLabel, eventName, basicFont);
			addInfoRow(infoTable, lb.venueLabel, venueName, basicFont);
			document.add(infoTable);

			// ---------- title ----------
			document.add(new Paragraph(lb.header).setFont(boldFont).setFontSize(14)
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(8).setMarginBottom(4));

			// ---------- items + totals table (single table, matches reference grid)
			// ----------
			float[] widths = { 22f, 16f, 14f, 7f, 7f, 10f, 10f, 14f };
			Table mainTable = new Table(UnitValue.createPercentArray(widths));
			mainTable.setWidth(UnitValue.createPercentValue(100));

			addHeaderCell2(mainTable, lb.colParticulars, boldFont);
			addHeaderCell2(mainTable, lb.colVenue, boldFont);
			addHeaderCell2(mainTable, lb.colDate, boldFont);
			addHeaderCell2(mainTable, lb.colPax, boldFont);
			addHeaderCell2(mainTable, lb.extraPaxLabel, boldFont);
			addHeaderCell2(mainTable, lb.colRate, boldFont);
			addHeaderCell2(mainTable, lb.colOfferedRate, boldFont);
			addHeaderCell2(mainTable, lb.colAmount, boldFont);

			for (QuotationResponseDto row : rows) {
				addBodyCell(mainTable, nz(row.getFunctionName()), basicFont);
				addBodyCell(mainTable, nz(row.getVenueName()), basicFont);
				addBodyCell(mainTable, nz(row.getFunctionDate()), basicFont);
				addBodyCell(mainTable, String.valueOf(row.getFunctionPax() == null ? 0 : row.getFunctionPax()),
						basicFont);
				addBodyCell(mainTable,
						String.valueOf(row.getFunctionExtraPax() == null ? 0 : row.getFunctionExtraPax()), basicFont);
				addBodyCell(mainTable, String.valueOf(round(row.getOfferedRate())), basicFont);
				addBodyCell(mainTable, String.valueOf(round(row.getRate())), basicFont);
				addBodyCell(mainTable, String.valueOf(round(row.getAmount())), basicFont);
			}

			// totals - order matches the reference: TOTAL, FINAL TOTAL, TOTAL AMOUNT,
			// RECEIVED PAYMENT, REMAINING PAYMENT, (ADVANCE PAYMENT)
			addTotalRow(mainTable, lb.totalLabel, total, boldFont, false);
			if (discount > 0) {
				Integer amountAfterDiscount = total - discount;
				addTotalRow(mainTable, lb.discountLabel, discount, boldFont, false);
				addTotalRow(mainTable, lb.amountAfterDiscountLabel, amountAfterDiscount, boldFont, false);
			}

			if (cashPay > 0) {
				addTotalRow(mainTable, lb.cashLabel, cashPay, boldFont, false);
			}

			if (chequePay > 0) {
				addTotalRow(mainTable, lb.cheqLabel, chequePay, boldFont, false);
			}

			if (foodTaxTotalAmount > 0) {
				addTotalRow(mainTable, lb.foodTaxLabel + " ( " + foodTax + " ) ", foodTaxTotalAmount, boldFont, false);
			}

			if (serviceTaxTotalAmount > 0) {
				addTotalRow(mainTable, lb.serviceTaxLabel + " ( " + serviceTax + " ) ", serviceTaxTotalAmount, boldFont,
						false);
			}

			if (vatTaxTotalAmount > 0) {
				addTotalRow(mainTable, lb.vatTaxLabel + " ( " + VatTax + " ) ", vatTaxTotalAmount, boldFont, false);
			}

			if (sgstAmnt > 0) {
				addTotalRow(mainTable, lb.sgstLabel + "( " + sgst + " )", sgstAmnt, boldFont, false);
			}

			if (cgstAmnt > 0) {
				addTotalRow(mainTable, lb.cgstLabel + "( " + cgst + " )", cgstAmnt, boldFont, false);
			}

			if (igstAmnt > 0) {
				addTotalRow(mainTable, lb.igstLabel + "( " + igst + " )", igstAmnt, boldFont, false);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			Integer chequeAmtInclGst = chequePay + cgstAmnt + sgstAmnt + igstAmnt;

			if (chequePay > 0) {
				addTotalRow(mainTable, lb.chequeAmtInclGstLabel, chequeAmtInclGst, boldFont, false);
			}

			addTotalRow(mainTable, lb.totalAmountLabel, totalAmount, boldFont, true);
			addTotalRow(mainTable, lb.receivedPaymentLabel, receivedPayment, basicFont, false);
			addTotalRow(mainTable, lb.remainingPaymentLabel, remainingPayment, basicFont, true);

			if (isAdvance != null && isAdvance == 1 && advancePayment > 0) {
				addTotalRow(mainTable, lb.advancePaymentLabel, advancePayment, basicFont, false);
			}

			document.add(mainTable);

			// ---------- remark ----------
			document.add(new Paragraph().add(new Text(lb.wordLabel).setFont(basicFont).setFontSize(10))
					.add(new Text("\t\t" + NumberToWordConverter.convert(totalAmount) + " Only.").setFont(basicFont)
							.setFontSize(10))
					.setMarginTop(3));

			// ---------- remark ----------
			document.add(new Paragraph().add(new Text(lb.remarkLabel).setFont(basicFont).setFontSize(10))
					.add(new Text("\t\t" + remark).setFont(basicFont).setFontSize(10)).setMarginTop(6));

			// ---------- terms & conditions (conditional) ----------
//			if (isTermsCond != null && isTermsCond == 1) {
//				String terms = nz(eventData.getTermsAndConditions());
//				if (!terms.isEmpty()) {
//					document.add(new Paragraph(lb.termsLabel).setFont(boldFont).setFontSize(10).setMarginTop(10));
//					document.add(new Paragraph(terms).setFont(basicFont).setFontSize(9));
//				}
//			}
			if (isQrCode != null && isQrCode == 1) {
				Table bankTable = new Table(UnitValue.createPercentArray(new float[] { 20, 2, 78 }));
				bankTable.setWidth(UnitValue.createPercentValue(100));
				bankTable.setBorder(Border.NO_BORDER);
				bankTable.setMarginTop(10f);

				bankTable.addCell(createBodyCell("A/c Holder's Name"));
				bankTable.addCell(createBodyCell(":-"));
				bankTable.addCell(createBodyCell(companyName));

				bankTable.addCell(createBodyCell("Bank Name"));
				bankTable.addCell(createBodyCell(":-"));
				bankTable.addCell(createBodyCell(bankName));

				bankTable.addCell(createBodyCell("Account No."));
				bankTable.addCell(createBodyCell(":-"));
				bankTable.addCell(createBodyCell(accountNo));

				bankTable.addCell(createBodyCell("Branch"));
				bankTable.addCell(createBodyCell(":-"));
				bankTable.addCell(createBodyCell(branchName));

				bankTable.addCell(createBodyCell("IFSC Code"));
				bankTable.addCell(createBodyCell(":-"));
				bankTable.addCell(createBodyCell(ifscCode));

				bankTable.addCell(createBodyCell("GST No"));
				bankTable.addCell(createBodyCell(":-"));
				bankTable.addCell(createBodyCell(cmpGstNumber));

				bankTable.addCell(createBodyCell("PAN Card"));
				bankTable.addCell(createBodyCell(":-"));
				bankTable.addCell(createBodyCell(cmpPanNumber));

				document.add(bankTable);

				// ---------- QR code (conditional) ----------

				String qrCodePath = eventData.getQrCodePath();
				if (qrCodePath != null && !qrCodePath.isEmpty()) {
					ImageData qrImg = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + qrCodePath);
					Image qr = new Image(qrImg);
					qr.setWidth(80);
					qr.setHorizontalAlignment(HorizontalAlignment.LEFT);
					document.add(qr);
				}
			}

			// ---------- signature row ----------
			Table sigTable = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
			sigTable.setWidth(UnitValue.createPercentValue(100));
			sigTable.setMarginTop(60);

			sigTable.addCell(signatureCell(lb.customerSignatureLabel, basicFont));
			sigTable.addCell(signatureCell(lb.authorisedSignatoryLabel, basicFont));
			document.add(sigTable);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventId + "/"
					+ pdfFile.getName();
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}
	}

	// =====================================================================
	// helpers
	// =====================================================================

	private static String nz(String s) {
		return s == null || s.isEmpty() ? "" : s;
	}

	private static Integer round(java.lang.Number n) {
		if (n == null)
			return 0;
		return (int) Math.round(n.doubleValue());
	}

	private static Integer round(BigDecimal n) {
		return n == null ? 0 : n.setScale(0, java.math.RoundingMode.HALF_UP).intValue();
	}

	/**
	 * One full-width bordered row inside the client-info box, e.g. "Client Name :
	 * ANOOP SHARMA"
	 */
	private static void addInfoRow(Table table, String label, String value, PdfFont font) {
		Cell cell = new Cell().setBorder(new SolidBorder(ColorConstants.BLACK, 1)).setPadding(4)
				.add(new Paragraph().add(new Text(label + " " + value).setFont(font).setFontSize(11)));
		table.addCell(cell);
	}

	private static void addHeaderCell2(Table table, String text, PdfFont boldFont) {
		Cell cell = new Cell().add(new Paragraph(text).setFont(boldFont).setFontSize(10)).setBackgroundColor(SHADE)
				.setBorder(new SolidBorder(ColorConstants.BLACK, 1)).setPadding(4)
				.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
		table.addHeaderCell(cell);
	}

	private static void addBodyCell(Table table, String text, PdfFont basicFont) {
		Cell cell = new Cell().add(new Paragraph(text).setFont(basicFont).setFontSize(10))
				.setBorder(new SolidBorder(ColorConstants.BLACK, 1)).setPadding(4)
				.setTextAlignment(TextAlignment.CENTER);
		table.addCell(cell);
	}

	/**
	 * A shaded, full-width totals row: label merged across all-but-last column,
	 * value in last column.
	 * 
	 * @param color
	 */
	private static void addTotalRow(Table table, String label, Integer value, PdfFont font, boolean color) {

		Color bgColor = color ? SHADE : ColorConstants.WHITE;

		// Label spans first 7 columns
		Cell labelCell = new Cell(1, 7).add(new Paragraph(label).setFont(font).setFontSize(10))
				.setBackgroundColor(bgColor).setBorder(new SolidBorder(ColorConstants.BLACK, 1)).setPadding(4)
				.setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE);

		table.addCell(labelCell);

		// Value in the last (8th) column
		Cell valueCell = new Cell().add(new Paragraph(String.valueOf(value)).setFont(font).setFontSize(10))
				.setBackgroundColor(bgColor).setBorder(new SolidBorder(ColorConstants.BLACK, 1)).setPadding(4)
				.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);

		table.addCell(valueCell);
	}

	/**
	 * Underline + label, e.g. a blank ruled line with "Customer's Signature"
	 * centred beneath it.
	 */
	private static Cell signatureCell(String label, PdfFont font) {
		Div line = new Div().setHeight(1).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1))
				.setWidth(UnitValue.createPercentValue(70)).setHorizontalAlignment(HorizontalAlignment.CENTER);

		Paragraph labelPara = new Paragraph(label).setFont(font).setFontSize(10).setTextAlignment(TextAlignment.CENTER)
				.setMarginTop(4);

		return new Cell().setBorder(Border.NO_BORDER).add(line).add(labelPara);
	}

	// =====================================================================
	// label set (English only)
	// =====================================================================

	private static class Labels {
		String reportType, header, codeLabel;
		String eventLabel, venueLabel, invoiceDateLabel, eventDateLabel;
		String clientNameLabel, clientNoLabel;
		String colParticulars, colVenue, colDate, colPax, colRate, colOfferedRate, colAmount;
		String finalTotalLabel, totalLabel, receivedPaymentLabel, remainingPaymentLabel, advancePaymentLabel,
				totalAmountLabel, remarkLabel, termsLabel, igstLabel, discountLabel, cgstLabel, sgstLabel,
				amountAfterDiscountLabel, extraPaxLabel, cashLabel, cheqLabel, chequeAmtInclGstLabel, foodTaxLabel,
				serviceTaxLabel, vatTaxLabel;
		String customerSignatureLabel, authorisedSignatoryLabel, wordLabel, billingLabel, clientGstLabel,
				clientEmailLabel;

		static Labels forType(Integer isInvoice) {
			Labels lb = new Labels();
			boolean invoice = isInvoice != null && isInvoice == 1;

			// Matches the two sample PDFs exactly:
			// isInvoice == 1 -> "ESTIMATE" doc, "Invoice No." code, shows Invoice Date
			// isInvoice != 1 -> "PROFORMA INVOICE" doc, "Quotation No" code
			lb.reportType = invoice ? "Invoice" : "Quotation";
			lb.header = invoice ? "TAX INVOICE" : "PROFORMA INVOICE";
			lb.codeLabel = invoice ? "Invoice No.:" : "Quotation No :";
			lb.eventDateLabel = "Event Date :";
			lb.invoiceDateLabel = "Invoice Date :";
			lb.clientNameLabel = "Client Name :";
			lb.clientNoLabel = "Client No. :";
			lb.eventLabel = "Event :";
			lb.venueLabel = "Venue :";
			lb.colParticulars = "Particulars";
			lb.colVenue = "Venue";
			lb.colDate = "Date";
			lb.colPax = "Pax";
			lb.colRate = "Rate";
			lb.colOfferedRate = "Offered Rate";
			lb.colAmount = "Amount";
			lb.finalTotalLabel = "FINAL TOTAL";
			lb.totalLabel = "SUB TOTAL";
			lb.receivedPaymentLabel = "RECEIVED PAYMENT";
			lb.remainingPaymentLabel = "REMAINING PAYMENT";
			lb.advancePaymentLabel = "ADVANCE PAYMENT";
			lb.totalAmountLabel = "TOTAL AMOUNT";
			lb.remarkLabel = "REMARK :";
			lb.termsLabel = "TERMS & CONDITIONS";
			lb.customerSignatureLabel = "Customer's Signature";
			lb.authorisedSignatoryLabel = "Authorised Signatory";
			lb.discountLabel = "Discount";
			lb.cgstLabel = "CGST";
			lb.sgstLabel = "SGST";
			lb.igstLabel = "IGST";
			lb.cashLabel = "Cash Amt.";
			lb.cheqLabel = "Cheque Amt.";
			lb.extraPaxLabel = "Extra Pax";
			lb.amountAfterDiscountLabel = "Amount After Discount";
			lb.chequeAmtInclGstLabel = "Cheque Amt(incl. GST)";
			lb.wordLabel = "In Words : ";
			lb.billingLabel = "Billing Name : ";
			lb.clientGstLabel = "Client Gst : ";
			lb.clientEmailLabel = "Client Email : ";
			lb.foodTaxLabel = "Food Tax";
			lb.serviceTaxLabel = "Service Tax";
			lb.vatTaxLabel = "Vat Tax";
			return lb;
		}
	}

	@Override
	public String generateInvoiceType7(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");

			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyPanNo = eventData.getCmpPanNumber() == null || eventData.getCmpPanNumber().isEmpty() ? ""
					: eventData.getCmpPanNumber();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();

			String shipAddress = eventData.getShipAddress() != null ? eventData.getShipAddress() : "";

			String cmpPanNumber = eventData.getCmpPanNumber() != null ? eventData.getCmpPanNumber() : "";

			Color blackColor = new DeviceRgb(0, 0, 0);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			String header = "ESTIMATE PROPOSAL";
			String code = "Quotation code";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "INVOICE";
				header = "TAX INVOICE";
				code = "Invoice code";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 30, 20);

			// ================ FIRST ROW ===================

			Table topTable = new Table(UnitValue.createPercentArray(new float[] { 30, 40, 30 }));
			topTable.setWidth(UnitValue.createPercentValue(100));
			topTable.setBorder(Border.NO_BORDER);

			// GSTIN
			Cell cell = new Cell().add(new Paragraph("GSTIN : " + cmpGstNumber).setFont(boldFont).setFontSize(11))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
			topTable.addCell(cell);

			// TAX INVOICE
			cell = new Cell().add(new Paragraph("TAX INVOICE").setFont(boldFont).setUnderline().setFontSize(13))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
			topTable.addCell(cell);

			// Original Copy
			cell = new Cell().add(new Paragraph("Original For Recipient").setFont(boldFont).setFontSize(10))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
			topTable.addCell(cell);

			document.add(topTable);

			Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 18, 82 }));
			companyTable.setWidth(UnitValue.createPercentValue(100));
			companyTable.setMarginTop(8);
			companyTable.setBorder(Border.NO_BORDER);

			ImageData imageData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);

			Image logo = new Image(imageData);
			logo.scaleToFit(80, 80);

			companyTable.addCell(
					new Cell().add(logo).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

			Paragraph company = new Paragraph().add(new Text(companyName + "\n").setFont(boldFont).setFontSize(20))

					.add(new Text(companyAddress + "\n").setFont(basicFont).setFontSize(10))
					
					.add(new Text("MSME - UDYAM-GA-01-0000188 (MICRO)"+ "\n") .setFont(basicFont).setFontSize(10))

					.add(new Text("Phone : " + officeNo + " | Email : " + companyEmail + "\n").setFont(boldFont)
							.setFontSize(10))

					.add(new Text("PAN : " + cmpPanNumber + "\n").setFont(basicFont).setFontSize(10))

					.add(new Text("CIN : U55209GA2018PTC013533\n") .setFont(boldFont).setFontSize(10))
					.add(new Text("FDA LICENSE : 10619001000176") .setFont(basicFont).setFontSize(10));
			

			company.setTextAlignment(TextAlignment.CENTER);

			companyTable.addCell(new Cell().add(company).setBorder(Border.NO_BORDER));

			document.add(companyTable);

			Border border = new SolidBorder(ColorConstants.BLACK, 1);

			Table invoiceTable = new Table(UnitValue.createPercentArray(new float[] { 18, 32, 18, 32 }));
			invoiceTable.setWidth(UnitValue.createPercentValue(100));
			invoiceTable.setFixedLayout();

			// ================ Row 1 =================//

			cell = new Cell().setBorder(border).setPadding(5);
			cell.add(new Paragraph("Invoice No.").setFont(boldFont).setFontSize(10));
			invoiceTable.addCell(cell);

			cell = new Cell().setBorder(border).setPadding(5);
			cell.add(new Paragraph(quotationCode).setFont(basicFont).setFontSize(10));
			invoiceTable.addCell(cell);

			cell = new Cell().setBorder(border).setPadding(5);
			cell.add(new Paragraph("Invoice Date").setFont(boldFont).setFontSize(10));
			invoiceTable.addCell(cell);

			cell = new Cell().setBorder(border).setPadding(5);
			cell.add(new Paragraph(generatedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
					.setFont(basicFont).setFontSize(10));
			invoiceTable.addCell(cell);

			document.add(invoiceTable);

			border = new SolidBorder(ColorConstants.BLACK, 1);

			Table addressTable = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
			addressTable.setWidth(UnitValue.createPercentValue(100));
			addressTable.setFixedLayout();

			// ================ BILLING ADDRESS =================//

			Cell billCell = new Cell();
			billCell.setBorder(border);
			billCell.setPadding(6);

			billCell.add(new Paragraph("Customer Name & Billing Address").setFont(boldFont).setFontSize(11));

			billCell.add(new Paragraph(partyName).setFont(boldFont).setFontSize(12));

			billCell.add(new Paragraph(partyAddress == null ? "" : partyAddress).setFont(basicFont).setFontSize(10));

			billCell.add(new Paragraph("GSTIN/UIN : " + (partyGst == null ? "" : partyGst)).setFont(basicFont)
					.setFontSize(10));

			addressTable.addCell(billCell);

			// ================ SHIPPING ADDRESS =================//

			Cell shipCell = new Cell();
			shipCell.setBorder(border);
			shipCell.setPadding(6);

			shipCell.add(new Paragraph("Shipping Address").setFont(boldFont).setFontSize(11));

			shipCell.add(new Paragraph(partyName).setFont(boldFont).setFontSize(12));

			shipCell.add(new Paragraph(shipAddress == null ? "" : shipAddress).setFont(basicFont).setFontSize(10));

			shipCell.add(new Paragraph("GSTIN/UIN : " + (partyGst == null ? "" : partyGst)).setFont(basicFont)
					.setFontSize(10));

			addressTable.addCell(shipCell);

			document.add(addressTable);

			float[] columnWidth3 = { 5f, 30f, 10f, 10f, 10f, 10f, 10f };
			float[] columnWidth6 = { 5f, 30f, 10f, 10f, 10f, 20f };

			Div divData1 = new Div();

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));
			funTbl.setMarginTop(0f);

			Paragraph data = new Paragraph().add(new Text("No.").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Function").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Person").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice == 1) {
				data = new Paragraph().add(new Text("Extra").setFontSize(10).setFont(boldFont));
				cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setTextAlignment(TextAlignment.CENTER);
				funTbl.addCell(cell);
			}

			data = new Paragraph().add(new Text("Rate").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasExtraTax) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice != 1) {
				if (hasExtraTax) {
					data = new Paragraph().add(new Text("Extra Tax").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}

				if (hasTaxRate) {
					data = new Paragraph().add(new Text("Tax Rate").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}
			}

			data = new Paragraph().add(new Text("Total Price").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Div divData2 = new Div();
			divData2.setWidth(UnitValue.createPercentValue(100));
			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";
				System.out.println("fun name : " + fun.getFunctionName());
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();

				if (Boolean.FALSE.equals(fun.getIs_event_function())) {

					// Sr No
					data = new Paragraph()
							.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					cell = new Cell(1, 4);
					data = new Paragraph()
							.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					// Amount
					data = new Paragraph().add(
							new Text(amount.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					if (isInvoice != 1 && !hasExtraTax) {
						cell = new Cell(1, 2);
					} else {
						cell = new Cell();
					}
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					i++;
					continue;
				}

				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(functionPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice == 1) {
					data = new Paragraph().add(
							new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);
				}

				data = new Paragraph()
						.add(new Text(rate.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice != 1) {
					if (hasExtraTax) {
						data = new Paragraph()
								.add(new Text(fun.getExtraTax() != null ? fun.getExtraTax().toString() : "")
										.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}

					if (hasTaxRate) {
						data = new Paragraph().add(new Text(fun.getTaxRate() != null ? fun.getTaxRate().toString() : "")
								.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}
				}

				data = new Paragraph()
						.add(new Text(amount.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasTaxRate) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				i++;
			}
			
			Cell spaceCell = new Cell(1, isInvoice == 1 ? 6 : 7)
			        .setHeight(50f)
			        .setPadding(0)
			        .setBorderLeft(new SolidBorder(blackColor, 1f))
			        .setBorderRight(new SolidBorder(blackColor, 1f))
			        .setBorderBottom(new SolidBorder(blackColor, 1f));

			funTbl2.addCell(spaceCell);

			divData2.add(funTbl2);
			document.add(divData2);

			// ========================================================
			// PART 3A : TAX SUMMARY + BILL SUMMARY
			// ========================================================

			border = new SolidBorder(ColorConstants.BLACK, 1);

			Table summaryTable = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }));
			summaryTable.setWidth(UnitValue.createPercentValue(100));
			summaryTable.setFixedLayout();

			// ========================================================
			// LEFT SIDE
			// ========================================================

			Cell leftCell = new Cell().setPadding(0);

			Table taxTable = new Table(UnitValue.createPercentArray(new float[] { 20, 22, 14, 14, 14, 16 }));

			taxTable.setWidth(UnitValue.createPercentValue(100));

			// ---------------- Header ----------------//

			String headers[] = { "Tax Rate", "Taxable Value", "CGST Amount", "SGST Amount", "IGST Amount",
					"Total Tax" };

			for (String h : headers) {

				Cell c = new Cell();

				c.add(new Paragraph(h).setFont(boldFont).setFontSize(9));

				// c.setBackgroundColor(ColorConstants.LIGHT_GRAY);
				c.setTextAlignment(TextAlignment.CENTER);
				c.setBorder(border);

				taxTable.addHeaderCell(c);
			}

			// ---------------- Data ----------------//

			double totalTax = cgstAmnt + sgstAmnt + igstAmnt;

			taxTable.addCell(new Cell().add(new Paragraph("GST @ 5%").setFont(basicFont).setFontSize(9)));

			taxTable.addCell(new Cell().add(new Paragraph(subTotle.toString()).setFont(basicFont).setFontSize(9))
					.setTextAlignment(TextAlignment.RIGHT));

			taxTable.addCell(new Cell().add(new Paragraph(String.valueOf(cgstAmnt))).setFont(basicFont).setFontSize(9)
					.setTextAlignment(TextAlignment.RIGHT));

			taxTable.addCell(new Cell().add(new Paragraph(String.valueOf(sgstAmnt))).setFont(basicFont).setFontSize(9)
					.setTextAlignment(TextAlignment.RIGHT));

			taxTable.addCell(new Cell().add(new Paragraph(String.valueOf(igstAmnt))).setFont(basicFont).setFontSize(9)
					.setTextAlignment(TextAlignment.RIGHT));

			taxTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", totalTax))).setFont(basicFont)
					.setFontSize(9).setTextAlignment(TextAlignment.RIGHT));

			leftCell.add(taxTable);

			// ========================================================
			// Amount in Words
			// ========================================================

			leftCell.add(new Paragraph().add(new Text("Tax Amount : ").setFont(boldFont))
					.add(new Text("INR " + NumberToWordConverter.convert((long) totalTax) + " Only")).setFontSize(10)
					.setMarginTop(5));

			leftCell.add(new Paragraph().add(new Text("Bill Amount : ").setFont(boldFont))
					.add(new Text("INR " + NumberToWordConverter.convert(grandTotal) + " Only")).setFontSize(10));

			leftCell.add(new Paragraph().add(new Text("Narration : ").setFont(boldFont))
					.add(new Text("Being Goods Sold To " + partyName)).setFontSize(10));

			summaryTable.addCell(leftCell);

			// ========================================================
			// RIGHT SIDE
			// ========================================================

			Cell rightCell = new Cell().setPadding(0);

			Table totalTable = new Table(UnitValue.createPercentArray(new float[] { 65, 35 }));

			totalTable.setWidth(UnitValue.createPercentValue(100));

			// ---------------- Sub Total ----------------//

			totalTable.addCell(new Cell().add(new Paragraph("Sub Total").setFont(boldFont).setFontSize(11))
					.setBorder(Border.NO_BORDER));

			totalTable.addCell(new Cell().add(new Paragraph(subTotle.toString()).setFont(boldFont).setFontSize(11))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			// ---------------- Taxable ----------------//

			totalTable.addCell(new Cell().add(new Paragraph("Taxable Amount").setFont(boldFont).setFontSize(11))
					.setBorder(Border.NO_BORDER));

			totalTable.addCell(new Cell().add(new Paragraph(subTotle.toString()).setFont(boldFont).setFontSize(11))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			// ---------------- CGST ----------------//

			totalTable.addCell(new Cell().add(new Paragraph("CGST")).setFont(basicFont).setFontSize(9)
					.setBorder(Border.NO_BORDER));

			totalTable.addCell(new Cell().add(new Paragraph(String.valueOf(cgstAmnt)).setFontSize(9))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			// ---------------- SGST ----------------//

			totalTable.addCell(new Cell().add(new Paragraph("SGST")).setFont(basicFont).setFontSize(9)
					.setBorder(Border.NO_BORDER));

			totalTable.addCell(new Cell().add(new Paragraph(String.valueOf(sgstAmnt)).setFontSize(9))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			// ---------------- IGST ----------------//

			totalTable.addCell(new Cell().add(new Paragraph("IGST")).setFont(basicFont).setFontSize(9)
					.setBorder(Border.NO_BORDER));

			totalTable.addCell(new Cell().add(new Paragraph(String.valueOf(igstAmnt)).setFontSize(9))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			// ---------------- Round Off ----------------//

			double roundOff = grandTotal - (subTotle + totalTax);

			totalTable.addCell(new Cell().add(new Paragraph("Round Off")).setFont(basicFont).setFontSize(9)
					.setBorder(Border.NO_BORDER));

			totalTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", roundOff)).setFontSize(9))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			// ---------------- Bill Total ----------------//

			Paragraph billTotal = new Paragraph(grandTotal.toString()).setFont(boldFont).setFontSize(14);

			totalTable.addCell(
					new Cell().add(new Paragraph("Bill Total").setFont(boldFont)).setBorderTop(new SolidBorder(1)));

			totalTable.addCell(
					new Cell().add(billTotal).setTextAlignment(TextAlignment.RIGHT).setBorderTop(new SolidBorder(1)));

			rightCell.add(totalTable);

			summaryTable.addCell(rightCell);

			// ========================================================

			document.add(summaryTable);

			// =====================================================
			// TERMS & BANK DETAILS (100% WIDTH)
			// =====================================================

			Table termsTable = new Table(UnitValue.createPercentArray(new float[] { 100 }));
			termsTable.setWidth(UnitValue.createPercentValue(100));
			termsTable.setBorder(new SolidBorder(ColorConstants.BLACK, 1));

			Cell termsCell = new Cell();
			termsCell.setPadding(5);

			// Payment Terms
			termsCell.add(new Paragraph(
					"1. All Payments should be made direct to the company by cheque/RTGS/NEFT/DD only as per the agreed contract terms.")
					.setFont(basicFont).setFontSize(10));

			termsCell.add(
					new Paragraph("2. All disputes subject to Goa Jurisdiction.").setFont(basicFont).setFontSize(10));

			termsCell.add(new Paragraph(
					"3. Interest at the rate of 18% will be charged for the payment made after the due date.")
					.setFont(basicFont).setFontSize(10));

			// Bank Details
			Paragraph bank = new Paragraph();
			bank.setMarginTop(5);

			bank.add(new Text("Bank Details : ").setFont(boldFont).setFontSize(10));
			bank.add(new Text("Name of the Bank ").setFont(boldFont).setFontSize(10));
			bank.add(new Text(bankName + " | ").setFontSize(10));
			bank.add(new Text("Account No ").setFont(boldFont).setFontSize(10));
			bank.add(new Text(accountNo + " | ").setFontSize(10));
			bank.add(new Text("Branch ").setFont(boldFont).setFontSize(10));
			bank.add(new Text(branchName + " | ").setFontSize(10));
			bank.add(new Text("Type of A/c ").setFont(boldFont).setFontSize(10));
			bank.add(new Text("CC | ").setFontSize(10));
			bank.add(new Text("IFSC Code ").setFont(boldFont).setFontSize(10));
			bank.add(new Text(ifscCode).setFontSize(10));

			bank.setTextAlignment(TextAlignment.CENTER);

			termsCell.add(bank);

			termsTable.addCell(termsCell);

			document.add(termsTable);

			// ======================================================
			// DECLARATION + SIGNATURE
			// ======================================================

			border = new SolidBorder(ColorConstants.BLACK, 1);

			Table declarationTable = new Table(UnitValue.createPercentArray(new float[] { 53, 47 }));
			declarationTable.setWidth(UnitValue.createPercentValue(100));
			declarationTable.setFixedLayout();

			// ======================================================
			// LEFT SIDE
			// ======================================================

			leftCell = new Cell();
			leftCell.setPadding(5);
			leftCell.setMinHeight(140);

			Paragraph declaration = new Paragraph();
			declaration.setMultipliedLeading(1.1f);

			declaration.add(new Text("Declaration:\n").setFont(boldFont).setFontSize(10));

			declaration.add(new Text("We declare that this invoice shows the actual price of the goods / services\n")
					.setFont(basicFont).setFontSize(10));

			declaration.add(new Text("described and that all particulars are true and correct.\n\n").setFont(basicFont)
					.setFontSize(10));

			declaration.add(new Text("Terms and Conditions:").setFont(boldFont).setFontSize(10));

			leftCell.add(declaration);

			declarationTable.addCell(leftCell);

			// ======================================================
			// RIGHT SIDE
			// ======================================================

			rightCell = new Cell();
			rightCell.setPadding(5);
			rightCell.setMinHeight(140);

			// Company Name
			company = new Paragraph("For " + companyName).setFont(boldFont).setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT);

			rightCell.add(company);

			// Empty space for signature
			rightCell.add(new Paragraph("\n\n\n\n\n\n"));

			// Bottom Signature Table
			Table signTable = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
			signTable.setWidth(UnitValue.createPercentValue(100));

			Cell receiver = new Cell()
					.add(new Paragraph("Receiver's Signature").setFont(boldFont).setFontSize(10)
							.setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.BOTTOM);

			Cell authorised = new Cell()
					.add(new Paragraph("Authorised Signatory").setFont(boldFont).setFontSize(10)
							.setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.BOTTOM);

			signTable.addCell(receiver);
			signTable.addCell(authorised);

			rightCell.add(signTable);

			declarationTable.addCell(rightCell);

			// ======================================================

			document.add(declarationTable);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}
	}

	@Override
	public String generateInvoiceType8(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");

			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			String fssaiNumber = eventData.getFssaiNumber() == null || eventData.getFssaiNumber().trim().isEmpty() ? ""
					: eventData.getFssaiNumber();
			String hsnNumber = eventData.getHsnNumber() == null || eventData.getHsnNumber().trim().isEmpty() ? ""
					: eventData.getHsnNumber();

			String cinNumber = eventData.getCinNumber() == null || eventData.getCinNumber().trim().isEmpty() ? ""
					: eventData.getCinNumber();
			String fdaLincense = eventData.getFdaLincense() == null || eventData.getFdaLincense().trim().isEmpty() ? ""
					: eventData.getFdaLincense();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();

			String shipAddress = eventData.getShipAddress() != null ? eventData.getShipAddress() : "";

			String VatTax = eventData.getVatTax();
			Integer vatTaxAmount = eventData.getVatTaxAmount();
			Integer vatTaxTotalAmount = eventData.getVatTaxTotalAmount();

			Color blackColor = new DeviceRgb(0, 0, 0);

//			ImageData img = menuPreparationServiceImpl
//					.loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/papayatreelogo.png");

			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			String header = "ESTIMATE PROPOSAL";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "INVOICE";
				header = "TAX INVOICE";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 100, 20);

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new PageBorderEventHandler(1f, blackColor, 20f));

			float[] columnWidth1 = { 30f, 70f };
			Table cmpData = new Table(UnitValue.createPercentArray(columnWidth1));
			cmpData.setWidth(UnitValue.createPercentValue(100));
			cmpData.setMargin(10f);

			Paragraph data = new Paragraph()
					.add(new Text("Papaya Tree Hotels & Resorts LLP").setFontSize(22).setFont(boldFont)).setPadding(0f)
					.setMultipliedLeading(1f);

			Cell cell = new Cell(3, 1).add(logo).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			data = new Paragraph()
					.add(new Text("Rau Bypass Square, Opp. Haridya Eye Hospital, \nA.B Road Indore MP-453331")
							.setFontSize(12).setFont(boldFont).setFontColor(blackColor))
					.setMultipliedLeading(1.1f);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			data = new Paragraph().add(new Text(
					"Contact Us: +91 731 471 4747 \n8224855547 \nwww.papayatreehotels.com\nnamaste@papayatreehotels.com")
					.setFontSize(12).setFont(boldFont).setFontColor(blackColor)).setMultipliedLeading(1f);
			cell = new Cell().add(data).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);
			cmpData.addCell(cell);

			Table headerData = new Table(UnitValue.createPercentArray(new float[] { 35f, 31.66f, 33.34f }));
			headerData.setWidth(UnitValue.createPercentValue(100f));
			headerData.setFixedLayout();

			cell = new Cell(1, 3).add(new Paragraph(header).setFontSize(16f).simulateBold())
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			headerData.addCell(cell);

			cell = new Cell().add(new Paragraph("Hotel GSTN #     : 23AARFP6801D3ZB ").setFontSize(10f))
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			headerData.addCell(cell);

			cell = new Cell().add(new Paragraph("SYMPOSIUM").simulateBold().setFontSize(12f))
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			headerData.addCell(cell);

			cell = new Cell().add(new Paragraph("Re-Printed").setFontSize(10f)).setTextAlignment(TextAlignment.CENTER)
					.setBorder(Border.NO_BORDER);
			headerData.addCell(cell);

			if (isCompanyDetails == 1) {
				document.add(cmpData);
			}

			document.add(headerData);

			Table detailsTable = new Table(
					UnitValue.createPercentArray(new float[] { 25f, 1.5f, 45f, 25f, 1.5f, 33f }));
			detailsTable.setWidth(UnitValue.createPercentValue(100f));
//			detailsTable.setFixedLayout();

			cell = new Cell()
					.add(new Paragraph("Name").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(partyName).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("FSSAI No").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f)).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("11418850000154").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER).setPadding(0).setBorderTop(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Company Name").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setPadding(0);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(partyName).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setPadding(0).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("HSN No").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph("996334").setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell(3, 1)
					.add(new Paragraph("Address").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell(3, 1).add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell(3, 1)
					.add(new Paragraph(isInvoice == 1 ? shipAddress : partyAddress).setFont(basicFont).setPadding(0)
							.setFontSize(10).setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Bill No").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(partyAddress).setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Bill Date & Time").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(generatedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
					.setFont(basicFont).setPadding(0).setFontSize(10).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("GSTN Bill #").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph("4258BQBIL0000036").setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("Guest GSTN #").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setPadding(0).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			cell = new Cell().add(new Paragraph(partyGst).setFont(basicFont).setPadding(0).setFontSize(10)
					.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			detailsTable.addCell(cell);

			document.add(detailsTable);

			float[] columnWidth3 = { 5f, 30f, 10f, 10f, 10f, 10f, 10f };
			float[] columnWidth6 = { 5f, 30f, 10f, 10f, 10f, 20f };

			Div divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));
			divData1.setBorderTop(new SolidBorder(blackColor, 1f));
			divData1.setBorderBottom(new SolidBorder(blackColor, 1f));

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));

			data = new Paragraph().add(new Text("No.").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Function").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Person").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice == 1) {
				data = new Paragraph().add(new Text("Extra").setFontSize(10).setFont(boldFont));
				cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
				funTbl.addCell(cell);
			}

			data = new Paragraph().add(new Text("Rate").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasExtraTax) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice != 1) {
				if (hasExtraTax) {
					data = new Paragraph().add(new Text("Extra Tax").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}

				if (hasTaxRate) {
					data = new Paragraph().add(new Text("Tax Rate").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}
			}

			data = new Paragraph().add(new Text("Total Price").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(25f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";
				System.out.println("fun name : " + fun.getFunctionName());
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();

				if (Boolean.FALSE.equals(fun.getIs_event_function())) {

					// Sr No
					data = new Paragraph()
							.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					cell = new Cell(1, 5);
					data = new Paragraph()
							.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					// Amount
					data = new Paragraph()
							.add(new Text("Rs. " + amount).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					if (isInvoice != 1 && !hasExtraTax) {
						cell = new Cell(1, 2);
					} else {
						cell = new Cell();
					}
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					i++;
					continue;
				}

				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(functionPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice == 1) {
					data = new Paragraph().add(
							new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);
				}

				data = new Paragraph().add(
						new Text("Rs. " + rate.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice != 1) {
					if (hasExtraTax) {
						data = new Paragraph()
								.add(new Text(fun.getExtraTax() != null ? fun.getExtraTax().toString() : "")
										.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(Border.NO_BORDER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}

					if (hasTaxRate) {
						data = new Paragraph().add(new Text(fun.getTaxRate() != null ? fun.getTaxRate().toString() : "")
								.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(Border.NO_BORDER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}
				}

				data = new Paragraph().add(new Text("Rs. " + amount.toString()).setFontSize(10).setFont(basicFont)
						.setFontColor(blackColor));

				if (isInvoice != 1 && !hasTaxRate) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(Border.NO_BORDER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				i++;
			}

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			data = new Paragraph()
					.add(new Text("Subtotal   :").setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPadding(0).setTextAlignment(TextAlignment.RIGHT);
			cell.setBorder(Border.NO_BORDER);
			funTbl2.addCell(cell);

			cell = new Cell();
			data = new Paragraph()
					.add(new Text(subTotle.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
			cell.setBorder(Border.NO_BORDER);
			funTbl2.addCell(cell);

			// Discount
			data = new Paragraph()
					.add(new Text("Discount   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(discount.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Amount After Discount   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}
			double amountAfterDiscount = subTotle - discount;
			data = new Paragraph()
					.add(new Text(String.valueOf((int) Math.round(amountAfterDiscount))).setFont(basicFont)
							.setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph()
					.add(new Text("Cash Amt   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cashPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cheqPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell(1, 5).setBorder(Border.NO_BORDER).add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(new Text("Vat Tax (" + VatTax + " %)   :").setFont(basicFont).setFontSize(10)
					.setFontColor(blackColor)).setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (vatTaxTotalAmount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text(vatTaxTotalAmount.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (vatTaxTotalAmount > 0) {
				funTbl2.addCell(cell);
			}

			// SGST
			data = new Paragraph().add(
					new Text("SGST (" + sgst + ")   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(sgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// CGST
			data = new Paragraph().add(
					new Text("CGST (" + cgst + ")   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// IGST
			data = new Paragraph().add(
					new Text("IGST (" + igst + ")   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(igstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(new Text("Cheque Amt(incl. GST)   :").setFont(basicFont).setFontSize(10)
						.setFontColor(blackColor)).setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.setBorder(Border.NO_BORDER).add(data);
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + chequeAmtInclGst).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);

				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				funTbl2.addCell(cell);

			}

			if (isAdvance == 1) {
				data = new Paragraph().add(
						new Text("Advance Payment   :").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}

				data = new Paragraph().add(
						new Text("Rs. " + advancePayment).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(10f);
				cell = new Cell().setBorder(Border.NO_BORDER).add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}
			}

			// ==========================================
			// Grand Total
			// ==========================================

			// grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total   :").setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			funTbl2.addCell(cell);

			data = new Paragraph().add(new Text(grandTotal.toString()).setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setBorderTop(new SolidBorder(blackColor, 3f))
					.setBorderBottom(new SolidBorder(blackColor, 3f)).setPaddingLeft(10f);

			cell = new Cell().setBorder(Border.NO_BORDER).add(data);
			funTbl2.addCell(cell);

			// In Words
			data = new Paragraph().add(new Text("In Words : ").setFont(boldFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert(grandTotal) + " Only.").setFont(basicFont)
							.setFontSize(10))
					.setPaddingLeft(10f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);

			if (isInvoice == 1) {
				cell = new Cell(1, 6);
			} else {
				cell = new Cell(1, 7);
			}
			cell.setBorder(Border.NO_BORDER).add(data);
			funTbl2.addCell(cell);

			divData1.add(funTbl2);
			document.add(divData1);

			if (cgstAmnt != 0 || igstAmnt != 0 || sgstAmnt != 0) {
				Table taxSummaryTable = new Table(UnitValue.createPercentArray(new float[] { 40f, 30f, 30f }));
				taxSummaryTable.setWidth(UnitValue.createPercentValue(60f));
				taxSummaryTable.setMarginTop(10f);
				taxSummaryTable.setFixedLayout();

				cell = new Cell(1, 3)
						.add(new Paragraph("Tax Summary").setFont(basicFont).setFontSize(12).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				taxSummaryTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph("Tax Details").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(2).setPaddingTop(2).setBorder(Border.NO_BORDER)
						.setBorderTop(new SolidBorder(blackColor, 1f)).setBorderBottom(new SolidBorder(blackColor, 1f))
						.simulateBold();
				taxSummaryTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph("Taxable Amount").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(2).setPaddingTop(2).setBorder(Border.NO_BORDER)
						.setBorderTop(new SolidBorder(blackColor, 1f)).setBorderBottom(new SolidBorder(blackColor, 1f))
						.simulateBold();
				taxSummaryTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph("Tax Amount").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(2).setPaddingTop(2).setBorder(Border.NO_BORDER)
						.setBorderTop(new SolidBorder(blackColor, 1f)).setBorderBottom(new SolidBorder(blackColor, 1f))
						.simulateBold();
				taxSummaryTable.addCell(cell);

				if (cgstAmnt != 0) {
					cell = new Cell()
							.add(new Paragraph("Central GST @ " + cgst).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph("").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(cgstAmnt.toString()).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);
				}

				if (sgstAmnt != 0) {
					cell = new Cell()
							.add(new Paragraph("State GST @ " + cgst).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph("").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(sgstAmnt.toString()).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);
				}

				if (igstAmnt != 0) {
					cell = new Cell()
							.add(new Paragraph("Integrated GST @ " + igst).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph("").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
									.setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(igstAmnt.toString()).setFont(basicFont).setFontSize(9)
									.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
							.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER)
							.simulateBold();
					taxSummaryTable.addCell(cell);
				}

				document.add(taxSummaryTable);
			}

			Table signTable = new Table(UnitValue.createPercentArray(new float[] { 33.33f, 33.33f, 33.34f }));
			signTable.setWidth(UnitValue.createPercentValue(100f));
			signTable.setMarginTop(50f);
			signTable.setFixedLayout();

			data = new Paragraph().add(new Text("Cashier Signature").setFont(basicFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER);
			signTable.addCell(new Cell().add(data).setBorder(Border.NO_BORDER));

			signTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			data = new Paragraph().add(new Text("Guest Signature").setFont(basicFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER);
			signTable.addCell(new Cell().add(data).setBorder(Border.NO_BORDER));

			document.add(signTable);

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int j = 1; j <= totalPages; j++) {

				PdfPage page = pdfDocument.getPage(j);
				Rectangle pageSize = page.getPageSize();

				PdfCanvas pdfCanvas = new PdfCanvas(page);

				// Draw top border line above footer
				pdfCanvas.saveState();
				pdfCanvas.setStrokeColor(blackColor);
				pdfCanvas.setLineWidth(1f);

				float leftMargin = 20f;
				float rightMargin = 20f;
				float lineY = 45f;

				pdfCanvas.moveTo(leftMargin, lineY);
				pdfCanvas.lineTo(pageSize.getWidth() - rightMargin, lineY);
				pdfCanvas.stroke();
				pdfCanvas.restoreState();

				// Add footer text
				Canvas canvas = new Canvas(pdfCanvas, pageSize);

				canvas.showTextAligned("** Thank You. Visit Again. **", pageSize.getWidth() / 2, 25,
						TextAlignment.CENTER);

				canvas.close();
			}

			if (isTermsCond == 1) {
				UserTermsAndConditionEntity terms = userTermsRepository
						.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(userId, reportType);

				if (terms == null) {
					// handle gracefully
					Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
							.setFontSize(12).setFontColor(ColorConstants.GRAY);

					document.add(noData);
				} else {

					List<TermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = termsFeaturesRepository.findByUserTermsConditionIdAndIsDeleteFalse(terms.getId());

					document.add(new AreaBreak());

					Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(boldFont).setFontSize(16)
							.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f);

					document.add(termsTitle);

					ISplitCharacters breakAll = new ISplitCharacters() {
						@Override
						public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
							return true; // break anywhere
						}
					};

					if (features == null || features.isEmpty()) {

						Paragraph noData = new Paragraph("No Terms & Conditions available.").setFont(basicFont)
								.setFontSize(12).setFontColor(ColorConstants.GRAY);

						document.add(noData);

					} else {

						int index = 1;

						for (TermsAndConditionFeaturesEntity feature : features) {

							String desc = feature.getDescription();

							Paragraph term = new Paragraph(index + ". " + desc).setFont(basicFont).setFontSize(12)
									.setMarginBottom(5f).setSplitCharacters((text, glyphPos) -> true);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}
	}

	@Override
	public String generateInvoiceType9(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");

			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();

			String shipAddress = eventData.getShipAddress() != null ? eventData.getShipAddress() : "";

			String cmpPanNumber = eventData.getCmpPanNumber() != null ? eventData.getCmpPanNumber() : "";

			String VatTax = eventData.getVatTax();
			Integer vatTaxAmount = eventData.getVatTaxAmount();
			Integer vatTaxTotalAmount = eventData.getVatTaxTotalAmount();

			Color blackColor = new DeviceRgb(0, 0, 0);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			String header = "ESTIMATE PROPOSAL";
			String code = "Quotation code";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "INVOICE";
				header = "TAX INVOICE";
				code = "Invoice code";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 30, 20);

			Paragraph headerPara = new Paragraph().add(header).setFontSize(16f).simulateBold()
					.setTextAlignment(TextAlignment.CENTER);

			document.add(headerPara);

			Border border = new SolidBorder(ColorConstants.BLACK, 1);

			// Main Table (Left 53%, Right 47%)
			Table mainTable = new Table(UnitValue.createPercentArray(new float[] { 60, 40 }));
			mainTable.setWidth(UnitValue.createPercentValue(100));
			mainTable.setFixedLayout();

//			Cell companyCell = new Cell(1, 2).setBorder(border).setPadding(5);
//
//			companyCell.add(new Paragraph()
//					.add(new Text("Blue Leaf Resort - PAPAYA TREE HOTELS").simulateBold().setFontSize(14))
//					.add(new Text(
//							"\n98/2/2, Blue Leaf Resort, Bypass Road, Bicholi Mardana, Indore - 452016\nGSTIN/UIN : 23AAZFP9944P1ZY\nState Name :  Madhya Pradesh, Code : 23\nE-Mail : finance@blueleafhotels.com")
//							.setFontSize(12)));
//
//			mainTable.addCell(companyCell);
			
			Cell companyCell = new Cell(1, 2)
			        .setBorder(border)
			        .setPadding(5);

			ImageData logoData = menuPreparationServiceImpl
			        .loadImageFromResource("/flipbook/pages/blue_leaf_logo.jpg");

			Image logo = new Image(logoData);
			logo.setWidth(UnitValue.createPercentValue(100f));
			logo.setAutoScale(false);

			Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 75, 25 }))
			        .useAllAvailableWidth();

			companyTable.addCell(new Cell()
			        .add(new Paragraph()
			                .add(new Text("Blue Leaf Resort - PAPAYA TREE HOTELS")
			                        .simulateBold()
			                        .setFontSize(14))
			                .add(new Text(
			                        "\n98/2/2, Blue Leaf Resort, Bypass Road, Bicholi Mardana, Indore - 452016"
			                        + "\nGSTIN/UIN : 23AAZFP9944P1ZY"
			                        + "\nState Name : Madhya Pradesh, Code : 23"
			                        + "\nE-Mail : finance@blueleafhotels.com"
			                        + "\nFSSAI No : 21421850003033")
			                        .setFontSize(12)))
			        .setBorder(Border.NO_BORDER)
			        .setVerticalAlignment(VerticalAlignment.MIDDLE)
			        .setPadding(3));

			companyTable.addCell(new Cell()
			        .add(logo)
			        .setBorder(Border.NO_BORDER)
			        .setVerticalAlignment(VerticalAlignment.MIDDLE)
			        .setPadding(3));
			
			companyCell.add(companyTable);

			mainTable.addCell(companyCell);

			Cell buyerCell = new Cell(2, 1).setBorder(border).setPadding(5);

			buyerCell.add(new Paragraph().add(new Text("Buyer (Bill To)\n").setFontSize(12))
					.add(new Text(partyName).simulateBold().setFontSize(14))
					.add(new Text("\n" + (isInvoice == 1 ? shipAddress : partyAddress) + "\nGSTIN/UIN : " + partyGst)
							.setFontSize(12)));

			mainTable.addCell(buyerCell);

			Cell invoiceNo = new Cell().setBorder(border).setPadding(5);
			invoiceNo.add(new Paragraph(code).setFontSize(12));
			invoiceNo.add(new Paragraph(quotationCode).simulateBold().setFontSize(12));
			mainTable.addCell(invoiceNo);

			Cell date = new Cell().setBorder(border).setPadding(5);
			date.add(new Paragraph("Dated").setFontSize(12));
			date.add(new Paragraph(generatedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).simulateBold()
					.setFontSize(12));
			mainTable.addCell(date);

			document.add(mainTable);

			float[] columnWidth3 = { 5f, 30f, 10f, 10f, 10f, 10f, 10f };
			float[] columnWidth6 = { 5f, 30f, 10f, 10f, 10f, 20f };

			Div divData1 = new Div();

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));
			funTbl.setMarginTop(0f);

			Paragraph data = new Paragraph().add(new Text("No.").setFontSize(10).setFont(boldFont));
			Cell cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Function").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph().add(new Text("Person").setFontSize(10).setFont(boldFont));
			cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice == 1) {
				data = new Paragraph().add(new Text("Extra").setFontSize(10).setFont(boldFont));
				cell = new Cell().add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setTextAlignment(TextAlignment.CENTER);
				funTbl.addCell(cell);
			}

			data = new Paragraph().add(new Text("Rate").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasExtraTax) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice != 1) {
				if (hasExtraTax) {
					data = new Paragraph().add(new Text("Extra Tax").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}

				if (hasTaxRate) {
					data = new Paragraph().add(new Text("Tax Rate").setFontSize(10).setFont(boldFont));
					cell = new Cell().add(data).setPadding(0).setHeight(25f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}
			}

			data = new Paragraph().add(new Text("Total Price").setFontSize(10).setFont(boldFont));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setHeight(25f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";
				System.out.println("fun name : " + fun.getFunctionName());
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();

				if (Boolean.FALSE.equals(fun.getIs_event_function())) {

					// Sr No
					data = new Paragraph()
							.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					cell = new Cell(1, 4);
					data = new Paragraph()
							.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					// Amount
					data = new Paragraph().add(
							new Text(amount.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					if (isInvoice != 1 && !hasExtraTax) {
						cell = new Cell(1, 2);
					} else {
						cell = new Cell();
					}
					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);

					i++;
					continue;
				}

				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(functionPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice == 1) {
					data = new Paragraph().add(
							new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
					cell.setBorderBottom(new SolidBorder(blackColor, 1f));
					funTbl2.addCell(cell);
				}

				data = new Paragraph()
						.add(new Text(rate.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				if (isInvoice != 1) {
					if (hasExtraTax) {
						data = new Paragraph()
								.add(new Text(fun.getExtraTax() != null ? fun.getExtraTax().toString() : "")
										.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}

					if (hasTaxRate) {
						data = new Paragraph().add(new Text(fun.getTaxRate() != null ? fun.getTaxRate().toString() : "")
								.setFontSize(10).setFont(basicFont).setFontColor(blackColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorderBottom(new SolidBorder(blackColor, 1f));
						funTbl2.addCell(cell);
					}
				}

				data = new Paragraph()
						.add(new Text(amount.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));

				if (isInvoice != 1 && !hasTaxRate) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorderBottom(new SolidBorder(blackColor, 1f));
				funTbl2.addCell(cell);

				i++;
			}

			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			data = new Paragraph()
					.add(new Text("Subtotal").setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setTextAlignment(TextAlignment.RIGHT);
			funTbl2.addCell(cell);

			cell = new Cell();
			data = new Paragraph()
					.add(new Text(subTotle.toString()).setFontSize(10).setFont(basicFont).setFontColor(blackColor));
			cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
			funTbl2.addCell(cell);

			// Discount
			data = new Paragraph().add(new Text("Discount").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(discount.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Amount After Discount").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}
			double amountAfterDiscount = subTotle - discount;
			data = new Paragraph()
					.add(new Text(String.valueOf((int) Math.round(amountAfterDiscount))).setFont(basicFont)
							.setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);

			cell = new Cell().add(data);

			if (discount > 0) {
				funTbl2.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph().add(new Text("Cash Amt").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}

			cell.add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cashPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (cashPay > 0) {
				funTbl2.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cheqPay.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell(1, 5).add(data);
			if (cheqPay > 0) {
				funTbl2.addCell(cell);
			}

			// VAT
			data = new Paragraph().add(new Text("Vat Tax (" + VatTax + "%)").setFont(basicFont).setFontSize(10)
					.setFontColor(blackColor)).setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (vatTaxTotalAmount > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph().add(
					new Text(vatTaxTotalAmount.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (vatTaxTotalAmount > 0) {
				funTbl2.addCell(cell);
			}

			// SGST
			data = new Paragraph()
					.add(new Text("SGST (" + sgst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(sgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (sgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// CGST
			data = new Paragraph()
					.add(new Text("CGST (" + cgst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(cgstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (cgstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// IGST
			data = new Paragraph()
					.add(new Text("IGST (" + igst + ")").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text(igstAmnt.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
			cell = new Cell().add(data);
			if (igstAmnt > 0) {
				funTbl2.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(
						new Text("Cheque Amt(incl. GST)").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.add(data);
				funTbl2.addCell(cell);

				data = new Paragraph().add(new Text(String.valueOf((int) chequeAmtInclGst)).setFont(basicFont)
						.setFontSize(10).setFontColor(blackColor)).setTextAlignment(TextAlignment.CENTER)
						.setPaddingLeft(0f);

				cell = new Cell().add(data);
				funTbl2.addCell(cell);

			}

			if (isAdvance == 1) {
				data = new Paragraph()
						.add(new Text("Advance Payment").setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);
				if (isInvoice == 1) {
					cell = new Cell(1, 5);
				} else {
					cell = new Cell(1, 6);
				}
				cell.add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}

				data = new Paragraph().add(
						new Text(advancePayment.toString()).setFont(basicFont).setFontSize(10).setFontColor(blackColor))
						.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);
				cell = new Cell().add(data);
				if (advancePayment > 0) {
					funTbl2.addCell(cell);
				}
			}

			// ==========================================
			// Grand Total
			// ==========================================

			// grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total").setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f);

			if (isInvoice == 1) {
				cell = new Cell(1, 5);
			} else {
				cell = new Cell(1, 6);
			}
			cell.add(data);
			funTbl2.addCell(cell);

			data = new Paragraph().add(new Text(grandTotal.toString()).setFont(boldFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0f);

			cell = new Cell().add(data);
			funTbl2.addCell(cell);

			divData1.add(funTbl2);
			document.add(divData1);

			// ================== FOOTER TABLE ==================
			Table footerTable = new Table(UnitValue.createPercentArray(new float[] { 53, 47 }));
			footerTable.setWidth(UnitValue.createPercentValue(100));
			footerTable.setFixedLayout();
			footerTable.setBorder(new SolidBorder(ColorConstants.BLACK, 1));

			// ==================================================
			// Amount Chargeable (in words)
			// ==================================================
			Paragraph amountWord = new Paragraph()
					.add(new Text("Amount Chargeable (in words)\n").setFont(basicFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert(grandTotal) + " Only").setFont(boldFont)
							.setFontSize(10))
					.setMultipliedLeading(1f).setPaddingLeft(2);

			cell = new Cell(1, 2).add(amountWord).setPadding(4)
					.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1));

			footerTable.addCell(cell);

			double totalTaxAmount = cgstAmnt + sgstAmnt + igstAmnt;

			amountWord = new Paragraph().add(new Text("Tax Amount (in words)  :").setFont(basicFont).setFontSize(10))
					.add(new Text(NumberToWordConverter.convert((long) totalTaxAmount) + " Only").setFont(boldFont)
							.setFontSize(10))
					.setMultipliedLeading(1f).setPaddingLeft(2);

			cell = new Cell(1, 2).add(amountWord).setPadding(4).setBorderBottom(Border.NO_BORDER);

			footerTable.addCell(cell);

			// ==================================================
			// Left Cell (PAN)
			// ==================================================
			Cell leftCell = new Cell();
			leftCell.setBorder(Border.NO_BORDER);
			leftCell.setPadding(5);
			leftCell.setMinHeight(120);

			Paragraph pan = new Paragraph().add(new Text("Company's PAN : ").setFont(basicFont).setFontSize(10))
					.add(new Text("AAZFP9944P").setFont(boldFont).setFontSize(10));

			leftCell.add(new Paragraph("\n\n"));
			leftCell.add(pan);

			footerTable.addCell(leftCell);

			// ==================================================
			// Right Cell (Bank Details + Signature)
			// ==================================================
			Cell rightCell = new Cell();
			rightCell.setBorderLeft(Border.NO_BORDER);
			rightCell.setBorderTop(Border.NO_BORDER);
			rightCell.setBorderRight(Border.NO_BORDER);
			rightCell.setBorderBottom(Border.NO_BORDER);
			rightCell.setPadding(0);

			// --------------- Bank Details ----------------
			Div bankDiv = new Div();
			bankDiv.setPaddings(0, 5, 5, 5);

			bankDiv.add(new Paragraph("Company's Bank Details").setFont(boldFont).setFontSize(11));

			Table bankTable = new Table(UnitValue.createPercentArray(new float[] { 44, 3, 53 }));
			bankTable.setWidth(UnitValue.createPercentValue(100));
			bankTable.setBorder(Border.NO_BORDER);

			bankTable.addCell(createBodyCell("A/c Holder's Name"));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell("Papaya Tree Hotels").setFont(boldFont));

			bankTable.addCell(createBodyCell("Bank Name"));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell("HDFC Bank Ltd.").setFont(boldFont));

			bankTable.addCell(createBodyCell("Account No."));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell("50200058144559").setFont(boldFont));

			bankTable.addCell(createBodyCell("Branch & IFSC Code"));
			bankTable.addCell(createBodyCell(":"));
			bankTable.addCell(createBodyCell("Pipliyahana Branch & HDFC0009387").setFont(boldFont));

			bankDiv.add(bankTable);

			rightCell.add(bankDiv);

			// --------------- Signature ----------------
			Div signDiv = new Div();
			signDiv.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
			signDiv.setHeight(80);
			signDiv.setPaddings(0, 5, 0, 5);
			signDiv.setKeepTogether(true);

			signDiv.add(new Paragraph("for Blue Leaf Resort - PAPAYA TREE HOTELS").setFont(boldFont).setFontSize(11));

			signDiv.add(new Paragraph("Authorised Signatory").setFont(basicFont).setFontSize(12)
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(33f));

			rightCell.add(signDiv);

			footerTable.addCell(rightCell);

			// ==================================================
			document.add(footerTable);

			// ================= PAGE NUMBERS =================
			int totalPages = pdfDocument.getNumberOfPages();
			System.out.println("total pages : " + totalPages);
			for (int j = 1; j <= totalPages; j++) {
				PdfPage page = pdfDocument.getPage(j);

				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				Paragraph footer = new Paragraph().add("This is a Computer Generated Invoice").setFontSize(11f);

				canvas.showTextAligned(footer, page.getPageSize().getWidth() / 2, 22, TextAlignment.CENTER);
				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}
	}
	
	@Override
	public String generateInvoiceType10(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/fraunces-Regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/fraunces-Bold.ttf");

			System.out.println("English font loaded successfully");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

			QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
					isInvoice, isDecore);

			String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();

			String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();

			String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();

			String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();

			String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
					? ""
					: eventData.getCompanyAddress();

			String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
					: eventData.getCompanyLogo();

			String accountHolderName = eventData.getAccountHolderName() == null
					|| eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

			String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
					: eventData.getAccountNo();

			String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
					: eventData.getBankName();

			String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
					: eventData.getIfscCode();

			String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

			String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
					: eventData.getBranchName();

			String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

			String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
					: eventData.getEventNo();

			String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();

			String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
					: eventData.getPartyAddress();

			String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
					: eventData.getPartyEmail();

			String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();

			String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
					: eventData.getPartyGst();

			String quotationDueDate = eventData.getQuotationDueDate() == null
					|| eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

			String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
					: eventData.getQuotationCode();

			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

			String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

			String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

			String fssaiNumber = eventData.getFssaiNumber() == null || eventData.getFssaiNumber().trim().isEmpty() ? ""
					: eventData.getFssaiNumber();
			String hsnNumber = eventData.getHsnNumber() == null || eventData.getHsnNumber().trim().isEmpty() ? ""
					: eventData.getHsnNumber();

			String cinNumber = eventData.getCinNumber() == null || eventData.getCinNumber().trim().isEmpty() ? ""
					: eventData.getCinNumber();
			String fdaLincense = eventData.getFdaLincense() == null || eventData.getFdaLincense().trim().isEmpty() ? ""
					: eventData.getFdaLincense();

			Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
			Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

			Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

			Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

			Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

			Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

			Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

			Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

			Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

			Integer advancePayment = eventData.getAdvancePayment() == null ? 0
					: (int) Math.round(eventData.getAdvancePayment());

			Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
					: (int) Math.round(eventData.getRemainingAmount());

			Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

			String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

			LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();

			String shipAddress = eventData.getShipAddress() != null ? eventData.getShipAddress() : "";

			Integer transportation = eventData.getTransportation() == null ? 0
					: (int) Math.round(eventData.getTransportation());
			
			String formatedEventDate = eventMasterEntity.getEventStartDateTime() != null
					? eventMasterEntity.getEventStartDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
					: "";

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			String reportType = "Quotation";
			String reportLabel = "ESTIMATE";
			String header = "ESTIMATE PROPOSAL";
			if (isInvoice == 1) {
				reportType = "Invoice";
				reportLabel = "INVOICE";
				header = "TAX INVOICE";
			}

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 40, 20);

//			ImageData watermarkBgData = loadImageFromResource(
//					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());

			ImageData watermarkBgData = null;
			if(isInvoice == 1) {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource("/flipbook/pages/noda_exclusive_invoice_bg.png");
			}else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource("/flipbook/pages/noda_exclusive_quotation_bg.png");
			}
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color blueColor = new DeviceRgb(0, 27, 82);
			Color yellowColor = new DeviceRgb(225, 168, 59);
			Color lightYellowColor = new DeviceRgb(253, 250, 215);
			
			Table clientTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			clientTable.setWidth(UnitValue.createPercentValue(100));
			clientTable.setMarginTop(210f);

			Paragraph clientNamePara = new Paragraph().add(new Text("To,\n")).add(new Text(partyName))
					.setFont(boldFont).setFontSize(20f).setFontColor(blueColor).setMargin(0).setFixedLeading(20f);

			Cell clientNameCell = new Cell().add(clientNamePara).setPaddingLeft(10f).setPaddingTop(5f)
					.setPaddingBottom(5f).setHeight(80f).setBorder(new SolidBorder(blackColor, 1.5f))
					.setBorderRadius(new BorderRadius(10f)).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setBorder(Border.NO_BORDER);

			Paragraph clientDetailsPara = new Paragraph()
					.add(new Text("Event : " + eventMasterEntity.getEventType().getNameEnglish()))
					.add(new Text("\nVenue : " + eventData.getVenueName()))
					.add(new Text("\nDate : " + formatedEventDate))
					.setFont(boldFont).setFontSize(18f)
					.setFontColor(blueColor).setMargin(0).setFixedLeading(20f);

			Cell clientDetailsCell = new Cell().add(clientDetailsPara).setPaddingLeft(10f).setPaddingTop(5f)
					.setPaddingBottom(5f).setHeight(80f).setBorder(new SolidBorder(blackColor, 1.5f))
					.setBorderRadius(new BorderRadius(10f)).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setBorder(Border.NO_BORDER);

			clientTable.addCell(clientNameCell);
			clientTable.addCell(clientDetailsCell);

			document.add(clientTable);
			
			float[] columnWidth3 = { 5f, 30f, 10f, 10f, 10f, 10f, 10f };
			float[] columnWidth6 = { 5f, 30f, 10f, 10f, 10f, 20f };

			Div divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));
			divData1.setBorder(new SolidBorder(blackColor, 1f));
			divData1.setBackgroundColor(blueColor);
			divData1.setBorderTopLeftRadius(new BorderRadius(10f));
			divData1.setBorderTopRightRadius(new BorderRadius(10f));
			divData1.setMarginTop(10f);

			List<QuotationResponseDto> funData = new ArrayList<>();
			if (isInvoice == 1) {
				funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
			} else {
				funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
			}

			boolean hasExtraTax = funData.stream()
					.anyMatch(item -> item.getExtraTax() != null && item.getExtraTax().compareTo(BigDecimal.ZERO) > 0);
			boolean hasTaxRate = funData.stream()
					.anyMatch(item -> item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0);

			Table funTbl = null;
			if (isInvoice == 1) {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl.setWidth(UnitValue.createPercentValue(100));

			Paragraph data = new Paragraph()
					.add(new Text("No.").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE));
			Cell cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(40f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Function").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(60f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Person").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE));
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(60f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Extra Person").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE))
					.setFixedLeading(14);
			cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(60f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Rate").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE));

			if (isInvoice != 1 && !hasExtraTax) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(60f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			if (isInvoice != 1) {
				if (hasExtraTax) {
					data = new Paragraph().add(
							new Text("Extra Tax").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(60f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}

				if (hasTaxRate) {
					data = new Paragraph().add(
							new Text("Tax Rate").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE));
					cell = new Cell().add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(60f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
					funTbl.addCell(cell);
				}
			}

			data = new Paragraph()
					.add(new Text("Total Price").setFontSize(14).setFont(boldFont).setFontColor(ColorConstants.WHITE));

			if (isInvoice != 1 && !hasTaxRate) {
				cell = new Cell(1, 2);
			} else {
				cell = new Cell();
			}
			cell.add(data).setPadding(0).setBorder(Border.NO_BORDER).setHeight(60f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);
			funTbl.addCell(cell);

			divData1.add(funTbl);
			document.add(divData1);

			Table funTbl2 = null;
			if (isInvoice == 1) {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth6));
			} else {
				funTbl2 = new Table(UnitValue.createPercentArray(columnWidth3));
			}
			funTbl2.setWidth(UnitValue.createPercentValue(100));

			Integer i = 1;
			for (QuotationResponseDto fun : funData) {
				String funDate = fun.getFunctionDate() != null ? fun.getFunctionDate() : "";

				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();

				if (isInvoice != 1 && funDate.trim().length() != 0) {
					functionName = functionName.concat("\n( " + funDate + " )");
					System.out.println("date : " + funDate);
				}

				Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
				Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();

				Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();
				Integer extraPax = fun.getFunctionExtraPax() == null ? 0 : fun.getFunctionExtraPax();

//				if (Boolean.FALSE.equals(fun.getIs_event_function())) {
//
//					// Sr No
//					data = new Paragraph()
//							.add(new Text(i.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//					cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//					cell.setBorder(new SolidBorder(linecolor, 1f));
//					funTbl2.addCell(cell);
//
//					if(isInvoice == 1) {
//						cell = new Cell(1, 4);
//						data = new Paragraph()
//								.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//						cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//						cell.setBorder(new SolidBorder(linecolor, 1f));
//						funTbl2.addCell(cell);
//					}else {
//						cell = new Cell(1, 2);
//						data = new Paragraph()
//								.add(new Text(functionName).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//						cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//						cell.setBorder(new SolidBorder(linecolor, 1f));
//						funTbl2.addCell(cell);
//						
//						data = new Paragraph().add(
//								new Text(extraPax.toString()).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//						cell = new Cell(1, 2).add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//						cell.setBorder(new SolidBorder(linecolor, 1f));
//						funTbl2.addCell(cell);
//					}
//					
//					// Amount
//					data = new Paragraph()
//							.add(new Text("Rs. " + amount).setFontSize(10).setFont(basicFont).setFontColor(fontColor2));
//					if (isInvoice != 1 && !hasExtraTax) {
//						cell = new Cell(1, 2);
//					} else {
//						cell = new Cell();
//					}
//					cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
//					cell.setBorder(new SolidBorder(linecolor, 1f));
//					funTbl2.addCell(cell);
//
//					i++;
//					continue;
//				}

				data = new Paragraph()
						.add(new Text(i.toString()).setFontSize(14).setFont(boldFont).setFontColor(blueColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(lightYellowColor);
				cell.setBorder(new SolidBorder(yellowColor, 1.5f));
				funTbl2.addCell(cell);

				data = new Paragraph()
						.add(new Text(functionName).setFontSize(14).setFont(boldFont).setFontColor(blueColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(yellowColor, 1.5f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(functionPax.toString()).setFontSize(14).setFont(boldFont).setFontColor(blueColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(yellowColor, 1.5f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text(extraPax.toString()).setFontSize(14).setFont(boldFont).setFontColor(blueColor));
				cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(yellowColor, 1.5f));
				funTbl2.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + rate.toString()).setFontSize(14).setFont(boldFont).setFontColor(blueColor));

				if (isInvoice != 1 && !hasExtraTax) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(yellowColor, 1.5f));
				funTbl2.addCell(cell);

				if (isInvoice != 1) {
					if (hasExtraTax) {
						data = new Paragraph()
								.add(new Text(fun.getExtraTax() != null ? fun.getExtraTax().toString() : "")
										.setFontSize(14).setFont(boldFont).setFontColor(blueColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(new SolidBorder(yellowColor, 1.5f));
						funTbl2.addCell(cell);
					}

					if (hasTaxRate) {
						data = new Paragraph().add(new Text(fun.getTaxRate() != null ? fun.getTaxRate().toString() : "")
								.setFontSize(14).setFont(boldFont).setFontColor(blueColor));
						cell = new Cell().add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
						cell.setBorder(new SolidBorder(yellowColor, 1.5f));
						funTbl2.addCell(cell);
					}
				}

				data = new Paragraph().add(new Text("Rs. " + amount.toString()).setFontSize(14).setFont(boldFont)
						.setFontColor(blueColor));

				if (isInvoice != 1 && !hasTaxRate) {
					cell = new Cell(1, 2);
				} else {
					cell = new Cell();
				}

				cell.add(data).setPadding(0).setTextAlignment(TextAlignment.CENTER);
				cell.setBorder(new SolidBorder(yellowColor, 1.5f));
				funTbl2.addCell(cell);

				i++;
			}
			
			divData1 = new Div();
			divData1.setWidth(UnitValue.createPercentValue(100));

			divData1.add(funTbl2);
			document.add(divData1);

			float[] columnWidth5 = { 77.5f, 23.5f };
			Table billTbl = new Table(UnitValue.createPercentArray(columnWidth5));
			billTbl.setWidth(UnitValue.createPercentValue(100f));
//			billTbl.setBorder(new SolidBorder(yellowColor, 1.5f));
			billTbl.setMarginTop(5f);

			// Subtotal
			data = new Paragraph()
					.add(new Text("Subtotal: ").setFont(boldFont).setFontSize(12)
							.setFontColor(blueColor))
					.setFixedLeading(12)
					.setTextAlignment(TextAlignment.RIGHT)
					.setPaddingLeft(10f);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			billTbl.addCell(cell);

			data = new Paragraph()
					.add(new Text("Rs. " + subTotle).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			billTbl.addCell(cell);

			// Transportation
			if (transportation != null && transportation != 0) {
				data = new Paragraph()
						.add(new Text("Transportation: ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);

				cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
				billTbl.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + transportation).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);

				cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
				billTbl.addCell(cell);
			}

			// Discount
			data = new Paragraph()
					.add(new Text("Discount: ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + discount).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			// Amount After Discount
			double amountAfterDiscount = subTotle - discount;

			data = new Paragraph().add(
					new Text("Amount After Discount: ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph().add(
					new Text("Rs. " + amountAfterDiscount).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));

			if (discount > 0) {
				billTbl.addCell(cell);
			}

			// CASH PAYMENT
			data = new Paragraph()
					.add(new Text("Cash Amt : ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (cashPay > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cashPay).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (cashPay > 0) {
				billTbl.addCell(cell);
			}

			// CHEQUE PAYMENT
			data = new Paragraph()
					.add(new Text("Cheque Amt : ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (cheqPay > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cheqPay).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (cheqPay > 0) {
				billTbl.addCell(cell);
			}

			// SGST
			data = new Paragraph()
					.add(new Text("SGST (" + sgst + "): ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (sgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + sgstAmnt).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (sgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// CGST
			data = new Paragraph()
					.add(new Text("CGST (" + cgst + "): ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (cgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + cgstAmnt).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (cgstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// IGST
			data = new Paragraph()
					.add(new Text("IGST (" + igst + "): ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (igstAmnt > 0) {
				billTbl.addCell(cell);
			}

			data = new Paragraph()
					.add(new Text("Rs. " + igstAmnt).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
					.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);
			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			if (igstAmnt > 0) {
				billTbl.addCell(cell);
			}

			// ==========================================
			// Cheque Amt (incl. GST)
			// ==========================================

			double chequeAmtInclGst = cheqPay + cgstAmnt + sgstAmnt + igstAmnt;

			if (cheqPay > 0) {
				data = new Paragraph().add(
						new Text("Cheque Amt(incl. GST): ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);

				cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
				billTbl.addCell(cell);

				data = new Paragraph().add(
						new Text("Rs. " + chequeAmtInclGst).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);

				cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
				billTbl.addCell(cell);

			}

			if (isAdvance == 1) {
				data = new Paragraph()
						.add(new Text("Advance Payment: ").setFont(boldFont).setFontSize(12).setFontColor(blueColor))
						.setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(10f).setFixedLeading(12);
				cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
				if (advancePayment > 0) {
					billTbl.addCell(cell);
				}

				data = new Paragraph().add(
						new Text("Rs. " + advancePayment).setFont(basicFont).setFontSize(12).setFontColor(blueColor))
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f).setFixedLeading(12);
				cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
				if (advancePayment > 0) {
					billTbl.addCell(cell);
				}
			}

			// ==========================================
			// Grand Total
			// ==========================================

			// grandTotal = (int) Math.round(chequeAmtInclGst + cashPay);

			data = new Paragraph().add(new Text("Grand Total: ").setFont(boldFont).setFontSize(12))
					.setTextAlignment(TextAlignment.RIGHT)
					.setPaddingLeft(10f)
					.setFontColor(blueColor)
					.setFixedLeading(12);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			billTbl.addCell(cell);

			data = new Paragraph().add(new Text("Rs. " + grandTotal).setFont(boldFont).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT)
					.setPaddingLeft(10f)
					.setFontColor(blueColor)
					.setFixedLeading(12);

			cell = new Cell().add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			billTbl.addCell(cell);

			// In Words
			data = new Paragraph().add(new Text("In Words : ").setFont(boldFont).setFontSize(12))
					.add(new Text(NumberToWordConverter.convert(grandTotal) + " Only.").setFont(basicFont)
							.setFontSize(10))
					.setTextAlignment(TextAlignment.LEFT)
					.setPaddingLeft(10f)
					.setFontColor(blueColor)
					.setFixedLeading(12);

			cell = new Cell(1, 2).add(data).setBorder(new SolidBorder(yellowColor, 1.5f));
			billTbl.addCell(cell);
			
			billTbl.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER).setHeight(50f));
			
			cell = new Cell(1, 2)
					.add(new Paragraph("For NODA CATERERS").setFixedLeading(16)
							.setFont(boldFont)
							.setFontSize(16)
							.setFontColor(blueColor))
					.setBorder(Border.NO_BORDER);
			billTbl.addCell(cell);
			
			cell = new Cell(1, 2)
					.add(new Paragraph("Noda Caterers Team").setFixedLeading(16)
							.setFont(boldFont)
							.setFontSize(16)
							.setFontColor(blueColor))
					.setBorder(Border.NO_BORDER);
			billTbl.addCell(cell);

			document.add(billTbl);
			 
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report Generation Failed");
		}
	}
	
	@Override
	public String generateInvoiceType11(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
	        Integer isAdvance, Integer isInvoice, Integer isWithPrice) {
	    try {
	        PdfFont basicFont = null;
	        menuPreparationServiceImpl.loadLicense();
	        // Hindi
	        System.out.println("Loading Hindi font...");
	        PdfFont basicFontHindi = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	        System.out.println("Hindi font loaded successfully");

	        // Gujarati
	        System.out.println("Loading Gujarati font...");
	        PdfFont basicFontGujarati = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	        System.out.println("Gujarati font loaded successfully");

	        // English
	        System.out.println("Loading English font...");
	        basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	        System.out.println("English font loaded successfully");

	        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

	        // Pick the item-row font by language, same convention as getQuotationReport1
	        PdfFont itemFont = basicFont;
	        if (lang == 1) {
	            itemFont = basicFontHindi;
	        } else if (lang == 2) {
	            itemFont = basicFontGujarati;
	        }

	        EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
	                .orElseThrow(() -> new RuntimeException("Event Not found with id : " + eventId));

	        QuotationResponseDto eventData = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0,
	                isInvoice, false);

	        String companyName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
	                : eventData.getCompanyName();

	        String countryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
	                : eventData.getCountryCode();

	        String officeNo = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
	                : eventData.getOfficeNo();

	        String companyEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
	                : eventData.getCompanyEmail();

	        String companyAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty()
	                ? ""
	                : eventData.getCompanyAddress();

	        String companyLogo = eventData.getCompanyLogo() == null || eventData.getCompanyLogo().isEmpty() ? ""
	                : eventData.getCompanyLogo();

	        String accountHolderName = eventData.getAccountHolderName() == null
	                || eventData.getAccountHolderName().isEmpty() ? "" : eventData.getAccountHolderName();

	        String accountNo = eventData.getAccountNo() == null || eventData.getAccountNo().isEmpty() ? ""
	                : eventData.getAccountNo();

	        String bankName = eventData.getBankName() == null || eventData.getBankName().isEmpty() ? ""
	                : eventData.getBankName();

	        String ifscCode = eventData.getIfscCode() == null || eventData.getIfscCode().isEmpty() ? ""
	                : eventData.getIfscCode();

	        String upiId = eventData.getUpiId() == null || eventData.getUpiId().isEmpty() ? "" : eventData.getUpiId();

	        String branchName = eventData.getBranchName() == null || eventData.getBranchName().isEmpty() ? ""
	                : eventData.getBranchName();

	        String qrCodePath = eventData.getQrCodePath() != null ? eventData.getQrCodePath() : null;

	        String notes = eventData.getNotes() != null ? eventData.getNotes() : "";

	        String eventNo = eventData.getEventNo() == null || eventData.getEventNo().isEmpty() ? ""
	                : eventData.getEventNo();

	        String partyName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
	                : eventData.getPartyName();

	        String partyAddress = eventData.getPartyAddress() == null || eventData.getPartyAddress().isEmpty() ? ""
	                : eventData.getPartyAddress();

	        String partyEmail = eventData.getPartyEmail() == null || eventData.getPartyEmail().isEmpty() ? ""
	                : eventData.getPartyEmail();

	        String partyMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
	                : eventData.getPartyMobile();

	        String partyGst = eventData.getPartyGst() == null || eventData.getPartyGst().isEmpty() ? ""
	                : eventData.getPartyGst();

	        String quotationDueDate = eventData.getQuotationDueDate() == null
	                || eventData.getQuotationDueDate().isEmpty() ? "" : eventData.getQuotationDueDate();

	        String quotationCode = eventData.getQuotationCode() == null || eventData.getQuotationCode().isEmpty() ? ""
	                : eventData.getQuotationCode();

	        String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
	                : eventData.getEventDate();

	        String cgst = eventData.getCgst() == null || eventData.getCgst().isEmpty() ? "" : eventData.getCgst();

	        String sgst = eventData.getSgst() == null || eventData.getSgst().isEmpty() ? "" : eventData.getSgst();

	        String igst = eventData.getIgst() == null || eventData.getIgst().isEmpty() ? "" : eventData.getIgst();

	        Long quotationId = eventData.getQuotationId() == null ? 0L : eventData.getQuotationId();
	        Long partyId = eventData.getPartyId() == null ? 0L : eventData.getPartyId();

	        Integer subTotle = eventData.getSubTotle() == null ? 0 : (int) Math.round(eventData.getSubTotle());

	        Integer transportation = eventData.getTransportation() == null ? 0
	                : (int) Math.round(eventData.getTransportation());

	        Integer cashPay = eventData.getCashPayment() == null ? 0 : (int) Math.round(eventData.getCashPayment());

	        Integer cheqPay = eventData.getChequePayment() == null ? 0 : (int) Math.round(eventData.getChequePayment());

	        Integer cgstAmnt = eventData.getCgstAmnt() == null ? 0 : (int) Math.round(eventData.getCgstAmnt());

	        Integer sgstAmnt = eventData.getSgstAmnt() == null ? 0 : (int) Math.round(eventData.getSgstAmnt());

	        Integer igstAmnt = eventData.getIgstAmnt() == null ? 0 : (int) Math.round(eventData.getIgstAmnt());

	        Integer discount = eventData.getDiscount() == null ? 0 : (int) Math.round(eventData.getDiscount());

	        Integer advancePayment = eventData.getAdvancePayment() == null ? 0
	                : (int) Math.round(eventData.getAdvancePayment());

	        Integer remainingAmount = eventData.getRemainingAmount() == null ? 0
	                : (int) Math.round(eventData.getRemainingAmount());

	        Integer grandTotal = eventData.getGrandTotal() == null ? 0 : (int) Math.round(eventData.getGrandTotal());

	        String cmpGstNumber = eventData.getCmpGstNumber() != null ? eventData.getCmpGstNumber() : "";

	        String eventVenue = eventData.getEventVenue() != null ? eventData.getEventVenue() : "";
	        
	        LocalDateTime generatedDateTime = eventData.getGeneratedDateTime() != null
					? eventData.getGeneratedDateTime()
					: LocalDateTime.now();
	        
	        String formatedGenerateDate = generatedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	        
	        Color blackColor = new DeviceRgb(0, 0, 0);
	        Color redColor = new DeviceRgb(200, 0, 0);

	        Date now = new Date();
	        String rootPath = re.getSession().getServletContext().getRealPath("/");
	        String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
	        File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

	        if (!outputPath.exists()) {
	            if (outputPath.mkdirs()) {
	                System.out.println("Directory Created!!!");
	            } else {
	                System.out.println("Error!");
	            }
	        }

	        String reportType = "Quotation";
	        String reportLabel = "ESTIMATE";
	        String header = "ESTIMATE PROPOSAL";
	        if (isInvoice == 1) {
	            reportType = "Invoice";
	            reportLabel = "INVOICE";
	            header = "TAX INVOICE";
	        }

	        File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
	                formatDate(eventMasterEntity.getEventStartDateTime()), reportType) + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument);
	        document.setMargins(20, 20, 20, 20);

	        // Pull the actual line items exactly as getQuotationReport1 does
	        List<QuotationResponseDto> funData;
	        if (isInvoice == 1) {
	            funData = eventFunctionQuotationServiceImpl.getFunctionDataInvoice(quotationId);
	        } else {
	            funData = eventFunctionQuotationServiceImpl.getFunctionData(quotationId);
	        }

	        // ===================== BUILD INVOICE TABLE =====================
	        Table invoiceTable = new Table(UnitValue.createPercentArray(new float[] { 55f, 25f, 20f }));
	        invoiceTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
	        invoiceTable.setWidth(UnitValue.createPercentValue(100f));

	        // --- Title row ---
	        invoiceTable.addCell(fullWidthCell(header, boldFont, 16f, TextAlignment.CENTER,
	                new SolidBorder(ColorConstants.BLACK, 1f)));

	        // --- Company block (left, rowspan 2) | blank middle | Dated (right, rowspan 2) ---
	        Cell companyCell = new Cell(2, 1)
	                .add(new Paragraph(companyName).setFont(boldFont).setFontSize(14f))
	                .add(new Paragraph(companyAddress).setFont(basicFont).setFontSize(13f).setFontColor(blackColor))
	                .add(new Paragraph("GST NO.: " + cmpGstNumber).setFont(basicFont).setFontSize(12f).setFontColor(blackColor))
	                .setBorder(new SolidBorder(blackColor, 0.5f))
	                .setVerticalAlignment(VerticalAlignment.TOP)
	                .setPadding(4);
	        invoiceTable.addCell(companyCell);

	        // No dedicated "venue" field exists on QuotationResponseDto — left blank.
	        // Wire in the correct getter here if one exists on eventMasterEntity/eventData.
	        invoiceTable.addCell(new Cell(2, 1).add(new Paragraph(eventVenue))
	                .setBorder(new SolidBorder(blackColor, 0.5f)).setPadding(4));

	        Cell datedCell = new Cell(2, 1)
	                .add(new Paragraph("Dated\n" + formatedGenerateDate).setFont(boldFont).setFontSize(13f))
	                .setBorder(new SolidBorder(blackColor, 0.5f))
	                .setVerticalAlignment(VerticalAlignment.TOP)
	                .setPadding(4);
	        invoiceTable.addCell(datedCell);

	        // --- TO / Function date row ---
	        Cell toCell = new Cell()
	                .add(new Paragraph("TO,").setFont(boldFont).setFontSize(13f))
	                .add(new Paragraph(partyName).setFont(boldFont).setFontSize(13f))
	                .setBorder(new SolidBorder(blackColor, 0.5f)).setPadding(4);
	        invoiceTable.addCell(toCell);

	        Cell functionDateCell = new Cell()
	                .add(new Paragraph("FUNCTION DATE").setFont(boldFont).setFontSize(13f))
	                .add(new Paragraph(eventDate).setFont(basicFont).setFontSize(13f))
	                .setBorder(new SolidBorder(blackColor, 0.5f)).setPadding(4);
	        invoiceTable.addCell(functionDateCell);
	        
			functionDateCell = new Cell().setBorder(new SolidBorder(blackColor, 0.5f)).setPadding(4);
	        invoiceTable.addCell(functionDateCell);

	        addSpacerRow(invoiceTable, blackColor);

	        
	        boolean withPrice = isWithPrice != null && isWithPrice == 1;
	        
	        for (QuotationResponseDto fun : funData) {
	            String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
	                    : fun.getFunctionName();

	            Integer functionPax = fun.getFunctionPax() == null ? 0 : fun.getFunctionPax();
	            Integer rate = fun.getRate() == null ? 0 : (int) fun.getRate().doubleValue();
	            Integer amount = fun.getAmount() == null ? 0 : (int) fun.getAmount().doubleValue();

	            String detail = functionPax + " X Rs." + rate + "/-";

	            addLineItemRow(invoiceTable, itemFont, boldFont, blackColor, withPrice, functionName, detail, amount);
	        }

	        // --- Subtotal ---
	        if(withPrice) {
		        addSubtotalRow(invoiceTable, basicFont, boldFont, blackColor, null, subTotle, true);
	
		        // --- Transportation (only if present) ---
		        if (transportation != null && transportation != 0) {
		            addSubtotalRow(invoiceTable, basicFont, boldFont, blackColor, "TRANSPORTATION", transportation, false);
		        }
	
		        // --- Discount (only if present) ---
		        if (discount != null && discount > 0) {
		            addSubtotalRow(invoiceTable, basicFont, boldFont, blackColor, "DISCOUNT", discount, false);
		        }
	
		        // --- GST breakdown (SGST/CGST or IGST, whichever apply) ---
		        if (sgstAmnt != null && sgstAmnt > 0) {
		            addSubtotalRow(invoiceTable, basicFont, boldFont, blackColor, "SGST (" + sgst + "%)", sgstAmnt, false);
		        }
		        if (cgstAmnt != null && cgstAmnt > 0) {
		            addSubtotalRow(invoiceTable, basicFont, boldFont, blackColor, "CGST (" + cgst + "%)", cgstAmnt, false);
		        }
		        if (igstAmnt != null && igstAmnt > 0) {
		            addSubtotalRow(invoiceTable, basicFont, boldFont, blackColor, "IGST (" + igst + "%)", igstAmnt, false);
		        }
	
		        // --- Advance ---
		        if (isAdvance == 1 && advancePayment != null && advancePayment > 0) {
		            addSubtotalRow(invoiceTable, basicFont, boldFont, blackColor, "ADVANCE RECEIVED", advancePayment, true);
		        }
	
		        // --- Balance (red, bold, like image) ---
		        addBalanceRow(invoiceTable, basicFont, boldFont, blackColor, redColor,
		                "BALANCE " + formatAmount(remainingAmount) + "/-", "BALANCE", remainingAmount, true);
	
		        addSpacerRow(invoiceTable, blackColor);
	
		        // --- Cash / Cheque breakdown footer ---
		        if (cashPay != null && cashPay > 0) {
		            addBalanceRow(invoiceTable, basicFont, boldFont, blackColor, redColor,
		                    "CASH " + formatAmount(cashPay) + "/-", null, null, false);
		        }
	
		        if (cheqPay != null && cheqPay > 0) {
		            addBalanceRow(invoiceTable, basicFont, boldFont, blackColor, redColor,
		                    "CHEQUE " + formatAmount(cheqPay) + "/-", null, null, false);
		        }
	
		        addSpacerRow(invoiceTable, blackColor);
	        }

	        // --- Signatory ---
	        invoiceTable.addCell(new Cell().add(new Paragraph("").setFont(boldFont).setFontSize(13f))
	                .setBorder(new SolidBorder(blackColor, 0.5f)).setPadding(4));
	        Cell signCell = new Cell(1, 2)
	                .add(new Paragraph("Authorised Signatory").simulateBold().setFont(basicFont).setFontSize(13f))
	                .setTextAlignment(TextAlignment.LEFT)
	                .setVerticalAlignment(VerticalAlignment.TOP)
	                .setBorder(new SolidBorder(blackColor, 0.5f))
	                .setPadding(4)
	                .setHeight(50f);
	        invoiceTable.addCell(signCell);

	        document.add(invoiceTable);

//	        // Notes, if present
//	        if (notes != null && !notes.trim().isEmpty()) {
//	            Paragraph notesPara = new Paragraph("NOTES: " + notes).setFont(basicFont).setFontSize(9f)
//	                    .setFontColor(blackColor).setMarginTop(8f);
//	            document.add(notesPara);
//	        }
	        // ===================== END TABLE =====================

	        document.close();

	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
	                + getReportName(eventMasterEntity.getParty().getNameEnglish(),
	                        formatDate(eventMasterEntity.getEventStartDateTime()), reportType)
	                + ".pdf";

	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Report Generation Failed");
	    }
	}

	// ===================== HELPER METHODS =====================

	private void addInvoiceCell(Table table, Paragraph p, Integer colspan, Integer rowspan, TextAlignment textAlignment,
	        VerticalAlignment verticalAlignment) {
	    Cell cell = new Cell(colspan, rowspan).add(p).setTextAlignment(textAlignment)
	            .setVerticalAlignment(verticalAlignment);

	    table.addCell(cell);
	}

	private Paragraph addInvoiceParagraph(PdfFont font, float fontSize, Text... texts) {
	    Paragraph paragraph = new Paragraph().setFont(font).setFontSize(fontSize);

	    for (Text text : texts) {
	        paragraph.add(text);
	    }

	    return paragraph;
	}

	private Cell fullWidthCell(String text, PdfFont boldFont, float fontSize, TextAlignment align, Border border) {
	    Paragraph p = new Paragraph(text).setFont(boldFont).setFontSize(fontSize);
	    return new Cell(1, 3).add(p)
	            .setTextAlignment(align)
	            .setVerticalAlignment(VerticalAlignment.MIDDLE)
	            .setBorder(border)
	            .setPadding(4);
	}

	private void addThreeColRow(Table table, PdfFont font, PdfFont boldFont, Color linecolor,
	        String colA, boolean boldA, TextAlignment alignA, Color colorA,
	        String colB, boolean boldB, TextAlignment alignB, Color colorB,
	        String colC, boolean boldC, TextAlignment alignC, Color colorC) {

	    table.addCell(buildCell(colA, font, boldFont, boldA, alignA, colorA, linecolor));
	    table.addCell(buildCell(colB, font, boldFont, boldB, alignB, colorB, linecolor));
	    table.addCell(buildCell(colC, font, boldFont, boldC, alignC, colorC, linecolor));
	}

//	private Cell buildCell(String text, PdfFont font, PdfFont boldFont, boolean bold,
//	        TextAlignment align, Color color, Color linecolor) {
//	    Paragraph p = new Paragraph(text == null ? "" : text)
//	            .setFont(bold ? boldFont : font)
//	            .setFontSize(13f);
//	    if (color != null) {
//	        p.setFontColor(color);
//	    }
//	    return new Cell().add(p)
//	            .setTextAlignment(align)
//	            .setVerticalAlignment(VerticalAlignment.MIDDLE)
//	            .setBorder(new SolidBorder(linecolor, 0.5f))
//	            .setPadding(3);
//	}
	
	private Cell buildCell(String text, PdfFont font, PdfFont boldFont, boolean bold,
	        TextAlignment align, Color color, Color linecolor, int colspan) {
	    Paragraph p = new Paragraph(text == null ? "" : text)
	            .setFont(bold ? boldFont : font)
	            .setFontSize(13f);
	    if (color != null) {
	        p.setFontColor(color);
	    }
	    return new Cell(1, colspan).add(p)
	            .setTextAlignment(align)
	            .setVerticalAlignment(VerticalAlignment.MIDDLE)
	            .setBorder(new SolidBorder(linecolor, 0.5f))
	            .setPadding(3);
	}

	// keep the old 3-arg-colspan-less overload so existing calls still compile
	private Cell buildCell(String text, PdfFont font, PdfFont boldFont, boolean bold,
	        TextAlignment align, Color color, Color linecolor) {
	    return buildCell(text, font, boldFont, bold, align, color, linecolor, 1);
	}

	private void addSpacerRow(Table table, Color linecolor) {
	    for (int i = 0; i < 3; i++) {
	        table.addCell(new Cell().add(new Paragraph(" ").setFontSize(4f))
	                .setBorder(new SolidBorder(linecolor, 0.5f))
	                .setHeight(15f));
	    }
	}

	private void addSubtotalRow(Table table, PdfFont font, PdfFont boldFont, Color linecolor, String labelB,
	        Number amount, boolean boldAmount) {
	    addThreeColRow(table, font, boldFont, linecolor,
	            "", false, TextAlignment.LEFT, ColorConstants.BLACK,
	            labelB == null ? "" : labelB, true, TextAlignment.LEFT, ColorConstants.BLACK,
	            formatAmount(amount), boldAmount, TextAlignment.RIGHT, ColorConstants.BLACK);
	}

	private void addBalanceRow(Table table, PdfFont font, PdfFont boldFont, Color linecolor, Color redColor,
	        String redLabelA, String labelB, Number amountC, boolean showAmount) {
	    addThreeColRow(table, font, boldFont, linecolor,
	            redLabelA == null ? "" : redLabelA, true, TextAlignment.LEFT, redColor,
	            labelB == null ? "" : labelB, true, TextAlignment.LEFT, ColorConstants.BLACK,
	            showAmount ? formatAmount(amountC) : "", true, TextAlignment.RIGHT, ColorConstants.BLACK);
	}

	private void addLineItemRow(Table table, PdfFont font, PdfFont boldFont, Color linecolor,
	        boolean withPrice, String functionName, String detail, Number amount) {

	    table.addCell(buildCell(functionName, font, boldFont, false, TextAlignment.LEFT, ColorConstants.BLACK, linecolor));

	    if (withPrice) {
	        table.addCell(buildCell(detail, font, boldFont, false, TextAlignment.LEFT, ColorConstants.BLACK, linecolor));
	        table.addCell(buildCell(formatAmount(amount), font, boldFont, false, TextAlignment.RIGHT, ColorConstants.BLACK, linecolor));
	    } else {
	        // merge columns 2 and 3 into one wider detail column
	        table.addCell(buildCell(detail, font, boldFont, false, TextAlignment.LEFT, ColorConstants.BLACK, linecolor, 2));
	    }
	}
	
//	private String formatAmount(Number amount) {
//	    return String.format("%,.2f", amount == null ? 0.0 : amount.doubleValue());
//	}
}