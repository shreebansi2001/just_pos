package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crmportal.controller.EventDishCostingController;
import com.crmportal.entity.CaptainReceipeMasterEntity;
import com.crmportal.entity.MenuItemCaptainReceipeEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.MenuItemRawMaterialRateDishCostingEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.MenuItemRawMaterialMapper;
import com.crmportal.mapper.RawMaterialMasterMapper;
import com.crmportal.mapper.UnitMasterMapper;
import com.crmportal.repository.MenuItemCaptainReceipeRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRateDishCostingRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.CostingReportAgencyResponseDto;
import com.crmportal.request.dto.UpdateItemRawMaterialWeightRequestDto;
import com.crmportal.request.dto.UpdateMisMatchedUnitsRequestDto;
import com.crmportal.response.dto.CostingReportResponseDto;
import com.crmportal.response.dto.ItemRawMaterialsResponseDto;
import com.crmportal.response.dto.MenuItemCaptainReceipeResponseDto;
import com.crmportal.response.dto.MenuItemRawMaterialsResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.response.dto.WrongItemRawMaterialResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.LaborReportService;
import com.crmportal.service.MenuItemRawMaterialService;
import com.crmportal.service.UnitMasterService;

@Service
public class MenuItemRawMaterialServiceImpl implements MenuItemRawMaterialService {

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	RawMaterialMasterMapper rawMaterialMasterMapper;

	@Autowired
	UnitMasterMapper unitMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	UnitMasterService unitMasterService;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	MenuItemRawMaterialRateDishCostingRepository menuItemRawMaterialRateDishCostingRepository;

