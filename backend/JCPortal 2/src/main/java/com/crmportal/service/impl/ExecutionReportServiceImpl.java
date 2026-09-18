package com.crmportal.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
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
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.MenuAllocationItemCaptainReceipeEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.MenuAllocationItemCaptainReceipeRepository;
import com.crmportal.repository.MenuAllocationItemRawMaterialRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.EventRawMaterialInfoDto;
import com.crmportal.response.dto.MenuItemRawMaterialCategoryResponseDto;
import com.crmportal.response.dto.MenuItemRawMaterialResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.service.ExecutionReportService;
import com.crmportal.utility.AdobeDocxGenerator;
import com.crmportal.utility.MenuItemRawMaterialHeaderEventHandler;
import com.crmportal.utility.PageNumberHandler;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
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
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class ExecutionReportServiceImpl implements ExecutionReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	AdobeDocxGenerator adobeDocxGenerator;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	@Autowired
	MenuAllocationItemRawMaterialRepository menuAllocationItemRawMaterialRepository;

	@Autowired
	RawMaterialReportServiceImpl rawMaterialReportServiceImpl;
	
	@Autowired
	UnitMasterRepository unitMasterRepository;
	
	@Autowired
	EventRawMaterialServiceImpl eventRawMaterialServiceImpl;
	
	@Autowired
	MenuAllocationItemCaptainReceipeRepository menuAllocationItemCaptainReceipeRepository;

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}

	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + fileSafe(date.replace("/", "_") + "_" + type.replace(" ", "_").toUpperCase());
	}

	public String formatDate(LocalDateTime dateTime) {
		return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
	}

	public String formatDate(String date) {
		DateTimeFormatter input = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		DateTimeFormatter output = DateTimeFormatter.ofPattern("dd_MM_yyyy");
		if (date == null || date.isEmpty()) {
			return "";
		}
		LocalDate localDate = LocalDate.parse(date, input);
		return localDate.format(output);
	}

	private String getPartyNameByEventId(Long eventId) {
		String party = eventMasterRepository.getPartyByEventId(eventId)
				.orElseThrow(() -> new RuntimeException("Party Details Not Found."));
		return party;
	}

	@Override
	public String menuReportWithQuantity(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer withQty, Integer isUserDetails, Integer isPartyDetails, Integer is3Column) {

		try {

			PdfFont basicFont = null;
			PdfFont itemFont = null;
			PdfFont boldFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "",
					personLabel = "", note = "", functionLabel = "", dateTime = "", outsoucename = "";
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				itemFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थान";
				functionLabel = "कार्यक्रम";
				dateTime = "दिनांक एवं समय";
				personLabel = "मेम्बर्स";
				note = "नोट";
				outsoucename = "आउटसोर्स";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				itemFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				customerName = "ગ્રાહકનું નામ";
				customerPhone = "મોબાઇલ નંબર";
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				fNotes = "ભોજન વિગતો";
				eVenue = "આયોજન સ્થળ";
				functionLabel = "કાર્યક્રમ";
				dateTime = "તારીખ અને સમય";
				personLabel = "વ્યક્તિ";
				note = "નોંધ";
				outsoucename = "ઓઉટસોઉર્સ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				itemFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				functionLabel = "Function";
				personLabel = "PERSONS";
				dateTime = "Date & Time";
				note = "Note";
				outsoucename = "Outsource";
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

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

			String reportType = "menuwithquantity";
			String title = "Menu With Quantity Report";
			if (withQty == 0) {
				reportType = "menuwithoutquantity";
				title = "Menu Without Quantity Report";
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(40, 20, 60, 20);

			List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl.getAllFunctionDetails(eventId,
					eventFunctionId, lang);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpCountryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String logo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

//			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			Image img = new Image(imgData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(100);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("Mobile No.")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text(cmpCountryCode + " " + cmpMobile)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isUserDetails == 1) {
				document.add(cmpHead);
			}

			Paragraph heading = new Paragraph().add(
					new Text(title).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(0f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			float[] cloumnWidthEvent = { 25f, 1f, 30f, 20f, 1f, 20f };
			Table eventDataTable = new Table(UnitValue.createPercentArray(cloumnWidthEvent));
			eventDataTable.setMarginBottom(8f);
			eventDataTable.setBorder(new SolidBorder(1f));

			String clientName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();
			String venue = eventData.getVenueName() == null || eventData.getVenueName().isEmpty() ? ""
					: eventData.getVenueName();
			String clientMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();
			String eventName = eventData.getEventName() == null || eventData.getEventName().isEmpty() ? ""
					: eventData.getEventName();
			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			Paragraph event = new Paragraph();
			Cell eventCell = new Cell();
			// 1st row
			if (isPartyDetails == 1) {
				event = new Paragraph().add(new Text(customerName).setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setPaddingLeft(10);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(clientName).setFontSize(14f).setFont(basicFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(customerPhone).setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(clientMobile).setFontSize(14f).setFont(basicFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);
			}
			// 2ed row
			event = new Paragraph().add(new Text(eName).setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventName).setFontSize(14f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eDate).setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventDate).setFontSize(14f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			// 3rd row
			event = new Paragraph().add(new Text(eVenue).setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(venue).setFontSize(14f).setFont(basicFont));
			eventCell = new Cell(3, 4).add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			document.add(eventDataTable);

			for (int k = 0; k < allFunctions.size(); k++) {

				MenuQuantityReponseDto fun = allFunctions.get(k);

				Long functionId = fun.getEventFunctionId();
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();
				Integer person = fun.getPerson();
				String functionTime = fun.getFunctionTime() == null || fun.getFunctionTime().isEmpty() ? ""
						: fun.getFunctionTime();

				float[] columnWidth = { 20f, 1f, 44f, 15f, 1f, 20f };
				Table function = new Table(UnitValue.createPercentArray(columnWidth));
				function.setWidth(UnitValue.createPercentValue(100));
				function.setBorder(new SolidBorder(Border.SOLID));

				Paragraph label = new Paragraph().add(new Text(functionLabel).setFont(boldFont).setFontSize(14));
				Paragraph saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14));
				Paragraph name = new Paragraph().add(new Text(functionName).setFont(basicFont).setFontSize(14));

				Cell cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(personLabel).setFont(boldFont).setFontSize(14));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14));
				name = new Paragraph().add(new Text(person.toString()).setFont(basicFont).setFontSize(14));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(dateTime).setFont(boldFont).setFontSize(14));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14));
				name = new Paragraph().add(new Text(functionTime).setFont(basicFont).setFontSize(14));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell(1, 4).add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				String functionVenue = "";
				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, functionId);
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = fun.getFnVenue();
				}

				label = new Paragraph().add(new Text(eVenue).setFont(boldFont).setFontSize(14));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14));
				name = new Paragraph().add(new Text(functionVenue).setFont(basicFont).setFontSize(14));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell(1, 4).add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				document.add(function);

				List<MenuQuantityReponseDto> allItems = menuItemRawMaterialServiceImpl.getFunctionItemDetails(eventId,
						functionId, lang);

				int index = 0;
				for (MenuQuantityReponseDto item : allItems) {
					Div itemBlock = new Div();
					itemBlock.setKeepTogether(true);

					String itemName = item.getItemName() == null || item.getItemName().isEmpty() ? ""
							: item.getItemName();

					Long itemId = item.getMenuItemId();
					Boolean outside = item.getOutside();

					String instruction = item.getInstructions() == null || item.getInstructions().isEmpty() ? ""
							: item.getInstructions();
					if (!instruction.trim().isEmpty()) {
						itemName = itemName + " (" + instruction + " )";
					}
					if (outside) {
						itemName = itemName + " ( " + outsoucename + " )";
					}
					name = new Paragraph()
							.add(new Text(++index + ". ")
									.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16))
							.add(new Text(itemName).setFont(boldFont).setFontSize(16)).setPaddingLeft(20);
					itemBlock.add(name);

					ILineDrawer line = new SolidLine(1f);
					LineSeparator separator = new LineSeparator(line);
					separator.setMarginTop(-5f);
					separator.setMarginBottom(5f);
					separator.setWidth(UnitValue.createPercentValue(100));
					itemBlock.add(separator);

					float[] columnWidth2 = null;
					float[] columnWidth3 = null;

					if (is3Column == 1) {
						columnWidth2 = new float[] { 26f, 4f, 2.33f, 26f, 4f, 2.33f, 26f, 4f, 2.33f };
						columnWidth3 = new float[] { 33.33f, 33.33f, 33.33f };
					} else {
						columnWidth2 = new float[] { 41f, 6f, 3f, 41f, 6f, 3f };
						columnWidth3 = new float[] { 50f, 50f };
					}
					Table rawMaterial = new Table(UnitValue.createPercentArray(columnWidth3));
					if (withQty == 1) {
						rawMaterial = new Table(UnitValue.createPercentArray(columnWidth2));
					}

					rawMaterial.setWidth(UnitValue.createPercentValue(100));
					rawMaterial.setHorizontalAlignment(HorizontalAlignment.CENTER);
					rawMaterial.setMarginBottom(5f);

					if (outside == false) {
						List<MenuQuantityReponseDto> allRawMaterialItems = menuItemRawMaterialServiceImpl
								.getFunctionItemRawMaterialDetails(eventId, functionId, itemId, lang);

						for (int i = 0; i < allRawMaterialItems.size(); i++) {
							MenuQuantityReponseDto rm = allRawMaterialItems.get(i++);

							Double qty = rm.getQty() == null ? 0 : rm.getQty();
							String unit = rm.getUnitSymbol() == null || rm.getUnitSymbol().isEmpty() ? ""
									: rm.getUnitSymbol();
							String rawmaterial = rm.getRawMaterialName() == null || rm.getRawMaterialName().isEmpty()
									? ""
									: rm.getRawMaterialName();

							Paragraph data;
							Cell dataCell;

							// 2. Item Name
							data = new Paragraph()
									.add(new Text(rawmaterial).setFont(basicFont).setFontSize(is3Column == 1 ? 12 : 15))
									.setMargin(0).setMultipliedLeading(1);
							dataCell = new Cell().add(data).setBorder(Border.NO_BORDER)
									.setTextAlignment(TextAlignment.LEFT).setPadding(0);
							rawMaterial.addCell(dataCell);

							if (withQty == 1) {
								// 3. Qty
								data = new Paragraph()
										.add(new Text(qty.toString()).setFont(basicFont)
												.setFontSize(is3Column == 1 ? 10 : 13))
										.setMargin(0).setMultipliedLeading(1);
								dataCell = new Cell().add(data).setBorder(Border.NO_BORDER)
										.setTextAlignment(TextAlignment.RIGHT);
								rawMaterial.addCell(dataCell);

								// 4. Unit
								data = new Paragraph()
										.add(new Text(unit).setFont(basicFont).setFontSize(is3Column == 1 ? 12 : 15))
										.setMargin(0).setMultipliedLeading(1);
								dataCell = new Cell().add(data).setPaddingLeft(3f).setBorder(Border.NO_BORDER)
										.setTextAlignment(TextAlignment.LEFT);
								rawMaterial.addCell(dataCell);
							}

							if (i < allRawMaterialItems.size()) {
								rm = allRawMaterialItems.get(i);

								qty = rm.getQty() == null ? 0 : rm.getQty();
								unit = rm.getUnitSymbol() == null || rm.getUnitSymbol().isEmpty() ? ""
										: rm.getUnitSymbol();
								rawmaterial = rm.getRawMaterialName() == null || rm.getRawMaterialName().isEmpty() ? ""
										: rm.getRawMaterialName();

								// 2. Item Name
								data = new Paragraph()
										.add(new Text(rawmaterial).setFont(basicFont)
												.setFontSize(is3Column == 1 ? 12 : 15))
										.setMargin(0).setMultipliedLeading(1);
								dataCell = new Cell().add(data).setBorder(Border.NO_BORDER)
										.setTextAlignment(TextAlignment.LEFT).setPadding(0);
								rawMaterial.addCell(dataCell);

								if (withQty == 1) {
									// 3. Qty
									data = new Paragraph()
											.add(new Text(qty.toString()).setFont(basicFont)
													.setFontSize(is3Column == 1 ? 10 : 13))
											.setMargin(0).setMultipliedLeading(1);
									dataCell = new Cell().add(data).setBorder(Border.NO_BORDER)
											.setTextAlignment(TextAlignment.RIGHT);
									rawMaterial.addCell(dataCell);

									// 4. Unit
									data = new Paragraph()
											.add(new Text(unit).setFont(basicFont)
													.setFontSize(is3Column == 1 ? 12 : 15))
											.setMargin(0).setMultipliedLeading(1);
									dataCell = new Cell().add(data).setPaddingLeft(3f).setBorder(Border.NO_BORDER)
											.setTextAlignment(TextAlignment.LEFT);
									rawMaterial.addCell(dataCell);
								}
							}
						}
						itemBlock.add(rawMaterial);
					}

					separator = new LineSeparator(line);
					separator.setMarginTop(0);
					separator.setMarginBottom(-5f);
					separator.setWidth(UnitValue.createPercentValue(100));
					itemBlock.add(separator);

					document.add(itemBlock);
				}

				if (k + 1 < allFunctions.size()) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
			}

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 5, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}

	@Override
	public String rawMaterialwiseMenuItem(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isUserDetails, Integer isPartyDetails) {

		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String customerName = "Customer Name";
			String customerPhone = "Mobile No.";
			String eName = "Event Name";
			String eDate = "Event Date";
			String fNotes = "Food Note";
			String eVenue = "Venue";
			String functionLabel = "Function";
			String personLabel = "Persons";
			String dateTime = "Date & Time";
			String note = "Note";
			String quantity = "Quantity";
			String rate = "Rate";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थल";
				functionLabel = "कार्यक्रम";
				dateTime = "दिनांक एवं समय";
				personLabel = "मेम्बर्स";
				note = "नोट";
				quantity = "मात्रा";
				rate = "मूल्य";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				customerName = "ગ્રાહકનું નામ";
				customerPhone = "મોબાઇલ નંબર";
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				fNotes = "ભોજન વિગતો";
				eVenue = "આયોજન સ્થળ";
				functionLabel = "કાર્યક્રમ";
				dateTime = "તારીખ અને સમય";
				personLabel = "વ્યક્તિ";
				note = "નોંધ";
				quantity = "માત્રા";
				rate = "કિંમત";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

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

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(LocalDateTime.parse(eventData.getEventDate())), "raw material wise menu item report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(40, 35, 50, 20);

			List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl.getAllFunctionDetails(eventId,
					eventFunctionId, lang);

			float[] columnWidthHead = { 20f, 18f, 2f, 59f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpCountryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String logo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? ""
					: environment.getProperty("app.image.url") + eventData.getLogo();

			Image img = new Image(ImageDataFactory.create(logo));
//			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			Image img = new Image(imgData);
			img.setWidth(100);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("Mobile No.")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text(cmpCountryCode + " " + cmpMobile)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			if (isUserDetails == 1) {
				document.add(cmpHead);
			}

			Paragraph heading = new Paragraph().add(new Text("Raw Material Wise Recipe")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(5f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			float[] cloumnWidthEvent = { 25f, 1f, 30f, 19f, 1f, 21f };
			Table eventDataTable = new Table(UnitValue.createPercentArray(cloumnWidthEvent));
			eventDataTable.setMarginBottom(8f);
			eventDataTable.setBorder(new SolidBorder(1f));

			String clientName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();
			String venue = eventData.getVenueName() == null || eventData.getVenueName().isEmpty() ? ""
					: eventData.getVenueName();
			String clientMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();
			String eventName = eventData.getEventName() == null || eventData.getEventName().isEmpty() ? ""
					: eventData.getEventName();
			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			Paragraph event = new Paragraph();
			Cell eventCell = new Cell();
			// 1st row
			if (isPartyDetails == 1) {
				event = new Paragraph().add(new Text(customerName).setFontSize(14f).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setPaddingLeft(10);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(clientName).setFontSize(14f).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(customerPhone).setFontSize(14f).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(clientMobile).setFontSize(14f).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

			}
			// 2ed row
			event = new Paragraph().add(new Text(eName).setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventName).setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eDate).setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventDate).setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			// 3rd row
			event = new Paragraph().add(new Text(venue).setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(venue).setFontSize(14f).setFont(boldFont)).setMargin(0)
					.setMultipliedLeading(1);
			eventCell = new Cell(3, 4).add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			document.add(eventDataTable);

			for (int k = 0; k < allFunctions.size(); k++) {

				MenuQuantityReponseDto fun = allFunctions.get(k++);

				Long functionId = fun.getEventFunctionId();
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();
				Integer person = fun.getPerson();
				String functionTime = fun.getFunctionTime() == null || fun.getFunctionTime().isEmpty() ? ""
						: fun.getFunctionTime();

				float[] columnWidth = { 20f, 1f, 44f, 15f, 1f, 20f };
				Table function = new Table(UnitValue.createPercentArray(columnWidth));
				function.setWidth(UnitValue.createPercentValue(100));
				function.setBorder(new SolidBorder(Border.SOLID));

				Paragraph label = new Paragraph().add(new Text(functionLabel).setFont(boldFont).setFontSize(14))
						.setMargin(0).setMultipliedLeading(1);
				Paragraph saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14)).setMargin(0)
						.setMultipliedLeading(1);
				Paragraph name = new Paragraph().add(new Text(functionName).setFont(basicFont).setFontSize(14))
						.setMargin(0).setMultipliedLeading(1);

				Cell cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(personLabel).setFont(boldFont).setFontSize(14)).setMargin(0)
						.setMultipliedLeading(1);
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14)).setMargin(0)
						.setMultipliedLeading(1);
				name = new Paragraph().add(new Text(person.toString()).setFont(basicFont).setFontSize(14)).setMargin(0)
						.setMultipliedLeading(1);
				cell = new Cell().add(label).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(dateTime).setFont(boldFont).setFontSize(14)).setMargin(0)
						.setMultipliedLeading(1);
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14)).setMargin(0)
						.setMultipliedLeading(1);
				name = new Paragraph().add(new Text(functionTime).setFont(basicFont).setFontSize(14)).setMargin(0)
						.setMultipliedLeading(1);
				cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell(3, 6).add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				document.add(function);

				List<MenuQuantityReponseDto> allRawMaterialCat = menuItemRawMaterialServiceImpl
						.getRawMaterialCategory(eventId, functionId, lang);

				for (MenuQuantityReponseDto cat : allRawMaterialCat) {
					Long catId = cat.getRawMaterialCatId();
					String catName = cat.getRawMaterialCatName() == null || cat.getRawMaterialCatName().isEmpty() ? ""
							: cat.getRawMaterialCatName();

					Paragraph catPara = new Paragraph().add(new Text(catName).setFont(boldFont)).setMargin(0)
							.setMultipliedLeading(1);
					catPara.setFontSize(16);
					catPara.setWidth(UnitValue.createPercentValue(100));
					catPara.setBackgroundColor(new DeviceRgb(200, 200, 200));
					catPara.setMarginTop(10f);
					catPara.setTextAlignment(TextAlignment.CENTER);
					document.add(catPara);

					ILineDrawer line = new SolidLine(1f);
					LineSeparator separator = new LineSeparator(line);
					separator.setMarginTop(5f);
					separator.setMarginBottom(5f);
					separator.setWidth(UnitValue.createPercentValue(100));
					document.add(separator);

					List<MenuQuantityReponseDto> allRawMaterial = menuItemRawMaterialServiceImpl.getRawMaterial(eventId,
							functionId, lang, catId);

					for (MenuQuantityReponseDto rawMaterial : allRawMaterial) {
						String rawMaterialName = rawMaterial.getRawMaterialName() == null
								|| rawMaterial.getRawMaterialName().isEmpty() ? "" : rawMaterial.getRawMaterialName();
						Long rawMaterialId = rawMaterial.getRawMaterialId();

						Div rawMaterialSection = new Div();
						rawMaterialSection.setKeepTogether(true);

						float[] columnWidthRawHead = { 70f, 30f };
						Table rawMaterialHead = new Table(UnitValue.createPercentArray(columnWidthRawHead));
						rawMaterialHead.setWidth(UnitValue.createPercentValue(100));

						Paragraph rawMaterialPara = new Paragraph()
								.add(new Text(rawMaterialName).setFont(boldFont).setFontSize(16)).setMargin(0)
								.setMultipliedLeading(1);
						rawMaterialPara.setTextAlignment(TextAlignment.LEFT);
						rawMaterialPara.setPaddingLeft(15f);

						Cell rh = new Cell().add(rawMaterialPara).setBorder(Border.NO_BORDER);
						rawMaterialHead.addCell(rh);

						rawMaterialPara = new Paragraph().add(new Text(quantity).setFont(boldFont).setFontSize(16))
								.setMargin(0).setMultipliedLeading(1);
						rawMaterialPara.setTextAlignment(TextAlignment.CENTER);
						rawMaterialPara.setHorizontalAlignment(HorizontalAlignment.RIGHT);

						rh = new Cell().add(rawMaterialPara).setBorder(Border.NO_BORDER);
						rawMaterialHead.addCell(rh);

						rawMaterialSection.add(rawMaterialHead);

						line = new SolidLine(1f);
						separator = new LineSeparator(line);
						separator.setMarginTop(0);
						separator.setMarginBottom(0);
						separator.setWidth(UnitValue.createPercentValue(30));
						separator.setHorizontalAlignment(HorizontalAlignment.RIGHT);
						rawMaterialSection.add(separator);

						List<MenuQuantityReponseDto> allItema = menuItemRawMaterialServiceImpl
								.getRawMaterialWiseItem(eventId, functionId, lang, catId, rawMaterialId);

						float[] columnWidthItem = { 70f, 30f };
						Table itemData = new Table(UnitValue.createPercentArray(columnWidthItem));
						itemData.setWidth(UnitValue.createPercentValue(100));
						int c = 1;
						Double totalWeight = 0.0;
						StringBuilder unit = new StringBuilder();
						for (MenuQuantityReponseDto item : allItema) {
							String iname = item.getItemName() == null || item.getItemName().isEmpty() ? ""
									: item.getItemName();
							String unitname = item.getUnitSymbol() == null || item.getUnitSymbol().isEmpty() ? ""
									: item.getUnitSymbol();

							Paragraph data = new Paragraph()
									.add(new Text(c++ + " " + iname).setFont(basicFont).setFontSize(14)).setMargin(0)
									.setMultipliedLeading(1);

							Cell dataCell = new Cell().add(data).setPaddingLeft(15f).setBorder(Border.NO_BORDER);

							itemData.addCell(dataCell);

							data = new Paragraph().add(new Text(item.getQty().toString() + " " + unitname)
									.setFont(basicFont).setFontSize(14)).setMargin(0).setMultipliedLeading(1);

							dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER)
									.setBorder(Border.NO_BORDER);

							itemData.addCell(dataCell);

							unit.replace(0, unit.length(), unitname);

							if (item.getUnitSymbol() != null && unitname.equalsIgnoreCase("gram")) {
								totalWeight += (item.getQty() / 1000);
								unit.replace(0, unit.length(), "KILO");
							} else {
								totalWeight += item.getQty();
							}

						}

						rawMaterialSection.add(itemData);

						line = new SolidLine(1f);
						separator = new LineSeparator(line);
						separator.setMarginTop(0);
						separator.setMarginBottom(0);
						separator.setWidth(UnitValue.createPercentValue(30));
						separator.setHorizontalAlignment(HorizontalAlignment.RIGHT);
						rawMaterialSection.add(separator);

						if (totalWeight > 1000) {
							totalWeight = totalWeight / 1000;
						}

						Paragraph total = new Paragraph()
								.add(new Text(totalWeight.toString() + " " + unit.toString()).setFont(basicFont))
								.setMargin(0).setMultipliedLeading(1);
						total.setFontSize(14);
						total.setWidth(UnitValue.createPercentValue(30f));
						total.setHorizontalAlignment(HorizontalAlignment.RIGHT);
						total.setTextAlignment(TextAlignment.CENTER);
//						total.setBorder(new SolidBorder(1f));
						rawMaterialSection.add(total);

						line = new SolidLine(1f);
						separator = new LineSeparator(line);
						separator.setMarginTop(5f);
						separator.setMarginBottom(5f);
						separator.setWidth(UnitValue.createPercentValue(100));
						rawMaterialSection.add(separator);

						document.add(rawMaterialSection);

					}
				}

				if (k < allFunctions.size()) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), eventData.getEventDate(),
							"raw material wise menu item report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}

