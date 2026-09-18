package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.controller.CityMasterController;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.FunctionMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.EventGeneralFixRawResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.EventRawMaterialCategoryResponse;
import com.crmportal.response.dto.EventRawMaterialInfoDto;
import com.crmportal.response.dto.EventRawMaterialPartyDTO;
import com.crmportal.response.dto.EventWiseGeneralFixCategoryResponseDto;
import com.crmportal.response.dto.EventWiseGeneralFixDetailsResponseDto;
import com.crmportal.response.dto.EventWiseGeneralFixResponseDto;
import com.crmportal.response.dto.GeneralFixRawResponseDto;
import com.crmportal.response.dto.GeneralFixResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.response.dto.SupplierRawMaterialResponseDto;
import com.crmportal.service.EventFunctionGeneralFixService;
import com.crmportal.service.EventMasterService;
import com.crmportal.service.EventRawMaterialService;
import com.crmportal.service.RawMaterialReportService;
import com.crmportal.utility.PageNumberHandler;
import com.crmportal.utility.RawMaterialEventDetailHeaderEventHandler;
import com.crmportal.utility.RawMaterialHeaderEventHandlerNew;
import com.crmportal.utility.RoundOffUtility;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
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
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.splitting.ISplitCharacters;

import springfox.documentation.spring.web.plugins.Docket;

@Service
public class RawMaterialReportServiceImpl implements RawMaterialReportService {

	private final Docket api;

	private final CityMasterController cityMasterController;

	private final FunctionMasterRepository functionMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	EventMasterService eventMasterService;

	@Autowired
	EventRawMaterialService eventRawMaterialService;

	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	RoundOffUtility roundOffUtility;

	@Autowired
	Environment environment;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	EventFunctionGeneralFixService eventFunctionGeneralFixService;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	RawMaterialReportServiceImpl(FunctionMasterRepository functionMasterRepository,
			CityMasterController cityMasterController, Docket api) {
		this.functionMasterRepository = functionMasterRepository;
		this.cityMasterController = cityMasterController;
		this.api = api;
	}

	public static String formatNumber(double value) {
		BigDecimal bd = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
		return bd.stripTrailingZeros().toPlainString();
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
	public String generateRawaterialReportType1(Long eventId, HttpServletRequest re, Integer lang,
			Long adminTemplateModuleId, Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyLogo, Integer isCompanyDetails, Integer isPartyDetails, List<Long> eventFunctionIds,
			List<Long> rawMaterialCatIds, Integer isAddStoreIssue) {

		try {
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "Event Not Found";

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;

			String customer = "", venue = "", eventName = "", customerNameLabel = "", venueLabel = "", dateLabel = "",
					functionLabel = "", personLabel = "", eventNameLabel = "", mobileNoLabel = "", weightLabel = "",
					priceLabel = "", nameLabel = "", mobileNo = "";

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
				eventName = event.getEventType() != null ? event.getEventType().getNameHindi() : "";
				dateLabel = "दिनांक";
				customerNameLabel = "ग्राहक का नाम";
				venueLabel = "स्थान";
				functionLabel = "समारोह";
				personLabel = "व्यक्ति";
				eventNameLabel = "कार्यक्रम का नाम";
				mobileNoLabel = "मोबाइल नंबर";
				weightLabel = "वजन";
				priceLabel = "कीमत";
				nameLabel = "नाम";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
				eventName = event.getEventType() != null ? event.getEventType().getNameGujarati() : "";
				dateLabel = "તારીખ";
				customerNameLabel = "ગ્રાહકનું નામ";
				venueLabel = "સ્થળ";
				functionLabel = "કાર્યક્રમ";
				personLabel = "વ્યક્તિ";
				eventNameLabel = "કાર્યક્રમ નુ નામ";
				mobileNoLabel = "મોબાઇલ નંબર";
				weightLabel = "વજન";
				priceLabel = "કિંમત";
				nameLabel = "નામ";
			} else {
				font = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");
				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
				eventName = event.getEventType() != null ? event.getEventType().getNameEnglish() : "";
				dateLabel = "Date";
				customerNameLabel = "Customer Name";
				venueLabel = "Venue";
				functionLabel = "Function";
				personLabel = "Person";
				eventNameLabel = "Event Name";
				mobileNoLabel = "Mobile No.";
				weightLabel = "WEIGHT";
				priceLabel = "Price";
				nameLabel = "NAME";
			}
			mobileNo = event.getMobileno();
			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			List<EventRawMaterialCategoryResponse> data = new ArrayList<>();
			if (eventFunctionIds == null || eventFunctionIds.isEmpty()) {
				data = eventRawMaterialService.getEventRawMaterialByEventId(eventId, rawMaterialCatIds,
						isAddStoreIssue);
			} else {
				data = eventRawMaterialService.getEventRawMaterialByEventIdAndEventFunctionId(eventId, eventFunctionIds,
						rawMaterialCatIds);
			}

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(
					dir + "/"
							+ getReportName(event.getParty().getNameEnglish(),
									formatDate(event.getEventStartDateTime().split(" ")[0]), "raw material report")
							+ ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDoc, PageSize.A4);
			document.setMargins(110, 40, 40, 40);

			String logoimg = getLogo(userid);
//	        String logoimg = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource(logoimg);

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new RawMaterialHeaderEventHandler(font, fontBold,
						logo, (lang == 0 ? eventData.getCompanyName().toUpperCase() : eventData.getCompanyName()),
						(lang == 0 ? eventData.getOfficeNo() : eventData.getOfficeNo()), eventData.getCompanyEmail()));
			}

			Cell cell;

			Table wrapperTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
			wrapperTable.setWidth(UnitValue.createPercentValue(100f));

			Table clientTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 28f, 20f, 2f, 28f }));
			clientTable.setWidth(UnitValue.createPercentValue(100f));

			if (isPartyDetails == 1) {
				cell = new Cell()
						.add(new Paragraph().add(new Text(customerNameLabel).simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(customer != null ? customer.toUpperCase() : "")).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(mobileNoLabel).simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(mobileNo != null ? mobileNo : "")).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);
			}
			cell = new Cell()
					.add(new Paragraph().add(new Text(eventNameLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(eventName != null ? eventName.toUpperCase() : "")).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(dateLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(
							event.getEventStartDateTime() != null ? event.getEventStartDateTime().split(" ")[0] : ""))
							.setFont(font).setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(venueLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(venue != null ? venue.toUpperCase() : "")).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			wrapperTable.addCell(clientTable);

			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,
					new RawMaterialEventDetailHeaderEventHandler(wrapperTable, isCompanyDetails == 1 ? 120 : 20));

			/* ================= 3 COLUMN LAYOUT ================= */
			float pageWidth = PageSize.A4.getWidth();
			float usableWidth = pageWidth - 80;
			float columnWidth = usableWidth / 3;

			float top = (isCompanyDetails == 1 ? PageSize.A4.getTop() - 130 : PageSize.A4.getTop() - 30)
					- calculateTableHeight(wrapperTable, pdfDoc);
			float bottom = PageSize.A4.getBottom() + 40;

			Rectangle[] columns = new Rectangle[] { new Rectangle(40, bottom, columnWidth, top - bottom),
					new Rectangle(40 + columnWidth, bottom, columnWidth, top - bottom),
					new Rectangle(40 + columnWidth * 2, bottom, columnWidth, top - bottom) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
				List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl
						.getMultiFunctionDetails(eventId, eventFunctionIds, lang);

				MenuQuantityReponseDto functionDto = null;
				StringBuilder functions = new StringBuilder();
				Integer totalPax = 0;
				for (int i = 0; i < allFunctions.size(); i++) {
					Integer pax = 0;
					String functionName = "";
					if (eventFunctionIds.contains(allFunctions.get(i).getEventFunctionId())) {
						functionDto = allFunctions.get(i);
						pax = functionDto.getPerson();
						functionName = functionDto.getFunctionName() + " (" + pax + ")";
						totalPax += pax;
					}
					if (functions.length() > 0) {
						functions.append(", ");
					}
					functions.append(functionName);
				}

				String functionNames = functions.toString();

				document.add(new Paragraph(
						functionNames.toUpperCase() + " FOR " + totalPax + " PAX \n" + functionDto.getFunctionTime())
						.setFont(fontBold).setFontSize(14).setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f))
						.setMarginBottom(0).setPadding(4));

			}

			/* FORCE FIRST COLUMN START */
//	        document.add(new Paragraph("").setMarginTop(1));

			for (EventRawMaterialCategoryResponse cat : data) {

				/* ---------- COLUMN HEADER (ONLY ON FIRST ROW OF COLUMN FLOW) ---------- */
				Table table = new Table(new float[] { 80, 20 });
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginTop(0);
				table.setMarginBottom(0);

				// NAME | WEIGHT header (always first in column)
				table.addCell(
						new Cell().add(new Paragraph(nameLabel).setFont(fontBold).setFontSize(lang == 0 ? 10 : 12))
								.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));

				table.addCell(
						new Cell().add(new Paragraph(weightLabel).setFont(fontBold).setFontSize(lang == 0 ? 10 : 12))
								.setTextAlignment(TextAlignment.RIGHT)
								.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));

				/* ---------- CATEGORY ROW (SPAN 2 COLUMNS, NO GAP) ---------- */
				String catName = menuPreparationServiceImpl.formatText(getCategoryNameByLang(cat, lang), lang);

				if (lang == 0)
					catName = catName.toUpperCase();

				table.addCell(new Cell(1, 2).add(new Paragraph(catName).setFont(fontBold).setFontSize(11))
						.setBackgroundColor(ColorConstants.LIGHT_GRAY)
						.setBorder(new SolidBorder(ColorConstants.BLACK, 0.7f)).setPadding(4));

				/* ---------- RAW MATERIAL ROWS ---------- */
				for (EventRawMaterialInfoDto rm : cat.getRawMaterials()) {

					String rmName = menuPreparationServiceImpl.formatText(getRawMaterialNameByLang(rm, lang), lang);

					if (lang == 0)
						rmName = rmName.toUpperCase();

					table.addCell(new Cell().add(new Paragraph(rmName).setFont(font).setFontSize(10))
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));

					table.addCell(new Cell()
							.add(new Paragraph(
									formatNumber(rm.getFinalQty().doubleValue()) + " " + rm.getUnitName().toLowerCase())
									.setFont(font).setFontSize(10))
							.setTextAlignment(TextAlignment.RIGHT)
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));
				}

				document.add(table);
			}

			document.close();

			/* ================= URL ================= */
			String scheme = "prod".equalsIgnoreCase(activeProfile) ? "https" : "http";
			return scheme + "://" + re.getServerName()
					+ ((re.getServerPort() == 80 || re.getServerPort() == 443) ? "" : ":" + re.getServerPort())
					+ re.getContextPath() + "/api/download/pdf/" + event.getEventNo() + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]), "raw material report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-1 PDF generation failed", e);
		}
	}

	@Override
	public String generateRawaterialReportType1_2(Long eventId, HttpServletRequest re, Integer lang,
			Long adminTemplateModuleId, Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyLogo, Integer isCompanyDetails, Integer isPartyDetails, List<Long> eventFunctionIds,
			List<Long> rawMaterialCatIds, Integer isAddStoreIssue) {

		try {
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "Event Not Found";

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;

			String customer = "", venue = "", dateLabel = "", customerNameLabel = "", venueLabel = "",
					functionLabel = "", personLabel = "", eventNameLabel = "", mobileNoLabel = "", mobileNo = "",
					eventName = "", weightLabel = "", priceLabel = "", nameLabel = "";

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
				eventName = event.getEventType() != null ? event.getEventType().getNameHindi() : "";
				dateLabel = "दिनांक";
				customerNameLabel = "ग्राहक का नाम";
				venueLabel = "स्थान";
				functionLabel = "समारोह";
				personLabel = "व्यक्ति";
				eventNameLabel = "कार्यक्रम का नाम";
				mobileNoLabel = "मोबाइल नंबर";
				weightLabel = "वजन";
				priceLabel = "कीमत";
				nameLabel = "नाम";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
				eventName = event.getEventType() != null ? event.getEventType().getNameGujarati() : "";
				dateLabel = "તારીખ";
				customerNameLabel = "ગ્રાહકનું નામ";
				venueLabel = "સ્થળ";
				functionLabel = "કાર્યક્રમ";
				personLabel = "વ્યક્તિ";
				eventNameLabel = "કાર્યક્રમ નુ નામ";
				mobileNoLabel = "મોબાઇલ નંબર";
				weightLabel = "વજન";
				priceLabel = "કિંમત";
				nameLabel = "નામ";
			} else {
				font = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/timesbd.ttf");
				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
				eventName = event.getEventType() != null ? event.getEventType().getNameEnglish() : "";
				dateLabel = "Date";
				customerNameLabel = "Customer Name";
				venueLabel = "Venue";
				functionLabel = "Function";
				personLabel = "Person";
				eventNameLabel = "Event Name";
				mobileNoLabel = "Mobile No.";
				weightLabel = "WEIGHT";
				priceLabel = "Price";
				nameLabel = "NAME";
			}
			mobileNo = event.getMobileno() != null ? event.getMobileno() : "";
			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			List<EventRawMaterialCategoryResponse> data = new ArrayList<>();
			if (eventFunctionIds == null || eventFunctionIds.isEmpty()) {
				data = eventRawMaterialService.getEventRawMaterialByEventId(eventId, rawMaterialCatIds,
						isAddStoreIssue);
			} else {
				data = eventRawMaterialService.getEventRawMaterialByEventIdAndEventFunctionId(eventId, eventFunctionIds,
						rawMaterialCatIds);
			}

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(
					dir + "/"
							+ getReportName(event.getParty().getNameEnglish(),
									formatDate(event.getEventStartDateTime().split(" ")[0]), "raw material report")
							+ ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDoc, PageSize.A4);
			document.setMargins(110, 40, 40, 40);

			String logoimg = getLogo(userid);
//	        String logoimg = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource(logoimg);

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new RawMaterialHeaderEventHandler(font, fontBold,
						logo, (lang == 0 ? eventData.getCompanyName().toUpperCase() : eventData.getCompanyName()),
						(lang == 0 ? eventData.getOfficeNo() : eventData.getOfficeNo()), eventData.getCompanyEmail()));
			}

			/* ================= 3 COLUMN LAYOUT ================= */
