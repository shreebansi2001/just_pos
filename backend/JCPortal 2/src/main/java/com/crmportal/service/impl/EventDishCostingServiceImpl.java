package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventDishCostingEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventLaborEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.repository.ContactTypeMasterRepository;
import com.crmportal.repository.EventDishCostingRepository;
import com.crmportal.repository.EventExtraExpenseRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventLaborRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialDisposableRepository;
import com.crmportal.repository.EventRawMaterialFunctionsRepository;
import com.crmportal.repository.EvetFunctionMenuAllocationOrderRepository;
import com.crmportal.repository.MenuAllocationItemRawMaterialRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.request.dto.EventLaborDetailsRequestDto;
import com.crmportal.request.dto.EventLaborRequestDto;
import com.crmportal.response.dto.EventDishCostingResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventLaborDetailsResponseDto;
import com.crmportal.response.dto.EventLaborResponseDto;
import com.crmportal.response.dto.ExtraRawItemDTO;
import com.crmportal.response.dto.RawMaterialCategoryRateResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventDishCostingService;
import com.crmportal.service.EventFunctionMasterService;
import com.crmportal.service.EventLaborService;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

@Service
public class EventDishCostingServiceImpl implements EventDishCostingService {

	@Autowired
	EventLaborRepository eventLaborRepository;

	@Autowired
	EventDishCostingRepository eventDishCostingRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	EvetFunctionMenuAllocationOrderRepository evetFunctionMenuAllocationOrderRepository;

	@Autowired
	MenuAllocationItemRawMaterialRepository menuAllocationItemRawMaterialRepository;

	@Autowired
	EventExtraExpenseRepository eventExtraExpenseRepository;

	@Autowired
	EventFunctionMasterService eventFunctionMasterService;

	@Autowired
	EventRawMaterialFunctionsRepository eventRawMaterialFunctionsRepository;

	@Autowired
	EventRawMaterialDisposableRepository eventRawMaterialDisposableRepository;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	public EventDishCostingResponseDto geDishCosting(Long eventId, EventMasterEntity eventMaster,
			Boolean isRawMaterialDone, Boolean isMenuAllocationDone) {
		try {
			EventDishCostingEntity en = new EventDishCostingEntity();
			EventDishCostingResponseDto dish = calculateDishCosting(eventId, isRawMaterialDone, isMenuAllocationDone);
			List<EventDishCostingEntity> evLabEntity = eventDishCostingRepository
					.findByEvent_IdAndEventFunctionIsNull(eventId);

			Integer totalPax = eventFunctionMasterRepository.getEventTotalPerson(eventId);

			dish.setPax(totalPax);
			dish.setEventFunction(null);

			if (evLabEntity.size() != 0) {
				en = evLabEntity.get(0);
			} else {
				en.setCreatedAt(commonService.getCurrentDateTime());
			}

			BigDecimal extraAmount = menuItemRawMaterialRepository.getTotalExtraRawMaterialAmount(eventId, -1l);

			if (extraAmount == null) {
				extraAmount = BigDecimal.ZERO;
			}

			BigDecimal dishRaw = dish.getRawmaterialcharge() != null ? BigDecimal.valueOf(dish.getRawmaterialcharge())
					: BigDecimal.ZERO;

			System.out.println("extra : " + extraAmount);
			System.out.println("dish : " + dish.getRawmaterialcharge());
			en.setCheflaborcharge(dish.getCheflaborcharge());
			en.setOutsideagencycharge(dish.getOutsideagencycharge());
			en.setExtraexpensecharge(dish.getExtraexpensecharge());
			en.setLaborcharge(dish.getLaborcharge());
			en.setRawmaterialcharge(dish.getRawmaterialcharge() != null ? dish.getRawmaterialcharge() : 0.0);
			System.out.println("final amount : " + en.getRawmaterialcharge());
			en.setPax(totalPax);
			en.setEvent(eventMaster);
			en.setEventFunction(null);
			en.setUpdatedAt(commonService.getCurrentDateTime());
			eventDishCostingRepository.save(en);

			return dish;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get dish costing details ", e);
		}
	}

