package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.controller.ExtraPaymentMasterController;
import com.crmportal.controller.UtilityController;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.NamePlateImagesEntity;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.NamePlateImagesRepository;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.MenuCategoryForTabkeMenuWithBgReportResponseDto;
import com.crmportal.response.dto.MenuItemForTableMenuWithBgReportResponseDto;
import com.crmportal.response.dto.NamePlateResponseDto;
import com.crmportal.response.dto.NamePlateTableMenuWithBgResponseDto;
import com.crmportal.response.dto.NameplateReportDataResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.NamePlateReportService;
import com.crmportal.service.NamePlateTableMenuWithBgService;
import com.crmportal.service.NameplateService;
import com.crmportal.utility.BackgroundEventHandler;
import com.crmportal.utility.TableMenuWithBgHeaderFooterEventHandler;
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
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
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
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.splitting.ISplitCharacters;

@Service
public class NamePlateReportServiceImpl implements NamePlateReportService {

	private final UtilityController utilityController;

	private final ExtraPaymentMasterController extraPaymentMasterController;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	NameplateService nameplateService;

	@Autowired
	Environment environment;

	@Autowired
	CommonService commonService;
	
	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	EventFunctionMenuAllocationServiceImpl menuAllocationServiceImpl;
	
	@Autowired
	NamePlateTableMenuWithBgService namePlateTableMenuWithBgService;
	
	@Autowired
	NamePlateImagesRepository namePlateImagesRepository;

	NamePlateReportServiceImpl(ExtraPaymentMasterController extraPaymentMasterController,
			UtilityController utilityController) {
		this.extraPaymentMasterController = extraPaymentMasterController;
		this.utilityController = utilityController;
	}