//			float pageWidth = PageSize.A4.getWidth();
//			float usableWidth = pageWidth - 80;
//			float columnWidth = usableWidth / 3;
//
//			float top = isCompanyLogo == 1 || isCompanyDetails == 1 ? PageSize.A4.getTop() - 130
//					: PageSize.A4.getTop() - 20;
//			float bottom = PageSize.A4.getBottom() + 40;
//
//			Rectangle[] columns = new Rectangle[] { new Rectangle(40, bottom, columnWidth, top - bottom),
//					new Rectangle(40 + columnWidth, bottom, columnWidth, top - bottom),
//					new Rectangle(40 + columnWidth * 2, bottom, columnWidth, top - bottom) };
			Cell cell;

			Table wrapperTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
			wrapperTable.setWidth(UnitValue.createPercentValue(100f));

			Table clientTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 28f, 20f, 2f, 28f }));
			clientTable.setWidth(UnitValue.createPercentValue(100f));

			if (isPartyDetails == 1) {
				cell = new Cell()
						.add(new Paragraph().add(new Text(customerNameLabel).simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(customer != null ? customer.toUpperCase() : "")).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(mobileNoLabel).simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text(mobileNo != null ? mobileNo : "")).setFont(font)
								.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				clientTable.addCell(cell);
			}
			cell = new Cell()
					.add(new Paragraph().add(new Text(eventNameLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(eventName != null ? eventName.toUpperCase() : "")).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(dateLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(
							event.getEventStartDateTime() != null ? event.getEventStartDateTime().split(" ")[0] : ""))
							.setFont(font).setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(venueLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(venue != null ? venue.toUpperCase() : "")).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			wrapperTable.addCell(clientTable);

			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,
					new RawMaterialEventDetailHeaderEventHandler(wrapperTable, isCompanyDetails == 1 ? 120 : 20));

			float wrapperTableHeight = calculateTableHeight(wrapperTable, pdfDoc);

			float leftMargin = 40;
			float rightMargin = 40;
			float topMargin = (isCompanyDetails == 1 ? 130 : 30) + wrapperTableHeight;
			float top = PageSize.A4.getTop() - topMargin;
			float bottom = PageSize.A4.getBottom() + 40;
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = usableWidth / 2;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, top - bottom),
					new Rectangle(leftMargin + columnWidth, bottom, columnWidth, top - bottom) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
				List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl
						.getMultiFunctionDetails(eventId, eventFunctionIds, lang);

				MenuQuantityReponseDto functionDto = null;
				StringBuilder functions = new StringBuilder();
				Integer totalPax = 0;
				for (int i = 0; i < allFunctions.size(); i++) {
					Integer pax = 0;
					String functionName = "";
					if (eventFunctionIds.contains(allFunctions.get(i).getEventFunctionId())) {
						functionDto = allFunctions.get(i);
						pax = functionDto.getPerson();
						functionName = functionDto.getFunctionName() + " (" + pax + ")";
						totalPax += pax;
					}
					if (functions.length() > 0) {
						functions.append(", ");
					}
					functions.append(functionName);
				}

				String functionNames = functions.toString();

				document.add(new Paragraph(
						functionNames.toUpperCase() + " FOR " + totalPax + " PAX \n" + functionDto.getFunctionTime())
						.setFont(fontBold).setFontSize(14).setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f))
						.setMarginBottom(0).setPadding(4));

			}

			/* FORCE FIRST COLUMN START */
//	        document.add(new Paragraph("").setMarginTop(1));

			for (EventRawMaterialCategoryResponse cat : data) {

				/* ---------- COLUMN HEADER (ONLY ON FIRST ROW OF COLUMN FLOW) ---------- */
				Table table = new Table(UnitValue.createPercentArray(new float[] { 50f, 25f, 25f }));
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginTop(0);
				table.setMarginBottom(0);
				table.addHeaderCell(
						new Cell().add(new Paragraph(nameLabel).setFont(fontBold).setFontSize(lang == 0 ? 10 : 12))
								.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));
				table.addHeaderCell(
						new Cell().add(new Paragraph(weightLabel).setFont(fontBold).setFontSize(lang == 0 ? 10 : 12))
								.setTextAlignment(TextAlignment.RIGHT)
								.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));
				table.addHeaderCell(
						new Cell().add(new Paragraph(priceLabel).setFont(fontBold).setFontSize(lang == 0 ? 10 : 12))
								.setTextAlignment(TextAlignment.RIGHT)
								.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));

				/* ---------- CATEGORY ROW (SPAN 2 COLUMNS, NO GAP) ---------- */
				String catName = menuPreparationServiceImpl.formatText(getCategoryNameByLang(cat, lang), lang);

				if (lang == 0)
					catName = catName.toUpperCase();

				table.addCell(new Cell(1, 3).add(new Paragraph(catName).setFont(fontBold).setFontSize(11))
						.setBackgroundColor(ColorConstants.LIGHT_GRAY)
						.setBorder(new SolidBorder(ColorConstants.BLACK, 0.7f)).setPadding(4));

				/* ---------- RAW MATERIAL ROWS ---------- */
				BigDecimal totalPrice = BigDecimal.ZERO;
				for (EventRawMaterialInfoDto rm : cat.getRawMaterials()) {
					totalPrice = totalPrice.add(BigDecimal.valueOf(rm.getTotalPrice()));

					String rmName = menuPreparationServiceImpl.formatText(getRawMaterialNameByLang(rm, lang), lang);

					if (lang == 0)
						rmName = rmName.toUpperCase();

					table.addCell(new Cell().add(new Paragraph(rmName).setFont(font).setFontSize(10))
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));

					table.addCell(new Cell()
							.add(new Paragraph(formatNumber(rm.getFinalQty()) + " " + rm.getUnitName().toLowerCase())
									.setFont(font).setFontSize(10))
							.setTextAlignment(TextAlignment.RIGHT)
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));

					table.addCell(new Cell()
							.add(new Paragraph(formatNumber(rm.getTotalPrice())).setFont(font).setFontSize(10))
							.setTextAlignment(TextAlignment.RIGHT)
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));
				}

				table.addCell(new Cell(1, 2).add(new Paragraph("Total Price").setFont(font).setFontSize(9))
						.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(4)
						.setTextAlignment(TextAlignment.RIGHT));
				table.addCell(dataCell(totalPrice.toString(), font).setTextAlignment(TextAlignment.RIGHT));

				document.add(table);
			}

			document.close();

			/* ================= URL ================= */
			String scheme = "prod".equalsIgnoreCase(activeProfile) ? "https" : "http";
			return scheme + "://" + re.getServerName()
					+ ((re.getServerPort() == 80 || re.getServerPort() == 443) ? "" : ":" + re.getServerPort())
					+ re.getContextPath() + "/api/download/pdf/" + event.getEventNo() + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]), "raw material report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-1 PDF generation failed", e);
		}
	}

	@Override
	public String generateRawaterialReportType3(Long eventId, HttpServletRequest re, Integer lang,
			Long adminTemplateModuleId, Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyDetails, List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isPartyDetails,
			Integer isAddStoreIssue) {

		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "Event Not Found";

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;
			String customer = "", venue = "", weightLabel = "", priceLabel = "", pkgLabel = "", eventName = "";

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
				weightLabel = "वजन";
				priceLabel = "कीमत";
				pkgLabel = "पैकेज";
				eventName = event.getEventType() != null ? event.getEventType().getNameHindi() : "";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = font;
				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
				weightLabel = "વજન";
				priceLabel = "કિંમત";
				pkgLabel = "પેકેજ";
				eventName = event.getEventType() != null ? event.getEventType().getNameGujarati() : "";
			} else {
				font = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
				weightLabel = "WEIGHT";
				priceLabel = "Price";
				pkgLabel = "Pkg";
				eventName = event.getEventType() != null ? event.getEventType().getNameEnglish() : "";
			}

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			List<EventRawMaterialCategoryResponse> data = new ArrayList<>();
			if (eventFunctionIds == null || eventFunctionIds.isEmpty()) {
				data = eventRawMaterialService.getEventRawMaterialByEventId(eventId, rawMaterialCatIds,
						isAddStoreIssue);
			} else {
				data = eventRawMaterialService.getEventRawMaterialByEventIdAndEventFunctionId(eventId, eventFunctionIds,
						rawMaterialCatIds);
			}

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(dir + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]), "raw material with price report")
					+ ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDoc, PageSize.A4, true);
			document.setMargins(110, 40, 60, 40); // extra bottom for footer

			String logoimg = getLogo(userid);
//	        String logoimg = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource(logoimg);

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			String functionNames = "";
			Integer totalPax = 0;
			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
				Map<Long, EventFunctionMasterEntity> funcMap = eventFunctionMasterRepository
						.findByIdIn(eventFunctionIds).stream()
						.collect(Collectors.toMap(EventFunctionMasterEntity::getId, Function.identity()));

				StringBuilder functions = new StringBuilder();
				for (Entry<Long, EventFunctionMasterEntity> entry : funcMap.entrySet()) {
					Integer pax = entry.getValue().getPax() != null ? entry.getValue().getPax() : 0;
					totalPax = totalPax + pax;
					String functionName;
					if (lang == 1) {
						functionName = entry.getValue().getFunction().getNameHindi() + " (" + pax + ")";
					} else if (lang == 2) {
						functionName = entry.getValue().getFunction().getNameGujarati() + " (" + pax + ")";
					} else {
						functionName = entry.getValue().getFunction().getNameEnglish() + " (" + pax + ")";
					}

					if (functions.length() > 0) {
						functions.append(", ");
					}
					functions.append(functionName);
				}

				functionNames = functions.toString();
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new RawMaterialHeaderEventHandler(font, fontBold,
						logo, (lang == 0 ? eventData.getCompanyName().toUpperCase() : eventData.getCompanyName()),
						(lang == 0 ? eventData.getOfficeNo() : eventData.getOfficeNo()), eventData.getCompanyEmail()));
			}

			Table headerTable = null;
			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
				headerTable = createHeaderTable(font, fontBold, logo, (lang == 0 ? customer.toUpperCase() : customer),
						(lang == 0 ? venue.toUpperCase() : venue), dateTime, isCompanyDetails, functionNames, totalPax,
						"", lang, event.getMobileno(), eventName, event.getEventStartDateTime(), isPartyDetails);
			} else {
				headerTable = createHeaderTable(font, fontBold, logo, (lang == 0 ? customer.toUpperCase() : customer),
						(lang == 0 ? venue.toUpperCase() : venue), dateTime, isCompanyDetails, "", -1, "", lang,
						event.getMobileno(), eventName, event.getEventStartDateTime(), isPartyDetails);
			}

			float leftMargin = 40;
			float rightMargin = 40;
			float topMargin = isCompanyDetails == 1 ? 120 : 20;
			float top = PageSize.A4.getTop() - topMargin;
			float bottom = PageSize.A4.getBottom() + 60;
			float height = top - bottom - calculateTableHeight(headerTable, pdfDoc);
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = usableWidth / 2;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
					new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));
			RawMaterialHeaderEventHandlerNew headerHandler = new RawMaterialHeaderEventHandlerNew(headerTable,
					topMargin);
			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

			boolean isFirstCategory = true;
			for (EventRawMaterialCategoryResponse cat : data) {
				String catName = menuPreparationServiceImpl.formatText(getCategoryNameByLang(cat, lang), lang);
				System.out.println("catName : " + catName);
				if (lang == 0) {
					catName = catName.toUpperCase();
				}
				Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 25, 25 }), false);
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginBottom(12);
				table.addHeaderCell(headerCell(catName, fontBold));
				table.addHeaderCell(headerCell(weightLabel, fontBold));
//				table.addHeaderCell(headerCell(pkgLabel, fontBold));
				table.addHeaderCell(headerCell(priceLabel, fontBold));

				if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
					headerTable = createHeaderTable(font, fontBold, logo,
							(lang == 0 ? customer.toUpperCase() : customer), (lang == 0 ? venue.toUpperCase() : venue),
							dateTime, isCompanyDetails, functionNames, totalPax, catName, lang, event.getMobileno(),
							eventName, event.getEventStartDateTime(), isPartyDetails);
				} else {
					headerTable = createHeaderTable(font, fontBold, logo,
							(lang == 0 ? customer.toUpperCase() : customer), (lang == 0 ? venue.toUpperCase() : venue),
							dateTime, isCompanyDetails, "", -1, catName, lang, event.getMobileno(), eventName,
							event.getEventStartDateTime(), isPartyDetails);
				}

				headerHandler.updateHeader(headerTable);

				if (!isFirstCategory) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirstCategory = false;

				/* ---------- DATA ROWS ---------- */
				BigDecimal totalPrice = BigDecimal.ZERO;
				for (EventRawMaterialInfoDto rm : cat.getRawMaterials()) {
					totalPrice = totalPrice.add(BigDecimal.valueOf(rm.getTotalPrice()));

					String rmName = menuPreparationServiceImpl.formatText(getRawMaterialNameByLang(rm, lang), lang);

					if (lang == 0) {
						rmName = rmName.toUpperCase();
					}

					System.out.println("raw material : " + rmName);
					table.addCell(dataCell(rmName, font));
					table.addCell(dataCell(formatNumber(rm.getFinalQty()) + " " + rm.getUnitName().toLowerCase(), font)
							.setTextAlignment(TextAlignment.RIGHT));
					table.addCell(dataCell(rm.getTotalPrice().toString(), font).setTextAlignment(TextAlignment.RIGHT));
				}
				table.addCell(new Cell(1, 2).add(new Paragraph("Total Price").setFont(font).setFontSize(9))
						.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(4)
						.setTextAlignment(TextAlignment.RIGHT));
				table.addCell(dataCell(totalPrice.toString(), font).setTextAlignment(TextAlignment.RIGHT));

				document.add(table);
			}
			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + event.getEventNo() + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]), "raw material with price report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-2 PDF generation failed", e);
		}
	}

	@Override
	public String generateRawaterialReportType4(Long eventId, HttpServletRequest re, Integer lang,
			Long adminTemplateModuleId, Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyDetails, List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isPartyDetails,
			Integer isAddStoreIssue) {

		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "Event Not Found";

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;
			String customer = "", venue = "", weightLabel = "", eventName = "";

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
				weightLabel = "वजन";
				eventName = event.getEventType() != null ? event.getEventType().getNameHindi() : "";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = font;
				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
				weightLabel = "વજન";
				eventName = event.getEventType() != null ? event.getEventType().getNameGujarati() : "";
			} else {
				font = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
				weightLabel = "WEIGHT";
				eventName = event.getEventType() != null ? event.getEventType().getNameEnglish() : "";
			}

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			int isRawMaterial = 0;
			if (rawMaterialCatIds != null && !rawMaterialCatIds.isEmpty() && rawMaterialCatIds.size() != 0) {
				isRawMaterial = 1;
			}

			List<EventRawMaterialCategoryResponse> data = new ArrayList<>();
			if (eventFunctionIds == null || eventFunctionIds.isEmpty()) {
				data = eventRawMaterialService.getEventRawMaterialByEventId(eventId, rawMaterialCatIds,
						isAddStoreIssue);
			} else {
				data = eventRawMaterialService.getEventRawMaterialByEventIdAndEventFunctionId(eventId, eventFunctionIds,
						rawMaterialCatIds);
			}

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(dir + "/" + getReportName(event.getParty().getNameEnglish(),
					formatDate(event.getEventStartDateTime().split(" ")[0]), "raw material without price report")
					+ ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDoc, PageSize.A4);
			document.setMargins(110, 40, 60, 40);

			String logoimg = getLogo(userid);
