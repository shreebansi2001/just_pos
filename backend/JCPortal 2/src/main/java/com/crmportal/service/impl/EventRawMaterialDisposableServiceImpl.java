package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.EventRawMaterialDisposableService;

import java.io.ByteArrayOutputStream;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Service
@Transactional
public class EventRawMaterialDisposableServiceImpl implements EventRawMaterialDisposableService {

	@Autowired
	private EventRawMaterialDisposableRepository DisposableRepository;

	@Autowired
	private EventMasterRepository eventMasterRepository;

	@Autowired
	private RawMaterialMasterRepository rawMaterialRepository;

	@Autowired
	private RawMaterialCategoryMasterRepository rawMaterialCatRepository;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;

	@Override
	public EventRawMaterialDisposableResponseDto getOrLoad(Long eventId, Long userId, Long rawCategoryId, Long qtyVal, Integer isAllItems) {

		EventRawMaterialDisposableResponseDto response = new EventRawMaterialDisposableResponseDto();

		response.setEventId(eventId);

		List<Object[]> savedList = DisposableRepository.findRawMaterialDisposable(eventId, userId, rawCategoryId,
				qtyVal, isAllItems);

		Map<String, EventRawMaterialDisposableCategoryDto> catMap = new LinkedHashMap<>();

		for (Object[] row : savedList) {

			Long id = row[0] != null ? ((Number) row[0]).longValue() : null;

			String categoryNameEnglish = row[1] != null ? row[1].toString() : "";
			String categoryNameHindi = row[2] != null ? row[2].toString() : "";
			String categoryNameGujarati = row[3] != null ? row[3].toString() : "";

			String rawMaterialNameEnglish = row[4] != null ? row[4].toString() : "";
			String rawMaterialNameHindi = row[5] != null ? row[5].toString() : "";
			String rawMaterialNameGujarati = row[6] != null ? row[6].toString() : "";

			String unitNameEnglish = row[7] != null ? row[7].toString() : "";
			String unitNameHindi = row[8] != null ? row[8].toString() : "";
			String unitNameGujarati = row[9] != null ? row[9].toString() : "";

			Double suppRate = row[10] != null ? ((Number) row[10]).doubleValue() : 0.0;

			Double qty = row[11] != null ? ((Number) row[11]).doubleValue() : 0.0;

			Double totalRate = row[12] != null ? ((Number) row[12]).doubleValue() : 0.0;

			Long rawMaterialId = row[14] != null ? ((Number) row[14]).longValue() : 0l;
			Long rawMaterialCatId = row[15] != null ? ((Number) row[15]).longValue() : 0l;

			Long unitId = row[13] != null ? ((Number) row[13]).longValue() : 0l;
			String imageUrl = row[16] != null ? environment.getProperty("app.image.url") + row[16].toString() : null;
			String categoryKey = categoryNameEnglish;

			catMap.putIfAbsent(categoryKey,
					buildCatDto(null, categoryNameEnglish, categoryNameGujarati, categoryNameHindi));

			EventRawMaterialDisposableItemResponseDto item = new EventRawMaterialDisposableItemResponseDto();

			item.setId(id);

			item.setCategoryNameEnglish(categoryNameEnglish);
			item.setCategoryNameHindi(categoryNameHindi);
			item.setCategoryNameGujarati(categoryNameGujarati);
			item.setRawMaterialCatId(rawMaterialCatId);
			item.setRawMaterialNameEnglish(rawMaterialNameEnglish);
			item.setRawMaterialNameHindi(rawMaterialNameHindi);
			item.setRawMaterialNameGujarati(rawMaterialNameGujarati);

			item.setUnitId(unitId);
			item.setUnitNameEnglish(unitNameEnglish);
			item.setUnitNameHindi(unitNameHindi);
			item.setUnitNameGujarati(unitNameGujarati);
			item.setRawMaterialId(rawMaterialId);
			item.setSupRate(suppRate);
			item.setQty(qty);
			item.setRawMaterialCatId(rawMaterialCatId);
			item.setTotalRate(totalRate);
			item.setImageUrl(imageUrl);
			catMap.get(categoryKey).getItems().add(item);
		}

		response.setCategories(new ArrayList<>(catMap.values()));

		return response;
	}