//	@Override
//	public String menuReportWithQuantityDocx(Long eventId, Long eventFunctionId,
//	        HttpServletRequest re, int lang, Long userid,
//	        Integer isWithQty, Integer isCompanyDetails,
//	        Integer isPartyDetails, Integer is3Column) {
// 
//	    // Generate PDF
//	    menuReportWithQuantity(eventId, eventFunctionId, re, lang, userid,
//	            isWithQty, isCompanyDetails, isPartyDetails, is3Column);
// 
//	    MenuQuantityReponseDto eventData =
//	            menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
// 
//	    String eventNo = eventData.getEventNo();
//	    String title = (isWithQty == 0)
//	            ? "Menu Without Quantity Report"
//	            : "Menu With Quantity Report";
// 
//	    String rootPath = re.getSession()
//	            .getServletContext()
//	            .getRealPath("/");
// 
//	    String pdfFileName = getReportName(
//	            getPartyNameByEventId(eventId),
//	            formatDate(eventData.getEventDate()),
//	            title) + ".pdf";
// 
//	    String pdfPath = rootPath
//	            + "resources/tempDownload/"
//	            + eventNo
//	            + "/"
//	            + pdfFileName;
// 
//	    return adobeDocxGenerator.convertPdfToDocxAndGetUrl(new File(pdfPath), eventNo);
//	}
//	
	@Override
	public String menuReportWithQuantityDocx(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer withQty, Integer isUserDetails, Integer isPartyDetails, Integer is3Column) {

		try {

			String customerName = "";
			String customerPhone = "";
			String eName = "";
			String eDate = "";
			String eVenue = "";
			String personLabel = "";
			String functionLabel = "";
			String dateTime = "";
			String outsourceName = "";

			if (lang == 1) {

				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				eVenue = "आयोजन स्थान";
				functionLabel = "कार्यक्रम";
				dateTime = "दिनांक एवं समय";
				personLabel = "मेम्बर्स";
				outsourceName = "आउटसोर्स";

			} else if (lang == 2) {

				customerName = "ગ્રાહકનું નામ";
				customerPhone = "મોબાઇલ નંબર";
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "આયોજન સ્થળ";
				functionLabel = "કાર્યક્રમ";
				dateTime = "તારીખ અને સમય";
				personLabel = "વ્યક્તિ";
				outsourceName = "ઓઉટસોઉર્સ";

			} else {

				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				eVenue = "Venue";
				functionLabel = "Function";
				personLabel = "PERSONS";
				dateTime = "Date & Time";
				outsourceName = "Outsource";
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			String eventNo = eventData.getEventNo();

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			String title = withQty == 0 ? "Menu Without Quantity Report" : "Menu With Quantity Report";

			File docFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".docx");

			XWPFDocument document = new XWPFDocument();

			// Footer
			addFooter(document);

			// PDF margins (40,20,25,20)
			CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();

			CTPageMar pageMar = sectPr.addNewPgMar();

			pageMar.setTop(BigInteger.valueOf(800));
			pageMar.setBottom(BigInteger.valueOf(250));
			pageMar.setLeft(BigInteger.valueOf(400));
			pageMar.setRight(BigInteger.valueOf(400));
			pageMar.setFooter(BigInteger.valueOf(200));

			List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl.getAllFunctionDetails(eventId,
					eventFunctionId, lang);

			// Company Header
			addCompanyHeader(document, eventData, isUserDetails);

			// Report Title
			XWPFParagraph heading = document.createParagraph();

			heading.setAlignment(ParagraphAlignment.CENTER);
			heading.setSpacingBefore(0);
			heading.setSpacingAfter(120);

			XWPFRun headingRun = heading.createRun();

			headingRun.setBold(true);
			headingRun.setFontFamily("Arial");
			headingRun.setFontSize(18);
			headingRun.setText(title);

			// Event Information Box
			addEventInfoTable(document, eventData, isPartyDetails, customerName, customerPhone, eName, eDate, eVenue);

			// Same gap as PDF
			XWPFParagraph gap = document.createParagraph();
			gap.setSpacingAfter(80);

			// Functions
			for (int i = 0; i < allFunctions.size(); i++) {

				MenuQuantityReponseDto functionData = allFunctions.get(i);

				// Separate Function Box
				addFunctionTable(document, functionData, functionLabel, personLabel, dateTime);

				List<MenuQuantityReponseDto> menuItems = menuItemRawMaterialServiceImpl.getFunctionItemDetails(eventId,
						functionData.getEventFunctionId(), lang);

				Integer catIndex = 0;
				for (MenuQuantityReponseDto menuItem : menuItems) {
					catIndex++;
					addMenuCategoryHeader(document, menuItem.getItemName(), catIndex);

//					addHorizontalLine(document);

					addRawMaterialTable(document, eventId, functionData.getEventFunctionId(), menuItem.getMenuItemId(),
							is3Column, withQty, lang, outsourceName);

					addHorizontalLine(document);
				}

				// Page break between functions
				if (i < allFunctions.size() - 1) {

					XWPFParagraph pageBreak = document.createParagraph();

					pageBreak.setPageBreak(true);
				}
			}

			try (FileOutputStream out = new FileOutputStream(docFile)) {

				document.write(out);
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/docx/" + eventNo + "/" + docFile.getName();

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("DOCX report generation failed.", e);
		}
	}

	private void setNoBorder(XWPFTableCell cell) {

		CTTcPr tcPr = cell.getCTTc().getTcPr();

		if (tcPr == null) {
			tcPr = cell.getCTTc().addNewTcPr();
		}

		CTTcBorders borders = tcPr.getTcBorders();

		if (borders == null) {
			borders = tcPr.addNewTcBorders();
		}

		CTBorder top = borders.getTop();
		if (top == null) {
			top = borders.addNewTop();
		}
		top.setVal(STBorder.NONE);

		CTBorder bottom = borders.getBottom();
		if (bottom == null) {
			bottom = borders.addNewBottom();
		}
		bottom.setVal(STBorder.NONE);

		CTBorder left = borders.getLeft();
		if (left == null) {
			left = borders.addNewLeft();
		}
		left.setVal(STBorder.NONE);

		CTBorder right = borders.getRight();
		if (right == null) {
			right = borders.addNewRight();
		}
		right.setVal(STBorder.NONE);
	}

	private void addCompanyHeader(XWPFDocument document, MenuQuantityReponseDto eventData, Integer isUserDetails)
			throws Exception {

		if (isUserDetails == null || isUserDetails != 1) {
			return;
		}

		String cmpName = eventData.getCompanyName() == null ? "" : eventData.getCompanyName();

		String cmpEmail = eventData.getCompanyEmail() == null ? "" : eventData.getCompanyEmail();

		String cmpCountryCode = eventData.getCountryCode() == null ? "" : eventData.getCountryCode();

		String cmpMobile = eventData.getOfficeNo() == null ? "" : eventData.getOfficeNo();

		String logo = eventData.getLogo() == null ? "" : eventData.getLogo();

		XWPFTable table = document.createTable(3, 4);

		table.setWidth("100%");

		removeBorders(table);

		// =====================================================
		// LOGO
		// =====================================================

		XWPFTableCell logoCell = table.getRow(0).getCell(0);

		setNoBorder(logoCell);

		int pictureType = XWPFDocument.PICTURE_TYPE_PNG;

		if (logo.toLowerCase().endsWith(".jpg") || logo.toLowerCase().endsWith(".jpeg")) {

			pictureType = XWPFDocument.PICTURE_TYPE_JPEG;

		} else if (logo.toLowerCase().endsWith(".png")) {

			pictureType = XWPFDocument.PICTURE_TYPE_PNG;
		}

		if (!logo.isEmpty()) {

			try (InputStream is = new URL(environment.getProperty("app.image.url") + logo).openStream()) {

				XWPFParagraph p = logoCell.getParagraphs().get(0);

				p.setAlignment(ParagraphAlignment.LEFT);
				p.setSpacingBefore(0);
				p.setSpacingAfter(0);

				XWPFRun run = p.createRun();

				run.addPicture(is, pictureType, logo, Units.toEMU(100), Units.toEMU(100));
			}
		}

		// Row span (3 rows)
		CTTcPr logoPr = logoCell.getCTTc().isSetTcPr() ? logoCell.getCTTc().getTcPr() : logoCell.getCTTc().addNewTcPr();

		logoPr.addNewVMerge().setVal(STMerge.RESTART);

		for (int i = 1; i < 3; i++) {

			XWPFTableCell cell = table.getRow(i).getCell(0);

			CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();

			tcPr.addNewVMerge().setVal(STMerge.CONTINUE);

			setNoBorder(cell);
		}

		// =====================================================
		// COMPANY NAME
		// =====================================================

		XWPFTableCell nameCell = table.getRow(0).getCell(1);

		setNoBorder(nameCell);

		XWPFParagraph namePara = nameCell.getParagraphs().get(0);

		namePara.setAlignment(ParagraphAlignment.LEFT);
		namePara.setSpacingBefore(0);
		namePara.setSpacingAfter(0);

		XWPFRun nameRun = namePara.createRun();

		nameRun.setBold(true);
		nameRun.setFontFamily("Arial");
		nameRun.setFontSize(18);
		nameRun.setText(cmpName);

		CTTcPr namePr = nameCell.getCTTc().isSetTcPr() ? nameCell.getCTTc().getTcPr() : nameCell.getCTTc().addNewTcPr();

		namePr.addNewGridSpan().setVal(BigInteger.valueOf(3));

		table.getRow(0).removeCell(3);
		table.getRow(0).removeCell(2);

		// =====================================================
		// MOBILE
		// =====================================================

		XWPFTableRow row1 = table.getRow(1);

		setCellText(row1.getCell(1), "Mobile No.", 14, true);
		setCellText(row1.getCell(2), ":", 14, true);
		setCellText(row1.getCell(3), cmpCountryCode + " " + cmpMobile, 14, false);

		setNoBorder(row1.getCell(1));
		setNoBorder(row1.getCell(2));
		setNoBorder(row1.getCell(3));

		// =====================================================
		// EMAIL
		// =====================================================

		XWPFTableRow row2 = table.getRow(2);

		setCellText(row2.getCell(1), "Email", 14, true);
		setCellText(row2.getCell(2), ":", 14, true);
		setCellText(row2.getCell(3), cmpEmail, 14, false);

		setNoBorder(row2.getCell(1));
		setNoBorder(row2.getCell(2));
		setNoBorder(row2.getCell(3));

		// Same gap as PDF
		XWPFParagraph gap = document.createParagraph();
		gap.setSpacingAfter(80);
	}

	private void addEventInfoTable(XWPFDocument document, MenuQuantityReponseDto eventData, Integer isPartyDetails,
			String customerName, String customerPhone, String eName, String eDate, String eVenue) {

		XWPFTable table = document.createTable(1, 6);

		table.setWidth("100%");

		setTableBorder(table);

		table.setCellMargins(50, 100, 50, 100);

		// Same proportions as PDF
		table.getRow(0).getCell(0).setWidth("2000");
		table.getRow(0).getCell(1).setWidth("300");
		table.getRow(0).getCell(2).setWidth("3500");

		table.getRow(0).getCell(3).setWidth("1800");
		table.getRow(0).getCell(4).setWidth("300");
		table.getRow(0).getCell(5).setWidth("2000");

		String clientName = eventData.getPartyName() == null ? "" : eventData.getPartyName();

		String clientMobile = eventData.getPartyMobile() == null ? "" : eventData.getPartyMobile();

		String eventName = eventData.getEventName() == null ? "" : eventData.getEventName();

		String eventDate = eventData.getEventDate() == null ? "" : eventData.getEventDate();

		String venue = eventData.getVenueName() == null ? "" : eventData.getVenueName();

		// ====================================================
		// ROW 1 : CUSTOMER DETAILS
		// ====================================================

		if (isPartyDetails != null && isPartyDetails == 1) {

			XWPFTableRow row1 = table.getRow(0);

			setCellText(row1.getCell(0), customerName, 14, true);
			setCellText(row1.getCell(1), ":", 14, true);
			setCellText(row1.getCell(2), clientName, 14, false);

			setCellText(row1.getCell(3), customerPhone, 14, true);
			setCellText(row1.getCell(4), ":", 14, true);
			setCellText(row1.getCell(5), clientMobile, 14, false);

			styleEventRow(row1);
		}

		// ====================================================
		// ROW 2 : EVENT NAME + DATE
		// ====================================================

		XWPFTableRow row2 = (isPartyDetails != null && isPartyDetails == 1) ? table.createRow() : table.getRow(0);

		setCellText(row2.getCell(0), eName, 14, true);
		setCellText(row2.getCell(1), ":", 14, true);
		setCellText(row2.getCell(2), eventName, 14, false);

		setCellText(row2.getCell(3), eDate, 14, true);
		setCellText(row2.getCell(4), ":", 14, true);
		setCellText(row2.getCell(5), eventDate, 14, false);

		styleEventRow(row2);

		// ====================================================
		// ROW 3 : VENUE
		// ====================================================

		XWPFTableRow row3 = table.createRow();

		setCellText(row3.getCell(0), eVenue, 14, true);
		setCellText(row3.getCell(1), ":", 14, true);
		setCellText(row3.getCell(2), venue, 14, false);

		setCellText(row3.getCell(3), "", 14, false);
		setCellText(row3.getCell(4), "", 14, false);
		setCellText(row3.getCell(5), "", 14, false);

		// Merge venue cell across remaining columns
		CTTcPr tcPr = row3.getCell(2).getCTTc().getTcPr();

		if (tcPr == null) {
			tcPr = row3.getCell(2).getCTTc().addNewTcPr();
		}

		tcPr.addNewGridSpan().setVal(BigInteger.valueOf(4));

		row3.removeCell(5);
		row3.removeCell(4);
		row3.removeCell(3);

		styleEventRow(row3);
	}

	private void styleEventRow(XWPFTableRow row) {

		for (XWPFTableCell cell : row.getTableCells()) {

			setNoBorder(cell);

			XWPFParagraph p = cell.getParagraphs().get(0);

			p.setSpacingBefore(0);
			p.setSpacingAfter(0);
		}
	}

	private void setTableBorder(XWPFTable table) {

		CTTblPr tblPr = table.getCTTbl().getTblPr();

		if (tblPr == null) {
			tblPr = table.getCTTbl().addNewTblPr();
		}

		CTTblBorders borders = tblPr.getTblBorders();

		if (borders == null) {
			borders = tblPr.addNewTblBorders();
		}

		setBorder(borders.isSetTop() ? borders.getTop() : borders.addNewTop());
		setBorder(borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom());
		setBorder(borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft());
		setBorder(borders.isSetRight() ? borders.getRight() : borders.addNewRight());

		CTBorder insideH = borders.isSetInsideH() ? borders.getInsideH() : borders.addNewInsideH();

		insideH.setVal(STBorder.NONE);

		CTBorder insideV = borders.isSetInsideV() ? borders.getInsideV() : borders.addNewInsideV();

		insideV.setVal(STBorder.NONE);
	}

	private void setBorder(CTBorder border) {

		border.setVal(STBorder.SINGLE);
		border.setSz(BigInteger.valueOf(8)); // 1pt
		border.setColor("000000");
	}

	private void addFunctionTable(XWPFDocument document, MenuQuantityReponseDto functionData, String functionLabel,
			String personLabel, String dateTime) {

		XWPFTable table = document.createTable(2, 6);

		table.setWidth("100%");

		setTableBorder(table);

		table.setCellMargins(50, 100, 50, 100);

		// Same proportions as PDF
		table.getRow(0).getCell(0).setWidth("1800");
		table.getRow(0).getCell(1).setWidth("300");
		table.getRow(0).getCell(2).setWidth("3500");

		table.getRow(0).getCell(3).setWidth("1500");
		table.getRow(0).getCell(4).setWidth("300");
		table.getRow(0).getCell(5).setWidth("1000");

		// =====================================================
		// ROW 1 : FUNCTION + PERSONS
		// =====================================================

		XWPFTableRow row1 = table.getRow(0);

		setCellText(row1.getCell(0), functionLabel, 14, true);
		setCellText(row1.getCell(1), ":", 14, true);
		setCellText(row1.getCell(2), nvl(functionData.getFunctionName()), 14, false);

		setCellText(row1.getCell(3), personLabel, 14, true);
		setCellText(row1.getCell(4), ":", 14, true);
		setCellText(row1.getCell(5), String.valueOf(functionData.getPerson() == null ? 0 : functionData.getPerson()),
				14, false);

		styleFunctionRow(row1);

		// =====================================================
		// ROW 2 : DATE & TIME
		// =====================================================

		XWPFTableRow row2 = table.getRow(1);

		setCellText(row2.getCell(0), dateTime, 14, true);
		setCellText(row2.getCell(1), ":", 14, true);

		String eventDate = functionData.getEventDate() == null ? "" : functionData.getEventDate();

		String functionTime = functionData.getFunctionTime() == null ? "" : functionData.getFunctionTime();

		setCellText(row2.getCell(2), eventDate + " " + functionTime, 14, false);

		// Merge remaining columns exactly like PDF
		CTTcPr tcPr = row2.getCell(2).getCTTc().getTcPr();

		if (tcPr == null) {
			tcPr = row2.getCell(2).getCTTc().addNewTcPr();
		}

		tcPr.addNewGridSpan().setVal(BigInteger.valueOf(4));

		row2.removeCell(5);
		row2.removeCell(4);
		row2.removeCell(3);

		styleFunctionRow(row2);

		// Gap after function box (same as PDF)
		XWPFParagraph gap = document.createParagraph();
		gap.setSpacingAfter(80);
	}

	private void styleFunctionRow(XWPFTableRow row) {

		for (XWPFTableCell cell : row.getTableCells()) {

			setNoBorder(cell);

			XWPFParagraph p = cell.getParagraphs().get(0);

			p.setSpacingBefore(0);
			p.setSpacingAfter(0);
		}
	}

	private String nvl(String value) {
		return value == null ? "" : value;
	}

	private void addMenuCategoryHeader(XWPFDocument document, String itemName, Integer catIndex) {

		XWPFParagraph p = document.createParagraph();

		// Same spacing as PDF
		p.setSpacingBefore(0);
		p.setSpacingAfter(0);
		p.setIndentationLeft(0);

		XWPFRun run = p.createRun();

		run.setBold(true);
		run.setFontFamily("Arial");
		run.setFontSize(14); // match PDF heading size
		run.setText(catIndex + " " + nvl(itemName));
	}

	private void addHorizontalLine(XWPFDocument document) {

		XWPFParagraph p = document.createParagraph();

		p.setSpacingBefore(0);
		p.setSpacingAfter(0);
		p.setSpacingBetween(1.0);
		p.setIndentationLeft(0);
		p.setIndentationRight(0);

		CTP ctp = p.getCTP();

		CTPPr pPr = ctp.getPPr();

		if (pPr == null) {
			pPr = ctp.addNewPPr();
		}

		CTPBdr borders = pPr.getPBdr();

		if (borders == null) {
			borders = pPr.addNewPBdr();
		}

		CTBorder bottom = borders.getBottom();

		if (bottom == null) {
			bottom = borders.addNewBottom();
		}

		bottom.setVal(STBorder.SINGLE);
		bottom.setSz(BigInteger.valueOf(8));
		bottom.setSpace(BigInteger.ZERO);
	}

	private void setCellText(XWPFTableCell cell, String value, int fontSize, boolean bold) {

		// Remove default paragraphs
		while (cell.getParagraphs().size() > 0) {
			cell.removeParagraph(0);
		}

		XWPFParagraph p = cell.addParagraph();

		p.setSpacingBefore(0);
		p.setSpacingAfter(0);
		p.setSpacingBetween(1.0);
		p.setIndentationLeft(0);

		XWPFRun run = p.createRun();

		run.setFontFamily("Arial");
		run.setFontSize(fontSize);
		run.setBold(bold);
		run.setText(value == null ? "" : value);

		cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
	}

	private void fillMaterialCell(XWPFTableRow row, int startIndex, int itemNumber, MenuQuantityReponseDto raw,
			Integer withQty, String outsourceName) {

		// now 4 cells per group: index, item, qty, unit
		while (row.getTableCells().size() < startIndex + 4) {
			row.addNewTableCell();
		}

//		XWPFTableCell indexCell = row.getCell(startIndex);
		XWPFTableCell itemCell = row.getCell(startIndex);
		XWPFTableCell qtyCell = row.getCell(startIndex + 1);
		XWPFTableCell unitCell = row.getCell(startIndex + 2);

//		setNoBorder(indexCell);
		setNoBorder(itemCell);
		setNoBorder(qtyCell);
		setNoBorder(unitCell);

		String qty = raw.getQty() == null ? "" : String.valueOf(raw.getQty());
		String unit = raw.getUnitSymbol() == null ? "" : raw.getUnitSymbol();
		String item = raw.getRawMaterialName() == null ? "" : raw.getRawMaterialName();

		if (Boolean.TRUE.equals(raw.getOutside())) {
			item += " ( " + outsourceName + " )";
		}

		int pdfFontSize = 12;

//		setCellText(indexCell, String.valueOf(itemNumber) + ".", pdfFontSize, false);
		setCellText(itemCell, item, pdfFontSize, false);

		if (withQty != null && withQty == 1) {
			setCellText(qtyCell, qty, pdfFontSize, false);
			setCellText(unitCell, unit, pdfFontSize, false);
		} else {
			setCellText(qtyCell, "", pdfFontSize, false);
			setCellText(unitCell, "", pdfFontSize, false);
		}

//		indexCell.getParagraphs().get(0).setAlignment(ParagraphAlignment.LEFT);
		itemCell.getParagraphs().get(0).setAlignment(ParagraphAlignment.LEFT);
		qtyCell.getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
		unitCell.getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

		// Match PDF column widths
		if (startIndex == 0 || startIndex == 4) {
//			indexCell.setWidth("400");
			itemCell.setWidth("2400");
			qtyCell.setWidth("600");
			unitCell.setWidth("500");
		} else {
//			indexCell.setWidth("350");
			itemCell.setWidth("2000");
			qtyCell.setWidth("500");
			unitCell.setWidth("400");
		}
	}

	private void add3ColumnRawMaterialTable(XWPFDocument document, List<MenuQuantityReponseDto> raws, Integer withQty,
			String outsourceName) {

		XWPFTable table = document.createTable();
		table.setWidth("100%");
		removeBorders(table);
		table.setCellMargins(0, 30, 0, 30);

		int index = 0;
		int itemNumber = 1;

		while (index < raws.size()) {

			XWPFTableRow row = index == 0 ? table.getRow(0) : table.createRow();

			// Column 1
			fillMaterialCell(row, 0, itemNumber++, raws.get(index), withQty, outsourceName);
			index++;

			// Column 2
			if (index < raws.size()) {
				fillMaterialCell(row, 4, itemNumber++, raws.get(index), withQty, outsourceName);
				index++;
			}

			// Column 3
			if (index < raws.size()) {
				fillMaterialCell(row, 8, itemNumber++, raws.get(index), withQty, outsourceName);
				index++;
			}

			for (XWPFTableCell cell : row.getTableCells()) {
				XWPFParagraph p = cell.getParagraphs().get(0);
				p.setSpacingBefore(0);
				p.setSpacingAfter(0);
				p.setSpacingBetween(1.0);
			}
		}
	}

	private void add2ColumnRawMaterialTable(XWPFDocument document, List<MenuQuantityReponseDto> raws, Integer withQty,
			String outsourceName) {

		XWPFTable table = document.createTable();
		table.setWidth("100%");
		removeBorders(table);
		table.setCellMargins(0, 40, 0, 40);

		int index = 0;
		int itemNumber = 1;

		while (index < raws.size()) {

			XWPFTableRow row = index == 0 ? table.getRow(0) : table.createRow();

			// First column
			fillMaterialCell(row, 0, itemNumber++, raws.get(index), withQty, outsourceName);
			index++;

			// Second column
			if (index < raws.size()) {
				fillMaterialCell(row, 4, itemNumber++, raws.get(index), withQty, outsourceName);
				index++;
			}

			for (XWPFTableCell cell : row.getTableCells()) {
				XWPFParagraph p = cell.getParagraphs().get(0);
				p.setSpacingBefore(0);
				p.setSpacingAfter(0);
				p.setSpacingBetween(1.0);
			}
		}
	}

	private void addFooter(XWPFDocument document) {

		CTSectPr sectPr = document.getDocument().getBody().isSetSectPr() ? document.getDocument().getBody().getSectPr()
				: document.getDocument().getBody().addNewSectPr();

		XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

		XWPFFooter footer = policy.createFooter(STHdrFtr.Enum.forString("default"));

		XWPFTable table = footer.createTable(1, 3);

		table.setWidth("100%");

		removeBorders(table);

		// ==========================
		// LEFT
		// ==========================

		XWPFParagraph left = table.getRow(0).getCell(0).getParagraphs().get(0);

		left.setAlignment(ParagraphAlignment.LEFT);
		left.setSpacingBefore(0);
		left.setSpacingAfter(0);
		left.setSpacingBetween(1.0);

		XWPFRun leftRun = left.createRun();

		leftRun.setFontFamily("Arial");
		leftRun.setFontSize(10);
		leftRun.setText("Prepared By __________");

		// ==========================
		// CENTER
		// ==========================

		XWPFParagraph center = table.getRow(0).getCell(1).getParagraphs().get(0);

		center.setAlignment(ParagraphAlignment.CENTER);
		center.setSpacingBefore(0);
		center.setSpacingAfter(0);
		center.setSpacingBetween(1.0);

		XWPFRun pageText = center.createRun();

		pageText.setFontFamily("Arial");
		pageText.setFontSize(10);
		pageText.setText("Page ");

		addPageField(center);

		XWPFRun ofRun = center.createRun();

		ofRun.setFontFamily("Arial");
		ofRun.setFontSize(10);
		ofRun.setText(" of ");

		addTotalPagesField(center);

		// ==========================
		// RIGHT
		// ==========================

		XWPFParagraph right = table.getRow(0).getCell(2).getParagraphs().get(0);

		right.setAlignment(ParagraphAlignment.RIGHT);
		right.setSpacingBefore(0);
		right.setSpacingAfter(0);
		right.setSpacingBetween(1.0);

		XWPFRun rightRun = right.createRun();

		rightRun.setFontFamily("Arial");
		rightRun.setFontSize(10);
		rightRun.setText("Submitted By __________");
	}

	private void addPageField(XWPFParagraph paragraph) {

		XWPFRun run = paragraph.createRun();

		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
		run.getCTR().addNewInstrText().setStringValue(" PAGE ");
		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
	}

	private void addTotalPagesField(XWPFParagraph paragraph) {

		XWPFRun run = paragraph.createRun();

		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
		run.getCTR().addNewInstrText().setStringValue(" NUMPAGES ");
		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
	}

	private void removeBorders(XWPFTable table) {

		CTTblPr pr = table.getCTTbl().getTblPr();

		if (pr == null) {
			pr = table.getCTTbl().addNewTblPr();
		}

		CTTblBorders borders = pr.getTblBorders();

		if (borders == null) {
			borders = pr.addNewTblBorders();
		}

		removeBorder(borders.isSetTop() ? borders.getTop() : borders.addNewTop());
		removeBorder(borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom());
		removeBorder(borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft());
		removeBorder(borders.isSetRight() ? borders.getRight() : borders.addNewRight());
		removeBorder(borders.isSetInsideH() ? borders.getInsideH() : borders.addNewInsideH());
		removeBorder(borders.isSetInsideV() ? borders.getInsideV() : borders.addNewInsideV());
	}

	private void removeBorder(CTBorder border) {

		border.setVal(STBorder.NONE);
	}

	private void addRawMaterialTable(XWPFDocument document, Long eventId, Long eventFunctionId, Long itemId,
			Integer is3Column, Integer withQty, Integer lang, String outsourceName) {

		List<MenuQuantityReponseDto> rawMaterials = menuItemRawMaterialServiceImpl
				.getFunctionItemRawMaterialDetails(eventId, eventFunctionId, itemId, lang);

		if (rawMaterials == null || rawMaterials.isEmpty()) {
			return;
		}

		if (is3Column != null && is3Column == 1) {

			add3ColumnRawMaterialTable(document, rawMaterials, withQty, outsourceName);

		} else {

			add2ColumnRawMaterialTable(document, rawMaterials, withQty, outsourceName);
		}
	}

	@Override
	public String menuReportWithQuantityRidhhiSidhhi(Long eventId, Long eventFunctionId, HttpServletRequest re,
			int lang, Long userid, Integer withQty, Integer isUserDetails, Integer isPartyDetails, Integer is3Column,
			Integer is5Column) {

		try {

			PdfFont basicFont = null;
			PdfFont itemFont = null;
			PdfFont boldFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "",
					personLabel = "", note = "", functionLabel = "", dateTime = "", outsoucename = "";
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				itemFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थान";
				functionLabel = "कार्यक्रम";
				dateTime = "दिनांक एवं समय";
				personLabel = "मेम्बर्स";
				note = "नोट";
				outsoucename = "आउटसोर्स";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				itemFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				customerName = "ગ્રાહકનું નામ";
				customerPhone = "મોબાઇલ નંબર";
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				fNotes = "ભોજન વિગતો";
				eVenue = "આયોજન સ્થળ";
				functionLabel = "કાર્યક્રમ";
				dateTime = "તારીખ અને સમય";
				personLabel = "વ્યક્તિ";
				note = "નોંધ";
				outsoucename = "ઓઉટસોઉર્સ";
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				itemFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				functionLabel = "Function";
				personLabel = "PERSONS";
				dateTime = "Date & Time";
				note = "Note";
				outsoucename = "Outsource";
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

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

			String reportType = "menuwithquantity";
			String title = "Menu With Quantity Report";
			if (withQty == 0) {
				reportType = "menuwithoutquantity";
				title = "Menu Without Quantity Report";
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(40, 20, 25, 20);

			List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl.getAllFunctionDetails(eventId,
					eventFunctionId, lang);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpCountryCode = eventData.getCountryCode() == null || eventData.getCountryCode().isEmpty() ? ""
					: eventData.getCountryCode();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String logo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(100);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("Mobile No.")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text(cmpCountryCode + " " + cmpMobile)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isUserDetails == 1) {
				document.add(cmpHead);
			}

			Paragraph heading = new Paragraph().add(
					new Text(title).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(0f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			float[] cloumnWidthEvent = { 25f, 1f, 30f, 20f, 1f, 20f };
			Table eventDataTable = new Table(UnitValue.createPercentArray(cloumnWidthEvent));
			eventDataTable.setMarginBottom(8f);
			eventDataTable.setBorder(new SolidBorder(1f));

			String clientName = eventData.getPartyName() == null || eventData.getPartyName().isEmpty() ? ""
					: eventData.getPartyName();
			String venue = eventData.getVenueName() == null || eventData.getVenueName().isEmpty() ? ""
					: eventData.getVenueName();
			String clientMobile = eventData.getPartyMobile() == null || eventData.getPartyMobile().isEmpty() ? ""
					: eventData.getPartyMobile();
			String eventName = eventData.getEventName() == null || eventData.getEventName().isEmpty() ? ""
					: eventData.getEventName();
			String eventDate = eventData.getEventDate() == null || eventData.getEventDate().isEmpty() ? ""
					: eventData.getEventDate();

			Paragraph event = new Paragraph();
			Cell eventCell = new Cell();
			// 1st row
			if (isPartyDetails == 1) {
				event = new Paragraph().add(new Text(customerName).setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setPaddingLeft(10);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(clientName).setFontSize(14f).setFont(basicFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(customerPhone).setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(clientMobile).setFontSize(14f).setFont(basicFont));
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);
			}
			// 2ed row
			event = new Paragraph().add(new Text(eName).setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventName).setFontSize(14f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eDate).setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventDate).setFontSize(14f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			// 3rd row
			event = new Paragraph().add(new Text(eVenue).setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(venue).setFontSize(14f).setFont(basicFont));
			eventCell = new Cell(3, 4).add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			document.add(eventDataTable);

			for (int k = 0; k < allFunctions.size(); k++) {

				MenuQuantityReponseDto fun = allFunctions.get(k);

				Long functionId = fun.getEventFunctionId();
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();
				Integer person = fun.getPerson();
				String functionTime = fun.getFunctionTime() == null || fun.getFunctionTime().isEmpty() ? ""
						: fun.getFunctionTime();

				float[] columnWidth = { 20f, 1f, 44f, 15f, 1f, 20f };
				Table function = new Table(UnitValue.createPercentArray(columnWidth));
				function.setWidth(UnitValue.createPercentValue(100));
				function.setBorder(new SolidBorder(Border.SOLID));

				Paragraph label = new Paragraph().add(new Text(functionLabel).setFont(boldFont).setFontSize(14));
				Paragraph saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14));
				Paragraph name = new Paragraph().add(new Text(functionName).setFont(basicFont).setFontSize(14));

				Cell cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(personLabel).setFont(boldFont).setFontSize(14));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14));
				name = new Paragraph().add(new Text(person.toString()).setFont(basicFont).setFontSize(14));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(dateTime).setFont(boldFont).setFontSize(14));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(14));
				name = new Paragraph().add(new Text(functionTime).setFont(basicFont).setFontSize(14));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell(3, 6).add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				document.add(function);

				List<MenuQuantityReponseDto> allItems = menuItemRawMaterialServiceImpl.getFunctionItemDetails(eventId,
						functionId, lang);

				int index = 0;
				for (MenuQuantityReponseDto item : allItems) {

					Div itemBlock = new Div();
					itemBlock.setKeepTogether(true);

					String itemName = item.getItemName() == null || item.getItemName().isEmpty() ? ""
							: item.getItemName();

					Long itemId = item.getMenuItemId();
					Boolean outside = item.getOutside();

					String instruction = item.getInstructions() == null || item.getInstructions().isEmpty() ? ""
							: item.getInstructions();
					if (!instruction.trim().isEmpty()) {
						itemName = itemName + " (" + instruction + " )";
					}
					if (outside) {
						itemName = itemName + " ( " + outsoucename + " )";
					}

					// ---- item title with running number (kept as before) ----
					name = new Paragraph()
							.add(new Text(++index + ". ")
									.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16))
							.add(new Text(itemName).setFont(boldFont).setFontSize(16)).setPaddingLeft(20);
					itemBlock.add(name);

					// ---- top underline separator (kept as before) ----
					ILineDrawer line = new SolidLine(1f);
					LineSeparator separator = new LineSeparator(line);
					separator.setMarginTop(-5f);
					separator.setMarginBottom(5f);
					separator.setWidth(UnitValue.createPercentValue(100));