//	        String logoimg = "/flipbook/pages/logo.png";

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource(logoimg);

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			Integer totalPax = 0;
			String functionNames = "";
			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
				Map<Long, EventFunctionMasterEntity> funcMap = eventFunctionMasterRepository
						.findByIdIn(eventFunctionIds).stream()
						.collect(Collectors.toMap(EventFunctionMasterEntity::getId, Function.identity()));

				StringBuilder functions = new StringBuilder();
				for (Entry<Long, EventFunctionMasterEntity> entry : funcMap.entrySet()) {
					Integer pax = entry.getValue().getPax() != null ? entry.getValue().getPax() : 0;
					totalPax = totalPax + pax;
					String functionName;
					if (lang == 1) {
						functionName = entry.getValue().getFunction().getNameHindi() + " (" + pax + ")";
					} else if (lang == 2) {
						functionName = entry.getValue().getFunction().getNameGujarati() + " (" + pax + ")";
					} else {
						functionName = entry.getValue().getFunction().getNameEnglish() + " (" + pax + ")";
					}

					if (functions.length() > 0) {
						functions.append(", ");
					}
					functions.append(functionName);
				}

				functionNames = functions.toString();
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new RawMaterialHeaderEventHandler(font, fontBold,
						logo, (lang == 0 ? eventData.getCompanyName().toUpperCase() : eventData.getCompanyName()),
						(lang == 0 ? eventData.getOfficeNo() : eventData.getOfficeNo()), eventData.getCompanyEmail()));
			}

			Table headerTable = null;
			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
				headerTable = createHeaderTable(font, fontBold, logo, (lang == 0 ? customer.toUpperCase() : customer),
						(lang == 0 ? venue.toUpperCase() : venue), dateTime, isCompanyDetails, functionNames, totalPax,
						"", lang, event.getMobileno(), eventName, event.getEventStartDateTime(), isPartyDetails);
			} else {
				headerTable = createHeaderTable(font, fontBold, logo, (lang == 0 ? customer.toUpperCase() : customer),
						(lang == 0 ? venue.toUpperCase() : venue), dateTime, isCompanyDetails, "", -1, "", lang,
						event.getMobileno(), eventName, event.getEventStartDateTime(), isPartyDetails);
			}

			float leftMargin = 40;
			float rightMargin = 40;
			float topMargin = isCompanyDetails == 1 ? 120 : 20;
			float top = PageSize.A4.getTop() - topMargin - calculateTableHeight(headerTable, pdfDoc);
			float bottom = PageSize.A4.getBottom() + 60;
			float height = top - bottom;
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = usableWidth / 3;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
					new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height),
					new Rectangle(leftMargin + (columnWidth * 2), bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			RawMaterialHeaderEventHandlerNew headerHandler = new RawMaterialHeaderEventHandlerNew(headerTable,
					topMargin);
			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

			boolean firstCategory = true;
			for (EventRawMaterialCategoryResponse cat : data) {
				String catName = menuPreparationServiceImpl.formatText(getCategoryNameByLang(cat, lang), lang);

				Table table = new Table(UnitValue.createPercentArray(new float[] { 70f, 30f }));
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginBottom(12);

				/* ---------- HEADER ROW (CATEGORY NAME INSTEAD OF NAME) ---------- */
				table.addHeaderCell(headerCell(catName, fontBold).setFontSize(lang == 0 ? 10 : 14));
				table.addHeaderCell(headerCell(weightLabel, fontBold).setFontSize(lang == 0 ? 10 : 14));

				if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {
					headerTable = createHeaderTable(font, fontBold, logo,
							(lang == 0 ? customer.toUpperCase() : customer), (lang == 0 ? venue.toUpperCase() : venue),
							dateTime, isCompanyDetails, functionNames, totalPax, catName, lang, event.getMobileno(),
							eventName, event.getEventStartDateTime(), isPartyDetails);
				} else {
					headerTable = createHeaderTable(font, fontBold, logo,
							(lang == 0 ? customer.toUpperCase() : customer), (lang == 0 ? venue.toUpperCase() : venue),
							dateTime, isCompanyDetails, "", -1, catName, lang, event.getMobileno(), eventName,
							event.getEventStartDateTime(), isPartyDetails);
				}

				headerHandler.updateHeader(headerTable);

				if (!firstCategory) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				firstCategory = false;

				if (lang == 0)
					catName = catName.toUpperCase();

				/* ---------- RAW MATERIAL ROWS ---------- */
				for (EventRawMaterialInfoDto rm : cat.getRawMaterials()) {

					String rmName = menuPreparationServiceImpl.formatText(getRawMaterialNameByLang(rm, lang), lang);

					if (lang == 0)
						rmName = rmName.toUpperCase();

					table.addCell(dataCell(rmName, font).setFontSize(lang == 0 ? 10 : 12));
					table.addCell(dataCell(formatNumber(rm.getFinalQty()) + " " + rm.getUnitName().toLowerCase(), font)
							.setFontSize(lang == 0 ? 10 : 12).setTextAlignment(TextAlignment.RIGHT));
				}
				document.add(table);
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + event.getEventNo() + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]),
							"raw material without price report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-3 PDF generation failed", e);
		}
	}

	@Override
	public String generateRawaterialReportType5(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyLogo, Integer isCompanyDetails, Integer isPartyDetails) {

		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
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
					formatDate(eventData.getEventDate()), "raw material report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(40, 35, 50, 20);

			List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl.getAllFunctionDetails(eventId,
					Long.valueOf(-1), lang);

			String logoimg = getLogo(userid);
//			String logoimg = "/flipbook/pages/logo.png";

			float[] columnWidthHead = { 20f, 15f, 2f, 62f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			if (isCompanyDetails == 1) {
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			if (isCompanyLogo == 1 || isCompanyDetails == 1) {
				Image img = new Image(ImageDataFactory.create(logoimg));
//	        	Image img = new Image(menuPreparationServiceImpl.loadImageFromResource(logoimg));
				img.setWidth(100);
				img.setHorizontalAlignment(HorizontalAlignment.LEFT);

				Cell cmp1 = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp1);
			}

			if (isCompanyLogo == 1 || isCompanyDetails == 1) {
				Paragraph cmpPara = new Paragraph().add(new Text(eventData.getCompanyName())
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
				Cell cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(new Text("Mobile No.")
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
				Cell cmp2 = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp2);

				cmpPara = new Paragraph().add(
						new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(new Text(eventData.getOfficeNo())
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(new Text("Email")
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(
						new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(new Text(eventData.getCompanyEmail())
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14));
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				document.add(cmpHead);
			}

			Paragraph heading = new Paragraph().add(new Text("Supplier Details Report")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(5f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			float[] cloumnWidthEvent = { 25f, 1f, 30f, 15f, 1f, 25f };
			Table eventDataTable = new Table(UnitValue.createPercentArray(cloumnWidthEvent));
			eventDataTable.setMarginBottom(8f);
			eventDataTable.setBorder(new SolidBorder(1f));

			// 1st row
			Paragraph event = new Paragraph().add(new Text("Customer Name").setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			Cell eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(
					new Text(":").setFontSize(14f).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventData.getPartyName()).setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text("Mobile No.").setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(
					new Text(":").setFontSize(14f).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventData.getPartyMobile()).setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			// 2ed row
			event = new Paragraph().add(new Text("Event Name").setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(
					new Text(":").setFontSize(14f).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventData.getEventName()).setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text("Event Date").setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(
					new Text(":").setFontSize(14f).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventData.getEventDate()).setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			// 3rd row
			event = new Paragraph().add(new Text("Venue").setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(
					new Text(":").setFontSize(14f).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(eventData.getVenueName()).setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
			eventCell = new Cell(3, 4).add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			document.add(eventDataTable);

			for (int k = 0; k < allFunctions.size(); k++) {

				MenuQuantityReponseDto fun = allFunctions.get(k);
				if (eventFunctionId == 0 || eventFunctionId == -1 || eventFunctionId == fun.getEventFunctionId()) {
					Long functionId = fun.getEventFunctionId();
					String functionName = fun.getFunctionName();
					Integer person = fun.getPerson();
					String functionTime = fun.getFunctionTime();

					float[] columnWidth = { 20f, 1f, 44f, 15f, 1f, 20f };
					Table function = new Table(UnitValue.createPercentArray(columnWidth));
					function.setWidth(UnitValue.createPercentValue(100));
					function.setBorder(new SolidBorder(Border.SOLID));

					Paragraph label = new Paragraph().add(new Text("Function")
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
					Paragraph saperator = new Paragraph().add(new Text(":")
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
					Paragraph name = new Paragraph().add(new Text(functionName).setFont(basicFont).setFontSize(14));

					Cell cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
					function.addCell(cell);
					cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
					function.addCell(cell);
					cell = new Cell().add(name).setBorder(Border.NO_BORDER);
					function.addCell(cell);

					label = new Paragraph().add(new Text("Persons")
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
					saperator = new Paragraph().add(new Text(":")
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
					name = new Paragraph().add(new Text(person.toString()).setFont(basicFont).setFontSize(14));
					cell = new Cell().add(label).setBorder(Border.NO_BORDER);
					function.addCell(cell);
					cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
					function.addCell(cell);
					cell = new Cell().add(name).setBorder(Border.NO_BORDER);
					function.addCell(cell);

					label = new Paragraph().add(new Text("Date & Time")
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
					saperator = new Paragraph().add(new Text(":")
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
					name = new Paragraph().add(new Text(functionTime).setFont(basicFont).setFontSize(14));
					cell = new Cell().add(label).setBorder(Border.NO_BORDER).setPaddingLeft(10);
					function.addCell(cell);
					cell = new Cell().add(saperator).setBorder(Border.NO_BORDER);
					function.addCell(cell);
					cell = new Cell(3, 6).add(name).setBorder(Border.NO_BORDER);
					function.addCell(cell);

					document.add(function);

					List<EventRawMaterialPartyDTO> evntSupplierData = eventRawMaterialService
							.getGroupedPartyByEventAndFunction(eventId, functionId);

					List<MenuQuantityReponseDto> allRawMaterialCat = menuItemRawMaterialServiceImpl
							.getRawMaterialCategory(eventId, functionId, lang);

					// Supplier table
					float[] supplierColWidth = { 30f, 25f, 20f, 25f };
					Table supplierTable = new Table(UnitValue.createPercentArray(supplierColWidth));
					supplierTable.setWidth(UnitValue.createPercentValue(100));
					supplierTable.setMarginTop(10);
					supplierTable.setMarginBottom(15);

					// Header style
					PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

					// Header cells
					supplierTable.addHeaderCell(new Cell().add(new Paragraph("Supplier Name").setFont(headerFont))
							.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));

					supplierTable.addHeaderCell(new Cell().add(new Paragraph("Contact Category").setFont(headerFont))
							.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));

					supplierTable.addHeaderCell(new Cell().add(new Paragraph("Mobile No.").setFont(headerFont))
							.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));

					supplierTable.addHeaderCell(new Cell().add(new Paragraph("Notes").setFont(headerFont))
							.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));

					for (EventRawMaterialPartyDTO cat : evntSupplierData) {

						supplierTable.addCell(new Cell().add(new Paragraph(cat.getPartyName()).setFont(basicFont))
								.setBorder(new SolidBorder(1)));

						supplierTable
								.addCell(new Cell().add(new Paragraph(cat.getContactCategoryName()).setFont(basicFont))
										.setBorder(new SolidBorder(1)));

						supplierTable.addCell(new Cell().add(new Paragraph(cat.getMobileNo()).setFont(basicFont))
								.setBorder(new SolidBorder(1)));

						// Notes column (empty as requested)
						supplierTable.addCell(new Cell().add(new Paragraph(" ")).setBorder(new SolidBorder(1)));
					}
					document.add(supplierTable);

					if (k < allFunctions.size()) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()),
							"raw material report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}

	@Override
	public String generateSupplireRawaterialReportType5(Long eventId, Long eventFunctionId, HttpServletRequest re,
			int lang, Long userid, Integer isCompanyLogo, Integer isCompanyDetails, Integer isPartyDetails,
			List<Long> agencyId) {

		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();
			float lineHeight = 1;

			String rawmaterial = "Raw Material", qtyL = "Quantity";
			String customerNameLabel = "Customer Name";
			String mobileNoLabel = "Mobile No.";
			String eventNameLabel = "Event Name";
			String eventDateLabel = "Event Date";
			String venueLabel = "Venue";
			String agencyNameLabel = "Agency Name";
			String reportTitle = "Supplier Details Report";
			String deliveryDateLabel = "Delivery DateTime";
			String deliveryPlaceLabel = "Delivery Place";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				rawmaterial = "कच्चा माल";
				qtyL = "मात्रा";
				customerNameLabel = "ग्राहक का नाम";
				mobileNoLabel = "मोबाइल नंबर";
				eventNameLabel = "कार्यक्रम का नाम";
				eventDateLabel = "कार्यक्रम तिथि";
				venueLabel = "स्थल";
				agencyNameLabel = "एजेंसी का नाम";
				reportTitle = "सप्लायर डिटेल्स रिपोर्ट";
				deliveryDateLabel = "डिलीवरी तिथि";
				deliveryPlaceLabel = "डिलीवरी स्थान";

				System.out.println("Hindi font loaded successfully");
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				rawmaterial = "કાચો માલ";
				qtyL = "માત્રા";
				customerNameLabel = "ગ્રાહકનું નામ";
				mobileNoLabel = "મોબાઇલ નંબર";
				eventNameLabel = "કાર્યક્રમનું નામ";
				eventDateLabel = "કાર્યક્રમ તારીખ";
				venueLabel = "સ્થળ";
				agencyNameLabel = "એજન્સીનું નામ";
				reportTitle = "સપ્લાયર ડિટેલ્સ રિપોર્ટ";
				lineHeight = 0.8f;
				deliveryDateLabel = "ડિલિવરી તારીખ";
				deliveryPlaceLabel = "ડિલિવરી સ્થળ";
				System.out.println("Gujarati font loaded successfully");
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
			String fileName = getPartyNameByEventId(eventId);
			if (agencyId.size() == 1) {
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}
			fileName = getReportName(fileName, formatDate(eventData.getEventDate()), "raw material report");

			File pdfFile = new File(outputPath + "/" + fileName + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 30, 20);

			List<MenuQuantityReponseDto> allFunctions = menuItemRawMaterialServiceImpl.getAllFunctionDetails(eventId,
					Long.valueOf(-1), lang);

			String logoimg = getLogo(userid);
//			String logoimg = "/flipbook/pages/logo.png";

			float[] columnWidthHead = { 20f, 15f, 2f, 62f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			if (isCompanyDetails == 1) {
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			if (isCompanyLogo == 1 || isCompanyDetails == 1) {
				Image img = new Image(ImageDataFactory.create(logoimg));
//	        	Image img = new Image(menuPreparationServiceImpl.loadImageFromResource(logoimg));
				img.setWidth(100);
				img.setHorizontalAlignment(HorizontalAlignment.LEFT);

				Cell cmp1 = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp1);
			}

			if (isCompanyLogo == 1 || isCompanyDetails == 1) {
				Paragraph cmpPara = new Paragraph().add(new Text(eventData.getCompanyName())
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
				Cell cmp = new Cell(1, 3).add(cmpPara).setBorder(Border.NO_BORDER).setPaddingLeft(5f);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(new Text("Mobile No.")
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
						.setMultipliedLeading(1);
				Cell cmp2 = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setPaddingLeft(5f);
				cmpHead.addCell(cmp2);

				cmpPara = new Paragraph().add(
						new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
						.setMultipliedLeading(1);
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph()
						.add(new Text(eventData.getOfficeNo())
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
						.setMultipliedLeading(1);
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(new Text("Email")
						.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
						.setMultipliedLeading(1);
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setPaddingLeft(5f);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph().add(
						new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
						.setMultipliedLeading(1);
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp);

				cmpPara = new Paragraph()
						.add(new Text(eventData.getCompanyEmail())
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
						.setMultipliedLeading(1);
				cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
				cmpHead.setMarginBottom(10f);
				cmpHead.addCell(cmp);

			}

			Paragraph heading = new Paragraph().add(new Text(reportTitle).setFont(boldFont).setFontSize(18))
					.setMultipliedLeading(1f);
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(5f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));

			int p = 0;

//			float[] cloumnWidthEvent = { 25f, 1f, 25f, 20f, 1f, 25f };
			float[] cloumnWidthEvent = { 25f, 1f, 74f };

			Map<String, Map<String, List<SupplierRawMaterialResponseDto>>> responseDtos = eventRawMaterialService
					.getSupplierwiseRawMaterial(eventId, lang, agencyId);

			List<Map.Entry<String, Map<String, List<SupplierRawMaterialResponseDto>>>> supplierList = new ArrayList<>(
					responseDtos.entrySet());

			for (int i = 0; i < supplierList.size(); i++) {

				Map.Entry<String, Map<String, List<SupplierRawMaterialResponseDto>>> entry = supplierList.get(i);

				document.add(cmpHead);

				if (p == 0) {
					document.add(heading);
					p = 1;
				}

				String partyName = entry.getKey();

				Table eventDataTable = new Table(UnitValue.createPercentArray(cloumnWidthEvent));
				eventDataTable.setMarginBottom(8f);
//				eventDataTable.setBorder(new SolidBorder(1f));

				// 3rd row
				Paragraph event = new Paragraph().add(new Text(agencyNameLabel).setFontSize(14f).setFont(boldFont))
						.setMultipliedLeading(lineHeight);
				Cell eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setPaddingLeft(10);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont))
						.setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(partyName).setFontSize(14f).setFont(boldFont))
						.setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				eventDataTable.setMarginBottom(10f);

				SupplierRawMaterialResponseDto firstDto = entry.getValue().values().stream().flatMap(List::stream)
						.findFirst().orElse(null);

				// Delivery Date
				event = new Paragraph().add(new Text(deliveryDateLabel).setFontSize(14f).setFont(boldFont))
						.setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setPaddingLeft(10);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont))
						.setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph()
						.add(new Text(firstDto.getDelieveryDateTime() != null ? firstDto.getDelieveryDateTime() : ""))
						.setFontSize(14f).setFont(boldFont).setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				// Delivery Place
				event = new Paragraph().add(new Text(deliveryPlaceLabel).setFontSize(14f).setFont(boldFont))
						.setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setPaddingLeft(10).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph().add(new Text(":").setFontSize(14f).setFont(boldFont))
						.setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				event = new Paragraph()
						.add(new Text(firstDto.getDelieveryPlace() != null ? firstDto.getDelieveryPlace() : ""))
						.setFontSize(14f).setFont(boldFont).setMultipliedLeading(lineHeight);
				eventCell = new Cell().add(event).setBorder(Border.NO_BORDER);
				eventDataTable.addCell(eventCell);

				document.add(eventDataTable);

				for (Map.Entry<String, List<SupplierRawMaterialResponseDto>> categoryEntry : entry.getValue()
						.entrySet()) {
					Div div = new Div();

					String categoryName = categoryEntry.getKey();

					float[] cw = { 70f, 30f };

					Table dataTable = new Table(UnitValue.createPercentArray(cw));
					dataTable.setWidth(UnitValue.createPercentValue(100));

					Cell data = new Cell(1, 2).add(new Paragraph(categoryName)
							.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER)
							.setFont(boldFont).setFontSize(15).setMultipliedLeading(lineHeight))
							.setBackgroundColor(new DeviceRgb(209, 209, 209));

					dataTable.addCell(data);
					data = new Cell().add(new Paragraph(rawmaterial).setWidth(UnitValue.createPercentValue(100))
							.setTextAlignment(TextAlignment.LEFT).setFont(boldFont).setFontSize(14)
							.setMultipliedLeading(lineHeight)).setPaddingLeft(5f);
					dataTable.addCell(data);

					data = new Cell().add(new Paragraph(qtyL).setWidth(UnitValue.createPercentValue(100))
							.setTextAlignment(TextAlignment.CENTER).setFont(boldFont).setFontSize(14)
							.setMultipliedLeading(lineHeight));
					dataTable.addCell(data);

					for (SupplierRawMaterialResponseDto dto : categoryEntry.getValue()) {
						String rawMaterialName = dto.getRawMaterialName();
						BigDecimal qty = dto.getQty();
						String unit = dto.getUnit();

						data = new Cell().add(new Paragraph(rawMaterialName).setWidth(UnitValue.createPercentValue(100))
								.setTextAlignment(TextAlignment.LEFT).setFont(basicFont).setFontSize(14)
								.setMultipliedLeading(lineHeight)).setPaddingLeft(5f);
						dataTable.addCell(data);

						data = new Cell().add(new Paragraph(qty + " " + unit)
								.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER)
								.setFont(basicFont).setFontSize(14).setMultipliedLeading(lineHeight));
						dataTable.addCell(data);
					}

					div.add(dataTable);
					document.add(div);
				}

				if (i < supplierList.size() - 1) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

			}

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 0, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/" + fileName
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}

	private Cell headerCell(String text, PdfFont fontBold) {

		return new Cell()
				.add(new Paragraph(text).setFont(fontBold).setFontSize(10).setTextAlignment(TextAlignment.CENTER))
				.setBackgroundColor(ColorConstants.LIGHT_GRAY).setBorder(new SolidBorder(ColorConstants.BLACK, 0.8f))
				.setPadding(5);
	}

	private String getLogo(Long userId) {
		/* ================= HEADER ================= */
		Optional<UserMasterEntity> userLst = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userLst.isPresent() || userLst.get().getLogo() == null || "".equals(userLst.get().getLogo())) {
			throw new RuntimeException("User Logo not found");
		}
		String logoimg = environment.getProperty("app.image.url") + userLst.get().getLogo();
//        String logoimg = "/flipbook/sample_watermark.png";
		System.out.println("get Logo : " + logoimg);
		return logoimg;
	}

	private Cell dataCell(String text, PdfFont font) {

		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9))
				.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(4);
	}

	private String fmt(String text, int lang) {
		if (text == null)
			return "";
		return lang == 0 ? text.toUpperCase() : text;
	}

	private String getCategoryNameByLang(EventRawMaterialCategoryResponse obj, int lang) {
		if (lang == 1) {
			return obj.getCategoryNameHindi() != null && !"".equals(obj.getCategoryNameHindi())
					? obj.getCategoryNameHindi()
					: obj.getCategoryNameEnglish();
		} else if (lang == 2) {
			return obj.getCategoryNameGujarati() != null && !"".equals(obj.getCategoryNameGujarati())
					? obj.getCategoryNameGujarati()
					: obj.getCategoryNameEnglish();
		} else {
			return obj.getCategoryNameEnglish();
		}
	}

	private String getFunctionNameByLang(EventFunctionMasterEntity entity, Integer lang) {
		if (lang == 1) {
			return entity.getFunction().getNameHindi() != null && !"".equals(entity.getFunction().getNameHindi())
					? entity.getFunction().getNameHindi()
					: entity.getFunction().getNameEnglish();
		} else if (lang == 2) {
			return entity.getFunction().getNameGujarati() != null && !"".equals(entity.getFunction().getNameGujarati())
					? entity.getFunction().getNameGujarati()
					: entity.getFunction().getNameEnglish();
		} else {
			return entity.getFunction().getNameEnglish();
		}
	}

	private String getRawMaterialNameByLang(EventRawMaterialInfoDto obj, int lang) {
		if (lang == 1) {
			return obj.getRawMaterialNameHindi() != null && !"".equals(obj.getRawMaterialNameHindi())
					? obj.getRawMaterialNameHindi()
					: obj.getRawMaterialNameEnglish();
		} else if (lang == 2) {
			return obj.getRawMaterialNameGujarati() != null && !"".equals(obj.getRawMaterialNameGujarati())
					? obj.getRawMaterialNameGujarati()
					: obj.getRawMaterialNameEnglish();
		} else {
			return obj.getRawMaterialNameEnglish();
		}
	}

	private Cell blankCell(float minHeight) {
		return new Cell().add(new Paragraph("\u00A0")) // non-breaking space
				.setMinHeight(minHeight).setBorder(Border.NO_BORDER);
	}

	public Table createMainHeaderTable(PdfFont font, PdfFont fontBold, ImageData logo, String clientName, String mobile,
			String email, Integer isCompanyDetails, Integer lang) {
		String dateLabel, customerNameLabel, venueLabel;

		if (lang == 1) {
			dateLabel = "ईमेल : ";
			customerNameLabel = "ग्राहक का नाम : ";
			venueLabel = "फोन नंबर : ";
		} else if (lang == 2) {
			dateLabel = "ઈમેલ : ";
			customerNameLabel = "ગ્રાહકનું નામ : ";
			venueLabel = "ફોન નં : ";
		} else {
			dateLabel = "Email : ";
			customerNameLabel = "Client Name : ";
			venueLabel = "Phone No. : ";
		}
		Image logoData = new Image(logo);
		logoData.setWidth(110f);
		logoData.setAutoScale(false);
		logoData.setHorizontalAlignment(HorizontalAlignment.CENTER);

		/* ---------- HEADER TABLE ---------- */
		Table table = new Table(UnitValue.createPercentArray(new float[] { 24f, 46f, 30f }));
		table.setFixedLayout();
		table.setWidth(UnitValue.createPercentValue(100f));

		// LOGO CELL
		Cell cell;

		if (isCompanyDetails == 1) {
			cell = new Cell(4, 1).add(logoData).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.MIDDLE);
			table.addCell(cell);
		}

		if (isCompanyDetails == 1) {
			cell = new Cell().setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 2).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);

		if (isCompanyDetails == 1) {
			cell = new Cell()
					.add(new Paragraph().add(new Text(customerNameLabel).simulateBold())
							.add(new Text(clientName.toUpperCase())).setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);

		} else {
			cell = new Cell(1, 2)
					.add(new Paragraph().add(new Text(customerNameLabel).simulateBold())
							.add(new Text(clientName.toUpperCase())).setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);

		cell = new Cell().setBorder(Border.NO_BORDER);
		table.addCell(cell);

		if (isCompanyDetails == 1) {
			cell = new Cell(1, 2).add(new Paragraph().add(new Text(venueLabel).simulateBold()).add(new Text(mobile))
					.setFont(font).setFontSize(lang == 0 ? 13f : 15f)).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 3).add(new Paragraph().add(new Text(venueLabel).simulateBold()).add(new Text(mobile))
					.setFont(font).setFontSize(lang == 0 ? 13f : 15f)).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);

		if (isCompanyDetails == 1) {
			cell = new Cell(1, 2).add(new Paragraph().add(new Text(dateLabel).simulateBold()).add(new Text(email))
					.setFont(font).setFontSize(lang == 0 ? 13f : 15f)).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 3).add(new Paragraph().add(new Text(dateLabel).simulateBold()).add(new Text(email))
					.setFont(font).setFontSize(lang == 0 ? 13f : 15f)).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);
		return table;
	}

	public Table createHeaderTable(PdfFont font, PdfFont fontBold, ImageData logo, String customer, String venue,
			String dateTime, Integer isCompanyDetails, String functionName, Integer person, String catName,
			Integer lang, String mobileNo, String eventName, String eventDate, Integer isPartyDetails) {

		String dateLabel, customerNameLabel, venueLabel, functionLabel, personLabel, eventNameLabel, mobileNoLabel;

		if (lang == 1) {
			dateLabel = "दिनांक";
			customerNameLabel = "ग्राहक का नाम";
			venueLabel = "स्थान";
			functionLabel = "समारोह";
			personLabel = "व्यक्ति";
			eventNameLabel = "कार्यक्रम का नाम";
			mobileNoLabel = "मोबाइल नंबर";
		} else if (lang == 2) {
			dateLabel = "તારીખ";
			customerNameLabel = "ગ્રાહકનું નામ";
			venueLabel = "સ્થળ";
			functionLabel = "કાર્યક્રમ";
			personLabel = "વ્યક્તિ";
			eventNameLabel = "કાર્યક્રમ નુ નામ";
			mobileNoLabel = "મોબાઇલ નંબર";
		} else {
			dateLabel = "Date";
			customerNameLabel = "Customer Name";
			venueLabel = "Venue";
			functionLabel = "Function";
			personLabel = "Person";
			eventNameLabel = "Event Name";
			mobileNoLabel = "Mobile No.";
		}

		/* ---------- HEADER TABLE ---------- */
		Table table = new Table(UnitValue.createPercentArray(new float[] { 24f, 46f, 30f }));
		table.setFixedLayout();
		table.setWidth(UnitValue.createPercentValue(100f));

		// LOGO CELL
		Cell cell;

		Table innerTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 30f, 40f }));
		innerTable.setWidth(UnitValue.createPercentValue(100f));

		Table clientTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 28f, 20f, 2f, 28f }));
		clientTable.setWidth(UnitValue.createPercentValue(100f));

		if (isPartyDetails == 1) {
			cell = new Cell()
					.add(new Paragraph().add(new Text(customerNameLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(customer != null ? customer.toUpperCase() : "")).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(mobileNoLabel).simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(mobileNo != null ? mobileNo : "")).setFont(font)
							.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			clientTable.addCell(cell);
		}
		cell = new Cell()
				.add(new Paragraph().add(new Text(eventNameLabel).simulateBold()).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell().add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
				.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(eventName != null ? eventName.toUpperCase() : "")).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(dateLabel).simulateBold()).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell().add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
				.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(eventDate != null ? eventDate.split(" ")[0] : "")).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(venueLabel).simulateBold()).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell().add(new Paragraph().add(new Text(" : ").simulateBold()).setFont(font)
				.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(venue != null ? venue.toUpperCase() : "")).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell(1, 3).add(clientTable);
		innerTable.addCell(cell);

		innerTable.addCell(new Cell(1, 3).add(new Paragraph("").setHeight(5f)).setBorder(Border.NO_BORDER));

		if (person != -1) {
			cell = new Cell()
					.add(new Paragraph().add(new Text(functionLabel + " : ").simulateBold()).add(new Text(functionName))
							.setFont(font).setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(
					new Paragraph().add(new Text(personLabel + " : ").simulateBold()).add(new Text(person.toString()))
							.setFont(font).setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);
		} else {
			cell = new Cell(1, 2).add(
					new Paragraph().setFont(font).setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);
		}

		cell = new Cell().add(new Paragraph(catName).setFont(font).setFontSize(lang == 0 ? 14 : 16)
				.setTextAlignment(TextAlignment.CENTER).setSplitCharacters(new ISplitCharacters() {

					@Override
					public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
						return true;
					}
				})).setBorder(Border.NO_BORDER).simulateBold().setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setMinHeight(20f).setBackgroundColor(new DeviceRgb(209, 209, 209));
		innerTable.addCell(cell);

		table.addCell(new Cell(1, 3).add(innerTable).setBorder(Border.NO_BORDER));
		table.addCell(new Cell(1, 3).add(new Paragraph("").setHeight(5f)).setBorder(Border.NO_BORDER));

		return table;
	}

	public Table createHeaderTableForDateWiseReport(PdfFont font, PdfFont fontBold, ImageData logo, String dateTime,
			Integer isCompanyDetails, String catName, Integer lang, String startDate, String endDate) {

		String dateLabel, customerNameLabel, venueLabel, functionLabel, personLabel, eventNameLabel, mobileNoLabel,
				fromLabel = "From ", toLabel = "To ";

		if (lang == 1) {
			dateLabel = "दिनांक";
			customerNameLabel = "ग्राहक का नाम";
			venueLabel = "स्थान";
			functionLabel = "समारोह";
			personLabel = "व्यक्ति";
			eventNameLabel = "कार्यक्रम का नाम";
			mobileNoLabel = "मोबाइल नंबर";
		} else if (lang == 2) {
			dateLabel = "તારીખ";
			customerNameLabel = "ગ્રાહકનું નામ";
			venueLabel = "સ્થળ";
			functionLabel = "કાર્યક્રમ";
			personLabel = "વ્યક્તિ";
			eventNameLabel = "કાર્યક્રમ નુ નામ";
			mobileNoLabel = "મોબાઇલ નંબર";
		} else {
			dateLabel = "Date";
			customerNameLabel = "Customer Name";
			venueLabel = "Venue";
			functionLabel = "Function";
			personLabel = "Person";
			eventNameLabel = "Event Name";
			mobileNoLabel = "Mobile No.";
		}

		/* ---------- HEADER TABLE ---------- */
		Table table = new Table(UnitValue.createPercentArray(new float[] { 24f, 46f, 30f }));
		table.setFixedLayout();
		table.setWidth(UnitValue.createPercentValue(100f));

		// LOGO CELL
		Cell cell;

		Table innerTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 30f, 40f }));
		innerTable.setWidth(UnitValue.createPercentValue(100f));
		innerTable.setBorder(Border.NO_BORDER);

		cell = new Cell(1, 3)
				.add(new Paragraph().add(new Text("DATEWISE RAW MATERIAL REPORT").simulateBold()).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.CENTER))
				.setBorder(Border.NO_BORDER);
		innerTable.addCell(cell);

		Table clientTable = new Table(UnitValue.createPercentArray(new float[] { 33.33f, 33.33f, 33.33f }));
		clientTable.setWidth(UnitValue.createPercentValue(100f));
		clientTable.setBorder(Border.NO_BORDER);

		cell = new Cell()
				.add(new Paragraph().add(new Text(fromLabel + " : " + startDate).simulateBold()).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.CENTER))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell().setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(toLabel + " : " + endDate).simulateBold()).setFont(font)
						.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.CENTER))
				.setBorder(Border.NO_BORDER);
		clientTable.addCell(cell);

		cell = new Cell(1, 3).add(clientTable).setBorder(Border.NO_BORDER);
		innerTable.addCell(cell);

		innerTable.addCell(new Cell(1, 3).add(new Paragraph("").setHeight(5f)).setBorder(Border.NO_BORDER));

		innerTable.addCell(new Cell(1, 2).add(new Paragraph("").setHeight(5f)).setBorder(Border.NO_BORDER));

		cell = new Cell().add(new Paragraph(catName).setFont(font).setFontSize(lang == 0 ? 14 : 16)
				.setTextAlignment(TextAlignment.CENTER).setSplitCharacters(new ISplitCharacters() {

					@Override
					public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
						return true;
					}
				})).setBorder(Border.NO_BORDER).simulateBold().setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setMinHeight(20f).setBackgroundColor(new DeviceRgb(209, 209, 209));
		innerTable.addCell(cell);

		table.addCell(new Cell(1, 3).add(innerTable).setBorder(Border.NO_BORDER));
		table.addCell(new Cell(1, 3).add(new Paragraph("").setHeight(5f)).setBorder(Border.NO_BORDER));

		return table;
	}

	public float calculateTableHeight(Table table, PdfDocument pdfDoc) {
		IRenderer renderer = table.createRendererSubTree();
		renderer.setParent(new Document(pdfDoc).getRenderer());

		LayoutResult result = renderer
				.layout(new LayoutContext(new LayoutArea(0, new Rectangle(PageSize.A4.getWidth() - 80, 1000 // large
																											// height
				))));

		return result.getOccupiedArea().getBBox().getHeight();
	}

	@Override
	public String generalFixReport(Long eventId, Long userid, Long eventFunctionId, List<Long> rawMaterialCatIds,
			int lang, Integer isWithQty, Integer isCompanyDetails, HttpServletRequest re) {

		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String nameLabel = "", qtyLabel = "";
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");

				nameLabel = "नाम";
				qtyLabel = "संख्या";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");

				nameLabel = "નામ";
				qtyLabel = "સંખ્યા";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");

				nameLabel = "Name";
				qtyLabel = "Quantity";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty() ? ""
					: eventData.getCompanyAddress();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpLogo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + cmpLogo);

