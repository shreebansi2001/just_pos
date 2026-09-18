package com.crmportal.service.impl;

import java.io.IOException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.PartyMasterMapper;
import com.crmportal.repository.ContactCategoryMasterRepository;
import com.crmportal.repository.ContactTypeMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.RawMaterialCategoryTypeMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.PartyMasterRequestDto;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.PartyMasterService;
import com.crmportal.service.UserFileService;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.font.constants.StandardFonts;
//iText 7 imports
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

//Apache POI imports
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.ByteArrayOutputStream;

@Service
public class PartyMasterServiceImpl implements PartyMasterService {

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	PartyMasterMapper partyMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	ContactCategoryMasterRepository categoryMasterRepository;

	@Autowired
	Environment environment;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	ContactTypeMasterRepository contactTypeMasterRepository;

	@Autowired
	UserFileService userFileService;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Autowired
	ContactCategoryMasterRepository contactCategoryMasterRepository;

	@Override
	public PartyMasterResponseDto addOrUpdatePartyMaster(@Valid PartyMasterRequestDto request, long id,
			MultipartFile file) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String dateFormat = "dd/MM/yyyy";
		PartyMasterEntity entity;
		Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		if (!userOptional.isPresent()) {
			throw new RuntimeException("User not found with id: " + request.getUserId());
		}

		ContactCategoryMasterEntity contactCategory = contactCategoryMasterRepository
				.findByIdAndIsDeleteFalse(request.getContactCategoryId())
				.orElseThrow(() -> new RuntimeException("Contact category is not exist."));

		if (id == -1) {

//			if (partyMasterRepository.existsByContact_ContactTypeAndMobilenoAndIsDeleteFalseAndUser(
//					contactCategory.getContactType(), request.getMobileno(), userOptional.get())) {
//
//				throw new RuntimeException("Mobile number already exists");
//			}

			entity = partyMasterMapper.requestToEntity(request);

		} else {

			Optional<PartyMasterEntity> optional = partyMasterRepository.findByIdAndIsDeleteFalse(id);

			if (!optional.isPresent()) {
				throw new RuntimeException("Party not found with id: " + id);
			}

			entity = optional.get();

			if (partyMasterRepository.existsByContact_ContactTypeAndMobilenoAndIdNotAndIsDeleteFalseAndUser(
					contactCategory.getContactType(), request.getMobileno(), entity.getId(), userOptional.get())) {

				throw new RuntimeException("Mobile number already exists");
			}

			entity.setUpdatedAt(commonService.getCurrentDateTime());
			partyMasterMapper.updateEntityFromRequest(request, entity);
		}

		if (entity.getOpb() == null) {
			entity.setOpb(BigDecimal.ZERO);
		}

		if (entity.getPrice() == null) {
			entity.setPrice(BigDecimal.ZERO);
		}

		if (request.getBdate() != null && !request.getBdate().isEmpty()) {
			try {
				entity.setBirthDate(commonService.stringToDate(request.getBdate(), dateFormat));
			} catch (Exception e) {
				throw new RuntimeException("Invalid birth date format. Please use dd/MM/yyyy.");
			}
		}

		entity.setContact(contactCategory);

		entity.setUser(userOptional.get());
		entity.setPan(request.getPan());

		if (id == -1) {

			if (entity.getContact().getContactType().getId() == 2 || entity.getContact().getContactType().getId() == 5
					|| entity.getContact().getContactType().getId() == 6) {

				Optional<String> lastInsertedPartyCodeOptional = partyMasterRepository
						.findLastInsertedPartyCode(userOptional.get().getId());
				String lastInsertedPartyCode = "0001";
				Integer newcode = 1;
				if (lastInsertedPartyCodeOptional.isPresent()) {
					lastInsertedPartyCode = lastInsertedPartyCodeOptional.get();
					newcode = Integer.parseInt(lastInsertedPartyCode.substring(lastInsertedPartyCode.length() - 4)) + 1;
				}
				String newcodeString = String.format("%04d", newcode);
				String userId = String.format("%02d", userOptional.get().getId());
				StringBuilder partycode = new StringBuilder("S");
				Optional<UserBasicDetailsMasterEntity> userBasicOptional = userBasicDetailsMasterRepository
						.findByUserAndIsDeleteFalse(userOptional.get());
				String[] compnayDetail = userBasicOptional.get().getCompanyName().split(" ");

				for (String ch : compnayDetail) {
					partycode.append(ch.charAt(0));
				}
				partycode.append(userId);
				partycode.append(newcodeString);

				entity.setPartyCode(partycode.toString());
			}

		}
		entity = partyMasterRepository.save(entity);
		if (file != null && !file.isEmpty()) {
			try {
				userFileService.storeFile(userOptional.get().getId(), ModuleName.PARTY.toString(), entity.getId(),
						FileType.IMAGE.toString(), file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		PartyMasterResponseDto responseDto = partyMasterMapper.entityToResponse(entity);
		responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));

		return responseDto;
	}

