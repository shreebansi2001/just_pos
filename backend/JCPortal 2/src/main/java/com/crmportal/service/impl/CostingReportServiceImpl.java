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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.controller.AdminTemplateModuleController;
import com.crmportal.controller.GlobalExceptionHandler;
import com.crmportal.mapper.NameplateMapperImpl;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialDisposableRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.PurchaseOrderStoreRepository;
import com.crmportal.request.dto.CostingReportAgencyResponseDto;
import com.crmportal.response.dto.CostingReportResponseDto;
import com.crmportal.response.dto.EventWiseStoreIssuePricing;
import com.crmportal.response.dto.ExtraRawItemDTO;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.response.dto.RawMaterialCategoryRateResponseDto;
import com.crmportal.response.dto.StoreIssueCostingReportResponseDto;
import com.crmportal.response.dto.StoreIssueDetailsResponseDto;
import com.crmportal.response.dto.StoreIssueItemsResponseDto;
import com.crmportal.service.CostingReportService;
import com.crmportal.service.EventDishCostingService;
import com.crmportal.utility.PageNumberHandler;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class CostingReportServiceImpl implements CostingReportService {

	private final NameplateMapperImpl nameplateMapperImpl;

	private final AdminTemplateModuleController adminTemplateModuleController;
	private final GlobalExceptionHandler globalExceptionHandler;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	EventRawMaterialDisposableRepository eventRawMaterialDisposableRepository;

	@Autowired
	EventDishCostingService eventDishCostingService;
	
	@Autowired
	PurchaseOrderStoreRepository purchaseOrderStoreRepository;
	
	@Autowired
	CostingReportServiceImpl(GlobalExceptionHandler globalExceptionHandler,
			AdminTemplateModuleController adminTemplateModuleController, NameplateMapperImpl nameplateMapperImpl) {
		this.globalExceptionHandler = globalExceptionHandler;
		this.adminTemplateModuleController = adminTemplateModuleController;
		this.nameplateMapperImpl = nameplateMapperImpl;
	}

	@Override
	public String generateCostingReport(Long eventId, Long userid, int lang, Integer isCompanyDetails,
			HttpServletRequest re) {
		try {

			PdfFont basicFont = null;
			PdfFont itemFont = null;
			PdfFont boldFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "",
					personLabel = "", note = "", functionLabel = "", startDateTime = "", endDateTime = "";
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				itemFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");;
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "कार्यक्रम की तिथि";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थल";
				functionLabel = "कार्यक्रम";
				startDateTime = "आरंभ समय";
				endDateTime = "समाप्त समय";
				personLabel = "व्यक्ति";
				note = "नोट";
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
				startDateTime = "આરંભ સમય";
				endDateTime = "સમાપ્તિનો સમય";
				personLabel = "વ્યક્તિ";
				note = "નોંધ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				itemFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				customerName = "Name of Customer";
				customerPhone = "Contact No.";
				eName = "Event Name";
				eDate = "Order Date";
				fNotes = "Food Note";
				eVenue = "Venue Address";
				functionLabel = "Function";
				personLabel = "Person";
				startDateTime = "Start Time";
				endDateTime = "End Time";
				note = "Note";
			}

			Boolean isRawMaterialDone = menuItemRawMaterialServiceImpl.checkEventId(eventId);
			Boolean isMenuAllocationDone = menuItemRawMaterialServiceImpl.checkEventId2(eventId);

			System.out.println("isRawMaterialDone : " + isRawMaterialDone);
			System.out.println("isMenuAllocationDone : " + isMenuAllocationDone);

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			if (eventData == null) {
			    throw new RuntimeException("No event data found for eventId : " + eventId);
			}
			
			String eventNo = eventData.getEventNo();

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}
			System.out.println("date : " + eventData.getEventDate());
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventData.getEventDate()), "Costing Report") + ".pdf");

			Color headingBackground = new DeviceRgb(242, 235, 223);
			Color headingFont = new DeviceRgb(131, 99, 67);
			Color redColor = new DeviceRgb(255, 0, 0);
			Color blackColor = new DeviceRgb(0, 0, 0);

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 30, 20);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setWidth(UnitValue.createPercentValue(100));

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
			img.setWidth(UnitValue.createPercentValue(100));
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18))
					.setMultipliedLeading(1f);
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("Mobile No.")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				document.add(cmpHead);
			}

			Paragraph heading = new Paragraph().add(new Text("Menu Report With Price")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(0f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			Map<String, BigDecimal> rawMataterialTotal = new HashMap<>();
			Map<String, BigDecimal> chefAgencyTotal = new HashMap<>();
			Map<String, BigDecimal> outsourceTotal = new HashMap<>();
			Map<String, BigDecimal> labourTotal = new HashMap<>();
			Map<String, BigDecimal> totalCosting = new HashMap<>();
			Set<Long> itemIds = new HashSet<>();
			List<Long> itemIdList = new ArrayList<>();
			Set<String> itemNames = new HashSet<>();
			BigDecimal totalFunctionPerson = BigDecimal.valueOf(0);

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

			// 1st row
			Paragraph event = new Paragraph().add(new Text(customerName).setFontSize(12f).setFont(boldFont));
			Cell eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(clientName).setFontSize(12f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(customerPhone).setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(clientMobile).setFontSize(12f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			// 2ed row
			event = new Paragraph().add(new Text(eName).setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventName).setFontSize(12f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eDate).setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventDate).setFontSize(12f).setFont(basicFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			// 3rd row
			event = new Paragraph().add(new Text(eVenue).setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(":").setFontSize(12f).setFont(boldFont));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(venue).setFontSize(12f).setFont(basicFont));
			eventCell = new Cell(3, 4).add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			document.add(eventDataTable);

			List<CostingReportResponseDto> allFunctions = menuItemRawMaterialServiceImpl
					.getAllFunctionDetailsForCosting(eventId, lang);
			BigDecimal extraTotal = BigDecimal.ZERO;
			for (int k = 0; k < allFunctions.size(); k++) {

				CostingReportResponseDto fun = allFunctions.get(k);

				Long eventFunctionId = fun.getEventFunctionId();
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();
				BigDecimal person = fun.getFunctionPerson();
				if (person == null) {
				    person = BigDecimal.ZERO;
				}

				String functionStartTime = fun.getFunctionStartTime() == null || fun.getFunctionStartTime().isEmpty()
						? ""
						: fun.getFunctionStartTime();
				String functionEndTime = fun.getFunctionEndTime() == null || fun.getFunctionEndTime().isEmpty() ? ""
						: fun.getFunctionEndTime();

				totalFunctionPerson = totalFunctionPerson.add(person);

				float[] columnWidth = { 15f, 1f, 34f, 15f, 1f, 34f };
				Table function = new Table(UnitValue.createPercentArray(columnWidth));
				function.setWidth(UnitValue.createPercentValue(100));
				function.setBorder(new SolidBorder(Border.SOLID));

				Paragraph label = new Paragraph().add(new Text(functionLabel).setFont(boldFont).setFontSize(12));
				Paragraph saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(12));
				Paragraph name = new Paragraph().add(new Text(functionName).setFont(basicFont).setFontSize(12));

				Cell cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(startDateTime).setFont(boldFont).setFontSize(12));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(12));
				name = new Paragraph().add(new Text(functionStartTime).setFont(basicFont).setFontSize(12));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(personLabel).setFont(boldFont).setFontSize(12));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(12));
				name = new Paragraph().add(new Text(person.toString()).setFont(basicFont).setFontSize(12));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				label = new Paragraph().add(new Text(endDateTime).setFont(boldFont).setFontSize(12));
				saperator = new Paragraph().add(new Text(":").setFont(boldFont).setFontSize(12));
				name = new Paragraph().add(new Text(functionEndTime).setFont(basicFont).setFontSize(12));
				cell = new Cell().add(label).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
				function.addCell(cell);
				cell = new Cell().add(name).setBorder(Border.NO_BORDER);
				function.addCell(cell);

				document.add(function);

				List<CostingReportResponseDto> allItemCategories = menuItemRawMaterialServiceImpl
						.getFunctionCategoryDetailsForCosting(eventId, eventFunctionId, lang, userid);

				BigDecimal totalRawMaterial = BigDecimal.ZERO;
				System.out.println("categroy sixe : " + allItemCategories.size());
				int index = 0;
				
				for (CostingReportResponseDto cat : allItemCategories) {
					try {
						index++;

						System.out.println("Iteration : " + index);
						System.out.println("Category Id : " + cat.getMenuCategoryId());

						System.out.println("CatSize:-" + allItemCategories.size());
						Long catId = cat.getMenuCategoryId();
						System.out.println("cat id : " + catId);
						String catName = cat.getMenuCategoryName() == null || cat.getMenuCategoryName().isEmpty() ? ""
								: cat.getMenuCategoryName();

						Paragraph menuCatName = new Paragraph()
								.add(new Text(catName).setFont(basicFont).setFontSize(18))
								.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.LEFT)
								.setUnderline().setBorderTop(new SolidBorder(blackColor, 0.5f));

						List<CostingReportResponseDto> allItems = new ArrayList<>();
						System.out.println("eventid : " + eventId);
						System.out.println("function id : " + eventFunctionId);

						if (isRawMaterialDone) {
							System.out.println("1");
							allItems = menuItemRawMaterialServiceImpl.getFunctionCategoryItemDetailsForCosting3(eventId,
									eventFunctionId, catId, lang);
						} else if (isMenuAllocationDone) {
							System.out.println("2");
							allItems = menuItemRawMaterialServiceImpl.getFunctionCategoryItemDetailsForCosting2(eventId,
									eventFunctionId, catId, lang);
						} else {
							System.out.println("3");
							allItems = menuItemRawMaterialServiceImpl.getFunctionCategoryItemDetailsForCosting(eventId,
									eventFunctionId, catId, lang);
						}
						System.out.println("size : " + allItems.size());
						
						if (allItems.size() > 0) {
							document.add(menuCatName);

							for (CostingReportResponseDto item : allItems) {
								Long itemId = item.getMenuItemId();
								itemIds.add(itemId);

								String itemName = item.getMenuItemName() == null || item.getMenuItemName().isEmpty()
										? ""
										: item.getMenuItemName();
								itemNames.add(itemName);

								BigDecimal totalprice = item.getTotalprice() != null ? item.getTotalprice()
										: BigDecimal.ZERO;

								BigDecimal perplateprice = item.getPerplateprice() != null
										? item.getPerplateprice().setScale(2, RoundingMode.HALF_UP)
										: BigDecimal.ZERO;

								String pax = item.getItemPerson() != null ? " (" + item.getItemPerson() + " Pax) " : "";

								Paragraph menuItemName = new Paragraph()
										.add(new Text(itemName + " - " + totalprice + " Rs.\n (" + perplateprice
												+ " Per plate) " + pax).setFont(boldFont).setFontSize(14))
										.setFontColor(redColor).setUnderline()
										.setWidth(UnitValue.createPercentValue(100))
										.setTextAlignment(TextAlignment.CENTER);

								List<CostingReportResponseDto> allRawMaterialCat = new ArrayList<>();

								if (isRawMaterialDone) {
									allRawMaterialCat = menuItemRawMaterialServiceImpl
											.getRawMaterialCategoryDetailsForCosting3(eventId, eventFunctionId, itemId,
													lang);
								} else if (isMenuAllocationDone) {
									List<Long> rawMaterialIds = menuItemRawMaterialServiceImpl
											.getAllRawMaterialIds(eventId, eventFunctionId, itemId);
									allRawMaterialCat = menuItemRawMaterialServiceImpl
											.getRawMaterialCategoryDetailsForCosting2(eventId, eventFunctionId, itemId,
													rawMaterialIds, lang);
								} else {
									allRawMaterialCat = menuItemRawMaterialServiceImpl
											.getRawMaterialCategoryDetailsForCosting(eventId, eventFunctionId, catId,
													itemId, lang);
								}

								document.add(menuItemName);
								if (allRawMaterialCat.size() > 0) {

									for (CostingReportResponseDto rawMaterialCat : allRawMaterialCat) {

										Long rawMaterialCatId = rawMaterialCat.getRawMaterialCatId();
										String rawMaterialCatName = rawMaterialCat.getRawMaterialCatName() == null
												|| rawMaterialCat.getRawMaterialCatName().isEmpty() ? ""
														: rawMaterialCat.getRawMaterialCatName();

										Paragraph rawMaterialCategoryName = new Paragraph()
												.add(new Text(rawMaterialCatName).setFont(basicFont).setFontSize(15))
												.setWidth(UnitValue.createPercentValue(100))
												.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(15f)
												.setUnderline();

										document.add(rawMaterialCategoryName);

										List<CostingReportResponseDto> allRawMaterial = new ArrayList<>();

										if (isRawMaterialDone) {
											allRawMaterial = menuItemRawMaterialServiceImpl
													.getRawMaterialDetailsForCosting3(eventId, eventFunctionId, itemId,
															rawMaterialCatId, lang);
										} else if (isMenuAllocationDone) {
											allRawMaterial = menuItemRawMaterialServiceImpl
													.getRawMaterialDetailsForCosting2(eventId, eventFunctionId, itemId,
															rawMaterialCatId, lang);
										} else {
											allRawMaterial = menuItemRawMaterialServiceImpl
													.getRawMaterialDetailsForCosting(eventId, eventFunctionId, catId,
															itemId, rawMaterialCatId, lang);
										}

										BigDecimal totalRmPrice = allRawMaterial.stream().map(
												rm -> rm.getTotalprice() != null ? rm.getTotalprice() : BigDecimal.ZERO)
												.reduce(BigDecimal.ZERO, BigDecimal::add);

										rawMataterialTotal.merge(rawMaterialCatName, totalRmPrice, BigDecimal::add);

										float[] columnWidth2 = { 22f, 5f, 6f, 7f, 9f, 22f, 5f, 6f, 7f, 9f };
										Table rawmaterialTable = new Table(UnitValue.createPercentArray(columnWidth2));
										rawmaterialTable.setWidth(UnitValue.createPercentValue(100));
										Paragraph data = null;
										Cell dataCell = null;

										for (int l = 0; l < allRawMaterial.size(); l++) {
											CostingReportResponseDto rawmaterial = allRawMaterial.get(l);

											Long rawMaterialId = rawmaterial.getRawMaterialItemId();

											String rawMaterialName = rawmaterial.getRawMaterialItemName() != null
													? rawmaterial.getRawMaterialItemName()
													: "";

											BigDecimal quantity = rawmaterial.getQuantity() != null
													? rawmaterial.getQuantity()
													: BigDecimal.ZERO;

											String unit = rawmaterial.getUnitName() != null ? rawmaterial.getUnitName()
													: "";

//										BigDecimal rate = rawmaterial.getRate() != null ? rawmaterial.getRate()
//												: BigDecimal.ZERO;

											BigDecimal totalPrice = rawmaterial.getTotalprice() != null
													? rawmaterial.getTotalprice()
													: BigDecimal.ZERO;

											data = new Paragraph().add(new Text(rawMaterialName)).setFont(basicFont)
													.setFontSize(9f).setMultipliedLeading(1f);
											dataCell = new Cell().add(data).setBorder(Border.NO_BORDER);
											rawmaterialTable.addCell(dataCell);

											data = new Paragraph().add(new Text(quantity.toString())).setFont(basicFont)
													.setFontSize(9f).setMultipliedLeading(1f);
											dataCell = new Cell().add(data).setBorder(Border.NO_BORDER);
											rawmaterialTable.addCell(dataCell);

											data = new Paragraph().add(new Text(unit)).setFont(basicFont)
													.setFontSize(8.5f).setMultipliedLeading(1f);
											dataCell = new Cell().add(data).setBorder(Border.NO_BORDER);
											rawmaterialTable.addCell(dataCell);

//										data = new Paragraph().add(new Text(" * " + rate.toString())).setFont(basicFont)
//												.setFontSize(8.5f).setMultipliedLeading(1f);
//										dataCell = new Cell().add(data).setBorder(Border.NO_BORDER);
//										rawmaterialTable.addCell(dataCell);

											data = new Paragraph().add(new Text(" = " + totalPrice.toString()))
													.setFont(basicFont).setFontSize(9f).setMultipliedLeading(1f);
											dataCell = new Cell(1, 2).add(data).setBorder(Border.NO_BORDER);
											rawmaterialTable.addCell(dataCell);

											totalRawMaterial = totalRawMaterial.add(totalPrice);
										}
										document.add(rawmaterialTable);
									}
									
								}
							}
						}
					} catch (Exception e) {

						System.out.println("FAILED AT ITERATION : " + index);
						e.printStackTrace();
					}
				}
				if (isRawMaterialDone) {

					List<Object[]> extraData = menuItemRawMaterialRepository
							.getExtraRawMaterialItemCategoryWise(eventId, eventFunctionId, lang);
					Map<String, List<ExtraRawItemDTO>> categoryMap = new LinkedHashMap<>();
					for (Object[] r : extraData) {
						Long rawCatId = r[0] != null ? ((Number) r[0]).longValue() : 0L;
						String rawCatName = (String) r[1];
						ExtraRawItemDTO extraitem = new ExtraRawItemDTO((String) r[2],
								r[3] != null ? ((Number) r[3]).doubleValue() : 0.0,
								r[4] != null ? ((Number) r[4]).doubleValue() : 0.0, (String) r[5]);
						categoryMap.computeIfAbsent(rawCatName, key -> new ArrayList<>())
								.add(extraitem);
					}

					for (Map.Entry<String, List<ExtraRawItemDTO>> entry : categoryMap.entrySet()) {

						String categoryName = entry.getKey();
						List<ExtraRawItemDTO> items = entry.getValue();

						// ✅ CATEGORY TITLE
						Paragraph rawMaterialCategoryName = new Paragraph()
								.add(new Text(categoryName).setFont(basicFont).setFontSize(15))
								.setWidth(UnitValue.createPercentValue(100))
								.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(15f)
								.setUnderline();

						document.add(rawMaterialCategoryName);

						// ✅ TABLE PER CATEGORY
						float[] columnWidth2 = { 22f, 5f, 6f, 7f, 9f, 22f, 5f, 6f, 7f, 9f };
						Table rawmaterialTable = new Table(
								UnitValue.createPercentArray(columnWidth2));
						rawmaterialTable.setWidth(UnitValue.createPercentValue(100));

						rawmaterialTable.addCell(new Cell(1, 10)
								.add(new Paragraph("EXTRA RAW MATERIAL")
								.simulateBold()
								.setFont(basicFont)
								.setFontSize(12f))
								.setBorder(Border.NO_BORDER)
								.setFontColor(redColor)
								.setTextAlignment(TextAlignment.CENTER));
						
						for (ExtraRawItemDTO raw : items) {

							String rawname = raw.getRawMaterialName() != null
									? raw.getRawMaterialName()
									: "";
							BigDecimal qty = raw.getQty() != null ? BigDecimal.valueOf(raw.getQty())
									: BigDecimal.ZERO;
							String unit = raw.getUnitName() != null ? raw.getUnitName() : "";
							BigDecimal price = raw.getPrice() != null
									? BigDecimal.valueOf(raw.getPrice())
									: BigDecimal.ZERO;

							// NAME
							rawmaterialTable.addCell(new Cell()
									.add(new Paragraph(rawname).setFont(basicFont).setFontSize(9f))
									.setBorder(Border.NO_BORDER));

							// QTY
							rawmaterialTable
									.addCell(
											new Cell()
													.add(new Paragraph(qty.toString())
															.setFont(basicFont).setFontSize(9f))
													.setBorder(Border.NO_BORDER));

							// UNIT
							rawmaterialTable.addCell(new Cell()
									.add(new Paragraph(unit).setFont(basicFont).setFontSize(9f))
									.setBorder(Border.NO_BORDER));

							// PRICE
							rawmaterialTable
									.addCell(new Cell()
											.add(new Paragraph("= " + price.toString())
													.setFont(basicFont).setFontSize(9f))
											.setBorder(Border.NO_BORDER));
							
							rawmaterialTable
							.addCell(new Cell()
									.add(new Paragraph("")
											.setFont(basicFont).setFontSize(9f))
									.setBorder(Border.NO_BORDER));

							extraTotal = extraTotal.add(price);
						}
						document.add(rawmaterialTable);
					}
				}