//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			float[] columnWidth2 = { 25f, 75f };
			Table cmpTbl = new Table(UnitValue.createPercentArray(columnWidth2));
			cmpTbl.setMarginBottom(5f);

			Paragraph cmpPara = new Paragraph().setMargin(0).setMultipliedLeading(1)
					.add(new Text(cmpName).setFontSize(22)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
					.add(new Text("\n" + cmpAddress).setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
					.add(new Text("\nMobile No.: ").setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
					.add(new Text(cmpMobile).setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
					.add(new Text("\nEmail: ").setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
					.add(new Text(cmpEmail).setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));

			Cell cmpLogoCell = new Cell().add(logo).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);

			Cell cmpCell = new Cell().add(cmpPara).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);

			cmpTbl.addCell(cmpLogoCell);
			cmpTbl.addCell(cmpCell);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventData.getEventDate()), "General Fix Report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(40, 20, 30, 20);

			List<GeneralFixResponseDto> allFunctions = eventRawMaterialService.getFunctions(eventId, eventFunctionId,
					lang);

			Integer hasAnyData = 0;
			boolean isFirstPage = true;
			for (int j = 0; j < allFunctions.size(); j++) {

				GeneralFixResponseDto function = allFunctions.get(j);

				Long functionId = function.getEventFunctionId();
				String functionName = lang == 0 ? function.getFunctionName().toUpperCase() : function.getFunctionName();
				Integer pax = function.getPerson();
				List<GeneralFixResponseDto> generalFixResponse = eventRawMaterialService.getGeneralFixItems(eventId,
						functionId, rawMaterialCatIds, lang, pax, userid);

				if (generalFixResponse.size() > 0) {
					hasAnyData = 1;
					if (!isFirstPage) {
						document.add(new AreaBreak());
					}

					isFirstPage = false;

					if (isCompanyDetails == 1) {
						document.add(cmpTbl);
					}

					Paragraph fun = new Paragraph()
							.add(new Text("General Fix Raw Material").simulateBold().setFontSize(18))
							.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER);

					document.add(fun);

					fun = new Paragraph().add(new Text("Function: ").simulateBold().setFontSize(15))
							.add(new Text(functionName).setFont(basicFont).setFontSize(15))
							.add(new Text("   Pax: ").simulateBold().setFontSize(15))
							.add(new Text(pax.toString()).setFont(basicFont).setFontSize(15))
							.setWidth(UnitValue.createPercentValue(100));

					document.add(fun);

					float[] columnWidth = { 30f, 20f, 30f, 20f };

					Table tbl = null;

					tbl = new Table(UnitValue.createPercentArray(columnWidth));
					tbl.setWidth(UnitValue.createPercentValue(100));

					Cell cell = new Cell()
							.add(new Paragraph().add(new Text(nameLabel).setFont(boldFont)).setFontSize(12))
							.setPaddingLeft(5f).setTextAlignment(TextAlignment.LEFT);
					tbl.addCell(cell);

					cell = new Cell().add(new Paragraph().add(new Text(qtyLabel).setFont(boldFont)).setFontSize(12))
							.setPaddingRight(5f).setTextAlignment(TextAlignment.RIGHT);
					tbl.addCell(cell);

					cell = new Cell().add(new Paragraph().add(new Text(nameLabel).setFont(boldFont)).setFontSize(12))
							.setPaddingLeft(5f).setTextAlignment(TextAlignment.LEFT);
					tbl.addCell(cell);

					cell = new Cell().add(new Paragraph().add(new Text(qtyLabel).setFont(boldFont)).setFontSize(12))
							.setPaddingRight(5f).setTextAlignment(TextAlignment.RIGHT);
					tbl.addCell(cell);

					for (int i = 0; i < generalFixResponse.size(); i++) {
						GeneralFixResponseDto data = generalFixResponse.get(i);

						cell = new Cell()
								.setPaddingLeft(5f).add(new Paragraph()
										.add(new Text(data.getRawMaterialName()).setFont(basicFont)).setFontSize(12))
								.setTextAlignment(TextAlignment.LEFT);
						tbl.addCell(cell);

						if (isWithQty == 1) {
							cell = new Cell().setPaddingRight(5f)
									.add(new Paragraph().add(
											new Text(data.getWeight() + " " + data.getUnitName()).setFont(basicFont))
											.setFontSize(12))
									.setTextAlignment(TextAlignment.RIGHT);
							tbl.addCell(cell);
						} else {
							cell = new Cell().setPaddingRight(5f)
									.add(new Paragraph().add(new Text("").setFont(basicFont)).setFontSize(12))
									.setTextAlignment(TextAlignment.RIGHT);
							tbl.addCell(cell);
						}
					}

					document.add(tbl);

				}

			}

			if (hasAnyData == 0) {
				throw new RuntimeException("There is no General Fix item in this Event!");
			}

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 5, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()),
							"General Fix Report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}

	}

	@Override
	public String crockeryCutleryReport(Long eventId, Long userid, Long eventFunctionId, int lang, Integer isWithQty,
			Integer isCompanyDetails, HttpServletRequest re, Integer isCompanyLogo, Integer isPartyDetails) {

		try {
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "";

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;

			String customer = "", venue = "";

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
			} else {
				font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
			}

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			Integer hasAnyData = 0;

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty() ? ""
					: eventData.getCompanyAddress();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpLogo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(dir + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventData.getEventDate()), "Crockert Cutlery Report") + ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);
			Document document = new Document(pdfDoc, PageSize.A4);

			document.setMargins(10, 30, 30, 30);

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + cmpLogo);
//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			Image logo = new Image(img);

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new AbstractPdfDocumentEventHandler() {
				@Override
				protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
					PdfDocument pdfDoc = event.getDocument();
					PdfPage page = ((PdfDocumentEvent) event).getPage();
					PdfCanvas canvas = new PdfCanvas(page);
					Rectangle pageSize = page.getPageSize();
					float pageWidth = pageSize.getWidth();

					try {
						// ✅ Define header area with top margin and centered positioning
						float headerTopMargin = 20f; // Space above header
						float headerHeight = 30f;
						if (isCompanyDetails == 1) {
							headerHeight = 130f;
						}
						float headerWidth = pageWidth - 60f; // Leave 30pt margin on each side
						float headerX = 30f; // Left margin
						float headerY = pageSize.getTop() - headerTopMargin - headerHeight;

						// Create rectangle for header area
						Rectangle headerRect = new Rectangle(headerX, headerY, headerWidth, headerHeight);
						Canvas headerCanvas = new Canvas(canvas, headerRect);

						logo.setWidth(UnitValue.createPercentValue(100));
						logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

						float[] columnWidth2 = { 25f, 75f };
						Table cmpTbl = new Table(UnitValue.createPercentArray(columnWidth2));
						cmpTbl.setWidth(UnitValue.createPercentValue(100));
						cmpTbl.setMarginBottom(5f);

						Paragraph cmpPara = new Paragraph().setMargin(0).setMultipliedLeading(1)
								.add(new Text(cmpName).setFontSize(22)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
								.add(new Text("\n" + cmpAddress).setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
								.add(new Text("\nMobile No.: ").setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
								.add(new Text(cmpMobile).setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
								.add(new Text("\nEmail: ").setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
								.add(new Text(cmpEmail).setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));

						Paragraph cmpTitle = new Paragraph().setMargin(0).setMultipliedLeading(1)
								.add(new Text("Crockery Report").setFontSize(22)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));

						Cell cmpLogoCell = new Cell().add(logo).setTextAlignment(TextAlignment.LEFT)
								.setBorder(Border.NO_BORDER).setPaddingLeft(10f)
								.setVerticalAlignment(VerticalAlignment.TOP);

						Cell cmpCell = new Cell().add(cmpPara).setTextAlignment(TextAlignment.LEFT)
								.setBorder(Border.NO_BORDER).setPaddingLeft(10f)
								.setVerticalAlignment(VerticalAlignment.TOP);

						Cell cmpCellTitle = new Cell(1, 2).add(cmpTitle).setTextAlignment(TextAlignment.CENTER)
								.setBorder(Border.NO_BORDER).setPaddingLeft(0f)
								.setVerticalAlignment(VerticalAlignment.TOP);

						if (isCompanyDetails == 1) {
							cmpTbl.addCell(cmpLogoCell);
							cmpTbl.addCell(cmpCell);
						}
						cmpTbl.addCell(cmpCellTitle);

						headerCanvas.add(cmpTbl);
						headerCanvas.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			/* ================= 3 COLUMN LAYOUT ================= */
			float pageWidth = PageSize.A4.getWidth();
			float usableWidth = pageWidth - 80;
			float columnWidth = usableWidth / 3;

			float top = isCompanyDetails == 1 ? PageSize.A4.getTop() - 130 : PageSize.A4.getTop() - 20;
			float bottom = PageSize.A4.getBottom() + 30;

			Rectangle[] columns = new Rectangle[] { new Rectangle(30, bottom, columnWidth, top - bottom),
					new Rectangle(30 + columnWidth, bottom, columnWidth, top - bottom),
					new Rectangle(30 + columnWidth * 2, bottom, columnWidth, top - bottom) };

			List<GeneralFixResponseDto> allFunctions = eventRawMaterialService.getFunctions(eventId, eventFunctionId,
					lang);

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			for (int i = 0; i < allFunctions.size(); i++) {

				GeneralFixResponseDto function = allFunctions.get(i);
				Long functionId = function.getEventFunctionId();
				Integer person = function.getPerson();

				List<GeneralFixResponseDto> categories = eventRawMaterialService.getCrockerCategory(lang);

				if (categories.size() > 0) {

					Paragraph fnm = new Paragraph().add(new Text("Function: ").setFont(fontBold))
							.add(new Text(function.getFunctionName()).setFont(font)).setFontSize(14).setPadding(0)
							.setMultipliedLeading(1);

					if (isCompanyDetails == 0) {
						fnm.setMarginTop(30f);
					}

					document.add(fnm);
					document.add(new Paragraph().add(new Text("Venue: ").setFont(fontBold))
							.add(new Text(function.getFunctionVenue()).setFont(font)).setFontSize(14).setPadding(0)
							.setMultipliedLeading(1));

					for (GeneralFixResponseDto category : categories) {

						Table table = null;
						if (isWithQty == 1) {
							table = new Table(UnitValue.createPercentArray(new float[] { 80, 20 }));
						} else {
							table = new Table(UnitValue.createPercentArray(new float[] { 100 }));
						}
						table.setWidth(UnitValue.createPercentValue(100));
						table.setMarginTop(0f);
						table.setMarginBottom(0);

						// NAME | WEIGHT header (always first in column)
						table.addCell(new Cell().add(new Paragraph("NAME").setFont(fontBold).setFontSize(10))
								.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));

						if (isWithQty == 1) {
							table.addCell(new Cell().add(new Paragraph("WEIGHT").setFont(fontBold).setFontSize(10))
									.setTextAlignment(TextAlignment.RIGHT)
									.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));
						}

						/* ---------- CATEGORY ROW (SPAN 2 COLUMNS, NO GAP) ---------- */
						String catName = category.getRawMaterialCatName();

						if (lang == 0)
							catName = catName.toUpperCase();

						if (isWithQty == 1) {
							table.addCell(new Cell(1, 2).add(new Paragraph(catName).setFont(fontBold).setFontSize(11))
									.setBackgroundColor(ColorConstants.LIGHT_GRAY)
									.setBorder(new SolidBorder(ColorConstants.BLACK, 0.7f)).setPadding(4));
						} else {
							table.addCell(new Cell().add(new Paragraph(catName).setFont(fontBold).setFontSize(11))
									.setBackgroundColor(ColorConstants.LIGHT_GRAY)
									.setBorder(new SolidBorder(ColorConstants.BLACK, 0.7f)).setPadding(4));
						}

						List<GeneralFixResponseDto> items = eventRawMaterialService
								.getCrockerItems(category.getRawMaterialCatId(), lang, person);

						for (GeneralFixResponseDto item : items) {
							String rmName = item.getRawMaterialName();
							hasAnyData = 1;
							if (lang == 0)
								rmName = rmName.toUpperCase();

							table.addCell(new Cell().add(new Paragraph(rmName).setFont(font).setFontSize(12))
									.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));

							if (isWithQty == 1) {
								table.addCell(new Cell()
										.add(new Paragraph(formatNumber(item.getFinalQty())).setFont(font)
												.setFontSize(12))
										.setTextAlignment(TextAlignment.RIGHT)
										.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));
							}
						}

						document.add(table);
					}
				}

				if (i < allFunctions.size() - 1) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

			}

			if (hasAnyData == 0) {
				throw new RuntimeException("No Crocker Cutlery items in this event!");
			}

			document.setRenderer(new DocumentRenderer(document));

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDoc);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDoc.getNumberOfPages()), 0, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()),
							"Crockert Cutlery Report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-1 PDF generation failed", e);
		}
	}

	@Override
	public String crockeryCutleryReport2(Long eventId, Long userid, List<Long> eventFunctionIds, int lang,
			Integer isWithQty, Integer isCompanyDetails, HttpServletRequest re, Integer isCompanyLogo,
			Integer isPartyDetails) {

		try {
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "";

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;

			String customer = "", venue = "", nameLabel = "", weightLabel = "", functionLabel = "";

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
				nameLabel = "नाम";
				weightLabel = "वज़न";
				functionLabel = "कार्यक्रम";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
				nameLabel = "નામ";
				weightLabel = "વજન";
				functionLabel = "કાર્યક્રમ";
			} else {
				font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
				nameLabel = "Name";
				weightLabel = "Weight";
				functionLabel = "Function";
			}

			final PdfFont finalFont = font;
			final PdfFont finalFontBold = fontBold;

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			Integer hasAnyData = 0;

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty() ? ""
					: eventData.getCompanyAddress();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpLogo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(dir + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventData.getEventDate()), "Crockert Cutlery Report") + ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);
			Document document = new Document(pdfDoc, PageSize.A4);

			document.setMargins(10, 30, 30, 30);

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + cmpLogo);
//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			Image logo = new Image(img);

			StringBuilder functionNames = new StringBuilder();

			Map<String, Map<String, BigDecimal>> crockerCutleryData = new LinkedHashMap<>();

			List<GeneralFixResponseDto> allFunctions = eventRawMaterialService.getFunctions(eventId, eventFunctionIds,
					lang);

			for (int i = 0; i < allFunctions.size(); i++) {
				GeneralFixResponseDto fun = allFunctions.get(i);
				String fnm = fun.getFunctionName() + " (" + fun.getPerson() + ") " + " (" + fun.getFunctionVenue()
						+ ") ";
				Integer person = fun.getPerson();

				if (i + 1 == allFunctions.size()) {
					functionNames.append(fnm);
				} else {
					functionNames.append(fnm + ", ");
				}

				crockerCutleryData = eventRawMaterialService.getFunctionwiseCrockerCutlery(lang, person,
						crockerCutleryData, userid);

			}

			final String allFunctionNames = functionNames.toString();

			final String finalFunction = functionLabel;
			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new AbstractPdfDocumentEventHandler() {
				@Override
				protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
					PdfDocument pdfDoc = event.getDocument();
					PdfPage page = ((PdfDocumentEvent) event).getPage();
					PdfCanvas canvas = new PdfCanvas(page);
					Rectangle pageSize = page.getPageSize();
					float pageWidth = pageSize.getWidth();

					try {
						// ✅ Define header area with top margin and centered positioning
						float headerTopMargin = 20f; // Space above header
						float headerHeight = 100f;
						if (isCompanyDetails == 1) {
							headerHeight = 170f;
						}
						float headerWidth = pageWidth - 60f; // Leave 30pt margin on each side
						float headerX = 30f; // Left margin
						float headerY = pageSize.getTop() - headerTopMargin - headerHeight;

						// Create rectangle for header area
						Rectangle headerRect = new Rectangle(headerX, headerY, headerWidth, headerHeight);
						Canvas headerCanvas = new Canvas(canvas, headerRect);

						logo.setWidth(UnitValue.createPercentValue(100));
						logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

						float[] columnWidth2 = { 25f, 75f };
						Table cmpTbl = new Table(UnitValue.createPercentArray(columnWidth2));
						cmpTbl.setWidth(UnitValue.createPercentValue(100));
						cmpTbl.setMarginBottom(5f);

						Paragraph cmpPara = new Paragraph().setMargin(0).setMultipliedLeading(1)
								.add(new Text(cmpName).setFontSize(22)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
								.add(new Text("\n" + cmpAddress).setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
								.add(new Text("\nMobile No.: ").setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
								.add(new Text(cmpMobile).setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
								.add(new Text("\nEmail: ").setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
								.add(new Text(cmpEmail).setFontSize(14)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));

						Paragraph cmpTitle = new Paragraph().setMargin(0).setMultipliedLeading(1)
								.add(new Text("Crockery Report").setFontSize(22)
										.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));

						Paragraph fnm = new Paragraph().add(new Text(finalFunction + " : ").setFont(finalFontBold))
								.add(new Text(allFunctionNames).setFont(finalFont)).setFontSize(14).setPadding(0)
								.setMultipliedLeading(1);

						Cell cmpLogoCell = new Cell().add(logo).setTextAlignment(TextAlignment.LEFT)
								.setBorder(Border.NO_BORDER).setPaddingLeft(10f)
								.setVerticalAlignment(VerticalAlignment.TOP);

						Cell cmpCell = new Cell().add(cmpPara).setTextAlignment(TextAlignment.LEFT)
								.setBorder(Border.NO_BORDER).setPaddingLeft(10f)
								.setVerticalAlignment(VerticalAlignment.TOP);

						Cell cmpCellTitle = new Cell(1, 2).add(cmpTitle).setTextAlignment(TextAlignment.CENTER)
								.setBorder(Border.NO_BORDER).setPaddingLeft(0f)
								.setVerticalAlignment(VerticalAlignment.TOP);

						Cell reportCellTitle = new Cell(1, 2).add(fnm).setTextAlignment(TextAlignment.LEFT)
								.setBorder(Border.NO_BORDER).setPaddingLeft(0f)
								.setVerticalAlignment(VerticalAlignment.TOP);

						if (isCompanyDetails == 1) {
							cmpTbl.addCell(cmpLogoCell);
							cmpTbl.addCell(cmpCell);
						}
						cmpTbl.addCell(cmpCellTitle);
						cmpTbl.addCell(reportCellTitle);

						headerCanvas.add(cmpTbl);
						headerCanvas.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			/* ================= 3 COLUMN LAYOUT ================= */
			float pageWidth = PageSize.A4.getWidth();
			float usableWidth = pageWidth - 80;
			float columnWidth = usableWidth / 3;

			float top = isCompanyDetails == 1 ? PageSize.A4.getTop() - 170 : PageSize.A4.getTop() - 90;
			float bottom = PageSize.A4.getBottom() + 30;

			Rectangle[] columns = new Rectangle[] { new Rectangle(30, bottom, columnWidth, top - bottom),
					new Rectangle(30 + columnWidth, bottom, columnWidth, top - bottom),
					new Rectangle(30 + columnWidth * 2, bottom, columnWidth, top - bottom) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

//			System.out.println("=== Crocker Cutlery Data ===");
			for (Map.Entry<String, Map<String, BigDecimal>> categoryEntry : crockerCutleryData.entrySet()) {
				hasAnyData = 1;
				String categoryName = categoryEntry.getKey();
				Map<String, BigDecimal> items = categoryEntry.getValue();

//				System.out.println("\nCategory: " + categoryName);

				Table table = null;
				if (isWithQty == 1) {
					table = new Table(UnitValue.createPercentArray(new float[] { 80, 20 }));
				} else {
					table = new Table(UnitValue.createPercentArray(new float[] { 100 }));
				}
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginTop(0f);
				table.setMarginBottom(0);

				// NAME | WEIGHT header (always first in column)
				table.addCell(new Cell().add(new Paragraph(nameLabel).setFont(fontBold).setFontSize(10))
						.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));

				if (isWithQty == 1) {
					table.addCell(new Cell().add(new Paragraph(weightLabel).setFont(fontBold).setFontSize(10))
							.setTextAlignment(TextAlignment.RIGHT)
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.6f)).setPadding(3));
				}

				/* ---------- CATEGORY ROW (SPAN 2 COLUMNS, NO GAP) ---------- */

				if (lang == 0)
					categoryName = categoryName.toUpperCase();

				if (isWithQty == 1) {
					table.addCell(new Cell(1, 2).add(new Paragraph(categoryName).setFont(fontBold).setFontSize(11))
							.setBackgroundColor(ColorConstants.LIGHT_GRAY)
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.7f)).setPadding(4));
				} else {
					table.addCell(new Cell().add(new Paragraph(categoryName).setFont(fontBold).setFontSize(11))
							.setBackgroundColor(ColorConstants.LIGHT_GRAY)
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.7f)).setPadding(4));
				}

				for (Map.Entry<String, BigDecimal> itemEntry : items.entrySet()) {
//					System.out.println("  - " + itemEntry.getKey() + ": " + itemEntry.getValue());
					String rmName = itemEntry.getKey();
					BigDecimal qty = itemEntry.getValue();
					hasAnyData = 1;

					if (qty.compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}

					if (lang == 0)
						rmName = rmName.toUpperCase();

					table.addCell(new Cell().add(new Paragraph(rmName).setFont(font).setFontSize(12))
							.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));

					if (isWithQty == 1) {
						table.addCell(new Cell().add(new Paragraph(qty.toString()).setFont(font).setFontSize(12))
								.setTextAlignment(TextAlignment.RIGHT)
								.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));
					}
				}

				document.add(table);
			}

			if (hasAnyData == 0) {
				throw new RuntimeException("No Crocker Cutlery items in this event!");
			}

			document.setRenderer(new DocumentRenderer(document));

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDoc);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDoc.getNumberOfPages()), 0, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()),
							"Crockert Cutlery Report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-1 PDF generation failed", e);
		}
	}

	@Override
	public String generateDateWiseRawMaterialReportWithPrice(Integer isCompanyDetails, String startDate, String endDate,
			HttpServletRequest re, Integer lang, Long userid, List<Long> rawMaterialCatIds) {
		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;
			String customer = "", venue = "", weightLabel = "", priceLabel = "", pkgLabel = "", eventName = "";

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				weightLabel = "वजन";
				priceLabel = "कीमत";
				pkgLabel = "पैकेज";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = font;
				weightLabel = "વજન";
				priceLabel = "કિંમત";
				pkgLabel = "પેકેજ";
			} else {
				font = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
				weightLabel = "WEIGHT";
				priceLabel = "Price";
				pkgLabel = "Pkg";
			}

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			UserBasicDetailsMasterEntity cmpData = getCompanyData(userid);

			if (cmpData == null) {
				return "User details not found.";
			}

			List<EventRawMaterialCategoryResponse> data = eventRawMaterialService.getDateWiseRawMaterial(
					rawMaterialCatIds, LocalDate.parse(startDate, dateTimeFormater).atStartOfDay(),
					LocalDate.parse(endDate, dateTimeFormater).atTime(LocalTime.MAX), userid);

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + startDate.replace("/", "_"));
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(dir + "/rawmaterial_report_" + ts + ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDoc, PageSize.A4, true);
			document.setMargins(110, 40, 60, 40); // extra bottom for footer

			String logoimg = getLogo(userid);
