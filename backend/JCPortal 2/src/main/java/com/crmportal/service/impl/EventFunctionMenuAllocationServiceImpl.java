package com.crmportal.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionMenuAllocationEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.MenuAllocationItemCaptainReceipeEntity;
import com.crmportal.entity.MenuAllocationItemRawMaterialEntity;
import com.crmportal.entity.MenuAllocationOrdersEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemCaptainReceipeEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserGodownEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AllocationType;
import com.crmportal.mapper.EventFunctionMasterMapper;
import com.crmportal.mapper.MenuPreparationDetailsMapper;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialFunctionsRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.EvetFunctionMenuAllocationOrderRepository;
import com.crmportal.repository.MenuAllocationItemCaptainReceipeRepository;
import com.crmportal.repository.MenuAllocationItemRawMaterialRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemCaptainReceipeRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRateDishCostingRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UnitRangeRepository;
import com.crmportal.repository.UnitStepwiseRangeRepository;
import com.crmportal.repository.UserGodownRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.CostingReportAgencyResponseDto;
import com.crmportal.request.dto.EventFunctionMenuAllocationOrderRequestDto;
import com.crmportal.request.dto.EventFunctionMenuAllocationRequestDto;
import com.crmportal.request.dto.ItemWeightRateCalRequestDto;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AgencyDataForDatewiseReportResponseDto;
import com.crmportal.response.dto.AgencyMenuCategoryResponseDto;
import com.crmportal.response.dto.AgencyMenuItemResponseDto;
import com.crmportal.response.dto.AgencyRawMaterialCategoryResponseDto;
import com.crmportal.response.dto.AgencyRawMaterialItemsResponseDto;
import com.crmportal.response.dto.AgencyRawMaterialReportResponseDto;
import com.crmportal.response.dto.AgencyResponseDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.ChefAndOutsideChitthiResponseDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.CostingReportResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationFullResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationFunctionFullResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationOrderResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.FnDetailsForDatewiseReportResponseDto;
import com.crmportal.response.dto.FunctionDetailsResponseDto;
import com.crmportal.response.dto.GetEventLaborResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyWithItemsResponseDto;
import com.crmportal.response.dto.MenuAllocationCatWiseResponseDto;
import com.crmportal.response.dto.MenuAllocationItemRawMaterialResponseDto;
import com.crmportal.response.dto.MenuAllocationItemsResponseDto;
import com.crmportal.response.dto.MenuItemAgencyAlloResponseDto;
import com.crmportal.response.dto.MenuItemAllocationResponseDto;
import com.crmportal.response.dto.MenuItemForDatewiseReportResponseDto;
import com.crmportal.response.dto.MenuPreparationItemForMenuAllocationResponseDto;
import com.crmportal.response.dto.SelectedMenuItemForMenuAllocationResponseDto;
import com.crmportal.response.dto.UnitConversionResult;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFunctionMasterService;
import com.crmportal.service.EventFunctionMenuAllocationService;
import com.crmportal.service.EventMasterService;
import com.crmportal.service.EventRawMaterialService;
import com.crmportal.service.MenuAllocationItemRawMaterialService;
import com.crmportal.service.MenuCategoryMasterService;
import com.crmportal.service.MenuItemMasterService;
import com.crmportal.service.PartyMasterService;
import com.crmportal.service.UnitMasterService;
import com.crmportal.service.UserMasterService;
import com.crmportal.utility.AdobeDocxGenerator;
import com.crmportal.utility.ChefWiseRawMaterialHeaderEventHandler;
import com.crmportal.utility.RoundOffUtility;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.splitting.ISplitCharacters;

@Service
@Transactional
public class EventFunctionMenuAllocationServiceImpl implements EventFunctionMenuAllocationService {

	@Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;

	@Autowired
	EventMasterRepository eventRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionRepository;

	@Autowired
	MenuItemMasterRepository menuItemRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryRepository;

	@Autowired
	UserMasterRepository userRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	MenuPreparationHelperService menuPreparationHelperService;

	@Autowired
	EvetFunctionMenuAllocationOrderRepository menuAllocationOrderRepository;

	@Autowired
	PartyMasterRepository partyRepository;

	@Autowired
	UnitMasterRepository unitRepository;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;

	@Autowired
	MenuPreparationDetailsMapper menuPreparationDetailsMapper;

	@Autowired
	MenuPreparationRepository menuPreparationRepository;

	@Autowired
	EventMasterService eventMasterService;

	@Autowired
	MenuItemMasterService menuItemMasterService;

	@Autowired
	MenuCategoryMasterService menuCategoryMasterService;

	@Autowired
	PartyMasterService partyMasterService;

	@Autowired
	EventFunctionMasterService eventFunctionMasterService;

	@Autowired
	UserMasterService userMasterService;

	@Autowired
	UnitMasterService unitMasterService;

	@Autowired
	MenuAllocationItemRawMaterialService MenuAllocationItemRawMaterialService;

	@Autowired
	MenuAllocationItemRawMaterialRepository menuAllocationItemRawMaterRepository;

	@Autowired
	MenuItemRawMaterialRateDishCostingRepository costingRepository;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	EventFunctionMasterMapper eventFunctionMasterMapper;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	UnitStepwiseRangeRepository unitStepwiseRangeRepository;

	@Autowired
	UnitRangeRepository unitRangeRepository;

	@Autowired
	Environment environment;

	@Autowired
	EventRawMaterialService eventRawMaterialService;

	@Autowired
	EventRawMaterialFunctionsRepository eventRawMaterialFunctionsRepository;

	@Autowired
	EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	RoundOffUtility roundOffUtility;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	EventLaborServiceImpl eventLaborServiceImpl;

	@Autowired
	UnitConversionServiceImpl unitConversionService;

	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;

	@Autowired
	EventRawMaterialHelperService eventRawMaterialHelperService;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	EntityManager entityManager;

	@Autowired
	UserGodownRepository userGodownRepository;

	@Autowired
	MenuItemCaptainReceipeRepository menuItemCaptainReceipeRepository;

	@Autowired
	MenuAllocationItemCaptainReceipeRepository menuAllocationItemCaptainReceipeRepository;

	@Autowired
	AdobeDocxGenerator adobeDocxGenerator;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

	// --- Helpers for null-safe BigDecimal math ---
	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);

	private BigDecimal safe(BigDecimal v) {
		return v == null ? ZERO : v;
	}

	private BigDecimal bd(Long v) {
		return v == null ? ZERO : BigDecimal.valueOf(v);
	}

	private BigDecimal bdInt(Integer v) {
		return v == null ? ZERO : BigDecimal.valueOf(v);
	}

	private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
		return safe(a).multiply(safe(b));
	}

	private BigDecimal safeDivide(BigDecimal a, BigDecimal b, int scale, RoundingMode mode) {
		BigDecimal divisor = safe(b);
		if (BigDecimal.ZERO.compareTo(divisor) == 0) {
			return ZERO;
		}
		return safe(a).divide(divisor, scale, mode);
	}

	private static String fileSafe(String data) {
		return data == null ? "" : data.replaceAll("[^\\p{L}\\p{N}_]", "_");
	}

	public String getReportName(String name, String date, String type) {
		return fileSafe(name) + fileSafe(date.replace("/", "_") + " (" + type.toUpperCase() + ")");
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

	@Transactional
	@Override
	public Boolean addOrUpdatedMenuAllocation(@Valid List<EventFunctionMenuAllocationRequestDto> requestList) {

		try {
			int batchSize = 300;

			for (int start = 0; start < requestList.size(); start += batchSize) {

				List<EventFunctionMenuAllocationRequestDto> batch = new ArrayList<>(
						requestList.subList(start, Math.min(start + batchSize, requestList.size())));

				processBatch(batch);
			}

			return true;

		} catch (Exception e) {
			throw new RuntimeException("Failed to save menu allocation", e);
		}
	}

	private void processBatch(List<EventFunctionMenuAllocationRequestDto> requestList) {

		Long eventId = null;

		Map<Long, EventFunctionMenuAllocationEntity> menuAllocationCache = new HashMap<>();
		Map<Long, PartyMasterEntity> partyCache = new HashMap<>();
		Map<Long, UnitMasterEntity> unitCache = new HashMap<>();

		Set<Long> allocationIds = new HashSet<Long>();
		Set<Long> partyIds = new HashSet<Long>();
		Set<Long> unitIds = new HashSet<Long>();

		// 🔹 Collect IDs
		for (EventFunctionMenuAllocationRequestDto dto : requestList) {

			if (dto.getId() != null && dto.getId() != 0) {
				allocationIds.add(dto.getId());
			}

			if (dto.getMenuAllocationOrders() != null) {
				for (EventFunctionMenuAllocationOrderRequestDto orderDto : dto.getMenuAllocationOrders()) {
					if (orderDto.getPartyId() != null) {
						partyIds.add(orderDto.getPartyId());
					}
					if (orderDto.getUnitId() != null) {
						unitIds.add(orderDto.getUnitId());
					}
				}
			}
		}

		// 🔹 Load existing allocations
		if (!allocationIds.isEmpty()) {
			menuAllocationRepository.findAllByIdInAndIsDeleteFalse(allocationIds)
					.forEach(e -> menuAllocationCache.put(e.getId(), e));
		}

		// 🔹 Load parties
		if (!partyIds.isEmpty()) {
			partyRepository.findAllById(partyIds).forEach(p -> partyCache.put(p.getId(), p));
		}

		// 🔹 Load units
		if (!unitIds.isEmpty()) {
			unitRepository.findAllById(unitIds).forEach(u -> unitCache.put(u.getId(), u));
		}

		List<EventFunctionMenuAllocationEntity> entitiesToSave = new ArrayList<EventFunctionMenuAllocationEntity>(
				requestList.size());

		// 🔹 Prepare allocation entities
		for (EventFunctionMenuAllocationRequestDto dto : requestList) {

			boolean isUpdate = dto.getId() != null && dto.getId() != 0;

			EventFunctionMenuAllocationEntity entity = isUpdate ? menuAllocationCache.get(dto.getId())
					: new EventFunctionMenuAllocationEntity();

			entity.setChefLabour(dto.getChefLabour());
			entity.setOutside(dto.getOutside());
			entity.setInside(dto.getInside());
			entity.setPlace(dto.getPlace());
			entity.setInstructions(dto.getInstructions());
			entity.setInstructionsGujarati(dto.getInstructionsGujarati());
			entity.setInstructionsHindi(dto.getInstructionsHindi());
			entity.setPersonCount(dto.getPersonCount());
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			entity.setIsPaxChange(dto.getIsPaxChange());

			entity.setEvent(getEvent(dto.getEventId()));
			entity.setEventFunction(getEventFunction(dto.getEventFunctionId()));
			entity.setMenuItem(getMenuItem(dto.getMenuItemId()));
			entity.setMenuCategory(getMenuCategory(dto.getMenuCategoryId()));
			entity.setUser(getUser(dto.getUserId()));

			entity.setMenuCategorySortOrder(dto.getMenuCategorySortOrder());
			entity.setMenuitemSortOrder(dto.getItemSortOrder());

			entitiesToSave.add(entity);
		}

		// 🔹 Save allocations
		List<EventFunctionMenuAllocationEntity> savedAllocations = menuAllocationRepository.saveAll(entitiesToSave);

		// 🔥 Bulk delete orders
		List<Long> allocationIdsToDelete = savedAllocations.stream()
				.map(new java.util.function.Function<EventFunctionMenuAllocationEntity, Long>() {
					@Override
					public Long apply(EventFunctionMenuAllocationEntity e) {
						return e.getId();
					}
				}).collect(java.util.stream.Collectors.toList());

		menuAllocationOrderRepository.deleteByAllocationIds(allocationIdsToDelete);

		List<MenuAllocationOrdersEntity> allOrders = new ArrayList<MenuAllocationOrdersEntity>();

		// 🔹 Prepare orders
		for (int i = 0; i < requestList.size(); i++) {

			EventFunctionMenuAllocationRequestDto dto = requestList.get(i);
			EventFunctionMenuAllocationEntity allocation = savedAllocations.get(i);

			if (dto.getMenuAllocationOrders() != null) {

				for (EventFunctionMenuAllocationOrderRequestDto orderDto : dto.getMenuAllocationOrders()) {

					MenuAllocationOrdersEntity order = new MenuAllocationOrdersEntity();

					order.setMenuAllocation(allocation);

					if (orderDto.getPartyId() != null) {
						order.setParty(partyCache.get(orderDto.getPartyId()));
					}

					if (orderDto.getUnitId() != null) {
						order.setUnit(unitCache.get(orderDto.getUnitId()));
					}

					order.setPrice(orderDto.getPrice());
					order.setQuantity(orderDto.getQuantity());
					order.setServiceType(orderDto.getServiceType());
					order.setCounterQuantity(orderDto.getCounterQuantity());
					order.setHelperQuantity(orderDto.getHelperQuantity());
					order.setCounterPrice(orderDto.getCounterPrice());
					order.setHelperPrice(orderDto.getHelperPrice());
					order.setTotalPrice(orderDto.getTotalPrice());
					order.setIsOutside(orderDto.getIsOutside());
					order.setRemarks(orderDto.getRemarks());
					order.setNumber(orderDto.getNumber());
					order.setShiftTransPrice(orderDto.getShiftTransPrice());
					order.setReportingTime(orderDto.getReportingTime());
					allOrders.add(order);
				}
			}

			if (eventId == null && allocation.getEvent() != null) {
				eventId = allocation.getEvent().getId();
			}
		}

		if (!allOrders.isEmpty()) {
			menuAllocationOrderRepository.saveAll(allOrders);
		}

		// 🔥 Deduplicate raw material processing
		Set<String> processed = new HashSet<String>();

		for (EventFunctionMenuAllocationEntity allocation : savedAllocations) {

			Long menuItemId = allocation.getMenuItem().getId();
			Long eventFunctionId = allocation.getEventFunction().getId();

			String key = menuItemId + "_" + eventFunctionId;

			if (!processed.add(key)) {
				continue;
			}

			if (Boolean.FALSE.equals(allocation.getOutside())) {

				if (Boolean.TRUE.equals(allocation.getIsPaxChange())) {
					menuAllocationItemRawMaterRepository.deleteRawMaterials(menuItemId, allocation.getEvent().getId(),
							eventFunctionId);

					menuAllocationItemCaptainReceipeRepository.deleteAllocatedCaptainReceipe(menuItemId, eventId,
							eventFunctionId);
				}

				getAllRawMaterial(menuItemId, allocation.getEventFunction(), null);

				if (Boolean.TRUE.equals(allocation.getIsPaxChange())) {
					allocation.setIsPaxChange(false);
				}

			} else {

				menuAllocationItemRawMaterRepository.deleteRawMaterials(menuItemId, allocation.getEvent().getId(),
						eventFunctionId);

				menuAllocationItemCaptainReceipeRepository.deleteAllocatedCaptainReceipe(menuItemId, eventId,
						eventFunctionId);

				eventRawMaterialHelperService.deleteEventRawMaterialFunctions(allocation.getEvent().getId(),
						eventFunctionId, java.util.Collections.singletonList(menuItemId));

			}
		}

		// ✅ Flush & clear (critical for performance)
		entityManager.flush();
		entityManager.clear();
	}

	private void applyCalculationFromEntity(MenuAllocationItemRawMaterialEntity entity, BigDecimal totalWeight,
			UnitMasterEntity unit, BigDecimal rawmaterial_rate, MenuItemRawMaterialEntity itemRawMaterialEntity) {

		BigDecimal rate = BigDecimal.ZERO;
		BigDecimal finalWeight = roundOffUtility.applyUnitRange(totalWeight, unit, itemRawMaterialEntity);
		UnitConversionResult conversion = unitConversionService.autoConvert(unit, finalWeight);

//		adjustBaseWeight(finalWeight, unit,
//				itemRawMaterialEntity.getUnit() == null ? unit : itemRawMaterialEntity.getUnit());

		entity.setUnit(conversion.getUnit());
		if (entity.getRawMaterialWeight().compareTo(BigDecimal.ZERO) == 0) {
			entity.setRate(removeDecimal(rawmaterial_rate));
		} else {
			rate = safeMultiply(entity.getRawmaterial_rate(), finalWeight).divide(entity.getRawMaterialWeight(), 4,
					RoundingMode.HALF_UP);
			entity.setRate(removeDecimal(rate));
		}

		entity.setWeight(conversion.getQuantity()
				.setScale(entity.getUnit() != null
						? (entity.getUnit().getDecimalLimit() == null ? 2 : entity.getUnit().getDecimalLimit())
						: 2, RoundingMode.HALF_UP));

	}

	private EventMasterEntity getEvent(Long id) {
		return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
	}

	private EventFunctionMasterEntity getEventFunction(Long id) {
		return eventFunctionRepository.findById(id).orElseThrow(() -> new RuntimeException("Event Function not found"));
	}

	private MenuItemMasterEntity getMenuItem(Long id) {
		return menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu Item not found"));
	}

	private MenuCategoryMasterEntity getMenuCategory(Long id) {
		return menuCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu Category not found"));
	}

	private UserMasterEntity getUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}

	private RawMaterialMasterEntity getRawMaterial(Long id) {
		return rawMaterialMasterRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Raw Material not found"));
	}

	@Override
	public List<EventFunctionMenuAllocationFullResponseDto> getMenuAllocation(Long eventId, Long eventFunctionId) {

		EventMasterEntity eventEntity = getEvent(eventId);
		List<EventFunctionMasterEntity> eventFunctions = getEventFunctions(eventEntity, eventFunctionId);

		List<EventFunctionMenuAllocationFullResponseDto> result = new ArrayList<>();

		for (EventFunctionMasterEntity function : eventFunctions) {

			AllocationBuildResult buildResult = buildAllocation(eventEntity, function, null);

			EventFunctionMenuAllocationFullResponseDto dto = new EventFunctionMenuAllocationFullResponseDto();
			dto.setMenuAllocation(buildResult.responseList);
			dto.setSelectedItemDetails(buildResult.selectedItems);
			dto.setTotalChefPrice(buildResult.totalChefPrice);
			dto.setTotalOutSidePrice(buildResult.totalOutSidePrice);

			function.setIsUpdate(false);
			eventFunctionRepository.save(function);
			result.add(dto);
		}
		return result;

	}

	@Override
	public List<EventFunctionMenuAllocationFunctionFullResponseDto> getMenuAllocation(Long eventId,
			Long eventFunctionId, String type) {

		EventMasterEntity eventEntity = getEvent(eventId);
		List<EventFunctionMasterEntity> eventFunctions = getEventFunctions(eventEntity, eventFunctionId);

		List<EventFunctionMenuAllocationFunctionFullResponseDto> result = new ArrayList<>();

		for (EventFunctionMasterEntity function : eventFunctions) {

			AllocationBuildResult buildResult = buildAllocation(eventEntity, function, type);

			EventFunctionMenuAllocationFunctionFullResponseDto dto = new EventFunctionMenuAllocationFunctionFullResponseDto();

			dto.setEventFunction(eventFunctionMasterMapper.entityToResponse(function));
			dto.setMenuAllocation(buildResult.responseList);
			dto.setSelectedItemDetails(buildResult.selectedItems);
			dto.setTotalChefPrice(buildResult.totalChefPrice);
			dto.setTotalOutSidePrice(buildResult.totalOutSidePrice);
			function.setIsUpdate(false);
			eventFunctionRepository.save(function);
			result.add(dto);
		}
		return result;
	}

	private AllocationBuildResult buildAllocation(EventMasterEntity eventEntity,
			EventFunctionMasterEntity eventFunctionEntity, String type) {

		List<Object[]> resultList = menuPreparationDetailsRepository
				.getAllMenuPreparationDetails(eventFunctionEntity.getId());

		List<EventFunctionMenuAllocationEntity> allocationEntities = fetchAllocations(eventEntity, eventFunctionEntity,
				null);

		Map<Long, Object[]> rowMap = new HashMap<>();
		for (Object[] row : resultList) {
			rowMap.put(((Number) row[0]).longValue(), row);
		}
		Map<Long, EventFunctionMenuAllocationEntity> allocationMap = new HashMap<>();
		for (EventFunctionMenuAllocationEntity e : allocationEntities) {
			allocationMap.put(e.getMenuItem().getId(), e);
		}

		Map<Long, List<MenuAllocationOrdersEntity>> orderMap = menuAllocationOrderRepository
				.findAllByMenuAllocationInAndIsDeleteFalse(allocationEntities).stream()
				.collect(Collectors.groupingBy(e -> e.getMenuAllocation().getMenuItem().getId()));

		QueryResult qr = processOptimized(resultList, rowMap, allocationMap, eventEntity, eventFunctionEntity, type,
				orderMap);

		return new AllocationBuildResult(qr.getResponseList(), qr.getSelectedItems(), qr.getTotalChefPrice(),
				qr.getTotalOutSidePrice());
	}

	private QueryResult processOptimized(List<Object[]> resultList, Map<Long, Object[]> rowMap,
			Map<Long, EventFunctionMenuAllocationEntity> allocationMap, EventMasterEntity eventEntity,
			EventFunctionMasterEntity eventFunctionEntity, String type,
			Map<Long, List<MenuAllocationOrdersEntity>> orderMap) {

		List<EventFunctionMenuAllocationResponseDto> responseList = new ArrayList<>();
		Map<Long, SelectedMenuItemForMenuAllocationResponseDto> categoryMap = new LinkedHashMap<>();

		BigInteger totalChefPrice = BigInteger.ZERO;
		BigInteger totalOutSidePrice = BigInteger.ZERO;

		for (Object[] row : resultList) {

			Long menuItemId = ((Number) row[0]).longValue();
			EventFunctionMenuAllocationEntity allocationEntity = allocationMap.get(menuItemId);

			EventFunctionMenuAllocationResponseDto dto;
			AllocationType status;
			if (allocationEntity != null) {

				status = AllocationType.from(allocationEntity.getChefLabour(), allocationEntity.getOutside(),
						allocationEntity.getInside());

				if (type != null && !type.equalsIgnoreCase(status.name()))
					continue;

				dto = buildDtoFromEntityFast(allocationEntity, eventFunctionEntity, rowMap, orderMap.get(menuItemId));
			} else {
				status = resolveStatusEnum(row);

				if (type != null && !type.equalsIgnoreCase(status.name()))
					continue;
				dto = buildItemDtoFromRowFast(row, eventEntity, eventFunctionEntity);
			}

			if (dto == null)
				continue;

			responseList.add(dto);

			BigDecimal totalOrderPrice = calculateTotalOrderPrice(dto);

			if (status == AllocationType.CHEF) {
				totalChefPrice = totalChefPrice.add(totalOrderPrice.toBigInteger());
			} else if (status == AllocationType.OUTSIDE) {
				totalOutSidePrice = totalOutSidePrice.add(totalOrderPrice.toBigInteger());
			}
			categoryMap.computeIfAbsent(dto.getMenuCategoryId(), k -> {
				SelectedMenuItemForMenuAllocationResponseDto cat = new SelectedMenuItemForMenuAllocationResponseDto();

				cat.setMenuCategoryId(dto.getMenuCategoryId());
				cat.setMenuCategoryName(dto.getMenuCategoryName());
				cat.setMenuCategoryNameGujarati(dto.getMenuCategoryNameGujarati());
				cat.setMenuCategoryNameHindi(dto.getMenuCategoryNameHindi());
				cat.setSelectedMenuPreparationItems(new ArrayList<>());
				return cat;

			}).getSelectedMenuPreparationItems()
					.add(new MenuPreparationItemForMenuAllocationResponseDto(dto.getMenuItemName(),
							dto.getMenuItemNameHindi(), dto.getMenuItemNameGujarati(),
							dto.getOutside() ? BigDecimal.ZERO
									: sumRawMaterialRate(menuItemId, eventFunctionEntity, null),
							resolveStatus(dto.getChefLabour(), dto.getOutside(), dto.getInside()), true, menuItemId));
		}
		return new QueryResult(responseList, new ArrayList<>(categoryMap.values()), totalChefPrice, totalOutSidePrice);
	}

	private EventFunctionMenuAllocationResponseDto buildDtoFromEntityFast(EventFunctionMenuAllocationEntity entity,
			EventFunctionMasterEntity eventFunctionEntity, Map<Long, Object[]> rowMap,
			List<MenuAllocationOrdersEntity> orderEntities) {

		EventFunctionMenuAllocationResponseDto dto = new EventFunctionMenuAllocationResponseDto();

		dto.setId(entity.getId());
		dto.setChefLabour(entity.getChefLabour());
		dto.setOutside(entity.getOutside());
		dto.setInside(entity.getInside());

		dto.setEventFunctionId(eventFunctionEntity.getId());
		if (entity.getEventFunction().getFunction() != null) {
			dto.setEventFunctionName(entity.getEventFunction().getFunction().getNameEnglish());
		}

		dto.setEventId(eventFunctionEntity.getEvent().getId());
		if (entity.getEvent().getEventType() != null) {
			dto.setEventName(entity.getEvent().getEventType().getNameEnglish());
		}

		dto.setInstructions(entity.getInstructions());
		dto.setInstructionsGujarati(entity.getInstructionsGujarati());
		dto.setInstructionsHindi(entity.getInstructionsHindi());
		dto.setIsPaxChange(entity.getIsPaxChange());
		dto.setItemSortorder(entity.getMenuitemSortOrder());
		dto.setMenuSortorder(entity.getMenuCategorySortOrder());
		dto.setPlace(entity.getPlace());
		dto.setPersonCount(Boolean.TRUE.equals(eventFunctionEntity.getIsUpdate()) ? eventFunctionEntity.getPax()
				: entity.getPersonCount());
		dto.setUserId(entity.getUser().getId());
		dto.setMenuCategoryId(entity.getMenuCategory().getId());
		dto.setMenuCategoryName(entity.getMenuCategory().getNameEnglish());
		dto.setMenuCategoryNameGujarati(entity.getMenuCategory().getNameGujarati());
		dto.setMenuCategoryNameHindi(entity.getMenuCategory().getNameHindi());

		dto.setMenuItemId(entity.getMenuItem().getId());
		dto.setMenuItemName(entity.getMenuItem().getNameEnglish());
		dto.setMenuItemNameGujarati(entity.getMenuItem().getNameGujarati());
		dto.setMenuItemNameHindi(entity.getMenuItem().getNameHindi());

		Object[] row = rowMap.get(entity.getMenuItem().getId());

		List<EventFunctionMenuAllocationOrderResponseDto> orders = new ArrayList<>();

		if (orderEntities != null && !orderEntities.isEmpty()) {

			for (MenuAllocationOrdersEntity orderEntity : orderEntities) {
				orders.add(mapOrderEntityToDto(orderEntity, entity));
			}

		} else {
			if (row != null) {
				orders.add(buildOrderDto(resolveStatus(entity.getChefLabour(), entity.getOutside(), entity.getInside()),
						getString(row, 5), getBigDecimal(row, 6), getInteger(row, 7), getBigDecimal(row, 9),
						getBigDecimal(row, 10), getBigDecimal(row, 11), getLong(row, 12), getString(row, 13),
						getLong(row, 14), getString(row, 15), getString(row, 19), getString(row, 20), 0,
						getString(row, 27), getString(row, 28), BigDecimal.ZERO, ""));
			}
		}
		dto.setEventFunctionMenuAllocations(orders);
		return dto;
	}

	private EventFunctionMenuAllocationOrderResponseDto mapOrderEntityToDto(MenuAllocationOrdersEntity orderEntity,
			EventFunctionMenuAllocationEntity entity) {
		EventFunctionMenuAllocationOrderResponseDto orderDto = new EventFunctionMenuAllocationOrderResponseDto();
		orderDto.setId(orderEntity.getId());
		orderDto.setPartyId(orderEntity.getParty() != null ? orderEntity.getParty().getId() : null);
		orderDto.setPartyName(orderEntity.getParty() != null ? orderEntity.getParty().getNameEnglish() : null);
		orderDto.setPartyNameHindi(orderEntity.getParty() != null ? orderEntity.getParty().getNameHindi() : null);
		orderDto.setPartyNameGujarati(orderEntity.getParty() != null ? orderEntity.getParty().getNameGujarati() : null);
		orderDto.setPrice(orderEntity.getPrice());
		orderDto.setQuantity(orderEntity.getQuantity());
		orderDto.setUnitId(orderEntity.getUnit() != null ? orderEntity.getUnit().getId() : null);
		orderDto.setUnitName(orderEntity.getUnit() != null ? orderEntity.getUnit().getNameEnglish() : null);
		orderDto.setServiceType(orderEntity.getServiceType());
		orderDto.setCounterQuantity(orderEntity.getCounterQuantity());
		orderDto.setHelperQuantity(orderEntity.getHelperQuantity());
		orderDto.setCounterPrice(orderEntity.getCounterPrice());
		orderDto.setHelperPrice(orderEntity.getHelperPrice());
		orderDto.setTotalPrice(orderEntity.getTotalPrice());
		orderDto.setIsOutside(orderEntity.getIsOutside());
		orderDto.setMenuAllocationId(entity.getId());
		orderDto.setRemarks(orderEntity.getRemarks());
		orderDto.setNumber(orderEntity.getNumber());
		orderDto.setPax(orderEntity.getPax());
		orderDto.setShiftTransPrice(orderEntity.getShiftTransPrice());
		orderDto.setReportingTime(orderEntity.getReportingTime());
		return orderDto;
	}

	private EventFunctionMenuAllocationResponseDto buildItemDtoFromRowFast(Object[] row, EventMasterEntity eventEntity,
			EventFunctionMasterEntity eventFunctionEntity) {

		String status = resolveStatus(row);

		List<EventFunctionMenuAllocationOrderResponseDto> orders = new ArrayList<>();

		orders.add(buildOrderDto(status, getString(row, 5), getBigDecimal(row, 6), getInteger(row, 7),
				getBigDecimal(row, 9), getBigDecimal(row, 10), getBigDecimal(row, 11), getLong(row, 12),
				getString(row, 13), getLong(row, 14), getString(row, 15), getString(row, 19), getString(row, 20), 0,
				getString(row, 27), getString(row, 28), BigDecimal.ZERO, ""));

		return buildItemDto(eventEntity, eventFunctionEntity, getLong(row, 2), getString(row, 3), getLong(row, 0),
				getString(row, 1), getString(row, 8), status, orders, getString(row, 18), getString(row, 22),
				getString(row, 21), getString(row, 24), getString(row, 23), eventFunctionEntity.getPax(), true,
				getInteger(row, 25), getInteger(row, 26), getString(row, 29), getString(row, 30));
	}

	private List<EventFunctionMasterEntity> getEventFunctions(EventMasterEntity eventEntity, Long eventFunctionId) {

		if (eventFunctionId == -1) {
			return eventFunctionRepository.findAllByEventAndIsDeleteFalse(eventEntity);
		}

		return Arrays.asList(eventFunctionRepository.findById(eventFunctionId)
				.orElseThrow(() -> new RuntimeException("Event Function not found")));
	}

	private List<EventFunctionMenuAllocationEntity> fetchAllocations(EventMasterEntity eventEntity,
			EventFunctionMasterEntity function, String type) {

		return menuAllocationRepository.findAllocations(eventEntity, function, type);
	}

	private static class AllocationBuildResult {
		List<EventFunctionMenuAllocationResponseDto> responseList;
		List<SelectedMenuItemForMenuAllocationResponseDto> selectedItems;
		BigInteger totalChefPrice;
		BigInteger totalOutSidePrice;

		AllocationBuildResult(List<EventFunctionMenuAllocationResponseDto> responseList,
				List<SelectedMenuItemForMenuAllocationResponseDto> selectedItems, BigInteger totalChefPrice,
				BigInteger totalOutSidePrice) {

			this.responseList = responseList;
			this.selectedItems = selectedItems;
			this.totalChefPrice = totalChefPrice;
			this.totalOutSidePrice = totalOutSidePrice;
		}
	}

	private QueryResult processQueryResult(List<Object[]> resultList, EventMasterEntity eventEntity,
			EventFunctionMasterEntity eventFunctionEntity, Boolean isFromNewTable, String type) {

		List<EventFunctionMenuAllocationResponseDto> responseList = new ArrayList<>();
		Map<Long, SelectedMenuItemForMenuAllocationResponseDto> categoryMap = new LinkedHashMap<>();

		BigInteger totalChefPrice = BigInteger.ZERO;
		BigInteger totalOutSidePrice = BigInteger.ZERO;

		for (Object[] row : resultList) {

			Long menuItemId = ((Number) row[0]).longValue();
			String menuItemName = (String) row[1];
			Long menuCategoryId = ((Number) row[2]).longValue();
			String menuCategoryName = (String) row[3];

			String menuCategoryNameGujarati = (String) row[21];
			String menuCategoryNameHindi = (String) row[22];
			String menuItemNameGujarati = (String) row[23];
			String menuItemNameHindi = (String) row[24];

			String allocationStatus = resolveStatus(row);

			EventFunctionMenuAllocationResponseDto itemDto = null;
			MenuPreparationItemForMenuAllocationResponseDto menuItemDto;

			// ================= NEW TABLE =================
			if (Boolean.TRUE.equals(isFromNewTable)) {

				itemDto = buildItemDtoFromRow(row, eventEntity, eventFunctionEntity, true, type);

				String finalStatus = allocationStatus;
				BigDecimal totalRawRate = BigDecimal.ZERO;
				BigDecimal totalOrderPrice = BigDecimal.ZERO;

				if (itemDto != null) {

					if (!itemDto.getOutside()) {
						totalRawRate = sumRawMaterialRate(menuItemId, eventFunctionEntity, null);
					}
					totalOrderPrice = sumOrderTotal(itemDto);

					if (itemDto.getChefLabour()) {
						finalStatus = "CHEF";
						totalChefPrice = totalChefPrice.add(totalOrderPrice.toBigInteger());
					} else if (itemDto.getOutside()) {
						finalStatus = "OUTSIDE";
						totalOutSidePrice = totalOutSidePrice.add(totalOrderPrice.toBigInteger());
					} else {
						finalStatus = "INSIDE";
					}
				}

				menuItemDto = new MenuPreparationItemForMenuAllocationResponseDto(menuItemName, menuItemNameHindi,
						menuItemNameGujarati, totalRawRate, finalStatus, true, menuItemId);

			}
			// ================= OLD TABLE =================
			else {

				if (type != null && !type.equalsIgnoreCase(allocationStatus)) {
					continue;
				}
				itemDto = buildItemDtoFromRow(row, eventEntity, eventFunctionEntity, false, type);

				BigDecimal totalRawRate = BigDecimal.ZERO;
				BigDecimal totalOrderPrice = BigDecimal.ZERO;

				if (itemDto != null) {

					if (!itemDto.getOutside()) {
						totalRawRate = sumRawMaterialRate(menuItemId, eventFunctionEntity, null);
					}
					totalOrderPrice = sumOrderTotal(itemDto);

					if (itemDto.getChefLabour()) {
						totalChefPrice = totalChefPrice.add(totalOrderPrice.toBigInteger());
					} else if (itemDto.getOutside()) {
						totalOutSidePrice = totalOutSidePrice.add(totalOrderPrice.toBigInteger());
					}
				}

				menuItemDto = new MenuPreparationItemForMenuAllocationResponseDto(menuItemName, menuItemNameHindi,
						menuItemNameGujarati, totalRawRate, allocationStatus, false, menuItemId);
			}

			// 🔹 Add item DTO
			if (itemDto != null) {
				responseList.add(itemDto);
			}

			// 🔹 Group by category
			SelectedMenuItemForMenuAllocationResponseDto categoryDto = categoryMap.get(menuCategoryId);

			if (categoryDto == null) {
				categoryDto = new SelectedMenuItemForMenuAllocationResponseDto();
				categoryDto.setMenuCategoryId(menuCategoryId);
				categoryDto.setMenuCategoryName(menuCategoryName);
				categoryDto.setMenuCategoryNameGujarati(menuCategoryNameGujarati);
				categoryDto.setMenuCategoryNameHindi(menuCategoryNameHindi);
				categoryDto.setSelectedMenuPreparationItems(new ArrayList<>());
				categoryMap.put(menuCategoryId, categoryDto);
			}

			categoryDto.getSelectedMenuPreparationItems().add(menuItemDto);
		}
		return new QueryResult(responseList, new ArrayList<>(categoryMap.values()), totalChefPrice, totalOutSidePrice);
	}

	private String resolveStatus(Object[] row) {
		if (row[4] != null && (Boolean) row[4])
			return "OUTSIDE";
		if (row.length > 17 && row[17] != null && (Boolean) row[17])
			return "CHEF";
		return "INSIDE";
	}

	private BigDecimal sumRawMaterialRate(Long menuItemId, EventFunctionMasterEntity eventFunctionEntity, Integer pax) {

		List<MenuAllocationItemRawMaterialResponseDto> rawMaterials = getAllRawMaterial(menuItemId, eventFunctionEntity,
				pax);

		BigDecimal total = BigDecimal.ZERO;
		for (MenuAllocationItemRawMaterialResponseDto rm : rawMaterials) {
			BigDecimal rate = rm.getRate();
			if (rate != null) {
				total = total.add(rate);
			}
		}
		return total;
	}

	private BigDecimal sumOrderTotal(EventFunctionMenuAllocationResponseDto itemDto) {
		BigDecimal total = BigDecimal.ZERO;

		if (itemDto != null && itemDto.getEventFunctionMenuAllocations() != null) {
			for (EventFunctionMenuAllocationOrderResponseDto o : itemDto.getEventFunctionMenuAllocations()) {

				total = total.add(o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO);
			}
		}
		return total;
	}

	private Map<String, Object> processExistingAllocations(List<EventFunctionMenuAllocationEntity> allocationEntities,
			EventMasterEntity eventEntity, EventFunctionMasterEntity eventFunctionEntity, List<Object[]> resultList,
			boolean isFromNewTable, String type) {
		Map<String, Object> resp = new HashMap<>();
		List<EventFunctionMenuAllocationResponseDto> responseList = new ArrayList<>();

		Map<Long, Object[]> rowByMenuItemId = new HashMap<>();
		for (Object[] row : resultList) {
			rowByMenuItemId.put(((Number) row[0]).longValue(), row);
		}

		Set<Long> queriedMenuItemIds = rowByMenuItemId.keySet();

		Set<Long> existingMenuItemIds = new HashSet<>();
		for (EventFunctionMenuAllocationEntity e : allocationEntities) {
			existingMenuItemIds.add(e.getMenuItem().getId());
		}

		BigInteger totalOutSidePrice = BigInteger.ZERO;
		BigInteger totalChefPrice = BigInteger.ZERO;
		// ---------- Handle Updates ----------
		if (isFromNewTable) {
			// Add new
			Set<Long> toAdd = new HashSet<>(queriedMenuItemIds);
			toAdd.removeAll(existingMenuItemIds);
			for (Long addId : toAdd) {

				Object[] row = rowByMenuItemId.get(addId);
				if (row == null)
					continue;
				EventFunctionMenuAllocationResponseDto dto = buildItemDtoFromRow(row, eventEntity, eventFunctionEntity,
						true, type);

				if (dto != null) {
					BigDecimal totalOrderPrice = calculateTotalOrderPrice(dto);
					if (dto.getOutside()) {
						totalOutSidePrice = totalOutSidePrice.add(totalOrderPrice.toBigInteger());
					} else if (dto.getChefLabour()) {

						totalChefPrice = totalChefPrice.add(totalOrderPrice.toBigInteger());
					}

					responseList.add(dto);
				}
			}
		}
		// ---------- Existing allocations ----------
		for (EventFunctionMenuAllocationEntity entity : allocationEntities) {
			EventFunctionMenuAllocationResponseDto dto = buildDtoFromEntity(entity, eventFunctionEntity, resultList,
					type);

			if (dto != null) {
				BigDecimal totalOrderPrice = calculateTotalOrderPrice(dto);

				if (dto.getOutside()) {
					totalOutSidePrice = totalOutSidePrice.add(totalOrderPrice.toBigInteger());
				} else if (dto.getChefLabour()) {
					totalChefPrice = totalChefPrice.add(totalOrderPrice.toBigInteger());
				}

				responseList.add(dto);
			}
		}
		resp.put("responseList", responseList);
		resp.put("totalOutSidePrice", totalOutSidePrice);
		resp.put("totalChefPrice", totalChefPrice);

		return resp;
	}

	private BigDecimal calculateTotalOrderPrice(EventFunctionMenuAllocationResponseDto dto) {
		BigDecimal total = BigDecimal.ZERO;

		if (dto != null && dto.getEventFunctionMenuAllocations() != null) {
			for (EventFunctionMenuAllocationOrderResponseDto o : dto.getEventFunctionMenuAllocations()) {
				total = total.add(o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO);
			}
		}
		return total;
	}

	private EventFunctionMenuAllocationResponseDto buildDtoFromEntity(EventFunctionMenuAllocationEntity entity,
			EventFunctionMasterEntity eventFunctionEntity, List<Object[]> resultList, String type) {

		String status = resolveStatus(entity.getChefLabour(), entity.getOutside(), entity.getInside());
		if (type != null && !type.equalsIgnoreCase(status)) {
			return null;
		}

		EventFunctionMenuAllocationResponseDto dto = new EventFunctionMenuAllocationResponseDto();

		dto.setId(entity.getId());
		dto.setChefLabour(entity.getChefLabour());
		dto.setOutside(entity.getOutside());
		dto.setInside(entity.getInside());

		dto.setPersonCount(Boolean.TRUE.equals(eventFunctionEntity.getIsUpdate()) ? eventFunctionEntity.getPax()
				: entity.getPersonCount());

		dto.setPlace(entity.getPlace());
		dto.setInstructions(entity.getInstructions());
		dto.setInstructionsGujarati(entity.getInstructionsGujarati());
		dto.setInstructionsHindi(entity.getInstructionsHindi());
		dto.setEventId(entity.getEvent().getId());
		if (entity.getEvent().getEventType() != null) {
			dto.setEventName(entity.getEvent().getEventType().getNameEnglish());
		}

		dto.setEventFunctionId(entity.getEventFunction().getId());
		if (entity.getEventFunction().getFunction() != null) {
			dto.setEventFunctionName(entity.getEventFunction().getFunction().getNameEnglish());
		}

		dto.setMenuCategoryId(entity.getMenuCategory().getId());
		dto.setMenuCategoryName(entity.getMenuCategory().getNameEnglish());
		dto.setMenuCategoryNameGujarati(entity.getMenuCategory().getNameGujarati());
		dto.setMenuCategoryNameHindi(entity.getMenuCategory().getNameHindi());

		dto.setMenuItemId(entity.getMenuItem().getId());
		dto.setMenuItemName(entity.getMenuItem().getNameEnglish());
		dto.setMenuItemNameGujarati(entity.getMenuItem().getNameGujarati());
		dto.setMenuItemNameHindi(entity.getMenuItem().getNameHindi());
		dto.setMenuSortorder(entity.getMenuCategorySortOrder());
		dto.setItemSortorder(entity.getMenuitemSortOrder());

		dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
		dto.setIsPaxChange(entity.getIsPaxChange());
		// ---------- Fetch Orders ----------
		List<EventFunctionMenuAllocationOrderResponseDto> orderDtos = new ArrayList<>();

		List<MenuAllocationOrdersEntity> orderEntities = menuAllocationOrderRepository
				.findAllByMenuAllocationAndIsDeleteFalse(entity);

		if (!orderEntities.isEmpty()) {

			for (MenuAllocationOrdersEntity orderEntity : orderEntities) {

				EventFunctionMenuAllocationOrderResponseDto orderDto = new EventFunctionMenuAllocationOrderResponseDto();

				orderDto.setId(orderEntity.getId());
				orderDto.setPartyId(orderEntity.getParty() != null ? orderEntity.getParty().getId() : null);
				orderDto.setPartyName(orderEntity.getParty() != null ? orderEntity.getParty().getNameEnglish() : null);
				orderDto.setPartyNameHindi(
						orderEntity.getParty() != null ? orderEntity.getParty().getNameHindi() : null);
				orderDto.setPartyNameGujarati(
						orderEntity.getParty() != null ? orderEntity.getParty().getNameGujarati() : null);
				orderDto.setPrice(orderEntity.getPrice());
				orderDto.setQuantity(orderEntity.getQuantity());
				orderDto.setUnitId(orderEntity.getUnit() != null ? orderEntity.getUnit().getId() : null);
				orderDto.setUnitName(orderEntity.getUnit() != null ? orderEntity.getUnit().getNameEnglish() : null);
				orderDto.setServiceType(orderEntity.getServiceType());
				orderDto.setCounterQuantity(orderEntity.getCounterQuantity());
				orderDto.setHelperQuantity(orderEntity.getHelperQuantity());
				orderDto.setCounterPrice(orderEntity.getCounterPrice());
				orderDto.setHelperPrice(orderEntity.getHelperPrice());
				orderDto.setTotalPrice(orderEntity.getTotalPrice());
				orderDto.setIsOutside(orderEntity.getIsOutside());
				orderDto.setMenuAllocationId(entity.getId());
				orderDto.setRemarks(orderEntity.getRemarks());
				orderDto.setNumber(orderEntity.getNumber());
				orderDto.setPax(orderEntity.getPax());
				orderDto.setShiftTransPrice(orderEntity.getShiftTransPrice());
				orderDto.setReportingTime(orderEntity.getReportingTime());
				orderDtos.add(orderDto);
			}

		} else {
			// ---------- Fallback from resultList ----------
			Object[] row = null;
			for (Object[] r : resultList) {
				if (((Number) r[0]).longValue() == entity.getMenuItem().getId()) {
					row = r;
					break;
				}
			}

			if (row != null) {
				orderDtos.add(buildOrderDto(
						resolveStatus(row.length > 17 && row[17] != null ? (Boolean) row[17] : false, (Boolean) row[4],
								true),
						(String) row[5], row[6] != null ? (BigDecimal) row[6] : BigDecimal.ZERO,
						row[7] != null ? ((Number) row[7]).intValue() : 0,
						row[9] != null ? (BigDecimal) row[9] : BigDecimal.ZERO,
						row[10] != null ? (BigDecimal) row[10] : BigDecimal.ZERO,
						row[11] != null ? (BigDecimal) row[11] : BigDecimal.ZERO,
						row[12] != null ? ((Number) row[12]).longValue() : null, (String) row[13],
						row[14] != null ? ((Number) row[14]).longValue() : null, (String) row[15], (String) row[19],
						(String) row[20], 0, row[27] != null ? (String) row[27] : null,
						row[28] != null ? (String) row[28] : null, BigDecimal.ZERO, ""));
			}
		}

		dto.setEventFunctionMenuAllocations(orderDtos);
		return dto;
	}

	private EventFunctionMenuAllocationOrderResponseDto buildOrderDto(String allocationStatus, String allocationType,
			BigDecimal basePrice, Integer counterNo, BigDecimal pricePerHelper, BigDecimal pricePerLabour,
			BigDecimal qtyPer100Person, Long partyId, String partyName, Long unitId, String unitName, String number,
			String remarks, Integer pax, String partyNameGujarati, String partyNameHindi, BigDecimal shiftTransPrice,
			String reportingTime) {

		basePrice = safe(basePrice);
		pricePerHelper = safe(pricePerHelper);
		pricePerLabour = safe(pricePerLabour);
		qtyPer100Person = safe(qtyPer100Person);

		int safeCounterNo = counterNo != null ? counterNo : 0;
		int safePax = pax != null ? pax : 0;

		EventFunctionMenuAllocationOrderResponseDto orderDto = new EventFunctionMenuAllocationOrderResponseDto();

		orderDto.setId(0L);
		orderDto.setMenuAllocationId(null);
		orderDto.setPartyId(partyId);
		orderDto.setPartyName(partyName);
		orderDto.setPartyNameGujarati(partyNameGujarati);
		orderDto.setPartyNameHindi(partyNameHindi);
		orderDto.setUnitId(unitId);
		orderDto.setUnitName(unitName);
		orderDto.setNumber(number);
		orderDto.setRemarks(remarks);
		orderDto.setPax(safePax);
		orderDto.setServiceType(allocationType);
		shiftTransPrice = shiftTransPrice == null ? BigDecimal.ZERO : shiftTransPrice;
		orderDto.setShiftTransPrice(shiftTransPrice == null ? BigDecimal.ZERO : shiftTransPrice);
		orderDto.setReportingTime(reportingTime);
		BigDecimal totalPrice = BigDecimal.ZERO;

		if ("CHEF".equalsIgnoreCase(allocationStatus)) {

			BigDecimal counters = BigDecimal.valueOf(safeCounterNo);

			BigDecimal labourTotal = pricePerLabour.multiply(counters);
			BigDecimal helperTotal = pricePerHelper.multiply(counters);

			totalPrice = labourTotal.add(helperTotal);

			orderDto.setCounterQuantity(safeCounterNo);
			orderDto.setHelperQuantity(0);
			orderDto.setCounterPrice(pricePerLabour);
			orderDto.setHelperPrice(pricePerHelper);

		} else if ("OUTSIDE".equalsIgnoreCase(allocationStatus)) {

			// (basePrice * qtyPer100Person * pax) / 100
			totalPrice = basePrice.multiply(qtyPer100Person).multiply(BigDecimal.valueOf(safePax))
					.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);

			orderDto.setQuantity(qtyPer100Person);
			orderDto.setPrice(basePrice);
		}

		orderDto.setIsOutside("OUTSIDE".equalsIgnoreCase(allocationStatus));
		orderDto.setTotalPrice(totalPrice.add(shiftTransPrice));
		return orderDto;
	}

	private EventFunctionMenuAllocationResponseDto buildItemDto(EventMasterEntity eventEntity,
			EventFunctionMasterEntity eventFunctionEntity, Long menuCategoryId, String menuCategoryName,
			Long menuItemId, String menuItemName, String godownLocation, String allocationStatus,
			List<EventFunctionMenuAllocationOrderResponseDto> orderDtos, String itemInstruction,
			String menuCategoryNameHindi, String menuCategoryNameGujarati, String menuItemNameHindi,
			String menuItemNameGujarati, Integer newPax, Boolean isPaxChange, Integer menuSortorder,
			Integer itemSortorder, String itemInstructionGujarati, String itemInstructionHindi) {

		EventFunctionMenuAllocationResponseDto itemDto = new EventFunctionMenuAllocationResponseDto();

		itemDto.setId(0L);
		itemDto.setEventId(eventEntity.getId());

		if (eventEntity.getEventType() != null) {
			itemDto.setEventName(eventEntity.getEventType().getNameEnglish());
		}

		itemDto.setEventFunctionId(eventFunctionEntity.getId());

		if (eventFunctionEntity.getFunction() != null) {
			itemDto.setEventFunctionName(eventFunctionEntity.getFunction().getNameEnglish());
		}

		itemDto.setMenuCategoryId(menuCategoryId);
		itemDto.setMenuCategoryName(menuCategoryName);
		itemDto.setMenuCategoryNameGujarati(menuCategoryNameGujarati);
		itemDto.setMenuCategoryNameHindi(menuCategoryNameHindi);
		itemDto.setMenuItemId(menuItemId);
		itemDto.setMenuItemName(menuItemName);
		itemDto.setMenuItemNameGujarati(menuItemNameGujarati);
		itemDto.setMenuItemNameHindi(menuItemNameHindi);
		itemDto.setItemSortorder(itemSortorder);
		itemDto.setMenuSortorder(menuSortorder);

		// Pax logic
		itemDto.setPersonCount(
				Boolean.TRUE.equals(eventFunctionEntity.getIsUpdate()) ? eventFunctionEntity.getPax() : newPax);

		// Allocation flags
		boolean isChef = "CHEF".equalsIgnoreCase(allocationStatus);
		boolean isOutside = "OUTSIDE".equalsIgnoreCase(allocationStatus);

		itemDto.setChefLabour(isChef);
		itemDto.setOutside(isOutside);
		itemDto.setInside(!isChef && !isOutside);

		itemDto.setPlace(godownLocation != null ? godownLocation : "");
		itemDto.setInstructions(itemInstruction);
		itemDto.setInstructionsGujarati(itemInstructionGujarati);
		itemDto.setInstructionsHindi(itemInstructionHindi);
		if (eventEntity.getUser() != null) {
			itemDto.setUserId(eventEntity.getUser().getId());
		}

		itemDto.setEventFunctionMenuAllocations(orderDtos != null ? orderDtos : new ArrayList<>());

		return itemDto;
	}

	private EventFunctionMenuAllocationResponseDto buildItemDtoFromRow(Object[] row, EventMasterEntity eventEntity,
			EventFunctionMasterEntity eventFunctionEntity, Boolean isFromNewTable, String type) {

		// -------- Base Row Parsing --------
		Long menuItemId = getLong(row, 0);
		String menuItemName = getString(row, 1);
		Long menuCategoryId = getLong(row, 2);
		String menuCategoryName = getString(row, 3);

		Boolean outside = getBoolean(row, 4);
		String allocationType = getString(row, 5);
		BigDecimal basePrice = getBigDecimal(row, 6);
		Integer counterNo = getInteger(row, 7);
		String godownLocation = getString(row, 8);
		BigDecimal pricePerHelper = getBigDecimal(row, 9);
		BigDecimal pricePerLabour = getBigDecimal(row, 10);
		BigDecimal qtyPer100Person = getBigDecimal(row, 11);
		Long partyId = getLong(row, 12);
		String partyName = getString(row, 13);
		String partyNameGujarati = getString(row, 27);
		String partyNameHindi = getString(row, 28);
		Long unitId = getLong(row, 14);
		String unitName = getString(row, 15);
		Boolean chef = row.length > 17 ? getBoolean(row, 17) : false;
		boolean inside = Boolean.FALSE.equals(chef) && Boolean.FALSE.equals(outside);
		String itemInstruction = getString(row, 18);
		String number = getString(row, 19);
		String remarks = getString(row, 20);

		String menuCategoryNameGujarati = getString(row, 21);
		String menuCategoryNameHindi = getString(row, 22);
		String menuItemNameGujarati = getString(row, 23);
		String menuItemNameHindi = getString(row, 24);
		Integer menuSortorder = getInteger(row, 25);
		Integer itemSortorder = getInteger(row, 26);
		String itemInstructionGujarati = getString(row, 29);
		String itemInstructionHindi = getString(row, 30);
		String allocationStatus = resolveStatus(chef, outside, inside);
		// -------- New Table Logic --------
		if (Boolean.TRUE.equals(isFromNewTable)) {

			EventFunctionMenuAllocationEntity allocationEntity = menuAllocationRepository
					.findByMenuItem_IdAndEventFunctionAndIsDeleteFalse(menuItemId, eventFunctionEntity);

			if (allocationEntity == null) {
				if (type != null && !type.equalsIgnoreCase(allocationStatus)) {
					return null;
				}

				List<EventFunctionMenuAllocationOrderResponseDto> orderDtos = new ArrayList<>();
				orderDtos.add(buildOrderDto(allocationStatus, allocationType, basePrice, counterNo, pricePerHelper,
						pricePerLabour, qtyPer100Person, partyId, partyName, unitId, unitName, number, remarks, 0,
						partyNameGujarati, partyNameHindi, BigDecimal.ZERO, ""));

				return buildItemDto(eventEntity, eventFunctionEntity, menuCategoryId, menuCategoryName, menuItemId,
						menuItemName, godownLocation, allocationStatus, orderDtos, itemInstruction,
						menuCategoryNameHindi, menuCategoryNameGujarati, menuItemNameHindi, menuItemNameGujarati,
						eventFunctionEntity.getPax(), true, menuSortorder, itemSortorder, itemInstructionGujarati,
						itemInstructionHindi);
			}

			String status = resolveStatus(allocationEntity.getChefLabour(), allocationEntity.getOutside(),
					allocationEntity.getInside());
			if (type != null && !type.equalsIgnoreCase(status)) {
				return null;
			}
			List<MenuAllocationOrdersEntity> orderEntities = menuAllocationOrderRepository.findByMenuItemId(menuItemId,
					allocationEntity.getId());

			List<EventFunctionMenuAllocationOrderResponseDto> orderDtos = buildOrderDtos(orderEntities, status,
					allocationStatus, allocationType, basePrice, counterNo, pricePerHelper, pricePerLabour,
					qtyPer100Person, partyId, partyName, unitId, unitName, number, remarks, partyNameGujarati,
					partyNameHindi);

			return buildItemDto(eventEntity, eventFunctionEntity, allocationEntity.getMenuCategory().getId(),
					allocationEntity.getMenuCategory().getNameEnglish(), allocationEntity.getMenuItem().getId(),
					allocationEntity.getMenuItem().getNameEnglish(), allocationEntity.getPlace(), status, orderDtos,
					itemInstruction, allocationEntity.getMenuCategory().getNameHindi(),
					allocationEntity.getMenuCategory().getNameGujarati(), allocationEntity.getMenuItem().getNameHindi(),
					allocationEntity.getMenuItem().getNameGujarati(), allocationEntity.getPersonCount(),
					allocationEntity.getIsPaxChange(), allocationEntity.getMenuCategorySortOrder(),
					allocationEntity.getMenuitemSortOrder(), allocationEntity.getInstructionsGujarati(),
					allocationEntity.getInstructionsHindi());
		}
		// -------- Old Table Logic --------
		if (type != null && !type.equalsIgnoreCase(allocationStatus)) {
			return null;
		}

		List<EventFunctionMenuAllocationOrderResponseDto> orderDtos = new ArrayList<>();
		orderDtos.add(buildOrderDto(allocationStatus, allocationType, basePrice, counterNo, pricePerHelper,
				pricePerLabour, qtyPer100Person, partyId, partyName, unitId, unitName, number, remarks, 0,
				partyNameGujarati, partyNameHindi, BigDecimal.ZERO, ""));

		return buildItemDto(eventEntity, eventFunctionEntity, menuCategoryId, menuCategoryName, menuItemId,
				menuItemName, godownLocation, allocationStatus, orderDtos, itemInstruction, menuCategoryNameHindi,
				menuCategoryNameGujarati, menuItemNameHindi, menuItemNameGujarati, eventFunctionEntity.getPax(), true,
				menuSortorder, itemSortorder, itemInstructionGujarati, itemInstructionHindi);
	}

	private String resolveStatus(Boolean chef, Boolean outside, Boolean inside) {
		if (Boolean.TRUE.equals(chef))
			return "CHEF";
		if (Boolean.TRUE.equals(outside))
			return "OUTSIDE";
		return "INSIDE";
	}

	private AllocationType resolveStatusEnum(Object[] row) {
		if (row[4] != null && (Boolean) row[4])
			return AllocationType.OUTSIDE;
		if (row.length > 17 && row[17] != null && (Boolean) row[17])
			return AllocationType.CHEF;
		return AllocationType.INSIDE;
	}

	private List<EventFunctionMenuAllocationOrderResponseDto> buildOrderDtos(List<MenuAllocationOrdersEntity> entities,
			String status, String allocationStatus, String allocationType, BigDecimal basePrice, Integer counterNo,
			BigDecimal pricePerHelper, BigDecimal pricePerLabour, BigDecimal qtyPer100Person, Long partyId,
			String partyName, Long unitId, String unitName, String number, String remarks, String partyNameGujarati,
			String partyNameHindi) {

		List<EventFunctionMenuAllocationOrderResponseDto> result = new ArrayList<>();

		if (entities == null || entities.isEmpty()) {
			result.add(buildOrderDto(allocationStatus, allocationType, basePrice, counterNo, pricePerHelper,
					pricePerLabour, qtyPer100Person, partyId, partyName, unitId, unitName, number, remarks, 0,
					partyNameGujarati, partyNameHindi, BigDecimal.ZERO, ""));
			return result;
		}

		for (MenuAllocationOrdersEntity e : entities) {
			result.add(buildOrderDto(status, e.getServiceType(), e.getPrice(), e.getCounterQuantity(),
					e.getHelperPrice(), e.getCounterPrice(), e.getQuantity(),
					e.getParty() != null ? e.getParty().getId() : null,
					e.getParty() != null ? e.getParty().getNameEnglish() : null,
					e.getUnit() != null ? e.getUnit().getId() : null,
					e.getUnit() != null ? e.getUnit().getNameEnglish() : null, e.getNumber(), e.getRemarks(),
					e.getPax(), partyNameGujarati, partyNameHindi, e.getShiftTransPrice(), e.getReportingTime()));
		}
		return result;
	}

	private Long getLong(Object[] row, int i) {
		return row[i] != null ? ((Number) row[i]).longValue() : null;
	}

	private Integer getInteger(Object[] row, int i) {
		return row[i] != null ? ((Number) row[i]).intValue() : 0;
	}

	private BigDecimal getBigDecimal(Object[] row, int i) {
		return row[i] != null ? (BigDecimal) row[i] : BigDecimal.ZERO;
	}

	private String getString(Object[] row, int i) {
		return row[i] != null ? row[i].toString() : null;
	}

	private Boolean getBoolean(Object[] row, int i) {
		return row[i] != null && (Boolean) row[i];
	}

	private static class QueryResult {
		private final List<EventFunctionMenuAllocationResponseDto> responseList;
		private final List<SelectedMenuItemForMenuAllocationResponseDto> selectedItems;
		private BigInteger totalChefPrice;
		private BigInteger totalOutSidePrice;

		public QueryResult(List<EventFunctionMenuAllocationResponseDto> responseList,
				List<SelectedMenuItemForMenuAllocationResponseDto> selectedItems, BigInteger totalChefPrice,
				BigInteger totalOutSidePrice) {
			this.responseList = responseList;
			this.selectedItems = selectedItems;
			this.totalChefPrice = totalChefPrice;
			this.totalOutSidePrice = totalOutSidePrice;
		}

		public List<EventFunctionMenuAllocationResponseDto> getResponseList() {
			return responseList;
		}

		public List<SelectedMenuItemForMenuAllocationResponseDto> getSelectedItems() {
			return selectedItems;
		}

		public BigInteger getTotalChefPrice() {
			return totalChefPrice;
		}

		public BigInteger getTotalOutSidePrice() {
			return totalOutSidePrice;
		}

	}

	@Override
	public Boolean deleteMenuAllocationOrder(Long id) {
		Boolean isSuccess = false;

		if (menuAllocationOrderRepository.existsByIdAndIsDeleteFalse(id)) {
			MenuAllocationOrdersEntity entity = menuAllocationOrderRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsDelete(true);
			menuAllocationOrderRepository.save(entity);
			isSuccess = true;
		}

		return isSuccess;

	}

	public List<MenuAllocationItemRawMaterialResponseDto> getAllRawMaterial(Long menuItemId,
			EventFunctionMasterEntity eventFunction, Integer pax) {

		List<MenuAllocationItemRawMaterialResponseDto> response = new ArrayList<>();

		List<MenuAllocationItemRawMaterialEntity> allocations = menuAllocationItemRawMaterRepository
				.findAllByMenuItem_IdAndEventFunctionAndIsDeleteFalse(menuItemId, eventFunction);

		List<MenuAllocationItemCaptainReceipeEntity> captainReceipes = menuAllocationItemCaptainReceipeRepository
				.findAllByMenuItem_IdAndEventFunctionAndIsDeleteFalse(menuItemId, eventFunction);

		System.out.println("captainReceipes size : " + captainReceipes.size());
		if (!allocations.isEmpty() || !captainReceipes.isEmpty()) {
			System.out.println("2");
			if (!allocations.isEmpty()) {
				response = new ArrayList<>(allocations.size());
				for (MenuAllocationItemRawMaterialEntity entity : allocations) {
					response.add(buildBaseDto(entity, FORMATTER));
				}
			}
			if (!captainReceipes.isEmpty()) {
				for (MenuAllocationItemCaptainReceipeEntity entity : captainReceipes) {
					response.add(buildBaseDto(entity, FORMATTER));
				}
			}
			return response;
		}
		System.out.println("3");
		/* %%%%%%%%%%%%%%%%% Raw Material %%%%%%%%%%%%%%%%%% */
		List<MenuItemRawMaterialEntity> rawMaterials = menuItemRawMaterialRepository.findAllByMenuItemWithRelations(
				menuItemId, eventFunction.getEvent().getId(), eventFunction.getId(),
				eventFunction.getEvent().getUser().getId());

		List<MenuItemCaptainReceipeEntity> captReceipe = menuItemCaptainReceipeRepository
				.findAllByMenuItemWithRelations(menuItemId);
		System.out.println("captain receipe 2 size : " + captReceipe.size());
		if (rawMaterials.isEmpty() && captReceipe.isEmpty()) {
			return Collections.emptyList();
		}

		EventFunctionMenuAllocationEntity isExisting = menuAllocationRepository.getData(menuItemId,
				eventFunction.getId());
		response = new ArrayList<>(rawMaterials.size());
		List<MenuAllocationItemRawMaterialEntity> entitiesToSave = new ArrayList<>(rawMaterials.size());

		Integer newPax;
		if (isExisting != null && !eventFunction.getIsUpdate()) {
			if (pax != null) {
				newPax = pax;
			} else {
				newPax = isExisting.getPersonCount();
			}
		} else {
			newPax = eventFunction.getPax();
		}

		Map<Long, UnitMasterEntity> unitCache = new HashMap<>();

		for (MenuItemRawMaterialEntity entity : rawMaterials) {
			MenuAllocationItemRawMaterialResponseDto dto = buildDtoIfNotExist(entity, eventFunction, FORMATTER);
			BigDecimal baseWeight = calculateDefaultWeight(entity, eventFunction, newPax);

			applyCalculation(dto, baseWeight, entity.getUnit(), entity.getRate(), entity);

			Long unitId = dto.getUnitId();
			UnitMasterEntity unitEntity = unitCache.get(unitId);
			if (unitEntity == null) {
				unitEntity = unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(unitId);
				unitCache.put(unitId, unitEntity);
			}

			MenuAllocationItemRawMaterialEntity allocationItemRawMaterialEntity = new MenuAllocationItemRawMaterialEntity();

			allocationItemRawMaterialEntity.setDateTime(eventFunction.getFunctionStartDateTime());
			allocationItemRawMaterialEntity.setEvent(eventFunction.getEvent());
			allocationItemRawMaterialEntity.setEventFunction(eventFunction);
			allocationItemRawMaterialEntity.setIsActive(true);
			allocationItemRawMaterialEntity.setIsDelete(false);
			allocationItemRawMaterialEntity.setIsNewRaw(false);
			allocationItemRawMaterialEntity.setMenuItem(entity.getMenuItem());
			allocationItemRawMaterialEntity.setParty(null);
			allocationItemRawMaterialEntity.setSupRate(
					entity.getRawMaterial() != null ? entity.getRawMaterial().getSupplierRate() : BigDecimal.ZERO);
			allocationItemRawMaterialEntity.setMasterRawUnitId(
					entity.getRawMaterial() != null ? entity.getRawMaterial().getUnit().getId() : null);
			allocationItemRawMaterialEntity.setPlace(
					(entity.getVenue() == null || entity.getVenue().trim().isEmpty()) ? "At venue" : entity.getVenue());
			allocationItemRawMaterialEntity.setRate(dto.getRate());
			allocationItemRawMaterialEntity.setWeight(dto.getWeight());
			allocationItemRawMaterialEntity.setRawMaterial(entity.getRawMaterial());
			allocationItemRawMaterialEntity.setRawmaterial_rate(dto.getRawmaterial_rate());
			allocationItemRawMaterialEntity.setRawMaterialWeight(dto.getRawmaterial_weight());
			allocationItemRawMaterialEntity.setUnit(unitEntity);

			entitiesToSave.add(allocationItemRawMaterialEntity);
			response.add(dto);
		}

		List<MenuAllocationItemRawMaterialEntity> saved = menuAllocationItemRawMaterRepository.saveAll(entitiesToSave);

		/* %%%%%%%%%%%%%%%%% Captain Receipe %%%%%%%%%%%%%%%%%% */
		List<MenuAllocationItemCaptainReceipeEntity> entitiesToSaveCaptainReceipe = new ArrayList<>();

		for (MenuItemCaptainReceipeEntity entity : captReceipe) {
			MenuAllocationItemRawMaterialResponseDto dto = buildDtoIfNotExist(entity, eventFunction, FORMATTER);
			BigDecimal baseWeight = calculateDefaultWeight(entity, eventFunction, newPax);

			applyCalculation(dto, baseWeight, entity.getUnit(), entity.getRate(), entity);

			Long unitId = dto.getUnitId();
			UnitMasterEntity unitEntity = unitCache.get(unitId);
			if (unitEntity == null) {
				unitEntity = unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(unitId);
				unitCache.put(unitId, unitEntity);
			}

			MenuAllocationItemCaptainReceipeEntity allocationItemCaptainReceipeEntity = new MenuAllocationItemCaptainReceipeEntity();

			allocationItemCaptainReceipeEntity.setRate(dto.getRate());
			allocationItemCaptainReceipeEntity.setWeight(dto.getWeight());
			allocationItemCaptainReceipeEntity.setCaptainReceipeRate(dto.getRawmaterial_rate());
			allocationItemCaptainReceipeEntity.setCaptainReceipeWeight(dto.getRawmaterial_weight());
			allocationItemCaptainReceipeEntity.setDateTime(eventFunction.getFunctionStartDateTime());
			allocationItemCaptainReceipeEntity.setPlace(
					(entity.getVenue() == null || entity.getVenue().trim().isEmpty()) ? "At venue" : entity.getVenue());
			allocationItemCaptainReceipeEntity.setParty(null);
			allocationItemCaptainReceipeEntity.setUnit(unitEntity);
			allocationItemCaptainReceipeEntity.setRawMaterialUnitId(entity.getUnit().getId());
			allocationItemCaptainReceipeEntity.setCaptainReceipe(entity.getCaptainReceipe());
			allocationItemCaptainReceipeEntity.setMenuItem(entity.getMenuItem());
			allocationItemCaptainReceipeEntity.setEvent(eventFunction.getEvent());
			allocationItemCaptainReceipeEntity.setEventFunction(eventFunction);
			allocationItemCaptainReceipeEntity.setIsActive(true);
			allocationItemCaptainReceipeEntity.setIsDelete(false);

			entitiesToSaveCaptainReceipe.add(allocationItemCaptainReceipeEntity);
			response.add(dto);
		}
		System.out.println("entities to save capt receipe size : " + entitiesToSaveCaptainReceipe.size());
		List<MenuAllocationItemCaptainReceipeEntity> savedCaptReceipes = menuAllocationItemCaptainReceipeRepository
				.saveAll(entitiesToSaveCaptainReceipe);

		int index = 0;

		for (MenuAllocationItemRawMaterialEntity entity : saved) {
			response.get(index).setId(entity.getId());
			index++;
		}

		for (MenuAllocationItemCaptainReceipeEntity entity : savedCaptReceipes) {
			response.get(index).setId(entity.getId());
			index++;
		}

		if (isExisting != null && !isExisting.getOutside()) {
			System.out.println("menuItemId:- " + menuItemId);
			List<Long> menuItemIds = Collections.singletonList(menuItemId);

			eventRawMaterialHelperService.deleteEventRawMaterialFunctions(eventFunction.getEvent().getId(),
					eventFunction.getId(), menuItemIds);

			eventRawMaterialHelperService.addEventRawMaterialFunctionsByMenuItemId(eventFunction.getEvent().getId(),
					eventFunction.getId(), menuItemIds);
		}
		return response;
	}

	private MenuAllocationItemRawMaterialResponseDto buildBaseDto(MenuAllocationItemCaptainReceipeEntity entity,
			DateTimeFormatter formatter) {
		MenuAllocationItemRawMaterialResponseDto dto = new MenuAllocationItemRawMaterialResponseDto();
		dto.setId(entity.getId());
		dto.setMenuItemId(entity.getMenuItem().getId());
		dto.setMenuItemName(entity.getMenuItem().getNameEnglish());
		dto.setRawMaterialId(entity.getCaptainReceipe().getId());
		dto.setRawMaterialName(entity.getCaptainReceipe().getName());
		dto.setDateTime(entity.getDateTime().format(formatter));
		dto.setPlace(entity.getPlace());
		dto.setPartyId(entity.getParty() != null ? entity.getParty().getId() : null);
		dto.setPartyName(entity.getParty() != null ? entity.getParty().getNameEnglish() : "");
		dto.setWeight(entity.getWeight());
		dto.setRate(entity.getRate());
		dto.setRawmaterial_rate(entity.getCaptainReceipeRate());
		dto.setRawmaterial_weight(entity.getCaptainReceipeWeight());
		dto.setEventId(entity.getEvent().getId());
		dto.setEventFunctionId(entity.getEventFunction().getId());
		dto.setIsNewRaw(null);
		dto.setSupRate(null);
		dto.setMasterRawUnitId(entity.getRawMaterialUnitId());
		UnitConversionResult conversion = unitConversionService.autoConvert(entity.getUnit(), entity.getWeight());
		applyUnit(dto, conversion.getUnit());
		dto.setIsCaptainReceipe(true);

		return dto;
	}

	private MenuAllocationItemCaptainReceipeRepository findAllByMenuItem_IdAndEventFunctionAndIsDeleteFalse(
			Long menuItemId, EventFunctionMasterEntity eventFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	private MenuAllocationItemRawMaterialResponseDto buildDtoIfNotExist(Object obj,
			EventFunctionMasterEntity eventFunction, DateTimeFormatter formatter) {
		MenuAllocationItemRawMaterialResponseDto dto = new MenuAllocationItemRawMaterialResponseDto();

		if (obj instanceof MenuItemRawMaterialEntity) {

			MenuItemRawMaterialEntity entity = (MenuItemRawMaterialEntity) obj;

			dto.setMenuItemId(entity.getMenuItem().getId());
			dto.setMenuItemName(entity.getMenuItem().getNameEnglish());

			dto.setRawMaterialId(entity.getRawMaterial().getId());
			dto.setRawMaterialName(entity.getRawMaterial().getNameEnglish());

			dto.setRawmaterial_rate(entity.getRate());
			dto.setRawmaterial_weight(entity.getWeight());

			dto.setWeight(entity.getWeight());
			dto.setRate(entity.getRate());

			dto.setIsCaptainReceipe(false);
		} else if (obj instanceof MenuItemCaptainReceipeEntity) {

			MenuItemCaptainReceipeEntity entity = (MenuItemCaptainReceipeEntity) obj;

			dto.setMenuItemId(entity.getMenuItem().getId());
			dto.setMenuItemName(entity.getMenuItem().getNameEnglish());

			dto.setRawMaterialId(entity.getCaptainReceipe().getId());
			dto.setRawMaterialName(entity.getCaptainReceipe().getName());

			dto.setRawmaterial_rate(entity.getRate());
			dto.setRawmaterial_weight(entity.getWeight());

			dto.setWeight(entity.getWeight());
			dto.setRate(entity.getRate());

			dto.setIsCaptainReceipe(true);
		} else {
			throw new RuntimeException("Invalid instance.");
		}
		return dto;
	}

	public static BigDecimal adjustBaseWeight(BigDecimal baseWeight, UnitMasterEntity unit,
			UnitMasterEntity rawMaterialUnit) {
		if (baseWeight == null || unit == null || rawMaterialUnit == null) {
			return baseWeight;
		}

		if (!unit.getId().equals(rawMaterialUnit.getId())) {
			return unit.getIsParentUnit() ? baseWeight.multiply(THOUSAND)
					: baseWeight.divide(THOUSAND, RoundingMode.HALF_UP);
		}

		return baseWeight;
	}

	private BigDecimal calculateUpdatedPaxWeight(MenuAllocationItemRawMaterialEntity entity,
			EventFunctionMasterEntity eventFunction) {
		return safeMultiply(BigDecimal.valueOf(eventFunction.getPax()), entity.getRawMaterialWeight()).divide(HUNDRED,
				3, RoundingMode.HALF_UP);

	}

	private BigDecimal calculateUpdatedWeight(MenuAllocationItemRawMaterialEntity entity,
			EventFunctionMasterEntity eventFunction, Integer newPax) {

		// Compare Integer values, not references
		return safeMultiply(BigDecimal.valueOf(newPax), entity.getRawMaterialWeight()).divide(HUNDRED, 3,
				RoundingMode.HALF_UP);
	}

//	private BigDecimal calculateDefaultWeight(MenuItemRawMaterialEntity entity, EventFunctionMasterEntity eventFunction,
//			Integer newPax) {
//		return safeMultiply(bd(newPax), entity.getWeight()).divide(HUNDRED, 2, RoundingMode.HALF_UP);
//	}

	private BigDecimal calculateDefaultWeight(Object obj, EventFunctionMasterEntity eventFunction, Integer newPax) {

		BigDecimal weight;

		if (obj instanceof MenuItemRawMaterialEntity) {
			MenuItemRawMaterialEntity entity = (MenuItemRawMaterialEntity) obj;
			weight = entity.getWeight();
		} else if (obj instanceof MenuItemCaptainReceipeEntity) {
			MenuItemCaptainReceipeEntity entity = (MenuItemCaptainReceipeEntity) obj;
			weight = entity.getWeight();
		} else {
			throw new RuntimeException("Invalid instance.");
		}

		return safeMultiply(bd(newPax), weight).divide(HUNDRED, 2, RoundingMode.HALF_UP);
	}

	private void applyCalculation(MenuAllocationItemRawMaterialResponseDto dto, BigDecimal weight,
			UnitMasterEntity unit, BigDecimal rate, Object obj) {
		BigDecimal finalWeight = BigDecimal.ZERO;

		if (obj instanceof MenuItemRawMaterialEntity) {
			MenuItemRawMaterialEntity itemRawMaterialEntity = (MenuItemRawMaterialEntity) obj;

			if (itemRawMaterialEntity.getRawMaterial().getIsApplyCal()) {
				finalWeight = roundOffUtility.applyUnitRange(weight, unit, itemRawMaterialEntity);
			} else {
				finalWeight = weight;
			}
		} else if (obj instanceof MenuItemCaptainReceipeEntity) {
			MenuItemCaptainReceipeEntity itemCaptainReceipeEntity = (MenuItemCaptainReceipeEntity) obj;

			finalWeight = roundOffUtility.applyUnitRange(weight, unit, null);
		} else {
			finalWeight = weight;
			throw new RuntimeException("Invalid Instance.");
		}

		UnitConversionResult conversion = unitConversionService.autoConvert(unit, finalWeight);
		applyUnit(dto, conversion.getUnit());
		if (dto.getRawmaterial_weight().compareTo(BigDecimal.ZERO) == 0) {
			dto.setRate(removeDecimal(rate));
		} else {
			rate = safeMultiply(rate, finalWeight).divide(dto.getRawmaterial_weight(), 4, RoundingMode.HALF_UP);
			dto.setRate(removeDecimal(rate));
		}
		dto.setWeight(conversion.getQuantity()
				.setScale(dto.getUnits() != null
						? (dto.getUnits().getDecimalLimit() == null ? 2 : dto.getUnits().getDecimalLimit())
						: 2, RoundingMode.HALF_UP));
	}

	private void applyUnit(MenuAllocationItemRawMaterialResponseDto dto, UnitMasterEntity unit) {
		if (unit == null)
			return;

		dto.setUnitId(unit.getId());
		dto.setUnitName(unit.getNameEnglish());
		dto.setUnits(unitMasterService.getById(unit.getId()));
		dto.setUnitHierarchy(unitMasterService.getParentUnitsWithChildren(unit.getId()));
	}

	private int getScale(UnitMasterEntity unit) {
		return unit == null || unit.getDecimalLimit() == null ? 3 : unit.getDecimalLimit();
	}

	private MenuAllocationItemRawMaterialResponseDto buildBaseDto(MenuAllocationItemRawMaterialEntity entity,
			DateTimeFormatter formatter) {

		MenuAllocationItemRawMaterialResponseDto dto = new MenuAllocationItemRawMaterialResponseDto();
		dto.setId(entity.getId());
		dto.setMenuItemId(entity.getMenuItem().getId());
		dto.setMenuItemName(entity.getMenuItem().getNameEnglish());
		dto.setRawMaterialId(entity.getRawMaterial().getId());
		dto.setRawMaterialName(entity.getRawMaterial().getNameEnglish());
		dto.setDateTime(entity.getDateTime().format(formatter));
		dto.setPlace(entity.getPlace());
		dto.setPartyId(entity.getParty() != null ? entity.getParty().getId() : null);
		dto.setPartyName(entity.getParty() != null ? entity.getParty().getNameEnglish() : "");
		dto.setWeight(entity.getWeight());
		dto.setRate(entity.getRate());
		dto.setRawmaterial_rate(entity.getRawmaterial_rate());
		dto.setRawmaterial_weight(entity.getRawMaterialWeight());
		dto.setEventId(entity.getEvent().getId());
		dto.setEventFunctionId(entity.getEventFunction().getId());
		dto.setIsNewRaw(entity.getIsNewRaw());
		dto.setSupRate(entity.getSupRate());
		dto.setMasterRawUnitId(entity.getMasterRawUnitId());
		dto.setIsCaptainReceipe(false);
		UnitConversionResult conversion = unitConversionService.autoConvert(entity.getUnit(), entity.getWeight());
		applyUnit(dto, conversion.getUnit());
		return dto;
	}

	private BigDecimal bd(Integer pax) {
		return pax == null ? ZERO : BigDecimal.valueOf(pax);
	}

	public static BigDecimal removeDecimal(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value.setScale(0, RoundingMode.DOWN); // removes everything after "."
	}

	@Override
	public Boolean syncRawMaterialItemByEventFunctionId(Long eventFunctionId, Long eventId) {

		List<EventFunctionMasterEntity> eventFunctions;

		if (eventFunctionId == -1) {
			eventFunctions = eventFunctionRepository.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventId);

			if (eventFunctions.isEmpty()) {
				return false;
			}
		} else {
			eventFunctions = eventFunctionRepository.findById(eventFunctionId).map(Collections::singletonList)
					.orElse(Collections.emptyList());

			if (eventFunctions.isEmpty()) {
				return false;
			}
		}

		for (EventFunctionMasterEntity eventFunctionMasterEntity : eventFunctions) {
			menuAllocationItemRawMaterRepository.deleteAllByEventFunction(eventFunctionMasterEntity);
			Long count = menuAllocationItemCaptainReceipeRepository.deleteAllByEventFunction(eventFunctionMasterEntity);
		}
		List<Long> eventrawmaterialid = eventRawMaterialFunctionsRepository
				.findDistinctEventRawMaterialIdsByEventIdAndEventFunctionId(eventId, eventFunctionId);
		Set<Long> setEventRawMaterialId = new HashSet<>(eventrawmaterialid);
		int deleted = eventRawMaterialFunctionsRepository.deleteByEventIdAndEventFunctionId(eventId, eventFunctionId);
		System.out.println("Deleted :- " + deleted);
		eventRawMaterialHelperService.updateQtyFromFunctions(setEventRawMaterialId);

		return true;
	}

	@Override
	public Boolean removeAgencyByEventFunctionId(Long eventFunctionId, Long userId) {

		Integer affectedRaws = menuAllocationItemRawMaterRepository
				.removeAgencyByEventFunctionIdAndUserId(eventFunctionId, userId);

		return affectedRaws > 0;
	}

	@Override
	public List<MenuAllocationAgencyWithItemsResponseDto> getAgencyWithItemType(String type, Long eventId,
			Long eventFunctionId, String startDate, String endDate) {

		List<MenuAllocationAgencyWithItemsResponseDto> responseList = new ArrayList<>();

		LocalDateTime stDate = null, edDate = null;
		if (startDate != null && endDate != null) {
			stDate = commonService.dateTimeFormatted(startDate, false);
			edDate = commonService.dateTimeFormatted(endDate, true);
		}

		// 1️⃣ Fetch Event Functions
		List<EventFunctionMasterResponseDto> eventFunctions;
		List<Long> allEventsIds = null;
		if (startDate != null && endDate != null) {
			allEventsIds = eventMasterService.findEventsByEventDate(stDate, edDate);
		}

		if (eventId != null && eventId == -1) {
			eventFunctions = eventFunctionMasterService.getAllEventFunctionByEventId(allEventsIds);
		} else if (eventFunctionId != null && eventFunctionId == -1) {
			eventFunctions = eventFunctionMasterService.getAllEventFunctionByEventId(eventId);
		} else {
			eventFunctions = eventFunctionMasterService.getEventFunctionById(eventFunctionId);
		}

		// 2️⃣ Loop Event Functions
		for (EventFunctionMasterResponseDto eventFunction : eventFunctions) {

			List<MenuAllocationOrdersEntity> orders = menuAllocationRepository.fetchAllAllocationData(type, eventId,
					eventFunction.getId(), stDate, edDate, null, null, false, false);

			// 3️⃣ Skip if no data for this EventFunction
			if (orders == null || orders.isEmpty()) {
				continue;
			}

			MenuAllocationAgencyWithItemsResponseDto root = new MenuAllocationAgencyWithItemsResponseDto();

			root.setEventFunction(eventFunction);

			// 4️⃣ Group by Party
			Map<Long, List<MenuAllocationOrdersEntity>> groupedByParty = orders.stream()
					.collect(Collectors.groupingBy(o -> o.getParty().getId()));

			List<MenuAllocationAgencyResponseDto> agencyList = new ArrayList<>();

			for (Map.Entry<Long, List<MenuAllocationOrdersEntity>> entry : groupedByParty.entrySet()) {

				PartyMasterEntity party = entry.getValue().get(0).getParty();

				MenuAllocationAgencyResponseDto agencyDto = new MenuAllocationAgencyResponseDto();

				agencyDto.setContactId(party.getId());
				agencyDto.setContactName(party.getNameEnglish());
				agencyDto.setNumber(party.getMobileno());
				agencyDto.setType(type);

				List<MenuAllocationItemsResponseDto> items = new ArrayList<>();

				BigDecimal totalPrice = BigDecimal.ZERO;
				BigDecimal totalShiftTransPrice = BigDecimal.ZERO;
				int totalPax = 0;
				int totalQty = 0;
				int totalCounterQty = 0;
				int totalCounterPrice = 0;
				int totalHelperQty = 0;
				int totalHelperPrice = 0;

				for (MenuAllocationOrdersEntity o : entry.getValue()) {

					MenuAllocationItemsResponseDto item = new MenuAllocationItemsResponseDto();

					item.setItemId(o.getMenuAllocation().getMenuItem().getId());
					item.setItemName(o.getMenuAllocation().getMenuItem().getNameEnglish());
					item.setItemNameHindi(o.getMenuAllocation().getMenuItem().getNameHindi());
					item.setItemNameGujarati(o.getMenuAllocation().getMenuItem().getNameGujarati());
					item.setPax(o.getMenuAllocation().getPersonCount());
					item.setRemarks(o.getMenuAllocation().getInstructions());
					item.setServiceType(o.getServiceType());
					item.setNotes(o.getRemarks());

					// Outside
					item.setUnitId(o.getUnit() != null ? o.getUnit().getId() : null);
					item.setUnitName(o.getUnit() != null ? o.getUnit().getNameEnglish() : null);
					item.setUnitNameHindi(o.getUnit() != null ? o.getUnit().getNameHindi() : null);
					item.setUnitNameGujarati(o.getUnit() != null ? o.getUnit().getNameGujarati() : null);
					item.setQty(o.getQuantity() != null ? o.getQuantity().intValue() : 0);
					item.setPrice(o.getTotalPrice() != null ? o.getTotalPrice().intValue() : 0);

					// Chef Labour
					item.setCounterQty(o.getCounterQuantity());
					item.setCounterPrice(o.getCounterPrice() != null ? o.getCounterPrice().intValue() : 0);
					item.setHelperQty(o.getHelperQuantity());
					item.setHelperPrice(o.getHelperPrice() != null ? o.getHelperPrice().intValue() : 0);

					items.add(item);

					// Totals
					if (o.getTotalPrice() != null)
						totalPrice = totalPrice.add(o.getTotalPrice());

					if (o.getShiftTransPrice() != null)
						totalShiftTransPrice = totalShiftTransPrice.add(o.getShiftTransPrice());

					if (o.getMenuAllocation().getPersonCount() != null)
						totalPax = Math.max(totalPax, o.getMenuAllocation().getPersonCount());

					if (o.getQuantity() != null)
						totalQty += o.getQuantity().intValue();

					if (o.getCounterQuantity() != null)
						totalCounterQty += o.getCounterQuantity();

					if (o.getCounterPrice() != null)
						totalCounterPrice += o.getCounterPrice().intValue();

					if (o.getHelperQuantity() != null)
						totalHelperQty += o.getHelperQuantity();

					if (o.getHelperPrice() != null)
						totalHelperPrice += o.getHelperPrice().intValue();
				}

				agencyDto.setAllocationItems(items);
				agencyDto.setTotalPrice(totalPrice);
				agencyDto.setTotalShiftTransPrice(totalShiftTransPrice);
				agencyDto.setTotalPax(totalPax);
				agencyDto.setTotalQty(totalQty);
				agencyDto.setTotalCounterQty(totalCounterQty);
				agencyDto.setTotalCounterPrice(totalCounterPrice);
				agencyDto.setTotalHelperQty(totalHelperQty);
				agencyDto.setTotalHeplerPrice(totalHelperPrice);

				agencyList.add(agencyDto);
			}

			root.setAgencyResponse(agencyList);
			responseList.add(root);
		}

		// 5️⃣ Return EventFunction-wise list
		return responseList;
	}

	@Override
	public List<MenuAllocationAgencyWithItemsResponseDto> getAgencyWithItemType(String type, Long eventId,
			Long eventFunctionId, List<Long> agencyId, List<Long> itemId, String startDate, String endDate) {
		List<MenuAllocationAgencyWithItemsResponseDto> responseList = new ArrayList<>();

		LocalDateTime stDate = null, edDate = null;
		if (startDate != null && endDate != null) {
			stDate = commonService.dateTimeFormatted(startDate, false);
			edDate = commonService.dateTimeFormatted(endDate, true);
		}

		// 1️⃣ Fetch Event Functions
		List<EventFunctionMasterResponseDto> eventFunctions;
		List<Long> allEventsIds = null;
		if (startDate != null && endDate != null) {
			allEventsIds = eventMasterService.findEventsByEventDate(stDate, edDate);
		}
		if (eventId != null && eventId == -1) {
			eventFunctions = eventFunctionMasterService.getAllEventFunctionByEventId(allEventsIds);
		} else if (eventFunctionId != null && eventFunctionId == -1) {
			eventFunctions = eventFunctionMasterService.getAllEventFunctionByEventId(eventId);
		} else {
			eventFunctions = eventFunctionMasterService.getEventFunctionById(eventFunctionId);
		}

		Boolean isAgencyWise = true, isItemWise = true;
		if (agencyId == null || agencyId.isEmpty()) {
			isAgencyWise = false;
		}
		if (itemId == null || itemId.isEmpty()) {
			isItemWise = false;
		}

		// 2️⃣ Loop Event Functions
		for (EventFunctionMasterResponseDto eventFunction : eventFunctions) {
			List<MenuAllocationOrdersEntity> orders = menuAllocationRepository.fetchAllAllocationData(type, eventId,
					eventFunction.getId(), stDate, edDate, agencyId, itemId, isAgencyWise, isItemWise);
			// 3️⃣ Skip if no data for this EventFunction
			if (orders == null || orders.isEmpty()) {
				continue;
			}
			MenuAllocationAgencyWithItemsResponseDto root = new MenuAllocationAgencyWithItemsResponseDto();

			root.setEventFunction(eventFunction);

			// 4️⃣ Group by Party
			Map<Long, List<MenuAllocationOrdersEntity>> groupedByParty = orders.stream()
					.collect(Collectors.groupingBy(o -> o.getParty().getId()));
			List<MenuAllocationAgencyResponseDto> agencyList = new ArrayList<>();

			for (Map.Entry<Long, List<MenuAllocationOrdersEntity>> entry : groupedByParty.entrySet()) {
				PartyMasterEntity party = entry.getValue().get(0).getParty();

				MenuAllocationAgencyResponseDto agencyDto = new MenuAllocationAgencyResponseDto();

				agencyDto.setContactId(party.getId());
				agencyDto.setContactName(party.getNameEnglish());
				agencyDto.setContactNameGujarati(party.getNameGujarati());
				agencyDto.setContactNameHindi(party.getNameHindi());
				agencyDto.setNumber(party.getMobileno());
				agencyDto.setType(type);

				List<MenuAllocationItemsResponseDto> items = new ArrayList<>();

				BigDecimal totalPrice = BigDecimal.ZERO;
				BigDecimal totalShiftTransPrice = BigDecimal.ZERO;
				int totalPax = 0;
				int totalQty = 0;
				int totalCounterQty = 0;
				int totalCounterPrice = 0;
				int totalHelperQty = 0;
				int totalHelperPrice = 0;

				for (MenuAllocationOrdersEntity o : entry.getValue()) {
					MenuAllocationItemsResponseDto item = new MenuAllocationItemsResponseDto();

					item.setItemId(o.getMenuAllocation().getMenuItem().getId());
					item.setItemName(o.getMenuAllocation().getMenuItem().getNameEnglish());
					item.setItemNameHindi(o.getMenuAllocation().getMenuItem().getNameHindi());
					item.setItemNameGujarati(o.getMenuAllocation().getMenuItem().getNameGujarati());
					item.setPax(o.getMenuAllocation().getPersonCount());
					item.setRemarks(o.getMenuAllocation().getInstructions());
					item.setRemarksHindi(o.getMenuAllocation().getInstructionsHindi());
					item.setRemarksGujarati(o.getMenuAllocation().getInstructionsGujarati());
					item.setServiceType(o.getServiceType());
					item.setNotes(o.getRemarks());
					item.setCategoryId(o.getMenuAllocation().getMenuCategory().getId());
					item.setCategoryName(o.getMenuAllocation().getMenuCategory().getNameEnglish());
					item.setCategoryNameGujarati(o.getMenuAllocation().getMenuCategory().getNameGujarati());
					item.setCategoryNameHindi(o.getMenuAllocation().getMenuCategory().getNameHindi());
					// Outside
					item.setUnitId(o.getUnit() != null ? o.getUnit().getId() : null);
					item.setUnitName(o.getUnit() != null ? o.getUnit().getNameEnglish() : null);
					item.setUnitNameHindi(o.getUnit() != null ? o.getUnit().getNameHindi() : null);
					item.setUnitNameGujarati(o.getUnit() != null ? o.getUnit().getNameGujarati() : null);
					item.setQty(o.getQuantity() != null ? o.getQuantity().intValue() : 0);
					item.setPrice(o.getPrice() != null ? o.getPrice().intValue() : 0);

					// Chef Labour
					item.setCounterQty(o.getCounterQuantity());
					item.setCounterPrice(o.getCounterPrice() != null ? o.getCounterPrice().intValue() : 0);
					item.setHelperQty(o.getHelperQuantity());
					item.setHelperPrice(o.getHelperPrice() != null ? o.getHelperPrice().intValue() : 0);

					// inside
					item.setNumber(o.getNumber() != null ? o.getNumber() : "");
					item.setPlace(o.getMenuAllocation().getPlace());

					items.add(item);

					// Totals
					if (o.getTotalPrice() != null)
						totalPrice = totalPrice.add(o.getTotalPrice());

					if (o.getShiftTransPrice() != null)
						totalShiftTransPrice = totalShiftTransPrice.add(o.getShiftTransPrice());

					if (o.getMenuAllocation().getPersonCount() != null)
						totalPax = Math.max(totalPax, o.getMenuAllocation().getPersonCount());

					if (o.getQuantity() != null)
						totalQty += o.getQuantity().intValue();

					if (o.getCounterQuantity() != null)
						totalCounterQty += o.getCounterQuantity();

					if (o.getCounterPrice() != null)
						totalCounterPrice += o.getCounterPrice().intValue();

					if (o.getHelperQuantity() != null)
						totalHelperQty += o.getHelperQuantity();

					if (o.getHelperPrice() != null)
						totalHelperPrice += o.getHelperPrice().intValue();
				}

				agencyDto.setAllocationItems(items);
				agencyDto.setTotalPrice(totalPrice);
				agencyDto.setTotalShiftTransPrice(totalShiftTransPrice);
				agencyDto.setTotalPax(totalPax);
				agencyDto.setTotalQty(totalQty);
				agencyDto.setTotalCounterQty(totalCounterQty);
				agencyDto.setTotalCounterPrice(totalCounterPrice);
				agencyDto.setTotalHelperQty(totalHelperQty);
				agencyDto.setTotalHeplerPrice(totalHelperPrice);

				agencyList.add(agencyDto);
			}

			root.setAgencyResponse(agencyList);
			responseList.add(root);
		}

		// 5️⃣ Return EventFunction-wise list
		return responseList;
	}

	@Override
	public List<MenuAllocationAgencyWithItemsResponseDto> getAgencyWithItemType(String type, Long eventId,
			Long eventFunctionId, Long partyId) {

		List<MenuAllocationAgencyWithItemsResponseDto> responseList = new ArrayList<>();

		// 1️⃣ Fetch Event Functions
		List<EventFunctionMasterResponseDto> eventFunctions;

		if (eventFunctionId != null && eventFunctionId == -1) {
			eventFunctions = eventFunctionMasterService.getAllEventFunctionByEventId(eventId);
		} else {
			eventFunctions = eventFunctionMasterService.getEventFunctionById(eventFunctionId);
		}

		// 2️⃣ Loop Event Functions
		for (EventFunctionMasterResponseDto eventFunction : eventFunctions) {

			List<MenuAllocationOrdersEntity> orders = menuAllocationRepository.fetchAllAllocationData(type, eventId,
					eventFunction.getId());

			// 3️⃣ Skip if no data for this EventFunction
			if (orders == null || orders.isEmpty()) {
				continue;
			}

			MenuAllocationAgencyWithItemsResponseDto root = new MenuAllocationAgencyWithItemsResponseDto();

			root.setEventFunction(eventFunction);

			// 4️⃣ Group by Party
			Map<Long, List<MenuAllocationOrdersEntity>> groupedByParty = orders.stream()
					.collect(Collectors.groupingBy(o -> o.getParty().getId()));

			List<MenuAllocationAgencyResponseDto> agencyList = new ArrayList<>();

			for (Map.Entry<Long, List<MenuAllocationOrdersEntity>> entry : groupedByParty.entrySet()) {

				PartyMasterEntity party = entry.getValue().get(0).getParty();
				if (partyId == -1 || partyId == 0 || party.getId() == partyId) {
					MenuAllocationAgencyResponseDto agencyDto = new MenuAllocationAgencyResponseDto();

					agencyDto.setContactId(party.getId());
					agencyDto.setContactName(party.getNameEnglish());
					agencyDto.setNumber(party.getMobileno());
					agencyDto.setType(type);

					List<MenuAllocationItemsResponseDto> items = new ArrayList<>();

					BigDecimal totalPrice = BigDecimal.ZERO;
					BigDecimal totalShiftTransPrice = BigDecimal.ZERO;
					int totalPax = 0;
					int totalQty = 0;
					int totalCounterQty = 0;
					int totalCounterPrice = 0;
					int totalHelperQty = 0;
					int totalHelperPrice = 0;

					for (MenuAllocationOrdersEntity o : entry.getValue()) {

						MenuAllocationItemsResponseDto item = new MenuAllocationItemsResponseDto();

						item.setItemId(o.getMenuAllocation().getMenuItem().getId());
						item.setItemName(o.getMenuAllocation().getMenuItem().getNameEnglish());
						item.setItemNameHindi(o.getMenuAllocation().getMenuItem().getNameHindi());
						item.setItemNameGujarati(o.getMenuAllocation().getMenuItem().getNameGujarati());
						item.setPax(o.getMenuAllocation().getPersonCount());
						item.setRemarks(o.getMenuAllocation().getInstructions());
						item.setServiceType(o.getServiceType());
						item.setNotes(o.getRemarks());

						// Outside
						item.setUnitId(o.getUnit() != null ? o.getUnit().getId() : null);
						item.setUnitName(o.getUnit() != null ? o.getUnit().getNameEnglish() : null);
						item.setUnitNameHindi(o.getUnit() != null ? o.getUnit().getNameHindi() : null);
						item.setUnitNameGujarati(o.getUnit() != null ? o.getUnit().getNameGujarati() : null);
						item.setQty(o.getQuantity() != null ? o.getQuantity().intValue() : 0);
						item.setPrice(o.getTotalPrice() != null ? o.getTotalPrice().intValue() : 0);

						// Chef Labour
						item.setCounterQty(o.getCounterQuantity());
						item.setCounterPrice(o.getCounterPrice() != null ? o.getCounterPrice().intValue() : 0);
						item.setHelperQty(o.getHelperQuantity());
						item.setHelperPrice(o.getHelperPrice() != null ? o.getHelperPrice().intValue() : 0);

						items.add(item);

						// Totals
						if (o.getTotalPrice() != null)
							totalPrice = totalPrice.add(o.getTotalPrice());

						if (o.getShiftTransPrice() != null)
							totalShiftTransPrice = totalShiftTransPrice.add(o.getShiftTransPrice());

						if (o.getMenuAllocation().getPersonCount() != null)
							totalPax = Math.max(totalPax, o.getMenuAllocation().getPersonCount());

						if (o.getQuantity() != null)
							totalQty += o.getQuantity().intValue();

						if (o.getCounterQuantity() != null)
							totalCounterQty += o.getCounterQuantity();

						if (o.getCounterPrice() != null)
							totalCounterPrice += o.getCounterPrice().intValue();

						if (o.getHelperQuantity() != null)
							totalHelperQty += o.getHelperQuantity();

						if (o.getHelperPrice() != null)
							totalHelperPrice += o.getHelperPrice().intValue();

						agencyDto.setRemarks(o.getRemarks());
					}

					agencyDto.setAllocationItems(items);
					agencyDto.setTotalPrice(totalPrice);
					agencyDto.setTotalShiftTransPrice(totalShiftTransPrice);
					agencyDto.setTotalPax(totalPax);
					agencyDto.setTotalQty(totalQty);
					agencyDto.setTotalCounterQty(totalCounterQty);
					agencyDto.setTotalCounterPrice(totalCounterPrice);
					agencyDto.setTotalHelperQty(totalHelperQty);
					agencyDto.setTotalHeplerPrice(totalHelperPrice);

					agencyList.add(agencyDto);
				}
			}

			root.setAgencyResponse(agencyList);
			responseList.add(root);
		}

		// 5️⃣ Return EventFunction-wise list
		return responseList;
	}

	public List<AgencyDataForDatewiseReportResponseDto> buildDtoForDateWiseReport(Long userId, String startDate,
			String endDate, String type, List<Long> agencyIds, Long partyId) {

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate startLocalDate = LocalDate.parse(startDate, inputFormatter);
		LocalDate endLocalDate = LocalDate.parse(endDate, inputFormatter);

		LocalDateTime startDateTime = startLocalDate.atStartOfDay();
		LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

		Integer flag = 0;
		if (agencyIds != null && !agencyIds.isEmpty()) {
			flag = 1;
		}
		List<Object[]> data = menuAllocationRepository.getDataForDatewiseReport(userId, startDateTime, endDateTime,
				type, agencyIds, flag, partyId);

		Map<String, AgencyDataForDatewiseReportResponseDto> agencyMap = new LinkedHashMap<>();

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

		for (Object[] row : data) {

			// 🔹 Agency Data
			Long agencyId = ((Number) row[0]).longValue();
			String nameEng = (String) row[1];
			String nameHindi = (String) row[2];
			String nameGuj = (String) row[3];
			String contactNo = (String) row[17];
			// 🔹 Function Data
			LocalDateTime fnDateTime = ((Timestamp) row[4]).toLocalDateTime();
			String formattedDate = fnDateTime.format(dateFormatter);
			String formattedTime = fnDateTime.format(timeFormatter);
			Long eventFunctionId = ((Number) row[26]).longValue();
			Long eventId = ((Number) row[27]).longValue();
			List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
					.findBanquetByEventFunctionId(eventId, eventFunctionId);

			final String venue = !banquets.isEmpty() ? banquets.stream()
					.map(BanquetHallShiftInfoDto::getBanquetHallName).collect(Collectors.joining(", "))
					: (String) row[5];

			// 🔹 Order Data
			Integer counterQty = row[6] != null ? ((Number) row[6]).intValue() : 0;
			Integer helperQty = row[7] != null ? ((Number) row[7]).intValue() : 0;
			Integer counterPrice = row[8] != null ? ((Number) row[8]).intValue() : 0;
			Integer helperPrice = row[9] != null ? ((Number) row[9]).intValue() : 0;

			String serviceType = (String) row[10];
			Integer quantity = row[11] != null ? ((Number) row[11]).intValue() : 0;
			Integer price = row[12] != null ? ((Number) row[12]).intValue() : 0;

			String itemEng = (String) row[13];
			String itemHindi = (String) row[14];
			String itemGuj = (String) row[15];

			String totalPrice = row[16] != null ? row[16].toString() : "0";

			Long unitId = row[18] != null ? ((Number) row[18]).longValue() : 0L;
			String unitNameEnglish = row[19] != null ? row[19].toString() : "";
			String unitNameHindi = row[20] != null ? row[20].toString() : "";
			String unitNameGujarati = row[21] != null ? row[21].toString() : "";

			Integer personCount = row[25] != null ? ((Number) row[25]).intValue() : 0;

			String agencyKey = nameEng;

			// 🔹 Agency Level
			AgencyDataForDatewiseReportResponseDto agency = agencyMap.computeIfAbsent(agencyKey, k -> {
				AgencyDataForDatewiseReportResponseDto dto = new AgencyDataForDatewiseReportResponseDto();
				dto.setNameEnglish(nameEng);
				dto.setNameHindi(nameHindi);
				dto.setNameGujarati(nameGuj);
				dto.setContactNo(contactNo);
				dto.setFunctions(new ArrayList<>());
				return dto;
			});

			// 🔹 Function Level (Date + Venue grouping)
			Optional<FnDetailsForDatewiseReportResponseDto> fnOptional = agency.getFunctions().stream().filter(
					f -> Objects.equals(f.getFnStartDate(), formattedDate) && Objects.equals(f.getVenue(), venue))
					.findFirst();

			FnDetailsForDatewiseReportResponseDto fnDto;

			if (fnOptional.isPresent()) {
				fnDto = fnOptional.get();
			} else {
				fnDto = new FnDetailsForDatewiseReportResponseDto();
				fnDto.setFnStartDate(formattedDate);
				fnDto.setVenue(venue);
				fnDto.setMenuItems(new ArrayList<>());
				agency.getFunctions().add(fnDto);
			}

			// 🔹 Menu Item Level
			MenuItemForDatewiseReportResponseDto menuItem = new MenuItemForDatewiseReportResponseDto();

			menuItem.setItemNameEnglish(itemEng);
			menuItem.setItemNameHindi(itemHindi);
			menuItem.setItemNameGujarati(itemGuj);

			menuItem.setDateTime(formattedTime);
			menuItem.setQty(quantity);
			menuItem.setRate(price);
			menuItem.setServiceType(serviceType);
			menuItem.setTotalPrice(totalPrice);

			menuItem.setCounterQty(counterQty);
			menuItem.setCounterPrice(counterPrice);
			menuItem.setHelperQty(helperQty);
			menuItem.setHelperPrice(helperPrice);

			menuItem.setUnitId(unitId);
			menuItem.setUnitNameEnglish(unitNameEnglish);
			menuItem.setUnitNameHindi(unitNameHindi);
			menuItem.setUnitNameGujarati(unitNameGujarati);

			menuItem.setPersonCount(personCount);

			fnDto.getMenuItems().add(menuItem);
		}

		return new ArrayList<>(agencyMap.values());
	}

	@Override
	public String generateOutsideAgencyReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang,
			Long userid) {

		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String phone, eDate, eVenue, agencyName, menuItemLabel, functionNameLabel, functionPaxLabel, fNote;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansDevanagari-Regular.ttf");

				System.out.println("Hindi font loaded successfully");
				y = 512;
				phone = "मोबाइल नंबर";
				eDate = "दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				menuItemLabel = "मेनू आइटम";
				functionNameLabel = "कार्यक्रम";
				functionPaxLabel = "मेम्बर्स";
				fNote = "भोजन व्यवस्था";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 510;
				phone = "મોબાઇલ નંબર";
				eDate = "તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				menuItemLabel = "મેનુ આઇટમ";
				functionNameLabel = "કાર્યક્રમ";
				functionPaxLabel = "વ્યક્તિ";
				fNote = "ભોજન વ્યવસ્થા";

			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				menuItemLabel = "Menu Item Name";
				functionNameLabel = "Function";
				functionPaxLabel = "Pax";
				fNote = "Food Preference";
			}
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			PdfFont basicFont3 = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponselist = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, null, null);

			if (menuAgencyWithItemsResponselist.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			ChefAndOutsideChitthiResponseDto response = convertToChefOutsideResponse(menuAgencyWithItemsResponselist);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			StringBuilder foodPrefName;
			String fNotes = "";
			if (lang == 1) {

				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameHindi() : "");
				fNotes = eventMasterEntity.getMeal_notes_hindi();
			} else if (lang == 2) {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameGujarati()
								: "");
				fNotes = eventMasterEntity.getMeal_notes_gujarati();
			} else {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameEnglish()
								: "");
				fNotes = eventMasterEntity.getMeal_notes();
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside chitthi")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 50, 50, 50);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color labelBgColor = new DeviceRgb(230, 234, 237);
			Color mainColor = new DeviceRgb(0, 0, 0);
			Color secondBgColor;
			Cell cell;

			boolean isFirst = true;
			for (AgencyResponseDto agency : response.getAgencyDetails()) {
				Table headerTable = null;
				if (!isFirst) {
					addSignatureAtBottom(document, basicFont2);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirst = false;
				headerTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setFixedLayout();

				if (isCompanyDetails == 1) {
					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//							.loadImageFromResource("/flipbook/pages/logo.png");
					Image logo = new Image(logoData);

					// Resize & align
					logo.setWidth(100f);
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

					cell = new Cell(3, 2).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setBorder(Border.NO_BORDER).setPaddingBottom(10f);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).setFontSize(14)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER).setPaddingLeft(15f);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph().add(new Text(phone + " : ")
									.setFont(basicFont).setFontSize(lang == 0 ? 14 : 16).setFontColor(mainColor)
									.simulateBold())
									.add(new Text(cmpDto.getOfficeNo() != null
											? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
											: "").setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
							.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f)
							.setPaddingLeft(lang == 0 ? 14f : 16f);
					headerTable.addCell(cell);

					headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				}

				cell = new Cell().add(new Paragraph(agencyName).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
						.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)
						.setFixedLeading(lang == 0 ? 14f : 16f).simulateBold()).setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
								.setTextAlignment(TextAlignment.LEFT).setFixedLeading(14f).simulateBold())
						.setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				String party;
				if (lang == 1) {
					party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";
				} else if (lang == 2) {
					party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";
				} else {
					party = agency.getContactName() != null ? agency.getContactName() : "";
				}
				cell = new Cell()
						.add(new Paragraph(party).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
								.setFontColor(mainColor).setFixedLeading(14f).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				document.add(headerTable);

				for (FunctionDetailsResponseDto function : agency.getFunctionDetails()) {

					EventFunctionMasterResponseDto fn = function.getEventFunction();

					String fnName;

					if (lang == 1) {

						fnName = fn.getFunction().getNameHindi() != null ? fn.getFunction().getNameHindi() : "";

					} else if (lang == 2) {

						fnName = fn.getFunction().getNameGujarati() != null ? fn.getFunction().getNameGujarati() : "";

					} else {

						fnName = fn.getFunction().getNameEnglish() != null ? fn.getFunction().getNameEnglish() : "";
					}

					Map<String, List<MenuAllocationItemsResponseDto>> placeWiseMap = function.getAllocationItems()
							.stream()
							.collect(Collectors
									.groupingBy(item -> item.getPlace() != null && !item.getPlace().trim().isEmpty()
											? item.getPlace()
											: "-", LinkedHashMap::new, Collectors.toList()));

					Table functionDetailTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }),
							true);

					functionDetailTable.setWidth(UnitValue.createPercentValue(100));

					functionDetailTable.setFixedLayout();

					cell = new Cell()
							.add(new Paragraph(eDate).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					String date = fn.getFunctionStartDateTime().split(" ")[0];

					String time = fn.getFunctionStartDateTime().split(" ")[1] + " "
							+ fn.getFunctionStartDateTime().split(" ")[2];

					Paragraph datePara = new Paragraph().add(new Text(date).setUnderline())
							.add(new Text(" on due ").simulateBold()).add(new Text(time).setUnderline())
							.setFont(basicFont3).setFontSize(14);

					functionDetailTable.addCell(new Cell().add(datePara).setBorder(Border.NO_BORDER));

					cell = new Cell()
							.add(new Paragraph(functionNameLabel).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(fnName).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					// FoodPreference NAME
					cell = new Cell()
							.add(new Paragraph(fNote).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(
							foodPrefName.toString() + (fNotes.trim().isEmpty() ? "" : " (" + fNotes + ")"))
							.setFont(basicFont).setFontSize(lang == 0 ? 14 : 16).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					document.add(functionDetailTable);

					for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> placeEntry : placeWiseMap.entrySet()) {

						String placeName = placeEntry.getKey();

						List<MenuAllocationItemsResponseDto> placeItems = placeEntry.getValue();

						if (placeName.equalsIgnoreCase("venue") || placeName.equalsIgnoreCase("at venue")
								|| placeName.equalsIgnoreCase("0")) {

							List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
									.findBanquetByEventFunctionId(eventId,
											fn.getId());
							if (!banquets.isEmpty()) {
								placeName = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
										.collect(Collectors.joining(", "));
							} else {
								placeName = lang == 1 ? fn.getFunction_venue_hindi()
										: lang == 2 ? fn.getFunction_venue_gujarati() : fn.getFunction_venue();
							}

						} else {

							try {

								UserGodownEntity userGodownEntity = userGodownRepository
										.findByIdAndIsDeleteFalse(Long.valueOf(placeName)).orElse(null);

								if (userGodownEntity == null) {

									placeName = "";

								} else if (lang == 0) {

									placeName = userGodownEntity.getAddressEnglish();

								} else if (lang == 1) {

									placeName = userGodownEntity.getAddressHindi();

								} else {

									placeName = userGodownEntity.getAddressGujarati();
								}

							} catch (Exception e) {

								placeName = "";
							}
						}

						Paragraph venuePara = new Paragraph()
								.add(new Text(eVenue + " : ").setFont(basicFont).setFontColor(mainColor).simulateBold())
								.add(new Text(placeName != null ? placeName : "").setFont(basicFont)
										.setFontColor(mainColor))
								.setFontSize(lang == 0 ? 14 : 16).setMarginTop(10f);

						document.add(venuePara);

						Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }), false);

						itemTable.setWidth(UnitValue.createPercentValue(100));

						itemTable.setFixedLayout();

						itemTable.setHorizontalAlignment(HorizontalAlignment.LEFT);

						itemTable.setMarginLeft(0);

						itemTable.setMarginBottom(15f);

						cell = new Cell()
								.add(new Paragraph(menuItemLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
								.setBackgroundColor(labelBgColor).setBorder(Border.NO_BORDER);

						itemTable.addCell(cell);

						boolean isEvenRaw = false;

						Map<String, List<MenuAllocationItemsResponseDto>> categoryWiseItems = new LinkedHashMap<>();

						for (MenuAllocationItemsResponseDto item : placeItems) {

							String categoryName;

							if (lang == 1) {
								categoryName = item.getCategoryNameHindi();
							} else if (lang == 2) {
								categoryName = item.getCategoryNameGujarati();
							} else {
								categoryName = item.getCategoryName();
							}

							if (categoryName == null || categoryName.trim().isEmpty()) {
								categoryName = "Other";
							}

							if (!categoryWiseItems.containsKey(categoryName)) {
								categoryWiseItems.put(categoryName, new ArrayList<MenuAllocationItemsResponseDto>());
							}

							categoryWiseItems.get(categoryName).add(item);
						}

						boolean firstCategory = true;

						for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> entry : categoryWiseItems
								.entrySet()) {

							// -------- Gap before category --------
							if (!firstCategory) {
								Cell gapCell = new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER)
										.setBackgroundColor(ColorConstants.WHITE).setPaddingTop(8).setPaddingBottom(8);

								itemTable.addCell(gapCell);
							}

							firstCategory = false;

							// -------- Category Header --------
							Cell categoryCell = new Cell()
									.add(new Paragraph(entry.getKey()).simulateBold().setFontColor(ColorConstants.BLACK)
											.setUnderline().setTextAlignment(TextAlignment.LEFT))
									.setBorder(Border.NO_BORDER).setPaddingTop(5).setPaddingBottom(5).setPaddingLeft(8);

							categoryCell.setFontSize(lang == 0 ? 14 : 16);

							itemTable.addCell(categoryCell);

							// -------- Category Items --------
							for (MenuAllocationItemsResponseDto item : entry.getValue()) {

								secondBgColor = isEvenRaw ? new DeviceRgb(245, 245, 245) : new DeviceRgb(255, 255, 255);

								String itemName = "";
								String unit = "";
								String remarks = "";
								if (lang == 1) {
									itemName = item.getItemNameHindi();
									unit = item.getUnitNameHindi() == null ? "" : item.getUnitNameHindi();
									remarks = item.getRemarksHindi() != null ? item.getRemarksHindi() : "";
								} else if (lang == 2) {
									itemName = item.getItemNameGujarati();
									unit = item.getUnitNameGujarati() == null ? "" : item.getUnitNameGujarati();
									remarks = item.getRemarksGujarati() != null ? item.getRemarksGujarati() : "";
								} else {
									itemName = item.getItemName();
									unit = item.getUnitName() == null ? "" : item.getUnitName();
									remarks = item.getRemarks() != null ? item.getRemarks() : "";
								}

								String qtyText = "";

								if (item.getQty() != null && item.getQty() != 0) {
									qtyText = " ( " + item.getQty() + " " + unit + " )";
								}

								String notes = " (" + remarks + ")";

								cell = new Cell()
										.add(new Paragraph((itemName != null ? itemName : "") + qtyText + notes)
												.setFont(basicFont).setFontColor(mainColor)
												.setTextAlignment(TextAlignment.LEFT))
										.setBackgroundColor(secondBgColor).setBorder(Border.NO_BORDER)
										.setPaddingLeft(12).setPaddingTop(3).setPaddingBottom(3);

								cell.setFontSize(lang == 0 ? 14 : 16);

								itemTable.addCell(cell);

								isEvenRaw = !isEvenRaw;
							}
						}

						document.add(itemTable);
					}
				}
			}
			addSignatureAtBottom(document, basicFont2);
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ eventMasterEntity.getEventNo() + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside chitthi")
					+ ".pdf";
			return fullUrl;

		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}

	}

	@Override
	public String generateInsideAgencyChithhiReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang,
			Long userid) {

		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String phone, eDate, eVenue, agencyName, menuItemLabel, functionNameLabel, functionPaxLabel, fNote;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansDevanagari-Regular.ttf");

				System.out.println("Hindi font loaded successfully");
				y = 512;
				phone = "मोबाइल नंबर";
				eDate = "दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				menuItemLabel = "मेनू आइटम";
				functionNameLabel = "कार्यक्रम";
				functionPaxLabel = "मेम्बर्स";
				fNote = "भोजन व्यवस्था";

			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 510;
				phone = "મોબાઇલ નંબર";
				eDate = "તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				menuItemLabel = "મેનુ આઇટમ";
				functionNameLabel = "કાર્યક્રમ";
				functionPaxLabel = "વ્યક્તિ";
				fNote = "ભોજન વ્યવસ્થા";
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				menuItemLabel = "Menu Item Name";
				functionNameLabel = "Function";
				functionPaxLabel = "Pax";
				fNote = "Food Preference";
			}
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			PdfFont basicFont3 = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponselist = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, null, null);

			if (menuAgencyWithItemsResponselist.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			ChefAndOutsideChitthiResponseDto response = convertToChefOutsideResponse(menuAgencyWithItemsResponselist);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			StringBuilder foodPrefName;
			String fNotes = "";
			if (lang == 1) {

				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameHindi() : "");
				fNotes = eventMasterEntity.getMeal_notes_hindi();
			} else if (lang == 2) {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameGujarati()
								: "");
				fNotes = eventMasterEntity.getMeal_notes_gujarati();
			} else {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameEnglish()
								: "");
				fNotes = eventMasterEntity.getMeal_notes();
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "inside chitthi")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 50, 50, 50);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color labelBgColor = new DeviceRgb(230, 234, 237);
			Color mainColor = new DeviceRgb(0, 0, 0);
			Color secondBgColor;
			Cell cell;

			boolean isFirst = true;
			for (AgencyResponseDto agency : response.getAgencyDetails()) {
				Table headerTable = null;
				if (!isFirst) {
					addSignatureAtBottom(document, basicFont2);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirst = false;
				headerTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setFixedLayout();

				if (isCompanyDetails == 1) {
					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//							.loadImageFromResource("/flipbook/pages/logo.png");
					Image logo = new Image(logoData);

					// Resize & align
					logo.setWidth(100f);
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

					cell = new Cell(3, 2).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setBorder(Border.NO_BORDER).setPaddingBottom(10f);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).setFontSize(14)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER).setPaddingLeft(15f);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph().add(new Text(phone + " : ")
									.setFont(basicFont).setFontSize(lang == 0 ? 14 : 16).setFontColor(mainColor)
									.simulateBold())
									.add(new Text(cmpDto.getOfficeNo() != null
											? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
											: "").setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
							.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f)
							.setPaddingLeft(lang == 0 ? 14f : 16f);
					headerTable.addCell(cell);

					headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				}

				cell = new Cell().add(new Paragraph(agencyName).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
						.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)
						.setFixedLeading(lang == 0 ? 14f : 16f).simulateBold()).setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
								.setTextAlignment(TextAlignment.LEFT).setFixedLeading(14f).simulateBold())
						.setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				String party;
				if (lang == 1) {
					party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";
				} else if (lang == 2) {
					party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";
				} else {
					party = agency.getContactName() != null ? agency.getContactName() : "";
				}
				cell = new Cell()
						.add(new Paragraph(party).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
								.setFontColor(mainColor).setFixedLeading(14f).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				document.add(headerTable);

				for (FunctionDetailsResponseDto function : agency.getFunctionDetails()) {

					EventFunctionMasterResponseDto fn = function.getEventFunction();

					String fnName;
					if (lang == 1) {

						fnName = fn.getFunction().getNameHindi() != null ? fn.getFunction().getNameHindi() : "";

					} else if (lang == 2) {

						fnName = fn.getFunction().getNameGujarati() != null ? fn.getFunction().getNameGujarati() : "";

					} else {

						fnName = fn.getFunction().getNameEnglish() != null ? fn.getFunction().getNameEnglish() : "";
					}

					Map<String, List<MenuAllocationItemsResponseDto>> placeWiseMap = function.getAllocationItems()
							.stream()
							.collect(Collectors
									.groupingBy(item -> item.getPlace() != null && !item.getPlace().trim().isEmpty()
											? item.getPlace()
											: "-", LinkedHashMap::new, Collectors.toList()));

					Table functionDetailTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }),
							true);

					functionDetailTable.setWidth(UnitValue.createPercentValue(100));

					functionDetailTable.setFixedLayout();

					cell = new Cell()
							.add(new Paragraph(eDate).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					String date = fn.getFunctionStartDateTime().split(" ")[0];

					String time = fn.getFunctionStartDateTime().split(" ")[1] + " "
							+ fn.getFunctionStartDateTime().split(" ")[2];

					Paragraph datePara = new Paragraph().add(new Text(date).setUnderline())
							.add(new Text(" on due ").simulateBold()).add(new Text(time).setUnderline())
							.setFont(basicFont3).setFontSize(14);

					functionDetailTable.addCell(new Cell().add(datePara).setBorder(Border.NO_BORDER));

					cell = new Cell()
							.add(new Paragraph(functionNameLabel).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(fnName).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					// FoodPreference NAME
					cell = new Cell()
							.add(new Paragraph(fNote).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(
							foodPrefName.toString() + (fNotes.trim().isEmpty() ? "" : " (" + fNotes + ")"))
							.setFont(basicFont).setFontSize(lang == 0 ? 14 : 16).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					document.add(functionDetailTable);

					for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> placeEntry : placeWiseMap.entrySet()) {

						String placeName = placeEntry.getKey();

						List<MenuAllocationItemsResponseDto> placeItems = placeEntry.getValue();

						if (placeName.equalsIgnoreCase("venue") || placeName.equalsIgnoreCase("at venue")
								|| placeName.equalsIgnoreCase("0")) {

							if (lang == 0) {

								placeName = fn.getFunction_venue();

							} else if (lang == 1) {

								placeName = fn.getFunction_venue_hindi();

							} else {

								placeName = fn.getFunction_venue_gujarati();
							}

						} else {

							try {

								UserGodownEntity userGodownEntity = userGodownRepository
										.findByIdAndIsDeleteFalse(Long.valueOf(placeName)).orElse(null);

								if (userGodownEntity == null) {

									placeName = "";

								} else if (lang == 0) {

									placeName = userGodownEntity.getAddressEnglish();

								} else if (lang == 1) {

									placeName = userGodownEntity.getAddressHindi();

								} else {

									placeName = userGodownEntity.getAddressGujarati();
								}

							} catch (Exception e) {

								placeName = "";
							}
						}

						Paragraph venuePara = new Paragraph()
								.add(new Text(eVenue + " : ").setFont(basicFont).setFontColor(mainColor).simulateBold())
								.add(new Text(placeName != null ? placeName : "").setFont(basicFont)
										.setFontColor(mainColor))
								.setFontSize(lang == 0 ? 14 : 16).setMarginTop(10f);

						document.add(venuePara);

						Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }), false);

						itemTable.setWidth(UnitValue.createPercentValue(100));

						itemTable.setFixedLayout();

						itemTable.setHorizontalAlignment(HorizontalAlignment.LEFT);

						itemTable.setMarginLeft(0);

						itemTable.setMarginBottom(15f);

						cell = new Cell()
								.add(new Paragraph(menuItemLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
								.setBackgroundColor(labelBgColor).setBorder(Border.NO_BORDER);

						itemTable.addCell(cell);

						boolean isEvenRaw = false;

						Map<String, List<MenuAllocationItemsResponseDto>> categoryWiseItems = new LinkedHashMap<>();

						for (MenuAllocationItemsResponseDto item : placeItems) {

							String categoryName;

							if (lang == 1) {
								categoryName = item.getCategoryNameHindi();
							} else if (lang == 2) {
								categoryName = item.getCategoryNameGujarati();
							} else {
								categoryName = item.getCategoryName();
							}

							if (categoryName == null || categoryName.trim().isEmpty()) {
								categoryName = "Other";
							}

							if (!categoryWiseItems.containsKey(categoryName)) {
								categoryWiseItems.put(categoryName, new ArrayList<MenuAllocationItemsResponseDto>());
							}

							categoryWiseItems.get(categoryName).add(item);
						}

						boolean firstCategory = true;

						for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> entry : categoryWiseItems
								.entrySet()) {

							// -------- Gap before category --------
							if (!firstCategory) {
								Cell gapCell = new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER)
										.setBackgroundColor(ColorConstants.WHITE).setPaddingTop(8).setPaddingBottom(8);

								itemTable.addCell(gapCell);
							}

							firstCategory = false;

							// -------- Category Header --------
							Cell categoryCell = new Cell()
									.add(new Paragraph(entry.getKey()).simulateBold().setFontColor(ColorConstants.BLACK)
											.setUnderline().setTextAlignment(TextAlignment.LEFT))
									.setBorder(Border.NO_BORDER).setPaddingTop(5).setPaddingBottom(5).setPaddingLeft(8);

							categoryCell.setFontSize(lang == 0 ? 14 : 16);

							itemTable.addCell(categoryCell);

							// -------- Category Items --------
							for (MenuAllocationItemsResponseDto item : entry.getValue()) {

								secondBgColor = isEvenRaw ? new DeviceRgb(245, 245, 245) : new DeviceRgb(255, 255, 255);

								String itemName = "";
								String unit = "";
								String remarks = "";
								if (lang == 1) {
									itemName = item.getItemNameHindi();
									unit = item.getUnitNameHindi() == null ? "" : item.getUnitNameHindi();
									remarks = item.getRemarksHindi() != null ? item.getRemarksHindi() : "";
								} else if (lang == 2) {
									itemName = item.getItemNameGujarati();
									unit = item.getUnitNameGujarati() == null ? "" : item.getUnitNameGujarati();
									remarks = item.getRemarksGujarati() != null ? item.getRemarksGujarati() : "";
								} else {
									itemName = item.getItemName();
									unit = item.getUnitName() == null ? "" : item.getUnitName();
									remarks = item.getRemarks() != null ? item.getRemarks() : "";
								}

								String notes = " (" + remarks + ")";

								cell = new Cell()
										.add(new Paragraph((itemName != null ? itemName : "") + notes)
												.setFont(basicFont).setFontColor(mainColor)
												.setTextAlignment(TextAlignment.LEFT))
										.setBackgroundColor(secondBgColor).setBorder(Border.NO_BORDER)
										.setPaddingLeft(12).setPaddingTop(3).setPaddingBottom(3);

								cell.setFontSize(lang == 0 ? 14 : 16);

								itemTable.addCell(cell);

								isEvenRaw = !isEvenRaw;
							}
						}

						document.add(itemTable);
					}
				}
			}
			addSignatureAtBottom(document, basicFont2);
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ eventMasterEntity.getEventNo() + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "inside chitthi")
					+ ".pdf";
			return fullUrl;

		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}

	}

//	@Override
//	public String generateOutsideAgencyReportType2(Long eventId, Long eventFunctionId, List<Long> agencyId,
//			List<Long> itemId, Integer isCompanyDetails, String type, String startDate, String endDate,
//			HttpServletRequest re, Integer lang, Long userid, Integer isWithPrice) {
//
//		try {
//			PdfFont basicFont = null;
//			menuPreparationServiceImpl.loadLicense();
//
//			int x = 185;
//			int y = 515;
//
//			String functionName, phone, eDate, eVenue, agencyName, email, funLabel, menuItemLabel, dateTimeLabel,
//					qtyLabel, unitLabel, notesLabel, priceLabel, totalPriceLabel, grandTotalLabel;
//
//			if (lang == 1) {
//				// Hindi
//				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
//				System.out.println("Hindi font loaded successfully");
//				y = 512;
//				phone = "मोबाइल नंबर";
//				eDate = "कार्यक्रम की दिनांक";
//				eVenue = "स्थान";
//				agencyName = "एजेंसी का नाम";
//				email = "ईमेल";
//				funLabel = "समारोह";
//				menuItemLabel = "मेनू आइटम";
//				dateTimeLabel = "दिनांक समय";
//				qtyLabel = "मात्रा";
//				unitLabel = "मात्रक";
//				notesLabel = "टिप्पणी";
//				priceLabel = "कीमत";
//				totalPriceLabel = "कुल कीमत";
//				grandTotalLabel = "ग्रांड टोटल";
//			} else if (lang == 2) {
//				// Gujarati
//				System.out.println("Loading Gujarati font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
//				System.out.println("Gujarati font loaded successfully");
//				y = 510;
//				phone = "મોબાઇલ નંબર";
//				eDate = "કાર્યક્રમની તારીખ";
//				eVenue = "સ્થળ";
//				agencyName = "એજન્સીનું નામ";
//				email = "ઈમૈલ";
//				funLabel = "કાર્યક્રમ";
//				menuItemLabel = "મેનુ આઇટમ";
//				dateTimeLabel = "તારીખ અને સમય";
//				qtyLabel = "સંખ્યા";
//				unitLabel = "એકમ";
//				notesLabel = "નોંધ";
//				priceLabel = "કિંમત";
//				totalPriceLabel = "કુલ કિંમત";
//				grandTotalLabel = "ગ્રાન્ડ ટોટલ";
//			} else {
//				// English
//				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
//				System.out.println("English font loaded successfully");
//				phone = "Mobile No.";
//				eDate = "Date";
//				eVenue = "Venue";
//				agencyName = "Agency Name";
//				email = "Email";
//				funLabel = "Function";
//				menuItemLabel = "Menu Item Name";
//				dateTimeLabel = "Date & Time";
//				qtyLabel = "Qty";
//				unitLabel = "Unit";
//				notesLabel = "Remarks";
//				priceLabel = "Price";
//				totalPriceLabel = "Total Price";
//				grandTotalLabel = "Grand Total";
//			}
//			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//
//			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponselist = getAgencyWithItemType(type,
//					eventId, eventFunctionId, agencyId, itemId, startDate, endDate);
//
//			if (menuAgencyWithItemsResponselist.isEmpty()) {
//				return "No agency has been allocated for any items.";
//			}
//
//			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);
//			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
//			Date now = new Date();
//			String rootPath = re.getSession().getServletContext().getRealPath("/");
//			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
//			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");
//
//			if (!outputPath.exists()) {
//				if (outputPath.mkdirs()) {
//					System.out.println("Directory Created!!!");
//				} else {
//					System.out.println("Error!!!");
//				}
//			}
//
//			String fileName = eventMasterEntity.getParty().getNameEnglish();
//
//			if (agencyId.size() == 1) {
//				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);
//
//				if (party != null) {
//					fileName = party.getNameEnglish();
//				}
//			}
//
//			File pdfFile = new File(outputPath + "/"
//					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside report")
//					+ ".pdf");
//
//			PdfWriter writer = new PdfWriter(pdfFile);
//			PdfDocument pdfDocument = new PdfDocument(writer);
//			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
//			document.setMargins(20, 20, 50, 20);
//
//			ImageData mainBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
//			ImageData watermarkBgData = menuPreparationServiceImpl
//					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");
//
//			Color mainColor = new DeviceRgb(0, 0, 0);
//			Cell cell;
//
//			Table headerTable = null;
//			Table companyTable = null;
//
//			boolean isFirst = true;
//			for (MenuAllocationAgencyWithItemsResponseDto menuAgencyWithItemsResponseDto : menuAgencyWithItemsResponselist) {
//				EventFunctionMasterResponseDto functionMasterDto = menuAgencyWithItemsResponseDto.getEventFunction();
//
//				String venue = functionMasterDto.getFunction_venue();
//				for (MenuAllocationAgencyResponseDto agency : menuAgencyWithItemsResponseDto.getAgencyResponse()) {
//					if (!isFirst) {
//						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//					}
//					isFirst = false;
//					if (isCompanyDetails == 1) {
//						companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);
//						ImageData logoData = menuPreparationServiceImpl
//								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
////								.loadImageFromResource("/flipbook/pages/logo.png");
//						Image logo = new Image(logoData);
//
//						// Resize & align
//						logo.setWidth(100f);
//						logo.setAutoScale(false);
//						logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
//
//						cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f);
//						companyTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).simulateBold()
//										.setFontSize(14).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
//								.setBorder(Border.NO_BORDER);
//						companyTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph()
//										.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//												.setFontColor(mainColor).simulateBold())
//										.add(new Text(cmpDto.getOfficeNo() != null
//												? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
//												: "").setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
//								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
//						companyTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph()
//										.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//												.setFontColor(mainColor).simulateBold())
//										.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
//												.setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
//								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
//						companyTable.addCell(cell);
//
//						document.add(companyTable);
//					}
//					headerTable = new Table(UnitValue.createPercentArray(new float[] { 19f, 1f, 33f, 16f, 1f, 30f }),
//							false);
//					headerTable.setWidth(UnitValue.createPercentValue(100));
//					headerTable.setBorder(new SolidBorder(1f));
//
//					cell = new Cell()
//							.add(new Paragraph(agencyName).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(12).setFontColor(mainColor)
//							.setTextAlignment(TextAlignment.LEFT).simulateBold()).setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					String party;
//					if (lang == 1) {
//						party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";
//					} else if (lang == 2) {
//						party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";
//					} else {
//						party = agency.getContactName() != null ? agency.getContactName() : "";
//					}
//					cell = new Cell()
//							.add(new Paragraph(party).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
//							.setBorder(Border.NO_BORDER).setPaddingBottom(10f);
//					headerTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(phone).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(12).setFontColor(mainColor)
//							.setTextAlignment(TextAlignment.LEFT).simulateBold()).setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(agency.getNumber()).setFont(basicFont).setFontSize(14)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
//							.setBorder(Border.NO_BORDER).setPaddingBottom(10f);
//					headerTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(eVenue).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(10).setFontColor(mainColor)
//							.setTextAlignment(TextAlignment.LEFT).simulateBold()).setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell(1, 4)
//							.add(new Paragraph(venue).setFont(basicFont).setFontSize(14).setFontColor(mainColor)
//									.setTextAlignment(TextAlignment.LEFT))
//							.setBorder(Border.NO_BORDER).setPaddingBottom(10f);
//					headerTable.addCell(cell);
//
//					document.add(headerTable);
//
//					Table itemTable = null;
//					if (isWithPrice == 1) {
//						itemTable = new Table(
//								UnitValue.createPercentArray(new float[] { 11f, 24f, 10f, 6f, 10f, 10f, 10f, 19f }),
//								false);
//					} else {
//						itemTable = new Table(UnitValue.createPercentArray(new float[] { 11f, 32f, 25f, 8f, 7f, 17f }),
//								false);
//					}
//
//					itemTable.setWidth(UnitValue.createPercentValue(100));
//					itemTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
//					itemTable.setMarginLeft(0);
//					itemTable.setMarginTop(10f);
//
//					cell = new Cell().add(new Paragraph(funLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(menuItemLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(dateTimeLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(qtyLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(unitLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					if (isWithPrice == 1) {
//						cell = new Cell()
//								.add(new Paragraph(priceLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//						itemTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph(totalPriceLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//						itemTable.addCell(cell);
//					}
//
//					cell = new Cell().add(new Paragraph(notesLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//					String itemName = "";
//					String unitName = "";
//					Long grandTotal = 0l;
//					for (MenuAllocationItemsResponseDto item : agency.getAllocationItems()) {
//						if (lang == 1) {
//							itemName = item.getItemNameHindi();
//							unitName = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "";
//						} else if (lang == 2) {
//							itemName = item.getItemNameGujarati();
//							unitName = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "";
//						} else {
//							itemName = item.getItemName();
//							unitName = item.getUnitName() != null ? item.getUnitName() : "";
//						}
//
//						cell = new Cell().add(new Paragraph(lang == 1 ? functionMasterDto.getFunction().getNameHindi()
//								: lang == 2 ? functionMasterDto.getFunction().getNameGujarati()
//										: functionMasterDto.getFunction().getNameEnglish())
//								.setFont(basicFont).setFontColor(mainColor));
//
//						if (lang == 0) {
//							cell.setFontSize(13);
//						} else {
//							cell.setFontSize(15);
//						}
//
//						itemTable.addCell(cell);
//
//						cell = new Cell().add(new Paragraph(itemName.toUpperCase()).setFont(basicFont)
//								.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//
//						if (lang == 0) {
//							cell.setFontSize(13);
//						} else {
//							cell.setFontSize(15);
//						}
//
//						itemTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph(functionMasterDto.getFunctionStartDateTime()).setFont(basicFont)
//										.setFontSize(13).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//						itemTable.addCell(cell);
//
//						cell = new Cell().add(new Paragraph(item.getQty() != 0 ? item.getQty().toString() : "")
//								.setFont(basicFont).setFontColor(mainColor).setTextAlignment(TextAlignment.CENTER));
//
//						if (lang == 0) {
//							cell.setFontSize(13);
//						} else {
//							cell.setFontSize(15);
//						}
//
//						itemTable.addCell(cell);
//
//						cell = new Cell().add(new Paragraph(unitName).setFont(basicFont).setFontColor(mainColor)
//								.setTextAlignment(TextAlignment.LEFT));
//
//						if (lang == 0) {
//							cell.setFontSize(13);
//						} else {
//							cell.setFontSize(15);
//						}
//
//						itemTable.addCell(cell);
//
//						if (isWithPrice == 1) {
//							cell = new Cell()
//									.add(new Paragraph(item.getPrice() != null ? item.getPrice().toString() : "")
//											.setFont(basicFont).setFontColor(mainColor)
//											.setTextAlignment(TextAlignment.LEFT));
//							if (lang == 0) {
//								cell.setFontSize(13);
//							} else {
//								cell.setFontSize(15);
//							}
//							itemTable.addCell(cell);
//
//							Integer total = item.getQty() * item.getPrice();
//							cell = new Cell().add(new Paragraph(total.toString()).setFont(basicFont)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//							if (lang == 0) {
//								cell.setFontSize(13);
//							} else {
//								cell.setFontSize(15);
//							}
//							itemTable.addCell(cell);
//
//							grandTotal = grandTotal + total;
//
//						}
//
//						cell = new Cell().add(new Paragraph(item.getRemarks() != null ? item.getRemarks() : "")
//								.setFont(basicFont).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//
//						if (lang == 0) {
//							cell.setFontSize(13);
//						} else {
//							cell.setFontSize(15);
//						}
//
//						itemTable.addCell(cell);
//					}
//
//					if (isWithPrice == 1) {
//						cell = new Cell(1, 6).add(new Paragraph(grandTotalLabel).setFont(basicFont).simulateBold()
//								.setFontColor(mainColor).setTextAlignment(TextAlignment.RIGHT));
//						if (lang == 0) {
//							cell.setFontSize(13);
//						} else {
//							cell.setFontSize(15);
//						}
//						itemTable.addCell(cell);
//
//						cell = new Cell(1, 2).add(new Paragraph(grandTotal.toString()).setFont(basicFont)
//								.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//						if (lang == 0) {
//							cell.setFontSize(13);
//						} else {
//							cell.setFontSize(15);
//						}
//						itemTable.addCell(cell);
//					}
//
//					document.add(itemTable);
//				}
//			}
//			document.close();
//
//			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
//					+ eventMasterEntity.getEventNo() + "/"
//					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside report")
//					+ ".pdf";
//			return fullUrl;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Failed to generate Event Menu Report", e);
//		}
//
//	}

	@Override
	public String generateOutsideAgencyReportType2(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, String startDate, String endDate,
			HttpServletRequest re, Integer lang, Long userid, Integer isWithPrice, Integer isAgencyNextPage) {

		try {
			if (isAgencyNextPage == null) {
				isAgencyNextPage = 1;
			}
			PdfFont basicFont = null;

			menuPreparationServiceImpl.loadLicense();

			String phone, eDate, eVenue, agencyName, email, funLabel, menuItemLabel, dateTimeLabel, qtyLabel, unitLabel,
					notesLabel, priceLabel, totalPriceLabel, grandTotalLabel;

			if (lang == 1) {

				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				dateTimeLabel = "दिनांक समय";
				qtyLabel = "मात्रा";
				unitLabel = "मात्रक";
				notesLabel = "टिप्पणी";
				priceLabel = "कीमत";
				totalPriceLabel = "कुल कीमत";
				grandTotalLabel = "ग्रांड टोटल";

			} else if (lang == 2) {

				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				dateTimeLabel = "તારીખ અને સમય";
				qtyLabel = "સંખ્યા";
				unitLabel = "એકમ";
				notesLabel = "નોંધ";
				priceLabel = "કિંમત";
				totalPriceLabel = "કુલ કિંમત";
				grandTotalLabel = "ગ્રાન્ડ ટોટલ";

			} else {

				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");

				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				dateTimeLabel = "Date & Time";
				qtyLabel = "Qty";
				unitLabel = "Unit";
				notesLabel = "Remarks";
				priceLabel = "Price";
				totalPriceLabel = "Total Price";
				grandTotalLabel = "Grand Total";
			}

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponselist = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, startDate, endDate);

			if (menuAgencyWithItemsResponselist.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			Date now = new Date();

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {

				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);

			PdfDocument pdfDocument = new PdfDocument(writer);

			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);

			document.setMargins(20, 20, 50, 20);

			Color mainColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			boolean isFirst = true;

			for (MenuAllocationAgencyWithItemsResponseDto menuAgencyWithItemsResponseDto : menuAgencyWithItemsResponselist) {

				EventFunctionMasterResponseDto functionMasterDto = menuAgencyWithItemsResponseDto.getEventFunction();

				for (MenuAllocationAgencyResponseDto agency : menuAgencyWithItemsResponseDto.getAgencyResponse()) {

					if (!isFirst && isAgencyNextPage == 1) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					}

					isFirst = false;

					// COMPANY TABLE
					if (isCompanyDetails == 1) {

						Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }),
								true);

						ImageData logoData = menuPreparationServiceImpl
								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());

						Image logo = new Image(logoData);

						logo.setWidth(100f);

						cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f);

						companyTable.addCell(cell);

						companyTable.addCell(new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont)
								.simulateBold().setFontSize(14)).setBorder(Border.NO_BORDER));

						companyTable.addCell(new Cell()
								.add(new Paragraph().add(new Text(phone + " : ").setFont(basicFont).simulateBold())
										.add(new Text(cmpDto.getOfficeNo() != null
												? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
												: "").setFont(basicFont)))
								.setBorder(Border.NO_BORDER));

						companyTable.addCell(new Cell()
								.add(new Paragraph().add(new Text(email + " : ").setFont(basicFont).simulateBold())
										.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
												.setFont(basicFont)))
								.setBorder(Border.NO_BORDER).setPaddingBottom(10f));

						document.add(companyTable);
					}

					// HEADER TABLE
					Table headerTable = new Table(
							UnitValue.createPercentArray(new float[] { 19f, 1f, 33f, 16f, 1f, 30f }), false);

					headerTable.setWidth(UnitValue.createPercentValue(100));

					headerTable.setMarginTop(15f);

					headerTable.setBorder(new SolidBorder(1f));

					headerTable.addCell(new Cell().add(new Paragraph(agencyName).setFont(basicFont).simulateBold())
							.setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER));

					String party;

					if (lang == 1) {

						party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";

					} else if (lang == 2) {

						party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";

					} else {

						party = agency.getContactName() != null ? agency.getContactName() : "";
					}

					headerTable.addCell(
							new Cell().add(new Paragraph(party).setFont(basicFont)).setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell().add(new Paragraph(phone).setFont(basicFont).simulateBold())
							.setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell()
							.add(new Paragraph(agency.getNumber() != null ? agency.getNumber() : "").setFont(basicFont))
							.setBorder(Border.NO_BORDER));

					document.add(headerTable);

					// PLACE WISE GROUPING
					Map<String, List<MenuAllocationItemsResponseDto>> placeWiseMap = agency.getAllocationItems()
							.stream()
							.collect(Collectors
									.groupingBy(item -> item.getPlace() != null && !item.getPlace().trim().isEmpty()
											? item.getPlace()
											: "-", LinkedHashMap::new, Collectors.toList()));

					Long grandTotal = 0L;

					for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> placeEntry : placeWiseMap.entrySet()) {

						String placeName = placeEntry.getKey();

						List<MenuAllocationItemsResponseDto> placeItems = placeEntry.getValue();

						if (placeName.equalsIgnoreCase("venue") || placeName.equalsIgnoreCase("at venue")
								|| placeName.equalsIgnoreCase("0")) {

							List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
									.findBanquetByEventFunctionId(eventId,
											functionMasterDto.getId());
							if (!banquets.isEmpty()) {
								placeName = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
										.collect(Collectors.joining(", "));
							} else {
								placeName = lang == 1 ? functionMasterDto.getFunction_venue_hindi()
										: lang == 2 ? functionMasterDto.getFunction_venue_gujarati() : functionMasterDto.getFunction_venue();
							}

						} else {

							try {

								UserGodownEntity userGodownEntity = userGodownRepository
										.findByIdAndIsDeleteFalse(Long.valueOf(placeName)).orElse(null);

								if (userGodownEntity == null) {

									placeName = "";

								} else if (lang == 0) {

									placeName = userGodownEntity.getAddressEnglish();

								} else if (lang == 1) {

									placeName = userGodownEntity.getAddressHindi();

								} else {

									placeName = userGodownEntity.getAddressGujarati();
								}

							} catch (Exception e) {

								placeName = "";
							}
						}

						// VENUE TITLE
						Paragraph venuePara = new Paragraph()
								.add(new Text(eVenue + " : ").setFont(basicFont).simulateBold())
								.add(new Text(placeName != null ? placeName : "").setFont(basicFont)).setMarginTop(10f);

						document.add(venuePara);

						// ITEM TABLE
						Table itemTable;

						if (isWithPrice == 1) {

							itemTable = new Table(
									UnitValue.createPercentArray(new float[] { 11f, 24f, 10f, 6f, 10f, 10f, 10f, 19f }),
									false);

						} else {

							itemTable = new Table(
									UnitValue.createPercentArray(new float[] { 11f, 32f, 25f, 8f, 7f, 17f }), false);
						}

						itemTable.setWidth(UnitValue.createPercentValue(100));

						itemTable.setMarginTop(10f);

						// HEADER
						itemTable.addCell(new Cell().add(new Paragraph(funLabel).setFont(basicFont).simulateBold()));

						itemTable.addCell(
								new Cell().add(new Paragraph(menuItemLabel).setFont(basicFont).simulateBold()));

						itemTable.addCell(
								new Cell().add(new Paragraph(dateTimeLabel).setFont(basicFont).simulateBold()));

						itemTable.addCell(new Cell().add(new Paragraph(qtyLabel).setFont(basicFont).simulateBold()));

						itemTable.addCell(new Cell().add(new Paragraph(unitLabel).setFont(basicFont).simulateBold()));

						if (isWithPrice == 1) {

							itemTable.addCell(
									new Cell().add(new Paragraph(priceLabel).setFont(basicFont).simulateBold()));

							itemTable.addCell(
									new Cell().add(new Paragraph(totalPriceLabel).setFont(basicFont).simulateBold()));
						}

						itemTable.addCell(new Cell().add(new Paragraph(notesLabel).setFont(basicFont).simulateBold()));

						// ITEMS
						for (MenuAllocationItemsResponseDto item : placeItems) {

							String itemName;
							String unitName;
							String remarks = "";
							if (lang == 1) {

								itemName = item.getItemNameHindi();

								unitName = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "";

								remarks = item.getRemarksHindi() != null ? item.getRemarksHindi() : "";
							} else if (lang == 2) {

								itemName = item.getItemNameGujarati();

								unitName = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "";

								remarks = item.getRemarksGujarati() != null ? item.getRemarksGujarati() : "";
							} else {
								itemName = item.getItemName();

								unitName = item.getUnitName() != null ? item.getUnitName() : "";

								remarks = item.getRemarks() != null ? item.getRemarks() : "";
							}

							itemTable.addCell(new Cell()
									.add(new Paragraph(lang == 1 ? functionMasterDto.getFunction().getNameHindi()
											: lang == 2 ? functionMasterDto.getFunction().getNameGujarati()
													: functionMasterDto.getFunction().getNameEnglish())
											.setFont(basicFont)));

							itemTable.addCell(new Cell().add(new Paragraph(itemName).setFont(basicFont)));

							itemTable.addCell(new Cell().add(
									new Paragraph(functionMasterDto.getFunctionStartDateTime()).setFont(basicFont)));

							itemTable.addCell(
									new Cell().add(new Paragraph(item.getQty() != null ? item.getQty().toString() : "")
											.setFont(basicFont)));

							itemTable.addCell(new Cell().add(new Paragraph(unitName).setFont(basicFont)));

							if (isWithPrice == 1) {

								itemTable.addCell(new Cell()
										.add(new Paragraph(item.getPrice() != null ? item.getPrice().toString() : "")
												.setFont(basicFont)));

								Long total = ((long) item.getQty()) * item.getPrice();

								itemTable.addCell(new Cell().add(new Paragraph(total.toString()).setFont(basicFont)));

								grandTotal += total;
							}

							itemTable.addCell(new Cell().add(new Paragraph(remarks).setFont(basicFont)));
						}

						// GRAND TOTAL
						if (isWithPrice == 1) {

							itemTable.addCell(
									new Cell(1, 6).add(new Paragraph(grandTotalLabel).setFont(basicFont).simulateBold())
											.setTextAlignment(TextAlignment.RIGHT));

							itemTable.addCell(
									new Cell(1, 2).add(new Paragraph(grandTotal.toString()).setFont(basicFont)));
						}

						document.add(itemTable);
					}
				}
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventMasterEntity.getEventNo()
					+ "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside report")
					+ ".pdf";

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String generateOutsideAgencyReportType3(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, String startDate, String endDate, Long partyId,
			HttpServletRequest re, Integer lang, Long userid, Integer withPrice) {

		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String functionName, phone, eDate, eVenue, email, funLabel, menuItemLabel, timeLabel, qtyLabel, unitLabel,
					notesLabel, rateLabel, totalLabel, totalMasterLabel;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 512;
				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की दिनांक";
				eVenue = "स्थान";
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				timeLabel = "समय";
				qtyLabel = "मात्रा";
				unitLabel = "मात्रक";
				notesLabel = "टिप्पणी";
				rateLabel = "भाव";
				totalLabel = "टोटल";
				totalMasterLabel = "ग्रैंड टोटल";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 510;
				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "સ્થળ";
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				timeLabel = "સમય";
				qtyLabel = "સંખ્યા";
				unitLabel = "એકમ";
				notesLabel = "નોંધ";
				rateLabel = "ભાવ";
				totalLabel = "ટોટલ";
				totalMasterLabel = "ગ્રાન્ડ ટોટલ";
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				timeLabel = "Date & Time";
				qtyLabel = "Quantity";
				unitLabel = "Unit";
				notesLabel = "Remarks";
				rateLabel = "Rate";
				totalLabel = "Total";
				totalMasterLabel = "Grand Total";
			}
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);
			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);

			File outputPath = null;
			if (eventMasterEntity == null) {
				outputPath = new File(rootPath + "resources/tempDownload/" + startDate.replace("/", "_") + "/");
			} else {
				outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");
			}

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			File pdfFile = new File(outputPath + "/OutsideAgency_" + datetimeforfile + ".pdf");
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 50, 20);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color backColor = new DeviceRgb(33, 33, 33);
			Color grayColor = new DeviceRgb(209, 209, 209);
			Color whiteColor = new DeviceRgb(255, 254, 247);
			Cell cell;

			Table companyTable = null;

			List<AgencyDataForDatewiseReportResponseDto> data = buildDtoForDateWiseReport(userid, startDate, endDate,
					type, agencyId, partyId);

			if (data == null || data.isEmpty()) {
				return "";
			}
			System.out.println("data size : " + data.size());
			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(basicFont));

			companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);
			if (isCompanyDetails == 1) {

				ImageData logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//						.loadImageFromResource("/flipbook/pages/logo.png");
				Image logo = new Image(logoData);

				// Resize & align
				logo.setWidth(100f);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f);
				companyTable.addCell(cell);

				cell = new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).simulateBold()
						.setFontSize(14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getOfficeNo() != null
										? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
										: "").setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
										.setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
				companyTable.addCell(cell);
			}

			cell = new Cell(1, 3).add(new Paragraph("Date Wise Outside Order Report").setFixedLeading(20))
					.setFont(basicFont).setFontSize(20).simulateBold().setTextAlignment(TextAlignment.CENTER)
					.setBorder(Border.NO_BORDER);
			companyTable.addCell(cell);

			cell = new Cell(1, 3).add(new Paragraph(startDate + " to " + endDate).setFixedLeading(14))
					.setFont(basicFont).setFontSize(14).setPaddingTop(5f).setTextAlignment(TextAlignment.CENTER)
					.setBorder(Border.NO_BORDER);
			companyTable.addCell(cell);

			cell = new Cell(1, 3).add(new Paragraph("")).setFont(basicFont).setHeight(10f).setBorder(Border.NO_BORDER);
			companyTable.addCell(cell);

			document.add(companyTable);

			Integer totalMaster = 0;
			for (AgencyDataForDatewiseReportResponseDto agency : data) {
				List<FnDetailsForDatewiseReportResponseDto> functionMasterDto = agency.getFunctions();

				Table agencyTable = null;
				agencyTable = new Table(
						UnitValue.createPercentArray(new float[] { 4f, 4f, 30f, 15f, 10f, 10f, 10f, 17f }));
				agencyTable.setWidth(UnitValue.createPercentValue(100f));
//				agencyTable.setKeepTogether(true);
				String contactName = "";
				if (lang == 1) {
					contactName = agency.getNameHindi() != null ? agency.getNameHindi().toUpperCase() : "";
				} else if (lang == 2) {
					contactName = agency.getNameGujarati() != null ? agency.getNameGujarati().toUpperCase() : "";
				} else {
					contactName = agency.getNameEnglish() != null ? agency.getNameEnglish().toUpperCase() : "";
				}
				cell = new Cell(1, 8)
						.add(new Paragraph().add(contactName).setFontSize(14)
								.add(new Text(
										" ( " + (agency.getContactNo() != null ? agency.getContactNo() : "") + " )"))
								.setFontSize(13))
						.setTextAlignment(TextAlignment.CENTER).setFont(basicFont).setBackgroundColor(backColor)
						.setFontColor(whiteColor);
				agencyTable.addCell(cell);

				for (FnDetailsForDatewiseReportResponseDto function : functionMasterDto) {

					String functionDate;
					if (function.getFnStartDate() != null) {
						functionDate = function.getFnStartDate();
					} else {
						functionDate = "";
					}
					cell = new Cell(1, 8).add(new Paragraph().add(functionDate).setFont(basicFont).setFontSize(13))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(5f)
							.setBackgroundColor(grayColor);
					agencyTable.addCell(cell);

					agencyTable.addCell(new Cell().setBorder(Border.NO_BORDER));

					cell = new Cell(1, 7)
							.add(new Paragraph().add(function.getVenue().toUpperCase()).setFont(basicFont)
									.setFontSize(13))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(5f)
							.setBackgroundColor(grayColor);
					agencyTable.addCell(cell);

					agencyTable.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER));

					if (withPrice == 1) {
						cell = new Cell();
					} else {
						cell = new Cell(1, 2);
					}
					cell.add(new Paragraph().add(menuItemLabel).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph().add(timeLabel).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					if (withPrice == 1) {
						cell = new Cell()
								.add(new Paragraph().add(rateLabel).setFont(basicFont)
										.setFontSize(lang == 0 ? 12f : 14f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
						agencyTable.addCell(cell);
					}

					if (withPrice == 1) {
						cell = new Cell();
					} else {
						cell = new Cell(1, 2);
					}
					cell.add(new Paragraph().add(qtyLabel).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph().add(unitLabel).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					if (withPrice == 1) {
						cell = new Cell()
								.add(new Paragraph().add(totalLabel).setFont(basicFont)
										.setFontSize(lang == 0 ? 12f : 14f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
						agencyTable.addCell(cell);
					}

					String itemName = "", unit = "";
					Integer total = 0, grandTotal = 0;
					for (MenuItemForDatewiseReportResponseDto item : function.getMenuItems()) {
						agencyTable.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER));
						total = item.getRate() * item.getQty();
						if (lang == 1) {
							itemName = item.getItemNameHindi() != null ? item.getItemNameHindi() : "";
							unit = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "";
						} else if (lang == 2) {
							itemName = item.getItemNameGujarati() != null ? item.getItemNameGujarati() : "";
							unit = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "";
						} else {
							itemName = item.getItemNameEnglish() != null ? item.getItemNameEnglish() : "";
							unit = item.getUnitNameEnglish() != null ? item.getUnitNameEnglish() : "";
						}

						if (withPrice == 1) {
							cell = new Cell();
						} else {
							cell = new Cell(1, 2);
						}
						cell.add(new Paragraph().add(itemName).setFont(basicFont).setFontSize(lang == 0 ? 11f : 13f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						cell = new Cell().add(new Paragraph().add(item.getDateTime().toUpperCase()).setFont(basicFont)
								.setFontSize(11)).setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						if (withPrice == 1) {
							cell = new Cell().add(
									new Paragraph().add(item.getRate().toString()).setFont(basicFont).setFontSize(11))
									.setHorizontalAlignment(HorizontalAlignment.LEFT);
							agencyTable.addCell(cell);
						}

						if (withPrice == 1) {
							cell = new Cell();
						} else {
							cell = new Cell(1, 2);
						}
						cell.add(new Paragraph().add(item.getQty().toString()).setFont(basicFont).setFontSize(11))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						cell = new Cell()
								.add(new Paragraph().add(unit).setFont(basicFont).setFontSize(lang == 0 ? 11f : 13f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						if (withPrice == 1) {
							cell = new Cell()
									.add(new Paragraph().add(total.toString()).setFont(basicFont).setFontSize(11))
									.setHorizontalAlignment(HorizontalAlignment.LEFT);
							agencyTable.addCell(cell);
						}

						grandTotal = grandTotal + total;
					}

					if (withPrice == 1) {
						cell = new Cell(1, 7)
								.add(new Paragraph().add(totalLabel).setFont(basicFont)
										.setFontSize(lang == 0 ? 12f : 14f))
								.setTextAlignment(TextAlignment.RIGHT).setPadding(2f).simulateBold();
						agencyTable.addCell(cell);

						cell = new Cell()
								.add(new Paragraph().add(grandTotal.toString()).setFont(basicFont).setFontSize(11))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);
						totalMaster = totalMaster + grandTotal;
					}
				}
				document.add(agencyTable);
			}

			if (withPrice == 1) {
				Table totalMasterTable = new Table(UnitValue.createPercentArray(new float[] { 70f, 30f }));
				totalMasterTable.setMarginTop(20f);
				totalMasterTable.setWidth(UnitValue.createPercentValue(100f));

				totalMasterTable.addCell(new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER));

				cell = new Cell()
						.add(new Paragraph().add(totalMasterLabel + " : ").setFont(basicFont)
								.setFontSize(lang == 0 ? 16f : 18f).setFixedLeading(lang == 0 ? 16f : 18f))
						.setTextAlignment(TextAlignment.RIGHT).setPadding(2f).simulateBold()
						.setBorderBottom(Border.NO_BORDER);
				totalMasterTable.addCell(cell);

				totalMasterTable.addCell(new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER));

				cell = new Cell()
						.add(new Paragraph().add(totalMaster.toString()).setFont(basicFont).setFontSize(16f)
								.setFixedLeading(16f))
						.setTextAlignment(TextAlignment.RIGHT).setPadding(2f).simulateBold()
						.setBorderTop(Border.NO_BORDER);
				totalMasterTable.addCell(cell);

				document.add(totalMasterTable);
			}
			document.close();

			String fullUrl = "";
			if (eventMasterEntity == null) {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + startDate.replace("/", "_")
						+ "/OutsideAgency_" + datetimeforfile + ".pdf";
			} else {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
						+ eventMasterEntity.getEventNo() + "/OutsideAgency_" + datetimeforfile + ".pdf";
			}

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}

	}

	@Override
	public String generateChefAgencyReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang,
			Long userid) {

		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String phone, eDate, eVenue, agencyName, menuItemLabel, functionNameLabel, functionPaxLabel, fNote;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansDevanagari-Regular.ttf");

				System.out.println("Hindi font loaded successfully");
				phone = "मोबाइल नंबर";
				eDate = "दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				menuItemLabel = "मेनू आइटम";
				functionNameLabel = "कार्यक्रम";
				functionPaxLabel = "मेम्बर्स";
				fNote = "भोजन व्यवस्था";

				y = 512;
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				phone = "મોબાઇલ નંબર";
				eDate = "તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				menuItemLabel = "મેનુ આઇટમ";
				functionNameLabel = "કાર્યક્રમ";
				functionPaxLabel = "વ્યક્તિ";
				fNote = "ભોજન વ્યવસ્થા";
				y = 510;
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				menuItemLabel = "Menu Item Name";
				functionNameLabel = "Function";
				functionPaxLabel = "Pax";
				fNote = "Food Preference";
			}

			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			PdfFont basicFont3 = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponseDtos = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, null, null);

			if (menuAgencyWithItemsResponseDtos.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			ChefAndOutsideChitthiResponseDto response = convertToChefOutsideResponse(menuAgencyWithItemsResponseDtos);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			StringBuilder foodPrefName;
			String fNotes = "";
			if (lang == 1) {

				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameHindi() : "");
				fNotes = eventMasterEntity.getMeal_notes_hindi();
			} else if (lang == 2) {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameGujarati()
								: "");
				fNotes = eventMasterEntity.getMeal_notes_gujarati();
			} else {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameEnglish()
								: "");
				fNotes = eventMasterEntity.getMeal_notes();
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Chitthi")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 50, 50, 50);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color labelBgColor = new DeviceRgb(230, 234, 237);
			Color mainColor = new DeviceRgb(0, 0, 0);
			Color secondBgColor;
			Cell cell;
			boolean isFirst = true;
			for (AgencyResponseDto agency : response.getAgencyDetails()) {
				Table headerTable = null;
				if (!isFirst) {
					addSignatureAtBottom(document, basicFont2);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				isFirst = false;
				headerTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);
				headerTable.setWidth(UnitValue.createPercentValue(100));
				headerTable.setFixedLayout();

				if (isCompanyDetails == 1) {
					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//							.loadImageFromResource("/flipbook/pages/logo.png");
					Image logo = new Image(logoData);

					// Resize & align
					logo.setWidth(100f);
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

					cell = new Cell(3, 2).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
							.setBorder(Border.NO_BORDER).setPaddingBottom(10f);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).setFontSize(14)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER).setPaddingLeft(15f);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph().add(new Text(phone + " : ")
									.setFont(basicFont).setFontSize(lang == 0 ? 14 : 16).setFontColor(mainColor)
									.simulateBold())
									.add(new Text(cmpDto.getOfficeNo() != null
											? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
											: "").setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
							.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f)
							.setPaddingLeft(lang == 0 ? 14f : 16f);
					headerTable.addCell(cell);

					headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
				}

				cell = new Cell().add(new Paragraph(agencyName).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
						.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)
						.setFixedLeading(lang == 0 ? 14f : 16f).simulateBold()).setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
								.setTextAlignment(TextAlignment.LEFT).setFixedLeading(14f).simulateBold())
						.setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				String party;
				if (lang == 1) {
					party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";
				} else if (lang == 2) {
					party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";
				} else {
					party = agency.getContactName() != null ? agency.getContactName() : "";
				}
				cell = new Cell()
						.add(new Paragraph(party).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
								.setFontColor(mainColor).setFixedLeading(14f).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER);
				headerTable.addCell(cell);

				document.add(headerTable);

				for (FunctionDetailsResponseDto function : agency.getFunctionDetails()) {

					EventFunctionMasterResponseDto fn = function.getEventFunction();

					String fnName;

					if (lang == 1) {

						fnName = fn.getFunction().getNameHindi() != null ? fn.getFunction().getNameHindi() : "";

					} else if (lang == 2) {

						fnName = fn.getFunction().getNameGujarati() != null ? fn.getFunction().getNameGujarati() : "";

					} else {

						fnName = fn.getFunction().getNameEnglish() != null ? fn.getFunction().getNameEnglish() : "";
					}

					// GROUP PLACE WISE
					Map<String, List<MenuAllocationItemsResponseDto>> placeWiseMap = function.getAllocationItems()
							.stream()
							.collect(Collectors
									.groupingBy(item -> item.getPlace() != null && !item.getPlace().trim().isEmpty()
											? item.getPlace()
											: "-", LinkedHashMap::new, Collectors.toList()));

					// FUNCTION DETAIL TABLE
					Table functionDetailTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }),
							true);

					functionDetailTable.setWidth(UnitValue.createPercentValue(100));

					functionDetailTable.setFixedLayout();

					// DATE
					cell = new Cell()
							.add(new Paragraph(eDate).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					String date = fn.getFunctionStartDateTime().split(" ")[0];

					String time = fn.getFunctionStartDateTime().split(" ")[1] + " "
							+ fn.getFunctionStartDateTime().split(" ")[2];

					Paragraph datePara = new Paragraph().add(new Text(date).setUnderline())
							.add(new Text(" on due ").simulateBold()).add(new Text(time).setUnderline()).setFontSize(14)
							.setFont(basicFont3);

					functionDetailTable.addCell(new Cell().add(datePara).setBorder(Border.NO_BORDER));

					// FUNCTION NAME
					cell = new Cell()
							.add(new Paragraph(functionNameLabel).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(fnName).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					// FoodPreference NAME
					cell = new Cell()
							.add(new Paragraph(fNote).setFont(basicFont).setFontSize(lang == 0 ? 14f : 16f)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.simulateBold()).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					cell = new Cell().add(new Paragraph(
							foodPrefName.toString() + (fNotes.trim().isEmpty() ? "" : " (" + fNotes + ")"))
							.setFont(basicFont).setFontSize(lang == 0 ? 14 : 16).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);

					functionDetailTable.addCell(cell);

					document.add(functionDetailTable);

					// PLACE WISE ITEMS
					for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> placeEntry : placeWiseMap.entrySet()) {

						String placeName = placeEntry.getKey();

						List<MenuAllocationItemsResponseDto> placeItems = placeEntry.getValue();

						if (placeName.equalsIgnoreCase("venue") || placeName.equalsIgnoreCase("at venue")
								|| placeName.equalsIgnoreCase("0")) {

							List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
									.findBanquetByEventFunctionId(eventId,
											fn.getId());
							if (!banquets.isEmpty()) {
								placeName = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
										.collect(Collectors.joining(", "));
							} else {
								placeName = lang == 1 ? fn.getFunction_venue_hindi()
										: lang == 2 ? fn.getFunction_venue_gujarati() : fn.getFunction_venue();
							}
						} else {

							try {

								UserGodownEntity userGodownEntity = userGodownRepository
										.findByIdAndIsDeleteFalse(Long.valueOf(placeName)).orElse(null);

								if (userGodownEntity == null) {

									placeName = "";

								} else if (lang == 0) {

									placeName = userGodownEntity.getAddressEnglish();

								} else if (lang == 1) {

									placeName = userGodownEntity.getAddressHindi();

								} else {

									placeName = userGodownEntity.getAddressGujarati();
								}

							} catch (Exception e) {

								placeName = "";
							}
						}

						// VENUE TITLE
						Paragraph venuePara = new Paragraph()
								.add(new Text(eVenue + " : ").setFont(basicFont).setFontColor(mainColor).simulateBold())
								.add(new Text(placeName != null ? placeName : "").setFont(basicFont)
										.setFontColor(mainColor))
								.setFontSize(lang == 0 ? 14 : 16).setMarginTop(10f);

						document.add(venuePara);

						// ITEM TABLE
						Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 100f }), false);

						itemTable.setWidth(UnitValue.createPercentValue(100));

						itemTable.setFixedLayout();

						itemTable.setHorizontalAlignment(HorizontalAlignment.LEFT);

						itemTable.setMarginLeft(0);

						itemTable.setMarginBottom(15f);

						cell = new Cell()
								.add(new Paragraph(menuItemLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
								.setBackgroundColor(labelBgColor).setBorder(Border.NO_BORDER);

						itemTable.addCell(cell);

						boolean isEvenRaw = false;
						boolean firstCategory = true;

						Map<String, List<MenuAllocationItemsResponseDto>> categoryWiseItems = placeItems.stream()
								.collect(Collectors.groupingBy(item -> {
									if (lang == 1) {
										return item.getCategoryNameHindi() != null ? item.getCategoryNameHindi()
												: "Other";
									} else if (lang == 2) {
										return item.getCategoryNameGujarati() != null ? item.getCategoryNameGujarati()
												: "Other";
									} else {
										return item.getCategoryName() != null ? item.getCategoryName() : "Other";
									}
								}, LinkedHashMap::new, Collectors.toList()));

						for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> entry : categoryWiseItems
								.entrySet()) {

							// ---------- Gap Between Categories ----------
							if (!firstCategory) {
								Cell gapCell = new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER)
										.setBackgroundColor(ColorConstants.WHITE).setPaddingTop(8).setPaddingBottom(8);

								itemTable.addCell(gapCell);
							}

							firstCategory = false;

							// ---------- Category Header ----------
							Cell categoryCell = new Cell()
									.add(new Paragraph(entry.getKey()).simulateBold().setFontColor(ColorConstants.BLACK)
											.setUnderline().setTextAlignment(TextAlignment.LEFT))
									.setBorder(Border.NO_BORDER).setPaddingTop(5).setPaddingBottom(5).setPaddingLeft(8);

							categoryCell.setFontSize(lang == 0 ? 14 : 16);
							itemTable.addCell(categoryCell);

							// ---------- Category Items ----------
							for (MenuAllocationItemsResponseDto item : entry.getValue()) {

								secondBgColor = isEvenRaw ? new DeviceRgb(245, 245, 245) : new DeviceRgb(255, 255, 255);

								String itemName = "";
								StringBuilder qtyBuilder = new StringBuilder();

								if (item.getServiceType().equalsIgnoreCase("counter_wise")
										|| item.getServiceType().equalsIgnoreCase("counter wise")) {

									qtyBuilder.append("( ").append(item.getPax()).append(" ").append(functionPaxLabel)
											.append(" )");

									if (item.getCounterQty() != 0 || item.getHelperQty() != 0) {

										List<String> parts = new ArrayList<>();

										if (item.getCounterQty() != 0) {
											parts.add(item.getCounterQty() + " "
													+ (lang == 1 ? "लेबर" : lang == 2 ? "લેબર" : "Labour"));
										}

										if (item.getHelperQty() != 0) {
											parts.add(item.getHelperQty() + " "
													+ (lang == 1 ? "हेल्पर" : lang == 2 ? "હેલ્પર" : "Helper"));
										}

										if (!parts.isEmpty()) {
											qtyBuilder.append(" (").append(String.join(", ", parts)).append(")");
										}
									}

								} else if (item.getServiceType().equalsIgnoreCase("plate_wise")
										|| item.getServiceType().equalsIgnoreCase("plate wise")) {

									if (item.getQty() != 0) {

										String unit = "";

										if (lang == 1) {
											unit = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "";
										} else if (lang == 2) {
											unit = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "";
										} else {
											unit = item.getUnitName() != null ? item.getUnitName() : "";
										}

										qtyBuilder.append(" (").append(item.getQty()).append(" ").append(unit)
												.append(")");
									}
								}

								String qty = qtyBuilder.toString();

								String remarks = "";
								if (lang == 1) {
									itemName = item.getItemNameHindi();
									remarks = item.getRemarksHindi() != null ? item.getRemarksHindi() : "";
								} else if (lang == 2) {
									itemName = item.getItemNameGujarati();
									remarks = item.getRemarksGujarati() != null ? item.getRemarksGujarati() : "";
								} else {
									itemName = item.getItemName();
									remarks = item.getRemarks() != null ? item.getRemarks() : "";
								}

								cell = new Cell()
										.add(new Paragraph((itemName != null ? itemName : "") + qty
												+ ((item.getRemarks() != null && item.getRemarks().trim().length() != 0)
														? (" (" + remarks + ")")
														: ""))
												.setFont(basicFont).setFontColor(mainColor)
												.setTextAlignment(TextAlignment.LEFT))
										.setBackgroundColor(secondBgColor).setBorder(Border.NO_BORDER)
										.setPaddingLeft(12).setPaddingTop(3).setPaddingBottom(3);

								cell.setFontSize(lang == 0 ? 14 : 16);

								itemTable.addCell(cell);

								isEvenRaw = !isEvenRaw;
							}
						}
						document.add(itemTable);
					}
				}
			}
			addSignatureAtBottom(document, basicFont2);
			document.close();
			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ eventMasterEntity.getEventNo() + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Chitthi")
					+ ".pdf";
			return fullUrl;
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private void addSignatureAtBottom(Document document, PdfFont font) {

		Table signTable = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }));
		signTable.setWidth(UnitValue.createPercentValue(100));

		signTable.addCell(new Cell().add(new Paragraph("Prepared By________________")).setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.LEFT).setFont(font));

		signTable.addCell(new Cell().add(new Paragraph("Submitted By________________")).setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.RIGHT).setFont(font));

		// x, y, width
		signTable.setFixedPosition(document.getPdfDocument().getNumberOfPages(), 50, // left margin
				30, // distance from bottom
				500 // table width
		);

		document.add(signTable);
	}

//	@Override
//	public String generateChefAgencyReportType2(Long eventId, Long eventFunctionId, List<Long> agencyId,
//			List<Long> itemId, Integer isCompanyDetails, String type, String startDate, String endDate,
//			HttpServletRequest re, Integer lang, Long userid, Integer isWithPrice) {
//
//		try {
//			PdfFont basicFont = null;
//			PdfFont boldFont = null;
//			menuPreparationServiceImpl.loadLicense();
//
//			int x = 185;
//			int y = 515;
//
//			String phone, eDate, eVenue, agencyName, email, funLabel, menuItemLabel, dateTimeLabel, qtyLabel,
//					priceLabel, totalPriceLabel, notesLabel, grandTotalLabel;
//			if (lang == 1) {
//				// Hindi
//				System.out.println("Loading Hindi font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
//				System.out.println("Hindi font loaded successfully");
//				phone = "मोबाइल नंबर";
//				eDate = "कार्यक्रम की दिनांक";
//				eVenue = "स्थान";
//				agencyName = "एजेंसी का नाम";
//				y = 512;
//				email = "ईमेल";
//				funLabel = "समारोह";
//				menuItemLabel = "मेनू आइटम";
//				dateTimeLabel = "दिनांक समय";
//				qtyLabel = "मात्रा";
//				notesLabel = "टिप्पणी";
//				priceLabel = "कीमत";
//				totalPriceLabel = "कुल कीमत";
//				grandTotalLabel = "ग्रांड टोटल";
//			} else if (lang == 2) {
//				// Gujarati
//				System.out.println("Loading Gujarati font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
//				System.out.println("Gujarati font loaded successfully");
//				phone = "મોબાઇલ નંબર";
//				eDate = "કાર્યક્રમની તારીખ";
//				eVenue = "સ્થળ";
//				agencyName = "એજન્સીનું નામ";
//				y = 510;
//				email = "ઈમૈલ";
//				funLabel = "કાર્યક્રમ";
//				menuItemLabel = "મેનુ આઇટમ";
//				dateTimeLabel = "તારીખ અને સમય";
//				qtyLabel = "સંખ્યા";
//				notesLabel = "નોંધ";
//				priceLabel = "કિંમત";
//				totalPriceLabel = "કુલ કિંમત";
//				grandTotalLabel = "ગ્રાન્ડ ટોટલ";
//			} else {
//				// English
//				System.out.println("Loading English font...");
////				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//				basicFont = PdfFontFactory.createFont("/fonts/arial-regular.ttf");
//				System.out.println("English font loaded successfully");
//				phone = "Mobile No.";
//				eDate = "Date";
//				eVenue = "Venue";
//				agencyName = "Agency Name";
//				email = "Email";
//				funLabel = "Function";
//				menuItemLabel = "Menu Item Name";
//				dateTimeLabel = "Date & Time";
//				qtyLabel = "Qnt";
//				notesLabel = "Remarks";
//				priceLabel = "Price";
//				totalPriceLabel = "Total Price";
//				grandTotalLabel = "Grand Total";
//			}
//			boldFont = PdfFontFactory.createFont("/fonts/arial-bold.ttf");
//			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
//			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);
//
//			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
//			Date now = new Date();
//			String rootPath = re.getSession().getServletContext().getRealPath("/");
//			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
//			File outputPath = null;
//
//			if (eventMasterEntity == null) {
//				outputPath = new File(rootPath + "resources/tempDownload/" + startDate.replace("/", "_") + "/");
//			} else {
//				outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");
//			}
//
//			if (!outputPath.exists()) {
//				if (outputPath.mkdirs()) {
//					System.out.println("Directory Created!!!");
//				} else {
//					System.out.println("Error!!!");
//				}
//			}
//
//			String fileName = eventMasterEntity.getParty().getNameEnglish();
//
//			if (agencyId.size() == 1) {
//				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);
//
//				if (party != null) {
//					fileName = party.getNameEnglish();
//				}
//			}
//
//			File pdfFile = new File(outputPath + "/"
//					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Report")
//					+ ".pdf");
//
//			PdfWriter writer = new PdfWriter(pdfFile);
//			PdfDocument pdfDocument = new PdfDocument(writer);
//			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
//			document.setMargins(20, 20, 50, 20);
//
//			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponseDtos = getAgencyWithItemType(type,
//					eventId, eventFunctionId, agencyId, itemId, startDate, endDate);
//
//			if (menuAgencyWithItemsResponseDtos.isEmpty()) {
//				return "No agency has been allocated for any items.";
//			}
//
//			for (int i = 0; i < menuAgencyWithItemsResponseDtos.size(); i++) {
//				MenuAllocationAgencyWithItemsResponseDto menuAgencyWithItemsResponseDto = menuAgencyWithItemsResponseDtos
//						.get(i);
//
//				EventFunctionMasterResponseDto functionMasterDto = menuAgencyWithItemsResponseDto.getEventFunction();
//
//				if (functionMasterDto == null) {
//					return "Event Function Not Found ";
//				}
//
//				ImageData mainBgData = menuPreparationServiceImpl
//						.loadImageFromResource("/flipbook/pages/simple_main_1.png");
//				ImageData watermarkBgData = menuPreparationServiceImpl
//						.loadImageFromResource("/flipbook/pages/simple_menu_1.png");
//
//				Color mainColor = new DeviceRgb(0, 0, 0);
//				Cell cell;
//
//				Table headerTable = null;
//				Table companyTable = null;
//
//				String venue = "";
//
//				if (lang == 1) {
//					venue = functionMasterDto.getFunction_venue_hindi() != null
//							? functionMasterDto.getFunction_venue_hindi()
//							: "";
//				} else if (lang == 2) {
//					venue = functionMasterDto.getFunction_venue_gujarati() != null
//							? functionMasterDto.getFunction_venue_gujarati()
//							: "";
//				} else {
//					venue = functionMasterDto.getFunction_venue() != null ? functionMasterDto.getFunction_venue() : "";
//				}
//
//				boolean isFirst = true;
//				for (MenuAllocationAgencyResponseDto agency : menuAgencyWithItemsResponseDto.getAgencyResponse()) {
//					if (!isFirst) {
//						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//					}
//
//					if (isCompanyDetails == 1) {
//						companyTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 2f, 73f }), true);
//						companyTable.setWidth(UnitValue.createPercentValue(100f));
//						ImageData logoData = menuPreparationServiceImpl
////								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//								.loadImageFromResource("/flipbook/pages/logo.png");
//						Image logo = new Image(logoData);
//
//						// Resize & align
//						logo.setWidth(100f);
//						logo.setAutoScale(false);
//						logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
//
//						cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f)
//								.setVerticalAlignment(VerticalAlignment.MIDDLE);
//						companyTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14)
//										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
//								.setBorder(Border.NO_BORDER);
//						companyTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph()
//										.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//												.setFontColor(mainColor).simulateBold())
//										.add(new Text(cmpDto.getOfficeNo() != null
//												? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
//												: "").setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
//								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
//						companyTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph()
//										.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//												.simulateBold().setFontColor(mainColor))
//										.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
//												.setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
//								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
//						companyTable.addCell(cell);
//
//						document.add(companyTable);
//					}
//					headerTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 1f, 33f, 16f, 1f, 29f }),
//							false);
//					headerTable.setWidth(UnitValue.createPercentValue(100));
//					headerTable.setBorder(new SolidBorder(1f));
//
//					cell = new Cell()
//							.add(new Paragraph(agencyName).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(":").setFont(boldFont).setFontSize(14).setFontColor(mainColor)
//							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					String party;
//					if (lang == 1) {
//						party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";
//					} else if (lang == 2) {
//						party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";
//					} else {
//						party = agency.getContactName() != null ? agency.getContactName() : "";
//					}
//					cell = new Cell()
//							.add(new Paragraph(party.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(phone).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).simulateBold().setTextAlignment(TextAlignment.LEFT))
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(":").setFont(boldFont).setFontSize(14).setFontColor(mainColor)
//							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(agency.getNumber()).setFont(basicFont).setFontSize(14)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(eVenue).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
//							.setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(":").setFont(boldFont).setFontSize(14).setFontColor(mainColor)
//							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					cell = new Cell(1, 4).add(new Paragraph(venue).setFont(basicFont).setFontSize(14)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
//					headerTable.addCell(cell);
//
//					document.add(headerTable);
//
//					Table itemTable = null;
//					if (isWithPrice == 1) {
//						itemTable = new Table(
//								UnitValue.createPercentArray(new float[] { 15f, 20f, 15f, 12f, 14f, 9f, 15f }));
//					} else {
//						itemTable = new Table(UnitValue.createPercentArray(new float[] { 19f, 30f, 23f, 15f, 25f }));
//					}
//
//					itemTable.setWidth(UnitValue.createPercentValue(100f));
//					itemTable.setFixedLayout();
//					itemTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
//					itemTable.setMarginLeft(0);
//					itemTable.setMarginTop(10f);
//
//					cell = new Cell().add(new Paragraph(funLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(menuItemLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					cell = new Cell()
//							.add(new Paragraph(dateTimeLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					cell = new Cell().add(new Paragraph(qtyLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					if (isWithPrice == 1) {
//						cell = new Cell()
//								.add(new Paragraph(priceLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//						itemTable.addCell(cell);
//
//						cell = new Cell()
//								.add(new Paragraph(totalPriceLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//						itemTable.addCell(cell);
//					}
//
//					cell = new Cell().add(new Paragraph(notesLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
//							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
//					itemTable.addCell(cell);
//
//					String functionName = "";
//					String itemName = "";
//					Paragraph p;
//					Long grandTotal = 0l;
//					for (MenuAllocationItemsResponseDto item : agency.getAllocationItems()) {
//						String qty = "";
//						String price = "";
//						Long totalPrice = 0l;
//						StringBuilder qtyBuilder = new StringBuilder();
//						StringBuilder priceBuilder = new StringBuilder();
//						if (item.getServiceType().equalsIgnoreCase("counter_wise")
//								|| item.getServiceType().equalsIgnoreCase("counter wise")) {
//							if (item.getCounterQty() != 0 || item.getHelperQty() != 0) {
//
//								List<String> parts = new ArrayList<>();
//
//								if (item.getCounterQty() != 0) {
//									parts.add((lang == 1 ? "लेबर" : lang == 2 ? "લેબર" : "Labour") + " : "
//											+ item.getCounterQty());
//								}
//
//								if (item.getHelperQty() != 0) {
//									parts.add((lang == 1 ? "हेल्पर" : lang == 2 ? "હેલ્પર" : "Helper") + " : "
//											+ item.getHelperQty());
//								}
//
//								if (!parts.isEmpty()) {
//									qtyBuilder.append(String.join(", ", parts));
//								}
//							}
//
//							if (item.getCounterPrice() != 0 || item.getCounterPrice() != 0) {
//								List<String> parts = new ArrayList<>();
//								if (item.getCounterPrice() != 0) {
//									parts.add((lang == 1 ? "लेबर" : lang == 2 ? "લેબર" : "Labour") + " : "
//											+ item.getCounterPrice());
//									totalPrice = totalPrice + (item.getCounterQty() * item.getCounterPrice());
//								}
//
//								if (item.getHelperPrice() != 0) {
//									parts.add((lang == 1 ? "हेल्पर" : lang == 2 ? "હેલ્પર" : "Helper") + " : "
//											+ item.getHelperPrice());
//									totalPrice = totalPrice + (item.getHelperQty() * item.getHelperPrice());
//								}
//
//								if (!parts.isEmpty()) {
//									priceBuilder.append(String.join(", ", parts));
//								}
//							}
//						} else if (item.getServiceType().equalsIgnoreCase("plate_wise")
//								|| item.getServiceType().equalsIgnoreCase("plate wise")) {
//							if (item.getQty() != 0) {
//								String unit = "";
//								if (lang == 1) {
//									unit = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "";
//								} else if (lang == 2) {
//									unit = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "";
//								} else {
//									unit = item.getUnitName() != null ? item.getUnitName() : "";
//								}
//								qtyBuilder.append(item.getQty()).append(" ").append(unit);
//							}
//
//							if (item.getPrice() != 0) {
//								priceBuilder.append(item.getPrice());
//								totalPrice = totalPrice + (item.getQty() * item.getPrice());
//							}
//						}
//						qty = qtyBuilder.toString();
//						price = priceBuilder.toString();
//						grandTotal = grandTotal + totalPrice;
//
//						if (lang == 1) {
//							functionName = functionMasterDto.getFunction().getNameHindi();
//							itemName = item.getItemNameHindi();
//						} else if (lang == 2) {
//							functionName = functionMasterDto.getFunction().getNameGujarati();
//							itemName = item.getItemNameGujarati();
//						} else {
//							functionName = functionMasterDto.getFunction().getNameEnglish();
//							itemName = item.getItemName();
//						}
//
//						p = new Paragraph(qty.toString()).setFont(basicFont).setFontSize(lang == 0 ? 11 : 13)
//								.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT);
//
//						cell = new Cell().add(
//								new Paragraph(functionName.toUpperCase()).setFont(basicFont).setFontColor(mainColor));
//
//						if (lang == 0) {
//							cell.setFontSize(11);
//						} else {
//							cell.setFontSize(13);
//						}
//						itemTable.addCell(cell);
//
//						cell = new Cell().add(new Paragraph(itemName).setFont(basicFont).setFontColor(mainColor)
//								.setTextAlignment(TextAlignment.LEFT));
//
//						if (lang == 0) {
//							cell.setFontSize(11);
//						} else {
//							cell.setFontSize(13);
//						}
//
//						itemTable.addCell(cell);
//
//						cell = new Cell().add(new Paragraph(functionMasterDto.getFunctionStartDateTime().toUpperCase())
//								.setFont(basicFont).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)
//								.setFontSize(11));
//
//						itemTable.addCell(cell);
//
//						cell = new Cell().add(p);
//						itemTable.addCell(cell);
//
//						if (isWithPrice == 1) {
//							cell = new Cell().add(new Paragraph(price != null ? price : "").setFont(basicFont)
//									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//
//							if (lang == 0) {
//								cell.setFontSize(11);
//							} else {
//								cell.setFontSize(13);
//							}
//
//							itemTable.addCell(cell);
//
//							cell = new Cell().add(new Paragraph(totalPrice != null ? totalPrice.toString() : "0")
//									.setFont(basicFont).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//
//							if (lang == 0) {
//								cell.setFontSize(11);
//							} else {
//								cell.setFontSize(13);
//							}
//
//							itemTable.addCell(cell);
//						}
//
//						cell = new Cell().add(new Paragraph(item.getRemarks() != null ? item.getRemarks() : "")
//								.setFont(basicFont).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//
//						if (lang == 0) {
//							cell.setFontSize(11);
//						} else {
//							cell.setFontSize(13);
//						}
//
//						itemTable.addCell(cell);
//					}
//					isFirst = false;
//
//					if (isWithPrice == 1) {
//						cell = new Cell(1, 5).add(new Paragraph(grandTotalLabel).setFont(basicFont).simulateBold()
//								.setFontColor(mainColor).setTextAlignment(TextAlignment.RIGHT));
//						if (lang == 0) {
//							cell.setFontSize(11);
//						} else {
//							cell.setFontSize(13);
//						}
//						itemTable.addCell(cell);
//
//						cell = new Cell(1, 2).add(new Paragraph(grandTotal.toString()).setFont(basicFont)
//								.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT));
//						if (lang == 0) {
//							cell.setFontSize(11);
//						} else {
//							cell.setFontSize(13);
//						}
//						itemTable.addCell(cell);
//					}
//
//					document.add(itemTable);
//				}
//				if (i < menuAgencyWithItemsResponseDtos.size() - 1) {
//					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//				}
//			}
//
//			document.close();
//
//			String fullUrl = "";
//
//			fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventMasterEntity.getEventNo()
//					+ "/"
//					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Report")
//					+ ".pdf";
//
//			return fullUrl;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Failed to generate Event Menu Report", e);
//		}
//	}

	@Override
	public String generateChefAgencyReportType2(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, String startDate, String endDate,
			HttpServletRequest re, Integer lang, Long userid, Integer isWithPrice, Integer isAgencyNextPage) {

		try {

			if (isAgencyNextPage == null) {
				isAgencyNextPage = 1;
			}
			PdfFont basicFont = null;
			PdfFont boldFont = null;

			menuPreparationServiceImpl.loadLicense();

			String phone, eDate, eVenue, agencyName, email, funLabel, menuItemLabel, dateTimeLabel, qtyLabel,
					priceLabel, totalPriceLabel, notesLabel, grandTotalLabel;

			if (lang == 1) {

				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");

				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				dateTimeLabel = "दिनांक समय";
				qtyLabel = "मात्रा";
				notesLabel = "टिप्पणी";
				priceLabel = "कीमत";
				totalPriceLabel = "कुल कीमत";
				grandTotalLabel = "ग्रांड टोटल";

			} else if (lang == 2) {

				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");

				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				dateTimeLabel = "તારીખ અને સમય";
				qtyLabel = "સંખ્યા";
				notesLabel = "નોંધ";
				priceLabel = "કિંમત";
				totalPriceLabel = "કુલ કિંમત";
				grandTotalLabel = "ગ્રાન્ડ ટોટલ";

			} else {

				basicFont = PdfFontFactory.createFont("/fonts/arial-regular.ttf");

				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				dateTimeLabel = "Date & Time";
				qtyLabel = "Qnt";
				notesLabel = "Remarks";
				priceLabel = "Price";
				totalPriceLabel = "Total Price";
				grandTotalLabel = "Grand Total";
			}

			boldFont = PdfFontFactory.createFont("/fonts/arial-bold.ttf");

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			if (eventMasterEntity == null) {
				return "Event not found";
			}

			String rootPath = re.getSession().getServletContext().getRealPath("/");

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {

				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);

			PdfDocument pdfDocument = new PdfDocument(writer);

			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);

			document.setMargins(20, 20, 50, 20);

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponseDtos = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, startDate, endDate);

			if (menuAgencyWithItemsResponseDtos.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			Color mainColor = new DeviceRgb(0, 0, 0);

			Cell cell;

			for (int i = 0; i < menuAgencyWithItemsResponseDtos.size(); i++) {

				MenuAllocationAgencyWithItemsResponseDto menuAgencyWithItemsResponseDto = menuAgencyWithItemsResponseDtos
						.get(i);

				EventFunctionMasterResponseDto functionMasterDto = menuAgencyWithItemsResponseDto.getEventFunction();

				if (functionMasterDto == null) {
					return "Event Function Not Found";
				}

				boolean isFirst = true;

				for (MenuAllocationAgencyResponseDto agency : menuAgencyWithItemsResponseDto.getAgencyResponse()) {

					if (!isFirst && isAgencyNextPage == 1) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					}

					isFirst = false;

					// COMPANY TABLE
					if (isCompanyDetails == 1) {

						Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 2f, 73f }),
								true);

						companyTable.setWidth(UnitValue.createPercentValue(100f));

//	                    ImageData logoData = menuPreparationServiceImpl
//	                            .loadImageFromResource("/flipbook/pages/logo.png");
						ImageData logoData = menuPreparationServiceImpl
								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());

						Image logo = new Image(logoData);

						logo.setWidth(100f);

						cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER);

						companyTable.addCell(cell);

						companyTable.addCell(
								new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14))
										.setBorder(Border.NO_BORDER));

						companyTable
								.addCell(new Cell().add(new Paragraph(phone + " : "
										+ (cmpDto.getOfficeNo() != null
												? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
												: ""))
										.setFont(basicFont)).setBorder(Border.NO_BORDER));

						companyTable.addCell(new Cell().add(new Paragraph(
								email + " : " + (cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : ""))
								.setFont(basicFont)).setBorder(Border.NO_BORDER));

						document.add(companyTable);
					}

					// HEADER TABLE
					Table headerTable = new Table(
							UnitValue.createPercentArray(new float[] { 20f, 1f, 33f, 16f, 1f, 29f }), false);

					headerTable.setWidth(UnitValue.createPercentValue(100));

					headerTable.setMarginTop(20f);

					headerTable.setBorder(new SolidBorder(1f));

					headerTable.addCell(new Cell().add(new Paragraph(agencyName).setFont(basicFont).simulateBold())
							.setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER));

					String party;

					if (lang == 1) {

						party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";

					} else if (lang == 2) {

						party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";

					} else {

						party = agency.getContactName() != null ? agency.getContactName() : "";
					}

					headerTable.addCell(
							new Cell().add(new Paragraph(party).setFont(basicFont)).setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell().add(new Paragraph(phone).setFont(basicFont).simulateBold())
							.setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER));

					headerTable.addCell(new Cell()
							.add(new Paragraph(agency.getNumber() != null ? agency.getNumber() : "").setFont(basicFont))
							.setBorder(Border.NO_BORDER));

					document.add(headerTable);

					// PLACE WISE GROUPING
					Map<String, List<MenuAllocationItemsResponseDto>> placeWiseMap = agency.getAllocationItems()
							.stream()
							.collect(Collectors
									.groupingBy(item -> item.getPlace() != null && !item.getPlace().trim().isEmpty()
											? item.getPlace()
											: "-", LinkedHashMap::new, Collectors.toList()));

					Long grandTotal = 0L;

					for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> placeEntry : placeWiseMap.entrySet()) {

						String placeName = placeEntry.getKey();

						List<MenuAllocationItemsResponseDto> placeItems = placeEntry.getValue();

						if (placeName.equalsIgnoreCase("venue") || placeName.equalsIgnoreCase("at venue")
								|| placeName.equalsIgnoreCase("0")) {

						
							List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
									.findBanquetByEventFunctionId(eventId,
											functionMasterDto.getId());
							if (!banquets.isEmpty()) {
								placeName = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
										.collect(Collectors.joining(", "));
							} else {
								placeName = lang == 1 ? functionMasterDto.getFunction_venue_hindi()
										: lang == 2 ? functionMasterDto.getFunction_venue_gujarati() : functionMasterDto.getFunction_venue();
							}

						} else {

							try {
								UserGodownEntity userGodownEntity = userGodownRepository
										.findByIdAndIsDeleteFalse(Long.valueOf(placeName)).orElse(null);

								if (userGodownEntity == null) {
									placeName = "";
								} else if (lang == 0) {
									placeName = userGodownEntity.getAddressEnglish();
								} else if (lang == 1) {
									placeName = userGodownEntity.getAddressHindi();
								} else {
									placeName = userGodownEntity.getAddressGujarati();
								}

							} catch (Exception e) {
								placeName = "";
							}
						}

						// VENUE TITLE
						Paragraph venuePara = new Paragraph()
								.add(new Text(eVenue + " : ").setFont(basicFont).simulateBold())
								.add(new Text(placeName != null ? placeName : "").setFont(basicFont))
								.setFontSize(lang == 0 ? 14 : 16).setMarginTop(10f);

						document.add(venuePara);

						// ITEM TABLE
						Table itemTable;

						if (isWithPrice == 1) {
							itemTable = new Table(
									UnitValue.createPercentArray(new float[] { 15f, 20f, 15f, 12f, 14f, 9f, 15f }));
						} else {
							itemTable = new Table(
									UnitValue.createPercentArray(new float[] { 19f, 30f, 23f, 15f, 25f }));
						}

						itemTable.setWidth(UnitValue.createPercentValue(100f));

						// HEADER
						itemTable.addCell(new Cell().add(new Paragraph(funLabel).setFont(basicFont).simulateBold()));

						itemTable.addCell(
								new Cell().add(new Paragraph(menuItemLabel).setFont(basicFont).simulateBold()));

						itemTable.addCell(
								new Cell().add(new Paragraph(dateTimeLabel).setFont(basicFont).simulateBold()));

						itemTable.addCell(new Cell().add(new Paragraph(qtyLabel).setFont(basicFont).simulateBold()));

						if (isWithPrice == 1) {

							itemTable.addCell(
									new Cell().add(new Paragraph(priceLabel).setFont(basicFont).simulateBold()));

							itemTable.addCell(
									new Cell().add(new Paragraph(totalPriceLabel).setFont(basicFont).simulateBold()));
						}

						itemTable.addCell(new Cell().add(new Paragraph(notesLabel).setFont(basicFont).simulateBold()));

						// ITEMS
						for (MenuAllocationItemsResponseDto item : placeItems) {

							String functionName;
							String itemName;
							String remarks = "";
							if (lang == 1) {

								functionName = functionMasterDto.getFunction().getNameHindi();

								itemName = item.getItemNameHindi();
								remarks = item.getRemarksHindi() != null ? item.getRemarksHindi() : "";

							} else if (lang == 2) {

								functionName = functionMasterDto.getFunction().getNameGujarati();

								itemName = item.getItemNameGujarati();

								remarks = item.getRemarksGujarati() != null ? item.getRemarksGujarati() : "";
							} else {

								functionName = functionMasterDto.getFunction().getNameEnglish();

								itemName = item.getItemName();

								remarks = item.getRemarks() != null ? item.getRemarks() : "";
							}

							String qty = "";
							String price = "";
							Long totalPrice = 0L;

							if (item.getServiceType().equalsIgnoreCase("counter_wise")
									|| item.getServiceType().equalsIgnoreCase("counter wise")) {

								qty = "Labour : " + item.getCounterQty() + ", Helper : " + item.getHelperQty();

								price = "Labour : " + item.getCounterPrice() + ", Helper : " + item.getHelperPrice();

								totalPrice = Long.valueOf((item.getCounterQty() * item.getCounterPrice())
										+ (item.getHelperQty() * item.getHelperPrice()));

							} else {

								qty = item.getQty() + " " + (item.getUnitName() != null ? item.getUnitName() : "");

								price = String.valueOf(item.getPrice());

								totalPrice = Long.valueOf(item.getQty() * item.getPrice());
							}

							grandTotal += totalPrice;

							itemTable.addCell(new Cell().add(new Paragraph(functionName).setFont(basicFont)));

							itemTable.addCell(new Cell().add(new Paragraph(itemName).setFont(basicFont)));

							itemTable.addCell(new Cell().add(
									new Paragraph(functionMasterDto.getFunctionStartDateTime()).setFont(basicFont)));

							itemTable.addCell(new Cell().add(new Paragraph(qty).setFont(basicFont)));

							if (isWithPrice == 1) {

								itemTable.addCell(new Cell().add(new Paragraph(price).setFont(basicFont)));

								itemTable.addCell(
										new Cell().add(new Paragraph(String.valueOf(totalPrice)).setFont(basicFont)));
							}

							itemTable.addCell(new Cell().add(new Paragraph(remarks).setFont(basicFont)));
						}

						// GRAND TOTAL
						if (isWithPrice == 1) {

							itemTable.addCell(
									new Cell(1, 5).add(new Paragraph(grandTotalLabel).setFont(basicFont).simulateBold())
											.setTextAlignment(TextAlignment.RIGHT));

							itemTable.addCell(
									new Cell(1, 2).add(new Paragraph(grandTotal.toString()).setFont(basicFont)));
						}

						document.add(itemTable);
					}
				}

				if (i < menuAgencyWithItemsResponseDtos.size() - 1) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + eventMasterEntity.getEventNo()
					+ "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Report")
					+ ".pdf";

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private Cell headerCell(String text, PdfFont font, Integer lang) {
		return new Cell()
				.add(new Paragraph(text).setFont(font).simulateBold().setFontSize(lang == 0 ? 13f : 15f)
						.setTextAlignment(TextAlignment.CENTER))
				.setBorder(new SolidBorder(ColorConstants.BLACK, 0.8f)).setPadding(5);
	}

	private Cell dataCell(String text, PdfFont font) {
		return new Cell().add(new Paragraph(text).setFont(font).setFontSize(13f).setFixedLeading(13f)).setPadding(4)
				.setKeepTogether(true).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f));
	}

	@Override
	public String generateChefAgencyReportType3(Long eventId, Long eventFunctionId, Integer isCompanyDetails,
			String type, HttpServletRequest re, Integer lang, Long userid, List<Long> agencyId, List<Long> itemId) {
		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "Event Not Found";

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			List<AgencyRawMaterialReportResponseDto> list = eventRawMaterialService.mapAgencyRawMaterialReport(eventId,
					lang, agencyId, itemId);

			if (list == null || list.isEmpty()) {
				return "No agency has been allocated for any RawMaterial.";
			}

			Table table;

			ChefWiseRawMaterialHeaderEventHandler headerHandler = null;

			// List to store all agency PDF files
			List<File> agencyPdfFiles = new ArrayList<>();

			for (AgencyRawMaterialReportResponseDto agency : list) {
				CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);
				ImageData logo = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//						.loadImageFromResource("/flipbook/pages/logo.png");

				/* ================= FONTS ================= */
				PdfFont font;
				PdfFont fontBold;
				String customer = "", venue = "", nameLabel, weightLabel;

				if (lang == 1) {
					font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
					customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
					venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
					nameLabel = "नाम";
					weightLabel = "वज़न";
				} else if (lang == 2) {
					font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
					fontBold = font;
					customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
					venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
					nameLabel = "નામ";
					weightLabel = "વજન";
				} else {
					font = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
					fontBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
					customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
					venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
					nameLabel = "Name";
					weightLabel = "Weight";
				}

				// Create separate PDF file for this agency
				File agencyPdfFile = new File(dir, "RawMaterial_Agency_" + agency.getAgencyId() + "_" + ts + ".pdf");
				PdfDocument pdfDoc = new PdfDocument(new PdfWriter(agencyPdfFile));
				Document document = new Document(pdfDoc, PageSize.A4);
				document.setMargins(110, 40, 60, 40);

				table = new Table(UnitValue.createPercentArray(new float[] { 60f, 40f }), true);
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginBottom(12);
				table.addHeaderCell(headerCell(nameLabel, font, lang));
				table.addHeaderCell(headerCell(weightLabel, font, lang));
				List<String> itemList = new ArrayList<>();

				for (AgencyMenuCategoryResponseDto cat : agency.getMenuCategories()) {
					for (AgencyMenuItemResponseDto menuItem : cat.getMenuItems()) {
						if (lang == 1) {
							itemList.add(menuItem.getMenuItemNameHindi() + " - " + menuItem.getPax());
						} else if (lang == 2) {
							itemList.add(menuItem.getMenuItemNameGujarati() + " - " + menuItem.getPax());
						} else {
							itemList.add(menuItem.getMenuItemNameEnglish() + " - " + menuItem.getPax());
						}
					}
				}

				String item = String.join(", ", itemList);

				if (headerHandler != null) {
					pdfDoc.removeEventHandler(headerHandler);
				}

				Table headerTable = createHeaderTable(font, fontBold, logo,
						(lang == 0 ? customer.toUpperCase() : customer), (lang == 0 ? venue.toUpperCase() : venue),
						dateTime, agency.getAgencyName(), item, true, isCompanyDetails, lang);

				float leftMargin = 40;
				float rightMargin = 40;
				float topMargin = 20;
				float top = PageSize.A4.getTop() - topMargin - calculateTableHeight(headerTable, pdfDoc);
				float bottom = PageSize.A4.getBottom() + 60;
				float height = top - bottom;
				float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
				float columnWidth = usableWidth / 3;

				Rectangle[] columns = new Rectangle[] { new Rectangle(leftMargin, bottom, columnWidth, height),
						new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height),
						new Rectangle(leftMargin + (columnWidth * 2), bottom, columnWidth, height) };

				document.setRenderer(new ColumnDocumentRenderer(document, columns));

				headerHandler = new ChefWiseRawMaterialHeaderEventHandler(headerTable, topMargin);
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

				for (AgencyRawMaterialCategoryResponseDto rawCat : agency.getRawMaterials()) {
					String rawCatName = "";
					if (lang == 1) {
						rawCatName = rawCat.getRawCatNameHindi() != null ? rawCat.getRawCatNameHindi() : "";
					} else if (lang == 2) {
						rawCatName = rawCat.getRawCatNameGujarati() != null ? rawCat.getRawCatNameGujarati() : "";
					} else {
						rawCatName = rawCat.getRawCatNameEnglish() != null ? rawCat.getRawCatNameEnglish().toUpperCase()
								: "";
					}

					table.addCell(new Cell(1, 2)
							.add(new Paragraph(rawCatName).setFontSize(lang == 0 ? 13f : 15f)
									.setBorder(Border.NO_BORDER).setFont(fontBold))
							.setBackgroundColor(ColorConstants.LIGHT_GRAY));

					for (AgencyRawMaterialItemsResponseDto rm : rawCat.getRawItems()) {
						String rawItemName = "";
						if (lang == 1) {
							rawItemName = rm.getRawItemNameHindi() != null ? rm.getRawItemNameHindi() : "";
						} else if (lang == 2) {
							rawItemName = rm.getRawItemNameGujarati() != null ? rm.getRawItemNameGujarati() : "";
						} else {
							rawItemName = rm.getRawItemNameEnglish() != null ? rm.getRawItemNameEnglish().toUpperCase()
									: "";
						}

						UnitMasterEntity unit = unitMasterRepository.findById(rm.getUnitId()).orElse(null);
						table.addCell(dataCell(rawItemName, font).setKeepTogether(true));
						table.addCell(dataCell(rm.getTotalWeight() + " " + rm.getUnitName().toUpperCase(), font)
								.setTextAlignment(TextAlignment.RIGHT).setKeepTogether(true));
					}
				}
				document.add(table);
				document.close();

				// Add this agency's PDF to the list
				agencyPdfFiles.add(agencyPdfFile);
			}

			// Merge all agency PDFs into one final PDF
			File finalPdfFile = new File(dir + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]), "Chef wise raw material report")
					+ ".pdf");
			mergePdfsAndAddFooter(agencyPdfFiles, finalPdfFile, dateTime);

			// Optional: Delete individual agency PDFs after merging
			for (File agencyFile : agencyPdfFiles) {
				agencyFile.delete();
			}

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + event.getEventNo() + "/"
					+ getReportName(event.getParty().getNameEnglish(),
							formatDate(event.getEventStartDateTime().split(" ")[0]), "Chef wise raw material report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-2 PDF generation failed", e);
		}
	}

	private void mergePdfsAndAddFooter(List<File> pdfFiles, File outputFile, String dateTime) throws IOException {
		int totalPages = 0;
		for (File pdfFile : pdfFiles) {
			PdfDocument tempPdf = new PdfDocument(new PdfReader(pdfFile));
			totalPages += tempPdf.getNumberOfPages();
			tempPdf.close();
		}

		PdfDocument mergedPdf = new PdfDocument(new PdfWriter(outputFile));
		int currentPageNumber = 1;

		for (File pdfFile : pdfFiles) {
			PdfDocument sourcePdf = new PdfDocument(new PdfReader(pdfFile));
			int sourcePdfPages = sourcePdf.getNumberOfPages();

			for (int i = 1; i <= sourcePdfPages; i++) {
				sourcePdf.copyPagesTo(i, i, mergedPdf);
				PdfPage page = mergedPdf.getPage(currentPageNumber);

				PdfCanvas canvas = new PdfCanvas(page);
				Canvas footerCanvas = new Canvas(canvas, page.getPageSize());

				footerCanvas.showTextAligned(new Paragraph("Signature: ________________").setFontSize(10), 60, 40,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM);

				footerCanvas.showTextAligned(
						new Paragraph("Page " + currentPageNumber + " of " + totalPages).setFontSize(10),
						PageSize.A4.getWidth() / 2, 40, TextAlignment.CENTER, VerticalAlignment.BOTTOM);

				footerCanvas.showTextAligned(new Paragraph(dateTime.toUpperCase()).setFontSize(10),
						PageSize.A4.getWidth() - 60, 40, TextAlignment.RIGHT, VerticalAlignment.BOTTOM);

				footerCanvas.close();
				currentPageNumber++;
			}

			sourcePdf.close();
		}

		mergedPdf.close();
	}

	@Override
	public String generateChefAgencyReportType4(Long eventId, Long eventFunctionId, Integer isCompanyDetails,
			String type, HttpServletRequest re, Integer lang, Long userid, List<Long> agencyId, List<Long> itemId) {
		try {
			/* ================= LICENSE ================= */
			menuPreparationServiceImpl.loadLicense();

			EventMasterResponseDto event = eventMasterService.getEventMasterById(eventId);
			if (event == null)
				return "Event Data Not Found";

			/* ================= FONTS ================= */
			PdfFont font;
			PdfFont fontBold;
			String customer = "", venue = "", nameLabel, weightLabel;

			if (lang == 1) {
				font = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/Nirmala-Bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameHindi() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameHindi() : "";
				nameLabel = "नाम";
				weightLabel = "वज़न";
			} else if (lang == 2) {
				font = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				fontBold = font;
				customer = event.getParty() != null ? event.getParty().getNameGujarati() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameGujarati() : "";
				nameLabel = "નામ";
				weightLabel = "વજન";
			} else {
				font = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				fontBold = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
				customer = event.getParty() != null ? event.getParty().getNameEnglish() : "";
				venue = event.getVenue() != null ? event.getVenue().getNameEnglish() : "";
				nameLabel = "Name";
				weightLabel = "Weight";
			}

			String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

			/* ================= FILE ================= */
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			File dir = new File(rootPath + "resources/tempDownload/" + event.getEventNo());
			if (!dir.exists())
				dir.mkdirs();

			File pdfFile = new File(
					dir + "/"
							+ menuPreparationServiceImpl.getReportName(event.getEventStartDateTime().toString(),
									event.getEventStartDateTime().toString(), "Chef wise raw material report")
							+ ".pdf");

			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDoc, PageSize.A4);
			document.setMargins(110, 40, 60, 40); // extra bottom for footer

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			/* ================= HEADER ================= */
			ImageData logo = menuPreparationServiceImpl
					.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//					.loadImageFromResource("/flipbook/pages/logo.png");

			List<AgencyRawMaterialReportResponseDto> list = eventRawMaterialService.mapAgencyRawMaterialReport(eventId,
					lang, agencyId, itemId);

			if (list == null || list.isEmpty()) {
				return "No agency has been allocated for any RawMaterial.";
			}

			boolean firstAgency = true;
			Table table;

			ChefWiseRawMaterialHeaderEventHandler headerHandler = null;

			for (AgencyRawMaterialReportResponseDto agency : list) {
				table = new Table(UnitValue.createPercentArray(new float[] { 60f, 40f }), true);
				table.setWidth(UnitValue.createPercentValue(100));
				table.setMarginBottom(12);
				table.addHeaderCell(headerCell(nameLabel, fontBold, lang));
				table.addHeaderCell(headerCell(weightLabel, fontBold, lang));
				Table headerTable = createHeaderTable(font, fontBold, logo,
						(lang == 0 ? customer.toUpperCase() : customer), (lang == 0 ? venue.toUpperCase() : venue),
						dateTime, agency.getAgencyName(), "", false, isCompanyDetails, lang);

				if (headerHandler != null) {
					pdfDoc.removeEventHandler(headerHandler);
				}

				float leftMargin = 40;
				float rightMargin = 40;
				float topMargin = 20;
				float top = PageSize.A4.getTop() - topMargin - calculateTableHeight(headerTable, pdfDoc);
				float bottom = PageSize.A4.getBottom() + 60;
				float height = top - bottom;
				float usableWidth = PageSize.A4.getWidth() - leftMargin - rightMargin;
				float columnWidth = usableWidth / 3;

				Rectangle[] columns = new Rectangle[] {
						// Column 1
						new Rectangle(leftMargin, bottom, columnWidth, height),

						// Column 2
						new Rectangle(leftMargin + columnWidth, bottom, columnWidth, height),

						// Column 3
						new Rectangle(leftMargin + (columnWidth * 2), bottom, columnWidth, height) };

				document.setRenderer(new ColumnDocumentRenderer(document, columns));

				headerHandler = new ChefWiseRawMaterialHeaderEventHandler(headerTable, topMargin);
				pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);
				/* ================= FOOTER ================= */
				pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(font));

				if (!firstAgency) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
				firstAgency = false;
				for (AgencyRawMaterialCategoryResponseDto rawCat : agency.getRawMaterials()) {
					/* ---------- HEADER ROW ---------- */

					String rawCatName = "";
					if (lang == 1) {
						rawCatName = rawCat.getRawCatNameHindi() != null ? rawCat.getRawCatNameHindi() : "";
					} else if (lang == 2) {
						rawCatName = rawCat.getRawCatNameGujarati() != null ? rawCat.getRawCatNameGujarati() : "";
					} else {
						rawCatName = rawCat.getRawCatNameEnglish() != null ? rawCat.getRawCatNameEnglish().toUpperCase()
								: "";
					}

					table.addCell(new Cell(1, 2)
							.add(new Paragraph(rawCatName).setFontSize(lang == 0 ? 13f : 15f)
									.setBorder(Border.NO_BORDER).setFont(fontBold))
							.setBackgroundColor(ColorConstants.LIGHT_GRAY));

					/* ---------- DATA ROWS ---------- */
					for (AgencyRawMaterialItemsResponseDto rm : rawCat.getRawItems()) {
						String rawItemName = "";
						if (lang == 1) {
							rawItemName = rm.getRawItemNameHindi() != null ? rm.getRawItemNameHindi() : "";
						} else if (lang == 2) {
							rawItemName = rm.getRawItemNameGujarati() != null ? rm.getRawItemNameGujarati() : "";
						} else {
							rawItemName = rm.getRawItemNameEnglish() != null ? rm.getRawItemNameEnglish().toUpperCase()
									: "";
						}

						table.addCell(dataCell(rawItemName, font));
						table.addCell(dataCell(rm.getTotalWeight() + " " + rm.getUnitName().toUpperCase(), font)
								.setTextAlignment(TextAlignment.RIGHT));
					}
				}
				document.add(table);
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + event.getEventNo() + "/"
					+ menuPreparationServiceImpl.getReportName(event.getEventStartDateTime().toString(),
							event.getEventStartDateTime().toString(), "Chef wise raw material report")
					+ ".pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Raw Material Type-2 PDF generation failed", e);
		}
	}

	// date wise with & without price
	@Override
	public String generateChefAgencyReportType8(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, String startDate, String endDate, Long partyId,
			HttpServletRequest re, Integer lang, Long userid, Integer withPrice) {
		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String functionName, phone, eDate, eVenue, email, funLabel, menuItemLabel, timeLabel, qtyLabel, unitLabel,
					notesLabel, rateLabel, totalLabel, totalMasterLabel;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 512;
				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की दिनांक";
				eVenue = "स्थान";
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				timeLabel = "समय";
				qtyLabel = "मात्रा";
				unitLabel = "मात्रक";
				notesLabel = "टिप्पणी";
				rateLabel = "भाव";
				totalLabel = "टोटल";
				totalMasterLabel = "ग्रैंड टोटल";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 510;
				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "સ્થળ";
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				timeLabel = "સમય";
				qtyLabel = "સંખ્યા";
				unitLabel = "એકમ";
				notesLabel = "નોંધ";
				rateLabel = "ભાવ";
				totalLabel = "ટોટલ";
				totalMasterLabel = "ગ્રાન્ડ ટોટલ";
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				timeLabel = "Time";
				qtyLabel = "Quantity";
				unitLabel = "Unit";
				notesLabel = "Remarks";
				rateLabel = "Rate";
				totalLabel = "Total";
				totalMasterLabel = "Grand Total";
			}
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);
			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);

			File outputPath = null;
			if (eventMasterEntity == null) {
				outputPath = new File(rootPath + "resources/tempDownload/" + startDate.replace("/", "_") + "/");
			} else {
				outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");
			}

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/ChefAgency_" + datetimeforfile + ".pdf");
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 50, 20);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color backColor = new DeviceRgb(33, 33, 33);
			Color grayColor = new DeviceRgb(209, 209, 209);
			Color whiteColor = new DeviceRgb(255, 254, 247);
			Cell cell;

			Table companyTable = null;

//			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponseDtos = getAgencyWithItemType(type,
//					eventId, eventFunctionId, agencyId, itemId, startDate, endDate);
			List<AgencyDataForDatewiseReportResponseDto> data = buildDtoForDateWiseReport(userid, startDate, endDate,
					type, agencyId, partyId);

			if (data == null || data.isEmpty()) {
				return "";
			}
			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new RawMaterialFooterEventHandler(basicFont));

			companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);
			if (isCompanyDetails == 1) {

				ImageData logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//						.loadImageFromResource("/flipbook/pages/logo.png");
				Image logo = new Image(logoData);

				// Resize & align
				logo.setWidth(100f);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f);
				companyTable.addCell(cell);

				cell = new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).simulateBold()
						.setFontSize(14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getOfficeNo() != null
										? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
										: "").setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
										.setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
				companyTable.addCell(cell);
			}

			cell = new Cell(1, 3).add(new Paragraph("Date Wise Chef Labour Order Report").setFixedLeading(20))
					.setFont(basicFont).setFontSize(20).simulateBold().setTextAlignment(TextAlignment.CENTER)
					.setBorder(Border.NO_BORDER);
			companyTable.addCell(cell);

			cell = new Cell(1, 3).add(new Paragraph(startDate + " to " + endDate).setFixedLeading(14))
					.setFont(basicFont).setFontSize(14).setPaddingTop(5f).setTextAlignment(TextAlignment.CENTER)
					.setBorder(Border.NO_BORDER);
			companyTable.addCell(cell);

			cell = new Cell(1, 3).add(new Paragraph("")).setFont(basicFont).setHeight(10f).setBorder(Border.NO_BORDER);
			companyTable.addCell(cell);

			document.add(companyTable);

			Long totalMaster = 0l;
			Table agencyTable = null;
			for (AgencyDataForDatewiseReportResponseDto agency : data) {
				List<FnDetailsForDatewiseReportResponseDto> functions = agency.getFunctions();
				agencyTable = new Table(
						UnitValue.createPercentArray(new float[] { 3f, 3f, 25f, 12f, 8f, 8f, 24f, 17f }));
				agencyTable.setWidth(UnitValue.createPercentValue(100f));
				agencyTable.setMarginTop(20f);
//				agencyTable.setKeepTogether(true);

				String contactName = "";
				if (lang == 1) {
					contactName = agency.getNameHindi() != null ? agency.getNameHindi().toUpperCase() : "";
				} else if (lang == 2) {
					contactName = agency.getNameGujarati() != null ? agency.getNameGujarati().toUpperCase() : "";
				} else {
					contactName = agency.getNameEnglish() != null ? agency.getNameEnglish().toUpperCase() : "";
				}
				cell = new Cell(1, 8)
						.add(new Paragraph().add(contactName).setFontSize(14)
								.add(new Text(
										" ( " + (agency.getContactNo() != null ? agency.getContactNo() : "") + " )"))
								.setFontSize(13))
						.setTextAlignment(TextAlignment.CENTER).setFont(basicFont).setBackgroundColor(backColor)
						.setFontColor(whiteColor);
				agencyTable.addCell(cell);

				for (FnDetailsForDatewiseReportResponseDto function : functions) {
					String functionDate;
					if (function.getFnStartDate() != null) {
						functionDate = function.getFnStartDate();
					} else {
						functionDate = "";
					}
					cell = new Cell(1, 8)
							.add(new Paragraph().add(eDate + " : " + functionDate).setFont(basicFont).setFontSize(13))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(5f)
							.setBackgroundColor(grayColor);
					agencyTable.addCell(cell);

					agencyTable.addCell(new Cell().setBorder(Border.NO_BORDER));

					cell = new Cell(1, 7)
							.add(new Paragraph().add(eVenue + " : " + function.getVenue()).setFont(basicFont)
									.setFontSize(13))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(5f)
							.setBackgroundColor(grayColor);
					agencyTable.addCell(cell);

					agencyTable.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER));

					cell = new Cell()
							.add(new Paragraph().add(menuItemLabel).setFont(basicFont)
									.setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph().add(timeLabel).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					if (withPrice == 1) {
						cell = new Cell()
								.add(new Paragraph().add(rateLabel).setFont(basicFont)
										.setFontSize(lang == 0 ? 12f : 14f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
						agencyTable.addCell(cell);
					}

					if (withPrice == 1) {
						cell = new Cell();
					} else {
						cell = new Cell(1, 2);
					}
					cell.add(new Paragraph().add(qtyLabel).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					if (withPrice == 1) {
						cell = new Cell();
					} else {
						cell = new Cell(1, 2);
					}
					cell.add(new Paragraph().add(notesLabel).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
							.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
					agencyTable.addCell(cell);

					if (withPrice == 1) {
						cell = new Cell()
								.add(new Paragraph().add(totalLabel).setFont(basicFont)
										.setFontSize(lang == 0 ? 12f : 14f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT).setPadding(2f).simulateBold();
						agencyTable.addCell(cell);
					}

					String itemName = "";
					Long grandTotal = 0l;
					for (MenuItemForDatewiseReportResponseDto item : function.getMenuItems()) {
						agencyTable.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER));

						Long totalPrice = 0l;
						StringBuilder qtyBuilder = new StringBuilder();
						StringBuilder priceBuilder = new StringBuilder();
						String qty = "", price = "";
						String unit = "";
						if (lang == 1) {
							itemName = item.getItemNameHindi() != null ? item.getItemNameHindi() : "";
						} else if (lang == 2) {
							itemName = item.getItemNameGujarati() != null ? item.getItemNameGujarati() : "";
						} else {
							itemName = item.getItemNameEnglish() != null ? item.getItemNameEnglish() : "";
						}

						if (item.getServiceType().equalsIgnoreCase("counter_wise")
								|| item.getServiceType().equalsIgnoreCase("counter wise")) {
							if (item.getCounterQty() != 0 || item.getHelperQty() != 0) {

								List<String> parts = new ArrayList<>();

								if (item.getCounterQty() != 0) {
									parts.add((lang == 1 ? "लेबर" : lang == 2 ? "લેબર" : "Labour") + " : "
											+ item.getCounterQty());
								}

								if (item.getHelperQty() != 0) {
									parts.add((lang == 1 ? "हेल्पर" : lang == 2 ? "હેલ્પર" : "Helper") + " : "
											+ item.getHelperQty());
								}

								if (!parts.isEmpty()) {
									qtyBuilder.append(String.join(", ", parts));
								}
							}

							if (item.getCounterPrice() != 0 || item.getCounterPrice() != 0) {
								List<String> parts = new ArrayList<>();
								if (item.getCounterPrice() != 0) {
									parts.add((lang == 1 ? "लेबर" : lang == 2 ? "લેબર" : "Labour") + " : "
											+ item.getCounterPrice());
									totalPrice = totalPrice + (item.getCounterQty() * item.getCounterPrice());
								}

								if (item.getHelperPrice() != 0) {
									parts.add((lang == 1 ? "हेल्पर" : lang == 2 ? "હેલ્પર" : "Helper") + " : "
											+ item.getHelperPrice());
									totalPrice = totalPrice + (item.getHelperQty() * item.getHelperPrice());
								}

								if (!parts.isEmpty()) {
									priceBuilder.append(String.join(", ", parts));
								}
							}
						} else if (item.getServiceType().equalsIgnoreCase("plate_wise")
								|| item.getServiceType().equalsIgnoreCase("plate wise")) {
							if (item.getQty() != 0) {
								if (lang == 1) {
									unit = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "";
								} else if (lang == 2) {
									unit = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "";
								} else {
									unit = item.getUnitNameEnglish() != null ? item.getUnitNameEnglish() : "";
								}
								qtyBuilder.append(item.getQty() + " " + unit);
							}

							if (item.getRate() != 0) {
								priceBuilder.append(item.getRate());
								totalPrice = totalPrice + (item.getQty() * item.getRate());
							}
						}
						qty = qtyBuilder.toString();
						price = priceBuilder.toString();

						cell = new Cell()
								.add(new Paragraph().add(itemName).setFont(basicFont)
										.setFontSize(lang == 0 ? 11f : 13f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						cell = new Cell()
								.add(new Paragraph().add(item.getDateTime()).setFont(basicFont).setFontSize(11))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						if (withPrice == 1) {
							cell = new Cell().add(new Paragraph().add(price).setFont(basicFont).setFontSize(11))
									.setHorizontalAlignment(HorizontalAlignment.LEFT);
							agencyTable.addCell(cell);
						}

						if (withPrice == 1) {
							cell = new Cell();
						} else {
							cell = new Cell(1, 2);
						}
						cell.add(new Paragraph().add(qty).setFont(basicFont).setFontSize(11))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						if (withPrice == 1) {
							cell = new Cell();
						} else {
							cell = new Cell(1, 2);
						}
						cell.add(new Paragraph().add(
								item.getRemarks() != null && item.getRemarks().trim().length() != 0 ? item.getRemarks()
										: "")
								.setFont(basicFont).setFontSize(lang == 0 ? 11f : 13f))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);

						if (withPrice == 1) {
							cell = new Cell()
									.add(new Paragraph().add(totalPrice.toString()).setFont(basicFont).setFontSize(11))
									.setHorizontalAlignment(HorizontalAlignment.LEFT);
							agencyTable.addCell(cell);
						}

						grandTotal = grandTotal + totalPrice;
					}

					if (withPrice == 1) {
						cell = new Cell(1, 7)
								.add(new Paragraph().add(totalLabel).setFont(basicFont)
										.setFontSize(lang == 0 ? 12f : 14f))
								.setTextAlignment(TextAlignment.RIGHT).setPadding(2f).simulateBold();
						agencyTable.addCell(cell);

						cell = new Cell()
								.add(new Paragraph().add(grandTotal.toString()).setFont(basicFont).setFontSize(11))
								.setHorizontalAlignment(HorizontalAlignment.LEFT);
						agencyTable.addCell(cell);
						totalMaster = totalMaster + grandTotal;
					}
				}
				document.add(agencyTable);
			}

			if (withPrice == 1) {
				Table totalMasterTable = new Table(UnitValue.createPercentArray(new float[] { 70f, 30f }));
				totalMasterTable.setMarginTop(20f);
				totalMasterTable.setWidth(UnitValue.createPercentValue(100f));

				totalMasterTable.addCell(new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER));

				cell = new Cell()
						.add(new Paragraph().add(totalMasterLabel + " : ").setFont(basicFont)
								.setFontSize(lang == 0 ? 16f : 18f).setFixedLeading(lang == 0 ? 16f : 18f))
						.setTextAlignment(TextAlignment.RIGHT).setPadding(2f).simulateBold()
						.setBorderBottom(Border.NO_BORDER);
				totalMasterTable.addCell(cell);

				totalMasterTable.addCell(new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER));

				cell = new Cell()
						.add(new Paragraph().add(totalMaster.toString()).setFont(basicFont).setFontSize(16f)
								.setFixedLeading(16f))
						.setTextAlignment(TextAlignment.RIGHT).setPadding(2f).simulateBold()
						.setBorderTop(Border.NO_BORDER);
				totalMasterTable.addCell(cell);

				document.add(totalMasterTable);
			}
			document.close();

			String fullUrl = "";
			if (eventMasterEntity == null) {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + startDate.replace("/", "_")
						+ "/ChefAgency_" + datetimeforfile + ".pdf";
			} else {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
						+ eventMasterEntity.getEventNo() + "/ChefAgency_" + datetimeforfile + ".pdf";
			}

			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}

	}

	public CompanyDetailsResponseDto getCompanyDetails(Long userId) {
		UserMasterEntity user = userMasterRepository.findById(userId)
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

	public Table createHeaderTable(PdfFont font, PdfFont fontBold, ImageData logo, String customer, String venue,
			String dateTime, String agencyName, String items, boolean showItems, Integer isCompanyDetails,
			Integer lang) {

		String dateLabel, customerNameLabel, venueLabel, agencyNameLabel, menuItemLabel;

		if (lang == 1) {
			dateLabel = "दिनांक : ";
			customerNameLabel = "ग्राहक का नाम : ";
			venueLabel = "स्थान : ";
			agencyNameLabel = "एजेंसी का नाम";
			menuItemLabel = "मेनू आइटम";
		} else if (lang == 2) {
			dateLabel = "તારીખ : ";
			customerNameLabel = "ગ્રાહકનું નામ : ";
			venueLabel = "સ્થળ : ";
			agencyNameLabel = "એજન્સીનું નામ";
			menuItemLabel = "મેનુ આઇટમ";
		} else {
			dateLabel = "Date : ";
			customerNameLabel = "Customer Name : ";
			venueLabel = "Venue : ";
			agencyNameLabel = "Agency Name";
			menuItemLabel = "Menu Items";
		}
		Image logoData = new Image(logo);
		logoData.setWidth(110f);
		logoData.setAutoScale(false);
		logoData.setHorizontalAlignment(HorizontalAlignment.CENTER);

		/* ---------- HEADER TABLE ---------- */
		Table table = new Table(UnitValue.createPercentArray(new float[] { 24f, 46f, 30f }));
		table.setFixedLayout();
		table.setWidth(UnitValue.createPercentValue(100f));

		// LOGO CELL
		Cell cell;

		if (isCompanyDetails == 1) {
			cell = new Cell(4, 1).add(logoData).setBorder(Border.NO_BORDER)
					.setVerticalAlignment(VerticalAlignment.MIDDLE);
			table.addCell(cell);
		}

		if (isCompanyDetails == 1) {
			cell = new Cell().setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 2).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);

		cell = new Cell()
				.add(new Paragraph().add(new Text(dateLabel).simulateBold().setFontSize(lang == 0 ? 11 : 12))
						.add(new Text(dateTime.toUpperCase()).setFontSize(11)).setFont(font))
				.setBorder(Border.NO_BORDER);
		table.addCell(cell);

		if (isCompanyDetails == 1) {
			cell = new Cell()
					.add(new Paragraph().add(new Text(customerNameLabel).simulateBold())
							.add(new Text(customer.toUpperCase())).setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);

		} else {
			cell = new Cell(1, 2)
					.add(new Paragraph().add(new Text(customerNameLabel).simulateBold())
							.add(new Text(customer.toUpperCase())).setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);

		cell = new Cell().setBorder(Border.NO_BORDER);
		table.addCell(cell);

		if (isCompanyDetails == 1) {
			cell = new Cell(1, 2)
					.add(new Paragraph().add(new Text(venueLabel).simulateBold()).add(new Text(venue.toUpperCase()))
							.setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 3)
					.add(new Paragraph().add(new Text(venueLabel).simulateBold()).add(new Text(venue.toUpperCase()))
							.setFont(font).setFontSize(lang == 0 ? 13f : 15f))
					.setPaddingLeft(10f).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);

		if (isCompanyDetails == 1) {
			cell = new Cell(1, 2).setBorder(Border.NO_BORDER);
		} else {
			cell = new Cell(1, 3).setBorder(Border.NO_BORDER);
		}
		table.addCell(cell);

		cell = new Cell(1, 3)
				.add(new Paragraph("Chef Labour Wise Raw Material Report").setFont(fontBold).setFontSize(18)
						.setTextAlignment(TextAlignment.CENTER).setPadding(0).setFixedLeading(18f))
				.setBorder(Border.NO_BORDER);
		table.addCell(cell);

		Table innerTable = new Table(UnitValue.createPercentArray(new float[] { 18f, 2f, 80f }));
		innerTable.setWidth(UnitValue.createPercentValue(100f));

		cell = new Cell().add(new Paragraph(agencyNameLabel).setFont(font).simulateBold()
				.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
		innerTable.addCell(cell);

		cell = new Cell().add(new Paragraph(" : ").setFont(font).setFontSize(lang == 0 ? 12 : 14)
				.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
		innerTable.addCell(cell);

		cell = new Cell().add(new Paragraph(agencyName.toUpperCase()).setFont(font).setFontSize(lang == 0 ? 12 : 14)
				.setTextAlignment(TextAlignment.LEFT).setSplitCharacters(new ISplitCharacters() {

					@Override
					public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
						return true;
					}
				})).setBorder(Border.NO_BORDER);
		innerTable.addCell(cell);

		if (showItems && items != null && !items.trim().isEmpty()) {
			cell = new Cell().add(new Paragraph(menuItemLabel).simulateBold().setFont(font)
					.setFontSize(lang == 0 ? 12 : 14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell().add(new Paragraph(" : ").setFont(font).setFontSize(lang == 0 ? 12 : 14)
					.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);

			cell = new Cell()
					.add(new Paragraph(items.toUpperCase()).setFont(font).setFontSize(lang == 0 ? 12 : 14)
							.setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f))
					.setBorder(Border.NO_BORDER);
			innerTable.addCell(cell);
		}
		table.addCell(new Cell(1, 3).add(innerTable).setBorder(Border.NO_BORDER));

		return table;
	}

	private float calculateTableHeight(Table table, PdfDocument pdfDoc) {
		IRenderer renderer = table.createRendererSubTree();
		renderer.setParent(new Document(pdfDoc).getRenderer());

		LayoutResult result = renderer
				.layout(new LayoutContext(new LayoutArea(0, new Rectangle(PageSize.A4.getWidth() - 80, 1000 // large
																											// height
				))));

		return result.getOccupiedArea().getBBox().getHeight();
	}

	@Override
	public String generateMenuForHmReport(Long eventId, Integer isCompanyDetails, HttpServletRequest re, Integer lang,
			Long userid, Integer isPartyDetails) {

		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String phone, eDate, eVenue, email, funLabel, menuItemLabel, timeLabel, qtyLabel, unitLabel, notesLabel,
					rateLabel, totalLabel, totalMasterLabel, printedLabel, eventType, guestLable, nameLabel,
					descriptionLabel, startLabel, endLabel, agencyNameLabel, extraLabel, otherVardiLabel,
					preparedByLabel, checkedByLabel, authorizedByLabel, signLabel;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 512;
				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की दिनांक";
				eVenue = "होल का नाम";
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				timeLabel = "कार्यक्रम का समय";
				qtyLabel = "मात्रा";
				unitLabel = "मात्रक";
				notesLabel = "टिप्पणी";
				rateLabel = "भाव";
				totalLabel = "टोटल";
				totalMasterLabel = "ग्रैंड टोटल";
				printedLabel = "प्रिंट समय";
				eventType = "कार्यक्रम का प्रकार";
				guestLable = "अदिनांक";
				nameLabel = "नाम";
				descriptionLabel = "वर्णन";
				startLabel = "प्रारंभ समय";
				endLabel = "अंत समय";
				agencyNameLabel = "एजेंसी का नाम";
				extraLabel = "अतिरिक्त";
				otherVardiLabel = "अन्य वर्दी";
				preparedByLabel = "तैयारकर्ता";
				checkedByLabel = "जाँचकर्ता";
				authorizedByLabel = "अनुमोदनकर्ता";
				signLabel = "";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 510;
				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "હોલ નું નામ";
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				timeLabel = "કાર્યક્રમ નો સમય";
				qtyLabel = "સંખ્યા";
				unitLabel = "એકમ";
				notesLabel = "નોંધ";
				rateLabel = "ભાવ";
				totalLabel = "ટોટલ";
				totalMasterLabel = "ગ્રાન્ડ ટોટલ";
				printedLabel = "પ્રિન્ટ સમય";
				eventType = "કાર્યક્રમ નો પ્રકાર";
				guestLable = "મહેમાન";
				nameLabel = "નામ";
				descriptionLabel = "વર્ણન";
				startLabel = "પ્રારંભ સમય";
				endLabel = "સમાપ્તિ સમય";
				agencyNameLabel = "એજન્સીનું નામ";
				extraLabel = "એક્સ્ટ્રા";
				otherVardiLabel = "અન્ય વર્દી";
				preparedByLabel = "તૈયાર કરનાર";
				checkedByLabel = "ચકાસનાર";
				authorizedByLabel = "મંજૂર કરનાર";
				signLabel = "";
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Event Date";
				eVenue = "HALL NAME";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				timeLabel = "Event Time";
				qtyLabel = "Quantity";
				unitLabel = "Unit";
				notesLabel = "Remarks";
				rateLabel = "Rate";
				totalLabel = "Total";
				totalMasterLabel = "Grand Total";
				printedLabel = "Printed On";
				eventType = "Event Type";
				guestLable = "Guest";
				nameLabel = "Name";
				descriptionLabel = "Description";
				startLabel = "Start Time";
				endLabel = "End Time";
				agencyNameLabel = "Agency Name";
				extraLabel = "Extra";
				otherVardiLabel = "Other vardi";
				preparedByLabel = "Prepared By";
				checkedByLabel = "Checked By";
				authorizedByLabel = "Authorized By";
				signLabel = "Sign";
			}
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);
			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			if (eventMasterEntity == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(
					outputPath + "/"
							+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
									formatDate(eventMasterEntity.getEventStartDateTime()), "Menu For HM Report")
							+ ".pdf");
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 50, 20);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color labelColor = new DeviceRgb(115, 99, 67);
			Color labelBgColor = new DeviceRgb(242, 235, 223);

			Cell cell;

			Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);

			if (isCompanyDetails == 1) {

				ImageData logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//						.loadImageFromResource("/flipbook/pages/logo.png");
				Image logo = new Image(logoData);

				// Resize & align
				logo.setWidth(100f);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f);
				companyTable.addCell(cell);

				cell = new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).simulateBold()
						.setFontSize(14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getOfficeNo() != null
										? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
										: "").setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
										.setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
				companyTable.addCell(cell);
			}
			document.add(companyTable);

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 30f, 20f, 30f }));
			eventTable.setWidth(UnitValue.createPercentValue(100f));

			String funcName = "", partyName = "", mealType = "";
			if (lang == 1) {
				funcName = eventMasterEntity.getEventType().getNameHindi() != null
						? eventMasterEntity.getEventType().getNameHindi()
						: "";
				partyName = eventMasterEntity.getParty().getNameHindi() != null
						? eventMasterEntity.getParty().getNameHindi()
						: "";
				mealType = eventMasterEntity.getMealType().getNameHindi() != null
						? eventMasterEntity.getMealType().getNameHindi()
						: "";
			} else if (lang == 2) {
				funcName = eventMasterEntity.getEventType().getNameGujarati() != null
						? eventMasterEntity.getEventType().getNameGujarati()
						: "";
				partyName = eventMasterEntity.getParty().getNameGujarati() != null
						? eventMasterEntity.getParty().getNameGujarati()
						: "";
				mealType = eventMasterEntity.getMealType().getNameGujarati() != null
						? eventMasterEntity.getMealType().getNameGujarati()
						: "";
			} else {
				funcName = eventMasterEntity.getEventType().getNameEnglish() != null
						? eventMasterEntity.getEventType().getNameEnglish()
						: "";
				partyName = eventMasterEntity.getParty().getNameEnglish() != null
						? eventMasterEntity.getParty().getNameEnglish()
						: "";
				mealType = eventMasterEntity.getMealType().getNameEnglish() != null
						? eventMasterEntity.getMealType().getNameEnglish()
						: "";
			}

			Cell label, value;

			SimpleDateFormat printedDate = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a");
			DateTimeFormatter ddmmyyyyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter hhmmaFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			/* event date */
			label = new Cell()
					.add(new Paragraph().add(eDate.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(eventMasterEntity.getEventStartDateTime().format(ddmmyyyyFormatter))
							.setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(value);

			/* event time */
			label = new Cell()
					.add(new Paragraph().add(timeLabel.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(eventMasterEntity.getEventStartDateTime().format(hhmmaFormatter))
							.setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(value);

			/* printed time */
			label = new Cell()
					.add(new Paragraph().add(printedLabel.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(printedDate.format(new Date())).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(value);

			/* hall name */
			label = new Cell()
					.add(new Paragraph().add(eVenue.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(label);

			value = new Cell()
					.add(new Paragraph()
							.add(eventMasterEntity.getVenue() != null
									? eventMasterEntity.getVenue().getNameEnglish().toUpperCase()
									: eventMasterEntity.getBanquetHall().getHallName())
							.setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(value);

			/* event name */
			label = new Cell()
					.add(new Paragraph().add(eventType.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(funcName.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(value);

			/* pax */
			label = new Cell()
					.add(new Paragraph().add(guestLable.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(label);

			value = new Cell().add(new Paragraph().add("").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
			eventTable.addCell(value);

			/* name */
			if (isPartyDetails == 1) {
				label = new Cell()
						.add(new Paragraph().add(nameLabel.toUpperCase()).setFont(basicFont)
								.setFontSize(lang == 0 ? 12f : 14f))
						.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
				eventTable.addCell(label);

				value = new Cell()
						.add(new Paragraph().add(partyName.toUpperCase()).setFont(basicFont)
								.setFontSize(lang == 0 ? 12f : 14f))
						.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
				eventTable.addCell(value);

				/* blank cell */
				label = new Cell().add(new Paragraph().add("").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
						.setPadding(2f).setFontColor(labelColor).setBorder(new SolidBorder(labelColor, 1f));
				eventTable.addCell(label);

				value = new Cell().add(new Paragraph().add("").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
						.setPadding(2f).setBorder(new SolidBorder(labelColor, 1f));
				eventTable.addCell(value);
			}
			/* menu for hm label */
			label = new Cell(1, 4)
					.add(new Paragraph().add("MENU FOR HM").setFont(basicFont).setFontSize(lang == 0 ? 16f : 18f)
							.setFixedLeading(lang == 0 ? 16f : 18f))
					.setFontColor(labelColor).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(labelColor, 1.5f)).setTextAlignment(TextAlignment.CENTER);
			eventTable.addCell(label);

			String mealNotes = eventMasterEntity.getMeal_notes() != null
					&& eventMasterEntity.getMeal_notes().trim().length() != 0
							? " (" + eventMasterEntity.getMeal_notes() + ") "
							: "";
			value = new Cell(1, 4)
					.add(new Paragraph().add(mealType + mealNotes).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
			eventTable.addCell(value);

			document.add(eventTable);

			Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 22f, 13f, 13f, 8f, 18f, 14f, 12f }));
			itemTable.setWidth(UnitValue.createPercentValue(100f));

			label = new Cell()
					.add(new Paragraph(descriptionLabel.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 10f : 12f).setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(startLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(endLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(qtyLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(agencyNameLabel.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 10f : 12f).setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(notesLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(signLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			itemTable.addHeaderCell(label);

			List<EventFunctionMasterEntity> functions = eventFunctionRepository
					.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventId);

			List<Long> partyIds = partyRepository.findAllIds();

			Table otherVardi = new Table(UnitValue.createPercentArray(new float[] { 25f, 10f, 20f, 20f, 15f, 10f }));
			otherVardi.setWidth(UnitValue.createPercentValue(100f));
			otherVardi.setMarginTop(10f);

			label = new Cell()
					.add(new Paragraph(otherVardiLabel.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 10f : 12f).setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			otherVardi.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(extraLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			otherVardi.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(qtyLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			otherVardi.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(agencyNameLabel.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 10f : 12f).setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			otherVardi.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(notesLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			otherVardi.addHeaderCell(label);

			label = new Cell()
					.add(new Paragraph(signLabel.toUpperCase()).setFont(basicFont).setFontSize(lang == 0 ? 10f : 12f)
							.setFontColor(labelColor))
					.setBorder(new SolidBorder(labelColor, 1f)).setBackgroundColor(labelBgColor)
					.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setPaddingTop(5f).setPaddingBottom(5f);
			otherVardi.addHeaderCell(label);

			String functionName = "", functionDate, functionStartTime, functionEndTime;
			for (EventFunctionMasterEntity eventFunctionMasterEntity : functions) {
				if (lang == 1) {
					functionName = eventFunctionMasterEntity.getFunction().getNameHindi() != null
							? eventFunctionMasterEntity.getFunction().getNameHindi()
							: "";
				} else if (lang == 2) {
					functionName = eventFunctionMasterEntity.getFunction().getNameGujarati() != null
							? eventFunctionMasterEntity.getFunction().getNameGujarati()
							: "";
				} else {
					functionName = eventFunctionMasterEntity.getFunction().getNameEnglish() != null
							? eventFunctionMasterEntity.getFunction().getNameEnglish().toUpperCase()
							: "";
				}

				if (eventFunctionMasterEntity.getFunctionStartDateTime() != null) {
					functionDate = eventFunctionMasterEntity.getFunctionStartDateTime().format(ddmmyyyyFormatter);
					functionStartTime = eventFunctionMasterEntity.getFunctionStartDateTime().format(hhmmaFormatter)
							.toUpperCase();
				} else {
					functionDate = "";
					functionStartTime = "";
				}

				if (eventFunctionMasterEntity.getFunctionEndDateTime() != null) {
					functionEndTime = eventFunctionMasterEntity.getFunctionEndDateTime().format(hhmmaFormatter)
							.toUpperCase();
				} else {
					functionEndTime = "";
				}

				value = new Cell()
						.add(new Paragraph()
								.add(new Text(functionName).setUnderline().setFontSize(lang == 0 ? 12f : 14f)).add("\n")
								.add(new Text(functionDate).setUnderline().setFontSize(lang == 0 ? 12f : 14f))
								.setTextAlignment(TextAlignment.CENTER))
						.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
						.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(basicFont);
				itemTable.addCell(value);

				value = new Cell()
						.add(new Paragraph().add(new Text(functionStartTime)).setTextAlignment(TextAlignment.CENTER))
						.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
						.setVerticalAlignment(VerticalAlignment.MIDDLE);
				itemTable.addCell(value);

				value = new Cell()
						.add(new Paragraph().add(new Text(functionEndTime)).setTextAlignment(TextAlignment.CENTER))
						.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
						.setVerticalAlignment(VerticalAlignment.MIDDLE);
				itemTable.addCell(value);

				value = new Cell()
						.add(new Paragraph().add(new Text(eventFunctionMasterEntity.getPax().toString()))
								.setTextAlignment(TextAlignment.CENTER))
						.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
						.setVerticalAlignment(VerticalAlignment.MIDDLE);
				itemTable.addCell(value);

				itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
				itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
				itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));

				List<EventFunctionMenuAllocationFullResponseDto> menuAllocationDtos = getMenuAllocation(eventId,
						eventFunctionMasterEntity.getId());
				String catName = "";
				for (EventFunctionMenuAllocationFullResponseDto menuAllocation : menuAllocationDtos) {
					List<MenuAllocationCatWiseResponseDto> menu = convertToCategoryWiseAllocation(
							menuAllocation.getMenuAllocation());

					for (MenuAllocationCatWiseResponseDto cat : menu) {
						if (lang == 1) {
							catName = cat.getMenuCategoryNameHindi() != null ? cat.getMenuCategoryNameHindi() : "";
						} else if (lang == 2) {
							catName = cat.getMenuCategoryNameGujarati() != null ? cat.getMenuCategoryNameGujarati()
									: "";
						} else {
							catName = cat.getMenuCategoryName() != null ? cat.getMenuCategoryName().toUpperCase() : "";
						}
						value = new Cell()
								.add(new Paragraph().add(new Text(catName).setFontSize(lang == 0 ? 12f : 14f))
										.setTextAlignment(TextAlignment.CENTER))
								.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
								.setFont(basicFont).setVerticalAlignment(VerticalAlignment.MIDDLE);
						itemTable.addCell(value);

						itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
						itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
						itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
						itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
						itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
						itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));

						String itemName = "";
						for (MenuItemAllocationResponseDto item : cat.getItems()) {
							if (lang == 1) {
								itemName = item.getMenuItemNameHindi() != null ? item.getMenuItemNameHindi() : "";
							} else if (lang == 2) {
								itemName = item.getMenuItemNameGujarati() != null ? item.getMenuItemNameGujarati() : "";
							} else {
								itemName = item.getMenuItemName() != null ? item.getMenuItemName() : "";
							}
							value = new Cell()
									.add(new Paragraph().add(new Text(itemName).setFontSize(lang == 0 ? 10f : 12f)))
									.setBorder(new SolidBorder(labelColor, 1f))
									.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(basicFont);
							itemTable.addCell(value);

							itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
							itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));

							String agency = "";
							MenuItemAgencyAlloResponseDto ag = item.getAgencies();
							if (lang == 1) {
								agency = ag.getPartyNameHindi() != null ? ag.getPartyNameHindi().toUpperCase() : "";
							} else if (lang == 2) {
								agency = ag.getPartyNameGujarati() != null ? ag.getPartyNameGujarati().toUpperCase()
										: "";
							} else {
								agency = ag.getPartyName() != null ? ag.getPartyName().toUpperCase() : "";
							}

							BigDecimal qty = BigDecimal.ZERO;
							String unit = "";
							if (ag.getQuantity() != null) {
								qty = ag.getQuantity();
							}

							if (ag.getUnitName() != null) {
								unit = ag.getUnitName();
							}
							value = new Cell()
									.add(new Paragraph()
											.add(new Text(ag.getAgencyType() == "OUTSIDE" ? (qty + " " + unit)
													: item.getPax().toString()).setFontSize(lang == 0 ? 10f : 12f)))
									.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
									.setVerticalAlignment(VerticalAlignment.MIDDLE);
							itemTable.addCell(value);

							value = new Cell()
									.add(new Paragraph().add(
											new Text(agency).setFontSize(lang == 0 ? 10f : 12f).setFont(basicFont)))
									.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
									.setVerticalAlignment(VerticalAlignment.MIDDLE);
							itemTable.addCell(value);

							itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
							itemTable.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));

						}
					}
				}

				for (Long partyId : partyIds) {
					List<GetEventLaborResponseDto> dtoDetailList = eventLaborServiceImpl.fetchEventLaborDataDetail(
							eventId, eventFunctionMasterEntity.getId(), re, lang, userid, partyId);

					Map<String, List<GetEventLaborResponseDto>> groupedMap = dtoDetailList.stream().collect(
							Collectors.groupingBy(dto -> dto.getContactCategoryName() + "|" + dto.getLaborName()));

					for (List<GetEventLaborResponseDto> group : groupedMap.values()) {
						GetEventLaborResponseDto dto = group.get(0);

						String qtyWithShifts = group.stream()
								.map(d -> d.getQty() + " (" + d.getLaborShift().toUpperCase() + ")")
								.collect(Collectors.joining(" + "));

						value = new Cell()
								.add(new Paragraph().add(new Text(
										dto.getContactCategoryName() != null ? dto.getContactCategoryName() : "")
										.setFontSize(lang == 0 ? 12f : 14f)))
								.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(basicFont);
						otherVardi.addCell(value);

						otherVardi.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));

//						value = new Cell()
//								.add(new Paragraph().add(new Text(dto.getQty().toString()).setFontSize(12f))
//										.add(new Text("\n"))
//										.add(new Text(dto.getLaborShift() != null
//												? "(" + dto.getLaborShift().toUpperCase().toString() + ")"
//												: "").setFontSize(12f)))
//								.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
//								.setVerticalAlignment(VerticalAlignment.MIDDLE);
//						otherVardi.addCell(value);

						value = new Cell().add(new Paragraph().add(new Text(qtyWithShifts).setFontSize(12f)))
								.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE);
						otherVardi.addCell(value);

						value = new Cell()
								.add(new Paragraph().add(
										new Text(dto.getLaborName() != null ? dto.getLaborName().toUpperCase() : "")
												.setFontSize(12f)))
								.setBorder(new SolidBorder(labelColor, 1f)).setTextAlignment(TextAlignment.CENTER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE).setFont(basicFont);
						otherVardi.addCell(value);

						otherVardi.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
						otherVardi.addCell(new Cell().setBorder(new SolidBorder(labelColor, 1f)));
					}
				}

			}
			document.add(itemTable);
			document.add(otherVardi);
			// Signature Section
			Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 33f, 33f, 34f }));
			signatureTable.setWidth(UnitValue.createPercentValue(100));
			signatureTable.setMarginTop(30f);

			Cell signCell;

			// Prepared By
			signCell = new Cell()
					.add(new Paragraph("\n\n________________________").setTextAlignment(TextAlignment.CENTER))
					.add(new Paragraph(preparedByLabel).setFont(basicFont).setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER);
			signatureTable.addCell(signCell);

			// Checked By
			signCell = new Cell()
					.add(new Paragraph("\n\n________________________").setTextAlignment(TextAlignment.CENTER))
					.add(new Paragraph(checkedByLabel).setFont(basicFont).setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER);
			signatureTable.addCell(signCell);

			// Authorized By
			signCell = new Cell()
					.add(new Paragraph("\n\n________________________").setTextAlignment(TextAlignment.CENTER))
					.add(new Paragraph(authorizedByLabel).setFont(basicFont).setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER);
			signatureTable.addCell(signCell);

			int lastPage = pdfDocument.getNumberOfPages();

			float width = pdfDocument.getDefaultPageSize().getWidth() - document.getLeftMargin()
					- document.getRightMargin();

			signatureTable.setFixedPosition(lastPage, // Last page number
					document.getLeftMargin(), // X position
					20f, // Y position from bottom
					width // Table width
			);

			document.add(signatureTable);

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ eventMasterEntity.getEventNo() + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), "Menu For HM Report")
					+ ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public String profitAndLossReport(Long eventId, Integer isCompanyDetails, HttpServletRequest re, Integer lang,
			Long userid) {

		try {
			PdfFont basicFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String phone, eDate, eVenue, email, funLabel, menuItemLabel, timeLabel, qtyLabel, unitLabel, notesLabel,
					rateLabel, totalLabel, totalMasterLabel, printedLabel, eventType, guestLable, nameLabel,
					descriptionLabel, startLabel, endLabel, agencyNameLabel, extraLabel, otherVardiLabel, eventNoLabel;

			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				y = 512;
				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की दिनांक";
				eVenue = "होल का नाम";
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				timeLabel = "कार्यक्रम का समय";
				qtyLabel = "मात्रा";
				unitLabel = "मात्रक";
				notesLabel = "टिप्पणी";
				rateLabel = "भाव";
				totalLabel = "टोटल";
				totalMasterLabel = "ग्रैंड टोटल";
				printedLabel = "प्रिंट समय";
				eventType = "कार्यक्रम का प्रकार";
				guestLable = "अदिनांक";
				nameLabel = "नाम";
				descriptionLabel = "वर्णन";
				startLabel = "प्रारंभ समय";
				endLabel = "अंत समय";
				agencyNameLabel = "एजेंसी का नाम";
				extraLabel = "अतिरिक्त";
				otherVardiLabel = "अन्य वर्दी";
				eventNoLabel = "कार्यक्रम नंबर";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				y = 510;
				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "હોલ નું નામ";
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				timeLabel = "કાર્યક્રમ નો સમય";
				qtyLabel = "સંખ્યા";
				unitLabel = "એકમ";
				notesLabel = "નોંધ";
				rateLabel = "ભાવ";
				totalLabel = "ટોટલ";
				totalMasterLabel = "ગ્રાન્ડ ટોટલ";
				printedLabel = "પ્રિન્ટ સમય";
				eventType = "કાર્યક્રમ નો પ્રકાર";
				guestLable = "મહેમાન";
				nameLabel = "નામ";
				descriptionLabel = "વર્ણન";
				startLabel = "પ્રારંભ સમય";
				endLabel = "સમાપ્તિ સમય";
				agencyNameLabel = "એજન્સીનું નામ";
				extraLabel = "એક્સ્ટ્રા";
				otherVardiLabel = "અન્ય વર્દી";
				eventNoLabel = "કાર્યક્રમ નંબર";
			} else {
				// English
				System.out.println("Loading English font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Contact No.";
				eDate = "Event Date";
				eVenue = "HALL NAME";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				timeLabel = "Event Time";
				qtyLabel = "Quantity";
				unitLabel = "Unit";
				notesLabel = "Remarks";
				rateLabel = "Rate";
				totalLabel = "Total";
				totalMasterLabel = "Grand Total";
				printedLabel = "Printed On";
				eventType = "Event Type";
				guestLable = "Guest";
				nameLabel = "Name";
				descriptionLabel = "Description";
				startLabel = "Start Time";
				endLabel = "End Time";
				agencyNameLabel = "Agency Name";
				extraLabel = "Extra";
				otherVardiLabel = "Other vardi";
				eventNoLabel = "Event No.";
			}
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);
			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			if (eventMasterEntity == null) {
				return "";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);

			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(
					outputPath + "/"
							+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
									formatDate(eventMasterEntity.getEventStartDateTime()), "Profit and Loss Report")
							+ ".pdf");
			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 50, 20);

			ImageData mainBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_main_1.png");
			ImageData watermarkBgData = menuPreparationServiceImpl
					.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

			Color labelColor = new DeviceRgb(115, 99, 67);
			Color labelBgColor = new DeviceRgb(242, 235, 223);

			Cell cell;

			Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 2f, 78f }), true);

			if (isCompanyDetails == 1) {

				ImageData logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//						.loadImageFromResource("/flipbook/pages/logo.png");
				Image logo = new Image(logoData);

				// Resize & align
				logo.setWidth(100f);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f);
				companyTable.addCell(cell);

				cell = new Cell().add(new Paragraph(cmpDto.getCompanyName()).setFont(basicFont).simulateBold()
						.setFontSize(14).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getOfficeNo() != null
										? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
										: "").setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
				companyTable.addCell(cell);

				cell = new Cell()
						.add(new Paragraph()
								.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
										.simulateBold())
								.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
										.setFont(basicFont).setFontSize(14)))
						.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
				companyTable.addCell(cell);
			}
			document.add(companyTable);

			Table eventTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 30f, 20f, 30f }));
			eventTable.setWidth(UnitValue.createPercentValue(100f));

			String funcName = "", partyName = "", mealType = "";
			if (lang == 1) {
				funcName = eventMasterEntity.getEventType().getNameHindi() != null
						? eventMasterEntity.getEventType().getNameHindi()
						: "";
				partyName = eventMasterEntity.getParty().getNameHindi() != null
						? eventMasterEntity.getParty().getNameHindi()
						: "";
				mealType = eventMasterEntity.getMealType().getNameHindi() != null
						? eventMasterEntity.getMealType().getNameHindi()
						: "";
			} else if (lang == 2) {
				funcName = eventMasterEntity.getEventType().getNameGujarati() != null
						? eventMasterEntity.getEventType().getNameGujarati()
						: "";
				partyName = eventMasterEntity.getParty().getNameGujarati() != null
						? eventMasterEntity.getParty().getNameGujarati()
						: "";
				mealType = eventMasterEntity.getMealType().getNameGujarati() != null
						? eventMasterEntity.getMealType().getNameGujarati()
						: "";
			} else {
				funcName = eventMasterEntity.getEventType().getNameEnglish() != null
						? eventMasterEntity.getEventType().getNameEnglish()
						: "";
				partyName = eventMasterEntity.getParty().getNameEnglish() != null
						? eventMasterEntity.getParty().getNameEnglish()
						: "";
				mealType = eventMasterEntity.getMealType().getNameEnglish() != null
						? eventMasterEntity.getMealType().getNameEnglish()
						: "";
			}

			Cell label, value;

			SimpleDateFormat printedDate = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a");
			DateTimeFormatter ddmmyyyyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter hhmmaFormatter = DateTimeFormatter.ofPattern("hh:mm a");

			Boolean isRawMaterialDone = menuItemRawMaterialServiceImpl.checkEventId(eventId);
			Boolean isMenuAllocationDone = menuItemRawMaterialServiceImpl.checkEventId2(eventId);
			Set<Long> itemIds = new HashSet<>();
			List<Long> itemIdList = new ArrayList<>();
			Set<String> itemNames = new HashSet<>();
			Map<String, BigDecimal> rawMataterialTotal = new HashMap<>();

			List<EventFunctionMasterEntity> eventFunctions = eventFunctionRepository
					.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventMasterEntity.getId());
			BigDecimal totalRawMaterialPrice = BigDecimal.valueOf(0);
			BigDecimal totalchefLabourPrice = BigDecimal.valueOf(0);
			BigDecimal totaloutsourcePrice = BigDecimal.valueOf(0);
			BigDecimal totallabourPrice = BigDecimal.valueOf(0);
			for (EventFunctionMasterEntity function : eventFunctions) {

				/* Total Raw Material Price */
				List<CostingReportResponseDto> allItemCategories = menuItemRawMaterialServiceImpl
						.getFunctionCategoryDetailsForCosting(eventId, function.getId(), lang, userid);

				for (CostingReportResponseDto cat : allItemCategories) {
					Long catId = cat.getMenuCategoryId();
					String catName = cat.getMenuCategoryName() == null || cat.getMenuCategoryName().isEmpty() ? ""
							: cat.getMenuCategoryName();
					List<CostingReportResponseDto> allItems = menuItemRawMaterialServiceImpl
							.getFunctionCategoryItemDetailsForCosting(eventId, function.getId(), catId, lang);

					if (allItems.size() > 0) {
						for (CostingReportResponseDto item : allItems) {
							Long itemId = item.getMenuItemId();
							itemIds.add(itemId);

							String itemName = item.getMenuItemName() == null || item.getMenuItemName().isEmpty() ? ""
									: item.getMenuItemName();
							itemNames.add(itemName);

							BigDecimal totalprice = item.getTotalprice() != null ? item.getTotalprice()
									: BigDecimal.ZERO;

							BigDecimal perplateprice = item.getPerplateprice() != null ? item.getPerplateprice()
									: BigDecimal.ZERO;

							List<CostingReportResponseDto> allRawMaterialCat = new ArrayList<>();

							if (isRawMaterialDone) {
								allRawMaterialCat = menuItemRawMaterialServiceImpl
										.getRawMaterialCategoryDetailsForCosting3(eventId, function.getId(), itemId,
												lang);
							} else if (isMenuAllocationDone) {
								List<Long> rawMaterialIds = menuItemRawMaterialServiceImpl.getAllRawMaterialIds(eventId,
										function.getId(), itemId);
								allRawMaterialCat = menuItemRawMaterialServiceImpl
										.getRawMaterialCategoryDetailsForCosting2(eventId, function.getId(), itemId,
												rawMaterialIds, lang);
							} else {
								allRawMaterialCat = menuItemRawMaterialServiceImpl
										.getRawMaterialCategoryDetailsForCosting(eventId, function.getId(), catId,
												itemId, lang);
							}

							if (allRawMaterialCat.size() > 0) {
								for (CostingReportResponseDto rawMaterialCat : allRawMaterialCat) {
									Long rawMaterialCatId = rawMaterialCat.getRawMaterialCatId();
									String rawMaterialCatName = rawMaterialCat.getRawMaterialCatName() == null
											|| rawMaterialCat.getRawMaterialCatName().isEmpty() ? ""
													: rawMaterialCat.getRawMaterialCatName();

									List<CostingReportResponseDto> allRawMaterial = new ArrayList<>();

									if (isRawMaterialDone) {
										allRawMaterial = menuItemRawMaterialServiceImpl
												.getRawMaterialDetailsForCosting3(eventId, function.getId(), itemId,
														rawMaterialCatId, lang);
									} else if (isMenuAllocationDone) {
										allRawMaterial = menuItemRawMaterialServiceImpl
												.getRawMaterialDetailsForCosting2(eventId, function.getId(), itemId,
														rawMaterialCatId, lang);
									} else {
										allRawMaterial = menuItemRawMaterialServiceImpl.getRawMaterialDetailsForCosting(
												eventId, function.getId(), catId, itemId, rawMaterialCatId, lang);
									}

									BigDecimal totalRmPrice = allRawMaterial.stream().map(
											rm -> rm.getTotalprice() != null ? rm.getTotalprice() : BigDecimal.ZERO)
											.reduce(BigDecimal.ZERO, BigDecimal::add);

									for (int l = 0; l < allRawMaterial.size(); l++) {
										CostingReportResponseDto rawmaterial = allRawMaterial.get(l);

										Long rawMaterialId = rawmaterial.getRawMaterialItemId();

										String rawMaterialName = rawmaterial.getRawMaterialItemName() != null
												? rawmaterial.getRawMaterialItemName()
												: "";

										BigDecimal quantity = rawmaterial.getQuantity() != null
												? rawmaterial.getQuantity()
												: BigDecimal.ZERO;

										String unit = rawmaterial.getUnitName() != null ? rawmaterial.getUnitName()
												: "";

										BigDecimal rate = rawmaterial.getRate() != null ? rawmaterial.getRate()
												: BigDecimal.ZERO;

										BigDecimal totalPrice = rawmaterial.getTotalprice() != null
												? rawmaterial.getTotalprice()
												: BigDecimal.ZERO;
										totalRawMaterialPrice = totalRawMaterialPrice.add(totalRmPrice);
									}
								}
							}
						}
					}
				}

				/* Chef Labour Price */
				List<CostingReportAgencyResponseDto> agencyReport = new ArrayList<>();

				if (isRawMaterialDone || isMenuAllocationDone) {
					agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport2(eventId, lang, true,
							false);
				} else {
					agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport(eventId, itemIdList, lang,
							true, false);
				}

				for (CostingReportAgencyResponseDto agency : agencyReport) {
					totalchefLabourPrice = totalchefLabourPrice.add(agency.getTotalChefLabourPrice());
				}

				/* Outsource Price */
				if (isRawMaterialDone || isMenuAllocationDone) {
					agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport2(eventId, lang, false,
							true);
				} else {
					agencyReport = menuItemRawMaterialServiceImpl.getAgencyForCostingReport(eventId, itemIdList, lang,
							false, true);
				}

				for (CostingReportAgencyResponseDto agency : agencyReport) {
					totaloutsourcePrice = totaloutsourcePrice.add(agency.getTotalOutsourcePrice());
				}

				/* Labour Price */
				agencyReport = menuItemRawMaterialServiceImpl.getLabourForCostingReport(eventId, lang);

				for (CostingReportAgencyResponseDto agency : agencyReport) {
					BigDecimal totalPriceValue = agency.getTotalLabourPrice() != null ? agency.getTotalLabourPrice()
							: BigDecimal.ZERO;

					totallabourPrice = totallabourPrice.add(totalPriceValue);
				}
			}

			BigDecimal totalIncome = BigDecimal.valueOf(0);
			BigDecimal totalExpanse = totalRawMaterialPrice.add(totalchefLabourPrice).add(totaloutsourcePrice)
					.add(totallabourPrice);
			BigDecimal totalLoss = totalIncome.subtract(totalExpanse);

			/* address */
			value = new Cell(1, 4)
					.add(new Paragraph().add(eventMasterEntity.getAddress().toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f)).setBorderLeft(new SolidBorder(1f))
					.setBorderRight(new SolidBorder(1f));
			eventTable.addCell(value);

			/* customer name */
			label = new Cell()
					.add(new Paragraph().add(nameLabel.toUpperCase() + " : ").setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(1f));
			eventTable.addCell(label);

			value = new Cell(1, 3)
					.add(new Paragraph().add(partyName.toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER).setBorderRight(new SolidBorder(1f));
			eventTable.addCell(value);

			/* contact number */
			label = new Cell()
					.add(new Paragraph().add(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(1f));
			eventTable.addCell(label);

			value = new Cell(1, 3)
					.add(new Paragraph().add(eventMasterEntity.getParty().getMobileno()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER).setBorderRight(new SolidBorder(1f));
			eventTable.addCell(value);

			/* event number */
			label = new Cell()
					.add(new Paragraph(eventNoLabel + " : ").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(1f))
					.setBorderBottom(new SolidBorder(1f));
			eventTable.addCell(label);

			value = new Cell(1, 3)
					.add(new Paragraph().add(eventMasterEntity.getEventNo().toUpperCase()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1f))
					.setBorderRight(new SolidBorder(1f));
			eventTable.addCell(value);

			document.add(eventTable);

			Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 20f, 30f, 20f }));
			itemTable.setWidth(UnitValue.createPercentValue(100f));
			itemTable.setMarginTop(10f);

			label = new Cell().add(new Paragraph("DR").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setBorder(Border.NO_BORDER);
			itemTable.addHeaderCell(label);

			label = new Cell().add(new Paragraph("Amount").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addHeaderCell(label);

			label = new Cell().add(new Paragraph("CR").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setBorder(Border.NO_BORDER);
			itemTable.addHeaderCell(label);

			label = new Cell().add(new Paragraph("Amount").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addHeaderCell(label);

			/* Income */
			label = new Cell().add(new Paragraph("Income").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell().add(new Paragraph().add("").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addCell(value);

			/* Raw Material Price */
			label = new Cell()
					.add(new Paragraph("Raw Material Price").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totalRawMaterialPrice.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addCell(value);

			/* blank cells */
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			/* Outside Price */
			label = new Cell().add(new Paragraph("Outside Price").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totaloutsourcePrice.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addCell(value);

			/* blank cells */
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			/* Chef Labour Price */
			label = new Cell()
					.add(new Paragraph("Chef Labour Price").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totalchefLabourPrice.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addCell(value);

			/* blank cells */
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			/* Labour Price */
			label = new Cell().add(new Paragraph("Labour Price").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totallabourPrice.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addCell(value);

			/* Total Income */
			label = new Cell().add(new Paragraph("Total Income").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totalIncome.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			itemTable.addCell(value);

			/* Total Expanse Price */
			label = new Cell().add(new Paragraph("Total Expanse").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totalExpanse.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			itemTable.addCell(value);

			/* blank cells */
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			/* Total Income Price */
			label = new Cell().add(new Paragraph("Total Income").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totalIncome.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
			itemTable.addCell(value);

			/* blank cells */
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			itemTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			/* Total Loss Price */
			label = new Cell().add(new Paragraph("Total Loss").setFont(basicFont).setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setBorder(Border.NO_BORDER);
			itemTable.addCell(label);

			value = new Cell()
					.add(new Paragraph().add(totalLoss.toString()).setFont(basicFont)
							.setFontSize(lang == 0 ? 12f : 14f))
					.setPadding(2f).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER)
					.setBorderTop(new SolidBorder(1f));
			itemTable.addCell(value);

			document.add(itemTable);
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
					+ eventMasterEntity.getEventNo() + "/"
					+ getReportName(eventMasterEntity.getParty().getNameEnglish(),
							formatDate(eventMasterEntity.getEventStartDateTime()), "Profit and Loss Report")
					+ ".pdf";

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	public List<MenuAllocationCatWiseResponseDto> convertToCategoryWiseAllocation(
			List<EventFunctionMenuAllocationResponseDto> menuAllocationList) {

		Map<Long, MenuAllocationCatWiseResponseDto> categoryMap = new LinkedHashMap<>();

		for (EventFunctionMenuAllocationResponseDto menu : menuAllocationList) {
			// 1. Get or create category DTO
			MenuAllocationCatWiseResponseDto catDto = categoryMap.computeIfAbsent(menu.getMenuCategoryId(), k -> {
				MenuAllocationCatWiseResponseDto newCat = new MenuAllocationCatWiseResponseDto();
				newCat.setMenuCategoryId(menu.getMenuCategoryId());
				newCat.setMenuCategoryName(menu.getMenuCategoryName());
				newCat.setMenuCategoryNameHindi(menu.getMenuCategoryNameHindi());
				newCat.setMenuCategoryNameGujarati(menu.getMenuCategoryNameGujarati());
				newCat.setMenuSortOrder(menu.getMenuSortorder());
				newCat.setItems(new ArrayList<>());
				return newCat;
			});

			// 2. Find or create MenuItem DTO
			MenuItemAllocationResponseDto itemDto = catDto.getItems().stream()
					.filter(i -> i.getMenuItemId().equals(menu.getMenuItemId())).findFirst().orElseGet(() -> {
						MenuItemAllocationResponseDto newItem = new MenuItemAllocationResponseDto();
						newItem.setMenuItemId(menu.getMenuItemId());
						newItem.setMenuItemName(menu.getMenuItemName());
						newItem.setMenuItemNameHindi(menu.getMenuItemNameHindi());
						newItem.setMenuItemNameGujarati(menu.getMenuItemNameGujarati());
						newItem.setPax(menu.getPersonCount());
						newItem.setItemSortOrder(menu.getItemSortorder());
						catDto.getItems().add(newItem);
						return newItem;
					});

			if (menu.getEventFunctionMenuAllocations() != null && !menu.getEventFunctionMenuAllocations().isEmpty()) {
				EventFunctionMenuAllocationOrderResponseDto agency = menu.getEventFunctionMenuAllocations().get(0);

				MenuItemAgencyAlloResponseDto agencyDto = new MenuItemAgencyAlloResponseDto();
				agencyDto.setPartyId(agency.getPartyId());
				agencyDto.setPartyName(agency.getPartyName());
				agencyDto.setPartyNameHindi(agency.getPartyNameHindi());
				agencyDto.setPartyNameGujarati(agency.getPartyNameGujarati());
				// agency type
				if (Boolean.TRUE.equals(menu.getChefLabour())) {
					agencyDto.setAgencyType("CHEF");
				} else if (Boolean.TRUE.equals(menu.getOutside())) {
					agencyDto.setAgencyType("OUTSIDE");
				} else if (Boolean.TRUE.equals(menu.getInside())) {
					agencyDto.setAgencyType("INSIDE");
				}
				agencyDto.setQuantity(agency.getQuantity());
				agencyDto.setPrice(agency.getPrice());
				agencyDto.setUnitId(agency.getUnitId());
				agencyDto.setUnitName(agency.getUnitName());
				itemDto.setAgencies(agencyDto);
			}

		}

		List<MenuAllocationCatWiseResponseDto> result = new ArrayList<>(categoryMap.values());

		result.sort(Comparator.comparing(MenuAllocationCatWiseResponseDto::getMenuSortOrder,
				Comparator.nullsLast(Integer::compareTo)));

		for (MenuAllocationCatWiseResponseDto cat : result) {
			if (cat.getItems() != null) {
				cat.getItems().sort(Comparator.comparing(MenuItemAllocationResponseDto::getItemSortOrder,
						Comparator.nullsLast(Integer::compareTo)));
			}
		}

		return result;
	}

	@Override
	public String generateInsideAgencyReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, String startDate, String endDate,
			HttpServletRequest re, Integer lang, Long userid, Integer isWithPrice) {

		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			int x = 185;
			int y = 515;

			String phone, eDate, eVenue, agencyName, email, funLabel, menuItemLabel, dateTimeLabel, qtyLabel,
					priceLabel, totalPriceLabel, notesLabel, grandTotalLabel, personLabel, fNote;
			if (lang == 1) {
				// Hindi
				System.out.println("Loading Hindi font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/Nirmala.ttf");
				System.out.println("Hindi font loaded successfully");
				phone = "मोबाइल नंबर";
				eDate = "कार्यक्रम की दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				y = 512;
				email = "ईमेल";
				funLabel = "समारोह";
				menuItemLabel = "मेनू आइटम";
				dateTimeLabel = "दिनांक समय";
				qtyLabel = "मात्रा";
				notesLabel = "टिप्पणी";
				priceLabel = "कीमत";
				totalPriceLabel = "कुल कीमत";
				grandTotalLabel = "ग्रांड टोटल";
				personLabel = "मेम्बर्स";
				fNote = "भोजन व्यवस्था";
			} else if (lang == 2) {
				// Gujarati
				System.out.println("Loading Gujarati font...");
				basicFont = menuPreparationServiceImpl.loadFont("/fonts/NotoSansGujarati-Regular.ttf");
				System.out.println("Gujarati font loaded successfully");
				phone = "મોબાઇલ નંબર";
				eDate = "કાર્યક્રમની તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				y = 510;
				email = "ઈમૈલ";
				funLabel = "કાર્યક્રમ";
				menuItemLabel = "મેનુ આઇટમ";
				dateTimeLabel = "તારીખ અને સમય";
				qtyLabel = "સંખ્યા";
				notesLabel = "નોંધ";
				priceLabel = "કિંમત";
				totalPriceLabel = "કુલ કિંમત";
				grandTotalLabel = "ગ્રાન્ડ ટોટલ";
				personLabel = "વ્યક્તિ";
				fNote = "ભોજન વ્યવસ્થા";
			} else {
				// English
				System.out.println("Loading English font...");
//				basicFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
				basicFont = PdfFontFactory.createFont("/fonts/arial-regular.ttf");
				System.out.println("English font loaded successfully");
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				email = "Email";
				funLabel = "Function";
				menuItemLabel = "Menu Item Name";
				dateTimeLabel = "Date & Time";
				qtyLabel = "Qnt";
				notesLabel = "Remarks";
				priceLabel = "Price";
				totalPriceLabel = "Total Price";
				grandTotalLabel = "Grand Total";
				personLabel = "Person";
				fNote = "Food Preference";
			}
			boldFont = PdfFontFactory.createFont("/fonts/arial-bold.ttf");
			PdfFont basicFont2 = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");
			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);

			StringBuilder foodPrefName;
			String fNotes = "";
			if (lang == 1) {

				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameHindi() : "");
				fNotes = eventMasterEntity.getMeal_notes_hindi();
			} else if (lang == 2) {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameGujarati()
								: "");
				fNotes = eventMasterEntity.getMeal_notes_gujarati();
			} else {
				foodPrefName = new StringBuilder(
						eventMasterEntity.getMealType() != null ? eventMasterEntity.getMealType().getNameEnglish()
								: "");
				fNotes = eventMasterEntity.getMeal_notes();
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = null;

			if (eventMasterEntity == null) {
				outputPath = new File(rootPath + "resources/tempDownload/" + startDate.replace("/", "_") + "/");
			} else {
				outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");
			}

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File pdfFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Inside Report")
					+ ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 50, 20);

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponseDtos = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, startDate, endDate);

			if (menuAgencyWithItemsResponseDtos.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			for (int i = 0; i < menuAgencyWithItemsResponseDtos.size(); i++) {
				MenuAllocationAgencyWithItemsResponseDto menuAgencyWithItemsResponseDto = menuAgencyWithItemsResponseDtos
						.get(i);

				EventFunctionMasterResponseDto functionMasterDto = menuAgencyWithItemsResponseDto.getEventFunction();

				if (functionMasterDto == null) {
					return "Event Function Not Found ";
				}

				ImageData mainBgData = menuPreparationServiceImpl
						.loadImageFromResource("/flipbook/pages/simple_main_1.png");
				ImageData watermarkBgData = menuPreparationServiceImpl
						.loadImageFromResource("/flipbook/pages/simple_menu_1.png");

				Color mainColor = new DeviceRgb(0, 0, 0);
				Cell cell;

				Table headerTable = null;
				Table companyTable = null;

				String venue = functionMasterDto.getFunction_venue();

				boolean isFirst = true;
				for (MenuAllocationAgencyResponseDto agency : menuAgencyWithItemsResponseDto.getAgencyResponse()) {
					if (!isFirst) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					}

					if (isCompanyDetails == 1) {
						companyTable = new Table(UnitValue.createPercentArray(new float[] { 25f, 2f, 73f }), true);
						companyTable.setWidth(UnitValue.createPercentValue(100f));
						ImageData logoData = menuPreparationServiceImpl
								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
//								.loadImageFromResource("/flipbook/pages/logo.png");
						Image logo = new Image(logoData);

						// Resize & align
						logo.setWidth(100f);
						logo.setAutoScale(false);
						logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

						cell = new Cell(3, 2).add(logo).setBorder(Border.NO_BORDER).setPaddingBottom(5f)
								.setVerticalAlignment(VerticalAlignment.MIDDLE);
						companyTable.addCell(cell);

						cell = new Cell()
								.add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14)
										.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
								.setBorder(Border.NO_BORDER);
						companyTable.addCell(cell);

						cell = new Cell()
								.add(new Paragraph()
										.add(new Text(phone + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
												.setFontColor(mainColor).simulateBold())
										.add(new Text(cmpDto.getOfficeNo() != null
												? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo()
												: "").setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
						companyTable.addCell(cell);

						cell = new Cell()
								.add(new Paragraph()
										.add(new Text(email + " : ").setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
												.simulateBold().setFontColor(mainColor))
										.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
												.setFont(basicFont).setFontSize(14).setFontColor(mainColor)))
								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingBottom(10f);
						companyTable.addCell(cell);

						document.add(companyTable);
					}
					headerTable = new Table(UnitValue.createPercentArray(new float[] { 20f, 1f, 33f, 16f, 1f, 29f }),
							false);
					headerTable.setWidth(UnitValue.createPercentValue(100));
					headerTable.setBorder(new SolidBorder(1f));

					cell = new Cell()
							.add(new Paragraph(agencyName).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(boldFont).setFontSize(14).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(agency.getContactName().toUpperCase()).setFont(basicFont).setFontSize(14)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(phone).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).simulateBold().setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(boldFont).setFontSize(14).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(agency.getNumber()).setFont(basicFont).setFontSize(14)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT))
							.setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(eVenue).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(boldFont).setFontSize(14).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell().add(new Paragraph(venue).setFont(basicFont).setFontSize(14)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(fNote).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
							.setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell().add(new Paragraph(":").setFont(boldFont).setFontSize(14).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					cell = new Cell().add(new Paragraph(
							foodPrefName.toString() + (fNotes.trim().isEmpty() ? "" : " (" + fNotes + ")"))
							.setFont(basicFont).setFontSize(14).setFontColor(mainColor)
							.setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER);
					headerTable.addCell(cell);

					document.add(headerTable);

					Table itemTable = new Table(UnitValue.createPercentArray(new float[] { 19f, 30f, 23f, 15f, 25f }));

					itemTable.setWidth(UnitValue.createPercentValue(100f));
					itemTable.setFixedLayout();
					itemTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
					itemTable.setMarginLeft(0);
					itemTable.setMarginTop(10f);

					cell = new Cell().add(new Paragraph(funLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
					itemTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(menuItemLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
					itemTable.addCell(cell);

					cell = new Cell()
							.add(new Paragraph(dateTimeLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
									.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
					itemTable.addCell(cell);

					cell = new Cell().add(new Paragraph(personLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
					itemTable.addCell(cell);

					cell = new Cell().add(new Paragraph(notesLabel).setFont(basicFont).setFontSize(lang == 0 ? 14 : 16)
							.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT).simulateBold());
					itemTable.addCell(cell);

					String functionName = "";
					String itemName = "";
					Paragraph p;
					for (MenuAllocationItemsResponseDto item : agency.getAllocationItems()) {
						String remarks = "";
						if (lang == 1) {
							functionName = functionMasterDto.getFunction().getNameHindi();
							itemName = item.getItemNameHindi();
							remarks = item.getRemarksHindi() != null ? item.getRemarksHindi() : "";
						} else if (lang == 2) {
							functionName = functionMasterDto.getFunction().getNameGujarati();
							itemName = item.getItemNameGujarati();
							remarks = item.getRemarksGujarati() != null ? item.getRemarksGujarati() : "";
						} else {
							functionName = functionMasterDto.getFunction().getNameEnglish();
							itemName = item.getItemName();
							remarks = item.getRemarks() != null ? item.getRemarks() : "";
						}

						cell = new Cell().add(
								new Paragraph(functionName.toUpperCase()).setFont(basicFont).setFontColor(mainColor));

						if (lang == 0) {
							cell.setFontSize(11);
						} else {
							cell.setFontSize(13);
						}
						itemTable.addCell(cell);

						cell = new Cell().add(new Paragraph(itemName).setFont(basicFont).setFontColor(mainColor)
								.setTextAlignment(TextAlignment.LEFT));

						if (lang == 0) {
							cell.setFontSize(11);
						} else {
							cell.setFontSize(13);
						}

						itemTable.addCell(cell);

						cell = new Cell().add(new Paragraph(functionMasterDto.getFunctionStartDateTime().toUpperCase())
								.setFont(basicFont).setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT)
								.setFontSize(11));

						itemTable.addCell(cell);

						p = new Paragraph(item.getPax().toString()).setFont(basicFont).setFontSize(lang == 0 ? 11 : 13)
								.setFontColor(mainColor).setTextAlignment(TextAlignment.LEFT);
						cell = new Cell().add(p);
						itemTable.addCell(cell);

						cell = new Cell().add(new Paragraph(remarks).setFont(basicFont).setFontColor(mainColor)
								.setTextAlignment(TextAlignment.LEFT));

						if (lang == 0) {
							cell.setFontSize(11);
						} else {
							cell.setFontSize(13);
						}

						itemTable.addCell(cell);
					}
					isFirst = false;

					document.add(itemTable);
				}
				if (i < menuAgencyWithItemsResponseDtos.size() - 1) {
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				}
			}

			document.close();

			String fullUrl = "";

			if (eventMasterEntity == null) {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + startDate.replace("/", "_")
						+ "/" + getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()),
								"Inside Report")
						+ ".pdf";
			} else {
				fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/"
						+ eventMasterEntity.getEventNo() + "/" + getReportName(fileName,
								formatDate(eventMasterEntity.getEventStartDateTime()), "Inside Report")
						+ ".pdf";
			}

			return fullUrl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	@Override
	public BigDecimal calculateRate(ItemWeightRateCalRequestDto request) {

		if (request.getIsOutSide()) {
			return BigDecimal.ZERO;
		}

		if (request.getIsPaxChange()) {
			menuAllocationItemRawMaterRepository.deleteRawMaterials(request.getMenuItemId(), request.getEventId(),
					request.getEventFunctionId());
			menuAllocationItemRawMaterRepository.deleteCaptainReceipeRawMaterial(request.getMenuItemId(),
					request.getEventId(), request.getEventFunctionId());
		}

		EventFunctionMasterEntity eventFunctionMasterEntity = eventFunctionRepository
				.findByIdAndIsDeleteFalse(request.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException("Event Function Not Found"));

		EventFunctionMenuAllocationEntity isExisting = menuAllocationRepository.getData(request.getMenuItemId(),
				eventFunctionMasterEntity.getId());
		if (isExisting != null) {
			isExisting.setPersonCount(request.getPersonCount());
			System.out.println("newPax Come:-" + isExisting.getPersonCount());
			menuAllocationRepository.save(isExisting);
		}
		BigDecimal totalRate = sumRawMaterialRate(request.getMenuItemId(), eventFunctionMasterEntity,
				request.getPersonCount());
		return totalRate;
	}

	public ChefAndOutsideChitthiResponseDto convertToChefOutsideResponse(
			List<MenuAllocationAgencyWithItemsResponseDto> source) {

		Map<Long, AgencyResponseDto> agencyMap = new LinkedHashMap<>();

		for (MenuAllocationAgencyWithItemsResponseDto functionData : source) {

			EventFunctionMasterResponseDto function = functionData.getEventFunction();

			for (MenuAllocationAgencyResponseDto agency : functionData.getAgencyResponse()) {

				// Get or create agency
				AgencyResponseDto agencyDto = agencyMap.computeIfAbsent(agency.getContactId(),
						id -> new AgencyResponseDto(agency.getContactId(), agency.getContactName(),
								agency.getContactNameHindi(), agency.getContactNameGujarati(), new ArrayList<>()));

				// Create function details
				FunctionDetailsResponseDto functionDetails = new FunctionDetailsResponseDto(function,
						agency.getAllocationItems());

				agencyDto.getFunctionDetails().add(functionDetails);
			}
		}

		return new ChefAndOutsideChitthiResponseDto(new ArrayList<>(agencyMap.values()));
	}

//	@Override
//	public String generateChefAgencyReportType1Docx(Long eventId, Long eventFunctionId, List<Long> agencyId,
//			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang,
//			Long userid) {
//
//		// Step 1: Generate PDF
//		generateChefAgencyReportType1(eventId, eventFunctionId, agencyId, itemId, isCompanyDetails, type, re, lang,
//				userid);
//
//		try {
//
//			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
//
//			if (eventMasterEntity == null) {
//				return null;
//			}
//
//			String fileName = eventMasterEntity.getParty().getNameEnglish();
//
//			if (agencyId != null && agencyId.size() == 1) {
//
//				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);
//
//				if (party != null) {
//					fileName = party.getNameEnglish();
//				}
//			}
//
//			String rootPath = re.getSession().getServletContext().getRealPath("/");
//
//			String pdfFileName = getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()),
//					"Chef Chitthi") + ".pdf";
//
//			String pdfPath = rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/" + pdfFileName;
//
//			File pdfFile = new File(pdfPath);
//
//			return adobeDocxGenerator.convertPdfToDocxAndGetUrl(pdfFile, eventMasterEntity.getEventNo());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Failed to generate Chef Chitthi DOCX", e);
//		}
//	}

	@Override
	public String generateChefAgencyReportType1Docx(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang,
			Long userid) {

		try {
			// ---- font family selection (POI uses font family NAMES, not embedded font
			// files) ----
			// These must be the same family names your earlier DOCX work standardized on
			// (Nirmala UI / Noto Sans Gujarati / Arial) so cross-platform rendering stays
			// consistent
			// with MenuReportDocxServiceImpl.
			String fontFamily;
			String phone, eDate, eVenue, agencyName, menuItemLabel, functionNameLabel, functionPaxLabel;

			if (lang == 1) {
				// Hindi
				fontFamily = "Nirmala UI";
				phone = "मोबाइल नंबर";
				eDate = "दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				menuItemLabel = "मेनू आइटम";
				functionNameLabel = "कार्यक्रम";
				functionPaxLabel = "मेम्बर्स";
			} else if (lang == 2) {
				// Gujarati
				fontFamily = "Noto Sans Gujarati";
				phone = "મોબાઇલ નંબર";
				eDate = "તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				menuItemLabel = "મેનુ આઇટમ";
				functionNameLabel = "કાર્યક્રમ";
				functionPaxLabel = "વ્યક્તિ";
			} else {
				// English
				fontFamily = "Arial";
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				menuItemLabel = "Menu Item Name";
				functionNameLabel = "Function";
				functionPaxLabel = "Pax";
			}

			String fontEnglish = "Times New Roman"; // equivalent of basicFont2 (times.ttf)
			String fontEnglish2 = "Arial"; // equivalent of basicFont3 (arial-regular.ttf)

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponseDtos = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, null, null);

			if (menuAgencyWithItemsResponseDtos.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			ChefAndOutsideChitthiResponseDto response = convertToChefOutsideResponse(menuAgencyWithItemsResponseDtos);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File docxFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Chitthi")
					+ ".docx");

			XWPFDocument document = new XWPFDocument();

			Color labelBgColorAwt = null; // kept for parity/reference; POI shading uses hex strings below
			String labelBgHex = "E6EAED"; // equivalent of DeviceRgb(230,234,237)
			String mainColorHex = "000000"; // equivalent of DeviceRgb(0,0,0)

			addFooter(document);

			boolean isFirst = true;
			for (AgencyResponseDto agency : response.getAgencyDetails()) {

				if (!isFirst) {
					addPageBreak(document);
				}
				isFirst = false;

				// ---- HEADER TABLE (20 / 2 / 78 percent columns, mirrored via twips) ----
				XWPFTable headerTable = document.createTable(1, 3);
				setOuterBorderOnly(headerTable); // reuse of your existing border helper
				setTableColumnWidths(headerTable, new int[] { 25, 2, 73 });

				XWPFTableRow headerRow0 = headerTable.getRow(0);

				if (isCompanyDetails == 1) {
					XWPFTableCell logoCell = headerRow0.getCell(0);
					clearCell(logoCell);
					XWPFParagraph logoPara = logoCell.getParagraphs().get(0);
					logoPara.setAlignment(ParagraphAlignment.CENTER);
					XWPFRun logoRun = logoPara.createRun();

					String logoUrl = environment.getProperty("app.image.url") + cmpDto.getLogo();
					System.out.println("logo : " + logoUrl);

					try (InputStream logoStream = fetchImageBytes(logoUrl)) {
						// 100pt width, auto-scaled height to match
						// logo.setWidth(100f)/setAutoScale(false)
						logoRun.addPicture(logoStream, PictureType.PNG, "logo.png", Units.toEMU(100), Units.toEMU(60));
					} catch (IOException e) {
						System.out.println("Failed to fetch/embed logo image: " + e.getMessage());
					}

					XWPFTableCell nameCell = headerRow0.getCell(1);
					clearCell(nameCell);
					XWPFParagraph namePara = nameCell.getParagraphs().get(0);
					namePara.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun nameRun = namePara.createRun();
					nameRun.setText(cmpDto.getCompanyName());
					nameRun.setFontFamily(fontFamily);
					setCs(nameRun, fontFamily);
					nameRun.setFontSize(14);
					nameRun.setColor(mainColorHex);
					nameRun.setBold(true);

					mergeCellsHorizontally(headerRow0, 1, 2); // merge cell 1 and cell 2

					XWPFParagraph phonePara = nameCell.addParagraph();
					phonePara.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun phoneLabelRun = phonePara.createRun();
					phoneLabelRun.setText(phone + " : ");
					phoneLabelRun.setFontFamily(fontFamily);
					setCs(phoneLabelRun, fontFamily);
					phoneLabelRun.setFontSize(lang == 0 ? 14 : 16);
					phoneLabelRun.setColor(mainColorHex);
					phoneLabelRun.setBold(true);

					XWPFRun phoneValRun = phonePara.createRun();
					phoneValRun.setText(
							cmpDto.getOfficeNo() != null ? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo() : "");
					phoneValRun.setFontFamily(fontFamily);
					setCs(phoneValRun, fontFamily);
					phoneValRun.setFontSize(14);
					phoneValRun.setColor(mainColorHex);

					clearCell(headerRow0.getCell(2));

					// second row placeholder to mirror the 3-row logo cell span in the original
					XWPFTableRow headerRow1 = headerTable.createRow();
					XWPFTableRow headerRow2 = headerTable.createRow();
				}

				// AGENCY NAME row
				XWPFTableRow agencyRow = headerTable.createRow();
				writeLabelCell(agencyRow.getCell(0), agencyName, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, true);
				writeLabelCell(agencyRow.getCell(1), ":", fontFamily, 14f, mainColorHex, true);

				String party;
				if (lang == 1) {
					party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";
				} else if (lang == 2) {
					party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";
				} else {
					party = agency.getContactName() != null ? agency.getContactName() : "";
				}
				writeLabelCell(agencyRow.getCell(2), party, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, false);

				for (FunctionDetailsResponseDto function : agency.getFunctionDetails()) {

					EventFunctionMasterResponseDto fn = function.getEventFunction();

					String fnName;
					if (lang == 1) {
						fnName = fn.getFunction().getNameHindi() != null ? fn.getFunction().getNameHindi() : "";
					} else if (lang == 2) {
						fnName = fn.getFunction().getNameGujarati() != null ? fn.getFunction().getNameGujarati() : "";
					} else {
						fnName = fn.getFunction().getNameEnglish() != null ? fn.getFunction().getNameEnglish() : "";
					}

					// GROUP PLACE WISE (identical grouping logic)
					Map<String, List<MenuAllocationItemsResponseDto>> placeWiseMap = function.getAllocationItems()
							.stream()
							.collect(Collectors
									.groupingBy(item -> item.getPlace() != null && !item.getPlace().trim().isEmpty()
											? item.getPlace()
											: "-", LinkedHashMap::new, Collectors.toList()));

					// FUNCTION DETAIL TABLE
					XWPFTable functionDetailTable = document.createTable(2, 3);
					setOuterBorderOnly(functionDetailTable);
					setTableColumnWidths(functionDetailTable, new int[] { 25, 2, 73 });

					XWPFTableRow dateRow = functionDetailTable.getRow(0);
					writeLabelCell(dateRow.getCell(0), eDate, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, true);
					writeLabelCell(dateRow.getCell(1), ":", fontFamily, 14f, mainColorHex, true);

					String date = fn.getFunctionStartDateTime().split(" ")[0];
					String time = fn.getFunctionStartDateTime().split(" ")[1] + " "
							+ fn.getFunctionStartDateTime().split(" ")[2];

					XWPFTableCell dateValCell = dateRow.getCell(2);
					clearCell(dateValCell);
					XWPFParagraph datePara = dateValCell.getParagraphs().get(0);
					XWPFRun dateRun = datePara.createRun();
					dateRun.setText(date);
					dateRun.setUnderline(UnderlinePatterns.SINGLE);
					dateRun.setFontFamily(fontEnglish2);
					dateRun.setFontSize(14);

					XWPFRun dueRun = datePara.createRun();
					dueRun.setText(" on due ");
					dueRun.setBold(true);
					dueRun.setFontFamily(fontEnglish2);
					dueRun.setFontSize(14);

					XWPFRun timeRun = datePara.createRun();
					timeRun.setText(time);
					timeRun.setUnderline(UnderlinePatterns.SINGLE);
					timeRun.setFontFamily(fontEnglish2);
					timeRun.setFontSize(14);

					XWPFTableRow fnRow = functionDetailTable.getRow(1);
					writeLabelCell(fnRow.getCell(0), functionNameLabel, fontFamily, lang == 0 ? 14f : 16f, mainColorHex,
							true);
					writeLabelCell(fnRow.getCell(1), ":", fontFamily, 14f, mainColorHex, true);
					writeLabelCell(fnRow.getCell(2), fnName, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, false);

					// PLACE WISE ITEMS (identical place-resolution logic)
					for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> placeEntry : placeWiseMap.entrySet()) {

						String placeName = placeEntry.getKey();
						List<MenuAllocationItemsResponseDto> placeItems = placeEntry.getValue();

						if (placeName.equalsIgnoreCase("venue") || placeName.equalsIgnoreCase("at venue")
								|| placeName.equalsIgnoreCase("0")) {

							if (lang == 0) {
								placeName = fn.getFunction_venue();
							} else if (lang == 1) {
								placeName = fn.getFunction_venue_hindi();
							} else {
								placeName = fn.getFunction_venue_gujarati();
							}

						} else {
							try {
								UserGodownEntity userGodownEntity = userGodownRepository
										.findByIdAndIsDeleteFalse(Long.valueOf(placeName)).orElse(null);

								if (userGodownEntity == null) {
									placeName = "";
								} else if (lang == 0) {
									placeName = userGodownEntity.getAddressEnglish();
								} else if (lang == 1) {
									placeName = userGodownEntity.getAddressHindi();
								} else {
									placeName = userGodownEntity.getAddressGujarati();
								}
							} catch (Exception e) {
								placeName = "";
							}
						}

						// VENUE TITLE
						XWPFParagraph venuePara = document.createParagraph();
						venuePara.setSpacingBefore(200);

						XWPFRun venueLabelRun = venuePara.createRun();
						venueLabelRun.setText(eVenue + " : ");
						venueLabelRun.setFontFamily(fontFamily);
						setCs(venueLabelRun, fontFamily);
						venueLabelRun.setColor(mainColorHex);
						venueLabelRun.setBold(true);
						venueLabelRun.setFontSize(lang == 0 ? 14 : 16);

						XWPFRun venueValRun = venuePara.createRun();
						venueValRun.setText(placeName != null ? placeName : "");
						venueValRun.setFontFamily(fontFamily);
						setCs(venueValRun, fontFamily);
						venueValRun.setColor(mainColorHex);
						venueValRun.setFontSize(lang == 0 ? 14 : 16);

						// ITEM TABLE (single column, header row + data rows)
						XWPFTable itemTable = document.createTable(1, 2);
						setOuterBorderOnly(itemTable);
						setTableColumnWidths(itemTable, new int[] { 50, 50 });

						// Set row height
						XWPFTableRow row = itemTable.getRow(0);
						row.setHeight(450); // adjust as needed

						// ================= Left Cell =================
						XWPFTableCell leftCell = row.getCell(0);
						clearCell(leftCell);
						shadeCell(leftCell, labelBgHex);
						leftCell.setVerticalAlignment(XWPFVertAlign.CENTER);

						XWPFParagraph leftPara = leftCell.getParagraphs().get(0);
						leftPara.setAlignment(ParagraphAlignment.LEFT);
						leftPara.setSpacingBefore(0);
						leftPara.setSpacingAfter(0);
						leftPara.setSpacingBetween(1.0);

						XWPFRun leftRun = leftPara.createRun();
						leftRun.setText(menuItemLabel);
						leftRun.setFontFamily(fontFamily);
						setCs(leftRun, fontFamily);
						leftRun.setFontSize(lang == 0 ? 14 : 16);
						leftRun.setColor(mainColorHex);
						leftRun.setBold(true);

						// ================= Right Cell =================
						XWPFTableCell rightCell = row.getCell(1);
						clearCell(rightCell);
						shadeCell(rightCell, labelBgHex);
						rightCell.setVerticalAlignment(XWPFVertAlign.CENTER);

						XWPFParagraph rightPara = rightCell.getParagraphs().get(0);
						rightPara.setAlignment(ParagraphAlignment.RIGHT);
						rightPara.setSpacingBefore(0);
						rightPara.setSpacingAfter(0);
						rightPara.setSpacingBetween(1.0);

						XWPFRun rightRun = rightPara.createRun();
						rightRun.setText(functionPaxLabel + " : " + fn.getPax());
						rightRun.setFontFamily(fontFamily);
						setCs(rightRun, fontFamily);
						rightRun.setFontSize(lang == 0 ? 14 : 16);
						rightRun.setColor(mainColorHex);
						rightRun.setBold(true);

						// Group items category-wise
						Map<String, List<MenuAllocationItemsResponseDto>> categoryWiseItems = new LinkedHashMap<>();

						for (MenuAllocationItemsResponseDto item : placeItems) {

							String categoryName;

							if (lang == 1) {
								categoryName = item.getCategoryNameHindi();
							} else if (lang == 2) {
								categoryName = item.getCategoryNameGujarati();
							} else {
								categoryName = item.getCategoryName();
							}

							if (categoryName == null || categoryName.trim().isEmpty()) {
								categoryName = "Other";
							}

							if (!categoryWiseItems.containsKey(categoryName)) {
								categoryWiseItems.put(categoryName, new ArrayList<MenuAllocationItemsResponseDto>());
							}

							categoryWiseItems.get(categoryName).add(item);
						}

						boolean isEvenRaw = false;
						boolean firstCategory = true;

						for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> categoryEntry : categoryWiseItems
								.entrySet()) {

							// ---------- Gap before category ----------
							XWPFTableRow gapRow = itemTable.createRow();

							clearCell(gapRow.getCell(0));
							clearCell(gapRow.getCell(1));

							shadeCell(gapRow.getCell(0), "FFFFFF");
							shadeCell(gapRow.getCell(1), "FFFFFF");

							// Small gap after Menu Item header, larger gap between categories
							gapRow.setHeight(100);

							firstCategory = false;

							// ---------- Category Header ----------
							XWPFTableRow categoryRow = itemTable.createRow();
							categoryRow.setHeight(350);

							XWPFTableCell categoryCell = categoryRow.getCell(0);
							clearCell(categoryCell);
							shadeCell(categoryCell, labelBgHex);

							XWPFParagraph categoryPara = categoryCell.getParagraphs().get(0);
							categoryPara.setAlignment(ParagraphAlignment.LEFT);
							categoryPara.setSpacingBefore(30);
							categoryPara.setSpacingAfter(30);

							XWPFRun categoryRun = categoryPara.createRun();
							categoryRun.setText(categoryEntry.getKey());
							categoryRun.setBold(true);
							categoryRun.setFontFamily(fontFamily);
							setCs(categoryRun, fontFamily);
							categoryRun.setFontSize(lang == 0 ? 14 : 16);
							categoryRun.setColor(mainColorHex);

							// Empty second column
							XWPFTableCell secondCell = categoryRow.getCell(1);
							clearCell(secondCell);
							shadeCell(secondCell, labelBgHex);

							// ---------- Category Items ----------
							for (MenuAllocationItemsResponseDto item : categoryEntry.getValue()) {

								String secondBgHex = isEvenRaw ? "F5F5F5" : "FFFFFF";

								String itemName = "";
								StringBuilder qtyBuilder = new StringBuilder();

								if (item.getServiceType().equalsIgnoreCase("counter_wise")
										|| item.getServiceType().equalsIgnoreCase("counter wise")) {

									qtyBuilder.append("( ").append(item.getPax()).append(" ").append(functionPaxLabel)
											.append(" )");

									if (item.getCounterQty() != 0 || item.getHelperQty() != 0) {

										List<String> parts = new ArrayList<>();

										if (item.getCounterQty() != 0) {
											parts.add(item.getCounterQty() + " "
													+ (lang == 1 ? "लेबर" : lang == 2 ? "લેબર" : "Labour"));
										}

										if (item.getHelperQty() != 0) {
											parts.add(item.getHelperQty() + " "
													+ (lang == 1 ? "हेल्पर" : lang == 2 ? "હેલ્પર" : "Helper"));
										}

										if (!parts.isEmpty()) {
											qtyBuilder.append(" (").append(String.join(", ", parts)).append(")");
										}
									}

								} else if (item.getServiceType().equalsIgnoreCase("plate_wise")
										|| item.getServiceType().equalsIgnoreCase("plate wise")) {

									if (item.getQty() != 0) {

										String unit = "";

										if (lang == 1) {
											unit = item.getUnitNameHindi() != null ? item.getUnitNameHindi() : "";
										} else if (lang == 2) {
											unit = item.getUnitNameGujarati() != null ? item.getUnitNameGujarati() : "";
										} else {
											unit = item.getUnitName() != null ? item.getUnitName() : "";
										}

										qtyBuilder.append(" (").append(item.getQty()).append(" ").append(unit)
												.append(")");
									}
								}

								String qty = qtyBuilder.toString();

								if (lang == 1) {
									itemName = item.getItemNameHindi();
								} else if (lang == 2) {
									itemName = item.getItemNameGujarati();
								} else {
									itemName = item.getItemName();
								}

								String rowText = (itemName != null ? itemName : "")
										+ ((item.getRemarks() != null && item.getRemarks().trim().length() != 0)
												? (" (" + item.getRemarks() + ")")
												: "");

								XWPFTableRow itemRow = itemTable.createRow();

								XWPFTableCell itemCell = itemRow.getCell(0);
								clearCell(itemCell);
								shadeCell(itemCell, secondBgHex);

								XWPFParagraph itemPara = itemCell.getParagraphs().get(0);
								itemPara.setAlignment(ParagraphAlignment.LEFT);

								XWPFRun itemRun = itemPara.createRun();
								itemRun.setText(rowText);
								itemRun.setFontFamily(fontFamily);
								setCs(itemRun, fontFamily);
								itemRun.setColor(mainColorHex);
								itemRun.setFontSize(lang == 0 ? 14 : 16);

								// Empty second column
								XWPFTableCell emptyCell = itemRow.getCell(1);
								clearCell(emptyCell);
								shadeCell(emptyCell, secondBgHex);

								isEvenRaw = !isEvenRaw;
							}
						}
					}
				}
			}

			try (FileOutputStream fos = new FileOutputStream(docxFile)) {
				document.write(fos);
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/docx/"
					+ eventMasterEntity.getEventNo() + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "Chef Chitthi")
					+ ".docx";
			return fullUrl;

		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private void mergeCellsHorizontally(XWPFTableRow row, int fromCell, int toCell) {
		for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
			XWPFTableCell cell = row.getCell(cellIndex);
			CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
			CTHMerge hMerge = tcPr.isSetHMerge() ? tcPr.getHMerge() : tcPr.addNewHMerge();
			hMerge.setVal(cellIndex == fromCell ? STMerge.RESTART : STMerge.CONTINUE);
		}
	}

	// ------------------------------------------------------------------------------------------
	// Helpers referenced above. If you already have equivalents in
	// MenuReportDocxServiceImpl
	// (setOuterBorderOnly, setCs, etc.) reuse those instead of duplicating these.
	// ------------------------------------------------------------------------------------------

	private void addFooter(XWPFDocument document) {

		CTSectPr sectPr = document.getDocument().getBody().isSetSectPr() ? document.getDocument().getBody().getSectPr()
				: document.getDocument().getBody().addNewSectPr();

		XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

		XWPFFooter footer = policy.createFooter(STHdrFtr.Enum.forString("default"));

		XWPFTable table = footer.createTable(1, 3);

		table.setWidth("100%");

		removeBorders(table);

		// ==========================
		// LEFT
		// ==========================

		XWPFParagraph left = table.getRow(0).getCell(0).getParagraphs().get(0);

		left.setAlignment(ParagraphAlignment.LEFT);
		left.setSpacingBefore(0);
		left.setSpacingAfter(0);
		left.setSpacingBetween(1.0);

		XWPFRun leftRun = left.createRun();

		leftRun.setFontFamily("Arial");
		leftRun.setFontSize(10);
		leftRun.setText("Prepared By __________");

		// ==========================
		// CENTER
		// ==========================

		XWPFParagraph center = table.getRow(0).getCell(1).getParagraphs().get(0);

		center.setAlignment(ParagraphAlignment.CENTER);
		center.setSpacingBefore(0);
		center.setSpacingAfter(0);
		center.setSpacingBetween(1.0);

		XWPFRun pageText = center.createRun();

		pageText.setFontFamily("Arial");
		pageText.setFontSize(10);
		pageText.setText("Page ");

		addPageField(center);

		XWPFRun ofRun = center.createRun();

		ofRun.setFontFamily("Arial");
		ofRun.setFontSize(10);
		ofRun.setText(" of ");

		addTotalPagesField(center);

		// ==========================
		// RIGHT
		// ==========================

		XWPFParagraph right = table.getRow(0).getCell(2).getParagraphs().get(0);

		right.setAlignment(ParagraphAlignment.RIGHT);
		right.setSpacingBefore(0);
		right.setSpacingAfter(0);
		right.setSpacingBetween(1.0);

		XWPFRun rightRun = right.createRun();

		rightRun.setFontFamily("Arial");
		rightRun.setFontSize(10);
		rightRun.setText("Submitted By __________");
	}

	private void addPageField(XWPFParagraph paragraph) {

		XWPFRun run = paragraph.createRun();

		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
		run.getCTR().addNewInstrText().setStringValue(" PAGE ");
		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
	}

	private void addTotalPagesField(XWPFParagraph paragraph) {

		XWPFRun run = paragraph.createRun();

		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
		run.getCTR().addNewInstrText().setStringValue(" NUMPAGES ");
		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
	}

	/**
	 * Converts iText's ImageData (as returned by
	 * menuPreparationServiceImpl.loadImageFromResource) into an InputStream that
	 * POI's XWPFRun.addPicture can consume. Falls back to re-reading from the
	 * image's source URL if the raw byte data isn't already loaded in memory.
	 */
	private InputStream imageDataToInputStream(com.itextpdf.io.image.ImageData imageData) throws IOException {
		byte[] data = imageData.getData();
		if (data == null && imageData.getUrl() != null) {
			try (InputStream urlStream = imageData.getUrl().openStream()) {
				java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
				byte[] chunk = new byte[8192];
				int bytesRead;
				while ((bytesRead = urlStream.read(chunk)) != -1) {
					buffer.write(chunk, 0, bytesRead);
				}
				data = buffer.toByteArray();
			}
		}
		if (data == null) {
			throw new IOException(
					"Unable to read image bytes from ImageData - no in-memory data and no URL available.");
		}
		return new ByteArrayInputStream(data);
	}

	private void addPageBreak(XWPFDocument document) {
		XWPFParagraph p = document.createParagraph();
		XWPFRun r = p.createRun();
		r.addBreak(BreakType.PAGE);
	}

	/**
	 * Sets w:cs (complex-script) font alongside the ascii font, per your
	 * established pattern for correct Hindi/Gujarati glyph shaping.
	 */
	private void setCs(XWPFRun run, String fontFamily) {
		run.setFontFamily(fontFamily); // sets ascii + hAnsi
		run.setFontFamily(fontFamily, XWPFRun.FontCharRange.cs); // sets the complex-script font
	}

	private void writeLabelCell(XWPFTableCell cell, String text, String fontFamily, float size, String colorHex,
			boolean bold) {
		clearCell(cell);
		XWPFParagraph p = cell.getParagraphs().get(0);
		p.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun r = p.createRun();
		r.setText(text);
		r.setFontFamily(fontFamily);
		setCs(r, fontFamily);
		r.setFontSize((int) size);
		r.setColor(colorHex);
		r.setBold(bold);
	}

	private void clearCell(XWPFTableCell cell) {
		cell.removeParagraph(0);
		cell.addParagraph();
	}

	private void shadeCell(XWPFTableCell cell, String hex) {
		cell.setColor(hex);
	}

	private void setTableColumnWidths(XWPFTable table, int[] percentages) {
		// Standard body width ~ 9350 DXA for A4 with default margins; adjust to your
		// page setup.
		int totalWidth = 9350;
		CTTblGrid grid = table.getCTTbl().getTblGrid();
		if (grid == null) {
			// tblGrid is a required element in the schema, so XMLBeans doesn't generate
			// isSetTblGrid() - POI's createTable() normally populates it already, this is
			// just a safety net.
			grid = table.getCTTbl().addNewTblGrid();
		}
		while (grid.sizeOfGridColArray() > 0) {
			grid.removeGridCol(0);
		}
		for (int pct : percentages) {
			CTTblGridCol col = grid.addNewGridCol();
			col.setW(BigInteger.valueOf((totalWidth * pct) / 100));
		}
		for (XWPFTableRow row : table.getRows()) {
			for (int i = 0; i < row.getTableCells().size() && i < percentages.length; i++) {
				row.getCell(i).setWidth(String.valueOf((totalWidth * percentages[i]) / 100));
			}
		}
	}

	/**
	 * Reuse of your existing setOuterBorderOnly() helper from
	 * MenuReportDocxServiceImpl.
	 */
	private void setOuterBorderOnly(XWPFTable table) {
		CTTblBorders borders = table.getCTTbl().getTblPr().isSetTblBorders()
				? table.getCTTbl().getTblPr().getTblBorders()
				: table.getCTTbl().getTblPr().addNewTblBorders();
		borders.addNewTop().setVal(STBorder.NONE);
		borders.addNewBottom().setVal(STBorder.NONE);
		borders.addNewLeft().setVal(STBorder.NONE);
		borders.addNewRight().setVal(STBorder.NONE);
		borders.addNewInsideH().setVal(STBorder.NONE);
		borders.addNewInsideV().setVal(STBorder.NONE);
	}

//	@Override
//	public String generateOutsideAgencyReportType1Docx(Long eventId, Long eventFunctionId, List<Long> agencyId,
//			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang,
//			Long userid) {
//
//		// Step 1: Generate PDF
//		generateOutsideAgencyReportType1(eventId, eventFunctionId, agencyId, itemId, isCompanyDetails, type, re, lang,
//				userid);
//
//		try {
//
//			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
//
//			if (eventMasterEntity == null) {
//				return null;
//			}
//
//			String fileName = eventMasterEntity.getParty().getNameEnglish();
//
//			if (agencyId != null && agencyId.size() == 1) {
//
//				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);
//
//				if (party != null) {
//					fileName = party.getNameEnglish();
//				}
//			}
//
//			String rootPath = re.getSession().getServletContext().getRealPath("/");
//
//			String pdfFileName = getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()),
//					"outside chitthi") + ".pdf";
//
//			String pdfPath = rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/" + pdfFileName;
//
//			return adobeDocxGenerator.convertPdfToDocxAndGetUrl(new File(pdfPath), eventMasterEntity.getEventNo());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Failed to generate Outside Chitthi DOCX", e);
//		}
//	}

	@Override
	public String generateOutsideAgencyReportType1Docx(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang,
			Long userid) {

		try {
			// ---- font family selection (POI uses font family NAMES, not embedded font
			// files) ----
			// These must be the same family names your earlier DOCX work standardized on
			// (Nirmala UI / Noto Sans Gujarati / Arial) so cross-platform rendering stays
			// consistent
			// with MenuReportDocxServiceImpl.
			String fontFamily;
			String phone, eDate, eVenue, agencyName, menuItemLabel, functionNameLabel, functionPaxLabel;

			if (lang == 1) {
				// Hindi
				fontFamily = "Nirmala UI";
				phone = "मोबाइल नंबर";
				eDate = "दिनांक";
				eVenue = "स्थान";
				agencyName = "एजेंसी का नाम";
				menuItemLabel = "मेनू आइटम";
				functionNameLabel = "कार्यक्रम";
				functionPaxLabel = "मेम्बर्स";
			} else if (lang == 2) {
				// Gujarati
				fontFamily = "Noto Sans Gujarati";
				phone = "મોબાઇલ નંબર";
				eDate = "તારીખ";
				eVenue = "સ્થળ";
				agencyName = "એજન્સીનું નામ";
				menuItemLabel = "મેનુ આઇટમ";
				functionNameLabel = "કાર્યક્રમ";
				functionPaxLabel = "વ્યક્તિ";
			} else {
				// English
				fontFamily = "Arial";
				phone = "Mobile No.";
				eDate = "Date";
				eVenue = "Venue";
				agencyName = "Agency Name";
				menuItemLabel = "Menu Item Name";
				functionNameLabel = "Function";
				functionPaxLabel = "Pax";
			}

			String fontEnglish = "Times New Roman"; // equivalent of basicFont2 (times.ttf)
			String fontEnglish2 = "Arial"; // equivalent of basicFont3 (arial-regular.ttf)

			CompanyDetailsResponseDto cmpDto = getCompanyDetails(userid);

			List<MenuAllocationAgencyWithItemsResponseDto> menuAgencyWithItemsResponseDtos = getAgencyWithItemType(type,
					eventId, eventFunctionId, agencyId, itemId, null, null);

			if (menuAgencyWithItemsResponseDtos.isEmpty()) {
				return "No agency has been allocated for any items.";
			}

			ChefAndOutsideChitthiResponseDto response = convertToChefOutsideResponse(menuAgencyWithItemsResponseDtos);

			EventMasterEntity eventMasterEntity = eventRepository.findByIdAndIsDeleteFalse(eventId).orElse(null);
			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/" + eventMasterEntity.getEventNo() + "/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}

			String fileName = eventMasterEntity.getParty().getNameEnglish();

			if (agencyId.size() == 1) {
				PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(agencyId.get(0)).orElse(null);

				if (party != null) {
					fileName = party.getNameEnglish();
				}
			}

			File docxFile = new File(outputPath + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside Chitthi")
					+ ".docx");

			XWPFDocument document = new XWPFDocument();

			Color labelBgColorAwt = null; // kept for parity/reference; POI shading uses hex strings below
			String labelBgHex = "E6EAED"; // equivalent of DeviceRgb(230,234,237)
			String mainColorHex = "000000"; // equivalent of DeviceRgb(0,0,0)

			addFooter(document);

			boolean isFirst = true;
			for (AgencyResponseDto agency : response.getAgencyDetails()) {

				if (!isFirst) {
					addPageBreak(document);
				}
				isFirst = false;

				// ---- HEADER TABLE (20 / 2 / 78 percent columns, mirrored via twips) ----
				XWPFTable headerTable = document.createTable(1, 3);
				setOuterBorderOnly(headerTable); // reuse of your existing border helper
				setTableColumnWidths(headerTable, new int[] { 25, 2, 73 });

				XWPFTableRow headerRow0 = headerTable.getRow(0);

				if (isCompanyDetails == 1) {
					XWPFTableCell logoCell = headerRow0.getCell(0);
					clearCell(logoCell);
					XWPFParagraph logoPara = logoCell.getParagraphs().get(0);
					logoPara.setAlignment(ParagraphAlignment.CENTER);
					XWPFRun logoRun = logoPara.createRun();

					String logoUrl = environment.getProperty("app.image.url") + cmpDto.getLogo();
					System.out.println("logo : " + logoUrl);

					try (InputStream logoStream = fetchImageBytes(logoUrl)) {
						// 100pt width, auto-scaled height to match
						// logo.setWidth(100f)/setAutoScale(false)
						logoRun.addPicture(logoStream, PictureType.PNG, "logo.png", Units.toEMU(100), Units.toEMU(60));
					} catch (IOException e) {
						System.out.println("Failed to fetch/embed logo image: " + e.getMessage());
					}

					XWPFTableCell nameCell = headerRow0.getCell(1);
					clearCell(nameCell);
					XWPFParagraph namePara = nameCell.getParagraphs().get(0);
					namePara.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun nameRun = namePara.createRun();
					nameRun.setText(cmpDto.getCompanyName());
					nameRun.setFontFamily(fontFamily);
					setCs(nameRun, fontFamily);
					nameRun.setFontSize(14);
					nameRun.setColor(mainColorHex);
					nameRun.setBold(true);

					mergeCellsHorizontally(headerRow0, 1, 2); // merge cell 1 and cell 2

					XWPFParagraph phonePara = nameCell.addParagraph();
					phonePara.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun phoneLabelRun = phonePara.createRun();
					phoneLabelRun.setText(phone + " : ");
					phoneLabelRun.setFontFamily(fontFamily);
					setCs(phoneLabelRun, fontFamily);
					phoneLabelRun.setFontSize(lang == 0 ? 14 : 16);
					phoneLabelRun.setColor(mainColorHex);
					phoneLabelRun.setBold(true);

					XWPFRun phoneValRun = phonePara.createRun();
					phoneValRun.setText(
							cmpDto.getOfficeNo() != null ? cmpDto.getCountryCode() + " " + cmpDto.getOfficeNo() : "");
					phoneValRun.setFontFamily(fontFamily);
					setCs(phoneValRun, fontFamily);
					phoneValRun.setFontSize(14);
					phoneValRun.setColor(mainColorHex);

					clearCell(headerRow0.getCell(2));

					// second row placeholder to mirror the 3-row logo cell span in the original
					XWPFTableRow headerRow1 = headerTable.createRow();
					XWPFTableRow headerRow2 = headerTable.createRow();
				}

				// AGENCY NAME row
				XWPFTableRow agencyRow = headerTable.createRow();
				writeLabelCell(agencyRow.getCell(0), agencyName, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, true);
				writeLabelCell(agencyRow.getCell(1), ":", fontFamily, 14f, mainColorHex, true);

				String party;
				if (lang == 1) {
					party = agency.getContactNameHindi() != null ? agency.getContactNameHindi() : "";
				} else if (lang == 2) {
					party = agency.getContactNameGujarati() != null ? agency.getContactNameGujarati() : "";
				} else {
					party = agency.getContactName() != null ? agency.getContactName() : "";
				}
				writeLabelCell(agencyRow.getCell(2), party, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, false);

				for (FunctionDetailsResponseDto function : agency.getFunctionDetails()) {

					EventFunctionMasterResponseDto fn = function.getEventFunction();

					String fnName;
					if (lang == 1) {
						fnName = fn.getFunction().getNameHindi() != null ? fn.getFunction().getNameHindi() : "";
					} else if (lang == 2) {
						fnName = fn.getFunction().getNameGujarati() != null ? fn.getFunction().getNameGujarati() : "";
					} else {
						fnName = fn.getFunction().getNameEnglish() != null ? fn.getFunction().getNameEnglish() : "";
					}

					// GROUP PLACE WISE (identical grouping logic)
					Map<String, List<MenuAllocationItemsResponseDto>> placeWiseMap = function.getAllocationItems()
							.stream()
							.collect(Collectors
									.groupingBy(item -> item.getPlace() != null && !item.getPlace().trim().isEmpty()
											? item.getPlace()
											: "-", LinkedHashMap::new, Collectors.toList()));

					// FUNCTION DETAIL TABLE
					XWPFTable functionDetailTable = document.createTable(2, 3);
					setOuterBorderOnly(functionDetailTable);
					setTableColumnWidths(functionDetailTable, new int[] { 25, 2, 73 });

					XWPFTableRow dateRow = functionDetailTable.getRow(0);
					writeLabelCell(dateRow.getCell(0), eDate, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, true);
					writeLabelCell(dateRow.getCell(1), ":", fontFamily, 14f, mainColorHex, true);

					String date = fn.getFunctionStartDateTime().split(" ")[0];
					String time = fn.getFunctionStartDateTime().split(" ")[1] + " "
							+ fn.getFunctionStartDateTime().split(" ")[2];

					XWPFTableCell dateValCell = dateRow.getCell(2);
					clearCell(dateValCell);
					XWPFParagraph datePara = dateValCell.getParagraphs().get(0);
					XWPFRun dateRun = datePara.createRun();
					dateRun.setText(date);
					dateRun.setUnderline(UnderlinePatterns.SINGLE);
					dateRun.setFontFamily(fontEnglish2);
					dateRun.setFontSize(14);

					XWPFRun dueRun = datePara.createRun();
					dueRun.setText(" on due ");
					dueRun.setBold(true);
					dueRun.setFontFamily(fontEnglish2);
					dueRun.setFontSize(14);

					XWPFRun timeRun = datePara.createRun();
					timeRun.setText(time);
					timeRun.setUnderline(UnderlinePatterns.SINGLE);
					timeRun.setFontFamily(fontEnglish2);
					timeRun.setFontSize(14);

					XWPFTableRow fnRow = functionDetailTable.getRow(1);
					writeLabelCell(fnRow.getCell(0), functionNameLabel, fontFamily, lang == 0 ? 14f : 16f, mainColorHex,
							true);
					writeLabelCell(fnRow.getCell(1), ":", fontFamily, 14f, mainColorHex, true);
					writeLabelCell(fnRow.getCell(2), fnName, fontFamily, lang == 0 ? 14f : 16f, mainColorHex, false);

					// PLACE WISE ITEMS (identical place-resolution logic)
					for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> placeEntry : placeWiseMap.entrySet()) {

						String placeName = placeEntry.getKey();
						List<MenuAllocationItemsResponseDto> placeItems = placeEntry.getValue();

						if (placeName.equalsIgnoreCase("venue") || placeName.equalsIgnoreCase("at venue")
								|| placeName.equalsIgnoreCase("0")) {

							if (lang == 0) {
								placeName = fn.getFunction_venue();
							} else if (lang == 1) {
								placeName = fn.getFunction_venue_hindi();
							} else {
								placeName = fn.getFunction_venue_gujarati();
							}

						} else {
							try {
								UserGodownEntity userGodownEntity = userGodownRepository
										.findByIdAndIsDeleteFalse(Long.valueOf(placeName)).orElse(null);

								if (userGodownEntity == null) {
									placeName = "";
								} else if (lang == 0) {
									placeName = userGodownEntity.getAddressEnglish();
								} else if (lang == 1) {
									placeName = userGodownEntity.getAddressHindi();
								} else {
									placeName = userGodownEntity.getAddressGujarati();
								}
							} catch (Exception e) {
								placeName = "";
							}
						}

						// VENUE TITLE
						XWPFParagraph venuePara = document.createParagraph();
						venuePara.setSpacingBefore(200);

						XWPFRun venueLabelRun = venuePara.createRun();
						venueLabelRun.setText(eVenue + " : ");
						venueLabelRun.setFontFamily(fontFamily);
						setCs(venueLabelRun, fontFamily);
						venueLabelRun.setColor(mainColorHex);
						venueLabelRun.setBold(true);
						venueLabelRun.setFontSize(lang == 0 ? 14 : 16);

						XWPFRun venueValRun = venuePara.createRun();
						venueValRun.setText(placeName != null ? placeName : "");
						venueValRun.setFontFamily(fontFamily);
						setCs(venueValRun, fontFamily);
						venueValRun.setColor(mainColorHex);
						venueValRun.setFontSize(lang == 0 ? 14 : 16);

						// ITEM TABLE (single column, header row + data rows)
						XWPFTable itemTable = document.createTable(1, 2);
						setOuterBorderOnly(itemTable);
						setTableColumnWidths(itemTable, new int[] { 50, 50 });

						// Set row height
						XWPFTableRow row = itemTable.getRow(0);
						row.setHeight(450); // adjust as needed

						// ================= Left Cell =================
						XWPFTableCell leftCell = row.getCell(0);
						clearCell(leftCell);
						shadeCell(leftCell, labelBgHex);
						leftCell.setVerticalAlignment(XWPFVertAlign.CENTER);

						XWPFParagraph leftPara = leftCell.getParagraphs().get(0);
						leftPara.setAlignment(ParagraphAlignment.LEFT);
						leftPara.setSpacingBefore(0);
						leftPara.setSpacingAfter(0);
						leftPara.setSpacingBetween(1.0);

						XWPFRun leftRun = leftPara.createRun();
						leftRun.setText(menuItemLabel);
						leftRun.setFontFamily(fontFamily);
						setCs(leftRun, fontFamily);
						leftRun.setFontSize(lang == 0 ? 14 : 16);
						leftRun.setColor(mainColorHex);
						leftRun.setBold(true);

						// ================= Right Cell =================
						XWPFTableCell rightCell = row.getCell(1);
						clearCell(rightCell);
						shadeCell(rightCell, labelBgHex);
						rightCell.setVerticalAlignment(XWPFVertAlign.CENTER);

						XWPFParagraph rightPara = rightCell.getParagraphs().get(0);
						rightPara.setAlignment(ParagraphAlignment.RIGHT);
						rightPara.setSpacingBefore(0);
						rightPara.setSpacingAfter(0);
						rightPara.setSpacingBetween(1.0);

						XWPFRun rightRun = rightPara.createRun();
						rightRun.setText(functionPaxLabel + " : " + fn.getPax());
						rightRun.setFontFamily(fontFamily);
						setCs(rightRun, fontFamily);
						rightRun.setFontSize(lang == 0 ? 14 : 16);
						rightRun.setColor(mainColorHex);
						rightRun.setBold(true);

						Map<String, List<MenuAllocationItemsResponseDto>> categoryWiseItems = new LinkedHashMap<>();

						for (MenuAllocationItemsResponseDto item : placeItems) {

							String categoryName;

							if (lang == 1) {
								categoryName = item.getCategoryNameHindi();
							} else if (lang == 2) {
								categoryName = item.getCategoryNameGujarati();
							} else {
								categoryName = item.getCategoryName();
							}

							if (categoryName == null || categoryName.trim().isEmpty()) {
								categoryName = "Other";
							}

							if (!categoryWiseItems.containsKey(categoryName)) {
								categoryWiseItems.put(categoryName, new ArrayList<MenuAllocationItemsResponseDto>());
							}

							categoryWiseItems.get(categoryName).add(item);
						}

						boolean isEvenRaw = false;
						boolean firstCategory = true;

						for (Map.Entry<String, List<MenuAllocationItemsResponseDto>> categoryEntry : categoryWiseItems
								.entrySet()) {

							// ---------- Gap before category ----------
							XWPFTableRow gapRow = itemTable.createRow();
							gapRow.setHeight(100);

							XWPFTableCell gapCell1 = gapRow.getCell(0);
							clearCell(gapCell1);
							shadeCell(gapCell1, "FFFFFF");

							XWPFTableCell gapCell2 = gapRow.getCell(1);
							clearCell(gapCell2);
							shadeCell(gapCell2, "FFFFFF");

							firstCategory = false;

							// ---------- Category Header ----------
							XWPFTableRow categoryRow = itemTable.createRow();
							categoryRow.setHeight(350);

							XWPFTableCell categoryCell = categoryRow.getCell(0);
							clearCell(categoryCell);
							shadeCell(categoryCell, labelBgHex);

							XWPFParagraph categoryPara = categoryCell.getParagraphs().get(0);
							categoryPara.setAlignment(ParagraphAlignment.LEFT);
							categoryPara.setSpacingBefore(30);
							categoryPara.setSpacingAfter(30);

							XWPFRun categoryRun = categoryPara.createRun();
							categoryRun.setText(categoryEntry.getKey());
							categoryRun.setBold(true);
							categoryRun.setFontFamily(fontFamily);
							setCs(categoryRun, fontFamily);
							categoryRun.setFontSize(lang == 0 ? 14 : 16);
							categoryRun.setColor(mainColorHex);

							// Empty second column
							XWPFTableCell secondCell = categoryRow.getCell(1);
							clearCell(secondCell);
							shadeCell(secondCell, labelBgHex);

							// ---------- Category Items ----------
							for (MenuAllocationItemsResponseDto item : categoryEntry.getValue()) {

								String secondBgHex = isEvenRaw ? "F5F5F5" : "FFFFFF";

								String itemName = "";
								String unit = "";

								if (lang == 1) {
									itemName = item.getItemNameHindi();
									unit = item.getUnitNameHindi() == null ? "" : item.getUnitNameHindi();
								} else if (lang == 2) {
									itemName = item.getItemNameGujarati();
									unit = item.getUnitNameGujarati() == null ? "" : item.getUnitNameGujarati();
								} else {
									itemName = item.getItemName();
									unit = item.getUnitName() == null ? "" : item.getUnitName();
								}

								String qtyText = "";

//								if (item.getQty() != null && item.getQty() != 0) {
//									qtyText = " ( " + item.getQty() + " " + unit + " )";
//								}

								String notes = (item.getRemarks() != null && item.getRemarks().trim().length() != 0)
										? (" (" + item.getRemarks() + ")")
										: "";

								XWPFTableRow itemRow = itemTable.createRow();

								XWPFTableCell itemCell = itemRow.getCell(0);
								clearCell(itemCell);
								shadeCell(itemCell, secondBgHex);

								XWPFParagraph itemPara = itemCell.getParagraphs().get(0);
								itemPara.setAlignment(ParagraphAlignment.LEFT);

								XWPFRun itemRun = itemPara.createRun();
								itemRun.setText((itemName != null ? itemName : "") + qtyText + notes);
								itemRun.setFontFamily(fontFamily);
								setCs(itemRun, fontFamily);
								itemRun.setFontSize(lang == 0 ? 14 : 16);
								itemRun.setColor(mainColorHex);

								// Empty second column
								XWPFTableCell qtyCell = itemRow.getCell(1);
								clearCell(qtyCell);
								shadeCell(qtyCell, secondBgHex);

								isEvenRaw = !isEvenRaw;
							}
						}
					}
				}
			}

			try (FileOutputStream fos = new FileOutputStream(docxFile)) {
				document.write(fos);
			}
			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/docx/"
					+ eventMasterEntity.getEventNo() + "/"
					+ getReportName(fileName, formatDate(eventMasterEntity.getEventStartDateTime()), "outside Chitthi")
					+ ".docx";
			return fullUrl;

		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private void removeBorders(XWPFTable table) {

		CTTblPr pr = table.getCTTbl().getTblPr();

		if (pr == null) {
			pr = table.getCTTbl().addNewTblPr();
		}

		CTTblBorders borders = pr.getTblBorders();

		if (borders == null) {
			borders = pr.addNewTblBorders();
		}

		removeBorder(borders.isSetTop() ? borders.getTop() : borders.addNewTop());
		removeBorder(borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom());
		removeBorder(borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft());
		removeBorder(borders.isSetRight() ? borders.getRight() : borders.addNewRight());
		removeBorder(borders.isSetInsideH() ? borders.getInsideH() : borders.addNewInsideH());
		removeBorder(borders.isSetInsideV() ? borders.getInsideV() : borders.addNewInsideV());
	}

	private void removeBorder(CTBorder border) {

		border.setVal(STBorder.NONE);
	}

	@Override
	public Boolean syncItemWiseRawmaterialByMenuItemId(Long eventFunctionId, Long eventId, Long menuItemId) {
		List<EventFunctionMasterEntity> eventFunctions;

		if (eventFunctionId == -1) {
			eventFunctions = eventFunctionRepository.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventId);

			if (eventFunctions.isEmpty()) {
				return false;
			}
		} else {
			eventFunctions = eventFunctionRepository.findById(eventFunctionId).map(Collections::singletonList)
					.orElse(Collections.emptyList());

			if (eventFunctions.isEmpty()) {
				return false;
			}
		}

		for (EventFunctionMasterEntity eventFunctionMasterEntity : eventFunctions) {
			List<Long> deletedMenuItemsId = new ArrayList<>();
			List<Long> addedMenuItemsId = new ArrayList<>();
			deletedMenuItemsId.add(menuItemId);

			menuAllocationItemRawMaterRepository.deleteRawMaterials(menuItemId,
					eventFunctionMasterEntity.getEvent().getId(), eventFunctionMasterEntity.getId());

			menuAllocationItemCaptainReceipeRepository.deleteAllocatedCaptainReceipe(menuItemId,
					eventFunctionMasterEntity.getEvent().getId(), eventFunctionMasterEntity.getId());
			getAllRawMaterial(menuItemId, eventFunctionMasterEntity, null);
			Map<String, List<Long>> rawMaterialMapId = new HashMap<>();
			rawMaterialMapId.put("delete", deletedMenuItemsId);
			rawMaterialMapId.put("add", addedMenuItemsId);
			eventRawMaterialHelperService.addEventRawMaterialFunctionsByMenuItemId(
					eventFunctionMasterEntity.getEvent().getId(), eventFunctionMasterEntity.getId(),
					deletedMenuItemsId);
		}
		return true;
	}

	/**
	 * Fetches raw image bytes directly over HTTP(S), bypassing iText's ImageData
	 * which may not have the bytes populated yet for URL-backed images.
	 */
	private InputStream fetchImageBytes(String urlStr) throws IOException {
		java.net.URL url = new java.net.URL(urlStr);
		java.net.URLConnection connection = url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		try (InputStream in = connection.getInputStream();
				java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream()) {
			byte[] chunk = new byte[8192];
			int bytesRead;
			while ((bytesRead = in.read(chunk)) != -1) {
				buffer.write(chunk, 0, bytesRead);
			}
			byte[] data = buffer.toByteArray();
			if (data.length == 0) {
				throw new IOException("Downloaded 0 bytes from " + urlStr);
			}
			return new ByteArrayInputStream(data);
		}
	}

	@Override
	public String generateAttendanceReport(Long eventId, HttpServletRequest request) {

		try {

			menuPreparationServiceImpl.loadLicense();

			PdfFont basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/times.ttf");

			EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));

			String rootPath = request.getSession().getServletContext().getRealPath("/");
			File outputPath = new File(rootPath + "resources/tempDownload/" + event.getEventNo() + "/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}

			File pdfFile = new File(outputPath, "attendance_report.pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, PageSize.A4);

			document.setMargins(20, 20, 20, 20);

			// =========================================================
			// Title
			// =========================================================

			Paragraph title = new Paragraph("Attendance Report").setFont(boldFont).setFontSize(24).simulateBold()
					.setUnderline().setTextAlignment(TextAlignment.CENTER).setMarginBottom(20);

			document.add(title);

			// =========================================================
			// Event Details
			// =========================================================

			Table detailsTable = new Table(UnitValue.createPercentArray(new float[] { 15, 45, 10, 30 }));
			detailsTable.setWidth(UnitValue.createPercentValue(100));
			detailsTable.setMarginBottom(15);

			detailsTable.addCell(
					new Cell().add(new Paragraph("Event Name :").setFont(basicFont)).setBorder(Border.NO_BORDER));

			detailsTable.addCell(new Cell().add(new Paragraph("").setFont(basicFont)).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(1f)));

			detailsTable.addCell(new Cell().setBorder(Border.NO_BORDER));
			detailsTable.addCell(new Cell().setBorder(Border.NO_BORDER));

			detailsTable.addCell(
					new Cell().add(new Paragraph("Event Date :").setFont(basicFont)).setBorder(Border.NO_BORDER));

			detailsTable.addCell(new Cell().add(new Paragraph("").setFont(basicFont)).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(1f)));

			detailsTable.addCell(new Cell().add(new Paragraph("Place :").setFont(basicFont)).setBorder(Border.NO_BORDER)
					.setPaddingLeft(10f));

			detailsTable.addCell(new Cell().add(new Paragraph("").setFont(basicFont)).setBorder(Border.NO_BORDER)
					.setBorderBottom(new SolidBorder(1f)));

			document.add(detailsTable);

			// =========================================================
			// Attendance Table
			// =========================================================

			float[] widths = { 7, 31, 26, 13, 13, 17 };

			Table table = new Table(UnitValue.createPercentArray(widths));
			table.setWidth(UnitValue.createPercentValue(100));

			String[] headers = { "No.", "Name", "Mo. Number", "In Time", "Out Time", "Sign." };

			for (String header : headers) {

				Cell cell = new Cell().add(new Paragraph(header).setFont(basicFont).simulateBold().setFontSize(11))
						.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setPadding(4);

				table.addHeaderCell(cell);
			}

			// =========================================================
			// Blank Rows
			// =========================================================

			for (int i = 1; i <= 20; i++) {

				table.addCell(
						new Cell().add(new Paragraph("").setFont(basicFont)).setTextAlignment(TextAlignment.CENTER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE).setHeight(24));

				table.addCell(new Cell().add(new Paragraph("")).setHeight(24));

				table.addCell(new Cell().add(new Paragraph("")).setHeight(24));

				table.addCell(new Cell().add(new Paragraph("")).setHeight(24));

				table.addCell(new Cell().add(new Paragraph("")).setHeight(24));

				table.addCell(new Cell().add(new Paragraph("")).setHeight(24));
			}

			document.add(table);

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + event.getEventNo()
					+ "/attendance_report.pdf";

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Attendance Report", e);
		}
	}

	@Override
	public String generateItemOrderHistoryReport(Integer isCompanyDetails, String startDate, String endDate,
			List<Long> itemId, ReportMenuPlanningRequestDTO request, HttpServletRequest re, Long userId) {

		try {

			menuPreparationServiceImpl.loadLicense();

			if (userId == null) {
				throw new RuntimeException("User ID not found.");
			}

			if (itemId == null || itemId.isEmpty()) {
				throw new RuntimeException("Item ID is required.");
			}

			DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			LocalDateTime stDate = LocalDate.parse(startDate.trim(), dateFormater).atStartOfDay();
			LocalDateTime eDate = LocalDate.parse(endDate.trim(), dateFormater).plusDays(1).atStartOfDay();

			List<Object[]> reportData = menuAllocationRepository.getItemOrderHistoryReport(userId, itemId, stDate,
					eDate);

			if (reportData == null || reportData.isEmpty()) {
				throw new RuntimeException("No data found for the selected item and date range.");
			}

			PdfFont regularFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			PdfFont boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");

			String reportName = "ITEM_ORDER_HISTORY_REPORT";

			String fileName = reportName + "_" + startDate.replace("/", "-") + "_" + endDate.replace("/", "-") + ".pdf";

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/itemorderhistory/");

			if (!outputPath.exists()) {
				outputPath.mkdirs();
			}
			File filePath = new File(outputPath + "/" + fileName);

			PdfWriter writer = new PdfWriter(filePath);
			PdfDocument pdfDocument = new PdfDocument(writer);

			PageSize pageSize = PageSize.A4;

			Document document = new Document(pdfDocument, pageSize, false);

			document.setMargins(25, 25, 35, 25);

			Paragraph reportTitle = new Paragraph("ITEM ORDER HISTORY REPORT").setFont(boldFont).setFontSize(15)
					.setTextAlignment(TextAlignment.CENTER).setMarginBottom(5);

			document.add(reportTitle);

			String formattedStartDate = startDate;
			String formattedEndDate = endDate;

			try {
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

				formattedStartDate = LocalDate.parse(startDate, inputFormatter).format(outputFormatter);

				formattedEndDate = LocalDate.parse(endDate, inputFormatter).format(outputFormatter);

			} catch (Exception ex) {
				// Keep original date if parsing fails
			}

			Paragraph dateParagraph = new Paragraph("From " + formattedStartDate + " To " + formattedEndDate)
					.setFont(regularFont).setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10);

			document.add(dateParagraph);

			if (isCompanyDetails != null && isCompanyDetails == 1) {

				Table companyTable = new Table(UnitValue.createPercentArray(new float[] { 100f }))
						.setWidth(UnitValue.createPercentValue(100));

				Cell companyCell = new Cell().setPadding(6).setBorder(new SolidBorder(1));

				companyCell.add(new Paragraph("Company Details").setFont(boldFont).setFontSize(10).setMargin(0));

				companyTable.addCell(companyCell);

				document.add(companyTable);
				document.add(new Paragraph().setMargin(0).setMarginBottom(5));
			}

			Table table = new Table(UnitValue.createPercentArray(new float[] { 8f, 15f, 22f, 20f, 10f, 25f }))
					.setWidth(UnitValue.createPercentValue(100)).setHorizontalAlignment(HorizontalAlignment.CENTER);

			addItemHistoryHeaderCell(table, "No.", boldFont);
			addItemHistoryHeaderCell(table, "Event Date", boldFont);
			addItemHistoryHeaderCell(table, "Party Name", boldFont);
			addItemHistoryHeaderCell(table, "Event Name", boldFont);
			addItemHistoryHeaderCell(table, "Pax", boldFont);
			addItemHistoryHeaderCell(table, "Venue", boldFont);

			int srNo = 1;

			for (Object[] row : reportData) {
				Long eventId = row[0] != null ? ((Number) row[0]).longValue() : null;
				String eventStartTime = row[1] != null ? row[1].toString() : "";
				String partyName = row[2] != null ? row[2].toString() : "";
				String eventName = row[3] != null ? row[3].toString() : "";
				BigDecimal pax = row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO;
				String venue = row[5] != null ? row[5].toString() : "";
				String formattedEventDate = eventStartTime;

				try {
					System.out.println("start date : " + eventStartTime);

					LocalDateTime dateTime = LocalDateTime.parse(eventStartTime,
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));

					formattedEventDate = dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

				} catch (Exception ex) {
					try {
						LocalDateTime dateTime = LocalDateTime.parse(eventStartTime.substring(0, 16),
								DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

						formattedEventDate = dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

					} catch (Exception e) {
						// Keep original value
					}
				}
				addItemHistoryBodyCell(table, String.valueOf(srNo), regularFont);
				addItemHistoryBodyCell(table, formattedEventDate != null ? formattedEventDate : "", regularFont);
				addItemHistoryBodyCell(table, partyName, regularFont);
				addItemHistoryBodyCell(table, eventName, regularFont);
				addItemHistoryBodyCell(table, pax.stripTrailingZeros().toPlainString(), regularFont);
				addItemHistoryBodyCell(table, venue, regularFont);

				srNo++;
			}

			document.add(table);

			int numberOfPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= numberOfPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				float pageWidth = page.getPageSize().getWidth();

				Paragraph footer = new Paragraph("Page " + i + " of " + numberOfPages).setFont(regularFont)
						.setFontSize(8).setTextAlignment(TextAlignment.CENTER);

				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned(footer, pageWidth / 2, 18, TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			return environment.getProperty("ws_image_path") + "/api/download/pdf/itemorderhistory/" + fileName;
		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException("Error while generating Item Order History Report: " + e.getMessage(), e);
		}
	}

	private void addItemHistoryBodyCell(Table table, String text, PdfFont font) {

		Cell cell = new Cell().setPadding(5).setBorder(new SolidBorder(0.5f));

		Paragraph paragraph = new Paragraph(text != null ? text : "").setFont(font).setFontSize(10)
				.setTextAlignment(TextAlignment.CENTER).setMargin(0);

		cell.add(paragraph);

		table.addCell(cell);
	}

	private void addItemHistoryHeaderCell(Table table, String text, PdfFont font) {
		Cell cell = new Cell().setPadding(5).setBackgroundColor(ColorConstants.LIGHT_GRAY)
				.setBorder(new SolidBorder(0.8f));

		Paragraph paragraph = new Paragraph(text).setFont(font).setFontSize(11).setTextAlignment(TextAlignment.CENTER)
				.setMargin(0);

		cell.add(paragraph);

		table.addHeaderCell(cell);
	}
}