	@Override
	public List<PartyMasterResponseDto> getAllPartyMaster() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<PartyMasterEntity> partyMasterEntities = partyMasterRepository.findAllByIsDeleteFalse();
		List<PartyMasterResponseDto> partyMasterResponseDtos = new ArrayList<>();

		if (partyMasterEntities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (PartyMasterEntity partyMasterEntity : partyMasterEntities) {
				PartyMasterResponseDto responseDto = partyMasterMapper.entityToResponse(partyMasterEntity);

				if (partyMasterEntity.getCreatedAt() != null) {
					responseDto.setCreatedAt(partyMasterEntity.getCreatedAt().format(formatter));
				}

				if (partyMasterEntity.getOpbDate() != null) {
					responseDto.setOpbDate(partyMasterEntity.getOpbDate().format(outputFormat));
				}

				if (partyMasterEntity.getDocPath() != null && !partyMasterEntity.getDocPath().trim().isEmpty()) {
					String fullImageUrl = environment.getProperty("app.image.url") + partyMasterEntity.getDocPath();
					responseDto.setDocPath(fullImageUrl);
				} else {
					responseDto.setDocPath("");
				}
				responseDto.setUserId(partyMasterEntity.getUser().getId());
				partyMasterResponseDtos.add(responseDto);
			}
			return partyMasterResponseDtos;
		}
	}

	@Override
	public PartyMasterResponseDto getPartyMasterById(Long id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateTimeFormatter outputFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Optional<PartyMasterEntity> entity = partyMasterRepository.findByIdAndIsDeleteFalse(id);

		if (!entity.isPresent()) {
			return null;
		}
		PartyMasterEntity partyMasterEntity = entity.get();

		PartyMasterResponseDto responseDto = partyMasterMapper.entityToResponse(partyMasterEntity);

		if (partyMasterEntity.getCreatedAt() != null) {
			responseDto.setCreatedAt(partyMasterEntity.getCreatedAt().format(formatter));
		}

		if (partyMasterEntity.getBirthDate() != null) {
			responseDto.setBirthDate(outputFormat.format(partyMasterEntity.getBirthDate()));
		}

		if (partyMasterEntity.getOpbDate() != null) {
			responseDto.setOpbDate(partyMasterEntity.getOpbDate().format(outputFormat2));
		}

		if (partyMasterEntity.getDocPath() != null && !partyMasterEntity.getDocPath().trim().isEmpty()) {
			responseDto.setDocPath(environment.getProperty("app.image.url") + partyMasterEntity.getDocPath());
		} else {
			responseDto.setDocPath("");
		}
		responseDto.setUserId(partyMasterEntity.getUser().getId());
		return responseDto;
	}

	@Override
	public Boolean deleteById(Long id) {
		if (!partyMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			return false;
		}

		Optional<PartyMasterEntity> optionalEntity = partyMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!optionalEntity.isPresent()) {
			return false;
		}

		PartyMasterEntity entity = optionalEntity.get();

		if (eventMasterRepository.existsByPartyAndIsDeleteFalse(entity)) {
			throw new RuntimeException("Customer already exists in Event. Please delete it first");
		}

		entity.setIsDelete(true);
		partyMasterRepository.save(entity);
		return true;
	}

	@Override
	public List<PartyMasterResponseDto> getAllPartyMasterByUserId(Long userId, String partyName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateTimeFormatter outputFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Optional<UserMasterEntity> entity = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!entity.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity masterEntity = entity.get();
		List<PartyMasterEntity> partyMasterEntities;

		if (partyName == null || partyName.trim().isEmpty()) {
			partyMasterEntities = partyMasterRepository.findAllByUserAndIsDeleteFalse(masterEntity);
		} else {
			partyMasterEntities = partyMasterRepository.findBySearchTextAndUserAndIsDeleteFalse(partyName.trim(),
					masterEntity);
		}

		List<PartyMasterResponseDto> partyMasterResponseDtos = new ArrayList<>();
		for (PartyMasterEntity partyMasterEntity : partyMasterEntities) {
			PartyMasterResponseDto responseDto = partyMasterMapper.entityToResponse(partyMasterEntity);

			if (partyMasterEntity.getCreatedAt() != null) {
				responseDto.setCreatedAt(partyMasterEntity.getCreatedAt().format(formatter));
			}

			if (partyMasterEntity.getBirthDate() != null) {
				responseDto.setBirthDate(outputFormat.format(partyMasterEntity.getBirthDate()));
			}

			if (partyMasterEntity.getOpbDate() != null) {
				responseDto.setOpbDate(partyMasterEntity.getOpbDate().format(outputFormat2));
			}

			if (partyMasterEntity.getDocPath() != null && !partyMasterEntity.getDocPath().trim().isEmpty()) {
				String fullImageUrl = environment.getProperty("app.image.url") + partyMasterEntity.getDocPath();
				responseDto.setDocPath(fullImageUrl);
			} else {
				responseDto.setDocPath("");
			}
			responseDto.setUserId(partyMasterEntity.getUser().getId());
			partyMasterResponseDtos.add(responseDto);
		}

		return partyMasterResponseDtos;
	}

	@Override
	public List<PartyMasterResponseDto> getPartyNameWithSearch(String partyName, Long userId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateTimeFormatter outputFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		if (partyName == null || partyName.trim().isEmpty()) {
			return Collections.emptyList();
		}

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity user = userOpt.get();

		List<PartyMasterEntity> partyMasterEntities = partyMasterRepository
				.findBySearchTextAndUserAndIsDeleteFalse(partyName, user);

		List<PartyMasterResponseDto> partyMasterResponseDtos = new ArrayList<>();

		if (partyMasterEntities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (PartyMasterEntity partyMasterEntity : partyMasterEntities) {
				PartyMasterResponseDto responseDto = partyMasterMapper.entityToResponse(partyMasterEntity);

				if (partyMasterEntity.getCreatedAt() != null) {
					responseDto.setCreatedAt(partyMasterEntity.getCreatedAt().format(formatter));
				}

				if (partyMasterEntity.getOpbDate() != null) {
					responseDto.setOpbDate(partyMasterEntity.getOpbDate().format(outputFormat2));
				}

				if (partyMasterEntity.getDocPath() != null && !partyMasterEntity.getDocPath().trim().isEmpty()) {
					String fullImageUrl = environment.getProperty("app.image.url") + partyMasterEntity.getDocPath();
					responseDto.setDocPath(fullImageUrl);
				} else {
					responseDto.setDocPath("");
				}
				responseDto.setUserId(partyMasterEntity.getUser().getId());
				partyMasterResponseDtos.add(responseDto);
			}
			return partyMasterResponseDtos;
		}
	}

	@Override
	public List<PartyMasterResponseDto> getAllPartyMasterByCatTypeId(Long catTypeId, String partyName, Long userId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateTimeFormatter outputFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		ContactTypeMasterEntity entity = contactTypeMasterRepository.findByIdAndIsDeleteFalse(catTypeId);
		if (entity == null) {
			return Collections.emptyList();
		}
		Optional<UserMasterEntity> user = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!user.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity masterEntity = user.get();
		List<PartyMasterEntity> partyMasterEntities;

		if (partyName == null || partyName.trim().isEmpty()) {
			partyMasterEntities = partyMasterRepository.findAllByUserAndIsDeleteFalseAndCatTypeId(masterEntity,
					entity.getId());
		} else {
			partyMasterEntities = partyMasterRepository
					.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndCatTypeId(partyName.trim(),
							masterEntity, entity.getId());
		}

		List<PartyMasterResponseDto> partyMasterResponseDtos = new ArrayList<>();
		for (PartyMasterEntity partyMasterEntity : partyMasterEntities) {
			PartyMasterResponseDto responseDto = partyMasterMapper.entityToResponse(partyMasterEntity);

			if (partyMasterEntity.getCreatedAt() != null) {
				responseDto.setCreatedAt(partyMasterEntity.getCreatedAt().format(formatter));
			}

			if (partyMasterEntity.getBirthDate() != null) {
				responseDto.setBirthDate(outputFormat.format(partyMasterEntity.getBirthDate()));
			}

			if (partyMasterEntity.getDocPath() != null && !partyMasterEntity.getDocPath().trim().isEmpty()) {
				String fullImageUrl = environment.getProperty("app.image.url") + partyMasterEntity.getDocPath();
				responseDto.setDocPath(fullImageUrl);
			} else {
				responseDto.setDocPath("");
			}
			if (partyMasterEntity.getOpbDate() != null) {
				responseDto.setOpbDate(partyMasterEntity.getOpbDate().format(outputFormat2));
			}
			responseDto.setUserId(partyMasterEntity.getUser().getId());
			partyMasterResponseDtos.add(responseDto);
		}

		return partyMasterResponseDtos;
	}

	@Override
	public List<PartyMasterResponseDto> getAllPartyMasterByContCatId(Long contCatId, String partyName, Long userId) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		SimpleDateFormat birthDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateTimeFormatter opbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity user = userOpt.get();

		boolean isSearch = partyName != null && !partyName.trim().isEmpty();
		boolean isAllCategory = contCatId != null && contCatId == -1;

		List<PartyMasterEntity> entities;

		if (isAllCategory) {

			entities = isSearch
					? partyMasterRepository
							.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(partyName.trim(), user)
					: partyMasterRepository.findAllByUserAndIsDeleteFalse(user);

		} else {

			Optional<ContactCategoryMasterEntity> categoryOpt = categoryMasterRepository
					.findByIdAndIsDeleteFalse(contCatId);

			if (!categoryOpt.isPresent()) {
				return Collections.emptyList();
			}

			Long categoryId = categoryOpt.get().getId();

			entities = isSearch
					? partyMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndContCatId(
							partyName.trim(), user, categoryId)
					: partyMasterRepository.findAllByUserAndIsDeleteFalseAndContCatId(user, categoryId);
		}

		return entities.stream().map(entity -> {
			PartyMasterResponseDto dto = partyMasterMapper.entityToResponse(entity);

			Optional.ofNullable(entity.getCreatedAt()).ifPresent(date -> dto.setCreatedAt(date.format(formatter)));

			Optional.ofNullable(entity.getBirthDate())
					.ifPresent(date -> dto.setBirthDate(birthDateFormat.format(date)));

			Optional.ofNullable(entity.getOpbDate()).ifPresent(date -> dto.setOpbDate(date.format(opbFormatter)));

			String docPath = entity.getDocPath();
			if (docPath != null && !docPath.trim().isEmpty()) {
				dto.setDocPath(environment.getProperty("app.image.url") + docPath);
			} else {
				dto.setDocPath("");
			}

			dto.setUserId(entity.getUser().getId());

			return dto;

		}).collect(Collectors.toList());
	}

	@Override
	public byte[] generatePartyReportPdf(String type, Long userId) {
		List<PartyMasterEntity> entities = getFilteredEntities(type, userId);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter writer = new PdfWriter(out);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc, PageSize.A4.rotate());
			document.setMargins(20, 20, 20, 20);
			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			// ---- Title ----
			String title = type.equalsIgnoreCase("party") ? "Party Report" : "Vendor Report";
			Paragraph titlePara = new Paragraph(title).setFontSize(20).setFont(boldFont).setUnderline()
					.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15);
			document.add(titlePara);

			// ---- Table ----
			float[] columnWidths = { 2f, 8f, 6f, 6f, 7f, 4f, 5f };
			Table table = new Table(UnitValue.createPercentArray(columnWidths));
			table.setWidth(UnitValue.createPercentValue(100));

			// ---- Header Background Color ----
			DeviceRgb headerBg = new DeviceRgb(235, 245, 255);

			// ---- Add Header Cells ----
			String[] headers = { "Sr.", "Name", "City", "Category", "Email", "Mobile No.", "GST No." };
			for (String header : headers) {
				Cell cell = new Cell().add(new Paragraph(header)).setFont(boldFont).setFontSize(13)
						.setFontColor(ColorConstants.BLACK).setBackgroundColor(headerBg)
						.setTextAlignment(TextAlignment.CENTER).setPadding(6);
				table.addHeaderCell(cell);
			}

			// ---- Data Rows ----
			DeviceRgb altRowBg = new DeviceRgb(255, 255, 255);
			int srNo = 1;
			for (PartyMasterEntity entity : entities) {
				DeviceRgb rowColor = (srNo % 2 == 0) ? altRowBg : null;

				addCell(table, String.valueOf(srNo++), rowColor, TextAlignment.CENTER, 10);
				addCell(table, nullSafe(entity.getNameEnglish()), rowColor, TextAlignment.LEFT, 10);
				addCell(table, nullSafe(entity.getAddressEnglish()), rowColor, TextAlignment.LEFT, 10);
				addCell(table, entity.getContact() != null ? nullSafe(entity.getContact().getNameEnglish()) : "",
						rowColor, TextAlignment.LEFT, 10);
				addCell(table, nullSafe(entity.getEmail()), rowColor, TextAlignment.LEFT, 10);
				addCell(table, nullSafe(entity.getMobileno()), rowColor, TextAlignment.CENTER, 10);
				addCell(table, nullSafe(entity.getGst()), rowColor, TextAlignment.CENTER, 10);
			}

			document.add(table);
			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	// ===================== EXCEL REPORT =====================
	@Override
	public byte[] generatePartyReportExcel(String type, Long userId) {
		List<PartyMasterEntity> entities = getFilteredEntities(type, userId);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			String sheetName = type.equalsIgnoreCase("party") ? "Party Report" : "Vendor Report";
			HSSFSheet sheet = workbook.createSheet(sheetName);

			// ---- Header Style ----
			HSSFCellStyle headerStyle = workbook.createCellStyle();
			HSSFFont headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			// ---- Data Style ----
			HSSFCellStyle dataStyle = workbook.createCellStyle();
			dataStyle.setBorderBottom(BorderStyle.THIN);
			dataStyle.setBorderTop(BorderStyle.THIN);
			dataStyle.setBorderLeft(BorderStyle.THIN);
			dataStyle.setBorderRight(BorderStyle.THIN);

			// ---- Header Row ----
			String[] headers = { "Sr No.", "Name", "City", "Category", "Email", "Mobile No.", "GST No." };
			HSSFRow headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				HSSFCell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// ---- Data Rows ----
			int rowNum = 1;
			for (PartyMasterEntity entity : entities) {
				HSSFRow row = sheet.createRow(rowNum);
				createExcelCell(row, 0, String.valueOf(rowNum), dataStyle);
				createExcelCell(row, 1, nullSafe(entity.getNameEnglish()), dataStyle);
				createExcelCell(row, 2, nullSafe(entity.getAddressEnglish()), dataStyle);
				createExcelCell(row, 3,
						entity.getContact() != null ? nullSafe(entity.getContact().getNameEnglish()) : "", dataStyle);
				createExcelCell(row, 4, nullSafe(entity.getEmail()), dataStyle);
				createExcelCell(row, 5, nullSafe(entity.getMobileno()), dataStyle);
				createExcelCell(row, 6, nullSafe(entity.getGst()), dataStyle);
				rowNum++;
			}

			// ---- Auto size ----
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	// ===================== HELPER METHODS =====================
	private List<PartyMasterEntity> getFilteredEntities(String type, Long userId) {
		if (type == null || type.trim().isEmpty()) {
			throw new RuntimeException("Type is required. Allowed values: 'party' or 'vendor'");
		}

		if (!type.equalsIgnoreCase("party") && !type.equalsIgnoreCase("vendor")) {
			throw new RuntimeException("Invalid type '" + type + "'. Allowed values: 'party' or 'vendor'");
		}

		if (type.equalsIgnoreCase("party")) {
			return partyMasterRepository.findAllPartyByUserId(userId);
		} else {
			return partyMasterRepository.findAllVendorByUserId(userId);
		}
	}

	private String nullSafe(String value) {
		return value != null ? value : "";
	}

	// ---- Replace old addPdfCell with this ----
	private void addCell(Table table, String text, DeviceRgb bgColor, TextAlignment alignment, float fontSize) {
		Cell cell = new Cell().add(new Paragraph(text)).setFontSize(fontSize).setTextAlignment(alignment).setPadding(4);
		if (bgColor != null) {
			cell.setBackgroundColor(bgColor);
		}
		table.addCell(cell);
	}

	private void createExcelCell(HSSFRow row, int col, String value, HSSFCellStyle style) {
		HSSFCell cell = row.createCell(col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	public Long getOrCreatePartyId(Long leadId) {

		LeadMasterEntity lead = leadMasterRepository.findById(leadId)
				.orElseThrow(() -> new RuntimeException("Lead not found with id : " + leadId));

		Optional<PartyMasterEntity> existingParty = partyMasterRepository
				.findByUserIdAndMobileno(lead.getUser().getId(), lead.getContactNumber());

		if (existingParty.isPresent()) {
			return existingParty.get().getId();
		}

		PartyMasterEntity party = new PartyMasterEntity();

		Optional<ContactCategoryMasterEntity> category = contactCategoryMasterRepository
				.findFirstByUserIdAndContactTypeIdAndIsDeleteFalseOrderBySequenceAsc(lead.getUser().getId(), 1L);

		party.setUser(lead.getUser());
		party.setNameEnglish(lead.getClientName());
		party.setMobileno(lead.getContactNumber());
		party.setEmail(lead.getEmailId());
		party.setAddressEnglish("");
		party.setOpbDate(LocalDate.now());
		ContactCategoryMasterEntity contactCategory = category.orElseThrow(
				() -> new RuntimeException("No contact category found for user " + lead.getUser().getId()));

		party.setContact(contactCategory);

		PartyMasterEntity savedParty = partyMasterRepository.save(party);

		return savedParty.getId();
	}

}