	public List<NamePlateResponseDto> getMenuIteams(Long eventId, Long eventFunctionId, int lang, Long userId) {

		List<NamePlateResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> result = menuPreparationDetailsRepository.getEventItems(eventId, lang, userId);

		for (Object[] row : result) {
			int index = 0;
			NamePlateResponseDto dto = new NamePlateResponseDto();
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventNo(commonService.getString(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setMenuPreparationId(commonService.getLong(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setMenuItemName(commonService.getString(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	private List<NamePlateResponseDto> getMenuCategories(Long eventId, int lang, Long userId) {
		List<NamePlateResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> result = menuPreparationDetailsRepository.getEventCategories(eventId, lang, userId);

		for (Object[] row : result) {
			int index = 0;
			NamePlateResponseDto dto = new NamePlateResponseDto();
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventNo(commonService.getString(row[index++]));
			dto.setCategoryId(commonService.getLong(row[index++]));
			dto.setCategoryName(commonService.getString(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	private List<NamePlateResponseDto> getMenuCategoryWiseItem(Long eventId, Long catId, int lang, Long userid) {
		List<NamePlateResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> result = menuPreparationDetailsRepository.getEventCategoryWiseItem(eventId, catId, lang, userid);

		for (Object[] row : result) {
			int index = 0;
			NamePlateResponseDto dto = new NamePlateResponseDto();
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventNo(commonService.getString(row[index++]));
			dto.setCategoryId(commonService.getLong(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setMenuItemName(commonService.getString(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	private NamePlateResponseDto getCmpData(Long eventId, Long userid, int lang) {

		Object result = menuPreparationDetailsRepository.getCmpData(eventId, userid, lang);

		if (result == null) {
			return null;
		}

		Object[] row = (Object[]) result;

		int index = 0;
		NamePlateResponseDto dto = new NamePlateResponseDto();
		dto.setCmpName(commonService.getString(row[index++]));
		dto.setCountryCode(commonService.getString(row[index++]));
		dto.setOfficeNo(commonService.getString(row[index++]));
		dto.setCmpEmail(commonService.getString(row[index++]));
		dto.setCmpAddress(commonService.getString(row[index++]));
		dto.setCmpLogo(commonService.getString(row[index++]));
		dto.setClientName(commonService.getString(row[index++]));
		dto.setClientAdderss(commonService.getString(row[index++]));
		dto.setClientNo(commonService.getString(row[index++]));
		dto.setOwnerFirstname(commonService.getString(row[index++]));
		dto.setOwnerLastname(commonService.getString(row[index++]));
		dto.setOwnerMobileNo(commonService.getString(row[index++]));

		return dto;
	}

	@Override
	public String counterNamePlateReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid) {

		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElseThrow(() -> new RuntimeException("Event not found with Id : " + eventId));
			
			Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
					lang, 1, 0, 0);

			
			if (eventDto == null) {
				return "";
			}
			String eventNo = eventDto.get("eventNo").toString();
			System.out.println("eventNo : " + eventNo);

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

			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), "Counter Name Plate") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(40, 20, 50, 20);

			float[] columnWidth = { 50f, 50f };
			Table itemTable = new Table(UnitValue.createPercentArray(columnWidth));
			itemTable.setWidth(UnitValue.createPercentValue(100));

			Paragraph itemPara = new Paragraph();
			Cell cell = new Cell();

			List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto
					.get("data");

			for (NameplateReportDataResponseDto dto : data) {
				String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty() ? ""
						: dto.getItemName();
				itemPara = new Paragraph()
						.add(new Text(itemNm).setFontSize(28).setFont(boldFont).setFontColor(ColorConstants.RED));

				for (int i = 0; i < dto.getItemCount().intValueExact(); i++) {
					cell = new Cell().add(itemPara).setPaddingTop(10f).setPaddingBottom(10f).setBorder(Border.NO_BORDER)
							.setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(cell);
				}
			}

			document.add(itemTable);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
					+ "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), "Counter Name Plate") + ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}

	}

	@Override
	public String customeNamePlateReportItem(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
	        Long userid, Integer isCompanyDetails, Integer columns, Integer items) {
	    try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        menuPreparationServiceImpl.loadLicense();
	        if (lang == 1) {
	            // Hindi
	            System.out.println("Loading Hindi font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
	            System.out.println("Hindi font loaded successfully");
	        } else if (lang == 2) {
	            // Gujarati
	            System.out.println("Loading Gujarati font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
	            System.out.println("Gujarati font loaded successfully");
	        } else {
	            // English
	            System.out.println("Loading English font...");
	            basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	            boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	            System.out.println("English font loaded successfully");
	        }
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, 1, 0, 0);

	        if (eventDto == null) {
	            return "";
	        }
	        
	        String eventNo = eventDto.get("eventNo").toString();

	        Date now = new Date();
	        String rootPath = re.getSession().getServletContext().getRealPath("/");
	        String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
	        File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);
	        if (!outputPath.exists()) {
	            if (outputPath.mkdirs()) {
	                System.out.println("Directory Created!!!");
	            } else {
	                System.out.println("Error!!!");
	            }
	        }
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");
	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
	        document.setMargins(10, 10, 10, 10);
	        
	        if(columns == 2 && items == 16) {
	        	document.setTopMargin(20f);
	        }
	        
	        float[] columnWidth = { 50f, 50f };
	        float[] columnWidth2 = { 100f };
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");
	        
	        int itemsPerPage = items;
	        int n = items / 2;
	        if(columns == 1) {
	        	n = items;
	        }
	        int extrsHeight = 0;
	        int currentItemCount = 0;
	        Table itemTable = null;
	        int fontSize = 30;
	        if(items == 12 || items == 6) {
	        	fontSize = 25;
	        	if(columns == 1) {
		        	fontSize = 27;
	        	}
	        } else if(items == 14 || items == 7) {
	        	fontSize = 23;
	        	if(columns == 1) {
		        	fontSize = 26;
	        	}
	        } else if(items == 16 || items == 8) {
	        	fontSize = 23;
	        	if(columns == 1) {
		        	fontSize = 26;
	        	}
	        } else if(items == 18 || items == 9) {
	        	fontSize = 23;
	        	extrsHeight = 2;
	        	if(columns == 1) {
		        	fontSize = 26;
	        	}
	        } 
	        
	        // Calculate page height and cell height for equal spacing
	        float pageHeight = pdfDocument.getDefaultPageSize().getHeight() - 20; // minus margins
	        float cellHeight = ((pageHeight / n) - n) + extrsHeight; // 6 rows per page (12 items / 2 columns)
	        
	        for (NameplateReportDataResponseDto dto : data) {
	            String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty() ? "" : dto.getItemName();
	            
	            Paragraph itemPara = new Paragraph()
	                    .add(new Text(itemNm).setFontSize(fontSize).setFont(basicFont).simulateBold());
	            itemPara.setMarginTop(0f).setMarginBottom(0f);
	            
	            for (int i = 0; i < dto.getItemCount().intValueExact(); i++) {
	                // Create new table if starting fresh or reached limit
	                if (itemTable == null || currentItemCount >= itemsPerPage) {
	                    // Add previous table if it exists
	                    if (itemTable != null) {
	                        document.add(itemTable);
	                    }
	                    
	                    itemTable = new Table(UnitValue.createPercentArray(columnWidth));
	                    if(columns == 1) {
		                    itemTable = new Table(UnitValue.createPercentArray(columnWidth2));	                    	
	                    }
	                    itemTable.setWidth(UnitValue.createPercentValue(100));
	                    currentItemCount = 0;
	                }
	                
	                Cell cell = new Cell()
	                        .add(itemPara)
	                        .setHeight(cellHeight)
	                        .setBorder(Border.NO_BORDER)
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
	                
	                itemTable.addCell(cell);
	                currentItemCount++;
	            }
	        }
	        
	        // Add the last table if it has items
	        if (itemTable != null && currentItemCount > 0) {
	            document.add(itemTable);
	        }
	        
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String counterNamePlateTwoLanguageReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid) {

		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
			
			Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			System.out.println(defaultLanguage + " --- " + preferedLanguage);
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}

			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}


			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
					lang, defaultLanguage, preferedLanguage, 1, 0, 0);
			String eventNo = eventDto.get("eventNo").toString();
			 System.out.println("eventNo 2:- "+eventNo);
			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo);

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			
			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElseThrow(() -> new RuntimeException("Event not found with Id : " + eventId));
			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), "Counter Name Plate") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(40, 20, 50, 20);

			float[] columnWidth = { 50f, 50f };
			Table itemTable = new Table(UnitValue.createPercentArray(columnWidth));
			itemTable.setWidth(UnitValue.createPercentValue(100));

			Paragraph itemPara = new Paragraph();
			Cell cell = new Cell();

			List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto
					.get("data");

			for (NameplateReportDataResponseDto dto : data) {
				String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty() ? ""
						: dto.getItemName();
				String itemNmPref = dto.getItemNamePref() == null || dto.getItemNamePref().isEmpty() ? ""
						: dto.getItemNamePref();
				itemPara = new Paragraph()
						.add(new Text(itemNm).setFontSize(28).setFont(boldFont).setFontColor(ColorConstants.RED))
						.add(new Text("\n"+itemNmPref).setFontSize(28).setFont(prefboldFont).setFontColor(ColorConstants.RED))
						.setPadding(0)
						.setMargin(0)
						.setKeepTogether(true);

				for (int i = 0; i < dto.getItemCount().intValueExact(); i++) {
					cell = new Cell().add(itemPara).setPaddingTop(10f).setPaddingBottom(10f).setBorder(Border.NO_BORDER)
							.setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(cell);
				}
			}

			document.add(itemTable);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
					+ "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), "Counter Name Plate") + ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}

	}

	
	@Override
	public String mainStandyMenuReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails) {

			try {

				PdfFont basicFont = null;
				PdfFont boldFont = null;
				menuPreparationServiceImpl.loadLicense();

				if (lang == 1) {
					// Hindi
					System.out.println("Loading Hindi font...");
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
					System.out.println("Hindi font loaded successfully");
				} else if (lang == 2) {
					// Gujarati
					System.out.println("Loading Gujarati font...");
					basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
					System.out.println("Gujarati font loaded successfully");
				} else {
					// English
					System.out.println("Loading English font...");
					basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
					boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
					System.out.println("English font loaded successfully");
				}

				PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

				Map<String, Object> eventDto = nameplateService.getNameplateCategoryOfItems(userid, eventId, eventFunctionId,
						lang, 0, 1, 0);
				String eventNo = eventDto.get("eventNo").toString();
				 System.out.println("eventNo 3:- "+eventNo);
				if (eventDto == null) {
					return "";
				}

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

				EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElseThrow(() -> new RuntimeException("Event not found with Id : " + eventId));
				File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
						formatDate(eventMasterEntity.getEventStartDateTime()), "Main Study Menu Report") + ".pdf");

				PdfWriter writer = new PdfWriter(pdfFile);
				PdfDocument pdfDocument = new PdfDocument(writer);
				Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
				document.setMargins(40, 20, 50, 20);

				NamePlateResponseDto cmpData = getCmpData(eventId, userid, lang);
				String cmpName = cmpData.getCmpName() == null || cmpData.getCmpName().isEmpty() ? "" : cmpData.getCmpName();
				String cmpAddress = cmpData.getCmpAddress() == null || cmpData.getCmpAddress().isEmpty() ? ""
						: cmpData.getCmpAddress();
				String cmpMobile = cmpData.getOfficeNo() == null || cmpData.getOfficeNo().isEmpty() ? ""
						: cmpData.getOfficeNo();
				String cmpEmail = cmpData.getCmpEmail() == null || cmpData.getCmpEmail().isEmpty() ? ""
						: cmpData.getCmpEmail();
				String cmpLogo = cmpData.getCmpLogo() == null || cmpData.getCmpLogo().isEmpty() ? "" : cmpData.getCmpLogo();
				
				ImageData img = null;
				try {
					img = menuPreparationServiceImpl.loadImageFromResource(environment.getProperty("app.image.url") + cmpLogo);
				} catch(Exception e) {				
					System.out.println("img : " + img);
				}
				
				
//				ImageData img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
				Image logo = null;
				if(img != null) {				
					logo = new Image(img);
					logo.setWidth(UnitValue.createPercentValue(100));
					logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
				}


				float[] columnWidth = { 25f, 75f };
				Table cmpTbl = new Table(UnitValue.createPercentArray(columnWidth));
				cmpTbl.setMarginBottom(20f);

				Paragraph cmpPara = new Paragraph().setMargin(0).setMultipliedLeading(1)
						.add(new Text(cmpName).setFontSize(22)
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
						.add(new Text("\n" + cmpAddress).setFontSize(14)
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
						.add(new Text("\nMobile No.: " + cmpMobile).setFontSize(14)
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)))
						.add(new Text("\nEmail: " + cmpEmail).setFontSize(14)
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
				
				Cell cmpLogoCell = null;
				if(logo != null) {				
					cmpLogoCell = new Cell().add(logo).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
							.setPaddingLeft(10f);
				} else {
					cmpLogoCell = new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
							.setPaddingLeft(10f);
				}
				

				Cell cmpCell = new Cell().add(cmpPara).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
						.setPaddingLeft(10f);

				cmpTbl.addCell(cmpLogoCell);
				cmpTbl.addCell(cmpCell);

//				if (isCompanyDetails == 1) {
					document.add(cmpTbl);
//				}

				List<NameplateReportDataResponseDto> reportData = (List<NameplateReportDataResponseDto>) eventDto.get("data");

				// Group by menuCategoryId while maintaining order
				Map<Long, List<NameplateReportDataResponseDto>> groupedCategories =
				        reportData.stream()
				                .collect(Collectors.groupingBy(
				                        NameplateReportDataResponseDto::getMenuCategoryId,
				                        LinkedHashMap::new,
				                        Collectors.toList()
				                ));
				
				Paragraph data = new Paragraph();

				for (Map.Entry<Long, List<NameplateReportDataResponseDto>> entry : groupedCategories.entrySet()) {
					Long catId = entry.getKey();
					NameplateReportDataResponseDto dto = entry.getValue().get(0); // take first for category name
					
					Map<String, Object> eventItemDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId,
							catId, lang, 0, 1, 0);
					
					List<NameplateReportDataResponseDto> eventItem = (List<NameplateReportDataResponseDto>) eventItemDto.get("data");
					
					if(eventItem.size() > 0) {					
						
						String cateNm = dto.getCategoryName() == null || dto.getCategoryName().isEmpty() ? ""
								: dto.getCategoryName();
						
						Div catBlock = new Div();
						catBlock.setKeepTogether(true);
						
						data = new Paragraph().add(new Text(cateNm).setFontSize(22).setFont(boldFont).setUnderline());
						data.setWidth(UnitValue.createPercentValue(100));
						data.setTextAlignment(TextAlignment.CENTER);
						data.setPadding(0);
						data.setMargin(0);
						catBlock.add(data);
						
						for (NameplateReportDataResponseDto item : eventItem) {
							Long itemId = item.getMenuItemId();
							String itemNm = item.getItemName() == null || item.getItemName().isEmpty() ? ""
									: item.getItemName();
							
							data = new Paragraph().add(new Text(itemNm).setFontSize(20).setFont(basicFont));
							data.setWidth(UnitValue.createPercentValue(100));
							data.setTextAlignment(TextAlignment.CENTER);
							data.setPadding(0);
							data.setMargin(0);
							
							catBlock.add(data);
						}
						
						document.add(catBlock);
					}

				}

				document.close();

				String scheme = re.getScheme();
				String serverName = re.getServerName();
				int serverPort = re.getServerPort();
				String contextPath = re.getContextPath();

				String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
						+ "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
								formatDate(eventMasterEntity.getEventStartDateTime()), "Main Study Menu Report") + ".pdf";

				return fullUrl;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to generate report");
			}

		}

//	@Override
//	public String tableMenuReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid, Integer tableMenuReport) {
//
//		try {
//
//			PdfFont basicFont = null;
//			PdfFont boldFont = null;
//			menuPreparationServiceImpl.loadLicense();
//
//			if (lang == 1) {
//				// Hindi
//				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
//				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
//				System.out.println("Hindi font loaded successfully");
//			} else if (lang == 2) {
//				// Gujarati
//				System.out.println("Loading Gujarati font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
//				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
//				System.out.println("Gujarati font loaded successfully");
//			} else {
//				// English
//				System.out.println("Loading English font...");
//				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
//				System.out.println("English font loaded successfully");
//			}
//
//			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//
//			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
//
//			System.out.println("userId : " + userid);
//			System.out.println("eventId : " + eventId);
//			System.out.println("eventFunctionId : " + eventFunctionId);
//			System.out.println("lang : " + lang);
//			System.out.println("isCounterNamePlate : " + 0);
//			System.out.println("isStandyNamePlate : " + 0);
//			System.out.println("isTableMenuNamePlate : " + 1);
//			Map<String, Object> eventDto = nameplateService.getNameplateCategoryOfItems(userid, eventId, eventFunctionId,
//					lang, 0, 0, 1);
//
//			String eventNo = eventDto.get("eventNo").toString();
//			Integer categoryFontSize = Integer.parseInt(eventDto.get("category_font_size").toString());
//			
//			if (eventDto == null) {
//				return "";
//			}
//
//			Date now = new Date();
//			String rootPath = re.getSession().getServletContext().getRealPath("/");
//			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
//			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo + "/");
//
//			if (!outputPath.exists()) {
//				if (outputPath.mkdirs()) {
//					System.out.println("Directory Created!!!");
//				} else {
//					System.out.println("Error!!!");
//				}
//			}
//			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElseThrow(() -> new RuntimeException("Event not found with Id : " + eventId));
//			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
//					formatDate(eventMasterEntity.getEventStartDateTime()), "Table Menu Report") + ".pdf");
//
//			PdfWriter writer = new PdfWriter(pdfFile);
//			PdfDocument pdfDocument = new PdfDocument(writer);
//			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
//			document.setMargins(40, 20, 50, 20);
//
//			NamePlateResponseDto cmpData = getCmpData(eventId, userid, lang);
//			String cmpName = cmpData.getCmpName() == null || cmpData.getCmpName().isEmpty() ? "" : cmpData.getCmpName();
//			String cmpAddress = cmpData.getCmpAddress() == null || cmpData.getCmpAddress().isEmpty() ? ""
//					: cmpData.getCmpAddress();
//			String cmpMobile = cmpData.getOfficeNo() == null || cmpData.getOfficeNo().isEmpty() ? ""
//					: cmpData.getOfficeNo();
//			String cmpEmail = cmpData.getCmpEmail() == null || cmpData.getCmpEmail().isEmpty() ? ""
//					: cmpData.getCmpEmail();
//			String cmpLogo = cmpData.getCmpLogo() == null || cmpData.getCmpLogo().isEmpty() ? "" : cmpData.getCmpLogo();
//			String clientName = cmpData.getClientName() == null || cmpData.getClientName().isEmpty() ? ""
//					: cmpData.getClientName();
//			String clientNo = cmpData.getClientNo() == null || cmpData.getClientNo().isEmpty() ? ""
//					: cmpData.getClientNo();
//			String ownerFirstName = cmpData.getOwnerFirstname() == null || cmpData.getOwnerFirstname().isEmpty() ? ""
//					: cmpData.getOwnerFirstname();
//			String ownerLastName = cmpData.getOwnerLastname() == null || cmpData.getOwnerLastname().isEmpty() ? ""
//					: cmpData.getOwnerLastname();
//			String ownerMobile = cmpData.getOwnerMobileNo() == null || cmpData.getOwnerMobileNo().isEmpty() ? ""
//					: cmpData.getOwnerMobileNo();
//
//			ImageData img = null;
//			
//			try {
//				img = menuPreparationServiceImpl.loadImageFromResource(environment.getProperty("app.image.url") + cmpLogo);
//			} catch(Exception e) {				
//				System.out.println("img : " + img);
//			}
//			
//			final PdfFont finalBoldFont = boldFont;
//			final ImageData finalImg = img;
//			final String finalClientName = ownerFirstName + " " + ownerLastName;
//			final String finalClientNo = ownerMobile;
//
//			Color headingFontColor = new DeviceRgb(70, 115, 37);
//			Color catBackColor = new DeviceRgb(227, 241, 216);
//
//			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new AbstractPdfDocumentEventHandler() {
//				@Override
//				protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
//					PdfDocument pdfDoc = event.getDocument();
//					PdfPage page = ((PdfDocumentEvent) event).getPage();
//					PdfCanvas canvas = new PdfCanvas(page);
//
//					Rectangle pageSize = page.getPageSize();
//					float pageWidth = pageSize.getWidth();
//
//					// Create a temporary canvas for header
//					Canvas headerCanvas = new Canvas(canvas, pageSize);
//
//					try {
//						Image logo = null;
//						if(finalImg != null) {							
//							logo = new Image(finalImg);
//							logo.setWidth(UnitValue.createPercentValue(100));
//						}
//
//						float[] columnWidth = { 35f, 30f, 35f };
//						Table headerTable = new Table(UnitValue.createPercentArray(columnWidth));
//						headerTable.setWidth(pageWidth - 40);
//						headerTable.setFixedPosition(20, pageSize.getTop() - 100, pageWidth - 40);
//
//						Paragraph cmpPara = new Paragraph().add(new Text(finalClientName).setFontSize(22)
//								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(headingFontColor));
//						Paragraph cmpPara2 = new Paragraph().add(new Text(finalClientNo).setFontSize(22)
//								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(headingFontColor));
//
//						Cell cmpCell = new Cell().add(cmpPara).setTextAlignment(TextAlignment.CENTER)
//								.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
//								.setPaddingLeft(10f);
//
//						Cell cmpLogoCell = null;
//						if(logo != null) {							
//							cmpLogoCell = new Cell().add(logo).setTextAlignment(TextAlignment.CENTER)
//									.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
//									.setPaddingLeft(10f);
//						} else {
//							cmpLogoCell = new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER)
//									.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
//									.setPaddingLeft(10f);
//						}
//
//						Cell cmpCell2 = new Cell().add(cmpPara2).setTextAlignment(TextAlignment.CENTER)
//								.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
//								.setPaddingLeft(10f);
//
//						headerTable.addCell(cmpCell);
//						headerTable.addCell(cmpLogoCell);
//						headerTable.addCell(cmpCell2);
//
//						headerCanvas.add(headerTable);
//						headerCanvas.close();
//
//						// Draw horizontal line below the header
//						canvas.saveState();
//						canvas.setStrokeColor(ColorConstants.RED); // Change color as needed
//						canvas.setLineWidth(1); // Change thickness as needed
//						float lineY = pageSize.getTop() - 110; // Position just below header (adjust as needed)
//						canvas.moveTo(20, lineY); // Start from left margin
//						canvas.lineTo(pageWidth - 20, lineY); // End at right margin
//						canvas.stroke();
//						canvas.restoreState();
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//
//			// Get page dimensions
//			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();
//			float pageHeight = document.getPdfDocument().getDefaultPageSize().getHeight();
//			float leftMargin = 20; // Same as document margin
//			float rightMargin = 20;
//			float topMargin = 30;
//			float bottomMargin = 30;
//			float columnGap = 20;
//
//			// Calculate column widths
//			float availableWidth = pageWidth - leftMargin - rightMargin;
//			float columnWidth2 = (availableWidth - columnGap) / 2;
//
//			// Calculate starting Y position (below the header table)
//			float currentY = pageHeight - topMargin - 100; // Adjust 100 based on your header height
//
//			// Define column rectangles starting from current position
//			Rectangle leftColumnRect = new Rectangle(leftMargin + 10f, bottomMargin, columnWidth2 - 20f,
//					currentY - bottomMargin);
//			Rectangle rightColumnRect = new Rectangle(leftMargin + columnWidth2 + columnGap + 10f, bottomMargin,
//					columnWidth2 - 20f, currentY - bottomMargin);
//
//			// Set column renderer ONCE before the loop
//			document.setRenderer(
//					new ColumnDocumentRenderer(document, new Rectangle[] { leftColumnRect, rightColumnRect }));
//
//			// Add event handler to draw vertical dashed line between columns on each page
//			final float separatorX = leftMargin + columnWidth2 + (columnGap / 2);
//			final float finalTopMargin = topMargin;
//			final float finalBottomMargin = bottomMargin;
//
//			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new AbstractPdfDocumentEventHandler() {
//				@Override
//				protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
//					PdfPage page = ((PdfDocumentEvent) event).getPage();
//					PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(),
//							page.getDocument());
//					Rectangle pageSize = page.getPageSize();
//
//					// Draw vertical dashed red line
//					canvas.saveState();
//					canvas.setStrokeColor(ColorConstants.RED);
//					canvas.setLineWidth(2);
//					canvas.setLineDash(5, 5); // 5 pixels dash, 5 pixels gap
//					canvas.moveTo(separatorX, finalBottomMargin);
//					canvas.lineTo(separatorX, pageSize.getTop() - 110); // Stop before header
//					canvas.stroke();
//					canvas.restoreState();
//				}
//			});
//
//			Paragraph data = new Paragraph();
//
//			List<NameplateReportDataResponseDto> reportData = (List<NameplateReportDataResponseDto>) eventDto
//					.get("data");
//
//			for (NameplateReportDataResponseDto dto : reportData) {
//				Long catId = dto.getMenuCategoryId();
//				Map<String, Object> eventItemDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId,
//						catId, lang, 0, 0, 1);
//				
//				List<NameplateReportDataResponseDto> eventItem = (List<NameplateReportDataResponseDto>) eventItemDto
//						.get("data");
//				Integer itemFontSize = Integer.parseInt(eventItemDto.get("item_font_size").toString());
//				
//				if(eventItem.size() > 0) {					
//					
//					String cateNm = dto.getCategoryName() == null || dto.getCategoryName().isEmpty() ? ""
//							: dto.getCategoryName();
//					
//					Div catBlock = new Div();
//					catBlock.setKeepTogether(true);
//					
//					data = new Paragraph().add(new Text(":: " + cateNm + " ::").setFontSize(categoryFontSize).setFont(boldFont));
//					data.setWidth(UnitValue.createPercentValue(100));
//					data.setTextAlignment(TextAlignment.CENTER);
//					data.setPadding(10f);
//					data.setBorderRadius(new BorderRadius(10f));
//					data.setBackgroundColor(catBackColor);
//					data.setMargin(0);
//					catBlock.add(data);
//					
//					
//					for (NameplateReportDataResponseDto item : eventItem) {
//						Long itemId = item.getMenuItemId();
//						String itemNm = item.getItemName() == null || item.getItemName().isEmpty() ? ""
//								: item.getItemName();
//						
//						data = new Paragraph().add(
//								new Text("•" + itemNm).setFontSize(itemFontSize).setFont(basicFont).setFontColor(headingFontColor));
//						data.setWidth(UnitValue.createPercentValue(100));
//						data.setTextAlignment(TextAlignment.LEFT);
//						data.setPadding(0);
//						data.setMargin(0);
//						
//						catBlock.add(data);
//					}
//					
//					document.add(catBlock);
//				}
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
//			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/" +
//					getReportName(eventMasterEntity.getParty().getNameEnglish(),
//							formatDate(eventMasterEntity.getEventStartDateTime()), "Table Menu Report")+ ".pdf";
//
//			return fullUrl;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Failed to generate report");
//		}
//
//	}
	
	@Override
	public String tableMenuReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid, Integer tableMenuReport) {

		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			Map<String, Object> eventDto = nameplateService.getNameplateCategoryOfItems(userid, eventId, eventFunctionId,
					lang, 0, 0, 1);
			if (eventDto == null) {
				return "";
			}
			
			String eventNo = eventDto.get("eventNo").toString();
			System.out.println("eventNo 4:- "+eventNo);
			Integer categoryFontSize = Integer.parseInt(eventDto.get("category_font_size").toString());
			
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
			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElseThrow(() -> new RuntimeException("Event not found with Id : " + eventId));
			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), "Table Menu Report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(40, 20, 50, 20);

			NamePlateResponseDto cmpData = getCmpData(eventId, userid, lang);
			String cmpName = cmpData.getCmpName() == null || cmpData.getCmpName().isEmpty() ? "" : cmpData.getCmpName();
			String cmpAddress = cmpData.getCmpAddress() == null || cmpData.getCmpAddress().isEmpty() ? ""
					: cmpData.getCmpAddress();
			String cmpMobile = cmpData.getOfficeNo() == null || cmpData.getOfficeNo().isEmpty() ? ""
					: cmpData.getOfficeNo();
			String cmpEmail = cmpData.getCmpEmail() == null || cmpData.getCmpEmail().isEmpty() ? ""
					: cmpData.getCmpEmail();
			String cmpLogo = cmpData.getCmpLogo() == null || cmpData.getCmpLogo().isEmpty() ? "" : cmpData.getCmpLogo();
			String clientName = cmpData.getClientName() == null || cmpData.getClientName().isEmpty() ? ""
					: cmpData.getClientName();
			String clientNo = cmpData.getClientNo() == null || cmpData.getClientNo().isEmpty() ? ""
					: cmpData.getClientNo();
			String ownerFirstName = cmpData.getOwnerFirstname() == null || cmpData.getOwnerFirstname().isEmpty() ? ""
					: cmpData.getOwnerFirstname();
			String ownerLastName = cmpData.getOwnerLastname() == null || cmpData.getOwnerLastname().isEmpty() ? ""
					: cmpData.getOwnerLastname();
			String ownerMobile = cmpData.getOwnerMobileNo() == null || cmpData.getOwnerMobileNo().isEmpty() ? ""
					: cmpData.getOwnerMobileNo();

			ImageData img = null;
			
			try {
				img = menuPreparationServiceImpl.loadImageFromResource(environment.getProperty("app.image.url") + cmpLogo);
//				img = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			} catch(Exception e) {				
				System.out.println("img : " + img);
			}
			
			final PdfFont finalBoldFont = boldFont;
			final ImageData finalImg = img;
			final String finalClientName = ownerFirstName + " " + ownerLastName;
			final String finalClientNo = ownerMobile;

			Color headingFontColor = new DeviceRgb(70, 115, 37);
			Color catBackColor = new DeviceRgb(227, 241, 216);

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new AbstractPdfDocumentEventHandler() {
				@Override
				protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
					PdfDocument pdfDoc = event.getDocument();
					PdfPage page = ((PdfDocumentEvent) event).getPage();
					PdfCanvas canvas = new PdfCanvas(page);

					Rectangle pageSize = page.getPageSize();
					float pageWidth = pageSize.getWidth();

					// Create a temporary canvas for header
					Canvas headerCanvas = new Canvas(canvas, pageSize);

					try {
						Image logo = null;
						if(finalImg != null) {							
							logo = new Image(finalImg);
//							logo.setWidth(UnitValue.createPercentValue(100));
							logo.scaleToFit(100f, 100f);
							logo.setTextAlignment(TextAlignment.CENTER);
							logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
						}

						float[] columnWidth = { 35f, 30f, 35f };
						Table headerTable = new Table(UnitValue.createPercentArray(columnWidth));
						headerTable.setWidth(pageWidth - 40);
						headerTable.setFixedPosition(20, pageSize.getTop() - 100, pageWidth - 40);

						Paragraph cmpPara = new Paragraph().add(new Text(finalClientName).setFontSize(22)
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(headingFontColor));
						Paragraph cmpPara2 = new Paragraph().add(new Text(finalClientNo).setFontSize(22)
								.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(headingFontColor));

						Cell cmpCell = new Cell().add(cmpPara).setTextAlignment(TextAlignment.CENTER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setBorder(Border.NO_BORDER)
								.setPaddingLeft(10f);

						Cell cmpLogoCell = null;
						if(logo != null) {							
							cmpLogoCell = new Cell().add(logo).setTextAlignment(TextAlignment.CENTER)
									.setVerticalAlignment(VerticalAlignment.MIDDLE)
									.setBorder(Border.NO_BORDER)
									.setPaddingLeft(10f);
						} else {
							cmpLogoCell = new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER)
									.setVerticalAlignment(VerticalAlignment.MIDDLE)
									.setBorder(Border.NO_BORDER)
									.setPaddingLeft(10f);
						}

						Cell cmpCell2 = new Cell().add(cmpPara2).setTextAlignment(TextAlignment.CENTER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE)
								.setBorder(Border.NO_BORDER)
								.setPaddingLeft(10f);

						headerTable.addCell(cmpCell);
						headerTable.addCell(cmpLogoCell);
						headerTable.addCell(cmpCell2);
						
						String headerNotes = "";
						if(lang == 1) {
							headerNotes = eventDto.get("headerNotesHindi") != null ? (String)eventDto.get("headerNotesHindi") : "";
						}else if(lang == 2) {
							headerNotes = eventDto.get("headerNotesGujarati") != null ? (String)eventDto.get("headerNotesGujarati") : "";
						}else {
							headerNotes = eventDto.get("headerNotesEnglish") != null ? (String)eventDto.get("headerNotesEnglish") : "";
						}

						if(headerNotes != "" && headerNotes.trim().length() != 0) {
							Cell cell = new Cell(1, 3)
									 		.add(new Paragraph(headerNotes)
									 				.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
									 				.setFontSize(14f)
									 				.setTextAlignment(TextAlignment.CENTER))
									 		.setBorder(Border.NO_BORDER);
									 
							System.out.println("header : " + headerNotes);
							headerTable.addCell(cell);
						}
						headerCanvas.add(headerTable);
						headerCanvas.close();

						// Draw horizontal line below the header
						canvas.saveState();
						canvas.setStrokeColor(ColorConstants.RED); // Change color as needed
						canvas.setLineWidth(1); // Change thickness as needed
						float lineY = pageSize.getTop() - 110; // Position just below header (adjust as needed)
						canvas.moveTo(20, lineY); // Start from left margin
						canvas.lineTo(pageWidth - 20, lineY); // End at right margin
						canvas.stroke();
						canvas.restoreState();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// Get page dimensions
			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();
			float pageHeight = document.getPdfDocument().getDefaultPageSize().getHeight();
			float leftMargin = 20; // Same as document margin
			float rightMargin = 20;
			float topMargin = 30;
			float bottomMargin = 65;
			float columnGap = 20;

			// Calculate column widths
			float availableWidth = pageWidth - leftMargin - rightMargin;
			float columnWidth2 = (availableWidth - columnGap) / 2;

			// Calculate starting Y position (below the header table)
			float currentY = pageHeight - topMargin - 100; // Adjust 100 based on your header height

			// Define column rectangles starting from current position
			Rectangle leftColumnRect = new Rectangle(leftMargin + 10f, bottomMargin, columnWidth2 - 20f,
					currentY - bottomMargin);
			Rectangle rightColumnRect = new Rectangle(leftMargin + columnWidth2 + columnGap + 10f, bottomMargin,
					columnWidth2 - 20f, currentY - bottomMargin);

			// Set column renderer ONCE before the loop
			document.setRenderer(
					new ColumnDocumentRenderer(document, new Rectangle[] { leftColumnRect, rightColumnRect }));

			// Add event handler to draw vertical dashed line between columns on each page
			final float separatorX = leftMargin + columnWidth2 + (columnGap / 2);
			final float finalTopMargin = topMargin;
			final float finalBottomMargin = bottomMargin;

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new AbstractPdfDocumentEventHandler() {
				@Override
				protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
					PdfPage page = ((PdfDocumentEvent) event).getPage();
					PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(),
							page.getDocument());
					Rectangle pageSize = page.getPageSize();

					// Draw vertical dashed red line
					canvas.saveState();
					canvas.setStrokeColor(ColorConstants.RED);
					canvas.setLineWidth(2);
					canvas.setLineDash(5, 5); // 5 pixels dash, 5 pixels gap
					canvas.moveTo(separatorX, finalBottomMargin);
					canvas.lineTo(separatorX, pageSize.getTop() - 110); // Stop before header
					canvas.stroke();
					canvas.restoreState();
				}
			});

			Paragraph data = new Paragraph();

			List<NameplateReportDataResponseDto> reportData = (List<NameplateReportDataResponseDto>) eventDto
					.get("data");
			
			// Group by menuCategoryId
			Map<Long, List<NameplateReportDataResponseDto>> groupedCategories =
			        reportData.stream()
			                .collect(Collectors.groupingBy(
			                        NameplateReportDataResponseDto::getMenuCategoryId,
			                        LinkedHashMap::new,   // keeps order
			                        Collectors.toList()
			                ));

			
			for (Map.Entry<Long, List<NameplateReportDataResponseDto>> entry : groupedCategories.entrySet()) {

			    Long catId = entry.getKey();
			    NameplateReportDataResponseDto dto = entry.getValue().get(0); // take first for category name

			    Map<String, Object> eventItemDto =
			            nameplateService.getNameplateItemsWithCategory(
			                    userid, eventId, eventFunctionId,
			                    catId, lang, 0, 0, 1
			            );

			    List<NameplateReportDataResponseDto> eventItem =
			            (List<NameplateReportDataResponseDto>) eventItemDto.get("data");

			    Integer itemFontSize =
			            Integer.parseInt(eventItemDto.get("item_font_size").toString());

			    if (eventItem != null && !eventItem.isEmpty()) {

			        String cateNm = dto.getCategoryName() == null ? "" : dto.getCategoryName();

			        Div catBlock = new Div();
			        catBlock.setKeepTogether(true);

			        Paragraph categoryPara = new Paragraph()
			                .add(new Text(":: " + cateNm + " ::")
			                        .setFontSize(categoryFontSize)
			                        .setFont(boldFont));

			        categoryPara.setTextAlignment(TextAlignment.CENTER);
			        categoryPara.setPadding(10f);
			        categoryPara.setBorderRadius(new BorderRadius(10f));
			        categoryPara.setBackgroundColor(catBackColor);
			        categoryPara.setMargin(0);

			        catBlock.add(categoryPara);

			        for (NameplateReportDataResponseDto item : eventItem) {

			            String itemNm = item.getItemName() == null ? "" : item.getItemName();

			            Paragraph itemPara = new Paragraph()
			                    .add(new Text("• " + itemNm)
			                            .setFontSize(itemFontSize)
			                            .setFont(basicFont)
			                            .setFontColor(headingFontColor));

			            itemPara.setTextAlignment(TextAlignment.LEFT);
			            itemPara.setMargin(0);

			            catBlock.add(itemPara);
			        }

			        document.add(catBlock);
			    }
			}

			String footerNotes = "";
			if(lang == 1) {
				footerNotes = eventDto.get("footerNotesHindi") != null ? (String)eventDto.get("footerNotesHindi") : "";
			}else if(lang == 2) {
				footerNotes = eventDto.get("footerNotesGujarati") != null ? (String)eventDto.get("footerNotesGujarati") : "";
			}else {
				footerNotes = eventDto.get("footerNotesEnglish") != null ? (String)eventDto.get("footerNotesEnglish") : "";
			}
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {

			    PdfPage page = pdfDocument.getPage(i);
			    Rectangle pageSize = page.getPageSize();

			    float margin = 20;
			    float footerHeight = 60; // space reserved for footer

			    Rectangle footerRect = new Rectangle(
			            margin,
			            10,
			            pageSize.getWidth() - (margin * 2),
			            footerHeight
			    );

			    Canvas canvas = new Canvas(new PdfCanvas(page), footerRect);

			    Paragraph footerPara = new Paragraph(footerNotes)
			            .setFont(boldFont)
			            .setFontSize(14f)
			            .setTextAlignment(TextAlignment.CENTER);

			    canvas.add(footerPara);
			    canvas.close();
			}

			document.close();

			String scheme = re.getScheme();
			String serverName = re.getServerName();
			int serverPort = re.getServerPort();
			String contextPath = re.getContextPath();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/" +
					getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), "Table Menu Report")+ ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}

	}

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}
	
	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + fileSafe(date.replace("/", "_") + " (" + type.toUpperCase() + ")");
	}

	public String formatDate(LocalDateTime dateTime) {
	    return dateTime == null ? "" :
	            dateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
	}
	
	@Override
	public String counterNamePlateWithLogo(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
	        Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate) {
	    try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        menuPreparationServiceImpl.loadLicense();
	        if (lang == 1) {
	            // Hindi
	            System.out.println("Loading Hindi font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
	            System.out.println("Hindi font loaded successfully");
	        } else if (lang == 2) {
	            // Gujarati
	            System.out.println("Loading Gujarati font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
	            System.out.println("Gujarati font loaded successfully");
	        } else {
	            // English
	            System.out.println("Loading English font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Cambria.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Cambria-Bold.ttf");
	            System.out.println("English font loaded successfully");
	        }
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        
	        NamePlateResponseDto namePlateDto = getCmpData(eventId, userid, lang);
	        
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, 1, 0, 0);
	        if (eventDto == null || ((List<?>)eventDto.get("data")).isEmpty()) {
	            return "";
	        }
	        String eventNo = eventDto.get("eventNo").toString();
	        System.out.println("event dto : " + eventDto.toString());
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");
	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
	        document.setMargins(10, 10, 5, 10);
	        
	        float[] columnWidth = { 50f, 50f };
	        float[] columnWidth2 = { 100f };
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");
	        
	        int itemsPerPage = 6;
	        int n = 3;
	        int extrsHeight = 0;
	        int currentItemCount = 0;
	        Table itemTable = null;
	        int fontSize = 30;
	        
	        // Calculate page height and cell height for equal spacing
	        float pageHeight = pdfDocument.getDefaultPageSize().getHeight() - 20;
	        float cellHeight = ((pageHeight / n) - n) + extrsHeight;
	        
	        ImageData logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + namePlateDto.getCmpLogo());
//					.loadImageFromResource("/flipbook/pages/shreehari_logo.png");
			Image logo = new Image(logoData);

			// Resize & align
			logo.scaleToFit(100, 50);
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
			
			String cmpName = namePlateDto.getCmpName();
			if(adminTemplate.getTemplateMaster().getNamePlateBg() != null) {
				ImageData watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/demo.png");
	
				BackgroundEventHandler bgHandler = new BackgroundEventHandler();
				bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
				pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			}
			Cell logoCell = new Cell().add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)).setPaddingTop(5f);
	        
	        Table innerTable = null;
	        for (NameplateReportDataResponseDto dto : data) {
	            String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty() ? "" : dto.getItemName();
	            
	            Paragraph itemPara = new Paragraph()
	                    .add(new Text(itemNm).setFontSize(fontSize).setFont(basicFont).simulateBold());
	            itemPara.setMarginTop(0f).setMarginBottom(0f);
	            
	            for (int i = 0; i < dto.getItemCount().intValueExact(); i++) {
	            	innerTable = new Table(UnitValue.createPercentArray(new float[] {30f, 70f}));
	            	innerTable.setWidth(UnitValue.createPercentValue(100f));
	            	
	                // Create new table if starting fresh or reached limit
	                if (itemTable == null || currentItemCount >= itemsPerPage) {
	                    // Add previous table if it exists
	                    if (itemTable != null) {
	                        document.add(itemTable);
	                    }
	                    
	                    itemTable = new Table(UnitValue.createPercentArray(columnWidth));
	                    itemTable.setWidth(UnitValue.createPercentValue(100f));
	                    currentItemCount = 0;
	                }

	                Cell cell = new Cell(1, 2)
	                        .add(itemPara)
	                        .setHeight(cellHeight - 80f)
	                        .setBorder(Border.NO_BORDER)
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
	                        .setSplitCharacters(new ISplitCharacters() {
								@Override
								public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
									return true;
								}
							});
	                innerTable.addCell(cell);
	                
	                innerTable.addCell(logoCell);
	                
	                cell = new Cell().add(new Paragraph(cmpName))
	                		 	 	 	 	.setVerticalAlignment(VerticalAlignment.MIDDLE)
	                		 	 	 	 	.setTextAlignment(TextAlignment.CENTER)
	                		 	 	 	    .setBorder(Border.NO_BORDER)
	                		 	 	 	    .setBorderTop(new SolidBorder(1f))
	                		 	 	 	    .setPadding(10f)
	                		 	 	 	    .setFontSize(22f)
	                		 	 	 	    .setHeight(80f);
	                innerTable.addCell(cell);
	                
	                itemTable.addCell(new Cell().add(innerTable)
	                		 				.setHeight(cellHeight)
	                		 				.setPaddingLeft(10f)
	                		 				.setPaddingRight(10f)
	                		 				.setBorder(Border.NO_BORDER)
	                		 				.setTextAlignment(TextAlignment.CENTER))
	                 						.setFont(basicFont)
	                 						.simulateBold();
	                currentItemCount++;
	            }
	        }
	        
	        // Add the last table if it has items
	        if (itemTable != null && currentItemCount > 0) {
	            document.add(itemTable);
	        }
	        
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String counterNamePlateWithLogo1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
	        Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate) {
	    try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        menuPreparationServiceImpl.loadLicense();
	        if (lang == 1) {
	            // Hindi
	            System.out.println("Loading Hindi font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
	            System.out.println("Hindi font loaded successfully");
	        } else if (lang == 2) {
	            // Gujarati
	            System.out.println("Loading Gujarati font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
	            System.out.println("Gujarati font loaded successfully");
	        } else {
	            // English
	            System.out.println("Loading English font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Cambria.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Cambria-Bold.ttf");
	            System.out.println("English font loaded successfully");
	        }
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        
	        NamePlateResponseDto namePlateDto = getCmpData(eventId, userid, lang);
	        
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, 1, 0, 0);
	        if (eventDto == null || ((List<?>)eventDto.get("data")).isEmpty()) {
	            return "";
	        }
	        String eventNo = eventDto.get("eventNo").toString();
	        System.out.println("event dto : " + eventDto.toString());
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");
	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
	        document.setMargins(10, 10, 5, 10);
	        
	        float[] columnWidth = { 50f, 50f };
	        float[] columnWidth2 = { 100f };
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");
	        
	        int itemsPerPage = 6;
	        int n = 3;
	        int extrsHeight = 0;
	        int currentItemCount = 0;
	        Table itemTable = null;
	        int fontSize = 30;
	        
	        // Calculate page height and cell height for equal spacing
	        float pageHeight = pdfDocument.getDefaultPageSize().getHeight() - 20;
	        float cellHeight = ((pageHeight / n) - n) + extrsHeight;
	        
	        ImageData logoData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + namePlateDto.getCmpLogo());
//					.loadImageFromResource("/flipbook/pages/shreehari_logo.png");
			Image logo = new Image(logoData);

			// Resize & align
			logo.scaleToFit(100, 70);
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
			
			String cmpName = namePlateDto.getCmpName();
			if(adminTemplate.getTemplateMaster().getNamePlateBg() != null) {
				ImageData watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/demo.png");
	
				BackgroundEventHandler bgHandler = new BackgroundEventHandler();
				bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
				pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			}
			Cell logoCell = new Cell().add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)).setPaddingTop(5f);
	        
	        Table innerTable = null;
	        for (NameplateReportDataResponseDto dto : data) {
	            String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty() ? "" : dto.getItemName();
	            
	            Paragraph itemPara = new Paragraph()
	                    .add(new Text(itemNm + "123 4568 789 321 6543 57894 000").setFontSize(fontSize).setFont(basicFont).simulateBold());
	            itemPara.setMarginTop(0f).setMarginBottom(0f);
	            
	            for (int i = 0; i < dto.getItemCount().intValueExact(); i++) {
	            	innerTable = new Table(UnitValue.createPercentArray(new float[] {30f, 70f}));
	            	innerTable.setWidth(UnitValue.createPercentValue(100f));
	            	
	                // Create new table if starting fresh or reached limit
	                if (itemTable == null || currentItemCount >= itemsPerPage) {
	                    // Add previous table if it exists
	                    if (itemTable != null) {
	                        document.add(itemTable);
	                    }
	                    
	                    itemTable = new Table(UnitValue.createPercentArray(columnWidth));
	                    itemTable.setWidth(UnitValue.createPercentValue(100f));
	                    currentItemCount = 0;
	                }

	                Cell cell = new Cell(1, 2)
	                        .add(itemPara)
	                        .setHeight(cellHeight - 100f)
	                        .setBorder(Border.NO_BORDER)
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
	                        .setSplitCharacters(new ISplitCharacters() {
								@Override
								public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
									return true;
								}
							});
	                innerTable.addCell(cell);
	                
	                innerTable.addCell(logoCell);
	                
	                cell = new Cell().add(new Paragraph("Shree Hari Caterers"))
	                		 	 	 	 	.setVerticalAlignment(VerticalAlignment.MIDDLE)
	                		 	 	 	 	.setTextAlignment(TextAlignment.CENTER)
	                		 	 	 	    .setBorder(Border.NO_BORDER)
	                		 	 	 	    .setBorderTop(new SolidBorder(1f))
	                		 	 	 	    .setPadding(10f)
	                		 	 	 	    .setFontSize(22f)
	                		 	 	 	    .setHeight(100f);
	                innerTable.addCell(cell);
	                
	                itemTable.addCell(new Cell().add(innerTable)
	                		 				.setHeight(cellHeight)
	                		 				.setPaddingLeft(10f)
	                		 				.setPaddingRight(10f)
	                		 				.setBorder(Border.NO_BORDER)
	                		 				.setTextAlignment(TextAlignment.CENTER))
	                 						.setFont(basicFont)
	                 						.simulateBold();
	                currentItemCount++;
	            }
	        }
	        
	        // Add the last table if it has items
	        if (itemTable != null && currentItemCount > 0) {
	            document.add(itemTable);
	        }
	        
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	

	@Override
	public String tableMenuReportWithBg(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid, Integer tableMenuReport,
			AdminTemplateModuleResponseDto adminTemplate) {
		try {
			
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			Map<String, Object> eventDto = namePlateTableMenuWithBgService.getNameplateItems(userid, eventId, eventFunctionId, lang);

			if (eventDto == null || ((List<?>)eventDto.get("data")).isEmpty()) {
				return "";
			}

			String eventNo = eventDto.get("eventNo").toString();
			
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
			EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElseThrow(() -> new RuntimeException("Event not found with Id : " + eventId));
			File pdfFile = new File(outputPath + "/" + getReportName(eventMasterEntity.getParty().getNameEnglish(),
					formatDate(eventMasterEntity.getEventStartDateTime()), "Table Menu Report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A5.rotate());
			document.setMargins(40, 20, 50, 20);
			
			Color itemBlackColor = new DeviceRgb(0, 0, 0);
			Color catRedColor = new DeviceRgb(191, 2, 2);
			
			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getNamePlateCoverBg());
//					.loadImageFromResource("/flipbook/pages/name_plate_cover_page.png");
			
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getNamePlateBg());
//					.loadImageFromResource("/flipbook/pages/name_plate_bg.png");
					
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData);
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			String footerNotes = "";
			String headerNotes = "";
			if(lang == 1) {
				footerNotes = eventDto.get("footerNotesHindi") != null ? (String)eventDto.get("footerNotesHindi") : "";
				headerNotes = eventDto.get("headerNotesHindi") != null ? (String)eventDto.get("headerNotesHindi") : "";
			}else if(lang == 2) {
				footerNotes = eventDto.get("footerNotesGujarati") != null ? (String)eventDto.get("footerNotesGujarati") : "";
				headerNotes = eventDto.get("headerNotesGujarati") != null ? (String)eventDto.get("headerNotesGujarati") : "";
			}else {
				footerNotes = eventDto.get("footerNotesEnglish") != null ? (String)eventDto.get("footerNotesEnglish") : "";
				headerNotes = eventDto.get("headerNotesEnglish") != null ? (String)eventDto.get("headerNotesEnglish") : "";
			}
			
			pdfDocument.addEventHandler(
			        PdfDocumentEvent.END_PAGE,
			        new TableMenuWithBgHeaderFooterEventHandler(headerNotes, footerNotes, boldFont)
			);
			
			// Add invisible content to ensure page 1 exists
			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(basicFont).setFontColor(itemBlackColor);
			invisibleContent.setFontSize(35);
			invisibleContent.setPaddingTop(140f);
			invisibleContent.setPaddingLeft(165f);
			document.add(invisibleContent);
			
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			
			// Get page dimensions
			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();
			float pageHeight = document.getPdfDocument().getDefaultPageSize().getHeight();

			float leftMargin = 20;
			float rightMargin = 20;
			float topMargin = 30;
			float bottomMargin = 70;

			float columnGap = 20;

			// Total available width
			float availableWidth = pageWidth - leftMargin - rightMargin;

			// 4 columns → 3 gaps
			float columnWidth = (availableWidth - (3 * columnGap)) / 4;

			// Starting Y position
			float currentY = pageHeight - topMargin - 40;
			float columnHeight = currentY - bottomMargin;

			// Define column rectangles starting from current position
			Rectangle firstColumnRect = new Rectangle(
			        leftMargin,
			        bottomMargin,
			        columnWidth,
			        columnHeight
			);

			Rectangle secondColumnRect = new Rectangle(
			        leftMargin + columnWidth + columnGap,
			        bottomMargin,
			        columnWidth,
			        columnHeight
			);

			Rectangle thirdColumnRect = new Rectangle(
			        leftMargin + (columnWidth + columnGap) * 2,
			        bottomMargin,
			        columnWidth,
			        columnHeight
			);

			Rectangle fourthColumnRect = new Rectangle(
			        leftMargin + (columnWidth + columnGap) * 3,
			        bottomMargin,
			        columnWidth,
			        columnHeight
			);
			
			// Set column renderer
			ColumnDocumentRenderer columnRenderer = new ColumnDocumentRenderer(document,
			        new Rectangle[] {
				            firstColumnRect,
				            secondColumnRect,
				            thirdColumnRect,
				            fourthColumnRect
				        }
				    );
			document.setRenderer(columnRenderer);

			Paragraph data = new Paragraph();

			List<NamePlateTableMenuWithBgResponseDto> reportData = (List<NamePlateTableMenuWithBgResponseDto>) eventDto.get("data");
			
			List<MenuCategoryForTabkeMenuWithBgReportResponseDto> groupedResponse =
			        reportData.stream()
			                .collect(Collectors.groupingBy(NamePlateTableMenuWithBgResponseDto::getMenuCatId))
			                .entrySet()
			                .stream()
			                .map(entry -> {

			                    NamePlateTableMenuWithBgResponseDto first = entry.getValue().get(0);

			                    List<MenuItemForTableMenuWithBgReportResponseDto> items =
			                            entry.getValue().stream()
			                                    .sorted(Comparator.comparing(NamePlateTableMenuWithBgResponseDto::getSequence))
			                                    .map(item -> new MenuItemForTableMenuWithBgReportResponseDto(
			                                            item.getId(),
			                                            item.getMenuItemId(),
			                                            item.getItemNameEnglish(),
			                                            item.getItemNameHindi(),
			                                            item.getItemNameGujarati(),
			                                            item.getItemCount(),
			                                            item.getSequence(),
			                                            item.getIs_checked()
			                                    ))
			                                    .collect(Collectors.toList());

			                    return new MenuCategoryForTabkeMenuWithBgReportResponseDto(
			                            entry.getKey(),
			                            first.getCatNameEnglish(),
			                            first.getCatNameHindi(),
			                            first.getCatNameGujarati(),
			                            items
			                    );
			                })
			                .collect(Collectors.toList());
			

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			for (MenuCategoryForTabkeMenuWithBgReportResponseDto category : groupedResponse) {

			    String categoryName = category.getCatNameEnglish() != null
			            ? category.getCatNameEnglish()
			            : "";

			    Div catBlock = new Div();
			    catBlock.setKeepTogether(true);

			    // ===== Category Title =====
			    Paragraph categoryPara = new Paragraph(categoryName)
			            .setFont(boldFont)
			            .setFontSize(14f)
			            .setFontColor(catRedColor)
			            .setMarginTop(20f)
			            .setMarginBottom(5f);

			    catBlock.add(categoryPara);

			    // ===== Items =====
			    if (category.getItems() != null) {

			        for (MenuItemForTableMenuWithBgReportResponseDto item : category.getItems()) {

			            String itemName = item.getItemNameEnglish() != null
			                    ? item.getItemNameEnglish()
			                    : "";

			            Paragraph itemPara = new Paragraph(itemName)
			                    .setFont(basicFont)
			                    .setFontSize(12f)
			                    .setFontColor(itemBlackColor)
			                    .setTextAlignment(TextAlignment.LEFT)
			                    .setMargin(0);

			            catBlock.add(itemPara);
			        }
			    }

			    document.add(catBlock);
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/" +
					getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), "Table Menu Report")+ ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate report");
		}
	}
	
	@Override
	public String onePageCounterNamePlate(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
	        Long userid, Integer isCompanyDetails) {
	    try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        menuPreparationServiceImpl.loadLicense();
	        if (lang == 1) {
	            // Hindi
	            System.out.println("Loading Hindi font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
	            System.out.println("Hindi font loaded successfully");
	        } else if (lang == 2) {
	            // Gujarati
	            System.out.println("Loading Gujarati font...");
	            basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	            boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
	            System.out.println("Gujarati font loaded successfully");
	        } else {
	            // English
	            System.out.println("Loading English font...");
	            basicFont = boldFont = PdfFontFactory.createFont("/fonts/Gabriola-regular.ttf");
	            System.out.println("English font loaded successfully");
	        }
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = new PageSize(411.02f, 623.62f);

	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");
	        
	        Boolean isFirstItem = true;
	        for (NameplateReportDataResponseDto dto : data) {
	        	if(!isFirstItem) {
	        		document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	        	}
	        	isFirstItem = false;
	        	
	            String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty()
	                    ? ""
	                    : dto.getItemName();

	            Div div = new Div()
	                    .setWidth(UnitValue.createPercentValue(100))
	                    .setHeight(269.29f)
	                    .setBorder(Border.NO_BORDER)
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setPadding(0)
	                    .setPaddingTop(60f)
	                    .setVerticalAlignment(VerticalAlignment.TOP);

	            Paragraph itemPara = new Paragraph(itemNm)
	                    .setFont(basicFont)
	                    .setFontSize(30f)
	                    .simulateBold()
	                    .setMargin(0)
	                    .setPadding(0)
	                    .setPaddingLeft(15f)
	                    .setPaddingRight(15f)
	                    .setFixedLeading(30f)
	                    .setTextAlignment(TextAlignment.CENTER);

	            div.add(itemPara);

	            document.add(div);
	        }
	        
	        
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String paarisoCounterNamePlate1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = new PageSize(758.016f, 595.008f);
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color blueColor = new DeviceRgb(0, 0, 153);
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getNamePlateBg());
//					.loadImageFromResource("/flipbook/pages/nameplate4.png");
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			// Expand records based on itemCount
			List<NameplateReportDataResponseDto> expandedData = new ArrayList<>();

			for (NameplateReportDataResponseDto dto : data) {

			    int count = 1;

			    if (dto.getItemCount() != null) {
			        try {
			            count = dto.getItemCount().intValueExact();
			        } catch (Exception ex) {
			            count = dto.getItemCount().intValue();
			        }
			    }

			    for (int x = 0; x < count; x++) {
			        expandedData.add(dto);
			    }
			}

			// 4 nameplates per page (2 x 2)
			for (int i = 0; i < expandedData.size(); i += 4) {

			    Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }))
			            .setWidth(UnitValue.createPercentValue(100))
			            .setHeight(595.008f)
			            .setMargin(0)
			            .setPadding(0);

			    for (int j = i; j < i + 4; j++) {

			        Cell cell = new Cell()
			                .setWidth(UnitValue.createPercentValue(50))
			                .setHeight(297.504f)
			                .setTextAlignment(TextAlignment.CENTER)
			                .setVerticalAlignment(VerticalAlignment.MIDDLE)
			                .setBorder(Border.NO_BORDER)
			                .setPadding(10f);

			        if (j < expandedData.size()) {

			            NameplateReportDataResponseDto dto = expandedData.get(j);

			            String itemNm = dto.getItemName() == null ? "" : dto.getItemName();
			            String itemNmPref = dto.getItemNamePref() == null ? "" : dto.getItemNamePref();

			            Paragraph itemPara = new Paragraph(itemNm)
			                    .setFont(basicFont)
			                    .setFontSize(26f)
			                    .setFontColor(blueColor)
			                    .simulateBold()
			                    .setMargin(0)
			                    .setTextAlignment(TextAlignment.CENTER);

			            Paragraph itemParaPref = new Paragraph(itemNmPref)
			                    .setFont(prefFont)
			                    .setFontSize(22f)
			                    .setFontColor(blueColor)
			                    .simulateBold()
			                    .setMargin(0)
			                    .setTextAlignment(TextAlignment.CENTER);

			            cell.add(itemPara);
			            cell.add(itemParaPref);
			        }

			        table.addCell(cell);
			    }

			    document.add(table);

			    if (i + 4 < expandedData.size()) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			}
			
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String paarisoCounterNamePlate2(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = new PageSize(595f, 758f);
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color blueColor = new DeviceRgb(0, 0, 153);
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getNamePlateBg());
//					.loadImageFromResource("/flipbook/pages/nameplate5.png");
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			// Expand records based on itemCount
			List<NameplateReportDataResponseDto> expandedData = new ArrayList<>();

			for (NameplateReportDataResponseDto dto : data) {

			    int count = 1;

			    if (dto.getItemCount() != null) {
			        try {
			            count = dto.getItemCount().intValueExact();
			        } catch (Exception ex) {
			            count = dto.getItemCount().intValue();
			        }
			    }

			    for (int x = 0; x < count; x++) {
			        expandedData.add(dto);
			    }
			}

			// 5 nameplates per page
			for (int i = 0; i < expandedData.size(); i += 5) {

			    Table table = new Table(UnitValue.createPercentArray(new float[] { 100f }))
			            .setWidth(UnitValue.createPercentValue(100f))
			            .setHeight(UnitValue.createPercentValue(100f))
			            .setMargin(0)
			            .setPadding(0);

			    for (int j = i; j < i + 5; j++) {

			        Cell cell = new Cell()
			                .setWidth(UnitValue.createPercentValue(100f))
			                .setHeight(148f)
			                .setTextAlignment(TextAlignment.CENTER)
			                .setVerticalAlignment(VerticalAlignment.MIDDLE)
			                .setBorder(Border.NO_BORDER)
			                .setPadding(0)
			                .setPaddingTop(3f);

			        if (j < expandedData.size()) {

			            NameplateReportDataResponseDto dto = expandedData.get(j);

			            String itemNm = dto.getItemName() == null ? "" : dto.getItemName();
			            String itemNmPref = dto.getItemNamePref() == null ? "" : dto.getItemNamePref();

			            Paragraph itemPara = new Paragraph(itemNm)
			                    .setFont(basicFont)
			                    .setFontSize(26f)
			                    .setFontColor(blueColor)
			                    .simulateBold()
			                    .setMargin(0)
			                    .setTextAlignment(TextAlignment.CENTER);

			            Paragraph itemParaPref = new Paragraph(itemNmPref)
			                    .setFont(prefFont)
			                    .setFontSize(22f)
			                    .setFontColor(blueColor)
			                    .simulateBold()
			                    .setMargin(0)
			                    .setTextAlignment(TextAlignment.CENTER);

			            cell.add(itemPara);
			            cell.add(itemParaPref);
			        }

			        table.addCell(cell);
			    }

			    document.add(table);

			    if (i + 4 < expandedData.size()) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			}
			
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String amoncarCounterNamePlate1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "Data not found.";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = pdfDocument.getDefaultPageSize();
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 80, 0, 80);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = null;
	        
			if (imageId != null && imageId != -1) {
				Optional<NamePlateImagesEntity> image = namePlateImagesRepository.findById(imageId);
				if (image.isPresent()) {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + image.get());
				}else {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url")
									+ adminTemplate.getTemplateMaster().getNamePlateBg());