//	        String logoimg = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource(logoimg);

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,
						new RawMaterialHeaderEventHandler(font, fontBold, logo, cmpData.getCompanyName().toUpperCase(),
								cmpData.getOfficeNo(), cmpData.getCompanyEmail()));
			}

			Table headerTable = createHeaderTableForDateWiseReport(font, fontBold, logo, dateTime, isCompanyDetails, "",
					lang, startDate, endDate);

			float leftMargin = 40;
			float rightMargin = 40;
			float topMargin = isCompanyDetails == 1 ? 120 : 20;
			float top = PageSize.A4.getTop() - topMargin;
			float bottom = PageSize.A4.getBottom() + 60;
			float height = top - bottom - calculateTableHeight(headerTable, pdfDoc);
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = usableWidth / 2;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
					new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));
			RawMaterialHeaderEventHandlerNew headerHandler = new RawMaterialHeaderEventHandlerNew(headerTable,
					topMargin);
			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

			boolean isFirstCategory = true;
			for (EventRawMaterialCategoryResponse cat : data) {
				String catName = menuPreparationServiceImpl.formatText(getCategoryNameByLang(cat, lang), lang);

				if (lang == 0) {
					catName = catName.toUpperCase();
				}
				Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 25, 25 }), false);
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginBottom(12);
				table.addHeaderCell(headerCell(catName, fontBold));
				table.addHeaderCell(headerCell(weightLabel, fontBold));
