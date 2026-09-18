package com.crmportal.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.EmployeeLeadPerformanceForReportResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyWithItemsResponseDto;
import com.crmportal.response.dto.MenuAllocationItemsResponseDto;
import com.crmportal.service.EmployeeReportService;
import com.crmportal.service.PipelineService;
import com.itextpdf.io.image.ImageData;
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
public class EmployeeReportServiceImpl implements EmployeeReportService {

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

	@Override
	public String employeePerformaceReport(Long employeeId, String startDate, String endDate, Integer lang, Long pipelineId,
			HttpServletRequest re,Long userId) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String srNoLabel, assignDateLabel, expiredDateLabel, employeeNameLabel, hotLeadLabel, coldLeadLabel, 
			wonLeadLabel, lostLeadLabel, totalLeadLabel, phoneLabel, qtyScoreLabel;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				boldFont =  menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 512;
				srNoLabel = "क्रम संख्या";
				assignDateLabel = "असाइन तिथि";
				expiredDateLabel = "समाप्ति तिथि";
				employeeNameLabel = "कर्मचारी का नाम";
				hotLeadLabel = "हॉट लीड";
				coldLeadLabel = "कोल्ड लीड";
				wonLeadLabel = "जीती हुई लीड";
				lostLeadLabel = "हारी हुई लीड";
				totalLeadLabel = "कुल लीड";
				phoneLabel = "मोबाइल नंबर";
				qtyScoreLabel = "गुणवत्ता अंक";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				boldFont =  menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 510;
				srNoLabel = "ક્રમ નંબર";
				assignDateLabel = "સોંપણી તારીખ";
				expiredDateLabel = "સમાપ્તિ તારીખ";
				employeeNameLabel = "કર્મચારીનું નામ";
				hotLeadLabel = "હોટ લીડ";
				coldLeadLabel = "કોલ્ડ લીડ";
				wonLeadLabel = "જીતેલી લીડ";
				lostLeadLabel = "હારેલી લીડ";
				totalLeadLabel = "કુલ લીડ";
				phoneLabel = "મોબાઇલ નંબર";
				qtyScoreLabel = "ગુણવત્તા ગુણાંક";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont("/fonts/arial-regular.ttf");
				boldFont = PdfFontFactory.createFont("/fonts/arial-bold.ttf");
				System.out.println("English font loaded successfully");
				srNoLabel = "Sr No.";
				assignDateLabel = "Assign Date";
				expiredDateLabel = "Expired Date";
				employeeNameLabel = "Employee Name";
				hotLeadLabel="Hot Lead";
				coldLeadLabel="Cold Lead";
				wonLeadLabel="Won Lead";
				lostLeadLabel="Lost Lead";
				totalLeadLabel="Total Lead";
				phoneLabel = "Mobile No.";
				qtyScoreLabel = "Quality Score";
			}
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			Color mainColor = new DeviceRgb(0, 0, 0);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = null;

			outputPath = new File(rootPath + "resources/tempDownload/employee_report");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			UserMasterEntity employee = null;
			File pdfFile = null;
			if (employeeId != 1) {
				employee = userMasterRepository.findByIdAndIsDeleteFalse(employeeId)
						.orElseThrow(() -> new RuntimeException("Employee not found with id : " + employeeId));
				pdfFile = new File(outputPath + "/" + employee.getFirstName() + "_" + employee.getLastName() + ".pdf");
			} else {
				pdfFile = new File(outputPath + "/ALL(" + startDate.replace("/", "_") + ").pdf");
			}
			System.out.println("1");
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4.rotate(), false);
			document.setMargins(20, 20, 50, 20);

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);
			headerTable.setWidth(UnitValue.createPercentValue(100));
			headerTable.setFixedLayout();

			ImageData logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

			Image logo = new Image(logoData);

			// Resize & align
			logo.setWidth(100f);
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			Cell cell;

			cell = new Cell(3, 2).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
					.setPaddingBottom(10f);
			headerTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph("THE WEB MINDS TECHNOLOGISIES PVT. LTD.").setFont(basicFont).setFontSize(14)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
					.setBorder(Border.NO_BORDER).setPaddingLeft(15f);
			headerTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph()
							.add(new Text(phoneLabel + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).simulateBold())
							.add(new Text("1234567890").setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f)
					.setPaddingLeft(lang == 0 ? 14f : 16f);
			headerTable.addCell(cell);

			headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			
			cell = new Cell(1, 3)
					.add(new Paragraph("EMPLOYEE PERFORMANCE REPORT").setFont(basicFont).setFontSize(14)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.CENTER).simulateBold())
					.setBorder(Border.NO_BORDER).setPaddingLeft(15f);
			headerTable.addCell(cell);
			
			cell = new Cell(1, 3)
					.add(new Paragraph(startDate + " TO " + endDate).setFont(basicFont).setFontSize(12)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER);
			headerTable.addCell(cell);
			
			document.add(headerTable);

			Table employeeTable = new Table(UnitValue.createPercentArray(new float[] { 5f, 11f, 25f, 9f, 9f, 9f, 9f, 11f , 11f }), false);
			employeeTable.setWidth(UnitValue.createPercentValue(100));
			employeeTable.setFixedLayout();
			employeeTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
			employeeTable.setMarginTop(20f);

			cell = new Cell()
					.add(new Paragraph(srNoLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(assignDateLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(expiredDateLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(employeeNameLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(hotLeadLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(coldLeadLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(wonLeadLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(lostLeadLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			cell = new Cell()
					.add(new Paragraph(totalLeadLabel).setFont(boldFont))
                    .setTextAlignment(TextAlignment.CENTER);
			employeeTable.addHeaderCell(cell);
			
			List<EmployeeLeadPerformanceForReportResponseDto> employees = pipelineService.getEmployeePerformanceForReport(employeeId, startDate, endDate, pipelineId,userId);
			System.out.println("size : " + employees.size());
			int count = 0;
			for(EmployeeLeadPerformanceForReportResponseDto emp : employees) {
				cell = new Cell()
						.add(new Paragraph(String.valueOf(++count)).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
				cell = new Cell()
						.add(new Paragraph(emp.getAssignDate()).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
				cell = new Cell()
						.add(new Paragraph(emp.getUserName()).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
				cell = new Cell()
						.add(new Paragraph(emp.getHotLeads().toString()).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
				cell = new Cell()
						.add(new Paragraph(emp.getColdLeads().toString()).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
				cell = new Cell()
						.add(new Paragraph(emp.getWonLeads().toString()).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
				cell = new Cell()
						.add(new Paragraph(emp.getLostLeads().toString()).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
				cell = new Cell()
						.add(new Paragraph(emp.getTotalLeads().toString()).setFont(basicFont))
	                    .setTextAlignment(TextAlignment.CENTER);
				employeeTable.addHeaderCell(cell);
				
			}
			document.add(employeeTable);
			document.close();

			String fullUrl = null;
			if (employeeId != 1) {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/employee_report/"+ employee.getFirstName() + "_" + employee.getLastName() + ".pdf";
			} else {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/employee_report/ALL("+ startDate.replace("/", "_") + ").pdf";
			}

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}
}