//				Table functionSummaryTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
//				functionSummaryTable.setWidth(UnitValue.createPercentValue(100f));
//				functionSummaryTable.setFixedLayout();
//				functionSummaryTable.setMarginTop(10f);
//
//				BigDecimal finalRawMaterialTotal = totalRawMaterial.add(extraTotal); 
//				
//				Paragraph para = new Paragraph().add(new Text("Total Raw Material Price").simulateBold())
//						.setFont(basicFont).setFontSize(14f).setMultipliedLeading(1f)
//						.setTextAlignment(TextAlignment.CENTER);
//				Cell dataCell = new Cell().add(para);
//				functionSummaryTable.addCell(dataCell);
//
//				para = new Paragraph().add(new Text("Per Dish Costing").simulateBold()).setFont(basicFont)
//						.setFontSize(14f).setMultipliedLeading(1f).setTextAlignment(TextAlignment.CENTER);
//				dataCell = new Cell().add(para);
//				functionSummaryTable.addCell(dataCell);
//
//				para = new Paragraph().add(new Text(finalRawMaterialTotal.toString())).setFont(basicFont).setFontSize(14f)
//						.setMultipliedLeading(1f).setTextAlignment(TextAlignment.CENTER);
//				dataCell = new Cell().add(para);
//				functionSummaryTable.addCell(dataCell);
//
//				para = new Paragraph()
//						.add(new Text(person != null && person.compareTo(BigDecimal.ZERO) != 0
//								? finalRawMaterialTotal.divide(person, 2, RoundingMode.HALF_UP).toString()
//								: "0.00"))
//						.setFont(basicFont).setFontSize(14f).setMultipliedLeading(1f)
//						.setTextAlignment(TextAlignment.CENTER);
//
//				dataCell = new Cell().add(para);
//				functionSummaryTable.addCell(dataCell);

