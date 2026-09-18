package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionDecorItemImagesEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.EventFunctionDecorItemImagesRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.DecoreItemReportResponseDto;
import com.crmportal.response.dto.DecoreReportResponseDto;
import com.crmportal.response.dto.EventFunctionReportResponseDto;
import com.crmportal.response.dto.EventReportResponseDto;
import com.crmportal.service.DecorationReportService;
import com.crmportal.utility.AdobeDocxGenerator;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class DecorationReportServiceImpl implements DecorationReportService {

	@Autowired
	Environment environment;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	AdobeDocxGenerator adobeDocxGenerator;

	@Autowired
	EventFunctionDecorItemImagesRepository eventFunctionDecorItemImagesRepository;

	private String safeText(String value) {
		return value == null ? "" : value;
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

	private void addRow(Table table, String label, String value, Style labelStyle, Style valueStyle, Color borderColor,
			Boolean isSetBorder, String borderType) {
		Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER).simulateBold();
		if (isSetBorder) {
			if (borderType.equalsIgnoreCase("BOTTOM")) {
				labelCell.setBorderBottom(new SolidBorder(borderColor, 1f));
			} else if (borderType.equalsIgnoreCase("TOP")) {
				labelCell.setBorderTop(new SolidBorder(borderColor, 1f));
			}
		}
		table.addCell(labelCell);

		Cell middleCell = new Cell().add(new Paragraph(safeText(":")).addStyle(valueStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER);
		if (isSetBorder) {
			if (borderType.equalsIgnoreCase("BOTTOM")) {
				middleCell.setBorderBottom(new SolidBorder(borderColor, 1f));
			} else if (borderType.equalsIgnoreCase("TOP")) {
				middleCell.setBorderTop(new SolidBorder(borderColor, 1f));
			}
		}
		table.addCell(middleCell);

		Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER);
		if (isSetBorder) {
			if (borderType.equalsIgnoreCase("BOTTOM")) {
				valueCell.setBorderBottom(new SolidBorder(borderColor, 1f));
			} else if (borderType.equalsIgnoreCase("TOP")) {
				valueCell.setBorderTop(new SolidBorder(borderColor, 1f));
			}
		}
		table.addCell(valueCell);
	}

	private void addFullRow(Table table, String label, String value, Style labelStyle, Style valueStyle,
			Color borderColor) {
		Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER).simulateBold();
		table.addCell(labelCell);

		Cell middleCell = new Cell().add(new Paragraph(safeText(":")).addStyle(valueStyle)).setBorder(Border.NO_BORDER)
				.setPadding(0).setTextAlignment(TextAlignment.CENTER);
		table.addCell(middleCell);

		Cell valueCell = new Cell(1, 4).add(new Paragraph(safeText(value)).addStyle(valueStyle))
				.setBorder(Border.NO_BORDER).setPadding(0).setTextAlignment(TextAlignment.CENTER);
		table.addCell(valueCell);

	}

	private void addLabelRow(Table table, String label, Style labelStyle, Color borderColor) {
		Cell labelCell = new Cell(1, 6)
				.add(new Paragraph(safeText(label)).setTextAlignment(TextAlignment.CENTER).setFontSize(16f)
						.addStyle(labelStyle))
				.setBorder(Border.NO_BORDER).setPadding(0).simulateBold().setBorder(Border.NO_BORDER)
				.setBorderTop(new SolidBorder(borderColor, 1f)).setBorderBottom(new SolidBorder(borderColor, 1f));
		table.addCell(labelCell);
	}

	@Override
	public String generateDecoreReport(AdminTemplateModuleResponseDto adminTemplate, Long eventId, Long eventFunctionId,
			Integer isCategorySlogan, Integer isCategoryInstruction, Integer isItemImage, Integer isItemSlogan,
			Integer isItemInstruction, Integer isCompanyLogo, Integer isCompanyDetails, HttpServletRequest re, int lang,
			Long userid, Integer isPartyDetails, Integer isWithoutBg, Integer withVendor,ReportMenuPlanningRequestDTO req) {

		try {

			PdfFont catFont = null;
			PdfFont catFontBold = null;

			PdfFont itemFont = null;
			PdfFont itemFontBold = null;

			PdfFont sloganFont = null;
			PdfFont sloganFontBold = null;

			Integer catFontSize = req.getCatFontSize();
			Integer itemFontSize = req.getItemFontSize();
			Integer sloganFontSize = req.getSloganFontSize();
			
			String customerName = "", customerPhone = "", eName = "", eDate = "", fNotes = "", eVenue = "", l1 = "",
					l2 = "", note = "", eContact = "", eventFlow = "", function = "", person = "", eTime = "",
					date = "", rate = "", party = "", eventNotes = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", remarks = "";

			String contactPersonLabel = "";
			String contactNumberLabel = "";
			String typeOfEventLabel = "";
			String noOfPersonsLabel = "";
			String decorationHeader = "";

			menuPreparationServiceImpl.loadLicense();

			// =========================================================
			// FONT AND LANGUAGE
			// =========================================================

			if (lang == 1) {

				catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

				eDate = "दिनांक";
				eTime = "समय";
				contactPersonLabel = "संपर्क व्यक्ति";
				contactNumberLabel = "संपर्क नंबर";
				eVenue = "आयोजन स्थान";
				typeOfEventLabel = "कार्यक्रम का प्रकार";
				noOfPersonsLabel = "व्यक्तियों की संख्या";
				decorationHeader = "सजावट";
				note = "नोट";
				rate = "रेट";

			} else if (lang == 2) {

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Bold.ttf");

					eDate = "தேதி";
					eTime = "நேரம்";
					contactPersonLabel = "தொடர்பு நபர்";
					contactNumberLabel = "தொடர்பு எண்";
					eVenue = "இடம்";
					typeOfEventLabel = "நிகழ்வு வகை";
					noOfPersonsLabel = "நபர்களின் எண்ணிக்கை";
					decorationHeader = "அலங்காரம்";

				} else if (language.equalsIgnoreCase("Telugu")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Bold.ttf");
					
					eDate = "తేదీ";
					eTime = "సమయం";
					contactPersonLabel = "సంప్రదింపు వ్యక్తి";
					contactNumberLabel = "సంప్రదింపు నంబర్";
					eVenue = "స్థలం";
					typeOfEventLabel = "కార్యక్రమ రకం";
					noOfPersonsLabel = "వ్యక్తుల సంఖ్య";
					decorationHeader = "అలంకరణ";

				} else if (language.equalsIgnoreCase("Malayalam")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");
					
					eDate = "തീയതി";
					eTime = "സമയം";
					contactPersonLabel = "ബന്ധപ്പെടേണ്ട വ്യക്തി";
					contactNumberLabel = "ബന്ധപ്പെടാനുള്ള നമ്പർ";
					eVenue = "സ്ഥലം";
					typeOfEventLabel = "പരിപാടിയുടെ തരം";
					noOfPersonsLabel = "വ്യക്തികളുടെ എണ്ണം";
					decorationHeader = "അലങ്കാരം";

				} else if (language.equalsIgnoreCase("Marathi")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

					eDate = "दिनांक";
					eTime = "वेळ";
					contactPersonLabel = "संपर्क व्यक्ती";
					contactNumberLabel = "संपर्क क्रमांक";
					eVenue = "ठिकाण";
					typeOfEventLabel = "कार्यक्रमाचा प्रकार";
					noOfPersonsLabel = "व्यक्तींची संख्या";
					decorationHeader = "सजावट";

				} else {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold =menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					eDate = "તારીખ";
					eTime = "સમય";
					contactPersonLabel = "સંપર્ક વ્યક્તિ";
					contactNumberLabel = "સંપર્ક નંબર";
					eVenue = "સ્થળ";
					typeOfEventLabel = "કાર્યક્રમનો પ્રકાર";
					noOfPersonsLabel = "વ્યક્તિઓની સંખ્યા";
					decorationHeader = "સુશોભન";
				}

			} else {

				catFont = menuPreparationServiceImpl.getFont(req.getCatFontId(), false, "gothic");
				catFontBold = menuPreparationServiceImpl.getFont(req.getCatFontId(), true, "gothic");

				itemFont = menuPreparationServiceImpl.getFont(req.getItemFontId(), false, "gothic");
				itemFontBold = menuPreparationServiceImpl.getFont(req.getItemFontId(), true, "gothic");

				sloganFont = menuPreparationServiceImpl.getFont(req.getSloganFontId(), false, "gothic");
				sloganFontBold = menuPreparationServiceImpl.getFont(req.getSloganFontId(), true, "gothic");

				eDate = "DATE";
				eTime = "TIME";
				contactPersonLabel = "CONTACT PERSON";
				contactNumberLabel = "CONTACT NUMBER";
				eVenue = "VENUE";
				typeOfEventLabel = "TYPE OF EVENT";
				noOfPersonsLabel = "NO OF PERSONS";
				decorationHeader = "Decoration";
				note = "Note";
				rate = "Rate";
			}

			// =========================================================
			// EVENT DATA
			// =========================================================

			EventReportResponseDto eventDto = menuPreparationServiceImpl.fetchAndPrepareEventReportData(eventId,
					eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "Data not found.";
			}

			// =========================================================
			// BACKGROUND IMAGES - SAME AS REPORT TYPE 14
			// =========================================================

			ImageData mainBgData = menuPreparationServiceImpl.loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());