//							.loadImageFromResource("/flipbook/pages/AmoncarNew-nameplate1.png");
				}
			} else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url")
								+ adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/AmoncarNew-nameplate1.png");
			}
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			Boolean isFirstItem = true;
			for (NameplateReportDataResponseDto dto : data) {
			    if (!isFirstItem) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			    isFirstItem = false;

			    String itemNm = dto.getItemName() == null ? "" : dto.getItemName();

			    Paragraph itemPara = new Paragraph(itemNm)
			            .setFont(basicFont)
			            .setFontSize(60f)
			            .setFontColor(color)
			            .simulateBold()
			            .setTextAlignment(TextAlignment.CENTER)
			            .setMargin(0);

			    Div div = new Div()
			            .setHeight(pageSize.getHeight()) // or your custom page height
			            .setVerticalAlignment(VerticalAlignment.MIDDLE)
			            .setTextAlignment(TextAlignment.CENTER);

			    div.add(itemPara);

			    document.add(div);
			}

	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String amoncarCounterNamePlate2(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
	        document.setMargins(0, 0, 0, 0);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = null;
	        
			if (imageId != null && imageId != -1) {
				Optional<NamePlateImagesEntity> image = namePlateImagesRepository.findById(imageId);
				if (image.isPresent()) {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + image.get());
				}else {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url")
									+ adminTemplate.getTemplateMaster().getNamePlateBg());
//							.loadImageFromResource("/flipbook/pages/AAmoncar_artwark_1_A4_6x4.png");
				}
			} else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url")
								+ adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/Amoncar_artwark_1_A4_6x4.png");
			}
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			// Expand records based on itemCount
			List<NameplateReportDataResponseDto> expandedData = new ArrayList<>();

			for (NameplateReportDataResponseDto dto : data) {

			    int count = 1;

			    if (dto.getItemCount() != null) {
			        try {
			            count = dto.getItemCount().intValueExact();
			        } catch (Exception ex) {
			            count = dto.getItemCount().intValue();
			        }
			    }

			    for (int x = 0; x < count; x++) {
			        expandedData.add(dto);
			    }
			}

			// 4 nameplates per page (2 x 2)
			for (int i = 0; i < expandedData.size(); i += 4) {

			    Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }))
			            .setWidth(UnitValue.createPercentValue(100))
			            .setHeight(pdfDocument.getDefaultPageSize().getHeight())
			            .setMargin(0)
			            .setPadding(0);

			    for (int j = i; j < i + 4; j++) {

			        Cell cell = new Cell()
			                .setWidth(UnitValue.createPercentValue(50))
			                .setHeight(pdfDocument.getDefaultPageSize().getHeight() / 2)
			                .setTextAlignment(TextAlignment.CENTER)
			                .setVerticalAlignment(VerticalAlignment.MIDDLE)
			                .setBorder(Border.NO_BORDER)
			                .setPadding(10f);

			        if (j < expandedData.size()) {

			            NameplateReportDataResponseDto dto = expandedData.get(j);

			            String itemNm = dto.getItemName() == null ? "" : dto.getItemName();

			            Paragraph itemPara = new Paragraph(itemNm)
			                    .setFont(basicFont)
			                    .setFontSize(25f)
			                    .setFontColor(color)
			                    .simulateBold()
			                    .setMargin(0)
			                    .setPaddings(0, 30, 0, 30)
			                    .setTextAlignment(TextAlignment.CENTER);

			            cell.add(itemPara);
			        }

			        table.addCell(cell);
			    }

			    document.add(table);

			    if (i + 4 < expandedData.size()) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			}
			
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String amoncarCounterNamePlate3(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row1 = (Object[]) result;
			String defaultLanguage = row1[0].toString();
			String preferedLanguage = row1[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = pdfDocument.getDefaultPageSize();
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = null;
	        
			if (imageId != null && imageId != -1) {
				Optional<NamePlateImagesEntity> image = namePlateImagesRepository.findById(imageId);
				if (image.isPresent()) {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + image.get());
				}else {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url")
									+ adminTemplate.getTemplateMaster().getNamePlateBg());
//							.loadImageFromResource("/flipbook/pages/NewTentA4.png");
				}
			} else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url")
								+ adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/NewTentA4.png");
			}
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
			table.setWidth(UnitValue.createPercentValue(100));
			table.setMargin(0);
			table.setPadding(0);

			float cellHeight = pageSize.getHeight() / 4f;

			int cardsPerPage = 4; // 4 unique items per page
			int itemCountOnPage = 0;

			for (int i = 0; i < data.size(); i += 2) {

			    NameplateReportDataResponseDto left = data.get(i);

			    NameplateReportDataResponseDto right = null;
			    if (i + 1 < data.size()) {
			        right = data.get(i + 1);
			    }

			    // -------- Rotated Row --------
			    table.addCell(createCard(left.getItemName(), basicFont, true, cellHeight));

			    if (right != null)
			        table.addCell(createCard(right.getItemName(), basicFont, true, cellHeight));
			    else
			        table.addCell(new Cell().setBorder(Border.NO_BORDER));

			    // -------- Normal Row --------
			    table.addCell(createCard(left.getItemName(), basicFont, false, cellHeight));

			    if (right != null)
			        table.addCell(createCard(right.getItemName(), basicFont, false, cellHeight));
			    else
			        table.addCell(new Cell().setBorder(Border.NO_BORDER));

			    itemCountOnPage += 2;

			    if (itemCountOnPage >= cardsPerPage) {

			        document.add(table);

			        if (i + 2 < data.size()) {
//			            document.add(new AreaBreak());

			            table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
			            table.setWidth(UnitValue.createPercentValue(100));
			            table.setMargin(0);
			            table.setPadding(0);
			        }

			        itemCountOnPage = 0;
			    }
			}

			if (itemCountOnPage > 0) {
			    document.add(table);
			}
			
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	private Cell createCard(String itemName, PdfFont font, boolean rotate, float cellHeight) {

	    Paragraph p = new Paragraph(itemName == null ? "" : itemName)
	            .setFont(font)
	            .setFontSize(21)
	            .simulateBold()
	            .setMargin(0)
	            .setPaddings(0, 35, 0, 35)
	            .setHorizontalAlignment(HorizontalAlignment.CENTER);

	    if (rotate) {
	    	p.setRotationAngle(Math.PI);
	    }

	    Div div = new Div()
	            .setHeight(UnitValue.createPointValue(cellHeight))
	            .setTextAlignment(TextAlignment.CENTER)
	            .setVerticalAlignment(VerticalAlignment.MIDDLE);


	    div.add(p);

	    return new Cell()
	            .setBorder(Border.NO_BORDER)
	            .setPadding(0)
	            .setHeight(cellHeight)
	            .add(div);
	}
	
	@Override
	public String amoncarCounterNamePlate4(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "Data not found.";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = PageSize.A3;
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 100, 0, 100);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = null;
	        
			if (imageId != null && imageId != -1) {
				Optional<NamePlateImagesEntity> image = namePlateImagesRepository.findById(imageId);
				if (image.isPresent()) {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + image.get());
				}else {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url")
									+ adminTemplate.getTemplateMaster().getNamePlateBg());
