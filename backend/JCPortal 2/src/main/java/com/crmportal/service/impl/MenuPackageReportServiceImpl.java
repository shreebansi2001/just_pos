package com.crmportal.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.EventTermsAndConditionEntity;
import com.crmportal.entity.EventTermsAndConditionFeaturesEntity;
import com.crmportal.repository.CustomPackageDetailsRepository;
import com.crmportal.repository.CustomPackageRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.EventFunctionReportResponseDto;
import com.crmportal.response.dto.EventReportResponseDto;
import com.crmportal.response.dto.ExtraChargesResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;
import com.crmportal.service.MenuPackageReportService;
import com.crmportal.utility.BackgroundEventHandler;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.splitting.ISplitCharacters;
@Service
public class MenuPackageReportServiceImpl implements MenuPackageReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	Environment environment;
	
	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;
	
	@Autowired
	CustomPackageRepository customPackageRepository;
	
	@Autowired
	CustomPackageDetailsRepository customPackageDetailsRepository;
	
	@Override
	public String generateMenuPackageReportType1(Integer lang, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req, HttpServletRequest re) {
		try {
			PdfFont basicFont = null;
			PdfFont basicFontBold = null;

			PdfFont catFont = null;
			PdfFont catFontBold = null;

			PdfFont itemFont = null;
			PdfFont itemFontBold = null;

			PdfFont sloganFont = null;
			PdfFont sloganFontBold = null;

			Integer catFontSize = req.getCatFontSize();
			Integer itemFontSize = req.getItemFontSize();
			Integer sloganFontSize = req.getSloganFontSize();

			menuPreparationServiceImpl.loadLicense();

			String pckNameLabel = "";
	        String pckPriceLabel = "";
	        String anyLabel = "";
	        String itemLabel = "";

			int x = 185;
			int y = 625;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				
				pckNameLabel = "पैकेज का नाम";
	            pckPriceLabel = "पैकेज मूल्य";
	            anyLabel = "कोई भी";
	            itemLabel = "आइटम";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Bold.ttf");
					
					pckNameLabel = "தொகுப்பு பெயர்";
	                anyLabel = "ஏதேனும்";
	                itemLabel = "பொருள்";
	                pckPriceLabel = "பேக்கேஜ் விலை";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Bold.ttf");
					
					pckNameLabel = "ప్యాకేజ్ పేరు";
	                anyLabel = "ఏదైనా";
	                itemLabel = "వస్తువు";
	                pckPriceLabel = "ప్యాకేజ్ ధర";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					pckNameLabel = "പാക്കേജിന്റെ പേര്";
	                anyLabel = "ഏതെങ്കിലും";
	                itemLabel = "ഇനം";
	                pckPriceLabel = "പാക്കേജ് വില";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

					pckNameLabel = "पॅकेजचे नाव";
	                itemLabel = "आयटम";
	                anyLabel = "कोणतेही";
	                pckPriceLabel = "पॅकेज रेट";
				} else {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

	                pckNameLabel = "પેકેજનું નામ";
	                anyLabel = "કોઈપણ";
	                itemLabel = "આઇટમ";
	                pckPriceLabel = "પેકેજ રેટ";
				}

				System.out.println("Gujarati font loaded successfully");
			} else {
				pckNameLabel = "Package Name";
	            anyLabel = "Any";
	            itemLabel = "Item";
	            pckPriceLabel = "Package Price";
	            
				catFont = menuPreparationServiceImpl.getFont(req.getCatFontId(), false, "times");
				catFontBold = menuPreparationServiceImpl.getFont(req.getCatFontId(), true, "times");

				itemFont = menuPreparationServiceImpl.getFont(req.getItemFontId(), false, "times");
				itemFontBold = menuPreparationServiceImpl.getFont(req.getItemFontId(), true, "times");

				sloganFont = menuPreparationServiceImpl.getFont(req.getSloganFontId(), false, "times");
				sloganFontBold = menuPreparationServiceImpl.getFont(req.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			String headingrgb = adminTemplate.getTemplateMaster().getHeadingFontColor();

			String[] parts1 = headingrgb.replace("rgba(", "").replace(")", "").split(",");

			int r1 = Integer.parseInt(parts1[0].trim());
			int g1 = Integer.parseInt(parts1[1].trim());
			int b1 = Integer.parseInt(parts1[2].trim());

			String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();

			String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");

			int r2 = Integer.parseInt(parts2[0].trim());
			int g2 = Integer.parseInt(parts2[1].trim());
			int b2 = Integer.parseInt(parts2[2].trim());

			String descriptionrgb = adminTemplate.getTemplateMaster().getDescriptionFontColor();
			String[] parts3 = descriptionrgb.replace("rgba(", "").replace(")", "").split(",");

			int r3 = Integer.parseInt(parts3[0].trim());
			int g3 = Integer.parseInt(parts3[1].trim());
			int b3 = Integer.parseInt(parts3[2].trim());

			// Define colors
			Color creamGold = new DeviceRgb(r2, g2, b2);
			Color softGold = new DeviceRgb(r1, g1, b1);
			Color descriptionFontColor = new DeviceRgb(r3, g3, b3);

//			Color creamGold = new DeviceRgb(0, 0, 0);
//			Color softGold = new DeviceRgb(0, 0, 0);

			CustomPackageEntity packageEntity = customPackageRepository
					.findByIdAndIsDeleteFalse(req.getCustomPackageId());

			if (packageEntity == null) {
				return "Data not found.";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/"
					+ packageEntity.getUser().getUserBasicDetails().getCompanyName() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + packageEntity.getNameEnglish() + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(170, 130, 60, 130);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/MainFront_Jay.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData watermarkBgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/aroma-watermark.png");
//			ImageData tncPage = null;
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData lastBgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/aroma-last.png");

			// Load all background images upfront
			ImageData watermarkBgData = menuPreparationServiceImpl.loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = menuPreparationServiceImpl.loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());
			ImageData whiteBgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			
			Paragraph invisibleContent = null;

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			float pageHeight = pdfDocument.getDefaultPageSize().getHeight();
			
			Table centerTable = new Table(1);

			centerTable.setWidth(UnitValue.createPercentValue(100));
			centerTable.setHeight(pageHeight - 40);
			centerTable.setBorder(Border.NO_BORDER);

			Cell centerCell = new Cell();

			centerCell.setBorder(Border.NO_BORDER);
			centerCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
			centerCell.setTextAlignment(TextAlignment.CENTER);

			centerCell.add(new Paragraph(pckNameLabel).setFont(catFontBold).setFontColor(softGold).setFontSize(22)
					.setMarginBottom(10));

			centerCell.add(new Paragraph(packageEntity.getNameEnglish()).setFont(itemFontBold)
					.setFontColor(descriptionFontColor).setFontSize(28).setMarginBottom(35));

			centerCell.add(new Paragraph(pckPriceLabel).setFont(catFontBold).setFontColor(softGold).setFontSize(22)
					.setMarginBottom(10));

			centerCell.add(new Paragraph(
					packageEntity.getPrice() != null ? "₹ " + packageEntity.getPrice().toPlainString() : "-")
					.setFont(itemFontBold).setFontColor(descriptionFontColor).setFontSize(26));

			centerTable.addCell(centerCell);

			document.add(centerTable);

			List<Object[]> allDetails = customPackageDetailsRepository.getPackageDetailsLangWise(packageEntity.getId(),
					userId, lang);

			// Unique menu categories (keep first occurrence)
			Map<Long, Object[]> menuMap = allDetails.stream().filter(e -> e[1] != null).collect(Collectors
					.toMap(e -> ((Number) e[9]).longValue(), e -> e, (first, second) -> first, LinkedHashMap::new));

			List<Object[]> menuList = new ArrayList<>(menuMap.values());

			// Group items by category
			Map<Long, List<Object[]>> itemMap = allDetails.stream().filter(e -> e[2] != null).collect(
					Collectors.groupingBy(e -> ((Number) e[9]).longValue(), LinkedHashMap::new, Collectors.toList()));

			// Add menu content
			for (Object[] menu : menuList) {
				Long menuCategoryId = ((Number) menu[9]).longValue();

				List<Object[]> items = itemMap.getOrDefault(menuCategoryId, Collections.emptyList());
				
				String menuName = menu[1] != null ? menu[1].toString() : "";
				String anyItem = menu[7] != null && ((Number)menu[7]).intValue() != 0 ? " (Any " + ((Number)menu[7]) + ")" : "";

				Div menuContent = new Div();
				menuContent.setKeepTogether(true);

				Paragraph categoryParagraph = new Paragraph().add(new Text(menuPreparationServiceImpl.formatText(menuName + anyItem, lang))
						.setFont(catFont).setFontSize(menuPreparationServiceImpl.getFontSize(catFontSize, 20)).setFontColor(softGold));

				categoryParagraph.setTextAlignment(TextAlignment.CENTER).setUnderline().setFixedLeading(17f);

				Table table = new Table(1);
				table.setBorder(Border.NO_BORDER);
				table.setWidth(UnitValue.createPercentValue(100));
				table.addCell(new Cell().add(categoryParagraph).setBorder(Border.NO_BORDER));

				table.setMarginBottom(5f);
				menuContent.add(table).setMarginTop(20f);

				// ================= MENU ITEMS =================
				for (Object[] item : items) {
					String itemName = item[2] != null ? item[2].toString() : "";
					
					// ---------- Item Name ----------
					Paragraph itemParagraph = new Paragraph().add(new Text(itemName)
							.setFont(itemFont).setFontSize(menuPreparationServiceImpl.getFontSize(itemFontSize, 16)).setFontColor(creamGold)).setMarginTop(10f);

					if(req.getIsWithPrice() == 1) {
						String price = item[8] != null ? " (RS." + ((Number)item[8]) + ")" : "";
						itemParagraph.add(new Text(price).setFont(itemFont).setFontSize(menuPreparationServiceImpl.getFontSize(itemFontSize, 16)).setFontColor(creamGold));
					}
					
					itemParagraph.setTextAlignment(TextAlignment.CENTER).setFixedLeading(15f)
							.setMarginBottom(0);

					menuContent.add(itemParagraph);
				}

				document.add(menuContent);
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// ============ LAST PAGE ============
			bgHandler.setPageBackground(lastPageNum, lastBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ packageEntity.getUser().getUserBasicDetails().getCompanyName() + "/"
					+ packageEntity.getNameEnglish() + ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}
	
	@Override
	public String generateMenuPackageReportType2(Integer lang, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req, HttpServletRequest re) {
		try {
			PdfFont basicFont = null;
			PdfFont basicFontBold = null;

			PdfFont catFont = null;
			PdfFont catFontBold = null;

			PdfFont itemFont = null;
			PdfFont itemFontBold = null;

			PdfFont sloganFont = null;
			PdfFont sloganFontBold = null;

			Integer catFontSize = req.getCatFontSize();
			Integer itemFontSize = req.getItemFontSize();
			Integer sloganFontSize = req.getSloganFontSize();

			menuPreparationServiceImpl.loadLicense();

			String pckNameLabel = "";
			String pckPriceLabel = "";
			String anyLabel = "";
			String itemLabel = "";
			
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				
				pckNameLabel = "पैकेज का नाम";
	            pckPriceLabel = "पैकेज मूल्य";
	            anyLabel = "कोई भी";
	            itemLabel = "आइटम";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTamil-Bold.ttf");
					
					pckNameLabel = "தொகுப்பு பெயர்";
	                anyLabel = "ஏதேனும்";
	                itemLabel = "பொருள்";
	                pckPriceLabel = "பேக்கேஜ் விலை";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansTelugu-Bold.ttf");
					
					pckNameLabel = "ప్యాకేజ్ పేరు";
	                anyLabel = "ఏదైనా";
	                itemLabel = "వస్తువు";
	                pckPriceLabel = "ప్యాకేజ్ ధర";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					pckNameLabel = "പാക്കേജിന്റെ പേര്";
	                anyLabel = "ഏതെങ്കിലും";
	                itemLabel = "ഇനം";
	                pckPriceLabel = "പാക്കേജ് വില";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");

					pckNameLabel = "पॅकेजचे नाव";
	                itemLabel = "आयटम";
	                anyLabel = "कोणतेही";
	                pckPriceLabel = "पॅकेज रेट";
				} else {
					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

	                pckNameLabel = "પેકેજનું નામ";
	                anyLabel = "કોઈપણ";
	                itemLabel = "આઇટમ";
	                pckPriceLabel = "પેકેજ રેટ";
				}

				System.out.println("Gujarati font loaded successfully");
			} else {
				pckNameLabel = "Package Name";
	            anyLabel = "Any";
	            itemLabel = "Item";
	            pckPriceLabel = "Package Price";
	            
				catFont = menuPreparationServiceImpl.getFont(req.getCatFontId(), false, "times");
				catFontBold = menuPreparationServiceImpl.getFont(req.getCatFontId(), true, "times");

				itemFont = menuPreparationServiceImpl.getFont(req.getItemFontId(), false, "times");
				itemFontBold = menuPreparationServiceImpl.getFont(req.getItemFontId(), true, "times");

				sloganFont = menuPreparationServiceImpl.getFont(req.getSloganFontId(), false, "times");
				sloganFontBold = menuPreparationServiceImpl.getFont(req.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			String headingrgb = adminTemplate.getTemplateMaster().getHeadingFontColor();

			String[] parts1 = headingrgb.replace("rgba(", "").replace(")", "").split(",");

			int r1 = Integer.parseInt(parts1[0].trim());
			int g1 = Integer.parseInt(parts1[1].trim());
			int b1 = Integer.parseInt(parts1[2].trim());

			String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();

			String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");

			int r2 = Integer.parseInt(parts2[0].trim());
			int g2 = Integer.parseInt(parts2[1].trim());
			int b2 = Integer.parseInt(parts2[2].trim());

			String descriptionrgb = adminTemplate.getTemplateMaster().getDescriptionFontColor();
			String[] parts3 = descriptionrgb.replace("rgba(", "").replace(")", "").split(",");

			int r3 = Integer.parseInt(parts3[0].trim());
			int g3 = Integer.parseInt(parts3[1].trim());
			int b3 = Integer.parseInt(parts3[2].trim());

			// Define colors
			Color creamGold = new DeviceRgb(r2, g2, b2);
			Color softGold = new DeviceRgb(r1, g1, b1);
			Color descriptionFontColor = new DeviceRgb(r3, g3, b3);

//			Color creamGold = new DeviceRgb(0, 0, 0);
//			Color softGold = new DeviceRgb(0, 0, 0);

			CustomPackageEntity packageEntity = customPackageRepository
					.findByIdAndIsDeleteFalse(req.getCustomPackageId());

			if (packageEntity == null) {
				return "Data not found.";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/"
					+ packageEntity.getUser().getUserBasicDetails().getCompanyName() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + packageEntity.getNameEnglish() + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(170, 130, 100, 130);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/MainFront_Jay.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData watermarkBgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/aroma_watermark1.png");
//			ImageData tncPage = null;
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData lastBgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/aroma_last1.png");

			// Load all background images upfront
			ImageData watermarkBgData = menuPreparationServiceImpl.loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = menuPreparationServiceImpl.loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			Paragraph invisibleContent = null;

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			float pageHeight = pdfDocument.getDefaultPageSize().getHeight();
			
			Table centerTable = new Table(1);

			centerTable.setWidth(UnitValue.createPercentValue(100));
			centerTable.setHeight(pageHeight - 40);
			centerTable.setBorder(Border.NO_BORDER);

			Cell centerCell = new Cell();

			centerCell.setBorder(Border.NO_BORDER);
			centerCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
			centerCell.setTextAlignment(TextAlignment.CENTER);

			centerCell.add(new Paragraph(pckNameLabel).setFont(catFontBold).setFontColor(softGold).setFontSize(22)
					.setMarginBottom(10));

			centerCell.add(new Paragraph(packageEntity.getNameEnglish()).setFont(itemFontBold)
					.setFontColor(descriptionFontColor).setFontSize(28).setMarginBottom(35));

			centerCell.add(new Paragraph(pckPriceLabel).setFont(catFontBold).setFontColor(softGold).setFontSize(22)
					.setMarginBottom(10));

			centerCell.add(new Paragraph(
					packageEntity.getPrice() != null ? "₹ " + packageEntity.getPrice().toPlainString() : "-")
					.setFont(itemFontBold).setFontColor(descriptionFontColor).setFontSize(26));

			centerTable.addCell(centerCell);

			document.add(centerTable);

			List<Object[]> allDetails = customPackageDetailsRepository.getPackageDetailsLangWise(packageEntity.getId(),
					userId, lang);

			// Unique menu categories (keep first occurrence)
			Map<Long, Object[]> menuMap = allDetails.stream().filter(e -> e[1] != null).collect(Collectors
					.toMap(e -> ((Number) e[9]).longValue(), e -> e, (first, second) -> first, LinkedHashMap::new));

			List<Object[]> menuList = new ArrayList<>(menuMap.values());

			// Group items by category
			Map<Long, List<Object[]>> itemMap = allDetails.stream().filter(e -> e[2] != null).collect(
					Collectors.groupingBy(e -> ((Number) e[9]).longValue(), LinkedHashMap::new, Collectors.toList()));
			
			// Add menu content
			for (Object[] menu : menuList) {
				Long menuCategoryId = ((Number) menu[9]).longValue();
				String anyItem = menu[7] != null && ((Number)menu[7]).intValue() != 0 ? " (Any " + ((Number)menu[7]) + ")" : "";
				
				List<Object[]> items = itemMap.getOrDefault(menuCategoryId, Collections.emptyList());
				
				String menuName = menu[1] != null ? menu[1].toString() : "";

				Div menuContent = new Div();
				menuContent.setKeepTogether(true);

				// ================= CATEGORY NAME + INSTRUCTION =================
				Paragraph categoryParagraph = new Paragraph().add(new Text(menuPreparationServiceImpl.formatText(menuName + anyItem, lang))
						.setFont(catFont).setFontSize(menuPreparationServiceImpl.getFontSize(catFontSize, 20)).setFontColor(softGold));
				categoryParagraph.setTextAlignment(TextAlignment.CENTER).setUnderline().setFixedLeading(17f);

				Table table = new Table(1);
				table.setBorder(Border.NO_BORDER);
				table.setWidth(UnitValue.createPercentValue(100));
				table.addCell(new Cell().add(categoryParagraph).setBorder(Border.NO_BORDER));

				table.setMarginBottom(5f);
				menuContent.add(table).setMarginTop(20f);

				// ================= MENU ITEMS =================
				for (Object[] item : items) {
					String itemName = item[2] != null ? item[2].toString() : "";
					
					// ---------- Item Name + Instruction ----------
					Paragraph itemParagraph = new Paragraph().add(new Text(menuPreparationServiceImpl.formatText(itemName, lang))
							.setFont(itemFont).setFontSize(menuPreparationServiceImpl.getFontSize(itemFontSize, 16)).setFontColor(creamGold)).setMarginTop(10f);
					
					if(req.getIsWithPrice() == 1) {
						String price = item[8] != null ? " (RS." + ((Number)item[8]) + ")" : "";
						itemParagraph.add(new Text(price).setFont(itemFont).setFontSize(menuPreparationServiceImpl.getFontSize(itemFontSize, 16)).setFontColor(creamGold));
					}
					
					itemParagraph.setTextAlignment(TextAlignment.CENTER).setFixedLeading(15f)
							.setMarginBottom(0);

					menuContent.add(itemParagraph);

				}

				document.add(menuContent);
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			// ============ LAST PAGE ============
			bgHandler.setPageBackground(lastPageNum, lastBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ packageEntity.getUser().getUserBasicDetails().getCompanyName() + "/"
					+ packageEntity.getNameEnglish() + ".pdf";
			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}
}
