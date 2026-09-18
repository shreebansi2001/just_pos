package com.crmportal.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PipelineRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.EmployeeLeadPerformanceForReportResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyWithItemsResponseDto;
import com.crmportal.response.dto.MenuAllocationItemsResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.response.dto.OrderSummaryResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EmployeeReportService;
import com.crmportal.service.LeadReportService;
import com.crmportal.service.PipelineService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
public class LeadReportServiceImpl implements LeadReportService {

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Autowired
	PipelineRepository pipelineRepository;

	@Autowired
	PipelineService pipelineService;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	Environment environment;

	@Autowired
	CommonService commonService;

	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;

	@Autowired
	LeadMasterServiceImpl leadMasterServiceImpl;

	public String formatDate(String date) {
		DateTimeFormatter input = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		DateTimeFormatter output = DateTimeFormatter.ofPattern("dd_MM_yyyy");
		if (date == null || date.isEmpty()) {
			return "";
		}
		LocalDate localDate = LocalDate.parse(date, input);
		return localDate.format(output);
	}

	@Override
	public String generateLeadReport(String startDate, String endDate, Long statusId, Long sourceId, String priority,
			List<Long> leadAssignId, Integer isCompanyDetails, HttpServletRequest re, Long userid) {
		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();
			int headFontSize = 13;
			int fontSize = 10;
			float lineHeight = 1.1f;

			basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/leadmodule");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/lead_summary_report_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4.rotate());
			document.setMargins(40, 35, 50, 20);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			MenuQuantityReponseDto cmpData = menuItemRawMaterialServiceImpl.getCmpData(userid);

			String cmpName = cmpData != null ? commonService.getString(cmpData.getCompanyName()) : "";
			String cmpCountryCode = cmpData != null ? commonService.getString(cmpData.getCountryCode()) : "";
			String cmpMobile = cmpData != null ? commonService.getString(cmpData.getOfficeNo()) : "";
			String cmpEmail = cmpData != null ? commonService.getString(cmpData.getCompanyEmail()) : "";
			String logo = cmpData != null ? commonService.getString(cmpData.getLogo()) : "";
			String companyAddress = cmpData != null ? commonService.getString(cmpData.getCompanyAddress()) : "";

			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			Image img = new Image(imgData);
//			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(100);
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
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				cmpHead.setMarginBottom(7f);
				document.add(cmpHead);
			}
			System.out.println("start date : " + startDate);
			LocalDate firstDate = startDate != null && startDate.trim().length() != 0 ? commonService.dateFormatted(startDate) : null;
			LocalDate lastDate = endDate != null && endDate.trim().length() != 0 ? commonService.dateFormatted(endDate) : null;