//				table.addHeaderCell(headerCell(pkgLabel, fontBold));
				table.addHeaderCell(headerCell(priceLabel, fontBold));

				headerTable = createHeaderTableForDateWiseReport(font, fontBold, logo, dateTime, isCompanyDetails,
						catName, lang, startDate, endDate);

				headerHandler.updateHeader(headerTable);

				if (!isFirstCategory) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirstCategory = false;

				/* ---------- DATA ROWS ---------- */
				BigDecimal totalPrice = BigDecimal.ZERO;
				for (EventRawMaterialInfoDto rm : cat.getRawMaterials()) {
					totalPrice = totalPrice.add(BigDecimal.valueOf(rm.getTotalPrice()));

					String rmName = menuPreparationServiceImpl.formatText(getRawMaterialNameByLang(rm, lang), lang);

					if (lang == 0) {
						rmName = rmName.toUpperCase();
					}

					System.out.println("raw material : " + rmName);
					table.addCell(dataCell(rmName, font));
					table.addCell(dataCell(formatNumber(rm.getFinalQty()) + " " + rm.getUnitName().toLowerCase(), font)
							.setTextAlignment(TextAlignment.RIGHT));
					table.addCell(dataCell(rm.getTotalPrice().toString(), font).setTextAlignment(TextAlignment.RIGHT));
				}
				table.addCell(new Cell(1, 2).add(new Paragraph("Total Price").setFont(font).setFontSize(9))
						.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(4)
						.setTextAlignment(TextAlignment.RIGHT));
				table.addCell(dataCell(totalPrice.toString(), font).setTextAlignment(TextAlignment.RIGHT));

				document.add(table);
			}
			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + startDate.replace("/", "_")
					+ "/rawmaterial_report_" + ts + ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material PDF generation failed", e);
		}
	}

	@Override
	public String generateDateWiseRawMaterialReportWithoutPrice(Integer isCompanyDetails, String startDate,
			String endDate, HttpServletRequest re, Integer lang, Long userid, List<Long> rawMaterialCatIds) {
		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;
			String customer = "", venue = "", weightLabel = "", priceLabel = "", pkgLabel = "", eventName = "";

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				weightLabel = "वजन";
				priceLabel = "कीमत";
				pkgLabel = "पैकेज";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = font;
				weightLabel = "વજન";
				priceLabel = "કિંમત";
				pkgLabel = "પેકેજ";
			} else {
				font = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
				weightLabel = "WEIGHT";
				priceLabel = "Price";
				pkgLabel = "Pkg";
			}

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());
			DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			UserBasicDetailsMasterEntity cmpData = getCompanyData(userid);

			if (cmpData == null) {
				return "User details not found.";
			}

			System.out.println("start date : " + LocalDate.parse(startDate, dateTimeFormater).atStartOfDay());
			System.out.println("end date : " + LocalDate.parse(endDate, dateTimeFormater).atStartOfDay());
			List<EventRawMaterialCategoryResponse> data = eventRawMaterialService.getDateWiseRawMaterial(
					rawMaterialCatIds, LocalDate.parse(startDate, dateTimeFormater).atStartOfDay(),
					LocalDate.parse(endDate, dateTimeFormater).atTime(LocalTime.MAX), userid);

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + startDate.replace("/", "_"));
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(dir + "/rawmaterial_report_" + ts + ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDoc, PageSize.A4, true);
			document.setMargins(110, 40, 60, 40); // extra bottom for footer

			String logoimg = getLogo(userid);
