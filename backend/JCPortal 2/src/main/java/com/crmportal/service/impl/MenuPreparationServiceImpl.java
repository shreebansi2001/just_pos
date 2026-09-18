package com.crmportal.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.CatBgSelectionEntity;
import com.crmportal.entity.CustomPackageDetailsEntity;
import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.DecoreMainCategoryItemImagesMasterEntity;
import com.crmportal.entity.EventFunctionDecorItemImagesEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionRawmaterialPermissionEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventTermsAndConditionEntity;
import com.crmportal.entity.EventTermsAndConditionFeaturesEntity;
import com.crmportal.entity.FontMasterEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuPreparationDetailsEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.entity.MenuPreparationHistoryEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.ChangeStatus;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.EventFunctionMasterMapper;
import com.crmportal.mapper.MenuCategoryMasterMapper;
import com.crmportal.mapper.MenuPreparationDetailsMapper;
import com.crmportal.mapper.MenuPreparationMapper;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.CatBgSelectionRepository;
import com.crmportal.repository.CustomPackageDetailsRepository;
import com.crmportal.repository.CustomPackageRepository;
import com.crmportal.repository.DecoreMainCategoryItemImagesMasterRepository;
import com.crmportal.repository.EventFoodTestingDetailsRepository;
import com.crmportal.repository.EventFoodTestingRepository;
import com.crmportal.repository.EventFunctionDecorItemImagesRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventFunctionRawMaterialPermissionRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventTermsAndConditionFeaturesRepository;
import com.crmportal.repository.EventTermsAndConditionRepository;
import com.crmportal.repository.EvetFunctionMenuAllocationOrderRepository;
import com.crmportal.repository.FontMasterRepository;
import com.crmportal.repository.MenuAllocationItemCaptainReceipeRepository;
import com.crmportal.repository.MenuAllocationItemRawMaterialRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationHistoryRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.TermsAndConditionFeaturesRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserLogsEntityRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserTermsAndConditionRepository;
import com.crmportal.request.dto.MenuPreparationRequestDto;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.AiEventFunctionMenuResponseDto;
import com.crmportal.response.dto.AiItemsResponseDto;
import com.crmportal.response.dto.AiMenuCategoryResponseDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.CategoryImagePageInfo;
import com.crmportal.response.dto.DecoreItemReportResponseDto;
import com.crmportal.response.dto.DecoreReportResponseDto;
import com.crmportal.response.dto.EventAndFunctionWisePartyResponseDto;
import com.crmportal.response.dto.EventFlowReportResponseDto;
import com.crmportal.response.dto.EventFunctionFlowResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionMenuPreparationResponseDto;
import com.crmportal.response.dto.EventFunctionRawMaterialDto;
import com.crmportal.response.dto.EventFunctionRawMaterialPermissionResponseDto;
import com.crmportal.response.dto.EventFunctionReportResponseDto;
import com.crmportal.response.dto.EventReportResponseDto;
import com.crmportal.response.dto.ExtraChargesResponseDto;
import com.crmportal.response.dto.MenuCategoryForPreparationResponseDto;
import com.crmportal.response.dto.MenuCategoryMenuPreparationResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuItemPartyMasterResponseDto;
import com.crmportal.response.dto.MenuPreparationCombResponseDto;
import com.crmportal.response.dto.MenuPreparationDetailsResponseDto;
import com.crmportal.response.dto.MenuPreparationItemResponseDto;
import com.crmportal.response.dto.MenuPreparationResponseDto;
import com.crmportal.response.dto.MenuPreparationSelectedItemDetailsResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;
import com.crmportal.response.dto.OrderSummaryResponseDto;
import com.crmportal.response.dto.QuotationResponseDto;
import com.crmportal.response.dto.RemaingDataResponseDto;
import com.crmportal.response.dto.SavedMenuPreparationResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFunctionMasterService;
import com.crmportal.service.EventMasterService;
import com.crmportal.service.ExtraChargesService;
import com.crmportal.service.MenuCategoryMasterService;
import com.crmportal.service.MenuPreparationService;
import com.crmportal.service.UserFileService;
import com.crmportal.utility.AdobeDocxGenerator;
import com.crmportal.utility.BackgroundEventHandler;
import com.crmportal.utility.PageNumberHandler;
import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
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
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.layout.splitting.ISplitCharacters;
import com.itextpdf.licensing.base.LicenseKey;

@Service
public class MenuPreparationServiceImpl implements MenuPreparationService {

	@Autowired
	MenuPreparationRepository menuPreparationRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	MenuPreparationMapper menuPreparationMapper;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	MenuPreparationDetailsMapper menuPreparationDetailsMapper;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	EventFunctionMasterMapper eventFunctionMasterMapper;

	@Autowired
	Environment environment;

	@Autowired
	EventMasterService eventMasterService;

	@Autowired
	EventFunctionMasterService eventFunctionMasterService;

	@Autowired
	CustomPackageRepository customPackageRepository;

	@Autowired
	MenuCategoryMasterService menuCategoryMasterService;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	MenuCategoryMasterMapper menuCategoryMasterMapper;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	MenuAllocationItemRawMaterialRepository menuAllocationItemRawMaterRepository;

	@Autowired
	UserTermsAndConditionRepository userTermsRepository;

	@Autowired
	AdobeDocxGenerator adobeDocxGenerator;

	@Autowired
	EventFunctionRawMaterialPermissionRepository eventFunctionRawMaterialPermissionRepository;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Autowired
	MenuPreparationHelperService menuPreparationHelperService;

	@Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;

	@Autowired
	EvetFunctionMenuAllocationOrderRepository menuAllocationOrderRepository;

	@Autowired
	FontMasterRepository fontMasterRepository;

	@Autowired
	private ExtraChargesService extraChargesService;

	@Autowired
	private CatBgSelectionRepository catBgSelectionRepository;

	@Autowired
	private TermsAndConditionFeaturesRepository termsAndConditionFeaturesRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	private CustomPackageDetailsRepository customPackageDetailsRepository;

	@Autowired
	private EventFoodTestingRepository eventFoodTestingRepository;

	@Autowired
	private EventFoodTestingDetailsRepository eventFoodTestingDetailsRepository;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	@Autowired
	DecoreMainCategoryItemImagesMasterRepository decoreMainCategoryItemImagesMasterRepository;

	@Autowired
	EventTermsAndConditionRepository eventTermsAndConditionRepository;

	@Autowired
	EventTermsAndConditionFeaturesRepository eventTermsAndConditionFeaturesRepository;

	@Autowired
	MenuAllocationItemCaptainReceipeRepository menuAllocationItemCaptainReceipeRepository;

	@Autowired
	EventFunctionQuotationServiceImpl eventFunctionQuotationServiceImpl;

	@Autowired
	UserLogsEntityRepository userLogsEntityRepository;

	@Autowired
	UserFileService userFileService;

	@Autowired
	EventFunctionDecorItemImagesRepository eventFunctionDecorItemImagesRepository;

	private static final String LICENSE_FILE = "src/main/resources/itextkey.json";

	@Autowired
	MenuPreparationHistoryRepository menuPreparationHistoryRepository;

	/*
	 * private static com.itextpdf.text.Font ex_pagenumberFont = new
	 * com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 19, Font.BOLD,
	 * BaseColor.BLACK); private static com.itextpdf.text.Font ex_pagenumberFontINC
	 * = new com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 17, Font.BOLD,
	 * BaseColor.BLACK); private static com.itextpdf.text.Font ex_pagenumberFontINC1
	 * = new com.itextpdf.text.Font(FontFamily.TIMES_ROMAN, 13, Font.BOLD,
	 * BaseColor.BLACK);
	 */

	@Override
	@Transactional
	public MenuPreparationResponseDto addOrUpdateMenuPreparation(@Valid MenuPreparationRequestDto request) {

		// Validate Event Function
		EventFunctionMasterEntity eventFunctionMasterEntity = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(request.getEventFunctionId()).orElseThrow(() -> new RuntimeException(
						"Event Function not found with id: " + request.getEventFunctionId()));

		Map<String, List<Long>> rawMaterialMapId = menuPreparationHelperService.manageEventRawMaterial(
				eventFunctionMasterEntity.getEvent().getId(), eventFunctionMasterEntity.getId(), request);

		CustomPackageEntity customPackageEntity = null;

		// Find or create Menu Preparation
		MenuPreparationEntity menuPreparationEntity;
		if (request.getId() != null && request.getId() != 0) {
			menuPreparationEntity = menuPreparationRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Menu Preparation not found with id: " + request.getId()));
		} else {
			menuPreparationEntity = new MenuPreparationEntity();
			Integer maxSortOrder = menuPreparationRepository.findMaxSortOrder(eventFunctionMasterEntity.getId());
			menuPreparationEntity.setSortorder(maxSortOrder != null ? maxSortOrder + 1 : 1);
			menuPreparationEntity.setIsUpdate(true);
		}

		if (!request.getIsPackage()) {
			menuPreparationEntity.setCustomPackage(customPackageEntity);
			menuPreparationEntity.setPackageName("");
			menuPreparationEntity.setPackagePrice(BigDecimal.ZERO);
		} else {
			customPackageEntity = customPackageRepository.findByIdAndIsDeleteFalse(request.getPackageId());
			menuPreparationEntity.setCustomPackage(customPackageEntity);
			menuPreparationEntity.setPackageName(request.getPackageName());
			menuPreparationEntity.setPackagePrice(request.getPackagePrice());
		}

		// Set fields
		menuPreparationEntity.setIsPackage(request.getIsPackage());
		menuPreparationEntity.setEventFunction(eventFunctionMasterEntity);
		menuPreparationEntity.setPax(request.getPax());
		menuPreparationEntity.setPrice(request.getPrice());
		menuPreparationEntity.setDefaultPrice(request.getDefaultPrice());
		menuPreparationEntity.setSortorder(
				request.getSortorder() != null ? request.getSortorder() : menuPreparationEntity.getSortorder());
		// Save preparation
		try {
			menuPreparationEntity = menuPreparationRepository.save(menuPreparationEntity);
		} catch (DataIntegrityViolationException ex) {
			throw new RuntimeException("Menu Preparation already exists for this Event Function");
		}

		List<MenuPreparationSelectedItemDetailsResponseDto> detailsResponseDtos = new ArrayList<>();

		boolean isCompleted = eventFunctionMasterEntity.getEvent() != null
				&& "COMPLETED".equalsIgnoreCase(eventFunctionMasterEntity.getEvent().getMenuPreparationStatus());

		if (!isCompleted) {
			System.out.println("in not complete");
			menuPreparationDetailsRepository.deleteAllByMenuPreparation(menuPreparationEntity);

			detailsResponseDtos = saveNormalMenu(request, menuPreparationEntity);

		} else {
			System.out.println("in complete");
			detailsResponseDtos = saveCompletedMenuChanges(request, menuPreparationEntity);
		}

		if (eventFunctionMasterEntity.getEvent() != null) {
			EventMasterEntity eveEntity = eventMasterRepository
					.findByIdAndIsDeleteFalse(eventFunctionMasterEntity.getEvent().getId()).orElse(null);
			if (eveEntity != null) {
				eveEntity.setIsRMenu(false);
				eventMasterRepository.save(eveEntity);
			}
		}

		if (request.getPermissionRawMaterials() != null) {

			List<EventFunctionRawmaterialPermissionEntity> entities = new ArrayList<>();

			Long eventId = eventFunctionMasterEntity.getEvent().getId();
			Long eventFunctionId = eventFunctionMasterEntity.getId();
			Long userId = request.getPermissionRawMaterials().getUserId();

			eventFunctionRawMaterialPermissionRepository.deleteAllByEventIdAndEventFunctionIdAndType(eventId,
					eventFunctionId, "NotPermissable");

			eventFunctionRawMaterialPermissionRepository.deleteAllByEventIdAndEventFunctionIdAndType(eventId,
					eventFunctionId, "Permissable");

			Optional.ofNullable(request.getPermissionRawMaterials().getNotPermissables())
					.orElse(Collections.emptyList()).forEach(item -> entities.add(createPermissionEntity(eventId,
							eventFunctionId, item.getRawMaterialId(), "NotPermissable", userId)));

			Optional.ofNullable(request.getPermissionRawMaterials().getPermissables()).orElse(Collections.emptyList())
					.forEach(item -> entities.add(createPermissionEntity(eventId, eventFunctionId,
							item.getRawMaterialId(), "Permissable", userId)));

			if (!entities.isEmpty()) {
				eventFunctionRawMaterialPermissionRepository.saveAll(entities);
			}
		}

		// Build response
		MenuPreparationResponseDto responseDto = new MenuPreparationResponseDto();
		responseDto.setId(menuPreparationEntity.getId());
		responseDto.setEventFunction(null);
		responseDto.setPax(menuPreparationEntity.getPax());
		responseDto.setPrice(menuPreparationEntity.getPrice());
		responseDto.setDefaultPrice(menuPreparationEntity.getDefaultPrice());
		responseDto.setSortorder(menuPreparationEntity.getSortorder());
		responseDto.setIsPackage(menuPreparationEntity.getIsPackage());

		// Add null check before accessing custom package
		if (menuPreparationEntity.getCustomPackage() != null) {
			responseDto.setPackageId(menuPreparationEntity.getCustomPackage().getId());
		} else {
			responseDto.setPackageId(null);
		}

		responseDto.setPackageName(menuPreparationEntity.getPackageName());
		responseDto.setPackagePrice(menuPreparationEntity.getPackagePrice());
		responseDto.setSelectedMenuPreparation(detailsResponseDtos);

		removeMenuItems(rawMaterialMapId.get("delete"), eventFunctionMasterEntity.getEvent(),
				eventFunctionMasterEntity);

		menuPreparationHelperService.addDeleteEventRawMaterial(eventFunctionMasterEntity.getEvent().getId(),
				eventFunctionMasterEntity.getId(), rawMaterialMapId);

		return responseDto;
	}

	private EventFunctionRawmaterialPermissionEntity createPermissionEntity(Long eventId, Long eventFunctionId,
			Long rawMaterialId, String type, Long userId) {

		EventFunctionRawmaterialPermissionEntity entity = new EventFunctionRawmaterialPermissionEntity();
		entity.setEventId(eventId);
		entity.setEventFunctionId(eventFunctionId);
		entity.setRawMaterialId(rawMaterialId);
		entity.setType(type);
		entity.setUserId(userId);
		return entity;
	}

	private List<MenuPreparationSelectedItemDetailsResponseDto> saveCompletedMenuChanges(
			MenuPreparationRequestDto request, MenuPreparationEntity menuPreparationEntity) {

		List<MenuPreparationDetailsEntity> existing = menuPreparationDetailsRepository
				.findAllByMenuPreparation(menuPreparationEntity);

		Map<String, MenuPreparationDetailsEntity> existingMap = existing.stream().collect(Collectors.toMap(
				x -> x.getMenuCategory().getId() + "_" + x.getMenuItem().getId(), Function.identity(), (a, b) -> a));

		Set<String> requestKeys = new HashSet<String>();
		Set<Long> requestCategoryIds = new HashSet<Long>();

		if (request.getSelectedMenuPreparation() != null && !request.getSelectedMenuPreparation().isEmpty()) {

			for (MenuPreparationSelectedItemDetailsResponseDto categoryDto : request.getSelectedMenuPreparation()) {

				requestCategoryIds.add(categoryDto.getMenuCategoryId());

				MenuCategoryMasterEntity category = menuCategoryMasterRepository
						.findByIdAndIsDeleteFalse(categoryDto.getMenuCategoryId())
						.orElseThrow(() -> new RuntimeException("Category not found"));

				boolean isNewCategory = existing.stream()
						.noneMatch(x -> x.getMenuCategory().getId().equals(category.getId()));

				if (categoryDto.getSelectedMenuPreparationItems() != null) {

					for (MenuPreparationDetailsResponseDto itemDto : categoryDto.getSelectedMenuPreparationItems()) {

						MenuItemMasterEntity item = menuItemMasterRepository
								.findByIdAndIsDeleteFalse(itemDto.getMenuItemId())
								.orElseThrow(() -> new RuntimeException("Item not found"));

						String key = category.getId() + "_" + item.getId();

						requestKeys.add(key);

						if (existingMap.containsKey(key)) {

							MenuPreparationDetailsEntity entity = existingMap.get(key);
							entity = buildUpdateEntity(entity, categoryDto, itemDto, menuPreparationEntity, category,
									item);
							menuPreparationDetailsRepository.save(entity);

						} else {

							MenuPreparationDetailsEntity entity = buildEntity(categoryDto, itemDto,
									menuPreparationEntity, category, item);

							entity.setItemStatus(ChangeStatus.ADDED);
							entity.setCategoryStatus(isNewCategory ? ChangeStatus.ADDED : ChangeStatus.NORMAL);

							entity.setChangedAfterCompletion(true);

							menuPreparationDetailsRepository.save(entity);
						}
					}
				}
			}
		}

		List<MenuPreparationDetailsEntity> toDelete = new ArrayList<>();

		for (MenuPreparationDetailsEntity old : existing) {

			String key = old.getMenuCategory().getId() + "_" + old.getMenuItem().getId();

			boolean categoryRemoved = !requestCategoryIds.contains(old.getMenuCategory().getId());

			boolean itemRemoved = !requestKeys.contains(key);

			if (categoryRemoved || itemRemoved) {

				MenuPreparationHistoryEntity history = buildHistoryEntity(old, categoryRemoved);

				menuPreparationHistoryRepository.save(history);

				toDelete.add(old);
			}
		}

		if (!toDelete.isEmpty()) {
			menuPreparationDetailsRepository.deleteAll(toDelete);
		}

		return loadSelectedItems(menuPreparationEntity.getEventFunction().getId(), menuPreparationEntity,
				menuPreparationEntity.getEventFunction());
	}

	private MenuPreparationDetailsEntity buildUpdateEntity(MenuPreparationDetailsEntity entity,
			MenuPreparationSelectedItemDetailsResponseDto categoryDto, MenuPreparationDetailsResponseDto itemDto,
			MenuPreparationEntity menuPreparationEntity, MenuCategoryMasterEntity category, MenuItemMasterEntity item) {
		entity.setMenuPreparation(menuPreparationEntity);
		entity.setMenuCategory(category);
		entity.setMenuItem(item);

		entity.setStartTime(categoryDto.getStartTime());

		entity.setMenuItemName(itemDto.getMenuItemName());
		entity.setMenuItemNameHindi(itemDto.getMenuItemNameHindi());
		entity.setMenuItemNameGujarati(itemDto.getMenuItemNameGujarati());

		entity.setItemSortOrder(itemDto.getItemSortOrder());

		entity.setItemPrice(itemDto.getItemPrice() == null ? BigDecimal.ZERO : itemDto.getItemPrice());

		entity.setItemNotes(itemDto.getItemNotes());
		entity.setItemNotesHindi(itemDto.getItemNotesHindi());
		entity.setItemNotesGujarati(itemDto.getItemNotesGujarati());

		entity.setItemSlogan(itemDto.getItemSlogan());

		entity.setIsItemAddons(itemDto.getIsItemAddons());

		entity.setCategoryPrice(categoryDto.getCategoryPrice());

		entity.setAnyItem(categoryDto.getAnyItem());

		entity.setMenuCategoryName(categoryDto.getMenuCategoryName());
		entity.setMenuCategoryNameHindi(categoryDto.getMenuCategoryNameHindi());
		entity.setMenuCategoryNameGujarati(categoryDto.getMenuCategoryNameGujarati());

		entity.setMenuSortOrder(categoryDto.getMenuSortOrder());

		entity.setMenuNotes(categoryDto.getMenuNotes());
		entity.setMenuNotesHindi(categoryDto.getMenuNotesHindi());
		entity.setMenuNotesGujarati(categoryDto.getMenuNotesGujarati());

		entity.setMenuSlogan(categoryDto.getMenuSlogan());

		entity.setIsMenuCatAddons(categoryDto.getIsMenuCatAddons());

		entity.setCatImgId(categoryDto.getCatImgId());
		entity.setBgImgId(categoryDto.getBgImgId());

		entity.setCatSpace(categoryDto.getCatSpace());

		entity.setSubCat(categoryDto.getSubCat());
		entity.setSubCatHindi(categoryDto.getSubCatHindi());
		entity.setSubCatGujarati(categoryDto.getSubCatGujarati());

		entity.setItemSpace(itemDto.getItemSpace());

		entity.setSubItem(itemDto.getSubItem());
		entity.setSubItemHindi(itemDto.getSubItemHindi());
		entity.setSubItemGujarati(itemDto.getSubItemGujarati());

		entity.setCatNickNameEnglish(categoryDto.getCatNickNameEnglish());
		entity.setCatNickNameHindi(categoryDto.getCatNickNameHindi());
		entity.setCatNickNameGujarati(categoryDto.getCatNickNameGujarati());

		entity.setItemNickNameEnglish(itemDto.getItemNickNameEnglish());
		entity.setItemNickNameHindi(itemDto.getItemNickNameHindi());
		entity.setItemNickNameGujarati(itemDto.getItemNickNameGujarati());

		entity.setIsCatImage(itemDto.getIsCatImage());
		entity.setItemStatus(ChangeStatus.valueOf(itemDto.getItemStatus()));
		entity.setCategoryStatus(ChangeStatus.valueOf(categoryDto.getCategoryStatus()));
		entity.setChangedAfterCompletion(itemDto.getChangedAfterCompletion());

		entity.setItemHeading(itemDto.getItemHeading());
		entity.setItemHeadingHindi(itemDto.getItemHeadingHindi());
		entity.setItemHeadingGujarati(itemDto.getItemHeadingGujarati());

		entity.setCatHeadingEnglish(categoryDto.getCatHeadingEnglish());
		entity.setCatHeadingHindi(categoryDto.getCatHeadingHindi());
		entity.setCatHeadingGujarati(categoryDto.getCatHeadingGujarati());

		return entity;
	}

	private MenuPreparationHistoryEntity buildHistoryEntity(MenuPreparationDetailsEntity detailsEntity,
			boolean categoryRemoved) {

		MenuPreparationHistoryEntity history = new MenuPreparationHistoryEntity();

		BeanUtils.copyProperties(detailsEntity, history);

		history.setId(null); // create new history record

		history.setMenuPreparation(detailsEntity.getMenuPreparation());
		history.setMenuCategory(detailsEntity.getMenuCategory());
		history.setMenuItem(detailsEntity.getMenuItem());

		history.setItemStatus(ChangeStatus.CANCELLED);

		history.setCategoryStatus(categoryRemoved ? ChangeStatus.CANCELLED : detailsEntity.getCategoryStatus());

		history.setChangedAfterCompletion(true);

		history.setItemHeading(detailsEntity.getItemHeading());
		history.setItemHeadingHindi(detailsEntity.getItemHeadingHindi());
		history.setItemHeadingGujarati(detailsEntity.getItemHeadingGujarati());

		history.setCatHeadingEnglish(detailsEntity.getCatHeadingEnglish());
		history.setCatHeadingHindi(detailsEntity.getCatHeadingHindi());
		history.setCatHeadingGujarati(detailsEntity.getCatHeadingGujarati());

		return history;
	}

	private MenuPreparationDetailsEntity buildEntity(MenuPreparationSelectedItemDetailsResponseDto categoryDto,
			MenuPreparationDetailsResponseDto itemDto, MenuPreparationEntity menuPreparationEntity,
			MenuCategoryMasterEntity category, MenuItemMasterEntity item) {

		MenuPreparationDetailsEntity entity = new MenuPreparationDetailsEntity();

		entity.setMenuPreparation(menuPreparationEntity);
		entity.setMenuCategory(category);
		entity.setMenuItem(item);

		entity.setStartTime(categoryDto.getStartTime());

		entity.setMenuItemName(itemDto.getMenuItemName());
		entity.setMenuItemNameHindi(itemDto.getMenuItemNameHindi());
		entity.setMenuItemNameGujarati(itemDto.getMenuItemNameGujarati());

		entity.setItemSortOrder(itemDto.getItemSortOrder());

		entity.setItemPrice(itemDto.getItemPrice() == null ? BigDecimal.ZERO : itemDto.getItemPrice());

		entity.setItemNotes(itemDto.getItemNotes());
		entity.setItemNotesHindi(itemDto.getItemNotesHindi());
		entity.setItemNotesGujarati(itemDto.getItemNotesGujarati());

		entity.setItemSlogan(itemDto.getItemSlogan());

		entity.setIsItemAddons(itemDto.getIsItemAddons());

		entity.setCategoryPrice(categoryDto.getCategoryPrice());

		entity.setAnyItem(categoryDto.getAnyItem());

		entity.setMenuCategoryName(categoryDto.getMenuCategoryName());
		entity.setMenuCategoryNameHindi(categoryDto.getMenuCategoryNameHindi());
		entity.setMenuCategoryNameGujarati(categoryDto.getMenuCategoryNameGujarati());

		entity.setMenuSortOrder(categoryDto.getMenuSortOrder());

		entity.setMenuNotes(categoryDto.getMenuNotes());
		entity.setMenuNotesHindi(categoryDto.getMenuNotesHindi());
		entity.setMenuNotesGujarati(categoryDto.getMenuNotesGujarati());

		entity.setMenuSlogan(categoryDto.getMenuSlogan());

		entity.setIsMenuCatAddons(categoryDto.getIsMenuCatAddons());

		entity.setCatImgId(categoryDto.getCatImgId());
		entity.setBgImgId(categoryDto.getBgImgId());

		entity.setCatSpace(categoryDto.getCatSpace());

		entity.setSubCat(categoryDto.getSubCat());
		entity.setSubCatHindi(categoryDto.getSubCatHindi());
		entity.setSubCatGujarati(categoryDto.getSubCatGujarati());

		entity.setItemSpace(itemDto.getItemSpace());

		entity.setSubItem(itemDto.getSubItem());
		entity.setSubItemHindi(itemDto.getSubItemHindi());
		entity.setSubItemGujarati(itemDto.getSubItemGujarati());

		entity.setCatNickNameEnglish(categoryDto.getCatNickNameEnglish());
		entity.setCatNickNameHindi(categoryDto.getCatNickNameHindi());
		entity.setCatNickNameGujarati(categoryDto.getCatNickNameGujarati());

		entity.setItemNickNameEnglish(itemDto.getItemNickNameEnglish());
		entity.setItemNickNameHindi(itemDto.getItemNickNameHindi());
		entity.setItemNickNameGujarati(itemDto.getItemNickNameGujarati());

		entity.setItemHeading(itemDto.getItemHeading());
		entity.setItemHeadingHindi(itemDto.getItemHeadingHindi());
		entity.setItemHeadingGujarati(itemDto.getItemHeadingGujarati());
		
		entity.setCatHeadingEnglish(categoryDto.getCatHeadingEnglish());
		entity.setCatHeadingHindi(categoryDto.getCatHeadingHindi());
		entity.setCatHeadingGujarati(categoryDto.getCatHeadingGujarati());

		entity.setIsCatImage(itemDto.getIsCatImage());

		return entity;
	}

	private List<MenuPreparationSelectedItemDetailsResponseDto> saveNormalMenu(MenuPreparationRequestDto request,
			MenuPreparationEntity menuPreparationEntity) {

		List<MenuPreparationSelectedItemDetailsResponseDto> detailsResponseDtos = new ArrayList<>();

		if (request.getSelectedMenuPreparation() != null && !request.getSelectedMenuPreparation().isEmpty()) {

			for (MenuPreparationSelectedItemDetailsResponseDto categoryDto : request.getSelectedMenuPreparation()) {

				MenuCategoryMasterEntity category = menuCategoryMasterRepository
						.findByIdAndIsDeleteFalse(categoryDto.getMenuCategoryId())
						.orElseThrow(() -> new RuntimeException("Category not found"));

				MenuPreparationSelectedItemDetailsResponseDto categoryResponse = new MenuPreparationSelectedItemDetailsResponseDto();

				BeanUtils.copyProperties(categoryDto, categoryResponse);

				List<MenuPreparationDetailsResponseDto> itemResponses = new ArrayList<>();

				if (categoryDto.getSelectedMenuPreparationItems() != null) {

					for (MenuPreparationDetailsResponseDto itemDto : categoryDto.getSelectedMenuPreparationItems()) {

						MenuItemMasterEntity item = menuItemMasterRepository
								.findByIdAndIsDeleteFalse(itemDto.getMenuItemId())
								.orElseThrow(() -> new RuntimeException("Item not found"));

						MenuPreparationDetailsEntity detailsEntity = buildEntity(categoryDto, itemDto,
								menuPreparationEntity, category, item);
						detailsEntity.setItemPrice(itemDto.getItemPrice());
						detailsEntity.setItemStatus(ChangeStatus.valueOf(itemDto.getItemStatus()));
						detailsEntity.setCategoryStatus(ChangeStatus.valueOf(categoryDto.getCategoryStatus()));
						detailsEntity.setChangedAfterCompletion(itemDto.getChangedAfterCompletion());
						detailsEntity.setIsItemAddons(itemDto.getIsItemAddons());
						detailsEntity.setIsMenuCatAddons(categoryDto.getIsMenuCatAddons());
						detailsEntity = menuPreparationDetailsRepository.save(detailsEntity);
						itemResponses.add(menuPreparationDetailsMapper.entityToResponse(detailsEntity));
					}
				}

				categoryResponse.setSelectedMenuPreparationItems(itemResponses);

				detailsResponseDtos.add(categoryResponse);
			}
		}

		return detailsResponseDtos;
	}

	@Override
	@Transactional
	public MenuPreparationCombResponseDto getMenuPreparationItems(Integer pageNo, Integer totalRecord,
			Long menuCategoryId, Long eventFunctionId, String itemName, Long userId) {

		EventFunctionMasterEntity eventFunction = eventFunctionMasterRepository.findById(eventFunctionId)
				.orElseThrow(() -> new RuntimeException("Event Function not found with id: " + eventFunctionId));

		EventFunctionMasterResponseDto eventFunctionDto = eventFunctionMasterMapper.entityToResponse(eventFunction);
		MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
				.findByEventFunctionAndIsDeleteFalse(eventFunction);

		// Load menu categories
//	    List<MenuCategoryMenuPreparationResponseDto> menuCategoryDtos = loadMenuCategories(userId);

		// Load items & paginate
		List<MenuPreparationItemResponseDto> allItems = convertToItemDtoList(menuPreparationRepository
				.getAllMenuPreparationItemsNative(eventFunctionId, userId, itemName, menuCategoryId));

//		// ── Filter by custom package if selected on this event function ───────
//		if (eventFunction.getCustomPackage() != null) {
//		    try {
//		        Long packageId = eventFunction.getCustomPackage().getId();
//
//		        System.out.println("Package ID: " + packageId);
//
//		        List<Long> packageMenuItemIds =
//		                customPackageDetailsRepository.findMenuItemIdsByPackageId(packageId);
//
//		        List<Long> anyItemCategoryIds =
//		                customPackageDetailsRepository.findAnyItemCategoryIdsByPackageId(packageId);
//
//		        System.out.println("Package menu item ids: " + packageMenuItemIds);
//		        System.out.println("AnyItem category ids: " + anyItemCategoryIds);
//		        System.out.println("Total items before filter: " + allItems.size());
//
//		        if (!packageMenuItemIds.isEmpty() || !anyItemCategoryIds.isEmpty()) {
//		            allItems = allItems.stream()
//		                    .filter(item ->
//		                        packageMenuItemIds.contains(item.getMenuItemId())
//		                        ||
//		                        anyItemCategoryIds.contains(item.getMenuCategoryId())
//		                    )
//		                    .collect(Collectors.toList());
//		        }
//
//		        System.out.println("Total items after filter: " + allItems.size());
//
//		    } catch (Exception e) {
//		        System.out.println("Package item filter failed: " + e.getMessage());
//		        e.printStackTrace();
//		    }
//		}
		// ─────────────────────────────────────────────────────────────────────

		// NOW paginate the filtered list
		PaginationResult<MenuPreparationItemResponseDto> pagination = paginateItems(allItems, pageNo, totalRecord);

		// Selected items for event
		List<MenuPreparationSelectedItemDetailsResponseDto> selectedItems = loadSelectedItems(eventFunctionId,
				menuPreparationEntity, eventFunction);

		EventFunctionMenuPreparationResponseDto eventFunctionMenuPreparationResponseDto = new EventFunctionMenuPreparationResponseDto();

		eventFunctionMenuPreparationResponseDto.setEventId(eventFunction.getEvent().getId());
		eventFunctionMenuPreparationResponseDto.setFuncId(eventFunctionDto.getFunction().getId());
		eventFunctionMenuPreparationResponseDto.setFunction_venue(eventFunctionDto.getFunction_venue());
		eventFunctionMenuPreparationResponseDto.setFunctionStartDateTime(eventFunctionDto.getFunctionStartDateTime());
		eventFunctionMenuPreparationResponseDto.setFunctionEndDateTime(eventFunctionDto.getFunctionEndDateTime());
		eventFunctionMenuPreparationResponseDto.setId(eventFunctionDto.getId());
		eventFunctionMenuPreparationResponseDto.setNameEnglish(eventFunctionDto.getFunction().getNameEnglish());
		eventFunctionMenuPreparationResponseDto.setNameHindi(eventFunctionDto.getFunction().getNameHindi());
		eventFunctionMenuPreparationResponseDto.setNameGujarati(eventFunctionDto.getFunction().getNameGujarati());
		eventFunctionMenuPreparationResponseDto.setNotesEnglish(eventFunctionDto.getNotesEnglish());
		eventFunctionMenuPreparationResponseDto.setNotesHindi(eventFunctionDto.getNotesHindi());
		eventFunctionMenuPreparationResponseDto.setNotesGujarati(eventFunctionDto.getNotesGujarati());
		eventFunctionMenuPreparationResponseDto.setPax(eventFunctionDto.getPax());
		eventFunctionMenuPreparationResponseDto.setRate(eventFunctionDto.getRate());

		// ── Custom Package in response ────────────────────────────────────────
		if (eventFunction.getCustomPackage() != null) {
			eventFunctionMenuPreparationResponseDto.setCustomPackageId(eventFunction.getCustomPackage().getId());
			eventFunctionMenuPreparationResponseDto
					.setCustomPackageName(eventFunction.getCustomPackage().getNameEnglish()); // ← nameEnglish
			eventFunctionMenuPreparationResponseDto.setIsPackage(true);
		} else {
			eventFunctionMenuPreparationResponseDto.setCustomPackageId(null);
			eventFunctionMenuPreparationResponseDto.setCustomPackageName(null);
			eventFunctionMenuPreparationResponseDto.setIsPackage(false);
		}

		// Prepare Menu Preparation DTO
		MenuPreparationResponseDto menuPrepResponse = buildMenuPreparationResponse(
				eventFunctionMenuPreparationResponseDto, menuPreparationEntity, allItems.size(), pagination.totalPage);

		// Final Response
		MenuPreparationCombResponseDto responseDto = new MenuPreparationCombResponseDto();
		responseDto.setMenuPreparationItems(pagination.paginatedList);
		responseDto.setSelectedMenuPreparationItems(selectedItems);
		responseDto.setMenuPreparation(menuPrepResponse);
		responseDto.setTotalPage(pagination.totalPage);
		responseDto.setTotalItems(pagination.totalItems);
		responseDto.setCurrentPage(pagination.currentPage);
		// ── Add package details to response ──────────────────────────────────
//		if (eventFunction.getCustomPackage() != null) {
//
//		    try {
//
//		        List<CustomPackageDetailsEntity> pkgDetails =
//		                customPackageDetailsRepository.findByPackageId(
//		                        eventFunction.getCustomPackage().getId());
//
//		        Map<Long, MenuPreparationSelectedItemDetailsResponseDto> categoryMap =
//		                new LinkedHashMap<>();
//
//		        for (CustomPackageDetailsEntity cd : pkgDetails) {
//
//		            Long categoryId =
//		                    cd.getMenuCategory() != null
//		                            ? cd.getMenuCategory().getId()
//		                            : 0L;
//
//		            MenuPreparationSelectedItemDetailsResponseDto categoryDto =
//		                    categoryMap.computeIfAbsent(categoryId, k -> {
//
//		                        MenuPreparationSelectedItemDetailsResponseDto dto =
//		                                new MenuPreparationSelectedItemDetailsResponseDto();
//
//		                        if (cd.getMenuCategory() != null) {
//
//		                            dto.setMenuCategoryId(
//		                                    cd.getMenuCategory().getId());
//
//		                            dto.setMenuCategoryName(
//		                                    cd.getMenuCategory().getNameEnglish());
//
//		                            dto.setMenuCategoryNameHindi(
//		                                    cd.getMenuCategory().getNameHindi());
//
//		                            dto.setMenuCategoryNameGujarati(
//		                                    cd.getMenuCategory().getNameGujarati());
//		                        }
//
//		                        dto.setMenuSortOrder(
//		                                cd.getMenuSortOrder());
//
//		                        dto.setMenuNotes(
//		                                cd.getMenuInstruction());
//
//		                        dto.setAnyItem(
//		                                cd.getAnyItem());
//
//		                        dto.setSelectedMenuPreparationItems(
//		                                new ArrayList<>());
//
//		                        return dto;
//		                    });
//
//		            if (cd.getMenuItem() != null) {
//
//		                MenuPreparationDetailsResponseDto itemDto =
//		                        new MenuPreparationDetailsResponseDto();
//
//		                itemDto.setId(cd.getId());
//
//		                itemDto.setMenuItemId(
//		                        cd.getMenuItem().getId());
//
//		                itemDto.setMenuItemName(
//		                        cd.getMenuItem().getNameEnglish());
//
//		                itemDto.setMenuItemNameHindi(
//		                        cd.getMenuItem().getNameHindi());
//
//		                itemDto.setMenuItemNameGujarati(
//		                        cd.getMenuItem().getNameGujarati());
//
//		                itemDto.setItemSortOrder(
//		                        cd.getItemSortOrder());
//
//		                itemDto.setItemNotes(
//		                        cd.getItemInstruction());
//
//		                itemDto.setItemPrice(
//		                        cd.getItemPrice());
//
//		                categoryDto.getSelectedMenuPreparationItems()
//		                           .add(itemDto);
//		            }
//		        }
//
//		        responseDto.setSelectedMenuPreparationItems(
//		                new ArrayList<>(categoryMap.values()));
//
//		    } catch (Exception e) {
//
//		        System.out.println(
//		                "Package details fetch failed : "
//		                        + e.getMessage());
//		    }
//		}
		// ─────────────────────────────────────────────────────────────────────

		return responseDto;
		// ─────────────────────────────────────────────────────────────────────

	}

	public MenuQuantityReponseDto getEventData(Long eventId, int lang) {

		Object result = menuItemRawMaterialRepository.getEventData(eventId, lang);
		if (result == null) {
			return null;
		}

		Object[] cmpData = (Object[]) result;
		int index = 0;
		MenuQuantityReponseDto dto = new MenuQuantityReponseDto();

		dto.setEventId(commonService.getLong(cmpData[index++]));
		dto.setCompanyName(commonService.getString(cmpData[index++]));
		dto.setCountryCode(commonService.getString(cmpData[index++]));
		dto.setOfficeNo(commonService.getString(cmpData[index++]));
		dto.setCompanyEmail(commonService.getString(cmpData[index++]));
		dto.setLogo(commonService.getString(cmpData[index++]));
		dto.setPartyName(commonService.getString(cmpData[index++]));
		dto.setPartyMobile(commonService.getString(cmpData[index++]));
		dto.setEventName(commonService.getString(cmpData[index++]));
		dto.setEventDate(commonService.getString(cmpData[index++]));
		dto.setVenueId(commonService.getLong(cmpData[index++]));
		dto.setVenueName(commonService.getString(cmpData[index++]));
		dto.setEventNo(commonService.getString(cmpData[index++]));
		dto.setCompanyAddress(commonService.getString(cmpData[index++]));

		return dto;
	}

	private List<MenuCategoryMenuPreparationResponseDto> loadMenuCategories(Long userId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);

		return menuCategoryMasterRepository.findAllByUserAndIsDeleteFalseAndIsActive(userOpt.get(), true).stream()
				.map(entity -> {
					MenuCategoryMenuPreparationResponseDto dto = menuCategoryMasterMapper.entityToResponses(entity);
					dto.setImagePath(entity.getImagePath() != null
							? environment.getProperty("app.image.url") + entity.getImagePath()
							: "");

					if (entity.getCreatedAt() != null) {
						dto.setCreatedAt(entity.getCreatedAt().format(formatter));
					}
					return dto;
				}).collect(Collectors.toList());
	}

	private List<MenuPreparationItemResponseDto> convertToItemDtoList(List<Object[]> rows) {
		String baseImageUrl = environment.getProperty("app.image.url", "");

		return rows.stream().map(row -> {
			MenuPreparationItemResponseDto dto = new MenuPreparationItemResponseDto();
			dto.setMenuItemName((String) row[0]);
			dto.setMenuCategoryName((String) row[1]);
			dto.setItemPrice((BigDecimal) row[2]);
			dto.setItemSlogan((String) row[3]);
			dto.setCategorySlogan((String) row[4]);
			dto.setMenuCategoryId(((Number) row[5]).longValue());
			dto.setMenuItemId(((Number) row[6]).longValue());
			dto.setImagePath(baseImageUrl + (String) row[7]);
			dto.setIsSelected(((Number) row[8]).intValue() == 1);
			dto.setMenuCategoryNameHindi((String) row[11]);
			dto.setMenuCategoryNameGujarati((String) row[12]);
			dto.setMenuItemNameHindi((String) row[13]);
			dto.setMenuItemNameGujarati((String) row[14]);
			dto.setInstructionEnglish((String) row[15]);
			dto.setInstructionGujarati((String) row[16]);
			dto.setInstructionHindi((String) row[17]);
			dto.setCategoryPrice((BigDecimal) row[18]);
			dto.setReportNameEnglish((String) row[19]);
			dto.setReportNameHindi((String) row[20]);
			dto.setReportNameGujarati((String) row[21]);
			dto.setUrl(baseImageUrl + (row[22]));
			return dto;
		}).collect(Collectors.toList());
	}

	private static class PaginationResult<T> {
		List<T> paginatedList;
		Integer totalPage;
		Integer totalItems;
		Integer currentPage;
	}

	private <T> PaginationResult<T> paginateItems(List<T> items, int pageNo, int totalRecord) {

		PaginationResult<T> result = new PaginationResult<>();

		int totalItem = items.size();

		result.totalItems = totalItem;
		result.currentPage = pageNo;
		result.totalPage = (int) Math.ceil((double) totalItem / totalRecord);

		int fromIndex = Math.max(0, (pageNo - 1) * totalRecord);
		int toIndex = Math.min(fromIndex + totalRecord, totalItem);

		result.paginatedList = fromIndex < totalItem ? items.subList(fromIndex, toIndex) : Collections.emptyList();

		return result;
	}

	public List<MenuPreparationSelectedItemDetailsResponseDto> loadSelectedItems(Long eventFunctionId,
			MenuPreparationEntity menuPreparationEntity, EventFunctionMasterEntity eventFunction) {

		List<MenuCategoryForPreparationResponseDto> menuCategoryList = menuPreparationDetailsRepository
				.findAllByEventFunctionIdAndIsDeleteFalse(eventFunctionId);

		if ((menuCategoryList == null || menuCategoryList.isEmpty()) && eventFunction.getCustomPackage() != null) {

			return loadPackageSelectedItems(eventFunction.getCustomPackage().getId());

		}
//		else if ((menuCategoryList != null && !menuCategoryList.isEmpty()) && eventFunction.getCustomPackage() != null
//				&& !Objects.equals(eventFunction.getCustomPackage().getId(),
//						menuPreparationEntity.getCustomPackage().getId())) {
//			System.out.println("in menu preparation update package");
//			return loadPackageSelectedItems(eventFunction.getCustomPackage().getId());
//		}

		List<MenuPreparationHistoryEntity> historyEntities = menuPreparationHistoryRepository
				.findAllByMenuPreparation(menuPreparationEntity);

		// Add cancelled categories missing from active table
		Set<Long> activeCategoryIds = menuCategoryList.stream()
				.map(MenuCategoryForPreparationResponseDto::getMenuCategoryId).collect(Collectors.toSet());

		historyEntities.stream().filter(x -> !activeCategoryIds.contains(x.getMenuCategory().getId())).forEach(x -> {

			MenuCategoryForPreparationResponseDto dto = new MenuCategoryForPreparationResponseDto();

			dto.setMenuCategoryId(x.getMenuCategory().getId());
			dto.setMenuCategoryName(x.getMenuCategoryName());
			dto.setMenuCategoryNameHindi(x.getMenuCategoryNameHindi());
			dto.setMenuCategoryNameGujarati(x.getMenuCategoryNameGujarati());

			dto.setMenuNotes(x.getMenuNotes());
			dto.setMenuNotesHindi(x.getMenuNotesHindi());
			dto.setMenuNotesGujarati(x.getMenuNotesGujarati());

			dto.setMenuSlogan(x.getMenuSlogan());
			dto.setMenuSortOrder(x.getMenuSortOrder());
			dto.setStartTime(x.getStartTime());

			dto.setIsMenuCatAddons(x.getIsMenuCatAddons());
			dto.setCatImgId(x.getCatImgId());
			dto.setBgImgId(x.getBgImgId());
			dto.setCatSpace(x.getCatSpace());

			dto.setSubCat(x.getSubCat());
			dto.setSubCatHindi(x.getSubCatHindi());
			dto.setSubCatGujarati(x.getSubCatGujarati());

			dto.setAnyItem(x.getAnyItem());

			dto.setCatNickNameEnglish(x.getCatNickNameEnglish());
			dto.setCatNickNameHindi(x.getCatNickNameHindi());
			dto.setCatNickNameGujarati(x.getCatNickNameGujarati());

			dto.setCatHeadingEnglish(x.getCatHeadingEnglish());
			dto.setCatHeadingHindi(x.getCatHeadingHindi());
			dto.setCatHeadingGujarati(x.getCatHeadingGujarati());

			menuCategoryList.add(dto);
		});

		List<MenuPreparationSelectedItemDetailsResponseDto> result = new ArrayList<>();

		for (MenuCategoryForPreparationResponseDto categoryResp : menuCategoryList) {

			menuCategoryMasterRepository.findById(categoryResp.getMenuCategoryId()).ifPresent(category -> {

				List<MenuPreparationDetailsEntity> activeItems = menuPreparationDetailsRepository
						.findAllByMenuCategoryAndMenuPreparationOrderByMenuSortOrderAscItemSortOrderAsc(category,
								menuPreparationEntity);

				List<MenuPreparationHistoryEntity> cancelledItems = historyEntities.stream()
						.filter(x -> x.getMenuCategory().getId().equals(category.getId())).collect(Collectors.toList());

				List<MenuPreparationDetailsResponseDto> detailsDtos = new ArrayList<>();

				// NORMAL + ADDED
				for (MenuPreparationDetailsEntity detail : activeItems) {

					MenuPreparationDetailsResponseDto dto = menuPreparationDetailsMapper.entityToResponse(detail);
					dto.setMenuItemId(detail.getMenuItem().getId());
					dto.setChangedAfterCompletion(detail.getChangedAfterCompletion());
					dto.setItemStatus(detail.getItemStatus() == null ? "NORMAL" : detail.getItemStatus().name());
					dto.setItemHeading(detail.getItemHeading());
					dto.setItemHeadingHindi(detail.getItemHeadingHindi());
					dto.setItemHeadingGujarati(detail.getItemHeadingGujarati());
					detailsDtos.add(dto);
				}

				// CANCELLED
				for (MenuPreparationHistoryEntity history : cancelledItems) {

					MenuPreparationDetailsResponseDto dto = new MenuPreparationDetailsResponseDto();

					BeanUtils.copyProperties(history, dto);
					dto.setMenuItemId(history.getMenuItem().getId());

					dto.setItemStatus(ChangeStatus.CANCELLED.name());
					dto.setItemHeading(history.getItemHeading());
					dto.setItemHeadingHindi(history.getItemHeadingHindi());
					dto.setItemHeadingGujarati(history.getItemHeadingGujarati());
					detailsDtos.add(dto);
				}

				MenuPreparationSelectedItemDetailsResponseDto selectedDto = new MenuPreparationSelectedItemDetailsResponseDto();

				selectedDto.setMenuCategoryId(categoryResp.getMenuCategoryId());

				selectedDto.setMenuCategoryName(categoryResp.getMenuCategoryName());

				selectedDto.setMenuCategoryNameHindi(categoryResp.getMenuCategoryNameHindi());

				selectedDto.setMenuCategoryNameGujarati(categoryResp.getMenuCategoryNameGujarati());

				selectedDto.setMenuNotes(categoryResp.getMenuNotes());

				selectedDto.setMenuNotesHindi(categoryResp.getMenuNotesHindi());

				selectedDto.setMenuNotesGujarati(categoryResp.getMenuNotesGujarati());

				selectedDto.setMenuSlogan(categoryResp.getMenuSlogan());

				selectedDto.setMenuSortOrder(categoryResp.getMenuSortOrder());

				selectedDto.setStartTime(categoryResp.getStartTime());

				selectedDto.setSelectedMenuPreparationItems(detailsDtos);

				selectedDto.setIsMenuCatAddons(categoryResp.getIsMenuCatAddons());

				selectedDto.setCatImgId(categoryResp.getCatImgId());

				selectedDto.setBgImgId(categoryResp.getBgImgId());

				selectedDto.setCatSpace(categoryResp.getCatSpace());

				selectedDto.setSubCat(categoryResp.getSubCat());

				selectedDto.setSubCatHindi(categoryResp.getSubCatHindi());

				selectedDto.setSubCatGujarati(categoryResp.getSubCatGujarati());

				selectedDto.setAnyItem(categoryResp.getAnyItem());

				selectedDto.setCatNickNameEnglish(categoryResp.getCatNickNameEnglish());

				selectedDto.setCatNickNameHindi(categoryResp.getCatNickNameHindi());

				selectedDto.setCatHeadingEnglish(categoryResp.getCatHeadingEnglish());

				selectedDto.setCatHeadingHindi(categoryResp.getCatHeadingHindi());

				selectedDto.setCatHeadingGujarati(categoryResp.getCatHeadingGujarati());

				selectedDto.setCatNickNameGujarati(categoryResp.getCatNickNameGujarati());

				String categoryStatus = ChangeStatus.NORMAL.name();

				if (!activeItems.isEmpty()) {
					categoryStatus = activeItems.get(0).getCategoryStatus().name();
				} else if (!cancelledItems.isEmpty()) {
					categoryStatus = cancelledItems.get(0).getCategoryStatus().name();
				}

				selectedDto.setCategoryStatus(categoryStatus);

				result.add(selectedDto);
			});
		}

		return result;
	}

	private List<MenuPreparationSelectedItemDetailsResponseDto> loadPackageSelectedItems(Long packageId) {

		List<CustomPackageDetailsEntity> packageItems = customPackageDetailsRepository.findByPackageId(packageId);

		Map<Long, MenuPreparationSelectedItemDetailsResponseDto> categoryMap = new LinkedHashMap<>();

		for (CustomPackageDetailsEntity item : packageItems) {

			Long categoryId = item.getMenuCategory().getId();

			MenuPreparationSelectedItemDetailsResponseDto categoryDto = categoryMap.computeIfAbsent(categoryId, k -> {

				MenuPreparationSelectedItemDetailsResponseDto dto = new MenuPreparationSelectedItemDetailsResponseDto();

				dto.setMenuCategoryId(item.getMenuCategory().getId());

				dto.setMenuCategoryName(item.getMenuCategory().getNameEnglish());

				dto.setMenuCategoryNameHindi(item.getMenuCategory().getNameHindi());

				dto.setMenuCategoryNameGujarati(item.getMenuCategory().getNameGujarati());

				dto.setMenuSortOrder(item.getMenuSortOrder());

				dto.setMenuNotes(item.getMenuInstruction());

				dto.setAnyItem(item.getAnyItem());
				dto.setCatNickNameEnglish(
						item.getCatNickNameEnglish() != null && !item.getCatNickNameEnglish().trim().isEmpty()
								? item.getCatNickNameEnglish()
								: item.getMenuCategory().getReportNameEnglish());

				dto.setCatNickNameHindi(
						item.getCatNickNameHindi() != null && !item.getCatNickNameHindi().trim().isEmpty()
								? item.getCatNickNameHindi()
								: item.getMenuCategory().getReportNameHindi());

				dto.setCatNickNameGujarati(
						item.getCatNickNameGujarati() != null && !item.getCatNickNameGujarati().trim().isEmpty()
								? item.getCatNickNameGujarati()
								: item.getMenuCategory().getReportNameGujarati());

				dto.setSelectedMenuPreparationItems(new ArrayList<>());

				return dto;
			});

			if (item.getMenuItem() != null) {

				MenuPreparationDetailsResponseDto itemDto = new MenuPreparationDetailsResponseDto();

				itemDto.setMenuItemId(item.getMenuItem().getId());

				itemDto.setMenuItemName(item.getMenuItem().getNameEnglish());

				itemDto.setMenuItemNameHindi(item.getMenuItem().getNameHindi());

				itemDto.setMenuItemNameGujarati(item.getMenuItem().getNameGujarati());

				itemDto.setItemSortOrder(item.getItemSortOrder());

				itemDto.setItemNotes(item.getItemInstruction());

				itemDto.setItemPrice(item.getItemPrice());

				itemDto.setItemNickNameEnglish(
						item.getItemNickNameEnglish() != null && !item.getItemNickNameEnglish().trim().isEmpty()
								? item.getItemNickNameEnglish()
								: "");
				itemDto.setItemNickNameHindi(
						item.getItemNickNameHindi() != null && !item.getItemNickNameHindi().trim().isEmpty()
								? item.getItemNickNameHindi()
								: "");
				itemDto.setItemNickNameGujarati(
						item.getItemNickNameGujarati() != null && !item.getItemNickNameGujarati().trim().isEmpty()
								? item.getItemNickNameGujarati()
								: "");

				categoryDto.getSelectedMenuPreparationItems().add(itemDto);
			}
		}

		return new ArrayList<>(categoryMap.values());
	}

	private MenuPreparationResponseDto buildMenuPreparationResponse(
			EventFunctionMenuPreparationResponseDto eventFunctionDto, MenuPreparationEntity entity, long totalItem,
			int totalPage) {

		MenuPreparationResponseDto response = new MenuPreparationResponseDto();
		response.setEventFunction(eventFunctionDto);
		response.setTotalItem(totalItem);
		response.setTotalPage(totalPage);

		if (entity == null) {
			response.setId(null);
			response.setDefaultPrice(BigDecimal.valueOf(eventFunctionDto.getRate()));
			response.setPax(eventFunctionDto.getPax());
			response.setSortorder(0);
			response.setPrice(BigDecimal.valueOf(eventFunctionDto.getRate()));

			// ── No menu prep yet — check if eventFunction has package ─────────
			if (eventFunctionDto.getIsPackage() != null && eventFunctionDto.getIsPackage()
					&& eventFunctionDto.getCustomPackageId() != null) {

				response.setIsPackage(true);
				response.setPackageId(eventFunctionDto.getCustomPackageId());
				response.setPackageName(
						eventFunctionDto.getCustomPackageName() != null ? eventFunctionDto.getCustomPackageName() : "");
				response.setPackagePrice(BigDecimal.valueOf(eventFunctionDto.getRate()));

			} else {
				response.setIsPackage(false);
				response.setPackageId(null);
				response.setPackageName("");
				response.setPackagePrice(BigDecimal.ZERO);
			}
			// ─────────────────────────────────────────────────────────────────
			return response;
		}

		response.setId(entity.getId());
		response.setDefaultPrice(entity.getDefaultPrice());
		response.setPax(entity.getPax());
		response.setSortorder(entity.getSortorder());
		response.setPrice(entity.getPrice());

		// ── Menu prep exists — prefer entity package, fallback to eventFunction
		if (Boolean.TRUE.equals(entity.getIsPackage()) && entity.getCustomPackage() != null) {

			response.setIsPackage(true);
			response.setPackageId(entity.getCustomPackage().getId());
			response.setPackageName(entity.getPackageName());
			response.setPackagePrice(entity.getPackagePrice());

		} else if (eventFunctionDto.getIsPackage() != null && eventFunctionDto.getIsPackage()
				&& eventFunctionDto.getCustomPackageId() != null) {

			// entity has no package but eventFunction does
			response.setIsPackage(true);
			response.setPackageId(eventFunctionDto.getCustomPackageId());
			response.setPackageName(
					eventFunctionDto.getCustomPackageName() != null ? eventFunctionDto.getCustomPackageName() : "");
			response.setPackagePrice(entity.getPackagePrice() != null ? entity.getPackagePrice() : BigDecimal.ZERO);

		} else {
			response.setIsPackage(entity.getIsPackage());
			response.setPackageId(null);
			response.setPackageName(entity.getPackageName());
			response.setPackagePrice(entity.getPackagePrice());
		}
		// ─────────────────────────────────────────────────────────────────────

		return response;
	}

	public void removeMenuItems(List<Long> toRemove, EventMasterEntity eventEntity,
			EventFunctionMasterEntity eventFunctionEntity) {

		for (Long menuItemId : toRemove) {

			try {
				menuAllocationItemRawMaterRepository.deleteRawMaterials(menuItemId, eventEntity.getId(),
						eventFunctionEntity.getId());

				menuAllocationOrderRepository.deleteOrders(menuItemId, eventEntity.getId(),
						eventFunctionEntity.getId());

				menuAllocationRepository.deleteMenuAllocations(menuItemId, eventEntity.getId(),
						eventFunctionEntity.getId());
				menuAllocationItemCaptainReceipeRepository.deleteAllocatedCaptainReceipe(menuItemId,
						eventEntity.getId(), eventFunctionEntity.getId());

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public Boolean deleteMenuPreparationItem(Long menuPreparationId, Long menuCategoryId, Long itemId) {
		MenuCategoryMasterEntity category = menuCategoryMasterRepository.findByIdAndIsDeleteFalse(menuCategoryId)
				.orElseThrow(() -> new RuntimeException("Menu Category not found with id: " + menuCategoryId));

		MenuItemMasterEntity item = menuItemMasterRepository.findByIdAndIsDeleteFalse(itemId)
				.orElseThrow(() -> new RuntimeException("Menu Item not found with id: " + itemId));

		MenuPreparationEntity menuPreparation = menuPreparationRepository.findByIdAndIsDeleteFalse(menuPreparationId)
				.orElseThrow(() -> new RuntimeException("Menu Preparation not found with id: " + menuPreparationId));

		Optional<MenuPreparationDetailsEntity> menuPreRes = menuPreparationDetailsRepository
				.findByMenuPreparationAndMenuItemAndMenuCategory(menuPreparation, item, category);
		if (!menuPreRes.isPresent()) {
			return false;
		}
		menuPreparationDetailsRepository.deleteById(menuPreRes.get().getId());
		return true;
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
				PdfFont font = pno < 10 ? ex_pagenumberFont : pno < 100 ? ex_pagenumberFontINC : ex_pagenumberFontINC1;
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
					canvas.addImageFittedIntoRectangle(bgImage.getProperty(Property.BACKGROUND_IMAGE),
							new Rectangle(0, 0, 595, 842), false);
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

	public EventReportResponseDto fetchAndPrepareEventReportData(Long eventId, Long eventFunctionId, Integer lang,
			Long userId) {

		List<Object[]> rows = menuPreparationDetailsRepository.getEventMenuReportRaw(eventId, eventFunctionId, lang,
				userId, "en", "en");
		List<Object[]> decorRows = menuPreparationDetailsRepository.getEventDecorReportRaw(eventId, eventFunctionId, 0,
				userId, "en", "en");

		EventReportResponseDto eventDto = new EventReportResponseDto();
		Map<Long, EventFunctionReportResponseDto> functionMap = new LinkedHashMap<>();

		boolean eventDataSet = false;

		for (Object[] r : rows) {
			if (!eventDataSet) {
				setEventData(eventDto, r);
				eventDataSet = true;
			}

			EventFunctionReportResponseDto functionDto = getOrCreateFunction(eventDto, functionMap, r);

			MenuReportResponseDto categoryDto = getOrCreateCategory(functionDto, r);

			MenuItemForReportResponseDto item = createMenuItem(r);

			categoryDto.getMenuItems().add(item);
			if (Boolean.TRUE.equals(r[77])) {
				categoryDto.setImagePath(item.getImagePath());
			}
		}

		for (Object[] r : decorRows) {
			if (!eventDataSet) {
				setEventData(eventDto, r);
				eventDataSet = true;
			}

			EventFunctionReportResponseDto functionDto = getOrCreateFunction(eventDto, functionMap, r);
			DecoreReportResponseDto categoryDto = getOrCreateDecoreCategory(functionDto, r);
			categoryDto.getDecoreItems().add(createDecoreItem(r));
		}

		return eventDto.getEventNo() == null ? null : eventDto;
	}

	public EventReportResponseDto fetchAndPrepareTesingEventReportData(Long eventId, Long eventFunctionId, Integer lang,
			Long testerId) {

		List<Object[]> rows = eventFoodTestingRepository.getAllTestingPreparationData(eventId, eventFunctionId,
				testerId, lang, null, null);

		System.out.println("rows : " + rows);
		EventReportResponseDto eventDto = new EventReportResponseDto();
		Map<Long, EventFunctionReportResponseDto> functionMap = new LinkedHashMap<>();

		boolean eventDataSet = false;

		for (Object[] r : rows) {

			if (!eventDataSet) {
				setEventData(eventDto, r);
				eventDataSet = true;
			}

			EventFunctionReportResponseDto functionDto = getOrCreateFunction(eventDto, functionMap, r);

			MenuReportResponseDto categoryDto = getOrCreateCategory(functionDto, r);

			categoryDto.getMenuItems().add(createMenuItem(r));
		}

		return eventDto.getEventNo() == null ? null : eventDto;
	}

	public EventReportResponseDto fetchAndPrepareEventReportData(Long eventId, Long eventFunctionId, Integer lang,
			Long userId, String defaultLanguage, String preferedLanguage) {

		List<Object[]> rows = menuPreparationDetailsRepository.getEventMenuReportRaw(eventId, eventFunctionId, lang,
				userId, defaultLanguage, preferedLanguage);

		EventReportResponseDto eventDto = new EventReportResponseDto();
		Map<Long, EventFunctionReportResponseDto> functionMap = new LinkedHashMap<>();

		boolean eventDataSet = false;

		for (Object[] r : rows) {

			if (!eventDataSet) {
				setEventData(eventDto, r);
				eventDataSet = true;
			}

			EventFunctionReportResponseDto functionDto = getOrCreateFunction(eventDto, functionMap, r);

			MenuReportResponseDto categoryDto = getOrCreateCategory(functionDto, r);

			categoryDto.getMenuItems().add(createMenuItem(r));
		}

		return eventDto.getEventNo() == null ? null : eventDto;
	}

	private void setEventData(EventReportResponseDto eventDto, Object[] r) {

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

		eventDto.setEventId(getLong(r[0]));
		eventDto.setEventNo(getString(r[1]));
		eventDto.setVenue(getString(r[4]));
		eventDto.setEventName(getString(r[6]));
		eventDto.setRemark(getString(r[26]));
		eventDto.setPartyName(getString(r[30]));
		eventDto.setMobileNo(getString(r[31]));
		eventDto.setFoodNotes(getString(r[28]));
		eventDto.setFoodType(getString(r[29]));
		eventDto.setCmpName(getString(r[32]));
		eventDto.setCmpPhone(getString(r[33]));
		eventDto.setCmpAddress(getString(r[34]));
		eventDto.setPax(getString(r[35]));
		eventDto.setUserFirstName(getString(r[36]));
		eventDto.setUserLastName(getString(r[37]));
		eventDto.setCountrycode(getString(r[38]));
		eventDto.setEmail(getString(r[39]));
		eventDto.setLogo(getString(r[40]));
		eventDto.setFunctions(new ArrayList<>());

		if (getString(r[3]) != null) {
			eventDto.setEventStartTimestamp(
					LocalDateTime.parse(getString(r[3]), inputFormatter).toLocalDate().format(dateOnlyFormatter));
			eventDto.setEventTime(
					LocalDateTime.parse(getString(r[3]), inputFormatter).toLocalTime().format(timeFormatter));
		}

		if (getString(r[2]) != null) {
			eventDto.setEventEndTimestamp(
					LocalDateTime.parse(getString(r[2]), inputFormatter).toLocalDate().format(dateOnlyFormatter));
		}

		eventDto.setBillingNameEnglish(getString(r[48]));
		eventDto.setBillingNameHindi(getString(r[49]));
		eventDto.setBillingNameGujarati(getString(r[50]));
		eventDto.setFoodNotesHindi(getString(r[51]));
		eventDto.setFoodNotesGujarati(getString(r[52]));
		eventDto.setServiceHindi(getString(r[55]));
		eventDto.setServiceGujarati(getString(r[56]));

		eventDto.setThemeHindi(getString(r[57]));
		eventDto.setThemeGujarati(getString(r[58]));

		eventDto.setService(getString(r[59]));
		eventDto.setTheme(getString(r[60]));

		eventDto.setBanquetHallId(getLong(r[73]));
		eventDto.setBanquetHallName(getString(r[74]));

		eventDto.setEventManagerId(getLong(r[81]));
		eventDto.setEventManagerName(getString(r[82]));

		eventDto.setCordinationPersonName(getString(r[85]));
		eventDto.setCordinationPersonContactno(getString(r[86]));

		eventDto.setPartyAddress(getString(r[89]));

		eventDto.setPermissable_item(getString(r[90]));

		eventDto.setNot_permissable_item(getString(r[91]));

		eventDto.setReference(getString(r[92]));

		eventDto.setVenue_img(getString(r[93]));

		eventDto.setPrefix(getString(r[95]));

		eventDto.setAltMobileNo(getString(r[101]));
	}

	private EventFunctionReportResponseDto getOrCreateFunction(EventReportResponseDto eventDto,
			Map<Long, EventFunctionReportResponseDto> functionMap, Object[] r) {

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Long functionId = getLong(r[10]);

		return functionMap.computeIfAbsent(functionId, id -> {

			EventFunctionReportResponseDto fn = new EventFunctionReportResponseDto();
			fn.setFunctionId(id);
			fn.setFunctionName(getString(r[7]));
			fn.setFunctionVenue(getString(r[13]));
			fn.setPax(getInt(r[14]));
			fn.setRate(getDouble(r[15]));
			fn.setIsPackage(commonService.getBoolean(r[45]));
			fn.setPackagePrice(getBigDecimal(r[15]));
			fn.setMenuCategories(new ArrayList<>());

			if (getString(r[11]) != null) {
				fn.setFunctionStartTimestamp(
						LocalDateTime.parse(getString(r[11]), inputFormatter).format(dateTimeFormatter));
			}

			if (getString(r[12]) != null) {
				fn.setFunctionEndTimestamp(
						LocalDateTime.parse(getString(r[12]), inputFormatter).format(dateTimeFormatter));
			}

			fn.setFunctionVenueHindi(getString(r[53]));
			fn.setFunctionVenueGujarati(getString(r[54]));

			fn.setNotesEnglish(getString(r[61]));
			fn.setNotesHindi(getString(r[62]));
			fn.setNotesGujarati(getString(r[63]));

			fn.setFoodType(getString(r[70]));
			fn.setMainFunction(getString(r[71]));
			fn.setRatePostFix(getString(r[72]));

			fn.setPackageName(getString(r[83]));
			fn.setPackPrice(getBigDecimal(r[84]));

			eventDto.getFunctions().add(fn);
			return fn;
		});
	}

	private MenuReportResponseDto getOrCreateCategory(EventFunctionReportResponseDto functionDto, Object[] r) {

		Long categoryId = getLong(r[17]);

		Map<Long, MenuReportResponseDto> categoryMap = functionDto.getMenuCategories().stream()
				.collect(Collectors.toMap(MenuReportResponseDto::getId, c -> c, (a, b) -> a, LinkedHashMap::new));

		MenuReportResponseDto category = categoryMap.get(categoryId);

		if (category == null) {
			category = new MenuReportResponseDto();
			category.setId(categoryId);
			if (r[78] == null) {
				category.setNameEnglish(getString(r[8]));
			} else {
				category.setNameEnglish(getString(r[78]));
			}
			category.setNameHindi(getString(r[41]));
			category.setNameGujarati(getString(r[42]));
			category.setSlogan(getString(r[19]));
			category.setMenuNotes(getString(r[18]));
			category.setImagePath(
					environment.getProperty("app.image.url") + replaceSpaceWithPercent20(getString(r[23])));
			category.setMenuItems(new ArrayList<>());
			category.setSubCat(getString(r[64]));
			category.setSubCatHindi(getString(r[65]));
			category.setSubCatGujarati(getString(r[66]));

			category.setCatItemSpace(getInt(r[75]));

			category.setCategoryStatus(getString(r[87]));

			category.setIsAddOnCat(r[96] != null ? ((Boolean) r[96]) : false);

			category.setCatHeadingEnglish(getString(r[98]));
			category.setCatHeadingHindi(getString(r[99]));
			category.setCatHeadingGujarati(getString(r[100]));
			;

			functionDto.getMenuCategories().add(category);
		}

		return category;
	}

	private DecoreReportResponseDto getOrCreateDecoreCategory(EventFunctionReportResponseDto functionDto, Object[] r) {

		Long categoryId = getLong(r[17]);

		if (functionDto.getDecoreCategories() == null) {
			functionDto.setDecoreCategories(new ArrayList<>());
		}

		Map<Long, DecoreReportResponseDto> categoryMap = functionDto.getDecoreCategories().stream()
				.collect(Collectors.toMap(DecoreReportResponseDto::getId, c -> c, (a, b) -> a, LinkedHashMap::new));

		DecoreReportResponseDto category = categoryMap.get(categoryId);

		if (category == null) {
			category = new DecoreReportResponseDto();
			category.setId(categoryId);
			category.setNameEnglish(getString(r[8]));
			category.setNameHindi(getString(r[41]));
			category.setNameGujarati(getString(r[42]));
			category.setSlogan(getString(r[19]));
			category.setDecoreNotes(getString(r[18]));
			category.setImagePath(
					environment.getProperty("app.image.url") + replaceSpaceWithPercent20(getString(r[23])));
			category.setDecoreItems(new ArrayList<>());
			category.setSubCat(getString(r[64]));
			category.setSubCatHindi(getString(r[65]));
			category.setSubCatGujarati(getString(r[66]));

			functionDto.getDecoreCategories().add(category);
		}

		return category;
	}

	public static String replaceSpaceWithPercent20(String input) {
		if (input == null)
			return null;
		return input.replace(" ", "%20");
	}

	private MenuItemForReportResponseDto createMenuItem(Object[] r) {

		MenuItemForReportResponseDto item = new MenuItemForReportResponseDto();
		item.setId(getLong(r[20]));
		if (r[79] == null) {
			item.setNameEnglish(getString(r[9]));
		} else {
			item.setNameEnglish(getString(r[79]));
		}
		item.setNameHindi(getString(r[43]));
		item.setNameGujarati(getString(r[44]));
		item.setSlogan(getString(r[21]));
		item.setImagePath(environment.getProperty("app.image.url") + replaceSpaceWithPercent20(getString(r[24])));
		item.setItemNotes(getString(r[27]));
		item.setPartyName(getString(r[47]));
		item.setSubItem(getString(r[67]));
		item.setSubItemHindi(getString(r[68]));
		item.setSubItemGujarati(getString(r[69]));
		item.setItemSpace(getInt(r[76]));
		item.setItemStatus(getString(r[88]));
		item.setItemHeading(getString(r[94]));

		item.setIsAddOnItem(r[97] != null ? ((Boolean) r[97]) : false);

		return item;
	}

	private DecoreItemReportResponseDto createDecoreItem(Object[] r) {

		DecoreItemReportResponseDto item = new DecoreItemReportResponseDto();
		item.setId(getLong(r[20]));
		item.setNameEnglish(getString(r[9]));
		item.setNameHindi(getString(r[43]));
		item.setNameGujarati(getString(r[44]));
		item.setSlogan(getString(r[21]));
		item.setImagePath(environment.getProperty("app.image.url") + replaceSpaceWithPercent20(getString(r[24])));
		item.setDecoreItemNotes(getString(r[27]));
		item.setPartyName(getString(r[47]));
		item.setSubItem(getString(r[67]));
		item.setSubItemHindi(getString(r[68]));
		item.setSubItemGujarati(getString(r[69]));
		item.setItemQty(getInt(r[80]));
		item.setPrice(getBigDecimal(r[81]));
		item.setItemSpace(getInt(r[76]));
		item.setVendorName(getString(r[97]));
		return item;
	}

	private BigDecimal getBigDecimal(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}
		if (value instanceof Number) {
			return BigDecimal.valueOf(((Number) value).doubleValue());
		}
		return new BigDecimal(value.toString());
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

	public PdfFont loadFont(String resourcePath) throws Exception {

		if (resourcePath == null || resourcePath.isEmpty()) {
			throw new IllegalArgumentException("Font path is empty");
		}

		File file = new File(resourcePath);

		if (file.exists()) {
			return PdfFontFactory.createFont(resourcePath, PdfEncodings.IDENTITY_H,
					PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
		}

		InputStream is = getClass().getResourceAsStream(resourcePath);

		if (is != null) {
			byte[] fontBytes = IOUtils.toByteArray(is);

			return PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H,
					PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
		}

		throw new FileNotFoundException("Font not found: " + resourcePath);
	}

	public String formatText(String text, Integer lang) {
		if (text == null)
			return "";

		// Hindi = 1, Gujarati = 2
		if (lang == 1 || lang == 2) {
			return text;
		}

		return WordUtils.capitalizeFully(text);
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

	// Helper method to load images from resources
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

	public PdfFont getFont(Long fontId, boolean bold, String defaultFont) {
		try {

			if (fontId != null && fontId != -1) {

				FontMasterEntity fontMasterEntity = fontMasterRepository.findByFontIdAndIsDeleteFalse(fontId)
						.orElseThrow(() -> new RuntimeException("Font not found with id : " + fontId));

				String basePath = environment.getProperty("app.font.path");
				String dbPath = fontMasterEntity.getFontPath();

				String fullPath = basePath + dbPath;

				System.out.println("FINAL FONT PATH: " + fullPath);

				return loadFont(fullPath);

			} else {

				String font = defaultFont.toLowerCase();

				switch (font) {

				case "times":
					return bold ? loadFont("/fonts/timesbd.ttf") : loadFont("/fonts/times.ttf");

				case "helvetica":
					return bold ? PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
							: PdfFontFactory.createFont(StandardFonts.HELVETICA);

				case "arial":
					return bold ? loadFont("/fonts/arial-bold.ttf") : loadFont("/fonts/arial-regular.ttf");
				case "palatino":
					return bold ? loadFont("/fonts/Palatino-Black.ttf") : loadFont("/fonts/Palatino-Black.ttf");
				case "verdana":
					return bold ? loadFont("/fonts/verdana-bold.ttf") : loadFont("/fonts/verdana.ttf");
				case "gothic":
					return bold ? loadFont("/fonts/gothic-bold.ttf") : loadFont("/fonts/gothic-regular.ttf");
				case "cinzel-dec-black":
					return loadFont("/fonts/cinzel-dec-black.otf");
				case "nunito-black":
					return loadFont("/fonts/nunito-black.ttf");
				case "arsenica":
					return bold ? loadFont("/fonts/arsenica-bold.ttf") : loadFont("/fonts/arsenica-regular.ttf");
				case "roboto-mono":
					return bold ? loadFont("/fonts/roboto-mono-bold.ttf") : loadFont("/fonts/roboto-mono-regular.ttf");
				case "gotham":
					return bold ? loadFont("/fonts/gotham-bold.ttf") : loadFont("/fonts/gotham-light.ttf");
				case "segoe_print":
					return bold ? loadFont("/fonts/segoe_print_bold.ttf") : loadFont("/fonts/segoe_print_regular.ttf");
				default:
					return bold ? loadFont("/fonts/timesbd.ttf") : loadFont("/fonts/times.ttf");
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Font loading failed", e);
		}
	}

	public Integer getFontSize(Integer fontSize, Integer defaultSize) {
		return fontSize != null && fontSize != 0 && fontSize != -1 ? fontSize : defaultSize;
	}

	@Override
	public String generateExclusiveReportType1(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req, Integer showAddOnLabel,
			Integer isAllItemTogether) {

		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					permissableLabel = "", notPermissableLabel = "", themeLabel = "", refLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				permissableLabel = "अनुमत्य सामग्री";
				notPermissableLabel = "अस्वीकृत सामग्री";
				refLabel = "रेफरेंस";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					permissableLabel = "அனுமதிக்கப்பட்ட பொருட்கள்";
					notPermissableLabel = "அனுமதிக்கப்படாத பொருட்கள்";
					refLabel = "பரிந்துரை";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					permissableLabel = "అనుమతించదగిన వస్తువులు";
					notPermissableLabel = "అనుమతించని వస్తువులు";
					refLabel = "రిఫరెన్స్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					permissableLabel = "അനുവദനീയമായ ഇനങ്ങൾ";
					notPermissableLabel = "അനുവദനീയമല്ലാത്ത ഇനങ്ങൾ";
					refLabel = "റഫറൻസ്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					permissableLabel = "अनुमत्य वस्तू";
					notPermissableLabel = "अस्वीकृत वस्तू";
					refLabel = "रेफरन्स";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					permissableLabel = "માન્ય સામગ્રી";
					notPermissableLabel = "અમાન્ય સામગ્રી";
					refLabel = "રેફરન્સ";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
				// English
				System.out.println("Loading English font...");

				hostLabel = "Guest Name";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Type of Event";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preference";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				permissableLabel = "Permissable Items";
				notPermissableLabel = "Not Permissable Items";
				refLabel = "Ref.";

				catFont = getFont(req.getCatFontId(), false, "times");
				catFontBold = getFont(req.getCatFontId(), true, "times");

				itemFont = getFont(req.getItemFontId(), false, "times");
				itemFontBold = getFont(req.getItemFontId(), true, "times");

				sloganFont = getFont(req.getSloganFontId(), false, "times");
				sloganFontBold = getFont(req.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color creamGold = new DeviceRgb(r2, g2, b2);
			Color softGold = new DeviceRgb(r1, g1, b1);
			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(60, 35, 60, 40);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/krishna_front_4.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/krishna_menu_4.png");
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/krishna_last_4.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null
					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			Paragraph invisibleContent = null;
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			}

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			if (isCompanyDetail == 1) {
				// Add invisible content to ensure page 1 exists
				invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(creamGold);
				invisibleContent.setFontSize(35);
				invisibleContent.setPaddingTop(140f);
				invisibleContent.setPaddingLeft(165f);
				document.add(invisibleContent);

				int secondPageNum = pdfDocument.getNumberOfPages() + 1;
				bgHandler.setPageBackground(secondPageNum, detailsPage);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}
			// Create the last page with minimal invisible content

			if (isCompanyDetail == 1) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			}

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			String reference = eventDto.getReference() != null ? eventDto.getReference() : "";

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			String permissable_item = "";
			String not_permissable_item = "";
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				permissable_item = eventDto.getPermissable_item() != null ? eventDto.getPermissable_item() : "";
				not_permissable_item = eventDto.getNot_permissable_item() != null ? eventDto.getNot_permissable_item()
						: "";
			}

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(10f);

			// Common Styles
			Style labelStyle = new Style().setFont(catFontBold).setFontColor(softGold)
					.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);

			Style valueStyle = new Style().setFont(itemFont).setFontColor(descriptionColor)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER);

			BiConsumer<String, String> addRow = (label, value) -> {
				// Label
				Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
						.setBorder(Border.NO_BORDER).setPaddingTop(15f).setPaddingBottom(2f)
						.setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(labelCell);

				// Value
				Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
						.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(valueCell);
			};

			addRow.accept(hostLabel, hostName);

			if (billingName != null && !billingName.trim().isEmpty()) {
				addRow.accept(billingNameLabel, billingName);
			}

			addRow.accept(phone, mobileNo);
			/*
			 * if (eventStartDate != "" && eventEndDate != "") { addRow.accept(eDate,
			 * eventStartDate + " - " + eventEndDate); }
			 */

			if (eventStartDate != null && !eventStartDate.isEmpty() && eventEndDate != null
					&& !eventEndDate.isEmpty()) {

				if (eventStartDate.equals(eventEndDate)) {
					addRow.accept(eDate, eventStartDate);
				} else {
					addRow.accept(eDate, eventStartDate + " - " + eventEndDate);
				}
			}
			addRow.accept(eName, eventName);

			if (venue != "") {
				addRow.accept(venueLabel, venue);
			}

			if (reference.trim().length() != 0) {
				addRow.accept(refLabel, reference);
			}

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark != "") {
				addRow.accept(remarks, remark);
			}

			addRow.accept(fNote, foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")"));

			if (service.trim().length() != 0) {
				addRow.accept(serviceLabel, service);
			}

			if (theme.trim().length() != 0) {
				addRow.accept(themeLabel, theme);
			}
			document.add(detailTable);

			EventFunctionRawMaterialPermissionResponseDto permissionResponseDto = getEventFunctionPermissionRawMaterial(
					eventId, eventFunctionId, userId);

			boolean hasPermissables = permissionResponseDto.getPermissables() != null
					&& !permissionResponseDto.getPermissables().isEmpty();

			boolean hasNotPermissables = permissionResponseDto.getNotPermissables() != null
					&& !permissionResponseDto.getNotPermissables().isEmpty();

			if (hasPermissables || hasNotPermissables) {

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
						.setMarginTop(10f);

				BiConsumer<String, String> addItemRow = (label, value) -> {
					Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
							.setBorder(Border.NO_BORDER).setPaddingTop(15f).setPaddingBottom(2f)
							.setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(labelCell);

					Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
							.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(valueCell);
				};

				if (hasPermissables) {
					addItemRow.accept(permissableLabel, "");

					String permissables = permissionResponseDto.getPermissables().stream()
							.map(item -> getRawMaterialNameByLang(item, lang)).collect(Collectors.joining(", "));

					addItemRow.accept("", permissables);
				}

				if (hasNotPermissables) {
					addItemRow.accept(notPermissableLabel, "");

					String notPermissables = permissionResponseDto.getNotPermissables().stream()
							.map(item -> getRawMaterialNameByLang(item, lang)).collect(Collectors.joining(", "));

					addItemRow.accept("", notPermissables);
				}

				document.add(itemTable);
			}

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {

				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(softGold);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 49f, 2f, 49f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(20f);

				// 1st row
				String mainFunction = eventFunctionMasterResponseDto.getMainFunction() != null
						? eventFunctionMasterResponseDto.getMainFunction()
						: "";
				// 1st row
				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph()
							.add(new com.itextpdf.layout.element.Text(mainFunction).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
					Cell mainFunctionCell = new Cell(1, 3).add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(mainFunctionCell);
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				String rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate().toString()
						: "";
				String ratePostFix = eventFunctionMasterResponseDto.getRatePostFix() != null
						? " (" + rate + " " + eventFunctionMasterResponseDto.getRatePostFix() + ") "
						: "";
				Paragraph eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(functionTitle + ratePostFix).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
				Cell eventCell = new Cell(1, 3).add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// 2ed row
				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(personLabel).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(person + foodType).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 3rd raw
				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(eTime).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");

				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(date + " " + time).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(venueLabel).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(functionVenue).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(remarks).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(functionNotes).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 4th raw
				if (req.getIsWithPrice() == 1) {

					String label;
					String value;

					if (eventFunctionMasterResponseDto.getIsPackage()) {
						label = pckPrice;
						value = eventFunctionMasterResponseDto.getPackagePrice().toString();
					} else {
						label = price;
						value = eventFunctionMasterResponseDto.getRate().toString();
					}

					// Label Cell
					eventPara = new Paragraph().add(new Text(label).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold)).setPaddingLeft(170f);

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);

					// Colon Cell
					eventPara = new Paragraph().add(new Text(":").setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(eventCell);

					// Value Cell
					eventPara = new Paragraph().add(new Text(value).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);
				}

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					String subCat = "";
					String catHeading = "";
					int catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;

					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
						catHeading = menu.getCatHeadingHindi() != null ? menu.getCatHeadingHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
						catHeading = menu.getCatHeadingGujarati() != null ? menu.getCatHeadingGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
						catHeading = menu.getCatHeadingEnglish() != null ? menu.getCatHeadingEnglish() : "";
					}

					Div menuContent = new Div();

					if (isAllItemTogether == 1) {
						menuContent.setKeepTogether(true);
					}

					Paragraph p = new Paragraph();
					for (int i = 0; i < catSpace; i++) {
						p.add("\n");
					}

					if (catHeading != null && catHeading.trim().length() != 0) {
						p.add(new Paragraph(formatText(catHeading, lang)).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 22)).setFixedLeading(22f).setFontColor(softGold)
								.setTextAlignment(TextAlignment.CENTER).setUnderline().setPaddingLeft(0)
								.setPaddingRight(0));
					}

					p.add("\n" + formatText(menu.getNameEnglish(), lang)
							+ (showAddOnLabel == 1 && menu.getIsAddOnCat() ? " (Add On)" : "")).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 20)).setFixedLeading(17f).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setPaddingLeft(0).setPaddingRight(0);

					Table table = new Table(1);
					table.setBorder(Border.NO_BORDER);
					table.setWidth(UnitValue.createPercentValue(100));
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
					table.setMarginBottom(5f);

					menuContent.add(table).setMarginTop(20f);
					String text = "";

					// Sub Category
					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);
						menuContent.add(new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 12))
								.setMultipliedLeading(1.2f).setFontColor(descriptionColor).setMarginBottom(0)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							menuContent.add(new Paragraph(text).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = " \"" + formatText(menu.getSlogan(), lang) + "\"";
							menuContent.add(
									new Paragraph(text).setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 12))
											.setMultipliedLeading(1.2f).setTextAlignment(TextAlignment.CENTER)
											.setMarginTop(0).setFontColor(descriptionColor).setMarginLeft(0));
						}
					}

					if (isCategoryImage == 1) {
						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

									img.setWidth(140);
									img.setHeight(100);

									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					// Add one blank line before items
					menuContent.add(new Paragraph(" ").setFontSize(5f).setMarginTop(0).setMarginBottom(0));

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String subItem = "";
						String itemHeading = item.getItemHeading() != null ? item.getItemHeading() : "";

						Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;

						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						for (int i = 0; i < itemSpace; i++) {
							menuContent.add(new Paragraph("\n"));
						}

						if (itemHeading != null && itemHeading.trim().length() != 0) {
							menuContent.add(new Paragraph(formatText(itemHeading, lang)).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 20)).setFixedLeading(17f)
									.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER).setUnderline()
									.setPaddingLeft(0).setPaddingRight(0));
						}

						menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)
								+ (showAddOnLabel == 1 && item.getIsAddOnItem() ? " (Add On)" : "")).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 16)).setFixedLeading(15f)
								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0));

						// Sub Item
						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);
							menuContent.add(new Paragraph(text).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
						}

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = " \"" + formatText(item.getSlogan(), lang) + "\"";
								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								menuContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

					}

					document.add(menuContent);
				}

				if (req.getIsAddDecoration() == 1) {
					List<DecoreReportResponseDto> decoreReportResponseDtos = eventFunctionMasterResponseDto
							.getDecoreCategories();

					if (decoreReportResponseDtos != null) {
//						if (isAddMenu == 0) {
						bgHandler.setDefaultBackground(watermarkBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//						}
						document.add(new Paragraph("Decoration Details:").setFont(catFont).setFontSize(22f)
								.simulateBold().setUnderline());

						for (DecoreReportResponseDto decore : decoreReportResponseDtos) {
							Div decoreContent = new Div();
							decoreContent.setKeepTogether(false);
							String text = "";
							for (DecoreItemReportResponseDto item : decore.getDecoreItems()) {
								List<DecoreMainCategoryItemImagesMasterEntity> imagesMasterEntities = decoreMainCategoryItemImagesMasterRepository
										.findAllByDecoreItem_IdAndIsDeleteFalse(item.getId());
								Integer itemQty = item.getItemQty();
								Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
								BigDecimal itemPrice = item.getPrice();

								String subItem = "";
								if (lang == 1) {
									subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
								} else if (lang == 2) {
									subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
								} else {
									subItem = item.getSubItem() != null ? item.getSubItem() : "";
								}

								BigDecimal price1 = itemPrice != null ? itemPrice : BigDecimal.ZERO;
								int qty = itemQty != null ? itemQty : 0;
								BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));

								String postLabel = "";
								if (qty > 1) {
									postLabel = " /- + Tax Each";
								} else {
									postLabel = " /- + Tax";
								}
								String text1 = "\u2022 " + formatText(item.getNameEnglish(), lang)
										+ (subItem.trim().length() != 0 ? " " + subItem : "")
										+ (totalAmount.compareTo(BigDecimal.ZERO) != 0
												? " @RS." + totalAmount.intValue() + postLabel
												: "");

								decoreContent.add(new Paragraph(text1).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 19)).setFixedLeading(20f)
										.setTextAlignment(TextAlignment.LEFT).setFontColor(creamGold).setMarginLeft(20f)
										.setMarginTop(15f));

								// Item Instructions
								if (isItemInstruction != null && isItemInstruction == 1) {
									if (item.getDecoreItemNotes() != null && !item.getDecoreItemNotes().isEmpty()) {
										text = formatText(item.getDecoreItemNotes(), lang);
										decoreContent.add(new Paragraph(text).setFont(itemFont)
												.setFontSize(getFontSize(itemFontSize, 19)).setMultipliedLeading(1.2f)
												.setFontColor(descriptionColor).setMarginLeft(0f).setMarginTop(0)
												.setTextAlignment(TextAlignment.LEFT).setMarginBottom(0).setPadding(0f)
												.setPaddingLeft(130f));
									}
								}

								if (imagesMasterEntities != null && !imagesMasterEntities.isEmpty()) {
									try {
										Image img = new Image(ImageDataFactory.create(
												environment.getProperty("app.image.url") + decore.getImagePath()));
//										Image img = new Image(loadImageFromResource("/flipbook/pages/logo.png"));

										img.setAutoScale(false);
										img.scaleToFit(300, 200);
										img.setHorizontalAlignment(HorizontalAlignment.CENTER);
										img.setMarginTop(5);
										img.setMarginBottom(5);

										decoreContent.add(img);
									} catch (Exception e) {
										// Skip if image loading fails
									}
								}

								for (int i = 0; i < itemSpace; i++) {
									decoreContent.add(new Paragraph("\n"));
								}
							}

							document.add(decoreContent);
						}
					}
				}
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// ================================================================
			// EXTRA CHARGES PAGE
			// ================================================================

			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ============================================================
					// Set background for Extra Charges page
					// ============================================================

					bgHandler.setPageBackground(lastPageNum, watermarkBgData);

					// Move to next page
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ============================================================
					// MAIN TITLE
					// ============================================================

					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setMarginBottom(15f);

					document.add(extraTitle);

					// ============================================================
					// LOOP EACH HEADING
					// ============================================================

					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// ========================================================
						// KEEP HEADING + TABLE + TOTAL TOGETHER
						// ========================================================

						Div headingBlock = new Div();

						// Important for iText 7
						headingBlock.setKeepTogether(true);

						// ========================================================
						// HEADING TITLE
						// ========================================================

						String headingName = "";

						if (lang == 1) {
							// Hindi
							headingName = heading.getHeadingNameHindi() != null ? heading.getHeadingNameHindi() : "";
						} else if (lang == 2) {
							// Gujarati
							headingName = heading.getHeadingNameGujarati() != null ? heading.getHeadingNameGujarati()
									: "";
						} else {
							// English
							headingName = heading.getHeadingName() != null ? heading.getHeadingName() : "";
						}

						headingName = headingName.toUpperCase();

						Paragraph headingTitle = new Paragraph(headingName).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold).setUnderline()
								.setTextAlignment(TextAlignment.LEFT).setMarginTop(12f).setMarginBottom(6f);

						headingBlock.add(headingTitle);

						// ========================================================
						// TABLE
						// ========================================================

						float[] colWidths = { 15f, // DATE
								17f, // START TIME
								15f, // END TIME
								13f, // SESSION
								12f, // QTY
								12f, // RATE
								16f // TOTAL
						};

						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));

						chargeTable.setWidth(UnitValue.createPercentValue(100));

						chargeTable.setBorder(new SolidBorder(softGold, 1f));

						// ========================================================
						// HEADER ROW
						// ========================================================

						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };

						for (String h : colHeaders) {

							Cell headerCell = new Cell();

							Paragraph headerParagraph = new Paragraph(h).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 12)).setFontColor(softGold).setUnderline()
									.setTextAlignment(TextAlignment.CENTER);

							headerCell.add(headerParagraph).setTextAlignment(TextAlignment.CENTER)
									.setBorder(new SolidBorder(softGold, 0.8f)).setPaddingTop(5f).setPaddingBottom(5f);

							chargeTable.addHeaderCell(headerCell);
						}

						// ========================================================
						// DATA ROWS
						// ========================================================

						if (heading.getRows() != null && !heading.getRows().isEmpty()) {

							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								// ------------------------------------------------
								// DATE
								// ------------------------------------------------

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// START TIME
								// ------------------------------------------------

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// END TIME
								// ------------------------------------------------

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// SESSION
								// ------------------------------------------------

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// QTY / PERSON ITEM
								// ------------------------------------------------

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// RATE
								// ------------------------------------------------

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// TOTAL
								// ------------------------------------------------

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));
							}

						} else {

							// ====================================================
							// EMPTY ROWS
							// ====================================================

							// 7 columns × 7 empty rows
							for (int i = 0; i < 7; i++) {

								for (int j = 0; j < 7; j++) {

									chargeTable.addCell(dataCell("", catFont, creamGold, catFontSize, softGold));
								}
							}
						}

						// ========================================================
						// ADD TABLE TO KEEP-TOGETHER BLOCK
						// ========================================================

						headingBlock.add(chargeTable);

						// ========================================================
						// HEADING TOTAL
						// ========================================================

						String headingTotal = heading.getHeadingTotal() != null
								? heading.getHeadingTotal().toPlainString()
								: "0";

						Paragraph headingTotalPara = new Paragraph("Total: " + headingTotal).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 13)).setFontColor(softGold)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f).setMarginBottom(0f);

						// Add total to same block
						headingBlock.add(headingTotalPara);

						// ========================================================
						// ADD COMPLETE BLOCK TO DOCUMENT
						// ========================================================

						document.add(headingBlock);
					}

					// ============================================================
					// GRAND TOTAL
					// ============================================================

					String grandTotal = extraCharges.getGrandTotal() != null
							? extraCharges.getGrandTotal().toPlainString()
							: "0";

					Paragraph grandTotalPara = new Paragraph("Grand Total: " + grandTotal).setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();

					document.add(grandTotalPara);

					// ============================================================
					// UPDATE LAST PAGE NUMBER
					// ============================================================

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ============ Terms And Condition PAGE ============
			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			// ============ LAST PAGE ============
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(lastPageNum, lastBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, watermarkBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Special Instruction").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	public String getRawMaterialNameByLang(EventFunctionRawMaterialDto item, int lang) {
		String name;
		if (lang == 0)
			name = item.getRawMaterialNameEnglish();
		else if (lang == 1)
			name = item.getRawMaterialNameHindi();
		else
			name = item.getRawMaterialNameGujarati();
		return (name != null && !name.isEmpty()) ? name : item.getRawMaterialNameEnglish();
	}

	@Override
	public String generateExclusiveReportType14(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req, Integer isAddDecoration,
			Integer showAdditional, Integer isAddMenu, Integer withVendor) {

		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", timeLabel = "", cordinatorPersonLabel = "", packageNameLabel = "",
					cordinatorPersonContactNumLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "ग्राहक का नाम";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				timeLabel = "";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					timeLabel = "";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					timeLabel = "";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					timeLabel = "";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					timeLabel = "";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					timeLabel = "";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
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

				catFont = getFont(req.getCatFontId(), false, "gothic");
				catFontBold = getFont(req.getCatFontId(), true, "gothic");

				itemFont = getFont(req.getItemFontId(), false, "gothic");
				itemFontBold = getFont(req.getItemFontId(), true, "gothic");

				sloganFont = getFont(req.getSloganFontId(), false, "gothic");
				sloganFontBold = getFont(req.getSloganFontId(), true, "gothic");

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
//			Color creamGold = new DeviceRgb(r2, g2, b2);
//			Color softGold = new DeviceRgb(r1, g1, b1);
//			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			Color creamGold = new DeviceRgb(0, 0, 0);
			Color softGold = new DeviceRgb(0, 0, 0);
			Color descriptionColor = new DeviceRgb(0, 0, 0);
			Color greenColor = new DeviceRgb(212, 175, 0);
			Color redColor = new DeviceRgb(255, 0, 0);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(50, 45, 60, 50);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/tgb4_1.png");
//			ImageData detailsPage = null;
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/tgb4_2.png");
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/tgb_tnc.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/krishna_last_4.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null
					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = null;
			if (adminTemplate.getTemplateMaster().getLastMainPage() != null
					&& adminTemplate.getTemplateMaster().getLastMainPage().trim().length() != 0) {
				lastBgData = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());
			}
			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			Paragraph invisibleContent = null;

			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName().toUpperCase() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo().toUpperCase() : "";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? ""
					: eventDto.getEventStartTimestamp().toUpperCase();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? ""
					: eventDto.getEventEndTimestamp().toUpperCase();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName().toUpperCase();
			String pax = eventDto.getPax();
			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime().toUpperCase() : "";
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String cordinationPersonName = eventDto.getCordinationPersonName() != null
					? eventDto.getCordinationPersonName().toUpperCase()
					: "";
			String cordinationPersonContactNo = eventDto.getCordinationPersonContactno() != null
					? eventDto.getCordinationPersonContactno().toUpperCase()
					: "";

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName().toUpperCase() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue().toUpperCase();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark().toUpperCase();
			String prefix = eventDto.getPrefix() != null ? eventDto.getPrefix().toUpperCase() : "";
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi().toUpperCase()
						: "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi().toUpperCase() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi().toUpperCase() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi().toUpperCase() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null
						? eventDto.getBillingNameGujarati().toUpperCase()
						: "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati().toUpperCase()
						: "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati().toUpperCase() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati().toUpperCase() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish().toUpperCase()
						: "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes().toUpperCase() : "";
				service = eventDto.getService() != null ? eventDto.getService().toUpperCase() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme().toUpperCase() : "";
			}

			String reference = eventDto.getReference() != null && eventDto.getReference().trim().length() != 0
					? " REF. " + eventDto.getReference().toUpperCase()
					: "";

			Boolean isFirstFun = true;
			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				bgHandler.setDefaultBackground(mainBgData); // Default: Watermark for content pages
				if (!isFirstFun) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirstFun = false;

				MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
						.findByEventFunction_IdAndIsDeleteFalse(eventFunctionMasterResponseDto.getFunctionId());

				List<MenuPreparationHistoryEntity> historyEntities = menuPreparationHistoryRepository
						.findAllByMenuPreparation(menuPreparationEntity);

				// Add cancelled categories missing from active table
				Set<Long> activeCategoryIds = eventFunctionMasterResponseDto.getMenuCategories().stream()
						.map(MenuReportResponseDto::getId).collect(Collectors.toSet());

				if (showAdditional == 1) {
					historyEntities.stream().filter(cats -> !activeCategoryIds.contains(cats.getMenuCategory().getId()))
							.forEach(cat -> {

								if (eventFunctionMasterResponseDto.getMenuCategories().stream().noneMatch(
										category -> category.getId().equals(cat.getMenuCategory().getId()))) {

									MenuReportResponseDto dto = new MenuReportResponseDto();

									dto.setId(cat.getMenuCategory().getId());
									dto.setNameEnglish(cat.getCatNickNameEnglish());
									dto.setNameHindi(cat.getCatNickNameHindi());
									dto.setNameGujarati(cat.getCatNickNameGujarati());

									dto.setMenuNotes(cat.getMenuNotes());
									dto.setSlogan(cat.getMenuSlogan());
									dto.setCatItemSpace(cat.getCatSpace());

									dto.setSubCat(cat.getSubCat());
									dto.setSubCatHindi(cat.getSubCatHindi());
									dto.setSubCatGujarati(cat.getSubCatGujarati());

									dto.setCategoryStatus(ChangeStatus.CANCELLED.name());

									eventFunctionMasterResponseDto.getMenuCategories().add(dto);
								}
							});
				}

				Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 35f, 2f, 26f, 15f, 2f, 20f }));

				detailTable.setWidth(UnitValue.createPercentValue(93f));
				detailTable.setFixedLayout();
				detailTable.setMarginTop(275f);
				detailTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi().toUpperCase()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati().toUpperCase()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish().toUpperCase()
							: "";
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang).toUpperCase();
				Double rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate()
						: 0D;

				Integer person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax()
						: 0;
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null && person != 0
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");

				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", ")).toUpperCase();
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue().toUpperCase();
				}

				Cell labelCell = new Cell()
						.add(new Paragraph(safeText(eDate)).setFont(catFont).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(softGold, 1f)).setPadding(0);
				detailTable.addCell(labelCell);

				Cell middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
						.setBorderBottom(new SolidBorder(softGold, 1f)).setPadding(0);
				detailTable.addCell(middleCell);

				Cell valueCell = new Cell()
						.add(new Paragraph(safeText(date)).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
						.setBorderBottom(new SolidBorder(softGold, 1f)).setPadding(0);
				detailTable.addCell(valueCell);

//				addRow.accept(eTime, eventTime);

				labelCell = new Cell()
						.add(new Paragraph(safeText(eTime)).setFont(catFont).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.RIGHT)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setBorderBottom(new SolidBorder(softGold, 1f))
						.setPadding(0);
				detailTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
						.setBorderBottom(new SolidBorder(softGold, 1f)).setPadding(0);
				detailTable.addCell(middleCell);

				valueCell = new Cell()
						.add(new Paragraph(safeText(time.toUpperCase())).setFont(itemFont)
								.setFontColor(descriptionColor).setFontSize(getFontSize(itemFontSize, 14))
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
						.setBorderBottom(new SolidBorder(softGold, 1f)).setPadding(0f);
				detailTable.addCell(valueCell);

				labelCell = new Cell()
						.add(new Paragraph(safeText(hostLabel)).setFont(catFont).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				detailTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(middleCell);

				valueCell = new Cell(1, 4)
						.add(new Paragraph(safeText(prefix + " " + hostName + reference)).setFont(itemFont)
								.setFontColor(descriptionColor).setFontSize(getFontSize(itemFontSize, 14))
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(valueCell);

//				addFullRow.accept(phone, mobileNo);
				if (cordinationPersonName != null && cordinationPersonName.trim().length() != 0
						&& cordinationPersonContactNo != null && cordinationPersonContactNo.trim().length() != 0) {
					mobileNo = cordinationPersonName + " - " + cordinationPersonContactNo;
				}

				if (mobileNo != null && mobileNo.trim().length() != 0) {
					labelCell = new Cell()
							.add(new Paragraph(safeText(phone)).setFont(catFont).setFontColor(softGold)
									.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
									.setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
					detailTable.addCell(labelCell);

					middleCell = new Cell()
							.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
									.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
					detailTable.addCell(middleCell);

					valueCell = new Cell(1, 4)
							.add(new Paragraph(safeText(mobileNo)).setFont(itemFont).setFontColor(descriptionColor)
									.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
					detailTable.addCell(valueCell);
				}

//				addFullRow.accept(venueLabel, venue);

				labelCell = new Cell()
						.add(new Paragraph(safeText(venueLabel)).setFont(catFont).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				detailTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(middleCell);

				valueCell = new Cell(1, 4)
						.add(new Paragraph(safeText(functionVenue)).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(valueCell);

//				addFullRow.accept(eName, eventName);

				labelCell = new Cell()
						.add(new Paragraph(safeText(eName)).setFont(catFont).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				detailTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(middleCell);

				valueCell = new Cell(1, 4)
						.add(new Paragraph(safeText(eventName)).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(valueCell);

//				addFullRow.accept(personLabel, pax);

				labelCell = new Cell()
						.add(new Paragraph(safeText(personLabel)).setFont(catFont).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				detailTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(middleCell);

				valueCell = new Cell(1, 4)
						.add(new Paragraph(safeText(person.toString()) + " PERSONS").setFont(itemFont)
								.setFontColor(descriptionColor).setFontSize(getFontSize(itemFontSize, 14))
								.simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(valueCell);

				if (remark != null && remark.trim().length() != 0) {
					// addFullRow.accept(remarks, remark);
					labelCell = new Cell()
							.add(new Paragraph(safeText(remarks)).setFont(catFont).setFontColor(softGold)
									.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
									.setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
					detailTable.addCell(labelCell);

					middleCell = new Cell()
							.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
									.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
					detailTable.addCell(middleCell);

					valueCell = new Cell(1, 4)
							.add(new Paragraph(safeText(remark)).setFont(itemFont).setFontColor(descriptionColor)
									.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
					detailTable.addCell(valueCell);
				}
//				addFullRow.accept(fNote,
//						foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")"));

				labelCell = new Cell()
						.add(new Paragraph(safeText(fNote)).setFont(catFont).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				detailTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(middleCell);

				valueCell = new Cell(1, 4)
						.add(new Paragraph(safeText(foodNotesName.toString().toUpperCase()
								+ (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")"))).setFont(itemFont)
								.setFontColor(descriptionColor).setFontSize(getFontSize(itemFontSize, 14))
								.simulateBold().setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				detailTable.addCell(valueCell);

				String packageName = eventFunctionMasterResponseDto.getPackageName() != null
						? eventFunctionMasterResponseDto.getPackageName().toUpperCase()
						: "";
				BigDecimal packagePrice = eventFunctionMasterResponseDto.getPackPrice() != null
						? eventFunctionMasterResponseDto.getPackPrice()
						: BigDecimal.ZERO;

				if (rate != 0D) {
					labelCell = new Cell()
							.add(new Paragraph(safeText(packageNameLabel)).setFont(catFont).setFontColor(softGold)
									.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
									.simulateBold().setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
					detailTable.addCell(labelCell);

					middleCell = new Cell()
							.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
									.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
					detailTable.addCell(middleCell);

					valueCell = new Cell(1, 4)
							.add(new Paragraph(packageName + " @RS." + rate.intValue() + "/- PP + TAX")
									.setFont(itemFont).setFontColor(descriptionColor)
									.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
							.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
					detailTable.addCell(valueCell);
				}

				labelCell = new Cell(1, 6)
						.add(new Paragraph(safeText("MENU")).setFont(catFont).setFontColor(softGold).simulateBold()
								.setFontSize(getFontSize(catFontSize, 16)).setTextAlignment(TextAlignment.CENTER)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f))
						.setBorderBottom(new SolidBorder(1f)).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				detailTable.addCell(labelCell);

				document.add(detailTable);

				/* ---------------------------------------------------- */

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();
				boolean isMenuAvailable = false;
				if (isAddMenu == 1) {
					// Add menu content
					for (MenuReportResponseDto menu : menuReportResponseDtos) {
						isMenuAvailable = true;
						bgHandler.setDefaultBackground(watermarkBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
						int catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;

						String subCat = "";
						if (lang == 1) {
							subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
						} else if (lang == 2) {
							subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
						} else {
							subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
						}
						String catStatus = menu.getCategoryStatus() != null ? menu.getCategoryStatus() : "NORMAL";
						Div menuContent = new Div();
						menuContent.setKeepTogether(true);

						Paragraph p = new Paragraph();

						for (int i = 0; i < catSpace; i++) {
							p.add("\n");
						}

						p.add(formatText(menu.getNameEnglish(), lang)).simulateBold().setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 24)).setFixedLeading(24f)
								.setTextAlignment(TextAlignment.CENTER).setUnderline().setPaddingLeft(0)
								.setPaddingRight(0);

						if (showAdditional == 1) {
							if (catStatus.equalsIgnoreCase("ADDED")) {
								p.setFontColor(greenColor);
							} else if (catStatus.equalsIgnoreCase("CANCELLED")) {
								p.setFontColor(redColor);
								p.setLineThrough();
							} else {
								p.setFontColor(softGold);
							}
						} else {
							p.setFontColor(softGold);
						}
						Table table = new Table(1);
						table.setBorder(Border.NO_BORDER);
						table.setWidth(UnitValue.createPercentValue(100));
						table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
						table.setMarginBottom(5f);

						menuContent.add(table).setMarginTop(20f);
						String text = "";

						// Sub Category
						if (subCat.trim().length() != 0) {
							text = formatText(subCat, lang);
							menuContent.add(new Paragraph(text).setFont(catFont).simulateBold().setUnderline()
									.setFontSize(getFontSize(catFontSize, 16)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}

						// Category Instructions
						if (isCategoryInstruction != null && isCategoryInstruction == 1
								&& !catStatus.equalsIgnoreCase("CANCELLED")) {
							if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
								text = "(" + formatText(menu.getMenuNotes(), lang) + ")";
								menuContent.add(new Paragraph(text).setFont(catFont)
										.setFontSize(getFontSize(catFontSize, 16)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginBottom(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
							}
						}

						// Category Slogan
						if (isCategorySlogan != null && isCategorySlogan == 1
								&& !catStatus.equalsIgnoreCase("CANCELLED")) {
							if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
								text = formatText(menu.getSlogan(), lang);
								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 16)).setMultipliedLeading(1.2f)
										.setTextAlignment(TextAlignment.CENTER).setMarginTop(0)
										.setFontColor(descriptionColor).setMarginLeft(0));
							}
						}

						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1
								&& !catStatus.equalsIgnoreCase("CANCELLED")) {

							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

									// Image img = new Image(loadImageFromResource("/flipbook/pages/logo.png"));

									img.setAutoScale(false);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setWidth(UnitValue.createPercentValue(40f));
									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}

						List<MenuPreparationHistoryEntity> cancelledItems = historyEntities.stream()
								.filter(item -> item.getMenuCategory().getId().equals(menu.getId()))
								.collect(Collectors.toList());

						if (showAdditional == 1) {
							// CANCELLED
							for (MenuPreparationHistoryEntity history : cancelledItems) {

								MenuItemForReportResponseDto dto = new MenuItemForReportResponseDto();

								dto.setNameEnglish(history.getItemNickNameEnglish());
								dto.setNameHindi(history.getItemNickNameHindi());
								dto.setNameGujarati(history.getItemNickNameGujarati());
								dto.setSlogan(history.getItemSlogan());
								dto.setItemNotes(history.getItemNotes());
								dto.setId(history.getMenuItem().getId());

								dto.setItemStatus(ChangeStatus.CANCELLED.name());

								if (menu.getMenuItems() == null) {
									menu.setMenuItems(new ArrayList<>());
								}

								menu.getMenuItems().add(dto);
							}
						}
						boolean previousHadSubItem = false; // tracks state across iterations

						for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
							Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
							String subItem = "";
							if (lang == 1) {
								subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
							} else if (lang == 2) {
								subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
							} else {
								subItem = item.getSubItem() != null ? item.getSubItem() : "";
							}
							boolean hasSubItem = subItem.trim().length() != 0;

							for (int i = 0; i < itemSpace; i++) {
								menuContent.add(new Paragraph("\n"));
							}
							String itemStatus = item.getItemStatus() != null ? item.getItemStatus() : "NORMAL";
							String itemHeading = item.getItemHeading();

							Paragraph paragraph = new Paragraph();

							if (itemHeading != null && !itemHeading.trim().isEmpty()) {
								menuContent.add(new AreaBreak(AreaBreakType.NEXT_AREA));
								menuContent.add(new Paragraph(formatText(itemHeading, lang)).setFont(catFont)
										.simulateBold().setUnderline().setFontSize(getFontSize(catFontSize, 24))
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(5));
							}

							Text itemName = new Text(formatText(item.getNameEnglish() + " ", lang)).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 20));

							if (showAdditional == 1) {
								if (itemStatus.equalsIgnoreCase("ADDED")) {
									itemName.setFontColor(greenColor);
								} else if (itemStatus.equalsIgnoreCase("CANCELLED")) {
									itemName.setFontColor(redColor);
									itemName.setLineThrough();
								} else {
									itemName.setFontColor(softGold);
								}
							} else {
								itemName.setFontColor(softGold);
							}

							paragraph.add(itemName);

							if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null

									&& !item.getItemNotes().trim().isEmpty()

									&& !itemStatus.equalsIgnoreCase("CANCELLED")) {

								paragraph.setFont(itemFont).setFontSize(16);

								String instructionText = item.getItemNotes();

								addFormattedText(paragraph, instructionText, lang, false);

							}

							float gapBeforeThisItem = previousHadSubItem ? 10f : 20f;

							paragraph.setFixedLeading(22f).setPaddingTop(gapBeforeThisItem).setMarginTop(0f)
									.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0);

							menuContent.add(paragraph);

							if (isItemSlogan != null && isItemSlogan == 1
									&& !itemStatus.equalsIgnoreCase("CANCELLED")) {
								if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
									text = formatText(item.getSlogan(), lang);
									menuContent.add(new Paragraph(text).setFont(sloganFont)
											.setFontSize(getFontSize(sloganFontSize, 16)).setMultipliedLeading(1.2f)
											.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
											.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
								}
							}

							if (hasSubItem) {
								text = formatText(subItem, lang);
								menuContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 16)).setMultipliedLeading(1f)
										.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0f)
										.setTextAlignment(TextAlignment.CENTER).simulateBold().simulateItalic()
										.setMarginBottom(0).setPaddingTop(8f).setPaddingBottom(0f));
							}

							previousHadSubItem = hasSubItem;
						}

						document.add(menuContent);
					}
				}

				int lastPageNum = pdfDocument.getNumberOfPages() + 1;

				// ============ EXTRA CHARGES (after ALL functions' menus/decorations are done,
				// no forced new page) ============
				if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

					ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
							userId);

					if (extraCharges != null && extraCharges.getHeadings() != null
							&& !extraCharges.getHeadings().isEmpty()) {

						for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges
								.getHeadings()) {

							if (heading.getHeadingName() != null && !heading.getHeadingName().trim().isEmpty()) {

								if (heading.getRows() != null && !heading.getRows().isEmpty()) {
									for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {
										Paragraph headingTitle = new Paragraph().setFont(itemFont)
												.setFontSize(getFontSize(catFontSize, 20)).setFontColor(softGold)
												.setTextAlignment(TextAlignment.CENTER).setMarginTop(25f)
												.setMarginBottom(0f);

										String headingText = heading.getHeadingName().trim();

										Integer qty = row.getPersonItem();

										String postLabel;
										String rsLabel;

										if (qty > 1) {
											postLabel = "<b>/- + Tax Each</b>";
											rsLabel = "\n@ RS ";
										} else {
											postLabel = "<b>/- Pp + Tax</b>";
											rsLabel = "@ Rs ";
										}

										headingText = "<b>" + (qty > 1 ? qty : "") + "</b> " + headingText + " <b>"
												+ rsLabel + row.getRate().intValue() + "</b>" + postLabel;

										addFormattedText(headingTitle, headingText, lang, false);

										document.add(headingTitle);

										if (heading.getSubHeadingName() != null
												&& !heading.getSubHeadingName().trim().isEmpty()) {
											Paragraph sloganParagraph = new Paragraph(heading.getSubHeadingName())
													.setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 16))
													.setMultipliedLeading(1.2f).setFontColor(descriptionColor)
													.setMarginLeft(0).setMarginTop(0)
													.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0)
													.setPadding(0f);

											document.add(sloganParagraph);
										}
									}
								}
							}

						}

						lastPageNum = pdfDocument.getNumberOfPages() + 1;
					}
				}

				if (isAddDecoration == 1) {
					List<DecoreReportResponseDto> decoreReportResponseDtos = eventFunctionMasterResponseDto
							.getDecoreCategories();

					if (decoreReportResponseDtos != null) {
//						if (isAddMenu == 0) {
						bgHandler.setDefaultBackground(watermarkBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//						}
						document.add(new Paragraph("Decoration Details:").setFont(catFont).setFontSize(22f)
								.simulateBold().setUnderline());

						for (DecoreReportResponseDto decore : decoreReportResponseDtos) {

							Div decoreContent = new Div();
							decoreContent.setKeepTogether(false);

							String text = "";

							for (DecoreItemReportResponseDto item : decore.getDecoreItems()) {

								List<EventFunctionDecorItemImagesEntity> decorItemImagesEntities = eventFunctionDecorItemImagesRepository
										.findByEventIdAndEventFunctionIdAndDecorItemIdAndIsDeleteFalse(eventId,
												eventFunctionMasterResponseDto.getFunctionId(), item.getId());

								List<String> images = decorItemImagesEntities.stream().map(
										image -> environment.getProperty("app.image.url", "") + image.getImagePath())
										.collect(Collectors.toList());

								Integer itemQty = item.getItemQty();

								Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;

								BigDecimal itemPrice = item.getPrice();

								String subItem = "";

								if (lang == 1) {
									subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
								} else if (lang == 2) {
									subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
								} else {
									subItem = item.getSubItem() != null ? item.getSubItem() : "";
								}

								BigDecimal price1 = itemPrice != null ? itemPrice : BigDecimal.ZERO;

								int qty = itemQty != null ? itemQty : 0;

								BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));

								String postLabel = "";

								if (qty > 1) {
									postLabel = " /- + Tax Each";
								} else {
									postLabel = " /- + Tax";
								}

								/*
								 * ITEM PARAGRAPH
								 */
								Paragraph paragraph = new Paragraph().setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 19)).setFixedLeading(20f)
										.setTextAlignment(TextAlignment.LEFT).setFontColor(creamGold).setMarginLeft(20f)
										.setMarginTop(15f);

								paragraph.add(
										new Text("• " + formatText(item.getNameEnglish(), lang).toUpperCase() + ": ")
												.simulateBold());

								if (subItem != null && !subItem.trim().isEmpty()) {

									addFormattedText(paragraph, subItem.trim(), lang, false);
								}

								if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {

									paragraph
											.add(new Text(" @RS." + totalAmount.intValue() + postLabel).simulateBold());
								}

								if (withVendor == 1) {

									if (item.getVendorName() != null && !item.getVendorName().trim().isEmpty()) {

										paragraph.add(new Text(" - " + item.getVendorName().trim()).simulateBold());
									}
								}

								decoreContent.add(paragraph);

								if (isItemInstruction != null && isItemInstruction == 1) {

									if (item.getDecoreItemNotes() != null
											&& !item.getDecoreItemNotes().trim().isEmpty()) {

										for (String line : item.getDecoreItemNotes().split("(?i)<br\\s*/?>")) {

											if (!line.trim().isEmpty()) {

												text = formatText(line.trim(), lang);

												Paragraph decoreParagraph = new Paragraph().setFont(itemFont)
														.setFontSize(getFontSize(itemFontSize, 19))
														.setMultipliedLeading(1f).setFontColor(descriptionColor)
														.setMarginLeft(0f).setMarginTop(0)
														.setTextAlignment(TextAlignment.LEFT).setMarginBottom(0)
														.setPadding(0f).setPaddingLeft(130f);

												addFormattedText(decoreParagraph, text, lang, true);
												decoreContent.add(decoreParagraph);
											}
										}
									}
								}

								if (images != null && !images.isEmpty()) {

									for (String imagePath : images) {

										if (imagePath == null || imagePath.trim().isEmpty()) {
											continue;
										}

										try {

											ImageData imgData = loadImageFromResource(imagePath);

											Image img = new Image(imgData);

											img.scaleAbsolute(432, 288);

											img.setHorizontalAlignment(HorizontalAlignment.CENTER);

											img.setMarginTop(8f);
											img.setMarginBottom(14f);

											img.setBorder(new SolidBorder(ColorConstants.BLACK, 1f));

											decoreContent.add(img);

										} catch (Exception imgEx) {

											System.out.println("Could not load image for decor item: " + imagePath);
										}
									}
								}

								for (int i = 0; i < itemSpace; i++) {
									decoreContent.add(new Paragraph("\n"));
								}
							}

							document.add(decoreContent);
						}
					}
				}

			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			if (req.getIsNotes() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, watermarkBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Special Instruction").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			// ============ Terms And Condition PAGE ============
			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Paragraph eventManager = new Paragraph().setFont(itemFont).setFontSize(14f).setMarginTop(650f)
						.setTextAlignment(TextAlignment.RIGHT).setMarginRight(30f);

				eventManager.add(new Text("EVENT MANAGER - ").setFont(catFont));

				eventManager.add(new Text(eventDto.getEventManagerName() != null ? eventDto.getEventManagerName() : "")
						.setFont(catFont).simulateBold().setUnderline());

				document.add(eventManager);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			// SECTION A - BUFFET
			QuotationResponseDto buffetDto = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, 0,
					false);
			boolean isBuffetQuotationShow = false;

			Table quotationTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 16.66f, 16.66f, 16.67f }));
			quotationTable.setBorder(Border.NO_BORDER);
			quotationTable.setWidth(UnitValue.createPercentValue(100f));
			quotationTable.setMarginTop(10f);
			quotationTable.setFixedLayout();
			quotationTable.setBackgroundColor(ColorConstants.WHITE);

			if (buffetDto != null) {
				List<QuotationResponseDto> items;

				items = eventFunctionQuotationServiceImpl.getFunctionData(buffetDto.getQuotationId());

				if (items != null && !items.isEmpty()) {
					isBuffetQuotationShow = true;

					Paragraph data = new Paragraph().add(new Text("GUEST NAME").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.BOTTOM).setTextAlignment(TextAlignment.LEFT);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text(buffetDto.getPartyName()).setFontSize(14).setFont(catFontBold));
					cell = new Cell(1, 3).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("DATE : " + buffetDto.getEventDate().split(" ")[0])
							.setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text(buffetDto.getVenueName()).setFontSize(14).setFont(catFontBold));
					cell = new Cell(1, 3).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph()
							.add(new Text("ESTIMATED BUFFET AMOUNT").setFontSize(16).setFont(catFontBold));
					cell = new Cell(1, 4).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("QTY.").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("RATE").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					data = new Paragraph().add(new Text("AMOUNT").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					quotationTable.addCell(cell);

					double total = 0D;

					for (QuotationResponseDto item : items) {

						quotationTable.addCell(createCell(item.getFunctionName().toUpperCase(), catFont));

						quotationTable.addCell(createRightCell(formatQty(item.getFunctionPax()), catFont));

						quotationTable.addCell(createRightCell(formatAmount(item.getRate()), catFont));

						quotationTable.addCell(createRightCell(formatAmount(item.getAmount()), catFont));

						total += safe(item.getAmount());
					}

					addSummaryRows(quotationTable, buffetDto, total, false, catFont, catFontBold);
				}
			}

			QuotationResponseDto decorDto = eventFunctionQuotationServiceImpl.getEventData(eventId, userId, 0, 0, true);

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
				items = eventFunctionQuotationServiceImpl.getFunctionData(decorDto.getQuotationId());

				if (items != null && !items.isEmpty()) {
					isDecorQuotationShow = true;

					Paragraph data = new Paragraph()
							.add(new Text("OTHER ESTIMATE AMOUNT").setFontSize(16).setFont(catFontBold));
					cell = new Cell(1, 4).add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setPaddingRight(5f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("QTY.").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("RATE").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					data = new Paragraph().add(new Text("AMOUNT").setFontSize(14).setFont(catFontBold));
					cell = new Cell().add(data).setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setTextAlignment(TextAlignment.CENTER);
					decorQuotationTable.addCell(cell);

					double total = 0D;

					for (QuotationResponseDto item : items) {

						decorQuotationTable.addCell(createCell(item.getFunctionName().toUpperCase(), catFont));

						decorQuotationTable.addCell(createRightCell(formatQty(item.getFunctionPax()), catFont));

						decorQuotationTable.addCell(createRightCell(formatAmount(item.getRate()), catFont));

						decorQuotationTable.addCell(createRightCell(formatAmount(item.getAmount()), catFont));

						total += safe(item.getAmount());
					}

					addSummaryRows(decorQuotationTable, decorDto, total, true, catFont, catFontBold);
				}
			}

			if (isBuffetQuotationShow || isDecorQuotationShow) {
				bgHandler.setDefaultBackground(watermarkBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				document.add(
						new Paragraph("You Are Also Requested To Pay Balance Estimated Amount Prior To The Function")
								.setFont(catFont).setFontSize(18f).setTextAlignment(TextAlignment.CENTER));

				document.add(quotationTable);
				document.add(decorQuotationTable);

				if (isBuffetQuotationShow && isDecorQuotationShow) {
					addCombinedGrandTotal(document, buffetDto, decorDto, catFont, catFontBold);
				}

				document.add(new Paragraph().add(new Text("All Cheque should be drawn in favor of"))
						.add(new Text(" TGB Banquets And Hotels Ltd").simulateBold()).setPaddingLeft(60f)
						.setMarginTop(15f));

				document.add(new Paragraph("BANK DETAILS FOR NEFT / RTGS / IMPS TRANSFER").setFont(catFontBold)
						.simulateBold().setPaddingLeft(60f).setMarginTop(15f));

				if (buffetDto != null) {
					Div divData2 = new Div();
					divData2.setWidth(UnitValue.createPercentValue(95));
					divData2.setPadding(5f);
					divData2.setPaddingLeft(55f);
					divData2.setHorizontalAlignment(HorizontalAlignment.LEFT);

					String accountHolderName = buffetDto.getAccountHolderName() == null
							|| buffetDto.getAccountHolderName().isEmpty() ? "" : buffetDto.getAccountHolderName();

					String accountNo = buffetDto.getAccountNo() == null || buffetDto.getAccountNo().isEmpty() ? ""
							: buffetDto.getAccountNo();

					String bankName = buffetDto.getBankName() == null || buffetDto.getBankName().isEmpty() ? ""
							: buffetDto.getBankName();

					String ifscCode = buffetDto.getIfscCode() == null || buffetDto.getIfscCode().isEmpty() ? ""
							: buffetDto.getIfscCode();

					String upiId = buffetDto.getUpiId() == null || buffetDto.getUpiId().isEmpty() ? ""
							: buffetDto.getUpiId();

					String branchName = buffetDto.getBranchName() == null || buffetDto.getBranchName().isEmpty() ? ""
							: buffetDto.getBranchName();

					Paragraph data = new Paragraph().add(new Text("TGB BANQUETS AND HOTELS LIMITED").setFontSize(10))
							.setMultipliedLeading(1f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
					divData2.add(data);
					System.out.println("cmp name : " + cmpName);
					data = new Paragraph().add(new Text("NAME OF COMPANY: ").setFont(catFontBold).setFontSize(10))
							.add(new Text(cmpName).setFont(catFont).setFontSize(10)).setMultipliedLeading(1f)
							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
					divData2.add(data);

					data = new Paragraph().add(new Text("BANK: ").setFont(catFontBold).setFontSize(10))
							.add(new Text(bankName).setFont(catFont).setFontSize(10)).setMultipliedLeading(1f)
							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
					divData2.add(data);

					data = new Paragraph().add(new Text("BRANCH: ").setFont(catFontBold).setFontSize(10))
							.add(new Text(branchName).setFont(catFont).setFontSize(10)).setMultipliedLeading(1f)
							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
					divData2.add(data);

					data = new Paragraph().add(new Text("ACCOUNT NO: ").setFont(catFontBold).setFontSize(10))
							.add(new Text(accountNo).setFont(catFont).setFontSize(10)).setMultipliedLeading(1f)
							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
					divData2.add(data);

					data = new Paragraph().add(new Text("IFSC CODE: ").setFont(catFontBold).setFontSize(10))
							.add(new Text(ifscCode).setFont(catFont).setFontSize(10)).setMultipliedLeading(1f)
							.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10f);
					divData2.add(data);

					document.add(divData2);
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	public void addFormattedText(Paragraph paragraph, String text, Integer lang, boolean isApplyFormatText) {

		Pattern pattern = Pattern.compile("<b>(.*?)</b>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		Matcher matcher = pattern.matcher(text);

		int lastEnd = 0;

		while (matcher.find()) {

			// Normal text
			if (matcher.start() > lastEnd) {
				String normalValue = text.substring(lastEnd, matcher.start());

				if (isApplyFormatText) {
					normalValue = formatText(normalValue, lang);
				}

				paragraph.add(new Text(normalValue));
			}

			// Bold text
			String boldValue = matcher.group(1);

			if (isApplyFormatText) {
				boldValue = formatText(boldValue, lang);
			}

			paragraph.add(new Text(boldValue).simulateBold());

			lastEnd = matcher.end();
		}

		// Remaining normal text
		if (lastEnd < text.length()) {
			String normalValue = text.substring(lastEnd);

			if (isApplyFormatText) {
				normalValue = formatText(normalValue, lang);
			}

			paragraph.add(new Text(normalValue));
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

		addCombinedRow(table, "TOTAL AMOUNT", formatAmount(total), boldFont);
		addCombinedRow(table, "ADVANCE PAYMENT", formatAmount(advance), normalFont);
		addCombinedRow(table, "BALANCE TOTAL", formatAmount(balance), normalFont);

		document.add(table);
	}

	private void addCombinedRow(Table table, String label, String value, PdfFont font) {

		table.addCell(new Cell().add(new Paragraph(label).setFont(font).setFontSize(12f)).setBorder(new SolidBorder(1))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2).simulateBold().setPaddingLeft(5f));

		table.addCell(new Cell().add(new Paragraph(value).setFont(font).setFontSize(12f)).setBorder(new SolidBorder(1))
				.setTextAlignment(TextAlignment.RIGHT).setPadding(2).simulateBold().setPaddingLeft(5f));
	}

	private double safe(Double value) {
		return value == null ? 0D : value;
	}

	private Cell createRightCell(String value, PdfFont font) {

		return createCell(value, font).setTextAlignment(TextAlignment.RIGHT);
	}

	private Cell createCell(String value, PdfFont font) {

		Paragraph paragraph = new Paragraph(value == null ? "" : value).setFont(font).setFontSize(11);

		return new Cell().add(paragraph).setPadding(2).setPaddingLeft(5f);
	}

	private String formatQty(Number value) {

		if (value == null) {
			return "0";
		}

		return String.format("%.0f", value.doubleValue());
	}

//	private String formatAmount(Number value) {
//
//		if (value == null) {
//			return "0.00";
//		}
//
//		return String.format("%,.2f", value.doubleValue());
//	}

	private String formatAmount(Number value) {

		if (value == null) {
			return "0";
		}

		return String.format("%,.0f", value.doubleValue());
	}

	private void addSummaryRows(Table table, QuotationResponseDto dto, double total, boolean isDecor, PdfFont normal,
			PdfFont bold) {

		addSummaryRow(table, "TOTAL", formatAmount(total), normal);

		addSummaryRow(table, "DISCOUNT", formatAmount(dto.getDiscount()), normal);

		Double subTotal = dto.getSubTotle() != null ? dto.getSubTotle() : 0;
		Double discount = dto.getDiscount() != null ? dto.getDiscount() : 0;
		addSummaryRow(table, "FINAL TOTAL", formatAmount(subTotal - discount), normal);

		addSummaryRow(table, isDecor ? "DECOR TAX" : "FOOD TAX",
				formatAmount(safe(dto.getCgstAmnt()) + safe(dto.getSgstAmnt()) + safe(dto.getIgstAmnt())), normal);

		// This section's own subtotal — e.g. 807975.00 for buffet, 207680.00 for decor
		addSummaryRow(table, "TOTAL AMOUNT", formatAmount(dto.getGrandTotal()), bold);

		double advance = safe(dto.getAdvancePayment());
		double balance = safe(dto.getGrandTotal()) - advance;

		if (advance > 0) {
			addSummaryRow(table, "ADVANCE PAYMENT", formatAmount(advance), normal);
			addSummaryRow(table, "BALANCE TOTAL", formatAmount(balance), normal);
		}
	}

	private void addSummaryRow(Table table, String label, String value, PdfFont font) {

		table.addCell(new Cell().add(new Paragraph(label).setFont(font).setFontSize(11))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2).setPaddingLeft(5f));

		table.addCell(new Cell().add(new Paragraph("").setFont(font).setFontSize(11))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2).setPaddingLeft(5f));

		table.addCell(new Cell().add(new Paragraph("").setFont(font).setFontSize(11))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2).setPaddingLeft(5f));

		table.addCell(new Cell().add(new Paragraph(value).setFont(font).setFontSize(11))
				.setTextAlignment(TextAlignment.LEFT).setPadding(2).setPaddingLeft(5f));
	}

	@Override
	public String generateExclusiveReportType13(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long partyId, Long adminTemplateModuleId,
			Long userId, AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {

		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
				// English
				System.out.println("Loading English font...");

				hostLabel = "Party Name";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Type of Event";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preparations";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";

				catFont = getFont(req.getCatFontId(), false, "times");
				catFontBold = getFont(req.getCatFontId(), true, "times");

				itemFont = getFont(req.getItemFontId(), false, "times");
				itemFontBold = getFont(req.getItemFontId(), true, "times");

				sloganFont = getFont(req.getSloganFontId(), false, "times");
				sloganFontBold = getFont(req.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color creamGold = new DeviceRgb(r2, g2, b2);
			Color softGold = new DeviceRgb(r1, g1, b1);
			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			EventReportResponseDto eventDto = fetchAndPrepareTesingEventReportData(eventId, eventFunctionId, lang,
					partyId);

			if (eventDto == null) {
				return "Data not found.";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(60, 35, 60, 40);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/krishna_front_4.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/krishna_menu_4.png");
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/krishna_last_4.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null
					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			Paragraph invisibleContent = null;
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			}

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			if (isCompanyDetail == 1) {
				// Add invisible content to ensure page 1 exists
				invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(creamGold);
				invisibleContent.setFontSize(35);
				invisibleContent.setPaddingTop(140f);
				invisibleContent.setPaddingLeft(165f);
				document.add(invisibleContent);

				int secondPageNum = pdfDocument.getNumberOfPages() + 1;
				bgHandler.setPageBackground(secondPageNum, detailsPage);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}
			// Create the last page with minimal invisible content

			if (isCompanyDetail == 1) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			}

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			// ============ PAGE 1: Front Page (Main) ============

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(10f);

			// Common Styles
			Style labelStyle = new Style().setFont(catFontBold).setFontColor(softGold)
					.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);

			Style valueStyle = new Style().setFont(itemFont).setFontColor(descriptionColor)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER);

			BiConsumer<String, String> addRow = (label, value) -> {

				// Label
				Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
						.setBorder(Border.NO_BORDER).setPaddingTop(15f).setPaddingBottom(2f)
						.setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(labelCell);

				// Value
				Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
						.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(valueCell);
			};

			addRow.accept(hostLabel, hostName);

			if (billingName != null && !billingName.trim().isEmpty()) {
				addRow.accept(billingNameLabel, billingName);
			}

			if (mobileNo != null && !mobileNo.trim().isEmpty()) {
				addRow.accept(phone, mobileNo);
			}

			if (eventStartDate != null && eventStartDate.trim().length() != 0 && eventEndDate != null
					&& eventEndDate.trim().length() != 0) {
				addRow.accept(eDate, eventStartDate + " - " + eventEndDate);
			}

			addRow.accept(eName, eventName);

			if (venue != null && venue.trim().length() != 0) {
				addRow.accept(venueLabel, venue);
			}

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark.trim().length() != 0) {
				addRow.accept(remarks, remark);
			}

			String notes = foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")");

			if (notes != null && notes.trim().length() != 0) {
				addRow.accept(fNote, notes);
			}

			if (service.trim().length() != 0) {
				addRow.accept(serviceLabel, service);
			}

			if (theme.trim().length() != 0) {
				addRow.accept(themeLabel, theme);
			}

			document.add(detailTable);

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(softGold);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 49f, 2f, 49f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(20f);

				String mainFunction = eventFunctionMasterResponseDto.getMainFunction() != null
						? eventFunctionMasterResponseDto.getMainFunction()
						: "";
				// 1st row
				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph()
							.add(new com.itextpdf.layout.element.Text(mainFunction).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
					Cell mainFunctionCell = new Cell(1, 3).add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(mainFunctionCell);
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				Paragraph eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(functionTitle).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
				Cell eventCell = new Cell(1, 3).add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// 2ed row
				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(personLabel).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(person + foodType).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 3rd raw
				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(eTime).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(date + " " + time).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(venueLabel).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(functionVenue).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

//				eventPara = new Paragraph()
//						.add(new com.itextpdf.layout.element.Text(remarks).setFont(catFont)
//								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
//						.setPaddingLeft(170f);
//				eventCell = new Cell().add(eventPara);
//				eventCell.setBorder(Border.NO_BORDER);
//				eventCell.setTextAlignment(TextAlignment.LEFT);
//				headerTable.addCell(eventCell);
//
//				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
//						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
//				eventCell = new Cell().add(eventPara);
//				eventCell.setBorder(Border.NO_BORDER);
//				eventCell.setTextAlignment(TextAlignment.CENTER);
//				headerTable.addCell(eventCell);
//
//				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(functionNotes).setFont(catFont)
//						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
//				eventCell = new Cell().add(eventPara);
//				eventCell.setBorder(Border.NO_BORDER);
//				eventCell.setTextAlignment(TextAlignment.LEFT);
//				headerTable.addCell(eventCell);

				// 4th raw
//				if (req.getIsWithPrice() == 1) {
//
//					String label;
//					String value;
//
//					if (eventFunctionMasterResponseDto.getIsPackage()) {
//						label = pckPrice;
//						value = eventFunctionMasterResponseDto.getPackagePrice().toString();
//					} else {
//						label = price;
//						value = eventFunctionMasterResponseDto.getRate().toString();
//					}
//
//					// Label Cell
//					eventPara = new Paragraph().add(new Text(label).setFont(catFont)
//							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold)).setPaddingLeft(170f);
//
//					eventCell = new Cell().add(eventPara);
//					eventCell.setBorder(Border.NO_BORDER);
//					eventCell.setTextAlignment(TextAlignment.LEFT);
//					headerTable.addCell(eventCell);
//
//					// Colon Cell
//					eventPara = new Paragraph().add(new Text(":").setFont(catFont)
//							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
//
//					eventCell = new Cell().add(eventPara);
//					eventCell.setBorder(Border.NO_BORDER);
//					eventCell.setTextAlignment(TextAlignment.CENTER);
//					headerTable.addCell(eventCell);
//
//					// Value Cell
//					eventPara = new Paragraph().add(new Text(value).setFont(catFont)
//							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
//
//					eventCell = new Cell().add(eventPara);
//					eventCell.setBorder(Border.NO_BORDER);
//					eventCell.setTextAlignment(TextAlignment.LEFT);
//					headerTable.addCell(eventCell);
//				}

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}

					Div menuContent = new Div();
					menuContent.setKeepTogether(true);

					Paragraph p = new Paragraph(formatText(menu.getNameEnglish(), lang)).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 20)).setFixedLeading(17f).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setPaddingLeft(0).setPaddingRight(0);

					Table table = new Table(1);
					table.setBorder(Border.NO_BORDER);
					table.setWidth(UnitValue.createPercentValue(100));
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
					table.setMarginBottom(5f);

					menuContent.add(table).setMarginTop(20f);
					String text = "";

					// Sub Category
					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);
						menuContent.add(new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 12))
								.setMultipliedLeading(1.2f).setFontColor(descriptionColor).setMarginBottom(0)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							menuContent.add(new Paragraph(text).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = " \"" + formatText(menu.getSlogan(), lang) + "\"";
							menuContent.add(
									new Paragraph(text).setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 12))
											.setMultipliedLeading(1.2f).setTextAlignment(TextAlignment.CENTER)
											.setMarginTop(0).setFontColor(descriptionColor).setMarginLeft(0));
						}
					}

					if (isCategoryImage == 1) {
						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));
									img.setWidth(140);
									img.setHeight(100);
									img.setAutoScale(true);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String subItem = "";
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 16)).setFixedLeading(15f)
								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0));

						// Sub Item
						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);
							menuContent.add(new Paragraph(text).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
						}

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = " (" + formatText(item.getSlogan(), lang) + ") ";
								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								menuContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

					}

					document.add(menuContent);
				}

			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

//			// EXTRA CHARGES PAGE
			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ── Set background for this page (same watermark as content pages) ──
					int extraPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(extraPageNum, watermarkBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline()
							// .setBackgroundColor(new DeviceRgb(255, 255, 0)) // yellow highlight
							.setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// Heading title: HEADING NAME
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
								.setUnderline().setTextAlignment(TextAlignment.LEFT).setMarginTop(12f)
								.setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(softGold).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, creamGold, catFontSize, softGold));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 13)).setFontColor(softGold)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ============ Terms And Condition PAGE ============
			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			// ============ LAST PAGE ============
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(lastPageNum, lastBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, whiteBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private Cell dataCell(String text, PdfFont font, Color textColor, Integer fontSize, Color borderColor) {
		return new Cell()
				.add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(getFontSize(fontSize, 12))
						.setFontColor(textColor))
				.setTextAlignment(TextAlignment.CENTER)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f)).setPaddingTop(8f)
				.setPaddingBottom(8f);
	}

	public String safeText(String value) {
		return value == null ? "" : value;
	}

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}

	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + fileSafe(date.replace("/", "_") + " (" + fileSafe(type.toUpperCase()) + ")");
	}

	public String getPartyNameByEventId(Long eventId) {
		String party = eventMasterRepository.getPartyByEventId(eventId)
				.orElseThrow(() -> new RuntimeException("Party Details Not Found."));
		return party;
	}

	@Override
	public String generateExclusiveReportType11(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {

		try {
			PdfFont catFont = null;
			PdfFont catFontBold = null;
			PdfFont itemFont = null;
			PdfFont itemFontBold = null;
			PdfFont sloganFont = null;

			Integer catFontSize = req.getCatFontSize();
			Integer itemFontSize = req.getItemFontSize();
			Integer sloganFontSize = req.getSloganFontSize();

			loadLicense();

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "";

			if (lang == 1) {
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
			} else if (lang == 2) {
				catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				catFontBold = itemFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");
				hostLabel = "માનનીય યજમાન";
				phone = "મોબાઇલ";
				eDate = "કાર્યક્રમની તારીખ";
				eName = "કાર્યક્રમનો પ્રકાર";
				venueLabel = "કાર્યક્રમ સ્થળ";
				personLabel = "વ્યક્તિ";
				eTime = "સમય";
				fNote = "ભોજન વ્યવસ્થા";
			} else {
				hostLabel = "Honourable Host";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Type of Event";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preparations";
				catFont = getFont(req.getCatFontId(), false, "times");
				catFontBold = getFont(req.getCatFontId(), true, "times");
				itemFont = getFont(req.getItemFontId(), false, "times");
				itemFontBold = getFont(req.getItemFontId(), true, "times");
				sloganFont = getFont(req.getSloganFontId(), false, "times");
			}

			// ── Colors from template ───────────────────────────────────────────
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

			Color headingColor = new DeviceRgb(r1, g1, b1); // category name, item name
			Color contentColor = new DeviceRgb(r2, g2, b2); // slogan, instructions, values

			// ── Fetch event data ───────────────────────────────────────────────
			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);
			if (eventDto == null)
				return "";

			// ── Output file setup ──────────────────────────────────────────────
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!outputPath.exists())
				outputPath.mkdirs();

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			// text area in background image
			document.setMargins(40, 35, 170, 100);

			// ── Static background images ───────────────────────────────────────
			ImageData mainBgData = loadImageFromResource("/flipbook/pages/jaival_front_.jpeg");
			ImageData detailsPage = loadImageFromResource("/flipbook/pages/jaival_detail_.jpeg");
			ImageData staticPage3 = loadImageFromResource("/flipbook/pages/jaival_static_p3.jpg");
			ImageData staticPage4 = loadImageFromResource("/flipbook/pages/jaival_static_p4.jpg");
			ImageData notesPage = loadImageFromResource("/flipbook/pages/jaival_special_notes.jpeg");
			ImageData staticPage5 = loadImageFromResource("/flipbook/pages/jaival_static_p5.jpg");
			ImageData defaultMenuBg = loadImageFromResource("/flipbook/pages/jaival_menu_4.jpeg");
			ImageData lastBgData = loadImageFromResource("/flipbook/pages/jaival_last_.jpeg");

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData);
			bgHandler.setDefaultBackground(defaultMenuBg);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// ════════════════════════════════════════════════════════════════════
			// PAGE 1 – Front cover (static image, invisible placeholder)
			// ════════════════════════════════════════════════════════════════════
			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(contentColor)
					.setFontSize(1);
			document.add(invisibleContent);

			// ════════════════════════════════════════════════════════════════════
			// PAGE 2 – Company details page (optional)
			// ════════════════════════════════════════════════════════════════════
//			if (isCompanyDetail == 1) {
//				bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, detailsPage);
//				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//				document.add(new Paragraph("\u00A0").setFontSize(1));
//			}

			// ════════════════════════════════════════════════════════════════════
			// PAGE 2 – Dynamic event details (matches PDF page 2 style)
			// ════════════════════════════════════════════════════════════════════
			bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, detailsPage);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			document.setMargins(40, 35, 100, 100);
			document.add(new Paragraph("\u00A0").setFontSize(1).setMargin(0));

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() != null ? eventDto.getEventStartTimestamp() : "";
			String eventName = eventDto.getEventName() != null ? eventDto.getEventName() : "";
			String venue = eventDto.getVenue() != null ? eventDto.getVenue() : "";
			String Food = eventDto.getFoodType() != null ? eventDto.getFoodType() : "";
			String foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
			String foodHeading = foodNotes.isEmpty() ? Food : Food + " (" + foodNotes + ")";
			String remark = eventDto.getRemark();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String formattedHost = toTitleCase(safeText(hostName));
			String formattedEventName = toTitleCase(safeText(eventName));
			String quotedHost = "\"" + formattedHost + "\"";

			// ── Party name ─────────────────────────────────────────────────────
			document.add(new Paragraph(quotedHost).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 24))
					.setFontColor(headingColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setFixedLeading(24f).setMarginTop(30f).setMarginBottom(25f));

			// ── "Dated on" label ───────────────────────────────────────────────
			document.add(new Paragraph("Dated on").setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			// ── Date value ─────────────────────────────────────────────────────
			document.add(new Paragraph(safeText(eventDate)).setFont(catFontBold).setFontSize(21)
					.setFontColor(headingColor).simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0)
					.setMultipliedLeading(1).setMarginBottom(15f));

			// ── "For the joyous occasion of" label ────────────────────────────
			document.add(new Paragraph("For the joyous occasion of").setFont(sloganFont).setFontSize(17)
					.setFontColor(headingColor).simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0)
					.setMultipliedLeading(1).setMarginTop(10f));

			// ── Event name value ───────────────────────────────────────────────
			document.add(new Paragraph(safeText(formattedEventName)).setFont(catFontBold).setFontSize(21)
					.setFontColor(headingColor).simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0)
					.setMultipliedLeading(1).setMarginBottom(15f));

			// ── "Located at" label ─────────────────────────────────────────────
			document.add(new Paragraph("Located at").setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			// ── Venue value ────────────────────────────────────────────────────
			document.add(new Paragraph(safeText(venue)).setFont(catFontBold).setFontSize(21).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginBottom(15f));

			// ── "Food preference" label ────────────────────────────────────────
			document.add(new Paragraph("Food preference").setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			// ── Food preference value ──────────────────────────────────────────
			document.add(new Paragraph(safeText(foodHeading)).setFont(catFontBold).setFontSize(21)
					.setFontColor(headingColor).simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0)
					.setMultipliedLeading(1).setMarginBottom(15f));

			// ── Functions (name + pax + time) ─────────────────────────────────
			for (EventFunctionReportResponseDto fn : eventDto.getFunctions()) {

				// Function name label
				document.add(new Paragraph(safeText(fn.getFunctionName())).setFont(sloganFont).setFontSize(17)
						.setFontColor(headingColor).simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0)
						.setMultipliedLeading(1).setMarginTop(10f));

				// Pax value
				String paxStr = fn.getPax() != null ? "For " + fn.getPax() + " Guests" : "";
				document.add(new Paragraph(paxStr).setFont(catFontBold).setFontSize(21).setFontColor(headingColor)
						.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1));

				// Time value
				if (fn.getFunctionStartTimestamp() != null) {
					String[] tParts = fn.getFunctionStartTimestamp().split(" ");
					if (tParts.length >= 3) {
						String timeStr = tParts[1] + " " + tParts[2] + " Onwards";
						document.add(new Paragraph(timeStr).setFont(catFontBold).setFontSize(21)
								.setFontColor(headingColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
								.setMargin(0).setMultipliedLeading(1).setMarginBottom(3f));
					}
				}
			}

			// ── Restore default margins before page 3 ─────────────────────────
			document.setMargins(40, 35, 170, 100);

			// ════════════════════════════════════════════════════════════════════
			// PAGE 3 – Terms & Conditions (dynamic, userId wise)
			// Background = jaival_static_p3
			// ════════════════════════════════════════════════════════════════════

			if (req.getIsTermsCond() == 1) {
				EventTermsAndConditionEntity termsAndCondition = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report").orElse(null);

				if (termsAndCondition != null) {
					List<EventTermsAndConditionFeaturesEntity> termsList = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(termsAndCondition.getId());

					if (termsList != null && !termsList.isEmpty()) {
						bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, staticPage3);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						// Each T&C line
						for (int i = 0; i < termsList.size(); i++) {
							EventTermsAndConditionFeaturesEntity term = termsList.get(i);

							// Pick correct language description
							String description = "";
							if (lang == 1 && term.getDescriptionHindi() != null
									&& !term.getDescriptionHindi().trim().isEmpty()) {
								description = term.getDescriptionHindi();
							} else if (lang == 2 && term.getDescriptionGujarati() != null
									&& !term.getDescriptionGujarati().trim().isEmpty()) {
								description = term.getDescriptionGujarati();
							} else {
								description = term.getDescription() != null ? term.getDescription() : "";
							}

							if (!description.trim().isEmpty()) {
								document.add(new Paragraph(description).setFont(sloganFont).setFontSize(15)
										.setFontColor(headingColor).setTextAlignment(TextAlignment.LEFT)
										.simulateItalic().setMultipliedLeading(1.3f).setMarginBottom(4f));
							}
						}
					}
				}
			}
			// ════════════════════════════════════════════════════════════════════
			// PAGES 4, 5 – Static special notes pages
			// ════════════════════════════════════════════════════════════════════

//			bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, notesPage);
//			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//			document.add(new Paragraph("\u00A0").setFontSize(1));

			bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, staticPage4);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(new Paragraph("\u00A0").setFontSize(1));

			bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, staticPage5);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(new Paragraph("\u00A0").setFontSize(1));

			// ════════════════════════════════════════════════════════════════════
			// MENU CONTENT PAGES – Page 6 onwards
			// Each category = new page
			// ════════════════════════════════════════════════════════════════════
			for (EventFunctionReportResponseDto eventFunctionDto : eventDto.getFunctions()) {

				// Build catMap: menuCategoryId -> selectedItemsDto (for catImgId / bgImgId)
				List<MenuPreparationSelectedItemDetailsResponseDto> selectedItems = loadSelectedItemsForType11(
						eventFunctionDto.getFunctionId());

				Map<Long, MenuPreparationSelectedItemDetailsResponseDto> catMap = new java.util.LinkedHashMap<>();
				for (MenuPreparationSelectedItemDetailsResponseDto sel : selectedItems) {
					catMap.put(sel.getMenuCategoryId(), sel);
				}

				// ── Per-category rendering ─────────────────────────────────────
				for (MenuReportResponseDto menu : eventFunctionDto.getMenuCategories()) {

					if (menu.getNameEnglish() != null && menu.getNameEnglish().trim().equalsIgnoreCase("staff food")) {
						continue;
					}

					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}

					Long menuCatId = menu.getId();
					MenuPreparationSelectedItemDetailsResponseDto catSel = catMap.get(menuCatId);

					Long catImgId = catSel != null ? catSel.getCatImgId() : null;
					Long bgImgId = catSel != null ? catSel.getBgImgId() : null;
					Integer catSpace = (catSel != null && catSel.getCatSpace() != null) ? catSel.getCatSpace() : 0;

					// ── Optional category image full page ──────────────────────
					if (catImgId != null && catImgId > 0) {
						try {
							CatBgSelectionEntity catImgEntity = catBgSelectionRepository
									.findByIdAndIsDeleteFalse(catImgId).orElse(null);
							if (catImgEntity != null && catImgEntity.getImagePath() != null) {

//	                            String base     = "D:";
//	                            String path     = catImgEntity.getImagePath();

								String base = environment.getProperty("app.image.url");
								String path = catImgEntity.getImagePath();

								String fullPath = base.endsWith("/") ? base + path : base + "/" + path;
								System.out.println("CAT IMG URL: " + fullPath);

								ImageData catPageImg = ImageDataFactory.create(fullPath);

								// This is a single full-page image — use specific page
								bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, catPageImg);
								document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
								document.add(new Paragraph("\u00A0").setFontSize(1));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					// ── Set background for ALL pages of this category ──────────
					// Clear any previous active background first
					bgHandler.clearActiveBackground();

					if (bgImgId != null && bgImgId > 0) {
						try {
							CatBgSelectionEntity bgEntity = catBgSelectionRepository.findByIdAndIsDeleteFalse(bgImgId)
									.orElse(null);
							if (bgEntity != null && bgEntity.getImagePath() != null) {

//	                            String base     = "D:";
//	                            String path     = bgEntity.getImagePath();

								String base = environment.getProperty("app.image.url");
								String path = bgEntity.getImagePath();

								String fullPath = base.endsWith("/") ? base + path : base + "/" + path;
								System.out.println("BG IMG URL: " + fullPath);

								ImageData bgImg = ImageDataFactory.create(fullPath);

								// Apply from the NEXT page (first content page of this category)
								// and onwards — covers overflow pages automatically
								bgHandler.setActiveBackground(pdfDocument.getNumberOfPages() + 1, bgImg);
							}
						} catch (Exception e) {
							e.printStackTrace(); // default bg will apply
						}
					}
					// If no bgImgId: clearActiveBackground() already called above,
					// so defaultMenuBg applies via the handler's default logic

					// ── New page for each category ─────────────────────────────
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					if (catSpace != null && catSpace > 0) {
						// Each space unit = one line height (~20f)
						float spaceHeight = catSpace * 20f;
						document.add(
								new Paragraph("\u00A0").setFontSize(1).setMarginTop(spaceHeight).setMarginBottom(0f));
					}

					// ── Category Name ──────────────────────────────────────────
					document.add(new Paragraph(formatText(menu.getNameEnglish(), lang).toUpperCase())
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 22)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.LEFT).setUnderline().simulateItalic().setMarginTop(20f)
							.simulateBold()
							// .setMarginTop(catSpace != null && catSpace > 0 ? 0f : 20f) // skip top margin
							// if space already added
							.setMarginTop(20f).setMarginBottom(6f));

					if (subCat.trim().length() != 0) {
						document.add(new Paragraph(formatText(subCat, lang)).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 20)).setFontColor(headingColor)
								.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
								.setMarginBottom(10f));
					}

					// ── Category Instruction ───────────────────────────────────
					if (isCategoryInstruction != null && isCategoryInstruction == 1 && menu.getMenuNotes() != null
							&& !menu.getMenuNotes().isEmpty()) {
						document.add(new Paragraph(formatText(menu.getMenuNotes(), lang)).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 12)).setFontColor(headingColor)
								.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
								.setMarginBottom(4f));
					}

					// ── Category Slogan ────────────────────────────────────────
					if (isCategorySlogan != null && isCategorySlogan == 1 && menu.getSlogan() != null
							&& !menu.getSlogan().isEmpty()) {
						document.add(new Paragraph(formatText(menu.getSlogan(), lang)).setFont(sloganFont)
								.setFontSize(getFontSize(sloganFontSize, 12)).setFontColor(headingColor)
								.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
								.setMarginBottom(10f));
					}

					// ── Category Image ────────────────────────────────────────
					if (isCategoryImage == 1) {
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));
									img.setWidth(140);
									img.setHeight(100);
									img.setAutoScale(true);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									document.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					// ── Menu Items ─────────────────────────────────────────────
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String subItem = "";
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						Integer itemSpace = 0;
						if (catSel != null && catSel.getSelectedMenuPreparationItems() != null) {
							itemSpace = catSel.getSelectedMenuPreparationItems().stream()
									.filter(i -> i.getMenuItemId() != null && i.getMenuItemId().equals(item.getId()))
									.map(i -> i.getItemSpace() != null ? i.getItemSpace() : 0).findFirst().orElse(0);
						}

						// Item space (above item name) ─────────────────
						if (itemSpace != null && itemSpace > 0) {
							float spaceHeight = itemSpace * 20f;
							document.add(new Paragraph("\u00A0").setFontSize(1).setMarginTop(spaceHeight)
									.setMarginBottom(0f));
						}

						Div itemBlock = new Div();
						itemBlock.setKeepTogether(true);

						if (item.getItemHeading() != null && item.getItemHeading().trim().length() != 0) {
							itemBlock.add(new Paragraph(formatText(item.getItemHeading(), lang).toUpperCase())
									.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 22))
									.setFontColor(headingColor).setTextAlignment(TextAlignment.LEFT).setUnderline()
									.simulateItalic().setMarginTop(20f).simulateBold()
									// .setMarginTop(catSpace != null && catSpace > 0 ? 0f : 20f) // skip top margin
									// if space already added
									.setMarginTop(20f).setMarginBottom(6f));
						}

						// Item Name
						itemBlock.add(new Paragraph(formatText(item.getNameEnglish(), lang).toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(itemFontSize, 18))
								.setFontColor(headingColor).setTextAlignment(TextAlignment.LEFT).simulateItalic()
								.setFixedLeading(17f).setMarginTop(20f).setMarginBottom(2f));

						if (subItem.trim().length() != 0) {
							itemBlock.add(new Paragraph(formatText(subItem, lang)).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(headingColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
									.setMarginBottom(2f));
						}

						// Item Slogan
						if (isItemSlogan != null && isItemSlogan == 1 && item.getSlogan() != null
								&& !item.getSlogan().isEmpty()) {
							itemBlock.add(new Paragraph(formatText(item.getSlogan(), lang)).setFont(sloganFont)
									.setFontSize(getFontSize(sloganFontSize, 12)).setFontColor(headingColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
									.setMarginBottom(2f));
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().isEmpty()) {
							itemBlock.add(new Paragraph(formatText(item.getItemNotes(), lang)).setFont(catFont)
									.setFontSize(getFontSize(itemFontSize, 12)).setFontColor(headingColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
									.setMarginBottom(2f));
						}

						document.add(itemBlock);
					}
				}

				// ── After all categories of this function, clear active bg ────
				bgHandler.clearActiveBackground();
			}

			// ════════════════════════════════════════════════════════════════════
			// EXTRA CHARGES PAGE (optional)
			// ════════════════════════════════════════════════════════════════════
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {
				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					document.add(new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setMarginBottom(15f));

					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16))
								.setFontColor(headingColor).setUnderline().setTextAlignment(TextAlignment.LEFT)
								.setMarginTop(12f).setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(headingColor).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, contentColor, catFontSize, headingColor));
								}
							}
						}
						document.add(chargeTable);

						document.add(new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 13))
								.setFontColor(headingColor).setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f));
					}

					document.add(new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline());

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ════════════════════════════════════════════════════════════════════
			// LAST PAGE – Terms & Conditions (static)
			// ════════════════════════════════════════════════════════════════════
			bgHandler.setPageBackground(lastPageNum, lastBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(new Paragraph("\u00A0").setFontSize(1));

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType12(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {

		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", customMenu = "", l1 = "", pckPrice = "", price = "", remarks = "",
					billingNameLabel = "", serviceLabel = "", themeLabel = "", forLabel = "";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
//				itemFont = boldFont;
				System.out.println("Hindi font loaded successfully");
				customMenu = "विशेष मेनू";
				eDate = "दिनांक";
				venueLabel = "कार्यक्रम स्थल";
				phone = "संपर्क जानकारी";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन संबंधी निर्देश";
				l1 = "बजे से";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				eName = "फॉर द जॉयस ओकेज़न ऑफ";
				forLabel = "के लिए";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					// Tamil
					customMenu = "சிறப்பு மெனு";
					eDate = "நிகழ்ச்சி தேதி";
					venueLabel = "நிகழ்ச்சி இடம்";
					phone = "தொடர்பு தகவல்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு தொடர்பான வழிமுறைகள்";
					l1 = "மணி முதல்";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					eName = "ஃபார் த ஜாயஸ் ஒக்கேஷன் ஆஃப்";
					forLabel = "க்காக";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					// Telugu
					customMenu = "ప్రత్యేక మెనూ";
					eDate = "కార్యక్రమ తేదీ";
					venueLabel = "కార్యక్రమ స్థలం";
					phone = "సంప్రదింపు సమాచారం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన సంబంధిత సూచనలు";
					l1 = "గంటల నుండి";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					eName = "ఫర్ ది జాయస్ అకేషన్ ఆఫ్";
					forLabel = "కోసం";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					customMenu = "പ്രത്യേക മെനു";
					eDate = "പരിപാടിയുടെ തീയതി";
					venueLabel = "പരിപാടി സ്ഥലം";
					phone = "ബന്ധപ്പെടാനുള്ള വിവരം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണവുമായി ബന്ധപ്പെട്ട നിർദേശങ്ങൾ";
					l1 = "മണി മുതൽ";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					eName = "ഫോർ ദ ജോയസ് ഒക്കേഷൻ ഓഫ്";
					forLabel = "വേണ്ടി";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					customMenu = "विशेष मेनू";
					eDate = "दिनांक";
					venueLabel = "कार्यक्रम स्थळ";
					phone = "संपर्क माहिती";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजनासंबंधी सूचना";
					l1 = "वाजल्यापासून";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					eName = "फॉर द जॉयस ओकेजन ऑफ";
					forLabel = "साठी";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					customMenu = "વિશેષ મેનુ";
					eDate = "કાર્યક્રમની તારીખ";
					venueLabel = "કાર્યક્રમ સ્થળ";
					phone = "સંપર્ક માહિતી";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન સંબંધિત સૂચનાઓ";
					l1 = "વાગ્યાથી";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					eName = "ફોર ધ જોયસ ઓકેઝન ઑફ";
					forLabel = "માટે";
				}
//				itemFont = boldFont;
				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				customMenu = "Customized Menu";
				eDate = "Date of the Event";
				venueLabel = "Venue";
				personLabel = "Guest";
				phone = "Contact Information";
				eTime = "Timing";
				fNote = "Food Preparation Notes";
				l1 = "Onwards";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				eName = "For the joyous occasion of";
				forLabel = "For";
				catFont = getFont(req.getCatFontId(), false, "palatino");
				catFontBold = getFont(req.getCatFontId(), true, "palatino");

				itemFont = getFont(req.getItemFontId(), false, "palatino");
				itemFontBold = getFont(req.getItemFontId(), true, "palatino");

				sloganFont = getFont(req.getSloganFontId(), false, "palatino");
				sloganFontBold = getFont(req.getSloganFontId(), true, "palatino");

//				itemFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

				System.out.println("English font loaded successfully");
			}

			// ── Colors from template ───────────────────────────────────────────
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

			Color headingColor = new DeviceRgb(r1, g1, b1); // category name, item name
			Color contentColor = new DeviceRgb(r2, g2, b2); // slogan, instructions, values
			Color descriptionFontColor = new DeviceRgb(r3, g3, b3);
			// ── Fetch event data ───────────────────────────────────────────────
			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);
			if (eventDto == null)
				return "";

			// ── Output file setup ──────────────────────────────────────────────
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!outputPath.exists())
				outputPath.mkdirs();

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			// text area in background image
			document.setMargins(150, 35, 100, 55);

			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData defaultMenuBg = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/Bhandari_1.jpeg");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/Bhandari_2.jpeg");
//			ImageData defaultMenuBg = loadImageFromResource("/flipbook/pages/Bhandari_3.jpeg");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/Bhandari_4.jpeg");

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(1, mainBgData);
			}
			bgHandler.setDefaultBackground(defaultMenuBg);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			String billingName = "";
			String foodNotes = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(contentColor)
					.setFontSize(1);
			document.add(invisibleContent);

			if (isCompanyDetail == 1) {
				if (userId != 501) {
					bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, detailsPage);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					document.add(new Paragraph("\u00A0").setFontSize(1));
				} else {
					bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					Paragraph p = new Paragraph().add(new Text("To,").simulateBold())
							.add(new Text("\n" + eventDto.getPartyName()).simulateBold())
							.add(new Text("\n" + eventDto.getPartyAddress()).simulateBold())
							.add(new Text(
									"\n\nThank you for choosing “Creative Cuisines India” as your catering partner for the upcoming function scheduled for 13th August, 2026. Following our discussion, we are delighted to present the menu options for your consideration."))
							.add(new Text("\n\nThe suggestive menu is designed to accommodate a minimum of "
									+ eventDto.getPax() + " guests."))
							.add(new Text("\n\n*Package Rate Rs. 2800 per head."))
							.add(new Text("\n*3% Service charge will be charged extra."
									+ "\n*Transportation charges will be charged extra."
									+ "\n*Govt. Taxes as applicable."))
							.add(new Text(
									"\n\nExtra Pax will be charged separately for the respective function. Final Billing will be done on basis of actual guest count during the party or minimum guarantee whichever is higher."))
							.add(new Text(
									"\n\nWe shall entertain 15% over and above to the guaranteed pax. We are not liable for food shortage and Service standards if pax increased above 15% of guaranteed pax."))
							.add(new Text(
									"\n\nMinimum 30% advance should be paid on the guaranteed number of persons at the time of booking the function. The advance is not returnable in case of booking is cancelled and the total 100% amount would be required before the function."))
							.add(new Text("\n\nElectricity connection for Buffet & Kitchen will be in your scope."))
							.add(new Text("\n\n\n\nFor furthermore details please contact us"))
							.add(new Text("\n\nWith warm regards"))
							.add(new Text("\nCreative Cuisines India").simulateBold()).setFont(catFont).setFontSize(10)
							.setFontColor(headingColor).setTextAlignment(TextAlignment.LEFT).setMarginTop(1f)
							.setMarginBottom(1f);

					document.add(p);
				}
			}

			bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() != null ? eventDto.getEventStartTimestamp() : "";
			String eventName = eventDto.getEventName() != null ? eventDto.getEventName() : "";
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			String Food = eventDto.getFoodType() != null ? eventDto.getFoodType() : "";
			String foodHeading = foodNotes.isEmpty() ? Food : Food + " (" + foodNotes + ")";
			String remark = eventDto.getRemark();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String formattedHost = toTitleCase(safeText(hostName));
			String formattedEventName = toTitleCase(safeText(eventName));
			String quotedHost = "\"" + formattedHost + "\"";

			document.add(new Paragraph(quotedHost).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 24))
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setFixedLeading(24f).setMarginTop(30f).setMarginBottom(25f));

			document.add(new Paragraph(eDate).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			document.add(new Paragraph(safeText(eventDate)).setFont(catFontBold).setFontSize(21)
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));

			document.add(new Paragraph(eName).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			document.add(new Paragraph(safeText(formattedEventName)).setFont(catFontBold).setFontSize(21)
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			if (!safeText(venue).isEmpty()) {
				document.add(new Paragraph(venueLabel).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
						.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
						.setMarginTop(10f));

				document.add(new Paragraph(safeText(venue)).setFont(catFontBold).setFontSize(21)
						.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
						.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			}
			document.add(new Paragraph(fNote).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			document.add(new Paragraph(safeText(foodHeading)).setFont(catFontBold).setFontSize(21)
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			if (!safeText(theme).trim().isEmpty()) {
				document.add(new Paragraph(themeLabel).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
						.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
						.setMarginTop(10f));

				document.add(new Paragraph(safeText(theme)).setFont(catFontBold).setFontSize(21)
						.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
						.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			}
			if (!safeText(service).trim().isEmpty()) {
				document.add(new Paragraph(serviceLabel).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
						.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
						.setMarginTop(10f));

				document.add(new Paragraph(safeText(service)).setFont(catFontBold).setFontSize(21)
						.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
						.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			}
			for (EventFunctionReportResponseDto eventFunctionDto : eventDto.getFunctions()) {
				bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				// Function name label
				document.add(new Paragraph(safeText(eventFunctionDto.getFunctionName())).setFont(catFontBold)
						.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
						.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1).setMarginTop(20f));

				String paxStr = eventFunctionDto.getPax() != null
						? forLabel + " " + eventFunctionDto.getPax() + " " + personLabel
						: "";
				String foodType = eventFunctionDto.getFoodType() != null ? " (" + eventFunctionDto.getFoodType() + ")"
						: "";
				document.add(new Paragraph(paxStr + foodType).setFont(catFontBold)
						.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
						.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1));

				if (req.getIsWithPrice() == 1) {
					String label;
					String value;

					if (eventFunctionDto.getIsPackage()) {
						label = pckPrice;
						value = eventFunctionDto.getPackagePrice().toString();
					} else {
						label = price;
						value = eventFunctionDto.getRate().toString();
					}

					document.add(new Paragraph(label + " " + value).setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
							.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1));
				}

				if (eventFunctionDto.getFunctionStartTimestamp() != null) {
					String[] tParts = eventFunctionDto.getFunctionStartTimestamp().split(" ");
					if (tParts.length >= 3) {
						String timeStr = tParts[0] + " " + tParts[1] + " " + tParts[2] + " " + l1;
						document.add(new Paragraph(timeStr).setFont(sloganFont)
								.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
								.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
								.setMarginBottom(3f));
					}
				}

				List<MenuPreparationSelectedItemDetailsResponseDto> selectedItems = loadSelectedItemsForType11(
						eventFunctionDto.getFunctionId());

				Map<Long, MenuPreparationSelectedItemDetailsResponseDto> catMap = new java.util.LinkedHashMap<>();
				for (MenuPreparationSelectedItemDetailsResponseDto sel : selectedItems) {
					catMap.put(sel.getMenuCategoryId(), sel);
				}

				for (MenuReportResponseDto menu : eventFunctionDto.getMenuCategories()) {

					if (menu.getNameEnglish() != null && menu.getNameEnglish().trim().equalsIgnoreCase("staff food")) {
						continue;
					}

					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}

					Div catBlock = new Div();
					catBlock.setMarginTop(10f);
					catBlock.setMarginBottom(5f);

					int catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;

					for (int i = 0; i < catSpace; i++) {
						catBlock.add(new Paragraph("\n"));
					}

					catBlock.add(new Paragraph(formatText(menu.getNameEnglish(), lang).toUpperCase())
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.LEFT).setUnderline().simulateItalic().setMarginTop(1f)
							.setMarginBottom(1f));

					if (subCat != null && !subCat.trim().isEmpty()) {
						catBlock.add(new Paragraph(formatText(subCat, lang).toUpperCase()).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor)
								.setTextAlignment(TextAlignment.LEFT).setUnderline().simulateItalic().setMarginTop(1f)
								.setMarginBottom(1f));
					}

					if (isCategoryInstruction != null && isCategoryInstruction == 1 && menu.getMenuNotes() != null
							&& !menu.getMenuNotes().isEmpty()) {

						catBlock.add(new Paragraph(formatText(menu.getMenuNotes(), lang)).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 10)).setFontColor(descriptionFontColor)
								.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
								.setMarginTop(1f).setMarginBottom(1f));
					}

					if (isCategorySlogan != null && isCategorySlogan == 1 && menu.getSlogan() != null
							&& !menu.getSlogan().isEmpty()) {

						catBlock.add(new Paragraph(formatText(menu.getSlogan(), lang)).setFont(sloganFont)
								.setFontSize(getFontSize(sloganFontSize, 10)).setFontColor(descriptionFontColor)
								.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
								.setMarginTop(1f).setMarginBottom(1f));
					}

					if (isCategoryImage != null && isCategoryImage == 1 && menu.getImagePath() != null
							&& !menu.getImagePath().trim().isEmpty()) {

						try {
							System.out.println("image path : " + menu.getImagePath());
							Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

							img.setAutoScale(true);
							img.setHorizontalAlignment(HorizontalAlignment.CENTER);

							catBlock.add(img);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					float categoryHeight = 140f;
					float firstItemHeight = 70f;

					if (!menu.getMenuItems().isEmpty()) {
						categoryHeight += firstItemHeight;
					}

					if (shouldMoveCategoryToNextPage(document, categoryHeight)) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					}

					document.add(catBlock);

					boolean firstItem = true;

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						String subItem = "";

						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						Div itemBlock = new Div();

						Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;

						for (int i = 0; i < itemSpace; i++) {
							itemBlock.add(new Paragraph("\n"));
						}

						boolean movedToNewPage = false;

						if (!firstItem) {

							float itemHeight = 70f;

							if (shouldMoveCategoryToNextPage(document, itemHeight)) {

								document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

								movedToNewPage = true;
							}
						}

						if (movedToNewPage) {
							itemBlock.setMarginTop(15f);
						}

						itemBlock.setMarginBottom(1f);

						itemBlock.add(new Paragraph(formatText(item.getNameEnglish(), lang).toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(itemFontSize, 12))
								.setFontColor(contentColor).setTextAlignment(TextAlignment.LEFT).simulateItalic()
								.setFixedLeading(17f).setMarginTop(1f).setMarginBottom(1f));

						if (subItem != null && !subItem.isEmpty()) {

							itemBlock.add(new Paragraph(formatText(subItem, lang).toUpperCase()).setFont(catFontBold)
									.setFontSize(getFontSize(itemFontSize, 12)).setFontColor(contentColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setFixedLeading(17f)
									.setMarginTop(1f).setMarginBottom(1f));
						}

						if (isItemSlogan != null && isItemSlogan == 1 && item.getSlogan() != null
								&& !item.getSlogan().isEmpty()) {

							itemBlock.add(new Paragraph(formatText(item.getSlogan(), lang)).setFont(sloganFont)
									.setFontSize(getFontSize(sloganFontSize, 10)).setFontColor(descriptionFontColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
									.setMarginTop(1f).setMarginBottom(1f));
						}

						if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().isEmpty()) {

							itemBlock.add(new Paragraph(formatText(item.getItemNotes(), lang)).setFont(catFont)
									.setFontSize(getFontSize(itemFontSize, 10)).setFontColor(descriptionFontColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
									.setMarginTop(1f).setMarginBottom(1f));
						}

						document.add(itemBlock);

						firstItem = false;
					}
				}

				if (req.getIsAddDecoration() == 1) {
					List<DecoreReportResponseDto> decoreReportResponseDtos = eventFunctionDto.getDecoreCategories();

					if (decoreReportResponseDtos != null) {
//						if (isAddMenu == 0) {
						bgHandler.setDefaultBackground(defaultMenuBg);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//						}
						document.add(new Paragraph("Decoration Details:").setFont(catFont).setFontSize(22f)
								.simulateBold().setUnderline());

						for (DecoreReportResponseDto decore : decoreReportResponseDtos) {
							Div decoreContent = new Div();
							decoreContent.setKeepTogether(false);
							String text = "";
							for (DecoreItemReportResponseDto item : decore.getDecoreItems()) {
								List<DecoreMainCategoryItemImagesMasterEntity> imagesMasterEntities = decoreMainCategoryItemImagesMasterRepository
										.findAllByDecoreItem_IdAndIsDeleteFalse(item.getId());
								Integer itemQty = item.getItemQty();
								Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
								BigDecimal itemPrice = item.getPrice();

								String subItem = "";
								if (lang == 1) {
									subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
								} else if (lang == 2) {
									subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
								} else {
									subItem = item.getSubItem() != null ? item.getSubItem() : "";
								}

								BigDecimal price1 = itemPrice != null ? itemPrice : BigDecimal.ZERO;
								int qty = itemQty != null ? itemQty : 0;
								BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));

								String postLabel = "";
								if (qty > 1) {
									postLabel = " /- + Tax Each";
								} else {
									postLabel = " /- + Tax";
								}
								String text1 = "\u2022 " + formatText(item.getNameEnglish(), lang)
										+ (subItem.trim().length() != 0 ? " " + subItem : "")
										+ (totalAmount.compareTo(BigDecimal.ZERO) != 0
												? " @RS." + totalAmount.intValue() + postLabel
												: "");

								decoreContent.add(new Paragraph(text1).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 19)).setFixedLeading(20f)
										.setTextAlignment(TextAlignment.LEFT).setFontColor(contentColor)
										.setMarginLeft(20f).setMarginTop(15f));

								// Item Instructions
								if (isItemInstruction != null && isItemInstruction == 1) {
									if (item.getDecoreItemNotes() != null && !item.getDecoreItemNotes().isEmpty()) {
										text = formatText(item.getDecoreItemNotes(), lang);
										decoreContent.add(new Paragraph(text).setFont(itemFont)
												.setFontSize(getFontSize(itemFontSize, 19)).setMultipliedLeading(1.2f)
												.setFontColor(descriptionFontColor).setMarginLeft(0f).setMarginTop(0)
												.setTextAlignment(TextAlignment.LEFT).setMarginBottom(0).setPadding(0f)
												.setPaddingLeft(130f));
									}
								}

								if (imagesMasterEntities != null && !imagesMasterEntities.isEmpty()) {
									try {
										Image img = new Image(ImageDataFactory.create(
												environment.getProperty("app.image.url") + decore.getImagePath()));
//										Image img = new Image(loadImageFromResource("/flipbook/pages/logo.png"));

										img.setAutoScale(false);
										img.scaleToFit(300, 200);
										img.setHorizontalAlignment(HorizontalAlignment.CENTER);
										img.setMarginTop(5);
										img.setMarginBottom(5);

										decoreContent.add(img);
									} catch (Exception e) {
										// Skip if image loading fails
									}
								}

								for (int i = 0; i < itemSpace; i++) {
									decoreContent.add(new Paragraph("\n"));
								}
							}

							document.add(decoreContent);
						}
					}
				}

				// ── After all categories of this function, clear active bg ────
				bgHandler.clearActiveBackground();
			}

			// ════════════════════════════════════════════════════════════════════
			// EXTRA CHARGES PAGE (optional)
			// ════════════════════════════════════════════════════════════════════
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {
				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					document.add(new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setMarginBottom(15f));

					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16))
								.setFontColor(headingColor).setUnderline().setTextAlignment(TextAlignment.LEFT)
								.setMarginTop(12f).setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(headingColor).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, contentColor, catFontSize, headingColor));
								}
							}
						}
						document.add(chargeTable);

						document.add(new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 13))
								.setFontColor(headingColor).setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f));
					}

					document.add(new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline());

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			if (req.getIsNotes() == 1 && tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (req.getIsNotes() == 1 && tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			// ════════════════════════════════════════════════════════════════════
			// LAST PAGE – Terms & Conditions (static)
			// ════════════════════════════════════════════════════════════════════
			bgHandler.setPageBackground(lastPageNum, lastBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(new Paragraph("\u00A0").setFontSize(1));

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(headingColor);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(95f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(contentColor);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private boolean shouldMoveCategoryToNextPage(Document document, float requiredHeight) {

		try {
			float remainingHeight = document.getRenderer().getCurrentArea().getBBox().getHeight();

			return remainingHeight < requiredHeight;
		} catch (Exception e) {
			return false;
		}
	}

	private String toTitleCase(String input) {
		if (input == null || input.isEmpty())
			return input;

		String[] words = input.toLowerCase().split("\\s+");
		StringBuilder result = new StringBuilder();

		for (String word : words) {
			if (word.length() > 0) {
				result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
			}
		}

		return result.toString().trim();
	}

	// ── Helper: add label:value row to function header table ──────────────────
	private void addHeaderRow(Table table, String label, String value, PdfFont labelFont, PdfFont valueFont,
			Integer labelFontSize, Integer valueFontSize, Color color) {

		Paragraph lp = new Paragraph().add(
				new Text(label).setFont(labelFont).setFontSize(getFontSize(labelFontSize, 16)).setFontColor(color));
		table.addCell(new Cell().add(lp).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

		table.addCell(
				new Cell()
						.add(new Paragraph(":").setFont(labelFont).setFontSize(getFontSize(labelFontSize, 16))
								.setFontColor(color))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

		table.addCell(new Cell().add(
				new Paragraph(value).setFont(valueFont).setFontSize(getFontSize(valueFontSize, 16)).setFontColor(color))
				.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
	}

	// ── Helper: load selected items with catImgId/bgImgId for type11 ──────────
	private List<MenuPreparationSelectedItemDetailsResponseDto> loadSelectedItemsForType11(Long eventFunctionId) {
		EventFunctionMasterEntity eventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(eventFunctionId).orElse(null);
		if (eventFunction == null)
			return Collections.emptyList();

		MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
				.findByEventFunctionAndIsDeleteFalse(eventFunction);

		return loadSelectedItems(eventFunctionId, menuPreparationEntity, eventFunction);
	}

	@Override
	public String generateExclusiveReportType3(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate) {

		try {
			PdfFont basicFont = null;
			loadLicense();

			int x = 185;
			int y = 695;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 690;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = loadFont("/fonts/times.ttf");
				System.out.println("English font loaded successfully");
			}

			PdfFont basicFont2 = loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color softGold = new DeviceRgb(r2, g2, b2);
			Color creamGold = new DeviceRgb(r1, g1, b1);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

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

			File pdfFile = new File(outputPath + "/"
					+ getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(10, 35, 140, 40);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/krishna_front_2.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/krishna_details_2.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/krishna_menu_2.png");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/krishna_last_2.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// ============ PAGE 1: Front Page (Main) ============
			// Add invisible content to ensure page 1 exists
			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(basicFont).setFontColor(creamGold);
			invisibleContent.setFontSize(35);
			invisibleContent.setPaddingTop(140f);
			invisibleContent.setPaddingLeft(165f);
			document.add(invisibleContent);

			int secondPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(secondPageNum, detailsPage);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = eventDto.getFoodNotes();
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			// ============ PAGE 1: Front Page (Main) ============

			// Party Name
			Paragraph dataContent = new Paragraph("Honourable Host")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(softGold)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y, pageWidth); // x, y,
																												// width
			document.add(dataContent);

			// Party Name
			Paragraph hostNameContent = new Paragraph(safeText(hostName)).setFont(basicFont)
					.setFontColor(ColorConstants.WHITE).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 30, pageWidth); // x,
																// y,
																// width
			document.add(hostNameContent);

			dataContent = new Paragraph("Mobile").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(softGold).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			// Mobile No.
			Paragraph mobileNoContent = new Paragraph(safeText(mobileNo)).setFont(basicFont)
					.setFontColor(ColorConstants.WHITE).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 30, pageWidth);
			document.add(mobileNoContent);

			dataContent = new Paragraph("Event Date").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(softGold).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			// Event Date
			Paragraph eventDateContent = new Paragraph(safeText(eventStartDate + " - " + eventEndDate))
					.setFont(basicFont).setFontColor(ColorConstants.WHITE).setFontSize(18f)
					.setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
			document.add(eventDateContent);

			// Event Name
			dataContent = new Paragraph("Type of Event")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(softGold)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 55, pageWidth); // x,
																														// y,
																														// width
			document.add(dataContent);

			Paragraph eventNameContent = new Paragraph(safeText(eventName)).setFont(basicFont)
					.setFontColor(ColorConstants.WHITE).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 30, pageWidth);
			document.add(eventNameContent);

			// Pax
//			dataContent = new Paragraph("Person").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
//					.setFontColor(softGold).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
//					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
//			document.add(dataContent);
//
//			Paragraph paxContent = new Paragraph(safeText(pax)).setFont(basicFont).setFontColor(ColorConstants.WHITE)
//					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
//			document.add(paxContent);

			// Venue
			dataContent = new Paragraph("Venue").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(softGold).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			Paragraph venueContent = new Paragraph(safeText(venue)).setFont(basicFont)
					.setFontColor(ColorConstants.WHITE).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 30, pageWidth);
			document.add(venueContent);

			// Food
			dataContent = new Paragraph("Food Preparations")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(softGold)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 55, pageWidth); // x,
																														// y,
																														// width
			document.add(dataContent);

			Paragraph foodContent = new Paragraph(safeText(foodNotesName.toString())).setFont(basicFont)
					.setFontColor(ColorConstants.WHITE).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 30, pageWidth);
			document.add(foodContent);

//			Paragraph cmpNameContent = new Paragraph(cmpName.toString())
//				    .setFont(basicFont)
//				    .setFontColor(softGold)
//				    .setFontSize(18f)
//				    .setTextAlignment(TextAlignment.CENTER)
//				    .setFixedPosition(0, y-=70, pageWidth);
//				document.add(cmpNameContent);

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(softGold);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 50f, 50f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorderTop(new SolidBorder(softGold, 1f));
				headerTable.setBorderBottom(new SolidBorder(softGold, 1f));
				headerTable.setMarginTop(5f);

				// === ROW 1: Event | Pax ===
				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				Paragraph eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text("Event : ").setFont(basicFont).setFontColor(softGold))
						.add(new com.itextpdf.layout.element.Text(functionTitle).setFont(basicFont)
								.setFontColor(softGold));
				Cell eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);

				String dish = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				Paragraph dishPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text("Pax : ").setFont(basicFont).setFontColor(softGold))
						.add(new com.itextpdf.layout.element.Text(dish).setFont(basicFont).setFontColor(softGold));
				Cell dishCell = new Cell().add(dishPara);
				dishCell.setBorder(Border.NO_BORDER);
				dishCell.setTextAlignment(TextAlignment.LEFT);

				headerTable.addCell(eventCell);
				headerTable.addCell(dishCell);

				// === ROW 2: Date | Time ===
				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				Paragraph datePara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text("Date : ").setFont(basicFont).setFontColor(softGold))
						.add(new com.itextpdf.layout.element.Text(date).setFont(basicFont).setFontColor(softGold));
				Cell dateCell = new Cell().add(datePara);
				dateCell.setBorder(Border.NO_BORDER);
				dateCell.setPaddingTop(5f);
				dateCell.setTextAlignment(TextAlignment.LEFT);

				Paragraph timePara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text("Time : ").setFont(basicFont).setFontColor(softGold))
						.add(new com.itextpdf.layout.element.Text(time).setFont(basicFont).setFontColor(softGold));
				Cell timeCell = new Cell().add(timePara);
				timeCell.setBorder(Border.NO_BORDER);
				timeCell.setPaddingTop(5f);
				timeCell.setTextAlignment(TextAlignment.LEFT);

				headerTable.addCell(dateCell);
				headerTable.addCell(timeCell);

				// === ROW 4: Address (spans all columns) ===
				String funcvenue = eventFunctionMasterResponseDto.getFunctionVenue();
				Paragraph addressPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text("Address : ").setFont(basicFont)
								.setFontColor(softGold))
						.add(new com.itextpdf.layout.element.Text(funcvenue).setFont(basicFont).setFontColor(softGold));
				Cell addressCell = new Cell(1, 2).add(addressPara);
				addressCell.setBorder(Border.NO_BORDER);
				addressCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(addressCell);

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Check if there's menu data to display
				boolean hasMenuData = menuReportResponseDtos != null && !menuReportResponseDtos.isEmpty();

				// Set up 2-column layout
				PageSize pageSize = pdfDocument.getDefaultPageSize();
				IRenderer renderer = headerTable.createRendererSubTree();
				renderer.setParent(document.getRenderer());

				LayoutResult layoutResult = renderer.layout(new LayoutContext(new LayoutArea(0,
						new Rectangle(document.getLeftMargin(), document.getBottomMargin(),
								pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin(),
								pageSize.getHeight()))));

				float headerHeight = layoutResult.getOccupiedArea().getBBox().getHeight();
				float bottomPadding = 100f;
				float gap = 20f;

				float columnWidth = (pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin() - gap)
						/ 2;

				float columnTopY = pageSize.getHeight() - headerHeight;

				float columnHeight = columnTopY - bottomPadding;

				// Adjust column height based on whether menu data exists
				float adjustedColumnHeight = hasMenuData ? (columnHeight - 10f) : columnHeight;

				System.out.println("hasMenuData : " + hasMenuData);

				Rectangle leftColumn = new Rectangle(document.getLeftMargin(), bottomPadding, columnWidth,
						adjustedColumnHeight);

				Rectangle rightColumn = new Rectangle(document.getLeftMargin() + columnWidth + gap, bottomPadding,
						columnWidth, adjustedColumnHeight);

				// Only set up columns and add menu content if there's data
				if (hasMenuData) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// Set the column renderer for menu content
					document.setRenderer(
							new ColumnDocumentRenderer(document, new Rectangle[] { leftColumn, rightColumn }));

					// Add menu content
					for (MenuReportResponseDto menu : menuReportResponseDtos) {

						Div menuContent = new Div();

						Paragraph p = new Paragraph(formatText(menu.getNameEnglish(), lang)).setFont(basicFont)
								.setFontSize(14).setFixedLeading(15f).setFontColor(ColorConstants.WHITE)
								.setPaddingLeft(10).setPaddingRight(14);

						Table table = new Table(1);
						table.setBorder(Border.NO_BORDER);
						table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

						menuContent.add(table).setMarginTop(8f);

						// Category Slogan
						if (isCategorySlogan != null && isCategorySlogan == 1) {
							if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
								menuContent.add(new Paragraph(formatText(menu.getSlogan(), lang)).setFont(basicFont)
										.setFontSize(8.5f).setMultipliedLeading(1.18f).setMarginBottom(2f)
										.setFontColor(softGold).setMarginLeft(6));
							}
						}

						// Category Instructions
						if (isCategoryInstruction != null && isCategoryInstruction == 1) {
							if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
								menuContent.add(new Paragraph(formatText(menu.getMenuNotes(), lang)).setFont(basicFont)
										.setFontSize(8.5f).setMultipliedLeading(1.18f).setFontColor(softGold)
										.setMarginLeft(6));
							}
						}

						for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

							menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(basicFont)
									.setFontSize(11f).setFixedLeading(12f).setFontColor(softGold).setMarginLeft(12));

							if (isItemSlogan != null && isItemSlogan == 1) {
								if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
									menuContent.add(new Paragraph(formatText(item.getSlogan(), lang)).setFont(basicFont)
											.setFontSize(8).setMultipliedLeading(1.2f).setFontColor(softGold)
											.setMarginLeft(18).setMarginTop(-4f).setMarginBottom(2f).setPadding(0f));
								}
							}

							// Item Instructions
							if (isItemInstruction != null && isItemInstruction == 1) {
								if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
									menuContent.add(new Paragraph(formatText(item.getItemNotes(), lang))
											.setFont(basicFont).setFontSize(8).setMultipliedLeading(1.2f)
											.setFontColor(softGold).setMarginLeft(18).setMarginTop(-4f)
											.setMarginBottom(2f).setPadding(0f));
								}
							}

						}

						document.add(menuContent);
					}

					// Reset renderer after menu content for next function
					document.setRenderer(new DocumentRenderer(document));
				}
			}

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ LAST PAGE ============
			// Set the background for the last page BEFORE creating it
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(lastPageNum, lastBgData);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType2(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate) {
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

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			// Output formats
			DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

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

			Color headingColor = new DeviceRgb(r1, g1, b1);
			Color contentColor = new DeviceRgb(r2, g2, b2);

			Style eventStyle = new Style().setFont(basicFont2).setFontSize(18).setFontColor(headingColor);
			Style funNameStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(headingColor);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();

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

			File pdfFile = new File(outputPath + "/"
					+ getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			// document.setMargins(5, 120, 110, 125);
			String baseUrl = environment.getProperty("app.image.url");
			String firstPage = baseUrl + adminTemplate.getTemplateMaster().getFrontPage().replace("null", "");
			String secondPage = baseUrl + adminTemplate.getTemplateMaster().getSecondFrontPage().replace("null", "");
			String watermark = baseUrl + adminTemplate.getTemplateMaster().getWatermark().replace("null", "");
			String lastPage = baseUrl + adminTemplate.getTemplateMaster().getLastMainPage().replace("null", "");

//			 Load all background images upfront
			ImageData mainBgData = loadImageFromResource(firstPage);
			ImageData secondBgData = loadImageFromResource(secondPage);
			ImageData watermarkBgData = loadImageFromResource(watermark);
			ImageData lastBgData = loadImageFromResource(lastPage);

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			bgHandler.setPageBackground(2, secondBgData); // Page 2: Detail
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// ============ PAGE 1: Front Page (Main) ============
			// Add invisible content to ensure page 1 exists
			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

			Style emailHeadingStyle = new Style().setFont(boldFont).setFontColor(headingColor);

			Paragraph invisibleContent = new Paragraph("\u00A0"); // Non-breaking space

			Paragraph partyName = new Paragraph(eventDto.getPartyName()).addStyle(emailHeadingStyle).setFontSize(50)
					.setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, PageSize.A4.getHeight() * (35f / 100f), PageSize.A4.getWidth());

			invisibleContent.setFontSize(10);
			document.add(invisibleContent);
			document.add(partyName);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ PAGE 2: Title Page (Detail) ============
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ PAGE 3: Event Information ============

			Style funHeaderLabelStyle = new Style().setFont(boldFont).setFontColor(headingColor);

			Style funHeaderValueStyle = new Style().setFontColor(headingColor);

			Cell cell;

			Table eventInfoTable = new Table(1);
			eventInfoTable.setWidth(UnitValue.createPercentValue(100));
			eventInfoTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
			eventInfoTable.setMarginTop(40f);
			eventInfoTable.setBorder(Border.NO_BORDER);

			// Menu Proposed On
			cell = new Cell().add(new Paragraph("Menu Proposed On").addStyle(funHeaderLabelStyle).setFontSize(20f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(20f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			cell = new Cell().add(new Paragraph(eventStartDate + " - " + eventEndDate).addStyle(funHeaderValueStyle)
					.setFontSize(18f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Host Name
			cell = new Cell().add(new Paragraph("Party Name").addStyle(funHeaderLabelStyle).setFontSize(20f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(18f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			cell = new Cell().add(new Paragraph(hostName).addStyle(funHeaderValueStyle).setFontSize(18f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Mobile No
			cell = new Cell().add(new Paragraph("Mobile No").addStyle(funHeaderLabelStyle).setFontSize(20f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(18f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			cell = new Cell().add(new Paragraph(mobileNo).addStyle(funHeaderValueStyle).setFontSize(18f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Event Date
			cell = new Cell().add(new Paragraph("Event Date").addStyle(funHeaderLabelStyle).setFontSize(20f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(18f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			cell = new Cell().add(new Paragraph(safeText(eventDate)).addStyle(funHeaderValueStyle).setFontSize(18f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Event Type
			cell = new Cell().add(new Paragraph("Type of Event").addStyle(funHeaderLabelStyle).setFontSize(20f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(18f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String eventType = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			cell = new Cell().add(new Paragraph(eventType).addStyle(funHeaderValueStyle).setFontSize(18f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Venue
			cell = new Cell().add(new Paragraph("Venue").addStyle(funHeaderLabelStyle).setFontSize(20f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(18f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			cell = new Cell().add(new Paragraph(venue).addStyle(funHeaderValueStyle).setFontSize(18f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Food Preparations
			cell = new Cell().add(new Paragraph("Food Preparations").addStyle(funHeaderLabelStyle).setFontSize(20f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(18f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String foodNotesName = eventDto.getFoodType();
			String cuisineType = (foodNotesName != null && !foodNotesName.trim().isEmpty()) ? foodNotesName
					: "Not Specified";
			cell = new Cell().add(new Paragraph(cuisineType).addStyle(funHeaderValueStyle).setFontSize(18f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Special Note (if exists)
			String foodNotes = eventDto.getFoodNotes();
			if (foodNotes != null && !foodNotes.trim().isEmpty()) {
				cell = new Cell().add(new Paragraph("Special Note").addStyle(funHeaderLabelStyle).setFontSize(20f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(18f);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);

				cell = new Cell().add(new Paragraph(foodNotes).addStyle(funHeaderValueStyle).setFontSize(18f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingBottom(5f);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);
			}

			document.add(eventInfoTable);

			// ============ MENU CONTENT PAGES ============
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			LineSeparator line = null;

			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				document.setMargins(70, 30, 50, 30);

				Table menuTable = new Table(UnitValue.createPercentArray(new float[] { 25, 25, 25, 25 }), true);
				menuTable.setMargin(0);
				menuTable.setWidth(UnitValue.createPercentValue(100));
				menuTable.setKeepTogether(true);
				menuTable.setBorder(Border.NO_BORDER);

				document.setRenderer(new DocumentRenderer(document));

				SolidLine solidLine = new SolidLine(6);
				solidLine.setColor(headingColor);

				line = new LineSeparator(solidLine);
				line.setWidth(UnitValue.createPercentValue(100));

				cell = new Cell(1, 4).add(line).setBorder(Border.NO_BORDER);
				menuTable.addCell(cell);

				Table funcTable = new Table(UnitValue.createPercentArray(new float[] { 12, 20, 2, 12, 20, 2, 12, 20 }),
						true);

//				cell = new Cell(1, 4).add(funcTable.addStyle(funNameStyle));

				// Function Header

				// function details
				String funTitle = formatText("Event : ", lang);
				cell = new Cell().add(new Paragraph(funTitle).addStyle(funHeaderLabelStyle));
				cell.setTextAlignment(TextAlignment.LEFT);
				cell.setBorder(Border.NO_BORDER);
				cell.setPadding(2.5f);
				funcTable.addCell(cell);

//				function value
				String funVal = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				cell = new Cell().add(new Paragraph(funVal).addStyle(funHeaderValueStyle));
				cell.setTextAlignment(TextAlignment.LEFT);
				cell.setBorder(Border.NO_BORDER);
				cell.setPadding(2.5f);
				funcTable.addCell(cell);

				cell = new Cell();
				cell.setBorder(Border.NO_BORDER);
				funcTable.addCell(cell);

				// pax details
				String paxTitle = formatText("Pax : ", lang);
				cell = new Cell().add(new Paragraph(paxTitle).addStyle(funHeaderLabelStyle));
				cell.setTextAlignment(TextAlignment.LEFT);
				cell.setBorder(Border.NO_BORDER);
				cell.setPadding(2.5f);
				funcTable.addCell(cell);

				String paxVal = formatText(eventFunctionMasterResponseDto.getPax().toString(), lang);
				cell = new Cell().add(new Paragraph(paxVal).addStyle(funHeaderValueStyle));
				cell.setTextAlignment(TextAlignment.LEFT);
				cell.setBorder(Border.NO_BORDER);
				cell.setPadding(2.5f);
				funcTable.addCell(cell);

				cell = new Cell();
				cell.setBorder(Border.NO_BORDER);
				funcTable.addCell(cell);

				// Start Time
				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String dateVal;
				if (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
					Date date = new Date(eventFunctionMasterResponseDto.getFunctionStartTimestamp());
					dateVal = formatText(sdf.format(date), lang);
				} else {
					dateVal = formatText("TBD", lang);
				}
				String dateTitle = formatText("Date : ", lang);
				cell = new Cell().add(new Paragraph(dateTitle).addStyle(funHeaderLabelStyle));
				cell.setTextAlignment(TextAlignment.LEFT);
				cell.setBorder(Border.NO_BORDER);
				cell.setPadding(2.5f);
				funcTable.addCell(cell);

				String dateVal1 = formatText(dateVal, lang);
				cell = new Cell().add(new Paragraph(dateVal1).addStyle(funHeaderValueStyle));
				cell.setTextAlignment(TextAlignment.LEFT);
				cell.setBorder(Border.NO_BORDER);
				cell.setPadding(2.5f);
				funcTable.addCell(cell);

				funcTable.addCell(new Cell().setBorder(Border.NO_BORDER));

				funcTable.addCell(new Cell().setBorder(Border.NO_BORDER));

				cell = new Cell(1, 4).add(funcTable).setBorder(Border.NO_BORDER);
				menuTable.addCell(cell);

				line = new LineSeparator(solidLine);
				line.setWidth(UnitValue.createPercentValue(100));

				cell = new Cell(1, 4).add(line.addStyle(funNameStyle)).setBorder(Border.NO_BORDER);
				menuTable.addCell(cell);

				document.add(menuTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				boolean hasMenuData = menuReportResponseDtos != null && !menuReportResponseDtos.isEmpty();

				PageSize pageSize = pdfDocument.getDefaultPageSize();
				IRenderer renderer = menuTable.createRendererSubTree();
				renderer.setParent(document.getRenderer());

				LayoutResult layoutResult = renderer.layout(new LayoutContext(new LayoutArea(1,
						new Rectangle(document.getLeftMargin(), document.getBottomMargin(),
								pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin(),
								pageSize.getHeight()))));

				float headerHeight = layoutResult.getOccupiedArea().getBBox().getHeight();
				float bottomPadding = 140f;
				float gap = 20f;

				float columnWidth = (pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin() - gap)
						/ 2;

				float columnTopY = pageSize.getHeight() - headerHeight;

				float columnHeight = columnTopY - bottomPadding;

				// Adjust column height based on whether menu data exists
				float adjustedColumnHeight = hasMenuData ? (columnHeight - 120f) : columnHeight;
				System.out.println("column height : " + columnHeight);
				System.out.println("document.getTopMargin() : " + document.getTopMargin());
				System.out.println("adjustedColumnHeight : " + adjustedColumnHeight);

				Rectangle[] firstPageColumns = new Rectangle[] {
						new Rectangle(document.getLeftMargin(), bottomPadding, columnWidth, adjustedColumnHeight),
						new Rectangle(document.getLeftMargin() + columnWidth + gap, bottomPadding, columnWidth,
								adjustedColumnHeight) };

				Style itemNameStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(contentColor);
				Style itemInstStyle = new Style().setFont(basicFont).setFontSize(14).setFontColor(contentColor);
				Style itemSloganStyle = new Style().setFont(basicFont).setFontSize(12).setFontColor(contentColor);
				Style menuNameStyle = new Style().setFont(basicFont).setFontSize(17).setFontColor(headingColor)
						.setUnderline().simulateBold();
				Style menuInstStyle = new Style().setFont(basicFont).setFontSize(15).setFontColor(headingColor);
				Style menuSloganStyle = new Style().setFont(basicFont).setFontSize(13).setFontColor(headingColor);

				if (hasMenuData) {
					System.out.println("header height : " + headerHeight);
					document.setRenderer(new ColumnDocumentRenderer(document, firstPageColumns));
					for (MenuReportResponseDto menuReportResponse : menuReportResponseDtos) {
						Div categoryDiv = new Div();
						categoryDiv.setKeepTogether(true);
						categoryDiv.setMarginTop(5);

						// Category Name
						String catNameDisplay = menuReportResponse.getNameEnglish() != null
								? menuReportResponse.getNameEnglish()
								: "";
						SolidLine catLine = new SolidLine(1);
						catLine.setColor(headingColor);

						Paragraph categoryName = new Paragraph("- " + catNameDisplay + " -").addStyle(menuNameStyle)
								.setTextAlignment(TextAlignment.LEFT);
						categoryDiv.add(categoryName);

						// Category Slogan
						if (isCategorySlogan != null && isCategorySlogan == 1) {
							String slogan = menuReportResponse.getSlogan();
							if (slogan != null && !slogan.trim().isEmpty()) {
								categoryDiv.add(new Paragraph(formatText(slogan, lang)).addStyle(menuSloganStyle)
										.setTextAlignment(TextAlignment.LEFT));
							}
						}

						// Category Instructions
						if (isCategoryInstruction != null && isCategoryInstruction == 1) {
							String instruction = menuReportResponse.getMenuNotes();
							if (instruction != null && !instruction.trim().isEmpty()) {
								categoryDiv.add(new Paragraph(formatText(instruction, lang)).addStyle(menuInstStyle)
										.setTextAlignment(TextAlignment.LEFT));
							}
						}

						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menuReportResponse.getImagePath() != null
									&& !menuReportResponse.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menuReportResponse.getImagePath()));

									img.setAutoScale(true);
									img.setHorizontalAlignment(HorizontalAlignment.LEFT);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									categoryDiv.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}

						document.add(categoryDiv);

						// Menu Items
						if (menuReportResponse.getMenuItems() != null) {
							for (MenuItemForReportResponseDto itemRecipesResponseDto : menuReportResponse
									.getMenuItems()) {
								Div itemDiv = new Div();
								itemDiv.setKeepTogether(true);
								itemDiv.setMarginTop(2);

								String itemName;

								itemName = itemRecipesResponseDto.getNameEnglish() != null
										? itemRecipesResponseDto.getNameEnglish()
										: "Unnamed Item";
								// }
								itemDiv.add(new Paragraph(formatText(itemName, lang)).addStyle(itemNameStyle)
										.setTextAlignment(TextAlignment.LEFT)).setMarginBottom(0);

								// Item Slogan
								if (isItemSlogan != null && isItemSlogan == 1) {
									String itemSlogan = itemRecipesResponseDto.getSlogan();
									if (itemSlogan != null && !itemSlogan.trim().isEmpty()) {
										itemDiv.add(new Paragraph(formatText(itemSlogan, lang))
												.addStyle(itemSloganStyle).setTextAlignment(TextAlignment.LEFT));
									}
								}

								// Item Instructions
								if (isItemInstruction != null && isItemInstruction == 1) {
									String itemInstruction = itemRecipesResponseDto.getItemNotes();
									if (itemInstruction != null && !itemInstruction.trim().isEmpty()) {
										itemDiv.add(new Paragraph(formatText(itemInstruction, lang))
												.addStyle(itemInstStyle).setTextAlignment(TextAlignment.LEFT));
									}
								}

								document.add(itemDiv);
							}
						}
					}
				}
				document.setRenderer(new DocumentRenderer(document));
			}

			// ============ LAST PAGE ============

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ LAST PAGE ============
			// Set the background for the last page BEFORE creating it
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(lastPageNum, lastBgData);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType4(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;

			PdfFont catFont = null;
			PdfFont catFontBold = null;

			PdfFont itemFont = null;
			PdfFont itemFontBold = null;

			PdfFont sloganFont = null;
			PdfFont sloganFontBold = null;

			Integer catFontSize = req.getCatFontSize();
			Integer itemFontSize = req.getItemFontSize();
			Integer sloganFontSize = req.getSloganFontSize();

			PdfFont lightFont = loadFont("/fonts/tarif-light.ttf");
			String customMenu = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "";
			String startDate = "", endDate = "", l1 = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "";

			loadLicense();

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = loadFont("/fonts/Nirmala.ttf");
				boldFont = loadFont("/fonts/Nirmala-Bold.ttf");
//				itemFont = boldFont;
				System.out.println("Hindi font loaded successfully");
				customMenu = "विशेष मेनू";
				eDate = "दिनांक";
				venueLabel = "कार्यक्रम स्थल";
				phone = "संपर्क जानकारी";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन संबंधी निर्देश";
				l1 = "बजे से";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					// Tamil
					customMenu = "சிறப்பு மெனு";
					eDate = "நிகழ்ச்சி தேதி";
					venueLabel = "நிகழ்ச்சி இடம்";
					phone = "தொடர்பு தகவல்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு தொடர்பான வழிமுறைகள்";
					l1 = "மணி முதல்";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					// Telugu
					customMenu = "ప్రత్యేక మెనూ";
					eDate = "కార్యక్రమ తేదీ";
					venueLabel = "కార్యక్రమ స్థలం";
					phone = "సంప్రదింపు సమాచారం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన సంబంధిత సూచనలు";
					l1 = "గంటల నుండి";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					customMenu = "പ്രത്യേക മെനു";
					eDate = "പരിപാടിയുടെ തീയതി";
					venueLabel = "പരിപാടി സ്ഥലം";
					phone = "ബന്ധപ്പെടാനുള്ള വിവരം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണവുമായി ബന്ധപ്പെട്ട നിർദേശങ്ങൾ";
					l1 = "മണി മുതൽ";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					customMenu = "विशेष मेनू";
					eDate = "दिनांक";
					venueLabel = "कार्यक्रम स्थळ";
					phone = "संपर्क माहिती";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजनासंबंधी सूचना";
					l1 = "वाजल्यापासून";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					customMenu = "વિશેષ મેનુ";
					eDate = "કાર્યક્રમની તારીખ";
					venueLabel = "કાર્યક્રમ સ્થળ";
					phone = "સંપર્ક માહિતી";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન સંબંધિત સૂચનાઓ";
					l1 = "વાગ્યાથી";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
				}
//				itemFont = boldFont;
				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				customMenu = "Customized Menu";
				eDate = "Date of the Event";
				venueLabel = "Venue";
				personLabel = "Number of Pax";
				phone = "Contact Information";
				eTime = "Timing";
				fNote = "Food Preparation Notes";
				l1 = "Onwards";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";

				catFont = getFont(req.getCatFontId(), false, "helvetica");
				catFontBold = getFont(req.getCatFontId(), true, "helvetica");

				itemFont = getFont(req.getItemFontId(), false, "helvetica");
				itemFontBold = getFont(req.getItemFontId(), true, "helvetica");

				sloganFont = getFont(req.getSloganFontId(), false, "helvetica");
				sloganFontBold = getFont(req.getSloganFontId(), true, "helvetica");

//				itemFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

				System.out.println("English font loaded successfully");
			}

			PdfFont basicFont2 = loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			// Output formats
//			DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String headingrgb = adminTemplate.getTemplateMaster().getHeadingFontColor();

			String[] parts1 = headingrgb.replace("rgba(", "").replace(")", "").split(",");

			int r1 = Integer.parseInt(parts1[0].trim());
			int g1 = Integer.parseInt(parts1[1].trim());
			int b1 = Integer.parseInt(parts1[2].trim());
			System.out.println("rgb(" + r1 + "," + g1 + "," + b1 + ")");
			String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();

			String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");

			int r2 = Integer.parseInt(parts2[0].trim());
			int g2 = Integer.parseInt(parts2[1].trim());
			int b2 = Integer.parseInt(parts2[2].trim());
			System.out.println("rgb(" + r2 + "," + g2 + "," + b2 + ")");
			Color headingColor = new DeviceRgb(r1, g1, b1);
			Color contentColor = new DeviceRgb(r2, g2, b2);
			Color back = new DeviceRgb(0, 0, 0);
			Color gray = new DeviceRgb(0, 51, 51);
			// Font styles
			Style normalStyle = new Style().setFont(basicFont).setFontSize(14);
			Style eventStyle = new Style().setFont(basicFont2).setFontSize(18).setFontColor(headingColor);
			Style boldStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(contentColor);
			Style funNameStyle = new Style().setFont(basicFont).setFontSize(20).setFontColor(headingColor);
			Style itemNameStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(headingColor);
			Style itemInstStyle = new Style().setFont(basicFont).setFontSize(14).setFontColor(contentColor);
			Style itemSloganStyle = new Style().setFont(basicFont).setFontSize(12).setFontColor(contentColor);
			Style menuNameStyle = new Style().setFont(basicFont).setFontSize(17).setFontColor(back);
			Style menuInstStyle = new Style().setFont(basicFont).setFontSize(15).setFontColor(back);
			Style menuSloganStyle = new Style().setFont(basicFont).setFontSize(13).setFontColor(gray);

			System.out.println("Fetching event data...");

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);
			System.out.println("Event data fetched : " + eventDto);

			if (eventDto == null) {
				return "";
			}

			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();

			String billingName = "";
			String foodNotes = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			System.out.println(" type 4 callinnngggggg");

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			System.out.println(rootPath);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			System.out.println(outputPath);
			System.out.println(" 2 ");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			System.out.println(" 3 ");

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageNumberHandler pageHandler = new PageNumberHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler);

			Document document = new Document(pdfDocument);
			document.setMargins(60, 60, 60, 60);

			ImageData tncPage = null;
			// Load all background images upfront
			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Cell cell;

			Table eventInfoTable = new Table(1);
			eventInfoTable.setWidth(UnitValue.createPercentValue(100));
			eventInfoTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
			eventInfoTable.setMarginTop(20f);
			eventInfoTable.setBorder(Border.NO_BORDER);

			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();

			ImageData logoData = loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());
//			ImageData logoData = loadImageFromResource("/flipbook/pages/logo.png");
			Image logo = new Image(logoData);

			// Resize & align
			logo.setWidth(UnitValue.createPercentValue(40));
			logo.setAutoScale(false);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

			cell = new Cell().add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
					.setPaddingBottom(10f);
			cell.setWidth(UnitValue.createPercentValue(40));
			eventInfoTable.addCell(cell);

			// Host Name
			cell = new Cell().add(new Paragraph(customMenu).setFont(catFontBold).simulateBold()
					.setFontSize(getFontSize(catFontSize, 29)).setFontColor(back).setFixedLeading(32.5f)
					.setCharacterSpacing(2f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(20f);
			cell.setPaddingBottom(0f);
			cell.setTextAlignment(TextAlignment.CENTER);
			cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
			eventInfoTable.addCell(cell);

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName().toUpperCase() : "Not Specified";
			cell = new Cell().add(new Paragraph(hostName).setFont(itemFontBold).simulateBold()
					.setFontSize(getFontSize(itemFontSize, 25)).setFontColor(back).setPadding(0)
					.setFixedLeading(32.5f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(10f);
			cell.setPadding(0);
			cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
			cell.setVerticalAlignment(VerticalAlignment.TOP);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			if (billingName != null && billingName.trim().length() != 0) {
				// Billing Name
				cell = new Cell().add(new Paragraph(billingNameLabel).setFont(catFont).simulateBold()
						.setFontSize(getFontSize(catFontSize, 20)).setFontColor(back).setFixedLeading(22f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(30f);
				cell.setPaddingBottom(0f);
				cell.setTextAlignment(TextAlignment.CENTER);
				cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
				eventInfoTable.addCell(cell);

				cell = new Cell().add(new Paragraph(safeText(billingName)).setFont(itemFont)
						.setFontSize(getFontSize(itemFontSize, 21)).setFontColor(back).setPadding(0)
						.setFixedLeading(21f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(10f);
				cell.setPadding(0);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);
			}
			// Event Date
			cell = new Cell().add(new Paragraph(eDate).setFont(catFont).simulateBold()
					.setFontSize(getFontSize(catFontSize, 20)).setFontColor(back).setFixedLeading(22f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(30f);
			cell.setPaddingBottom(0f);
			cell.setTextAlignment(TextAlignment.CENTER);
			cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
			eventInfoTable.addCell(cell);

			cell = new Cell().add(new Paragraph(safeText(eventStartDate + " - " + eventEndDate)).setFont(itemFont)
					.setFontSize(getFontSize(itemFontSize, 21)).setFontColor(back).setPadding(0).setFixedLeading(21f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(10f);
			cell.setPadding(0);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String venue = "";
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			if (venue != null && venue.trim().length() != 0) {
				// Venue
				cell = new Cell().add(new Paragraph(venueLabel).setFont(catFont).simulateBold()
						.setFontSize(getFontSize(catFontSize, 20)).setFontColor(back).setPadding(0)
						.setFixedLeading(22f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(30f);
				cell.setPaddingBottom(0f);
				cell.setTextAlignment(TextAlignment.CENTER);
				cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
				eventInfoTable.addCell(cell);

				cell = new Cell().add(new Paragraph(venue).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 21))
						.setFontColor(back).setPadding(0).setFixedLeading(21f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(10f);
				cell.setPadding(0);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);
			}

			// Mobile No
			cell = new Cell().add(new Paragraph(phone).setFont(catFont).simulateBold()
					.setFontSize(getFontSize(catFontSize, 20)).setFontColor(back).setPadding(0).setFixedLeading(22f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(30f);
			cell.setPaddingBottom(0f);
			cell.setTextAlignment(TextAlignment.CENTER);
			cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
			eventInfoTable.addCell(cell);

			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			cell = new Cell().add(new Paragraph(mobileNo).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 21))
					.setFontColor(back).setPadding(0).setFixedLeading(21f));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(10f);
			cell.setPadding(0);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String remark = eventDto.getRemark() == null ? "" : eventDto.getRemark().toUpperCase();

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark.trim().length() != 0) {
				// Remarks
				cell = new Cell().add(
						new Paragraph(remarks).setFont(catFont).simulateBold().setFontSize(getFontSize(catFontSize, 20))
								.setFontColor(back).setPadding(0).setFixedLeading(22f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(30f);
				cell.setPaddingBottom(0f);
				cell.setTextAlignment(TextAlignment.CENTER);
				cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
				eventInfoTable.addCell(cell);

				cell = new Cell().add(new Paragraph(remark).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 21))
						.setFontColor(back).setPadding(0).setFixedLeading(21f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(10f);
				cell.setPadding(0);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);
			}

			String foodNotesName = eventDto.getFoodType();

			String cuisineType = (foodNotesName != null && !foodNotesName.trim().isEmpty())
					? foodNotesName.toUpperCase()
					: "Not Specified";

			String notesPart = (foodNotes != null && !foodNotes.trim().isEmpty()) ? " (" + foodNotes + ")" : "";

			cuisineType = cuisineType + notesPart;

			if (cuisineType != null && cuisineType.trim().length() != 0) {
				// Food Preparations
				cell = new Cell().add(
						new Paragraph(fNote).setFont(catFont).simulateBold().setFontSize(getFontSize(catFontSize, 20))
								.setFontColor(back).setPadding(0).setFixedLeading(22f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(30f);
				cell.setPaddingBottom(0f);
				cell.setTextAlignment(TextAlignment.CENTER);
				cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
				eventInfoTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(cuisineType).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 21))
								.setFontColor(back).setPadding(0).setFixedLeading(21f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(10f);
				cell.setPadding(0);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);
			}

			if (service.trim().length() != 0) {
				// Service
				cell = new Cell().add(new Paragraph(serviceLabel).setFont(catFont).simulateBold()
						.setFontSize(getFontSize(catFontSize, 20)).setFontColor(back).setPadding(0)
						.setFixedLeading(22f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(30f);
				cell.setPaddingBottom(0f);
				cell.setTextAlignment(TextAlignment.CENTER);
				cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
				eventInfoTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(service).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 21))
								.setFontColor(back).setPadding(0).setFixedLeading(21f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(10f);
				cell.setPadding(0);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);
			}

			if (theme.trim().length() != 0) {
				// Theme
				cell = new Cell().add(new Paragraph(themeLabel).setFont(catFont).simulateBold()
						.setFontSize(getFontSize(catFontSize, 20)).setFontColor(back).setPadding(0)
						.setFixedLeading(22f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(30f);
				cell.setPaddingBottom(0f);
				cell.setTextAlignment(TextAlignment.CENTER);
				cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
				eventInfoTable.addCell(cell);

				cell = new Cell().add(new Paragraph(theme).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 21))
						.setFontColor(back).setPadding(0).setFixedLeading(21f));
				cell.setBorder(Border.NO_BORDER);
				cell.setPaddingTop(10f);
				cell.setPadding(0);
				cell.setTextAlignment(TextAlignment.CENTER);
				eventInfoTable.addCell(cell);
			}

			document.add(eventInfoTable);

			/* ================= MENU CONTENT PAGES ================= */
			for (EventFunctionReportResponseDto functionDto : eventDto.getFunctions()) {

				// Start each function on new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				/* ================= MAIN CONTAINER ================= */
				Table mainTable = new Table(1);
				mainTable.setWidth(UnitValue.createPercentValue(100));
				mainTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
				mainTable.setBorder(Border.NO_BORDER);

				/* ================= HEADER SECTION ================= */

				String mainFunction = functionDto.getMainFunction() != null ? functionDto.getMainFunction() : "";
				// 1st row
				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph()
							.add(new com.itextpdf.layout.element.Text(mainFunction).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 22)).setFontColor(back).setUnderline());
					Cell mainFunctionCell = new Cell().add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					mainTable.addCell(mainFunctionCell);
				}

				String rate = functionDto.getRate() != null ? functionDto.getRate().toString() : "";
				String ratePostFix = functionDto.getRatePostFix() != null
						? " (" + rate + " " + functionDto.getRatePostFix() + ") "
						: "";
				// Function Name (DINNER)
				// formatText(functionDto.getFunctionName(), lang).toUpperCase()
				cell = new Cell()
						.add(new Paragraph(functionDto.getFunctionName() + ratePostFix).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 22)).simulateBold().setFontColor(back))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
				mainTable.addCell(cell);

				// Timing Label
				cell = new Cell()
						.add(new Paragraph(eTime).setCharacterSpacing(1.5f).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 17)).simulateBold().setFontColor(back)
								.setFixedLeading(17f))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPadding(0);
				mainTable.addCell(cell);

				// Timing Value
				String startTime = functionDto.getFunctionStartTimestamp() != null
						? functionDto.getFunctionStartTimestamp().toUpperCase()
						: "TBD";

				cell = new Cell()
						.add(new Paragraph(startTime.split(" ")[1] + " " + startTime.split(" ")[2] + " " + l1)
								.setFontColor(back).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 18))
								.setFixedLeading(18f))
						.setPadding(0).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
						.setPaddingBottom(15);
				mainTable.addCell(cell);

				String foodType = functionDto.getFoodType() != null ? " (" + functionDto.getFoodType() + ")" : "";

				// Number of Pax Label
				cell = new Cell()
						.add(new Paragraph(personLabel).setFont(catFont).simulateBold()
								.setFontSize(getFontSize(catFontSize, 18)).setFontColor(back).setFixedLeading(17f))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
				mainTable.addCell(cell);

				// Pax Value
				String pax = functionDto.getPax() != null ? functionDto.getPax().toString() : "0";

				cell = new Cell()
						.add(new Paragraph(pax + foodType).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 18))
								.setFontColor(back).setFixedLeading(18f))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(5)
						.setPaddingBottom(20);
				mainTable.addCell(cell);

				String functionNotes = "";
				if (lang == 1) {
					functionNotes = functionDto.getNotesHindi() != null ? functionDto.getNotesHindi() : "";
				} else if (lang == 2) {
					functionNotes = functionDto.getNotesGujarati() != null ? functionDto.getNotesGujarati() : "";
				} else {
					functionNotes = functionDto.getNotesEnglish() != null ? functionDto.getNotesEnglish() : "";
				}

				cell = new Cell()
						.add(new Paragraph(functionNotes).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 18))
								.setFontColor(back).setFixedLeading(18f))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(5)
						.setPaddingBottom(20);
				mainTable.addCell(cell);

				if (req.getIsWithPrice() == 1) {

					String label;
					String value;

					if (functionDto.getIsPackage()) {
						label = pckPrice;
						value = functionDto.getPackagePrice().toString();
					} else {
						label = price;
						value = functionDto.getRate().toString();
					}

					// Label Cell
					cell = new Cell()
							.add(new Paragraph(label).setFont(catFont).simulateBold()
									.setFontSize(getFontSize(catFontSize, 18)).setFontColor(back).setFixedLeading(17f))
							.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
					mainTable.addCell(cell);

					// Value Cell
					cell = new Cell()
							.add(new Paragraph(value).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 18))
									.setFontColor(back).setFixedLeading(18f))
							.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(5)
							.setPaddingBottom(20);
					mainTable.addCell(cell);
				}

				String functionVenue = null;
				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, functionDto.getFunctionId());

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? functionDto.getFunctionVenueHindi()
							: lang == 2 ? functionDto.getFunctionVenueGujarati() : functionDto.getFunctionVenue();
				}

				cell = new Cell()
						.add(new Paragraph(venueLabel).setFont(catFont).simulateBold()
								.setFontSize(getFontSize(catFontSize, 18)).setFontColor(back).setFixedLeading(17f))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
				mainTable.addCell(cell);

				// Value Cell
				cell = new Cell()
						.add(new Paragraph(functionVenue).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 18))
								.setFontColor(back).setFixedLeading(18f))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(5)
						.setPaddingBottom(20);
				mainTable.addCell(cell);
				/* ================= MENU CATEGORIES ================= */
				int itemCountInPage = 0;
				boolean isFirstPageOfFunction = true; // Track if we're on the first page with function header
				int itemsPerPage = 4; // Start with 4 items on first page (with header)

				for (MenuReportResponseDto categoryDto : functionDto.getMenuCategories()) {

					boolean categoryHeaderAdded = false; // Track if category header is added

					/* ================= MENU ITEMS ================= */
					for (int i = 0; i < categoryDto.getMenuItems().size(); i++) {
						MenuItemForReportResponseDto itemDto = categoryDto.getMenuItems().get(i);

						// Check if we need to start a new page
						if (itemCountInPage >= itemsPerPage) {
							// Add current table to document
							document.add(mainTable);
							document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

							// Create new main table for next page
							mainTable = new Table(1);
							mainTable.setWidth(UnitValue.createPercentValue(100));
							mainTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
							mainTable.setBorder(Border.NO_BORDER);

							// Reset counter and update settings for subsequent pages
							itemCountInPage = 0;
							isFirstPageOfFunction = false;
							itemsPerPage = 6; // Subsequent pages can hold 6 items
						}

						// Add category header if not added yet for this category
						if (!categoryHeaderAdded) {

							// 🔹 NEW: move category to next page if no space for items
							if (itemCountInPage + 1 >= itemsPerPage) {
								document.add(mainTable);
								document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

								mainTable = new Table(1);
								mainTable.setWidth(UnitValue.createPercentValue(100));
								mainTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
								mainTable.setBorder(Border.NO_BORDER);

								itemCountInPage = 0;
								itemsPerPage = 6;
							}

							cell = new Cell()
									.add(new Paragraph().add(new Text(
											formatText(categoryDto.getNameEnglish(), lang).toUpperCase())
											.setFont(catFont).setFontSize(getFontSize(catFontSize, 17)).simulateBold())
											.add(new Text(
													(isCategoryInstruction == 1 && categoryDto.getMenuNotes() != null
															&& categoryDto.getMenuNotes().trim().length() != 0)
																	? formatText(categoryDto.getMenuNotes(), lang)
																			.toUpperCase()
																	: "")
													.setFont(catFont).setFontSize(getFontSize(catFontSize, 15))
													.simulateBold())
											.add(new Text((isCategorySlogan == 1 && categoryDto.getSlogan() != null
													&& categoryDto.getSlogan().trim().length() != 0)
															? formatText(categoryDto.getSlogan(), lang).toUpperCase()
															: "")
													.setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 13))))
									.setSplitCharacters(new ISplitCharacters() {
										@Override
										public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
											return true;
										}
									}).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
									.setPaddingTop(10).setPaddingBottom(10).setMarginBottom(15).setKeepTogether(true);
							mainTable.addCell(cell);

							categoryHeaderAdded = true;
						}

						// Create 2-column table with FIXED widths
						Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 1, 3 }));
						itemTable.setWidth(UnitValue.createPercentValue(100));
//						itemTable.setKeepTogether(true);

						/* -------- LEFT: IMAGE CELL (FIXED SIZE BOX) -------- */
						Cell imgCell = new Cell();
						imgCell.setBorder(Border.NO_BORDER);
						imgCell.setBorderRight(new SolidBorder(back, 1));
						imgCell.setPadding(2);
						imgCell.setTextAlignment(TextAlignment.CENTER);
						imgCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						// Fixed width, flexible height
						imgCell.setWidth(50);
						imgCell.setMinHeight(50);

						if (itemDto.getImagePath() != null && !itemDto.getImagePath().isEmpty()) {
							try {
								ImageData imageData = ImageDataFactory.create(itemDto.getImagePath());
//								ImageData imageData = loadImageFromResource("/flipbook/pages/logo.png");
								Image image = new Image(imageData);

								image.setWidth(80);
								image.setHeight(60);
								image.setAutoScale(true);
								image.setHorizontalAlignment(HorizontalAlignment.CENTER);

								imgCell.add(image);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						itemTable.addCell(imgCell);

						/* -------- RIGHT: TEXT CELL (FLEXIBLE HEIGHT) -------- */
						Cell textCell = new Cell();
						textCell.setBorder(Border.NO_BORDER);
						textCell.setPadding(2);
						textCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
						textCell.setMinHeight(80);

						// Item Name
						Paragraph itemName = new Paragraph()
								.add(new Text(formatText(itemDto.getNameEnglish(), lang).toUpperCase())
										.setFont(itemFont).setFontColor(back).setFontSize(getFontSize(itemFontSize, 15))
										.simulateBold())
								.add(new Text((isItemInstruction == 1 && itemDto.getItemNotes() != null
										&& itemDto.getItemNotes().trim().length() != 0)
												? formatText(itemDto.getItemNotes(), lang).toUpperCase()
												: "")
										.setFont(itemFont).setFontColor(back).setFontSize(getFontSize(itemFontSize, 12))
										.simulateBold())
								.add(new Text((isItemSlogan == 1 && itemDto.getSlogan() != null
										&& itemDto.getSlogan().trim().length() != 0)
												? "\n(" + formatText(itemDto.getSlogan(), lang).toUpperCase() + ""
												: "")
										.setFont(sloganFont).setFontColor(gray)
										.setFontSize(getFontSize(sloganFontSize, 11)))
								.setSplitCharacters(new ISplitCharacters() {
									@Override
									public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
										return true;
									}
								}).setTextAlignment(TextAlignment.CENTER).setMarginBottom(5);

						textCell.add(itemName);
						itemTable.addCell(textCell);

						/* -------- WRAPPER WITH BORDER -------- */
						Cell wrapper = new Cell();
						wrapper.add(itemTable);
						wrapper.setBorder(new SolidBorder(back, 1));
						wrapper.setPadding(0);
						wrapper.setKeepTogether(true); // ADD
						wrapper.setMarginBottom(10);

						mainTable.addCell(wrapper);

						// Increment item counter
						itemCountInPage++;
					}
				}

				document.add(mainTable);
			}

			// ---- EXTRA CHARGES PAGE (before closing) ----
			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title ────────────────────────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-")
							.setFont(catFontBold != null ? catFontBold : boldFont)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(back)
							.setTextAlignment(TextAlignment.CENTER).setUnderline()
							// .setBackgroundColor(new DeviceRgb(255, 255, 0))
							.setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// ── Heading title ─────────────────────────────────────────
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16))
								.setFontColor(headingColor).setUnderline().setTextAlignment(TextAlignment.LEFT)
								.setMarginTop(12f).setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(headingColor).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, contentColor, catFontSize, headingColor));
								}
							}
						}

						document.add(chargeTable);

						// Heading Total
						document.add(new Paragraph("Total : "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold != null ? catFontBold : boldFont)
								.setFontSize(getFontSize(catFontSize, 13)).setFontColor(back)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f));
					}

					// Grand Total
					document.add(new Paragraph("Grand Total : "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold != null ? catFontBold : boldFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(back)
							// .setBackgroundColor(new DeviceRgb(255, 255, 0))
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline());
				}
			}

			// ---- write total page count into the placeholder ----
			PdfFormXObject totalPagesPlaceholder = pageHandler.getTotalPagesPlaceholder();

			PdfCanvas pdfCanvas = new PdfCanvas(totalPagesPlaceholder, pdfDocument);

			Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, 0, 50, 30));

			canvas.showTextAligned(String.valueOf(pdfDocument.getNumberOfPages()), 0, 5, TextAlignment.LEFT);

			canvas.close();

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private Cell extraDataCell(String text, PdfFont font, Color textColor, Integer fontSize) {
		return new Cell()
				.add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(getFontSize(fontSize, 12))
						.setFontColor(textColor))
				.setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(textColor, 0.5f)).setPaddingTop(8f)
				.setPaddingBottom(8f);
	}

	@Override
	public String generateExclusiveReportType5(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate) {
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

			System.out.println(" 1 ");

			PdfFont basicFont2 = loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			// Output formats
			DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

			String headingrgb = adminTemplate.getTemplateMaster().getHeadingFontColor();

			String[] parts1 = headingrgb.replace("rgba(", "").replace(")", "").split(",");

			int r1 = Integer.parseInt(parts1[0].trim());
			int g1 = Integer.parseInt(parts1[1].trim());
			int b1 = Integer.parseInt(parts1[2].trim());
			System.out.println("rgb(" + r1 + "," + g1 + "," + b1 + ")");
			String contentrgb = adminTemplate.getTemplateMaster().getContentFontColor();

			String[] parts2 = contentrgb.replace("rgba(", "").replace(")", "").split(",");

			int r2 = Integer.parseInt(parts2[0].trim());
			int g2 = Integer.parseInt(parts2[1].trim());
			int b2 = Integer.parseInt(parts2[2].trim());
			System.out.println("rgb(" + r2 + "," + g2 + "," + b2 + ")");
			Color normalColor = new DeviceRgb(r1, g1, b1);
			Color boldColor = new DeviceRgb(r2, g2, b2);
			Color back = new DeviceRgb(0, 0, 0);
			// Font styles
			Style normalStyle = new Style().setFont(basicFont).setFontSize(14);
			Style eventStyle = new Style().setFont(basicFont2).setFontSize(18).setFontColor(normalColor);
			Style boldStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(boldColor);
			Style funNameStyle = new Style().setFont(basicFont).setFontSize(20).setFontColor(normalColor);
			Style itemNameStyle = new Style().setFont(basicFont).setFontSize(16).setFontColor(normalColor);
			Style itemInstStyle = new Style().setFont(basicFont).setFontSize(14).setFontColor(boldColor);
			Style itemSloganStyle = new Style().setFont(basicFont).setFontSize(12).setFontColor(boldColor);
			Style menuNameStyle = new Style().setFont(basicFont).setFontSize(17).setFontColor(normalColor);
			Style menuInstStyle = new Style().setFont(basicFont).setFontSize(15).setFontColor(boldColor);
			Style menuSloganStyle = new Style().setFont(basicFont).setFontSize(13).setFontColor(boldColor);

			System.out.println("Fetching event data...");
			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);
			System.out.println("Event data fetched : " + eventDto);

			if (eventDto == null) {
				return "";
			}

			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			System.out.println(" 2 ");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			System.out.println(" 3 ");

			File pdfFile = new File(outputPath + "/"
					+ getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(60, 60, 60, 60);

			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			bgHandler.setPageBackground(2, detailBgData); // Page 2: Detail
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// ============ PAGE 1: Front Page (Main) ============
			// Add invisible content to ensure page 1 exists
			Paragraph invisibleContent = new Paragraph("\u00A0"); // Non-breaking space
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ PAGE 2: Title Page (Detail) ============
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ PAGE 3: Event Information ============
			Cell cell;

			Table eventInfoTable = new Table(1);
			eventInfoTable.setWidth(UnitValue.createPercentValue(100));
			eventInfoTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
			eventInfoTable.setBorder(Border.NO_BORDER);

			// Menu Proposed On
//			cell = new Cell().add(new Paragraph("Menu Proposed On").addStyle(eventStyle));
//			cell.setBorder(Border.NO_BORDER);
//			cell.setPaddingTop(20f);
//			cell.setTextAlignment(TextAlignment.CENTER);
//			eventInfoTable.addCell(cell);
//
//			cell = new Cell().add(new Paragraph(eventDate).addStyle(boldStyle));
//			cell.setBorder(Border.NO_BORDER);
//			cell.setPaddingTop(10f);
//			cell.setPaddingBottom(5f);
//			cell.setTextAlignment(TextAlignment.CENTER);
//			eventInfoTable.addCell(cell);

			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			// Host Name
			cell = new Cell().add(new Paragraph("Customize Menu").addStyle(eventStyle));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(20f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			cell = new Cell().add(new Paragraph(hostName).addStyle(boldStyle));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(10f);
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
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
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Event Date
			cell = new Cell().add(new Paragraph("Event Date").addStyle(eventStyle));
			cell.setBorder(Border.NO_BORDER);
			cell.setPaddingTop(18f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			cell = new Cell().add(new Paragraph(safeText(eventStartDate + " - " + eventEndDate)).addStyle(boldStyle));
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

			// Venue
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

			// Food Preparations
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
			cell.setPaddingBottom(5f);
			cell.setTextAlignment(TextAlignment.CENTER);
			eventInfoTable.addCell(cell);

			// Special Note (if exists)
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

			document.add(eventInfoTable);

			/* ================= MENU CONTENT PAGES ================= */
			for (EventFunctionReportResponseDto functionDto : eventDto.getFunctions()) {

				// Start each function on new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				/* ================= MAIN CONTAINER ================= */
				Table mainTable = new Table(1);
				mainTable.setWidth(UnitValue.createPercentValue(100));
				mainTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
				mainTable.setBorder(Border.NO_BORDER);

				/* ================= HEADER SECTION ================= */

				// Function Name (DINNER)
				cell = new Cell()
						.add(new Paragraph(formatText(functionDto.getFunctionName(), lang)).addStyle(funNameStyle))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(20)
						.setPaddingBottom(15);
				mainTable.addCell(cell);

				// Timing Label
				cell = new Cell().add(new Paragraph("Timing").addStyle(eventStyle))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(5);
				mainTable.addCell(cell);

				// Timing Value
				String startTime = functionDto.getFunctionStartTimestamp() != null
						? functionDto.getFunctionStartTimestamp()
						: "TBD";

				cell = new Cell().add(new Paragraph(startTime).addStyle(boldStyle))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(5)
						.setPaddingBottom(15);
				mainTable.addCell(cell);

				// Number of Pax Label
				cell = new Cell().add(new Paragraph("Number of Pax").addStyle(eventStyle))
						.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingTop(5);
				mainTable.addCell(cell);

				// Pax Value
				String pax = functionDto.getPax() != null ? functionDto.getPax().toString() : "0";

				cell = new Cell().add(new Paragraph(pax).addStyle(boldStyle)).setTextAlignment(TextAlignment.CENTER)
						.setBorder(Border.NO_BORDER).setPaddingTop(5).setPaddingBottom(20);
				mainTable.addCell(cell);

				/* ================= MENU CATEGORIES ================= */
				int itemCountInPage = 0;
				boolean isFirstPageOfFunction = true; // Track if we're on the first page with function header
				int itemsPerPage = 4; // Start with 4 items on first page (with header)

				for (MenuReportResponseDto categoryDto : functionDto.getMenuCategories()) {

					boolean categoryHeaderAdded = false; // Track if category header is added

					/* ================= MENU ITEMS ================= */
					for (int i = 0; i < categoryDto.getMenuItems().size(); i++) {
						MenuItemForReportResponseDto itemDto = categoryDto.getMenuItems().get(i);

						// Check if we need to start a new page
						if (itemCountInPage >= itemsPerPage) {
							// Add current table to document
							document.add(mainTable);
							document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

							// Create new main table for next page
							mainTable = new Table(1);
							mainTable.setWidth(UnitValue.createPercentValue(100));
							mainTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
							mainTable.setBorder(Border.NO_BORDER);

							// Reset counter and update settings for subsequent pages
							itemCountInPage = 0;
							isFirstPageOfFunction = false;
							itemsPerPage = 5; // Subsequent pages can hold 6 items
						}

						// Add category header if not added yet for this category
						if (!categoryHeaderAdded) {
							cell = new Cell()
									.add(new Paragraph(formatText(categoryDto.getNameEnglish(), lang))
											.addStyle(menuNameStyle))
									.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
									.setPaddingTop(10).setPaddingBottom(10).setMarginBottom(15);
							mainTable.addCell(cell);
							categoryHeaderAdded = true;
						}

						// Create 2-column table with FIXED widths
						Table itemTable = new Table(new float[] { 30, 70 });
						itemTable.setWidth(UnitValue.createPercentValue(100));
						itemTable.setFixedLayout();

						/* -------- LEFT: IMAGE CELL (FIXED SIZE BOX) -------- */
						Cell imgCell = new Cell();
						imgCell.setBorder(Border.NO_BORDER);
						imgCell.setBorderRight(new SolidBorder(back, 1));
						imgCell.setPadding(2);
						imgCell.setTextAlignment(TextAlignment.CENTER);
						imgCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

						// Fixed width, flexible height
						imgCell.setWidth(50);
						imgCell.setMinHeight(50);

						if (itemDto.getImagePath() != null && !itemDto.getImagePath().isEmpty()) {
							try {
								ImageData imageData = ImageDataFactory.create(itemDto.getImagePath());
								Image image = new Image(imageData);

								image.setWidth(80);
								image.setHeight(60);
								image.setAutoScale(true);
								image.setHorizontalAlignment(HorizontalAlignment.CENTER);

								imgCell.add(image);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						itemTable.addCell(imgCell);

						/* -------- RIGHT: TEXT CELL (FLEXIBLE HEIGHT) -------- */
						Cell textCell = new Cell();
						textCell.setBorder(Border.NO_BORDER);
						textCell.setPadding(2);
						textCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
						textCell.setMinHeight(80);

						// Item Name
						Paragraph itemName = new Paragraph(formatText(itemDto.getNameEnglish(), lang))
								.addStyle(itemNameStyle);
						itemName.setTextAlignment(TextAlignment.CENTER);
						itemName.setMarginBottom(5);
						textCell.add(itemName);

						// Item Slogan
						if (isItemSlogan == 1 && itemDto.getSlogan() != null && !itemDto.getSlogan().isEmpty()) {
							Paragraph slogan = new Paragraph("'" + formatText(itemDto.getSlogan(), lang) + "'")
									.addStyle(itemSloganStyle);
							slogan.setTextAlignment(TextAlignment.CENTER);
							slogan.setMarginTop(3);
							slogan.setMarginBottom(3);
							textCell.add(slogan);
						}

						// Item Instructions/Notes
						if (isItemInstruction == 1 && itemDto.getItemNotes() != null
								&& !itemDto.getItemNotes().isEmpty()) {
							Paragraph notes = new Paragraph(formatText(itemDto.getItemNotes(), lang))
									.addStyle(itemInstStyle);
							notes.setTextAlignment(TextAlignment.CENTER);
							notes.setMarginTop(3);
							textCell.add(notes);
						}

						itemTable.addCell(textCell);

						/* -------- WRAPPER WITH BORDER -------- */
						Cell wrapper = new Cell();
						wrapper.add(itemTable);
						wrapper.setBorder(new SolidBorder(back, 1));
						wrapper.setPadding(0);
						wrapper.setMarginBottom(10);

						mainTable.addCell(wrapper);

						// Increment item counter
						itemCountInPage++;
					}
				}

				document.add(mainTable);
			}

			// ============ LAST PAGE ============
			// Set the background for the last page BEFORE creating it
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(lastPageNum, lastBgData);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf";

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType6(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate) {

		try {
			PdfFont basicFont = null;
			loadLicense();

			int x = 185;
			int y = 695;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 690;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = loadFont("/fonts/times.ttf");
				System.out.println("English font loaded successfully");
			}

			PdfFont basicFont2 = loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			// Output formats
			DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

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

			// Define colors
			Color orangeColor = new DeviceRgb(r2, g2, b2);
			Color goldBorder = new DeviceRgb(r1, g1, b1);
			Color GOLD = new DeviceRgb(r1, g1, b1);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

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

			File pdfFile = new File(outputPath + "/"
					+ getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(10, 35, 140, 40);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/shrihari_main_1.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/shrihari_details_1.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/shrihari_menu_1.png");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/shrihari_last_1.png");

			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// ============ PAGE 1: Front Page (Main) ============
			// Add invisible content to ensure page 1 exists
			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(basicFont).setFontColor(goldBorder);
			invisibleContent.setFontSize(35);
			invisibleContent.setPaddingTop(140f);
			invisibleContent.setPaddingLeft(165f);
			document.add(invisibleContent);

			int secondPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(secondPageNum, detailsPage);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = safeText(eventDto.getPax());
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = safeText(eventDto.getFoodNotes());
			String cmpName = safeText(eventDto.getCmpName());
			String cmpPhone = safeText(eventDto.getCmpPhone());
			String cmpAddress = safeText(eventDto.getCmpAddress());
			String userFirstName = safeText(eventDto.getUserFirstName());
			String userLastName = safeText(eventDto.getUserLastName());
			String countryCode = safeText(eventDto.getCountrycode());
			String email = safeText(eventDto.getEmail());

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			// ============ PAGE 1: Front Page (Main) ============

			// Party Name
			Paragraph dataContent = new Paragraph("Party Name")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(orangeColor)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y, pageWidth); // x, y,
																												// width
			document.add(dataContent);

			// Party Name
			Paragraph hostNameContent = new Paragraph(hostName).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth); // x,
																														// y,
																														// width
			document.add(hostNameContent);

			dataContent = new Paragraph("Mobile").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			// Mobile No.
			Paragraph mobileNoContent = new Paragraph(mobileNo).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
			document.add(mobileNoContent);

			dataContent = new Paragraph("Event Date").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			// Event Date
			Paragraph eventDateContent = new Paragraph(eventStartDate + " - " + eventEndDate).setFont(basicFont)
					.setFontColor(orangeColor).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 30, pageWidth);
			document.add(eventDateContent);

			// Event Name
			dataContent = new Paragraph("Type of Event")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(orangeColor)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 55, pageWidth); // x,
																														// y,
																														// width
			document.add(dataContent);

			Paragraph eventNameContent = new Paragraph(eventName).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
			document.add(eventNameContent);

			// Pax
//			dataContent = new Paragraph("Person").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
//					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
//					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
//			document.add(dataContent);
//
//			Paragraph paxContent = new Paragraph(pax).setFont(basicFont).setFontColor(orangeColor).setFontSize(18f)
//					.setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
//			document.add(paxContent);

			// Venue
			dataContent = new Paragraph("Venue").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			Paragraph venueContent = new Paragraph(venue).setFont(basicFont).setFontColor(orangeColor).setFontSize(18f)
					.setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 27, pageWidth);
			document.add(venueContent);

			// Food
			dataContent = new Paragraph("Food Preparations")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(orangeColor)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 55, pageWidth); // x,
																														// y,
																														// width
			document.add(dataContent);

			Paragraph foodContent = new Paragraph(foodNotesName.toString()).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 28, pageWidth);
			document.add(foodContent);

//			Paragraph cmpNameContent = new Paragraph(cmpName.toString())
//				    .setFont(basicFont)
//				    .setFontColor(orangeColor)
//				    .setFontSize(18f)
//				    .setTextAlignment(TextAlignment.CENTER)
//				    .setFixedPosition(0, y-=70, pageWidth);
//				document.add(cmpNameContent);

			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(orangeColor);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 50f, 50f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorderTop(new SolidBorder(orangeColor, 1f));
				headerTable.setBorderBottom(new SolidBorder(orangeColor, 1f));
				headerTable.setMarginTop(5f);

				// === ROW 1: Event | Pax ===
				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				Paragraph eventPara = new Paragraph()
						.add(new Paragraph("Function : ").setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f))
						.add(new Paragraph(functionTitle).setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f));
				Cell eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);

				String dish = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				Paragraph dishPara = new Paragraph()
						.add(new Paragraph("Pax : ").setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f))
						.add(new Paragraph(dish).setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f));
				Cell dishCell = new Cell().add(dishPara);
				dishCell.setBorder(Border.NO_BORDER);
				dishCell.setTextAlignment(TextAlignment.LEFT);

				headerTable.addCell(eventCell);
				headerTable.addCell(dishCell);

				// === ROW 2: Date | Time ===
				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				Paragraph datePara = new Paragraph()
						.add(new Paragraph("Date : ").setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f))
						.add(new Paragraph(date).setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f));
				Cell dateCell = new Cell().add(datePara);
				dateCell.setBorder(Border.NO_BORDER);
				dateCell.setPaddingTop(5f);
				dateCell.setTextAlignment(TextAlignment.LEFT);

				Paragraph timePara = new Paragraph()
						.add(new Paragraph("Time : ").setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f))
						.add(new Paragraph(time).setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f));
				Cell timeCell = new Cell().add(timePara);
				timeCell.setBorder(Border.NO_BORDER);
				timeCell.setPaddingTop(5f);
				timeCell.setTextAlignment(TextAlignment.LEFT);

				headerTable.addCell(dateCell);
				headerTable.addCell(timeCell);

				// === ROW 4: Address (spans all columns) ===
				String funcvenue = eventFunctionMasterResponseDto.getFunctionVenue();
				Paragraph addressPara = new Paragraph()
						.add(new Paragraph("Address : ").setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f))
						.add(new Paragraph(funcvenue).setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f));
				Cell addressCell = new Cell(1, 2).add(addressPara);
				addressCell.setBorder(Border.NO_BORDER);
				addressCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(addressCell);

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Check if there's menu data to display
				boolean hasMenuData = menuReportResponseDtos != null && !menuReportResponseDtos.isEmpty();

				// Set up 2-column layout
				PageSize pageSize = pdfDocument.getDefaultPageSize();
				IRenderer renderer = headerTable.createRendererSubTree();
				renderer.setParent(document.getRenderer());

				LayoutResult layoutResult = renderer.layout(new LayoutContext(new LayoutArea(0,
						new Rectangle(document.getLeftMargin(), document.getBottomMargin(),
								pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin(),
								pageSize.getHeight()))));

				float headerHeight = layoutResult.getOccupiedArea().getBBox().getHeight();
				float bottomPadding = 140f;
				float gap = 20f;

				float columnWidth = (pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin() - gap)
						/ 2;

				float columnTopY = pageSize.getHeight() - headerHeight;

				float columnHeight = columnTopY - bottomPadding;

				// Adjust column height based on whether menu data exists
				float adjustedColumnHeight = hasMenuData ? (columnHeight - 10f) : columnHeight;

				System.out.println("hasMenuData : " + hasMenuData);

				Rectangle leftColumn = new Rectangle(document.getLeftMargin(), bottomPadding, columnWidth,
						adjustedColumnHeight);

				Rectangle rightColumn = new Rectangle(document.getLeftMargin() + columnWidth + gap, bottomPadding,
						columnWidth, adjustedColumnHeight);

				// Only set up columns and add menu content if there's data
				if (hasMenuData) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// Set the column renderer for menu content
					document.setRenderer(
							new ColumnDocumentRenderer(document, new Rectangle[] { leftColumn, rightColumn }));

					// Add menu content
					for (MenuReportResponseDto menu : menuReportResponseDtos) {

						Div menuContent = new Div();

						Paragraph p = new Paragraph(formatText(menu.getNameEnglish(), lang)).setFont(basicFont)
								.setFontSize(12).setFixedLeading(13f).setFontColor(ColorConstants.WHITE)
								.setPaddingLeft(10).setPaddingRight(14);

						Table table = new Table(1);
						table.setBorder(Border.NO_BORDER);
						table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER)
								.setBorderRadius(new BorderRadius(20)).setBackgroundColor(GOLD));

						menuContent.add(table).setMarginTop(8f);

						// Category Slogan
						if (isCategorySlogan != null && isCategorySlogan == 1) {
							if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
								menuContent.add(new Paragraph(formatText(menu.getSlogan(), lang)).setFont(basicFont)
										.setFontSize(8.5f).setMultipliedLeading(1.18f).setMarginBottom(2f)
										.setFontColor(orangeColor).setMarginLeft(6));
							}
						}

						// Category Instructions
						if (isCategoryInstruction != null && isCategoryInstruction == 1) {
							if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
								menuContent.add(new Paragraph(formatText(menu.getMenuNotes(), lang)).setFont(basicFont)
										.setFontSize(8.5f).setMultipliedLeading(1.18f).setFontColor(orangeColor)
										.setMarginLeft(6));
							}
						}

						for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

							menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(basicFont)
									.setFontSize(11f).setFixedLeading(12f).setFontColor(orangeColor).setMarginLeft(12));

							// Item Slogan
							if (isItemSlogan != null && isItemSlogan == 1) {
								menuContent.add(new Paragraph(formatText(item.getSlogan(), lang)).setFont(basicFont)
										.setFontSize(8).setMultipliedLeading(1.2f).setFontColor(orangeColor)
										.setMarginLeft(18).setMarginTop(-4f).setMarginBottom(2f).setPadding(0f));
							}

							// Item Instructions
							if (isItemInstruction != null && isItemInstruction == 1) {
								menuContent.add(new Paragraph(formatText(item.getItemNotes(), lang)).setFont(basicFont)
										.setFontSize(8).setMultipliedLeading(1.2f).setFontColor(orangeColor)
										.setMarginLeft(18).setMarginTop(-4f).setMarginBottom(2f).setPadding(0f));
							}

						}

						document.add(menuContent);
					}

					// Reset renderer after menu content for next function
					document.setRenderer(new DocumentRenderer(document));
				}
			}

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ LAST PAGE ============
			// Set the background for the last page BEFORE creating it
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(lastPageNum, lastBgData);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType7(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate) {

		try {
			PdfFont basicFont = null;
			loadLicense();

			int x = 185;
			int y = 695;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 690;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = loadFont("/fonts/times.ttf");
				System.out.println("English font loaded successfully");
			}

			PdfFont basicFont2 = loadFont("/fonts/times.ttf");

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			// Output formats
			DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

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

			// Define colors
			Color orangeColor = new DeviceRgb(r2, g2, b2);
			Color goldBorder = new DeviceRgb(r1, g1, b1);
			Color GOLD = new DeviceRgb(r1, g1, b1);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

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

			File pdfFile = new File(outputPath + "/"
					+ getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(10, 35, 140, 40);

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/k1.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/k2.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/k3.png");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/k4.jpg");

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			// ============ PAGE 1: Front Page (Main) ============
			// Add invisible content to ensure page 1 exists
			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(basicFont).setFontColor(goldBorder);
			invisibleContent.setFontSize(35);
			invisibleContent.setPaddingTop(140f);
			invisibleContent.setPaddingLeft(165f);
			document.add(invisibleContent);

			int secondPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(secondPageNum, detailsPage);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			String venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = eventDto.getFoodNotes();
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			// ============ PAGE 1: Front Page (Main) ============

			// Party Name
			Paragraph dataContent = new Paragraph("Party Name")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(orangeColor)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y, pageWidth); // x, y,
																												// width
			document.add(dataContent);

			// Party Name
			Paragraph hostNameContent = new Paragraph(safeText(hostName)).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth); // x,
																														// y,
																														// width
			document.add(hostNameContent);

			dataContent = new Paragraph("Mobile").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			// Mobile No.
			Paragraph mobileNoContent = new Paragraph(safeText(mobileNo)).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
			document.add(mobileNoContent);

			dataContent = new Paragraph("Event Date").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			// Event Date
			Paragraph eventDateContent = new Paragraph(safeText(eventStartDate + " - " + eventEndDate))
					.setFont(basicFont).setFontColor(orangeColor).setFontSize(18f)
					.setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
			document.add(eventDateContent);

			// Event Name
			dataContent = new Paragraph("Type of Event")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(orangeColor)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 55, pageWidth); // x,
																														// y,
																														// width
			document.add(dataContent);

			Paragraph eventNameContent = new Paragraph(safeText(eventName)).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
			document.add(eventNameContent);

			// Pax
//			dataContent = new Paragraph("Person").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
//					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
//					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
//			document.add(dataContent);
//
//			Paragraph paxContent = new Paragraph(safeText(pax)).setFont(basicFont).setFontColor(orangeColor)
//					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 30, pageWidth);
//			document.add(paxContent);

			// Venue
			dataContent = new Paragraph("Venue").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
					.setFontColor(orangeColor).setFontSize(20f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 55, pageWidth); // x, y, width
			document.add(dataContent);

			Paragraph venueContent = new Paragraph(safeText(venue)).setFont(basicFont).setFontColor(orangeColor)
					.setFontSize(18f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 27, pageWidth);
			document.add(venueContent);

			// Food
			dataContent = new Paragraph("Food Preparations")
					.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontColor(orangeColor)
					.setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, y -= 55, pageWidth); // x,
																														// y,
																														// width
			document.add(dataContent);

			Paragraph foodContent = new Paragraph(safeText(foodNotesName.toString())).setFont(basicFont)
					.setFontColor(orangeColor).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 28, pageWidth);
			document.add(foodContent);

//			Paragraph cmpNameContent = new Paragraph(cmpName.toString())
//				    .setFont(basicFont)
//				    .setFontColor(orangeColor)
//				    .setFontSize(18f)
//				    .setTextAlignment(TextAlignment.CENTER)
//				    .setFixedPosition(0, y-=70, pageWidth);
//				document.add(cmpNameContent);

			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(orangeColor);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 50f, 50f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorderTop(new SolidBorder(orangeColor, 1f));
				headerTable.setBorderBottom(new SolidBorder(orangeColor, 1f));
				headerTable.setMarginTop(15f);

				// === ROW 1: Event | Pax ===
				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				Paragraph eventPara = new Paragraph()
						.add(new Paragraph("Function : ").setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f))
						.add(new Paragraph(functionTitle).setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f));
				Cell eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);

				String dish = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				Paragraph dishPara = new Paragraph()
						.add(new Paragraph("Pax : ").setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f))
						.add(new Paragraph(dish).setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f));
				Cell dishCell = new Cell().add(dishPara);
				dishCell.setBorder(Border.NO_BORDER);
				dishCell.setTextAlignment(TextAlignment.LEFT);

				headerTable.addCell(eventCell);
				headerTable.addCell(dishCell);

				// === ROW 2: Date | Time ===
				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				Paragraph datePara = new Paragraph()
						.add(new Paragraph("Date : ").setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f))
						.add(new Paragraph(date).setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f));
				Cell dateCell = new Cell().add(datePara);
				dateCell.setBorder(Border.NO_BORDER);
				dateCell.setPaddingTop(5f);
				dateCell.setTextAlignment(TextAlignment.LEFT);

				Paragraph timePara = new Paragraph()
						.add(new Paragraph("Time : ").setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f))
						.add(new Paragraph(time).setFont(basicFont).setFontColor(orangeColor).setFixedLeading(15f));
				Cell timeCell = new Cell().add(timePara);
				timeCell.setBorder(Border.NO_BORDER);
				timeCell.setPaddingTop(5f);
				timeCell.setTextAlignment(TextAlignment.LEFT);

				headerTable.addCell(dateCell);
				headerTable.addCell(timeCell);

				// === ROW 4: Address (spans all columns) ===
				String funcvenue = eventFunctionMasterResponseDto.getFunctionVenue();
				Paragraph addressPara = new Paragraph()
						.add(new Paragraph("Address : ").setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f))
						.add(new Paragraph(funcvenue).setFont(basicFont).setFontColor(orangeColor)
								.setFixedLeading(15f));
				Cell addressCell = new Cell(1, 2).add(addressPara);
				addressCell.setBorder(Border.NO_BORDER);
				addressCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(addressCell);

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Check if there's menu data to display
				boolean hasMenuData = menuReportResponseDtos != null && !menuReportResponseDtos.isEmpty();

				// Set up 2-column layout
				PageSize pageSize = pdfDocument.getDefaultPageSize();
				IRenderer renderer = headerTable.createRendererSubTree();
				renderer.setParent(document.getRenderer());

				LayoutResult layoutResult = renderer.layout(new LayoutContext(new LayoutArea(0,
						new Rectangle(document.getLeftMargin(), document.getBottomMargin(),
								pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin(),
								pageSize.getHeight()))));

				float headerHeight = layoutResult.getOccupiedArea().getBBox().getHeight();
				float bottomPadding = 140f;
				float gap = 20f;

				float columnWidth = (pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin() - gap)
						/ 2;

				float columnTopY = pageSize.getHeight() - headerHeight;

				float columnHeight = columnTopY - bottomPadding;

				// Adjust column height based on whether menu data exists
				float adjustedColumnHeight = hasMenuData ? (columnHeight - 20f) : columnHeight;

				Rectangle leftColumn = new Rectangle(document.getLeftMargin(), bottomPadding, columnWidth,
						adjustedColumnHeight);

				Rectangle rightColumn = new Rectangle(document.getLeftMargin() + columnWidth + gap, bottomPadding,
						columnWidth, adjustedColumnHeight);

				// Only set up columns and add menu content if there's data
				if (hasMenuData) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// Set the column renderer for menu content
					document.setRenderer(
							new ColumnDocumentRenderer(document, new Rectangle[] { leftColumn, rightColumn }));

					// Add menu content
					for (MenuReportResponseDto menu : menuReportResponseDtos) {

						Div menuContent = new Div();

						Paragraph p = new Paragraph(formatText(menu.getNameEnglish(), lang)).setFont(basicFont)
								.setFontSize(12).setFixedLeading(13f).setFontColor(ColorConstants.WHITE)
								.setPaddingLeft(10).setPaddingRight(14);

						Table table = new Table(1);
						table.setBorder(Border.NO_BORDER);
						table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER)
								.setBorderRadius(new BorderRadius(20)).setBackgroundColor(GOLD));

						menuContent.add(table).setMarginTop(8f);

						// Category Slogan
						if (isCategorySlogan != null && isCategorySlogan == 1) {
							if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
								menuContent.add(new Paragraph(formatText(menu.getSlogan(), lang)).setFont(basicFont)
										.setFontSize(8.5f).setMultipliedLeading(1.18f).setMarginBottom(2f)
										.setFontColor(orangeColor).setMarginLeft(6));
							}
						}

						// Category Instructions
						if (isCategoryInstruction != null && isCategoryInstruction == 1) {
							if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
								menuContent.add(new Paragraph(formatText(menu.getMenuNotes(), lang)).setFont(basicFont)
										.setFontSize(8.5f).setMultipliedLeading(1.18f).setFontColor(orangeColor)
										.setMarginLeft(6));
							}
						}

						for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

							menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(basicFont)
									.setFontSize(11f).setFixedLeading(12f).setFontColor(orangeColor).setMarginLeft(12));

							// Item Slogan
							if (isItemSlogan != null && isItemSlogan == 1) {
								menuContent.add(new Paragraph(formatText(item.getSlogan(), lang)).setFont(basicFont)
										.setFontSize(8).setMultipliedLeading(1.2f).setFontColor(orangeColor)
										.setMarginLeft(18).setMarginTop(-4f).setMarginBottom(2f).setPadding(0f));
							}

							// Item Instructions
							if (isItemInstruction != null && isItemInstruction == 1) {
								menuContent.add(new Paragraph(formatText(item.getItemNotes(), lang)).setFont(basicFont)
										.setFontSize(8).setMultipliedLeading(1.2f).setFontColor(orangeColor)
										.setMarginLeft(18).setMarginTop(-4f).setMarginBottom(2f).setPadding(0f));
							}

						}

						document.add(menuContent);
					}

					// Reset renderer after menu content for next function
					document.setRenderer(new DocumentRenderer(document));
				}
			}

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ LAST PAGE ============
			// Set the background for the last page BEFORE creating it
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;
			bgHandler.setPageBackground(lastPageNum, lastBgData);

			// Create the last page with minimal invisible content
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			document.close();
			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(eventDto.getPartyName(), eventDto.getEventStartTimestamp(), "excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType8(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {
		try {
			PdfFont catFont = null;
			PdfFont catFontBold = null;

			PdfFont itemFont = null;
			PdfFont itemFontBold = null;

			PdfFont sloganFont = null;
			PdfFont sloganFontBold = null;

			Integer catFontSize = req.getCatFontSize();
			Integer itemFontSize = req.getItemFontSize();
			Integer sloganFontSize = req.getSloganFontSize();

			String customerName = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", billingNameLabel = "";
			String startDate = "", endDate = "", remarks = "", serviceLabel = "", themeLabel = "";
			loadLicense();

			int x = 185;
			int y = 695;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				customerName = "ग्राहक का नाम";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का नाम";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				startDate = "प्रारंभ दिनांक";
				endDate = "समाप्ति दिनांक";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				y = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					customerName = "வாடிக்கையாளர் பெயர்";
					phone = "மொபைல்";
					eDate = "நிகழ்ச்சி தேதி";
					eName = "நிகழ்ச்சி பெயர்";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					startDate = "தொடக்க தேதி";
					endDate = "முடிவு தேதி";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					// Telugu
					customerName = "కస్టమర్ పేరు";
					phone = "మొబైల్";
					eDate = "కార్యక్రమ తేదీ";
					eName = "కార్యక్రమ పేరు";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					startDate = "ప్రారంభ తేదీ";
					endDate = "ముగింపు తేదీ";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					customerName = "ഉപഭോക്താവിന്റെ പേര്";
					phone = "മൊബൈൽ";
					eDate = "പരിപാടിയുടെ തീയതി";
					eName = "പരിപാടിയുടെ പേര്";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					startDate = "ആരംഭ തീയതി";
					endDate = "അവസാന തീയതി";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					customerName = "ग्राहकाचे नाव";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रमाचे नाव";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					startDate = "प्रारंभ दिनांक";
					endDate = "समाप्ती दिनांक";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					customerName = "ગ્રાહકનું નામ";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનું નામ";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					startDate = "પ્રારંભ તારીખ";
					endDate = "સમાપ્તિ તારીખ";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
				}

				System.out.println("Gujarati font loaded successfully");
				y = 690;
			} else {
				// English
				System.out.println("Loading English font...");
				customerName = "Customer Name";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Event Name";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preparations";
				startDate = "Start Date";
				endDate = "End Date";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";

				catFont = getFont(req.getCatFontId(), false, "helvetica");
				catFontBold = getFont(req.getCatFontId(), true, "helvetica");

				itemFont = getFont(req.getItemFontId(), false, "helvetica");
				itemFontBold = getFont(req.getItemFontId(), true, "helvetica");

				sloganFont = getFont(req.getSloganFontId(), false, "helvetica");
				sloganFontBold = getFont(req.getSloganFontId(), true, "helvetica");

				System.out.println("English font loaded successfully");
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color headingColor = new DeviceRgb(r1, g1, b1);
			Color contentColor = new DeviceRgb(r2, g2, b2);
			Color lightGrey = new DeviceRgb(219, 219, 219);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(25, 20, 90, 20);

			// Load all background images upfront
			ImageData tncPage = null;
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());
			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/MainFront11.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/Watermark11.png");
//			ImageData titleHeadBgData = loadImageFromResource("/flipbook/pages/TitleHead11.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/Watermark11.png");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/MainLast11.png");

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
//			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(2, detailsPage); // Page 2: Detail
			}
//			bgHandler.setPageBackground(3, titleHeadBgData); // Page 2: Detail
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Paragraph invisibleContent = new Paragraph();

			// ============ PAGE 1: Front Page (Main) ============
			// Add invisible content to ensure page 1 exists
//			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(basicFont).setFontColor(headingColor);
//			invisibleContent.setFontSize(35);
//			invisibleContent.setPaddingTop(140f);
//			invisibleContent.setPaddingLeft(165f);
//			document.add(invisibleContent);
//			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			// ============ PAGE 2: Details Page ============
			// Add invisible content to ensure page 1 exists
//			if (isCompanyDetail == 1) {
//				invisibleContent = new Paragraph("\u00A0").setFont(basicFont).setFontColor(headingColor);
//				invisibleContent.setFontSize(35);
//				invisibleContent.setPaddingTop(140f);
//				invisibleContent.setPaddingLeft(165f);
//				document.add(invisibleContent);
//				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//			}

			// ============ PAGE 3: Event Detail Page ============

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String companyLogo = eventDto.getLogo();
			String remark = eventDto.getRemark();
			ImageData logoImg = loadImageFromResource(environment.getProperty("app.image.url") + companyLogo);
//			ImageData logoImg = loadImageFromResource("/flipbook/pages/krishnai_logo.png");
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			Image logo = new Image(logoImg);

			logo.setWidth(UnitValue.createPercentValue(100));
			logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			Div imgDiv = new Div();
			imgDiv.add(logo);
			imgDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
			imgDiv.setMarginTop(20f);
			imgDiv.setWidth(UnitValue.createPercentValue(40));

			document.add(imgDiv);

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			// ============ PAGE 1: Front Page (Main) ============
			Paragraph p = new Paragraph("Custom Menu Report").setFont(catFont).setFontSize(getFontSize(catFontSize, 18))
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(40f); // x, y,
			document.add(p);

			Color lineColor = new DeviceRgb(179, 179, 179);

			Table eventDetail = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }), false);
			eventDetail.setWidth(UnitValue.createPercentValue(93f));
			eventDetail.setHorizontalAlignment(HorizontalAlignment.CENTER);
			eventDetail.setMarginBottom(20f);
			eventDetail.setMarginTop(30f);
			eventDetail.setBorderTop(new SolidBorder(lineColor, 1f));
			eventDetail.setBorderBottom(new SolidBorder(lineColor, 1f));

			// Event Name
			Cell label = new Cell()
					.add(new Paragraph(eName + " : ").setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
					.setPadding(4f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(label);

			Cell value = new Cell()
					.add(new Paragraph(safeText(eventName).toUpperCase()).setFont(itemFont).setFontColor(headingColor)
							.setFontSize(getFontSize(itemFontSize, 15)).setTextAlignment(TextAlignment.LEFT))
					.setPadding(4f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(value);

			// Customer Name
			label = new Cell()
					.add(new Paragraph(customerName + " : ").setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
					.setBorder(Border.NO_BORDER).setPadding(4f).setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(label);

			value = new Cell()
					.add(new Paragraph(safeText(hostName).toUpperCase()).setFont(itemFont).setFontColor(headingColor)
							.setFontSize(getFontSize(itemFontSize, 15)).setTextAlignment(TextAlignment.LEFT))
					.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(value);

			if (billingName.trim().length() != 0) {
				// Billing Name
				label = new Cell()
						.add(new Paragraph(billingNameLabel + " : ").setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
						.setBorder(Border.NO_BORDER).setPadding(4f).setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(billingName).toUpperCase()).setFont(itemFont)
								.setFontColor(headingColor).setFontSize(getFontSize(itemFontSize, 15))
								.setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			// Event Date
			label = new Cell()
					.add(new Paragraph(eDate + " : ").setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
					.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(label);

			value = new Cell()
					.add(new Paragraph(safeText(eventStartDate + " - " + eventEndDate).toUpperCase()).setFont(itemFont)
							.setFontColor(headingColor).setFontSize(getFontSize(itemFontSize, 15))
							.setTextAlignment(TextAlignment.LEFT))
					.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(lineColor, 1f));
			eventDetail.addCell(value);

			if (venue != null && venue.trim().length() != 0) {
				// Venue
				label = new Cell()
						.add(new Paragraph(venueLabel + " : ").setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(venue).toUpperCase()).setFont(itemFont).setFontColor(headingColor)
								.setFontSize(getFontSize(itemFontSize, 15)).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (mobileNo != null && mobileNo.trim().length() != 0) {
				// Mobile No.
				label = new Cell()
						.add(new Paragraph(phone + " : ").setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
																														// y,
																														// width
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(mobileNo)).setFont(itemFont).setFontColor(headingColor)
								.setFontSize(getFontSize(itemFontSize, 15)).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (foodNotesName != null && foodNotesName.toString().trim().length() != 0) {
				// Food
				label = new Cell()
						.add(new Paragraph(fNote + " : ").setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(foodNotesName.toString()).toUpperCase()
								+ (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")")).setFont(itemFont)
								.setFontColor(contentColor).setFontSize(getFontSize(itemFontSize, 15))
								.setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark != "") {
				label = new Cell()
						.add(new Paragraph(remarks + " : ").setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(remark)).setFont(itemFont).setFontColor(headingColor)
								.setFontSize(getFontSize(itemFontSize, 15)).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (service.trim().length() != 0) {
				label = new Cell()
						.add(new Paragraph(serviceLabel + " : ").setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(service)).setFont(itemFont).setFontColor(headingColor)
								.setFontSize(getFontSize(itemFontSize, 15)).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}

			if (theme.trim().length() != 0) {
				label = new Cell()
						.add(new Paragraph(themeLabel + " : ").setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 15)).setTextAlignment(TextAlignment.RIGHT))
						.setPadding(3f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(lineColor, 1f)); // x,
				eventDetail.addCell(label);

				value = new Cell()
						.add(new Paragraph(safeText(theme)).setFont(itemFont).setFontColor(headingColor)
								.setFontSize(getFontSize(itemFontSize, 15)).setTextAlignment(TextAlignment.LEFT))
						.setPadding(3f).setPaddingLeft(15f).setBorder(Border.NO_BORDER)
						.setBorderBottom(new SolidBorder(lineColor, 1f));
				eventDetail.addCell(value);
			}
			document.add(eventDetail);

			String fn_name = "";

			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Table funTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }), false);
				funTable.setWidth(UnitValue.createPercentValue(100f));
				funTable.setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1f));
				funTable.setMarginBottom(20f);
				funTable.setMarginTop(20f);

				// 1st row
				String mainFunction = eventFunctionMasterResponseDto.getMainFunction() != null
						? eventFunctionMasterResponseDto.getMainFunction()
						: "";

				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(mainFunction)
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 22)).setFontColor(headingColor)
							.setUnderline());
					Cell mainFunctionCell = new Cell(1, 2).add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					funTable.addCell(mainFunctionCell);
				}

				String foodType = eventFunctionMasterResponseDto.getFoodType() != null
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";
				String rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate().toString()
						: "";
				String ratePostFix = eventFunctionMasterResponseDto.getRatePostFix() != null
						? " (" + rate + " " + eventFunctionMasterResponseDto.getRatePostFix() + ") "
						: "";
				value = new Cell()
						.add(new Paragraph(
								safeText(eventFunctionMasterResponseDto.getFunctionName() + ratePostFix).toUpperCase()
										+ " | "
										+ safeText(eventFunctionMasterResponseDto.getPax().toString() + foodType))
								.setFont(catFontBold).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT))
						.setPadding(0).setPadding(3f).setBorder(Border.NO_BORDER);
				funTable.addCell(value);

				label = new Cell()
						.add(new Paragraph().add(startDate + " : ").setFont(catFontBold).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.RIGHT)
								.add(new Text(eventFunctionMasterResponseDto.getFunctionStartTimestamp().toUpperCase())
										.setFont(catFont))
								.setFontColor(headingColor))
						.setPadding(0).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT); // x, y, width
				funTable.addCell(label);

				if (req.getIsWithPrice() == 1) {

					String text;

					if (eventFunctionMasterResponseDto.getIsPackage()) {
						text = pckPrice + eventFunctionMasterResponseDto.getPackagePrice();
					} else {
						text = price + eventFunctionMasterResponseDto.getRate();
					}

					value = new Cell()
							.add(new Paragraph(text).setFont(catFontBold).setFontColor(headingColor)
									.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT))
							.setPadding(3f).setBorder(Border.NO_BORDER)
							.setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1f));

					funTable.addCell(value);
				}

				label = new Cell()
						.add(new Paragraph().add(endDate + " : ").setFont(catFontBold).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 14))
								.add(new Text(eventFunctionMasterResponseDto.getFunctionEndTimestamp().toUpperCase())
										.setFont(catFont))
								.setFontColor(headingColor))

						.setPadding(0).setBorder(Border.NO_BORDER)
						.setTextAlignment(eventFunctionMasterResponseDto.getIsPackage() == true ? TextAlignment.RIGHT
								: TextAlignment.LEFT); // x, y, width
				funTable.addCell(label);

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				label = new Cell()
						.add(new Paragraph().add(venueLabel + " : ").setFont(catFontBold).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 14)).add(new Text(functionVenue).setFont(catFont))
								.setFontColor(headingColor))

						.setPadding(0).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT); // x, y, width
				funTable.addCell(label);

				label = new Cell()
						.add(new Paragraph().add(remarks + " : ").setFont(catFontBold).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 14)).add(new Text(functionNotes).setFont(catFont))
								.setFontColor(headingColor))

						.setPadding(0).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT); // x, y, width
				funTable.addCell(label);

				document.add(funTable);

				Table table = new Table(1);
				table.setBorder(Border.NO_BORDER);
				table.setWidth(UnitValue.createPercentValue(100));

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					Div div = new Div();
					div.setKeepTogether(true);
					div.setBorder(Border.NO_BORDER);
					div.setPaddingTop(10f);

					Paragraph p1 = new Paragraph(safeText(menu.getNameEnglish()).toUpperCase()).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 18)).setFontColor(contentColor)
							.setTextAlignment(TextAlignment.CENTER).setPadding(0).setFixedLeading(16f);
					div.add(p1).setBorder(Border.NO_BORDER);

					String text = "";
					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = " \"" + formatText(menu.getSlogan(), lang) + "\"";
							div.add(new Paragraph(text).setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 13))
									.setTextAlignment(TextAlignment.CENTER).setFontColor(contentColor)
									.setFixedLeading(13f)).setBorder(Border.NO_BORDER);
						}
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							div.add(new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 13))
									.setFontColor(contentColor).setTextAlignment(TextAlignment.CENTER)
									.setFixedLeading(13f)).setBorder(Border.NO_BORDER);
						}
					}
					div.add(new Paragraph("").setBorderBottom(new SolidBorder(lightGrey, 1f)));

					if (isCategoryImage == 1) {
						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));
									img.setWidth(140);
									img.setHeight(100);
									img.setAutoScale(true);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									div.add(img).setBorder(Border.NO_BORDER);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						div.add(new Paragraph(safeText(item.getNameEnglish())).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 16)).setTextAlignment(TextAlignment.CENTER)
								.setFontColor(headingColor).setMarginTop(3f).setPadding(0).setFixedLeading(14f))
								.setBorder(Border.NO_BORDER);

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = " \"" + formatText(item.getSlogan(), lang) + "\"";
								div.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 13)).setFontColor(headingColor)
										.setTextAlignment(TextAlignment.CENTER).setFixedLeading(13f))
										.setBorder(Border.NO_BORDER);

							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								div.add(new Paragraph(text).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 13))
										.setFontColor(headingColor).setTextAlignment(TextAlignment.CENTER)
										.setFixedLeading(13f)).setBorder(Border.NO_BORDER);

							}
						}
					}
					table.addCell(new Cell().add(div).setBorder(Border.NO_BORDER).setPadding(0));
				}
				document.add(table);
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// ============ EXTRA CHARGES PAGE (before TnC) ============
			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userid);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ── Set background for this page (same watermark as content pages) ──
					int extraPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(extraPageNum, watermarkBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.CENTER).setUnderline()
							// .setBackgroundColor(new DeviceRgb(255, 255, 0)) // yellow highlight
							.setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// ── Heading title ─────────────────────────────────────────
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16))
								.setFontColor(headingColor).setUnderline().setTextAlignment(TextAlignment.LEFT)
								.setMarginTop(12f).setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(headingColor).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, contentColor, catFontSize, headingColor));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 13))
								.setFontColor(headingColor).setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);

					// Update lastPageNum so TnC/last page numbering stays correct
					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ============ Terms And Condition PAGE ============
			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			// ============ LAST PAGE ============
//			bgHandler.setPageBackground(lastPageNum, lastBgData);
//			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//			invisibleContent = new Paragraph("\u00A0");
//			invisibleContent.setFontSize(1);
//			document.add(invisibleContent);

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 10,
						TextAlignment.CENTER);

				canvas.close();
			}

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, whiteBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(contentColor);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(headingColor);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public List<MenuItemPartyMasterResponseDto> getSelectedMenuItemByEventFunctionid(Long eventId, Long eventFunctionId,
			List<Long> partyIds) {

		List<Object[]> detailsEntities;

		boolean hasPartyIds = partyIds != null && !partyIds.isEmpty();
		if (eventFunctionId != -1) {
			detailsEntities = hasPartyIds
					? menuPreparationDetailsRepository
							.getAllMenuPreparationDetailsByEventFunctionIdAndPartyIds(eventFunctionId, partyIds)
					: menuPreparationDetailsRepository.findByEventFunctionId(eventFunctionId);

		} else {
			detailsEntities = hasPartyIds
					? menuPreparationDetailsRepository.getAllMenuPreparationDetailsByEventIdAndPartyIds(eventId,
							partyIds)
					: menuPreparationDetailsRepository.findByEventId(eventId);

		}

		if (detailsEntities.isEmpty()) {
			return Collections.emptyList();
		}

		List<MenuItemPartyMasterResponseDto> dtos = new ArrayList<>();

		for (Object[] o : detailsEntities) {
			dtos.add(
					new MenuItemPartyMasterResponseDto(((Number) o[0]).longValue(), o[1] == null ? "" : o[1].toString(),
							o[3] == null ? "" : o[3].toString(), o[2] == null ? "" : o[2].toString(), ""));
		}

		return dtos;
	}

	@Override
	public List<EventAndFunctionWisePartyResponseDto> getAgencyByEventAndEventFunctionid(Long eventId,
			List<Long> eventFunctionIds, String type, Long userId) {

		if (type == null || type.trim().isEmpty()) {
			return Collections.emptyList();
		}

		List<EventAndFunctionWisePartyResponseDto> dtos;
		String normalizedType = type.trim();

		Boolean isFunctionWise = true;
		if (eventFunctionIds == null || eventFunctionIds.isEmpty()) {
			isFunctionWise = false;
		}
		if ("labour".equalsIgnoreCase(normalizedType)) {
			dtos = menuPreparationDetailsRepository.getLabourAgencyByEventFunctionId(eventId, eventFunctionIds, userId,
					isFunctionWise);
		} else if ("supplier".equalsIgnoreCase(normalizedType)) {
			dtos = menuPreparationDetailsRepository.getSupplierByEvent(eventId, userId);
		} else {
			dtos = menuPreparationDetailsRepository.getAgencyByEventFunctionId(eventId, eventFunctionIds,
					normalizedType, userId, isFunctionWise);
		}
		return dtos == null ? Collections.emptyList() : dtos;
	}

	public String formatDate(LocalDateTime dateTime) {
		return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
	}

	public String formatDate(String date) {
		DateTimeFormatter input = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		DateTimeFormatter output = DateTimeFormatter.ofPattern("dd_MM_yyyy");
		if (date == null || date.isEmpty()) {
			return "";
		}
		LocalDate localDate = LocalDate.parse(date, input);
		return localDate.format(output);
	}

	public List<RemaingDataResponseDto> getItemOfRemaingRawMaterial(Long userid, int lang) {

		List<Object[]> responseDtos = menuPreparationDetailsRepository.getItemOfRemaingRawMaterial(userid, lang);
		List<RemaingDataResponseDto> dataResponseDtos = new ArrayList<>();

		if (responseDtos == null) {
			return null;
		}

		for (Object[] row : responseDtos) {
			int index = 0;
			RemaingDataResponseDto dto = new RemaingDataResponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setTotalRawMaterial(commonService.getInteger(row[index++]));

			dataResponseDtos.add(dto);
		}

		return dataResponseDtos;
	}

	public List<RemaingDataResponseDto> getItemOfRemaingSlogan(Long userid, int lang) {
		List<Object[]> responseDtos = menuPreparationDetailsRepository.getItemOfRemaingSlogan(userid, lang);
		List<RemaingDataResponseDto> dataResponseDtos = new ArrayList<>();

		if (responseDtos == null) {
			return null;
		}

		for (Object[] row : responseDtos) {
			int index = 0;
			RemaingDataResponseDto dto = new RemaingDataResponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setSlogan(commonService.getString(row[index++]));

			dataResponseDtos.add(dto);
		}

		return dataResponseDtos;
	}

	public List<RemaingDataResponseDto> getItemOfRemaingRecipyRate(Long userid, int lang) {
		List<Object[]> responseDtos = menuPreparationDetailsRepository.getItemOfRemaingRecipyRate(userid, lang);
		List<RemaingDataResponseDto> dataResponseDtos = new ArrayList<>();

		if (responseDtos == null) {
			return null;
		}

		for (Object[] row : responseDtos) {
			int index = 0;
			RemaingDataResponseDto dto = new RemaingDataResponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setRawMaterialId(commonService.getLong(row[index++]));
			dto.setRawMaterialName(commonService.getString(row[index++]));
			dto.setRawMaterialRate(commonService.getBigDecimal(row[index++]));

			dataResponseDtos.add(dto);
		}

		return dataResponseDtos;
	}

	public List<RemaingDataResponseDto> getItemOfRemaingImage(Long userid, int lang) {
		List<Object[]> responseDtos = menuPreparationDetailsRepository.getItemOfRemaingImage(userid, lang);
		List<RemaingDataResponseDto> dataResponseDtos = new ArrayList<>();

		if (responseDtos == null) {
			return null;
		}

		for (Object[] row : responseDtos) {
			int index = 0;
			RemaingDataResponseDto dto = new RemaingDataResponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));

			dataResponseDtos.add(dto);
		}

		return dataResponseDtos;
	}

	@Transactional
	@Override
	public Boolean copyeventfunctionmenu(Long oldEventFunctionId, Long activeEventFunctionId) {

		// 1. OLD EVENT FUNCTION
		EventFunctionMasterEntity oldEventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(oldEventFunctionId)
				.orElseThrow(() -> new RuntimeException("Old Event Function Not Found"));

		// 2. OLD MENU PREPARATION
		MenuPreparationEntity oldMenuPreparation = menuPreparationRepository
				.findByEventFunctionAndIsDeleteFalse(oldEventFunction);

		if (oldMenuPreparation == null
				|| !menuPreparationDetailsRepository.existsByMenuPreparation(oldMenuPreparation)) {

			throw new RuntimeException("Old Event Function Menu Planning Not Exist");
		}

		ArrayList<Long> tobeAddedMenuItemIdList = new ArrayList<>();
		// 3. ACTIVE EVENT FUNCTION
		EventFunctionMasterEntity activeEventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(activeEventFunctionId)
				.orElseThrow(() -> new RuntimeException("Active Event Function Not Found"));

		// 4. ACTIVE MENU PREPARATION
		MenuPreparationEntity activeMenuPreparation = menuPreparationRepository
				.findByEventFunctionAndIsDeleteFalse(activeEventFunction);

		if (activeMenuPreparation != null) {

			// remove existing items
			menuPreparationDetailsRepository.deleteAllByMenuPreparation(activeMenuPreparation);

		} else {

			// create new menu
			activeMenuPreparation = new MenuPreparationEntity();
			activeMenuPreparation.setEventFunction(activeEventFunction);
			activeMenuPreparation.setCustomPackage(oldMenuPreparation.getCustomPackage());
			activeMenuPreparation.setDefaultPrice(oldMenuPreparation.getDefaultPrice());
			activeMenuPreparation.setIsDelete(false);
			activeMenuPreparation.setIsPackage(oldMenuPreparation.getIsPackage());
			activeMenuPreparation.setIsUpdate(true);
			activeMenuPreparation.setPackageName(oldMenuPreparation.getPackageName());
			activeMenuPreparation.setPackagePrice(oldMenuPreparation.getPackagePrice());
			activeMenuPreparation.setPax(activeEventFunction.getPax());
			activeMenuPreparation.setPrice(BigDecimal.valueOf(activeEventFunction.getRate()));

			Integer maxSortOrder = menuPreparationRepository.findMaxSortOrder(activeEventFunction.getId());
			activeMenuPreparation.setSortorder(maxSortOrder != null ? maxSortOrder + 1 : 1);

			activeMenuPreparation = menuPreparationRepository.save(activeMenuPreparation);
		}

		// 5. COPY DETAILS
		List<MenuPreparationDetailsEntity> oldDetails = menuPreparationDetailsRepository
				.findAllByMenuPreparation(oldMenuPreparation);

		List<MenuPreparationDetailsEntity> newDetails = new ArrayList<>();

		for (MenuPreparationDetailsEntity detail : oldDetails) {

			MenuPreparationDetailsEntity newDetail = new MenuPreparationDetailsEntity();

			newDetail.setMenuPreparation(activeMenuPreparation);
			newDetail.setItemPrice(detail.getItemPrice());
			newDetail.setItemSlogan(detail.getItemSlogan());
			newDetail.setItemSortOrder(detail.getItemSortOrder());
			newDetail.setMenuCategory(detail.getMenuCategory());
			newDetail.setMenuCategoryName(detail.getMenuCategoryName());
			newDetail.setMenuCategoryNameHindi(detail.getMenuCategoryNameHindi());
			newDetail.setMenuCategoryNameGujarati(detail.getMenuCategoryNameGujarati());
			newDetail.setMenuItem(detail.getMenuItem());
			newDetail.setMenuItemName(detail.getMenuItemName());
			newDetail.setMenuItemNameHindi(detail.getMenuItemNameHindi());
			newDetail.setMenuItemNameGujarati(detail.getMenuItemNameGujarati());
			newDetail.setMenuSlogan(detail.getMenuSlogan());
			newDetail.setMenuSortOrder(detail.getMenuSortOrder());
			newDetail.setIsItemAddons(detail.getIsItemAddons());
			newDetail.setIsMenuCatAddons(detail.getIsMenuCatAddons());
			newDetail.setCatSpace(detail.getCatSpace());
			newDetail.setSubCat(detail.getSubCat());
			newDetail.setItemSpace(detail.getItemSpace());
			newDetail.setSubItem(detail.getSubItem());
			newDetail.setCatNickNameEnglish(detail.getCatNickNameEnglish());
			newDetail.setCatNickNameHindi(detail.getCatNickNameHindi());
			newDetail.setCatNickNameGujarati(detail.getCatNickNameGujarati());
			newDetail.setItemNickNameEnglish(detail.getItemNickNameEnglish());
			newDetail.setItemNickNameHindi(detail.getItemNickNameHindi());
			newDetail.setItemNickNameGujarati(detail.getItemNickNameGujarati());
			newDetail.setIsCatImage(detail.getIsCatImage());
			tobeAddedMenuItemIdList.add(newDetail.getMenuItem().getId());
			newDetails.add(newDetail);
		}

		menuPreparationDetailsRepository.saveAll(newDetails);
		EventMasterEntity eventMasterEntity = activeEventFunction.getEvent();
		if (eventMasterEntity != null) {
			eventMasterEntity.setIsRMenu(false);
			eventMasterRepository.save(eventMasterEntity);
		}

		List<Long> tobeDeletedMenuItemIdList = new ArrayList<>();

		Map<String, List<Long>> manageData = new HashMap<>();
		manageData.put("delete", tobeDeletedMenuItemIdList);
		manageData.put("add", tobeAddedMenuItemIdList);
		menuPreparationHelperService.addDeleteEventRawMaterial(activeEventFunction.getEvent().getId(),
				activeEventFunction.getId(), manageData);
		return true;
	}

	@Override
	public String generateExclusiveReportType9(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req, Integer showAddonLabel) {

		try {
			PdfFont basicFont = null;
			PdfFont basicFontBold = null;

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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					permissableLabel = "", notPermissableLabel = "", themeLabel = "";

			int x = 185;
			int y = 625;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				permissableLabel = "अनुमत्य सामग्री";
				notPermissableLabel = "अस्वीकृत सामग्री";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					// Tamil
					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "நிகழ்ச்சி தேதி";
					eName = "நிகழ்ச்சி வகை";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					permissableLabel = "அனுமதிக்கப்பட்ட பொருட்கள்";
					notPermissableLabel = "அனுமதிக்கப்படாத பொருட்கள்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					// Telugu
					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "కార్యక్రమ తేదీ";
					eName = "కార్యక్రమ రకం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					permissableLabel = "అనుమతించదగిన వస్తువులు";
					notPermissableLabel = "అనుమతించని వస్తువులు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "പരിപാടിയുടെ തീയതി";
					eName = "പരിപാടിയുടെ തരം";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					permissableLabel = "അനുവദനീയമായ ഇനങ്ങൾ";
					notPermissableLabel = "അനുവദനീയമല്ലാത്ത ഇനങ്ങൾ";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रमाचा प्रकार";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					permissableLabel = "अनुमत्य वस्तू";
					notPermissableLabel = "अस्वीकृत वस्तू";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					permissableLabel = "માન્ય સામગ્રી";
					notPermissableLabel = "અમાન્ય સામગ્રી";
				}

				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				hostLabel = "Guest Name";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Type of Event";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preference";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				permissableLabel = "Permissable Items";
				notPermissableLabel = "Not Permissable Items";
				catFont = getFont(req.getCatFontId(), false, "times");
				catFontBold = getFont(req.getCatFontId(), true, "times");

				itemFont = getFont(req.getItemFontId(), false, "times");
				itemFontBold = getFont(req.getItemFontId(), true, "times");

				sloganFont = getFont(req.getSloganFontId(), false, "times");
				sloganFontBold = getFont(req.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color creamGold = new DeviceRgb(r2, g2, b2);
			Color softGold = new DeviceRgb(r1, g1, b1);
			Color descriptionFontColor = new DeviceRgb(r3, g3, b3);

//			Color creamGold = new DeviceRgb(0, 0, 0);
//			Color softGold = new DeviceRgb(0, 0, 0);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(170, 130, 60, 130);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/MainFront_Jay.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/Watermark_Jay.png");
//			ImageData tncPage = null;
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/MainLast_Jay.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null
					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());
			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");
			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			Paragraph invisibleContent = null;
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			}

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			if (isCompanyDetail == 1) {
				// ============ PAGE 1: Front Page (Main) ============
				// Add invisible content to ensure page 1 exists
				invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(creamGold);
				invisibleContent.setFontSize(35);
				invisibleContent.setPaddingTop(140f);
				invisibleContent.setPaddingLeft(165f);
				document.add(invisibleContent);

				int secondPageNum = pdfDocument.getNumberOfPages() + 1;
				bgHandler.setPageBackground(secondPageNum, detailsPage);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			}
			// Create the last page with minimal invisible content

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String billingName = "";
			String service = "";
			String theme = "";
			String permissable_item = "";
			String not_permissable_item = "";

			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				permissable_item = eventDto.getPermissable_item() != null ? eventDto.getPermissable_item() : "";
				not_permissable_item = eventDto.getNot_permissable_item() != null ? eventDto.getNot_permissable_item()
						: "";

			}

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(10);

			// Styles
			Style labelStyle = new Style().setFont(catFontBold).setFontColor(softGold)
					.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);

			Style valueStyle = new Style().setFont(itemFont).setFontColor(descriptionFontColor)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER);

			// Common Method
			BiConsumer<String, String> addRow = (label, value) -> {

				// Label Cell
				Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
						.setBorder(Border.NO_BORDER).setPaddingTop(9).setPaddingBottom(3)
						.setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(labelCell);

				// Value Cell
				Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
						.setBorder(Border.NO_BORDER).setPaddingBottom(10).setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(valueCell);
			};

			if (req.getIsPartyDetails() != null && req.getIsPartyDetails() != 0) { // Party Name
				addRow.accept(hostLabel, hostName);

				if (billingName != null && billingName.trim().length() != 0) {
					// Billing Name
					addRow.accept(billingNameLabel, billingName);
				}

				// Mobile No
				addRow.accept(phone, mobileNo);
			}
			/*
			 * if (eventStartDate != null && eventStartDate.trim().length() != 0 &&
			 * eventEndDate != null && eventEndDate.trim().length() != 0) { // Event Date
			 * addRow.accept(eDate, eventStartDate + " - " + eventEndDate); }
			 */

			if (eventStartDate != null && !eventStartDate.isEmpty() && eventEndDate != null
					&& !eventEndDate.isEmpty()) {

				if (eventStartDate.equals(eventEndDate)) {
					addRow.accept(eDate, eventStartDate);
				} else {
					addRow.accept(eDate, eventStartDate + " - " + eventEndDate);
				}
			}

			// Event Name
			addRow.accept(eName, eventName);

			if (venue != null && venue.trim().length() != 0) {
				// Venue
				addRow.accept(venueLabel, venue);
			}
			// Remarks

			String notes = foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")");

			if (notes != null && notes.trim().length() != 0) {
				// Food Notes
				addRow.accept(fNote, notes);
			}

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark.trim().length() != 0) {
				addRow.accept(remarks, remark);
			}

			if (service.trim().length() != 0) {
				addRow.accept(serviceLabel, service);
			}

			if (theme.trim().length() != 0) {
				addRow.accept(themeLabel, theme);
			}
			document.add(detailTable);

			EventFunctionRawMaterialPermissionResponseDto permissionResponseDto = getEventFunctionPermissionRawMaterial(
					eventId, eventFunctionId, userId);

			boolean hasPermissables = permissionResponseDto.getPermissables() != null
					&& !permissionResponseDto.getPermissables().isEmpty();

			boolean hasNotPermissables = permissionResponseDto.getNotPermissables() != null
					&& !permissionResponseDto.getNotPermissables().isEmpty();

			if (hasPermissables || hasNotPermissables) {

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
						.setMarginTop(10f);

				BiConsumer<String, String> addItemRow = (label, value) -> {
					Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
							.setBorder(Border.NO_BORDER).setPaddingTop(15f).setPaddingBottom(2f)
							.setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(labelCell);

					Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
							.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(valueCell);
				};

				if (hasPermissables) {
					addItemRow.accept(permissableLabel, "");

					String permissables = permissionResponseDto.getPermissables().stream()
							.map(item -> getRawMaterialNameByLang(item, lang)).collect(Collectors.joining(", "));

					addItemRow.accept("", permissables);
				}

				if (hasNotPermissables) {
					addItemRow.accept(notPermissableLabel, "");

					String notPermissables = permissionResponseDto.getNotPermissables().stream()
							.map(item -> getRawMaterialNameByLang(item, lang)).collect(Collectors.joining(", "));

					addItemRow.accept("", notPermissables);
				}

				document.add(itemTable);
			}

//			Paragraph cmpNameContent = new Paragraph(cmpName.toString())
//				    .setFont(basicFont)
//				    .setFontColor(softGold)
//				    .setFontSize(18f)
//				    .setTextAlignment(TextAlignment.CENTER)
//				    .setFixedPosition(0, y-=70, pageWidth);
//				document.add(cmpNameContent);

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(softGold);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 49f, 2f, 49f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(20f);

				// 1st row
				String mainFunction = eventFunctionMasterResponseDto.getMainFunction() != null
						? eventFunctionMasterResponseDto.getMainFunction()
						: "";
				// 1st row
				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph()
							.add(new com.itextpdf.layout.element.Text(mainFunction).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
					Cell mainFunctionCell = new Cell(1, 3).add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(mainFunctionCell);
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				String rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate().toString()
						: "";
				String ratePostFix = eventFunctionMasterResponseDto.getRatePostFix() != null
						? " (" + rate + " " + eventFunctionMasterResponseDto.getRatePostFix() + ") "
						: "";
				Paragraph eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(functionTitle + ratePostFix).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
				Cell eventCell = new Cell(1, 3).add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// 2ed row
				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(personLabel).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(person + foodType)
						.setFont(itemFont).setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 3rd raw
				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(eTime).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(date + " " + time)
						.setFont(itemFont).setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				if (req.getIsWithPrice() == 1) {

					String label;
					String value;

					if (eventFunctionMasterResponseDto.getIsPackage()) {
						label = pckPrice;
						value = eventFunctionMasterResponseDto.getPackagePrice().toString();
					} else {
						label = price;
						value = eventFunctionMasterResponseDto.getRate().toString();
					}

					// Label Cell
					eventPara = new Paragraph().add(new Text(label).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.RIGHT);
					headerTable.addCell(eventCell);

					// Colon Cell
					eventPara = new Paragraph().add(new Text(":").setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(eventCell);

					// Value Cell
					eventPara = new Paragraph().add(new Text(value).setFont(itemFont)
							.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);
				}

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				// Label Cell
				eventPara = new Paragraph().add(new Text(venueLabel).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				// Colon Cell
				eventPara = new Paragraph().add(new Text(":").setFont(catFont).setFontSize(getFontSize(catFontSize, 16))
						.setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// Value Cell
				eventPara = new Paragraph().add(new Text(functionVenue).setFont(itemFont)
						.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				if (functionNotes != null && !functionNotes.trim().isEmpty()) {
					// Label Cell
					eventPara = new Paragraph().add(new Text(remarks).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.RIGHT);
					headerTable.addCell(eventCell);

					// Colon Cell
					eventPara = new Paragraph().add(new Text(":").setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(eventCell);

					// Value Cell
					eventPara = new Paragraph().add(new Text(functionNotes).setFont(itemFont)
							.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);

				}

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}

					Div menuContent = new Div();
//					menuContent.setKeepTogether(true);

					Paragraph p = new Paragraph(formatText(menu.getNameEnglish(), lang)
							+ (showAddonLabel == 1 && menu.getIsAddOnCat() ? " (Add On)" : "")).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 20)).setFixedLeading(17f).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setPaddingLeft(0).setPaddingRight(0);

					Table table = new Table(1);
					table.setBorder(Border.NO_BORDER);
					table.setWidth(UnitValue.createPercentValue(100));
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
					table.setMarginBottom(5f);

					menuContent.add(table).setMarginTop(20f);
					String text = "";

					// Sub Category
					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);
						menuContent.add(new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 12))
								.setMultipliedLeading(1.2f).setFontColor(descriptionFontColor).setMarginBottom(0)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							menuContent.add(new Paragraph(text).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionFontColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = " \"" + formatText(menu.getSlogan(), lang) + "\"";
							menuContent.add(
									new Paragraph(text).setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 12))
											.setMultipliedLeading(1.2f).setTextAlignment(TextAlignment.CENTER)
											.setMarginTop(0).setFontColor(descriptionFontColor).setMarginLeft(0));
						}
					}

					if (isCategoryImage == 1) {
						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

									img.setWidth(140);
									img.setHeight(100);

									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					// Add one blank line before items
					menuContent.add(new Paragraph(" ").setFontSize(5f).setMarginTop(0).setMarginBottom(0));

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String subItem = "";
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)
								+ (showAddonLabel == 1 && item.getIsAddOnItem() ? " (Add On)" : "")).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 16)).setFixedLeading(15f)
								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0));

						// Sub Item
						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);
							menuContent.add(new Paragraph(text).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionFontColor).setMarginLeft(18).setMarginTop(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
						}

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = " \"" + formatText(item.getSlogan(), lang) + "\"";
								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionFontColor).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								menuContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionFontColor).setMarginLeft(18).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

					}

					document.add(menuContent);
				}

			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// ============ EXTRA CHARGES PAGE (before TnC) ============

			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ============================================================
					// Set background for Extra Charges page
					// ============================================================

					int extraPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(extraPageNum, watermarkBgData);

					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ============================================================
					// MAIN TITLE: "-: EXTRA CHARGE :-"
					// ============================================================

					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setMarginBottom(15f);

					document.add(extraTitle);

					// ============================================================
					// LOOP EACH HEADING
					// ============================================================

					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// ========================================================
						// KEEP HEADING + TABLE + TOTAL TOGETHER
						// ========================================================

						Div headingBlock = new Div();
						headingBlock.setKeepTogether(true);

						// ========================================================
						// HEADING TITLE
						// ========================================================

						String headingName = heading.getHeadingName() != null ? heading.getHeadingName().toUpperCase()
								: "";

						Paragraph headingTitle = new Paragraph(headingName).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold).setUnderline()
								.setTextAlignment(TextAlignment.LEFT).setMarginTop(12f).setMarginBottom(6f);

						headingBlock.add(headingTitle);

						// ========================================================
						// TABLE
						// ========================================================

						float[] colWidths = { 15f, // DATE
								17f, // START TIME
								15f, // END TIME
								13f, // SESSION
								12f, // QTY
								12f, // RATE
								16f // TOTAL
						};

						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));

						chargeTable.setWidth(UnitValue.createPercentValue(100));

						chargeTable.setBorder(new SolidBorder(softGold, 1f));

						// ========================================================
						// HEADER ROW
						// ========================================================

						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };

						for (String h : colHeaders) {

							Cell headerCell = new Cell();

							Paragraph headerParagraph = new Paragraph(h).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 12)).setFontColor(softGold).setUnderline();

							headerCell.add(headerParagraph).setTextAlignment(TextAlignment.CENTER)
									.setBorder(new SolidBorder(softGold, 0.8f)).setPaddingTop(5f).setPaddingBottom(5f);

							chargeTable.addHeaderCell(headerCell);
						}

						// ========================================================
						// DATA ROWS
						// ========================================================

						if (heading.getRows() != null && !heading.getRows().isEmpty()) {

							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								// ------------------------------------------------
								// DATE
								// ------------------------------------------------

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// START TIME
								// ------------------------------------------------

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// END TIME
								// ------------------------------------------------

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// SESSION
								// ------------------------------------------------

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// QTY / PERSON ITEM
								// ------------------------------------------------

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// RATE
								// ------------------------------------------------

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));

								// ------------------------------------------------
								// TOTAL
								// ------------------------------------------------

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));
							}

						} else {

							// ====================================================
							// EMPTY ROWS FOR VISUAL LAYOUT
							// ====================================================

							for (int i = 0; i < 7; i++) {

								for (int j = 0; j < 7; j++) {

									chargeTable.addCell(dataCell("", catFont, creamGold, catFontSize, softGold));
								}
							}
						}

						// ========================================================
						// ADD TABLE TO KEEP-TOGETHER BLOCK
						// ========================================================

						headingBlock.add(chargeTable);

						// ========================================================
						// HEADING TOTAL
						// ========================================================

						String headingTotal = heading.getHeadingTotal() != null
								? heading.getHeadingTotal().toPlainString()
								: "0";

						Paragraph headingTotalPara = new Paragraph("Total: " + headingTotal).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 13)).setFontColor(softGold)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f).setMarginBottom(0f);

						headingBlock.add(headingTotalPara);

						// ========================================================
						// ADD COMPLETE BLOCK TO DOCUMENT
						// ========================================================

						document.add(headingBlock);
					}

					// ============================================================
					// GRAND TOTAL
					// ============================================================

					String grandTotal = extraCharges.getGrandTotal() != null
							? extraCharges.getGrandTotal().toPlainString()
							: "0";

					Paragraph grandTotalPara = new Paragraph("Grand Total: " + grandTotal).setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();

					document.add(grandTotalPara);

					// ============================================================
					// UPDATE LAST PAGE NUMBER
					// ============================================================

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}
			// ============ Terms And Condition PAGE ============
			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);
			}

			if (isCompanyDetail == 1) {
				lastPageNum = pdfDocument.getNumberOfPages() + 1;
				// ============ LAST PAGE ============
				bgHandler.setPageBackground(lastPageNum, lastBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, whiteBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType15(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {

		try {
			PdfFont basicFont = null;
			PdfFont basicFontBold = null;

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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "";

			int x = 185;
			int y = 625;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					// Tamil
					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "நிகழ்ச்சி தேதி";
					eName = "நிகழ்ச்சி வகை";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					// Telugu
					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "కార్యక్రమ తేదీ";
					eName = "కార్యక్రమ రకం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "പരിപാടിയുടെ തീയതി";
					eName = "പരിപാടിയുടെ തരം";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रमाचा प्रकार";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
				}

				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				hostLabel = "Party Name";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Type of Event";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preparations";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";

				catFont = getFont(req.getCatFontId(), false, "times");
				catFontBold = getFont(req.getCatFontId(), true, "times");

				itemFont = getFont(req.getItemFontId(), false, "times");
				itemFontBold = getFont(req.getItemFontId(), true, "times");

				sloganFont = getFont(req.getSloganFontId(), false, "times");
				sloganFontBold = getFont(req.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color creamGold = new DeviceRgb(r2, g2, b2);
			Color softGold = new DeviceRgb(r1, g1, b1);
			Color descriptionFontColor = new DeviceRgb(r3, g3, b3);

//			Color creamGold = new DeviceRgb(0, 0, 0);
//			Color softGold = new DeviceRgb(0, 0, 0);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(170, 130, 80, 130);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/MainFront_Jay.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/Watermark_Jay.png");
//			ImageData tncPage = null;
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/FrontDetail_Jay.png");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/MainLast_Jay.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null
					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());
			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");
			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			Paragraph invisibleContent = null;
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			}

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			if (isCompanyDetail == 1) {
				// ============ PAGE 1: Front Page (Main) ============
				// Add invisible content to ensure page 1 exists
				invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(creamGold);
				invisibleContent.setFontSize(35);
				invisibleContent.setPaddingTop(140f);
				invisibleContent.setPaddingLeft(165f);
				document.add(invisibleContent);

				int secondPageNum = pdfDocument.getNumberOfPages() + 1;
				bgHandler.setPageBackground(secondPageNum, detailsPage);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			}
			// Create the last page with minimal invisible content

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(10);

			// Styles
			Style labelStyle = new Style().setFont(catFontBold).setFontColor(softGold)
					.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);

			Style valueStyle = new Style().setFont(itemFont).setFontColor(descriptionFontColor)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER);

			// Common Method
			BiConsumer<String, String> addRow = (label, value) -> {

				// Label Cell
				Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
						.setBorder(Border.NO_BORDER).setPaddingTop(9).setPaddingBottom(3)
						.setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(labelCell);

				// Value Cell
				Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
						.setBorder(Border.NO_BORDER).setPaddingBottom(10).setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(valueCell);
			};

			// Party Name
			addRow.accept(hostLabel, hostName);

			if (billingName != null && billingName.trim().length() != 0) {
				// Billing Name
				addRow.accept(billingNameLabel, billingName);
			}

			// Mobile No
			addRow.accept(phone, mobileNo);

			if (eventStartDate != null && eventStartDate.trim().length() != 0 && eventEndDate != null
					&& eventEndDate.trim().length() != 0) {
				// Event Date
				addRow.accept(eDate, eventStartDate + " - " + eventEndDate);
			}

			// Event Name
			addRow.accept(eName, eventName);

			if (venue != null && venue.trim().length() != 0) {
				// Venue
				addRow.accept(venueLabel, venue);
			}
			// Remarks

			String notes = foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")");

			if (notes != null && notes.trim().length() != 0) {
				// Food Notes
				addRow.accept(fNote, notes);
			}

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark.trim().length() != 0) {
				addRow.accept(remarks, remark);
			}

			if (service.trim().length() != 0) {
				addRow.accept(serviceLabel, service);
			}

			if (theme.trim().length() != 0) {
				addRow.accept(themeLabel, theme);
			}

			document.add(detailTable);

//			Paragraph cmpNameContent = new Paragraph(cmpName.toString())
//				    .setFont(basicFont)
//				    .setFontColor(softGold)
//				    .setFontSize(18f)
//				    .setTextAlignment(TextAlignment.CENTER)
//				    .setFixedPosition(0, y-=70, pageWidth);
//				document.add(cmpNameContent);

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(softGold);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 49f, 2f, 49f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(20f);

				// 1st row
				String mainFunction = eventFunctionMasterResponseDto.getMainFunction() != null
						? eventFunctionMasterResponseDto.getMainFunction()
						: "";
				// 1st row
				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph()
							.add(new com.itextpdf.layout.element.Text(mainFunction).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
					Cell mainFunctionCell = new Cell(1, 3).add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(mainFunctionCell);
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				String rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate().toString()
						: "";
				String ratePostFix = eventFunctionMasterResponseDto.getRatePostFix() != null
						? " (" + rate + " " + eventFunctionMasterResponseDto.getRatePostFix() + ") "
						: "";
				Paragraph eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(functionTitle + ratePostFix).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
				Cell eventCell = new Cell(1, 3).add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// 2ed row
				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(personLabel).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(person + foodType)
						.setFont(itemFont).setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 3rd raw
				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(eTime).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(date + " " + time)
						.setFont(itemFont).setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				if (req.getIsWithPrice() == 1) {

					String label;
					String value;

					if (eventFunctionMasterResponseDto.getIsPackage()) {
						label = pckPrice;
						value = eventFunctionMasterResponseDto.getPackagePrice().toString();
					} else {
						label = price;
						value = eventFunctionMasterResponseDto.getRate().toString();
					}

					// Label Cell
					eventPara = new Paragraph().add(new Text(label).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.RIGHT);
					headerTable.addCell(eventCell);

					// Colon Cell
					eventPara = new Paragraph().add(new Text(":").setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(eventCell);

					// Value Cell
					eventPara = new Paragraph().add(new Text(value).setFont(itemFont)
							.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);
				}

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				// Label Cell
				eventPara = new Paragraph().add(new Text(venueLabel).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				// Colon Cell
				eventPara = new Paragraph().add(new Text(":").setFont(catFont).setFontSize(getFontSize(catFontSize, 16))
						.setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// Value Cell
				eventPara = new Paragraph().add(new Text(functionVenue).setFont(itemFont)
						.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// Label Cell
				eventPara = new Paragraph().add(new Text(remarks).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				// Colon Cell
				eventPara = new Paragraph().add(new Text(":").setFont(catFont).setFontSize(getFontSize(catFontSize, 16))
						.setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// Value Cell
				eventPara = new Paragraph().add(new Text(functionNotes).setFont(itemFont)
						.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
//				for (MenuReportResponseDto menu : menuReportResponseDtos) {
//
//					String subCat = "";
//					if (lang == 1) {
//						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
//					} else if (lang == 2) {
//						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
//					} else {
//						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
//					}
//
//					Div menuContent = new Div();
//					menuContent.setKeepTogether(true);
//
//					// ================= SUB CATEGORY =================
//					if (!subCat.trim().isEmpty()) {
//						menuContent.add(new Paragraph(formatText(subCat, lang)).setFont(catFont)
//								.setFontSize(getFontSize(catFontSize, 12)).setMultipliedLeading(1.2f)
//								.setFontColor(descriptionFontColor).setTextAlignment(TextAlignment.CENTER)
//								.setMarginTop(0).setMarginBottom(0));
//					}
//
//					// ================= CATEGORY NAME + INSTRUCTION =================
//					String categoryInstruction = "";
//
//					if (isCategoryInstruction != null && isCategoryInstruction == 1 && menu.getMenuNotes() != null
//							&& !menu.getMenuNotes().trim().isEmpty()) {
//
//						categoryInstruction = formatText(menu.getMenuNotes(), lang);
//					}
//
//					Paragraph categoryParagraph = new Paragraph().add(new Text(formatText(menu.getNameEnglish(), lang))
//							.setFont(catFont).setFontSize(getFontSize(catFontSize, 20)).setFontColor(softGold));
//
//					if (!categoryInstruction.isEmpty()) {
//						categoryParagraph.add(new Text("    " + categoryInstruction).setFont(catFont)
//								.setFontSize(getFontSize(catFontSize, 12)).setFontColor(descriptionFontColor));
//					}
//
//					categoryParagraph.setTextAlignment(TextAlignment.CENTER).setUnderline().setFixedLeading(17f);
//
//					Table table = new Table(1);
//					table.setBorder(Border.NO_BORDER);
//					table.setWidth(UnitValue.createPercentValue(100));
//					table.addCell(new Cell().add(categoryParagraph).setBorder(Border.NO_BORDER));
//
//					table.setMarginBottom(5f);
//					menuContent.add(table).setMarginTop(20f);
//
//					// ================= CATEGORY SLOGAN =================
//					if (isCategorySlogan != null && isCategorySlogan == 1 && menu.getSlogan() != null
//							&& !menu.getSlogan().isEmpty()) {
//
//						menuContent.add(new Paragraph("\"" + formatText(menu.getSlogan(), lang) + "\"")
//								.setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 12))
//								.setMultipliedLeading(1.2f).setTextAlignment(TextAlignment.CENTER)
//								.setFontColor(descriptionFontColor).setMarginTop(0));
//					}
//
//					// ================= CATEGORY IMAGE =================
//					if (isCategoryImage != null && isCategoryImage == 1 && menu.getImagePath() != null
//							&& !menu.getImagePath().trim().isEmpty()) {
//
//						try {
//							Image img = new Image(ImageDataFactory
//									.create(environment.getProperty("app.image.url") + menu.getImagePath()));
//
//							img.setAutoScale(true);
//							img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//							img.setMarginTop(5);
//							img.setMarginBottom(5);
//
//							menuContent.add(img);
//
//						} catch (Exception e) {
//							// Ignore image loading errors
//						}
//					}
//
//					// ================= MENU ITEMS =================
//					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
//
//						String subItem = "";
//						if (lang == 1) {
//							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
//						} else if (lang == 2) {
//							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
//						} else {
//							subItem = item.getSubItem() != null ? item.getSubItem() : "";
//						}
//						String itemHeading = item.getItemHeading();
//						
//
//						if (itemHeading != null && !itemHeading.trim().isEmpty()) {
//							menuContent.add(new Paragraph(formatText(itemHeading, lang)).setFont(catFont).setUnderline()
//									.setFontSize(getFontSize(catFontSize, 20)).setMultipliedLeading(1.2f)
//									.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER)
//									.setMarginTop(10f).setMarginBottom(5f));
//						}
//
//						// ---------- Sub Item ----------
//						if (!subItem.trim().isEmpty()) {
//							menuContent.add(new Paragraph(formatText(subItem, lang)).setFont(itemFont)
//									.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
//									.setFontColor(descriptionFontColor).setTextAlignment(TextAlignment.CENTER)
//									.setMarginTop(0).setMarginBottom(0));
//						}
//
//						// ---------- Item Instruction ----------
//						String itemInstruction = "";
//
//						if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
//								&& !item.getItemNotes().trim().isEmpty()) {
//
//							itemInstruction = formatText(item.getItemNotes(), lang);
//						}
//
//						// ---------- Item Name + Instruction ----------
//						Paragraph itemParagraph = new Paragraph().add(new Text(formatText(item.getNameEnglish(), lang))
//								.setFont(itemFont).setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold)).setMarginTop(10f);
//
//						if (!itemInstruction.isEmpty()) {
//							itemParagraph.add(new Text("\n" + itemInstruction).setFont(itemFont)
//									.setFontSize(getFontSize(itemFontSize, 12)).setFontColor(descriptionFontColor));
//						}
//
//						itemParagraph.setTextAlignment(TextAlignment.CENTER).setFixedLeading(15f)
//								.setMarginBottom(0);
//
//						menuContent.add(itemParagraph);
//
//						// ---------- Item Slogan ----------
//						if (isItemSlogan != null && isItemSlogan == 1 && item.getSlogan() != null
//								&& !item.getSlogan().isEmpty()) {
//
//							menuContent.add(new Paragraph("\"" + formatText(item.getSlogan(), lang) + "\"")
//									.setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 12))
//									.setMultipliedLeading(1.2f).setFontColor(descriptionFontColor)
//									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginBottom(0));
//						}
//					}
//
//					document.add(menuContent);
//				}

				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					// =========================================================
					// CATEGORY CONTAINER (keeps subCat + name + instruction + slogan + image + ALL
					// items together)
					// =========================================================
					Div menuContent = new Div();
					menuContent.setKeepTogether(true);
					menuContent.setWidth(UnitValue.createPercentValue(90));
					menuContent.setHorizontalAlignment(HorizontalAlignment.CENTER);
					menuContent.setMarginTop(20f);

					menuContent.add(buildCategoryHeader(menu, lang, catFont, catFontSize, sloganFont, sloganFontSize,
							descriptionFontColor, softGold, isCategoryInstruction, isCategorySlogan, isCategoryImage,
							environment));

					// ================= ITEMS =================
					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						menuContent.add(buildItemDiv(item, lang, catFont, catFontSize, itemFont, itemFontSize,
								sloganFont, sloganFontSize, descriptionFontColor, softGold, creamGold,
								isItemInstruction, isItemSlogan));
					}

					document.add(menuContent);
				}
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// ============ EXTRA CHARGES PAGE (before TnC) ============
			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ── Set background for this page (same watermark as content pages) ──
					int extraPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(extraPageNum, watermarkBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline()
							// .setBackgroundColor(new DeviceRgb(255, 255, 0)) // yellow highlight
							.setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// ── Heading title ─────────────────────────────────────────
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
								.setUnderline().setTextAlignment(TextAlignment.LEFT).setMarginTop(12f)
								.setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(softGold).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, creamGold, catFontSize, softGold));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 13)).setFontColor(softGold)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);

					// Update lastPageNum so TnC/last page numbering stays correct
					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ============ Terms And Condition PAGE ============
			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);
			}

			if (isCompanyDetail == 1) {
				lastPageNum = pdfDocument.getNumberOfPages() + 1;
				// ============ LAST PAGE ============
				bgHandler.setPageBackground(lastPageNum, lastBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, whiteBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType10(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request) {
		try {
			PdfFont basicFont = null;
			PdfFont basicFontBold = null;

			PdfFont catFont = null;
			PdfFont catFontBold = null;

			PdfFont itemFont = null;
			PdfFont itemFontBold = null;

			PdfFont sloganFont = null;
			PdfFont sloganFontBold = null;

			Integer catFontSize = request.getCatFontSize();
			Integer itemFontSize = request.getItemFontSize();
			Integer sloganFontSize = request.getSloganFontSize();

			loadLicense();

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", flowLabel = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "",
					serviceLabel = "", themeLabel = "";

			int x = 185;
			int y = 650;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = loadFont("/fonts/Nirmala.ttf");
				basicFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				flowLabel = "कार्यक्रम का प्रवाह";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					flowLabel = "நிகழ்ச்சியின் ஓட்டம்";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					flowLabel = "కార్యక్రమ ప్రవాహం";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					flowLabel = "പരിപാടിയുടെ പ്രവാഹം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					flowLabel = "कार्यक्रमाचा प्रवाह";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "તારીખ";
					eName = "કાર્યક્રમ";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					flowLabel = "કાર્યક્રમનો પ્રવાહ";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
				}

				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				hostLabel = "Party Name";
				phone = "Mobile";
				eDate = "Date";
				eName = "Event";
				venueLabel = "Venue";
				personLabel = "Pax";
				eTime = "Start Time";
				fNote = "Food Notes";
				flowLabel = "Flow of Function";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";

				catFont = getFont(request.getCatFontId(), false, "times");
				catFontBold = getFont(request.getCatFontId(), true, "times");

				itemFont = getFont(request.getItemFontId(), false, "times");
				itemFontBold = getFont(request.getItemFontId(), true, "times");

				sloganFont = getFont(request.getSloganFontId(), false, "times");
				sloganFontBold = getFont(request.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
//			Color creamGold = new DeviceRgb(r2, g2, b2);
//			Color softGold = new DeviceRgb(r1, g1, b1);

			Color creamGold = new DeviceRgb(0, 0, 0);
			Color softGold = new DeviceRgb(0, 0, 0);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(70, 130, 60, 130);

			// Load all background images upfront
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/Demo.png");
//			ImageData tncPage = null;
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/TncDemo.png");

			// Load all background images upfront
			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
//			bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";
			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}
			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			// ============ PAGE 1: Front Page (Main) ============
			ImageData logoData = loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo()); // e.g.
//			ImageData logoData = loadImageFromResource("/flipbook/pages/logo.png");

			Image logo = new Image(logoData);

			// Resize & align
			logo.scaleToFit(200, 100);
			logo.setAutoScale(false);
			logo.setTextAlignment(TextAlignment.CENTER);
			logo.setFixedPosition((PageSize.A4.getWidth() / 2) - 80, y);

			document.add(logo);

			// Party Name
			Paragraph hostNameContent = new Paragraph(safeText(hostName)).setFont(catFont).setFontColor(creamGold)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER)
					.setFixedPosition(0, y -= 40, pageWidth); // x,
			document.add(hostNameContent);

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(20);

			BiConsumer<String, Paragraph> addRow = (key, paragraph) -> {
				Cell cell1 = new Cell().add(paragraph).setBorder(Border.NO_BORDER).setPaddingTop(15)
						.setPaddingBottom(15).setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(cell1);
			};

			addRow.accept(billingName, new Paragraph(safeText(billingName)).setFont(catFont).setFontColor(creamGold)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER));

			addRow.accept("eventDate",
					new Paragraph().add(new Text(eDate + " "))
							.add(new Text(safeText(eventStartDate + " - " + eventEndDate)).setUnderline())
							.setFont(catFontBold).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
							.setTextAlignment(TextAlignment.CENTER));

			if (venue != null && venue.trim().length() != 0) {
				addRow.accept("venue",
						new Paragraph().add(new Text(venueLabel + " : ")).add(new Text(safeText(venue)))
								.setFont(catFont).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
								.setTextAlignment(TextAlignment.CENTER));
			}

			addRow.accept("eventName",
					new Paragraph().add(new Text(eName + " : ")).add(new Text(safeText(eventName))).setFont(catFont)
							.setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
							.setTextAlignment(TextAlignment.CENTER));

			if (request.getIsShowEventRemarks() == 1 && remarks != null && remarks.trim().length() != 0) {
				addRow.accept("remarks",
						new Paragraph().add(new Text(remarks + " : ")).add(new Text(safeText(remark))).setFont(catFont)
								.setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
								.setTextAlignment(TextAlignment.CENTER));
			}

			if (fNote != null && fNote.trim().length() != 0) {
				addRow.accept("foodTitle",
						new Paragraph(fNote.toUpperCase()).setUnderline().setFont(catFontBold).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER));
			}

			String notes = foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")");

			if (notes != null && notes.trim().length() != 0) {
				addRow.accept("foodContent", new Paragraph(safeText(notes)).setFont(itemFont).setFontColor(creamGold)
						.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER));
			}

			if (phone != null && phone.trim().length() != 0) {
				addRow.accept("mobileNo",
						new Paragraph().add(new Text(phone + " : ").setFont(catFontBold))
								.add(new Text(safeText(mobileNo)).setFont(catFont)).setFontColor(softGold)
								.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER));
			}

			addRow.accept("pax",
					new Paragraph().add(new Text(pax + " " + personLabel.toUpperCase())).setUnderline()
							.setFont(catFontBold).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
							.setTextAlignment(TextAlignment.CENTER));

			if (service.trim().length() != 0) {
				addRow.accept("Service",
						new Paragraph().add(new Text(serviceLabel + " " + service.toUpperCase())).setUnderline()
								.setFont(catFontBold).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
								.setTextAlignment(TextAlignment.CENTER));

			}

			if (theme.trim().length() != 0) {
				addRow.accept("Theme",
						new Paragraph().add(new Text(themeLabel + " " + theme.toUpperCase())).setUnderline()
								.setFont(catFontBold).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
								.setTextAlignment(TextAlignment.CENTER));

			}

			document.add(detailTable);

			document.add(new AreaBreak(AreaBreakType.NEXT_AREA));

			// Flow Label
			Paragraph data = new Paragraph(flowLabel.toUpperCase()).setTextAlignment(TextAlignment.CENTER)
					.setFont(catFontBold).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 20))
					.setMarginTop(20f);

			document.add(data);

			for (EventFunctionReportResponseDto fn : eventDto.getFunctions()) {
				data = new Paragraph(fn.getFunctionStartTimestamp()).setTextAlignment(TextAlignment.CENTER)
						.setFont(catFont).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 16));
				document.add(data);

				data = new Paragraph(formatText(fn.getFunctionName(), lang)).setTextAlignment(TextAlignment.CENTER)
						.setFont(catFont).setFontColor(softGold).setFontSize(getFontSize(catFontSize, 16));
				document.add(data);
			}

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(softGold);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 49f, 2f, 49f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(20f);

				// 1st row
				String mainFunction = eventFunctionMasterResponseDto.getMainFunction() != null
						? eventFunctionMasterResponseDto.getMainFunction()
						: "";
				// 1st row
				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph()
							.add(new com.itextpdf.layout.element.Text(mainFunction).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
					Cell mainFunctionCell = new Cell(1, 3).add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(mainFunctionCell);
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				String rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate().toString()
						: "";
				String ratePostFix = eventFunctionMasterResponseDto.getRatePostFix() != null
						? rate + " (" + eventFunctionMasterResponseDto.getRatePostFix() + ") "
						: "";
				Paragraph eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(functionTitle + ratePostFix).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
				Cell eventCell = new Cell(1, 3).add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// 2ed row
				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(personLabel).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(person + foodType)
						.setFont(itemFont).setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 3rd raw
				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(eTime).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");
				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(date + " " + time)
						.setFont(itemFont).setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				if (request.getIsWithPrice() == 1) {

					String label;
					String value;

					if (eventFunctionMasterResponseDto.getIsPackage()) {
						label = pckPrice;
						value = eventFunctionMasterResponseDto.getPackagePrice().toString();
					} else {
						label = price;
						value = eventFunctionMasterResponseDto.getRate().toString();
					}

					// Label Cell
					eventPara = new Paragraph().add(new Text(label).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.RIGHT);
					headerTable.addCell(eventCell);

					// Colon Cell
					eventPara = new Paragraph().add(new Text(":").setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(eventCell);

					// Value Cell
					eventPara = new Paragraph().add(new Text(value).setFont(itemFont)
							.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);
				}

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				// Label Cell
				eventPara = new Paragraph().add(new Text(venueLabel).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				// Colon Cell
				eventPara = new Paragraph().add(new Text(":").setFont(catFont).setFontSize(getFontSize(catFontSize, 16))
						.setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// Value Cell
				eventPara = new Paragraph().add(new Text(functionVenue).setFont(itemFont)
						.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// Label Cell
				eventPara = new Paragraph().add(new Text(remarks).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.RIGHT);
				headerTable.addCell(eventCell);

				// Colon Cell
				eventPara = new Paragraph().add(new Text(":").setFont(catFont).setFontSize(getFontSize(catFontSize, 16))
						.setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// Value Cell
				eventPara = new Paragraph().add(new Text(functionNotes).setFont(itemFont)
						.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold));

				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					Div menuContent = new Div();
					menuContent.setKeepTogether(true);

					Paragraph p = new Paragraph(formatText(menu.getNameEnglish(), lang)).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 20)).setFixedLeading(17f).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setPaddingLeft(0).setPaddingRight(0);

					Table table = new Table(1);
					table.setBorder(Border.NO_BORDER);
					table.setWidth(UnitValue.createPercentValue(100));
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
					table.setMarginBottom(5f);

					menuContent.add(table).setMarginTop(20f);
					String text = "";

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							menuContent
									.add(new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 12))
											.setMultipliedLeading(1.2f).setFontColor(softGold).setMarginBottom(0)
											.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = " \"" + formatText(menu.getSlogan(), lang) + "\"";
							menuContent.add(
									new Paragraph(text).setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 12))
											.setMultipliedLeading(1.2f).setTextAlignment(TextAlignment.CENTER)
											.setMarginTop(0).setFontColor(creamGold).setMarginLeft(0));
						}
					}

					if (isCategoryImage == 1) {
						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));
									img.setWidth(140);
									img.setHeight(100);
									img.setAutoScale(true);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 16)).setFixedLeading(15f)
								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0));

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = " \"" + formatText(item.getSlogan(), lang) + "\"";
								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(creamGold).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								menuContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(creamGold).setMarginLeft(18).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

					}

					document.add(menuContent);
				}

			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			Paragraph invisibleContent;
			// ============ Terms And Condition PAGE ============
			if (tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);
			}

			/* ================= FOOTER AND PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			String footer = cmpName + " / " + hostName + " / "
					+ (eventDate != null ? eventDate.split(" ")[0].replace("/", ".") : "") + " / " + venue;
			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned(footer, page.getPageSize().getWidth() / 2, 30, TextAlignment.CENTER);

				canvas.showTextAligned("Page " + i, page.getPageSize().getWidth() - 25, 80, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE, (float) Math.toRadians(90));

				canvas.close();
			}

			if (request.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, whiteBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	public List<OrderSummaryResponseDto> getDatewiseOrderSummaryReport(LocalDate firstDate, LocalDate lastDate,
			Long userid, int lang, List<Integer> eventStatus, List<Long> managerIds, Long partyId, String type) {

		List<OrderSummaryResponseDto> responseDtos = new ArrayList<>();

		Boolean flag = false;
		if (managerIds == null || managerIds.isEmpty()) {
			flag = true;
		}
		List<Object[]> result = new ArrayList<>();
		if (type.equalsIgnoreCase("type1")) {
			System.out.println("1");
			result = menuPreparationRepository.findDatewiseOrderSummary(firstDate, lastDate, userid, lang, eventStatus,
					managerIds, flag, partyId);
		} else if (type.equalsIgnoreCase("type2")) {
			System.out.println("2");
			result = menuPreparationRepository.findDatewiseOrderSummary2(firstDate, lastDate, userid, lang, eventStatus,
					managerIds, flag, partyId);
		} else {
			System.out.println("3");
			result = menuPreparationRepository.findDatewiseOrderSummary3(firstDate, lastDate, userid, lang, eventStatus,
					managerIds, flag, partyId);
		}
		for (Object[] row : result) {
			int index = 1;
			OrderSummaryResponseDto dto = new OrderSummaryResponseDto();

			dto.setEventDate(commonService.getString(row[index++]));
			dto.setEventName(commonService.getString(row[index++]));
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionPax(commonService.getString(row[index++]));
			Long eventId = commonService.getLong(row[0] != null ? row[0] : 0);
			Long eventfunctionId = commonService.getLong(row[9] != null ? row[9] : 0);
			List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
					.findBanquetByEventFunctionId(eventId, eventfunctionId);
			Object venue = row[index++];
			String functionVenue = commonService.getString(venue != null ? venue : "");
			if (!banquets.isEmpty()) {
				functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
						.collect(Collectors.joining(", "));
			}

			dto.setFunctionVenue(functionVenue);
			dto.setPartyName(commonService.getString(row[index++]));
			dto.setManagerName(commonService.getString(row[index++]));
			dto.setStatus(commonService.getString(row[index++]));
			dto.setEventNo(commonService.getString(row[10]));
			dto.setSession(banquets.isEmpty() ? "" : banquets.get(0).getShiftName());
			dto.setInquiryDate(commonService.getString(row[11]));

			String lastChangedBy = userLogsEntityRepository.getLastChangedName(eventId);
			if (lastChangedBy != null && lastChangedBy.trim().length() > 0) {
				dto.setLastUpdatedBy(lastChangedBy);
			} else {
				dto.setLastUpdatedBy(dto.getManagerName());
			}
			dto.setMobileNo(commonService.getString(row[12]));
			dto.setSubTotal(commonService.getInteger(row[13]));
			dto.setTransportation(commonService.getInteger(row[14]));
			dto.setGrandTotal(commonService.getInteger(row[15]));
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	// Add this public wrapper so MenuShareLinkServiceImpl can call it
	public List<MenuPreparationItemResponseDto> convertToItemDtoListPublic(List<Object[]> rows) {
		return convertToItemDtoList(rows);
	}

	@Override
	public String generateExclusiveReportType1Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request, Integer showAddOnLabel,
			Integer isAllItemTogether) {

		generateExclusiveReportType1(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction, isCategoryImage,
				isItemSlogan, isItemInstruction, re, lang, isCompanyDetails, adminTemplateModuleId, userid,
				adminTemplate, request, showAddOnLabel, isAllItemTogether);

		return convertGeneratedPdfToDocx(eventId, re, "excusive report");

	}

	@Override
	public String generateExclusiveReportType8Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request) {

		generateExclusiveReportType8(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction, isCategoryImage,
				isItemSlogan, isItemInstruction, re, lang, isCompanyDetails, adminTemplateModuleId, userid,
				adminTemplate, request);

		return convertGeneratedPdfToDocx(eventId, re, "excusive report");
	}

	@Override
	public String generateExclusiveReportType4Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request) {

		generateExclusiveReportType4(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction, isCategoryImage,
				isItemSlogan, isItemInstruction, re, lang, isCompanyDetails, adminTemplateModuleId, userid,
				adminTemplate, request);
		return convertGeneratedPdfToDocx(eventId, re, "excusive report");
	}

	@Override
	public String generateExclusiveReportType10Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request) {
		generateExclusiveReportType10(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction,
				isCategoryImage, isItemSlogan, isItemInstruction, re, lang, isCompanyDetails, adminTemplateModuleId,
				userid, adminTemplate, request);
		return convertGeneratedPdfToDocx(eventId, re, "excusive report");
	}

	@Override
	public String generateExclusiveReportType11Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request) {
		generateExclusiveReportType11(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction,
				isCategoryImage, isItemSlogan, isItemInstruction, re, lang, isCompanyDetails, adminTemplateModuleId,
				userid, adminTemplate, request);
		return convertGeneratedPdfToDocx(eventId, re, "excusive report");
	}

	@Override
	public String generateExclusiveReportType12Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request) {

		generateExclusiveReportType12(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction,
				isCategoryImage, isItemSlogan, isItemInstruction, re, lang, isCompanyDetails, adminTemplateModuleId,
				userid, adminTemplate, request);
		return convertGeneratedPdfToDocx(eventId, re, "excusive report");
	}

	@Override
	public String generateExclusiveReportType9Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request,
			Integer showAddOnLabel) {

		generateExclusiveReportType9(eventId, eventFunctionId, isCategorySlogan, isCategoryInstruction, isCategoryImage,
				isItemSlogan, isItemInstruction, re, lang, isCompanyDetails, adminTemplateModuleId, userid,
				adminTemplate, request, showAddOnLabel);
		return convertGeneratedPdfToDocx(eventId, re, "excusive report");
	}

	private String convertGeneratedPdfToDocx(Long eventId, HttpServletRequest request, String reportName) {

		try {

			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			if (event == null) {
				return null;
			}

			String rootPath = request.getSession().getServletContext().getRealPath("/");

			String pdfFileName = getReportName(getPartyNameByEventId(eventId),
					formatDate(event.getEventStartDateTime()), reportName) + ".pdf";

			String pdfPath = rootPath + "resources/tempDownload/" + event.getEventNo() + "/" + pdfFileName;

			return adobeDocxGenerator.convertPdfToDocxAndGetUrl(new File(pdfPath), event.getEventNo());

		} catch (Exception e) {
			throw new RuntimeException("Failed to convert PDF to DOCX", e);
		}
	}

	@Override
	public Boolean updateEventMenuPreparationStatus(Long eventId, String status) {
		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));

		eventMaster.setMenuPreparationStatus(status);

		eventMaster.setUpdatedAt(LocalDateTime.now());

		eventMasterRepository.save(eventMaster);

		return true;
	}

	@Override
	public String getMenuPreparationStatus(Long eventId) {
		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));

		return eventMaster.getMenuPreparationStatus();
	}

	@Override
	public List<AiEventFunctionMenuResponseDto> getAiMenuData(String eventFunctionIdsCsv) throws Exception {

		List<Long> eventFunctionIds = Arrays.stream(eventFunctionIdsCsv.split(",")).map(String::trim)
				.filter(s -> !s.isEmpty()).map(Long::valueOf).collect(Collectors.toList());

		if (eventFunctionIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<MenuPreparationEntity> menuPreparations = menuPreparationRepository
				.findByEventFunction_IdIn(eventFunctionIds);

		if (menuPreparations.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> menuPreparationIds = menuPreparations.stream().map(MenuPreparationEntity::getId)
				.collect(Collectors.toList());

		List<MenuPreparationDetailsEntity> details = menuPreparationDetailsRepository
				.findByMenuPreparation_IdIn(menuPreparationIds);

		// Group MenuPreparation by EventFunction
		Map<Long, List<MenuPreparationEntity>> eventFunctionMap = menuPreparations.stream()
				.collect(Collectors.groupingBy(mp -> mp.getEventFunction().getId()));

		// Group Details by MenuPreparation
		Map<Long, List<MenuPreparationDetailsEntity>> detailsMap = details.stream()
				.collect(Collectors.groupingBy(d -> d.getMenuPreparation().getId()));

		List<AiEventFunctionMenuResponseDto> response = new ArrayList<>();

		for (Map.Entry<Long, List<MenuPreparationEntity>> eventEntry : eventFunctionMap.entrySet()) {

			Long eventFunctionId = eventEntry.getKey();

			Map<Long, AiMenuCategoryResponseDto> categoryMap = new LinkedHashMap<>();

			for (MenuPreparationEntity preparation : eventEntry.getValue()) {

				List<MenuPreparationDetailsEntity> preparationDetails = detailsMap.getOrDefault(preparation.getId(),
						Collections.emptyList());

				for (MenuPreparationDetailsEntity detail : preparationDetails) {

					Long categoryId = detail.getMenuCategory().getId();

					AiMenuCategoryResponseDto categoryDto = categoryMap.computeIfAbsent(categoryId, id -> {

						AiMenuCategoryResponseDto dto = new AiMenuCategoryResponseDto();

						dto.setMenuCategoryId(id);
						dto.setMenuSlogan(detail.getMenuSlogan());
						dto.setMenuName(detail.getMenuCategoryName());
						dto.setItems(new ArrayList<>());

						return dto;
					});

					boolean itemExists = categoryDto.getItems().stream()
							.anyMatch(i -> i.getMenuItemId().equals(detail.getMenuItem().getId()));

					if (!itemExists) {
						categoryDto.getItems().add(new AiItemsResponseDto(detail.getMenuItem().getId(),
								detail.getMenuItemName(), detail.getItemSlogan()));
					}
				}
			}

			AiEventFunctionMenuResponseDto eventDto = new AiEventFunctionMenuResponseDto();

			eventDto.setEventFunctionId(eventFunctionId);
			eventDto.setMenuCategories(new ArrayList<>(categoryMap.values()));

			response.add(eventDto);
		}

		return response;
	}

	@Override
	public String generateExclusiveReportType16(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {
		try {
//			PdfFont headerFont1 = loadFont("/fonts/Cambria.ttf");
//			PdfFont headerFont2 = loadFont("/fonts/nunito-black.ttf");

			PdfFont font1 = loadFont("/fonts/arial-regular.ttf");
			PdfFont font2 = loadFont("/fonts/cinzel-dec-black.otf");
			PdfFont font3 = loadFont("/fonts/Cambria.ttf");
			PdfFont font4 = loadFont("/fonts/nunito-black.ttf");

//			Integer catFontSize = req.getCatFontSize();
//			Integer itemFontSize = req.getItemFontSize();
//			Integer sloganFontSize = req.getSloganFontSize();

			Integer catFontSize = 0;
			Integer itemFontSize = 0;
			Integer sloganFontSize = 0;

			loadLicense();

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", addressLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				font1 = font2 = font3 = font4 = loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				addressLabel = "पता";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansTamil-Regular.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					addressLabel = "முகவரி";
				} else if (language.equalsIgnoreCase("Telugu")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					addressLabel = "చిరునామా";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					addressLabel = "വിലാസം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/Nirmala.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					addressLabel = "पत्ता";
				} else {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					addressLabel = "સરનામું";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
				// English
				System.out.println("Loading English font...");

				hostLabel = "Client Name";
				phone = "Mobile";
				eDate = "Date";
				eName = "Event";
				venueLabel = "Venue";
				personLabel = "Person";
				eTime = "Time";
				fNote = "Food Status";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Spl";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				addressLabel = "Address";

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
//			Color creamGold = new DeviceRgb(r2, g2, b2);
//			Color softGold = new DeviceRgb(r1, g1, b1);
//			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			Color creamGold = new DeviceRgb(109, 67, 3);
			Color softGold = new DeviceRgb(147, 91, 57);
			Color descriptionColor = new DeviceRgb(109, 67, 3);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "Data not found.";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(110, 35, 100, 40);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/aroma-front.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/aroma-watermark.png");
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/aroma-last.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String partyAddress = eventDto.getPartyAddress() != null ? eventDto.getPartyAddress() : "";
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime() : "";

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String fn_name = "";
			ImageData borderData = loadImageFromResource("/flipbook/pages/border.png");
			PdfImageXObject borderXObject = new PdfImageXObject(borderData);
			Image borderImg = new Image(borderXObject);

			borderImg.setAutoScale(false);
			borderImg.setWidth(UnitValue.createPercentValue(50f));
			borderImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
			borderImg.setMarginTop(2f);
			borderImg.setMarginBottom(10f);

			bgHandler.setDefaultBackground(mainBgData);

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f }));

			detailTable.setWidth(UnitValue.createPercentValue(87f));
			detailTable.setMarginTop(210f);
			detailTable.setFixedLayout();
			detailTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

			String person = eventDto.getPax() != null ? eventDto.getPax().toString() : "0";

			String startTimeText = (eventDto.getEventStartTimestamp() != null ? eventDto.getEventStartTimestamp()
					: "TBD");

			final PdfFont font1Final = font1;
			BiConsumer<String, String> addRow = (label, value) -> {
				Cell cell1 = new Cell()
						.add(new Paragraph()
								.add(new Text(safeText(label) + " : ").setFontColor(softGold).setFontSize(18f)
										.setFont(font1Final))
								.add(new Text(safeText(value)).setFontColor(softGold).simulateBold().setFontSize(18f)
										.setFont(font1Final))
								.setFixedLeading(18f).setPaddingTop(3f))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);

				detailTable.addCell(cell1);
			};

			addRow.accept(hostLabel, hostName);

			addRow.accept(phone, mobileNo);

			addRow.accept(venueLabel, venue);

			addRow.accept(addressLabel, partyAddress);

			addRow.accept(eDate, startTimeText);

			addRow.accept(eTime, eventTime);

			addRow.accept(personLabel, person);

			addRow.accept(eName, eventName);

			addRow.accept(fNote, foodNotes.trim().isEmpty() ? "" : foodNotes);

			if (req.getIsShowEventRemarks() == 1) {
				addRow.accept(remarks, remark);
			}
			document.add(detailTable);

			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				bgHandler.setDefaultBackground(watermarkBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				String fnName = eventFunctionMasterResponseDto.getFunctionName() != null
						? eventFunctionMasterResponseDto.getFunctionName()
						: "";

				String fnPax = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "";

				String fnStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "";

				String functionVenue = "";
				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				Table fnTable = new Table(UnitValue.createPercentArray(new float[] { 1f }));
				fnTable.setWidth(UnitValue.createPercentValue(100f));
				fnTable.setFixedLayout();
				fnTable.setMarginTop(10f);
				fnTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

				cell = new Cell()
						.add(new Paragraph().add(new Text(safeText(fnName)).setFontColor(softGold).setFontSize(24f)
								.setFont(font2).simulateBold()).setFixedLeading(24f).setPaddingTop(10f))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text("(" + fnPax + " Person, " + fnStartTime + ")")
								.setFontColor(softGold).setFontSize(17f).setFont(font3)).setFixedLeading(17f))
						.setBorder(Border.NO_BORDER).setPaddingTop(10f).setTextAlignment(TextAlignment.CENTER);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text("Venue : " + functionVenue).setFontColor(softGold)
								.setFontSize(17f).setFont(font3)).setFixedLeading(17f))
						.setBorder(Border.NO_BORDER).setPaddingTop(10f).setTextAlignment(TextAlignment.CENTER);
				fnTable.addCell(cell);

				try {
					fnTable.addCell(new Cell().add(borderImg).setBorder(Border.NO_BORDER));
				} catch (Exception e) {
					e.printStackTrace();
				}

				document.add(fnTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

//				// Add menu content
//				for (MenuReportResponseDto menu : menuReportResponseDtos) {
//					String subCat = "";
//					if (lang == 1) {
//						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
//					} else if (lang == 2) {
//						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
//					} else {
//						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
//					}
//
//					Div menuContent = new Div();
//					menuContent.setKeepTogether(true);
//
//					Paragraph p = new Paragraph(menu.getNameEnglish().toUpperCase()).setUnderline().setFont(font2)
//							.setFontSize(getFontSize(catFontSize, 24)).setFixedLeading(24f).setFontColor(softGold)
//							.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0).setPaddingRight(0);
//					menuContent.add(p).setMarginTop(30f);
//
//					String text = "";
//
//					// Sub Category
//					if (subCat.trim().length() != 0) {
//						text = formatText(subCat, lang);
//						menuContent.add(new Paragraph(text).setFont(font4).setFontSize(getFontSize(catFontSize, 17))
//								.setFontColor(descriptionColor).setMarginBottom(0)
//								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
//					}
//
//					// Category Instructions
//					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
//						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
//							text = formatText(menu.getMenuNotes(), lang);
//							menuContent.add(new Paragraph(text).setFont(font3).setFontSize(getFontSize(catFontSize, 14))
//									.setFontColor(descriptionColor).setMarginBottom(0)
//									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
//						}
//					}
//
//					// Category Slogan
//					if (isCategorySlogan != null && isCategorySlogan == 0) {
//						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
//							text = "(" + formatText(menu.getSlogan(), lang) + ")";
//							menuContent.add(new Paragraph(text).setFont(font3)
//									.setFontSize(getFontSize(sloganFontSize, 14)).setTextAlignment(TextAlignment.CENTER)
//									.setMarginTop(0).setFontColor(descriptionColor).setMarginLeft(0));
//						}
//					}
//
//					// Category Image
//					if (isCategoryImage == 1 && menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
//						try {
//							Image img = new Image(ImageDataFactory
//									.create(environment.getProperty("app.image.url") + menu.getImagePath()));
//
////							ImageData logoData = loadImageFromResource("/flipbook/pages/Aftermeal.png");
////							Image img = new Image(logoData);
//
//							img.scaleToFit(500, 250f);
//							img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//							img.setMarginTop(10f);
//
//							menuContent.add(img);
//						} catch (Exception e) {
//							// Skip if image loading fails
//						}
//					}
//
//					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
//						String subItem = "";
//						if (lang == 1) {
//							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
//						} else if (lang == 2) {
//							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
//						} else {
//							subItem = item.getSubItem() != null ? item.getSubItem() : "";
//						}
//						String itemHeading = item.getItemHeading();
//						
//						if (itemHeading != null && !itemHeading.trim().isEmpty()) {
//							menuContent.add(new Paragraph(formatText(itemHeading, lang)).setFont(font2).setUnderline()
//									.setFontSize(getFontSize(catFontSize, 24)).setMultipliedLeading(1f)
//									.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER)
//									.setPaddingLeft(0).setPaddingRight(0))
//									.setMarginTop(15f).setMarginBottom(5f);
//						}
//						
//						menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(font4)
//								.setFontSize(getFontSize(itemFontSize, 17)).setFixedLeading(24f).simulateBold()
//								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0)
//								.setMarginTop(15f));
//
//						// Sub Item
//						if (subItem.trim().length() != 0) {
//							text = formatText(subItem, lang);
//							menuContent
//									.add(new Paragraph(text).setFont(font4).setFontSize(getFontSize(itemFontSize, 20))
//											.simulateBold().setFontColor(descriptionColor).setMultipliedLeading(1f)
//											.setMarginLeft(0).setMarginTop(0).setTextAlignment(TextAlignment.CENTER)
//											.setMarginBottom(0).setPadding(0f));
//						}
//
//						// Item Instructions
//						if (isItemInstruction != null && isItemInstruction == 1) {
//							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
//								text = formatText(item.getItemNotes(), lang);
//								menuContent.add(new Paragraph(text).setFont(font3)
//										.setFontSize(getFontSize(itemFontSize, 15)).setMultipliedLeading(1f)
//										.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
//										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
//							}
//						}
//
//						if (isItemSlogan != null && isItemSlogan == 1) {
//							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
//								text = "(" + formatText(item.getSlogan(), lang) + ")";
//								menuContent.add(new Paragraph(text).setFont(font3).simulateItalic()
//										.setFontSize(getFontSize(sloganFontSize, 14)).setMultipliedLeading(1f)
//										.setFontColor(softGold).setMarginLeft(0).setMarginTop(0)
//										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
//							}
//						}
//
//					}
//
//					document.add(menuContent);
//				}

				for (MenuReportResponseDto menu : menuReportResponseDtos) {

					String subCat = "";
					String catHeading = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
						catHeading = menu.getCatHeadingHindi() != null ? menu.getCatHeadingHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
						catHeading = menu.getCatHeadingGujarati() != null ? menu.getCatHeadingGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
						catHeading = menu.getCatHeadingEnglish() != null ? menu.getCatHeadingEnglish() : "";
					}

					// Category-level: title + subcat + instructions + slogan + image + ALL items
					// together
					Div menuContent = new Div();

					if (req.getIsAllItemTogether() == 1) {
						menuContent.setKeepTogether(true);
					}

					if (catHeading != null && !catHeading.trim().isEmpty()) {
						menuContent.add(new Paragraph(formatText(catHeading, lang)).setFont(font2).setUnderline()
								.setFontSize(getFontSize(catFontSize, 26)).setMultipliedLeading(1f)
								.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0)
								.setPaddingRight(0)).setMarginTop(15f).setMarginBottom(5f);
					}

					Paragraph p = new Paragraph(menu.getNameEnglish().toUpperCase()).setUnderline().setFont(font2)
							.setFontSize(getFontSize(catFontSize, 24)).setFixedLeading(24f).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0).setPaddingRight(0);
					menuContent.add(p).setMarginTop(30f);

					String text = "";

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = "(" + formatText(menu.getSlogan(), lang) + ")";
							menuContent.add(new Paragraph(text).setFont(font3)
									.setFontSize(getFontSize(sloganFontSize, 14)).setTextAlignment(TextAlignment.CENTER)
									.setMarginTop(0).setFontColor(descriptionColor).setMarginLeft(0));
						}
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							menuContent.add(new Paragraph(text).setFont(font3).setFontSize(getFontSize(catFontSize, 14))
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Sub Category
					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);
						menuContent.add(new Paragraph(text).setFont(font4).setFontSize(getFontSize(catFontSize, 18))
								.setFontColor(descriptionColor).setMarginBottom(0)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0)
								.simulateBold());
					}

					// Category Image
					if (isCategoryImage == 1 && menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
						try {
							System.out.println("image : " + menu.getImagePath());
							Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

							img.scaleToFit(500, 250f);
							img.setHorizontalAlignment(HorizontalAlignment.CENTER);
							img.setMarginTop(10f);

							// Rounded border around image
							img.setBorder(new SolidBorder(softGold, 5f));
							img.setBorderRadius(new BorderRadius(15f));

							menuContent.add(img);
						} catch (Exception e) {
							// Skip if image loading fails
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						String subItem = "";
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						String itemHeading = item.getItemHeading();

						// Item-level: heading + name + sub-item + instructions + slogan together
						Div itemContent = new Div();

						if (req.getIsAllItemTogether() == 1) {
							itemContent.setKeepTogether(true);
						}

						if (itemHeading != null && !itemHeading.trim().isEmpty()) {
							itemContent
									.add(new Paragraph(formatText(itemHeading, lang)).setFont(font2).setUnderline()
											.setFontSize(getFontSize(catFontSize, 24)).setMultipliedLeading(1f)
											.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER)
											.setPaddingLeft(0).setPaddingRight(0))
									.setMarginTop(15f).setMarginBottom(5f);
						}

						itemContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(font4)
								.setFontSize(getFontSize(itemFontSize, 17)).setFixedLeading(24f).simulateBold()
								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0)
								.setMarginTop(15f));

						// Item Slogan
						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = "(" + formatText(item.getSlogan(), lang) + ")";
								itemContent.add(new Paragraph(text).setFont(font3).simulateItalic()
										.setFontSize(getFontSize(sloganFontSize, 14)).setMultipliedLeading(1f)
										.setFontColor(softGold).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								itemContent.add(new Paragraph(text).setFont(font3)
										.setFontSize(getFontSize(itemFontSize, 15)).setMultipliedLeading(1f)
										.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Sub Item
						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);
							itemContent
									.add(new Paragraph(text).setFont(font4).setFontSize(getFontSize(itemFontSize, 20))
											.simulateBold().setFontColor(descriptionColor).setMultipliedLeading(1f)
											.setMarginLeft(0).setMarginTop(0).setTextAlignment(TextAlignment.CENTER)
											.setMarginBottom(0).setPadding(0f));
						}

						menuContent.add(itemContent);
					}

					document.add(menuContent);
				}

			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// EXTRA CHARGES PAGE
			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ── Set background for this page (same watermark as content pages) ──
					int extraPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(extraPageNum, watermarkBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(font3)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline()
							// .setBackgroundColor(new DeviceRgb(255, 255, 0)) // yellow highlight
							.setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// Heading title: HEADING NAME
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase()).setFont(font3)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold).setUnderline()
								.setTextAlignment(TextAlignment.LEFT).setMarginTop(12f).setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(font3).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(softGold).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0", font3,
										creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												font3, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												font3, creamGold, catFontSize, softGold));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", font3, creamGold, catFontSize, softGold));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(font3).setFontSize(getFontSize(catFontSize, 13)).setFontColor(softGold)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(font3).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ============ LAST PAGE ============
			bgHandler.setPageBackground(lastPageNum, lastBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			Paragraph invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, watermarkBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(font3).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(font3).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			Color textColor = ColorConstants.WHITE;

			for (int i = 1; i <= totalPages; i++) {

				PdfPage page = pdfDocument.getPage(i);
				Rectangle pageSize = page.getPageSize();

				float centerX = pageSize.getWidth() / 2;
				float centerY = 45;
				float radius = 20f;

				PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDocument);

				// Draw circle
				pdfCanvas.saveState();
				pdfCanvas.setFillColor(softGold);
				pdfCanvas.circle(centerX, centerY, radius);
				pdfCanvas.fill();
				pdfCanvas.restoreState();

				// Draw page number
				Canvas canvas = new Canvas(pdfCanvas, pageSize);

				canvas.setFontColor(textColor);
				canvas.setFontSize(10);

				Paragraph pageNumber = new Paragraph(String.valueOf(i)).setFontColor(textColor).setFontSize(18);

				canvas.showTextAligned(pageNumber, centerX, centerY - 10, TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType17(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {
		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", addressLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				addressLabel = "पता";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userid);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					addressLabel = "முகவரி";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					addressLabel = "చిరునామా";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					addressLabel = "വിലാസം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					addressLabel = "पत्ता";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					addressLabel = "સરનામું";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
				// English
				System.out.println("Loading English font...");

				hostLabel = "Client Name";
				phone = "Mobile";
				eDate = "Date";
				eName = "Event";
				venueLabel = "Venue";
				personLabel = "Person";
				eTime = "Time";
				fNote = "Food Status";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Spl Instruction";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				addressLabel = "Address";

				catFont = getFont(req.getCatFontId(), false, "gotham");
				catFontBold = getFont(req.getCatFontId(), true, "gotham");

				itemFont = getFont(req.getItemFontId(), false, "roboto-mono");
				itemFontBold = getFont(req.getItemFontId(), true, "roboto-mono");

				sloganFont = getFont(req.getSloganFontId(), false, "gotham");
				sloganFontBold = getFont(req.getSloganFontId(), true, "gotham");

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
//			Color creamGold = new DeviceRgb(r2, g2, b2);
//			Color softGold = new DeviceRgb(r1, g1, b1);
//			Color descriptionColor = new DeviceRgb(r3, g3, b3);

//			Color creamGold = new DeviceRgb(109, 67, 3);
//			Color softGold = new DeviceRgb(109, 67, 3);
//			Color descriptionColor = new DeviceRgb(109, 67, 3);

			Color headingFontColor = new DeviceRgb(22, 74, 64);
			Color contentFontColor = new DeviceRgb(22, 74, 64);
			Color descriptionFontColor = new DeviceRgb(22, 74, 64);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userid);

			if (eventDto == null) {
				return "Data not found.";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			PageSize pageSize = new PageSize(1920f, 1080f);
			Document document = new Document(pdfDocument, pageSize, false);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/lakhani_1.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/lakhani_2.png");
//			ImageData thirdPage = loadImageFromResource("/flipbook/pages/lakhani_3.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/lakhani_4.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/lakhani_5.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData thirdPage = null;
			if (eventDto.getVenue_img() != null) {
				thirdPage = loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getVenue_img());
			}
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String partyAddress = eventDto.getPartyAddress() != null ? eventDto.getPartyAddress() : "";
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			String partyName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "";
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			document.setMargins(0, 0, 0, 0);

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			bgHandler.setPageBackground(1, mainBgData);
			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(headingFontColor);
			invisibleContent.setFontSize(35);
			invisibleContent.setPaddingTop(140f);
			invisibleContent.setPaddingLeft(165f);
			document.add(invisibleContent);

			bgHandler.setPageBackground(2, detailsPage);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(invisibleContent);

			bgHandler.setDefaultBackground(watermarkBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Image venueImg = null;
			if (thirdPage != null) {
				try {
					float pageWidth = pageSize.getWidth();
					float pageHeight = pageSize.getHeight();

					float targetWidth = pageWidth; // full width
					float targetHeight = pageHeight * 0.6f; // 60% of usable height

					venueImg = new Image(thirdPage);
					venueImg.scaleAbsolute(targetWidth, targetHeight);
					venueImg.setBorder(new SolidBorder(ColorConstants.BLACK, 1f));

					document.add(venueImg);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
			}

			Cell cell;
			Table eventDetailTable = new Table(UnitValue.createPercentArray(new float[] { 70f, 30f }));
			eventDetailTable.setWidth(UnitValue.createPercentValue(93f));
			eventDetailTable.setMarginTop(30f);
			eventDetailTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
			eventDetailTable.setFixedLayout();

			Paragraph partyNamePara = new Paragraph().add(partyName).setFont(catFont).setFontColor(headingFontColor)
					.simulateBold().setFontSize(50f);
			cell = new Cell(1, 2).add(partyNamePara).setBorder(Border.NO_BORDER);
			eventDetailTable.addCell(cell);

			Paragraph venuePara = new Paragraph().add(venue).setFont(catFont).setFontColor(headingFontColor)
					.simulateBold().setFontSize(40f);
			cell = new Cell().add(venuePara).setBorder(Border.NO_BORDER);
			eventDetailTable.addCell(cell);

			Paragraph notesPara = new Paragraph().add("Notes :- " + remark).setFont(catFont)
					.setFontColor(headingFontColor).simulateBold().setFontSize(40f);
			cell = new Cell().add(notesPara).setBorder(Border.NO_BORDER);
			eventDetailTable.addCell(cell);

			cell = new Cell().setBorder(Border.NO_BORDER);
			eventDetailTable.addCell(cell);

			Paragraph cmpPara = new Paragraph().add("Event Company : " + cmpName).setFont(catFont)
					.setFontColor(headingFontColor).simulateBold().setFontSize(40f);
			cell = new Cell().add(cmpPara).setBorder(Border.NO_BORDER);
			eventDetailTable.addCell(cell);

			cell = new Cell().setBorder(Border.NO_BORDER);
			eventDetailTable.addCell(cell);

			Paragraph foodNotesPara = new Paragraph().add(foodNotes).setFont(catFont).setFontColor(headingFontColor)
					.simulateBold().setFontSize(40f);
			cell = new Cell().add(foodNotesPara).setBorder(Border.NO_BORDER);
			eventDetailTable.addCell(cell);

			document.add(eventDetailTable);

			document.setMargins(110, 35, 100, 40);

			List<EventFunctionReportResponseDto> functions = eventDto.getFunctions();

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
			DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");

			// Group functions by date, preserving chronological order
			Map<LocalDate, List<EventFunctionReportResponseDto>> grouped = functions.stream()
					.sorted(Comparator.comparing(f -> LocalDateTime.parse(f.getFunctionStartTimestamp(), inputFmt)))
					.collect(Collectors.groupingBy(
							f -> LocalDateTime.parse(f.getFunctionStartTimestamp(), inputFmt).toLocalDate(),
							LinkedHashMap::new, Collectors.toList()));

			int dayIndex = 1;
			for (Map.Entry<LocalDate, List<EventFunctionReportResponseDto>> entry : grouped.entrySet()) {
				LocalDate date = entry.getKey();
				List<EventFunctionReportResponseDto> dayFunctions = entry.getValue();

				// "Day - 1 18th February 2026"
				Paragraph dayHeader = new Paragraph("Day - " + dayIndex + "   " + formatOrdinalDate(date))
						.setFont(catFontBold).setFontSize(50).setUnderline().setMarginTop(16).setMarginBottom(4)
						.setTextAlignment(TextAlignment.CENTER);
				document.add(dayHeader);

				Paragraph flowHeader = new Paragraph("FLOW OF EVENT").setFont(catFontBold).setFontSize(50)
						.setMarginBottom(6).setTextAlignment(TextAlignment.CENTER).setUnderline();
				document.add(flowHeader);

				for (EventFunctionReportResponseDto fun : dayFunctions) {
					LocalDateTime startTime = LocalDateTime.parse(fun.getFunctionStartTimestamp(), inputFmt);

					Paragraph timePara = new Paragraph(startTime.format(timeFmt)).setFont(catFont).setFontSize(40)
							.setMarginTop(25).setMarginBottom(2).setTextAlignment(TextAlignment.CENTER);
					document.add(timePara);

					Paragraph funcPara = new Paragraph(fun.getFunctionName() + " : " + fun.getFunctionVenue())
							.setFont(catFont).setFontSize(40).setMarginBottom(4).setTextAlignment(TextAlignment.CENTER);
					document.add(funcPara);
				}

				dayIndex++;
			}

//			for (EventFunctionReportResponseDto fun : functions) {
//				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//
//				Paragraph functionNamePara = new Paragraph(fun.getFunctionName().toUpperCase()).setFont(catFontBold)
//						.setFontColor(headingFontColor).setFontSize(80).setTextAlignment(TextAlignment.CENTER)
//						.simulateBold();
//
//				document.showTextAligned(functionNamePara, pageSize.getWidth() / 2, pageSize.getHeight() / 2,
//						pdfDocument.getNumberOfPages(), TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
//
//				List<MenuReportResponseDto> categories = fun.getMenuCategories();
//
//				Integer catNo = 0;
//				for (MenuReportResponseDto menu : categories) {
//
//					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//					int catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;
//					catNo = ++catNo;
//					boolean isOddCat = (catNo % 2) == 1;
//
//					String subCat = "";
//					if (lang == 1) {
//						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
//					} else if (lang == 2) {
//						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
//					} else {
//						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
//					}
//
//					Table layoutTable;
//
//					if (isOddCat) {
//						// Text Left (65%), Image Right (35%)
//						layoutTable = new Table(UnitValue.createPercentArray(new float[] { 65, 35 }));
//					} else {
//						// Image Left (35%), Text Right (65%)
//						layoutTable = new Table(UnitValue.createPercentArray(new float[] { 35, 65 }));
//					}
//
//					layoutTable.setWidth(UnitValue.createPercentValue(100));
//					layoutTable.setBorder(Border.NO_BORDER);
//					layoutTable.setKeepTogether(false);
//
//					Div textContent = new Div();
////				    textContent.setKeepTogether(true);
//
//					Image img = null;
//					// ===========================
//					// CATEGORY IMAGE
//					// ===========================
////					if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
//						try {
////							img = new Image(ImageDataFactory.create(menu.getImagePath()));
//							img = new Image(loadImageFromResource("/flipbook/pages/demo.jpg"));
//
//							img.scaleAbsolute(700f, 800f);
//							img.setBorder(new SolidBorder(ColorConstants.BLACK, 1f));
//
//						} catch (Exception e) {
//							// Ignore image loading errors
//							System.out.println(e.getLocalizedMessage());
//						}
////					}
//
//					// ===========================
//					// SUB CATEGORY
//					// ===========================
//
//					String text = "";
//
//					Table titleTable = new Table(1);
//					titleTable.setWidth(UnitValue.createPercentValue(100));
//					titleTable.setBorder(Border.NO_BORDER);
//					Paragraph p = new Paragraph();
//					for (int i = 0; i < catSpace; i++) {
//						p.add("\n");
//					}
//					titleTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
//					if (subCat.trim().length() != 0) {
//						text = formatText(subCat, lang);
//						p = new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 40))
//								.setMultipliedLeading(1f).setFontColor(descriptionFontColor).setMarginBottom(0)
//								.setMarginTop(0).setMarginLeft(0).simulateBold().setTextAlignment(TextAlignment.LEFT);
//						titleTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
//					}
//
//					// ===========================
//					// CATEGORY NAME
//					// ===========================
//
//					p = new Paragraph(menu.getNameEnglish().toUpperCase()).setFont(catFontBold)
//							.setFontSize(getFontSize(catFontSize, 50)).setFixedLeading(50f)
//							.setFontColor(headingFontColor).setTextAlignment(TextAlignment.LEFT);
//					titleTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
//
//					titleTable.setMarginBottom(5f);
//
//					textContent.add(titleTable).setMarginTop(20f);
//
//					// ===========================
//					// CATEGORY INSTRUCTION
//					// ===========================
//
//					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
//
//						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
//
//							text = formatText(menu.getMenuNotes(), lang);
//
//							textContent.add(new Paragraph(text).setFont(sloganFont)
//									.setFontSize(getFontSize(catFontSize, 25)).setMultipliedLeading(1.1f)
//									.setFontColor(descriptionFontColor).setMarginBottom(0).setMarginTop(0)
//									.setMarginLeft(0).simulateBold().setTextAlignment(TextAlignment.LEFT));
//						}
//					}
//
//					// ===========================
//					// CATEGORY SLOGAN
//					// ===========================
//
//					if (isCategorySlogan != null && isCategorySlogan == 1) {
//
//						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
//
//							text = "(" + formatText(menu.getSlogan(), lang) + ")";
//
//							textContent.add(new Paragraph(text).setFont(sloganFont)
//									.setFontSize(getFontSize(sloganFontSize, 24)).setMultipliedLeading(1.1f)
//									.setTextAlignment(TextAlignment.LEFT).setFontColor(descriptionFontColor)
//									.setMarginTop(0).simulateBold().setMarginLeft(0));
//						}
//					}
//
//					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
//
//						String subItem = "";
//
//						if (lang == 1) {
//							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
//						} else if (lang == 2) {
//							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
//						} else {
//							subItem = item.getSubItem() != null ? item.getSubItem() : "";
//						}
//						Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
//
//						Div itemContent = new Div();
//
//						for (int i = 0; i < itemSpace; i++) {
//							itemContent.add(new Paragraph("\n"));
//						}
//						itemContent.setKeepTogether(true);
//
//						if (item.getItemHeading() != null && item.getItemHeading().trim().length() != 0) {
//							itemContent.add(new Paragraph(item.getItemHeading().toUpperCase()).setFont(catFontBold)
//									.setFontSize(getFontSize(catFontSize, 50)).setFixedLeading(50f)
//									.setFontColor(headingFontColor).setTextAlignment(TextAlignment.LEFT)
//									.setMarginTop(30f));
//						}
//
//						itemContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
//								.setFontSize(getFontSize(itemFontSize, 30)).setFixedLeading(30f).simulateBold()
//								.setTextAlignment(TextAlignment.LEFT).setFontColor(contentFontColor).setMarginLeft(0)
//								.setMarginTop(30f));
//
//						// Sub Item
//						if (subItem.trim().length() != 0) {
//
//							text = formatText(subItem, lang);
//
//							itemContent.add(new Paragraph(text).setFont(itemFont)
//									.setFontSize(getFontSize(itemFontSize, 26)).simulateBold().setMultipliedLeading(1f)
//									.setFontColor(descriptionFontColor).setMarginLeft(0).setMarginTop(0)
//									.setMarginBottom(0).setPadding(0f).setTextAlignment(TextAlignment.LEFT));
//						}
//
//						// Item Slogan
//						if (isItemSlogan != null && isItemSlogan == 1) {
//
//							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
//
//								text = formatText(item.getSlogan(), lang);
//
//								itemContent.add(new Paragraph(text).setFont(itemFont)
//										.setFontSize(getFontSize(sloganFontSize, 20)).setMultipliedLeading(1f)
//										.setFontColor(descriptionFontColor).setMarginLeft(0).setMarginTop(0)
//										.setMarginBottom(0).setPadding(0f).simulateBold()
//										.setTextAlignment(TextAlignment.LEFT));
//							}
//						}
//
//						// Item Instruction
//						if (isItemInstruction != null && isItemInstruction == 1) {
//
//							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
//
//								text = formatText(item.getItemNotes(), lang);
//
//								itemContent.add(
//										new Paragraph(text).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 20))
//												.setMultipliedLeading(1f).setFontColor(descriptionFontColor)
//												.setMarginLeft(0).setMarginTop(0).setMarginBottom(0).setPadding(0f)
//												.simulateBold().setTextAlignment(TextAlignment.LEFT));
//							}
//						}
//
//						textContent.add(itemContent);
//					}
//
//					// ===========================
//					// FINAL LAYOUT
//					// Odd Page : Text | Image
//					// Even Page : Image | Text
//					// ===========================
//
//					Cell textCell = new Cell().add(textContent).setBorder(Border.NO_BORDER)
//							.setVerticalAlignment(VerticalAlignment.TOP);
//
//					Cell imageCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.TOP)
//							.setTextAlignment(TextAlignment.CENTER);
//
//					if (img != null) {
//						imageCell.add(img);
////				    	imageCell.add(new Paragraph(""));
//					} else {
//						imageCell.add(new Paragraph(""));
//					}
//
//					if (isOddCat) {
//						// Page 1,3,5...
//						layoutTable.addCell(textCell.setPaddingRight(20f));
//						layoutTable.addCell(imageCell);
//					} else {
//						// Page 2,4,6...
//						layoutTable.addCell(imageCell);
//						layoutTable.addCell(textCell.setPaddingLeft(20f));
//					}
//
//					document.add(layoutTable);
//				}
//			}

			List<CategoryImagePageInfo> categoryImagePages = new ArrayList<CategoryImagePageInfo>();

			for (EventFunctionReportResponseDto fun : functions) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Paragraph functionNamePara = new Paragraph(fun.getFunctionName().toUpperCase()).setFont(catFontBold)
						.setFontColor(headingFontColor).setFontSize(80).setTextAlignment(TextAlignment.CENTER)
						.simulateBold();

				document.showTextAligned(functionNamePara, pageSize.getWidth() / 2, pageSize.getHeight() / 2,
						pdfDocument.getNumberOfPages(), TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);

				List<MenuReportResponseDto> categories = fun.getMenuCategories();

				Integer catNo = 0;

				for (MenuReportResponseDto menu : categories) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					int categoryStartPage = pdfDocument.getNumberOfPages();

					Integer catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;

					catNo = ++catNo;

					boolean isOddCat = (catNo % 2) == 1;

					String subCat = "";
					String catHeading = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
						catHeading = menu.getCatHeadingHindi() != null ? menu.getCatHeadingHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
						catHeading = menu.getCatHeadingGujarati() != null ? menu.getCatHeadingGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
						catHeading = menu.getCatHeadingEnglish() != null ? menu.getCatHeadingEnglish() : "";
					}

					ImageData categoryImageData = null;

					if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
						try {
							categoryImageData = loadImageFromResource(menu.getImagePath());
//							categoryImageData = loadImageFromResource("/flipbook/pages/demo.jpg");
						} catch (Exception e) {
							System.out.println("Category image loading error: " + e.getLocalizedMessage());
						}
					}

					Table layoutTable;

					if (isOddCat) {
						layoutTable = new Table(UnitValue.createPercentArray(new float[] { 60f, 40f }));
					} else {
						layoutTable = new Table(UnitValue.createPercentArray(new float[] { 40f, 60f }));
					}

					layoutTable.setWidth(UnitValue.createPercentValue(100));
					layoutTable.setBorder(Border.NO_BORDER);
					layoutTable.setKeepTogether(false);

					Div textContent = new Div();

					String text = "";

					Table titleTable = new Table(1);
					titleTable.setWidth(UnitValue.createPercentValue(100));
					titleTable.setBorder(Border.NO_BORDER);

					Paragraph p = new Paragraph();

					for (int i = 0; i < catSpace; i++) {
						p.add("\n");
					}

					titleTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

					if (catHeading != null && catHeading.trim().length() != 0) {
						p.add(new Paragraph(catHeading.toUpperCase()).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 52)).setFixedLeading(52f)
								.setFontColor(headingFontColor).setTextAlignment(TextAlignment.LEFT).setMarginTop(30f));
					}

					p = new Paragraph(menu.getNameEnglish().toUpperCase()).setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 50)).setFixedLeading(50f)
							.setFontColor(headingFontColor).setTextAlignment(TextAlignment.LEFT);

					titleTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);

						p = new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 40))
								.setMultipliedLeading(1f).setFontColor(descriptionFontColor).setMarginBottom(0)
								.setMarginTop(0).setMarginLeft(0).simulateBold().setTextAlignment(TextAlignment.LEFT);

						titleTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
					}

					titleTable.setMarginBottom(5f);

					textContent.add(titleTable).setMarginTop(20f);

					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);

							textContent.add(new Paragraph(text).setFont(sloganFont)
									.setFontSize(getFontSize(catFontSize, 25)).setMultipliedLeading(1.1f)
									.setFontColor(descriptionFontColor).setMarginBottom(0).setMarginTop(0)
									.setMarginLeft(0).simulateBold().setTextAlignment(TextAlignment.LEFT));
						}
					}

					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = "(" + formatText(menu.getSlogan(), lang) + ")";

							textContent.add(new Paragraph(text).setFont(sloganFont)
									.setFontSize(getFontSize(sloganFontSize, 24)).setMultipliedLeading(1.1f)
									.setTextAlignment(TextAlignment.LEFT).setFontColor(descriptionFontColor)
									.setMarginTop(0).simulateBold().setMarginLeft(0));
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String subItem = "";

						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;

						Div itemContent = new Div();
						itemContent.setKeepTogether(true);

						for (int i = 0; i < itemSpace; i++) {
							itemContent.add(new Paragraph("\n"));
						}

						if (item.getItemHeading() != null && item.getItemHeading().trim().length() != 0) {
							itemContent.add(new Paragraph(item.getItemHeading().toUpperCase()).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 50)).setFixedLeading(50f)
									.setFontColor(headingFontColor).setTextAlignment(TextAlignment.LEFT)
									.setMarginTop(30f));
						}

						itemContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 30)).setFixedLeading(30f).simulateBold()
								.setTextAlignment(TextAlignment.LEFT).setFontColor(contentFontColor).setMarginLeft(0)
								.setMarginTop(30f));

						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);

							itemContent.add(new Paragraph(text).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 26)).simulateBold().setMultipliedLeading(1f)
									.setFontColor(descriptionFontColor).setMarginLeft(0).setMarginTop(0)
									.setMarginBottom(0).setPadding(0f).setTextAlignment(TextAlignment.LEFT));
						}

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = formatText(item.getSlogan(), lang);

								itemContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(sloganFontSize, 20)).setMultipliedLeading(1f)
										.setFontColor(descriptionFontColor).setMarginLeft(0).setMarginTop(0)
										.setMarginBottom(0).setPadding(0f).simulateBold()
										.setTextAlignment(TextAlignment.LEFT));
							}
						}

						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);

								itemContent.add(
										new Paragraph(text).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 20))
												.setMultipliedLeading(1f).setFontColor(descriptionFontColor)
												.setMarginLeft(0).setMarginTop(0).setMarginBottom(0).setPadding(0f)
												.simulateBold().setTextAlignment(TextAlignment.LEFT));
							}
						}

						textContent.add(itemContent);
					}

					Cell textCell = new Cell().add(textContent).setBorder(Border.NO_BORDER)
							.setVerticalAlignment(VerticalAlignment.TOP);

					Cell imageCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.TOP);

					if (isOddCat) {
						layoutTable.addCell(textCell.setPaddingRight(20f));

						layoutTable.addCell(imageCell);
					} else {
						layoutTable.addCell(imageCell);

						layoutTable.addCell(textCell.setPaddingLeft(20f));
					}

					document.add(layoutTable);

					int categoryEndPage = pdfDocument.getNumberOfPages();

					if (categoryImageData != null) {
						categoryImagePages.add(new CategoryImagePageInfo(categoryStartPage, categoryEndPage,
								categoryImageData, isOddCat));
					}
				}
			}

			addCategoryImagesToPages(pdfDocument, pageSize, categoryImagePages);

			bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, lastBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(invisibleContent);

			// ================= PAGE NUMBERS =================
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 3; i < totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);

				Canvas canvas = new Canvas(new PdfCanvas(page), pageSize);

				// Header - Line 1
				canvas.setFontSize(28);
				canvas.setFontColor(headingFontColor);
				canvas.showTextAligned("Om Ganeshaya Namah", pageSize.getWidth() / 2, pageSize.getTop() - 50,
						TextAlignment.CENTER);

				// Header - Line 2
				canvas.setFontSize(28);
				canvas.setFontColor(headingFontColor);
				canvas.showTextAligned("Jai Jalaram Bapa", pageSize.getWidth() / 2, pageSize.getTop() - 80,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType18(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {
		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", timeLabel = "", cordinatorPersonLabel = "", packageNameLabel = "",
					cordinatorPersonContactNumLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "ग्राहक का नाम";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				timeLabel = "";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					timeLabel = "";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					timeLabel = "";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					timeLabel = "";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					timeLabel = "";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					timeLabel = "";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
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

				catFont = getFont(req.getCatFontId(), false, "gothic");
				catFontBold = getFont(req.getCatFontId(), true, "gothic");

				itemFont = getFont(req.getItemFontId(), false, "gothic");
				itemFontBold = getFont(req.getItemFontId(), true, "gothic");

				sloganFont = getFont(req.getSloganFontId(), false, "gothic");
				sloganFontBold = getFont(req.getSloganFontId(), true, "gothic");

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color contentColor = new DeviceRgb(r2, g2, b2);
			Color headingColor = new DeviceRgb(r1, g1, b1);
			Color descriptionColor = new DeviceRgb(r3, g3, b3);

//			Color headingColor = new DeviceRgb(255, 255, 255);
//			Color contentColor = new DeviceRgb(255, 255, 255);
//			Color descriptionColor = new DeviceRgb(255, 255, 255);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(100, 45, 60, 50);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/southavenue1.png");
//			ImageData detailsPage = null;
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/southavenue2.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/southavenue3.png");
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/southavenue1.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/krishna_last_4.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

//			ImageData tncPage1 = null;
//			if (adminTemplate.getTemplateMaster().getExtraPage() != null
//					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
//				tncPage1 = loadImageFromResource(
//						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
//			}
//
//			ImageData lastBgData = null;
//			if (adminTemplate.getTemplateMaster().getLastMainPage() != null
//					&& adminTemplate.getTemplateMaster().getLastMainPage().trim().length() != 0) {
//				lastBgData = loadImageFromResource(
//						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());
//			}
			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName().toUpperCase() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo().toUpperCase() : "";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? ""
					: eventDto.getEventStartTimestamp().toUpperCase();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? ""
					: eventDto.getEventEndTimestamp().toUpperCase();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName().toUpperCase();
			String pax = eventDto.getPax();
			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime().toUpperCase() : "";
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String cordinationPersonName = eventDto.getCordinationPersonName() != null
					? eventDto.getCordinationPersonName().toUpperCase()
					: "";
			String cordinationPersonContactNo = eventDto.getCordinationPersonContactno() != null
					? eventDto.getCordinationPersonContactno().toUpperCase()
					: "";

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName().toUpperCase() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue().toUpperCase();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark().toUpperCase();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi().toUpperCase()
						: "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi().toUpperCase() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi().toUpperCase() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi().toUpperCase() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null
						? eventDto.getBillingNameGujarati().toUpperCase()
						: "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati().toUpperCase()
						: "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati().toUpperCase() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati().toUpperCase() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish().toUpperCase()
						: "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes().toUpperCase() : "";
				service = eventDto.getService() != null ? eventDto.getService().toUpperCase() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme().toUpperCase() : "";
			}

			bgHandler.setPageBackground(1, mainBgData);
			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(catFont);
			invisibleContent.setFontSize(35);
			invisibleContent.setPaddingTop(90f);
			invisibleContent.setPaddingLeft(165f);
			document.add(invisibleContent);

			bgHandler.setPageBackground(2, detailsPage);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 35f, 2f, 26f, 15f, 2f, 20f }));

			eventTable.setWidth(UnitValue.createPercentValue(96f));
			eventTable.setFixedLayout();
			eventTable.setMarginTop(400f);
			eventTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

			Cell labelCell = new Cell()
					.add(new Paragraph(safeText(eDate)).setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
							.setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(headingColor, 1f)).setPadding(0);
			eventTable.addCell(labelCell);

			Cell middleCell = new Cell()
					.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
					.setBorderBottom(new SolidBorder(headingColor, 1f)).setPadding(0);
			eventTable.addCell(middleCell);

			Cell valueCell = new Cell()
					.add(new Paragraph(safeText(eventStartDate)).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
					.setBorderBottom(new SolidBorder(headingColor, 1f)).setPadding(0);
			eventTable.addCell(valueCell);

			labelCell = new Cell()
					.add(new Paragraph(safeText(eTime)).setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.RIGHT)
							.setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setBorderBottom(new SolidBorder(headingColor, 1f))
					.setPadding(0);
			eventTable.addCell(labelCell);

			middleCell = new Cell()
					.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
					.setBorderBottom(new SolidBorder(headingColor, 1f)).setPadding(0);
			eventTable.addCell(middleCell);

			valueCell = new Cell()
					.add(new Paragraph(safeText(eventTime)).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
					.setBorderBottom(new SolidBorder(headingColor, 1f)).setPadding(0f);
			eventTable.addCell(valueCell);

			labelCell = new Cell()
					.add(new Paragraph(safeText(hostLabel)).setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
							.setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
			eventTable.addCell(labelCell);

			middleCell = new Cell()
					.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(middleCell);

			valueCell = new Cell(1, 4)
					.add(new Paragraph(safeText(hostName)).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(valueCell);

			if (cordinationPersonName != null && cordinationPersonName.trim().length() != 0
					&& cordinationPersonContactNo != null && cordinationPersonContactNo.trim().length() != 0) {
				mobileNo = cordinationPersonName + " - " + cordinationPersonContactNo;
			}

			if (mobileNo != null && mobileNo.trim().length() != 0) {
				labelCell = new Cell()
						.add(new Paragraph(safeText(phone)).setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				eventTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				eventTable.addCell(middleCell);

				valueCell = new Cell(1, 4)
						.add(new Paragraph(safeText(mobileNo)).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				eventTable.addCell(valueCell);
			}

			labelCell = new Cell()
					.add(new Paragraph(safeText(venueLabel)).setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
							.setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
			eventTable.addCell(labelCell);

			middleCell = new Cell()
					.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(middleCell);

			valueCell = new Cell(1, 4)
					.add(new Paragraph(safeText(venue)).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(valueCell);

			labelCell = new Cell()
					.add(new Paragraph(safeText(eName)).setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
							.simulateBold().setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
			eventTable.addCell(labelCell);

			middleCell = new Cell()
					.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(middleCell);

			valueCell = new Cell(1, 4)
					.add(new Paragraph(safeText(eventName)).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(valueCell);

			labelCell = new Cell()
					.add(new Paragraph(safeText(personLabel)).setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
							.setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
			eventTable.addCell(labelCell);

			middleCell = new Cell()
					.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(middleCell);

			valueCell = new Cell(1, 4)
					.add(new Paragraph(safeText(pax + " PERSONS")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(valueCell);

			if (remark != null && remark.trim().length() != 0) {
				labelCell = new Cell()
						.add(new Paragraph(safeText(remarks)).setFont(catFont).setFontColor(headingColor)
								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
								.setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
				eventTable.addCell(labelCell);

				middleCell = new Cell()
						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				eventTable.addCell(middleCell);

				valueCell = new Cell(1, 4)
						.add(new Paragraph(safeText(remark)).setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f))
						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
				eventTable.addCell(valueCell);
			}

			labelCell = new Cell()
					.add(new Paragraph(safeText(fNote)).setFont(catFont).setFontColor(headingColor)
							.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
							.simulateBold().setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
			eventTable.addCell(labelCell);

			middleCell = new Cell()
					.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
							.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(middleCell);

			valueCell = new Cell(1, 4)
					.add(new Paragraph(safeText(foodNotesName.toString().toUpperCase()
							+ (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")"))).setFont(itemFont)
							.setFontColor(descriptionColor).setFontSize(getFontSize(itemFontSize, 14)).simulateBold()
							.setCharacterSpacing(2f))
					.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
			eventTable.addCell(valueCell);

//			String packageName = eventFunctionMasterResponseDto.getPackageName() != null
//					? eventFunctionMasterResponseDto.getPackageName().toUpperCase()
//					: "";
//			BigDecimal packagePrice = eventFunctionMasterResponseDto.getPackPrice() != null
//					? eventFunctionMasterResponseDto.getPackPrice()
//					: BigDecimal.ZERO;
//
//			if (rate != 0D) {
//				labelCell = new Cell()
//						.add(new Paragraph(safeText(packageNameLabel)).setFont(catFont).setFontColor(softGold)
//								.setFontSize(getFontSize(catFontSize, 14)).setTextAlignment(TextAlignment.LEFT)
//								.simulateBold().setCharacterSpacing(2f))
//						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.CENTER);
//				eventTable.addCell(labelCell);
//
//				middleCell = new Cell()
//						.add(new Paragraph(safeText(":")).setFont(itemFont).setFontColor(descriptionColor)
//								.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
//						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
//				eventTable.addCell(middleCell);
//
//				valueCell = new Cell(1, 4)
//						.add(new Paragraph(packageName + " @RS." + rate.intValue() + "/- PP + TAX")
//								.setFont(itemFont).setFontColor(descriptionColor)
//								.setFontSize(getFontSize(itemFontSize, 14)).simulateBold().setCharacterSpacing(2f))
//						.setBorder(Border.NO_BORDER).setPadding(0f).setTextAlignment(TextAlignment.LEFT);
//				eventTable.addCell(valueCell);
//			}

			document.add(eventTable);

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
						.findByEventFunction_IdAndIsDeleteFalse(eventFunctionMasterResponseDto.getFunctionId());

				Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f }));

				detailTable.setWidth(UnitValue.createPercentValue(93f));
				detailTable.setFixedLayout();
				detailTable.setMarginTop(20f);
				detailTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi().toUpperCase()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati().toUpperCase()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish().toUpperCase()
							: "";
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang).toUpperCase();
				Double rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate()
						: 0D;

				Integer person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax()
						: 0;
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null && person != 0
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");

				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				labelCell = new Cell()
						.add(new Paragraph(safeText(functionTitle + " FOR " + person + " PERSONS")).setFont(catFont)
								.setFontColor(headingColor).setFontSize(getFontSize(catFontSize, 14))
								.setTextAlignment(TextAlignment.CENTER).setCharacterSpacing(2f).simulateBold())
						.setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(headingColor, 1f)).setPadding(0);
				detailTable.addCell(labelCell);

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", ")).toUpperCase();
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue().toUpperCase();
				}

				String packageName = eventFunctionMasterResponseDto.getPackageName() != null
						? eventFunctionMasterResponseDto.getPackageName().toUpperCase()
						: "";
				BigDecimal packagePrice = eventFunctionMasterResponseDto.getPackPrice() != null
						? eventFunctionMasterResponseDto.getPackPrice()
						: BigDecimal.ZERO;

				middleCell = new Cell()
						.add(new Paragraph(safeText(packageName + " - RS " + rate.intValue() + "/- PP + TAXES"))
								.setFont(itemFont).setFontColor(descriptionColor)
								.setFontSize(getFontSize(itemFontSize, 14)).setCharacterSpacing(2f).simulateBold())
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
						.setBorderBottom(new SolidBorder(headingColor, 1f))
						.setBorderTop(new SolidBorder(headingColor, 1f)).setPadding(0);
				detailTable.addCell(middleCell);

				document.add(detailTable);

				/* ---------------------------------------------------- */

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				boolean isMenuAvailable = false;
				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					isMenuAvailable = true;
					int catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;

					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}
					Div menuContent = new Div();
					menuContent.setKeepTogether(true);

					Paragraph p = new Paragraph();

					for (int i = 0; i < catSpace; i++) {
						p.add("\n");
					}

					p.add(formatText(menu.getNameEnglish(), lang)).simulateBold().setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 24)).setFixedLeading(24f)
							.setTextAlignment(TextAlignment.CENTER).setFontColor(headingColor).setUnderline()
							.setPaddingTop(15f).setPaddingLeft(0).setPaddingRight(0);

					Table table = new Table(1);
					table.setBorder(Border.NO_BORDER);
					table.setWidth(UnitValue.createPercentValue(100));
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
					table.setMarginBottom(5f);

					menuContent.add(table).setMarginTop(20f);
					String text = "";

					// Sub Category
					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);
						menuContent.add(new Paragraph(text).setFont(catFont).simulateBold().setUnderline()
								.setFontSize(getFontSize(catFontSize, 24)).setMultipliedLeading(1.2f)
								.setFontColor(descriptionColor).setMarginBottom(0)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = "(" + formatText(menu.getMenuNotes(), lang) + ")";
							menuContent.add(new Paragraph(text).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 16)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = formatText(menu.getSlogan(), lang);
							menuContent.add(
									new Paragraph(text).setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 16))
											.setMultipliedLeading(1.2f).setTextAlignment(TextAlignment.CENTER)
											.setMarginTop(0).setFontColor(descriptionColor).setMarginLeft(0));
						}
					}

					// Category Image
					if (isCategoryImage != null && isCategoryImage == 1) {

						if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
							try {
								Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

//								Image img = new Image(loadImageFromResource("/flipbook/pages/logo.png"));

								img.setAutoScale(false);
								img.setHorizontalAlignment(HorizontalAlignment.CENTER);
								img.setWidth(UnitValue.createPercentValue(40f));
								img.setMarginTop(5);
								img.setMarginBottom(5);

								menuContent.add(img);
							} catch (Exception e) {
								// Skip if image loading fails
							}
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
						String subItem = "";
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						for (int i = 0; i < itemSpace; i++) {
							menuContent.add(new Paragraph("\n"));
						}

						Paragraph paragraph = new Paragraph();

						Text itemName = new Text(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 20));

						paragraph.add(itemName);

						if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().trim().isEmpty()) {

							Text instruction = new Text(formatText(item.getItemNotes(), lang)).setFont(itemFont)
									.setFontSize(16);

							paragraph.add(instruction);
						}

						paragraph.setFixedLeading(22f).setPaddingTop(20f).setTextAlignment(TextAlignment.CENTER)
								.setFontColor(headingColor).setMarginLeft(0);

						menuContent.add(paragraph);

//						menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
//								.setFontSize(getFontSize(itemFontSize, 22)).setFixedLeading(22f)
//								.setPaddingTop(20f)
//								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0));

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = formatText(item.getSlogan(), lang);
								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 16)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Sub Item
						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);
							menuContent.add(
									new Paragraph(text).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 20))
											.setMultipliedLeading(1.2f).setFontColor(descriptionColor).setMarginLeft(18)
											.setMarginTop(0f).setTextAlignment(TextAlignment.CENTER).simulateBold()
											.simulateItalic().setMarginBottom(0).setPaddingTop(10f));
						}
					}
					document.add(menuContent);
				}
			}

			// ================= PAGE NUMBERS =================
			int totalPages = pdfDocument.getNumberOfPages();
			int idx = 0;
			for (int i = 3; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
				canvas.setFontColor(descriptionColor);
				canvas.showTextAligned("Page " + ++idx + " of " + (totalPages - 2), page.getPageSize().getWidth() / 2,
						22, TextAlignment.CENTER);
				canvas.close();
			}

			if (tncPage != null) {
				bgHandler.setPageBackground(totalPages + 1, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public EventFunctionRawMaterialPermissionResponseDto getEventFunctionPermissionRawMaterial(Long eventId,
			Long eventFunctionId, Long userId) {
		System.out.println("1");
		List<Object[]> rows = eventFunctionRawMaterialPermissionRepository.findPermissionRawMaterials(eventId,
				eventFunctionId, userId);

		List<EventFunctionRawMaterialDto> permissables = new ArrayList<>();
		List<EventFunctionRawMaterialDto> notPermissables = new ArrayList<>();

		for (Object[] row : rows) {

			EventFunctionRawMaterialDto dto = new EventFunctionRawMaterialDto();
			dto.setRawMaterialId(((Number) row[3]).longValue());
			dto.setRawMaterialNameEnglish((String) row[4]);
			dto.setRawMaterialNameHindi((String) row[5]);
			dto.setRawMaterialNameGujarati((String) row[6]);

			String type = (String) row[2];

			if ("Permissable".equalsIgnoreCase(type)) {
				permissables.add(dto);
			} else if ("NotPermissable".equalsIgnoreCase(type)) {
				notPermissables.add(dto);
			}
		}

		EventFunctionRawMaterialPermissionResponseDto response = new EventFunctionRawMaterialPermissionResponseDto();

		response.setEventId(eventId);
		response.setEventFunctionId(eventFunctionId);
		response.setPermissables(permissables);
		response.setNotPermissables(notPermissables);

		return response;
	}

	// =========================================================
	// CATEGORY HEADER: subCat + name + instruction + slogan + image, all grouped
	// together
	// =========================================================
	private Div buildCategoryHeader(MenuReportResponseDto menu, int lang, PdfFont catFont, Integer catFontSize,
			PdfFont sloganFont, Integer sloganFontSize, Color descriptionFontColor, Color softGold,
			Integer isCategoryInstruction, Integer isCategorySlogan, Long isCategoryImage, Environment environment) {
		Div headerDiv = new Div();
		headerDiv.setKeepTogether(true);

		// ---- Sub category ----
		String subCat = getLocalizedText(menu.getSubCat(), menu.getSubCatHindi(), menu.getSubCatGujarati(), lang);
		if (!subCat.trim().isEmpty()) {
			headerDiv.add(
					new Paragraph(formatText(subCat, lang)).setFont(catFont).setFontSize(getFontSize(catFontSize, 12))
							.setMultipliedLeading(1.2f).setFontColor(descriptionFontColor)
							.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginBottom(0));
		}

		// ---- Category name + instruction ----
		String categoryInstruction = "";
		if (isCategoryInstruction != null && isCategoryInstruction == 1 && menu.getMenuNotes() != null
				&& !menu.getMenuNotes().trim().isEmpty()) {
			categoryInstruction = formatText(menu.getMenuNotes(), lang);
		}

		Paragraph categoryParagraph = new Paragraph().add(new Text(formatText(menu.getNameEnglish(), lang))
				.setFont(catFont).setFontSize(getFontSize(catFontSize, 20)).setFontColor(softGold));

		if (!categoryInstruction.isEmpty()) {
			categoryParagraph.add(new Text("    " + categoryInstruction).setFont(catFont)
					.setFontSize(getFontSize(catFontSize, 12)).setFontColor(descriptionFontColor));
		}

		categoryParagraph.setUnderline().setTextAlignment(TextAlignment.CENTER).setFixedLeading(17f);

		Table table = new Table(1);
		table.setWidth(UnitValue.createPercentValue(100));
		table.setBorder(Border.NO_BORDER);
		table.setKeepTogether(true);
		table.addCell(new Cell().setBorder(Border.NO_BORDER).add(categoryParagraph));

		headerDiv.add(table);

		// ---- Category slogan ----
		if (isCategorySlogan != null && isCategorySlogan == 1 && menu.getSlogan() != null
				&& !menu.getSlogan().isEmpty()) {
			headerDiv.add(new Paragraph("\"" + formatText(menu.getSlogan(), lang) + "\"").setFont(sloganFont)
					.setFontSize(getFontSize(sloganFontSize, 12)).setFontColor(descriptionFontColor)
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(0));
		}

		// ---- Category image ----
		if (isCategoryImage != null && isCategoryImage == 1 && menu.getImagePath() != null
				&& !menu.getImagePath().trim().isEmpty()) {
			try {
				System.out.println("image : " + menu.getImagePath());
				Image img = new Image(ImageDataFactory.create(menu.getImagePath()));
				img.scaleAbsolute(300, 150f);
				img.setHorizontalAlignment(HorizontalAlignment.CENTER);
				img.setMarginTop(10f);

				headerDiv.add(img);
			} catch (Exception e) {
				// ignore
			}
		}

		return headerDiv;
	}

	// =========================================================
	// ITEM BLOCK: subItem + name + instruction + slogan, all grouped together
	// =========================================================
	private Div buildItemDiv(MenuItemForReportResponseDto item, int lang, PdfFont catFont, Integer catFontSize,
			PdfFont itemFont, Integer itemFontSize, PdfFont sloganFont, Integer sloganFontSize,
			Color descriptionFontColor, Color softGold, Color creamGold, Integer isItemInstruction,
			Integer isItemSlogan) {

		Div itemDiv = new Div();
		itemDiv.setKeepTogether(true);

		Paragraph headingParagraph = new Paragraph()
				.add(new Text(formatText(item.getItemHeading(), lang)).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 20)).setFontColor(softGold))
				.setTextAlignment(TextAlignment.CENTER).setUnderline();
		itemDiv.add(headingParagraph);

		// ---- Item name ----
		Paragraph itemParagraph = new Paragraph()
				.add(new Text(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
						.setFontSize(getFontSize(itemFontSize, 16)).setFontColor(creamGold))
				.setTextAlignment(TextAlignment.CENTER).setFixedLeading(15f).setMarginTop(10f).setMarginBottom(0);

		itemDiv.add(itemParagraph);

		// ---- Sub item ----
		String subItem = getLocalizedText(item.getSubItem(), item.getSubItemHindi(), item.getSubItemGujarati(), lang);
		if (!subItem.trim().isEmpty()) {
			itemDiv.add(new Paragraph(formatText(subItem, lang)).setFont(itemFont)
					.setFontSize(getFontSize(itemFontSize, 12)).setFontColor(descriptionFontColor)
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginBottom(0));
		}

		// ---- Item instruction ----
		String itemInstruction = "";
		if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
				&& !item.getItemNotes().trim().isEmpty()) {
			itemInstruction = formatText(item.getItemNotes(), lang);
		}

		if (!itemInstruction.isEmpty()) {
			itemDiv.add(new Paragraph(itemInstruction).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 12))
					.setFontColor(descriptionFontColor).setTextAlignment(TextAlignment.CENTER).setMarginTop(0)
					.setMarginBottom(0));
		}

		// Item Slogan
		if (isItemSlogan != null && isItemSlogan == 1) {
			if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
				String text = "(" + formatText(item.getSlogan(), lang) + ")";
				itemDiv.add(new Paragraph(text).setFont(itemFont).simulateItalic()
						.setFontSize(getFontSize(sloganFontSize, 12)).setMultipliedLeading(1f).setFontColor(softGold)
						.setMarginLeft(0).setMarginTop(0).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0)
						.setPadding(0f));
			}
		}

		return itemDiv;
	}

	// =========================================================
	// SHARED HELPER: pick localized text by lang (0=English, 1=Hindi, 2=Gujarati)
	// =========================================================
	private String getLocalizedText(String english, String hindi, String gujarati, int lang) {
		if (lang == 1) {
			return hindi != null ? hindi : "";
		} else if (lang == 2) {
			return gujarati != null ? gujarati : "";
		} else {
			return english != null ? english : "";
		}
	}

	private String formatOrdinalDate(LocalDate date) {
		int day = date.getDayOfMonth();
		String suffix;
		if (day >= 11 && day <= 13) {
			suffix = "th";
		} else {
			switch (day % 10) {
			case 1:
				suffix = "st";
				break;
			case 2:
				suffix = "nd";
				break;
			case 3:
				suffix = "rd";
				break;
			default:
				suffix = "th";
			}
		}
		return day + suffix + " " + date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
	}

	@Override
	public String generateMenuItemSlogan(Long menuItemId, Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		MenuItemMasterEntity menuItem = menuItemMasterRepository.findByIdAndIsDeleteFalse(menuItemId)
				.orElseThrow(() -> new RuntimeException("Menu Item not found with id : " + menuItemId));

		return menuItem.getSlogan();
	}

	@Override
	@Transactional
	public String updateMenuItemImage(Long menuItemId, Long userId, MultipartFile image) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		MenuItemMasterEntity menuItem = menuItemMasterRepository.findByIdAndIsDeleteFalse(menuItemId)
				.orElseThrow(() -> new RuntimeException("Menu Item not found with id : " + menuItemId));

		if (image != null && !image.isEmpty()) {
			try {
				Map<String, Object> response = userFileService.storeFile(user.getId(), ModuleName.MENUITEM.toString(),
						menuItemId, FileType.IMAGE.toString(), image);
				String path = (String) response.get("fullPath");

				return path;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public String generateExclusiveReportType15_1(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {
		try {
//			PdfFont headerFont1 = loadFont("/fonts/Cambria.ttf");
//			PdfFont headerFont2 = loadFont("/fonts/nunito-black.ttf");

			PdfFont font1 = loadFont("/fonts/arial-regular.ttf");
			PdfFont font2 = loadFont("/fonts/cinzel-dec-black.otf");
			PdfFont font3 = loadFont("/fonts/Cambria.ttf");
			PdfFont font4 = loadFont("/fonts/nunito-black.ttf");

//			Integer catFontSize = req.getCatFontSize();
//			Integer itemFontSize = req.getItemFontSize();
//			Integer sloganFontSize = req.getSloganFontSize();

			Integer catFontSize = 0;
			Integer itemFontSize = 0;
			Integer sloganFontSize = 0;

			loadLicense();

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					themeLabel = "", addressLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				font1 = font2 = font3 = font4 = loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				addressLabel = "पता";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansTamil-Regular.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					addressLabel = "முகவரி";
				} else if (language.equalsIgnoreCase("Telugu")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansTelugu-Regular.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					addressLabel = "చిరునామా";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					addressLabel = "വിലാസം";
				} else if (language.equalsIgnoreCase("Marathi")) {
					font1 = font2 = font3 = font4 = loadFont("/fonts/Nirmala.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					addressLabel = "पत्ता";
				} else {
					font1 = font2 = font3 = font4 = loadFont("/fonts/NotoSansGujarati-Regular.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					addressLabel = "સરનામું";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
				// English
				System.out.println("Loading English font...");

				hostLabel = "Client Name";
				phone = "Mobile";
				eDate = "Date";
				eName = "Event";
				venueLabel = "Venue";
				personLabel = "Person";
				eTime = "Time";
				fNote = "Food Status";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Spl";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				addressLabel = "Address";

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
//			Color creamGold = new DeviceRgb(r2, g2, b2);
//			Color softGold = new DeviceRgb(r1, g1, b1);
//			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			Color creamGold = new DeviceRgb(109, 67, 3);
			Color softGold = new DeviceRgb(147, 91, 57);
			Color descriptionColor = new DeviceRgb(109, 67, 3);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "Data not found.";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(120, 140, 120, 140);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/aroma_front1.png");
//			ImageData secondBgData = loadImageFromResource("/flipbook/pages/aroma_second_page1.png");
//			ImageData thirdBgData = loadImageFromResource("/flipbook/pages/aroma_third_page1.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/aroma_watermark1.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/aroma_last1.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData secondBgData = null;
			if (adminTemplate.getTemplateMaster().getSecondFrontPage() != null) {
				secondBgData = loadImageFromResource(environment.getProperty("app.image.url")
						+ adminTemplate.getTemplateMaster().getSecondFrontPage());
			}
			ImageData thirdBgData = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null) {
				thirdBgData = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			Paragraph invisibleContent = null;
			if (isCompanyDetails == 1) {
				bgHandler.setDefaultBackground(mainBgData);
				invisibleContent = new Paragraph("\u00A0").setFontColor(creamGold);
				invisibleContent.setFontSize(35);
				invisibleContent.setPaddingTop(140f);
				invisibleContent.setPaddingLeft(165f);
				document.add(invisibleContent);

				if (secondBgData != null) {
					int secondPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(secondPageNum, secondBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					invisibleContent = new Paragraph("\u00A0");
					invisibleContent.setFontSize(1);
					document.add(invisibleContent);
				}

				if (thirdBgData != null) {
					int thirdPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(thirdPageNum, thirdBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					invisibleContent = new Paragraph("\u00A0");
					invisibleContent.setFontSize(1);
					document.add(invisibleContent);
				}
			}

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String partyAddress = eventDto.getPartyAddress() != null ? eventDto.getPartyAddress() : "";
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
			String eventTime = eventDto.getEventTime() != null ? eventDto.getEventTime() : "";

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String fn_name = "";
			ImageData borderData = loadImageFromResource("/flipbook/pages/border.png");
			PdfImageXObject borderXObject = new PdfImageXObject(borderData);
			Image borderImg = new Image(borderXObject);

			borderImg.setAutoScale(false);
			borderImg.setWidth(UnitValue.createPercentValue(50f));
			borderImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
			borderImg.setMarginTop(2f);
			borderImg.setMarginBottom(5f);

			String person = eventDto.getPax() != null ? eventDto.getPax().toString() : "0";

			String startTimeText = (eventDto.getEventStartTimestamp() != null ? eventDto.getEventStartTimestamp()
					: "TBD");

			bgHandler.setDefaultBackground(watermarkBgData);
			if (isCompanyDetails == 1) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			}

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(10);

			// Styles
			Style labelStyle = new Style().setFont(font3).setFontColor(softGold)
					.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);

			Style valueStyle = new Style().setFont(font4).setFontColor(creamGold)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER);

			// Common Method
			BiConsumer<String, String> addRow = (label, value) -> {

				// Label Cell
				Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle).setFixedLeading(20))
						.setBorder(Border.NO_BORDER).setPaddingTop(9).setPaddingBottom(3)
						.setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(labelCell);

				// Value Cell
				Cell valueCell = new Cell()
						.add(new Paragraph(safeText(value)).simulateBold().addStyle(valueStyle).setFixedLeading(18))
						.setBorder(Border.NO_BORDER).setPaddingBottom(10).setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(valueCell);
			};

			// Party Name
			addRow.accept(hostLabel, hostName);

			if (billingName != null && billingName.trim().length() != 0) {
				// Billing Name
				addRow.accept(billingNameLabel, billingName);
			}

			// Mobile No
			addRow.accept(phone, mobileNo);

			if (eventStartDate != null && eventStartDate.trim().length() != 0 && eventEndDate != null
					&& eventEndDate.trim().length() != 0) {
				// Event Date
				addRow.accept(eDate, eventStartDate + " - " + eventEndDate);
			}

			// Event Name
			addRow.accept(eName, eventName);

			if (venue != null && venue.trim().length() != 0) {
				// Venue
				addRow.accept(venueLabel, venue);
			}
			// Remarks

			String notes = foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")");

			if (notes != null && notes.trim().length() != 0) {
				// Food Notes
				addRow.accept(fNote, notes);
			}

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark.trim().length() != 0) {
				addRow.accept(remarks, remark);
			}

			if (service.trim().length() != 0) {
				addRow.accept(serviceLabel, service);
			}

			if (theme.trim().length() != 0) {
				addRow.accept(themeLabel, theme);
			}

			document.add(detailTable);

			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				String fnName = eventFunctionMasterResponseDto.getFunctionName() != null
						? eventFunctionMasterResponseDto.getFunctionName()
						: "";

				String fnPax = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "";

				String fnStartTime = eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "";

				String functionVenue = "";
				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				Table fnTable = new Table(UnitValue.createPercentArray(new float[] { 1f }));
				fnTable.setWidth(UnitValue.createPercentValue(100f));
				fnTable.setFixedLayout();
				fnTable.setMarginTop(10f);
				fnTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

				cell = new Cell()
						.add(new Paragraph().add(new Text(safeText(fnName)).setFontColor(softGold).setFontSize(24f)
								.setFont(font2).simulateBold()).setFixedLeading(24f).setPaddingTop(10f))
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text("(" + fnPax + " Person, " + fnStartTime + ")")
								.setFontColor(softGold).setFontSize(17f).setFont(font3)).setFixedLeading(17f))
						.setBorder(Border.NO_BORDER).setPaddingTop(10f).setTextAlignment(TextAlignment.CENTER);
				fnTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph().add(new Text("Venue : " + functionVenue).setFontColor(softGold)
								.setFontSize(17f).setFont(font3)).setFixedLeading(17f))
						.setBorder(Border.NO_BORDER).setPaddingTop(10f).setTextAlignment(TextAlignment.CENTER);
				fnTable.addCell(cell);

				try {
					fnTable.addCell(new Cell().add(borderImg).setBorder(Border.NO_BORDER));
				} catch (Exception e) {
					e.printStackTrace();
				}

				document.add(fnTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					int catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;

					String subCat = "";
					String catHeading = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
						catHeading = menu.getCatHeadingHindi() != null ? menu.getCatHeadingHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
						catHeading = menu.getCatHeadingGujarati() != null ? menu.getCatHeadingGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
						catHeading = menu.getCatHeadingEnglish() != null ? menu.getCatHeadingEnglish() : "";
					}

					// Category-level: title + subcat + instructions + slogan + image + ALL items
					// together
					Div menuContent = new Div();

					if (req.getIsAllItemTogether() == 1) {
						menuContent.setKeepTogether(true);
					}

					Paragraph p = new Paragraph();

					for (int i = 0; i < catSpace; i++) {
						p.add("\n");
					}

					if (catHeading != null && !catHeading.trim().isEmpty()) {
						p.add(new Paragraph(formatText(catHeading, lang)).setFont(font2).setUnderline()
								.setFontSize(getFontSize(catFontSize, 26)).setMultipliedLeading(1f)
								.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0)
								.setPaddingRight(0)).setMarginTop(15f).setMarginBottom(5f);
					}

					p.add("\n" + menu.getNameEnglish().toUpperCase()).setUnderline().setFont(font2)
							.setFontSize(getFontSize(catFontSize, 24)).setMultipliedLeading(1.1f).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setPaddingLeft(0).setPaddingRight(0);
//					p.setKeepTogether(true);
					menuContent.add(p).setMarginTop(30f);

					String text = "";

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = "(" + formatText(menu.getSlogan(), lang) + ")";
							menuContent.add(new Paragraph(text).setFont(font3)
									.setFontSize(getFontSize(sloganFontSize, 14)).setTextAlignment(TextAlignment.CENTER)
									.setMarginTop(0).setFontColor(descriptionColor).setMarginLeft(0));
						}
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							menuContent.add(new Paragraph(text).setFont(font3).setFontSize(getFontSize(catFontSize, 14))
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Sub Category
					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);
						menuContent.add(new Paragraph(text).setFont(font4).setFontSize(getFontSize(catFontSize, 18))
								.setFontColor(descriptionColor).setMarginBottom(0)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0)
								.simulateBold());
					}

					// Category Image
					if (isCategoryImage == 1 && menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
						try {
//							System.out.println("image : " + menu.getImagePath());
							Image img = new Image(ImageDataFactory.create(menu.getImagePath()));
//							Image img = new Image(loadImageFromResource("/flipbook/pages/Aftermeal.png"));

							img.scaleAbsolute(300f, 150f);
							img.setHorizontalAlignment(HorizontalAlignment.CENTER);
							img.setMarginTop(10f);

							menuContent.add(img);
						} catch (Exception e) {
							// Skip if image loading fails
						}
					}

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
						String subItem = "";
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						String itemHeading = item.getItemHeading();

						// Item-level: heading + name + sub-item + instructions + slogan together
						Div itemContent = new Div();

						if (req.getIsAllItemTogether() == 1) {
							itemContent.setKeepTogether(true);
						}

						for (int i = 0; i < itemSpace; i++) {
							itemContent.add(new Paragraph("\n"));
						}

						if (itemHeading != null && !itemHeading.trim().isEmpty()) {
							itemContent
									.add(new Paragraph(formatText(itemHeading, lang)).setFont(font2).setUnderline()
											.setFontSize(getFontSize(catFontSize, 24)).setMultipliedLeading(1f)
											.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER)
											.setPaddingLeft(0).setPaddingRight(0))
									.setMarginTop(15f).setMarginBottom(5f);
						}

						itemContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(font4)
								.setFontSize(getFontSize(itemFontSize, 17)).setFixedLeading(24f).simulateBold()
								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0)
								.setMarginTop(15f));

						// Item Slogan
						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = "(" + formatText(item.getSlogan(), lang) + ")";
								itemContent.add(new Paragraph(text).setFont(font3).simulateItalic()
										.setFontSize(getFontSize(sloganFontSize, 14)).setMultipliedLeading(1f)
										.setFontColor(softGold).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								itemContent.add(new Paragraph(text).setFont(font3)
										.setFontSize(getFontSize(itemFontSize, 15)).setMultipliedLeading(1f)
										.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Sub Item
						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);
							itemContent
									.add(new Paragraph(text).setFont(font4).setFontSize(getFontSize(itemFontSize, 20))
											.simulateBold().setFontColor(descriptionColor).setMultipliedLeading(1f)
											.setMarginLeft(0).setMarginTop(0).setTextAlignment(TextAlignment.CENTER)
											.setMarginBottom(0).setPadding(0f));
						}

						menuContent.add(itemContent);
					}

					document.add(menuContent);
				}

			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// EXTRA CHARGES PAGE
			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ── Set background for this page (same watermark as content pages) ──
					int extraPageNum = pdfDocument.getNumberOfPages() + 1;
					bgHandler.setPageBackground(extraPageNum, watermarkBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(font3)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline()
							// .setBackgroundColor(new DeviceRgb(255, 255, 0)) // yellow highlight
							.setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// Heading title: HEADING NAME
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase()).setFont(font3)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold).setUnderline()
								.setTextAlignment(TextAlignment.LEFT).setMarginTop(12f).setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(font3).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(softGold).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												font3, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0", font3,
										creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												font3, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												font3, creamGold, catFontSize, softGold));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", font3, creamGold, catFontSize, softGold));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(font3).setFontSize(getFontSize(catFontSize, 13)).setFontColor(softGold)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(font3).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ============ LAST PAGE ============
			bgHandler.setPageBackground(lastPageNum, lastBgData);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			invisibleContent = new Paragraph("\u00A0");
			invisibleContent.setFontSize(1);
			document.add(invisibleContent);

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, watermarkBgData);
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

						Paragraph termsTitle = new Paragraph("Terms & Conditions").setFont(font3).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(font3).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();
			for (int i = 4; i < totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());
				canvas.showTextAligned("Page " + (i - 3) + " of " + (totalPages - 4), page.getPageSize().getWidth() / 2,
						18, TextAlignment.CENTER);
				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType21(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req, Integer showAddOnLabel) {

		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					permissableLabel = "", notPermissableLabel = "", themeLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				permissableLabel = "अनुमत्य सामग्री";
				notPermissableLabel = "अस्वीकृत सामग्री";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					permissableLabel = "அனுமதிக்கப்பட்ட பொருட்கள்";
					notPermissableLabel = "அனுமதிக்கப்படாத பொருட்கள்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					permissableLabel = "అనుమతించదగిన వస్తువులు";
					notPermissableLabel = "అనుమతించని వస్తువులు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					permissableLabel = "അനുവദനീയമായ ഇനങ്ങൾ";
					notPermissableLabel = "അനുവദനീയമല്ലാത്ത ഇനങ്ങൾ";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					permissableLabel = "अनुमत्य वस्तू";
					notPermissableLabel = "अस्वीकृत वस्तू";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					permissableLabel = "માન્ય સામગ્રી";
					notPermissableLabel = "અમાન્ય સામગ્રી";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
				// English
				System.out.println("Loading English font...");

				hostLabel = "Guest Name";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Type of Event";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preference";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				permissableLabel = "Permissable Items";
				notPermissableLabel = "Not Permissable Items";

				catFont = getFont(req.getCatFontId(), false, "times");
				catFontBold = getFont(req.getCatFontId(), true, "times");

				itemFont = getFont(req.getItemFontId(), false, "times");
				itemFontBold = getFont(req.getItemFontId(), true, "times");

				sloganFont = getFont(req.getSloganFontId(), false, "times");
				sloganFontBold = getFont(req.getSloganFontId(), true, "times");

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
			Color creamGold = new DeviceRgb(r2, g2, b2);
			Color softGold = new DeviceRgb(r1, g1, b1);
			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);

			if (eventDto == null) {
				return "";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			document.setMargins(60, 35, 60, 40);

			// Load all background images upfront
//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/krishna_front_4.png");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/krishna_menu_4.png");
//			ImageData tncPage = loadImageFromResource("/flipbook/pages/krishna_detail_4.png");
//			ImageData tncPage = null;
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/krishna_last_4.png");

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null
					&& adminTemplate.getTemplateMaster().getCatBgPage().trim().length() != 0) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null
					&& adminTemplate.getTemplateMaster().getExtraPage().trim().length() != 0) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

			// Create and register the background event handler
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			Paragraph invisibleContent = null;
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(1, mainBgData); // Page 1: Main
			}

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			if (isCompanyDetail == 1) {
				// Add invisible content to ensure page 1 exists
				invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(creamGold);
				invisibleContent.setFontSize(35);
				invisibleContent.setPaddingTop(140f);
				invisibleContent.setPaddingLeft(165f);
				document.add(invisibleContent);

				int secondPageNum = pdfDocument.getNumberOfPages() + 1;
				bgHandler.setPageBackground(secondPageNum, detailsPage);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}
			// Create the last page with minimal invisible content

			if (isCompanyDetail == 1) {
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			}

			Cell cell;

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			String permissable_item = "";
			String not_permissable_item = "";
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				permissable_item = eventDto.getPermissable_item() != null ? eventDto.getPermissable_item() : "";
				not_permissable_item = eventDto.getNot_permissable_item() != null ? eventDto.getNot_permissable_item()
						: "";
			}

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(10f);

			// Common Styles
			Style labelStyle = new Style().setFont(catFontBold).setFontColor(softGold)
					.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);

			Style valueStyle = new Style().setFont(itemFont).setFontColor(descriptionColor)
					.setFontSize(getFontSize(itemFontSize, 18)).setTextAlignment(TextAlignment.CENTER);

			BiConsumer<String, String> addRow = (label, value) -> {
				// Label
				Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
						.setBorder(Border.NO_BORDER).setPaddingTop(15f).setPaddingBottom(2f)
						.setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(labelCell);

				// Value
				Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
						.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setTextAlignment(TextAlignment.CENTER);

				detailTable.addCell(valueCell);
			};

			addRow.accept(hostLabel, hostName);

			if (billingName != null && !billingName.trim().isEmpty()) {
				addRow.accept(billingNameLabel, billingName);
			}

			addRow.accept(phone, mobileNo);
			/*
			 * if (eventStartDate != "" && eventEndDate != "") { addRow.accept(eDate,
			 * eventStartDate + " - " + eventEndDate); }
			 */

			if (eventStartDate != null && !eventStartDate.isEmpty() && eventEndDate != null
					&& !eventEndDate.isEmpty()) {

				if (eventStartDate.equals(eventEndDate)) {
					addRow.accept(eDate, eventStartDate);
				} else {
					addRow.accept(eDate, eventStartDate + " - " + eventEndDate);
				}
			}
			addRow.accept(eName, eventName);

			if (venue != "") {
				addRow.accept(venueLabel, venue);
			}

			if (req.getIsShowEventRemarks() == 1 && remark != null && remark != "") {
				addRow.accept(remarks, remark);
			}

			addRow.accept(fNote, foodNotesName.toString() + (foodNotes.trim().isEmpty() ? "" : " (" + foodNotes + ")"));

			if (service.trim().length() != 0) {
				addRow.accept(serviceLabel, service);
			}

			if (theme.trim().length() != 0) {
				addRow.accept(themeLabel, theme);
			}
			document.add(detailTable);

			EventFunctionRawMaterialPermissionResponseDto permissionResponseDto = getEventFunctionPermissionRawMaterial(
					eventId, eventFunctionId, userId);

			boolean hasPermissables = permissionResponseDto.getPermissables() != null
					&& !permissionResponseDto.getPermissables().isEmpty();

			boolean hasNotPermissables = permissionResponseDto.getNotPermissables() != null
					&& !permissionResponseDto.getNotPermissables().isEmpty();

			if (hasPermissables || hasNotPermissables) {

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
						.setMarginTop(10f);

				BiConsumer<String, String> addItemRow = (label, value) -> {
					Cell labelCell = new Cell().add(new Paragraph(safeText(label)).addStyle(labelStyle))
							.setBorder(Border.NO_BORDER).setPaddingTop(15f).setPaddingBottom(2f)
							.setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(labelCell);

					Cell valueCell = new Cell().add(new Paragraph(safeText(value)).addStyle(valueStyle))
							.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setTextAlignment(TextAlignment.CENTER);

					itemTable.addCell(valueCell);
				};

				if (hasPermissables) {
					addItemRow.accept(permissableLabel, "");

					String permissables = permissionResponseDto.getPermissables().stream()
							.map(item -> getRawMaterialNameByLang(item, lang)).collect(Collectors.joining(", "));

					addItemRow.accept("", permissables);
				}

				if (hasNotPermissables) {
					addItemRow.accept(notPermissableLabel, "");

					String notPermissables = permissionResponseDto.getNotPermissables().stream()
							.map(item -> getRawMaterialNameByLang(item, lang)).collect(Collectors.joining(", "));

					addItemRow.accept("", notPermissables);
				}

				document.add(itemTable);
			}

			String fn_name = "";
			// ============ MENU CONTENT PAGES ============
			for (EventFunctionReportResponseDto eventFunctionMasterResponseDto : eventDto.getFunctions()) {

				// Always start each function on a new page
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				SolidLine lineDrawer = new SolidLine(1f);
				lineDrawer.setColor(softGold);

				// Create main table with 2 columns for the header layout
				float[] columnWidths = { 49f, 2f, 49f };
				Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setBorder(Border.NO_BORDER);
				headerTable.setMarginTop(20f);

				// 1st row
				String mainFunction = eventFunctionMasterResponseDto.getMainFunction() != null
						? eventFunctionMasterResponseDto.getMainFunction()
						: "";
				// 1st row
				if (mainFunction.trim().length() != 0) {
					Paragraph eventPara = new Paragraph()
							.add(new com.itextpdf.layout.element.Text(mainFunction).setFont(catFontBold)
									.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
					Cell mainFunctionCell = new Cell(1, 3).add(eventPara);
					mainFunctionCell.setBorder(Border.NO_BORDER);
					mainFunctionCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(mainFunctionCell);
				}

				String functionTitle = formatText(eventFunctionMasterResponseDto.getFunctionName(), lang);
				String rate = eventFunctionMasterResponseDto.getRate() != null
						? eventFunctionMasterResponseDto.getRate().toString()
						: "";
				String ratePostFix = eventFunctionMasterResponseDto.getRatePostFix() != null
						? " (" + rate + " " + eventFunctionMasterResponseDto.getRatePostFix() + ") "
						: "";
				Paragraph eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(functionTitle + ratePostFix).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold).setUnderline());
				Cell eventCell = new Cell(1, 3).add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				// 2ed row
				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(personLabel).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String person = eventFunctionMasterResponseDto.getPax() != null
						? eventFunctionMasterResponseDto.getPax().toString()
						: "0";
				String foodType = eventFunctionMasterResponseDto.getFoodType() != null
						? " (" + eventFunctionMasterResponseDto.getFoodType() + ")"
						: "";

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(person + foodType).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 3rd raw
				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(eTime).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				String startTimeText = (eventFunctionMasterResponseDto.getFunctionStartTimestamp() != null
						? eventFunctionMasterResponseDto.getFunctionStartTimestamp()
						: "TBD");

				String date = startTimeText.split(" ")[0];
				String time = startTimeText.split(" ")[1] + " " + startTimeText.split(" ")[2];

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(date + " " + time).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				String functionVenue = "", functionNotes = "";
				if (lang == 1) {
					functionNotes = eventFunctionMasterResponseDto.getNotesHindi() != null
							? eventFunctionMasterResponseDto.getNotesHindi()
							: "";
				} else if (lang == 2) {
					functionNotes = eventFunctionMasterResponseDto.getNotesGujarati() != null
							? eventFunctionMasterResponseDto.getNotesGujarati()
							: "";
				} else {
					functionNotes = eventFunctionMasterResponseDto.getNotesEnglish() != null
							? eventFunctionMasterResponseDto.getNotesEnglish()
							: "";
				}

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
						.findBanquetByEventFunctionId(eventId, eventFunctionMasterResponseDto.getFunctionId());
				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = lang == 1 ? eventFunctionMasterResponseDto.getFunctionVenueHindi()
							: lang == 2 ? eventFunctionMasterResponseDto.getFunctionVenueGujarati()
									: eventFunctionMasterResponseDto.getFunctionVenue();
				}

				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(venueLabel).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(functionVenue).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph()
						.add(new com.itextpdf.layout.element.Text(remarks).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold))
						.setPaddingLeft(170f);
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(":").setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.CENTER);
				headerTable.addCell(eventCell);

				eventPara = new Paragraph().add(new com.itextpdf.layout.element.Text(functionNotes).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));
				eventCell = new Cell().add(eventPara);
				eventCell.setBorder(Border.NO_BORDER);
				eventCell.setTextAlignment(TextAlignment.LEFT);
				headerTable.addCell(eventCell);

				// 4th raw
				if (req.getIsWithPrice() == 1) {

					String label;
					String value;

					if (eventFunctionMasterResponseDto.getIsPackage()) {
						label = pckPrice;
						value = eventFunctionMasterResponseDto.getPackagePrice().toString();
					} else {
						label = price;
						value = eventFunctionMasterResponseDto.getRate().toString();
					}

					// Label Cell
					eventPara = new Paragraph().add(new Text(label).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold)).setPaddingLeft(170f);

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);

					// Colon Cell
					eventPara = new Paragraph().add(new Text(":").setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.CENTER);
					headerTable.addCell(eventCell);

					// Value Cell
					eventPara = new Paragraph().add(new Text(value).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 16)).setFontColor(creamGold));

					eventCell = new Cell().add(eventPara);
					eventCell.setBorder(Border.NO_BORDER);
					eventCell.setTextAlignment(TextAlignment.LEFT);
					headerTable.addCell(eventCell);
				}

				document.add(headerTable);

				List<MenuReportResponseDto> menuReportResponseDtos = eventFunctionMasterResponseDto.getMenuCategories();

				// Add menu content
				for (MenuReportResponseDto menu : menuReportResponseDtos) {
					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}

					Div menuContent = new Div();
					menuContent.setKeepTogether(true);

					Paragraph p = new Paragraph(formatText(menu.getNameEnglish(), lang)
							+ (showAddOnLabel == 1 && menu.getIsAddOnCat() ? " (Add On)" : "")).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 20)).setFixedLeading(17f).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setPaddingLeft(0).setPaddingRight(0);

					Table table = new Table(1);
					table.setBorder(Border.NO_BORDER);
					table.setWidth(UnitValue.createPercentValue(100));
					table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
					table.setMarginBottom(5f);

					menuContent.add(table).setMarginTop(20f);
					String text = "";

					// Sub Category
					if (subCat.trim().length() != 0) {
						text = formatText(subCat, lang);
						menuContent.add(new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 12))
								.setMultipliedLeading(1.2f).setFontColor(descriptionColor).setMarginBottom(0)
								.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
					}

					// Category Instructions
					if (isCategoryInstruction != null && isCategoryInstruction == 1) {
						if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
							text = formatText(menu.getMenuNotes(), lang);
							menuContent.add(new Paragraph(text).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}
					}

					// Category Slogan
					if (isCategorySlogan != null && isCategorySlogan == 1) {
						if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
							text = " \"" + formatText(menu.getSlogan(), lang) + "\"";
							menuContent.add(
									new Paragraph(text).setFont(sloganFont).setFontSize(getFontSize(sloganFontSize, 12))
											.setMultipliedLeading(1.2f).setTextAlignment(TextAlignment.CENTER)
											.setMarginTop(0).setFontColor(descriptionColor).setMarginLeft(0));
						}
					}

					if (isCategoryImage == 1) {
						// Category Image
						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

									img.setWidth(140);
									img.setHeight(100);

									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}
						}
					}

					// Add one blank line before items
					menuContent.add(new Paragraph(" ").setFontSize(5f).setMarginTop(0).setMarginBottom(0));

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
						String subItem = "";
						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)
								+ (showAddOnLabel == 1 && item.getIsAddOnItem() ? " (Add On)" : "")).setFont(itemFont)
								.setFontSize(getFontSize(itemFontSize, 16)).setFixedLeading(15f)
								.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0));

						// Sub Item
						if (subItem.trim().length() != 0) {
							text = formatText(subItem, lang);
							menuContent.add(new Paragraph(text).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
						}

						if (isItemSlogan != null && isItemSlogan == 1) {
							if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {
								text = " \"" + formatText(item.getSlogan(), lang) + "\"";
								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

						// Item Instructions
						if (isItemInstruction != null && isItemInstruction == 1) {
							if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {
								text = formatText(item.getItemNotes(), lang);
								menuContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 12)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f));
							}
						}

					}

					document.add(menuContent);
				}
			}

			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {
						bgHandler.setPageBackground(++lastPageNum, watermarkBgData);

						Paragraph termsTitle = new Paragraph("Special Instruction").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setMarginTop(15f)
								.setFontColor(softGold);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(90f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(creamGold);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			if (req.getIsAddDecoration() == 1) {
				List<DecoreReportResponseDto> decoreReportResponseDtos = eventDto.getFunctions().stream().flatMap(
						fn -> Optional.ofNullable(fn.getDecoreCategories()).orElse(Collections.emptyList()).stream())
						.collect(Collectors.toList());

				if (decoreReportResponseDtos != null) {
//					if (isAddMenu == 0) {
					bgHandler.setDefaultBackground(watermarkBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//					}
					document.add(new Paragraph("Setup Details:").setFont(catFont).setFontSize(22f).simulateBold()
							.setUnderline());

					for (DecoreReportResponseDto decore : decoreReportResponseDtos) {
						Div decoreContent = new Div();
						decoreContent.setKeepTogether(false);
						String text = "";
						for (DecoreItemReportResponseDto item : decore.getDecoreItems()) {
							List<DecoreMainCategoryItemImagesMasterEntity> imagesMasterEntities = decoreMainCategoryItemImagesMasterRepository
									.findAllByDecoreItem_IdAndIsDeleteFalse(item.getId());
							Integer itemQty = item.getItemQty();
							Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
							BigDecimal itemPrice = item.getPrice();

							String subItem = "";
							if (lang == 1) {
								subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
							} else if (lang == 2) {
								subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
							} else {
								subItem = item.getSubItem() != null ? item.getSubItem() : "";
							}

							BigDecimal price1 = itemPrice != null ? itemPrice : BigDecimal.ZERO;
							int qty = itemQty != null ? itemQty : 0;
							BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));

							String postLabel = "";
							if (qty > 1) {
								postLabel = " /- + Tax Each";
							} else {
								postLabel = " /- + Tax";
							}
							String text1 = "\u2022 " + formatText(item.getNameEnglish(), lang)
									+ (subItem.trim().length() != 0 ? " " + subItem : "")
									+ (totalAmount.compareTo(BigDecimal.ZERO) != 0
											? " @RS." + totalAmount.intValue() + postLabel
											: "");

							decoreContent.add(
									new Paragraph(text1).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 19))
											.setFixedLeading(20f).setTextAlignment(TextAlignment.LEFT)
											.setFontColor(creamGold).setMarginLeft(20f).setMarginTop(15f));

							// Item Instructions
							if (isItemInstruction != null && isItemInstruction == 1) {
								if (item.getDecoreItemNotes() != null && !item.getDecoreItemNotes().isEmpty()) {
									text = formatText(item.getDecoreItemNotes(), lang);
									decoreContent.add(new Paragraph(text).setFont(itemFont)
											.setFontSize(getFontSize(itemFontSize, 19)).setMultipliedLeading(1.2f)
											.setFontColor(descriptionColor).setMarginLeft(0f).setMarginTop(0)
											.setTextAlignment(TextAlignment.LEFT).setMarginBottom(0).setPadding(0f)
											.setPaddingLeft(130f));
								}
							}

							if (imagesMasterEntities != null && !imagesMasterEntities.isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory
											.create(environment.getProperty("app.image.url") + decore.getImagePath()));
//									Image img = new Image(loadImageFromResource("/flipbook/pages/logo.png"));

									img.setAutoScale(false);
									img.scaleToFit(300, 200);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									decoreContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}

							for (int i = 0; i < itemSpace; i++) {
								decoreContent.add(new Paragraph("\n"));
							}
						}

						document.add(decoreContent);
					}
				}
			}

			lastPageNum = pdfDocument.getNumberOfPages() + 1;

			// EXTRA CHARGES PAGE
			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {

				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					// ── Set background for this page (same watermark as content pages) ──
					bgHandler.setPageBackground(lastPageNum, watermarkBgData);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					// ── Main Title: "-: EXTRA CHARGE :-" ─────────────────────────────
					Paragraph extraTitle = new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.CENTER).setUnderline()
							// .setBackgroundColor(new DeviceRgb(255, 255, 0)) // yellow highlight
							.setMarginBottom(15f);
					document.add(extraTitle);

					// ── Loop each heading ─────────────────────────────────────────────
					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						// Heading title: HEADING NAME
						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
								.setUnderline().setTextAlignment(TextAlignment.LEFT).setMarginTop(12f)
								.setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(softGold).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(softGold, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, creamGold, catFontSize, softGold));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, creamGold, catFontSize, softGold));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, creamGold, catFontSize, softGold));
								}
							}
						}

						document.add(chargeTable);

						// ── Heading Total ─────────────────────────────────────────────
						Paragraph headingTotalPara = new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 13)).setFontColor(softGold)
								.setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f);
						document.add(headingTotalPara);
					}

					// ── Grand Total ───────────────────────────────────────────────────
					Paragraph grandTotalPara = new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(softGold)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline();
					document.add(grandTotalPara);

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			// ============ Terms And Condition PAGE ============
			if (req.getIsNotes() == 1 && tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (req.getIsNotes() == 1 && tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}
			System.out.println("show last page : " + req.getShowLastPage());
			// ============ LAST PAGE ============
			if (req.getShowLastPage() == 1) {
				bgHandler.setPageBackground(lastPageNum, lastBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);
				document.add(invisibleContent);
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType22(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req, Integer showAddOnLabel) {

		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", customMenu = "", l1 = "", pckPrice = "", price = "", remarks = "",
					billingNameLabel = "", serviceLabel = "", themeLabel = "", forLabel = "";

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
//				itemFont = boldFont;
				System.out.println("Hindi font loaded successfully");
				customMenu = "विशेष मेनू";
				eDate = "दिनांक";
				venueLabel = "कार्यक्रम स्थल";
				phone = "संपर्क जानकारी";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन संबंधी निर्देश";
				l1 = "बजे से";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				eName = "फॉर द जॉयस ओकेज़न ऑफ";
				forLabel = "के लिए";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					// Tamil
					customMenu = "சிறப்பு மெனு";
					eDate = "நிகழ்ச்சி தேதி";
					venueLabel = "நிகழ்ச்சி இடம்";
					phone = "தொடர்பு தகவல்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு தொடர்பான வழிமுறைகள்";
					l1 = "மணி முதல்";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					eName = "ஃபார் த ஜாயஸ் ஒக்கேஷன் ஆஃப்";
					forLabel = "க்காக";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					// Telugu
					customMenu = "ప్రత్యేక మెనూ";
					eDate = "కార్యక్రమ తేదీ";
					venueLabel = "కార్యక్రమ స్థలం";
					phone = "సంప్రదింపు సమాచారం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన సంబంధిత సూచనలు";
					l1 = "గంటల నుండి";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					eName = "ఫర్ ది జాయస్ అకేషన్ ఆఫ్";
					forLabel = "కోసం";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					customMenu = "പ്രത്യേക മെനു";
					eDate = "പരിപാടിയുടെ തീയതി";
					venueLabel = "പരിപാടി സ്ഥലം";
					phone = "ബന്ധപ്പെടാനുള്ള വിവരം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണവുമായി ബന്ധപ്പെട്ട നിർദേശങ്ങൾ";
					l1 = "മണി മുതൽ";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					eName = "ഫോർ ദ ജോയസ് ഒക്കേഷൻ ഓഫ്";
					forLabel = "വേണ്ടി";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					customMenu = "विशेष मेनू";
					eDate = "दिनांक";
					venueLabel = "कार्यक्रम स्थळ";
					phone = "संपर्क माहिती";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजनासंबंधी सूचना";
					l1 = "वाजल्यापासून";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					eName = "फॉर द जॉयस ओकेजन ऑफ";
					forLabel = "साठी";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					customMenu = "વિશેષ મેનુ";
					eDate = "કાર્યક્રમની તારીખ";
					venueLabel = "કાર્યક્રમ સ્થળ";
					phone = "સંપર્ક માહિતી";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન સંબંધિત સૂચનાઓ";
					l1 = "વાગ્યાથી";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					eName = "ફોર ધ જોયસ ઓકેઝન ઑફ";
					forLabel = "માટે";
				}
//				itemFont = boldFont;
				System.out.println("Gujarati font loaded successfully");
			} else {
				// English
				System.out.println("Loading English font...");
				customMenu = "Customized Menu";
				eDate = "Date of the Event";
				venueLabel = "Venue";
				personLabel = "Guest";
				phone = "Contact Information";
				eTime = "Timing";
				fNote = "Food Preparation Notes";
				l1 = "Onwards";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				eName = "For the joyous occasion of";
				forLabel = "For";
				catFont = getFont(null, false, "palatino");
				catFontBold = getFont(null, true, "palatino");

				itemFont = getFont(null, false, "palatino");
				itemFontBold = getFont(null, true, "palatino");

				sloganFont = getFont(null, false, "palatino");
				sloganFontBold = getFont(null, true, "palatino");

//				itemFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

				System.out.println("English font loaded successfully");
			}

			// ── Colors from template ───────────────────────────────────────────
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

			Color headingColor = new DeviceRgb(r1, g1, b1); // category name, item name
			Color contentColor = new DeviceRgb(r2, g2, b2); // slogan, instructions, values
			Color descriptionFontColor = new DeviceRgb(r3, g3, b3);
			// ── Fetch event data ───────────────────────────────────────────────
			EventReportResponseDto eventDto = fetchAndPrepareEventReportData(eventId, eventFunctionId, lang, userId);
			if (eventDto == null)
				return "";

			// ── Output file setup ──────────────────────────────────────────────
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventDto.getEventNo() + "/");
			if (!outputPath.exists())
				outputPath.mkdirs();

			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);
			// text area in background image
			document.setMargins(150, 35, 100, 55);

			ImageData mainBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getFrontPage());
			ImageData detailsPage = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getSecondFrontPage());
			ImageData defaultMenuBg = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
			ImageData tncPage = null;
			if (adminTemplate.getTemplateMaster().getCatBgPage() != null) {
				tncPage = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getCatBgPage());
			}

			ImageData tncPage1 = null;
			if (adminTemplate.getTemplateMaster().getExtraPage() != null) {
				tncPage1 = loadImageFromResource(
						environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getExtraPage());
			}

			ImageData lastBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getLastMainPage());

//			ImageData mainBgData = loadImageFromResource("/flipbook/pages/Bhandari_1.jpeg");
//			ImageData detailsPage = loadImageFromResource("/flipbook/pages/Bhandari_2.jpeg");
//			ImageData defaultMenuBg = loadImageFromResource("/flipbook/pages/Bhandari_3.jpeg");
//			ImageData lastBgData = loadImageFromResource("/flipbook/pages/Bhandari_4.jpeg");

			BackgroundEventHandler bgHandler = new BackgroundEventHandler();
			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(1, mainBgData);
			}
			bgHandler.setDefaultBackground(defaultMenuBg);
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			String billingName = "";
			String foodNotes = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
			}

			Paragraph invisibleContent = new Paragraph("\u00A0").setFont(catFont).setFontColor(contentColor)
					.setFontSize(1);
			document.add(invisibleContent);

			if (isCompanyDetail == 1) {
				bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, detailsPage);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				document.add(new Paragraph("\u00A0").setFontSize(1));
			}

			bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventDate = eventDto.getEventStartTimestamp() != null ? eventDto.getEventStartTimestamp() : "";
			String eventName = eventDto.getEventName() != null ? eventDto.getEventName() : "";
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;
			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			String Food = eventDto.getFoodType() != null ? eventDto.getFoodType() : "";
			String foodHeading = foodNotes.isEmpty() ? Food : Food + " (" + foodNotes + ")";
			String remark = eventDto.getRemark();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			String formattedHost = toTitleCase(safeText(hostName));
			String formattedEventName = toTitleCase(safeText(eventName));
			String quotedHost = "\"" + formattedHost + "\"";

			document.add(new Paragraph(quotedHost).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 24))
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setFixedLeading(24f).setMarginTop(30f).setMarginBottom(25f));

			document.add(new Paragraph(eDate).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			document.add(new Paragraph(safeText(eventDate)).setFont(catFontBold).setFontSize(21)
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));

			document.add(new Paragraph(eName).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			document.add(new Paragraph(safeText(formattedEventName)).setFont(catFontBold).setFontSize(21)
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			if (!safeText(venue).isEmpty()) {
				document.add(new Paragraph(venueLabel).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
						.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
						.setMarginTop(10f));

				document.add(new Paragraph(safeText(venue)).setFont(catFontBold).setFontSize(21)
						.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
						.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			}
			document.add(new Paragraph(fNote).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
					.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
					.setMarginTop(10f));

			document.add(new Paragraph(safeText(foodHeading)).setFont(catFontBold).setFontSize(21)
					.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
					.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			if (!safeText(theme).trim().isEmpty()) {
				document.add(new Paragraph(themeLabel).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
						.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
						.setMarginTop(10f));

				document.add(new Paragraph(safeText(theme)).setFont(catFontBold).setFontSize(21)
						.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
						.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			}
			if (!safeText(service).trim().isEmpty()) {
				document.add(new Paragraph(serviceLabel).setFont(sloganFont).setFontSize(17).setFontColor(headingColor)
						.simulateItalic().setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
						.setMarginTop(10f));

				document.add(new Paragraph(safeText(service)).setFont(catFontBold).setFontSize(21)
						.setFontColor(descriptionFontColor).simulateItalic().setTextAlignment(TextAlignment.LEFT)
						.setMargin(0).setMultipliedLeading(1).setMarginBottom(15f));
			}
			for (EventFunctionReportResponseDto eventFunctionDto : eventDto.getFunctions()) {
				bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				// Function name label
				document.add(new Paragraph(safeText(eventFunctionDto.getFunctionName())).setFont(catFontBold)
						.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
						.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1).setMarginTop(20f));

				String paxStr = eventFunctionDto.getPax() != null
						? forLabel + " " + eventFunctionDto.getPax() + " " + personLabel
						: "";
				String foodType = eventFunctionDto.getFoodType() != null ? " (" + eventFunctionDto.getFoodType() + ")"
						: "";
				document.add(new Paragraph(paxStr + foodType).setFont(catFontBold)
						.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
						.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1));

				if (req.getIsWithPrice() == 1) {
					String label;
					String value;

					if (eventFunctionDto.getIsPackage()) {
						label = pckPrice;
						value = eventFunctionDto.getPackagePrice().toString();
					} else {
						label = price;
						value = eventFunctionDto.getRate().toString();
					}

					document.add(new Paragraph(label + " " + value).setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
							.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1));
				}

				if (eventFunctionDto.getFunctionStartTimestamp() != null) {
					String[] tParts = eventFunctionDto.getFunctionStartTimestamp().split(" ");
					if (tParts.length >= 3) {
						String timeStr = tParts[0] + " " + tParts[1] + " " + tParts[2] + " " + l1;
						document.add(new Paragraph(timeStr).setFont(sloganFont)
								.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor).simulateItalic()
								.setTextAlignment(TextAlignment.LEFT).setMargin(0).setMultipliedLeading(1)
								.setMarginBottom(3f));
					}
				}

				List<MenuPreparationSelectedItemDetailsResponseDto> selectedItems = loadSelectedItemsForType11(
						eventFunctionDto.getFunctionId());

				Map<Long, MenuPreparationSelectedItemDetailsResponseDto> catMap = new java.util.LinkedHashMap<>();
				for (MenuPreparationSelectedItemDetailsResponseDto sel : selectedItems) {
					catMap.put(sel.getMenuCategoryId(), sel);
				}

				for (MenuReportResponseDto menu : eventFunctionDto.getMenuCategories()) {

					if (menu.getNameEnglish() != null && menu.getNameEnglish().trim().equalsIgnoreCase("staff food")) {
						continue;
					}

					String subCat = "";
					if (lang == 1) {
						subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
					} else if (lang == 2) {
						subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
					} else {
						subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
					}

					Div catBlock = new Div();
					catBlock.setMarginTop(10f);
					catBlock.setMarginBottom(5f);

					catBlock.add(new Paragraph(formatText(menu.getNameEnglish(), lang).toUpperCase()
							+ (showAddOnLabel == 1 && menu.getIsAddOnCat() ? " (Add On)" : "")).setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.LEFT).setUnderline().simulateItalic().setMarginTop(1f)
							.setMarginBottom(1f));

					if (subCat != null && !subCat.trim().isEmpty()) {
						catBlock.add(new Paragraph(formatText(subCat, lang).toUpperCase()).setFont(catFontBold)
								.setFontSize(getFontSize(catFontSize, 14)).setFontColor(headingColor)
								.setTextAlignment(TextAlignment.LEFT).setUnderline().simulateItalic().setMarginTop(1f)
								.setMarginBottom(1f));
					}

					if (isCategoryInstruction != null && isCategoryInstruction == 1 && menu.getMenuNotes() != null
							&& !menu.getMenuNotes().isEmpty()) {

						catBlock.add(new Paragraph(formatText(menu.getMenuNotes(), lang)).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 10)).setFontColor(descriptionFontColor)
								.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
								.setMarginTop(1f).setMarginBottom(1f));
					}

					if (isCategorySlogan != null && isCategorySlogan == 1 && menu.getSlogan() != null
							&& !menu.getSlogan().isEmpty()) {

						catBlock.add(new Paragraph(formatText(menu.getSlogan(), lang)).setFont(sloganFont)
								.setFontSize(getFontSize(sloganFontSize, 10)).setFontColor(descriptionFontColor)
								.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
								.setMarginTop(1f).setMarginBottom(1f));
					}

					if (isCategoryImage != null && isCategoryImage == 1 && menu.getImagePath() != null
							&& !menu.getImagePath().trim().isEmpty()) {

						try {
							System.out.println("image path : " + menu.getImagePath());
							Image img = new Image(ImageDataFactory.create(menu.getImagePath()));

							img.setAutoScale(true);
							img.setHorizontalAlignment(HorizontalAlignment.CENTER);

							catBlock.add(img);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					float categoryHeight = 140f;
					float firstItemHeight = 70f;

					if (!menu.getMenuItems().isEmpty()) {
						categoryHeight += firstItemHeight;
					}

					if (shouldMoveCategoryToNextPage(document, categoryHeight)) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					}

					document.add(catBlock);

					boolean firstItem = true;

					for (MenuItemForReportResponseDto item : menu.getMenuItems()) {

						String subItem = "";

						if (lang == 1) {
							subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
						} else if (lang == 2) {
							subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
						} else {
							subItem = item.getSubItem() != null ? item.getSubItem() : "";
						}

						Div itemBlock = new Div();

						boolean movedToNewPage = false;

						if (!firstItem) {

							float itemHeight = 70f;

							if (shouldMoveCategoryToNextPage(document, itemHeight)) {

								document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

								movedToNewPage = true;
							}
						}

						if (movedToNewPage) {
							itemBlock.setMarginTop(15f);
						}

						itemBlock.setMarginBottom(1f);

						itemBlock.add(new Paragraph(formatText(item.getNameEnglish(), lang).toUpperCase()
								+ (showAddOnLabel == 1 && item.getIsAddOnItem() ? " (Add On)" : ""))
								.setFont(catFontBold).setFontSize(getFontSize(itemFontSize, 12))
								.setFontColor(contentColor).setTextAlignment(TextAlignment.LEFT).simulateItalic()
								.setFixedLeading(17f).setMarginTop(1f).setMarginBottom(1f));

						if (subItem != null && !subItem.isEmpty()) {

							itemBlock.add(new Paragraph(formatText(subItem, lang).toUpperCase()).setFont(catFontBold)
									.setFontSize(getFontSize(itemFontSize, 12)).setFontColor(contentColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setFixedLeading(17f)
									.setMarginTop(1f).setMarginBottom(1f));
						}

						if (isItemSlogan != null && isItemSlogan == 1 && item.getSlogan() != null
								&& !item.getSlogan().isEmpty()) {

							itemBlock.add(new Paragraph(formatText(item.getSlogan(), lang)).setFont(sloganFont)
									.setFontSize(getFontSize(sloganFontSize, 10)).setFontColor(descriptionFontColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
									.setMarginTop(1f).setMarginBottom(1f));
						}

						if (isItemInstruction != null && isItemInstruction == 1 && item.getItemNotes() != null
								&& !item.getItemNotes().isEmpty()) {

							itemBlock.add(new Paragraph(formatText(item.getItemNotes(), lang)).setFont(catFont)
									.setFontSize(getFontSize(itemFontSize, 10)).setFontColor(descriptionFontColor)
									.setTextAlignment(TextAlignment.LEFT).simulateItalic().setMultipliedLeading(1.2f)
									.setMarginTop(1f).setMarginBottom(1f));
						}

						document.add(itemBlock);

						firstItem = false;
					}
				}

			}

			if (req.getIsTermsCond() == 1) {
				Optional<EventTermsAndConditionEntity> terms = eventTermsAndConditionRepository
						.findByEventIdAndNameEnglishAndIsDeleteFalse(eventId, "Menu Report");

				if (terms != null && terms.isPresent()) {
					EventTermsAndConditionEntity eventTermsEntity = terms.get();
					List<EventTermsAndConditionFeaturesEntity> features = new ArrayList<>();
					features = eventTermsAndConditionFeaturesRepository
							.findByEventTermsConditionIdAndIsDeleteFalse(eventTermsEntity.getId());

					if (features != null && !features.isEmpty()) {

						Paragraph termsTitle = new Paragraph("Special Instruction").setFont(catFontBold).setFontSize(16)
								.setTextAlignment(TextAlignment.CENTER).setMarginBottom(15f).setMarginTop(15f)
								.setFontColor(headingColor);

						document.add(termsTitle);

						ISplitCharacters breakAll = new ISplitCharacters() {
							@Override
							public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
								return true; // break anywhere
							}
						};

						int index = 1;
						for (EventTermsAndConditionFeaturesEntity feature : features) {
							String desc;

							if (lang == 1) {
								desc = feature.getDescriptionHindi();
							} else if (lang == 2) {
								desc = feature.getDescriptionGujarati();
							} else {
								desc = feature.getDescription();
							}

							Paragraph term = new Paragraph(index + ". " + desc).setFont(itemFont).setFontSize(12)
									.setWidth(UnitValue.createPercentValue(95f))
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(5f)
									.setSplitCharacters((text, glyphPos) -> true).setFontColor(contentColor);

							term.setSplitCharacters(breakAll);

							document.add(term);
							index++;
						}
					}
				}
			}

			if (req.getIsAddDecoration() == 1) {
//				List<DecoreReportResponseDto> decoreReportResponseDtos = eventDto.getFunctions().stream()
//						.flatMap(fn -> fn.getDecoreCategories().stream()).collect(Collectors.toList());

				List<DecoreReportResponseDto> decoreReportResponseDtos = eventDto.getFunctions().stream().flatMap(
						fn -> Optional.ofNullable(fn.getDecoreCategories()).orElse(Collections.emptyList()).stream())
						.collect(Collectors.toList());

				if (decoreReportResponseDtos != null) {
//					if (isAddMenu == 0) {
					bgHandler.setDefaultBackground(defaultMenuBg);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//					}
					document.add(new Paragraph("Setup Details:").setFont(catFont).setFontSize(22f).simulateBold()
							.setUnderline());

					for (DecoreReportResponseDto decore : decoreReportResponseDtos) {
						Div decoreContent = new Div();
						decoreContent.setKeepTogether(false);
						String text = "";
						for (DecoreItemReportResponseDto item : decore.getDecoreItems()) {
							List<DecoreMainCategoryItemImagesMasterEntity> imagesMasterEntities = decoreMainCategoryItemImagesMasterRepository
									.findAllByDecoreItem_IdAndIsDeleteFalse(item.getId());
							Integer itemQty = item.getItemQty();
							Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;
							BigDecimal itemPrice = item.getPrice();

							String subItem = "";
							if (lang == 1) {
								subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
							} else if (lang == 2) {
								subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
							} else {
								subItem = item.getSubItem() != null ? item.getSubItem() : "";
							}

							BigDecimal price1 = itemPrice != null ? itemPrice : BigDecimal.ZERO;
							int qty = itemQty != null ? itemQty : 0;
							BigDecimal totalAmount = price1.multiply(BigDecimal.valueOf(qty));

							String postLabel = "";
							if (qty > 1) {
								postLabel = " /- + Tax Each";
							} else {
								postLabel = " /- + Tax";
							}
							String text1 = "\u2022 " + formatText(item.getNameEnglish(), lang)
									+ (subItem.trim().length() != 0 ? " " + subItem : "")
									+ (totalAmount.compareTo(BigDecimal.ZERO) != 0
											? " @RS." + totalAmount.intValue() + postLabel
											: "");

							decoreContent.add(
									new Paragraph(text1).setFont(itemFont).setFontSize(getFontSize(itemFontSize, 19))
											.setFixedLeading(20f).setTextAlignment(TextAlignment.LEFT)
											.setFontColor(contentColor).setMarginLeft(20f).setMarginTop(15f));

							// Item Instructions
							if (isItemInstruction != null && isItemInstruction == 1) {
								if (item.getDecoreItemNotes() != null && !item.getDecoreItemNotes().isEmpty()) {
									text = formatText(item.getDecoreItemNotes(), lang);
									decoreContent.add(new Paragraph(text).setFont(itemFont)
											.setFontSize(getFontSize(itemFontSize, 19)).setMultipliedLeading(1.2f)
											.setFontColor(descriptionFontColor).setMarginLeft(0f).setMarginTop(0)
											.setTextAlignment(TextAlignment.LEFT).setMarginBottom(0).setPadding(0f)
											.setPaddingLeft(130f));
								}
							}

							if (imagesMasterEntities != null && !imagesMasterEntities.isEmpty()) {
								try {
									Image img = new Image(ImageDataFactory
											.create(environment.getProperty("app.image.url") + decore.getImagePath()));
//									Image img = new Image(loadImageFromResource("/flipbook/pages/logo.png"));

									img.setAutoScale(false);
									img.scaleToFit(300, 200);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);
									img.setMarginTop(5);
									img.setMarginBottom(5);

									decoreContent.add(img);
								} catch (Exception e) {
									// Skip if image loading fails
								}
							}

							for (int i = 0; i < itemSpace; i++) {
								decoreContent.add(new Paragraph("\n"));
							}
						}

						document.add(decoreContent);
					}
				}
			}

			// ════════════════════════════════════════════════════════════════════
			// EXTRA CHARGES PAGE (optional)
			// ════════════════════════════════════════════════════════════════════
			int lastPageNum = pdfDocument.getNumberOfPages() + 1;

			if (req.getIsExtraCharges() != null && req.getIsExtraCharges() == 1) {
				ExtraChargesResponseDto extraCharges = extraChargesService.getExtraCharges(eventId, eventFunctionId,
						userId);

				if (extraCharges != null && extraCharges.getHeadings() != null
						&& !extraCharges.getHeadings().isEmpty()) {

					bgHandler.setPageBackground(pdfDocument.getNumberOfPages() + 1, defaultMenuBg);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					document.add(new Paragraph("-: EXTRA CHARGE :-").setFont(catFontBold)
							.setFontSize(getFontSize(catFontSize, 22)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.CENTER).setUnderline().setMarginBottom(15f));

					for (ExtraChargesResponseDto.ExtraChargesHeadingResponseDto heading : extraCharges.getHeadings()) {

						Paragraph headingTitle = new Paragraph(heading.getHeadingName().toUpperCase())
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16))
								.setFontColor(headingColor).setUnderline().setTextAlignment(TextAlignment.LEFT)
								.setMarginTop(12f).setMarginBottom(6f);
						document.add(headingTitle);

						// ── Table: DATE | TIME | QTY/PERSON | RATE | TOTAL ──────────
						float[] colWidths = { 15f, 17f, 15f, 13f, 12f, 12f, 16f };
						Table chargeTable = new Table(UnitValue.createPercentArray(colWidths));
						chargeTable.setWidth(UnitValue.createPercentValue(100));
						chargeTable.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 1f));

						// ── Header Row ────────────────────────────────────────────────
						String[] colHeaders = { "DATE", "START TIME", "END TIME", "SESSION", "QTY", "RATE", "TOTAL" };
						for (String h : colHeaders) {
							chargeTable.addHeaderCell(new Cell()
									.add(new Paragraph(h).setFont(catFontBold).setFontSize(getFontSize(catFontSize, 12))
											.setFontColor(headingColor).setUnderline())
									.setTextAlignment(TextAlignment.CENTER)
									.setBorder(new com.itextpdf.layout.borders.SolidBorder(headingColor, 0.8f))
									.setPaddingTop(5f).setPaddingBottom(5f));
						}

						// ── Data Rows ─────────────────────────────────────────────────
						if (heading.getRows() != null && !heading.getRows().isEmpty()) {
							for (ExtraChargesResponseDto.ExtraChargesRowResponseDto row : heading.getRows()) {

								chargeTable.addCell(dataCell(
										row.getChargeDate() != null ? row.getChargeDate().format(dateFormatter) : "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(row.getChargeStartTime() != null
										? row.getChargeStartTime().format(timeFormatter)
										: "", catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getChargeEndTime() != null ? row.getChargeEndTime().format(timeFormatter)
												: "",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(
										dataCell(row.getSession() != null ? String.valueOf(row.getSession()) : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable.addCell(dataCell(
										row.getPersonItem() != null ? String.valueOf(row.getPersonItem()) : "0",
										catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getRate() != null ? row.getRate().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));

								chargeTable
										.addCell(dataCell(row.getTotal() != null ? row.getTotal().toPlainString() : "0",
												catFont, contentColor, catFontSize, headingColor));
							}
						} else {
							// Empty rows for visual layout (like the sample image)
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 7; j++) {
									chargeTable.addCell(dataCell("", catFont, contentColor, catFontSize, headingColor));
								}
							}
						}
						document.add(chargeTable);

						document.add(new Paragraph("Total: "
								+ (heading.getHeadingTotal() != null ? heading.getHeadingTotal().toPlainString() : "0"))
								.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 13))
								.setFontColor(headingColor).setTextAlignment(TextAlignment.RIGHT).setMarginTop(4f));
					}

					document.add(new Paragraph("Grand Total: "
							+ (extraCharges.getGrandTotal() != null ? extraCharges.getGrandTotal().toPlainString()
									: "0"))
							.setFont(catFontBold).setFontSize(getFontSize(catFontSize, 16)).setFontColor(headingColor)
							.setTextAlignment(TextAlignment.RIGHT).setMarginTop(10f).setUnderline());

					lastPageNum = pdfDocument.getNumberOfPages() + 1;
				}
			}

			if (req.getIsNotes() == 1 && tncPage != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			if (req.getIsNotes() == 1 && tncPage1 != null) {
				bgHandler.setPageBackground(lastPageNum, tncPage1);

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				invisibleContent = new Paragraph("\u00A0");
				invisibleContent.setFontSize(1);

				document.add(invisibleContent);

				lastPageNum = pdfDocument.getNumberOfPages() + 1;
			}

			// ════════════════════════════════════════════════════════════════════
			// LAST PAGE – Terms & Conditions (static)
			// ════════════════════════════════════════════════════════════════════
			if (req.getShowLastPage() == 1) {
				bgHandler.setPageBackground(lastPageNum, lastBgData);
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				document.add(new Paragraph("\u00A0").setFontSize(1));
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateExclusiveReportType23(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO req) {
		try {
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

			String hostLabel = "", phone = "", eDate = "", eName = "", venueLabel = "", fNote = "", personLabel = "",
					eTime = "", pckPrice = "", price = "", remarks = "", billingNameLabel = "", serviceLabel = "",
					permissableLabel = "", notPermissableLabel = "", themeLabel = "";

			int x = 185;
			int initVal = 715;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
				catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");
				System.out.println("Hindi font loaded successfully");
				hostLabel = "माननीय आयोजक";
				phone = "मोबाइल";
				eDate = "दिनांक";
				eName = "कार्यक्रम का प्रकार";
				venueLabel = "कार्यक्रम स्थल";
				personLabel = "व्यक्ति";
				eTime = "समय";
				fNote = "भोजन व्यवस्था";
				pckPrice = "पैकेज मूल्य";
				price = "मूल्य";
				remarks = "रिमार्क्स";
				billingNameLabel = "बिलिंग नाम";
				serviceLabel = "सर्विस";
				themeLabel = "थीम";
				permissableLabel = "अनुमत्य सामग्री";
				notPermissableLabel = "अस्वीकृत सामग्री";
				initVal = 692;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");

				String language = userBasicDetailsMasterRepository.getLangByUserId(userId);

				if (language.equalsIgnoreCase("Tamil")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTamil-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTamil-Bold.ttf");

					hostLabel = "மாண்புமிகு ஏற்பாட்டாளர்";
					phone = "மொபைல்";
					eDate = "தேதி";
					eName = "நிகழ்ச்சி";
					venueLabel = "நிகழ்ச்சி இடம்";
					personLabel = "நபர்";
					eTime = "நேரம்";
					fNote = "உணவு ஏற்பாடு";
					pckPrice = "பேக்கேஜ் விலை";
					price = "விலை";
					remarks = "ரிமார்க்ஸ்";
					billingNameLabel = "பில்லிங் பெயர்";
					serviceLabel = "சர்வீஸ்";
					themeLabel = "தீம்";
					permissableLabel = "அனுமதிக்கப்பட்ட பொருட்கள்";
					notPermissableLabel = "அனுமதிக்கப்படாத பொருட்கள்";
				} else if (language.equalsIgnoreCase("Telugu")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansTelugu-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansTelugu-Bold.ttf");

					hostLabel = "గౌరవనీయ నిర్వాహకుడు";
					phone = "మొబైల్";
					eDate = "తేదీ";
					eName = "కార్యక్రమం";
					venueLabel = "కార్యక్రమ స్థలం";
					personLabel = "వ్యక్తి";
					eTime = "సమయం";
					fNote = "భోజన ఏర్పాటు";
					pckPrice = "ప్యాకేజ్ ధర";
					price = "ధర";
					remarks = "రిమార్క్స్";
					billingNameLabel = "బిల్లింగ్ పేరు";
					serviceLabel = "సర్వీస్";
					themeLabel = "థీమ్";
					permissableLabel = "అనుమతించదగిన వస్తువులు";
					notPermissableLabel = "అనుమతించని వస్తువులు";
				} else if (language.equalsIgnoreCase("Malayalam")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansMalayalam-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansMalayalam-Bold.ttf");

					// Malayalam
					hostLabel = "മാന്യ സംഘാടകൻ";
					phone = "മൊബൈൽ";
					eDate = "തീയതി";
					eName = "പരിപാടി";
					venueLabel = "പരിപാടി സ്ഥലം";
					personLabel = "വ്യക്തി";
					eTime = "സമയം";
					fNote = "ഭക്ഷണ സംവിധാനം";
					pckPrice = "പാക്കേജ് വില";
					price = "വില";
					remarks = "റിമാർക്സ്";
					billingNameLabel = "ബില്ലിംഗ് പേര്";
					serviceLabel = "സർവീസ്";
					themeLabel = "തീം";
					permissableLabel = "അനുവദനീയമായ ഇനങ്ങൾ";
					notPermissableLabel = "അനുവദനീയമല്ലാത്ത ഇനങ്ങൾ";
				} else if (language.equalsIgnoreCase("Marathi")) {
					catFont = itemFont = sloganFont = loadFont("/fonts/Nirmala.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/Nirmala-Bold.ttf");

					// Marathi
					hostLabel = "माननीय आयोजक";
					phone = "मोबाईल";
					eDate = "दिनांक";
					eName = "कार्यक्रम";
					venueLabel = "कार्यक्रम स्थळ";
					personLabel = "व्यक्ती";
					eTime = "वेळ";
					fNote = "भोजन व्यवस्था";
					pckPrice = "पॅकेज रेट";
					price = "रेट";
					remarks = "रिमार्क्स";
					billingNameLabel = "बिलिंग नाव";
					serviceLabel = "सर्व्हिस";
					themeLabel = "थीम";
					permissableLabel = "अनुमत्य वस्तू";
					notPermissableLabel = "अस्वीकृत वस्तू";
				} else {
					catFont = itemFont = sloganFont = loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					catFontBold = itemFontBold = sloganFontBold = loadFont("/fonts/NotoSansGujarati-Bold.ttf");

					hostLabel = "માનનીય યજમાન";
					phone = "મોબાઇલ";
					eDate = "કાર્યક્રમની તારીખ";
					eName = "કાર્યક્રમનો પ્રકાર";
					venueLabel = "કાર્યક્રમ સ્થળ";
					personLabel = "વ્યક્તિ";
					eTime = "સમય";
					fNote = "ભોજન વ્યવસ્થા";
					pckPrice = "પેકેજ રેટ";
					price = "રેટ";
					remarks = "રિમાર્ક્સ";
					billingNameLabel = "બિલિંગ નામ";
					serviceLabel = "સર્વિસ";
					themeLabel = "થીમ";
					permissableLabel = "માન્ય સામગ્રી";
					notPermissableLabel = "અમાન્ય સામગ્રી";
				}

				System.out.println("Gujarati font loaded successfully");
				initVal = 690;
			} else {
				// English
				System.out.println("Loading English font...");

				hostLabel = "Guest Name";
				phone = "Mobile";
				eDate = "Event Date";
				eName = "Type of Event";
				venueLabel = "Venue";
				personLabel = "Persons";
				eTime = "Start Time";
				fNote = "Food Preference";
				pckPrice = "Package Price";
				price = "price";
				remarks = "Remarks";
				billingNameLabel = "Billing Name";
				serviceLabel = "Service";
				themeLabel = "Theme";
				permissableLabel = "Permissable Items";
				notPermissableLabel = "Not Permissable Items";

				catFont = getFont(req.getCatFontId(), false, "segoe_print");
				catFontBold = getFont(req.getCatFontId(), true, "segoe_print");

				itemFont = getFont(req.getItemFontId(), false, "segoe_print");
				itemFontBold = getFont(req.getItemFontId(), true, "segoe_print");

				sloganFont = getFont(req.getSloganFontId(), false, "segoe_print");
				sloganFontBold = getFont(req.getSloganFontId(), true, "segoe_print");

				System.out.println("English font loaded successfully");
			}
			int y = initVal;

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

			// Define colors
//			Color creamGold = new DeviceRgb(r2, g2, b2);
//			Color softGold = new DeviceRgb(r1, g1, b1);
//			Color descriptionColor = new DeviceRgb(r3, g3, b3);

			Color creamGold = new DeviceRgb(0, 0, 0);
			Color softGold = new DeviceRgb(0, 0, 0);
			Color descriptionColor = new DeviceRgb(0, 0, 0);

			EventFlowReportResponseDto eventDto = fetchAndPrepareEventFlowReportData(eventId, eventFunctionId, lang,
					userId);

			if (eventDto == null) {
				return "";
			}

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
			File pdfFile = new File(outputPath + "/" + getReportName(getPartyNameByEventId(eventId),
					formatDate(eventDto.getEventStartTimestamp()), "excusive report") + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4, false);
			document.setMargins(60, 35, 60, 40);

			String hostName = eventDto.getPartyName() != null ? eventDto.getPartyName() : "Not Specified";
			String mobileNo = eventDto.getMobileNo() != null ? eventDto.getMobileNo() : "Not Specified";
			String eventStartDate = eventDto.getEventStartTimestamp() == "" ? "" : eventDto.getEventStartTimestamp();
			String eventEndDate = eventDto.getEventEndTimestamp() == "" ? "" : eventDto.getEventEndTimestamp();
			String eventName = eventDto.getEventName() == null ? "" : eventDto.getEventName();
			String pax = eventDto.getPax();
			Long banquetHallId = eventDto.getBanquetHallId() != null ? eventDto.getBanquetHallId() : 0;

			String venue = "";

			if (banquetHallId != 0) {
				venue = eventDto.getBanquetHallName() != null ? eventDto.getBanquetHallName() : "";
			} else {
				venue = eventDto.getVenue() == null ? "" : eventDto.getVenue();
			}

			StringBuilder foodNotesName = new StringBuilder(eventDto.getFoodType());
			String foodNotes = "";
			String cmpName = eventDto.getCmpName();
			String cmpPhone = eventDto.getCmpPhone();
			String cmpAddress = eventDto.getCmpAddress();
			String userFirstName = eventDto.getUserFirstName();
			String userLastName = eventDto.getUserLastName();
			String countryCode = eventDto.getCountrycode();
			String email = eventDto.getEmail();
			String remark = eventDto.getRemark();
			String permissable_item = "";
			String not_permissable_item = "";
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			String billingName = "";
			String service = "";
			String theme = "";
			if (lang == 1) {
				billingName = eventDto.getBillingNameHindi() != null ? eventDto.getBillingNameHindi() : "";
				foodNotes = eventDto.getFoodNotesHindi() != null ? eventDto.getFoodNotesHindi() : "";
				service = eventDto.getServiceHindi() != null ? eventDto.getServiceHindi() : "";
				theme = eventDto.getThemeHindi() != null ? eventDto.getThemeHindi() : "";
			} else if (lang == 2) {
				billingName = eventDto.getBillingNameGujarati() != null ? eventDto.getBillingNameGujarati() : "";
				foodNotes = eventDto.getFoodNotesGujarati() != null ? eventDto.getFoodNotesGujarati() : "";
				service = eventDto.getServiceGujarati() != null ? eventDto.getServiceGujarati() : "";
				theme = eventDto.getThemeGujarati() != null ? eventDto.getThemeGujarati() : "";
			} else {
				billingName = eventDto.getBillingNameEnglish() != null ? eventDto.getBillingNameEnglish() : "";
				foodNotes = eventDto.getFoodNotes() != null ? eventDto.getFoodNotes() : "";
				service = eventDto.getService() != null ? eventDto.getService() : "";
				theme = eventDto.getTheme() != null ? eventDto.getTheme() : "";
				permissable_item = eventDto.getPermissable_item() != null ? eventDto.getPermissable_item() : "";
				not_permissable_item = eventDto.getNot_permissable_item() != null ? eventDto.getNot_permissable_item()
						: "";
			}

			ImageData whiteBgData = loadImageFromResource("/flipbook/pages/Blankpage.png");

			// Load all background images upfront
			ImageData watermarkBgData = loadImageFromResource(
					environment.getProperty("app.image.url") + adminTemplate.getTemplateMaster().getWatermark());
//			ImageData watermarkBgData = loadImageFromResource("/flipbook/pages/lakhani_bg_2.png");
			BackgroundEventHandler bgHandler = new BackgroundEventHandler();

			bgHandler.setDefaultBackground(watermarkBgData); // Default: Watermark for content pages
			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, bgHandler);

			float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();

			Table detailTable = new Table(UnitValue.createPercentArray(new float[] { 1f })).useAllAvailableWidth()
					.setMarginTop(10f);

			ImageData logoData = loadImageFromResource(environment.getProperty("app.image.url") + eventDto.getLogo());

//			ImageData logoData = loadImageFromResource("/flipbook/pages/logo.png");
			Image logo = new Image(logoData);
			logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
			logo.scaleAbsolute(200f, 200f);

			Cell cell = new Cell().add(logo).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
			detailTable.addCell(cell);

			cell = new Cell().add(new Paragraph(hostName)).setBorder(Border.NO_BORDER).setPaddingBottom(5f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
					.setFontSize(getFontSize(catFontSize, 24)).setTextAlignment(TextAlignment.CENTER);
			detailTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text("Date : "))
							.add(new Text(eventStartDate).simulateBold().setUnderline()))
					.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFont(catFont).setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);
			detailTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph().add(new Text("Venue : ")).add(new Text(venue).simulateBold().setUnderline()))
					.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFont(catFont).setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER);
			detailTable.addCell(cell);

			cell = new Cell().add(new Paragraph("NOTE:").simulateBold().setUnderline()).setBorder(Border.NO_BORDER)
					.setPaddingBottom(5f).setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
					.setFontSize(getFontSize(catFontSize, 24)).setTextAlignment(TextAlignment.CENTER);
			detailTable.addCell(cell);

			cell = new Cell().add(new Paragraph(remark)).setBorder(Border.NO_BORDER).setPaddingBottom(5f)
					.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
					.setFontSize(getFontSize(catFontSize, 24)).setTextAlignment(TextAlignment.CENTER);
			detailTable.addCell(cell);

			cell = new Cell().add(new Paragraph("MENU FOR SELECTION").simulateBold().setUnderline())
					.setBorder(Border.NO_BORDER).setPaddingBottom(5f).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFont(catFont).setFontSize(getFontSize(catFontSize, 24)).setTextAlignment(TextAlignment.CENTER);
			detailTable.addCell(cell);

			document.add(detailTable);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			Table eventFlowTable = new Table(UnitValue.createPercentArray(new float[] { 10f, 22f, 35f, 27f }));
			eventFlowTable.setWidth(UnitValue.createPercentValue(100f));
			eventFlowTable.setFixedLayout();

			for (EventFunctionFlowResponseDto functionFlow : eventDto.getFunctionFlow()) {
				cell = new Cell(1, 4).add(new Paragraph()
						.add(new Text(functionFlow.getDate() + " Day " + functionFlow.getDayCount()).simulateBold())
						.setPadding(2f).setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
						.setFontSize(getFontSize(catFontSize, 16)).setTextAlignment(TextAlignment.CENTER));
				eventFlowTable.addCell(cell);

				cell = new Cell().add(new Paragraph("Sr. No.").simulateBold()).setPadding(2f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
						.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.LEFT);
				eventFlowTable.addCell(cell);

				cell = new Cell().add(new Paragraph("Time").simulateBold()).setPadding(2f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
						.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.LEFT);
				eventFlowTable.addCell(cell);

				cell = new Cell().add(new Paragraph("Particular").simulateBold()).setPadding(2f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
						.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.LEFT);
				eventFlowTable.addCell(cell);

				cell = new Cell().add(new Paragraph("Venue").simulateBold()).setPadding(2f)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
						.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.CENTER);
				eventFlowTable.addCell(cell);

				Integer idx = 0;
				for (EventFunctionReportResponseDto function : functionFlow.getFunctions()) {
					String fnStartTimeStamp = function.getFunctionStartTimestamp().split(" ")[1] + " "
							+ function.getFunctionStartTimestamp().split(" ")[2];
					String fnName = function.getFunctionName();
					String fnVenue = function.getFunctionVenue();

					cell = new Cell().add(new Paragraph((++idx).toString()).simulateBold()).setPadding(2f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
							.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.CENTER);
					eventFlowTable.addCell(cell);

					cell = new Cell().add(new Paragraph(fnStartTimeStamp).simulateBold()).setPadding(2f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
							.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.CENTER);
					eventFlowTable.addCell(cell);

					cell = new Cell().add(new Paragraph(fnName).simulateBold()).setPadding(2f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
							.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.LEFT);
					eventFlowTable.addCell(cell);

					cell = new Cell().add(new Paragraph(fnVenue).simulateBold()).setPadding(2f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
							.setFontSize(getFontSize(itemFontSize, 14)).setTextAlignment(TextAlignment.CENTER);
					eventFlowTable.addCell(cell);
				}
			}
			document.add(eventFlowTable);

			for (EventFunctionFlowResponseDto functionFlow : eventDto.getFunctionFlow()) {

				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

				Table functionFlowTable = new Table(UnitValue.createPercentArray(new float[] { 1 }));

				functionFlowTable.setWidth(UnitValue.createPercentValue(100f));
				functionFlowTable.setFixedLayout();

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(functionFlow.getDate() + " Day " + functionFlow.getDayCount())
										.simulateBold().setUnderline())
								.setPadding(2f).setMultipliedLeading(1.1f)
								.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 24)).setTextAlignment(TextAlignment.CENTER))
						.setBorder(Border.NO_BORDER);

				functionFlowTable.addCell(cell);

				cell = new Cell().add(new Paragraph().add(new Text("FLOW OF EVENT").simulateBold().setUnderline())
						.setPadding(2f).setMultipliedLeading(1.1f).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setFont(catFont).setFontSize(getFontSize(catFontSize, 24))
						.setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER);

				functionFlowTable.addCell(cell);

				for (EventFunctionReportResponseDto function : functionFlow.getFunctions()) {

					String fnStartTimeStamp = "";

					if (function.getFunctionStartTimestamp() != null
							&& function.getFunctionStartTimestamp().trim().length() > 0) {

						String[] timeParts = function.getFunctionStartTimestamp().split(" ");

						if (timeParts.length >= 3) {
							fnStartTimeStamp = timeParts[1] + " " + timeParts[2];
						} else {
							fnStartTimeStamp = function.getFunctionStartTimestamp();
						}
					}

					String fnName = function.getFunctionName();

					String functionListText = fnStartTimeStamp;

					cell = new Cell();

					Paragraph functionPara = new Paragraph().setMargin(0).setPadding(0).setFont(catFont)
							.setFontSize(getFontSize(catFontSize, 22)).setFixedLeading(getFontSize(catFontSize, 22))
							.setTextAlignment(TextAlignment.CENTER);

					functionPara.add(new Paragraph(functionListText).setMarginBottom(10f));

					functionPara.add(new Text("\n"));

					functionPara.add(new Text(fnName));

					cell.add(functionPara).setPadding(0).setPaddingBottom(28f)
							.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);

					functionFlowTable.addCell(cell);
				}

				document.add(functionFlowTable);

				for (EventFunctionReportResponseDto function : functionFlow.getFunctions()) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

					String fnStartTimeStamp = "";

					if (function.getFunctionStartTimestamp() != null
							&& function.getFunctionStartTimestamp().trim().length() > 0) {

						String[] timeParts = function.getFunctionStartTimestamp().split(" ");

						if (timeParts.length >= 3) {
							fnStartTimeStamp = timeParts[1] + " " + timeParts[2];
						} else {
							fnStartTimeStamp = function.getFunctionStartTimestamp();
						}
					}

					String fnName = function.getFunctionName();
					String fnVenue = function.getFunctionVenue();

					Table fnDetailTable = new Table(UnitValue.createPercentArray(new float[] { 1 }));

					fnDetailTable.setWidth(UnitValue.createPercentValue(100f));
					fnDetailTable.setFixedLayout();

					String functionHeader = fnStartTimeStamp;

					if (fnVenue != null && fnVenue.trim().length() > 0) {
						functionHeader += " AT " + fnVenue;
					}

					cell = new Cell()
							.add(new Paragraph().add(new Text(functionHeader).simulateBold().setUnderline())
									.setPadding(2f).setFixedLeading(getFontSize(catFontSize, 24))
									.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 20)).setTextAlignment(TextAlignment.CENTER))
							.setBorder(Border.NO_BORDER);

					fnDetailTable.addCell(cell);

					for (MenuReportResponseDto menu : function.getMenuCategories()) {

						String subCat = "";
						String catHeading = "";

						int catSpace = menu.getCatItemSpace() != null ? menu.getCatItemSpace() : 0;

						if (lang == 1) {
							subCat = menu.getSubCatHindi() != null ? menu.getSubCatHindi() : "";
							catHeading = menu.getCatHeadingHindi() != null ? menu.getCatHeadingHindi() : "";
						} else if (lang == 2) {
							subCat = menu.getSubCatGujarati() != null ? menu.getSubCatGujarati() : "";
							catHeading = menu.getCatHeadingHindi() != null ? menu.getCatHeadingHindi() : "";
						} else {
							catHeading = menu.getCatHeadingEnglish() != null ? menu.getCatHeadingEnglish() : "";
							subCat = menu.getSubCat() != null ? menu.getSubCat() : "";
						}

						Div menuContent = new Div();

						if (req.getIsAllItemTogether() == 1) {
							menuContent.setKeepTogether(true);
						}

						Paragraph p = new Paragraph();

						if (req.getIsAllItemTogether() == 1) {
							p.setKeepTogether(true);
						}

						for (int i = 0; i < catSpace; i++) {
							p.add("\n");
						}

						if (catHeading != null && catHeading.trim().length() != 0) {
							p.add(new Paragraph(formatText(catHeading, lang)).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 24))
									.setFixedLeading(getFontSize(catFontSize, 24))
									.setTextAlignment(TextAlignment.CENTER).simulateBold().setUnderline()
									.setFontColor(softGold).setMarginLeft(0).setMarginTop(15f).setPaddingLeft(0)
									.setPaddingRight(0));
						}

						p.add("\n" + formatText(menu.getNameEnglish(), lang)).setFont(catFont)
								.setFontSize(getFontSize(catFontSize, 22)).setFixedLeading(getFontSize(catFontSize, 22))
								.setFontColor(softGold).setTextAlignment(TextAlignment.CENTER).simulateBold()
								.setUnderline().setPaddingLeft(0).setPaddingRight(0);

						Table table = new Table(1);

						table.setBorder(Border.NO_BORDER);
						table.setWidth(UnitValue.createPercentValue(100));

						table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

						table.setMarginBottom(5f);

						menuContent.add(table).setMarginTop(20f);

						String text = "";

						if (subCat.trim().length() != 0) {
							text = formatText(subCat, lang);

							menuContent.add(new Paragraph(text).setFont(catFont)
									.setFontSize(getFontSize(catFontSize, 16)).setMultipliedLeading(1.2f)
									.setFontColor(descriptionColor).setMarginBottom(0)
									.setTextAlignment(TextAlignment.CENTER).setMarginTop(0).setMarginLeft(0));
						}

						if (isCategoryInstruction != null && isCategoryInstruction == 1) {
							if (menu.getMenuNotes() != null && !menu.getMenuNotes().isEmpty()) {
								text = formatText(menu.getMenuNotes(), lang);

								menuContent.add(
										new Paragraph(text).setFont(catFont).setFontSize(getFontSize(catFontSize, 16))
												.setMultipliedLeading(1.2f).setFontColor(descriptionColor)
												.setMarginBottom(0).setTextAlignment(TextAlignment.CENTER)
												.setMarginTop(0).simulateItalic().setMarginLeft(0));
							}
						}

						if (isCategorySlogan != null && isCategorySlogan == 1) {
							if (menu.getSlogan() != null && !menu.getSlogan().isEmpty()) {
								text = " \"" + formatText(menu.getSlogan(), lang) + "\"";

								menuContent.add(new Paragraph(text).setFont(sloganFont)
										.setFontSize(getFontSize(sloganFontSize, 16)).setMultipliedLeading(1.2f)
										.setTextAlignment(TextAlignment.CENTER).setMarginTop(0)
										.setFontColor(descriptionColor).setMarginLeft(0).simulateItalic());
							}
						}

						if (isCategoryImage != null && isCategoryImage == 1) {
							if (menu.getImagePath() != null && !menu.getImagePath().trim().isEmpty()) {
								try {

									Image img = new Image(ImageDataFactory.create(menu.getImagePath()));
									img.scaleAbsolute(120f, 120f);
									img.setHorizontalAlignment(HorizontalAlignment.CENTER);

									img.setMarginTop(5);
									img.setMarginBottom(5);

									menuContent.add(img);

								} catch (Exception e) {

									// Skip if image loading fails
								}
							}
						}

						menuContent.add(new Paragraph(" ").setFontSize(5f).setMarginTop(0).setMarginBottom(0));

						for (MenuItemForReportResponseDto item : menu.getMenuItems()) {
							String subItem = "";

							Integer itemSpace = item.getItemSpace() != null ? item.getItemSpace() : 0;

							if (lang == 1) {
								subItem = item.getSubItemHindi() != null ? item.getSubItemHindi() : "";
							} else if (lang == 2) {
								subItem = item.getSubItemGujarati() != null ? item.getSubItemGujarati() : "";
							} else {
								subItem = item.getSubItem() != null ? item.getSubItem() : "";
							}

							for (int i = 0; i < itemSpace; i++) {
								menuContent.add(new Paragraph("\n"));
							}

							if (item.getItemHeading() != null && item.getItemHeading().trim().length() != 0) {
								menuContent.add(new Paragraph(formatText(item.getItemHeading(), lang)).setFont(catFont)
										.setFontSize(getFontSize(catFontSize, 22))
										.setFixedLeading(getFontSize(catFontSize, 22))
										.setTextAlignment(TextAlignment.CENTER).simulateBold().setUnderline()
										.setFontColor(softGold).setMarginLeft(0).setMarginTop(15f).setPaddingLeft(0)
										.setPaddingRight(0));
							}

							menuContent.add(new Paragraph(formatText(item.getNameEnglish(), lang)).setFont(itemFont)
									.setFontSize(getFontSize(itemFontSize, 20))
									.setFixedLeading(getFontSize(itemFontSize, 20))
									.setTextAlignment(TextAlignment.CENTER).setFontColor(creamGold).setMarginLeft(0)
									.setMarginTop(15f));

							if (subItem.trim().length() != 0) {

								text = formatText(subItem, lang);

								menuContent.add(new Paragraph(text).setFont(itemFont)
										.setFontSize(getFontSize(itemFontSize, 16)).setMultipliedLeading(1.2f)
										.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
										.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f)
										.simulateItalic());
							}

							if (isItemSlogan != null && isItemSlogan == 1) {

								if (item.getSlogan() != null && !item.getSlogan().isEmpty()) {

									text = " \"" + formatText(item.getSlogan(), lang) + "\"";

									menuContent.add(new Paragraph(text).setFont(sloganFont)
											.setFontSize(getFontSize(sloganFontSize, 16)).setMultipliedLeading(1.2f)
											.setFontColor(descriptionColor).setMarginLeft(0).setMarginTop(0)
											.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f)
											.simulateItalic());
								}
							}

							if (isItemInstruction != null && isItemInstruction == 1) {

								if (item.getItemNotes() != null && !item.getItemNotes().isEmpty()) {

									text = formatText(item.getItemNotes(), lang);

									menuContent.add(new Paragraph(text).setFont(itemFont)
											.setFontSize(getFontSize(itemFontSize, 16)).setMultipliedLeading(1.2f)
											.setFontColor(descriptionColor).setMarginLeft(18).setMarginTop(0)
											.setTextAlignment(TextAlignment.CENTER).setMarginBottom(0).setPadding(0f)
											.simulateItalic());
								}
							}
						}

						fnDetailTable.addCell(new Cell().add(menuContent).setBorder(Border.NO_BORDER));
					}

					document.add(fnDetailTable);
				}
			}

			// ================= PAGE NUMBERS =================
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);

				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Om Ganeshaya Namah\nJai Jalaram Bapa", page.getPageSize().getWidth() / 2,
						page.getPageSize().getTop() - 35, TextAlignment.CENTER);

				canvas.showTextAligned(cmpName + " / " + hostName + " / " + eventDto.getEventStartTimestamp() + " / "
						+ eventDto.getVenue(), page.getPageSize().getWidth() / 2, 22, TextAlignment.CENTER)
						.setFont(catFont);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventDto.getEventNo()
					+ "/" + getReportName(getPartyNameByEventId(eventId), formatDate(eventDto.getEventStartTimestamp()),
							"excusive report")
					+ ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	public EventFlowReportResponseDto fetchAndPrepareEventFlowReportData(Long eventId, Long eventFunctionId,
			Integer lang, Long userId) {

		List<Object[]> rows = menuPreparationDetailsRepository.getEventMenuReportRaw(eventId, eventFunctionId, lang,
				userId, "en", "en");

		List<Object[]> decorRows = menuPreparationDetailsRepository.getEventDecorReportRaw(eventId, eventFunctionId, 0,
				userId, "en", "en");

		EventFlowReportResponseDto eventDto = new EventFlowReportResponseDto();

		EventReportResponseDto tempEventDto = new EventReportResponseDto();

		Map<Long, EventFunctionReportResponseDto> functionMap = new LinkedHashMap<>();

		Map<String, EventFunctionFlowResponseDto> flowMap = new LinkedHashMap<>();

		boolean eventDataSet = false;

		for (Object[] r : rows) {

			if (!eventDataSet) {
				setEventFlowData(eventDto, r);

				tempEventDto.setFunctions(new ArrayList<>());

				eventDataSet = true;
			}

			EventFunctionReportResponseDto functionDto = getOrCreateFunction(tempEventDto, functionMap, r);

			String functionDate = getFunctionDate(r[11]);

			EventFunctionFlowResponseDto flowDto = getOrCreateFunctionFlow(flowMap, functionDate);

			Long functionId = getLong(r[10]);

			if (!containsFunction(flowDto.getFunctions(), functionId)) {
				flowDto.getFunctions().add(functionDto);
			}

			MenuReportResponseDto categoryDto = getOrCreateCategory(functionDto, r);

			MenuItemForReportResponseDto item = createMenuItem(r);

			categoryDto.getMenuItems().add(item);

			if (Boolean.TRUE.equals(r[77])) {
				categoryDto.setImagePath(item.getImagePath());
			}
		}

		for (Object[] r : decorRows) {

			if (!eventDataSet) {
				setEventFlowData(eventDto, r);

				tempEventDto.setFunctions(new ArrayList<>());

				eventDataSet = true;
			}

			EventFunctionReportResponseDto functionDto = getOrCreateFunction(tempEventDto, functionMap, r);

			String functionDate = getFunctionDate(r[11]);

			EventFunctionFlowResponseDto flowDto = getOrCreateFunctionFlow(flowMap, functionDate);

			Long functionId = getLong(r[10]);

			if (!containsFunction(flowDto.getFunctions(), functionId)) {
				flowDto.getFunctions().add(functionDto);
			}

			DecoreReportResponseDto categoryDto = getOrCreateDecoreCategory(functionDto, r);

			categoryDto.getDecoreItems().add(createDecoreItem(r));
		}

		int dayCount = 1;

		for (EventFunctionFlowResponseDto flow : flowMap.values()) {
			flow.setDayCount(String.valueOf(dayCount));
			dayCount++;
		}

		eventDto.setFunctionFlow(new ArrayList<>(flowMap.values()));

		return eventDto.getEventNo() == null ? null : eventDto;
	}

	private String getFunctionDate(Object value) {

		if (value == null) {
			return null;
		}

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String dateTime = getString(value);

		if (dateTime == null || dateTime.trim().isEmpty()) {
			return null;
		}

		try {
			return LocalDateTime.parse(dateTime, inputFormatter).toLocalDate().format(dateFormatter);

		} catch (Exception e) {
			return dateTime;
		}
	}

	private EventFunctionFlowResponseDto getOrCreateFunctionFlow(Map<String, EventFunctionFlowResponseDto> flowMap,
			String functionDate) {

		String key = functionDate == null ? "" : functionDate;

		EventFunctionFlowResponseDto flowDto = flowMap.get(key);

		if (flowDto == null) {

			flowDto = new EventFunctionFlowResponseDto();

			flowDto.setDate(functionDate);
			flowDto.setFunctions(new ArrayList<>());

			flowMap.put(key, flowDto);
		}

		return flowDto;
	}

	private boolean containsFunction(List<EventFunctionReportResponseDto> functions, Long functionId) {

		if (functions == null || functionId == null) {
			return false;
		}

		for (EventFunctionReportResponseDto function : functions) {

			if (functionId.equals(function.getFunctionId())) {
				return true;
			}
		}

		return false;
	}

	private void setEventFlowData(EventFlowReportResponseDto eventDto, Object[] r) {

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

		DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

		eventDto.setEventId(getLong(r[0]));
		eventDto.setEventNo(getString(r[1]));

		eventDto.setVenue(getString(r[4]));

		eventDto.setEventName(getString(r[6]));

		eventDto.setRemark(getString(r[26]));

		eventDto.setPartyName(getString(r[30]));
		eventDto.setMobileNo(getString(r[31]));

		eventDto.setFoodNotes(getString(r[28]));
		eventDto.setFoodType(getString(r[29]));

		eventDto.setCmpName(getString(r[32]));
		eventDto.setCmpPhone(getString(r[33]));
		eventDto.setCmpAddress(getString(r[34]));

		eventDto.setPax(getString(r[35]));

		eventDto.setUserFirstName(getString(r[36]));
		eventDto.setUserLastName(getString(r[37]));

		eventDto.setCountrycode(getString(r[38]));
		eventDto.setEmail(getString(r[39]));
		eventDto.setLogo(getString(r[40]));

		if (getString(r[3]) != null) {

			LocalDateTime startDateTime = LocalDateTime.parse(getString(r[3]), inputFormatter);

			eventDto.setEventStartTimestamp(startDateTime.toLocalDate().format(dateOnlyFormatter));

			eventDto.setEventTime(startDateTime.toLocalTime().format(timeFormatter));
		}

		if (getString(r[2]) != null) {

			LocalDateTime endDateTime = LocalDateTime.parse(getString(r[2]), inputFormatter);

			eventDto.setEventEndTimestamp(endDateTime.toLocalDate().format(dateOnlyFormatter));
		}

		eventDto.setBillingNameEnglish(getString(r[48]));
		eventDto.setBillingNameHindi(getString(r[49]));
		eventDto.setBillingNameGujarati(getString(r[50]));

		eventDto.setFoodNotesHindi(getString(r[51]));
		eventDto.setFoodNotesGujarati(getString(r[52]));

		eventDto.setServiceHindi(getString(r[55]));
		eventDto.setServiceGujarati(getString(r[56]));

		eventDto.setThemeHindi(getString(r[57]));
		eventDto.setThemeGujarati(getString(r[58]));

		eventDto.setService(getString(r[59]));
		eventDto.setTheme(getString(r[60]));

		eventDto.setBanquetHallId(getLong(r[73]));
		eventDto.setBanquetHallName(getString(r[74]));

		eventDto.setEventManagerId(getLong(r[81]));
		eventDto.setEventManagerName(getString(r[82]));

		eventDto.setCordinationPersonName(getString(r[85]));
		eventDto.setCordinationPersonContactno(getString(r[86]));

		eventDto.setPartyAddress(getString(r[89]));

		eventDto.setPermissable_item(getString(r[90]));
		eventDto.setNot_permissable_item(getString(r[91]));

		eventDto.setReference(getString(r[92]));

		eventDto.setVenue_img(getString(r[93]));

		eventDto.setPrefix(getString(r[95]));

		eventDto.setFunctionFlow(new ArrayList<>());
	}

	private void addCategoryImagesToPages(PdfDocument pdfDocument, PageSize pageSize,
			List<CategoryImagePageInfo> categoryImagePages) {

		if (categoryImagePages == null || categoryImagePages.isEmpty()) {
			return;
		}

		float pageWidth = pageSize.getWidth();
		float pageHeight = pageSize.getHeight();

		float imageWidth = 700f;
		float imageHeight = 800f;

		float leftMargin = 40f;
		float rightMargin = 35f;
		float topMargin = 100f;

		for (CategoryImagePageInfo info : categoryImagePages) {

			if (info.getImageData() == null) {
				continue;
			}

			int startPage = info.getStartPage();
			int endPage = info.getEndPage();

			for (int pageNumber = startPage; pageNumber <= endPage; pageNumber++) {

				if (pageNumber < 1 || pageNumber > pdfDocument.getNumberOfPages()) {
					continue;
				}

				PdfPage page = pdfDocument.getPage(pageNumber);

				float x;

				if (info.isImageOnRight()) {
					x = pageWidth - rightMargin - imageWidth;
				} else {
					x = leftMargin;
				}

				float y = pageHeight - topMargin - imageHeight;

				Image image = new Image(info.getImageData());
				image.scaleAbsolute(imageWidth, imageHeight);
				image.setFixedPosition(x, y);

				Canvas canvas = new Canvas(new PdfCanvas(page), pageSize);
				canvas.add(image);

				canvas.close();
			}
		}
	}
	
	@Override
	public List<SavedMenuPreparationResponseDto> getAllMenuPreparationItems(Long eventId, Long eventFunctionId) {
	    List<Object[]> data = menuPreparationRepository.getMenuPreparationDetails(eventFunctionId, eventId);

	    List<SavedMenuPreparationResponseDto> response = new ArrayList<>();

	    for (Object[] row : data) {

	        SavedMenuPreparationResponseDto dto = new SavedMenuPreparationResponseDto();

	        dto.setMenuPreparationDetailsId(row[0] != null ? ((Number) row[0]).longValue() : null);
	        dto.setMenuItemId(row[1] != null ? ((Number) row[1]).longValue() : null);
	        dto.setNameEnglish(row[2] != null ? row[2].toString() : null);
	        dto.setNameHindi(row[3] != null ? row[3].toString() : null);
	        dto.setNameGujarati(row[4] != null ? row[4].toString() : null);

	        response.add(dto);
	    }

	    return response;
	}
}
