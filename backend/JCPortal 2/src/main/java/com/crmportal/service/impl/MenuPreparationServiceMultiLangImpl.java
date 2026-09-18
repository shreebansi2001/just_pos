package com.crmportal.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//iText 7 imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.response.dto.EventFunctionReportResponseDto;
import com.crmportal.response.dto.EventReportResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;
import com.crmportal.service.MenuPreparationServiceMultiLang;
import com.crmportal.service.impl.MenuPreparationServiceMultiLangImpl.BackgroundImageHandler;
import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

//Standard Java imports
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletRequest;

//Apache Commons (for IOUtils)
import org.apache.commons.io.IOUtils;



@Service
public class MenuPreparationServiceMultiLangImpl implements MenuPreparationServiceMultiLang {

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;
	
	private static final String LICENSE_FILE = "src/main/resources/itextkey.json";
	
	private static final String FONTS_DIR = "src/main/resources/fonts/";
	
	@Override
	public String generateExclusiveReport(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
	        Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
	        HttpServletRequest re, int formateType, Integer lang) {

	    try {
	        PdfFont basicFont = null;
	        loadLicense();
	        
	        if (lang == 1) {
	            // Hindi
	            System.out.println("Loading Hindi font...");
	            basicFont = loadFont("/fonts/Nirmala.ttf");
	            System.out.println("Hindi font loaded successfully");
	        } else if (lang == 2) {
	            // Gujarati
	            System.out.println("Loading Gujarati font...");
	            basicFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
	            System.out.println("Gujarati font loaded successfully");
	        } else {
	            // English
	            System.out.println("Loading English font...");
	            basicFont = loadFont("/fonts/times.ttf");
	            System.out.println("English font loaded successfully");
	        }

	        PdfFont basicFont2 = loadFont("/fonts/times.ttf");

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

	        // Colors in iText 9 - Changed from DeviceRgb to ColorConstants
	        Color boldColor = new DeviceRgb(147, 91, 57);
	        Color normalColor = new DeviceRgb(156, 104, 75);

	        // Font styles - iText 9 uses Style
	        Style normalStyle = new Style().setFont(basicFont).setFontSize(14);
	        Style eventStyle = new Style().setFont(basicFont2).setFontSize(18).setFontColor(normalColor);
	        Style boldStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(boldColor);

	        Style funNameStyle = new Style().setFont(basicFont).setFontSize(20).setFontColor(normalColor);
	        Style funBasicStyle = new Style().setFont(basicFont).setFontSize(17).setFontColor(boldColor);

	        Style itemNameStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(normalColor);
	        Style itemInstStyle = new Style().setFont(basicFont).setFontSize(14).setFontColor(boldColor);
	        Style itemSloganStyle = new Style().setFont(basicFont).setFontSize(12).setFontColor(boldColor);

	        Style menuNameStyle = new Style().setFont(basicFont).setFontSize(17).setFontColor(normalColor);
	        Style menuInstStyle = new Style().setFont(basicFont).setFontSize(15).setFontColor(boldColor);
	        Style menuSloganStyle = new Style().setFont(basicFont).setFontSize(13).setFontColor(boldColor);

	        List<Object[]> rows = menuPreparationDetailsRepository.getEventMenuReportRaw(eventId,eventFunctionId, lang, Long.valueOf(0), "en", "en");

	        EventReportResponseDto eventDto = new EventReportResponseDto();
	        Map<Long, EventFunctionReportResponseDto> functionMap = new LinkedHashMap<>();
	        Map<Long, MenuReportResponseDto> categoryMap;
	        Map<String, MenuItemForReportResponseDto> itemMap;

	        boolean eventDataSet = false;

	        for (Object[] r : rows) {
	            // ---- Extract columns ----
	            Long eId = getLong(r[0]);
	            String eventNo = getString(r[1]);
	            String eventEnd = getString(r[2]);
	            String eventStart = getString(r[3]);
	            String venue = getString(r[4]);
	            Long eventTypeId = getLong(r[5]);
	            String eventName = getString(r[6]);
	            String fnName = getString(r[7]);
	            String catName = getString(r[8]);
	            String itemName = getString(r[9]);

	            Long fnId = getLong(r[10]);
	            String fnStart = getString(r[11]);
	            String fnEnd = getString(r[12]);
	            String fnVenue = getString(r[13]);
	            Integer pax = getInt(r[14]);
	            Double rate = getDouble(r[15]);
	            Double defaultPrice = getDouble(r[16]);
	            Long catId = getLong(r[17]);
	            String catNotes = getString(r[18]);
	            String catSlogan = getString(r[19]);
	            Long itemId = getLong(r[20]);
	            String itemSlogan = getString(r[21]);
	            Double itemPrice = getDouble(r[22]);
	            String catImage = getString(r[23]);
	            String itemImage = getString(r[24]);
	            String itemNotes = getString(r[27]);
	            String catStartTime = getString(r[25]);
	            String remark = getString(r[26]);
	            String foodNotes = getString(r[28]);
	            String foodType = getString(r[29]);
	            String partyName = getString(r[30]);
	            String partyMobileNo = getString(r[31]);

	            // -------- Set Event Level Data Once ----------
	            if (!eventDataSet) {
	                eventDto.setEventId(eId);
	                eventDto.setEventNo(eventNo);
	                eventDto.setEventName(eventName);
	                eventDto.setVenue(venue);
	                eventDto.setEventStartTimestamp(eventStart != null ? LocalDate.parse(eventStart, formatter).format(formatter) : "");
	                eventDto.setEventEndTimestamp(eventEnd != null ? LocalDate.parse(eventEnd, formatter).format(formatter) : "");
	                eventDto.setRemark(remark);
	                eventDto.setPartyName(partyName);
	                eventDto.setMobileNo(partyMobileNo);
	                eventDto.setFoodNotes(foodNotes);
	                eventDto.setFoodType(foodType);
	                eventDto.setFunctions(new ArrayList<>());
	                eventDataSet = true;
	            }

	            // ---------- Function Level Grouping ----------
	            EventFunctionReportResponseDto fnDto = functionMap.get(fnId);
	            if (fnDto == null) {
	                fnDto = new EventFunctionReportResponseDto();
	                fnDto.setFunctionId(fnId);
	                fnDto.setFunctionName(fnName);
	                fnDto.setFunctionStartTimestamp(fnStart != null ? LocalDate.parse(fnStart, formatter2).format(formatter2) : "");
	                fnDto.setFunctionEndTimestamp(fnEnd != null ? LocalDate.parse(fnEnd, formatter2).format(formatter2) : "");
	                fnDto.setFunctionVenue(fnVenue);
	                fnDto.setPax(pax);
	                fnDto.setRate(rate);
	                fnDto.setMenuCategories(new ArrayList<>());

	                functionMap.put(fnId, fnDto);
	                eventDto.getFunctions().add(fnDto);
	            }

	            // Each function needs its own category map
	            if (fnDto.getMenuCategories().isEmpty()) {
	                categoryMap = new LinkedHashMap<>();
	            } else {
	                categoryMap = fnDto.getMenuCategories().stream().collect(LinkedHashMap::new,
	                        (map, c) -> map.put(c.getId(), c), LinkedHashMap::putAll);
	            }

	            // ---------- Category Level Grouping ----------
	            MenuReportResponseDto catDto = categoryMap.get(catId);
	            if (catDto == null) {
	                catDto = new MenuReportResponseDto();
	                catDto.setId(catId);
	                catDto.setNameEnglish(catName);
	                catDto.setImagePath(catImage);
	                catDto.setSlogan(catSlogan);
	                catDto.setMenuNotes(catNotes);
	                catDto.setMenuItems(new ArrayList<>());

	                categoryMap.put(catId, catDto);
	                fnDto.getMenuCategories().add(catDto);
	            }

	            // ---------- Item Level Grouping ----------
	            MenuItemForReportResponseDto itemDto = new MenuItemForReportResponseDto();
	            itemDto.setId(itemId);
	            itemDto.setNameEnglish(itemName);
	            itemDto.setSlogan(itemSlogan);
	            itemDto.setImagePath(itemImage);
	            itemDto.setItemNotes(itemNotes);

	            catDto.getMenuItems().add(itemDto);
	        }

	        if (eventDto == null) {
	            return "";
	        } else {
	            Date now = new Date();
	            String rootPath = re.getSession().getServletContext().getRealPath("/");
	            String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
	            File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

	            if (!outputPath.exists()) {
	                if (outputPath.mkdirs()) {
	                    System.out.println("Directory Created!!!");
	                } else {
	                    System.out.println("Error!!!");
	                }
	            }

	            File pdfFile = new File(outputPath + "/ExclusiveMenu_" + datetimeforfile + ".pdf");

	            // iText 9: Create PdfWriter and PdfDocument
	            PdfWriter writer = new PdfWriter(pdfFile);
	            PdfDocument pdfDocument = new PdfDocument(writer);
	            Document document = new Document(pdfDocument);
	            
	            // Set margins
	            document.setMargins(110, 120, 125, 120);
	            
	            // Add page event handler for header/footer
	            HeaderAndFooter event = new HeaderAndFooter(basicFont, basicFont, basicFont);
//	            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, event);

	            String noimage = re.getSession().getServletContext().getRealPath("/resources/image/noimage.png");

	            // Front Page
	            Table frontPageTable = new Table(1);
	            frontPageTable.setWidth(UnitValue.createPercentValue(100));
	            frontPageTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

	            InputStream inputStream = getClass().getResourceAsStream("/flipbook/pages/Tiptop_Main.png");
	            if (inputStream == null) {
	                throw new FileNotFoundException("Image not found in resources!");
	            }
	            byte[] imageBytes = IOUtils.toByteArray(inputStream);
	            ImageData imgData = ImageDataFactory.create(imageBytes);
	            Image imgf = new Image(imgData);
	            
	            // Background image handler
	            BackgroundImageHandler backgroundHandler = new BackgroundImageHandler(imgf);
	            
	            Cell tcellmain = new Cell().add(new Paragraph(" ").addStyle(normalStyle));
	            tcellmain.setTextAlignment(TextAlignment.CENTER);
	            tcellmain.setBorder(Border.NO_BORDER);
	            frontPageTable.addCell(tcellmain);
	            
	            // Set background using canvas
	            frontPageTable.setNextRenderer(new TableWithBackgroundRenderer(frontPageTable, backgroundHandler));

	            document.add(frontPageTable);
	            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

	            // Title Page
	            frontPageTable = new Table(1);
	            frontPageTable.setWidth(UnitValue.createPercentValue(100));
	            frontPageTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

	            inputStream = getClass().getResourceAsStream("/flipbook/pages/Tiptop_Detail.png");
	            if (inputStream == null) {
	                throw new FileNotFoundException("Image not found in resources!");
	            }
	            byte[] imageBytes1 = IOUtils.toByteArray(inputStream);
	            imgData = ImageDataFactory.create(imageBytes1);
	            imgf = new Image(imgData);
	            backgroundHandler = new BackgroundImageHandler(imgf);

	            tcellmain = new Cell().add(new Paragraph(" ").addStyle(normalStyle));
	            tcellmain.setTextAlignment(TextAlignment.CENTER);
	            tcellmain.setBorder(Border.NO_BORDER);
	            frontPageTable.addCell(tcellmain);
	            frontPageTable.setNextRenderer(new TableWithBackgroundRenderer(frontPageTable, backgroundHandler));

	            document.add(frontPageTable);
	            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

	            // IMPROVED EVENT INFORMATION PAGE
	            Table eventInfoTable = new Table(1);
	            eventInfoTable.setWidth(UnitValue.createPercentValue(100));
	            eventInfoTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

	            // Set background image
	            inputStream = getClass().getResourceAsStream("/flipbook/pages/Tiptop_Watermark.png");
	            if (inputStream == null) {
	                throw new FileNotFoundException("Image not found in resources!");
	            }
	            imageBytes = IOUtils.toByteArray(inputStream);
	            imgData = ImageDataFactory.create(imageBytes);
	            imgf = new Image(imgData);
	            backgroundHandler = new BackgroundImageHandler(imgf);

	            Cell cell;

	            // Event Date
	            cell = new Cell().add(new Paragraph("Menu Proposed On").addStyle(eventStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(20f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
	            cell = new Cell().add(new Paragraph(eventDate).addStyle(boldStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setPaddingBottom(5f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            // Host Name
	            cell = new Cell().add(new Paragraph("Honourable Host").addStyle(eventStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(18f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
	            cell = new Cell().add(new Paragraph(hostName).addStyle(boldStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            cell.setPaddingBottom(5f);
	            eventInfoTable.addCell(cell);

	            // Mobile No
	            cell = new Cell().add(new Paragraph("Mobile No").addStyle(eventStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(18f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
	            cell = new Cell().add(new Paragraph(mobileNo).addStyle(boldStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            cell.setPaddingBottom(5f);
	            eventInfoTable.addCell(cell);

	            cell = new Cell().add(new Paragraph("Event Date").addStyle(eventStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(18f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
	            cell = new Cell().add(new Paragraph(eventDate).addStyle(boldStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setPaddingBottom(5f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            // Event Type
	            cell = new Cell().add(new Paragraph("Type of Event").addStyle(eventStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(18f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            String eventType = eventDto.getEventName() == null ? "" : eventDto.getEventName();
	            cell = new Cell().add(new Paragraph(eventType).addStyle(boldStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setPaddingBottom(5f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            // Venue Information
	            cell = new Cell().add(new Paragraph("Venue").addStyle(eventStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(18f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
	            cell = new Cell().add(new Paragraph(venue).addStyle(boldStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setPaddingBottom(5f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            cell = new Cell().add(new Paragraph("Food Preparations").addStyle(eventStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(18f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);

	            String foodNotesName = eventDto.getFoodType();
	            String cuisineType = (foodNotesName != null && !foodNotesName.trim().isEmpty()) ? foodNotesName
	                    : "Not Specified";
	            cell = new Cell().add(new Paragraph(cuisineType).addStyle(boldStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            cell.setPaddingBottom(5f);
	            eventInfoTable.addCell(cell);

	            String foodNotes = eventDto.getFoodNotes();
	            if (foodNotes != null && !foodNotes.trim().isEmpty()) {
	                cell = new Cell().add(new Paragraph("Special Note").addStyle(eventStyle));
	                cell.setBorder(Border.NO_BORDER);
	                cell.setPaddingTop(18f);
	                cell.setTextAlignment(TextAlignment.CENTER);
	                eventInfoTable.addCell(cell);

	                cell = new Cell().add(new Paragraph(foodNotes).addStyle(boldStyle));
	                cell.setBorder(Border.NO_BORDER);
	                cell.setPaddingTop(10f);
	                cell.setPaddingBottom(5f);
	                cell.setTextAlignment(TextAlignment.CENTER);
	                eventInfoTable.addCell(cell);
	            }

	            cell = new Cell().add(new Paragraph(" ").addStyle(normalStyle));
	            cell.setBorder(Border.NO_BORDER);
	            cell.setPaddingTop(10f);
	            cell.setTextAlignment(TextAlignment.CENTER);
	            eventInfoTable.addCell(cell);
	            
	            eventInfoTable.setNextRenderer(new TableWithBackgroundRenderer(eventInfoTable, backgroundHandler));
	            document.add(eventInfoTable);
	            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

	            // Menu Content Pages
	            inputStream = getClass().getResourceAsStream("/flipbook/pages/Tiptop_Watermark.png");
	            if (inputStream == null) {
	                throw new FileNotFoundException("Image not found in resources!");
	            }
	            imageBytes = IOUtils.toByteArray(inputStream);
	            imgData = ImageDataFactory.create(imageBytes);
	            imgf = new Image(imgData);

	            boolean firstFunction = true;

	            for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {

	                if (!firstFunction) {
	                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	                }
	                firstFunction = false;

	                Table menuTable = new Table(4);
	                menuTable.setWidth(UnitValue.createPercentValue(100));
	                backgroundHandler = new BackgroundImageHandler(imgf);

	                // Function Header
	                String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
	                cell = new Cell(1, 4).add(new Paragraph(functionTitle).addStyle(funNameStyle));
	                cell.setTextAlignment(TextAlignment.CENTER);
	                cell.setBorder(Border.NO_BORDER);
	                cell.setPaddingTop(20f);
	                menuTable.addCell(cell);

	                String functionPerson = eventFunctionMasterResponseDto.getPax().toString();
	                cell = new Cell(1, 4).add(new Paragraph("Person      : " + functionPerson).addStyle(eventStyle));
	                cell.setTextAlignment(TextAlignment.CENTER);
	                cell.setBorder(Border.NO_BORDER);
	                cell.setPaddingTop(10);
	                menuTable.addCell(cell);

	                // Start Time
	                String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
	                        ? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
	                        : "TBD");
	                cell = new Cell(1, 4).add(new Paragraph("Start Time       : " + startTimeText).addStyle(eventStyle));
	                cell.setTextAlignment(TextAlignment.CENTER);
	                cell.setBorder(Border.NO_BORDER);
	                cell.setPaddingTop(10);
	                menuTable.addCell(cell);

	                List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto
	                        .getMenuCategories();

	                for (MenuReportResponseDto menuReportResponse : menuReportResponseDtos) {

	                    // Category Name
	                    System.out.println("inn");
	                    cell = new Cell(1, 4).add(new Paragraph(menuReportResponse.getNameEnglish()).addStyle(menuNameStyle));
	                    cell.setTextAlignment(TextAlignment.CENTER);
	                    cell.setBorder(Border.NO_BORDER);
	                    cell.setPaddingTop(20);
	                    cell.setPaddingBottom(5);
	                    menuTable.addCell(cell);

	                    // Category Slogan
	                    if (isCategorySlogan == 1) {
	                        String slogan = menuReportResponse.getSlogan();
	                        if (slogan != null && !slogan.trim().isEmpty()) {
	                            cell = new Cell(1, 4).add(new Paragraph(formatText(slogan, lang)).addStyle(menuSloganStyle));
	                            cell.setTextAlignment(TextAlignment.CENTER);
	                            cell.setBorder(Border.NO_BORDER);
	                            cell.setPaddingBottom(5);
	                            menuTable.addCell(cell);
	                        }
	                    }

	                    // Category Instructions
	                    if (isCategoryInstruction == 1) {
	                        String instruction = menuReportResponse.getMenuNotes();
	                        if (instruction != null && !instruction.trim().isEmpty()) {
	                            cell = new Cell(1, 4).add(new Paragraph(formatText(instruction, lang)).addStyle(menuInstStyle));
	                            cell.setTextAlignment(TextAlignment.CENTER);
	                            cell.setBorder(Border.NO_BORDER);
	                            cell.setPaddingBottom(5);
	                            menuTable.addCell(cell);
	                        }
	                    }

	                    // Category Image
	                    if (isCategoryImage == 1) {
	                        System.out.println("in 1");
	                        if (menuReportResponse.getImagePath() != null
	                                && !menuReportResponse.getImagePath().trim().isEmpty()) {
	                            try {
	                                System.out.println("in 2");
	                                System.out.println(menuReportResponse.getImagePath());
	                                ImageData catImgData = ImageDataFactory.create(menuReportResponse.getImagePath());
	                                Image img = new Image(catImgData);
	                                cell = new Cell(1, 4).add(img);
	                                cell.setHeight(200f);
	                                cell.setTextAlignment(TextAlignment.CENTER);
	                                cell.setBorder(Border.NO_BORDER);
	                                cell.setPaddingTop(5f);
	                                cell.setPaddingBottom(5);
	                                menuTable.addCell(cell);
	                            } catch (Exception e) {
	                                // If image loading fails, add empty cell
	                                cell = new Cell(1, 4).add(new Paragraph(" ").addStyle(menuInstStyle));
	                                cell.setTextAlignment(TextAlignment.CENTER);
	                                cell.setBorder(Border.NO_BORDER);
	                                cell.setPaddingBottom(5);
	                                menuTable.addCell(cell);
	                            }
	                        }
	                    }

	                    // Menu Items
	                    for (MenuItemForReportResponseDto itemRecipesResponseDto : menuReportResponse.getMenuItems()) {
	                        // Item Name
	                        String itemName = itemRecipesResponseDto.getNameEnglish() != null
	                                ? itemRecipesResponseDto.getNameEnglish()
	                                : "Unnamed Item";
	                        String formattedName = formatText(itemName, lang);
	                        cell = new Cell(1, 4).add(new Paragraph(formattedName).addStyle(itemNameStyle));
	                        cell.setTextAlignment(TextAlignment.CENTER);
	                        cell.setBorder(Border.NO_BORDER);
	                        cell.setPaddingTop(10f);
	                        cell.setPaddingBottom(5);
	                        menuTable.addCell(cell);

	                        // Item Slogan (if enabled)
	                        if (isItemSlogan == 1) {
	                            String itemSlogan = itemRecipesResponseDto.getSlogan();
	                            if (itemSlogan != null && !itemSlogan.trim().isEmpty()) {
	                                cell = new Cell(1, 4).add(new Paragraph(formatText(itemSlogan, lang)).addStyle(itemSloganStyle));
	                                cell.setTextAlignment(TextAlignment.CENTER);
	                                cell.setBorder(Border.NO_BORDER);
	                                cell.setPaddingBottom(5);
	                                menuTable.addCell(cell);
	                            }
	                        }

	                        // Item Instructions
	                        if (isItemInstruction == 1) {
	                            String itemInstruction = itemRecipesResponseDto.getItemNotes();
	                            if (itemInstruction != null && !itemInstruction.trim().isEmpty()) {
	                                cell = new Cell(1, 4).add(new Paragraph(formatText(itemInstruction, lang)).addStyle(itemInstStyle));
	                                cell.setTextAlignment(TextAlignment.CENTER);
	                                cell.setBorder(Border.NO_BORDER);
	                                cell.setPaddingBottom(5);
	                                menuTable.addCell(cell);
	                            }
	                        }
	                    }
	                }
	                menuTable.setNextRenderer(new TableWithBackgroundRenderer(menuTable, backgroundHandler));
	                document.add(menuTable);
	            }

	            // Last Page
	            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

	            frontPageTable = new Table(1);
	            frontPageTable.setWidth(UnitValue.createPercentValue(80));
	            frontPageTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

	            inputStream = getClass().getResourceAsStream("/flipbook/pages/Tiptop_Last.png");
	            if (inputStream == null) {
	                throw new FileNotFoundException("Image not found in resources!");
	            }
	            byte[] imageBytes2 = IOUtils.toByteArray(inputStream);
	            imgData = ImageDataFactory.create(imageBytes2);
	            imgf = new Image(imgData);
	            backgroundHandler = new BackgroundImageHandler(imgf);

	            tcellmain = new Cell().add(new Paragraph(" ").addStyle(normalStyle));
	            tcellmain.setTextAlignment(TextAlignment.CENTER);
	            tcellmain.setBorder(Border.NO_BORDER);
	            frontPageTable.addCell(tcellmain);
	            frontPageTable.setNextRenderer(new TableWithBackgroundRenderer(frontPageTable, backgroundHandler));

	            document.add(frontPageTable);

	            document.close();

	            String scheme = re.getScheme();
	            String serverName = re.getServerName();
	            int serverPort = re.getServerPort();
	            String contextPath = re.getContextPath();

	            String fullUrl;
	            if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
	                fullUrl = scheme + "://" + serverName + contextPath + "/api/download/pdf/" + eventDto.getEventNo()
	                        + "/ExclusiveMenu_" + datetimeforfile + ".pdf";
	            } else {
	                fullUrl = scheme + "://" + serverName + ":" + serverPort + contextPath + "/api/download/pdf/"
	                        + eventDto.getEventNo() + "/ExclusiveMenu_" + datetimeforfile + ".pdf";
	            }

	            return fullUrl;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to generate Event Menu Report", e);
	    }
	}
	
	public class HeaderAndFooter implements IEventHandler {
	    private PdfFont ex_pagenumberFont;
	    private PdfFont ex_pagenumberFontINC;
	    private PdfFont ex_pagenumberFontINC1;
	    
	    public HeaderAndFooter(PdfFont f1, PdfFont f2, PdfFont f3) {
	        this.ex_pagenumberFont = f1;
	        this.ex_pagenumberFontINC = f2;
	        this.ex_pagenumberFontINC1 = f3;
	    }
	    
	    @Override
	    public void onEvent(IEvent event) {
	        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
	        PdfDocument pdfDoc = docEvent.getDocument();
	        PdfPage page = docEvent.getPage();
	        int pno = pdfDoc.getPageNumber(page);
	        
	        if (pno > 1) {
	            PdfCanvas canvas = new PdfCanvas(page);
	            PdfFont font = pno < 10 ? ex_pagenumberFont :
	                           pno < 100 ? ex_pagenumberFontINC : ex_pagenumberFontINC1;
	            float x = page.getPageSize().getRight() - (pno < 10 ? 288 : 283);
	            float y = page.getPageSize().getBottom() - 27;
	            
	            canvas.beginText();
	            canvas.setFontAndSize(font, 12);
	            canvas.moveText(x, y);
	            canvas.showText(String.valueOf(pno));
	            canvas.endText();
	            canvas.release();
	        }
	    }

	}

	
	public class BackgroundImageHandler {
	    private Image image;
	    
	    public BackgroundImageHandler(Image image) {
	        this.image = image;
	    }
	    
	    public Image getImage() {
	        return image;
	    }
	}
	
	public class TableWithBackgroundRenderer extends TableRenderer {
	    private BackgroundImageHandler backgroundHandler;
	    
	    public TableWithBackgroundRenderer(Table modelElement, BackgroundImageHandler handler) {
	        super(modelElement);
	        this.backgroundHandler = handler;
	    }
	    
	    @Override
	    public void drawBackground(DrawContext drawContext) {
	        super.drawBackground(drawContext);
	        
	        if (backgroundHandler != null && backgroundHandler.getImage() != null) {
	            Rectangle area = getOccupiedAreaBBox();
	            PdfCanvas canvas = drawContext.getCanvas();
	            
	            canvas.saveState();
	            Image bgImage = backgroundHandler.getImage();
	            bgImage.scaleToFit(595, 842);
	            bgImage.setFixedPosition(0, 0);
	            
	            try {
	                canvas.addImageFittedIntoRectangle(
	                    bgImage.getProperty(Property.BACKGROUND_IMAGE), 
	                    new Rectangle(0, 0, 595, 842), 
	                    false
	                );
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            canvas.restoreState();
	        }
	    }
	    
	    @Override
	    public IRenderer getNextRenderer() {
	        return new TableWithBackgroundRenderer((Table) modelElement, backgroundHandler);
	    }
	}
	
	// Helper methods
	private Long getLong(Object o) {
	    return o == null ? null : ((Number) o).longValue();
	}

	private Integer getInt(Object o) {
	    return o == null ? null : ((Number) o).intValue();
	}

	private Double getDouble(Object o) {
	    return o == null ? null : ((Number) o).doubleValue();
	}

	private String getString(Object o) {
	    return o == null ? null : o.toString();
	}
	
	private PdfFont loadFont(String resourcePath) throws Exception {
	    InputStream is = getClass().getResourceAsStream(resourcePath);
	    if (is == null) {
	        throw new FileNotFoundException("Font not found: " + resourcePath);
	    }

	    byte[] fontBytes = IOUtils.toByteArray(is);

	    return PdfFontFactory.createFont(
	        fontBytes,
	        PdfEncodings.IDENTITY_H,
	        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
	    );
	}

	private String formatText(String text, Integer lang) {
	    if (text == null) return "";
	    
	    // Hindi = 1, Gujarati = 2
	    if (lang == 1 || lang == 2) {
	        return text.trim();
	    }

	    return WordUtils.capitalizeFully(text.trim());
	}
	
	private void loadLicense() {
	    try {
	        File licenseFile = new File("src/main/resources/itextkey.json");
	        if (licenseFile.exists()) {
	            System.out.println("Loading license from file");
	            com.itextpdf.licensing.base.LicenseKey.loadLicenseFile(licenseFile);
	            System.out.println("License loaded successfully");
	        }
	    } catch (Exception e) {
	        System.err.println("Failed to load license: " + e.getMessage());
	        System.err.println("Running in unlicensed mode - watermarks will appear");
	    }
	}
	
}