//			ImageData mainBgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/tgb4_1.png");

			ImageData watermarkBgData = menuPreparationServiceImpl.loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());

//			ImageData watermarkBgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/tgb4_2.png");

			ImageData tncPage = null;

			if (adminTemplate.getTemplateMaster().getCatBgPage() != null	
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {

				tncPage = menuPreparationServiceImpl.loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
//			tncPage = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/tgb_tnc.png");
			}

			// =========================================================
			// OUTPUT PATH
			// =========================================================

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "decor report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);

			PdfDocument pdfDocument = new PdfDocument(writer);

			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);

			document.setMargins(60, 60, 60, 50);

			Color blackColor = new DeviceRgb(0, 0, 0);

			// =========================================================
			// EVENT DETAILS
			// =========================================================

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";

			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";

			String eventDate = eventDto.getEventStartTimestamp() == null ? "" : eventDto.getEventStartTimestamp();

			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime() : "";

			String pax = eventDto.getPax() != null ? eventDto.getPax() : "";

			String prefix = eventDto.getPrefix() != null ? eventDto.getPrefix() : "";

			String reference = eventDto.getReference() != null && eventDto.getReference().trim().length() != 0
					? " REF. " + eventDto.getReference().toUpperCase()
					: "";

			// =========================================================
			// STYLES
			// =========================================================

			Style labelStyle = new Style().setFont(catFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT);

			Style valueStyle = new Style().setFont(itemFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT);

			Style boldValueStyle = new Style().setFont(itemFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT).simulateBold();

			Style boldLabelValueStyle = new Style().setFont(catFont).setFontColor(blackColor).setFontSize(14)
					.setTextAlignment(TextAlignment.LEFT).simulateBold();

			boolean isFirstFunction = true;

			// =========================================================
			// FUNCTION LOOP
			// =========================================================

			for (EventFunctionReportResponseDto fn : eventDto.getFunctions()) {

				List<DecoreReportResponseDto> decoreCategories = fn.getDecoreCategories();

				if (decoreCategories == null || decoreCategories.isEmpty()) {
					continue;
				}

				if (!isFirstFunction) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}

				// =====================================================
				// HEADER INFO
				// =====================================================

				Table header = new Table(UnitValue.createPercentArray(new float[] { 30f, 2f, 30f, 15f, 2f, 21f }));

				header.setWidth(UnitValue.createPercentValue(100f));
				header.setFixedLayout();
				header.setHorizontalAlignment(HorizontalAlignment.CENTER);
				header.setMarginTop(240f);

				// DATE + TIME

				addTwoColRowWithBottomBorder(header, eDate, eventDate.toUpperCase(), eTime, eventTime.toUpperCase(),
						labelStyle, valueStyle, valueStyle, blackColor);

				header.setBorderBottom(new SolidBorder(blackColor, 1f));

				// CONTACT PERSON

				addFullRowNoBorder(header, contactPersonLabel,
						prefix.toUpperCase() + " " + hostName.toUpperCase() + reference, labelStyle, valueStyle,
						blackColor);

				// CONTACT NUMBER

				addFullRowNoBorder(header, contactNumberLabel, mobileNo.toUpperCase(), labelStyle,
						new Style().setFont(itemFont).setFontColor(blackColor).setFontSize(14).simulateItalic(),
						blackColor);

				String functionVenue = "";

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, fn.getFunctionId());

				if (!banquets.isEmpty()) {

					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", ")).toUpperCase();

				} else {

					functionVenue = lang == 1 ? fn.getFunctionVenueHindi()
							: lang == 2 ? fn.getFunctionVenueGujarati() : fn.getFunctionVenue().toUpperCase();
				}

				addFullRowNoBorder(header, eVenue, functionVenue.toUpperCase(), labelStyle, boldValueStyle,
						blackColor);

				String functionName = fn.getFunctionName() != null ? fn.getFunctionName().toUpperCase() : "";

				addFullRowNoBorder(header, typeOfEventLabel, functionName, boldLabelValueStyle, boldValueStyle,
						blackColor);

				addFullRowNoBorder(header, noOfPersonsLabel, pax + " PERSONS", labelStyle, boldValueStyle, blackColor);

				header.setBorderBottom(new SolidBorder(blackColor, 1f));

				document.add(header);

				Paragraph sectionTitle = new Paragraph(decorationHeader).setFont(catFont).setFontSize(16)
						.setFontColor(blackColor).simulateBold().setTextAlignment(TextAlignment.CENTER).setMarginTop(6f)
						.setMarginBottom(6f);

				document.add(sectionTitle);

				document.add(new LineSeparator(new SolidLine(1f)).setMarginBottom(10f));

				Color greenColor = new DeviceRgb(212, 175, 0);
				Color redColor = new DeviceRgb(255, 0, 0);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				// =====================================================
				// DECOR CATEGORY / ITEM LOOP
				// =====================================================

				for (DecoreReportResponseDto category : decoreCategories) {

					List<DecoreItemReportResponseDto> items = category.getDecoreItems();

					if (items == null || items.isEmpty()) {
						continue;
					}

					for (DecoreItemReportResponseDto item : items) {

						String itemName = item.getNameEnglish() != null ? item.getNameEnglish() : "";

						String subName = item.getSubItem() != null ? item.getSubItem() : "";

						Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;

						BigDecimal price = item.getPrice();

						Integer itemQty = item.getItemQty();

						// ==== CHANGED: font size now dynamic via itemFontSize (matches report 14) ====
						Paragraph headerPara = new Paragraph().setFont(itemFont)
								.setFontSize(menuPreparationServiceImpl.getFontSize(itemFontSize, 19))
								.setFontColor(blackColor).setMarginBottom(3f);

						if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {

							headerPara.add(new Text("• " + itemName.toUpperCase() + ": ").simulateBold());

							if (!subName.isEmpty()) {

								menuPreparationServiceImpl.addFormattedText(headerPara, subName.trim(), lang,false);
							}

							BigDecimal price1 = price != null ? price : BigDecimal.ZERO;

							int qty = itemQty != null ? itemQty : 0;

							BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));

							String postLabel = "";

							if (qty > 1) {
								postLabel = " /- + Tax Each";
							} else {
								postLabel = " /- + Tax";
							}

							headerPara.add(new Text("  @Rs. " + totalAmount + postLabel).simulateBold());

						} else if (!subName.isEmpty()) {

							headerPara.add(new Text("• " + itemName.toUpperCase() + ": ").simulateBold());

							menuPreparationServiceImpl.addFormattedText(headerPara, subName.trim(), lang,false);

						} else {

							headerPara.add(new Text("• " + itemName.toUpperCase()).simulateBold());
						}

						if (withVendor == 1) {
							if (item.getVendorName() != null && !item.getVendorName().trim().isEmpty()) {

								headerPara.add(new Text(" - " + item.getVendorName()).simulateBold());
							}
						}

						document.add(headerPara);

						// ITEM INSTRUCTION

						if (isItemInstruction == 1 && item.getDecoreItemNotes() != null
								&& !item.getDecoreItemNotes().trim().isEmpty()) {
							for (String line : item.getDecoreItemNotes().split("(?i)<br\\s*/?>")) {

								if (!line.trim().isEmpty()) {


									// ==== CHANGED: font now itemFont (was basicFont), size now dynamic via itemFontSize ====
									Paragraph notePara = new Paragraph().setFont(itemFont)
											.setFontSize(menuPreparationServiceImpl.getFontSize(itemFontSize, 19))
											.setFontColor(blackColor).setMultipliedLeading(1f).setMarginLeft(0f)
											.setMarginTop(0).setTextAlignment(TextAlignment.LEFT).setMarginBottom(0)
											.setPadding(0f).setPaddingLeft(130f);

									menuPreparationServiceImpl.addFormattedText(notePara, line.trim(), lang,false);

									document.add(notePara);
								}
							}
						}

						// ITEM IMAGES

						List<EventFunctionDecorItemImagesEntity> decorItemImagesEntities = eventFunctionDecorItemImagesRepository
								.findByEventIdAndEventFunctionIdAndDecorItemIdAndIsDeleteFalse(eventId,
										fn.getFunctionId(), item.getId());

						List<String> images = decorItemImagesEntities.stream()
								.map(image -> environment.getProperty("app.image.url", "") + image.getImagePath())
								.collect(Collectors.toList());

						if (isItemImage == 1 && images != null && !images.isEmpty()) {

							for (String imagePath : images) {

								if (imagePath == null || imagePath.trim().isEmpty()) {
									continue;
								}

								try {

									ImageData imgData = menuPreparationServiceImpl.loadImageFromResource(imagePath);

									Image img = new Image(imgData);

									img.scaleAbsolute(432, 288);

									img.setHorizontalAlignment(HorizontalAlignment.CENTER);

									img.setMarginTop(8f);

									img.setMarginBottom(14f);

									img.setBorder(new SolidBorder(ColorConstants.BLACK, 1f));

									document.add(img);

								} catch (Exception imgEx) {

									System.out.println("Could not load image for decor item: " + imagePath);
								}
							}
						}

						// ITEM SPACE

						for (int i = 0; i < itemSpace; i++) {
							document.add(new Paragraph("\n"));
						}

						// SPACE BETWEEN DECORATION ITEMS

						document.add(new Paragraph().setMarginBottom(10f));
					}
				}

				isFirstFunction = false;
			}

			// Keep existing last-page creation logic
			if (tncPage != null) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Paragraph eventManager = new Paragraph().setFont(itemFont).setFontSize(14f).setMarginTop(650f)
						.setTextAlignment(TextAlignment.RIGHT).setMarginRight(30f);

				eventManager.add(new Text("EVENT MANAGER - ").setFont(catFont));

				eventManager.add(new Text(eventDto.getEventManagerName() != null ? eventDto.getEventManagerName() : "")
						.setFont(catFont).simulateBold().setUnderline());

				document.add(eventManager);
			}

			// =========================================================
			// BACKGROUND SETUP
			// KEEP SAME EXISTING STATIC + DYNAMIC PAGE LOGIC
			// =========================================================

			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {

				PdfPage page = pdfDocument.getPage(i);

				Rectangle pageSize = page.getPageSize();

				ImageData backgroundImage = null;

				if (i == 1) {

					backgroundImage = mainBgData;

				} else if (i == totalPages && tncPage != null) {

					backgroundImage = tncPage;

				} else {

					backgroundImage = watermarkBgData;

				}

				if (backgroundImage != null) {

					PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(),
							pdfDocument);

					Canvas backgroundCanvas = new Canvas(pdfCanvas, pageSize);

					Image bgImage = new Image(backgroundImage);

					bgImage.scaleAbsolute(pageSize.getWidth(), pageSize.getHeight());

					bgImage.setFixedPosition(pageSize.getLeft(), pageSize.getBottom());

					backgroundCanvas.add(bgImage);

					backgroundCanvas.close();
				}
			}

			// =========================================================
			// PAGE NUMBERS
			// =========================================================