	@Autowired
	MenuItemCaptainReceipeRepository menuItemCaptainReceipeRepository;

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);

	@Override
	public List<MenuItemRawMaterialsResponseDto> getMenuItemRawMaterialByMenuId(Long menuItemId, Long userId,
			Boolean isSync) {
		MenuItemMasterEntity item = menuItemMasterRepository.findByIdAndIsDeleteFalse(menuItemId)
				.orElseThrow(() -> new RuntimeException("Menu Item not found with id: " + menuItemId));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		List<MenuItemRawMaterialEntity> entities = menuItemRawMaterialRepository
				.findAllByMenuItemAndUserAndIsDeleteFalse(item, user);
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<MenuItemRawMaterialsResponseDto> dtos = new ArrayList<>();
			for (MenuItemRawMaterialEntity menuItemRawMaterialEntity : entities) {
				MenuItemRawMaterialsResponseDto dto = setMenuItemRawMaterialResp(menuItemRawMaterialEntity, isSync);
				dtos.add(dto);
			}
			return dtos;
		}
	}

	private MenuItemRawMaterialsResponseDto setMenuItemRawMaterialResp(
			MenuItemRawMaterialEntity menuItemRawMaterialEntity, Boolean isSync) {

		MenuItemRawMaterialsResponseDto dto = new MenuItemRawMaterialsResponseDto();
		dto.setId(menuItemRawMaterialEntity.getId());
		if (isSync) {

			BigDecimal weight = menuItemRawMaterialEntity.getWeight() == null ? BigDecimal.ZERO
					: menuItemRawMaterialEntity.getWeight();

			BigDecimal finalWeight = adjustBaseWeight(weight, menuItemRawMaterialEntity.getUnit(),
					menuItemRawMaterialEntity.getRawMaterial().getUnit() == null ? menuItemRawMaterialEntity.getUnit()
							: menuItemRawMaterialEntity.getRawMaterial().getUnit());

			BigDecimal supRate = safeMultiply(menuItemRawMaterialEntity.getRawMaterial().getSupplierRate(), finalWeight)
					.divide(BigDecimal.ONE, 4, RoundingMode.HALF_UP);

			dto.setRate(supRate);
		} else {
			dto.setRate(menuItemRawMaterialEntity.getRate());
		}
		dto.setRawMaterial(menuItemRawMaterialEntity.getRawMaterial() == null ? null
				: rawMaterialMasterMapper.entityToResponse(menuItemRawMaterialEntity.getRawMaterial()));
		dto.setUnit(menuItemRawMaterialEntity.getUnit() == null ? null
				: unitMasterMapper.entityToResponse(menuItemRawMaterialEntity.getUnit()));
		dto.setWeight(menuItemRawMaterialEntity.getWeight());
		dto.setVenue(menuItemRawMaterialEntity.getVenue());
		dto.setIsVisible(menuItemRawMaterialEntity.getIsVisible());

		return dto;
	}

	@Override
	public List<MenuItemCaptainReceipeResponseDto> getMenuItemCaptainReceipeByMenuId(Long menuItemId, Long userId,
			Boolean isSync) {
		MenuItemMasterEntity item = menuItemMasterRepository.findByIdAndIsDeleteFalse(menuItemId)
				.orElseThrow(() -> new RuntimeException("Menu Item not found with id: " + menuItemId));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		List<MenuItemCaptainReceipeEntity> entities = menuItemCaptainReceipeRepository
				.findAllByMenuItemAndUserAndIsDeleteFalse(item, user);
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<MenuItemCaptainReceipeResponseDto> dtos = new ArrayList<>();

			for (MenuItemCaptainReceipeEntity menuItemRawMaterialEntity : entities) {
				MenuItemCaptainReceipeResponseDto dto = setMenuItemCaptainReceipeResp(menuItemRawMaterialEntity,
						isSync);
				dtos.add(dto);
			}

			return dtos;
		}
	}

	private MenuItemCaptainReceipeResponseDto setMenuItemCaptainReceipeResp(
			MenuItemCaptainReceipeEntity menuItemCaptainReceipeEntity, Boolean isSync) {

		MenuItemCaptainReceipeResponseDto dto = new MenuItemCaptainReceipeResponseDto();
		dto.setId(menuItemCaptainReceipeEntity.getId());
		if (isSync) {

			BigDecimal weight = menuItemCaptainReceipeEntity.getWeight() == null ? BigDecimal.ZERO
					: menuItemCaptainReceipeEntity.getWeight();

			UnitMasterEntity unit = unitMasterRepository
					.findByIdAndIsDeleteFalse(menuItemCaptainReceipeEntity.getCaptainReceipe().getUnitId())
					.orElse(null);

			BigDecimal finalWeight = adjustBaseWeight(weight, menuItemCaptainReceipeEntity.getUnit(),
					unit == null ? menuItemCaptainReceipeEntity.getUnit() : unit);

			BigDecimal supRate = safeMultiply(menuItemCaptainReceipeEntity.getCaptainReceipe().getRate(), finalWeight)
					.divide(menuItemCaptainReceipeEntity.getCaptainReceipe().getWeight(), 4, RoundingMode.HALF_UP);

			dto.setRate(supRate);
		} else {
			dto.setRate(menuItemCaptainReceipeEntity.getRate());
		}

		dto.setUnitId(
				menuItemCaptainReceipeEntity.getUnit() == null ? null : menuItemCaptainReceipeEntity.getUnit().getId());
		dto.setWeight(menuItemCaptainReceipeEntity.getWeight());
		dto.setVenue(menuItemCaptainReceipeEntity.getVenue());
		return dto;
	}

	private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
		return safe(a).multiply(safe(b));
	}

	private BigDecimal safe(BigDecimal v) {
		return v == null ? ZERO : v;
	}

	public static BigDecimal adjustBaseWeight(BigDecimal baseWeight, UnitMasterEntity unit,
			UnitMasterEntity rawMaterialUnit) {
		System.out.println("Before:-" + baseWeight);
		if (baseWeight == null || unit == null || rawMaterialUnit == null) {
			return baseWeight;
		}
		if (!unit.getId().equals(rawMaterialUnit.getId())) {
			return unit.getIsParentUnit() ? baseWeight.multiply(THOUSAND)
					: baseWeight.divide(THOUSAND, 3, RoundingMode.HALF_UP);

		}

		return baseWeight;
	}

	public List<MenuQuantityReponseDto> getAllFunctionDetails(Long eventId, Long eventFunctionId, int lang) {

		List<MenuQuantityReponseDto> reponseDtos = new ArrayList<>();

		List<Object[]> eventFunctions = menuItemRawMaterialRepository.getEventFunctions(eventId, eventFunctionId, lang);
		for (Object[] row : eventFunctions) {
			int index = 0;
			MenuQuantityReponseDto dto = new MenuQuantityReponseDto();
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionTime(commonService.getString(row[index++]));
			dto.setPerson(commonService.getInteger(row[index++]));
			dto.setFnVenue(commonService.getString(row[index++]));
			reponseDtos.add(dto);
		}

		return reponseDtos;
	}

	public List<MenuQuantityReponseDto> getMultiFunctionDetails(Long eventId, List<Long> eventFunctionIds, int lang) {

		List<MenuQuantityReponseDto> reponseDtos = new ArrayList<>();

		List<Object[]> eventFunctions = menuItemRawMaterialRepository.getEventMultiFunctions(eventId, eventFunctionIds,
				lang);
		for (Object[] row : eventFunctions) {
			int index = 0;
			MenuQuantityReponseDto dto = new MenuQuantityReponseDto();
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionTime(commonService.getString(row[index++]));
			dto.setPerson(commonService.getInteger(row[index++]));
			reponseDtos.add(dto);
		}

		return reponseDtos;
	}

	public List<MenuQuantityReponseDto> getFunctionItemDetails(Long eventId, Long functionId, int lang) {
		List<MenuQuantityReponseDto> reponseDtos = new ArrayList<>();

		List<Object[]> eventFunctions = menuItemRawMaterialRepository.getEventFunctionItems(eventId, functionId, lang);

		for (Object[] row : eventFunctions) {
			int index = 0;
			MenuQuantityReponseDto dto = new MenuQuantityReponseDto();
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setInside(commonService.getBoolean(row[index++]));
			dto.setOutside(commonService.getBoolean(row[index++]));
			dto.setChefLabour(commonService.getBoolean(row[index++]));
			dto.setInstructions(commonService.getString(row[9]));
			reponseDtos.add(dto);
		}

		return reponseDtos;
	}

	public List<MenuQuantityReponseDto> getFunctionItemRawMaterialDetails(Long eventId, Long functionId, Long itemId,
			int lang) {

		List<MenuQuantityReponseDto> reponseDtos = new ArrayList<>();

		List<Object[]> eventFunctions = menuItemRawMaterialRepository.getEventFunctionItemsRawMaterials(eventId,
				functionId, itemId, lang);

		for (Object[] row : eventFunctions) {
			int index = 0;
			MenuQuantityReponseDto dto = new MenuQuantityReponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setRawMaterialId(commonService.getLong(row[index++]));
			dto.setRawMaterialName(commonService.getString(row[index++]));
			dto.setQty(commonService.getDouble(row[index++]));
			dto.setUnitId(commonService.getLong(row[index++]));
			dto.setUnitSymbol(commonService.getString(row[index++]));
			reponseDtos.add(dto);
		}

		return reponseDtos;
	}

	public MenuQuantityReponseDto getEventData(Long eventId, int lang) {

		Object result = menuItemRawMaterialRepository.getEventData(eventId, lang);
		System.out.println(result);
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
		dto.setEventStartTime(commonService.getString(cmpData[index++]));
		dto.setRemarks(commonService.getString(cmpData[index++]));

		return dto;
	}

	public MenuQuantityReponseDto getCmpData(Long userId) {

		Object result = menuItemRawMaterialRepository.getCmpData(userId);
		System.out.println(result);
		if (result == null) {
			return null;
		}

		Object[] cmpData = (Object[]) result;
		int index = 0;
		MenuQuantityReponseDto dto = new MenuQuantityReponseDto();

		dto.setCompanyName(commonService.getString(cmpData[index++]));
		dto.setCountryCode(commonService.getString(cmpData[index++]));
		dto.setOfficeNo(commonService.getString(cmpData[index++]));
		dto.setCompanyEmail(commonService.getString(cmpData[index++]));
		dto.setLogo(commonService.getString(cmpData[index++]));
		dto.setCompanyAddress(commonService.getString(cmpData[index++]));

		return dto;
	}

	public List<MenuQuantityReponseDto> getRawMaterialCategory(Long eventId, Long functionId, int lang) {

		List<MenuQuantityReponseDto> reponseDtos = new ArrayList<>();

		List<Object[]> data = menuItemRawMaterialRepository.getEventFunctionRawMaterialCat(eventId, functionId, lang);

		for (Object[] row : data) {
			int index = 0;
			MenuQuantityReponseDto dto = new MenuQuantityReponseDto();
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialCatName(commonService.getString(row[index++]));
			reponseDtos.add(dto);
		}

		return reponseDtos;

	}

	public List<MenuQuantityReponseDto> getRawMaterial(Long eventId, Long functionId, int lang, Long catId) {
		List<MenuQuantityReponseDto> reponseDtos = new ArrayList<>();

		List<Object[]> data = menuItemRawMaterialRepository.getEventFunctionRawMaterial(eventId, functionId, lang,
				catId);

		for (Object[] row : data) {
			int index = 0;
			MenuQuantityReponseDto dto = new MenuQuantityReponseDto();
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialId(commonService.getLong(row[index++]));
			dto.setRawMaterialCatName(commonService.getString(row[index++]));
			dto.setRawMaterialName(commonService.getString(row[index++]));
			reponseDtos.add(dto);
		}

		return reponseDtos;
	}

	public List<MenuQuantityReponseDto> getRawMaterialWiseItem(Long eventId, Long functionId, int lang, Long catId,
			Long rawMaterialId) {

		List<MenuQuantityReponseDto> reponseDtos = new ArrayList<>();

		List<Object[]> data = menuItemRawMaterialRepository.getEventFunctionRawMaterialWiseItem(eventId, functionId,
				lang, catId, rawMaterialId);

		for (Object[] row : data) {
			int index = 0;
			MenuQuantityReponseDto dto = new MenuQuantityReponseDto();
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialId(commonService.getLong(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setQty(commonService.getDouble(row[index++]));
			dto.setUnitId(commonService.getLong(row[index++]));
			dto.setUnitSymbol(commonService.getString(row[index++]));
			dto.setRate(commonService.getDouble(row[index++]));
			reponseDtos.add(dto);
		}

		return reponseDtos;

	}

	public List<CostingReportResponseDto> getAllFunctionDetailsForCosting(Long eventId, int lang) {

		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getEventFunction(eventId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionPerson(commonService.getBigDecimal(row[index++]));
			dto.setFunctionStartTime(commonService.getString(row[index++]));
			dto.setFunctionEndTime(commonService.getString(row[index++]));
			dto.setPartyName(commonService.getString(row[index++]));
			dto.setPartyMobileNo(commonService.getString(row[index++]));
			dto.setVenueName(commonService.getString(row[index++]));
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getFunctionCategoryDetailsForCosting(Long eventId, Long eventFunctionId,
			int lang, Long userId) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getEventFunctionCategory(eventId, eventFunctionId, lang,
				userId);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setMenuCategoryId(commonService.getLong(row[index++]));
			dto.setMenuCategoryName(commonService.getString(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getFunctionCategoryItemDetailsForCosting(Long eventId, Long eventFunctionId,
			Long catId, int lang) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getEventFunctionCategoryItem(eventId, eventFunctionId,
				catId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setMenuItemName(commonService.getString(row[index++]));
			dto.setTotalprice(commonService.getBigDecimal(row[index++]));
			dto.setPerplateprice(commonService.getBigDecimal(row[index++]));
			dto.setTotalRawMaterialItems(commonService.getInteger(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getFunctionCategoryItemDetailsForCosting2(Long eventId, Long eventFunctionId,
			Long catId, int lang) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getEventFunctionCategoryItem2(eventId, eventFunctionId,
				catId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setMenuItemName(commonService.getString(row[index++]));
			dto.setTotalprice(commonService.getBigDecimal(row[index++]));
			dto.setPerplateprice(commonService.getBigDecimal(row[index++]));
			dto.setTotalRawMaterialItems(commonService.getInteger(row[index++]));
			dto.setMenuCategoryId(commonService.getLong(row[index++]));
			dto.setItemPerson(commonService.getBigDecimal(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getFunctionCategoryItemDetailsForCosting3(Long eventId, Long eventFunctionId,
			Long catId, int lang) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getEventFunctionCategoryItem3(eventId, eventFunctionId,
				catId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setMenuItemName(commonService.getString(row[index++]));
			dto.setTotalprice(commonService.getBigDecimal(row[index++]));
			dto.setPerplateprice(commonService.getBigDecimal(row[index++]));
			dto.setTotalRawMaterialItems(commonService.getInteger(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getRawMaterialCategoryDetailsForCosting(Long eventId, Long eventFunctionId,
			Long catId, Long itemId, int lang) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getRawMaterialCategory(eventId, eventFunctionId, catId,
				itemId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialCatName(commonService.getString(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getRawMaterialCategoryDetailsForCosting2(Long eventId, Long eventFunctionId,
			Long itemId, List<Long> rawMaterialIds, int lang) {

		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getRawMaterialCategory(eventId, eventFunctionId, itemId,
				rawMaterialIds, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialCatName(commonService.getString(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getRawMaterialCategoryDetailsForCosting3(Long eventId, Long eventFunctionId,
			Long menuItemId, int lang) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getRawMaterialCategory(eventId, eventFunctionId, menuItemId,
				lang);
//		System.out.println(eventId + " : " + eventFunctionId + " : " + itemName + " --- " + data.size());

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialCatName(commonService.getString(row[index++]));
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getRawMaterialDetailsForCosting(Long eventId, Long eventFunctionId,
			Long catId, Long itemId, Long rawMaterialCatId, int lang) {

		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getRawMaterialItem(eventId, eventFunctionId, catId, itemId,
				rawMaterialCatId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setRawMaterialItemId(commonService.getLong(row[index++]));
			dto.setRawMaterialItemName(commonService.getString(row[index++]));
			dto.setQuantity(commonService.getBigDecimal(row[index++]));
			dto.setUnitName(commonService.getString(row[index++]));
			dto.setRate(commonService.getBigDecimal(row[index++]));
			dto.setTotalprice(commonService.getBigDecimal(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getRawMaterialDetailsForCosting2(Long eventId, Long eventFunctionId,
			Long itemId, Long rawMaterialCatId, int lang) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getRawMaterialItem2(eventId, eventFunctionId, itemId,
				rawMaterialCatId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setRawMaterialItemId(commonService.getLong(row[index++]));
			dto.setRawMaterialItemName(commonService.getString(row[index++]));
			dto.setQuantity(commonService.getBigDecimal(row[index++]));
			dto.setUnitName(commonService.getString(row[index++]));
			dto.setRate(commonService.getBigDecimal(row[index++]));
			dto.setTotalprice(commonService.getBigDecimal(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportResponseDto> getRawMaterialDetailsForCosting3(Long eventId, Long eventFunctionId,
			Long menuItemId, Long rawMaterialCatId, int lang) {
		List<CostingReportResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getRawMaterialItem3(eventId, eventFunctionId, menuItemId,
				rawMaterialCatId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportResponseDto dto = new CostingReportResponseDto();
			dto.setRawMaterialItemId(commonService.getLong(row[index++]));
			dto.setRawMaterialItemName(commonService.getString(row[index++]));
			dto.setQuantity(commonService.getBigDecimal(row[index++]));
			dto.setTotalprice(commonService.getBigDecimal(row[index++]));
			dto.setUnitName(commonService.getString(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportAgencyResponseDto> getAgencyForCostingReport(Long eventId, List<Long> itemIds, int lang,
			boolean chefLabour, boolean outsource) {

		List<CostingReportAgencyResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getAgencyData(eventId, itemIds, lang, chefLabour,
				outsource);

		for (Object[] row : data) {
			int index = 0;
			CostingReportAgencyResponseDto dto = new CostingReportAgencyResponseDto();
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setPartyId(commonService.getLong(row[index++]));
			dto.setAgencyName(commonService.getString(row[index++]));
			dto.setChefLabourPrice(commonService.getString(row[index++]));
			dto.setOutsourcePrice(commonService.getBigDecimal(row[index++]));
			dto.setIsChefLabour(commonService.getBoolean(row[index++]));
			dto.setIsOutsource(commonService.getBoolean(row[index++]));
			dto.setQuantity(commonService.getString(row[index++]));
			dto.setUnitName(commonService.getString(row[index++]));
			dto.setTotalChefLabourPrice(commonService.getBigDecimal(row[index++]));
			dto.setTotalOutsourcePrice(commonService.getBigDecimal(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<CostingReportAgencyResponseDto> getAgencyForCostingReport2(Long eventId, int lang, boolean chefLabour,
			boolean outsource) {

		List<CostingReportAgencyResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getAgencyData2(eventId, lang, chefLabour, outsource);

		for (Object[] row : data) {
			int index = 0;
			CostingReportAgencyResponseDto dto = new CostingReportAgencyResponseDto();
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setPartyId(commonService.getLong(row[index++]));
			dto.setAgencyName(commonService.getString(row[index++]));
			dto.setChefLabourPrice(commonService.getString(row[index++]));
			dto.setOutsourcePrice(commonService.getBigDecimal(row[index++]));
			dto.setIsChefLabour(commonService.getBoolean(row[index++]));
			dto.setIsOutsource(commonService.getBoolean(row[index++]));
			dto.setQuantity(commonService.getString(row[index++]));
			dto.setUnitName(commonService.getString(row[index++]));
			dto.setTotalChefLabourPrice(commonService.getBigDecimal(row[index++]));
			dto.setTotalOutsourcePrice(commonService.getBigDecimal(row[index++]));
			dto.setTransRate(commonService.getBigDecimal(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<Long> getAllRawMaterialIds(Long eventId, Long eventFunctionId, Long itemId) {

		List<Long> list = menuItemRawMaterialRepository.getRawMaterialIds(eventId, eventFunctionId, itemId);

		return list;
	}

	public List<CostingReportAgencyResponseDto> getLabourForCostingReport(Long eventId, int lang) {

		List<CostingReportAgencyResponseDto> responseDtos = new ArrayList<>();
		List<Object[]> data = menuItemRawMaterialRepository.getLabourData(eventId, lang);

		for (Object[] row : data) {
			int index = 0;
			CostingReportAgencyResponseDto dto = new CostingReportAgencyResponseDto();
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setAgencyName(commonService.getString(row[index++]));
			dto.setQuantity(commonService.getString(row[index++]));
			dto.setLabourPrice(commonService.getBigDecimal(row[index++]));
			dto.setTotalLabourPrice(commonService.getBigDecimal(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public Boolean checkEventId(Long eventId) {
		Long id = menuItemRawMaterialRepository.checkEventId(eventId);
		return id != null && id > 0;
	}

	public Boolean checkEventId2(Long eventId) {
		Long id = menuItemRawMaterialRepository.checkEventId2(eventId);
		return id != null && id > 0;
	}

	public Boolean checkEventIdAndEventFunctionId(Long eventId, Long eventFunctionId) {
		Long id = menuItemRawMaterialRepository.checkEventIdAndFunctionId(eventId, eventFunctionId);
		return id != null && id > 0;
	}

	public Boolean checkEventIdAndEventFunctionId2(Long eventId, Long eventFunctionId) {
		Long id = menuItemRawMaterialRepository.checkEventIdAndFunctionId2(eventId, eventFunctionId);
		return id != null && id > 0;
	}

	@Override
	public List<WrongItemRawMaterialResponseDto> getMismatchedUnitsByUserId(Long userId) {

		List<Object[]> data = menuItemRawMaterialRepository.findMismatchedUnitsByUserId(userId);

		return data.stream().map(row -> {

			Long itemRawMaterialId = toLong(row[0]);
			Long itemRawMaterialUnitId = toLong(row[1]);
			BigDecimal weight = toBigDecimal(row[3]);
			Long rawMaterialId = toLong(row[4]);
			Long rawMaterialUnitId = toLong(row[6]);
			Long itemId = toLong(row[8]);
			BigDecimal rate = toBigDecimal(row[10]);

			WrongItemRawMaterialResponseDto dto = new WrongItemRawMaterialResponseDto();
			dto.setItemRawMaterialId(itemRawMaterialId);
			dto.setItemRawMaterialUnitId(itemRawMaterialUnitId);
			dto.setItemRawMaterialUnitName(toStr(row[2]));

			dto.setWeight(weight);

			dto.setRawMaterialId(rawMaterialId);
			dto.setRawMaterialName(toStr(row[5]));
			dto.setRawMaterialUnitId(rawMaterialUnitId);
			dto.setRawMaterialUnitName(toStr(row[7]));

			dto.setItemId(itemId);
			dto.setItemName(toStr(row[9]));

			dto.setFinalWeight(weight);
			dto.setUnit(unitMasterService.getParentUnitsWithChildren(rawMaterialUnitId));
			dto.setSupplierRate(rate);
			return dto;

		}).collect(Collectors.toList());
	}

	private Long toLong(Object obj) {
		return obj == null ? null : Long.valueOf(obj.toString());
	}

	private BigDecimal toBigDecimal(Object obj) {
		return obj == null ? null : new BigDecimal(obj.toString());
	}

	private String toStr(Object obj) {
		return obj == null ? null : obj.toString();
	}

	@Override
	@Transactional
	public Boolean updateMisMatchedUnits(List<UpdateMisMatchedUnitsRequestDto> request) {

		if (request == null || request.isEmpty()) {
			return Boolean.FALSE;
		}

		Set<Long> entityIds = request.stream().map(UpdateMisMatchedUnitsRequestDto::getItemRawMaterialId)
				.collect(Collectors.toSet());

		Set<Long> unitIds = request.stream()
				.flatMap(dto -> Stream.of(dto.getItemRawMaterialUnitId(), dto.getRawMaterialUnitId()))
				.filter(Objects::nonNull).collect(Collectors.toSet());

		Map<Long, MenuItemRawMaterialEntity> entityMap = fetchValidEntities(entityIds);
		Map<Long, UnitMasterEntity> unitMap = fetchValidUnits(unitIds);

		List<MenuItemRawMaterialEntity> updatedEntities = buildMisMatchedUpdatedEntities(request, entityMap, unitMap);

		menuItemRawMaterialRepository.saveAll(updatedEntities);

		updateDishCosting(updatedEntities);

		return Boolean.TRUE;
	}

	private List<MenuItemRawMaterialEntity> buildMisMatchedUpdatedEntities(
			List<UpdateMisMatchedUnitsRequestDto> request, Map<Long, MenuItemRawMaterialEntity> entityMap,
			Map<Long, UnitMasterEntity> unitMap) {

		final BigDecimal ZERO = BigDecimal.ZERO;

		return request.stream().map(dto -> {

			MenuItemRawMaterialEntity entity = Optional.ofNullable(entityMap.get(dto.getItemRawMaterialId()))
					.orElseThrow(() -> new RuntimeException("Entity not found: " + dto.getItemRawMaterialId()));

			BigDecimal weight = Optional.ofNullable(dto.getFinalWeight()).orElse(ZERO);
			BigDecimal supplierRate = Optional.ofNullable(dto.getSupplierRate()).orElse(ZERO);

			UnitMasterEntity finalUnit = getUnitOrThrow(dto.getItemRawMaterialUnitId(), unitMap,
					"itemRawMaterialUnitId");

			UnitMasterEntity rawUnit = unitMap.get(dto.getRawMaterialUnitId());

			BigDecimal finalWeight = adjustBaseWeight(weight, finalUnit, rawUnit != null ? rawUnit : finalUnit);

			BigDecimal finalRate = safeMultiply(supplierRate, finalWeight).setScale(4, RoundingMode.HALF_UP);

			entity.setWeight(weight);
			entity.setRate(finalRate);
			entity.setUnit(finalUnit);

			return entity;

		}).collect(Collectors.toList());
	}

	private UnitMasterEntity getUnitOrThrow(Long unitId, Map<Long, UnitMasterEntity> unitMap, String fieldName) {
		return Optional.ofNullable(unitMap.get(unitId))
				.orElseThrow(() -> new RuntimeException("Unit not found for " + fieldName + ": " + unitId));
	}

	@Override
	@Transactional
	public Boolean syncAllItemRawMaterialRate(Long userId) {
		menuItemRawMaterialRepository.updateLatestMenuItemRawMatRateByUser(userId);
		menuItemRawMaterialRepository.upsertDishCostingRate(userId);
		return true;
	}

	@Override
	@Transactional
	public Boolean syncAllCaptainReceipeRate(Long userId) {
		menuItemRawMaterialRepository.updateLatestMenuItemCaptainReceipeRateByUser(userId);
		menuItemRawMaterialRepository.upsertDishCostingRate(userId);
		return true;
	}

	@Override
	public List<ItemRawMaterialsResponseDto> getItemRawMaterialByRawMaterialId(Long rawMaterialId, Long userId) {

		List<Object[]> data = menuItemRawMaterialRepository.getAllByRawMaterial(rawMaterialId, userId);

		List<ItemRawMaterialsResponseDto> responseList = new ArrayList<>();

		Map<Long, UnitHierarchyDto> unitHierarchyMap = new HashMap<>();

		for (Object[] obj : data) {

			ItemRawMaterialsResponseDto dto = new ItemRawMaterialsResponseDto();

			dto.setId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
			dto.setWeight(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
			dto.setItemRawMatUnitId(obj[2] != null ? ((Number) obj[2]).longValue() : null);
			dto.setItemRawMatUnitName(obj[3] != null ? (String) obj[3] : "");
			dto.setItemName(obj[5] != null ? (String) obj[5] : "");

			dto.setRawMatUnitId(obj[8] != null ? ((Number) obj[8]).longValue() : null);
			dto.setRawMatUnitName(obj[9] != null ? (String) obj[9] : "");
			dto.setSupplierRate(obj[10] != null ? (BigDecimal) obj[10] : BigDecimal.ZERO);
			dto.setItemId(obj[11] != null ? ((Number) obj[11]).longValue() : null);
			Long unitId = dto.getItemRawMatUnitId();

			if (unitId == null) {
				dto.setUnitHierarchy(null);
			} else {
				if (!unitHierarchyMap.containsKey(unitId)) {
					UnitHierarchyDto hierarchy = unitMasterService.getParentUnitsWithChildren(unitId);
					unitHierarchyMap.put(unitId, hierarchy);
				}
				dto.setUnitHierarchy(unitHierarchyMap.get(unitId));
			}
			dto.setIsVisible(obj[12] != null ? (Boolean) obj[12] : false);
			responseList.add(dto);
		}

		return responseList;
	}

	@Override
	public Boolean updateItemRawMaterialWeight(List<UpdateItemRawMaterialWeightRequestDto> request) {

		if (request == null || request.isEmpty()) {
			return Boolean.FALSE;
		}

		Set<Long> entityIds = extractEntityIds(request);
		Set<Long> unitIds = extractUnitIds(request);

		Map<Long, MenuItemRawMaterialEntity> entityMap = fetchValidEntities(entityIds);
		Map<Long, UnitMasterEntity> unitMap = fetchValidUnits(unitIds);

		List<MenuItemRawMaterialEntity> updatedEntities = buildUpdatedEntities(request, entityMap, unitMap);

		menuItemRawMaterialRepository.saveAll(updatedEntities);

		updateDishCosting(updatedEntities);

		return Boolean.TRUE;
	}

	private Set<Long> extractEntityIds(List<UpdateItemRawMaterialWeightRequestDto> request) {
		return request.stream().map(UpdateItemRawMaterialWeightRequestDto::getId).collect(Collectors.toSet());
	}

	private Set<Long> extractUnitIds(List<UpdateItemRawMaterialWeightRequestDto> request) {
		return request.stream().flatMap(req -> Stream.of(req.getFinalUnitId(), req.getRawMaterialUnitId()))
				.filter(Objects::nonNull).collect(Collectors.toSet());
	}

	private Map<Long, MenuItemRawMaterialEntity> fetchValidEntities(Set<Long> entityIds) {
		return menuItemRawMaterialRepository.findAllById(entityIds).stream()
				.filter(e -> Boolean.FALSE.equals(e.getIsDelete()))
				.collect(Collectors.toMap(MenuItemRawMaterialEntity::getId, Function.identity()));
	}

	private Map<Long, UnitMasterEntity> fetchValidUnits(Set<Long> unitIds) {
		return unitMasterRepository.findAllById(unitIds).stream()
				.filter(u -> Boolean.FALSE.equals(u.getIsDelete()) && Boolean.TRUE.equals(u.getIsActive()))
				.collect(Collectors.toMap(UnitMasterEntity::getId, Function.identity()));
	}

	private List<MenuItemRawMaterialEntity> buildUpdatedEntities(List<UpdateItemRawMaterialWeightRequestDto> request,
			Map<Long, MenuItemRawMaterialEntity> entityMap, Map<Long, UnitMasterEntity> unitMap) {

		final BigDecimal ZERO = BigDecimal.ZERO;

		return request.stream().map(req -> {

			MenuItemRawMaterialEntity entity = Optional.ofNullable(entityMap.get(req.getId()))
					.orElseThrow(() -> new RuntimeException("Menu Item RawMaterial Not Found With Id:-" + req.getId()));

			RawMaterialMasterEntity rawEntity = entity.getRawMaterial();

			if (req.getNewRawMaterialId() != null) {
				rawEntity = rawMaterialMasterRepository.findByIdAndIsDeleteFalse(req.getNewRawMaterialId());
			}

			BigDecimal weight = Optional.ofNullable(req.getFinalWeight()).orElse(ZERO);
			BigDecimal supplierRate = Optional.ofNullable(req.getSupplierRate()).orElse(ZERO);

			UnitMasterEntity finalUnit = getUnitOrThrow(req.getFinalUnitId(), unitMap, "finalUnitId");
			UnitMasterEntity rawUnit = unitMap.get(req.getRawMaterialUnitId());

			BigDecimal finalWeight = adjustBaseWeight(weight, finalUnit, rawUnit != null ? rawUnit : finalUnit);
			BigDecimal finalRate = safeMultiply(supplierRate, finalWeight).setScale(4, RoundingMode.HALF_UP);

			entity.setRawMaterial(rawEntity);
			entity.setWeight(weight);
			entity.setRate(finalRate);
			entity.setUnit(finalUnit);

			return entity;

		}).collect(Collectors.toList());
	}

	private void updateDishCosting(List<MenuItemRawMaterialEntity> entities) {

		final BigDecimal ZERO = BigDecimal.ZERO;
		final BigDecimal ONE_HUNDRED = new BigDecimal("100");

		Map<Long, List<MenuItemRawMaterialEntity>> groupedByItem = entities.stream()
				.collect(Collectors.groupingBy(e -> e.getMenuItem().getId()));

		Set<Long> itemIds = groupedByItem.keySet();

		Map<Long, MenuItemRawMaterialRateDishCostingEntity> costingMap = menuItemRawMaterialRateDishCostingRepository
				.findByMenuItemIdInAndIsDeleteFalse(itemIds).stream()
				.collect(Collectors.toMap(e -> e.getMenuItem().getId(), Function.identity()));

		List<MenuItemRawMaterialRateDishCostingEntity> costingEntities = groupedByItem.entrySet().stream()
				.map(entry -> buildCostingEntity(entry, costingMap, ZERO, ONE_HUNDRED)).collect(Collectors.toList());

		menuItemRawMaterialRateDishCostingRepository.saveAll(costingEntities);
	}

	private MenuItemRawMaterialRateDishCostingEntity buildCostingEntity(
			Map.Entry<Long, List<MenuItemRawMaterialEntity>> entry,
			Map<Long, MenuItemRawMaterialRateDishCostingEntity> costingMap, BigDecimal ZERO, BigDecimal ONE_HUNDRED) {

		Long itemId = entry.getKey();
		List<MenuItemRawMaterialEntity> itemEntities = entry.getValue();

		BigDecimal totalRate = itemEntities.stream().map(MenuItemRawMaterialEntity::getRate).filter(Objects::nonNull)
				.reduce(ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

		BigDecimal dishCosting = totalRate.divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

		MenuItemRawMaterialRateDishCostingEntity costing = costingMap.getOrDefault(itemId,
				new MenuItemRawMaterialRateDishCostingEntity());

		MenuItemMasterEntity menuItem = new MenuItemMasterEntity();
		menuItem.setId(itemId);

		costing.setMenuItem(menuItem);
		costing.setTotalRate(totalRate);
		costing.setDishCosting(dishCosting);
		costing.setUpdatedAt(LocalDateTime.now());

		if (costing.getId() == null) {
			costing.setIsActive(true);
			costing.setIsDelete(false);
			costing.setUuid(UUID.randomUUID().toString());
		}

		return costing;
	}
}
