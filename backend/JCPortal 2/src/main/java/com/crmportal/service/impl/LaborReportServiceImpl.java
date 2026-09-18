package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.math3.analysis.function.Ceil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserGodownEntity;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.UserGodownRepository;
import com.crmportal.response.dto.EventLaborDetailsResponseDto;
import com.crmportal.response.dto.GetEventLaborResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.service.LaborReportService;
import com.crmportal.service.TemplateMappingService;
import com.crmportal.utility.PageNumberHandler;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
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
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import kotlin.Unit;

@Service
public class LaborReportServiceImpl implements LaborReportService {

	private final TemplateMappingService templateMappingService;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	EventLaborServiceImpl eventLaborServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	UserGodownRepository userGodownRepository;
	
//	@Autowired
//	EventFunctionMenuAllocationServiceImpl menuAllocationServiceImpl;

	LaborReportServiceImpl(TemplateMappingService templateMappingService) {
		this.templateMappingService = templateMappingService;
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

	private String getPartyNameByEventId(Long eventId) {
		String party = eventMasterRepository.getPartyByEventId(eventId)
				.orElseThrow(() -> new RuntimeException("Party Details Not Found."));
		return party;
	}

//	@Override
//	public String generateLaborChithhi(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
//			Long userid, Integer isUserDetails, List<Long> agencyId) {
//		
//		try {
//			PdfFont basicFont = null;
//			PdfFont boldFont = null;
//			menuPreparationServiceImpl.loadLicense();
//
//			String agencyName  = "Agency";
//			String dateLabel  = "Date";
//			String venueName   = "Venue";
//			String details = "Details";
//
//			if (lang == 1) {
//				// Hindi
//				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
//				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
//				System.out.println("Hindi font loaded successfully");
//				agencyName  = "एजेंसी";
//				dateLabel    = "तारीख";
//				venueName   = "स्थान";
//				details = "विवरण";
//			} else if (lang == 2) {
//				// Gujarati
//				System.out.println("Loading Gujarati font...");
//
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");		
//				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
//				System.out.println("Gujarati font loaded successfully");
//				agencyName  = "એજન્સી";
//				dateLabel  = "તારીખ";
//				venueName   = "સ્થળ";
//				details = "વિગતો";
//				
//			} else {
//				// English
//				System.out.println("Loading English font...");
////				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
//				System.out.println("English font loaded successfully");
//			}
//			
//			List<GetEventLaborResponseDto> dtoList = eventLaborServiceImpl.fetchEventLaborData(eventId, eventFunctionId, re, lang, userid, agencyId);
//			
//			Date now = new Date();
//			String rootPath = re.getSession().getServletContext().getRealPath("/");
//			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
//			File outputPath = new File(rootPath + "resources/tempDownload/labour");
//			
//			if (!outputPath.exists()) {
//				if (outputPath.mkdirs()) {
//					System.out.println("Directory Created!!!");
//				} else {
//					System.out.println("Error!!!");
//				}
//			}
//			
//			File pdfFile = new File(outputPath + "/laborchithhi_" + datetimeforfile + ".pdf");
//		
//			PdfWriter writer = new PdfWriter(pdfFile);
//			PdfDocument pdfDocument = new PdfDocument(writer);
//			Document document = new Document(pdfDocument);
//			document.setMargins(30,  35,  50,  20);
//			System.out.println("dtoList : " + dtoList.size());
//			for (int i = 0; i < dtoList.size(); i++) {
//
//			    GetEventLaborResponseDto dto = dtoList.get(i);
//				
//				String companyName = dto.getCompanyName() == null || dto.getCompanyName().isEmpty() ? "" : dto.getCompanyName();
//				String companyMobile = dto.getCompanyMobile() == null || dto.getCompanyMobile().isEmpty() ? "" : dto.getCompanyMobile();
//				String countryCode = dto.getCountryCode() == null || dto.getCountryCode().isEmpty() ? "" : dto.getCountryCode();
//				String companyEmail = dto.getCompanyEmail() == null || dto.getCompanyEmail().isEmpty() ? "" : dto.getCompanyEmail();
//				String agency = dto.getLaborName() == null || dto.getLaborName().isEmpty() ? "" : dto.getLaborName();
//				String date = dto.getLaborDate() == null || dto.getLaborDate().isEmpty() ? "" : dto.getLaborDate();
//				String venue = dto.getVenue() == null || dto.getVenue().isEmpty() ? "" : dto.getVenue();
//				String companuyLogo = dto.getLogo() == null || dto.getLogo().isEmpty() ? "" : dto.getLogo();
//				String functionVenue = dto.getFunctionVenue() == null || dto.getFunctionVenue().isEmpty() ? "" : dto.getFunctionVenue();
//				String functionDate = dto.getFunctionStartDate() == null || dto.getFunctionStartDate().isEmpty() ? "" : dto.getFunctionStartDate();
//				String functionTime = dto.getFunctionStartTime() == null || dto.getFunctionStartTime().isEmpty() ? "" : dto.getFunctionStartTime();
//				String laborDate = dto.getLaborDate() == null || dto.getLaborDate().isEmpty() ? "" : dto.getLaborDate();
//				Long partyId = dto.getPartyId();
//				Long efId = dto.getEventFunctionId();
//				
//				ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//				Image img = new Image(logoData);
//				
////				Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url")+companuyLogo));
//				img.setWidth(120);
//				img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//				
//				float[] columnWidth = {20f, 80f};
//				Table cmpHead = new Table(UnitValue.createPercentArray(columnWidth));
//				cmpHead.setWidth(UnitValue.createPercentValue(80));
//				cmpHead.setHorizontalAlignment(HorizontalAlignment.CENTER);
////				cmpHead.setMarginTop(100f);
//				
//				Cell logo = new Cell()
//							.add(img)
//							.setTextAlignment(TextAlignment.CENTER)
//							.setBorder(Border.NO_BORDER);
//				
//				Paragraph cmpInfo = new Paragraph()
//						.add(new Text(companyName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16))
//						.add(new Text("\nMobile No : ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
//						.add(new Text(countryCode + " " + companyMobile).setFont(basicFont).setFontSize(12))
//						.add(new Text("\nEmail : ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
//						.add(new Text(companyEmail).setFont(basicFont).setFontSize(12));
//				
//				Cell info = new Cell()
//						.add(cmpInfo)
//						.setPaddingLeft(10f)
//						.setVerticalAlignment(VerticalAlignment.MIDDLE)
//						.setBorder(Border.NO_BORDER);
//				
//				cmpHead.addCell(logo);
//				cmpHead.addCell(info);
//				
//				if(isUserDetails == 1) {					
//					document.add(cmpHead);
//				}
//				
//				document.add(new Paragraph("\n"));
//
//				float[] columnWidth2 = {18f, 3f, 79f};
//				Table laborTable = new Table(UnitValue.createPercentArray(columnWidth2));
//				laborTable.setWidth(UnitValue.createPercentValue(80));
//				laborTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
//				laborTable.setMarginTop(-20f);
//				
//				Cell label = new Cell()
//						.add(new Paragraph(agencyName).setFont(boldFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(15)
//						.setBorder(Border.NO_BORDER);
//				
//				Cell seperator = new Cell()
//						.add(new Paragraph(":").setFont(basicFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				Cell detail = new Cell()
//						.add(new Paragraph(agency).setFont(basicFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				laborTable.addCell(label);
//				laborTable.addCell(seperator);
//				laborTable.addCell(detail);
//				
//				label = new Cell()
//						.add(new Paragraph(dateLabel).setFont(boldFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(15)
//						.setBorder(Border.NO_BORDER);
//				
//				seperator = new Cell()
//						.add(new Paragraph(":").setFont(basicFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				detail = new Cell()
//						.add(new Paragraph(laborDate).setFont(basicFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				laborTable.addCell(label);
//				laborTable.addCell(seperator);
//				laborTable.addCell(detail);
//				
//				label = new Cell()
//						.add(new Paragraph(venueName).setFont(boldFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(15)
//						.setBorder(Border.NO_BORDER);
//				
//				seperator = new Cell()
//						.add(new Paragraph(":").setFont(basicFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				detail = new Cell()
//						.add(new Paragraph(functionVenue).setFont(basicFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				laborTable.addCell(label);
//				laborTable.addCell(seperator);
//				laborTable.addCell(detail);
//				
//				document.add(laborTable);
//				
//				float[] columnWidth3 = {100f};
//				Table detailsTab = new Table(UnitValue.createPercentArray(columnWidth3));
//				detailsTab.setWidth(UnitValue.createPercentValue(80));
//				detailsTab.setHorizontalAlignment(HorizontalAlignment.CENTER);
//				
//				Cell detailsHeader = new Cell()
//						.add(new Paragraph(details).setFont(boldFont).setFontSize(15).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(15)
//						.setBackgroundColor(new DeviceRgb(240, 240, 240))
//						.setBorder(Border.NO_BORDER);
//
//				detailsTab.addCell(detailsHeader);
//				
//				document.add(detailsTab);
//				
//				float[] columnWidth4 = {100f};
//				Table laborDetail = new Table(columnWidth4);
//				laborDetail.setWidth(UnitValue.createPercentValue(80));
//				laborDetail.setHorizontalAlignment(HorizontalAlignment.CENTER);
//					Integer qty = dto.getQty();
//					String contactCategoryName = dto.getContactCategoryName() == null || dto.getContactCategoryName().isEmpty() ? "" : dto.getContactCategoryName();
//					String time = dto.getFunctionStartTime() == null || dto.getFunctionStartTime().isEmpty() ? "" : dto.getFunctionStartTime();
//					System.out.println("dtoDetail : " + dto.toString());
//					 
//					Paragraph data = new Paragraph()
//							.add(new Text(qty.toString()).setUnderline())
//							.add(new Text(" - "))
//							.add(new Text(contactCategoryName))
//							.add(new Text(" ("))
//							.add(new Text(time))
//							.add(new Text(") "))
//							.setFont(basicFont)
//							.setFontSize(15)
//							.setMargin(0).setMultipliedLeading(1)
//							.setPaddingLeft(15f);
//					
//					Cell laborData = new Cell()
//							.add(data)
//							.setTextAlignment(TextAlignment.LEFT)
//							.setBorder(Border.NO_BORDER);
//					
//					laborDetail.addCell(laborData);
//						
//				document.add(laborDetail);
//				
//				if (i < dtoList.size() - 1) {
//			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//			    }
//				
//			}
//			
//			document.close();
//			
//			String scheme = re.getScheme();
//			String serverName = re.getServerName();
//			int serverPort = re.getServerPort();
//			String contextPath = re.getContextPath();
//
//			String fullUrl;
//			if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
//				fullUrl = "https://" + serverName + contextPath + "/api/download/pdf/labour" 
//						+ "/laborchithhi_" + datetimeforfile + ".pdf";
//			} else {
//				fullUrl = "https://" + serverName + ":" + serverPort + contextPath + "/api/download/pdf/labour"
//						+ "/laborchithhi_" + datetimeforfile + ".pdf";
//			}
//			
//			return fullUrl;
//			
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Fails to generate labor chithhi report.");
//		}
//				
//	}

	@Override
	public String generateLaborChithhi(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isUserDetails, List<Long> agencyId, Integer withPrice) {

		try {

			// ---------- Load Fonts ----------
			PdfFont basicFont = null;
			PdfFont boldFont = null;

			menuPreparationServiceImpl.loadLicense();

			String agencyLabel = "Agency";
			String dateLabel = "Date";
			String venueLabel = "Venue";
			String detailsLabel = "Details";
			String priceLabel = "Price";
			String totalpriceLabel = "Total";
			String qtyLable = "Qty";
			String mobileLabel = "Mobile No : ";
			String emailLabel = "Email : ";
			String eventLabel = "Event";

			if (lang == 1) {

				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

				agencyLabel = "एजेंसी";
				dateLabel = "तारीख";
				venueLabel = "स्थान";
				detailsLabel = "विवरण";
				mobileLabel = "मोबाइल नंबर : ";
				qtyLable = "मात्रा";
				emailLabel = "ईमेल : ";
				priceLabel = "मूल्य";
				totalpriceLabel = "कुल मूल्य";
				eventLabel = "कार्यक्रम";

			} else if (lang == 2) {

				basicFont = menuPreparationServiceImpl
						.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

				boldFont = menuPreparationServiceImpl
						.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

				agencyLabel = "એજન્સી";
				dateLabel = "તારીખ";
				venueLabel = "સ્થળ";
				detailsLabel = "વિગતો";
				mobileLabel = "મોબાઈલ નંબર : ";
				qtyLable = "માત્રા";
				priceLabel = "કિંમત";
				totalpriceLabel = "કુલ કિંમત";
				emailLabel = "ઈમેલ : ";
				eventLabel = "કાર્યક્રમ";

			} else {

				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}

			EventMasterEntity event = eventMasterRepository.findById(eventId)
					.orElseThrow(() ->
							new RuntimeException("Event Id not found with id : " + eventId));

			// ---------- Fetch Data ----------
			List<GetEventLaborResponseDto> dtoList =
					eventLaborServiceImpl.fetchEventLaborData(
							eventId,
							eventFunctionId,
							re,
							lang,
							userid,
							agencyId);

			// ---------- Prepare File ----------
			String rootPath = re.getSession().getServletContext().getRealPath("/");

			File outputPath = new File(rootPath + "resources/tempDownload/labour");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			String fileName = getPartyNameByEventId(eventId);

			if (agencyId.size() == 1) {

				PartyMasterEntity party =
						partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0))
								.orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File pdfFile = new File(
					outputPath + "/"
							+ getReportName(
									fileName,
									formatDate(event.getEventStartDateTime()),
									"labor chitthi")
							+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);

			PdfDocument pdfDocument = new PdfDocument(writer);

			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);

			document.setMargins(30, 30, 70, 30);

			// =====================================================
			// GROUPING : AGENCY -> DATE -> VENUE
			// =====================================================

			Map<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> groupedData =
					dtoList.stream()
							.collect(Collectors.groupingBy(
									GetEventLaborResponseDto::getLaborName,
									LinkedHashMap::new,

									Collectors.groupingBy(
											GetEventLaborResponseDto::getLaborDate,
											LinkedHashMap::new,

											Collectors.groupingBy(
													dto -> dto.getFunctionVenue() != null
															? dto.getFunctionVenue()
															: "",
													LinkedHashMap::new,
													Collectors.toList()))));

			List<Map.Entry<String,
					Map<String,
							Map<String, List<GetEventLaborResponseDto>>>>> laborEntries =
					new ArrayList<>(groupedData.entrySet());

			for (int i = 0; i < laborEntries.size(); i++) {

				Map.Entry<String,
						Map<String,
								Map<String, List<GetEventLaborResponseDto>>>> laborEntry =
						laborEntries.get(i);

				String laborName = laborEntry.getKey();

				Map<String, Map<String, List<GetEventLaborResponseDto>>> dateMap =
						laborEntry.getValue();

				List<Map.Entry<String,
						Map<String, List<GetEventLaborResponseDto>>>> dateEntries =
						new ArrayList<>(dateMap.entrySet());

				for (int j = 0; j < dateEntries.size(); j++) {

					Map.Entry<String,
							Map<String, List<GetEventLaborResponseDto>>> dateEntry =
							dateEntries.get(j);

					String laborDate = dateEntry.getKey();

					Map<String, List<GetEventLaborResponseDto>> venueMap =
							dateEntry.getValue();

					// =====================================================
					// HEADER DATA
					// =====================================================

					GetEventLaborResponseDto firstDto =
							venueMap.values().iterator().next().get(0);

					String companyName =
							firstDto.getCompanyName() != null
									? firstDto.getCompanyName()
									: "";

					String companyMobile =
							firstDto.getCompanyMobile() != null
									? firstDto.getCompanyMobile()
									: "";

					String countryCode =
							firstDto.getCountryCode() != null
									? firstDto.getCountryCode()
									: "";

					String companyEmail =
							firstDto.getCompanyEmail() != null
									? firstDto.getCompanyEmail()
									: "";

					String eventName = "";

					if (lang == 1) {

						eventName =
								firstDto.getEventNameHindi() != null
										? firstDto.getEventNameHindi()
										: "";

					} else if (lang == 2) {

						eventName =
								firstDto.getEventNameGujarati() != null
										? firstDto.getEventNameGujarati()
										: "";

					} else {

						eventName =
								firstDto.getEventNameEnglish() != null
										? firstDto.getEventNameEnglish()
										: "";
					}

					// =====================================================
					// COMPANY HEADER
					// =====================================================

					ImageData logoData =
							menuPreparationServiceImpl.loadImageFromResource(
									environment.getProperty("app.image.url")
											+ firstDto.getLogo());

					Image img = new Image(logoData)
							.setWidth(110)
							.setHorizontalAlignment(HorizontalAlignment.CENTER);

					float[] columnWidth = { 20f, 80f };

					Table cmpHead =
							new Table(UnitValue.createPercentArray(columnWidth))
									.setWidth(UnitValue.createPercentValue(80))
									.setHorizontalAlignment(
											HorizontalAlignment.CENTER)
									.setMarginBottom(10f);

					Cell logoCell =
							new Cell()
									.add(img)
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(Border.NO_BORDER);

					Paragraph cmpInfo =
							new Paragraph()
									.add(new Text(companyName + "\n")
											.setFont(basicFont)
											.setFontSize(16)
											.simulateBold())
									.add(new Text(mobileLabel)
											.setFont(boldFont)
											.setFontSize(12))
									.add(new Text(countryCode + " "
											+ companyMobile + "\n")
											.setFont(basicFont)
											.setFontSize(12))
									.add(new Text(emailLabel)
											.setFont(boldFont)
											.setFontSize(12))
									.add(new Text(companyEmail)
											.setFont(basicFont)
											.setFontSize(12));

					Cell infoCell =
							new Cell()
									.add(cmpInfo)
									.setPaddingLeft(10f)
									.setVerticalAlignment(
											VerticalAlignment.MIDDLE)
									.setBorder(Border.NO_BORDER);

					cmpHead.addCell(logoCell);
					cmpHead.addCell(infoCell);

					if (isUserDetails != null && isUserDetails == 1) {
						document.add(cmpHead);
					}

					// =====================================================
					// MAIN HEADER (DISPLAY ONE TIME)
					// =====================================================

					Table laborTable =
							new Table(UnitValue.createPercentArray(
									new float[] { 18f, 3f, 79f }))
									.setWidth(UnitValue.createPercentValue(80))
									.setHorizontalAlignment(
											HorizontalAlignment.CENTER)
									.setMarginTop(10f);

					laborTable.addCell(
							createLabelCell(agencyLabel, boldFont));

					laborTable.addCell(
							createSeparatorCell(basicFont));

					laborTable.addCell(
							createDetailCell(laborName, basicFont));

					laborTable.addCell(
							createLabelCell(dateLabel, boldFont));

					laborTable.addCell(
							createSeparatorCell(basicFont));

					laborTable.addCell(
							createDetailCell(laborDate, basicFont));

					laborTable.addCell(
							createLabelCell(eventLabel, boldFont));

					laborTable.addCell(
							createSeparatorCell(basicFont));

					laborTable.addCell(
							createDetailCell(eventName, basicFont));

					document.add(laborTable);

					// =====================================================
					// VENUE WISE DATA
					// =====================================================

					List<Map.Entry<String,
							List<GetEventLaborResponseDto>>> venueEntries =
							new ArrayList<>(venueMap.entrySet());

					for (int k = 0; k < venueEntries.size(); k++) {

						Map.Entry<String,
								List<GetEventLaborResponseDto>> venueEntry =
								venueEntries.get(k);

						String functionVenue = venueEntry.getKey();

						List<GetEventLaborResponseDto> records =
								venueEntry.getValue();

						// =====================================================
						// VENUE HEADER
						// =====================================================

						Table venueTable =
								new Table(UnitValue.createPercentArray(
										new float[] { 18f, 3f, 79f }))
										.setWidth(
												UnitValue.createPercentValue(80))
										.setHorizontalAlignment(
												HorizontalAlignment.CENTER)
										.setMarginTop(10f);

						venueTable.addCell(
								createLabelCell(venueLabel, boldFont));

						venueTable.addCell(
								createSeparatorCell(basicFont));

						venueTable.addCell(
								createDetailCell(functionVenue, basicFont));

						document.add(venueTable);

						// =====================================================
						// DETAILS HEADER
						// =====================================================

						Table detailsTab;

						if (withPrice == 1) {

							detailsTab =
									new Table(UnitValue.createPercentArray(
											new float[] {
													10f,
													60f,
													10f,
													20f
											}));

						} else {

							detailsTab =
									new Table(UnitValue.createPercentArray(
											new float[] {
													10f,
													90f
											}));
						}

						detailsTab
								.setWidth(UnitValue.createPercentValue(80))
								.setHorizontalAlignment(
										HorizontalAlignment.CENTER);

						detailsTab.addCell(
								new Cell()
										.add(new Paragraph(qtyLable)
												.setFont(boldFont)
												.setFontSize(15))
										.setPaddingLeft(15)
										.setBackgroundColor(
												new DeviceRgb(240, 240, 240))
										.setBorder(Border.NO_BORDER));

						detailsTab.addCell(
								new Cell()
										.add(new Paragraph(detailsLabel)
												.setFont(boldFont)
												.setFontSize(15))
										.setPaddingLeft(15)
										.setBackgroundColor(
												new DeviceRgb(240, 240, 240))
										.setBorder(Border.NO_BORDER));

						if (withPrice == 1) {

							detailsTab.addCell(
									new Cell()
											.add(new Paragraph(priceLabel)
													.setFont(boldFont)
													.setFontSize(15))
											.setPaddingLeft(15)
											.setBackgroundColor(
													new DeviceRgb(
															240,
															240,
															240))
											.setBorder(Border.NO_BORDER));

							detailsTab.addCell(
									new Cell()
											.add(new Paragraph(
													totalpriceLabel)
													.setFont(boldFont)
													.setFontSize(15))
											.setPaddingLeft(15)
											.setBackgroundColor(
													new DeviceRgb(
															240,
															240,
															240))
											.setBorder(Border.NO_BORDER));
						}

						document.add(detailsTab);

						// =====================================================
						// LABOR DETAILS
						// =====================================================

						Table laborDetail;

						if (withPrice == 1) {

							laborDetail =
									new Table(UnitValue.createPercentArray(
											new float[] {
													10f,
													60f,
													10f,
													20f
											}));

						} else {

							laborDetail =
									new Table(UnitValue.createPercentArray(
											new float[] {
													10f,
													90f
											}));
						}

						laborDetail
								.setWidth(UnitValue.createPercentValue(80))
								.setHorizontalAlignment(
										HorizontalAlignment.CENTER);

						for (GetEventLaborResponseDto dto : records) {

							String contactCat = "";

							BigDecimal price =
									dto.getPrice() != null
											? dto.getPrice()
											: BigDecimal.ZERO;

							BigDecimal totalprice =
									dto.getTotalPrice() != null
											? dto.getTotalPrice()
											: BigDecimal.ZERO;

							if (lang == 1) {

								contactCat =
										dto.getContactCategoryNameHindi() != null
												? dto.getContactCategoryNameHindi()
												: "";

							} else if (lang == 2) {

								contactCat =
										dto.getContactCategoryNameGujarati() != null
												? dto.getContactCategoryNameGujarati()
												: "";

							} else {

								contactCat =
										dto.getContactCategoryName() != null
												? dto.getContactCategoryName()
												: "";
							}

							Paragraph data =
									new Paragraph()
											.add(" - ")
											.add(contactCat)
											.add(" (")
											.add(dto.getLaborTime())
											.add(")")
											.add(" (")
											.add(dto.getLaborShift() != null
													? dto.getLaborShift()
															.toUpperCase()
													: "")
											.add(")")
											.setFont(basicFont)
											.setFontSize(15)
											.setPaddingLeft(5);

							laborDetail.addCell(
									new Cell()
											.add(new Paragraph(
													dto.getQty().toString())
													.setFont(basicFont)
													.setFontSize(15))
											.setUnderline()
											.setPaddingLeft(10f)
											.setBorder(Border.NO_BORDER)
											.setTextAlignment(
													TextAlignment.CENTER));

							laborDetail.addCell(
									new Cell()
											.add(data)
											.setBorder(Border.NO_BORDER));

							if (withPrice == 1) {

								laborDetail.addCell(
										new Cell()
												.add(new Paragraph(
														price.toString())
														.setFont(basicFont)
														.setFontSize(15))
												.setBorder(Border.NO_BORDER)
												.setTextAlignment(
														TextAlignment.CENTER));

								laborDetail.addCell(
										new Cell()
												.add(new Paragraph(
														totalprice.toString())
														.setFont(basicFont)
														.setFontSize(15))
												.setBorder(Border.NO_BORDER)
												.setTextAlignment(
														TextAlignment.CENTER));
							}
						}

						document.add(laborDetail);
					}

					// =====================================================
					// PAGE BREAK
					// =====================================================

					boolean isLastLabor =
							(i == laborEntries.size() - 1);

					boolean isLastDate =
							(j == dateEntries.size() - 1);

					if (!(isLastLabor && isLastDate)) {

						document.add(
								new AreaBreak(AreaBreakType.NEXT_PAGE));
					}
				}
			}

			// =====================================================
			// TOTAL PAGE COUNT
			// =====================================================

			PdfFormXObject totalPagesPlaceholder =
					pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas =
					new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas =
					new Canvas(pdfCanvas,
							new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(
					String.valueOf(pdfDocument.getNumberOfPages()),
					5,
					5,
					TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl =
					environment.getProperty("ws_image_path")
							+ "/api/download/pdf/labour/"
							+ getReportName(
									fileName,
									formatDate(event.getEventStartDateTime()),
									"labor chitthi")
							+ ".pdf";

			return fullUrl;

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException(
					"Failed to generate labor chithhi report.");
		}
	}

	@Override
	public String generateLaborChithhiPageSizeA5(Long eventId, Long eventFunctionId, HttpServletRequest re,
	        int lang, Long userid, Integer isUserDetails, List<Long> agencyId, Integer withPrice) {

	    try {

	        // ---------- Load Fonts ----------
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        menuPreparationServiceImpl.loadLicense();

	        String agencyLabel = "Agency";
	        String dateLabel = "Date";
	        String venueLabel = "Venue";
	        String detailsLabel = "Details";
	        String priceLabel = "Price";
	        String totalpriceLabel = "Total";
	        String qtyLable = "Qty";
	        String mobileLabel = "Mobile No : ";
	        String emailLabel = "Email : ";
	        String eventLabel = "Event";
	        String grandTotalLabel = "Grand Total";

	        if (lang == 1) {

	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

	            agencyLabel = "एजेंसी";
	            dateLabel = "तारीख";
	            venueLabel = "स्थान";
	            detailsLabel = "विवरण";
	            mobileLabel = "मोबाइल नंबर : ";
	            qtyLable = "मात्रा";
	            emailLabel = "ईमेल : ";
	            priceLabel = "मूल्य";
	            totalpriceLabel = "कुल मूल्य";
	            eventLabel = "कार्यक्रम";
	            grandTotalLabel = "ग्रांड टोटल";

	        } else if (lang == 2) {

	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

	            agencyLabel = "એજન્સી";
	            dateLabel = "તારીખ";
	            venueLabel = "સ્થળ";
	            detailsLabel = "વિગતો";
	            mobileLabel = "મોબાઈલ નંબર : ";
	            qtyLable = "માત્રા";
	            priceLabel = "કિંમત";
	            totalpriceLabel = "કુલ કિંમત";
	            emailLabel = "ઈમેલ : ";
	            eventLabel = "કાર્યક્રમ";
	            grandTotalLabel = "ગ્રાન્ડ ટોટલ";

	        } else {

	            basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	            boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	        }

	        EventMasterEntity event = eventMasterRepository.findById(eventId)
	                .orElseThrow(() -> new RuntimeException("Event Id not found with id : " + eventId));

	        // ---------- Fetch Data ----------
	        List<GetEventLaborResponseDto> dtoList = eventLaborServiceImpl.fetchEventLaborData(eventId,
	                eventFunctionId, re, lang, userid, agencyId);

	        // ---------- Prepare File ----------
	        String rootPath = re.getSession().getServletContext().getRealPath("/");
	        File outputPath = new File(rootPath + "resources/tempDownload/labour");

	        if (!outputPath.exists()) {
	            outputPath.mkdirs();
	        }

	        String fileName = getPartyNameByEventId(eventId);

	        if (agencyId.size() == 1) {

	            PartyMasterEntity party = partyMasterRepository
	                    .findByIdAndIsDeleteFalse(agencyId.get(0))
	                    .orElse(null);

	            if (party != null) {
	                fileName = party.getNameEnglish();
	            }
	        }

	        File pdfFile = new File(outputPath + "/"
	                + getReportName(fileName,
	                        formatDate(event.getEventStartDateTime()),
	                        "labor chitthi")
	                + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);

	        pdfDocument.setDefaultPageSize(PageSize.A5);

	        PageNumberHandler pageHandler = new PageNumberHandler();
	        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

	        Document document = new Document(pdfDocument);
	        document.setMargins(30, 30, 70, 30);

	        /*
	         * =========================================================
	         * GROUPING :
	         * Labor Name -> Date -> Venue
	         * =========================================================
	         */
	        Map<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> groupedData = dtoList
	                .stream()
	                .collect(Collectors.groupingBy(
	                        GetEventLaborResponseDto::getLaborName,
	                        LinkedHashMap::new,
	                        Collectors.groupingBy(
	                                GetEventLaborResponseDto::getLaborDate,
	                                LinkedHashMap::new,
	                                Collectors.groupingBy(
	                                        dto -> dto.getFunctionVenue() != null
	                                                ? dto.getFunctionVenue()
	                                                : "",
	                                        LinkedHashMap::new,
	                                        Collectors.toList()))));

	        List<Map.Entry<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>>> laborEntries =
	                new ArrayList<>(groupedData.entrySet());

	        for (int i = 0; i < laborEntries.size(); i++) {

	            Map.Entry<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> laborEntry =
	                    laborEntries.get(i);

	            String laborName = laborEntry.getKey();

	            Map<String, Map<String, List<GetEventLaborResponseDto>>> dateMap =
	                    laborEntry.getValue();

	            List<Map.Entry<String, Map<String, List<GetEventLaborResponseDto>>>> dateEntries =
	                    new ArrayList<>(dateMap.entrySet());

	            for (int j = 0; j < dateEntries.size(); j++) {

	                Map.Entry<String, Map<String, List<GetEventLaborResponseDto>>> dateEntry =
	                        dateEntries.get(j);

	                String laborDate = dateEntry.getKey();

	                Map<String, List<GetEventLaborResponseDto>> venueMap =
	                        dateEntry.getValue();

	                /*
	                 * =========================================================
	                 * FIRST RECORD FOR COMMON HEADER
	                 * =========================================================
	                 */
	                GetEventLaborResponseDto firstDto =
	                        venueMap.values().iterator().next().get(0);

	                // ---------- Company Values ----------
	                String companyName = firstDto.getCompanyName() != null
	                        ? firstDto.getCompanyName()
	                        : "";

	                String companyMobile = firstDto.getCompanyMobile() != null
	                        ? firstDto.getCompanyMobile()
	                        : "";

	                String countryCode = firstDto.getCountryCode() != null
	                        ? firstDto.getCountryCode()
	                        : "";

	                String companyEmail = firstDto.getCompanyEmail() != null
	                        ? firstDto.getCompanyEmail()
	                        : "";

	                String eventName = "";

	                if (lang == 1) {

	                    eventName = firstDto.getEventNameHindi() != null
	                            ? firstDto.getEventNameHindi()
	                            : "";

	                } else if (lang == 2) {

	                    eventName = firstDto.getEventNameGujarati() != null
	                            ? firstDto.getEventNameGujarati()
	                            : "";

	                } else {

	                    eventName = firstDto.getEventNameEnglish() != null
	                            ? firstDto.getEventNameEnglish()
	                            : "";
	                }

	                /*
	                 * =========================================================
	                 * COMPANY HEADER
	                 * =========================================================
	                 */

	                ImageData logoData = menuPreparationServiceImpl
	                        .loadImageFromResource(
	                                environment.getProperty("app.image.url")
	                                        + firstDto.getLogo());

//	                ImageData logoData = menuPreparationServiceImpl
//	                        .loadImageFromResource("/flipbook/pages/logo.png");
	                
	                Image img = new Image(logoData)
	                        .setWidth(80)
	                        .setHorizontalAlignment(HorizontalAlignment.CENTER);

	                float[] columnWidth = { 20f, 80f };

	                Table cmpHead = new Table(UnitValue.createPercentArray(columnWidth))
	                        .setWidth(UnitValue.createPercentValue(100))
	                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
	                        .setMarginBottom(10f);

	                Cell logoCell = new Cell()
	                        .add(img)
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setBorder(Border.NO_BORDER);

	                Paragraph cmpInfo = new Paragraph()
	                        .add(new Text(companyName + "\n")
	                                .setFont(basicFont)
	                                .setFontSize(14)
	                                .simulateBold())
	                        .add(new Text(mobileLabel)
	                                .setFont(boldFont)
	                                .setFontSize(10))
	                        .add(new Text(countryCode + " " + companyMobile + "\n")
	                                .setFont(basicFont)
	                                .setFontSize(10))
	                        .add(new Text(emailLabel)
	                                .setFont(boldFont)
	                                .setFontSize(10))
	                        .add(new Text(companyEmail)
	                                .setFont(basicFont)
	                                .setFontSize(10));

	                Cell infoCell = new Cell()
	                        .add(cmpInfo)
	                        .setPaddingLeft(10f)
	                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
	                        .setBorder(Border.NO_BORDER);

	                cmpHead.addCell(logoCell);
	                cmpHead.addCell(infoCell);

	                if (isUserDetails != null && isUserDetails == 1) {
	                    document.add(cmpHead);
	                }

	                /*
	                 * =========================================================
	                 * COMMON HEADER
	                 * SHOW ONLY ONE TIME
	                 * =========================================================
	                 */

	                Table laborTable = new Table(
	                        UnitValue.createPercentArray(new float[] { 18f, 3f, 79f }))
	                        .setWidth(UnitValue.createPercentValue(100))
	                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
	                        .setMarginTop(5f);

	                laborTable.addCell(createLabelCellForA5(agencyLabel, boldFont));
	                laborTable.addCell(createSeparatorCellForA5(basicFont));
	                laborTable.addCell(createDetailCellForA5(laborName, basicFont));

	                laborTable.addCell(createLabelCellForA5(dateLabel, boldFont));
	                laborTable.addCell(createSeparatorCellForA5(basicFont));
	                laborTable.addCell(createDetailCellForA5(laborDate, basicFont));

	                laborTable.addCell(createLabelCellForA5(eventLabel, boldFont));
	                laborTable.addCell(createSeparatorCellForA5(basicFont));
	                laborTable.addCell(createDetailCellForA5(eventName, basicFont));

	                document.add(laborTable);

	                /*
	                 * =========================================================
	                 * VENUE WISE DATA
	                 * MULTIPLE VENUE IN SAME PAGE
	                 * =========================================================
	                 */

	                for (Map.Entry<String, List<GetEventLaborResponseDto>> venueEntry :
	                        venueMap.entrySet()) {

	                    String functionVenue = venueEntry.getKey();

	                    List<GetEventLaborResponseDto> records =
	                            venueEntry.getValue();

	                    // ---------- Venue Heading ----------
	                    Paragraph venuePara = new Paragraph(
	                            venueLabel + " : " + functionVenue)
	                            .setFont(boldFont)
	                            .setFontSize(12)
	                            .setMarginTop(15f)
	                            .setMarginBottom(5f);

	                    document.add(venuePara);

	                    /*
	                     * =========================================================
	                     * DETAILS HEADER
	                     * =========================================================
	                     */

	                    Table detailsTab = new Table(
	                            UnitValue.createPercentArray(
	                                    withPrice == 1
	                                            ? new float[] { 10f, 60f, 10f, 20f }
	                                            : new float[] { 10f, 90f }))
	                            .setWidth(UnitValue.createPercentValue(100))
	                            .setHorizontalAlignment(HorizontalAlignment.CENTER);

	                    detailsTab.addCell(
	                            new Cell()
	                                    .add(new Paragraph(qtyLable)
	                                            .setFont(boldFont)
	                                            .setFontSize(12))
	                                    .setPaddingLeft(12)
	                                    .setBackgroundColor(new DeviceRgb(240, 240, 240))
	                                    .setBorder(Border.NO_BORDER));

	                    detailsTab.addCell(
	                            new Cell()
	                                    .add(new Paragraph(detailsLabel)
	                                            .setFont(boldFont)
	                                            .setFontSize(12))
	                                    .setPaddingLeft(10f)
	                                    .setBackgroundColor(new DeviceRgb(240, 240, 240))
	                                    .setBorder(Border.NO_BORDER));

	                    if (withPrice == 1) {

	                        detailsTab.addCell(
	                                new Cell()
	                                        .add(new Paragraph(priceLabel)
	                                                .setFont(boldFont)
	                                                .setFontSize(12))
	                                        .setPaddingLeft(12)
	                                        .setBackgroundColor(new DeviceRgb(240, 240, 240))
	                                        .setBorder(Border.NO_BORDER));

	                        detailsTab.addCell(
	                                new Cell()
	                                        .add(new Paragraph(totalpriceLabel)
	                                                .setFont(boldFont)
	                                                .setFontSize(12))
	                                        .setPaddingLeft(12)
	                                        .setBackgroundColor(new DeviceRgb(240, 240, 240))
	                                        .setBorder(Border.NO_BORDER));
	                    }

	                    document.add(detailsTab);

	                    /*
	                     * =========================================================
	                     * VENUE DETAIL TABLE
	                     * =========================================================
	                     */

	                    Table laborDetail = new Table(
	                            UnitValue.createPercentArray(
	                                    withPrice == 1
	                                            ? new float[] { 10f, 60f, 10f, 20f }
	                                            : new float[] { 10f, 90f }))
	                            .setWidth(UnitValue.createPercentValue(100))
	                            .setHorizontalAlignment(HorizontalAlignment.CENTER);

	                    BigDecimal grandTotal = BigDecimal.ZERO;

	                    for (GetEventLaborResponseDto dto : records) {

	                        String contactCat = "";

	                        if (lang == 1) {

	                            contactCat =
	                                    dto.getContactCategoryNameHindi() != null
	                                            ? dto.getContactCategoryNameHindi()
	                                            : "";

	                        } else if (lang == 2) {

	                            contactCat =
	                                    dto.getContactCategoryNameGujarati() != null
	                                            ? dto.getContactCategoryNameGujarati()
	                                            : "";

	                        } else {

	                            contactCat =
	                                    dto.getContactCategoryName() != null
	                                            ? dto.getContactCategoryName()
	                                            : "";
	                        }

	                        BigDecimal price =
	                                dto.getPrice() != null
	                                        ? dto.getPrice()
	                                        : BigDecimal.ZERO;

	                        BigDecimal totalprice =
	                                dto.getTotalPrice() != null
	                                        ? dto.getTotalPrice()
	                                        : BigDecimal.ZERO;

	                        grandTotal = grandTotal.add(totalprice);

	                        Paragraph data = new Paragraph()
	                                .add(" - ")
	                                .add(contactCat)
	                                .add(" (")
	                                .add(dto.getLaborTime())
	                                .add(")")
	                                .add(" (")
	                                .add(dto.getLaborShift() != null
	                                        ? dto.getLaborShift().toUpperCase()
	                                        : "")
	                                .add(")")
	                                .setFont(basicFont)
	                                .setFontSize(12);

	                        laborDetail.addCell(
	                                new Cell()
	                                        .add(new Paragraph(dto.getQty().toString())
	                                                .setFont(basicFont)
	                                                .setFontSize(12))
	                                        .setUnderline()
	                                        .setPaddingLeft(10f)
	                                        .setBorder(Border.NO_BORDER)
	                                        .setTextAlignment(TextAlignment.CENTER));

	                        laborDetail.addCell(
	                                new Cell()
	                                        .add(data)
	                                        .setBorder(Border.NO_BORDER));

	                        if (withPrice == 1) {

	                            laborDetail.addCell(
	                                    new Cell()
	                                            .add(new Paragraph(price.toString())
	                                                    .setFont(basicFont)
	                                                    .setFontSize(12))
	                                            .setBorder(Border.NO_BORDER)
	                                            .setTextAlignment(TextAlignment.CENTER));

	                            laborDetail.addCell(
	                                    new Cell()
	                                            .add(new Paragraph(totalprice.toString())
	                                                    .setFont(basicFont)
	                                                    .setFontSize(12))
	                                            .setBorder(Border.NO_BORDER)
	                                            .setTextAlignment(TextAlignment.CENTER));
	                        }
	                    }

	                    // ---------- Venue Grand Total ----------
	                    if (withPrice == 1) {

	                        laborDetail.addCell(
	                                new Cell(1, 3)
	                                        .add(new Paragraph(grandTotalLabel)
	                                                .setFont(boldFont)
	                                                .setFontSize(12))
	                                        .setBorder(Border.NO_BORDER)
	                                        .setTextAlignment(TextAlignment.RIGHT));

	                        laborDetail.addCell(
	                                new Cell()
	                                        .add(new Paragraph(grandTotal.toString())
	                                                .setFont(boldFont)
	                                                .setFontSize(12))
	                                        .setBorder(Border.NO_BORDER)
	                                        .setTextAlignment(TextAlignment.CENTER));
	                    }

	                    document.add(laborDetail);
	                }

	                boolean isLastLabor = (i == laborEntries.size() - 1);
	                boolean isLastDate = (j == dateEntries.size() - 1);

	                if (!(isLastLabor && isLastDate)) {
	                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	                }
	            }
	        }

	        // ---------- Total Page Count ----------
	        PdfFormXObject totalPagesPlaceholder =
	                pageHandler.getTotalPagesPlaceholder();

	        PdfCanvas pdfCanvas =
	                new PdfCanvas(totalPagesPlaceholder, pdfDocument);

	        Canvas canvas =
	                new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

	        canvas.showTextAligned(
	                String.valueOf(pdfDocument.getNumberOfPages()),
	                5,
	                5,
	                TextAlignment.LEFT);

	        canvas.close();

	        document.close();

	        String fullUrl =
	                environment.getProperty("ws_image_path")
	                        + "/api/download/pdf/labour/"
	                        + getReportName(
	                                fileName,
	                                formatDate(event.getEventStartDateTime()),
	                                "labor chitthi")
	                        + ".pdf";

	        return fullUrl;

	    } catch (Exception e) {

	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate labor chithhi report.");
	    }
	}
	
	// ---------- Helper Methods for Table Cells ----------
	private Cell createLabelCell(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(15).setMargin(0).setMultipliedLeading(1))
				.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(15).setBorder(Border.NO_BORDER);
	}