//	        String logoimg = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource(logoimg);

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			if (isCompanyDetails == 1) {
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,
						new RawMaterialHeaderEventHandler(font, fontBold, logo, cmpData.getCompanyName().toUpperCase(),
								cmpData.getOfficeNo(), cmpData.getCompanyEmail()));
			}

			Table headerTable = createHeaderTableForDateWiseReport(font, fontBold, logo, dateTime, isCompanyDetails, "",
					lang, startDate, endDate);

			float leftMargin = 40;
			float rightMargin = 40;
			float topMargin = isCompanyDetails == 1 ? 120 : 20;
			float top = PageSize.A4.getTop() - topMargin - calculateTableHeight(headerTable, pdfDoc);
			float bottom = PageSize.A4.getBottom() + 60;
			float height = top - bottom;
			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
			float columnWidth = usableWidth / 3;

			Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
					new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height),
					new Rectangle(leftMargin + (columnWidth * 2), bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));
			RawMaterialHeaderEventHandlerNew headerHandler = new RawMaterialHeaderEventHandlerNew(headerTable,
					topMargin);
			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

			boolean isFirstCategory = true;
			for (EventRawMaterialCategoryResponse cat : data) {
				String catName = menuPreparationServiceImpl.formatText(getCategoryNameByLang(cat, lang), lang);

				if (lang == 0) {
					catName = catName.toUpperCase();
				}
				Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }), false);
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginBottom(12);
				table.addHeaderCell(headerCell(catName, fontBold));
				table.addHeaderCell(headerCell(weightLabel, fontBold));
//				table.addHeaderCell(headerCell(pkgLabel, fontBold));

				headerTable = createHeaderTableForDateWiseReport(font, fontBold, logo, dateTime, isCompanyDetails,
						catName, lang, startDate, endDate);

				headerHandler.updateHeader(headerTable);

				if (!isFirstCategory) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirstCategory = false;

				/* ---------- DATA ROWS ---------- */
				BigDecimal totalPrice = BigDecimal.ZERO;
				for (EventRawMaterialInfoDto rm : cat.getRawMaterials()) {
					totalPrice = totalPrice.add(BigDecimal.valueOf(rm.getTotalPrice()));

					String rmName = menuPreparationServiceImpl.formatText(getRawMaterialNameByLang(rm, lang), lang);

					if (lang == 0) {
						rmName = rmName.toUpperCase();
					}

					table.addCell(dataCell(rmName, font));
					table.addCell(dataCell(formatNumber(rm.getFinalQty()) + " " + rm.getUnitName().toLowerCase(), font)
							.setTextAlignment(TextAlignment.RIGHT));
				}

				document.add(table);
			}
			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + startDate.replace("/", "_")
					+ "/rawmaterial_report_" + ts + ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material PDF generation failed", e);
		}
	}

	@Override
	public String generalFixReportComboReport(Long eventId, Long userid, Long eventFunctionId,
			List<Long> rawMaterialCatIds, int lang, Integer isWithQty, Integer isCompanyDetails,
			HttpServletRequest re) {
		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String nameLabel = "", qtyLabel = "";
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");

				nameLabel = "नाम";
				qtyLabel = "संख्या";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");

				nameLabel = "નામ";
				qtyLabel = "સંખ્યા";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");

				nameLabel = "Name";
				qtyLabel = "Quantity";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);
			String eventNo = eventData.getEventNo();

			String cmpName = eventData.getCompanyName() == null || eventData.getCompanyName().isEmpty() ? ""
					: eventData.getCompanyName();
			String cmpAddress = eventData.getCompanyAddress() == null || eventData.getCompanyAddress().isEmpty() ? ""
					: eventData.getCompanyAddress();
			String cmpMobile = eventData.getOfficeNo() == null || eventData.getOfficeNo().isEmpty() ? ""
					: eventData.getOfficeNo();
			String cmpEmail = eventData.getCompanyEmail() == null || eventData.getCompanyEmail().isEmpty() ? ""
					: eventData.getCompanyEmail();
			String cmpLogo = eventData.getLogo() == null || eventData.getLogo().isEmpty() ? "" : eventData.getLogo();

			ImageData img = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + cmpLogo);

//			ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			Image logo = new Image(img);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			float[] columnWidth2 = { 25f, 75f };
			Table cmpTbl = new Table(UnitValue.createPercentArray(columnWidth2));
			cmpTbl.setMarginBottom(5f);

			Paragraph cmpPara = new Paragraph().setMargin(0).setMultipliedLeading(1)
					.add(new Text(cmpName).setFontSize(22)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
					.add(new Text("\n" + cmpAddress).setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
					.add(new Text("\nMobile No.: ").setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
					.add(new Text(cmpMobile).setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
					.add(new Text("\nEmail: ").setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
					.add(new Text(cmpEmail).setFontSize(14)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));

			Cell cmpLogoCell = new Cell().add(logo).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);

			Cell cmpCell = new Cell().add(cmpPara).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f);

			cmpTbl.addCell(cmpLogoCell);
			cmpTbl.addCell(cmpCell);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventData.getEventDate()), "General Fix Report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(40, 20, 30, 20);

			EventWiseGeneralFixResponseDto data = mapGeneralFixReportData(eventId);

			if (data == null) {
				return "";
			}

			if (isCompanyDetails == 1) {
				document.add(cmpTbl);
			}

			String eventName = data.getEventName() != null ? data.getEventName() : "";
			Integer pax = data.getPax() != null ? data.getPax() : 0;

			Paragraph fun = new Paragraph().add(new Text("General Fix Raw Material").simulateBold().setFontSize(18))
					.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER);

			document.add(fun);

			fun = new Paragraph().add(new Text("Event: ").simulateBold().setFontSize(15))
					.add(new Text(eventName).setFont(basicFont).setFontSize(15))
					.add(new Text("   Pax: ").simulateBold().setFontSize(15))
					.add(new Text(pax.toString()).setFont(basicFont).setFontSize(15))
					.setWidth(UnitValue.createPercentValue(100));

			document.add(fun);

			List<EventWiseGeneralFixCategoryResponseDto> details = data.getDetails();

			Table tbl = null;
			float[] columnWidth = { 30f, 20f, 30f, 20f };
			tbl = new Table(UnitValue.createPercentArray(columnWidth));
			tbl.setWidth(UnitValue.createPercentValue(100));

			Cell cell = new Cell().add(new Paragraph().add(new Text(nameLabel).setFont(boldFont)).setFontSize(12))
					.setPaddingLeft(5f).setTextAlignment(TextAlignment.LEFT);
			tbl.addCell(cell);

			cell = new Cell().add(new Paragraph().add(new Text(qtyLabel).setFont(boldFont)).setFontSize(12))
					.setPaddingRight(5f).setTextAlignment(TextAlignment.RIGHT);
			tbl.addCell(cell);

			cell = new Cell().add(new Paragraph().add(new Text(nameLabel).setFont(boldFont)).setFontSize(12))
					.setPaddingLeft(5f).setTextAlignment(TextAlignment.LEFT);
			tbl.addCell(cell);

			cell = new Cell().add(new Paragraph().add(new Text(qtyLabel).setFont(boldFont)).setFontSize(12))
					.setPaddingRight(5f).setTextAlignment(TextAlignment.RIGHT);
			tbl.addCell(cell);

			for (EventWiseGeneralFixCategoryResponseDto cat : details) {
				Long rawCategoryId = cat.getRawMaterialCatId() != null ? cat.getRawMaterialCatId() : 0;
				String rawCategoryName = cat.getRawMaterialCatName() != null ? cat.getRawMaterialCatName() : "";

				cell = new Cell(1, 4).setPaddingLeft(5f)
						.add(new Paragraph().add(new Text(rawCategoryName).setFont(boldFont)).setFontSize(12))
						.setTextAlignment(TextAlignment.LEFT);
				tbl.addCell(cell);

				List<EventWiseGeneralFixDetailsResponseDto> items = cat.getItems();

				for (EventWiseGeneralFixDetailsResponseDto detail : items) {

					Long rawMaterialId = detail.getRawMaterialId() != null ? detail.getRawMaterialId() : 0;
					String rawMaterialName = detail.getRawMaterialName() != null ? detail.getRawMaterialName() : "";
					BigDecimal weightPer100Pax = detail.getWeightPer100Pax() != null ? detail.getWeightPer100Pax()
							: BigDecimal.ZERO;
					Long unitId = detail.getUnitId() != null ? detail.getUnitId() : 0;
					String unitName = detail.getUnitName() != null ? detail.getUnitName() : "";
					BigDecimal requiredQty = detail.getRequiredQty() != null ? detail.getRequiredQty()
							: BigDecimal.ZERO;

					cell = new Cell().setPaddingLeft(5f)
							.add(new Paragraph().add(new Text(rawMaterialName).setFont(basicFont)).setFontSize(12))
							.setTextAlignment(TextAlignment.LEFT);
					tbl.addCell(cell);

					if (isWithQty == 1) {
						cell = new Cell()
								.setPaddingRight(5f).add(new Paragraph()
										.add(new Text(requiredQty + " " + unitName).setFont(basicFont)).setFontSize(12))
								.setTextAlignment(TextAlignment.RIGHT);
						tbl.addCell(cell);
					} else {
						cell = new Cell().setPaddingRight(5f)
								.add(new Paragraph().add(new Text("").setFont(basicFont)).setFontSize(12))
								.setTextAlignment(TextAlignment.RIGHT);
						tbl.addCell(cell);
					}
				}

				if (items.size() % 2 != 0) {
					Cell filler1 = new Cell().add(new Paragraph(""));
					Cell filler2 = new Cell().add(new Paragraph(""));
					tbl.addCell(filler1);
					tbl.addCell(filler2);
				}
			}
			document.add(tbl);

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 5, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ getReportName(getPartyNameByEventId(eventId), formatDate(eventData.getEventDate()),
							"General Fix Report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}
	}

	private EventWiseGeneralFixResponseDto mapGeneralFixReportData(Long eventId) {
		List<Object[]> rows = eventRawMaterialRepository.getEventWiseGeneralFix(eventId);

		EventWiseGeneralFixResponseDto response = new EventWiseGeneralFixResponseDto();

		Map<Long, EventWiseGeneralFixCategoryResponseDto> categoryMap = new LinkedHashMap<>();

		for (Object[] row : rows) {

			if (response.getEventId() == null) {
				response.setEventId(((Number) row[0]).longValue());
				response.setEventName((String) row[1]);
				response.setPax(((Number) row[2]).intValue());
			}

			Long categoryId = row[3] != null ? ((Number) row[3]).longValue() : 0L;

			EventWiseGeneralFixCategoryResponseDto category = categoryMap.get(categoryId);

			if (category == null) {
				category = new EventWiseGeneralFixCategoryResponseDto();
				category.setRawMaterialCatId(categoryId);
				category.setRawMaterialCatName((String) row[4]);
				category.setItems(new ArrayList<>());

				categoryMap.put(categoryId, category);
			}

			EventWiseGeneralFixDetailsResponseDto item = new EventWiseGeneralFixDetailsResponseDto();

			item.setRawMaterialId(((Number) row[5]).longValue());
			item.setRawMaterialName((String) row[6]);

			if (row[7] != null) {
				item.setWeightPer100Pax((BigDecimal) row[7]);
			}

			if (row[8] != null) {
				item.setUnitId(((Number) row[8]).longValue());
			}

			item.setUnitName((String) row[9]);

			if (row[10] != null) {
				item.setRequiredQty((BigDecimal) row[10]);
			}

			category.getItems().add(item);
		}

		response.setDetails(new ArrayList<>(categoryMap.values()));

		return response;
	}

	public UserBasicDetailsMasterEntity getCompanyData(Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		UserBasicDetailsMasterEntity userBasicDetailsEntity = userBasicDetailsMasterRepository.findByUser(user);

		return userBasicDetailsEntity;
	}

	@Override
	public String generateRawaterialReportType7(Long eventId, HttpServletRequest re, Integer lang,
			Long adminTemplateModuleId, Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyDetails, List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isPartyDetails,
			Integer isWithPrice, Integer isCombo, Integer isWithQty) {

		try {

			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);

			if (event == null) {
				return "Event Not Found";
			}

			PdfFont font;
			PdfFont fontBold;

			String customer = "";
			String venue = "";
			String eventName = "";

			String nameLabel = "";
			String weightLabel = "";
			String unitLabel = "";
			String priceLabel = "";
			String totalPriceLabel = "";

			if (lang == 1) {

				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";

				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";

				nameLabel = "नाम";
				weightLabel = "वजन";
				unitLabel = "इकाई";
				priceLabel = "कीमत";
				totalPriceLabel = "कुल कीमत";

				eventName = event.getEventType() != null ? event.getEventType().getNameHindi() : "";

			} else if (lang == 2) {

				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

				fontBold = font;

				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";

				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";

				nameLabel = "નામ";
				weightLabel = "વજન";
				unitLabel = "એકમ";
				priceLabel = "કિંમત";
				totalPriceLabel = "કુલ કિંમત";

				eventName = event.getEventType() != null ? event.getEventType().getNameGujarati() : "";

			} else {

				font = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");

				fontBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");

				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";

				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";

				nameLabel = "NAME";
				weightLabel = "WEIGHT";
				unitLabel = "UNIT";
				priceLabel = "PRICE";
				totalPriceLabel = "TOTAL PRICE";

				eventName = event.getEventType() != null ? event.getEventType().getNameEnglish() : "";
			}

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			boolean withQty = isWithQty != null && isWithQty == 1;

			boolean withPrice = isWithPrice != null && isWithPrice == 1;

			boolean combo = isCombo != null && isCombo == 1;

			GeneralFixRawResponseDto generalFixRawResponseDto = eventFunctionGeneralFixService
					.getAllGeneralFixRaw(eventId, eventFunctionIds, rawMaterialCatIds);

			List<EventGeneralFixRawResponseDto> flatData = (generalFixRawResponseDto != null
					&& generalFixRawResponseDto.getEventGeneralFixRaws() != null)

							? generalFixRawResponseDto.getEventGeneralFixRaws()
							: new ArrayList<>();

			Map<Long, List<EventGeneralFixRawResponseDto>> categoryMap = new LinkedHashMap<>();

			for (EventGeneralFixRawResponseDto row : flatData) {

				categoryMap.computeIfAbsent(row.getRawCatId(), k -> new ArrayList<>()).add(row);
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());

			if (!dir.exists()) {
				dir.mkdirs();
			}

			File pdfFile = new File(dir + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]), "general fix raw material report")
					+ ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));

			Document document = new Document(pdfDoc, PageSize.A4, true);

			document.setMargins(110, 40, 60, 40);

			String logoimg = getLogo(userid);
