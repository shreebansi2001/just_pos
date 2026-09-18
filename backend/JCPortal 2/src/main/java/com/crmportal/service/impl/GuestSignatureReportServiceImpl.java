package com.crmportal.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.GuestSignatureRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.GuestSignatureReportResponseDto;
import com.crmportal.response.dto.GuestSignatureResponseDto;
import com.crmportal.service.GuestSignatureReportService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
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
public class GuestSignatureReportServiceImpl implements GuestSignatureReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventFunctionMenuAllocationRepository eventFunctionMenuAllocationRepository;

	@Autowired
	Environment environment;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;
	
	@Autowired
	GuestSignatureRepository guestSignatureRepository;

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}

	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + fileSafe(date.replace("/", "_") + " (" + type.toUpperCase() + ")");
	}

	public String formatDate(LocalDateTime dateTime) {
		return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
	}

	private Table getCompanyTable(CompanyDetailsResponseDto cmpDto, PdfFont basicFont, PdfFont boldFont, Integer lang,
			Color mainColor) throws IOException {
		String phone, email;
		if (lang == 1) {
			phone = "मोबाइल नंबर";
			email = "ईमेल";
		} else if (lang == 2) {
			phone = "મોબાઇલ નંબર";
			email = "ઈમૈલ";
		} else {
			phone = "Mobile No.";
			email = "Email";
		}

		Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 2f, 73f }), false);
		companyTable.setWidth(UnitValue.createPercentValue(100f));
		ImageData logoData = menuPreparationServiceImpl
				.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//				.loadImageFromResource("/flipbook/pages/logo.png");
		Image logo = new Image(logoData);

		// Resize & align
		logo.setWidth(100f);
		logo.setAutoScale(false);
		logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

		Cell cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f)
				.setVerticalAlignment(VerticalAlignment.MIDDLE);
		companyTable.addCell(cell);

		cell = new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14)
				.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).setFixedLeading(14))
				.setBorder(Border.NO_BORDER);
		companyTable.addCell(cell);

		cell = new Cell().add(new Paragraph()
				.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 15).setFontColor(mainColor)
						.simulateBold())
				.add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo() : "")
						.setFont(basicFont).setFontSize(14).setFontColor(mainColor))
				.setFixedLeading(lang == 0 ? 14 : 16)).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
		companyTable.addCell(cell);

		cell = new Cell()
				.add(new Paragraph()
						.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 15).simulateBold()
								.setFontColor(mainColor))
						.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
								.setFont(basicFont).setFontSize(14).setFontColor(mainColor))
						.setFixedLeading(lang == 0 ? 14 : 16))
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
		companyTable.addCell(cell);

		cell = new Cell(1, 3).add(new Paragraph("")).setBorder(Border.NO_BORDER)
				.setBorderBottom(new SolidBorder(mainColor, 0.5f));
		companyTable.addCell(cell);

		return companyTable;
	}

	private Cell addCell(String label, PdfFont font, Color fontColor, Color borderColor, Color bgColor, Integer colSpan,
			Integer rowSpan, TextAlignment textAlignment, VerticalAlignment verticalAlignment, float fontSize,
			float padding, float borderThickness) {
		return new Cell(colSpan, rowSpan)
				.add(new Paragraph(label).setFont(font).setFontSize(fontSize).setFontColor(fontColor)
						.setFixedLeading(fontSize))
				.setPadding(padding).setBorder(borderThickness == 0 ? Border.NO_BORDER : new SolidBorder(borderColor, borderThickness))
				.setVerticalAlignment(verticalAlignment).setTextAlignment(textAlignment).setBackgroundColor(bgColor);
	}
	
	private void addFunctionBlock(EventFunctionMasterEntity function, Table table, PdfFont basicFont, Color blackColor, Color labelColor, Color whiteColor,
			Integer lang) {
		String fnName = "";
		Integer pax = function.getPax();
		if(lang == 1) {
			fnName = function.getFunction() != null ? function.getFunction().getNameHindi() : "";
		}else if(lang == 2) {
			fnName = function.getFunction() != null ? function.getFunction().getNameGujarati() : "";
		}else {
			fnName = function.getFunction() != null ? function.getFunction().getNameEnglish() : "";
		}
		
		Cell cell = addCell(fnName.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 4, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 5f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("ORDER", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell(pax.toString(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("EXTRA", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("EXTRA", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("TOTAL", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
	}

	private void addFunctionBlockForReport1(GuestSignatureReportResponseDto sign, Table table, PdfFont basicFont, Color blackColor, Color labelColor, Color whiteColor,
			Integer lang) {
		Integer pax = sign.getPersons() != null ? sign.getPersons() : 0;
		String fnName = sign.getFunctionName() != null ? sign.getFunctionName() : "";
		String particulars = sign.getParticulars() != null ? sign.getParticulars() : "";
		Integer extra = sign.getExtra() != null ? sign.getExtra() : 0;
		
		Cell cell = addCell(fnName.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell(particulars, basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell(pax.toString(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell(extra.toString(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
		
		cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 10f, 2f, 0.5f);
		table.addCell(cell);
	}
	
	@Override
	public String generateGuestSignatureReport(Long eventId, Long userid, Integer lang, Integer isCompanyDetails,
			HttpServletRequest re) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String phone, eDate, eVenue, agencyName, email, funLabel, menuItemLabel, dateTimeLabel, qtyLabel,
					priceLabel, totalPriceLabel, notesLabel, grandTotalLabel, personLabel, eventLabel, printedLabel,
					referenceLabel, managerNameLabel, nameLabel, particularLabel, signLabel, extraItemLabel, checkOutTimeLabel,
					managerSignLabel, guestSignLabel, guestNameLabel;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की तिथि";
				eVenue = "स्थल";
				agencyName = "एजेंसी का नाम";
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				dateTimeLabel = "कार्यक्रमका समय";
				qtyLabel = "मात्रा";
				notesLabel = "टिप्पणी";
				priceLabel = "कीमत";
				totalPriceLabel = "कुल कीमत";
				grandTotalLabel = "ग्रांड टोटल";
				personLabel = "व्यक्ति";
				eventLabel = "कार्यक्रम";
				printedLabel = "प्रिंट का समय";
				managerNameLabel = "मेनेजर का नाम";
				referenceLabel = "संदर्भ";
				nameLabel = "नाम";
				particularLabel = "विवरण";
				signLabel = "अतिथि के हस्ताक्षर";
				extraItemLabel = "अतिरिक्त वस्तुएँ / सेवाएँ";
				checkOutTimeLabel = "प्रस्थान समय";
				managerSignLabel = "मैनेजर हस्ताक्षर";
				guestSignLabel = "अतिथि हस्ताक्षर";
				guestNameLabel = "अतिथि का नाम";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				dateTimeLabel = "કાર્યક્રમનો સમય";
				qtyLabel = "સંખ્યા";
				notesLabel = "નોંધ";
				priceLabel = "કિંમત";
				totalPriceLabel = "કુલ કિંમત";
				grandTotalLabel = "ગ્રાન્ડ ટોટલ";
				personLabel = "વ્યક્તિ";
				eventLabel = "કાર્યક્રમ";
				printedLabel = "પ્રિન્ટ નો સમય";
				managerNameLabel = "મેનેજર નું નામ";
				referenceLabel = "સંદર્ભ";
				nameLabel = "નામ";
				particularLabel = "વિગત";
				signLabel = "મહેમાનની સહી";
				extraItemLabel = "વધારાની વસ્તુઓ / સેવાઓ";
				checkOutTimeLabel = "પ્રસ્થાન સમય";
				managerSignLabel = "મેનેજર હસ્તાક્ષર";
				guestSignLabel = "મહેમાન હસ્તાક્ષર";
				guestNameLabel = "મહેમાનનું નામ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Event Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				dateTimeLabel = "Event Time";
				qtyLabel = "Qnt";
				notesLabel = "Remarks";
				priceLabel = "Price";
				totalPriceLabel = "Total Price";
				grandTotalLabel = "Grand Total";
				personLabel = "Person";
				eventLabel = "Event";
				printedLabel = "Printed On";
				managerNameLabel = "Manager Name";
				referenceLabel = "Reference";
				nameLabel = "Name";
				particularLabel = "Particulars";
				signLabel = "Guest Signature";
				extraItemLabel = "Extra Items / Services";
				checkOutTimeLabel = "Check out time";
				managerSignLabel = "Manager Sign";
				guestSignLabel = "Guest Sign";
				guestNameLabel = "Guest Name";
			}
			boldFont = PdfFontFactory.createFont("/fonts/arial-bold.ttf");
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			List<EventFunctionMasterEntity> eventFunctions = eventFunctionMasterRepository
					.findAllByEventIdAndIsDeleteFalseOrderByFunctionStartDateTimeAscSortorderAsc(eventId);

			if (eventFunctions == null || eventFunctions.isEmpty()) {
				return "Event functions not found.";
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			File pdfFile = new File(outputPath + "/" + getReportName(fileName,
					formatDate(eventMasterEntity.getEventStartDateTime()), "Guest Signature Report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4, false);
			document.setMargins(20, 20, 20, 20);

			Color labelColor = new DeviceRgb(115, 99, 67);
			Color labelBgColor = new DeviceRgb(242, 235, 223);
			Color whiteColor = new DeviceRgb(255, 255, 255);
			Color blackColor = new DeviceRgb(0, 0, 0);

			if (isCompanyDetails == 1) {
				Table companyTable = getCompanyTable(cmpDto, basicFont, boldFont, lang, blackColor);
				document.add(companyTable);
			}

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:ss a");
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");

			String event = "";
			String partyName = "";
			String phoneNumber = eventMasterEntity.getParty().getMobileno();
			String foodNotes = "";
			String venue = "";
			String reference = eventMasterEntity.getReference() != null ? eventMasterEntity.getReference() : "";
			String managerName = eventMasterEntity.getManager() != null
					? eventMasterEntity.getManager().getFirstName() + " " + eventMasterEntity.getManager().getLastName()
					: "";
			if (lang == 1) {
				venue = eventMasterEntity.getVenue() != null ? eventMasterEntity.getVenue().getNameHindi() : "";
				event = eventMasterEntity.getEventType() != null ? eventMasterEntity.getEventType().getNameHindi() : "";
				partyName = eventMasterEntity.getParty() != null ? eventMasterEntity.getParty().getNameHindi() : "";
				foodNotes = eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameHindi()
						: "";
			} else if (lang == 2) {
				venue = eventMasterEntity.getVenue() != null ? eventMasterEntity.getVenue().getNameGujarati() : "";
				event = eventMasterEntity.getEventType() != null ? eventMasterEntity.getEventType().getNameGujarati()
						: "";
				partyName = eventMasterEntity.getParty() != null ? eventMasterEntity.getParty().getNameGujarati() : "";
				foodNotes = eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameGujarati()
						: "";
			} else {
				venue = eventMasterEntity.getVenue() != null ? eventMasterEntity.getVenue().getNameEnglish() : "";
				event = eventMasterEntity.getEventType() != null ? eventMasterEntity.getEventType().getNameEnglish()
						: "";
				partyName = eventMasterEntity.getParty() != null ? eventMasterEntity.getParty().getNameEnglish() : "";
				foodNotes = eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameEnglish()
						: "";
			}

			Cell cell;
			
			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 25f, 25f, 25f }));
			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setFixedLayout();
			headerTable.setMarginTop(10f);

			cell = addCell(eDate.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eventMasterEntity.getEventStartDateTime().format(dateFormatter), basicFont, blackColor,
					labelColor, whiteColor, 1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(dateTimeLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eventMasterEntity.getEventStartDateTime().format(timeFormatter), basicFont, blackColor,
					labelColor, whiteColor, 1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(printedLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(LocalDateTime.now().format(dateTimeFormatter), basicFont, blackColor, labelColor, whiteColor,
					1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eVenue.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(venue.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(managerNameLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(managerName.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eventLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(event.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(referenceLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(reference, basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(nameLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(partyName.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(phone.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(phoneNumber.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			document.add(headerTable);

			Paragraph foodNo = new Paragraph().add(new Text(foodNotes.toUpperCase())).setWidth(UnitValue.createPercentValue(100f))
					.setTextAlignment(TextAlignment.CENTER).setFontSize(12f).setMarginTop(3f).setMarginBottom(3f);
			document.add(foodNo);

			Table fnTable = new Table(UnitValue.createPercentArray(new float[] { 35f, 20f, 15f, 30f }));
			fnTable.setWidth(UnitValue.createPercentValue(100f));
			fnTable.setFixedLayout();
			
			cell = addCell(particularLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 12f : 14f, 5f, 0.5f);
			fnTable.addCell(cell);
			
			cell = addCell(particularLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 12f : 14f, 5f, 0.5f);
			fnTable.addCell(cell);
			
			cell = addCell(qtyLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 12f : 14f, 5f, 0.5f);
			fnTable.addCell(cell);
			
			cell = addCell(signLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 12f : 14f, 5f, 0.5f);
			fnTable.addCell(cell);

			for (EventFunctionMasterEntity eventFunctionMasterEntity : eventFunctions) {
				addFunctionBlock(eventFunctionMasterEntity, fnTable, basicFont, blackColor, labelColor, whiteColor, lang);
			}
			document.add(fnTable);
			
			Table extraItemTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 25f, 25f, 25f }));
			extraItemTable.setWidth(UnitValue.createPercentValue(100f));
			extraItemTable.setFixedLayout();
			
			cell = addCell(extraItemLabel.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 4,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, lang == 0 ? 12f : 14f, 5f, 0f);
			extraItemTable.addCell(cell.setUnderline());
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 4,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 12f : 14f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f)).setMinHeight(10f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 4,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 12f : 14f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f)).setMinHeight(10f);
			extraItemTable.addCell(cell);
			
			cell = addCell(checkOutTimeLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell(managerSignLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell(managerNameLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 4,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f)
					.setMinHeight(1f).setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell("WE CONFIRMED RECEIVED FOOD /DECORATION / ALLIED SERVICES AS PER OUR ORDER", basicFont, blackColor, labelColor, whiteColor, 1, 4,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, lang == 0 ? 9f : 11f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell(guestSignLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell(guestNameLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			document.add(extraItemTable);
			
			Table feedbackTable = new Table(UnitValue.createPercentArray(new float[] { 40f, 15f, 15f, 15f, 15f }));
			feedbackTable.setWidth(UnitValue.createPercentValue(100f));
			feedbackTable.setFixedLayout();
			feedbackTable.setMarginTop(10f);
			feedbackTable.setKeepTogether(true);
			
			cell = addCell("REMARK", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("EXCELLENT", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("GOOD", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("FAIR", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("POOR", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("SERVICES PROVIDED", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("HELPFULNESS & COURTESY OF STAFF", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("CLEANLINESS & HYGIENE", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("STAFF GROOMING AND SKILLS", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("FOOD QUALITY & TASTE", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, lang == 0 ? 11f : 13f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("GUEST SUGGESTION:", basicFont, blackColor, labelColor, whiteColor, 1, 5,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			feedbackTable.addCell(cell.setUnderline());
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 5,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			feedbackTable.addCell(cell.setUnderline().setMinHeight(10f));
			
			cell = addCell("OVERALL DECORATION(IF ANY)", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("NOTE : If Guest wish to appreciate our efforts by gratuity, kindly drop your TIP in to the specific box and not in the hands of anyone.", basicFont, blackColor, labelColor, whiteColor, 1, 5,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 9f, 5f, 0f);
			feedbackTable.addCell(cell);
			
			document.add(feedbackTable);
			
			document.close();
			
			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ eventMasterEntity.getEventNo() + "/" + getReportName(fileName,
							formatDate(eventMasterEntity.getEventStartDateTime()), "Guest Signature Report")
					+ ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateGuestSignatureReport2(Long eventId, Long userid, Integer isCompanyDetails,
			HttpServletRequest re) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String phone, eDate, eVenue, agencyName, email, funLabel, menuItemLabel, dateTimeLabel, qtyLabel,
					priceLabel, totalPriceLabel, notesLabel, grandTotalLabel, personLabel, eventLabel, printedLabel,
					referenceLabel, managerNameLabel, nameLabel, particularLabel, signLabel, extraItemLabel, checkOutTimeLabel,
					managerSignLabel, guestSignLabel, guestNameLabel;
			
			// English
			basicFont = PdfFontFactory.createFont("/fonts/arial-regular.ttf");
			System.out.println("English font loaded successfully");
			phone = "Mobile No.";
			eDate = "Event Date";
			eVenue = "Venue";
			agencyName = "Agency Name";
			email = "Email";
			funLabel = "Function";
			menuItemLabel = "Menu Item Name";
			dateTimeLabel = "Event Time";
			qtyLabel = "Qnt";
			notesLabel = "Remarks";
			priceLabel = "Price";
			totalPriceLabel = "Total Price";
			grandTotalLabel = "Grand Total";
			personLabel = "Person";
			eventLabel = "Event";
			printedLabel = "Printed On";
			managerNameLabel = "Manager Name";
			referenceLabel = "Reference";
			nameLabel = "Name";
			particularLabel = "Particulars";
			signLabel = "Guest Signature";
			extraItemLabel = "Extra Items / Services";
			checkOutTimeLabel = "Check out time";
			managerSignLabel = "Manager Sign";
			guestSignLabel = "Guest Sign";
			guestNameLabel = "Guest Name";
			
			boldFont = PdfFontFactory.createFont("/fonts/arial-bold.ttf");
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			List<GuestSignatureReportResponseDto> signatures = guestSignatureRepository.getGuestSignatures(eventId);

			if (signatures == null || signatures.isEmpty()) {
				throw new RuntimeException("Data not found.");
			}
			
			String fileName = eventMasterEntity.getParty().getNameEnglish();

			File pdfFile = new File(outputPath + "/" + getReportName(fileName,
					formatDate(eventMasterEntity.getEventStartDateTime()), "Guest Signature Report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4, false);
			document.setMargins(20, 20, 20, 20);

			Color labelColor = new DeviceRgb(115, 99, 67);
			Color labelBgColor = new DeviceRgb(242, 235, 223);
			Color whiteColor = new DeviceRgb(255, 255, 255);
			Color blackColor = new DeviceRgb(0, 0, 0);

			if (isCompanyDetails == 1) {
				Table companyTable = getCompanyTable(cmpDto, basicFont, boldFont, 0, blackColor);
				document.add(companyTable);
			}

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:ss a");
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");

			String event = "";
			String partyName = "";
			String phoneNumber = eventMasterEntity.getParty().getMobileno();
			String foodNotes = "";
			String venue = "";
			String reference = eventMasterEntity.getReference() != null ? eventMasterEntity.getReference() : "";
			String managerName = eventMasterEntity.getManager() != null
					? eventMasterEntity.getManager().getFirstName() + " " + eventMasterEntity.getManager().getLastName()
					: "";
			venue = eventMasterEntity.getVenue() != null ? eventMasterEntity.getVenue().getNameEnglish() : "";
			event = eventMasterEntity.getEventType() != null ? eventMasterEntity.getEventType().getNameEnglish()
					: "";
			partyName = eventMasterEntity.getParty() != null ? eventMasterEntity.getParty().getNameEnglish() : "";
			foodNotes = eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameEnglish()
					: "";

			Cell cell;
			
			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 25f, 25f, 25f }));
			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setFixedLayout();
			headerTable.setMarginTop(10f);

			cell = addCell(eDate.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eventMasterEntity.getEventStartDateTime().format(dateFormatter), basicFont, blackColor,
					labelColor, whiteColor, 1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(dateTimeLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eventMasterEntity.getEventStartDateTime().format(timeFormatter), basicFont, blackColor,
					labelColor, whiteColor, 1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(printedLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(LocalDateTime.now().format(dateTimeFormatter), basicFont, blackColor, labelColor, whiteColor,
					1, 1, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eVenue.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(venue.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(managerNameLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(managerName.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(eventLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(event.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(referenceLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(reference, basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(nameLabel.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(partyName.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(phone.toUpperCase(), basicFont, labelColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			cell = addCell(phoneNumber.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 10f, 5f, 0.5f);
			headerTable.addCell(cell);

			document.add(headerTable);

			Paragraph foodNo = new Paragraph().add(new Text(foodNotes.toUpperCase())).setWidth(UnitValue.createPercentValue(100f))
					.setTextAlignment(TextAlignment.CENTER).setFontSize(12f).setMarginTop(3f).setMarginBottom(3f);
			document.add(foodNo);

			Table fnTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 20f, 15f, 15f, 20f }));
			fnTable.setWidth(UnitValue.createPercentValue(100f));
			fnTable.setFixedLayout();
			
			cell = addCell(funLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f, 0.5f);
			fnTable.addCell(cell);
			
			cell = addCell(particularLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f, 0.5f);
			fnTable.addCell(cell);
			
			cell = addCell(personLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f, 0.5f);
			fnTable.addCell(cell);
			
			cell = addCell(extraItemLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f, 0.5f);
			fnTable.addCell(cell);
			
			cell = addCell(guestSignLabel.toUpperCase(), basicFont, labelColor, labelColor, labelBgColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f, 0.5f);
			fnTable.addCell(cell);

			for (GuestSignatureReportResponseDto sign : signatures) {
				addFunctionBlockForReport1(sign, fnTable, basicFont, blackColor, labelColor, whiteColor, 0);
			}
			document.add(fnTable);
			
			Table extraItemTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 25f, 25f, 25f }));
			extraItemTable.setWidth(UnitValue.createPercentValue(100f));
			extraItemTable.setFixedLayout();
			
			cell = addCell(extraItemLabel.toUpperCase(), basicFont, blackColor, labelColor, whiteColor, 1, 4,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 12f, 5f, 0f);
			extraItemTable.addCell(cell.setUnderline());
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 4,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f)).setMinHeight(10f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 4,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f)).setMinHeight(10f);
			extraItemTable.addCell(cell);
			
			cell = addCell(checkOutTimeLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell(managerSignLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell(managerNameLabel.toUpperCase() + " : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.RIGHT, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, blackColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0f)
					.setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 4,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0f)
					.setMinHeight(1f).setBorderBottom(new SolidBorder(labelColor, 0.5f));
			extraItemTable.addCell(cell);
			
			document.add(extraItemTable);
			
			Table feedbackTable = new Table(UnitValue.createPercentArray(new float[] { 40f, 15f, 15f, 15f, 15f }));
			feedbackTable.setWidth(UnitValue.createPercentValue(100f));
			feedbackTable.setFixedLayout();
			feedbackTable.setMarginTop(10f);
			feedbackTable.setKeepTogether(true);
			
			cell = addCell("REMARK", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("EXCELLENT", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("GOOD", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("FAIR", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("POOR", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("SERVICES PROVIDED", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("HELPFULNESS & COURTESY OF STAFF", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("CLEANLINESS & HYGIENE", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("STAFF GROOMING AND SKILLS", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("FOOD QUALITY & TASTE", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 5,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0f);
			feedbackTable.addCell(cell.setUnderline());
			
			cell = addCell("OVERALL DECORATION(IF ANY)", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			feedbackTable.addCell(cell);
			
			cell = addCell("NOTE : If Guest wish to appreciate our efforts by gratuity, kindly drop your TIP in to the specific box and not in the hands of anyone.", basicFont, blackColor, labelColor, whiteColor, 1, 5,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 9f, 5f, 0f);
			feedbackTable.addCell(cell);
			
			document.add(feedbackTable);
			
			Table tipTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }));
			tipTable.setWidth(UnitValue.createPercentValue(100f));
			tipTable.setFixedLayout();
//			tipTable.setMarginTop(10f);
			tipTable.setKeepTogether(true);
			
			cell = addCell("Tip Box : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 14f, 5f, 0.5f);
			tipTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f, 5f, 0.5f);
			tipTable.addCell(cell);
			
			cell = addCell("WE CONFIRMED RECEIVED FOOD/DECORATION/ALLIED SERVICES AS PER OUR ORDER", basicFont, blackColor, labelColor, whiteColor, 1, 5,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 9f, 5f, 0f);
			tipTable.addCell(cell);
			
			document.add(tipTable);
			
			Table footerTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 25f, 50f }));
			footerTable.setWidth(UnitValue.createPercentValue(100f));
			footerTable.setFixedLayout();
			footerTable.setMarginTop(10f);
			footerTable.setKeepTogether(true);
			
			cell = addCell("COORDINATOR NAME : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell.setBorderBottom(new SolidBorder(blackColor, 0.5f)));
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell);
			
			cell = addCell("MO. NUMBER : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell.setBorderBottom(new SolidBorder(blackColor, 0.5f)));
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell);
			
			cell = addCell("COORDINATOR SIGN : ", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell);
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell.setBorderBottom(new SolidBorder(blackColor, 0.5f)));
			
			cell = addCell("", basicFont, blackColor, labelColor, whiteColor, 1, 1,
					TextAlignment.LEFT, VerticalAlignment.MIDDLE, 11f, 5f, 0);
			footerTable.addCell(cell);

			ImageData logoData = menuPreparationServiceImpl
//					.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
					.loadImageFromResource("/flipbook/pages/amoncar_guest_signature_qr.png");
			Image logo = new Image(logoData);

			// Resize & align
			logo.scaleAbsolute(70f, 70f);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			footerTable.addCell(new Cell(1, 3).add(logo).setPaddingTop(20f).setBorder(Border.NO_BORDER));
			
			document.add(footerTable);
			
			document.close();
			
			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ eventMasterEntity.getEventNo() + "/" + getReportName(fileName,
							formatDate(eventMasterEntity.getEventStartDateTime()), "Guest Signature Report")
					+ ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}
	
	public CompanyDetailsResponseDto getCompanyDetails(Long userId) {
		UserMasterEntity user = userMasterRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		Optional<Object[]> op = eventFunctionMenuAllocationRepository.getCompanyDetailsByUserId(userId);

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