			Paragraph para = new Paragraph().add(new Text("DATEWISE LEAD SUMMARY"))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16)
					.setTextAlignment(TextAlignment.CENTER);

			document.add(para);

			float[] cw2 = { 50, 50 };
			Table dTable = new Table(UnitValue.createPercentArray(cw2));
			dTable.setWidth(UnitValue.createPercentValue(100));
			dTable.setMarginBottom(7f);

			para = new Paragraph().add(new Text("From Date: " + startDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			Cell cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			para = new Paragraph().add(new Text("To Date: " + endDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			document.add(dTable);

			
			  List<LeadMasterResponseDto> summaryResponseDtos = leadMasterServiceImpl
			  .getDatewiseLeadSummaryReport(firstDate, lastDate, statusId, sourceId, priority,
			  leadAssignId, userid);
			  
			  // --- Header Table (separate, so it doesn't get pulled with data) ---
			  float[] cw = { 12, 10, 11, 7, 7, 7, 11, 10, 11, 13 };
			  
			  Table detailTable = new Table(UnitValue.createPercentArray(cw));
			  detailTable.setWidth(UnitValue.createPercentValue(100));
			  
			  String[] headers = { "CLIENT NAME", "CONTACT NO", "LEAD ASSIGN", "STATUS",
			  "SOURCE", "PRIORITY" , "EVENT", "PAX", "EVENT DATE", "REMARK" }; 
			  for (String header :headers) 
			  { 
				  para = new Paragraph().add(new Text(header)).setFont(boldFont).setFontSize(headFontSize).setTextAlignment(TextAlignment.CENTER); cell = new Cell().add(para);
				  detailTable.addCell(cell); 
				  
			  } 
			  //document.add(detailTable);
			  
			  // Details Table Table detailTable = new
				/*
				 * Table detailTable = new Table(UnitValue.createPercentArray(cw));
				 * detailTable.setWidth(UnitValue.createPercentValue(100));
				 */
			  
			  for (LeadMasterResponseDto dto : summaryResponseDtos) {
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getClientName())) .setFont(basicFont)
			  .setFontSize(fontSize)));
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getContactNumber()))
			  .setFont(basicFont) .setFontSize(fontSize)));
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getLeadAssignName()))
			  .setFont(basicFont) .setFontSize(fontSize)));
			  
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getLeadStatusName()))
			  .setFont(basicFont) .setFontSize(fontSize)));
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getLeadSourceName()))
			  .setFont(basicFont) .setFontSize(fontSize)));
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getLeadPriority()))
			  .setFont(basicFont) .setFontSize(fontSize)));
			  
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getEventTypeName()))
			  .setFont(basicFont) .setFontSize(fontSize)));
			  
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getPerson())) .setFont(basicFont)
			  .setFontSize(fontSize)));
			  
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getInquiryDate())) .setFont(basicFont)
			  .setFontSize(fontSize)));
			  
			  detailTable.addCell(new Cell().add( new
			  Paragraph(commonService.getString(dto.getLeadRemark())) .setFont(basicFont)
			  .setFontSize(fontSize))); }
			  
			  document.add(detailTable);
			 

			document.close();

			String fullUrl = environment.getProperty("ws_image_path")
					+ "/api/download/pdf/leadmodule/lead_summary_report_" + datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}

	@Override
	public String generateFollowupReport(String startDate, String endDate, Long statusId, Long sourceId, String priority,
			List<Long> leadAssignId, Integer isCompanyDetails, HttpServletRequest re, Long userid) {
		try {

			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();
			int headFontSize = 13;
			int fontSize = 10;
			float lineHeight = 1.1f;

			basicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/followupmodule");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!");
				}
			}

			File pdfFile = new File(outputPath + "/followup_summary_report_" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4.rotate());
			document.setMargins(40, 35, 50, 20);

			float[] columnWidthHead = { 20f, 18f, 2f, 57f };
			Table cmpHead = new Table(UnitValue.createPercentArray(columnWidthHead));

			MenuQuantityReponseDto cmpData = menuItemRawMaterialServiceImpl.getCmpData(userid);

			String cmpName = cmpData != null ? commonService.getString(cmpData.getCompanyName()) : "";
			String cmpCountryCode = cmpData != null ? commonService.getString(cmpData.getCountryCode()) : "";
			String cmpMobile = cmpData != null ? commonService.getString(cmpData.getOfficeNo()) : "";
			String cmpEmail = cmpData != null ? commonService.getString(cmpData.getCompanyEmail()) : "";
			String logo = cmpData != null ? commonService.getString(cmpData.getLogo()) : "";
			String companyAddress = cmpData != null ? commonService.getString(cmpData.getCompanyAddress()) : "";

			ImageData imgData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			Image img = new Image(imgData);