//			String logoimg = "/flipbook/pages/logo.png";

			if (isCompanyDetails == 1) {

				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}

			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

			ImageData logo = menuPreparationServiceImpl.loadImageFromResource(logoimg);

			String functionNames = "";

			Integer totalPax = 0;

			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {

				Map<Long, EventFunctionMasterEntity> funcMap = eventFunctionMasterRepository
						.findByIdIn(eventFunctionIds).stream()
						.collect(Collectors.toMap(EventFunctionMasterEntity::getId, Function.identity()));

				StringBuilder functions = new StringBuilder();

				for (Entry<Long, EventFunctionMasterEntity> entry : funcMap.entrySet()) {

					Integer pax = entry.getValue().getPax() != null ? entry.getValue().getPax() : 0;

					totalPax = totalPax + pax;

					String functionName;

					if (lang == 1) {

						functionName = entry.getValue().getFunction().getNameHindi() + " (" + pax + ")";

					} else if (lang == 2) {

						functionName = entry.getValue().getFunction().getNameGujarati() + " (" + pax + ")";

					} else {

						functionName = entry.getValue().getFunction().getNameEnglish() + " (" + pax + ")";
					}

					if (functions.length() > 0) {
						functions.append(", ");
					}

					functions.append(functionName);
				}

				functionNames = functions.toString();
			}

			MenuQuantityReponseDto eventData = menuItemRawMaterialServiceImpl.getEventData(eventId, lang);

			if (isCompanyDetails == 1) {

				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,

						new RawMaterialHeaderEventHandler(font, fontBold, logo,

								(lang == 0 ? eventData.getCompanyName().toUpperCase() : eventData.getCompanyName()),

								eventData.getOfficeNo(), eventData.getCompanyEmail()));
			}

			Table headerTable;

			if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {

				headerTable = createHeaderTable(font, fontBold, logo,

						(lang == 0 ? customer.toUpperCase() : customer),

						(lang == 0 ? venue.toUpperCase() : venue),

						dateTime,

						isCompanyDetails,

						functionNames,

						totalPax,

						"",

						lang,

						event.getMobileno(),

						eventName,

						event.getEventStartDateTime(),

						isPartyDetails);

			} else {

				headerTable = createHeaderTable(font, fontBold, logo,

						(lang == 0 ? customer.toUpperCase() : customer),

						(lang == 0 ? venue.toUpperCase() : venue),

						dateTime,

						isCompanyDetails,

						"",

						-1,

						"",

						lang,

						event.getMobileno(),

						eventName,

						event.getEventStartDateTime(),

						isPartyDetails);
			}

			float leftMargin = 40;

			float rightMargin = 40;

			float topMargin = isCompanyDetails == 1 ? 120 : 20;

			float top = PageSize.A4.getTop() - topMargin;

			float bottom = PageSize.A4.getBottom() + 60;

			float height = top - bottom - calculateTableHeight(headerTable, pdfDoc);

			float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;

			float columnWidth = usableWidth / 2;

			Rectangle[] columns = new Rectangle[] {

					new Rectangle(leftMargin, bottom, columnWidth, height),

					new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height) };

			document.setRenderer(new ColumnDocumentRenderer(document, columns));

			RawMaterialHeaderEventHandlerNew headerHandler = new RawMaterialHeaderEventHandlerNew(headerTable,
					topMargin);

			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

			boolean isFirstCategory = true;

			for (Entry<Long, List<EventGeneralFixRawResponseDto>> catEntry : categoryMap.entrySet()) {

				List<EventGeneralFixRawResponseDto> rows = catEntry.getValue();

				if (rows == null || rows.isEmpty()) {
					continue;
				}

				String catName = menuPreparationServiceImpl.formatText(getCategoryNameByLangGeneral(rows.get(0), lang),
						lang);

				if (lang == 0 && catName != null) {
					catName = catName.toUpperCase();
				}

				/* ================= CATEGORY HEADER TABLE ================= */

				Table catHeaderTable;

				if (eventFunctionIds != null && !eventFunctionIds.isEmpty()) {

					catHeaderTable = createHeaderTable(font, fontBold, logo,

							(lang == 0 ? customer.toUpperCase() : customer),

							(lang == 0 ? venue.toUpperCase() : venue),

							dateTime,

							isCompanyDetails,

							functionNames,

							totalPax,

							catName,

							lang,

							event.getMobileno(),

							eventName,

							event.getEventStartDateTime(),

							isPartyDetails);

				} else {

					catHeaderTable = createHeaderTable(font, fontBold, logo,

							(lang == 0 ? customer.toUpperCase() : customer),

							(lang == 0 ? venue.toUpperCase() : venue),

							dateTime,

							isCompanyDetails,

							"",

							-1,

							catName,

							lang,

							event.getMobileno(),

							eventName,

							event.getEventStartDateTime(),

							isPartyDetails);
				}

				headerHandler.updateHeader(catHeaderTable);

				if (!isFirstCategory && !combo) {

					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				if (combo) {

					Table categoryLabelTable = createCategoryLabel(catName, fontBold);

					if (isFirstCategory) {
						categoryLabelTable.setMarginTop(0);
					}

					document.add(categoryLabelTable);
				}

				Table table = new Table(

						UnitValue.createPercentArray(withPrice ? new float[] { 55, 25, 20 } : new float[] { 75f, 25f }),

						false);

				table.setWidth(UnitValue.createPercentValue(100));

				table.setMarginBottom(12);

				table.addHeaderCell(headerCell(nameLabel, fontBold));

				table.addHeaderCell(headerCell(weightLabel + "/" + unitLabel, fontBold));

//				table.addHeaderCell(headerCell(unitLabel, fontBold));

				if(withPrice) {
					table.addHeaderCell(headerCell(priceLabel, fontBold));
				}
				
				BigDecimal totalPrice = BigDecimal.ZERO;

				for (EventGeneralFixRawResponseDto rm : rows) {

					String rmName = menuPreparationServiceImpl.formatText(

							getRawMaterialNameByLangGeneral(rm, lang),

							lang);

					if (lang == 0 && rmName != null) {
						rmName = rmName.toUpperCase();
					}

					table.addCell(dataCell(rmName, font));

					String weightText = "";

					if (withQty && rm.getWeight() != null) {

						weightText = formatNumber(rm.getWeight().doubleValue());
					}

					String unitText = "";

					if (withQty && rm.getUnit() != null) {

						unitText = getUnitNameByLang(rm, lang);
					}

					if (lang == 0 && unitText != null) {

						unitText = unitText.toUpperCase();
					}

					table.addCell(

							dataCell(weightText + " " + unitText, font).setTextAlignment(TextAlignment.RIGHT));
					
//					table.addCell(
//
//							dataCell(unitText, font).setTextAlignment(TextAlignment.LEFT));

					String priceText = "";

					if (withPrice && rm.getPrice() != null) {

						priceText = formatNumber(rm.getPrice().doubleValue());

						totalPrice = totalPrice.add(rm.getPrice());

						table.addCell(
								
								dataCell(priceText, font).setTextAlignment(TextAlignment.RIGHT));
					}

				}

				if (withPrice) {

					table.addCell(

							new Cell(1, 2)

									.add(

											new Paragraph(totalPriceLabel)

													.setFont(font)

													.setFontSize(9))

									.setBorder(

											new SolidBorder(ColorConstants.BLACK, 0.5f))

									.setPadding(4)

									.setTextAlignment(TextAlignment.RIGHT));

					table.addCell(

							dataCell(

									formatNumber(totalPrice.doubleValue()),

									font)

									.setTextAlignment(TextAlignment.RIGHT));
				}

				document.add(table);

				isFirstCategory = false;
			}

			document.close();

			return environment.getProperty("ws_image_path")

					+ "/api/download/pdf/"

					+ event.getEventNo()

					+ "/"

					+ getReportName(

							event.getParty().getNameEnglish(),

							formatDate(

									event.getEventStartDateTime().split(" ")[0]),

							"general fix raw material report")

					+ ".pdf";

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("General Fix Raw Material PDF generation failed", e);
		}

	}

	private String getRawMaterialNameByLangGeneral(EventGeneralFixRawResponseDto row, Integer lang) {

		if (row == null) {
			return "";
		}

		if (lang == 1) {

			return row.getRawNameHindi() != null ? row.getRawNameHindi() : "";
		}

		if (lang == 2) {

			return row.getRawNameGujarati() != null ? row.getRawNameGujarati() : "";
		}

		return row.getRawNameEnglish() != null ? row.getRawNameEnglish() : "";

	}

	private String getCategoryNameByLangGeneral(EventGeneralFixRawResponseDto row, Integer lang) {

		if (row == null) {
			return "";
		}

		if (lang == 1) {

			return row.getRawCatNameHindi() != null ? row.getRawCatNameHindi() : "";
		}

		if (lang == 2) {

			return row.getRawCatNameGujarati() != null ? row.getRawCatNameGujarati() : "";
		}

		return row.getRawCatNameEnglish() != null ? row.getRawCatNameEnglish() : "";

	}

	private String getUnitNameByLang(EventGeneralFixRawResponseDto row, Integer lang) {

		if (row == null || row.getUnit() == null) {
			return "";
		}

		if (lang == 1) {

			return row.getUnit().getNameHindi() != null ? row.getUnit().getNameHindi() : "";
		}

		if (lang == 2) {

			return row.getUnit().getNameGujarati() != null ? row.getUnit().getNameGujarati() : "";
		}

		return row.getUnit().getNameEnglish() != null ? row.getUnit().getNameEnglish() : "";

	}

	private Table createCategoryLabel(String catName, PdfFont fontBold) {

		Table categoryLabelTable = new Table(UnitValue.createPercentArray(new float[] { 100 }), false);

		categoryLabelTable.setWidth(UnitValue.createPercentValue(100));

		categoryLabelTable.setMarginTop(8);
		categoryLabelTable.setMarginBottom(4);

		Cell categoryCell = new Cell()

				.add(new Paragraph(catName).setFont(fontBold).setFontSize(10).simulateBold()
						.setTextAlignment(TextAlignment.CENTER))

				.setBackgroundColor(new DeviceRgb(220, 220, 220))

				.setBorder(new SolidBorder(ColorConstants.WHITE, 1))

				.setPaddingTop(5).setPaddingBottom(5).setPaddingLeft(5).setPaddingRight(5)

				.setTextAlignment(TextAlignment.CENTER);

		categoryLabelTable.addCell(categoryCell);

		return categoryLabelTable;
	}
}
