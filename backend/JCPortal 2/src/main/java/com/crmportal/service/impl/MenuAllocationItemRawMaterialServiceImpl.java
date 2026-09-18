package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CaptainReceipeMasterEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionMenuAllocationEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.MenuAllocationItemCaptainReceipeEntity;
import com.crmportal.entity.MenuAllocationItemRawMaterialEntity;
import com.crmportal.entity.MenuItemCaptainReceipeEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.repository.CaptainReceipeMasterRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EvetFunctionMenuAllocationOrderRepository;
import com.crmportal.repository.MenuAllocationItemCaptainReceipeRepository;
import com.crmportal.repository.MenuAllocationItemRawMaterialRepository;
import com.crmportal.repository.MenuItemCaptainReceipeRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.request.dto.EventFunctionMenuItemRawMaterialRequestDto;
import com.crmportal.response.dto.UnitConversionResult;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFunctionMenuAllocationService;
import com.crmportal.service.MenuAllocationItemRawMaterialService;
import com.crmportal.utility.RoundOffUtility;

@Service
public class MenuAllocationItemRawMaterialServiceImpl implements MenuAllocationItemRawMaterialService {

	@Autowired
	EvetFunctionMenuAllocationOrderRepository menuAllocationOrderRepository;

	@Autowired
	EventMasterRepository eventRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionRepository;

	@Autowired
	MenuItemMasterRepository menuItemRepository;

	@Autowired
	UnitMasterRepository unitRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	PartyMasterRepository partyRepository;

	@Autowired
	MenuAllocationItemRawMaterialRepository menuAllocationItemRawMaterRepository;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	EventFunctionMenuAllocationRepository eventFunctionMenuAllocationRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialRepository;

	@Autowired
	RoundOffUtility roundOffUtility;

	@Autowired
	UnitConversionServiceImpl unitConversionService;

	@Autowired
	EventRawMaterialHelperService eventRawMaterialHelperService;
	
	@Autowired
	MenuAllocationItemCaptainReceipeRepository menuAllocationItemCaptainReceipeRepository;
	
	@Autowired
	CaptainReceipeMasterRepository captainReceipeMasterRepository;
	