//							.loadImageFromResource("/flipbook/pages/A31.png");
				}
			} else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url")
								+ adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/A31.png");
			}
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			Boolean isFirstItem = true;
			for (NameplateReportDataResponseDto dto : data) {
			    if (!isFirstItem) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			    isFirstItem = false;

			    String itemNm = dto.getItemName() == null ? "" : dto.getItemName();

			    Paragraph itemPara = new Paragraph(itemNm)
			            .setFont(basicFont)
			            .setFontSize(55f)
			            .setFontColor(color)
			            .simulateBold()
			            .setTextAlignment(TextAlignment.CENTER)
			            .setMargin(0);

			    Div div = new Div()
			            .setHeight(pageSize.getHeight()) // or your custom page height
			            .setVerticalAlignment(VerticalAlignment.MIDDLE)
			            .setTextAlignment(TextAlignment.CENTER);

			    div.add(itemPara);

			    document.add(div);
			}

	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String amoncarCounterNamePlate5(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = PageSize.A3;
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = null;
	        
			if (imageId != null && imageId != -1) {
				Optional<NamePlateImagesEntity> image = namePlateImagesRepository.findById(imageId);
				if (image.isPresent()) {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + image.get());
				}else {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url")
									+ adminTemplate.getTemplateMaster().getNamePlateBg());
//							.loadImageFromResource("/flipbook/pages/A641.png");
				}
			} else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url")
								+ adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/A641.png");
			}
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			// Expand records based on itemCount
			List<NameplateReportDataResponseDto> expandedData = new ArrayList<>();

			for (NameplateReportDataResponseDto dto : data) {

			    int count = 1;

			    if (dto.getItemCount() != null) {
			        try {
			            count = dto.getItemCount().intValueExact();
			        } catch (Exception ex) {
			            count = dto.getItemCount().intValue();
			        }
			    }

			    for (int x = 0; x < count; x++) {
			        expandedData.add(dto);
			    }
			}

			// 9 nameplates per page (3 x 3)
			for (int i = 0; i < expandedData.size(); i += 9) {

			    Table table = new Table(UnitValue.createPercentArray(new float[] { 33.33f, 33.33f,33.34f }))
			            .setWidth(UnitValue.createPercentValue(100))
			            .setHeight(pageSize.getHeight())
			            .setMargin(0)
			            .setPadding(0)
			            .setFixedLayout();

			    for (int j = i; j < i + 9; j++) {

			        Cell cell = new Cell()
			                .setWidth(UnitValue.createPercentValue(50))
			                .setHeight(pageSize.getHeight() / 3)
			                .setTextAlignment(TextAlignment.CENTER)
			                .setVerticalAlignment(VerticalAlignment.MIDDLE)
			                .setBorder(Border.NO_BORDER)
			                .setPaddings(0, 30, 0, 30);

			        if (j < expandedData.size()) {

			            NameplateReportDataResponseDto dto = expandedData.get(j);

			            String itemNm = dto.getItemName() == null ? "" : dto.getItemName();

			            Paragraph itemPara = new Paragraph(itemNm)
			                    .setFont(basicFont)
			                    .setFontSize(25f)
			                    .setFontColor(color)
			                    .simulateBold()
			                    .setMargin(0)
			                    .setTextAlignment(TextAlignment.CENTER);

			            cell.add(itemPara);
			        }

			        table.addCell(cell);
			    }

			    document.add(table);

			    if (i + 4 < expandedData.size()) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			}
			
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String amoncarCounterNamePlate6(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row1 = (Object[]) result;
			String defaultLanguage = row1[0].toString();
			String preferedLanguage = row1[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = PageSize.A3;
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = null;
	        
			if (imageId != null && imageId != -1) {
				Optional<NamePlateImagesEntity> image = namePlateImagesRepository.findById(imageId);
				if (image.isPresent()) {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + image.get());
				}else {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url")
									+ adminTemplate.getTemplateMaster().getNamePlateBg());
//							.loadImageFromResource("/flipbook/pages/Amoncar_artwark_1_A3_Tent_Card.png");
				}
			} else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url")
								+ adminTemplate.getTemplateMaster().getNamePlateBg());