//			for (int i = 1; i <= totalPages; i++) {
//
//				PdfPage page = pdfDocument.getPage(i);
//
//				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
//
//				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 40,
//						TextAlignment.CENTER);
//
//				canvas.close();
//			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"decor report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private void addTwoColRowWithBottomBorder(Table table, String label1, String value1, String label2, String value2,
			Style labelStyle, Style valueStyle1, Style valueStyle2, Color blackColor) {

		Border bottom = new SolidBorder(blackColor, 1f);

		table.addCell(new Cell().add(new Paragraph(label1).addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setBorderBottom(bottom));

		table.addCell(new Cell().add(new Paragraph(":").addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setBorderBottom(bottom));

		table.addCell(new Cell().add(new Paragraph(value1).addStyle(valueStyle1)).setBorder(Border.NO_BORDER)
				.setBorderBottom(bottom));

		table.addCell(new Cell().add(new Paragraph(label2).addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setBorderBottom(bottom));

		table.addCell(new Cell().add(new Paragraph(":").addStyle(labelStyle)).setBorder(Border.NO_BORDER)
				.setBorderBottom(bottom));

		table.addCell(new Cell().add(new Paragraph(value2).addStyle(valueStyle2)).setBorder(Border.NO_BORDER)
				.setBorderBottom(bottom));
	}

	private void addTwoColRow(Table table, String label1, String value1, String label2, String value2, Style labelStyle,
			Style valueStyle1, Style valueStyle2, Color blackColor) {

		table.addCell(new Cell().add(new Paragraph(label1).addStyle(labelStyle)).setBorder(Border.NO_BORDER));

		table.addCell(new Cell().add(new Paragraph(":").addStyle(labelStyle)).setBorder(Border.NO_BORDER));

		table.addCell(new Cell().add(new Paragraph(value1).addStyle(valueStyle1)).setBorder(Border.NO_BORDER));

		table.addCell(new Cell().add(new Paragraph(label2).addStyle(labelStyle)).setBorder(Border.NO_BORDER));

		table.addCell(new Cell().add(new Paragraph(":").addStyle(labelStyle)).setBorder(Border.NO_BORDER));

		table.addCell(new Cell().add(new Paragraph(value2).addStyle(valueStyle2)).setBorder(Border.NO_BORDER));
	}

	private void addFullRowNoBorder(Table table, String label, String value, Style labelStyle, Style valueStyle,
			Color blackColor) {

		table.addCell(new Cell(1, 1).add(new Paragraph(label).addStyle(labelStyle)).setBorder(Border.NO_BORDER));

		table.addCell(new Cell(1, 1).add(new Paragraph(":").addStyle(labelStyle)).setBorder(Border.NO_BORDER));

		table.addCell(new Cell(1, 4).add(new Paragraph(value).addStyle(valueStyle)).setBorder(Border.NO_BORDER));
	}

	@Override
	public String generateDecoreReportDocx(AdminTemplateModuleResponseDto adminTemplate, Long eventId,
			Long eventFunctionId, Integer isCategorySlogan, Integer isCategoryInstruction, Integer isItemImage,
			Integer isItemSlogan, Integer isItemInstruction, Integer isCompanyLogo, Integer isCompanyDetails,
			HttpServletRequest re, int lang, Long userid, Integer isPartyDetails, Integer withOutBg,
			Integer withVendor,ReportMenuPlanningRequestDTO req) {

		generateDecoreReport(adminTemplate, eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction,
				isItemImage, isItemSlogan, isItemInstruction, isCompanyLogo, isCompanyDetails, re, lang, userid,
				isPartyDetails, withOutBg, withVendor,req);

		try {
			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			if (event == null) {
				return null;
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			String pdfFileName = getReportName(getPartyNameByEventId(eventId),
					formatDate(event.getEventStartDateTime()), "back office report") + ".pdf";

			String pdfPath = rootPath + "resources/tempDownload/" + event.getEventNo() + "/" + pdfFileName;

			return adobeDocxGenerator.convertPdfToDocxAndGetUrl(new File(pdfPath), event.getEventNo());

		} catch (Exception e) {
			throw new RuntimeException("Failed to convert PDF to DOCX", e);
		}
	}

}