	@Autowired
	MenuItemCaptainReceipeRepository menuItemCaptainReceipeRepository;

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);

	@Override
	@Transactional
	public Boolean addOrUpdateMenuItemRawMaterial(@Valid List<EventFunctionMenuItemRawMaterialRequestDto> requests) {

		if (requests == null || requests.isEmpty()) {
			return false;
		}

		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("dd/MM/yyyy hh:mm a").toFormatter(Locale.ENGLISH);

		for (EventFunctionMenuItemRawMaterialRequestDto dto : requests) {
			if(dto.getIsCaptainReceipe() != null && dto.getIsCaptainReceipe()) {
				MenuAllocationItemCaptainReceipeEntity entity = (dto.getId() != null && dto.getId() != 0)
						? menuAllocationItemCaptainReceipeRepository.findById(dto.getId())
								.orElseThrow(() -> new RuntimeException("Captain Receipe not found"))
						: new MenuAllocationItemCaptainReceipeEntity();
				
				if (dto.getId() == null || dto.getId() == 0) {
					entity.setCreatedAt(commonService.getCurrentDateTime());
				} else {
					entity.setUpdatedAt(commonService.getCurrentDateTime());
				}
				
				if (dto.getDateTime() != null && !dto.getDateTime().isEmpty()) {
					entity.setDateTime(LocalDateTime.parse(dto.getDateTime(), formatter));
				}
				
				EventMasterEntity event = eventRepository.findById(dto.getEventId())
						.orElseThrow(() -> new RuntimeException("Event not found"));
	
				EventFunctionMasterEntity eventFunction = eventFunctionRepository.findById(dto.getEventFunctionId())
						.orElseThrow(() -> new RuntimeException("Event Function not found"));
	
				MenuItemMasterEntity menuItem = menuItemRepository.findById(dto.getMenuItemId())
						.orElseThrow(() -> new RuntimeException("Menu Item not found"));
				UnitMasterEntity selectedUnit = unitRepository.findById(dto.getUnitId())
						.orElseThrow(() -> new RuntimeException("Unit not found"));
				
				CaptainReceipeMasterEntity captainReceipe = captainReceipeMasterRepository.findById(dto.getRawMaterialId())
						.orElseThrow(() -> new RuntimeException("Raw Material not found"));
				entity.setEvent(event);
				entity.setEventFunction(eventFunction);
				entity.setMenuItem(menuItem);
				entity.setUnit(selectedUnit);
				entity.setPlace(dto.getPlace());
				entity.setCaptainReceipe(captainReceipe);
				
				if (dto.getPartyId() != null && dto.getPartyId() != 0) {
					entity.setParty(partyRepository.findById(dto.getPartyId())
							.orElseThrow(() -> new RuntimeException("Party not found")));
	 			} else {
					entity.setParty(null);
				}
				
				MenuItemCaptainReceipeEntity master = menuItemCaptainReceipeRepository
						.findByMenuItemAndCaptainReceipeAndIsDeleteFalse(menuItem, captainReceipe)
						.orElseThrow(() -> new RuntimeException("Menu Item captain Receipe not found."));
				
				if (entity.getWeight().compareTo(dto.getWeight()) != 0) {
					MenuItemCaptainReceipeEntity itemCaptainReceipeEntity = menuItemCaptainReceipeRepository
							.findByMenuItemAndCaptainReceipeAndIsDeleteFalse(entity.getMenuItem(), entity.getCaptainReceipe())
							.orElse(null);
					
					applyCalculationFromEntity(entity, dto.getWeight(), selectedUnit, entity.getCaptainReceipeRate(),
							itemCaptainReceipeEntity);
					
					ArrayList<Long> menuItemIds = new ArrayList<>();
					
					menuItemIds.add(menuItem.getId());
					
					eventRawMaterialHelperService.deleteEventRawMaterialFunctions(event.getId(), eventFunction.getId(),
							menuItemIds);
					eventRawMaterialHelperService.addEventRawMaterialFunctionsByMenuItemId(event.getId(),
							eventFunction.getId(), menuItemIds);
				} else {
					entity.setWeight(dto.getWeight());
					entity.setRate(dto.getRate());
				}
				if (master != null) {
					entity.setCaptainReceipeRate(master.getRate() != null ? master.getRate() : BigDecimal.ZERO);
	
					entity.setCaptainReceipeWeight(master.getWeight() != null ? master.getWeight() : BigDecimal.ZERO);
				}
//				entity.setSupRate(dto.getSupRate());
//				entity.setMasterRawUnitId(dto.getMasterRawUnitId());
				menuAllocationItemCaptainReceipeRepository.save(entity);
			} else {
				MenuAllocationItemRawMaterialEntity entity = (dto.getId() != null && dto.getId() != 0)
						? menuAllocationItemRawMaterRepository.findById(dto.getId())
								.orElseThrow(() -> new RuntimeException("Allocation not found"))
						: new MenuAllocationItemRawMaterialEntity();
	
				if (dto.getId() == null || dto.getId() == 0) {
					entity.setCreatedAt(commonService.getCurrentDateTime());
				} else {
					entity.setUpdatedAt(commonService.getCurrentDateTime());
				}
	
				if (dto.getDateTime() != null && !dto.getDateTime().isEmpty()) {
					entity.setDateTime(LocalDateTime.parse(dto.getDateTime(), formatter));
				}
	
				EventMasterEntity event = eventRepository.findById(dto.getEventId())
						.orElseThrow(() -> new RuntimeException("Event not found"));
	
				EventFunctionMasterEntity eventFunction = eventFunctionRepository.findById(dto.getEventFunctionId())
						.orElseThrow(() -> new RuntimeException("Event Function not found"));
	
				MenuItemMasterEntity menuItem = menuItemRepository.findById(dto.getMenuItemId())
						.orElseThrow(() -> new RuntimeException("Menu Item not found"));
				UnitMasterEntity selectedUnit = unitRepository.findById(dto.getUnitId())
						.orElseThrow(() -> new RuntimeException("Unit not found"));
	
				RawMaterialMasterEntity rawMaterial = rawMaterialRepository.findById(dto.getRawMaterialId())
						.orElseThrow(() -> new RuntimeException("Raw Material not found"));
				entity.setEvent(event);
				entity.setEventFunction(eventFunction);
				entity.setMenuItem(menuItem);
				entity.setUnit(selectedUnit);
				entity.setPlace(dto.getPlace());
				entity.setRawMaterial(rawMaterial);
				if (dto.getPartyId() != null && dto.getPartyId() != 0) {
					entity.setParty(partyRepository.findById(dto.getPartyId())
							.orElseThrow(() -> new RuntimeException("Party not found")));
	 			} else {
					entity.setParty(null);
				}
				MenuItemRawMaterialEntity master = menuItemRawMaterialRepository
						.findByMenuItemAndRawMaterialAndIsDeleteFalse(menuItem, rawMaterial);
	
				if(dto.getIsNewRaw()) {
					entity.setIsNewRaw(true);
				}
				if (entity.getWeight().compareTo(dto.getWeight()) != 0 && !dto.getIsNewRaw()) {
					MenuItemRawMaterialEntity itemRawMaterialEntity = menuItemRawMaterialRepository
							.findByMenuItemAndRawMaterialAndIsDeleteFalse(entity.getMenuItem(), entity.getRawMaterial());
					applyCalculationFromEntity(entity, dto.getWeight(), selectedUnit, entity.getRawmaterial_rate(),
							itemRawMaterialEntity);
					ArrayList<Long> menuItemIds = new ArrayList<>();
					menuItemIds.add(menuItem.getId());
					eventRawMaterialHelperService.deleteEventRawMaterialFunctions(event.getId(), eventFunction.getId(),
							menuItemIds);
					eventRawMaterialHelperService.addEventRawMaterialFunctionsByMenuItemId(event.getId(),
							eventFunction.getId(), menuItemIds);
				} else {
					entity.setWeight(dto.getWeight());
					entity.setRate(dto.getRate());
				}
				if (master != null) {
					entity.setRawmaterial_rate(master.getRate() != null ? master.getRate() : BigDecimal.ZERO);
	
					entity.setRawMaterialWeight(master.getWeight() != null ? master.getWeight() : BigDecimal.ZERO);
				}
				entity.setSupRate(dto.getSupRate());
				entity.setMasterRawUnitId(dto.getMasterRawUnitId());
				menuAllocationItemRawMaterRepository.save(entity);
				if(entity.getIsNewRaw()) {
					ArrayList<Long> menuItemIds = new ArrayList<>();
					menuItemIds.add(menuItem.getId());
					eventRawMaterialHelperService.deleteEventRawMaterialFunctions(event.getId(), eventFunction.getId(),
							menuItemIds);
					eventRawMaterialHelperService.addEventRawMaterialFunctionsByMenuItemId(event.getId(),
							eventFunction.getId(), menuItemIds);
				}
			}
		}
		return true;
	}