//					.loadImageFromResource("/flipbook/pages/Amoncar_artwark_1_A3_Tent_Card.png");
			}
			
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			Table table = new Table(UnitValue.createPercentArray(new float[] {33.33f, 33.33f, 33.34f}));
			table.setWidth(UnitValue.createPercentValue(100));

			float cellHeight = pageSize.getHeight() / 6f;

			int itemsOnPage = 0;

			for (int i = 0; i < data.size(); i += 3) {

			    if (itemsOnPage == 9) {
			        document.add(table);
			        document.add(new AreaBreak());

			        table = new Table(UnitValue.createPercentArray(new float[] {33.33f,33.33f,33.34f}));
			        table.setWidth(UnitValue.createPercentValue(100));

			        itemsOnPage = 0;
			    }

			    NameplateReportDataResponseDto item1 = data.get(i);
			    NameplateReportDataResponseDto item2 = (i + 1 < data.size()) ? data.get(i + 1) : null;
			    NameplateReportDataResponseDto item3 = (i + 2 < data.size()) ? data.get(i + 2) : null;

			    // ---------- Rotated Row ----------
			    table.addCell(createCard(item1.getItemName(), basicFont, true, cellHeight));

			    if (item2 != null)
			        table.addCell(createCard(item2.getItemName(), basicFont, true, cellHeight));
			    else
			        table.addCell(new Cell().setBorder(Border.NO_BORDER));

			    if (item3 != null)
			        table.addCell(createCard(item3.getItemName(), basicFont, true, cellHeight));
			    else
			        table.addCell(new Cell().setBorder(Border.NO_BORDER));

			    // ---------- Normal Row ----------
			    table.addCell(createCard(item1.getItemName(), basicFont, false, cellHeight));

			    if (item2 != null)
			        table.addCell(createCard(item2.getItemName(), basicFont, false, cellHeight));
			    else
			        table.addCell(new Cell().setBorder(Border.NO_BORDER));

			    if (item3 != null)
			        table.addCell(createCard(item3.getItemName(), basicFont, false, cellHeight));
			    else
			        table.addCell(new Cell().setBorder(Border.NO_BORDER));

			    itemsOnPage += 3;
			}

			if (itemsOnPage > 0) {
			    document.add(table);
			}
			
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String amoncarCounterNamePlate7(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				prefboldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = PageSize.A3.rotate();
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");

	        ImageData watermarkBgData = null;
	        
			if (imageId != null && imageId != -1) {
				Optional<NamePlateImagesEntity> image = namePlateImagesRepository.findById(imageId);
				if (image.isPresent()) {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + image.get());
				}else {
					watermarkBgData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url")
									+ adminTemplate.getTemplateMaster().getNamePlateBg());
//							.loadImageFromResource("/flipbook/pages/Amoncar artwark 2a A4 6x4 (1).png");
				}
			} else {
				watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url")
								+ adminTemplate.getTemplateMaster().getNamePlateBg());