	@Override
	public EventDishCostingResponseDto geDishCosting(Long eventId, Long eventFunctionId, Boolean isRawMaterialDone,
			Boolean isMenuAllocationDone) {
		try {
			EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event Master with this eventId not found: " + eventId));

			if (eventFunctionId == -1 || eventFunctionId == 0) {
				System.out.println("in 1");
				return geDishCosting(eventId, eventMaster, isRawMaterialDone, isMenuAllocationDone);
			}

			EventFunctionMasterEntity functionEntity = eventFunctionMasterRepository
					.findByIdAndIsDeleteFalse(eventFunctionId).orElseThrow(
							() -> new RuntimeException("Event Function Master not found with id: " + eventFunctionId));

			List<EventFunctionMasterResponseDto> eventFunctionRes = eventFunctionMasterService
					.getEventFunctionById(eventFunctionId);
			EventDishCostingEntity en = new EventDishCostingEntity();
			System.out.println("in 2");
			EventDishCostingResponseDto dish = calculateDishCosting(eventId, eventFunctionId, isRawMaterialDone,
					isMenuAllocationDone);
			dish.setPax(functionEntity.getPax());
			dish.setEventFunction(eventFunctionRes.size() != 0 ? eventFunctionRes.get(0) : null);
			List<EventDishCostingEntity> evLabEntity = eventDishCostingRepository
					.findByEvent_IdAndEventFunction_Id(eventId, eventFunctionId);
			if (evLabEntity.size() != 0) {
				en = evLabEntity.get(0);
			} else {
				en.setCreatedAt(commonService.getCurrentDateTime());
			}
			en.setCheflaborcharge(dish.getCheflaborcharge());
			en.setOutsideagencycharge(dish.getOutsideagencycharge());
			en.setExtraexpensecharge(dish.getExtraexpensecharge());
			en.setLaborcharge(dish.getLaborcharge());
			en.setRawmaterialcharge(dish.getRawmaterialcharge());
			en.setPax(functionEntity.getPax());
			en.setEvent(eventMaster);
			en.setEventFunction(functionEntity);
			en.setUpdatedAt(commonService.getCurrentDateTime());
			eventDishCostingRepository.save(en);

			return dish;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get dish costing details ", e);
		}
	}

	public EventDishCostingResponseDto calculateDishCosting(Long eventId, Long eventFunctionId,
			Boolean isRawMaterialDone, Boolean isMenuAllocationDone) {
		try {
			EventDishCostingResponseDto res = new EventDishCostingResponseDto();
			res.setCheflaborcharge(evetFunctionMenuAllocationOrderRepository
					.getChefLabourTotalForEventAndFunction(eventId, eventFunctionId).doubleValue());
			res.setOutsideagencycharge(evetFunctionMenuAllocationOrderRepository
					.getOutsideTotalForEventAndFunction(eventId, eventFunctionId).doubleValue());
			res.setExtraexpensecharge(eventExtraExpenseRepository.getTotalExtraExpense(eventId, eventFunctionId));
			res.setLaborcharge(eventLaborRepository.getTotalLaborPrice(eventId, eventFunctionId));

			if (isRawMaterialDone) {
				BigDecimal extraAmount = menuItemRawMaterialRepository.getTotalExtraRawMaterialAmount(eventId,
						eventFunctionId);

				if (extraAmount == null) {
					extraAmount = BigDecimal.ZERO;
				}

				res.setRawmaterialcharge(menuAllocationItemRawMaterialRepository
						.getTotalRateByEventAndFunctionFromRowMaterial(eventId, eventFunctionId).add(extraAmount)
						.doubleValue());
			} else if (isMenuAllocationDone) {
				res.setRawmaterialcharge(menuAllocationItemRawMaterialRepository
						.getTotalRateByEventAndFunction(eventId, eventFunctionId).doubleValue());
			}

			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed get dish costing details ", e);
		}
	}

