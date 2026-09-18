package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AccountContactEntity;
import com.crmportal.entity.IncomeExpenseTypeEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.AccountContactRepository;
import com.crmportal.repository.IncomeExpenseTypeRepository;
import com.crmportal.repository.OfficeExpenseDocRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.AllExpensesReportResponseDto;
import com.crmportal.response.dto.AllExpensesResponseDto;
import com.crmportal.response.dto.ExpenseDetailResponseDto;
import com.crmportal.response.dto.ExpenseResponseDto;
import com.crmportal.response.dto.OfficeExpenseResponseDto;
import com.crmportal.service.ExpenseReportService;
import com.crmportal.service.ExpenseService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
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
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class ExpenseReportServiceImpl implements ExpenseReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	ExpenseService expenseService;

	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	AccountContactRepository accountContactRepository;
	
	@Autowired
	IncomeExpenseTypeRepository incomeExpenseTypeRepository;
	
	@Autowired
	OfficeExpenseDocRepository officeExpenseDocRepository;

	public static String getReportName(String userName, String startDate, String endDate, String expenseType) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		LocalDate start = startDate != null ? LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				: null;
		LocalDate end = endDate != null ? LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;

		String stDate = startDate != null ? start.format(formatter) : "NA";
		String eDate = endDate != null ? end.format(formatter) : "NA";
		String safeUserName = userName != null ? userName.replaceAll("[^a-zA-Z0-9]", "") : "All";
		String safeType = expenseType != null ? expenseType.replaceAll("[^a-zA-Z0-9]", "") : "Expense";

		return safeUserName + "_" + stDate + "_to_" + eDate + "_" + safeType;
	}

	private Cell addHeaderTableCell(String label, PdfFont font, Color fontColor, Color borderColor,
			TextAlignment textAlignment) {
		return new Cell()
				.add(new Paragraph(label).setFont(font).setFontSize(16f).setFontColor(fontColor).setFixedLeading(16f)
						.setTextAlignment(textAlignment))
				.setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(borderColor, 0.5f))
				.setBorderBottom(new SolidBorder(borderColor, 0.5f)).setPadding(5f);
	}

	private Cell addDetailsTableCell(String label, PdfFont font, Color fontColor, Color borderColor,
			TextAlignment textAlignment) {
		return new Cell().add(new Paragraph(label).setFont(font).setFontSize(14f).setFontColor(fontColor)
				.setFixedLeading(14f).setTextAlignment(textAlignment)).setPadding(5f)
				.setBorder(new SolidBorder(borderColor, 0.5f));
	}

	private Cell addExpenseTableHeaderCell(String label, PdfFont font, Color fontColor, Color borderColor,
			Integer mergeCell, TextAlignment textAlignment, VerticalAlignment verticalAlignment, float fontSize) {
		return new Cell(1, mergeCell)
				.add(new Paragraph(label).setFont(font).setFontSize(fontSize).setFontColor(fontColor)
						.setFixedLeading(fontSize))
				.setPadding(5f).setBorder(new SolidBorder(borderColor, 0.5f)).setVerticalAlignment(verticalAlignment)
				.setTextAlignment(textAlignment);
	}

	private Cell addExpenseTableDetailCell(String label, PdfFont font, Color fontColor, Color borderColor,
			Integer mergeCell, TextAlignment textAlignment, VerticalAlignment verticalAlignment) {
		return new Cell(1, mergeCell)
				.add(new Paragraph(label).setFont(font).setFontSize(11f).setFontColor(fontColor).setFixedLeading(11f))
				.setPadding(5f).setBorder(new SolidBorder(borderColor, 0.5f)).setVerticalAlignment(verticalAlignment)
				.setTextAlignment(textAlignment);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String generateTripExpenseReport(Long userId, String type, Long expenseId, String startDate,
			String endDate, HttpServletRequest re, Long accountContactId) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			System.out.println("English font loaded successfully");

			List<ExpenseResponseDto> expenses = new ArrayList<ExpenseResponseDto>();
			BigDecimal totalExpense = BigDecimal.ZERO;
			BigDecimal remainingExpense = BigDecimal.ZERO;
			BigDecimal paidExpense = BigDecimal.ZERO;

			String name = accountContactRepository.getNameByUserId(userId).toUpperCase();
			
			if (expenseId == -1) {
				Map<String, Object> data = expenseService.getTripExpenseByExpenseType("TRIP", userId, startDate,
						endDate, accountContactId);

				expenses = (List<ExpenseResponseDto>) data.get("data");
				totalExpense = (BigDecimal) data.get("total_expense");
				remainingExpense = (BigDecimal) data.get("remaing_expense");
				paidExpense = (BigDecimal) data.get("paid_expense");
			} else {
				ExpenseResponseDto data = expenseService.getByExpenseId(expenseId);
				expenses.add(data);
			}

			if (expenses.isEmpty()) {
				return "Data not found.";
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/expense/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			File pdfFile = null;

			if (expenseId == -1) {
				pdfFile = new File(outputPath + "/" + getReportName(name,
						startDate, endDate, "Trip Expense") + ".pdf");
			} else {
				startDate = expenses.get(0).getFromDate();
				endDate = expenses.get(0).getToDate();
				pdfFile = new File(outputPath + "/" + getReportName(name,
						startDate, endDate, "Trip Expense") + ".pdf");
			}

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 20, 20);

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setFixedLayout();

			Color headingColor = new DeviceRgb(115, 99, 67);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			cell = addHeaderTableCell("TRIP EXPENCES", basicFont, headingColor, headingColor, TextAlignment.CENTER);
			headerTable.addCell(cell);

			Boolean isFirst = true;
			for (ExpenseResponseDto exp : expenses) {
				if (!isFirst) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					headerTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
					headerTable.setWidth(UnitValue.createPercentValue(100f));
					headerTable.setFixedLayout();
				}
				isFirst = false;
				String employeeName = exp.getAccountContactName() != null ? exp.getAccountContactName().toUpperCase() : "";
//				String employeeName = "";
				String tripName = exp.getTitle() != null ? exp.getTitle().toUpperCase() : "";
				String tripFromDate = exp.getFromDate() != null ? exp.getFromDate() : "";
				String tripToDate = exp.getToDate() != null ? exp.getToDate() : "";

				cell = addHeaderTableCell(employeeName, basicFont, headingColor, headingColor, TextAlignment.CENTER);
				headerTable.addCell(cell);

				document.add(headerTable);

				Table tripDetailTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }));
				tripDetailTable.setWidth(UnitValue.createPercentValue(100f));
				tripDetailTable.setFixedLayout();
				tripDetailTable.setMarginTop(10f);

				cell = addDetailsTableCell("TRIP NAME", basicFont, headingColor, headingColor, TextAlignment.LEFT);
				tripDetailTable.addCell(cell);

				cell = addDetailsTableCell(tripName, basicFont, blackColor, headingColor, TextAlignment.LEFT);
				tripDetailTable.addCell(cell);

				cell = addDetailsTableCell("DATE", basicFont, headingColor, headingColor, TextAlignment.LEFT);
				tripDetailTable.addCell(cell);

				cell = addDetailsTableCell(tripFromDate + " - " + tripToDate, basicFont, blackColor, headingColor,
						TextAlignment.LEFT);
				tripDetailTable.addCell(cell);

				document.add(tripDetailTable);

				Table tripExpenseTable = new Table(
						UnitValue.createPercentArray(new float[] { 30f, 20f, 20f, 15f, 15f }));
				tripExpenseTable.setWidth(UnitValue.createPercentValue(100f));
				tripExpenseTable.setFixedLayout();
				tripExpenseTable.setMarginTop(10f);

				cell = addExpenseTableHeaderCell("DESCRIPTION", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				tripExpenseTable.addCell(cell);

				cell = addExpenseTableHeaderCell("PAYMENT MODE", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				tripExpenseTable.addCell(cell);

				cell = addExpenseTableHeaderCell("EXPENSE DATE", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				tripExpenseTable.addCell(cell);

				cell = addExpenseTableHeaderCell("TRIP KM", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				tripExpenseTable.addCell(cell);

				cell = addExpenseTableHeaderCell("AMOUNT", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				tripExpenseTable.addCell(cell);

				BigDecimal totalAmount = BigDecimal.ZERO;
				List<String> docPaths = new ArrayList<>();
				for (ExpenseDetailResponseDto expense : exp.getDetailRequestDtos()) {
					String perticulars = expense.getPerticular() != null ? expense.getPerticular() : "";
					String paymentMode = expense.getPaymentMode() != null ? expense.getPaymentMode() : "";
					String expenseDate = expense.getExpenseDate() != null ? expense.getExpenseDate() : "";
					Long km = expense.getKm() != null ? expense.getKm() : Long.valueOf(0);
					BigDecimal amount = expense.getAmount() != null ? expense.getAmount() : BigDecimal.ZERO;
					String path = expense.getDocPath() != null ? expense.getDocPath() : "";

					if (path.trim().length() != 0 && !path.contains("null")) {
						docPaths.add(path);
					}

					totalAmount = totalAmount.add(amount);

					cell = addExpenseTableDetailCell(perticulars, basicFont, blackColor, headingColor, 1,
							TextAlignment.CENTER, VerticalAlignment.MIDDLE);
					tripExpenseTable.addCell(cell);

					cell = addExpenseTableDetailCell(paymentMode, basicFont, blackColor, headingColor, 1,
							TextAlignment.CENTER, VerticalAlignment.MIDDLE);
					tripExpenseTable.addCell(cell);

					cell = addExpenseTableDetailCell(expenseDate, basicFont, blackColor, headingColor, 1,
							TextAlignment.CENTER, VerticalAlignment.MIDDLE);
					tripExpenseTable.addCell(cell);

					cell = addExpenseTableDetailCell(km.toString(), basicFont, blackColor, headingColor, 1,
							TextAlignment.CENTER, VerticalAlignment.MIDDLE);
					tripExpenseTable.addCell(cell);

					cell = addExpenseTableDetailCell(amount.toString(), basicFont, blackColor, headingColor, 1,
							TextAlignment.CENTER, VerticalAlignment.MIDDLE);
					tripExpenseTable.addCell(cell);
				}

				cell = addExpenseTableDetailCell("TOTAL AMOUNT", basicFont, headingColor, headingColor, 4, TextAlignment.RIGHT,
						VerticalAlignment.MIDDLE);
				tripExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(totalAmount.toString(), basicFont, blackColor, headingColor, 4,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE);
				tripExpenseTable.addCell(cell);
				
				document.add(tripExpenseTable);

				if (!docPaths.isEmpty()) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					Rectangle pageSize = pdfDocument.getDefaultPageSize();

					float usableWidth = pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin();
					float usableHeight = pageSize.getHeight() - document.getTopMargin() - document.getBottomMargin()
							- 2;

					float imgWidth = usableWidth / 2;
					float imgHeight = usableHeight / 3;

					Table imageTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
					imageTable.setWidth(UnitValue.createPercentValue(100));

					for (String doc : docPaths) {
						try {
							ImageData imageData = ImageDataFactory.create(doc);
							Image image = new Image(imageData);

							image.setWidth(imgWidth);
							image.setHeight(imgHeight);
							image.setAutoScale(false);

							cell = new Cell().add(image).setPadding(0);
							imageTable.addCell(cell);
						} catch (Exception ex) {
							System.out.println("Unable to load attachment: " + doc);
						}
					}

					document.add(imageTable);
				}
			}

			if (expenseId == -1) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				Table totalAmountTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
				totalAmountTable.setWidth(UnitValue.createPercentValue(100f));
				totalAmountTable.setFixedLayout();

				cell = addExpenseTableHeaderCell("TOTAL AMOUNT", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				totalAmountTable.addCell(cell);

				cell = addExpenseTableHeaderCell(totalExpense.toString(), basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				totalAmountTable.addCell(cell);

				cell = addExpenseTableHeaderCell("PAID AMOUNT", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				totalAmountTable.addCell(cell);

				cell = addExpenseTableHeaderCell(paidExpense.toString(), basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				totalAmountTable.addCell(cell);

				cell = addExpenseTableHeaderCell("UNPAID AMOUNT", basicFont, headingColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				totalAmountTable.addCell(cell);

				cell = addExpenseTableHeaderCell(remainingExpense.toString(), basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
				totalAmountTable.addCell(cell);

				document.add(totalAmountTable);
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/expense/"
					+ getReportName(name, startDate, endDate, "Trip Expense")
					+ ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String generateOfficeExpenseReport(Long memberId, Long incomeExpenseTypeId, String startDate, String endDate,
			HttpServletRequest re, Long accountContactId) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			System.out.println("English font loaded successfully");

			Map<String, Object> data = expenseService.getOfficeExpenseByExpenseType(incomeExpenseTypeId, memberId, startDate, endDate, accountContactId);

			if (data == null || ((List<OfficeExpenseResponseDto>) data.get("data")).isEmpty()) {
				return "Data not found.";
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/expense/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			String expenseType = ((List<OfficeExpenseResponseDto>)data.get("data")).get(0).getIncomeExpenseTypeName().toUpperCase();

			String name = accountContactRepository.getNameByUserId(memberId).toUpperCase();
			
			File pdfFile = new File(outputPath + "/" + getReportName(name, startDate, endDate, expenseType) + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 20, 20);

			BigDecimal totalExpense = (BigDecimal) data.get("total_expense");
			BigDecimal totalPaidAmount = (BigDecimal) data.get("paid_expense");
			BigDecimal totalUnpaidAmount = (BigDecimal) data.get("remaing_expense");

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setFixedLayout();

			Color headingColor = new DeviceRgb(115, 99, 67);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			cell = addHeaderTableCell(expenseType.toUpperCase(), basicFont, headingColor, headingColor,
					TextAlignment.CENTER);
			headerTable.addCell(cell);

			document.add(headerTable);

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }));
			detailTable.setWidth(UnitValue.createPercentValue(100f));
			detailTable.setFixedLayout();
			detailTable.setMarginTop(10f);

			cell = addDetailsTableCell("DATE", basicFont, headingColor, headingColor, TextAlignment.LEFT);
			detailTable.addCell(cell);

			cell = addDetailsTableCell(startDate + " - " + endDate, basicFont, blackColor, headingColor,
					TextAlignment.LEFT);
			detailTable.addCell(cell);

			document.add(detailTable);

			Table officeExpenseTable = new Table(
					UnitValue.createPercentArray(new float[] { 3f, 14f, 12f, 12f, 12f, 12f, 12f, 14f, 9f }));
			officeExpenseTable.setWidth(UnitValue.createPercentValue(100f));
			officeExpenseTable.setFixedLayout();
			officeExpenseTable.setMarginTop(10f);

			cell = addExpenseTableHeaderCell("#", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("NAME", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("EXP. DATE", basicFont, headingColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("DUE DATE", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("PAID DATE", basicFont, headingColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("AMOUNT", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("PAID", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("REMAINING", basicFont, headingColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableHeaderCell("STATUS", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE, 11f);
			officeExpenseTable.addCell(cell);

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			List<String> docPaths = new ArrayList<>();
			Integer count = 0;
			for (OfficeExpenseResponseDto expense : (List<OfficeExpenseResponseDto>) data.get("data")) {
				String accountContactName = expense.getAccountContactName() != null ? expense.getAccountContactName() : "";
				String expenseDate = expense.getExpenseDate() != null
						? expense.getExpenseDate()
						: "";

				String dueDate = expense.getDueDate() != null
						? expense.getDueDate()
						: "";
				String paidDate = expense.getPaidDate() != null
						? expense.getPaidDate()
						: "";
				BigDecimal expenseAmount = expense.getExpenseAmount() != null ? expense.getExpenseAmount()
						: BigDecimal.ZERO;
				BigDecimal paidAmount = expense.getPayoutAmount() != null ? expense.getPayoutAmount() : BigDecimal.ZERO;
				BigDecimal remainingPaidAmount = expense.getRemaingAmount() != null ? expense.getRemaingAmount()
						: BigDecimal.ZERO;
				String status = expense.getStatus() != null ? expense.getStatus() : "";

				List<String> docs = officeExpenseDocRepository.findAllPath(expense.getId());
				
				if (!docs.isEmpty()) {
					for (String doc : docs) {
						docPaths.add(environment.getProperty("app.image.url") + doc);						
					}
				}

				cell = addExpenseTableDetailCell((++count).toString(), basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(accountContactName, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(expenseDate, basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(dueDate, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(paidDate, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(expenseAmount.toString(), basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(paidAmount.toString(), basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(remainingPaidAmount.toString(), basicFont, blackColor, headingColor, 1,
						TextAlignment.CENTER, VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);

				cell = addExpenseTableDetailCell(status, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE);
				officeExpenseTable.addCell(cell);
			}

			cell = addExpenseTableDetailCell("TOTAL", basicFont, headingColor, headingColor, 5, TextAlignment.RIGHT,
					VerticalAlignment.MIDDLE);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(totalExpense.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(totalPaidAmount.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(totalUnpaidAmount.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell("", basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			officeExpenseTable.addCell(cell);

			document.add(officeExpenseTable);

			if (!docPaths.isEmpty()) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Rectangle pageSize = pdfDocument.getDefaultPageSize();

				float usableWidth = pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin();
				float usableHeight = pageSize.getHeight() - document.getTopMargin() - document.getBottomMargin() - 2;

				float imgWidth = usableWidth / 2;
				float imgHeight = usableHeight / 3;

				Table imageTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
				imageTable.setWidth(UnitValue.createPercentValue(100));

				for (String doc : docPaths) {
					try {
						ImageData imageData = ImageDataFactory.create(doc);
						Image image = new Image(imageData);

						image.setWidth(imgWidth);
						image.setHeight(imgHeight);
						image.setAutoScale(false);

						cell = new Cell().add(image).setPadding(0);
						imageTable.addCell(cell);
					} catch (Exception ex) {
						System.out.println("Unable to load attachment: " + doc);
					}
				}

				document.add(imageTable);
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/expense/"
					+ getReportName(name, startDate, endDate, expenseType) + ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String generateAllExpensesReport(Long memberId, String startDate, String endDate, HttpServletRequest re, Long accountContactId) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			System.out.println("Loading English font...");
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			System.out.println("English font loaded successfully");

			UserMasterEntity member = userMasterRepository.findByIdAndIsDeleteFalse(memberId)
					.orElseThrow(() -> new RuntimeException("Member not found with id : " + memberId));

			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/expense/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			File pdfFile = new File(outputPath + "/" + getReportName("AllExpenses", startDate, endDate, "") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 20, 20);

			Color headingColor = new DeviceRgb(115, 99, 67);
			Color blackColor = new DeviceRgb(0, 0, 0);

			Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 100f }));
			headerTable.setWidth(UnitValue.createPercentValue(100f));
			headerTable.setFixedLayout();

			Cell cell;

			cell = addHeaderTableCell("ALL EXPENCES", boldFont, headingColor, headingColor, TextAlignment.CENTER);
			headerTable.addCell(cell);

			cell = addHeaderTableCell(startDate + " - " + endDate, basicFont, headingColor, headingColor,
					TextAlignment.CENTER);
			headerTable.addCell(cell);

			document.add(headerTable);

			BigDecimal totalExpense = BigDecimal.ZERO;
			BigDecimal totalPaidExpense = BigDecimal.ZERO;
			BigDecimal totalUnpaidExpense = BigDecimal.ZERO;
			
			Table summaryTable = new Table(UnitValue.createPercentArray(new float[] { 33.33f, 33.33f, 33.33f }));
			summaryTable.setWidth(UnitValue.createPercentValue(100f));
			summaryTable.setFixedLayout();
			summaryTable.setMarginTop(15f);
			
			cell = addExpenseTableDetailCell("TOTAL AMOUNT", boldFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			summaryTable.addCell(cell);

			cell = addExpenseTableDetailCell("PAID AMOUNT", boldFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			summaryTable.addCell(cell);
			
			cell = addExpenseTableDetailCell("UNPAID AMOUNT", boldFont, headingColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			summaryTable.addCell(cell);

			cell = addExpenseTableDetailCell(totalExpense.toString(), boldFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			summaryTable.addCell(cell);

			cell = addExpenseTableDetailCell(totalPaidExpense.toString(), boldFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			summaryTable.addCell(cell);
			
			cell = addExpenseTableDetailCell(totalUnpaidExpense.toString(), boldFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			summaryTable.addCell(cell);
			
			document.add(summaryTable);
			
			/* Trip Expenses */
			Map<String, Object> data = expenseService.getTripExpenseByExpenseType("TRIP", memberId, startDate, endDate, accountContactId);
			AllExpensesReportResponseDto tripExpense = getTripExpenseForAllExpenseReport(data, basicFont, headingColor, blackColor, boldFont);
			totalExpense = totalExpense.add(tripExpense.getTotalAmount());
			totalPaidExpense = totalPaidExpense.add(tripExpense.getTotalPaidAmount());
			totalUnpaidExpense = totalUnpaidExpense.add(tripExpense.getTotalUnPaidAmount());
			
			document.add(tripExpense.getTable());
			
			/* Office Expenses */
			List<IncomeExpenseTypeEntity> expenseTypes = incomeExpenseTypeRepository.findAllByTypeAndUserIdAndIsDeleteFalse("EXPENSES", memberId);
			
			for (IncomeExpenseTypeEntity type : expenseTypes) {
				Map<String, Object> officeExpenseData = expenseService.getOfficeExpenseByExpenseType(type.getTypeId(), memberId,startDate, endDate, accountContactId);
				AllExpensesReportResponseDto officeExpense = getOfficeExpenseForAllExpenseReport(officeExpenseData, basicFont, headingColor, blackColor, type.getName().toUpperCase(), boldFont);
				totalExpense = totalExpense.add(officeExpense.getTotalAmount());
				totalPaidExpense = totalPaidExpense.add(officeExpense.getTotalPaidAmount());
				totalUnpaidExpense = totalUnpaidExpense.add(officeExpense.getTotalUnPaidAmount());
				
				document.add(officeExpense.getTable());
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/expense/"
					+ getReportName("AllExpenses", startDate, endDate, "") + ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@SuppressWarnings("unchecked")
	private AllExpensesReportResponseDto getTripExpenseForAllExpenseReport(Map<String, Object> data, PdfFont basicFont,
			Color headingColor, Color blackColor, PdfFont boldFont) {
		
		List<ExpenseResponseDto> expenses = (List<ExpenseResponseDto>) data.get("data");
		BigDecimal totalExpense = (BigDecimal) data.get("total_expense");
		BigDecimal remainingExpense = (BigDecimal) data.get("remaing_expense");
		BigDecimal paidExpense = (BigDecimal) data.get("paid_expense");

		Cell cell;

		Table tripExpenseTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 40f, 15f, 15f, 15f, 15f }));
		tripExpenseTable.setWidth(UnitValue.createPercentValue(100f));
		tripExpenseTable.setFixedLayout();
		tripExpenseTable.setMarginTop(15f);

		cell = addExpenseTableHeaderCell("TRIP EXPENSES", boldFont, headingColor, headingColor, 6,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 16f);
		tripExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("NAME", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		tripExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("TRIP NAME", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		tripExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("STATUS", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		tripExpenseTable.addCell(cell);
		
		cell = addExpenseTableHeaderCell("PAID AMOUNT", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		tripExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("UNPAID AMOUNT", basicFont, headingColor, headingColor, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
		tripExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("TOTAL AMOUNT", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		tripExpenseTable.addCell(cell);

		for (ExpenseResponseDto exp : expenses) {
//			String employeeName = exp.getUserName() != null ? exp.getUserName().toUpperCase() : "";
			String employeeName = "";
			String tripName = exp.getTitle() != null ? exp.getTitle().toUpperCase() : "";
			BigDecimal totalTripExpense = exp.getTotalAmount() != null ? exp.getTotalAmount() : BigDecimal.ZERO;
			BigDecimal totalPaidTripExpense = exp.getPayoutAmount() != null ? exp.getPayoutAmount() : BigDecimal.ZERO;
			BigDecimal totalUnpaidTripExpense = exp.getRemaingAmount() != null ? exp.getRemaingAmount()
					: BigDecimal.ZERO;
//			String isPayout = exp.getIsPayout() != null ? exp.getIsPayout() : "";

//			BigDecimal totalPaidTripExpense = BigDecimal.ZERO;
//			BigDecimal totalUnpaidTripExpense = BigDecimal.ZERO;
			String isPayout = "";

			cell = addExpenseTableDetailCell(employeeName, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			tripExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(tripName, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			tripExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(isPayout.toUpperCase(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			tripExpenseTable.addCell(cell);
			
			cell = addExpenseTableDetailCell(totalPaidTripExpense.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			tripExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(totalUnpaidTripExpense.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			tripExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(totalTripExpense.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			tripExpenseTable.addCell(cell);

		}
		cell = addExpenseTableDetailCell("TOTAL", boldFont, headingColor, headingColor, 3, TextAlignment.RIGHT,
				VerticalAlignment.MIDDLE);
		tripExpenseTable.addCell(cell);

		cell = addExpenseTableDetailCell(paidExpense.toString(), boldFont, blackColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE);
		tripExpenseTable.addCell(cell);
		
		cell = addExpenseTableDetailCell(remainingExpense.toString(), boldFont, blackColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE);
		tripExpenseTable.addCell(cell);
		
		cell = addExpenseTableDetailCell(totalExpense.toString(), boldFont, blackColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE);
		tripExpenseTable.addCell(cell);

		AllExpensesReportResponseDto response = new AllExpensesReportResponseDto();
		response.setTable(tripExpenseTable);
		response.setTotalAmount(totalExpense);
		response.setTotalPaidAmount(paidExpense);
		response.setTotalUnPaidAmount(remainingExpense);
		
		return response;
	}

	@SuppressWarnings("unchecked")
	private AllExpensesReportResponseDto getOfficeExpenseForAllExpenseReport(Map<String, Object> officeExpense, PdfFont basicFont,
			Color headingColor, Color blackColor, String label, PdfFont boldFont) {

		List<OfficeExpenseResponseDto> expenses = (List<OfficeExpenseResponseDto>) officeExpense.get("data");
		BigDecimal totalExpense = (BigDecimal) officeExpense.get("total_expense");
		BigDecimal paidExpense = (BigDecimal) officeExpense.get("paid_expense");
		BigDecimal remaingExpense = (BigDecimal) officeExpense.get("remaing_expense");

		Cell cell;

		Table officeExpenseTable = new Table(
				UnitValue.createPercentArray(new float[] { 30f, 40f, 15f, 15f, 15f, 15f }));
		officeExpenseTable.setWidth(UnitValue.createPercentValue(100f));
		officeExpenseTable.setFixedLayout();
		officeExpenseTable.setMarginTop(15f);

		cell = addExpenseTableHeaderCell(label.toUpperCase(), boldFont, headingColor, headingColor, 6,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 16f);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("NAME", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("DESCRIPTION", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("STATUS", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);
		
		cell = addExpenseTableHeaderCell("PAID AMOUNT", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("UNPAID AMOUNT", basicFont, headingColor, headingColor, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE, 11f);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableHeaderCell("TOTAL AMOUNT", basicFont, headingColor, headingColor, 1, TextAlignment.CENTER,
				VerticalAlignment.MIDDLE, 11f);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		for (OfficeExpenseResponseDto expense : expenses) {
			String accountContactName = expense.getAccountContactName() != null ? expense.getAccountContactName() : "";
			String remark = expense.getRemarks() != null ? expense.getRemarks() : "";
			BigDecimal expenseAmount = expense.getExpenseAmount() != null ? expense.getExpenseAmount()
					: BigDecimal.ZERO;
			BigDecimal paidAmount = expense.getPayoutAmount() != null ? expense.getPayoutAmount() : BigDecimal.ZERO;
			BigDecimal remainingPaidAmount = expense.getRemaingAmount() != null ? expense.getRemaingAmount()
					: BigDecimal.ZERO;
			String status = expense.getStatus() != null ? expense.getStatus() : "";

			cell = addExpenseTableDetailCell(accountContactName, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			cell.setKeepTogether(true);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(remark, basicFont, blackColor, headingColor, 1, TextAlignment.CENTER,
					VerticalAlignment.MIDDLE);
			cell.setKeepTogether(true);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(status.toUpperCase(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			cell.setKeepTogether(true);
			officeExpenseTable.addCell(cell);
			
			cell = addExpenseTableDetailCell(paidAmount.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			cell.setKeepTogether(true);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(remainingPaidAmount.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			cell.setKeepTogether(true);
			officeExpenseTable.addCell(cell);

			cell = addExpenseTableDetailCell(expenseAmount.toString(), basicFont, blackColor, headingColor, 1,
					TextAlignment.CENTER, VerticalAlignment.MIDDLE);
			cell.setKeepTogether(true);
			officeExpenseTable.addCell(cell);
		}

		cell = addExpenseTableDetailCell("TOTAL", boldFont, headingColor, headingColor, 3, TextAlignment.RIGHT,
				VerticalAlignment.MIDDLE);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableDetailCell(paidExpense.toString(), boldFont, blackColor, headingColor, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableDetailCell(remaingExpense.toString(), boldFont, blackColor, headingColor, 1,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		cell = addExpenseTableDetailCell(totalExpense.toString(), boldFont, blackColor, headingColor, 2,
				TextAlignment.CENTER, VerticalAlignment.MIDDLE);
		cell.setKeepTogether(true);
		officeExpenseTable.addCell(cell);

		AllExpensesReportResponseDto response = new AllExpensesReportResponseDto();
		response.setTable(officeExpenseTable);
		response.setTotalAmount(totalExpense);
		response.setTotalPaidAmount(paidExpense);
		response.setTotalUnPaidAmount(remaingExpense);
		
		return response;
	}
	
}
