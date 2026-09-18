package com.crmportal.service.impl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.UserLogsEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.UserLogsEntityRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.service.LogReportService;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class LogReportServiceImpl implements LogReportService {

	@Autowired
	Environment environment;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	UserLogsEntityRepository userLogsEntityRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Override
	public String generateLogReport(Long userid, HttpServletRequest re, String startDate,
			String endDate) {
		try {
			menuPreparationServiceImpl.loadLicense();

			PdfFont basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/log_reports/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			String reportName = getReportName(startDate, endDate);

			File pdfFile = new File(outputPath + "/" + reportName + ".pdf");

			LocalDateTime start = null;
			LocalDateTime end = null;

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			if (startDate != null && !startDate.trim().isEmpty()) {
				LocalDate startLocalDate = LocalDate.parse(startDate, dateFormatter);
				start = startLocalDate.atStartOfDay();
			}

			if (endDate != null && !endDate.trim().isEmpty()) {
				LocalDate endLocalDate = LocalDate.parse(endDate, dateFormatter);

				end = endLocalDate.atTime(23, 59, 59);
			}

			List<String> userEmails = new ArrayList<String>();

			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userid)
					.orElseThrow(() -> new RuntimeException("User not found with id : " + userid));

			userEmails.add(user.getEmail());
			
			List<UserLogsEntity> logs = userLogsEntityRepository.findLogsWithFilters(userEmails, start, end, null,
					null).stream().filter(log -> !log.getIpAddress().equalsIgnoreCase("103.1.101.244")).collect(Collectors.toList());

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(25, 30, 45, 30);

			Paragraph title = new Paragraph("USER LOG REPORT").setFont(basicFont).setFontSize(20).simulateBold()
					.setTextAlignment(TextAlignment.CENTER).setMarginBottom(5);
			document.add(title);

			String dateRange = "";

			if (startDate != null && !startDate.trim().isEmpty()) {
				dateRange = startDate;
			}

			if (endDate != null && !endDate.trim().isEmpty()) {
				dateRange = dateRange + " - " + endDate;
			}

			if (!dateRange.trim().isEmpty()) {

				Paragraph period = new Paragraph(dateRange).setFont(basicFont).setFontSize(11)
						.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15);

				document.add(period);
			}

			Paragraph total = new Paragraph("Total Records : " + logs.size()).setFont(basicFont).setFontSize(11)
					.setMarginBottom(10);

			document.add(total);

			float[] columnWidths = { 35f, 110f, 70f, 80f, 80f, 240f };

			Table table = new Table(UnitValue.createPointArray(columnWidths));
			table.setWidth(UnitValue.createPercentValue(100));

			addLogHeaderCell(table, "No.", basicFont);
			addLogHeaderCell(table, "User", basicFont);
			addLogHeaderCell(table, "Event Type", basicFont);
			addLogHeaderCell(table, "Date & Time", basicFont);
			addLogHeaderCell(table, "IP Address", basicFont);
			addLogHeaderCell(table, "Description", basicFont);

			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");

			int srNo = 1;

			for (UserLogsEntity log : logs) {
				addLogCell(table, String.valueOf(srNo++), basicFont);
				addLogCell(table, log.getUser() != null ? log.getUser() : "", basicFont);
				addLogCell(table, log.getEventType() != null ? log.getEventType() : "", basicFont);

				String createdAt = "";

				if (log.getCreateAt() != null) {
					createdAt = log.getCreateAt().format(outputFormatter);
				}

				addLogCell(table, createdAt, basicFont);
				addLogCell(table, log.getIpAddress() != null ? log.getIpAddress() : "", basicFont);
				addLogCell(table, log.getDescription() != null ? log.getDescription() : "", basicFont);
			}

			if (logs.isEmpty()) {
				Cell emptyCell = new Cell(1, 7).add(new Paragraph("No log records found.").setFont(basicFont)
						.setFontSize(12).setTextAlignment(TextAlignment.CENTER));

				table.addCell(emptyCell);
			}

			document.add(table);

			// ================= PAGE NUMBERS =================
			int totalPages = pdfDocument.getNumberOfPages();
			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);
				canvas.close();
			}
						
			document.flush();
			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/log_reports/" + reportName + ".pdf";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Log Report", e);
		}
	}

	private void addLogHeaderCell(Table table, String text, PdfFont font) {
		Cell cell = new Cell();

		Paragraph paragraph = new Paragraph(text).setFont(font).setFontSize(10).simulateBold()
				.setTextAlignment(TextAlignment.CENTER);
		cell.add(paragraph);
		cell.setPadding(5);
		table.addHeaderCell(cell);
	}

	private void addLogCell(Table table, String text, PdfFont font) {
		Cell cell = new Cell();

		Paragraph paragraph = new Paragraph(text != null ? text : "").setFont(font).setFontSize(9)
				.setTextAlignment(TextAlignment.LEFT);
		cell.add(paragraph);
		cell.setPadding(4);
		table.addCell(cell);
	}
	
	private String getReportName(String startDate, String endDate) {
		StringBuilder reportName = new StringBuilder("LOG_REPORT");

		if (startDate != null && !startDate.trim().isEmpty()) {
			reportName.append("_FROM_").append(startDate.trim().replace("/", "-"));
		}

		if (endDate != null && !endDate.trim().isEmpty()) {
			reportName.append("_TO_").append(endDate.trim().replace("/", "-"));
		}

		return reportName.toString();
	}
}