	public EventDishCostingResponseDto calculateDishCosting(Long eventId, Boolean isRawMaterialDone,
			Boolean isMenuAllocationDone) {
		try {
			EventDishCostingResponseDto res = new EventDishCostingResponseDto();
			Double totalRawMaterialRate = 0D;
			if (isRawMaterialDone) {
				System.out.println("in isRawMaterialDone");
				System.out.println("Sum Of totalPrice:- "+menuAllocationItemRawMaterialRepository
						.getTotalRateByEventFromRowMaterial(eventId));
				totalRawMaterialRate = menuAllocationItemRawMaterialRepository
						.getTotalRateByEventFromRowMaterial(eventId)
						.doubleValue();
				res.setRawmaterialcharge(totalRawMaterialRate);
			} else if (isMenuAllocationDone) {
				System.out.println("in isMenuAllocationDone");
				 totalRawMaterialRate = menuAllocationItemRawMaterialRepository.getTotalRateByEvent(eventId)
						.doubleValue();

				res.setRawmaterialcharge(totalRawMaterialRate);
			}

			BigDecimal disposableAmount = eventRawMaterialDisposableRepository.getTotalDisposibleAmount(eventId);
System.out.println("disposableAmount :-"+disposableAmount);
			if (disposableAmount == null) {
				disposableAmount = BigDecimal.ZERO;
			}

			BigDecimal extraAmount = menuItemRawMaterialRepository.getTotalExtraRawMaterialAmount(eventId, -1l);

			if (extraAmount == null) {
				extraAmount = BigDecimal.ZERO;
			}

			res.setCheflaborcharge(
					evetFunctionMenuAllocationOrderRepository.getChefLabourTotalForEvent(eventId).doubleValue());
			res.setOutsideagencycharge(
					evetFunctionMenuAllocationOrderRepository.getOutsideTotalForEvent(eventId).doubleValue());
			res.setExtraexpensecharge(eventExtraExpenseRepository.getTotalExtraExpense(eventId));
			res.setLaborcharge(eventLaborRepository.getTotalLaborPrice(eventId));
			res.setRawmaterialcharge(disposableAmount.doubleValue() + totalRawMaterialRate + extraAmount.doubleValue());
			System.out.println("In Outer:- " + res.getRawmaterialcharge());
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed get dish costing details ", e);
		}
	}

	@Override
	public List<RawMaterialCategoryRateResponseDto> getRawMaterialTotalCategoryWise(Long eventId, Long eventFunctionId,
			Boolean isRawMaterialDone, Boolean isMenuAllocationDone) {
		List<RawMaterialCategoryRateResponseDto> response = new ArrayList<>();
		if (isRawMaterialDone) {
			System.out.println("in if");
			BigDecimal extraAmount = menuItemRawMaterialRepository.getTotalExtraRawMaterialAmount(eventId,
					eventFunctionId);

			if (extraAmount == null) {
				extraAmount = BigDecimal.ZERO;
			}
			System.out.println("extraAmount:- " + extraAmount);
			if(eventFunctionId != -1) {
				response = eventRawMaterialFunctionsRepository.getCategoryWiseTotalRate(eventId, eventFunctionId);
			}else {
				response = eventRawMaterialFunctionsRepository.getCategoryWiseTotalRateFromEventRaw(eventId);
			}

			RawMaterialCategoryRateResponseDto dto = new RawMaterialCategoryRateResponseDto();
			dto.setCategoryId(null);
			dto.setCategoryNameEng("EXTRA RAW MATERIAL");
			dto.setTotalRate(extraAmount);
			response.add(dto);
		} else {
			System.out.println("in else");
			response = menuAllocationItemRawMaterialRepository.getCategoryWiseTotalRate(eventId, eventFunctionId);
		}
		BigDecimal disposableAmount = BigDecimal.ZERO;
		if(eventFunctionId == -1 || eventFunctionId == 0) {
			disposableAmount  = eventRawMaterialDisposableRepository.getTotalDisposibleAmount(eventId);
		
			RawMaterialCategoryRateResponseDto dto = new RawMaterialCategoryRateResponseDto();
			dto.setCategoryId(null);
			dto.setCategoryNameEng("CROCKERY ");
			dto.setTotalRate(disposableAmount);
			response.add(dto);
		}
		
		System.out.println("In inner:- " + response);
		return response;
	}
}