//				document.add(functionSummaryTable);

				if (k + 1 < allFunctions.size()) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

			}

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			heading = new Paragraph().add(new Text("Agency Price")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(0f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			float[] columnWidth3 = { 18f, 27f, 15f, 10f, 11f, 10f, 12f };
			Table agencyTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			agencyTbl.setWidth(UnitValue.createPercentValue(100));
			Paragraph data = null;
			Cell dataCell = null;
			BigDecimal finalTotal = BigDecimal.valueOf(0);

			data = new Paragraph().add(new Text("")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Agency Name")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Qty.")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Rate")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Trans. Rate")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Total")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Chef Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 7).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			agencyTbl.addCell(dataCell);

			itemIdList = itemIds.stream().collect(Collectors.toList());

			List<CostingReportAgencyResponseDto> agencyReport = new ArrayList<>();

			if (isRawMaterialDone || isMenuAllocationDone) {
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport2(eventId, lang,
						true, false);
			} else {
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport(eventId, itemIdList, lang, true,
						false);
			}

			for (CostingReportAgencyResponseDto agency : agencyReport) {
				String functionName = agency.getFunctionName() != null ? agency.getFunctionName() : "";
				String itemName = agency.getItemName() != null ? agency.getItemName() : "";
				String agencyName = agency.getAgencyName() != null ? agency.getAgencyName() : "";
				String unit = agency.getUnitName() != null ? agency.getUnitName() : "";
				String quantity = agency.getQuantity() != null ? agency.getQuantity() : "";
				String chefLabourPrice = agency.getChefLabourPrice() != null ? agency.getChefLabourPrice() : "";
				BigDecimal totalChefLabourPrice = agency.getTotalChefLabourPrice() != null
						? agency.getTotalChefLabourPrice().setScale(2)
						: BigDecimal.ZERO;
				BigDecimal transRate = agency.getTransRate() != null ? agency.getTransRate().setScale(2)
						: BigDecimal.ZERO.setScale(2);

				data = new Paragraph().add(new Text(functionName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(itemName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(agencyName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(quantity)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(chefLabourPrice)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(transRate.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(totalChefLabourPrice.toString())).setFont(basicFont)
						.setFontSize(10f).setMultipliedLeading(1f).setPaddingLeft(2f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				finalTotal = finalTotal.add(totalChefLabourPrice);

				chefAgencyTotal.merge(agencyName, totalChefLabourPrice, BigDecimal::add);

			}

			data = new Paragraph().add(new Text("Outsource Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 7).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			agencyTbl.addCell(dataCell);

			if (isRawMaterialDone || isMenuAllocationDone) {
//				itemIdList = menuItemRawMaterialRepository.getOutsourceItemAllocatedId(eventId);
				System.out.println("1");
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport2(eventId, lang,
						false, true);
			} else {
				System.out.println("2");
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport(eventId, itemIdList, lang,
						false, true);
			}

			for (CostingReportAgencyResponseDto agency : agencyReport) {

				String functionName = agency.getFunctionName() != null ? agency.getFunctionName() : "";
				String itemName = agency.getItemName() != null ? agency.getItemName() : "";
				String agencyName = agency.getAgencyName() != null ? agency.getAgencyName() : "";
				String unit = agency.getUnitName() != null ? agency.getUnitName() : "";
				String quantity = agency.getQuantity() != null ? agency.getQuantity() : "";
				BigDecimal outSourcePrice = agency.getOutsourcePrice() != null
						? agency.getOutsourcePrice().setScale(2, RoundingMode.HALF_UP)
						: BigDecimal.ZERO.setScale(2);
				BigDecimal totalOutsourcePrice = agency.getTotalOutsourcePrice() != null
						? agency.getTotalOutsourcePrice().setScale(2, RoundingMode.HALF_UP)
						: BigDecimal.ZERO.setScale(2);
				BigDecimal transRate = agency.getTransRate() != null ? agency.getTransRate().setScale(2)
						: BigDecimal.ZERO.setScale(2);

				data = new Paragraph().add(new Text(functionName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(itemName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(agencyName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(quantity + " " + unit)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(outSourcePrice.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(transRate.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(totalOutsourcePrice.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				finalTotal = finalTotal.add(totalOutsourcePrice);

				outsourceTotal.merge(agencyName, totalOutsourcePrice, BigDecimal::add);

			}

			data = new Paragraph().add(new Text("Labour Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 7).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			agencyTbl.addCell(dataCell);

			agencyReport = menuItemRawMaterialServiceImpl.getLabourForCostingReport(eventId, lang);

			for (CostingReportAgencyResponseDto agency : agencyReport) {
				// Check all values at the start of the loop
				String functionName = agency.getFunctionName() != null ? agency.getFunctionName() : "";
				String itemName = agency.getItemName() != null ? agency.getItemName() : "";
				String agencyName = agency.getAgencyName() != null ? agency.getAgencyName() : "";
				String quantity = agency.getQuantity() != null ? agency.getQuantity() : "0";
				String labourPrice = agency.getLabourPrice() != null
						? agency.getLabourPrice().setScale(2, RoundingMode.HALF_UP).toString()
						: "0.00";
				String totalLabourPrice = agency.getTotalLabourPrice() != null
						? agency.getTotalLabourPrice().setScale(2, RoundingMode.HALF_UP).toString()
						: "0.00";
				BigDecimal totalPriceValue = agency.getTotalLabourPrice() != null
						? agency.getTotalLabourPrice().setScale(2)
						: BigDecimal.ZERO.setScale(2);
				BigDecimal transRate = agency.getTransRate() != null ? agency.getTransRate().setScale(2)
						: BigDecimal.ZERO.setScale(2);

				// Now assign to data and add cells
				data = new Paragraph().add(new Text(functionName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(itemName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(agencyName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(quantity)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(labourPrice)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(transRate.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(totalLabourPrice)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				// Accumulate totals
				finalTotal = finalTotal.add(totalPriceValue);
				labourTotal.merge(agencyName, totalPriceValue, BigDecimal::add);
			}

			data = new Paragraph().add(new Text("Final Amount")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 6).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text(finalTotal.toString())).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell().add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			agencyTbl.addCell(dataCell);

			document.add(agencyTbl);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			float[] columnWidth4 = { 70f, 30f };
			Table bifercationTbl = new Table(UnitValue.createPercentArray(columnWidth4));
			bifercationTbl.setWidth(UnitValue.createPercentValue(100));
			data = null;
			dataCell = null;
			finalTotal = BigDecimal.valueOf(0);

			data = new Paragraph().add(new Text("Costing Bifurcation")).setFont(basicFont).setFontSize(14f)
					.setMultipliedLeading(1.5f).setFontColor(headingFont);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setTextAlignment(TextAlignment.CENTER)
					.setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Agency Name")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Total")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Chef Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : chefAgencyTotal.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

				totalCosting.merge("Chef Labour Price", entry.getValue(), BigDecimal::add);

			}

			data = new Paragraph().add(new Text("Outsource Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : outsourceTotal.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

				totalCosting.merge("Outside Price", entry.getValue(), BigDecimal::add);
			}

			data = new Paragraph().add(new Text("Labour Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : labourTotal.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

				totalCosting.merge("Labour Price", entry.getValue(), BigDecimal::add);
			}

			List<RawMaterialCategoryRateResponseDto> responseDto = eventDishCostingService
					.getRawMaterialTotalCategoryWise(eventId, -1l, isRawMaterialDone, isMenuAllocationDone);
			
			data = new Paragraph().add(new Text("Raw Material")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

//			BigDecimal disposableItemAmount = eventRawMaterialDisposableRepository.getTotalDisposibleAmount(eventId);
//			if (disposableItemAmount == null) {
//				disposableItemAmount = BigDecimal.ZERO;
//			}
//			rawMataterialTotal.merge("DISPOSABLE",
//					disposableItemAmount != null ? disposableItemAmount : BigDecimal.ZERO, BigDecimal::add);

			for (RawMaterialCategoryRateResponseDto rawCat : responseDto) {
				data = new Paragraph().add(new Text(rawCat.getCategoryNameEng())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(rawCat.getTotalRate() != null ? rawCat.getTotalRate().toString() : "")).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				BigDecimal totalRate =
				        rawCat.getTotalRate() != null
				        ? rawCat.getTotalRate()
				        : BigDecimal.ZERO;

				finalTotal = finalTotal.add(totalRate);

				totalCosting.merge("Raw Material Price", totalRate, BigDecimal::add);
			}
			
//			data = new Paragraph().add(new Text("EXTRA RAW MATERIAL")).setFont(basicFont).setFontSize(10f)
//					.setMultipliedLeading(1f);
//			dataCell = new Cell().add(data).setPaddingLeft(2f);
//			bifercationTbl.addCell(dataCell);
//			
//			data = new Paragraph().add(new Text(extraTotal.toString())).setFont(basicFont).setFontSize(10f)
//					.setMultipliedLeading(1f);
//			dataCell = new Cell().add(data).setPaddingLeft(2f);
//			bifercationTbl.addCell(dataCell);
//
//			totalCosting.merge("Raw Material Price", extraTotal, BigDecimal::add);
			
			data = new Paragraph().add(new Text("Total Raw Material Price")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell().add(data).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);
			
			data = new Paragraph().add(new Text(totalCosting.get("Raw Material Price").setScale(2, RoundingMode.HALF_UP).toString()))
					.setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell().add(data).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Final Amount")).setFont(boldFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell().add(data).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text(finalTotal.add(extraTotal).setScale(2, RoundingMode.HALF_UP).toString())).setFont(boldFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(headingFont);
			dataCell = new Cell().add(data).setBackgroundColor(headingBackground);
			bifercationTbl.addCell(dataCell);

			document.add(bifercationTbl);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			float[] columnWidth5 = { 60f, 40f };
			Table totalCostingTbl = new Table(UnitValue.createPercentArray(columnWidth4));
			totalCostingTbl.setWidth(UnitValue.createPercentValue(100));
			data = null;
			dataCell = null;
			finalTotal = BigDecimal.valueOf(0);

			data = new Paragraph().add(new Text("Total Costing")).setFont(basicFont).setFontSize(14f)
					.setMultipliedLeading(1.5f).setFontColor(headingFont);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setTextAlignment(TextAlignment.CENTER)
					.setBackgroundColor(headingBackground);
			totalCostingTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : totalCosting.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(12f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
				totalCostingTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().setScale(2, RoundingMode.HALF_UP).toString())).setFont(basicFont).setFontSize(12f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
				totalCostingTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

			}
			
			EventWiseStoreIssuePricing storeIssueData = getStoreIssuePriceByEventId(eventId);
			
			if(storeIssueData != null) {
				data = new Paragraph().add(new Text("Store Issue Price")).setFont(basicFont).setFontSize(12f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
				totalCostingTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(storeIssueData.getTotalPrice().toString())).setFont(basicFont).setFontSize(12f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
				totalCostingTbl.addCell(dataCell);

				finalTotal = finalTotal.add(storeIssueData.getTotalPrice());
			}

//			dataCell = new Cell().setBorder(Border.NO_BORDER).setMinHeight(10f);
//			totalCostingTbl.addCell(dataCell);
//			
//			dataCell = new Cell().setBorder(Border.NO_BORDER).setMinHeight(5f);
//			totalCostingTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Total Amount").simulateBold()).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			data = new Paragraph().add(new Text(finalTotal.setScale(2, RoundingMode.HALF_UP).toString()).simulateBold()).setFont(basicFont)
					.setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Per Dish Amount").simulateBold()).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			BigDecimal perDishAmount = BigDecimal.ZERO;

			if (totalFunctionPerson.compareTo(BigDecimal.ZERO) > 0) {
			    perDishAmount = finalTotal.divide(totalFunctionPerson, 2, RoundingMode.HALF_UP);
			}

			data = new Paragraph().add(new Text(perDishAmount.setScale(2, RoundingMode.HALF_UP).toString()).simulateBold()).setFont(basicFont)
					.setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			document.add(totalCostingTbl);

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 0, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()),
							"Costing Report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}
	}

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}

	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + fileSafe(date.replace("/", "_") + " (" + type.toUpperCase() + ")");
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
	public String generateStoreIssueWiseCostingReport(Long eventId, Long userid, int lang, Integer isCompanyDetails,
			HttpServletRequest re) {
		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "",
					personLabel = "", note = "", functionLabel = "", startDateTime = "", endDateTime = "";
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");;
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम";
				customerPhone = "मोबाइल नंबर";
				eName = "कार्यक्रम का नाम";
				eDate = "कार्यक्रम की तिथि";
				fNotes = "भोजन विवरण";
				eVenue = "आयोजन स्थल";
				functionLabel = "कार्यक्रम";
				startDateTime = "आरंभ समय";
				endDateTime = "समाप्त समय";
				personLabel = "व्यक्ति";
				note = "नोट";
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
				startDateTime = "આરંભ સમય";
				endDateTime = "સમાપ્તિનો સમય";
				personLabel = "વ્યક્તિ";
				note = "નોંધ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				customerName = "Name of Customer";
				customerPhone = "Contact No.";
				eName = "Event Name";
				eDate = "Order Date";
				fNotes = "Food Note";
				eVenue = "Venue Address";
				functionLabel = "Function";
				personLabel = "Person";
				startDateTime = "Start Time";
				endDateTime = "End Time";
				note = "Note";
			}

			StoreIssueCostingReportResponseDto storeIssueData = getStoreIssueCostingReport(eventId, lang);
			
			if(storeIssueData == null) {
				return "Data not found.";
			}
			String eventNo = storeIssueData.getEventNo();

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(storeIssueData.getEventDate()), "Costing Report") + ".pdf");

			/* colors */
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color bgColor = new DeviceRgb(242, 235, 223);
			Color fontColor = new DeviceRgb(115, 99, 67);
			
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);

			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 20, 20);
			
			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setWidth(UnitValue.createPercentValue(100));

			String cmpName = storeIssueData.getCmpName() == null || storeIssueData.getCmpName().isEmpty() ? ""
					: storeIssueData.getCmpName();
			String cmpEmail = storeIssueData.getCmpEmail() == null || storeIssueData.getCmpEmail().isEmpty() ? ""
					: storeIssueData.getCmpEmail();
			String cmpMobile = storeIssueData.getCmpContactNo() == null || storeIssueData.getCmpContactNo().isEmpty() ? ""
					: storeIssueData.getCmpContactNo();
			String logo = storeIssueData.getLogo() == null || storeIssueData.getLogo().isEmpty() ? "" : storeIssueData.getLogo();

//			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//			Image img = new Image(imgData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(UnitValue.createPercentValue(100));
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFontSize(18))
					.setMultipliedLeading(1f);
			cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text("Mobile No.")
					.setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpMobile).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFontSize(12))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				document.add(cmpHead);
			}
			
			Paragraph headerPara = new Paragraph().add("PARTY DETAILS").setFontSize(16f).setFontColor(fontColor)
					.setTextAlignment(TextAlignment.CENTER).simulateBold();
			document.add(headerPara);
			
			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 18f, 2f, 30f, 18f, 2f, 30f }));
			headerTable.setFixedLayout();
			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setMarginTop(10f);
			
			TextAlignment leftAlignment = TextAlignment.LEFT;
			TextAlignment rightAlignment = TextAlignment.RIGHT;
			TextAlignment centerAlignment = TextAlignment.CENTER;
			
			VerticalAlignment topAlignment = VerticalAlignment.TOP;
			VerticalAlignment middleAlignment = VerticalAlignment.MIDDLE;
			VerticalAlignment bottomAlignment = VerticalAlignment.BOTTOM;
			
			createCell(headerTable, "Name", fontColor, null, 14f, false, null, 2f, leftAlignment, middleAlignment, true, 1, 1, null);
			createCell(headerTable, ":", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, basicFont);
			createCell(headerTable, storeIssueData.getPartyName(), fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, false, 1, 1, basicFont);

			createCell(headerTable, "Mobile No", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(headerTable, ":", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, basicFont);
			createCell(headerTable, storeIssueData.getPartyContactNo(), fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, false, 1, 1, basicFont);

			createCell(headerTable, "Email", fontColor, null, 14f, false, null, 2f, leftAlignment, middleAlignment, true, 1, 1, null);
			createCell(headerTable, ":", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, basicFont);
			createCell(headerTable, storeIssueData.getPartyEmail(), fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, false, 1, 1, basicFont);

			createCell(headerTable, "Address", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(headerTable, ":", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, basicFont);
			createCell(headerTable, storeIssueData.getPartyAddress(), fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, false, 1, 1, basicFont);

			createCell(headerTable, "Event Name", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(headerTable, ":", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, basicFont);
			createCell(headerTable, storeIssueData.getEventName(), fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, false, 1, 1, basicFont);

			createCell(headerTable, "Event Date", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(headerTable, ":", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, basicFont);
			createCell(headerTable, storeIssueData.getEventDate(), fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, false, 1, 1, basicFont);

			createCell(headerTable, "Venue", fontColor, null, 14f, false, null, 2f, leftAlignment, middleAlignment, true, 1, 1, null);
			createCell(headerTable, ":", fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, true, 1, 1, basicFont);
			createCell(headerTable, storeIssueData.getEventVenue(), fontColor, null, 14f, false, null, 2f, leftAlignment,
					middleAlignment, false, 1, 1, basicFont);

			document.add(headerTable);
			
			Table dataTable = new Table(UnitValue.createPercentArray(new float[] { 10f, 15f, 20f, 25f, 15f, 15f }));
			dataTable.setFixedLayout();
			dataTable.setWidth(UnitValue.createPercentValue(100f));
			dataTable.setMarginTop(20f);

			createCell(dataTable, "Store Issue", fontColor, bgColor, 16f, true, fontColor, 2f, centerAlignment,
					middleAlignment, true, 1, 6, null);

			createCell(dataTable, "SR NO", fontColor, bgColor, 12f, true, fontColor, 2f, centerAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(dataTable, "PO Number", fontColor, bgColor, 12f, true, fontColor, 2f, centerAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(dataTable, "Kitchen Type", fontColor, bgColor, 12f, true, fontColor, 2f, centerAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(dataTable, "Item Name", fontColor, bgColor, 12f, true, fontColor, 2f, centerAlignment,
					middleAlignment, true, 1, 1, null);
			createCell(dataTable, "Qty", fontColor, bgColor, 12f, true, fontColor, 2f, centerAlignment, middleAlignment,
					true, 1, 1, null);
			createCell(dataTable, "Amount", fontColor, bgColor, 12f, true, fontColor, 2f, centerAlignment,
					middleAlignment, true, 1, 1, null);

			BigDecimal totalAmount = BigDecimal.ZERO;
			
			Map<String, BigDecimal> rawMataterialTotal = new HashMap<>();

			BigDecimal totalRawMaterial = BigDecimal.ZERO;
			
			for(StoreIssueDetailsResponseDto cat : storeIssueData.getCategories()) {
				createCell(dataTable, cat.getRawCatName(), fontColor, bgColor, 11f, true, fontColor, 2f,
						centerAlignment, middleAlignment, true, 1, 6, basicFont);
				Integer i = 0;
				
				String rawMaterialCatName = cat.getRawCatName() == null
						|| cat.getRawCatName().isEmpty() ? ""
								: cat.getRawCatName();
				
				BigDecimal totalRmPrice = cat.getItems().stream().map(
						rm -> rm.getTotalAmount() != null ? rm.getTotalAmount() : BigDecimal.ZERO)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				rawMataterialTotal.merge(rawMaterialCatName, totalRmPrice, BigDecimal::add);
				
				for(StoreIssueItemsResponseDto item : cat.getItems()) {
					BigDecimal amount = item.getTotalAmount();
					totalAmount = totalAmount.add(amount);
					createCell(dataTable, (++i).toString(), blackColor, null, 10f, true, fontColor, 2f, centerAlignment,
							middleAlignment, false, 1, 1, basicFont);
					createCell(dataTable, item.getPoCode(), blackColor, null, 10f, true, fontColor, 2f, centerAlignment,
							middleAlignment, false, 1, 1, basicFont);
					createCell(dataTable, item.getKitchenType(), blackColor, null, 10f, true, fontColor, 2f, centerAlignment,
							middleAlignment, false, 1, 1, basicFont);
					createCell(dataTable, item.getRawItemName(), blackColor, null, 10f, true, fontColor, 2f, centerAlignment,
							middleAlignment, false, 1, 1, basicFont);
					createCell(dataTable, item.getQty() + " " + item.getUnitName(), blackColor, null, 10f, true, fontColor, 2f, centerAlignment, middleAlignment,
							false, 1, 1, basicFont);
					createCell(dataTable, amount.toString(), blackColor, null, 10f, true, fontColor, 2f, centerAlignment,
							middleAlignment, false, 1, 1, basicFont);
					
					BigDecimal totalPrice = item.getTotalAmount() != null
							? item.getTotalAmount()
							: BigDecimal.ZERO;

					totalRawMaterial = totalRawMaterial.add(totalPrice);
				}
			}
			
			createCell(dataTable, "Total", fontColor, bgColor, 12f, true, fontColor, 2f, rightAlignment,
					middleAlignment, true, 1, 5, null);
			
			createCell(dataTable, totalAmount.toString(), fontColor, bgColor, 10f, true, fontColor, 2f, centerAlignment,
					middleAlignment, true, 1, 1, basicFont);
			
			document.add(dataTable);
			
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Boolean isRawMaterialDone = menuItemRawMaterialServiceImpl.checkEventId(eventId);
			Boolean isMenuAllocationDone = menuItemRawMaterialServiceImpl.checkEventId2(eventId);
			
			Paragraph heading = new Paragraph().add(new Text("Agency Price")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(0f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			float[] columnWidth3 = { 18f, 27f, 15f, 10f, 11f, 10f, 12f };
			Table agencyTbl = new Table(UnitValue.createPercentArray(columnWidth3));
			agencyTbl.setWidth(UnitValue.createPercentValue(100));
			Paragraph data = null;
			Cell dataCell = null;
			BigDecimal finalTotal = BigDecimal.valueOf(0);

			data = new Paragraph().add(new Text("")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Agency Name")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Qty.")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Rate")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Trans. Rate")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Total")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Chef Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 7).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			agencyTbl.addCell(dataCell);

			Set<Long> itemIds = new HashSet<>();
			Set<String> itemNames = new HashSet<>();
			
			Map<String, BigDecimal> chefAgencyTotal = new HashMap<>();
			Map<String, BigDecimal> outsourceTotal = new HashMap<>();
			Map<String, BigDecimal> labourTotal = new HashMap<>();
			Map<String, BigDecimal> totalCosting = new HashMap<>();
			
			BigDecimal totalFunctionPerson = BigDecimal.valueOf(0);
			
			List<CostingReportResponseDto> allItems = new ArrayList<>();

			List<CostingReportResponseDto> allFunctions = menuItemRawMaterialServiceImpl
					.getAllFunctionDetailsForCosting(eventId, lang);
			BigDecimal extraTotal = BigDecimal.ZERO;
			for (int k = 0; k < allFunctions.size(); k++) {
				CostingReportResponseDto fun = allFunctions.get(k);
				
				Long eventFunctionId = fun.getEventFunctionId();
				String functionName = fun.getFunctionName() == null || fun.getFunctionName().isEmpty() ? ""
						: fun.getFunctionName();
				BigDecimal person = fun.getFunctionPerson();
				if (person == null) {
				    person = BigDecimal.ZERO;
				}
				
				totalFunctionPerson = totalFunctionPerson.add(person);
				
				List<CostingReportResponseDto> allItemCategories = menuItemRawMaterialServiceImpl
						.getFunctionCategoryDetailsForCosting(eventId, eventFunctionId, lang, userid);
				
				for (CostingReportResponseDto cat : allItemCategories) {
					Long catId = cat.getMenuCategoryId();
					
					if (isRawMaterialDone) {
						System.out.println("1");
						allItems = menuItemRawMaterialServiceImpl.getFunctionCategoryItemDetailsForCosting3(eventId,
								eventFunctionId, catId, lang);
					} else if (isMenuAllocationDone) {
						System.out.println("2");
						allItems = menuItemRawMaterialServiceImpl.getFunctionCategoryItemDetailsForCosting2(eventId,
								eventFunctionId, catId, lang);
					} else {
						System.out.println("3");
						allItems = menuItemRawMaterialServiceImpl.getFunctionCategoryItemDetailsForCosting(eventId,
								eventFunctionId, catId, lang);
					}
					
					if (allItems.size() > 0) {
						for (CostingReportResponseDto item : allItems) {
							Long itemId = item.getMenuItemId();
							itemIds.add(itemId);
						}
					}
				}
			}
			List<Long> itemIdList = new ArrayList<>();
			itemIdList = itemIds.stream().collect(Collectors.toList());

			List<CostingReportAgencyResponseDto> agencyReport = new ArrayList<>();

			if (isRawMaterialDone || isMenuAllocationDone) {
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport2(eventId, lang,
						true, false);
			} else {
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport(eventId, itemIdList, lang, true,
						false);
			}

			for (CostingReportAgencyResponseDto agency : agencyReport) {
				String functionName = agency.getFunctionName() != null ? agency.getFunctionName() : "";
				String itemName = agency.getItemName() != null ? agency.getItemName() : "";
				String agencyName = agency.getAgencyName() != null ? agency.getAgencyName() : "";
				String unit = agency.getUnitName() != null ? agency.getUnitName() : "";
				String quantity = agency.getQuantity() != null ? agency.getQuantity() : "";
				String chefLabourPrice = agency.getChefLabourPrice() != null ? agency.getChefLabourPrice() : "";
				BigDecimal totalChefLabourPrice = agency.getTotalChefLabourPrice() != null
						? agency.getTotalChefLabourPrice().setScale(2)
						: BigDecimal.ZERO;
				BigDecimal transRate = agency.getTransRate() != null ? agency.getTransRate().setScale(2)
						: BigDecimal.ZERO.setScale(2);

				data = new Paragraph().add(new Text(functionName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(itemName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(agencyName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(quantity)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(chefLabourPrice)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(transRate.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(totalChefLabourPrice.toString())).setFont(basicFont)
						.setFontSize(10f).setMultipliedLeading(1f).setPaddingLeft(2f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				finalTotal = finalTotal.add(totalChefLabourPrice);

				chefAgencyTotal.merge(agencyName, totalChefLabourPrice, BigDecimal::add);

			}

			data = new Paragraph().add(new Text("Outsource Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 7).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			agencyTbl.addCell(dataCell);

			if (isRawMaterialDone || isMenuAllocationDone) {
//				itemIdList = menuItemRawMaterialRepository.getOutsourceItemAllocatedId(eventId);
				System.out.println("1");
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport2(eventId, lang,
						false, true);
			} else {
				System.out.println("2");
				agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport(eventId, itemIdList, lang,
						false, true);
			}

			for (CostingReportAgencyResponseDto agency : agencyReport) {

				String functionName = agency.getFunctionName() != null ? agency.getFunctionName() : "";
				String itemName = agency.getItemName() != null ? agency.getItemName() : "";
				String agencyName = agency.getAgencyName() != null ? agency.getAgencyName() : "";
				String unit = agency.getUnitName() != null ? agency.getUnitName() : "";
				String quantity = agency.getQuantity() != null ? agency.getQuantity() : "";
				BigDecimal outSourcePrice = agency.getOutsourcePrice() != null
						? agency.getOutsourcePrice().setScale(2, RoundingMode.HALF_UP)
						: BigDecimal.ZERO.setScale(2);
				BigDecimal totalOutsourcePrice = agency.getTotalOutsourcePrice() != null
						? agency.getTotalOutsourcePrice().setScale(2, RoundingMode.HALF_UP)
						: BigDecimal.ZERO.setScale(2);
				BigDecimal transRate = agency.getTransRate() != null ? agency.getTransRate().setScale(2)
						: BigDecimal.ZERO.setScale(2);

				data = new Paragraph().add(new Text(functionName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(itemName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(agencyName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(quantity + " " + unit)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(outSourcePrice.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(transRate.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(totalOutsourcePrice.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				finalTotal = finalTotal.add(totalOutsourcePrice);

				outsourceTotal.merge(agencyName, totalOutsourcePrice, BigDecimal::add);

			}

			data = new Paragraph().add(new Text("Labour Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 7).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			agencyTbl.addCell(dataCell);

			agencyReport = menuItemRawMaterialServiceImpl.getLabourForCostingReport(eventId, lang);

			for (CostingReportAgencyResponseDto agency : agencyReport) {
				// Check all values at the start of the loop
				String functionName = agency.getFunctionName() != null ? agency.getFunctionName() : "";
				String itemName = agency.getItemName() != null ? agency.getItemName() : "";
				String agencyName = agency.getAgencyName() != null ? agency.getAgencyName() : "";
				String quantity = agency.getQuantity() != null ? agency.getQuantity() : "0";
				String labourPrice = agency.getLabourPrice() != null
						? agency.getLabourPrice().setScale(2, RoundingMode.HALF_UP).toString()
						: "0.00";
				String totalLabourPrice = agency.getTotalLabourPrice() != null
						? agency.getTotalLabourPrice().setScale(2, RoundingMode.HALF_UP).toString()
						: "0.00";
				BigDecimal totalPriceValue = agency.getTotalLabourPrice() != null
						? agency.getTotalLabourPrice().setScale(2)
						: BigDecimal.ZERO.setScale(2);
				BigDecimal transRate = agency.getTransRate() != null ? agency.getTransRate().setScale(2)
						: BigDecimal.ZERO.setScale(2);

				// Now assign to data and add cells
				data = new Paragraph().add(new Text(functionName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(itemName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(agencyName)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(quantity)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(labourPrice)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(transRate.toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(totalLabourPrice)).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f).setPaddingLeft(2f).setVerticalAlignment(VerticalAlignment.MIDDLE);
				dataCell = new Cell().add(data);
				agencyTbl.addCell(dataCell);

				// Accumulate totals
				finalTotal = finalTotal.add(totalPriceValue);
				labourTotal.merge(agencyName, totalPriceValue, BigDecimal::add);
			}

			data = new Paragraph().add(new Text("Final Amount")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 6).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			agencyTbl.addCell(dataCell);

			data = new Paragraph().add(new Text(finalTotal.toString())).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell().add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			agencyTbl.addCell(dataCell);

			document.add(agencyTbl);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			
			float[] columnWidth4 = { 70f, 30f };
			Table bifercationTbl = new Table(UnitValue.createPercentArray(columnWidth4));
			bifercationTbl.setWidth(UnitValue.createPercentValue(100));
			data = null;
			dataCell = null;
			finalTotal = BigDecimal.valueOf(0);

			data = new Paragraph().add(new Text("Costing Bifurcation")).setFont(basicFont).setFontSize(14f)
					.setMultipliedLeading(1.5f).setFontColor(fontColor);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setTextAlignment(TextAlignment.CENTER)
					.setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Agency Name")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Total")).setFont(basicFont).setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Chef Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : chefAgencyTotal.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

				totalCosting.merge("Chef Labour Price", entry.getValue(), BigDecimal::add);

			}

			data = new Paragraph().add(new Text("Outsource Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : outsourceTotal.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

				totalCosting.merge("Outside Price", entry.getValue(), BigDecimal::add);
			}

			data = new Paragraph().add(new Text("Labour Agency")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : labourTotal.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().toString())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

				totalCosting.merge("Labour Price", entry.getValue(), BigDecimal::add);
			}

			List<RawMaterialCategoryRateResponseDto> responseDto = eventDishCostingService
					.getRawMaterialTotalCategoryWise(eventId, -1l, isRawMaterialDone, isMenuAllocationDone);
			
			data = new Paragraph().add(new Text("Raw Material")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

//			BigDecimal disposableItemAmount = eventRawMaterialDisposableRepository.getTotalDisposibleAmount(eventId);
//			if (disposableItemAmount == null) {
//				disposableItemAmount = BigDecimal.ZERO;
//			}
//			rawMataterialTotal.merge("DISPOSABLE",
//					disposableItemAmount != null ? disposableItemAmount : BigDecimal.ZERO, BigDecimal::add);

			for (Entry<String, BigDecimal> rawCat : rawMataterialTotal.entrySet()) {
				data = new Paragraph().add(new Text(rawCat.getKey())).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(rawCat.getValue() != null ? rawCat.getValue().toString() : "")).setFont(basicFont).setFontSize(10f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setPaddingLeft(2f);
				bifercationTbl.addCell(dataCell);

				BigDecimal totalRate =
				        rawCat.getValue() != null
				        ? rawCat.getValue()
				        : BigDecimal.ZERO;

				finalTotal = finalTotal.add(totalRate);

				totalCosting.merge("Raw Material Price", totalRate, BigDecimal::add);
			}
			
//			data = new Paragraph().add(new Text("EXTRA RAW MATERIAL")).setFont(basicFont).setFontSize(10f)
//					.setMultipliedLeading(1f);
//			dataCell = new Cell().add(data).setPaddingLeft(2f);
//			bifercationTbl.addCell(dataCell);
//			
//			data = new Paragraph().add(new Text(extraTotal.toString())).setFont(basicFont).setFontSize(10f)
//					.setMultipliedLeading(1f);
//			dataCell = new Cell().add(data).setPaddingLeft(2f);
//			bifercationTbl.addCell(dataCell);
//
//			totalCosting.merge("Raw Material Price", extraTotal, BigDecimal::add);
			
			data = new Paragraph().add(new Text("Total Raw Material Price")).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell().add(data).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text(totalCosting.get("Raw Material Price").toString())).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell().add(data).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);
			
			data = new Paragraph().add(new Text("Final Amount")).setFont(boldFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell().add(data).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

			data = new Paragraph().add(new Text(finalTotal.add(extraTotal).toString())).setFont(boldFont).setFontSize(12f)
					.setMultipliedLeading(1f).setFontColor(fontColor);
			dataCell = new Cell().add(data).setBackgroundColor(bgColor);
			bifercationTbl.addCell(dataCell);

			document.add(bifercationTbl);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			float[] columnWidth5 = { 60f, 40f };
			Table totalCostingTbl = new Table(UnitValue.createPercentArray(columnWidth4));
			totalCostingTbl.setWidth(UnitValue.createPercentValue(100));
			data = null;
			dataCell = null;
			finalTotal = BigDecimal.valueOf(0);

			data = new Paragraph().add(new Text("Total Costing")).setFont(basicFont).setFontSize(14f)
					.setMultipliedLeading(1.5f).setFontColor(fontColor);
			dataCell = new Cell(1, 2).add(data).setPaddingLeft(12f).setTextAlignment(TextAlignment.CENTER)
					.setBackgroundColor(bgColor);
			totalCostingTbl.addCell(dataCell);

			for (Map.Entry<String, BigDecimal> entry : totalCosting.entrySet()) {
				data = new Paragraph().add(new Text(entry.getKey())).setFont(basicFont).setFontSize(12f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
				totalCostingTbl.addCell(dataCell);

				data = new Paragraph().add(new Text(entry.getValue().toString())).setFont(basicFont).setFontSize(12f)
						.setMultipliedLeading(1f);
				dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
				totalCostingTbl.addCell(dataCell);

				finalTotal = finalTotal.add(entry.getValue());

			}

//			dataCell = new Cell().setBorder(Border.NO_BORDER).setMinHeight(10f);
//			totalCostingTbl.addCell(dataCell);
//			
//			dataCell = new Cell().setBorder(Border.NO_BORDER).setMinHeight(5f);
//			totalCostingTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Total Amount").simulateBold()).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			data = new Paragraph().add(new Text(finalTotal.toString()).simulateBold()).setFont(basicFont)
					.setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			data = new Paragraph().add(new Text("Per Dish Amount").simulateBold()).setFont(basicFont).setFontSize(12f)
					.setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			BigDecimal perDishAmount = BigDecimal.ZERO;

			if (totalFunctionPerson.compareTo(BigDecimal.ZERO) > 0) {
			    perDishAmount = finalTotal.divide(totalFunctionPerson, 2, RoundingMode.HALF_UP);
			}

			data = new Paragraph().add(new Text(perDishAmount.toString()).simulateBold()).setFont(basicFont)
					.setFontSize(12f).setMultipliedLeading(1f);
			dataCell = new Cell().add(data).setTextAlignment(TextAlignment.CENTER);
			totalCostingTbl.addCell(dataCell);

			document.add(totalCostingTbl);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(storeIssueData.getEventDate()),
							"Costing Report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}
	}
	
	private void createCell(Table table, String content, Color fontColor, Color bgColor, float fontSize,
			Boolean isBorder, Color borderColor, float padding, TextAlignment textAlignment,
			VerticalAlignment verticalAlignment, Boolean isBold, Integer rowSpan, Integer colSpan, PdfFont basicFont) {
		Cell cell = new Cell(rowSpan, colSpan).add(
				new Paragraph().add(content).setFontSize(fontSize).setFontColor(fontColor))
				.setPadding(padding)
				.setTextAlignment(textAlignment)
				.setVerticalAlignment(verticalAlignment);
		
		if(isBold) {
			cell.simulateBold();
		}
		
		if(basicFont != null) {
			cell.setFont(basicFont);
		}
		
		if(bgColor != null) {
			cell.setBackgroundColor(bgColor);
		}
		
		if(isBorder) {
			cell.setBorder(new SolidBorder(borderColor, 1f));
		}else {
			cell.setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);
	}
	
	public StoreIssueCostingReportResponseDto getStoreIssueCostingReport(Long eventId, Integer lang) {

	    List<Object[]> result = purchaseOrderStoreRepository.getStoreIssueDataForCostingReport(eventId, lang);

	    if (result == null || result.isEmpty()) {
	        return null;
	    }

	    Object[] first = result.get(0);

	    StoreIssueCostingReportResponseDto response = new StoreIssueCostingReportResponseDto();

	    response.setEventId(first[0] == null ? 0L : ((Number) first[0]).longValue());
	    response.setEventNo(first[1] == null ? "" : first[1].toString());
	    response.setEventName(first[2] == null ? "" : first[2].toString());
//	    response.setEventDate(formatDate(first[3]));
	    response.setEventDate("");

	    response.setPartyId(first[4] == null ? 0L : ((Number) first[4]).longValue());
	    response.setPartyName(first[5] == null ? "" : first[5].toString());
	    response.setPartyContactNo(first[6] == null ? "" : first[6].toString());
	    response.setPartyEmail(first[7] == null ? "" : first[7].toString());
	    response.setPartyAddress(first[8] == null ? "" : first[8].toString());
	    response.setEventVenue(first[9] == null ? "" : first[9].toString());

	    response.setUserId(first[10] == null ? 0L : ((Number) first[10]).longValue());
	    response.setCmpName(first[11] == null ? "" : first[11].toString());
	    response.setLogo(first[12] == null ? "" : first[12].toString());
	    response.setCmpContactNo(first[13] == null ? "" : first[13].toString());
	    response.setCmpEmail(first[14] == null ? "" : first[14].toString());
	    response.setCmpAddress(first[15] == null ? "" : first[15].toString());

	    Map<Long, StoreIssueDetailsResponseDto> categoryMap = new LinkedHashMap<>();

	    for (Object[] row : result) {

	        Long rawCatId = row[28] == null ? 0L : ((Number) row[28]).longValue();

	        StoreIssueDetailsResponseDto category = categoryMap.get(rawCatId);

	        if (category == null) {
	            category = new StoreIssueDetailsResponseDto();
	            category.setRawCatId(rawCatId);
	            category.setRawCatName(row[29] == null ? "" : row[29].toString());
	            category.setItems(new ArrayList<>());
	            categoryMap.put(rawCatId, category);
	        }

	        StoreIssueItemsResponseDto item = new StoreIssueItemsResponseDto();

	        // Store Issue Details
	        item.setStoreIssueId(row[16] == null ? 0L : ((Number) row[16]).longValue());
	        item.setPoCode(row[17] == null ? "" : row[17].toString());
//	        item.setPoDate(formatDate(row[18]));
	        item.setPoDate("");
	        item.setRemarks(row[19] == null ? "" : row[19].toString());
	        item.setVoucherNo(row[20] == null ? "" : row[20].toString());

	        item.setStockTypeId(row[21] == null ? 0L : ((Number) row[21]).longValue());
	        item.setStockTypeName(row[22] == null ? "" : row[22].toString());

	        item.setStatus(row[23] == null ? "" : row[23].toString());

	        item.setCrId(row[24] == null ? 0L : ((Number) row[24]).longValue());
	        item.setCrCode(row[25] == null ? "" : row[25].toString());

	        item.setKitchenTypeId(row[26] == null ? 0L : ((Number) row[26]).longValue());
	        item.setKitchenType(row[27] == null ? "" : row[27].toString());

	        // Item Details
	        item.setStoreIssueDetailId(row[30] == null ? 0L : ((Number) row[30]).longValue());
	        item.setRawItemId(row[31] == null ? 0L : ((Number) row[31]).longValue());
	        item.setRawItemName(row[32] == null ? "" : row[32].toString());

	        if (row[33] instanceof BigDecimal) {
	            item.setQty((BigDecimal) row[33]);
	        } else if (row[33] != null) {
	            item.setQty(BigDecimal.valueOf(((Number) row[33]).doubleValue()));
	        } else {
	            item.setQty(BigDecimal.ZERO);
	        }

	        item.setUnitId(row[34] == null ? 0L : ((Number) row[34]).longValue());
	        item.setUnitName(row[35] == null ? "" : row[35].toString());

	        if (row[36] instanceof BigDecimal) {
	            item.setAvgRate((BigDecimal) row[36]);
	        } else if (row[36] != null) {
	            item.setAvgRate(BigDecimal.valueOf(((Number) row[36]).doubleValue()));
	        } else {
	            item.setAvgRate(BigDecimal.ZERO);
	        }

	        if (row[37] instanceof BigDecimal) {
	            item.setTotalAmount((BigDecimal) row[37]);
	        } else if (row[37] != null) {
	            item.setTotalAmount(BigDecimal.valueOf(((Number) row[37]).doubleValue()));
	        } else {
	            item.setTotalAmount(BigDecimal.ZERO);
	        }

	        category.getItems().add(item);
	    }

	    response.setCategories(new ArrayList<>(categoryMap.values()));

	    return response;
	}
	
	private String formatDate(Object value) {

	    if (value == null) {
	        return "";
	    }

	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	    if (value instanceof java.sql.Timestamp) {
	        return sdf.format((java.sql.Timestamp) value);
	    }

	    if (value instanceof java.sql.Date) {
	        return sdf.format((java.sql.Date) value);
	    }

	    if (value instanceof java.util.Date) {
	        return sdf.format((java.util.Date) value);
	    }

	    return value.toString();
	}
	
	private EventWiseStoreIssuePricing getStoreIssuePriceByEventId(Long eventId) {
		Object result = purchaseOrderStoreRepository.getStoreIssuePriceByEventId(eventId);

		if (result == null) {
			return null;
		}

		Object[] row = (Object[]) result;

		Long resultEventId = row[0] != null ? ((Number) row[0]).longValue() : eventId;

		BigDecimal totalIssuePrice = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;

		BigDecimal totalReturnPrice = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

		BigDecimal totalPrice = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;

		return new EventWiseStoreIssuePricing(resultEventId, totalIssuePrice, totalReturnPrice, totalPrice);
	}
}