	// =========================================================================
	// SAVE — delete old event-wise rows, insert fresh
	// =========================================================================
	@Override
	public EventRawMaterialDisposableResponseDto save(EventRawMaterialDisposableRequestDto request) {

		EventMasterEntity event = eventMasterRepository.findById(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found: " + request.getEventId()));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("user Not Found"));

		RawMaterialCategoryMasterEntity cat = request.getRawMaterialCatId() != null
				? rawMaterialCatRepository.findByIdAndIsDeleteFalse(request.getRawMaterialCatId()).orElseThrow(
						() -> new RuntimeException("Raw Material Catgory not Found"))
				: null;

		DisposableRepository.deleteAllByEventAndUserIdAndRawMaterialCat(event, user.getId(), cat);
		// Save new rows
		List<EventRawMaterialDisposableEntity> newList = new ArrayList<>();

		for (EventRawMaterialDisposableItemDto item : request.getItems()) {

			RawMaterialMasterEntity rm = item.getRawMaterialId() != null
					? rawMaterialRepository.findById(item.getRawMaterialId()).orElse(null)
					: null;

			UnitMasterEntity unit = item.getUnitId() != null
					? unitMasterRepository.findByIdAndIsDeleteFalse(item.getUnitId()).orElseThrow(
							() -> new RuntimeException("Unit master not found"))
					: null;

			EventRawMaterialDisposableEntity entity = new EventRawMaterialDisposableEntity();
			entity.setEvent(event);
			entity.setRawMaterial(rm);
			entity.setRawMaterialCat(cat);
			entity.setTotalRate(item.getTotalRate() != null ? item.getTotalRate() : 0.0);
			entity.setQty(item.getQty() != null ? item.getQty() : 0.0);
			entity.setUserId(request.getUserId());
			entity.setUnit(unit);
			entity.setUpdatedAt(LocalDateTime.now());
			newList.add(entity);
		}

		DisposableRepository.saveAll(newList);

		return getOrLoad(request.getEventId(), request.getUserId(), request.getRawMaterialCatId(), null, 1);
	}

	private EventRawMaterialDisposableCategoryDto buildCatDto(Long catId, String catName, String catNameGujarati,
			String catNameHindi) {
		EventRawMaterialDisposableCategoryDto dto = new EventRawMaterialDisposableCategoryDto();
		dto.setCatId(catId);
		dto.setCategoryNameEnglish(catName);
		dto.setCategoryNameGujarati(catNameGujarati);
		dto.setCategoryNameHindi(catNameHindi);
		dto.setItems(new ArrayList<>());
		return dto;
	}

//	@Override
//	public byte[] generatePdfReport(Long eventId, Long userId, Integer isCompanyDetails, Integer isWithPrice,
//			Integer lang, Integer isAllItems, Long rawCategoryId, Integer isWithImage) {
//		try {
//			EventRawMaterialDisposableResponseDto data = getOrLoad(eventId, userId, rawCategoryId, -1L, isAllItems);
//
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			PdfWriter writer = new PdfWriter(baos);
//			PdfDocument pdf = new PdfDocument(writer);
//			Document document = new Document(pdf, PageSize.A4);
//			document.setMargins(30, 30, 30, 30);
//
//			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
//			PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//
//			if (lang == 1) {
//				// Hindi
//				System.out.println("Loading Hindi font...");
//				boldFont = regularFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
//				System.out.println("Hindi font loaded successfully");
//			} else if (lang == 2) {
//				// Gujarati
//				System.out.println("Loading Gujarati font...");
//
//				boldFont = regularFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
//
//				System.out.println("Gujarati font loaded successfully");
//			} else {
//				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
//				regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//
//				System.out.println("English font loaded successfully");
//			}
//			// ── Colors ────────────────────────────────────────────────────────
//			Color darkBlue = new DeviceRgb(13, 71, 116);
//			Color blackColor = new DeviceRgb(0, 0, 0);
//			Color tableBlue = new DeviceRgb(26, 99, 153);
//			Color catHeader = new DeviceRgb(44, 82, 130);
//			Color lightBlueBg = new DeviceRgb(235, 245, 255);
//			Color cardBg = new DeviceRgb(245, 248, 252);
//			Color borderGray = new DeviceRgb(200, 210, 220);
//			Color labelGray = new DeviceRgb(100, 120, 140);
//			Color altRow = new DeviceRgb(245, 248, 252);
//
//			// ── showPrice flag ────────────────────────────────────────────────
//			boolean showPrice = isWithPrice != null && isWithPrice == 1;
//			boolean showImage = isWithImage != null && isWithImage == 1;
//
//			// ══════════════════════════════════════════════════════════════════
//			// SECTION 0 — COMPANY HEADER
//			// ══════════════════════════════════════════════════════════════════
//			try {
//				Object cmpDtoObj = menuPreparationServiceImpl.getClass().getMethod("getCompanyDetails", Long.class)
//						.invoke(menuPreparationServiceImpl, userId);
//			} catch (Exception ignored) {
//			}
//
//			try {
//				com.crmportal.response.dto.CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId);
//
//				if (cmpDto != null) {
//					float[] cw = { 20f, 2f, 78f };
//					Table ht = new Table(UnitValue.createPercentArray(cw));
//					ht.setWidth(UnitValue.createPercentValue(100));
//
//					try {
//						com.itextpdf.io.image.ImageData logoData = menuPreparationServiceImpl
//								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//						com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(logoData);
//						logo.setWidth(100f);
//						logo.setAutoScale(false);
//						logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
//						ht.addCell(new Cell(3, 2).add(logo)
//								.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
//								.setBorder(Border.NO_BORDER).setPaddingBottom(10f));
//					} catch (Exception e) {
//						ht.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
//					}
//
//					ht.addCell(new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14)
//							.setFontColor(blackColor)).setBorder(Border.NO_BORDER).setPaddingLeft(15f));
//
//					ht.addCell(new Cell()
//							.add(new Paragraph()
//									.add(new Text("Phone : ").setFont(boldFont).setFontSize(12)
//											.setFontColor(blackColor))
//									.add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getOfficeNo() : "")
//											.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
//							.setBorder(Border.NO_BORDER).setPaddingLeft(15f).setPaddingBottom(10f));
//
//					ht.addCell(new Cell()
//							.add(new Paragraph()
//									.add(new Text("Email : ").setFont(boldFont).setFontSize(14)
//											.setFontColor(blackColor))
//									.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
//											.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
//							.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(15f)
//							.setPaddingBottom(10f));
//
//					ht.addCell(new Cell().setBorder(Border.NO_BORDER));
//					document.add(ht);
//
//					Table div = new Table(UnitValue.createPercentArray(new float[] { 100f }));
//					div.setWidth(UnitValue.createPercentValue(100));
//					div.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
//					document.add(div);
//					document.add(new Paragraph("").setMarginBottom(8));
//				}
//			} catch (Exception ignored) {
//			}
//
//			// ══════════════════════════════════════════════════════════════════
//			// SECTION 1 — TITLE + DATE
//			// ══════════════════════════════════════════════════════════════════
//			String printedOn = "Printed on: "
//					+ java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));
//
//			float[] topW = { 60f, 40f };
//			Table topTable = new Table(UnitValue.createPercentArray(topW));
//			topTable.setWidth(UnitValue.createPercentValue(100));
//
//			topTable.addCell(new Cell()
//					.add(new Paragraph("Disposable Items Report").setFont(boldFont).setFontSize(20)
//							.setFontColor(darkBlue).setMarginBottom(2))
//					.add(new Paragraph(printedOn).setFont(regularFont).setFontSize(8).setFontColor(labelGray))
//					.setBorder(Border.NO_BORDER).setPaddingBottom(6));
//
//			topTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//			document.add(topTable);
//
//			// ── Divider ───────────────────────────────────────────────────────
//			Table divider = new Table(UnitValue.createPercentArray(new float[] { 100f }));
//			divider.setWidth(UnitValue.createPercentValue(100));
//			divider.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
//			document.add(divider);
//			document.add(new Paragraph("").setMarginBottom(12));
//
//			// ══════════════════════════════════════════════════════════════════
//			// SECTION 2 — CATEGORY TABLES
//			// ══════════════════════════════════════════════════════════════════
//			double grandTotal = 0;
//			int totalItems = 0;
//			if (!data.getCategories().isEmpty()) {
//				for (EventRawMaterialDisposableCategoryDto cat : data.getCategories()) {
//					if (cat.getItems() == null || cat.getItems().isEmpty()) {
//						continue;
//					}
//
//					// ── Category Header Band ──────────────────────────────────────
//					Table catBand = new Table(UnitValue.createPercentArray(new float[] { 100f }));
//					catBand.setWidth(UnitValue.createPercentValue(100));
//
//					catBand.addCell(new Cell()
//							.add(new Paragraph(
//									cat.getCategoryNameEnglish() != null ? cat.getCategoryNameEnglish().toUpperCase()
//											: "UNCATEGORIZED")
//									.setFont(boldFont).setFontSize(11).setFontColor(ColorConstants.WHITE)
//									.setTextAlignment(TextAlignment.CENTER))
//							.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
//							.setBackgroundColor(catHeader).setBorder(Border.NO_BORDER).setPaddingTop(6)
//							.setPaddingBottom(6));
//
//					document.add(catBand);
//
////					String[] headers = showPrice
////							? new String[] { "#", "Image", "Raw Material Name", "Supp. Rate", "Qty", "Unit",
////									"Total Rate" }
////							: new String[] { "#", "Image", "Raw Material Name", "Qty", "Unit" };
//					
//					 // ── Headers and Column Widths ─────────────────────────────────
//			        String[] headers;
//			        float[] colW;
//
//			        if (showPrice) {
//
//			            if (showImage) {
//			                headers = new String[] {
//			                        "#", "Image", "Raw Material Name",
//			                        "Supp. Rate", "Qty", "Unit", "Total Rate"
//			                };
//
//			                colW = new float[] {
//			                        10f, 20f, 20f, 10f, 10f, 15f, 15f
//			                };
//
//			            } else {
//			                headers = new String[] {
//			                        "#", "Raw Material Name",
//			                        "Supp. Rate", "Qty", "Unit", "Total Rate"
//			                };
//
//			                colW = new float[] {
//			                        10f, 40f, 10f, 10f, 15f, 15f
//			                };
//			            }
//
//			        } else {
//
//			            if (showImage) {
//			                headers = new String[] {
//			                        "#", "Image", "Raw Material Name", "Qty", "Unit"
//			                };
//
//			                colW = new float[] {
//			                        10f, 25f, 35f, 15f, 15f
//			                };
//
//			            } else {
//			                headers = new String[] {
//			                        "#", "Raw Material Name", "Qty", "Unit"
//			                };
//
//			                colW = new float[] {
//			                        10f, 50f, 20f, 20f
//			                };
//			            }
//			        }
//
////					float[] colW = showPrice ? new float[] { 10f, 20f, 20f, 10f, 10f, 15f, 15f }
////							: new float[] { 10f, 25f, 35f, 15f, 15f };
//
//					// ── Items Table ───────────────────────────────────────────────
//					Table itemTable = new Table(UnitValue.createPercentArray(colW));
//					itemTable.setWidth(UnitValue.createPercentValue(100));
//					itemTable.setFixedLayout();
//
//					TextAlignment[] aligns = new TextAlignment[headers.length];
//					Arrays.fill(aligns, TextAlignment.CENTER);
//					aligns[2] = TextAlignment.LEFT;
//
//					for (int i = 0; i < headers.length; i++) {
//						itemTable.addHeaderCell(new Cell()
//								.add(new Paragraph(headers[i]).setFont(boldFont).setFontSize(9)
//										.setFontColor(ColorConstants.WHITE))
//								.setBackgroundColor(tableBlue).setTextAlignment(aligns[i]).setPaddingTop(6)
//								.setPaddingBottom(6)
//								.setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.3f)));
//					}
//
//					boolean alternate = false;
//					int srNo = 1;
//					double catTotal = 0;
//					System.out.println("Size:- " + cat.getItems().size());
//					for (EventRawMaterialDisposableItemResponseDto item : cat.getItems()) {
//						Color bg = alternate ? altRow : ColorConstants.WHITE;
//						alternate = !alternate;
//
//						if (isAllItems != 1 && item.getQty() == 0) {
//							continue;
//						}
//						
//						double qty = item.getQty() != null ? item.getQty() : 0.0;
//						double suppRate = item.getSupRate() != null ? item.getSupRate() : 0.0;
//						double totalRate = item.getTotalRate() != null ? item.getTotalRate() : 0.0;
//						catTotal += totalRate;
//						totalItems++;
//						
//						String rawMaterial = "";
//						String unit = "";
//						if(lang == 1) {
//							rawMaterial = item.getRawMaterialNameHindi() != null ? item.getRawMaterialNameHindi() : "";
//							unit = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "-";
//						}else if(lang == 2) {
//							rawMaterial = item.getRawMaterialNameGujarati() != null ? item.getRawMaterialNameGujarati() : "-";
//							unit = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "-";
//						}else {
//							rawMaterial = item.getRawMaterialNameEnglish() != null ? item.getRawMaterialNameEnglish() : "-";
//							unit = item.getUnitNameEnglish() != null ? item.getUnitNameEnglish() : "-";
//						}
//						itemTable.addCell(
//								dCell(String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER, borderGray));
//
//						if(showImage) {
//							try {
//							ImageData itemData = menuPreparationServiceImpl.loadImageFromResource(item.getImageUrl());
//								if (itemData != null) {
//								    Image itemImg = new Image(itemData);
//	
//								    itemImg.setWidth(UnitValue.createPercentValue(100f));
//								    itemImg.setAutoScale(false);
//								    itemImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
//	
//								    itemTable.addCell(new Cell().add(itemImg).setBorder(new SolidBorder(borderGray, 0.5f)));
//								} else {
//								    itemTable.addCell(new Cell().setBorder(new SolidBorder(borderGray, 0.5f)).setBackgroundColor(bg));
//								}
//							} catch(Exception e) {
//								itemTable.addCell(new Cell().setBorder(new SolidBorder(borderGray, 0.5f)).setBackgroundColor(bg));
//							}
//						}
//						itemTable.addCell(dCell(
//								rawMaterial,
//								bg, regularFont, TextAlignment.LEFT, borderGray));
//
//						if (showPrice) {
//							itemTable.addCell(
//									dCell(dFmt(suppRate), bg, regularFont, TextAlignment.CENTER, borderGray));
//						}
//
//						itemTable.addCell(dCell(dFmt(qty), bg, regularFont, TextAlignment.CENTER, borderGray));
//
//						itemTable.addCell(dCell(unit,
//								bg, regularFont, TextAlignment.CENTER, borderGray));
//
//						if (showPrice) {
//							itemTable.addCell(
//									dCell(dFmt(totalRate), bg, boldFont, TextAlignment.CENTER, borderGray));
//						}
//					}
//
//					// ── Category subtotal row ─────────────────────────────────────
//					if (showPrice) {
//						Color subtotalBg = new DeviceRgb(220, 232, 245);
//
//						int colspan = showImage ? 6 : 5;
//						
//						itemTable.addCell(new Cell(1, colspan).add(new Paragraph("Subtotal").setFont(boldFont).setFontSize(9))
//								.setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(subtotalBg)
//								.setPaddingRight(8).setBorder(new SolidBorder(borderGray, 0.5f)));
//
//						itemTable.addCell(new Cell().add(new Paragraph(dFmt(catTotal)).setFont(boldFont).setFontSize(9))
//								.setBackgroundColor(subtotalBg).setTextAlignment(TextAlignment.RIGHT).setBorder(new SolidBorder(borderGray, 0.5f)));
//					}
//
//					document.add(itemTable);
//					document.add(new Paragraph("").setMarginBottom(12));
//
//					grandTotal += catTotal;
//				}
//			}
//
//			// ══════════════════════════════════════════════════════════════════
//			// SECTION 3 — SUMMARY CARDS
//			// ══════════════════════════════════════════════════════════════════
//			float[] cardW = { 49f, 2f, 49f };
//			Table cardTable = new Table(UnitValue.createPercentArray(cardW));
//			cardTable.setWidth(UnitValue.createPercentValue(100));
//
//			cardTable.addCell(buildDisposableCard("TOTAL ITEMS", String.valueOf(totalItems), cardBg, darkBlue,
//					labelGray, boldFont, regularFont, borderGray));
//
//			cardTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // spacer column
//
//			if (showPrice) {
//				cardTable.addCell(buildDisposableCard("GRAND TOTAL", dFmt(grandTotal), cardBg, darkBlue, labelGray,
//						boldFont, regularFont, borderGray));
//			} else {
//				cardTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // empty placeholder
//			}
//
//			document.add(cardTable);
//
//			// ══════════════════════════════════════════════════════════════════
//			// SECTION 4 — FOOTER
//			// ══════════════════════════════════════════════════════════════════
//			document.add(new Paragraph("").setMarginBottom(16));
//
//			com.itextpdf.layout.borders.Border topBorder = new com.itextpdf.layout.borders.SolidBorder(borderGray,
//					0.5f);
//
//			Table footer = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
//			footer.setWidth(UnitValue.createPercentValue(100));
//			footer.setKeepTogether(true);
//
//			footer.addCell(new Cell()
//					.add(new Paragraph("Disposable Items Report").setFont(regularFont).setFontSize(8)
//							.setFontColor(labelGray))
//					.setBorder(topBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
//					.setBorderBottom(Border.NO_BORDER).setPaddingTop(6));
//
//			document.add(footer);
//			document.close();
//
//			return baos.toByteArray();
//
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	@Override
	public byte[] generatePdfReport(Long eventId, Long userId, Integer isCompanyDetails, Integer isWithPrice,
			Integer lang, Integer isAllItems, Long rawCategoryId, Integer isWithImage, Integer twoColumn) {
		try {
			EventRawMaterialDisposableResponseDto data = getOrLoad(eventId, userId, rawCategoryId, -1L, isAllItems);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter writer = new PdfWriter(baos);

			PdfDocument pdf = new PdfDocument(writer);

			Document document = new Document(pdf, PageSize.A4);

			document.setMargins(30, 30, 30, 30);

			// ============================================================
			// FONTS
			// ============================================================

			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

			if (lang == 1) {

				// Hindi
				System.out.println("Loading Hindi font...");

				boldFont = regularFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

				System.out.println("Hindi font loaded successfully");

			} else if (lang == 2) {

				// Gujarati
				System.out.println("Loading Gujarati font...");

				boldFont = regularFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

				System.out.println("Gujarati font loaded successfully");

			} else {

				boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

				regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

				System.out.println("English font loaded successfully");
			}

			// ============================================================
			// COLORS
			// ============================================================

			Color darkBlue = new DeviceRgb(13, 71, 116);

			Color blackColor = new DeviceRgb(0, 0, 0);

			Color tableBlue = new DeviceRgb(26, 99, 153);

			Color catHeader = new DeviceRgb(44, 82, 130);

			Color lightBlueBg = new DeviceRgb(235, 245, 255);

			Color cardBg = new DeviceRgb(245, 248, 252);

			Color borderGray = new DeviceRgb(200, 210, 220);

			Color labelGray = new DeviceRgb(100, 120, 140);

			Color altRow = new DeviceRgb(245, 248, 252);

			// ============================================================
			// FLAGS
			// ============================================================

			boolean showPrice = isWithPrice != null && isWithPrice == 1;

			boolean showImage = isWithImage != null && isWithImage == 1;

			boolean isTwoColumn = twoColumn != null && twoColumn == 1;

			// ============================================================
			// SECTION 0 — COMPANY HEADER
			// ============================================================

			try {

				Object cmpDtoObj = menuPreparationServiceImpl.getClass().getMethod("getCompanyDetails", Long.class)
						.invoke(menuPreparationServiceImpl, userId);

			} catch (Exception ignored) {

			}

			try {

				com.crmportal.response.dto.CompanyDetailsResponseDto cmpDto = getCompanyDetails(userId);

				if (cmpDto != null) {

					float[] cw = { 20f, 2f, 78f };

					Table ht = new Table(UnitValue.createPercentArray(cw));

					ht.setWidth(UnitValue.createPercentValue(100));

					// ----------------------------------------------------
					// Logo
					// ----------------------------------------------------

					try {

						com.itextpdf.io.image.ImageData logoData = menuPreparationServiceImpl
								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());

						com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(logoData);

						logo.setWidth(100f);

						logo.setAutoScale(false);

						logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

						ht.addCell(new Cell(3, 2).add(logo)
								.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
								.setBorder(Border.NO_BORDER).setPaddingBottom(10f));

					} catch (Exception e) {

						ht.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
					}

					// ----------------------------------------------------
					// Company Name
					// ----------------------------------------------------

					ht.addCell(new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14)
							.setFontColor(blackColor)).setBorder(Border.NO_BORDER).setPaddingLeft(15f));

					// ----------------------------------------------------
					// Phone
					// ----------------------------------------------------

					ht.addCell(new Cell()
							.add(new Paragraph()
									.add(new Text("Phone : ").setFont(boldFont).setFontSize(12)
											.setFontColor(blackColor))
									.add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getOfficeNo() : "")
											.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
							.setBorder(Border.NO_BORDER).setPaddingLeft(15f).setPaddingBottom(10f));

					// ----------------------------------------------------
					// Email
					// ----------------------------------------------------

					ht.addCell(new Cell()
							.add(new Paragraph()
									.add(new Text("Email : ").setFont(boldFont).setFontSize(14)
											.setFontColor(blackColor))
									.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
											.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
							.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(15f)
							.setPaddingBottom(10f));

					ht.addCell(new Cell().setBorder(Border.NO_BORDER));

					document.add(ht);

					// ----------------------------------------------------
					// Divider
					// ----------------------------------------------------

					Table div = new Table(UnitValue.createPercentArray(new float[] { 100f }));

					div.setWidth(UnitValue.createPercentValue(100));

					div.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));

					document.add(div);

					document.add(new Paragraph("").setMarginBottom(8));
				}

			} catch (Exception ignored) {

			}

			// ============================================================
			// SECTION 1 — TITLE + DATE
			// ============================================================

			String printedOn = "Printed on: "
					+ java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));

			float[] topW = { 60f, 40f };

			Table topTable = new Table(UnitValue.createPercentArray(topW));

			topTable.setWidth(UnitValue.createPercentValue(100));

			topTable.addCell(new Cell()
					.add(new Paragraph("Disposable Items Report").setFont(boldFont).setFontSize(20)
							.setFontColor(darkBlue).setMarginBottom(2))
					.add(new Paragraph(printedOn).setFont(regularFont).setFontSize(8).setFontColor(labelGray))
					.setBorder(Border.NO_BORDER).setPaddingBottom(6));

			topTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			document.add(topTable);

			// ------------------------------------------------------------
			// Divider
			// ------------------------------------------------------------

			Table divider = new Table(UnitValue.createPercentArray(new float[] { 100f }));

			divider.setWidth(UnitValue.createPercentValue(100));

			divider.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));

			document.add(divider);

			document.add(new Paragraph("").setMarginBottom(12));

			// ============================================================
			// SECTION 2 — CATEGORY TABLES
			// ============================================================

			double grandTotal = 0;

			int totalItems = 0;

			if (data.getCategories() != null && !data.getCategories().isEmpty()) {

				for (EventRawMaterialDisposableCategoryDto cat : data.getCategories()) {

					if (cat.getItems() == null || cat.getItems().isEmpty()) {

						continue;
					}

					// ====================================================
					// CATEGORY HEADER
					// ====================================================

					Table catBand = new Table(UnitValue.createPercentArray(new float[] { 100f }));

					catBand.setWidth(UnitValue.createPercentValue(100));

					catBand.addCell(new Cell()
							.add(new Paragraph(
									cat.getCategoryNameEnglish() != null ? cat.getCategoryNameEnglish().toUpperCase()
											: "UNCATEGORIZED")
									.setFont(boldFont).setFontSize(11).setFontColor(ColorConstants.WHITE)
									.setTextAlignment(TextAlignment.CENTER))
							.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
							.setBackgroundColor(catHeader).setBorder(Border.NO_BORDER).setPaddingTop(6)
							.setPaddingBottom(6));

					document.add(catBand);

					if (isTwoColumn) {
						float[] colW = { 8f, 37f, 15f, 8f, 37f, 15f };

						String[] headers = { "#", "Raw Material Name", "Qty / Unit", "#", "Raw Material Name",
								"Qty / Unit" };

						Table itemTable = new Table(UnitValue.createPercentArray(colW));

						itemTable.setWidth(UnitValue.createPercentValue(100));

						itemTable.setFixedLayout();

						// ------------------------------------------------
						// Headers
						// ------------------------------------------------

						for (int i = 0; i < headers.length; i++) {

							TextAlignment alignment = TextAlignment.CENTER;

							if (i == 1 || i == 4) {
								alignment = TextAlignment.LEFT;
							}

							itemTable.addHeaderCell(new Cell()
									.add(new Paragraph(headers[i]).setFont(boldFont).setFontSize(8)
											.setFontColor(ColorConstants.WHITE))
									.setBackgroundColor(tableBlue).setTextAlignment(alignment).setPaddingTop(5)
									.setPaddingBottom(5).setPaddingLeft(3).setPaddingRight(3)
									.setBorder(new SolidBorder(borderGray, 0.3f)));
						}

						boolean alternate = false;

						int srNo = 1;

						int itemIndex = 0;

						while (itemIndex < cat.getItems().size()) {

							EventRawMaterialDisposableItemResponseDto item1 = cat.getItems().get(itemIndex);

							if (isAllItems != null && isAllItems != 1
									&& (item1.getQty() == null || item1.getQty() == 0)) {

								itemIndex++;
								continue;
							}

							Color bg = alternate ? altRow : ColorConstants.WHITE;

							alternate = !alternate;

							String rawMaterial1 = "";
							String unit1 = "";

							if (lang == 1) {
								rawMaterial1 = item1.getRawMaterialNameHindi() != null ? item1.getRawMaterialNameHindi()
										: "-";

								unit1 = item1.getUnitNameHindi() != null ? item1.getUnitNameHindi() : "-";

							} else if (lang == 2) {
								rawMaterial1 = item1.getRawMaterialNameGujarati() != null
										? item1.getRawMaterialNameGujarati()
										: "-";

								unit1 = item1.getUnitNameGujarati() != null ? item1.getUnitNameGujarati() : "-";

							} else {
								rawMaterial1 = item1.getRawMaterialNameEnglish() != null
										? item1.getRawMaterialNameEnglish()
										: "-";

								unit1 = item1.getUnitNameEnglish() != null ? item1.getUnitNameEnglish() : "-";
							}

							double qty1 = item1.getQty() != null ? item1.getQty() : 0.0;

							String qtyUnit1 = dFmt(qty1) + " " + unit1;

							itemTable.addCell(
									dCell(String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER, borderGray));

							itemTable.addCell(dCell(rawMaterial1, bg, regularFont, TextAlignment.LEFT, borderGray));

							itemTable.addCell(dCell(qtyUnit1, bg, regularFont, TextAlignment.CENTER, borderGray));

							totalItems++;

							itemIndex++;

							EventRawMaterialDisposableItemResponseDto item2 = null;

							while (itemIndex < cat.getItems().size()) {

								EventRawMaterialDisposableItemResponseDto nextItem = cat.getItems().get(itemIndex);

								if (isAllItems != null && isAllItems != 1
										&& (nextItem.getQty() == null || nextItem.getQty() == 0)) {

									itemIndex++;
									continue;
								}

								item2 = nextItem;
								break;
							}

							if (item2 != null) {

								String rawMaterial2 = "";
								String unit2 = "";

								if (lang == 1) {

									rawMaterial2 = item2.getRawMaterialNameHindi() != null
											? item2.getRawMaterialNameHindi()
											: "-";

									unit2 = item2.getUnitNameHindi() != null ? item2.getUnitNameHindi() : "-";

								} else if (lang == 2) {

									rawMaterial2 = item2.getRawMaterialNameGujarati() != null
											? item2.getRawMaterialNameGujarati()
											: "-";

									unit2 = item2.getUnitNameGujarati() != null ? item2.getUnitNameGujarati() : "-";

								} else {

									rawMaterial2 = item2.getRawMaterialNameEnglish() != null
											? item2.getRawMaterialNameEnglish()
											: "-";

									unit2 = item2.getUnitNameEnglish() != null ? item2.getUnitNameEnglish() : "-";
								}

								double qty2 = item2.getQty() != null ? item2.getQty() : 0.0;

								String qtyUnit2 = dFmt(qty2) + " " + unit2;

								itemTable.addCell(dCell(String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER,
										borderGray));

								itemTable.addCell(dCell(rawMaterial2, bg, regularFont, TextAlignment.LEFT, borderGray));

								itemTable.addCell(dCell(qtyUnit2, bg, regularFont, TextAlignment.CENTER, borderGray));

								totalItems++;

								itemIndex++;

							} else {
								itemTable.addCell(
										new Cell().setBackgroundColor(bg).setBorder(new SolidBorder(borderGray, 0.3f)));

								itemTable.addCell(
										new Cell().setBackgroundColor(bg).setBorder(new SolidBorder(borderGray, 0.3f)));

								itemTable.addCell(
										new Cell().setBackgroundColor(bg).setBorder(new SolidBorder(borderGray, 0.3f)));
							}
						}

						document.add(itemTable);

						document.add(new Paragraph("").setMarginBottom(12));

					} else {
						String[] headers;

						float[] colW;

						if (showPrice) {

							if (showImage) {

								headers = new String[] { "#", "Image", "Raw Material Name", "Supp. Rate", "Qty", "Unit",
										"Total Rate" };

								colW = new float[] { 10f, 20f, 20f, 10f, 10f, 15f, 15f };

							} else {

								headers = new String[] { "#", "Raw Material Name", "Supp. Rate", "Qty", "Unit",
										"Total Rate" };

								colW = new float[] { 10f, 40f, 10f, 10f, 15f, 15f };
							}

						} else {

							if (showImage) {

								headers = new String[] { "#", "Image", "Raw Material Name", "Qty", "Unit" };

								colW = new float[] { 10f, 25f, 35f, 15f, 15f };

							} else {

								headers = new String[] { "#", "Raw Material Name", "Qty", "Unit" };

								colW = new float[] { 10f, 50f, 20f, 20f };
							}
						}

						// -------------------------------------------------
						// Items Table
						// -------------------------------------------------

						Table itemTable = new Table(UnitValue.createPercentArray(colW));

						itemTable.setWidth(UnitValue.createPercentValue(100));

						itemTable.setFixedLayout();

						TextAlignment[] aligns = new TextAlignment[headers.length];

						Arrays.fill(aligns, TextAlignment.CENTER);

						if (showPrice) {

							if (showImage) {
								aligns[2] = TextAlignment.LEFT;
							} else {
								aligns[1] = TextAlignment.LEFT;
							}

						} else {

							if (showImage) {
								aligns[2] = TextAlignment.LEFT;
							} else {
								aligns[1] = TextAlignment.LEFT;
							}
						}

						// -------------------------------------------------
						// Header
						// -------------------------------------------------

						for (int i = 0; i < headers.length; i++) {

							itemTable.addHeaderCell(new Cell()
									.add(new Paragraph(headers[i]).setFont(boldFont).setFontSize(9)
											.setFontColor(ColorConstants.WHITE))
									.setBackgroundColor(tableBlue).setTextAlignment(aligns[i]).setPaddingTop(6)
									.setPaddingBottom(6).setBorder(new SolidBorder(borderGray, 0.3f)));
						}

						boolean alternate = false;

						int srNo = 1;

						double catTotal = 0;

						System.out.println("Size:- " + cat.getItems().size());

						// -------------------------------------------------
						// Items
						// -------------------------------------------------

						for (EventRawMaterialDisposableItemResponseDto item : cat.getItems()) {

							if (isAllItems != null && isAllItems != 1 && item.getQty() != null && item.getQty() == 0) {

								continue;
							}

							Color bg = alternate ? altRow : ColorConstants.WHITE;

							alternate = !alternate;

							double qty = item.getQty() != null ? item.getQty() : 0.0;

							double suppRate = item.getSupRate() != null ? item.getSupRate() : 0.0;

							double totalRate = item.getTotalRate() != null ? item.getTotalRate() : 0.0;

							catTotal += totalRate;

							totalItems++;

							String rawMaterial = "";

							String unit = "";

							if (lang == 1) {

								rawMaterial = item.getRawMaterialNameHindi() != null ? item.getRawMaterialNameHindi()
										: "";

								unit = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "-";

							} else if (lang == 2) {

								rawMaterial = item.getRawMaterialNameGujarati() != null
										? item.getRawMaterialNameGujarati()
										: "-";

								unit = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "-";

							} else {

								rawMaterial = item.getRawMaterialNameEnglish() != null
										? item.getRawMaterialNameEnglish()
										: "-";

								unit = item.getUnitNameEnglish() != null ? item.getUnitNameEnglish() : "-";
							}

							// -------------------------------------------------
							// Sr No
							// -------------------------------------------------

							itemTable.addCell(
									dCell(String.valueOf(srNo++), bg, regularFont, TextAlignment.CENTER, borderGray));

							// -------------------------------------------------
							// Image
							// -------------------------------------------------

							if (showImage) {

								try {

									ImageData itemData = menuPreparationServiceImpl
											.loadImageFromResource(item.getImageUrl());

									if (itemData != null) {

										Image itemImg = new Image(itemData);

										itemImg.setWidth(UnitValue.createPercentValue(100f));

										itemImg.setAutoScale(false);

										itemImg.setHorizontalAlignment(HorizontalAlignment.CENTER);

										itemTable.addCell(
												new Cell().add(itemImg).setBorder(new SolidBorder(borderGray, 0.5f)));

									} else {

										itemTable.addCell(new Cell().setBorder(new SolidBorder(borderGray, 0.5f))
												.setBackgroundColor(bg));
									}

								} catch (Exception e) {

									itemTable.addCell(new Cell().setBorder(new SolidBorder(borderGray, 0.5f))
											.setBackgroundColor(bg));
								}
							}

							// -------------------------------------------------
							// Raw Material
							// -------------------------------------------------

							itemTable.addCell(dCell(rawMaterial, bg, regularFont, TextAlignment.LEFT, borderGray));

							// -------------------------------------------------
							// Supplier Rate
							// -------------------------------------------------

							if (showPrice) {

								itemTable.addCell(
										dCell(dFmt(suppRate), bg, regularFont, TextAlignment.CENTER, borderGray));
							}

							// -------------------------------------------------
							// Qty
							// -------------------------------------------------

							itemTable.addCell(dCell(dFmt(qty), bg, regularFont, TextAlignment.CENTER, borderGray));

							// -------------------------------------------------
							// Unit
							// -------------------------------------------------

							itemTable.addCell(dCell(unit, bg, regularFont, TextAlignment.CENTER, borderGray));

							// -------------------------------------------------
							// Total Rate
							// -------------------------------------------------

							if (showPrice) {

								itemTable.addCell(
										dCell(dFmt(totalRate), bg, boldFont, TextAlignment.CENTER, borderGray));
							}
						}

						// -------------------------------------------------
						// Category subtotal
						// -------------------------------------------------

						if (showPrice) {

							Color subtotalBg = new DeviceRgb(220, 232, 245);

							int colspan = showImage ? 6 : 5;

							itemTable.addCell(
									new Cell(1, colspan).add(new Paragraph("Subtotal").setFont(boldFont).setFontSize(9))
											.setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(subtotalBg)
											.setPaddingRight(8).setBorder(new SolidBorder(borderGray, 0.5f)));

							itemTable.addCell(
									new Cell().add(new Paragraph(dFmt(catTotal)).setFont(boldFont).setFontSize(9))
											.setBackgroundColor(subtotalBg).setTextAlignment(TextAlignment.RIGHT)
											.setBorder(new SolidBorder(borderGray, 0.5f)));
						}

						document.add(itemTable);

						document.add(new Paragraph("").setMarginBottom(12));

						grandTotal += catTotal;
					}
				}
			}

			// ============================================================
			// SECTION 3 — SUMMARY CARDS
			// ============================================================

			float[] cardW = { 49f, 2f, 49f };

			Table cardTable = new Table(UnitValue.createPercentArray(cardW));

			cardTable.setWidth(UnitValue.createPercentValue(100));

			cardTable.addCell(buildDisposableCard("TOTAL ITEMS", String.valueOf(totalItems), cardBg, darkBlue,
					labelGray, boldFont, regularFont, borderGray));

			cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			/*
			 * In two-column mode price information is not displayed.
			 */
			if (showPrice && !isTwoColumn) {

				cardTable.addCell(buildDisposableCard("GRAND TOTAL", dFmt(grandTotal), cardBg, darkBlue, labelGray,
						boldFont, regularFont, borderGray));

			} else {

				cardTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			}

			document.add(cardTable);

			// ============================================================
			// SECTION 4 — FOOTER
			// ============================================================

			document.add(new Paragraph("").setMarginBottom(16));

			com.itextpdf.layout.borders.Border topBorder = new com.itextpdf.layout.borders.SolidBorder(borderGray,
					0.5f);

			Table footer = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));

			footer.setWidth(UnitValue.createPercentValue(100));

			footer.setKeepTogether(true);

			footer.addCell(new Cell()
					.add(new Paragraph("Disposable Items Report").setFont(regularFont).setFontSize(8)
							.setFontColor(labelGray))
					.setBorder(topBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
					.setBorderBottom(Border.NO_BORDER).setPaddingTop(6));

			document.add(footer);

			// ============================================================
			// CLOSE
			// ============================================================

			document.close();

			return baos.toByteArray();

		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
	
	private Cell dCell(String text, Color bg, PdfFont font, TextAlignment align, Color borderColor) {
		return new Cell().add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(9))
				.setBackgroundColor(bg).setTextAlignment(align)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.3f)).setPaddingTop(4)
				.setPaddingBottom(4).setPaddingLeft(4).setPaddingRight(4);
	}

	private Cell buildDisposableCard(String label, String value, Color cardBg, Color darkBlue, Color labelGray,
			PdfFont boldFont, PdfFont regularFont, Color borderGray) {
		return new Cell()
				.add(new Paragraph(label).setFont(regularFont).setFontSize(7).setFontColor(labelGray)
						.setMarginBottom(4))
				.add(new Paragraph(value).setFont(boldFont).setFontSize(20).setFontColor(darkBlue))
				.setBackgroundColor(cardBg).setBorder(new com.itextpdf.layout.borders.SolidBorder(borderGray, 0.8f))
				.setPadding(12);
	}

	private String dFmt(double val) {
		if (val == Math.floor(val) && !Double.isInfinite(val)) {
			return String.format("%.2f", val);
		}
		return String.format("%.2f", val);
	}

	public CompanyDetailsResponseDto getCompanyDetails(Long userId) {
		UserMasterEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		Optional<Object[]> op = menuAllocationRepository.getCompanyDetailsByUserId(userId);

		if (!op.isPresent()) {
			return new CompanyDetailsResponseDto();
		} else {
			Object[] row = op.get();

			if (row.length == 1 && row[0] instanceof Object[]) {
				row = (Object[]) row[0];
			}

			CompanyDetailsResponseDto dto = new CompanyDetailsResponseDto();
			dto.setCompanyName(row[0] != null ? row[0].toString() : "");
			dto.setCountryCode(row[1] != null ? row[1].toString() : "");
			dto.setCompanyEmail(row[2] != null ? row[2].toString() : "");
			dto.setOfficeNo(row[3] != null ? row[3].toString() : "");
			dto.setAddress(row[4] != null ? row[4].toString() : "");
			dto.setLogo(row[5] != null ? row[5].toString() : "");
			return dto;
		}
	}
}