//	private void applyCalculationFromEntity(MenuAllocationItemRawMaterialEntity entity, BigDecimal finalWeight,
//			UnitMasterEntity unit, BigDecimal rawmaterial_rate, MenuItemRawMaterialEntity itemRawMaterialEntity) {
//
//		BigDecimal rate = BigDecimal.ZERO;
//		if (itemRawMaterialEntity.getRawMaterial().getIsApplyCal()) {
//			finalWeight = roundOffUtility.applyUnitRange(finalWeight, unit, itemRawMaterialEntity);
//		}
//		BigDecimal value = finalWeight;
//		if (itemRawMaterialEntity != null) {
//			value = adjustBaseWeight(value, unit, itemRawMaterialEntity.getUnit());
//		}
//
//		UnitConversionResult conversion = unitConversionService.autoConvert(unit, finalWeight);
//
//		entity.setUnit(conversion.getUnit());
//		if (entity.getRawMaterialWeight().compareTo(BigDecimal.ZERO) == 0) {
//			entity.setRate(removeDecimal(rawmaterial_rate));
//		} else {
//			rate = safeMultiply(entity.getRawmaterial_rate(), value).divide(entity.getRawMaterialWeight(), 4,
//					RoundingMode.HALF_UP);
//			entity.setRate(removeDecimal(rate));
//		}
//		entity.setWeight(conversion.getQuantity()
//				.setScale(entity.getUnit() != null
//						? (entity.getUnit().getDecimalLimit() == null ? 2 : entity.getUnit().getDecimalLimit())
//						: 2, RoundingMode.HALF_UP));
//	}
	
	private void applyCalculationFromEntity(
	        Object entity,
	        BigDecimal finalWeight,
	        UnitMasterEntity unit,
	        BigDecimal rawmaterialRate,
	        Object itemEntity) {

	    BigDecimal rate = BigDecimal.ZERO;
	    UnitMasterEntity baseUnit = null;

	    // Handle MenuItem entities
	    if (itemEntity instanceof MenuItemRawMaterialEntity) {
	        MenuItemRawMaterialEntity rawMaterialEntity =
	                (MenuItemRawMaterialEntity) itemEntity;

	        if (Boolean.TRUE.equals(rawMaterialEntity.getRawMaterial().getIsApplyCal())) {
	            finalWeight = roundOffUtility.applyUnitRange(
	                    finalWeight, unit, rawMaterialEntity);
	        }
	        baseUnit = rawMaterialEntity.getUnit();

	    } else if (itemEntity instanceof MenuItemCaptainReceipeEntity) {

	        MenuItemCaptainReceipeEntity captainReceipeEntity =
	                (MenuItemCaptainReceipeEntity) itemEntity;

//	        if (Boolean.TRUE.equals(captainReceipeEntity.getCaptainReceipe().getIsApplyCal())) {
	            finalWeight = roundOffUtility.applyUnitRange(
	                    finalWeight, unit, null);
//	        }

	        baseUnit = captainReceipeEntity.getUnit();
	    }

	    BigDecimal value = finalWeight;

	    if (baseUnit != null) {
	        value = adjustBaseWeight(value, unit, baseUnit);
	    }
	    UnitConversionResult conversion =
	            unitConversionService.autoConvert(unit, finalWeight);

	    // Handle Allocation entities
	    if (entity instanceof MenuAllocationItemRawMaterialEntity) {

	        MenuAllocationItemRawMaterialEntity rawEntity =
	                (MenuAllocationItemRawMaterialEntity) entity;

	        rawEntity.setUnit(conversion.getUnit());

	        if (rawEntity.getRawMaterialWeight().compareTo(BigDecimal.ZERO) == 0) {
	            rawEntity.setRate(removeDecimal(rawmaterialRate));
	        } else {
	            rate = safeMultiply(rawEntity.getRawmaterial_rate(), value)
	                    .divide(rawEntity.getRawMaterialWeight(), 4, RoundingMode.HALF_UP);

	            rawEntity.setRate(removeDecimal(rate));
	        }
	        rawEntity.setWeight(
	                conversion.getQuantity().setScale(
	                        rawEntity.getUnit() != null
	                                ? (rawEntity.getUnit().getDecimalLimit() == null
	                                        ? 2
	                                        : rawEntity.getUnit().getDecimalLimit())
	                                : 2,
	                        RoundingMode.HALF_UP));

	    } else if (entity instanceof MenuAllocationItemCaptainReceipeEntity) {

	        MenuAllocationItemCaptainReceipeEntity captainEntity =
	                (MenuAllocationItemCaptainReceipeEntity) entity;

	        captainEntity.setUnit(conversion.getUnit());

	        if (captainEntity.getCaptainReceipeWeight().compareTo(BigDecimal.ZERO) == 0) {
	            captainEntity.setRate(removeDecimal(rawmaterialRate));
	        } else {
	            rate = safeMultiply(captainEntity.getCaptainReceipeRate(), value)
	                    .divide(captainEntity.getCaptainReceipeWeight(), 4, RoundingMode.HALF_UP);

	            captainEntity.setRate(removeDecimal(rate));
	        }

	        captainEntity.setWeight(
	                conversion.getQuantity().setScale(
	                        captainEntity.getUnit() != null
	                                ? (captainEntity.getUnit().getDecimalLimit() == null
	                                        ? 2
	                                        : captainEntity.getUnit().getDecimalLimit())
	                                : 2,
	                        RoundingMode.HALF_UP));
	    }
	}

	public static BigDecimal adjustBaseWeight(BigDecimal baseWeight, UnitMasterEntity unit,
			UnitMasterEntity rawMaterialUnit) {
		if (baseWeight == null || unit == null || rawMaterialUnit == null) {
			System.out.println("innnnn");
			return baseWeight;
		}

		if (!unit.getId().equals(rawMaterialUnit.getId())) {
			System.out.println("innnnn 2");
			BigDecimal result = rawMaterialUnit.getIsParentUnit()
			        ? baseWeight.divide(THOUSAND,2, RoundingMode.HALF_UP)
			        : baseWeight.multiply(THOUSAND);
			return result;
		}

		return baseWeight;
	}

	private BigDecimal calculateUpdatedWeight(MenuAllocationItemRawMaterialEntity entity,
			EventFunctionMasterEntity eventFunction, Integer newPax) {
		System.err.println("inn calculateUpdatedWeight");
		// Compare Integer values, not references
		return safeMultiply(BigDecimal.valueOf(newPax), entity.getRawMaterialWeight()).divide(HUNDRED,
				getScale(entity.getUnit()), RoundingMode.HALF_UP);
	}

	private int getScale(UnitMasterEntity unit) {
		return unit == null || unit.getDecimalLimit() == null ? 3 : unit.getDecimalLimit();
	}

	private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
		return safe(a).multiply(safe(b));
	}

	private BigDecimal safe(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

	private BigDecimal safeConvertToBaseUnit(BigDecimal qty, UnitMasterEntity unit) {

		if (qty == null || unit == null) {
			return BigDecimal.ZERO;
		}

		if (qty.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		if (Boolean.TRUE.equals(unit.getIsParentUnit())) {
			return qty.setScale(6, RoundingMode.HALF_UP);
		}

		if (unit.getEquivalentValue() == null || unit.getEquivalentValue() <= 0) {

			return BigDecimal.ZERO;
		}

		return qty.divide(BigDecimal.valueOf(unit.getEquivalentValue()), 6, RoundingMode.HALF_UP);
	}

	@Override
	public Boolean deleteMenuItemRawMaterialById(Long id, Long menuItemId, Long eventId, Long eventFunctionId) {
		if (menuAllocationItemRawMaterRepository.existsById(id)) {
			menuAllocationItemRawMaterRepository.deleteById(id);
			System.out.println("itemId:-" + menuItemId);
			List<Long> menuItems = new ArrayList<>();
			menuItems.add(menuItemId);
			System.out.println("in deleteMenuItemRawMaterialById");
			eventRawMaterialHelperService.addEventRawMaterialFunctionsByMenuItemId(eventId, eventFunctionId, menuItems);
			return true;
		} else {
			return false;
		}
	}

	public static BigDecimal removeDecimal(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value.setScale(0, RoundingMode.DOWN); // removes everything after "."
	}

}
