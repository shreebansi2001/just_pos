package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.plugins.Docket;
import com.crmportal.controller.CityMasterController;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.FunctionMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.FunctionMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.EventFunctionReportResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.EventRawMaterialCategoryResponse;
import com.crmportal.response.dto.EventRawMaterialInfoDto;
import com.crmportal.response.dto.EventRawMaterialPartyDTO;
import com.crmportal.response.dto.EventReportResponseDto;
import com.crmportal.response.dto.GeneralFixResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyWithItemsResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;
import com.crmportal.service.EventFunctionMenuAllocationService;
import com.crmportal.service.EventMasterService;
import com.crmportal.service.EventRawMaterialService;
import com.crmportal.service.RawMaterialReportService;
import com.crmportal.utility.ChefWiseRawMaterialHeaderEventHandler;
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
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.borders.Border.Side;
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
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.splitting.ISplitCharacters;

@Service
public class ChitthiReportService {

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
	EventFunctionMenuAllocationService eventFunctionMenuAllocationService;

	@Autowired
	RoundOffUtility roundOffUtility;

	@Autowired
	Environment environment;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	public String generateChitthiReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, Long contactId, String type) {

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

			File pdfFile = new File(outputPath + "/RawMaterial_" + datetimeforfile + ".pdf");

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

			if(isCompanyDetails == 1) {
				pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new ImageWatermarkEventHandler(logoimg));
			}
			
			if (isCompanyDetails == 1) {
				Image img = new Image(ImageDataFactory.create(logoimg));
//	        	Image img = new Image(menuPreparationServiceImpl.loadImageFromResource(logoimg));
				img.setWidth(100);
				img.setHorizontalAlignment(HorizontalAlignment.LEFT);

				Cell cmp1 = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				cmpHead.addCell(cmp1);
			}

			if (isCompanyDetails == 1) {
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

			

			float[] cloumnWidthEvent = { 25f, 1f, 30f, 15f, 1f, 25f };
			Table eventDataTable = new Table(UnitValue.createPercentArray(cloumnWidthEvent));
			eventDataTable.setMarginBottom(8f);
			eventDataTable.setBorder(new SolidBorder(1f));
			
			List<MenuAllocationAgencyWithItemsResponseDto> mLst =  eventFunctionMenuAllocationService.getAgencyWithItemType(type, eventId,eventFunctionId, contactId);
			MenuAllocationAgencyWithItemsResponseDto ma = mLst.size() != 0 ? mLst.get(0) : null;
			MenuAllocationAgencyResponseDto mar = ma != null && ma.getAgencyResponse().size() != 0 ? ma.getAgencyResponse().get(0) : null;
			
			// 1st row
			Paragraph event = new Paragraph().add(new Text("Agency Name").setFontSize(14f)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			Cell eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(
					new Text(":").setFontSize(14f).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
			eventCell = new Cell().add(event).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
			eventDataTable.addCell(eventCell);

			event = new Paragraph().add(new Text(mar.getContactName()).setFontSize(14f)
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

			event = new Paragraph().add(new Text(mar.getNumber()).setFontSize(14f)
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

			String scheme = re.getScheme();
			String serverName = re.getServerName();
			int serverPort = re.getServerPort();
			String contextPath = re.getContextPath();

			String fullUrl;
			if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
				fullUrl = "https://" + serverName + contextPath + "/api/download/pdf/" + eventNo + "/RawMaterial_"
						+ datetimeforfile + ".pdf";
			} else {
				fullUrl = "https://" + serverName + ":" + serverPort + contextPath + "/api/download/pdf/" + eventNo
						+ "/RawMaterial_" + datetimeforfile + ".pdf";
			}

			fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/RawMaterial_"
					+ datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}

	public static String formatNumber(double value) {
		BigDecimal bd = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
		return bd.stripTrailingZeros().toPlainString();
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
			cell = new Cell(1, 2)
					.add(new Paragraph().add(new Text(venueLabel).simulateBold()).add(new Text(mobile))
							.setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 3)
					.add(new Paragraph().add(new Text(venueLabel).simulateBold()).add(new Text(mobile))
							.setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);
		
		if (isCompanyDetails == 1) {
			cell = new Cell(1, 2)
					.add(new Paragraph().add(new Text(dateLabel).simulateBold()).add(new Text(email))
							.setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 3)
					.add(new Paragraph().add(new Text(dateLabel).simulateBold()).add(new Text(email))
							.setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);
		return table;
	}
	
	public Table createHeaderTable(PdfFont font, PdfFont fontBold, ImageData logo, String customer, String venue,
			String dateTime, Integer isCompanyDetails, String functionName, Integer person, String catName,
			Integer lang) {

		String dateLabel, customerNameLabel, venueLabel, functionLabel, personLabel;

		if (lang == 1) {
			dateLabel = "दिनांक : ";
			customerNameLabel = "ग्राहक का नाम : ";
			venueLabel = "स्थान : ";
			functionLabel = "समारोह : ";
			personLabel = "व्यक्ति : ";
		} else if (lang == 2) {
			dateLabel = "તારીખ : ";
			customerNameLabel = "ગ્રાહકનું નામ : ";
			venueLabel = "સ્થળ : ";
			functionLabel = "કાર્યક્રમ : ";
			personLabel = "વ્યક્તિ : ";
		} else {
			dateLabel = "Date : ";
			customerNameLabel = "Customer Name : ";
			venueLabel = "Venue : ";
			functionLabel = "Function : ";
			personLabel = "Person : ";
		}
		
		/* ---------- HEADER TABLE ---------- */
		Table table = new Table(UnitValue.createPercentArray(new float[] { 24f, 46f, 30f }));
		table.setFixedLayout();
		table.setWidth(UnitValue.createPercentValue(100f));

		// LOGO CELL
		Cell cell;

		Table innerTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 30f, 40f }));
		innerTable.setWidth(UnitValue.createPercentValue(100f));

		if (person != -1) {
			cell = new Cell()
					.add(new Paragraph().add(new Text(functionLabel).simulateBold()).add(new Text(functionName))
							.setFont(font).setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT))
					.setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text(personLabel).simulateBold()).add(new Text(person.toString()))
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
				})).setBorder(Border.NO_BORDER).simulateBold()
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setMinHeight(20f)
				.setBackgroundColor(new DeviceRgb(209, 209, 209));
		innerTable.addCell(cell);

		table.addCell(new Cell(1, 3).add(innerTable).setBorder(Border.NO_BORDER));
		table.addCell(new Cell(1, 3).add(new Paragraph("").setHeight(5f)).setBorder(Border.NO_BORDER));

		return table;
	}


}
