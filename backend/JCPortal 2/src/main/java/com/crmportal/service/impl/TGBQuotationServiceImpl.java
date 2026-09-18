package com.crmportal.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.response.dto.QuotationResponseDto;
import com.crmportal.service.TGBQuotationReportService;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class TGBQuotationServiceImpl implements TGBQuotationReportService {

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	Environment environment;

	@Autowired
	EventFunctionQuotationServiceImpl eventFunctionQuotationServiceImpl;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Override
	public String getQuotationReport3(Long eventId, HttpServletRequest re, int lang, Long userId, int i,
			Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isWithPrice, Boolean isDecore,
			Integer isCombo, Integer isOnePage, Integer isCompanyDetails) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;

			menuPreparationServiceImpl.loadLicense();

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", timeLabel = "", cordinatorPersonLabel = "", packageNameLabel = "",
					cordinatorPersonContactNumLabel = "";

			// English
			System.out.println("Loading English font...");

			hostLabel = "GUEST NAME";
			phone = "CONTACT PERSON";
			eDate = "DATE";
			eName = "TYPE OF EVENT";
			venueLabel = "VENUE";
			personLabel = "NO. OF PERSONS";
			eTime = "TIME";
			fNote = "FOOD STATUS";
			pckPrice = "Package Price";
			price = "RATE PER PERSON";
			remarks = "REMARKS";
			billingNameLabel = "BILLING NAME";
			serviceLabel = "SERVICE";
			themeLabel = "THEME";
			cordinatorPersonLabel = "CORDINATOR PERSON";
			cordinatorPersonContactNumLabel = "CORDINATOR CONTACT NO";
			packageNameLabel = "RATE PER PERSON";

			basicFont = menuPreparationServiceImpl.loadFont("/fonts/gothic-regular.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/gothic-bold.ttf");

			System.out.println("English font loaded successfully");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			// Define colors
//			Color creamGold = new DeviceRgb(r2, g2, b2);
//			Color softGold = new DeviceRgb(r1, g1, b1);
//			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			EventMasterEntity eventMasterEntity = eventMasterRepository.findById(eventId)
					.orElseThrow(() -> new RuntimeException("Event not found"));

			if (eventMasterEntity == null) {
				return "Data not found.";
			}
			// SECTION A - BUFFET
			QuotationResponseDto buffetDto = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, 0,
					false);

			QuotationResponseDto decorDto = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, 0, true);

			if (isCombo == 1) {
				if (buffetDto == null && decorDto == null) {
					return "Data not found.";
				}
			} else {
				if (isDecore && decorDto == null) {
					return "Data not found";
				} else if (!isDecore && buffetDto == null) {
					return "Data not found";
				}
			}

			Color blackColor = new DeviceRgb(0, 0, 0);

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);

			String eventNo = eventMasterEntity.getEventNo();
			LocalDateTime eventStartTimeStamp = eventMasterEntity.getEventStartDateTime();

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventNo + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!");
				}
			}
			File pdfFile = new File(outputPath + "/"
					+ menuPreparationServiceImpl.getReportName(
							menuPreparationServiceImpl.getPartyNameByEventId(eventId),
							menuPreparationServiceImpl.formatDate(eventStartTimeStamp), "quotation report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(50, 45, 60, 50);

			boolean isBuffetQuotationShow = false;

			Table quotationTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 16.66f, 16.66f, 16.67f }));
			quotationTable.setBorder(Border.NO_BORDER);
			quotationTable.setWidth(UnitValue.createPercentValue(100f));
			quotationTable.setFixedLayout();
			quotationTable.setBackgroundColor(ColorConstants.WHITE);

			Cell cell;

			if (buffetDto != null) {
				List<QuotationResponseDto> items;

				items = eventFunctionQuotationServiceImpl.getFunctionData(buffetDto.getQuotationId());

				if (items != null && !items.isEmpty()) {
					isBuffetQuotationShow = true;

					Paragraph data = new Paragraph().add(new Text("GUEST NAME").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.BOTTOM).setTextAlignment(TextAlignment.LEFT);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text(buffetDto.getPrefix().toUpperCase() + " " + buffetDto.getPartyName().toUpperCase()).setFontSize(14).setFont(boldFont));
					cell = new Cell(1, 3).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("DATE : " + buffetDto.getEventDate().split(" ")[0])
							.setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text(buffetDto.getVenueName().toUpperCase()).setFontSize(14).setFont(boldFont));
					cell = new Cell(1, 3).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("ESTIMATED BUFFET AMOUNT").setFontSize(16).setFont(boldFont));
					cell = new Cell(1, 4).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("QTY.").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("RATE").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("AMOUNT").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					double total = 0D;

					for (QuotationResponseDto item : items) {
						quotationTable.addCell(createCell(item.getFunctionName().toUpperCase(), basicFont));

						quotationTable.addCell(createRightCell(formatQty(item.getFunctionPax()), basicFont));

						quotationTable.addCell(createRightCell(formatAmount(item.getRate()), basicFont));

						quotationTable.addCell(createRightCell(formatAmount(item.getAmount()), basicFont));

						total += safe(item.getAmount());
					}

					addSummaryRows(quotationTable, buffetDto, total, false, basicFont, boldFont, isCombo);
				}
			}

			Boolean isDecorQuotationShow = false;

			Table decorQuotationTable = new Table(
					UnitValue.createPercentArray(new float[] { 50f, 16.66f, 16.66f, 16.67f }));
			decorQuotationTable.setBorder(Border.NO_BORDER);
			decorQuotationTable.setWidth(UnitValue.createPercentValue(100f));
			decorQuotationTable.setMarginTop(10f);
			decorQuotationTable.setFixedLayout();
			decorQuotationTable.setBackgroundColor(ColorConstants.WHITE);

			if (decorDto != null) {
				List<QuotationResponseDto> items;
				items = eventFunctionQuotationServiceImpl.getFunctionData1(decorDto.getQuotationId());

				if (items != null && !items.isEmpty()) {
					isDecorQuotationShow = true;

					Paragraph data = new Paragraph()
							.add(new Text("OTHER ESTIMATE AMOUNT").setFontSize(16).setFont(boldFont));
					cell = new Cell(1, 4).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("QTY.").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("RATE").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("AMOUNT").setFontSize(14).setFont(boldFont));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					double total = 0D;

					for (QuotationResponseDto item : items) {
						if (!item.getIsAddons()) {
							decorQuotationTable.addCell(createCell(item.getFunctionName().toUpperCase(), basicFont));

							decorQuotationTable.addCell(createRightCell(formatQty(item.getFunctionPax()), basicFont));

							decorQuotationTable.addCell(createRightCell(formatAmount(item.getRate()), basicFont));

							decorQuotationTable.addCell(createRightCell(formatAmount(item.getAmount()), basicFont));
						}
						total += safe(item.getAmount());
					}

					addSummaryRows(decorQuotationTable, decorDto, total, true, basicFont, boldFont, isCombo);
				}
			}

			if (isBuffetQuotationShow || isDecorQuotationShow) {
//				document.add(quotationTable);
//				document.add(decorQuotationTable);
				boolean hasData = false;
				if (isCombo == 1) {
					// Show both tables
					if (isBuffetQuotationShow) {
						document.add(quotationTable);
						hasData = true;
					}

					if (isDecorQuotationShow) {
						if (isOnePage != 1) {
							document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
						}
						document.add(decorQuotationTable);
						hasData = true;
					}

					if (isBuffetQuotationShow && isDecorQuotationShow) {
						addCombinedGrandTotal(document, buffetDto, decorDto, basicFont, boldFont);
					}

				} else {
					// Show only one table
					if (Boolean.TRUE.equals(isDecore)) {
						if (isDecorQuotationShow) {
							document.add(decorQuotationTable);
							hasData = true;
						}
					} else {
						if (isBuffetQuotationShow) {
							document.add(quotationTable);
							hasData = true;
						}
					}
				}

//				if (isBuffetQuotationShow && isDecorQuotationShow) {
//					addCombinedGrandTotal(document, buffetDto, decorDto, basicFont, boldFont);
//				}

//				document.add(new Paragraph().add(new Text("All Cheque should be drawn in favor of"))
//						.add(new Text(" TGB Banquets And Hotels Ltd").simulateBold()).setPaddingLeft(60f)
//						.setMarginTop(15f));
//
//				document.add(new Paragraph("BANK DETAILS FOR NEFT / RTGS / IMPS TRANSFER").setFont(boldFont)
//						.simulateBold().setPaddingLeft(60f).setMarginTop(15f));
//
//				if (buffetDto != null) {
//					Div divData2 = new Div();
//					divData2.setWidth(UnitValue.createPercentValue(95));
//					divData2.setPadding(5f);
//					divData2.setPaddingLeft(55f);
//					divData2.setHorizontalAlignment(HorizontalAlignment.LEFT);
//
//					String accountHolderName = buffetDto.getAccountHolderName() == null
//							|| buffetDto.getAccountHolderName().isEmpty() ? "" : buffetDto.getAccountHolderName();
//
//					String accountNo = buffetDto.getAccountNo() == null || buffetDto.getAccountNo().isEmpty() ? ""
//							: buffetDto.getAccountNo();
//
//					String bankName = buffetDto.getBankName() == null || buffetDto.getBankName().isEmpty() ? ""
//							: buffetDto.getBankName();
//
//					String ifscCode = buffetDto.getIfscCode() == null || buffetDto.getIfscCode().isEmpty() ? ""
//							: buffetDto.getIfscCode();
//
//					String upiId = buffetDto.getUpiId() == null || buffetDto.getUpiId().isEmpty() ? ""
//							: buffetDto.getUpiId();
//
//					String branchName = buffetDto.getBranchName() == null || buffetDto.getBranchName().isEmpty() ? ""
//							: buffetDto.getBranchName();
//
//					Paragraph data = new Paragraph().add(new Text("TGB BANQUETS AND HOTELS LIMITED").setFontSize(10))
//							.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
//					divData2.add(data);
////					System.out.println("cmp name : " + cmpName);
////					data = new Paragraph().add(new Text("NAME OF COMPANY: ").setFont(boldFont).setFontSize(10))
////							.add(new Text(cmpName).setFont(basicFont).setFontSize(10)).setMultipliedLeading(1f)
////							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
////					divData2.add(data);
//
//					data = new Paragraph().add(new Text("BANK: ").setFont(boldFont).setFontSize(10))
//							.add(new Text(bankName).setFont(basicFont).setFontSize(10)).setMultipliedLeading(1f)
//							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
//					divData2.add(data);
//
//					data = new Paragraph().add(new Text("BRANCH: ").setFont(boldFont).setFontSize(10))
//							.add(new Text(branchName).setFont(basicFont).setFontSize(10)).setMultipliedLeading(1f)
//							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
//					divData2.add(data);
//
//					data = new Paragraph().add(new Text("ACCOUNT NO: ").setFont(boldFont).setFontSize(10))
//							.add(new Text(accountNo).setFont(basicFont).setFontSize(10)).setMultipliedLeading(1f)
//							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
//					divData2.add(data);
//
//					data = new Paragraph().add(new Text("IFSC CODE: ").setFont(boldFont).setFontSize(10))
//							.add(new Text(ifscCode).setFont(basicFont).setFontSize(10)).setMultipliedLeading(1f)
//							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
//					divData2.add(data);
//
//					document.add(divData2);
//				}

				if (hasData) {

					document.add(new Paragraph().add(new Text("All Cheque should be drawn in favor of"))
							.add(new Text(" TGB Banquets And Hotels Ltd").simulateBold()).setPaddingLeft(10f)
							.setMarginTop(15f));

					document.add(new Paragraph("BANK DETAILS FOR NEFT / RTGS / IMPS TRANSFER").setFont(boldFont)
							.simulateBold().setPaddingLeft(10f).setMarginTop(15f));

					// Select DTO from which bank details should be taken
					QuotationResponseDto bankDetailsDto;

					if (isCombo == 1) {
						bankDetailsDto = buffetDto != null ? buffetDto : decorDto;
					} else {
						bankDetailsDto = Boolean.TRUE.equals(isDecore) ? decorDto : buffetDto;
					}

					if (bankDetailsDto != null) {

						Div divData2 = new Div();
						divData2.setWidth(UnitValue.createPercentValue(95));
						divData2.setPadding(5f);
						divData2.setHorizontalAlignment(HorizontalAlignment.LEFT);

						String accountHolderName = bankDetailsDto.getAccountHolderName() == null
								|| bankDetailsDto.getAccountHolderName().isEmpty() ? ""
										: bankDetailsDto.getAccountHolderName();

						String accountNo = bankDetailsDto.getAccountNo() == null
								|| bankDetailsDto.getAccountNo().isEmpty() ? "" : bankDetailsDto.getAccountNo();

						String bankName = bankDetailsDto.getBankName() == null || bankDetailsDto.getBankName().isEmpty()
								? ""
								: bankDetailsDto.getBankName();

						String ifscCode = bankDetailsDto.getIfscCode() == null || bankDetailsDto.getIfscCode().isEmpty()
								? ""
								: bankDetailsDto.getIfscCode();

						String upiId = bankDetailsDto.getUpiId() == null || bankDetailsDto.getUpiId().isEmpty() ? ""
								: bankDetailsDto.getUpiId();

						String branchName = bankDetailsDto.getBranchName() == null
								|| bankDetailsDto.getBranchName().isEmpty() ? "" : bankDetailsDto.getBranchName();

						Paragraph data = new Paragraph()
								.add(new Text("TGB BANQUETS AND HOTELS LIMITED").setFontSize(10))
								.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
						divData2.add(data);

						if (bankName != null && bankName.trim().length() != 0) {
							data = new Paragraph().add(new Text("BANK: ").setFont(boldFont).setFontSize(10))
									.add(new Text(bankName).setFont(basicFont).setFontSize(10)).setMultipliedLeading(1f)
									.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
							divData2.add(data);
						}

						if (branchName != null && branchName.trim().length() != 0) {
							data = new Paragraph().add(new Text("BRANCH: ").setFont(boldFont).setFontSize(10))
									.add(new Text(branchName).setFont(basicFont).setFontSize(10))
									.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
							divData2.add(data);
						}

						if (accountNo != null && accountNo.trim().length() != 0) {
							data = new Paragraph().add(new Text("ACCOUNT NO: ").setFont(boldFont).setFontSize(10))
									.add(new Text(accountNo).setFont(basicFont).setFontSize(10))
									.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
							divData2.add(data);
						}

						if (ifscCode != null && ifscCode.trim().length() != 0) {
							data = new Paragraph().add(new Text("IFSC CODE: ").setFont(boldFont).setFontSize(10))
									.add(new Text(ifscCode).setFont(basicFont).setFontSize(10)).setMultipliedLeading(1f)
									.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
							divData2.add(data);
						}

						document.add(divData2);
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventNo + "/"
					+ menuPreparationServiceImpl.getReportName(
							menuPreparationServiceImpl.getPartyNameByEventId(eventId),
							menuPreparationServiceImpl.formatDate(eventStartTimeStamp), "quotation report")
					+ ".pdf";
			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private void addCombinedRow(Table table, String label, String value, PdfFont font) {

		table.addCell(new Cell().add(new Paragraph(label).setFont(font).setFontSize(14f)).setBorder(new SolidBorder(1))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2).simulateBold().setPaddingLeft(5f));

		table.addCell(new Cell().add(new Paragraph(value).setFont(font).setFontSize(14f)).setBorder(new SolidBorder(1))
				.setTextAlignment(TextAlignment.RIGHT).setPadding(2).simulateBold().setPaddingRight(5f));
	}

	private double safe(Double value) {
		return value == null ? 0D : value;
	}

	private Cell createRightCell(String value, PdfFont font) {

		return createCell(value, font).setPaddingRight(5f).setTextAlignment(TextAlignment.RIGHT);
	}

	private Cell createCell(String value, PdfFont font) {

		Paragraph paragraph = new Paragraph(value == null ? "" : value).setFont(font).setFontSize(13)
				.setFixedLeading(13);

		return new Cell().add(paragraph).setPadding(2f).setPaddingLeft(5f);
	}

	private String formatQty(Number value) {

		if (value == null) {
			return "0";
		}

		return String.format("%.0f", value.doubleValue());
	}

	private void addSummaryRows(Table table, QuotationResponseDto dto, double total, boolean isDecor, PdfFont normal,
			PdfFont bold, Integer isCombo) {

		String discountLabel = "";
		if (dto.getDiscountPercent() != null && !dto.getDiscountPercent().trim().isEmpty()
				&& Double.parseDouble(dto.getDiscountPercent().trim()) > 0) {
			discountLabel = dto.getDiscountPercent() + " ";
			addSummaryRow(table, discountLabel + "% DISCOUNT", "-" + formatAmount(dto.getDiscount()), bold, true);
		}

		addSummaryRow(table, "", "", normal, false);

		addSummaryRow(table, "TOTAL", formatAmount(total - dto.getDiscount()), normal, false);

		addSummaryRow(table, isDecor ? "18% GST" : "5% GST",
				formatAmount(safe(dto.getCgstAmnt()) + safe(dto.getSgstAmnt()) + safe(dto.getIgstAmnt())), normal,
				false);

//		Double subTotal = dto.getSubTotle() != null ? dto.getSubTotle() : 0;
//		Double discount = dto.getDiscount() != null ? dto.getDiscount() : 0;
//		addSummaryRow(table, "FINAL TOTAL", formatAmount(subTotal - discount), normal, false);

		// This section's own subtotal — e.g. 807975.00 for buffet, 207680.00 for decor
		addSummaryRow(table, isCombo == 1 ? isDecor ? "TOTAL AMOUNT (B) " : "TOTAL AMOUNT (A)" : "TOTAL AMOUNT",
				formatAmount(dto.getGrandTotal()), bold, true);

		double advance = safe(dto.getAdvancePayment());
		double balance = safe(dto.getGrandTotal()) - advance;

		if (isCombo != 1 && advance > 0) {
			addSummaryRow(table, "ADVANCE PAYMENT", formatAmount(advance), bold, true);
			addSummaryRow(table, "BALANCE TOTAL", formatAmount(balance), bold, true);
		}
	}

	private void addCombinedGrandTotal(Document document, QuotationResponseDto buffet, QuotationResponseDto decor,
			PdfFont normalFont, PdfFont boldFont) {

		double total = safe(buffet.getGrandTotal()) + safe(decor.getGrandTotal());
		double advance = safe(buffet.getAdvancePayment()) + safe(decor.getAdvancePayment());
		double balance = total - advance;

		// Force this onto its own block, after the decor section's own table has fully
		// flowed,
		// so it can't float up and overlap the decor section's summary rows.
		document.add(new Paragraph("\n"));

		Table table = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }))
				.setWidth(UnitValue.createPercentValue(100f)).setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setKeepTogether(true).setBackgroundColor(ColorConstants.WHITE);

		addCombinedRow(table, "GRAND TOTAL", formatAmount(total), boldFont);
		addCombinedRow(table, "ADVANCE", "-" + formatAmount(advance), boldFont);
		addCombinedRow(table, "BALANCE", formatAmount(balance), boldFont);

		document.add(table);
	}

	private String formatAmount(Number value) {

		if (value == null) {
			return "0";
		}

		return String.format("%,.0f", value.doubleValue());
	}

	private void addSummaryRow(Table table, String label, String value, PdfFont font, Boolean isBold) {

		Cell cell = new Cell().add(new Paragraph(label).setFont(font).setFontSize(13).setFixedLeading(13))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2f).setPaddingLeft(5f).setMinHeight(15f);
		if (isBold) {
			cell.simulateBold();
		}
		table.addCell(cell);

		table.addCell(new Cell().add(new Paragraph("").setFont(font).setFontSize(13).setFixedLeading(13))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2f).setPaddingLeft(5f).setMinHeight(15f));

		table.addCell(new Cell().add(new Paragraph("").setFont(font).setFontSize(13).setFixedLeading(13))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2f).setPaddingLeft(5f).setMinHeight(15f));

		cell = new Cell().add(new Paragraph(value).setFont(font).setFontSize(13).setFixedLeading(13))
				.setTextAlignment(TextAlignment.RIGHT).setPadding(2f).setPaddingLeft(5f).setPaddingRight(5f)
				.setMinHeight(15f);
		if (isBold) {
			cell.simulateBold();
		}
		table.addCell(cell);
	}
}
