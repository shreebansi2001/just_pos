package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.MenuPreparationDetailsEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventCopyService;

@Service
public class EventCopyServiceImpl implements EventCopyService {

	@Autowired
	private EventMasterRepository eventRepository;
	
	@Autowired
	private EventFunctionMasterRepository eventFunctionRepository;
	
	@Autowired
	private MenuPreparationRepository menuPreparationRepository;
	
	@Autowired
	private MenuPreparationDetailsRepository menuPreparationDetailsRepository;
	
	@Autowired
	CommonService commonService;

	@Override
	@Transactional
	public EventMasterEntity copyEvent(Long eventId, Long chileUserId,BigDecimal paxPercentage) {

		// =========================
		// OLD EVENT
		// =========================
		EventMasterEntity oldEvent = eventRepository.findById(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found"));

		// =========================
		// NEW EVENT
		// =========================
		EventMasterEntity newEvent = new EventMasterEntity();
		String eventNum = commonService.getLastestEventNo(oldEvent.getUser().getId());
		BeanUtils.copyProperties(oldEvent, newEvent,
				"id",
				"createdAt",
				"updatedAt");
		
		newEvent.setCreatedAt(null);
		newEvent.setChilduserid(chileUserId);
		// Optional
		newEvent.setEventNo(eventNum);

		newEvent = eventRepository.save(newEvent);

		// =========================
		// EVENT FUNCTIONS
		// =========================
		List<EventFunctionMasterEntity> oldFunctions =
				eventFunctionRepository.findByEventId(oldEvent.getId());

		for (EventFunctionMasterEntity oldFunction : oldFunctions) {

			EventFunctionMasterEntity newFunction =
					new EventFunctionMasterEntity();

			BeanUtils.copyProperties(oldFunction, newFunction,
					"id",
					"createdAt",
					"updatedAt");

			// IMPORTANT
			newFunction.setEvent(newEvent);
			if (paxPercentage != null) {
			    int calculatedPax = BigDecimal.valueOf(oldFunction.getPax())
			            .multiply(paxPercentage)
			            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
			            .intValue();

			    newFunction.setPax(calculatedPax);
			}
			newFunction = eventFunctionRepository.save(newFunction);

			// =========================
			// MENU PREPARATION
			// =========================
			List<MenuPreparationEntity> oldPreparations =
					menuPreparationRepository.findByEventFunctionId(oldFunction.getId());

			for (MenuPreparationEntity oldPreparation : oldPreparations) {

				MenuPreparationEntity newPreparation =
						new MenuPreparationEntity();

				BeanUtils.copyProperties(oldPreparation, newPreparation,
						"id",
						"createdAt",
						"updatedAt");

				// IMPORTANT
				newPreparation.setEventFunction(newFunction);

				newPreparation =
						menuPreparationRepository.save(newPreparation);

				// =========================
				// MENU PREPARATION DETAILS
				// =========================
				List<MenuPreparationDetailsEntity> oldDetails =
						menuPreparationDetailsRepository
								.findByMenuPreparationId(oldPreparation.getId());
				
				List<MenuPreparationDetailsEntity> newDetailsList = new ArrayList<>();

				for (MenuPreparationDetailsEntity oldDetail : oldDetails) {

					MenuPreparationDetailsEntity newDetail =
							new MenuPreparationDetailsEntity();

					BeanUtils.copyProperties(oldDetail, newDetail,
							"id",
							"createdAt");

					// IMPORTANT
					newDetail.setMenuPreparation(newPreparation);

					newDetailsList.add(newDetail);
				}
				if(newDetailsList.size() != 0) {
					menuPreparationDetailsRepository.saveAll(newDetailsList);
				}
			}
		}

		return newEvent;
	}
}
