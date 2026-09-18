package com.crmportal.service.impl;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.CustomPackageDetailsEntity;
import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.UserMasterMapper;
import com.crmportal.repository.AdminTemplateModuleRepository;
import com.crmportal.repository.CustomPackageDetailsRepository;
import com.crmportal.repository.CustomPackageRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.CustomPackageDetailsRequestDto;
import com.crmportal.request.dto.CustomPackageMenuItemDetailsDto;
import com.crmportal.request.dto.CustomPackageReportRequestDto;
import com.crmportal.request.dto.CustomPackageRequestDto;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.CustomPackageDetailsResponseDto;
import com.crmportal.response.dto.CustomPackageMenuItemDetailsResponseDto;
import com.crmportal.response.dto.CustomPackageResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.service.AdminTemplateModuleService;
import com.crmportal.service.CustomPackageService;
import com.crmportal.utility.BackgroundEventHandler;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.licensing.base.LicenseKey;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.core.env.Environment;
import com.itextpdf.kernel.colors.Color;

@Service
public class CustomPackageServiceImpl implements CustomPackageService {

	@Autowired
	CustomPackageRepository customPackageRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;

	@Autowired
	CustomPackageDetailsRepository customPackageDetailsRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	UserMasterMapper userMasterMapper;

	@Autowired
	private AdminTemplateModuleRepository adminTemplateMasterRepository;

	@Autowired
	private AdminTemplateModuleService adminTemplateModuleService;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	Environment environment;

	@Override
	@Transactional
	public CustomPackageResponseDto addOrUpdateCustomPackage(@Valid CustomPackageRequestDto request, Long id) {

		UserMasterEntity user = getUserOrThrow(request.getUserId());

		CustomPackageEntity entity = (id == -1) ? new CustomPackageEntity() : getExistingPackageOrThrow(id);

		if (id != -1) {
			customPackageDetailsRepository.deleteByCustomPackage(entity);
		}

		validateDuplicateName(request.getNameEnglish(), user, id);

		entity.setUser(user);
		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setNameHindi(request.getNameHindi());
		entity.setPrice(request.getPrice());

		if (request.getSequence() != null) {
			customPackageRepository.shiftSequencesForUser(user.getId(), request.getSequence());
			entity.setSequence(request.getSequence());
		} else {
			Integer lastSeq = customPackageRepository.findMaxSequenceByUserAndIsDeleteFalse(user.getId());
			entity.setSequence(lastSeq == null ? 1 : lastSeq + 1);
		}

		CustomPackageEntity savedPackage = customPackageRepository.save(entity);

		savePackageDetails(request, savedPackage, user);

		return buildResponse(savedPackage, user);
	}

	private CustomPackageEntity getExistingPackageOrThrow(Long id) {
		CustomPackageEntity entity = customPackageRepository.findByIdAndIsDeleteFalse(id);
		if (entity == null) {
			throw new RuntimeException("Custom Package not found with id: " + id);
		}
		return entity;
	}