//			Image img = new Image(ImageDataFactory.create(environment.getProperty("app.image.url") + logo));
			img.setWidth(100);
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
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpCountryCode + " " + cmpMobile)
							.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph().add(
					new Text("Email").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmp.setPaddingLeft(10f);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(":").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			cmpHead.addCell(cmp);

			cmpPara = new Paragraph()
					.add(new Text(cmpEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14))
					.setMultipliedLeading(1f);
			cmp = new Cell().add(cmpPara).setBorder(Border.NO_BORDER).setMarginBottom(3f);
			cmpHead.addCell(cmp);

			if (isCompanyDetails == 1) {
				cmpHead.setMarginBottom(7f);
				document.add(cmpHead);
			}
			System.out.println("start date : " + startDate);
			LocalDate firstDate = startDate != null && startDate.trim().length() != 0 ? commonService.dateFormatted(startDate) : null;
			LocalDate lastDate = endDate != null && endDate.trim().length() != 0 ? commonService.dateFormatted(endDate) : null;

			Paragraph para = new Paragraph().add(new Text("DATEWISE FOLLOW UP SUMMARY"))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(16)
					.setTextAlignment(TextAlignment.CENTER);

			document.add(para);

			float[] cw2 = { 50, 50 };
			Table dTable = new Table(UnitValue.createPercentArray(cw2));
			dTable.setWidth(UnitValue.createPercentValue(100));
			dTable.setMarginBottom(7f);

			para = new Paragraph().add(new Text("From Date: " + startDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			Cell cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			para = new Paragraph().add(new Text("To Date: " + endDate))
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(13)
					.setTextAlignment(TextAlignment.CENTER);
			cell = new Cell().add(para).setBorder(Border.NO_BORDER);
			dTable.addCell(cell);

			document.add(dTable);

			
			  List<LeadMasterResponseDto> summaryResponseDtos = leadMasterServiceImpl
			  .getDatewiseFollowupSummaryReport(firstDate, lastDate, statusId, sourceId, priority,
			  leadAssignId, userid);
			  
			  // --- Header Table (separate, so it doesn't get pulled with data) ---
			  float[] cw = {
					    5f,   // SR NO
					    13f,  // CLIENT NAME
					    10f,  // CONTACT NO
					    12f,  // COMPANY NAME
					    9f,   // LEAD SOURCE
					    10f,  // LEAD GENERATE DATE
					    10f,  // FOLLOW UP DATE
					    8f,   // FOLLOW UP MODE
					    13f,  // DISCUSSION
					    8f,   // LEAD STATUS
					    7f,   // LEAD PRIORITY
					    8f,   // EXPECTED VALUE
					    10f   // COORDINATOR NAME
					};
			  
			  Table detailTable = new Table(UnitValue.createPercentArray(cw));
			  detailTable.setWidth(UnitValue.createPercentValue(100));
			  
			  String[] headers = {
					    "SR. NO.",
					    "CLIENT NAME",
					    "CONTACT NO",
					    "COMPANY NAME",
					    "LEAD SOURCE",
					    "LEAD GENERATE DATE",
					    "FOLLOW UP DATE",
					    "FOLLOW UP MODE",
					    "DISCUSSION",
					    "LEAD STATUS",
					    "LEAD PRIORITY",
					    "EXPECTED VALUE",
					    "COORDINATOR NAME"
					};
			  for (String header :headers) 
			  { 
				  para = new Paragraph().add(new Text(header)).setFont(boldFont).setFontSize(headFontSize).setTextAlignment(TextAlignment.CENTER); cell = new Cell().add(para);
				  detailTable.addCell(cell); 
				  
			  } 
			  //document.add(detailTable);
			  
			  // Details Table Table detailTable = new
				/*
				 * Table detailTable = new Table(UnitValue.createPercentArray(cw));
				 * detailTable.setWidth(UnitValue.createPercentValue(100));
				 */
			  
			  int srNo = 1;

			  for (LeadMasterResponseDto dto : summaryResponseDtos) {

			      detailTable.addCell(new Cell().add(
			              new Paragraph(String.valueOf(srNo++))
			                      .setFont(basicFont)
			                      .setFontSize(fontSize)
			                      .setTextAlignment(TextAlignment.CENTER)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getClientName()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getContactNumber()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getCompanyName()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getLeadSourceName()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getInquiryDate()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getFollowUpDate()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getFollowUpMode()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getDiscussion()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getLeadStatusName()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getLeadPriority()))
			                      .setFont(basicFont).setFontSize(fontSize)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getDealValue()))
			                      .setFont(basicFont).setFontSize(fontSize)
			                      .setTextAlignment(TextAlignment.RIGHT)));

			      detailTable.addCell(new Cell().add(
			              new Paragraph(commonService.getString(dto.getCoordinatorName()))
			                      .setFont(basicFont).setFontSize(fontSize)));
			  }

			  
			  
			  document.add(detailTable);
			 

			document.close();

			String fullUrl = environment.getProperty("ws_image_path")
					+ "/api/download/pdf/followupmodule/followup_summary_report_" + datetimeforfile + ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Report generation failed.");
		}

	}
}