//					itemBlock.add(separator);

					// =========================================================
					// NEW LAYOUT: raw materials rendered as
					// [name] [name] [name] ... <- header row
					// [qty unit] [qty unit] ... <- value row
					// wrapping to a new name/value block every `maxCols` items.
					// Column count is driven by is3Column / is5Column, default 2.
					// =========================================================
					if (outside == false && withQty != null) {

						int maxCols;
						if (is5Column != null && is5Column == 1) {
							maxCols = 5;
						} else if (is3Column != null && is3Column == 1) {
							maxCols = 3;
						} else {
							maxCols = 2;
						}

						float[] rawColumnWidths = new float[maxCols];
						for (int c = 0; c < maxCols; c++) {
							rawColumnWidths[c] = 100f / maxCols;
						}

						List<MenuQuantityReponseDto> allRawMaterialItems = menuItemRawMaterialServiceImpl
								.getFunctionItemRawMaterialDetails(eventId, functionId, itemId, lang);

						float nameFontSize = maxCols >= 5 ? 12f : (maxCols == 3 ? 13f : 15f);
						float qtyFontSize = maxCols >= 5 ? 10f : (maxCols == 3 ? 11f : 13f);

						Table rawMaterial = new Table(UnitValue.createPercentArray(rawColumnWidths));
						rawMaterial.setWidth(UnitValue.createPercentValue(100));
						rawMaterial.setHorizontalAlignment(HorizontalAlignment.CENTER);
						rawMaterial.setMarginBottom(5f);

						int col = 0;
						List<Cell> nameRowCells = new ArrayList<>();
						List<Cell> qtyRowCells = new ArrayList<>();

						for (int i = 0; i < allRawMaterialItems.size(); i++) {
							MenuQuantityReponseDto rm = allRawMaterialItems.get(i);

							Double qty = rm.getQty() == null ? 0 : rm.getQty();
							String unit = rm.getUnitSymbol() == null || rm.getUnitSymbol().isEmpty() ? ""
									: rm.getUnitSymbol();
							String rawmaterial = rm.getRawMaterialName() == null || rm.getRawMaterialName().isEmpty()
									? ""
									: rm.getRawMaterialName();

							// Name cell (top row)
							Paragraph nameP = new Paragraph()
									.add(new Text(rawmaterial).setFont(basicFont).setFontSize(nameFontSize))
									.setMargin(0).setMultipliedLeading(1);
							Cell nameCell = new Cell().add(nameP).setBorder(Border.NO_BORDER)
									.setTextAlignment(TextAlignment.LEFT).setPadding(2f).setMarginLeft(15f)
									.setPaddingBottom(4f);
							nameRowCells.add(nameCell);

							// Qty+unit cell (bottom row) - only if withQty requested
							Paragraph qtyP;
							if (withQty == 1) {
								qtyP = new Paragraph().add(new Text(qty.toString() + " " + unit).setFont(basicFont)
										.setFontSize(qtyFontSize)).setMarginLeft(5f).setMultipliedLeading(1);
							} else {
								qtyP = new Paragraph().add(new Text("").setFont(basicFont).setFontSize(qtyFontSize));
							}
							Cell qtyCell = new Cell().add(qtyP).setBorder(Border.NO_BORDER)
									.setTextAlignment(TextAlignment.CENTER).setPadding(2f);
							qtyRowCells.add(qtyCell);

							col++;

							boolean isLastItem = (i == allRawMaterialItems.size() - 1);

							if (col == maxCols || isLastItem) {
								// pad remaining columns with empty cells so the row completes
								while (col < maxCols) {
									nameRowCells.add(new Cell().setBorder(Border.NO_BORDER));
									qtyRowCells.add(new Cell().setBorder(Border.NO_BORDER));
									col++;
								}
								for (Cell c : nameRowCells) {
									rawMaterial.addCell(c);
								}
								for (Cell c : qtyRowCells) {
									rawMaterial.addCell(c);
								}
								nameRowCells = new ArrayList<>();
								qtyRowCells = new ArrayList<>();
								col = 0;
							}
						}

						itemBlock.add(rawMaterial);
					}

					// ---- bottom underline separator (kept as before) ----
					separator = new LineSeparator(line);
					separator.setMarginTop(0);
					separator.setMarginBottom(-5f);
					separator.setWidth(UnitValue.createPercentValue(100));
					itemBlock.add(separator);

					document.add(itemBlock);
				}

				if (k + 1 < allFunctions.size()) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
			}

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 5, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}

	/**
	 * DOCX counterpart of menuReportWithQuantity(...) /
	 * menuReportWithQuantityRidhhiSidhhi(...). Same conditions/logic as the PDF
	 * version: - lang: 1 = Hindi, 2 = Gujarati, else English (labels + font family)
	 * - isUserDetails: show/hide company header block - isPartyDetails: show/hide
	 * customer name/mobile row - is3Column / is5Column / (neither -> default 2):
	 * raw material grid column count - withQty: 1 = show qty+unit row under
	 * material names, 0 = names only - Numbering ("1. ItemName"), top/bottom
	 * separator lines, and the gap between the name row and qty row (and between
	 * wrapped blocks) are preserved.
	 *
	 * NOTE ON FONTS: Word resolves font-family names against fonts installed on the
	 * machine that opens/renders the document (there is no TTF embedding here like
	 * iText's PdfFontFactory). Adjust FONT_HINDI / FONT_GUJARATI below to match
	 * fonts actually installed on your users' machines if "Nirmala UI" / "Shruti"
	 * aren't right.
	 */
	@Override
	public String menuReportWithQuantityRidhhiSidhhiDocx(Long eventId, Long eventFunctionId, HttpServletRequest re,
			int lang, Long userid, Integer withQty, Integer isUserDetails, Integer isPartyDetails, Integer is3Column,
			Integer is5Column) {

		final String FONT_HINDI = "Nirmala UI";
		final String FONT_GUJARATI = "Shruti"; // swap for "Noto Sans Gujarati" if that's installed instead
		final String FONT_ENGLISH = "Calibri";

		try {

			String fontFamily;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "",
					personLabel = "", note = "", functionLabel = "", dateTime = "", outsoucename = "";

			if (lang == 1) {
				// Hindi
				fontFamily = FONT_HINDI;
				customerName = "नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "दिनांक";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थान";
				functionLabel = "कार्यक्रम";
				dateTime = "दिनांक एवं समय";
				personLabel = "मेम्बर्स";
				note = "नोट";
				outsoucename = "आउटसोर्स";
			} else if (lang == 2) {
				// Gujarati
				fontFamily = FONT_GUJARATI;
				customerName = "ગ્રાહકનું નામ";
				customerPhone = "મોબાઇલ નંબર";
				eName = "કાર્યક્રમનું નામ";
				eDate = "કાર્યક્રમની તારીખ";
				fNotes = "ભોજન વિગતો";
				eVenue = "આયોજન સ્થળ";
				functionLabel = "કાર્યક્રમ";
				dateTime = "તારીખ અને સમય";
				personLabel = "વ્યક્તિ";
				note = "નોંધ";
				outsoucename = "ઓઉટસોઉર્સ";
			} else {
				// English
				fontFamily = FONT_ENGLISH;
				customerName = "Customer Name";
				customerPhone = "Mobile No.";
				eName = "Event Name";
				eDate = "Event Date";
				fNotes = "Food Note";
				eVenue = "Venue";
				functionLabel = "Function";
				personLabel = "PERSONS";
				dateTime = "Date & Time";
				note = "Note";
				outsoucename = "Outsource";
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

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

			String title = "Menu With Quantity Report";
			if (withQty == 0) {
				title = "Menu Without Quantity Report";
			}

			File docxFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".docx");

			XWPFDocument document = new XWPFDocument();

			// keep page margins similar to PDF's 40/20/25/20 (twips: 1440 per inch, 20
			// twips per point)
			setPageMargins(document, 800, 400, 500, 400);

			List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl.getAllFunctionDetails(eventId,
					eventFunctionId, lang);

			// ================= company header =================
			if (isUserDetails == 1) {
				addCompanyHeader(document, eventData);
			}

			// ================= title =================
			XWPFParagraph heading = document.createParagraph();
			heading.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun headingRun = heading.createRun();
			headingRun.setText(title);
			headingRun.setBold(true);
			headingRun.setFontSize(18);
			headingRun.setFontFamily(FONT_ENGLISH);

			// ================= event details table =================
			String clientName = nz(eventData.getPartyName());
			String venue = nz(eventData.getVenueName());
			String clientMobile = nz(eventData.getPartyMobile());
			String eventName = nz(eventData.getEventName());
			String eventDate = nz(eventData.getEventDate());

			XWPFTable eventTable = document.createTable(isPartyDetails == 1 ? 3 : 2, 3);
			setTableBorders(eventTable, 4);
			eventTable.setWidth("100%");

			int r = 0;
			if (isPartyDetails == 1) {
				setLabelValueRow(eventTable, r++, customerName, clientName, customerPhone, clientMobile, fontFamily);
			}
			setLabelValueRow(eventTable, r++, eName, eventName, eDate, eventDate, fontFamily);
			setLabelValueRow(eventTable, r, eVenue, venue, null, null, fontFamily);

			addSpacer(document, 8);

			// ================= functions =================
			for (int k = 0; k < allFunctions.size(); k++) {

				MenuQuantityReponseDto fun = allFunctions.get(k);

				Long functionId = fun.getEventFunctionId();
				String functionName = nz(fun.getFunctionName());
				Integer person = fun.getPerson();
				String functionTime = nz(fun.getFunctionTime());

				XWPFTable functionTable = document.createTable(1, 3);
				setTableBorders(functionTable, 4);
				functionTable.setWidth("100%");

				setCellLabelColonValue(functionTable.getRow(0).getCell(0), functionLabel, functionName, fontFamily,
						true);
				setCellLabelColonValue(functionTable.getRow(0).getCell(1), personLabel,
						person == null ? "" : person.toString(), fontFamily, true);
				setCellLabelColonValue(functionTable.getRow(0).getCell(2), dateTime, functionTime, fontFamily, true);

				addSpacer(document, 4);

				List<MenuQuantityReponseDto> allItems = menuItemRawMaterialServiceImpl.getFunctionItemDetails(eventId,
						functionId, lang);

				int index = 0;
				for (MenuQuantityReponseDto item : allItems) {

					String itemName = nz(item.getItemName());
					Long itemId = item.getMenuItemId();
					Boolean outside = item.getOutside();

					String instruction = nz(item.getInstructions());
					if (!instruction.trim().isEmpty()) {
						itemName = itemName + " (" + instruction + " )";
					}
					if (outside != null && outside) {
						itemName = itemName + " ( " + outsoucename + " )";
					}

					// ---- item title with running number ----
					XWPFParagraph itemPara = document.createParagraph();
					itemPara.setIndentationLeft(280); // ~ padding-left 20 in PDF
					XWPFRun numRun = itemPara.createRun();
					numRun.setText((++index) + ". ");
					numRun.setBold(true);
					numRun.setFontSize(16);
					numRun.setFontFamily(FONT_ENGLISH);
					XWPFRun itemNameRun = itemPara.createRun();
					itemNameRun.setText(itemName);
					itemNameRun.setBold(true);
					itemNameRun.setFontSize(16);
					itemNameRun.setFontFamily(fontFamily);

					// ---- top underline separator ----
//					addHorizontalLine(itemPara, 6);

					// =========================================================
					// raw material grid: name row, then qty+unit row, wrapping
					// every maxCols items. Same column-count rule as PDF version.
					// =========================================================
					if ((outside == null || !outside) && withQty != null) {

						int maxCols;
						if (is5Column != null && is5Column == 1) {
							maxCols = 5;
						} else if (is3Column != null && is3Column == 1) {
							maxCols = 3;
						} else {
							maxCols = 2;
						}

						List<MenuQuantityReponseDto> allRawMaterialItems = menuItemRawMaterialServiceImpl
								.getFunctionItemRawMaterialDetails(eventId, functionId, itemId, lang);

						int nameFontSize = maxCols >= 5 ? 12 : (maxCols == 3 ? 13 : 15);
						int qtyFontSize = maxCols >= 5 ? 10 : (maxCols == 3 ? 11 : 13);

						int rowsNeeded = (int) Math.ceil(allRawMaterialItems.size() / (double) maxCols) * 2;
						if (rowsNeeded == 0) {
							rowsNeeded = 0;
						}

						if (!allRawMaterialItems.isEmpty()) {
							XWPFTable rawTable = document.createTable(rowsNeeded, maxCols);
							removeTableBorders(rawTable);
							rawTable.setWidth("100%");

							int col = 0;
							int rowPair = 0; // 0 = name row, 1 = qty row, advances by 2 each block

							for (int i = 0; i < allRawMaterialItems.size(); i++) {
								MenuQuantityReponseDto rm = allRawMaterialItems.get(i);

								Double qty = rm.getQty() == null ? 0 : rm.getQty();
								String unit = nz(rm.getUnitSymbol());
								String rawmaterial = nz(rm.getRawMaterialName());

								XWPFTableCell nameCell = rawTable.getRow(rowPair).getCell(col);
								setCellText(nameCell, rawmaterial, fontFamily, nameFontSize, false,
										ParagraphAlignment.LEFT);
								setCellBottomMargin(nameCell, 80); // gap between name row and qty row
								setCellLeftMargin(nameCell, 250);

								XWPFTableCell qtyCell = rawTable.getRow(rowPair + 1).getCell(col);
								String qtyText = withQty == 1 ? (qty.toString() + " " + unit) : "";
								setCellText(qtyCell, qtyText, fontFamily, qtyFontSize, false,
										ParagraphAlignment.CENTER);
								setCellBottomMargin(qtyCell, 160); // gap before next wrapped block
//								setCellLeftMargin(nameCell, 150);
								col++;
								if (col == maxCols) {
									col = 0;
									rowPair += 2;
								}
							}
						}
					}

					// ---- bottom underline separator ----
					XWPFParagraph bottomLinePara = document.createParagraph();
					addHorizontalLine(bottomLinePara, 0);

					addSpacer(document, 4);
				}

				if (k + 1 < allFunctions.size()) {
					XWPFParagraph pageBreak = document.createParagraph();
					pageBreak.createRun().addBreak(BreakType.PAGE);
				}
			}

			try (FileOutputStream out = new FileOutputStream(docxFile)) {
				document.write(out);
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/docx/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".docx";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}
	}

	@Override
	public String generateRawMaterialReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid) {
		try {

			PdfFont basicFont = null;
			PdfFont itemFont = null;
			PdfFont boldFont = null;
			
			String rawItemName = "", weightLabel = "";
			
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				itemFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				rawItemName = "नाम";
				weightLabel = "मात्रा";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				itemFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				rawItemName = "ગ્રાહકનું નામ";
				weightLabel = "જથ્થો";
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				itemFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				rawItemName = "Name";
				weightLabel = "Qty";
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

			List<MenuItemRawMaterialCategoryResponseDto> rawMaterials = getAllRawMaterial(eventId, eventFunctionId, lang);

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

			String title = "Raw Material Report";

			File pdfFile = new File(outputPath + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			Document document = new Document(pdfDocument);
			document.setMargins(40, 20, 25, 20);

			String venue = eventData.getVenueName() != null ? eventData.getVenueName() : "";
			String eventDate = eventData.getEventDate() != null ? eventData.getEventDate() : "";

			Table headerTable = createHeaderTable(basicFont, boldFont, (lang == 0 ? venue.toUpperCase() : venue),
					eventDate, "", lang);

			float leftMargin = 40;
			float rightMargin = 40;
			float topMargin = 20;
			float top = PageSize.A4.getTop() - topMargin;
			float bottom = PageSize.A4.getBottom() + 60;
			float height = top - bottom - rawMaterialReportServiceImpl.calculateTableHeight(headerTable, pdfDocument);
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = usableWidth / 2;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
					new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			MenuItemRawMaterialHeaderEventHandler headerHandler = new MenuItemRawMaterialHeaderEventHandler(headerTable,
					20f);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

			Boolean isFirstCategory = true;
			for (MenuItemRawMaterialCategoryResponseDto rawCategory : rawMaterials) {
				headerTable = createHeaderTable(basicFont, boldFont, (lang == 0 ? venue.toUpperCase() : venue),
						eventDate, rawCategory.getRawMaterialCatName(), lang);
				headerHandler.updateHeader(headerTable);
				
				if (!isFirstCategory) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirstCategory = false;
				
				Table table = new Table(UnitValue.createPercentArray(new float[] { 60f, 40f }), false);
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginBottom(12);
				table.addHeaderCell(headerCell(rawItemName, boldFont));
				table.addHeaderCell(headerCell(weightLabel, boldFont));
				
				for (MenuItemRawMaterialResponseDto item : rawCategory.getRawMaterials()) {
					String rmName = item.getRawMaterial();

					if (lang == 0) {
						rmName = rmName.toUpperCase();
					}

					table.addCell(dataCell(rmName, basicFont));
					table.addCell(dataCell(item.getWeight() + " " + item.getUnitName().toLowerCase(), basicFont)
							.setTextAlignment(TextAlignment.RIGHT));
				}
				
				document.add(table);
			}

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 5, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()), title)
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}
	}

	// ================================================================
	// Helper methods
	// ================================================================

	private String nz(String s) {
		return s == null || s.isEmpty() ? "" : s;
	}

	private void setCellLeftMargin(XWPFTableCell cell, int twips) {
		CTTc ctTc = cell.getCTTc();
		CTTcPr tcPr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
		CTTcMar tcMar = tcPr.isSetTcMar() ? tcPr.getTcMar() : tcPr.addNewTcMar();
		CTTblWidth left = tcMar.isSetLeft() ? tcMar.getLeft() : tcMar.addNewLeft();
		left.setW(BigInteger.valueOf(twips));
		left.setType(STTblWidth.DXA);
	}

	private void setPageMargins(XWPFDocument document, int top, int right, int bottom, int left) {
		CTSectPr sectPr = document.getDocument().getBody().isSetSectPr() ? document.getDocument().getBody().getSectPr()
				: document.getDocument().getBody().addNewSectPr();
		CTPageMar pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
		pageMar.setTop(BigInteger.valueOf(top));
		pageMar.setRight(BigInteger.valueOf(right));
		pageMar.setBottom(BigInteger.valueOf(bottom));
		pageMar.setLeft(BigInteger.valueOf(left));
	}

	private void addCompanyHeader(XWPFDocument document, MenuQuantityReponseDto eventData) throws Exception {

		String cmpName = nz(eventData.getCompanyName());
		String cmpEmail = nz(eventData.getCompanyEmail());
		String cmpCountryCode = nz(eventData.getCountryCode());
		String cmpMobile = nz(eventData.getOfficeNo());
		String logo = nz(eventData.getLogo());

		XWPFTable headerTable = document.createTable(3, 4);
		removeTableBorders(headerTable);
		headerTable.setWidth("100%");

		// logo cell, merged across the 3 rows in column 0
		XWPFTableCell logoCell = headerTable.getRow(0).getCell(0);
		try {
			String imageUrl = environment.getProperty("app.image.url") + logo;
			try (InputStream imgStream = new URL(imageUrl).openStream()) {
				XWPFParagraph logoPara = logoCell.getParagraphs().get(0);
				XWPFRun logoRun = logoPara.createRun();
				logoRun.addPicture(imgStream, XWPFDocument.PICTURE_TYPE_PNG, "logo.png", Units.toEMU(100),
						Units.toEMU(60));
			}
		} catch (Exception imgEx) {
			System.out.println("Logo image could not be loaded: " + imgEx.getMessage());
		}
		mergeCellsVertically(headerTable, 0, 0, 2);

		// company name spans columns 1-3 of row 0
		setCellText(headerTable.getRow(0).getCell(1), cmpName, "Calibri", 18, true, ParagraphAlignment.LEFT);
		mergeCellsHorizontally(headerTable, 0, 1, 3);

		// mobile row
		setCellText(headerTable.getRow(1).getCell(1), "Mobile No.", "Calibri", 14, true, ParagraphAlignment.LEFT);
		setCellText(headerTable.getRow(1).getCell(2), ":", "Calibri", 14, true, ParagraphAlignment.LEFT);
		setCellText(headerTable.getRow(1).getCell(3), (cmpCountryCode + " " + cmpMobile).trim(), "Calibri", 14, false,
				ParagraphAlignment.LEFT);

		// email row
		setCellText(headerTable.getRow(2).getCell(1), "Email", "Calibri", 14, true, ParagraphAlignment.LEFT);
		setCellText(headerTable.getRow(2).getCell(2), ":", "Calibri", 14, true, ParagraphAlignment.LEFT);
		setCellText(headerTable.getRow(2).getCell(3), cmpEmail, "Calibri", 14, false, ParagraphAlignment.LEFT);
	}

	private void setLabelValueRow(XWPFTable table, int rowIndex, String label1, String value1, String label2,
			String value2, String fontFamily) {
		XWPFTableRow row = table.getRow(rowIndex);
		setCellLabelColonValue(row.getCell(0), label1, value1, fontFamily, true);
		if (label2 != null) {
			setCellLabelColonValue(row.getCell(1), label2, value2, fontFamily, true);
		}
	}

	private void setCellLabelColonValue(XWPFTableCell cell, String label, String value, String fontFamily,
			boolean paddingLeft) {
		XWPFParagraph para = cell.getParagraphs().get(0);
		para.setIndentationLeft(paddingLeft ? 140 : 0);
		XWPFRun labelRun = para.createRun();
		labelRun.setText(label + " : ");
		labelRun.setBold(true);
		labelRun.setFontSize(14);
		labelRun.setFontFamily(fontFamily);
		XWPFRun valueRun = para.createRun();
		valueRun.setText(value == null ? "" : value);
		valueRun.setFontSize(14);
		valueRun.setFontFamily(fontFamily);
	}

	private void setCellText(XWPFTableCell cell, String text, String fontFamily, int fontSize, boolean bold,
			ParagraphAlignment alignment) {
		XWPFParagraph para = cell.getParagraphs().get(0);
		para.setAlignment(alignment);
		XWPFRun run = para.createRun();
		run.setText(text == null ? "" : text);
		run.setBold(bold);
		run.setFontSize(fontSize);
		run.setFontFamily(fontFamily);
	}

	/**
	 * Adds bottom cell margin (in twips) to create a gap below the cell's content.
	 */
	private void setCellBottomMargin(XWPFTableCell cell, int twips) {
		CTTc ctTc = cell.getCTTc();
		CTTcPr tcPr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
		CTTcMar tcMar = tcPr.isSetTcMar() ? tcPr.getTcMar() : tcPr.addNewTcMar();
		CTTblWidth bottom = tcMar.isSetBottom() ? tcMar.getBottom() : tcMar.addNewBottom();
		bottom.setW(BigInteger.valueOf(twips));
		bottom.setType(STTblWidth.DXA);
	}

	private void addSpacer(XWPFDocument document, int fontSize) {
		XWPFParagraph spacer = document.createParagraph();
		XWPFRun r = spacer.createRun();
		r.setFontSize(fontSize <= 4 ? 4 : fontSize);
	}

	/**
	 * Draws a horizontal rule under the given paragraph (equivalent to the PDF's
	 * LineSeparator).
	 */
	private void addHorizontalLine(XWPFParagraph paragraph, int spacingAfterTwips) {
		CTPPr pPr = paragraph.getCTP().isSetPPr() ? paragraph.getCTP().getPPr() : paragraph.getCTP().addNewPPr();
		CTPBdr border = pPr.isSetPBdr() ? pPr.getPBdr() : pPr.addNewPBdr();
		CTBorder bottom = border.isSetBottom() ? border.getBottom() : border.addNewBottom();
		bottom.setVal(STBorder.SINGLE);
		bottom.setSz(BigInteger.valueOf(6));
		bottom.setSpace(BigInteger.valueOf(1));
		bottom.setColor("000000");
		if (spacingAfterTwips > 0) {
			paragraph.setSpacingAfter(spacingAfterTwips);
		}
	}

	private void setTableBorders(XWPFTable table, int size) {
		table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, size, 0, "000000");
		table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, size, 0, "000000");
		table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, size, 0, "000000");
		table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, size, 0, "000000");
		table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, size, 0, "000000");
		table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, size, 0, "000000");
	}

	private void removeTableBorders(XWPFTable table) {
		table.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
		table.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
		table.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
		table.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
		table.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
		table.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
	}

	private void mergeCellsHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
		for (int colIndex = fromCol; colIndex <= toCol; colIndex++) {
			XWPFTableCell cell = table.getRow(row).getCell(colIndex);
			CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
			CTHMerge hMerge = tcPr.addNewHMerge();
			hMerge.setVal(colIndex == fromCol ? STMerge.RESTART : STMerge.CONTINUE);
		}
	}

	private void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
			CTVMerge vMerge = tcPr.addNewVMerge();
			vMerge.setVal(rowIndex == fromRow ? STMerge.RESTART : STMerge.CONTINUE);
		}
	}

	public List<MenuItemRawMaterialCategoryResponseDto> mapAllRawMaterial(List<Object[]> result) {
		Map<Long, MenuItemRawMaterialCategoryResponseDto> categoryMap = new LinkedHashMap<>();

		for (Object[] obj : result) {
			Long rawMaterialCatId = obj[5] != null ? ((Number) obj[5]).longValue() : null;

			String rawMaterialCatName = (String) obj[6];

			MenuItemRawMaterialCategoryResponseDto category = categoryMap.get(rawMaterialCatId);

			if (category == null) {
				category = new MenuItemRawMaterialCategoryResponseDto();

				category.setRawMaterialCatId(rawMaterialCatId);
				category.setRawMaterialCatName(rawMaterialCatName);
				category.setRawMaterials(new ArrayList<>());

				categoryMap.put(rawMaterialCatId, category);
			}

			MenuItemRawMaterialResponseDto rawMaterial = new MenuItemRawMaterialResponseDto();

			rawMaterial.setRawMaterialId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
			rawMaterial.setRawMaterial((String) obj[1]);
			rawMaterial.setWeight(obj[2] != null ? new BigDecimal(obj[2].toString()) : null);
			rawMaterial.setUnitId(obj[3] != null ? ((Number) obj[3]).longValue() : null);
			rawMaterial.setUnitName((String) obj[4]);
			rawMaterial.setMenuItemId(obj[7] != null ? ((Number) obj[7]).longValue() : null);

			category.getRawMaterials().add(rawMaterial);
		}

		return new ArrayList<>(categoryMap.values());
	}

	public Table createHeaderTable(PdfFont font, PdfFont fontBold, String venue, String dateTime, String catName,
			Integer lang) {

		String dateLabel;

		if (lang == 1) {
			dateLabel = "दिनांक";
		} else if (lang == 2) {
			dateLabel = "તારીખ";
		} else {
			dateLabel = "Date";
		}

		/* ---------- HEADER TABLE ---------- */
		Table table = new Table(UnitValue.createPercentArray(new float[] { 35f, 30f, 35f }));
		table.setFixedLayout();
		table.setWidth(UnitValue.createPercentValue(100f));

		Cell cell = new Cell(1, 3)
				.add(new Paragraph().add(new Text("OM GANESHAYA NAMAH:\nJAI JALARAM BAPA:").simulateBold())
						.setFont(font).setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.CENTER))
				.setBorder(Border.NO_BORDER);
		table.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(dateLabel + " : " + dateTime).simulateBold()).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		table.addCell(cell);

		cell = new Cell(2, 2).add(new Paragraph().add(new Text(catName).simulateBold().setUnderline()).setFont(font)
				.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)).setBorder(Border.NO_BORDER);
		table.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(venue != null ? venue.toUpperCase() : "")).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		table.addCell(cell);

		return table;
	}
	
	private Cell headerCell(String text, PdfFont fontBold) {

		return new Cell()
				.add(new Paragraph(text).setFont(fontBold).setFontSize(10).setTextAlignment(TextAlignment.CENTER))
				.setBackgroundColor(ColorConstants.LIGHT_GRAY).setBorder(new SolidBorder(ColorConstants.BLACK, 0.8f))
				.setPadding(5);
	}

	private Cell dataCell(String text, PdfFont font) {

		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9))
				.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(4);
	}
	
	private List<MenuItemRawMaterialCategoryResponseDto> getAllRawMaterial(Long eventId, Long eventFunctionId, Integer lang) {

		List<Object[]> rawMaterialsData = menuAllocationItemRawMaterialRepository.getAllRawMaterial(eventId,
				eventFunctionId, lang);

		List<Object[]> captainReceipeRawMaterialsData = menuAllocationItemCaptainReceipeRepository
				.getAllCaptainReceipeRawMaterial(eventId, eventFunctionId, lang);

		if ((rawMaterialsData == null || rawMaterialsData.isEmpty())
				&& (captainReceipeRawMaterialsData == null || captainReceipeRawMaterialsData.isEmpty())) {

			throw new RuntimeException("Data not found.");
		}

		List<MenuItemRawMaterialCategoryResponseDto> rawMaterials = new ArrayList<>();

		if (rawMaterialsData != null && !rawMaterialsData.isEmpty()) {

			rawMaterials = mapAllRawMaterial(rawMaterialsData);

			rawMaterials = mergeRawMaterialQty(rawMaterials);
		}

		List<MenuItemRawMaterialCategoryResponseDto> captainReceipeRawMaterials = new ArrayList<>();

		if (captainReceipeRawMaterialsData != null && !captainReceipeRawMaterialsData.isEmpty()) {
			captainReceipeRawMaterials = mapAllRawMaterial(captainReceipeRawMaterialsData);
			captainReceipeRawMaterials = mergeRawMaterialQty(captainReceipeRawMaterials);
		}

		Map<Long, MenuItemRawMaterialCategoryResponseDto> categoryMap = new LinkedHashMap<>();

		addCategories(categoryMap, rawMaterials);
		addCategories(categoryMap, captainReceipeRawMaterials);

		return new ArrayList<>(categoryMap.values());
	}
	
	private void addCategories(Map<Long, MenuItemRawMaterialCategoryResponseDto> categoryMap,
			List<MenuItemRawMaterialCategoryResponseDto> categories) {

		if (categories == null || categories.isEmpty()) {
			return;
		}

		for (MenuItemRawMaterialCategoryResponseDto category : categories) {
			Long categoryId = category.getRawMaterialCatId();

			MenuItemRawMaterialCategoryResponseDto existingCategory = categoryMap.get(categoryId);

			if (existingCategory == null) {
				MenuItemRawMaterialCategoryResponseDto newCategory = new MenuItemRawMaterialCategoryResponseDto();

				newCategory.setRawMaterialCatId(category.getRawMaterialCatId());
				newCategory.setRawMaterialCatName(category.getRawMaterialCatName());
				newCategory.setRawMaterials(new ArrayList<>(category.getRawMaterials()));

				categoryMap.put(categoryId, newCategory);
			} else {
				if (category.getRawMaterials() != null) {
					existingCategory.getRawMaterials().addAll(category.getRawMaterials());
				}
			}
		}
	}

	public List<MenuItemRawMaterialCategoryResponseDto> mergeRawMaterialQty(
			List<MenuItemRawMaterialCategoryResponseDto> categoryList) {

		for (MenuItemRawMaterialCategoryResponseDto category : categoryList) {

			if (category.getRawMaterials() == null || category.getRawMaterials().isEmpty()) {
				continue;
			}

			Map<String, List<MenuItemRawMaterialResponseDto>> rawMaterialMap = category.getRawMaterials().stream()
					.filter(rm -> rm.getRawMaterialId() != null)
					.collect(Collectors.groupingBy(rm -> rm.getRawMaterialId() + "_" + rm.getMenuItemId(),
							TreeMap::new, Collectors.toList()));

			List<MenuItemRawMaterialResponseDto> mergedRawMaterials = new ArrayList<>();

			for (Map.Entry<String, List<MenuItemRawMaterialResponseDto>> entry : rawMaterialMap.entrySet()) {

				List<MenuItemRawMaterialResponseDto> list = entry.getValue();

				MenuItemRawMaterialResponseDto first = list.get(0);

				Map<Long, Double> unitQtyMap = list.stream().filter(rm -> rm.getUnitId() != null).collect(
						Collectors.groupingBy(MenuItemRawMaterialResponseDto::getUnitId, LinkedHashMap::new, Collectors
								.summingDouble(rm -> rm.getWeight() != null ? rm.getWeight().doubleValue() : 0.0)));

				Map<Long, Double> convertedUnitQtyMap = eventRawMaterialServiceImpl.convertToParentUnit(unitQtyMap);

				Long finalUnitId = first.getUnitId();

				Double finalQty = list.stream()
						.mapToDouble(rm -> rm.getWeight() != null ? rm.getWeight().doubleValue() : 0.0).sum();

				if (!convertedUnitQtyMap.isEmpty()) {
					Map.Entry<Long, Double> firstEntry = convertedUnitQtyMap.entrySet().iterator().next();

					finalUnitId = firstEntry.getKey();
					finalQty = firstEntry.getValue();
				}

				first.setWeight(BigDecimal.valueOf(finalQty));
				first.setUnitId(finalUnitId);

				if (finalUnitId != null) {
					UnitMasterEntity unit = unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(finalUnitId);

					if (unit != null) {
						first.setUnitName(unit.getNameEnglish());
					}
				}
				mergedRawMaterials.add(first);
			}

			category.setRawMaterials(mergedRawMaterials);
		}

		return categoryList;
	}
}