	private void validateDuplicateName(String name, UserMasterEntity user, Long id) {
		Optional<CustomPackageEntity> existing = customPackageRepository.findByNameEnglishAndUserAndIsDeleteFalse(name,
				user);

		if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
			throw new RuntimeException("Package '" + name + "' already exists for this user.");
		}
	}

	private void mapBasicFields(CustomPackageEntity entity, CustomPackageRequestDto request, UserMasterEntity user) {

		entity.setUser(user);
		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setNameHindi(request.getNameHindi());
		entity.setPrice(request.getPrice());
	}

	private void setSequence(CustomPackageEntity entity, CustomPackageRequestDto request, UserMasterEntity user) {

		if (request.getSequence() != null) {
			customPackageRepository.shiftSequencesForUser(user.getId(), request.getSequence());
			entity.setSequence(request.getSequence());
		} else {
			Integer lastSeq = customPackageRepository.findMaxSequenceByUserAndIsDeleteFalse(user.getId());
			entity.setSequence(lastSeq == null ? 1 : lastSeq + 1);
		}
	}

	private void savePackageDetails(CustomPackageRequestDto request, CustomPackageEntity savedPackage,
			UserMasterEntity user) {

		if (request.getCustomPackageDetails() == null)
			return;

		List<CustomPackageDetailsEntity> allEntities = new ArrayList<>();

		for (CustomPackageDetailsRequestDto detailDto : request.getCustomPackageDetails()) {

			MenuCategoryMasterEntity category = menuCategoryMasterRepository
					.findByIdAndIsDeleteFalse(detailDto.getMenuId()).orElseThrow(
							() -> new RuntimeException("Menu Category not found with id: " + detailDto.getMenuId()));

			CustomPackageDetailsEntity menuEntity = new CustomPackageDetailsEntity();
			menuEntity.setCustomPackage(savedPackage);
			menuEntity.setUser(user);
			menuEntity.setMenuCategory(category);
			menuEntity.setIsActive(true);
			menuEntity.setIsDelete(false);
			menuEntity.setMenuName(detailDto.getMenuName());
			menuEntity.setMenuInstruction(detailDto.getMenuInstruction());
			menuEntity.setMenuSortOrder(detailDto.getMenuSortOrder());
			menuEntity.setAnyItem(detailDto.getAnyItem());
			menuEntity.setCatNickNameEnglish(detailDto.getCatNickNameEnglish());
			menuEntity.setCatNickNameGujarati(detailDto.getCatNickNameGujarati());
			menuEntity.setCatNickNameHindi(detailDto.getCatNickNameHindi());
			allEntities.add(menuEntity);

			if (detailDto.getCustomPackageMenuItemDetails() != null) {

				for (CustomPackageMenuItemDetailsDto itemDto : detailDto.getCustomPackageMenuItemDetails()) {

					MenuItemMasterEntity menuItem = menuItemMasterRepository
							.findByIdAndIsDeleteFalse(itemDto.getMenuItemId()).orElseThrow(() -> new RuntimeException(
									"Menu Item not found with id: " + itemDto.getMenuItemId()));

					CustomPackageDetailsEntity itemEntity = new CustomPackageDetailsEntity();
					itemEntity.setCustomPackage(savedPackage);
					itemEntity.setUser(user);
					itemEntity.setMenuCategory(category);
					itemEntity.setAnyItem(detailDto.getAnyItem());
					itemEntity.setIsActive(true);
					itemEntity.setIsDelete(false);

					itemEntity.setMenuItem(menuItem);
					itemEntity.setItemName(itemDto.getItemName());
					itemEntity.setItemInstruction(itemDto.getItemInstruction());
					itemEntity.setMenuSortOrder(detailDto.getMenuSortOrder());
					itemEntity.setItemSortOrder(itemDto.getItemSortOrder());
					itemEntity.setItemPrice(itemDto.getItemPrice());
					itemEntity.setItemNickNameEnglish(itemDto.getItemNickNameEnglish());
					itemEntity.setItemNickNameGujarati(itemDto.getItemNickNameGujarati());
					itemEntity.setItemNickNameHindi(itemDto.getItemNickNameHindi());
					allEntities.add(itemEntity);
				}
			}
		}

		customPackageDetailsRepository.saveAll(allEntities);
	}

	private CustomPackageResponseDto buildResponse(CustomPackageEntity savedPackage, UserMasterEntity user) {

		CustomPackageResponseDto response = new CustomPackageResponseDto();

		response.setId(savedPackage.getId());
		response.setNameEnglish(savedPackage.getNameEnglish());
		response.setNameGujarati(savedPackage.getNameGujarati());
		response.setNameHindi(savedPackage.getNameHindi());
		response.setPrice(savedPackage.getPrice());
		response.setSequence(savedPackage.getSequence());
		response.setUserId(user.getId());

		List<CustomPackageDetailsEntity> entities = customPackageDetailsRepository
				.findAllByCustomPackageAndUserAndIsDeleteFalse(savedPackage, user);

		response.setCustomPackageDetails(mapDetails(entities));

		return response;
	}

	private List<CustomPackageDetailsResponseDto> mapDetails(List<CustomPackageDetailsEntity> entities) {

		List<CustomPackageDetailsResponseDto> result = new ArrayList<>();

		Map<Long, List<CustomPackageDetailsEntity>> itemMap = entities.stream().filter(e -> e.getItemName() != null)
				.collect(Collectors.groupingBy(e -> e.getMenuCategory().getId()));

		for (CustomPackageDetailsEntity menu : entities) {

			if (menu.getMenuName() == null)
				continue;

			CustomPackageDetailsResponseDto dto = new CustomPackageDetailsResponseDto();
			dto.setMenuId(menu.getMenuCategory().getId());
			dto.setMenuName(menu.getMenuName());
			dto.setMenuSortOrder(menu.getMenuSortOrder());
			dto.setMenuInstruction(menu.getMenuInstruction());
			dto.setAnyItem(menu.getAnyItem());
			dto.setCatNickNameEnglish(menu.getCatNickNameEnglish());
			dto.setCatNickNameHindi(menu.getCatNickNameHindi());
			dto.setCatNickNameGujarati(menu.getCatNickNameGujarati());
			List<CustomPackageMenuItemDetailsResponseDto> itemList = new ArrayList<>();

			List<CustomPackageDetailsEntity> items = itemMap.getOrDefault(menu.getMenuCategory().getId(),
					Collections.emptyList());

			for (CustomPackageDetailsEntity item : items) {

				CustomPackageMenuItemDetailsResponseDto itemDto = new CustomPackageMenuItemDetailsResponseDto();

				itemDto.setId(item.getId());
				itemDto.setItemName(item.getItemName());
				itemDto.setItemInstruction(item.getItemInstruction());
				itemDto.setItemSortOrder(item.getItemSortOrder());
				itemDto.setItemPrice(item.getItemPrice());
				itemDto.setMenuItemId(item.getMenuItem().getId());
				itemDto.setUserId(item.getUser().getId());
				itemDto.setItemNickNameEnglish(item.getItemNickNameEnglish());
				itemDto.setItemNickNameHindi(item.getItemNickNameHindi());
				itemDto.setItemNickNameGujarati(item.getItemNickNameGujarati());

				itemList.add(itemDto);
			}

			dto.setCustomPackageMenuItemDetails(itemList);
			result.add(dto);
		}

		return result;
	}

	@Override
	public List<CustomPackageResponseDto> getAllCustomPackageByUserId(Long userId, String packageName,
			Boolean isActive) {

		UserMasterEntity user = getUserOrThrow(userId);

		List<CustomPackageEntity> entities = getCustomPackages(user, packageName, isActive);

		return entities.stream().map(entity -> mapToCustomPackageResponse(entity, user)).collect(Collectors.toList());
	}

	@Override
	public Boolean deleteById(Long id) {
		Boolean isSuccess = false;
		if (customPackageRepository.existsByIdAndIsDeleteFalse(id)) {
			CustomPackageEntity entity = customPackageRepository.findByIdAndIsDeleteFalse(id);
			List<CustomPackageDetailsEntity> detailsEntity = customPackageDetailsRepository
					.findAllByCustomPackageAndIsDeleteFalse(entity);
			for (CustomPackageDetailsEntity customPackageDetailsEntity : detailsEntity) {
				customPackageDetailsEntity.setIsDelete(true);
				customPackageDetailsRepository.save(customPackageDetailsEntity);
			}
			entity.setIsDelete(true);
			customPackageRepository.save(entity);
			isSuccess = true;
		}
		return isSuccess;
	}

	@Override
	public Boolean updateStatus(Long id, Boolean isActive) {
		if (customPackageRepository.existsByIdAndIsDeleteFalse(id)) {
			CustomPackageEntity entity = customPackageRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsActive(isActive);
			customPackageRepository.save(entity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public CustomPackageResponseDto getCustomPackageById(Long id) {

		CustomPackageEntity entity = customPackageRepository.findByIdAndIsDeleteFalse(id);

		if (entity == null) {
			throw new RuntimeException("Custom Package not found with id: " + id);
		}
		UserMasterEntity user = entity.getUser();

		return mapToCustomPackageResponse(entity, user);
	}

	private UserMasterEntity getUserOrThrow(Long userId) {
		return userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
	}

	private List<CustomPackageEntity> getCustomPackages(UserMasterEntity user, String packageName, Boolean isActive) {

		boolean noName = (packageName == null || packageName.trim().isEmpty());

		if (noName) {
			return (isActive == null) ? customPackageRepository.findAllByUserAndIsDeleteFalse(user)
					: customPackageRepository.findAllByUserAndIsActiveAndIsDeleteFalse(user, isActive);
		}

		return (isActive == null)
				? customPackageRepository.findAllByUserAndNameEnglishContainingIgnoreCaseAndIsDeleteFalse(user,
						packageName)
				: customPackageRepository.findAllByUserAndNameEnglishContainingIgnoreCaseAndIsActiveAndIsDeleteFalse(
						user, packageName, isActive);
	}

	private CustomPackageResponseDto mapToCustomPackageResponse(CustomPackageEntity entity, UserMasterEntity user) {

		CustomPackageResponseDto dto = new CustomPackageResponseDto();
		dto.setId(entity.getId());
		dto.setNameEnglish(entity.getNameEnglish());
		dto.setNameGujarati(entity.getNameGujarati());
		dto.setNameHindi(entity.getNameHindi());
		dto.setPrice(entity.getPrice());
		dto.setSequence(entity.getSequence());
		dto.setIsActive(entity.getIsActive());

		if (entity.getUser() != null) {
			dto.setUserId(user.getId());
		}

		dto.setCustomPackageDetails(getPackageDetails(entity, user));

		return dto;
	}

	private List<CustomPackageDetailsResponseDto> getPackageDetails(CustomPackageEntity entity, UserMasterEntity user) {

		List<CustomPackageDetailsEntity> allEntities = customPackageDetailsRepository
				.findAllByCustomPackageAndUserAndIsDeleteFalse(entity, user);

		List<CustomPackageDetailsResponseDto> detailDtos = new ArrayList<>();

		if (allEntities == null || allEntities.isEmpty()) {
			return detailDtos;
		}

		List<CustomPackageDetailsEntity> menuList = allEntities.stream().filter(e -> e.getMenuName() != null)
				.collect(Collectors.toList());

		Map<Long, List<CustomPackageDetailsEntity>> itemMap = allEntities.stream().filter(e -> e.getItemName() != null)
				.collect(Collectors.groupingBy(e -> e.getMenuCategory().getId()));

		for (CustomPackageDetailsEntity menu : menuList) {

			Long menuId = menu.getMenuCategory().getId();

			CustomPackageDetailsResponseDto detailDto = new CustomPackageDetailsResponseDto();
			detailDto.setMenuId(menuId);
			detailDto.setMenuName(menu.getMenuName());
			detailDto.setMenuSortOrder(menu.getMenuSortOrder());
			detailDto.setMenuInstruction(menu.getMenuInstruction());
			detailDto.setAnyItem(menu.getAnyItem());
			detailDto.setCatNickNameEnglish(menu.getCatNickNameEnglish());
			detailDto.setCatNickNameHindi(menu.getCatNickNameHindi());
			detailDto.setCatNickNameGujarati(menu.getCatNickNameGujarati());

			List<CustomPackageMenuItemDetailsResponseDto> itemDtos = mapMenuItems(itemMap.get(menuId));

			detailDto.setCustomPackageMenuItemDetails(itemDtos);

			detailDtos.add(detailDto);
		}

		return detailDtos;
	}

	private List<CustomPackageMenuItemDetailsResponseDto> mapMenuItems(List<CustomPackageDetailsEntity> items) {

		List<CustomPackageMenuItemDetailsResponseDto> itemDtos = new ArrayList<>();

		if (items == null || items.isEmpty())
			return itemDtos;

		for (CustomPackageDetailsEntity item : items) {

			CustomPackageMenuItemDetailsResponseDto dto = new CustomPackageMenuItemDetailsResponseDto();

			dto.setId(item.getId());
			dto.setItemName(item.getItemName());
			dto.setItemInstruction(item.getItemInstruction());
			dto.setItemSortOrder(item.getItemSortOrder());
			dto.setItemPrice(item.getItemPrice());
			dto.setUserId(item.getUser().getId());
			dto.setItemNickNameEnglish(item.getItemNickNameEnglish());
			dto.setItemNickNameHindi(item.getItemNickNameHindi());
			dto.setItemNickNameGujarati(item.getItemNickNameGujarati());
			if (item.getMenuItem() != null) {
				dto.setMenuItemId(item.getMenuItem().getId());
			}

			itemDtos.add(dto);
		}

		return itemDtos;
	}

	@Override
	public String generateCustomPackageReport(CustomPackageReportRequestDto req, HttpServletRequest re) {

		try {

			loadLicense();

			PdfFont catFont = null;
			PdfFont catFontBold = null;

			PdfFont itemFont = null;
			PdfFont itemFontBold = null;

			PdfFont sloganFont = null;
			PdfFont sloganFontBold = null;

			Integer catFontSize = req.getCatFontSize();
			Integer itemFontSize = req.getItemFontSize();
			Integer sloganFontSize = req.getSloganFontSize();

			loadLicense();

			Integer lang = req.getLang();

			String pckNameLabel = "";
			String pckPriceLabel = "";
			String anyLabel = "";
			String itemLabel = "";

			int initVal = 715;

			if (lang == 1) {

				// Hindi

				catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

				catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl
						.loadFont("/fonts/Nirmala-Bold.ttf");

				pckNameLabel = "पैकेज का नाम";
				pckPriceLabel = "पैकेज मूल्य";
				anyLabel = "कोई भी";
				itemLabel = "आइटम";

				initVal = 692;

			} else if (lang == 2) {

				// Gujarati / Regional

				String language = userBasicDetailsMasterRepository.getLangByUserId(req.getUserId());

				if (language.equalsIgnoreCase("Tamil")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansTamil-Regular.ttf");

					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansTamil-Bold.ttf");

					pckNameLabel = "தொகுப்பு பெயர்";
					anyLabel = "ஏதேனும்";
					itemLabel = "பொருள்";
					pckPriceLabel = "பேக்கேஜ் விலை";

				} else if (language.equalsIgnoreCase("Telugu")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					pckNameLabel = "ప్యాకేజ్ పేరు";
					anyLabel = "ఏదైనా";
					itemLabel = "వస్తువు";
					pckPriceLabel = "ప్యాకేజ్ ధర";

				} else if (language.equalsIgnoreCase("Malayalam")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					pckNameLabel = "പാക്കേജിന്റെ പേര്";
					anyLabel = "ഏതെങ്കിലും";
					itemLabel = "ഇനം";
					pckPriceLabel = "പാക്കേജ് വില";

				} else if (language.equalsIgnoreCase("Marathi")) {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl
							.loadFont("/fonts/Nirmala-Bold.ttf");

					pckNameLabel = "पॅकेजचे नाव";
					itemLabel = "आयटम";
					anyLabel = "कोणतेही";
					pckPriceLabel = "पॅकेज रेट";

				} else {

					catFont = itemFont = sloganFont = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					catFontBold = itemFontBold = sloganFontBold = menuPreparationServiceImpl
							.loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					pckNameLabel = "પેકેજનું નામ";
					anyLabel = "કોઈપણ";
					itemLabel = "આઇટમ";
					pckPriceLabel = "પેકેજ રેટ";
				}

				initVal = 690;

			} else {

				// English

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
			}

			// =========================================================
			// Fetch Package
			// =========================================================

			CustomPackageEntity packageEntity = customPackageRepository
					.findByIdAndIsDeleteFalse(req.getCustomPackageId());

			if (packageEntity == null) {

				throw new RuntimeException("Custom Package not found with id: " + req.getCustomPackageId());
			}

			UserMasterEntity user = getUserOrThrow(req.getUserId());

			// =========================================================
			// Admin Template
			// =========================================================

			AdminTemplateModuleResponseDto adminTemplate = adminTemplateModuleService
					.getAdminTemplateModuleById(req.getAdminTemplateId(), req.getUserId());

			// =========================================================
			// Colors
			// =========================================================

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

			Color creamGold = new DeviceRgb(r2, g2, b2);

			Color softGold = new DeviceRgb(r1, g1, b1);

			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			// =========================================================
			// Output File
			// =========================================================

			Date now = new Date();

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			String folderName = String.valueOf(req.getUserId());

			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);

			File outputDir = new File(rootPath + "resources/tempDownload/" + folderName + "/");

			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}

			String safePackageName = packageEntity.getNameEnglish() != null
					? packageEntity.getNameEnglish().replaceAll("[^\\p{L}\\p{N}_]", "_")
					: "package";

			String fileName = safePackageName + "_" + datetimeforfile + ".pdf";

			File pdfFile = new File(outputDir, fileName);

			// =========================================================
			// Create PDF
			// =========================================================

			PdfWriter pdfWriter = new PdfWriter(pdfFile);

			PdfDocument pdfDocument = new PdfDocument(pdfWriter);

			Document document = new Document(pdfDocument);

			document.setMargins(10, 35, 30, 40);

			// =========================================================
			// Background Images
			// =========================================================

			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			bgHandler.setPageBackground(1, watermarkBgData);

			bgHandler.setDefaultBackground(watermarkBgData);

			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// =========================================================
			// FRONT PAGE
			// =========================================================

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

			centerCell.add(new Paragraph(nullSafe(packageEntity.getNameEnglish())).setFont(itemFontBold)
					.setFontColor(descriptionColor).setFontSize(28).setMarginBottom(35));

			centerCell.add(new Paragraph(pckPriceLabel).setFont(catFontBold).setFontColor(softGold).setFontSize(22)
					.setMarginBottom(10));

			centerCell.add(new Paragraph(
					packageEntity.getPrice() != null ? "₹ " + packageEntity.getPrice().toPlainString() : "-")
					.setFont(itemFontBold).setFontColor(descriptionColor).setFontSize(26));

			centerTable.addCell(centerCell);

			document.add(centerTable);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// =========================================================
			// Native Query
			// =========================================================

			List<Object[]> allDetails = customPackageDetailsRepository.getPackageDetailsLangWise(packageEntity.getId(),
					user.getId(), lang);
			// =========================================================
			// Object[] Mapping
			// =========================================================
			//
			// 0 = custom_package_details_id
			// 1 = menu_name
			// 2 = item_name
			// 3 = menu_instruction
			// 4 = menu_sortorder
			// 5 = item_instruction
			// 6 = item_sortorder
			// 7 = any_item
			// 8 = item_price
			// 9 = menu_category_id
			//
			// =========================================================

			Map<Long, List<Object[]>> itemMap = allDetails.stream().filter(e -> e[2] != null).collect(
					Collectors.groupingBy(e -> ((Number) e[9]).longValue(), LinkedHashMap::new, Collectors.toList()));

			List<Object[]> menuList = allDetails.stream().filter(e -> e[1] != null)
					.collect(Collectors.collectingAndThen(
							Collectors.toMap(e -> ((Number) e[9]).longValue(), e -> e,
									(existing, replacement) -> existing, LinkedHashMap::new),
							map -> new ArrayList<>(map.values())));

			// =========================================================
			// Menu Categories
			// =========================================================

			for (Object[] menu : menuList) {

				Long menuCategoryId = ((Number) menu[9]).longValue();

				List<Object[]> items = itemMap.getOrDefault(menuCategoryId, Collections.emptyList());

				float[] columnWidths = { 49f, 2f, 49f };

				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));

				headerTable.setWidth(UnitValue.createPercentValue(100));

				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(20f);

				Paragraph catPara = new Paragraph();

				String menuName = menu[1] != null ? menu[1].toString() : "";

				catPara.add(new Text(menuName).setFont(catFontBold).setFontSize(22).setFontColor(softGold));

				// Number of items in this category
				int itemCount = items.size();

				if (itemCount > 0) {

					catPara.add(new Text("  •  ").setFont(catFont).setFontSize(18).setFontColor(creamGold));

					catPara.add(new Text(anyLabel + " " + itemCount + " " + itemLabel).setFont(catFont).setFontSize(16)
							.setFontColor(creamGold));
				}

				catPara.setTextAlignment(TextAlignment.CENTER);
				catPara.setMarginTop(3);

				Cell catCell = new Cell(1, 3).add(catPara);

				catCell.setBorder(Border.NO_BORDER);

				headerTable.addCell(catCell);

				document.add(headerTable);

				// =====================================================
				// Items
				// =====================================================

				Div itemsDiv = new Div();

				itemsDiv.setKeepTogether(false);

				for (Object[] item : items) {

					String itemName = item[2] != null ? item[2].toString() : "";

					itemsDiv.add(new Paragraph(itemName).setFont(itemFont).setFontSize(16).setFixedLeading(15f)
							.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0)
							.setMarginBottom(8));
				}

				document.add(itemsDiv);
			}

			// =========================================================
			// LAST PAGE
			// =========================================================

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			bgHandler.setPageBackground(lastPageNum, lastBgData);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Paragraph invisibleContent = new Paragraph("\u00A0").setFontSize(1);

			document.add(invisibleContent);

			document.close();

			// =========================================================
			// Return URL
			// =========================================================

			String fileUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + folderName + "/"
					+ fileName;

			return fileUrl;

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("Failed to generate Custom Package Report: " + e.getMessage(), e);
		}
	}

	public ImageData loadImageFromResource(String path) throws IOException {

		InputStream inputStream;

		// Case 1: Remote URL
		if (path.startsWith("http://") || path.startsWith("https://")) {
			URL url = new URL(path);
			inputStream = url.openStream();

			// Case 2: Classpath resource
		} else {
			inputStream = getClass().getResourceAsStream(path);

			// Try file system as fallback
			if (inputStream == null) {
				File file = new File(path);
				if (file.exists()) {
					inputStream = new FileInputStream(file);
				}
			}
		}

		if (inputStream == null) {
			throw new FileNotFoundException("Image not found: " + path);
		}

		try (InputStream is = inputStream) {
			return ImageDataFactory.create(IOUtils.toByteArray(is));
		}
	}

	@PostConstruct
	public void loadLicense() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("itextkey.json")) {

			if (is == null) {
				throw new RuntimeException("iText license not found");
			}

			LicenseKey.loadLicenseFile(is);
			System.out.println("iText license loaded");

		} catch (Exception e) {
			throw new RuntimeException("Failed to load iText license", e);
		}
	}

	// ===================== HELPER: ADD ITEM CELL =====================
	private void addItemCell(Table table, String text, PdfFont font, DeviceRgb textColor, DeviceRgb bgColor,
			TextAlignment alignment) {
		Cell cell = new Cell()
				.add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(10).setFontColor(textColor))
				.setPadding(5f)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(200, 200, 200), 0.5f))
				.setTextAlignment(alignment);
		if (bgColor != null) {
			cell.setBackgroundColor(bgColor);
		}
		table.addCell(cell);
	}

	private String nullSafe(String value) {
		return value != null ? value : "";
	}

}