//						.loadImageFromResource("/flipbook/pages/Amoncar artwark 2a A4 6x4 (1).png");
			}
	        
	        BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
			
			// Expand records based on itemCount
			List<NameplateReportDataResponseDto> expandedData = new ArrayList<>();

			for (NameplateReportDataResponseDto dto : data) {

			    int count = 1;

			    if (dto.getItemCount() != null) {
			        try {
			            count = dto.getItemCount().intValueExact();
			        } catch (Exception ex) {
			            count = dto.getItemCount().intValue();
			        }
			    }

			    for (int x = 0; x < count; x++) {
			        expandedData.add(dto);
			    }
			}

			// 4 nameplates per page (2 x 2)
			for (int i = 0; i < expandedData.size(); i += 4) {

			    Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }))
			            .setWidth(UnitValue.createPercentValue(100))
			            .setHeight(pageSize.getHeight())
			            .setMargin(0)
			            .setPadding(0);

			    for (int j = i; j < i + 4; j++) {

			        Cell cell = new Cell()
			                .setWidth(UnitValue.createPercentValue(50))
			                .setHeight(pageSize.getHeight() / 2)
			                .setTextAlignment(TextAlignment.CENTER)
			                .setVerticalAlignment(VerticalAlignment.MIDDLE)
			                .setBorder(Border.NO_BORDER)
			                .setPadding(10f);

			        if (j < expandedData.size()) {

			            NameplateReportDataResponseDto dto = expandedData.get(j);

			            String itemNm = dto.getItemName() == null ? "" : dto.getItemName();

			            Paragraph itemPara = new Paragraph(itemNm)
			                    .setFont(basicFont)
			                    .setFontSize(38f)
			                    .setFontColor(color)
			                    .simulateBold()
			                    .setMargin(0)
			                    .setPaddings(0, 30, 0, 30)
			                    .setTextAlignment(TextAlignment.CENTER);

			            cell.add(itemPara);
			        }

			        table.addCell(cell);
			    }

			    document.add(table);

			    if (i + 4 < expandedData.size()) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			}
			
	        document.close();
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String rDevrajCounterNamePlate1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, AdminTemplateModuleResponseDto adminTemplate) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefboldFont = prefFont = menuPreparationServiceImpl.loadFont("/fonts/arial-unicode-ms.ttf");
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "Data not found.";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = new PageSize(432f, 828f);
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");
	        
			Boolean isFirstItem = true;
			for (NameplateReportDataResponseDto dto : data) {
			    if (!isFirstItem) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			    isFirstItem = false;

			    String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty() ? ""
						: dto.getItemName();
				String itemNmPref = dto.getItemNamePref() == null || dto.getItemNamePref().isEmpty() ? ""
						: dto.getItemNamePref();
				Paragraph itemPara = new Paragraph()
						.add(new Text(itemNm).setFontSize(16f).setFont(basicFont))
						.add(new Text("\n"+itemNmPref).setFontSize(16f).setFont(prefFont))
						.setPadding(0)
						.setMargin(0)
						.setKeepTogether(true)
						.simulateBold()
						.setTextAlignment(TextAlignment.CENTER);

				Div div = new Div().setHeight(pageSize.getHeight()).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setTextAlignment(TextAlignment.CENTER).setMarginTop(180f);
				div.add(itemPara);

				document.add(div);
			}
			
	        document.close();
	        
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
	
	@Override
	public String rDevrajCounterNamePlate2(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, AdminTemplateModuleResponseDto adminTemplate) {
		try {
	        PdfFont basicFont = null;
	        PdfFont boldFont = null;
	        
			PdfFont prefFont = null;
			PdfFont prefboldFont = null;

			menuPreparationServiceImpl.loadLicense();
	       
	        Object result = menuPreparationDetailsRepository.getLanguageSpecification(userid);

			if (result == null) {
				return null;
			}

			Object[] row = (Object[]) result;
			String defaultLanguage = row[0].toString();
			String preferedLanguage = row[1].toString();
			
			if (defaultLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				lang = 1;
			} else if (defaultLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				lang = 2;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				System.out.println("English font loaded successfully");
				lang = 0;
			}
			
			if (preferedLanguage.equalsIgnoreCase("hi")) {
				// Hindi
				System.out.println("Loading Hindi font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
			} else if (preferedLanguage.equalsIgnoreCase("gu")) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				prefFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				prefboldFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
			} else {
				// English
				System.out.println("Loading English font...");
				prefboldFont = prefFont = menuPreparationServiceImpl.loadFont("/fonts/arial-unicode-ms.ttf");
			}
			
	        PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	        Map<String, Object> eventDto = nameplateService.getNameplateItemsWithCategory(userid, eventId, eventFunctionId, Long.valueOf(-1),
	                lang, defaultLanguage, preferedLanguage, 1, 0, 0);
	        
	        if (eventDto == null) {
	            return "Data not found.";
	        }

	        String eventNo = eventDto.get("eventNo").toString();
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
	        File pdfFile = new File(outputPath + "/counternameplate_" + datetimeforfile + ".pdf");

	        PdfWriter writer = new PdfWriter(pdfFile);
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        PageSize pageSize = new PageSize(460f, 324f);
	        Document document = new Document(pdfDocument, pageSize, false);
	        document.setMargins(0, 0, 0, 0);

	        Color color = null;
	        
	        String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();
	        
	        if(contentrgb != null && contentrgb.trim().length() != 0) {
				String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");
	
				int r = Integer.parseInt(parts2[0].trim());
				int g = Integer.parseInt(parts2[1].trim());
				int b = Integer.parseInt(parts2[2].trim());
				
				color = new DeviceRgb(r, g, b);
	        }else {
	        	color = new DeviceRgb(0, 0, 0);
	        }
	        List<NameplateReportDataResponseDto> data = (List<NameplateReportDataResponseDto>) eventDto.get("data");
	        
			Boolean isFirstItem = true;
			for (NameplateReportDataResponseDto dto : data) {
			    if (!isFirstItem) {
			        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			    }
			    isFirstItem = false;

			    String itemNm = dto.getItemName() == null || dto.getItemName().isEmpty() ? ""
						: dto.getItemName();
				String itemNmPref = dto.getItemNamePref() == null || dto.getItemNamePref().isEmpty() ? ""
						: dto.getItemNamePref();
				Paragraph itemPara = new Paragraph()
						.add(new Text(itemNm).setFontSize(16f).setFont(basicFont))
						.add(new Text("\n"+itemNmPref).setFontSize(16f).setFont(prefFont))
						.setPadding(0)
						.setMargin(0)
						.setKeepTogether(true)
						.simulateBold()
						.setTextAlignment(TextAlignment.CENTER);

				Div div = new Div().setHeight(pageSize.getHeight()).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setTextAlignment(TextAlignment.CENTER).setMarginTop(62f);
				div.add(itemPara);

				document.add(div);
			}
			
	        document.close();
	        
	        String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo
	                + "/counternameplate_" + datetimeforfile + ".pdf";
	        return fullUrl;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate report");
	    }
	}
}