	private Cell createSeparatorCell(PdfFont font) {
		return new Cell().add(new Paragraph(":").setFont(font).setFontSize(15).setMargin(0).setMultipliedLeading(1))
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
	}

	private Cell createDetailCell(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(15).setMargin(0).setMultipliedLeading(1))
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
	}
	
	// ---------- Helper Methods for Table Cells ----------
	private Cell createLabelCellForA5(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(12).setMargin(0).setMultipliedLeading(1))
				.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(12).setBorder(Border.NO_BORDER);
	}

	private Cell createSeparatorCellForA5(PdfFont font) {
		return new Cell().add(new Paragraph(":").setFont(font).setFontSize(12).setMargin(0).setMultipliedLeading(1))
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
	}

	private Cell createDetailCellForA5(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(12).setMargin(0).setMultipliedLeading(1))
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
	}

//	@Override
//	public String generateLaborChithhiPageSizeA6(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
//			Long userid, Integer isUserDetails, List<Long> agencyId) {
//		
//		try {
//			PdfFont basicFont = null;
//			PdfFont boldFont = null;
//			menuPreparationServiceImpl.loadLicense();
//
//			String agencyName  = "Agency";
//			String dateLabel  = "Date";
//			String venueName   = "Venue";
//			String details = "Details";
//
//			if (lang == 1) {
//				// Hindi
//				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
//				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
//				System.out.println("Hindi font loaded successfully");
//				agencyName  = "एजेंसी";
//				dateLabel    = "तारीख";
//				venueName   = "स्थान";
//				details = "विवरण";
//			} else if (lang == 2) {
//				// Gujarati
//				System.out.println("Loading Gujarati font...");
//
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");		
//				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
//				System.out.println("Gujarati font loaded successfully");
//				agencyName  = "એજન્સી";
//				dateLabel  = "તારીખ";
//				venueName   = "સ્થળ";
//				details = "વિગતો";
//				
//			} else {
//				// English
//				System.out.println("Loading English font...");
////				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
//				System.out.println("English font loaded successfully");
//			}
//			
//			List<GetEventLaborResponseDto> dtoList = eventLaborServiceImpl.fetchEventLaborData(eventId, eventFunctionId, re, lang, userid, agencyId);
//			
//			Date now = new Date();
//			String rootPath = re.getSession().getServletContext().getRealPath("/");
//			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
//			File outputPath = new File(rootPath + "resources/tempDownload/labour");
//			
//			if (!outputPath.exists()) {
//				if (outputPath.mkdirs()) {
//					System.out.println("Directory Created!!!");
//				} else {
//					System.out.println("Error!!!");
//				}
//			}
//			
//			File pdfFile = new File(outputPath + "/laborchithhi_" + datetimeforfile + ".pdf");
//		
//			PdfWriter writer = new PdfWriter(pdfFile);
//			PdfDocument pdfDocument = new PdfDocument(writer);
//			pdfDocument.setDefaultPageSize(PageSize.A6);
//			Document document = new Document(pdfDocument);
//			document.setMargins(20,  10,  20,  10);
//			
//			for (int i = 0; i < dtoList.size(); i++) {
//
//			    GetEventLaborResponseDto dto = dtoList.get(i);
//				
//				String companyName = dto.getCompanyName() == null || dto.getCompanyName().isEmpty() ? "" : dto.getCompanyName();
//				String companyMobile = dto.getCompanyMobile() == null || dto.getCompanyMobile().isEmpty() ? "" : dto.getCompanyMobile();
//				String countryCode = dto.getCountryCode() == null || dto.getCountryCode().isEmpty() ? "" : dto.getCountryCode();
//				String companyEmail = dto.getCompanyEmail() == null || dto.getCompanyEmail().isEmpty() ? "" : dto.getCompanyEmail();
//				String agency = dto.getLaborName() == null || dto.getLaborName().isEmpty() ? "" : dto.getLaborName();
//				String date = dto.getLaborDate() == null || dto.getLaborDate().isEmpty() ? "" : dto.getLaborDate();
//				String venue = dto.getVenue() == null || dto.getVenue().isEmpty() ? "" : dto.getVenue();
//				String companuyLogo = dto.getLogo() == null || dto.getLogo().isEmpty() ? "" : dto.getLogo();
//				String functionVenue = dto.getFunctionVenue() == null || dto.getFunctionVenue().isEmpty() ? "" : dto.getFunctionVenue();
//				String functionDate = dto.getFunctionStartDate() == null || dto.getFunctionStartDate().isEmpty() ? "" : dto.getFunctionStartDate();
//				String functionTime = dto.getFunctionStartTime() == null || dto.getFunctionStartTime().isEmpty() ? "" : dto.getFunctionStartTime();
//				String laborDate = dto.getLaborDate() == null || dto.getLaborDate().isEmpty() ? "" : dto.getLaborDate();
//				Long partyId = dto.getPartyId();
//				Long efId = dto.getEventFunctionId();
//				
//				ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
//				Image img = new Image(logoData);
//				
////				Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url")+companuyLogo));
//				img.setWidth(UnitValue.createPercentValue(100));
//				img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//				
//				float[] columnWidth = {30f, 70f};
//				Table cmpHead = new Table(UnitValue.createPercentArray(columnWidth));
//				cmpHead.setWidth(UnitValue.createPercentValue(80));
//				cmpHead.setHorizontalAlignment(HorizontalAlignment.CENTER);
////				cmpHead.setMarginTop(100f);
//				
//				Cell logo = new Cell()
//							.add(img)
//							.setTextAlignment(TextAlignment.CENTER)
//							.setBorder(Border.NO_BORDER);
//				
//				Paragraph cmpInfo = new Paragraph()
//						.add(new Text(companyName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
//						.add(new Text("\nMobile No : ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(8))
//						.add(new Text(countryCode + " " + companyMobile).setFont(basicFont).setFontSize(8))
//						.add(new Text("\nEmail : ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(8))
//						.add(new Text(companyEmail).setFont(basicFont).setFontSize(8));
//				
//				Cell info = new Cell()
//						.add(cmpInfo)
//						.setPaddingLeft(5f)
//						.setVerticalAlignment(VerticalAlignment.MIDDLE)
//						.setBorder(Border.NO_BORDER);
//				
//				cmpHead.addCell(logo);
//				cmpHead.addCell(info);
//				
//				if(isUserDetails == 1) {					
//					document.add(cmpHead);
//				}
//				
//				document.add(new Paragraph("\n"));
//
//				float[] columnWidth2 = {18f, 3f, 79f};
//				Table laborTable = new Table(UnitValue.createPercentArray(columnWidth2));
//				laborTable.setWidth(UnitValue.createPercentValue(80));
//				laborTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
//				laborTable.setMarginTop(-20f);
//				
//				Cell label = new Cell()
//						.add(new Paragraph(agencyName).setFont(boldFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5)
//						.setBorder(Border.NO_BORDER);
//				
//				Cell seperator = new Cell()
//						.add(new Paragraph(":").setFont(basicFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				Cell detail = new Cell()
//						.add(new Paragraph(agency).setFont(basicFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				laborTable.addCell(label);
//				laborTable.addCell(seperator);
//				laborTable.addCell(detail);
//				
//				label = new Cell()
//						.add(new Paragraph(dateLabel).setFont(boldFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5)
//						.setBorder(Border.NO_BORDER);
//				
//				seperator = new Cell()
//						.add(new Paragraph(":").setFont(basicFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				detail = new Cell()
//						.add(new Paragraph(laborDate).setFont(basicFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				laborTable.addCell(label);
//				laborTable.addCell(seperator);
//				laborTable.addCell(detail);
//				
//				label = new Cell()
//						.add(new Paragraph(venueName).setFont(boldFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5)
//						.setBorder(Border.NO_BORDER);
//				
//				seperator = new Cell()
//						.add(new Paragraph(":").setFont(basicFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				detail = new Cell()
//						.add(new Paragraph(functionVenue).setFont(basicFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT)
//						.setBorder(Border.NO_BORDER);
//				
//				laborTable.addCell(label);
//				laborTable.addCell(seperator);
//				laborTable.addCell(detail);
//				
//				document.add(laborTable);
//				
//				float[] columnWidth3 = {100f};
//				Table detailsTab = new Table(UnitValue.createPercentArray(columnWidth3));
//				detailsTab.setWidth(UnitValue.createPercentValue(80));
//				detailsTab.setHorizontalAlignment(HorizontalAlignment.CENTER);
//				
//				Cell detailsHeader = new Cell()
//						.add(new Paragraph(details).setFont(boldFont).setFontSize(11).setMargin(0).setMultipliedLeading(1))
//						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5)
//						.setBackgroundColor(new DeviceRgb(240, 240, 240))
//						.setBorder(Border.NO_BORDER);
//
//				detailsTab.addCell(detailsHeader);
//				
//				document.add(detailsTab);
//				
//				List<GetEventLaborResponseDto> dtoDetailList = eventLaborServiceImpl.fetchEventLaborDataDetail(eventId, efId, re, lang, userid, partyId);
//				
//				float[] columnWidth4 = {100f};
//				Table laborDetail = new Table(columnWidth4);
//				laborDetail.setWidth(UnitValue.createPercentValue(80));
//				laborDetail.setHorizontalAlignment(HorizontalAlignment.CENTER);
//				for (GetEventLaborResponseDto dtoDetail : dtoDetailList) {
//					Integer qty = dtoDetail.getQty();
//					String contactCategoryName = dtoDetail.getContactCategoryName() == null || dtoDetail.getContactCategoryName().isEmpty() ? "" : dtoDetail.getContactCategoryName();
//					String time = dtoDetail.getFunctionStartTime() == null || dtoDetail.getFunctionStartTime().isEmpty() ? "" : dtoDetail.getFunctionStartTime();
//					 
//					Paragraph data = new Paragraph()
//							.add(new Text(qty.toString()).setUnderline())
//							.add(new Text(" - "))
//							.add(new Text(contactCategoryName))
//							.add(new Text(" ("))
//							.add(new Text(time))
//							.add(new Text(") "))
//							.setFont(basicFont)
//							.setFontSize(10)
//							.setMargin(0).setMultipliedLeading(1)
//							.setPaddingLeft(5f);
//					
//					Cell laborData = new Cell()
//							.add(data)
//							.setTextAlignment(TextAlignment.LEFT)
//							.setBorder(Border.NO_BORDER);
//					
//					laborDetail.addCell(laborData);
//				}
//						
//				document.add(laborDetail);
//				
//				if (i < dtoList.size() - 1) {
//			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//			    }
//				
//			}
//			
//			document.close();
//			
//			String scheme = re.getScheme();
//			String serverName = re.getServerName();
//			int serverPort = re.getServerPort();
//			String contextPath = re.getContextPath();
//
//			String fullUrl;
//			if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
//				fullUrl = "https://" + serverName + contextPath + "/api/download/pdf/labour" 
//						+ "/laborchithhi_" + datetimeforfile + ".pdf";
//			} else {
//				fullUrl = "https://" + serverName + ":" + serverPort + contextPath + "/api/download/pdf/labour"
//						+ "/laborchithhi_" + datetimeforfile + ".pdf";
//			}
//			
//			return fullUrl;
//			
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Fails to generate labor chithhi report.");
//		}
//				
//	}

		@Override
		public String generateLaborChithhiPageSizeA6(Long eventId, Long eventFunctionId,
				HttpServletRequest re, int lang, Long userid,
				Integer isUserDetails, List<Long> agencyId,
				Integer withPrice) {
	
			try {
	
				// ---------- Load Fonts ----------
				PdfFont basicFont = null;
				PdfFont boldFont = null;
	
				menuPreparationServiceImpl.loadLicense();
	
				String agencyLabel = "Agency";
				String dateLabel = "Date";
				String venueLabel = "Venue";
				String detailsLabel = "Details";
				String mobileLabel = "Mobile No : ";
				String emailLabel = "Email : ";
				String priceLabel = "Price";
				String totalpriceLabel = "Total";
				String qtyLable = "Qty";
				String functionLabel = "Event";
	
				if (lang == 1) {
	
					basicFont =
							menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	
					boldFont =
							menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
	
					agencyLabel = "एजेंसी";
					dateLabel = "तारीख";
					venueLabel = "स्थान";
					detailsLabel = "विवरण";
					mobileLabel = "मोबाइल नंबर : ";
					emailLabel = "ईमेल : ";
					qtyLable = "मात्रा";
					priceLabel = "मूल्य";
					totalpriceLabel = "कुल मूल्य";
					functionLabel = "कार्यक्रम";
	
				} else if (lang == 2) {
	
					basicFont =
							menuPreparationServiceImpl.loadFont(
									"/fonts/NotoSansGujarati-Regular.ttf");
	
					boldFont =
							menuPreparationServiceImpl.loadFont(
									"/fonts/NotoSansGujarati-Bold.ttf");
	
					agencyLabel = "એજન્સી";
					dateLabel = "તારીખ";
					venueLabel = "સ્થળ";
					detailsLabel = "વિગતો";
					mobileLabel = "મોબાઈલ નંબર : ";
					emailLabel = "ઈમેલ : ";
					qtyLable = "માત્રા";
					priceLabel = "કિંમત";
					totalpriceLabel = "કુલ કિંમત";
					functionLabel = "કાર્યક્રમ";
	
				} else {
	
					basicFont =
							PdfFontFactory.createFont(
									StandardFonts.HELVETICA);
	
					boldFont =
							PdfFontFactory.createFont(
									StandardFonts.HELVETICA_BOLD);
				}
	
				EventMasterEntity event =
						eventMasterRepository.findById(eventId)
								.orElseThrow(() -> new RuntimeException(
										"Event not found with id : "
												+ eventId));
	
				// ---------- Fetch Data ----------
				List<GetEventLaborResponseDto> dtoList =
						eventLaborServiceImpl.fetchEventLaborData(
								eventId,
								eventFunctionId,
								re,
								lang,
								userid,
								agencyId);
	
				// ---------- Prepare File ----------
				Date now = new Date();
	
				String rootPath =
						re.getSession()
								.getServletContext()
								.getRealPath("/");
	
				File outputPath =
						new File(
								rootPath
										+ "resources/tempDownload/labour");
	
				if (!outputPath.exists()) {
	
					outputPath.mkdirs();
				}
	
				String fileName =
						getPartyNameByEventId(eventId);
	
				if (agencyId.size() == 1) {
	
					PartyMasterEntity party =
							partyMasterRepository
									.findByIdAndIsDeleteFalse(
											agencyId.get(0))
									.orElse(null);
	
					if (party != null) {
	
						fileName = party.getNameEnglish();
					}
				}
	
				File pdfFile =
						new File(
								outputPath
										+ "/"
										+ getReportName(
												fileName,
												formatDate(
														event.getEventStartDateTime()),
												"labor chitthi")
										+ ".pdf");
	
				PdfWriter writer =
						new PdfWriter(pdfFile);
	
				PdfDocument pdfDocument =
						new PdfDocument(writer);
	
				PageNumberHandler pageHandler =
						new PageNumberHandler();
	
				pdfDocument.addEventHandler(
						PdfDocumentEvent.END_PAGE,
						pageHandler);
	
				pdfDocument.setDefaultPageSize(PageSize.A6);
	
				Document document =
						new Document(pdfDocument);
	
				document.setMargins(20, 10, 70, 10);
	
				// ---------- Group Data ----------
				Map<String, Map<String, Map<String,
						List<GetEventLaborResponseDto>>>> groupedData =
								dtoList.stream()
										.collect(Collectors.groupingBy(
												GetEventLaborResponseDto::getLaborName,
												LinkedHashMap::new,
												Collectors.groupingBy(
														GetEventLaborResponseDto::getLaborDate,
														LinkedHashMap::new,
														Collectors.groupingBy(
																dto -> dto.getFunctionVenue() != null
																		&& !dto.getFunctionVenue()
																				.trim()
																				.isEmpty()
																						? dto.getFunctionVenue()
																						: "-",
																LinkedHashMap::new,
																Collectors.toList()))));
	
				List<Map.Entry<String,
						Map<String,
								Map<String,
										List<GetEventLaborResponseDto>>>>>
												laborEntries =
														new ArrayList<>(
																groupedData.entrySet());
	
				for (int i = 0;
						i < laborEntries.size();
						i++) {
	
					Map.Entry<String,
							Map<String,
									Map<String,
											List<GetEventLaborResponseDto>>>>
													laborEntry =
															laborEntries.get(i);
	
					String laborName =
							laborEntry.getKey();
	
					Map<String,
							Map<String,
									List<GetEventLaborResponseDto>>> dateMap =
											laborEntry.getValue();
	
					List<Map.Entry<String,
							Map<String,
									List<GetEventLaborResponseDto>>>> dateEntries =
											new ArrayList<>(
													dateMap.entrySet());
	
					for (int j = 0;
							j < dateEntries.size();
							j++) {
	
						Map.Entry<String,
								Map<String,
										List<GetEventLaborResponseDto>>> dateEntry =
												dateEntries.get(j);
	
						String laborDate =
								dateEntry.getKey();
	
						Map<String,
								List<GetEventLaborResponseDto>> venueMap =
										dateEntry.getValue();
	
						List<GetEventLaborResponseDto> firstRecords =
								venueMap.values()
										.iterator()
										.next();
	
						GetEventLaborResponseDto dto1 =
								firstRecords.get(0);
	
						// ---------- Company Values ----------
						String companyName =
								dto1.getCompanyName() != null
										? dto1.getCompanyName()
										: "";
	
						String companyMobile =
								dto1.getCompanyMobile() != null
										? dto1.getCompanyMobile()
										: "";
	
						String countryCode =
								dto1.getCountryCode() != null
										? dto1.getCountryCode()
										: "";
	
						String companyEmail =
								dto1.getCompanyEmail() != null
										? dto1.getCompanyEmail()
										: "";
	
						String eventName = "";
	
						if (lang == 1) {
	
							eventName =
									dto1.getEventNameHindi() != null
											? dto1.getEventNameHindi()
											: "";
	
						} else if (lang == 2) {
	
							eventName =
									dto1.getEventNameGujarati() != null
											? dto1.getEventNameGujarati()
											: "";
	
						} else {
	
							eventName =
									dto1.getEventNameEnglish() != null
											? dto1.getEventNameEnglish()
											: "";
						}
	
						// ---------- Logo ----------
						ImageData logoData =
								menuPreparationServiceImpl
										.loadImageFromResource(
												environment.getProperty(
														"app.image.url")
														+ dto1.getLogo());
	
						Image img =
								new Image(logoData)
										.setWidth(
												UnitValue
														.createPercentValue(
																100))
										.setHorizontalAlignment(
												HorizontalAlignment.CENTER);
	
						// ---------- Header ----------
						float[] columnWidth = { 30f, 70f };
	
						Table cmpHead =
								new Table(
										UnitValue.createPercentArray(
												columnWidth))
														.setWidth(
																UnitValue
																		.createPercentValue(
																				80))
														.setHorizontalAlignment(
																HorizontalAlignment.CENTER);
	
						Cell logoCell =
								new Cell()
										.add(img)
										.setBorder(Border.NO_BORDER);
	
						Paragraph cmpInfo =
								new Paragraph()
										.add(new Text(
												companyName + "\n")
														.setFont(
																basicFont)
														.setFontSize(
																12)
														.simulateBold())
										.add(new Text(mobileLabel)
												.setFont(boldFont)
												.setFontSize(8))
										.add(new Text(
												countryCode
														+ " "
														+ companyMobile
														+ "\n")
																.setFont(
																		basicFont)
																.setFontSize(
																		8))
										.add(new Text(emailLabel)
												.setFont(boldFont)
												.setFontSize(8))
										.add(new Text(companyEmail)
												.setFont(basicFont)
												.setFontSize(8));
	
						Cell infoCell =
								new Cell()
										.add(cmpInfo)
										.setPaddingLeft(10f)
										.setBorder(Border.NO_BORDER);
	
						cmpHead.addCell(logoCell);
						cmpHead.addCell(infoCell);
	
						if (isUserDetails != null
								&& isUserDetails == 1) {
	
							document.add(cmpHead);
						}
	
						// ---------- Main Header ----------
						Table laborTable =
								new Table(UnitValue.createPercentArray(
										new float[] { 18f, 3f, 79f }))
												.setWidth(
														UnitValue
																.createPercentValue(
																		80));
	
						Cell cell =
								new Cell()
										.add(new Paragraph(
												agencyLabel)
														.setFont(
																boldFont)
														.setFontSize(
																11))
										.setBorder(
												Border.NO_BORDER);
	
						laborTable.addCell(cell);
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(":")
												.setFont(
														basicFont))
										.setBorder(
												Border.NO_BORDER));
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(
												laborName)
														.setFont(
																basicFont)
														.setFontSize(
																11))
										.setBorder(
												Border.NO_BORDER));
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(
												dateLabel)
														.setFont(
																boldFont)
														.setFontSize(
																11))
										.setBorder(
												Border.NO_BORDER));
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(":")
												.setFont(
														basicFont))
										.setBorder(
												Border.NO_BORDER));
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(
												laborDate)
														.setFont(
																basicFont)
														.setFontSize(
																11))
										.setBorder(
												Border.NO_BORDER));
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(
												functionLabel)
														.setFont(
																boldFont)
														.setFontSize(
																11))
										.setBorder(
												Border.NO_BORDER));
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(":")
												.setFont(
														basicFont))
										.setBorder(
												Border.NO_BORDER));
	
						laborTable.addCell(
								new Cell()
										.add(new Paragraph(
												eventName)
														.setFont(
																basicFont)
														.setFontSize(
																11))
										.setBorder(
												Border.NO_BORDER));
	
						document.add(laborTable);
	
						// ---------- Venue Wise ----------
						for (Map.Entry<String,
								List<GetEventLaborResponseDto>> venueEntry
										: venueMap.entrySet()) {
	
							String venueName =
									venueEntry.getKey();
	
							List<GetEventLaborResponseDto> records =
									venueEntry.getValue();
	
							Paragraph venuePara =
									new Paragraph()
											.add(new Text(
													venueLabel
															+ " : ")
																	.setFont(
																			boldFont))
											.add(new Text(
													venueName)
															.setFont(
																	basicFont))
											.setFontSize(10)
											.setMarginTop(8f)
											.setMarginBottom(5f);
	
							document.add(venuePara);
	
							// ---------- Header ----------
							Table detailsTab;
	
							if (withPrice == 1) {
	
								detailsTab =
										new Table(
												UnitValue.createPercentArray(
														new float[] {
																10f,
																60f,
																10f,
																20f }));
	
							} else {
	
								detailsTab =
										new Table(
												UnitValue.createPercentArray(
														new float[] {
																10f,
																90f }));
							}
	
							detailsTab.setWidth(
									UnitValue.createPercentValue(
											80));
	
							detailsTab.addCell(
									new Cell()
											.add(new Paragraph(
													qtyLable)
															.setFont(
																	boldFont)
															.setFontSize(
																	8))
											.setBackgroundColor(
													new DeviceRgb(
															240,
															240,
															240)));
	
							detailsTab.addCell(
									new Cell()
											.add(new Paragraph(
													detailsLabel)
															.setFont(
																	boldFont)
															.setFontSize(
																	8))
											.setBackgroundColor(
													new DeviceRgb(
															240,
															240,
															240)));
	
							if (withPrice == 1) {
	
								detailsTab.addCell(
										new Cell()
												.add(new Paragraph(
														priceLabel)
																.setFont(
																		boldFont)
																.setFontSize(
																		8))
												.setBackgroundColor(
														new DeviceRgb(
																240,
																240,
																240)));
	
								detailsTab.addCell(
										new Cell()
												.add(new Paragraph(
														totalpriceLabel)
																.setFont(
																		boldFont)
																.setFontSize(
																		8))
												.setBackgroundColor(
														new DeviceRgb(
																240,
																240,
																240)));
							}
	
							document.add(detailsTab);
	
							// ---------- Data ----------
							Table laborDetail;
	
							if (withPrice == 1) {
	
								laborDetail =
										new Table(
												UnitValue.createPercentArray(
														new float[] {
																10f,
																60f,
																10f,
																20f }));
	
							} else {
	
								laborDetail =
										new Table(
												UnitValue.createPercentArray(
														new float[] {
																10f,
																90f }));
							}
	
							laborDetail.setWidth(
									UnitValue.createPercentValue(
											80));
	
							for (GetEventLaborResponseDto dto
									: records) {
	
								String contactCat = "";
	
								if (lang == 1) {
	
									contactCat =
											dto.getContactCategoryNameHindi() != null
													? dto.getContactCategoryNameHindi()
													: "";
	
								} else if (lang == 2) {
	
									contactCat =
											dto.getContactCategoryNameGujarati() != null
													? dto.getContactCategoryNameGujarati()
													: "";
	
								} else {
	
									contactCat =
											dto.getContactCategoryName() != null
													? dto.getContactCategoryName()
													: "";
								}
	
								Paragraph data =
										new Paragraph()
												.add(" - ")
												.add(contactCat)
												.add(" (")
												.add(dto.getLaborTime())
												.add(")")
												.add(" (")
												.add(dto.getLaborShift() != null
														? dto.getLaborShift()
																.toUpperCase()
														: "")
												.add(")")
												.setFont(basicFont)
												.setFontSize(8);
	
								laborDetail.addCell(
										new Cell()
												.add(new Paragraph(
														dto.getQty()
																.toString())
																		.setFont(
																				basicFont)
																		.setFontSize(
																				8))
												.setTextAlignment(
														TextAlignment.CENTER)
												.setBorder(
														Border.NO_BORDER));
	
								laborDetail.addCell(
										new Cell()
												.add(data)
												.setBorder(
														Border.NO_BORDER));
	
								if (withPrice == 1) {
	
									BigDecimal price =
											dto.getPrice() != null
													? dto.getPrice()
													: BigDecimal.ZERO;
	
									BigDecimal totalprice =
											dto.getTotalPrice() != null
													? dto.getTotalPrice()
													: BigDecimal.ZERO;
	
									laborDetail.addCell(
											new Cell()
													.add(new Paragraph(
															price.toString())
																	.setFont(
																			basicFont)
																	.setFontSize(
																			8))
													.setTextAlignment(
															TextAlignment.CENTER)
													.setBorder(
															Border.NO_BORDER));
	
									laborDetail.addCell(
											new Cell()
													.add(new Paragraph(
															totalprice.toString())
																	.setFont(
																			basicFont)
																	.setFontSize(
																			8))
													.setTextAlignment(
															TextAlignment.CENTER)
													.setBorder(
															Border.NO_BORDER));
								}
							}
	
							document.add(laborDetail);
						}
	
						boolean isLastLabor =
								(i == laborEntries.size() - 1);
	
						boolean isLastDate =
								(j == dateEntries.size() - 1);
	
						if (!(isLastLabor && isLastDate)) {
	
							document.add(
									new AreaBreak(
											AreaBreakType.NEXT_PAGE));
						}
					}
				}
	
				// ---------- Total Page ----------
				PdfFormXObject totalPagesPlaceholder =
						pageHandler.getTotalPagesPlaceholder();
	
				PdfCanvas pdfCanvas =
						new PdfCanvas(
								totalPagesPlaceholder,
								pdfDocument);
	
				Canvas canvas =
						new Canvas(
								pdfCanvas,
								new Rectangle(0, 0, 50, 30));
	
				canvas.showTextAligned(
						String.valueOf(
								pdfDocument.getNumberOfPages()),
						5,
						5,
						TextAlignment.LEFT);
	
				canvas.close();
	
				document.close();
	
				String fullUrl =
						environment.getProperty("ws_image_path")
								+ "/api/download/pdf/labour/"
								+ getReportName(
										fileName,
										formatDate(
												event.getEventStartDateTime()),
										"labor chitthi")
								+ ".pdf";
	
				return fullUrl;
	
			} catch (Exception e) {
	
				e.printStackTrace();
	
				throw new RuntimeException(
						"Failed to generate labor chithhi report.");
			}
		}

	@Override
	public String laborReportDateWiseWithoutPrice(HttpServletRequest re, int lang, Long userid, String startDate,
			String endDate, Integer isCompanyDetails, List<Long> agencyId, Long partyId) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String labour = "Labour";
			String dateTimeLabel = "Date & Time";
			String shiftName = "Shift";
			String persons = "Persons";
			String note = "Note";
			String customerName = "Customer Name";
			String function = "Function";
			String venueName = "Venue";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				labour = "श्रमिक";
				dateTimeLabel = "दिनांक एवं समय";
				shiftName = "कार्य शिफ्ट";
				persons = "व्यक्ति";
				note = "टिप्पणी";
				customerName = "ग्राहक का नाम";
				function = "कार्यक्रम";
				venueName = "स्थल";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				labour = "મજૂર";
				dateTimeLabel = "તારીખ અને સમય";
				shiftName = "કાર્ય શિફ્ટ";
				persons = "વ્યક્તિઓ";
				note = "નોંધ";
				customerName = "ગ્રાહકનું નામ";
				function = "કાર્યક્રમ";
				venueName = "સ્થળ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			GetEventLaborResponseDto cmpData = new GetEventLaborResponseDto();
			String eventNo = "";
			cmpData = eventLaborServiceImpl.getCmpData(userid, lang);
			eventNo = startDate.replace("/", "_");
			Long uId = cmpData.getUserId();
			String cmpName = cmpData.getCompanyName() == null || cmpData.getCompanyName().isEmpty() ? ""
					: cmpData.getCompanyName();
			String cmpEmail = cmpData.getCompanyEmail() == null || cmpData.getCompanyEmail().isEmpty() ? ""
					: cmpData.getCompanyEmail();
			String cmpCountryCode = cmpData.getCountryCode() == null || cmpData.getCountryCode().isEmpty() ? ""
					: cmpData.getCountryCode();
			String cmpMobile = cmpData.getCompanyMobile() == null || cmpData.getCompanyMobile().isEmpty() ? ""
					: cmpData.getCompanyMobile();
			String logo = cmpData.getLogo() == null || cmpData.getLogo().isEmpty() ? "" : cmpData.getLogo();

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

			File pdfFile = new File(outputPath + "/LabourAgency_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 50, 20);

			float[] columnWidthHead = { 25f, 15f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setMarginBottom(10f);

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
//			Image img = new Image(logoData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(120);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setPadding(0).setPaddingLeft(10f)
					.setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setPadding(0).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Mobile No.").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text(cmpCountryCode + " " + cmpMobile)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				document.add(cmpHead);
			}

			Paragraph heading = new Paragraph().add(new Text("Labour Agencywise Report")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(0f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			// Get the data
			List<GetEventLaborResponseDto> labourData = eventLaborServiceImpl.getEventLabourData(startDate, endDate,
					lang, userid, agencyId, partyId);

			// Group the data
			Map<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> groupedData = groupLaborDataForPDF(
					labourData);

			Div div = null;
			for (Map.Entry<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> agencyEntry : groupedData
					.entrySet()) {
				div = new Div();
				String agencyName = agencyEntry.getKey();

				// Agency Name Header
				Paragraph agencyPara = new Paragraph()
						.add(new Text(agencyName.toUpperCase()).setFont(basicFont).setFontColor(ColorConstants.WHITE)
								.setFontSize(16))
						.setBorder(new SolidBorder(1f)).setBackgroundColor(new DeviceRgb(51, 51, 51))
						.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER).setMargin(0)
						.setVerticalAlignment(VerticalAlignment.MIDDLE);
				div.add(agencyPara);

				// Iterate through dates
				for (Map.Entry<String, Map<String, List<GetEventLaborResponseDto>>> dateEntry : agencyEntry.getValue()
						.entrySet()) {

					String date = dateEntry.getKey() != null ? dateEntry.getKey() : "";
					Paragraph datePara = new Paragraph().add(new Text(date).setFont(boldFont))
							.setBorder(new SolidBorder(1f)).setBackgroundColor(new DeviceRgb(204, 204, 204))
							.setPaddingLeft(5f).setMargin(0).setWidth(UnitValue.createPercentValue(100));

					// Date Header
					div.add(datePara);

					// Iterate through venues
					for (Map.Entry<String, List<GetEventLaborResponseDto>> venueEntry : dateEntry.getValue()
							.entrySet()) {

						String venue = venueEntry.getKey() != null ? venueEntry.getKey() : "";
						Paragraph venuePara = new Paragraph().add(new Text(venue).setFont(boldFont))
								.setBorder(new SolidBorder(1f)).setBackgroundColor(new DeviceRgb(230, 230, 230))
								.setPaddingLeft(5f).setMargin(0).setWidth(UnitValue.createPercentValue(97))
								.setHorizontalAlignment(HorizontalAlignment.RIGHT);

						List<GetEventLaborResponseDto> laborEntries = venueEntry.getValue();

						// Venue/Address
						div.add(venuePara);

						// Create table for labor details
						float[] cw = { 3f, 2f, 1.5f, 1.5f, 2f };
						Table table = new Table(UnitValue.createPercentArray(cw)); // Category, Vardhi No., Time, Qty,
																					// Rate, Total
						table.setWidth(UnitValue.createPercentValue(96));
						table.setHorizontalAlignment(HorizontalAlignment.RIGHT);
						table.setFixedLayout();

						// Table headers
						table.addCell("Category Name").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Shift").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Time").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Quantity").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Notes").setTextAlignment(TextAlignment.CENTER);

						// Add labor entries
						for (GetEventLaborResponseDto labor : laborEntries) {
							table.addCell(new Cell().add(new Paragraph(labor.getContactCategoryName() != null ? labor.getContactCategoryName() : "").setFont(basicFont)));
							table.addCell(new Cell().add(new Paragraph(labor.getLaborShift() != null ? labor.getLaborShift() : "").setFont(basicFont))).setTextAlignment(TextAlignment.CENTER);
							table.addCell(labor.getLaborTime() != null ? labor.getLaborTime() : "")
									.setTextAlignment(TextAlignment.CENTER);
							table.addCell(labor.getQty() != null ? String.valueOf(labor.getQty()) : "0")
									.setTextAlignment(TextAlignment.CENTER);
							table.addCell(new Cell().add(new Paragraph(labor.getNotes() != null ? labor.getNotes() : "").setFont(basicFont)))
							.setTextAlignment(TextAlignment.CENTER);

						}

						div.add(table);
//						document.add(new Paragraph("\n"));
					}
				}
				document.add(div);
				document.add(new Paragraph("\n"));

			}

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 0, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo 
					+ "/LabourAgency_" + datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}

	}

	@Override
	public String laborReportDateWiseWithPrice(HttpServletRequest re, int lang, Long userid, String startDate,
			String endDate, Integer isCompanyDetails, List<Long> agencyId, Long partyId) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String labour = "Labour";
			String dateTimeLabel = "Date & Time";
			String shiftName = "Shift";
			String persons = "Persons";
			String note = "Note";
			String customerName = "Customer Name";
			String function = "Function";
			String venueName = "Venue";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				labour = "श्रमिक";
				dateTimeLabel = "दिनांक एवं समय";
				shiftName = "कार्य शिफ्ट";
				persons = "व्यक्ति";
				note = "टिप्पणी";
				customerName = "ग्राहक का नाम";
				function = "कार्यक्रम";
				venueName = "स्थल";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				labour = "મજૂર";
				dateTimeLabel = "તારીખ અને સમય";
				shiftName = "કાર્ય શિફ્ટ";
				persons = "વ્યક્તિઓ";
				note = "નોંધ";
				customerName = "ગ્રાહકનું નામ";
				function = "કાર્યક્રમ";
				venueName = "સ્થળ";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			GetEventLaborResponseDto cmpData = new GetEventLaborResponseDto();
			String eventNo = "";
			cmpData = eventLaborServiceImpl.getCmpData(userid, lang);
			eventNo = startDate.replace("/", "_");
			Long uId = cmpData.getUserId();
			String cmpName = cmpData.getCompanyName() == null || cmpData.getCompanyName().isEmpty() ? ""
					: cmpData.getCompanyName();
			String cmpEmail = cmpData.getCompanyEmail() == null || cmpData.getCompanyEmail().isEmpty() ? ""
					: cmpData.getCompanyEmail();
			String cmpCountryCode = cmpData.getCountryCode() == null || cmpData.getCountryCode().isEmpty() ? ""
					: cmpData.getCountryCode();
			String cmpMobile = cmpData.getCompanyMobile() == null || cmpData.getCompanyMobile().isEmpty() ? ""
					: cmpData.getCompanyMobile();
			String logo = cmpData.getLogo() == null || cmpData.getLogo().isEmpty() ? "" : cmpData.getLogo();

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
			System.out.println("name : " + cmpData.getPartyName());
			File pdfFile = new File(outputPath + "/LabourAgency_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(20, 20, 50, 20);

			float[] columnWidthHead = { 25f, 15f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setMarginBottom(10f);

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
//			Image img = new Image(logoData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(120);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setPadding(0).setPaddingLeft(10f)
					.setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			cmp = new Cell(1, 3).add(cmpPara).setPadding(0).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Mobile No.").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text(cmpCountryCode + " " + cmpMobile)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				document.add(cmpHead);
			}

			Paragraph heading = new Paragraph().add(new Text("Labour Agencywise Report")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
			heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
			heading.setMarginTop(0f);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setWidth(UnitValue.createPercentValue(100));
			document.add(heading);

			// Get the data
			List<GetEventLaborResponseDto> labourData = eventLaborServiceImpl.getEventLabourData(startDate, endDate,
					lang, userid, agencyId, partyId);

			// Group the data
			Map<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> groupedData = groupLaborDataForPDF(
					labourData);

			BigDecimal grandTotal = BigDecimal.ZERO;

			Div div = null;

			for (Map.Entry<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> agencyEntry : groupedData
					.entrySet()) {

				div = new Div();

				String agencyName = agencyEntry.getKey();
				BigDecimal agencyTotal = BigDecimal.ZERO;

				// Agency Name Header
				Paragraph agencyPara = new Paragraph()
						.add(new Text(agencyName.toUpperCase()).setFont(basicFont).setFontColor(ColorConstants.WHITE)
								.setFontSize(16))
						.setBorder(new SolidBorder(1f)).setBackgroundColor(new DeviceRgb(51, 51, 51))
						.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.CENTER).setMargin(0)
						.setVerticalAlignment(VerticalAlignment.MIDDLE);
				div.add(agencyPara);

				// Iterate through dates
				for (Map.Entry<String, Map<String, List<GetEventLaborResponseDto>>> dateEntry : agencyEntry.getValue()
						.entrySet()) {

					String date = dateEntry.getKey() != null ? dateEntry.getKey() : "";
					Paragraph datePara = new Paragraph().add(new Text(date).setFont(boldFont))
							.setBorder(new SolidBorder(1f)).setBackgroundColor(new DeviceRgb(204, 204, 204))
							.setPaddingLeft(5f).setMargin(0).setWidth(UnitValue.createPercentValue(100));

					// Date Header
					div.add(datePara);

					// Iterate through venues
					for (Map.Entry<String, List<GetEventLaborResponseDto>> venueEntry : dateEntry.getValue()
							.entrySet()) {

						String venue = venueEntry.getKey() != null ? venueEntry.getKey() : "";
						Paragraph venuePara = new Paragraph().add(new Text(venue).setFont(boldFont))
								.setBorder(new SolidBorder(1f)).setBackgroundColor(new DeviceRgb(230, 230, 230))
								.setPaddingLeft(5f).setMargin(0).setWidth(UnitValue.createPercentValue(97))
								.setHorizontalAlignment(HorizontalAlignment.RIGHT);

						List<GetEventLaborResponseDto> laborEntries = venueEntry.getValue();

						// Venue/Address
						div.add(venuePara);

						// Create table for labor details
						float[] cw = { 3f, 1.5f, 1f, 1f, 1f, 1.5f, 1f };
						Table table = new Table(UnitValue.createPercentArray(cw)); // Category, Vardhi No., Time, Qty,
																					// Rate, Total
						table.setWidth(UnitValue.createPercentValue(96));
						table.setHorizontalAlignment(HorizontalAlignment.RIGHT);
						table.setFixedLayout();

						// Table headers
						table.addCell("Category Name").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Shift").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Time").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Quantity").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Rate").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Total").setTextAlignment(TextAlignment.CENTER);
						table.addCell("Notes").setTextAlignment(TextAlignment.CENTER);

						// Add labor entries
						for (GetEventLaborResponseDto labor : laborEntries) {
							table.addCell(new Cell().add(new Paragraph(labor.getContactCategoryName() != null ? labor.getContactCategoryName() : "").setFont(basicFont)));
							table.addCell(new Cell().add(new Paragraph(labor.getLaborShift() != null ? labor.getLaborShift() : "").setFont(basicFont))).setTextAlignment(TextAlignment.CENTER);
							table.addCell(labor.getLaborTime() != null ? labor.getLaborTime() : "")
									.setTextAlignment(TextAlignment.CENTER);
							table.addCell(labor.getQty() != null ? String.valueOf(labor.getQty()) : "0")
									.setTextAlignment(TextAlignment.CENTER);
							table.addCell(labor.getPrice() != null ? labor.getPrice().toString() : "0")
									.setTextAlignment(TextAlignment.CENTER);
							table.addCell(labor.getTotalPrice() != null ? labor.getTotalPrice().toString() : "0")
									.setTextAlignment(TextAlignment.CENTER);
							table.addCell(new Cell().add(new Paragraph(labor.getNotes() != null ? labor.getNotes() : "").setFont(basicFont)))
							.setTextAlignment(TextAlignment.CENTER);

							// Null-safe addition for agencyTotal
							if (labor.getTotalPrice() != null) {
								agencyTotal = agencyTotal.add(labor.getTotalPrice());
							}
						}

						div.add(table);
//						document.add(new Paragraph("\n"));
					}
				}

				// Agency Total
				Paragraph agencyTotalPara = new Paragraph()
						.add(new Text("Total: " + agencyTotal.toString()).setFont(basicFont).setFontSize(14))
						.setMargin(0).setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.RIGHT)
						.setPaddingRight(5f).setBorder(new SolidBorder(1f));
				div.add(agencyTotalPara);
				grandTotal = grandTotal.add(agencyTotal);
				document.add(div);

				document.add(new Paragraph("\n"));
			}

			Paragraph grandTotalPara = new Paragraph()
					.add(new Text("Grand Total: " + grandTotal.toString()).setFont(basicFont).setFontSize(16))
					.setWidth(UnitValue.createPercentValue(100)).setTextAlignment(TextAlignment.RIGHT)
					.setPaddingRight(5f);
			document.add(grandTotalPara);

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 0, 5, TextAlignment.LEFT);

			canvas.close();

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/LabourAgency_" + datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}

	}

	public Map<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> groupLaborDataForPDF(
			List<GetEventLaborResponseDto> labourData) {

		  // Sort labor data by labor date/time ascending
	    Collections.sort(labourData, new Comparator<GetEventLaborResponseDto>() {
	        @Override
	        public int compare(GetEventLaborResponseDto o1, GetEventLaborResponseDto o2) {
	            if (o1.getLaborDateTime() == null && o2.getLaborDateTime() == null) {
	                return 0;
	            }
	            if (o1.getLaborDateTime() == null) {
	                return 1;
	            }
	            if (o2.getLaborDateTime() == null) {
	                return -1;
	            }

	            return o1.getLaborDateTime().compareTo(o2.getLaborDateTime());
	        }
	    });
	    
		// Group by: Agency Name -> Date -> Venue
		Map<String, Map<String, Map<String, List<GetEventLaborResponseDto>>>> groupedData = new LinkedHashMap<>();

		for (GetEventLaborResponseDto dto : labourData) {
			String mobileNo = dto.getMobileNo() != null ? dto.getMobileNo() : "";
			StringBuilder agencyName = new StringBuilder(dto.getPartyName() != null ? dto.getPartyName() : "");
			if (mobileNo != null && mobileNo.trim().length() > 0) {
				agencyName.append(" (+91 ");
				agencyName.append(mobileNo);
				agencyName.append(") ");
			}
			String agNm = agencyName.toString();
			String laborDate = dto.getLaborDate();
			String venue = dto.getVenue();

			// Initialize nested maps if they don't exist
			groupedData.putIfAbsent(agNm, new LinkedHashMap<>());
			groupedData.get(agNm).putIfAbsent(laborDate, new LinkedHashMap<>());
			groupedData.get(agNm).get(laborDate).putIfAbsent(venue, new ArrayList<>());

			// Add the labor entry
			groupedData.get(agNm).get(laborDate).get(venue).add(dto);
		}

		return groupedData;
	}

	@Override
	public String laborReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer withQty, String startDate, String endDate, Integer isCompanyDetails, List<Long> agencyId,
			Integer withPrice,Integer isPartyDetails, Integer isAgencyNextPage) {

		try {
			if(isAgencyNextPage == null) {
				isAgencyNextPage = 1;
			}
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			String labour = "Labour";
			String dateTimeLabel = "Date & Time";
			String shiftName = "Shift";
			String persons = "Qty";
			String note = "Note";
			String customerName = "Customer Name";
			String function = "Function";
			String venueName = "Venue";
			String priceLabel = "Price";
			String totalpriceLabel = "Total";
			
			float lineHeight = 1f;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				labour = "श्रमिक";
				dateTimeLabel = "दिनांक एवं समय";
				shiftName = "कार्य शिफ्ट";
				persons = "मात्रा";
				note = "टिप्पणी";
				customerName = "ग्राहक का नाम";
				function = "कार्यक्रम";
				venueName = "स्थल";
				priceLabel = "मूल्य";
				totalpriceLabel = "कुल मूल्य";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				labour = "મજૂર";
				dateTimeLabel = "તારીખ અને સમય";
				shiftName = "કાર્ય શિફ્ટ";
				persons = "માત્રા";
				note = "નોંધ";
				customerName = "ગ્રાહકનું નામ";
				function = "કાર્યક્રમ";
				venueName = "સ્થળ";
				priceLabel = "કિંમત";
				totalpriceLabel = "કુલ કિંમત";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			GetEventLaborResponseDto cmpData = new GetEventLaborResponseDto();
			String eventNo = "";
			if (eventId != -1) {
				cmpData = eventLaborServiceImpl.getCmpAndEventData(eventId, lang);
				eventNo = cmpData.getEventNo();
			} else {
				cmpData = eventLaborServiceImpl.getCmpData(userid, lang);
				eventNo = startDate.replace("/", "_");
			}
			Long uId = cmpData.getUserId();
			String cmpName = cmpData.getCompanyName() == null || cmpData.getCompanyName().isEmpty() ? ""
					: cmpData.getCompanyName();
			String cmpEmail = cmpData.getCompanyEmail() == null || cmpData.getCompanyEmail().isEmpty() ? ""
					: cmpData.getCompanyEmail();
			String cmpCountryCode = cmpData.getCountryCode() == null || cmpData.getCountryCode().isEmpty() ? ""
					: cmpData.getCountryCode();
			String cmpMobile = cmpData.getCompanyMobile() == null || cmpData.getCompanyMobile().isEmpty() ? ""
					: cmpData.getCompanyMobile();
			String logo = cmpData.getLogo() == null || cmpData.getLogo().isEmpty() ? "" : cmpData.getLogo();

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
			

			File pdfFile = new File(outputPath + "/" + getReportName(fileName,
					formatDate(cmpData.getEventStartDateTime()), "labor report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(40, 35, 50, 20);

			float[] columnWidthHead = { 25f, 15f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));
			cmpHead.setMarginBottom(5f);

//			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");
//			Image img = new Image(logoData);
			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(120);
			img.setHorizontalAlignment(HorizontalAlignment.LEFT);

			Cell cmp = new Cell(3, 1).add(img).setTextAlignment(TextAlignment.LEFT).setPadding(0).setPaddingLeft(10f)
					.setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			Paragraph cmpPara = new Paragraph().add(
					new Text(cmpName).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18))
					.setMultipliedLeading(lineHeight);
			cmp = new Cell(1, 3).add(cmpPara).setPadding(0).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Mobile No.").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(lineHeight);
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(lineHeight);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(new Text(cmpCountryCode + " " + cmpMobile)
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(lineHeight);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(lineHeight);
			cmp = new Cell().add(cmpPara).setPaddingLeft(7f).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(lineHeight);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
					.setMultipliedLeading(lineHeight);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			List<GetEventLaborResponseDto> eventData = eventLaborServiceImpl.getEventData(eventId, eventFunctionId,
					startDate, endDate, lang, agencyId);
			for (int i = 0; i < eventData.size(); i++) {
				
				if (isCompanyDetails == 1) {
					document.add(cmpHead);
				}

				if(i == 0) {					
					Paragraph heading = new Paragraph().add(new Text("Labour Report")
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18));
					heading.setHorizontalAlignment(HorizontalAlignment.CENTER);
					heading.setMarginTop(0f);
					heading.setTextAlignment(TextAlignment.CENTER);
					heading.setWidth(UnitValue.createPercentValue(100));
					document.add(heading);
				}
				
				GetEventLaborResponseDto data = eventData.get(i);

				String clientName = data.getPartyName() == null || data.getPartyName().isEmpty() ? ""
						: data.getPartyName();
				String functionName = data.getFunctionName() == null || data.getFunctionName().isEmpty() ? ""
						: data.getFunctionName();
				Long functionId = data.getEventFunctionId();
				String venue = "";
				if(lang == 0) {
					venue = data.getFunctionVenue() == null || data.getFunctionVenue().isEmpty() ? ""
							: data.getFunctionVenue();					
				}else if(lang == 1) {
					venue = data.getFunctionVenueHindi() == null || data.getFunctionVenueHindi().isEmpty() ? ""
							: data.getFunctionVenueHindi();					
				}else {
					venue = data.getFunctionVenueGujarati() == null || data.getFunctionVenueGujarati().isEmpty() ? ""
							: data.getFunctionVenueGujarati();					
				}
				Long partyId = data.getPartyId();

				float[] cw = { 25f, 2f, 73 };
				Table tbl = new Table(UnitValue.createPercentArray(cw));
				tbl.setWidth(UnitValue.createPercentValue(100));
				tbl.setMarginTop(5f);
				tbl.setMarginBottom(7f);

				Paragraph funData;
				Cell funCell;
				// 1st row
				if(isPartyDetails == 1){
				funData = new Paragraph().add(new Text(customerName).setFontSize(13).setFont(boldFont))
						.setMargin(0).setMultipliedLeading(1);
				funCell = new Cell().add(funData).setPadding(0).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
				tbl.addCell(funCell);
				funData = new Paragraph().add(new Text(":").setFontSize(13).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setBorder(Border.NO_BORDER).setPadding(0);
				tbl.addCell(funCell);
				funData = new Paragraph().add(new Text(clientName).setFontSize(13).setFont(basicFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setBorder(Border.NO_BORDER).setPadding(0);
				tbl.addCell(funCell);
			}

				// 2ed row
				funData = new Paragraph().add(new Text(function).setFontSize(13).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setPadding(0).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
				tbl.addCell(funCell);
				funData = new Paragraph().add(new Text(":").setFontSize(13).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setBorder(Border.NO_BORDER).setPadding(0);
				tbl.addCell(funCell);
				funData = new Paragraph().add(new Text(functionName).setFontSize(13).setFont(basicFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setBorder(Border.NO_BORDER).setPadding(0);
				tbl.addCell(funCell);

				// 3rd row
				funData = new Paragraph().add(new Text(venueName).setFontSize(13).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setPadding(0).setPaddingLeft(10f).setBorder(Border.NO_BORDER);
				tbl.addCell(funCell);
				funData = new Paragraph().add(new Text(":").setFontSize(13).setFont(boldFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setBorder(Border.NO_BORDER).setPadding(0);
				tbl.addCell(funCell);
				funData = new Paragraph().add(new Text(venue).setFontSize(13).setFont(basicFont)).setMargin(0)
						.setMultipliedLeading(1);
				funCell = new Cell().add(funData).setBorder(Border.NO_BORDER).setPadding(0);
				tbl.addCell(funCell);

				document.add(tbl);

				List<GetEventLaborResponseDto> laborData = eventLaborServiceImpl.getLaborData(eventId, functionId,
						startDate, endDate, lang, partyId);

				for (GetEventLaborResponseDto labor : laborData) {
					Div content = new Div().setKeepTogether(true);

					Long pId = labor.getPartyId();
					String laborName = labor.getLaborName() == null || labor.getLaborName().isEmpty() ? ""
							: labor.getLaborName();
					String laborMobile = labor.getLaborMobile() == null || labor.getLaborMobile().isEmpty() ? ""
							: labor.getLaborMobile();
					Paragraph laborPara = new Paragraph()
							.add(new Text(laborName + " (+91 " + laborMobile + ") ").setFontSize(14).setFont(boldFont))
							.setMargin(0).setMultipliedLeading(1);
					laborPara.setWidth(UnitValue.createPercentValue(100));
					laborPara.setBorder(new SolidBorder(1));
					laborPara.setPaddingLeft(10f);
					laborPara.setPaddingBottom(2f);
					laborPara.setPaddingTop(2f);
					laborPara.setMargin(0);

					content.add(laborPara);

					float[] lbw = { 30f, 25f, 20f, 10f, 15f };
					Table lb = new Table(UnitValue.createPercentArray(lbw));

					if (withPrice == 1) {
						float[] lbw2 = { 22f, 23f, 15f, 5f, 10f, 10f, 15f };
						lb = new Table(UnitValue.createPercentArray(lbw2));
					}

					lb.setWidth(UnitValue.createPercentValue(100));
					lb.setMarginTop(0f);
//					lb.setMarginBottom(20f);

					laborPara = new Paragraph().add(new Text(labour).setFontSize(12).setFont(boldFont)).setMargin(0)
							.setMultipliedLeading(1);
					Cell lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER);
					lb.addCell(lbCell);

					laborPara = new Paragraph().add(new Text(dateTimeLabel).setFontSize(12).setFont(boldFont))
							.setMargin(0).setMultipliedLeading(1);
					lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER);
					lb.addCell(lbCell);

					laborPara = new Paragraph().add(new Text(shiftName).setFontSize(12).setFont(boldFont)).setMargin(0)
							.setMultipliedLeading(1);
					lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER);
					lb.addCell(lbCell);

					laborPara = new Paragraph().add(new Text(persons).setFontSize(12).setFont(boldFont)).setMargin(0)
							.setMultipliedLeading(1);
					lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER);
					lb.addCell(lbCell);

					if (withPrice == 1) {

						laborPara = new Paragraph().add(new Text(priceLabel).setFontSize(12).setFont(boldFont))
								.setMargin(0).setMultipliedLeading(1);
						lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER);
						lb.addCell(lbCell);

						laborPara = new Paragraph().add(new Text(totalpriceLabel).setFontSize(12).setFont(boldFont))
								.setMargin(0).setMultipliedLeading(1);
						lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER);
						lb.addCell(lbCell);
					}

					laborPara = new Paragraph().add(new Text(note).setFontSize(12).setFont(boldFont)).setMargin(0)
							.setMultipliedLeading(1);
					lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER);
					lb.addCell(lbCell);

					List<GetEventLaborResponseDto> laborShiftData = eventLaborServiceImpl.getLaborShiftData(eventId,
							functionId, startDate, endDate, pId, lang, userid);

					for (GetEventLaborResponseDto lsd : laborShiftData) {
						String contactCatName = lsd.getContactCategoryName() == null
								|| lsd.getContactCategoryName().isEmpty() ? "" : lsd.getContactCategoryName();
						String dateTime = lsd.getLaborDate() == null || lsd.getLaborDate().isEmpty() ? ""
								: lsd.getLaborDate();
						String shift = lsd.getLaborShift() == null || lsd.getLaborShift().isEmpty() ? ""
								: lsd.getLaborShift();
						Integer qty = lsd.getQty();
						String notes = lsd.getNotes() == null || lsd.getNotes().isEmpty() ? "" : lsd.getNotes();
						BigDecimal price = lsd.getPrice() == null ? BigDecimal.ZERO : lsd.getPrice();
						BigDecimal totalprice = lsd.getTotalPrice() == null ? BigDecimal.ZERO : lsd.getTotalPrice();

						laborPara = new Paragraph().add(new Text(contactCatName).setFontSize(12).setFont(basicFont))
								.setMargin(0).setMultipliedLeading(1.2f);
						lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER).setPadding(0);
						lb.addCell(lbCell);

						laborPara = new Paragraph().add(new Text(dateTime).setFontSize(12).setFont(basicFont))
								.setMargin(0).setMultipliedLeading(1.2f);
						lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER).setPadding(0);
						lb.addCell(lbCell);

						laborPara = new Paragraph().add(new Text(shift).setFontSize(12).setFont(basicFont)).setMargin(0)
								.setMultipliedLeading(1.2f);
						lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER).setPadding(0);
						lb.addCell(lbCell);

						laborPara = new Paragraph().add(new Text(qty.toString()).setFontSize(12).setFont(basicFont))
								.setMargin(0).setMultipliedLeading(1.2f);
						lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER).setPadding(0);
						lb.addCell(lbCell);

						if (withPrice == 1) {
							laborPara = new Paragraph()
									.add(new Text(price.toString()).setFontSize(12).setFont(basicFont)).setMargin(0)
									.setMultipliedLeading(1.2f);
							lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER).setPadding(0);
							lb.addCell(lbCell);

							laborPara = new Paragraph()
									.add(new Text(totalprice.toString()).setFontSize(12).setFont(basicFont))
									.setMargin(0).setMultipliedLeading(1.2f);
							lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER).setPadding(0);
							lb.addCell(lbCell);
						}

						laborPara = new Paragraph().add(new Text(notes).setFontSize(12).setFont(basicFont)).setMargin(0)
								.setMultipliedLeading(1.2f);
						lbCell = new Cell().add(laborPara).setTextAlignment(TextAlignment.CENTER).setPadding(0);
						lb.addCell(lbCell);
					}
					content.add(lb);
					document.add(content);
				}

				if (i < eventData.size() - 1 && isAgencyNextPage == 1) {
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
					+ getReportName(fileName, formatDate(cmpData.getEventStartDateTime()),
							"labor report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}

	}

	private void addCompanyHeader(Document document, GetEventLaborResponseDto dto, PdfFont basicFont, PdfFont boldFont,
			Integer isUserDetails) throws Exception {

		if (isUserDetails != 1)
			return;

		ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");

		Image img = new Image(logoData).setWidth(120).setHorizontalAlignment(HorizontalAlignment.CENTER);

		float[] columnWidth = { 20f, 80f };
		Table cmpHead = new Table(UnitValue.createPercentArray(columnWidth)).setWidth(UnitValue.createPercentValue(80))
				.setHorizontalAlignment(HorizontalAlignment.CENTER);

		cmpHead.addCell(new Cell().add(img).setBorder(Border.NO_BORDER));

		Paragraph cmpInfo = new Paragraph().add(new Text(dto.getCompanyName() + "\n").setFont(boldFont).setFontSize(16))
				.add(new Text("Mobile : ").setFont(boldFont))
				.add(new Text(dto.getCountryCode() + " " + dto.getCompanyMobile() + "\n").setFont(basicFont))
				.add(new Text("Email : ").setFont(boldFont)).add(new Text(dto.getCompanyEmail()).setFont(basicFont));

		cmpHead.addCell(new Cell().add(cmpInfo).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(Border.NO_BORDER));

		document.add(cmpHead);
		document.add(new Paragraph("\n"));
	}

}
