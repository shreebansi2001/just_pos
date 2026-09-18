package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.request.NameplateRequestDto;
import com.crmportal.response.dto.EventFunctionNameplateResponseDto;
import com.crmportal.response.dto.NameplateReportDataResponseDto;
import com.crmportal.service.NameplateService;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.NameplateRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.NameplateEntity;
import com.crmportal.mapper.NameplateMapper;
import com.crmportal.service.CommonService;

@Service
public class NameplateServiceImpl implements NameplateService {

	@Autowired
	private EventMasterRepository eventMasterRepository;

	@Autowired
	private NameplateRepository nameplateRepository;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private NameplateMapper nameplateMapper;

	@Autowired
	private CommonService commonService;

	@Override
	@Transactional
	public Map<String, Object> addNameplateItems(List<NameplateRequestDto> request, Integer itemFontSize,
			Integer categoryFontSize, Long eventId, Long eventFunctionId, Long userId, Integer isCounterItem,
			Integer isStandyItem, Integer isTableMenuItem, String headerNotesEnglish, String headerNotesHindi,
			String headerNotesGujarati, String footerNotesEnglish, String footerNotesHindi,
			String footerNotesGujarati) {

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));

		List<EventFunctionNameplateResponseDto> responseDtos = new ArrayList<>();
		nameplateRepository.deleteAllByEventAndEventFunctionId(eventMasterEntity, eventFunctionId);
		for (NameplateRequestDto dto : request) {
			NameplateEntity entity = nameplateMapper.requestToEntity(dto);
			entity.setEvent(eventMasterEntity);
			entity.setEventFunctionId(eventFunctionId);
			entity.setItemFontSize(itemFontSize);
			entity.setCategoryFontSize(categoryFontSize);
			entity.setUser(userMasterEntity);
			entity.setIsCounterItem(isCounterItem);
			entity.setIsStandyItem(isStandyItem);
			entity.setIsTableMenuItem(isTableMenuItem);

			entity.setHeaderNotesEnglish(headerNotesEnglish);
			entity.setHeaderNotesHindi(headerNotesHindi);
			entity.setHeaderNotesGujarati(headerNotesGujarati);
			entity.setFooterNotesEnglish(footerNotesEnglish);
			entity.setFooterNotesHindi(footerNotesHindi);
			entity.setFooterNotesGujarati(footerNotesGujarati);
			entity = nameplateRepository.save(entity);

			EventFunctionNameplateResponseDto responseDto = nameplateMapper.entityToResponse(entity);

			responseDtos.add(responseDto);
		}
		Map<String, Object> response = new HashMap<>();
		response.put("data", responseDtos);
		response.put("item_font_size", itemFontSize);
		response.put("category_font_size", categoryFontSize);
		response.put("event_id", eventId);
		response.put("event_function_id", eventFunctionId);
		response.put("is_counter_item", isCounterItem);
		response.put("is_standy_item", isStandyItem);
		response.put("is_table_menu_item", isTableMenuItem);

		response.put("headerNotesEnglish", headerNotesEnglish);
		response.put("headerNotesHindi", headerNotesHindi);
		response.put("headerNotesGujarati", headerNotesGujarati);
		response.put("footerNotesEnglish", footerNotesEnglish);
		response.put("footerNotesHindi", footerNotesHindi);
		response.put("footerNotesGujarati", footerNotesGujarati);

		return response;
	}

	@Override
	public Map<String, Object> getNameplateItems(Long userId, Long eventId, Long eventFunctionId, Integer lang) {

		List<Object[]> resulls = nameplateRepository.findAllNamePlateItems(eventId, eventFunctionId, userId);
		List<EventFunctionNameplateResponseDto> dtos = new ArrayList<>();

		Integer itemFontSize = 0;
		Integer categoryFontSize = 0;
		Long event_id = Long.valueOf(0);
		Long event_function_id = Long.valueOf(0);
		String eventNo = "";

		for (Object[] row : resulls) {
			int index = 0;
			EventFunctionNameplateResponseDto dto = new EventFunctionNameplateResponseDto();
			dto.setId(commonService.getLong(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemNameEnglish(commonService.getString(row[index++]));
			dto.setItemNameHindi(commonService.getString(row[index++]));
			dto.setItemNameGujarati(commonService.getString(row[index++]));
			dto.setItemCount(commonService.getBigDecimal(row[index++]));

			itemFontSize = commonService.getInteger(row[index++]);
			categoryFontSize = commonService.getInteger(row[index++]);
			event_id = commonService.getLong(row[index++]);
			event_function_id = commonService.getLong(row[index++]);

			dto.setSequence(commonService.getBigDecimal(row[index++]));

			eventNo = commonService.getString(row[index++]);

			dto.setIsStandyChecked(commonService.getInteger(row[index++]));
			dto.setIsTableMenuChecked(commonService.getInteger(row[index++]));

			dtos.add(dto);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("data", dtos);
		response.put("item_font_size", itemFontSize);
		response.put("category_font_size", categoryFontSize);
		response.put("event_id", event_id);
		response.put("eventNo", eventNo);
		response.put("event_function_id", event_function_id);

		return response;
	}

	@Override
	public Map<String, Object> getNameplateItemsWithNamePlateType(Long userId, Long eventId, Long eventFunctionId,
			Integer lang, Integer isCounterItem, Integer isStandyItem, Integer isTableMenuItem) {

		List<Object[]> resulls = nameplateRepository.findAllNamePlateItems(eventId, eventFunctionId, userId,
				isCounterItem, isStandyItem, isTableMenuItem);
		List<EventFunctionNameplateResponseDto> dtos = new ArrayList<>();

		Integer itemFontSize = 0;
		Integer categoryFontSize = 0;
		Long event_id = Long.valueOf(0);
		Long event_function_id = Long.valueOf(0);
		String eventNo = "";
		Integer isCounterNamePlate = 0;
		Integer isStandyNamePlate = 0;
		Integer isTableMenuNamePlate = 0;

		String headerNotesEnglish = "";
		String headerNotesHindi = "";
		String headerNotesGujarati = "";
		String footerNotesEnglish = "";
		String footerNotesHindi = "";
		String footerNotesGujarati = "";

		for (Object[] row : resulls) {
			int index = 0;
			EventFunctionNameplateResponseDto dto = new EventFunctionNameplateResponseDto();
			dto.setId(commonService.getLong(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemNameEnglish(commonService.getString(row[index++]));
			dto.setItemNameHindi(commonService.getString(row[index++]));
			dto.setItemNameGujarati(commonService.getString(row[index++]));
			dto.setItemCount(commonService.getBigDecimal(row[index++]));

			itemFontSize = commonService.getInteger(row[index++]);
			categoryFontSize = commonService.getInteger(row[index++]);
			event_id = commonService.getLong(row[index++]);
			event_function_id = commonService.getLong(row[index++]);

			dto.setSequence(commonService.getBigDecimal(row[index++]));

			eventNo = commonService.getString(row[index++]);

			dto.setIsStandyChecked(commonService.getInteger(row[index++]));
			dto.setIsTableMenuChecked(commonService.getInteger(row[index++]));

			isCounterNamePlate = commonService.getInteger(row[index++]);
			isStandyNamePlate = commonService.getInteger(row[index++]);
			isTableMenuNamePlate = commonService.getInteger(row[index++]);

			headerNotesEnglish = commonService.getString(row[index++]);
			headerNotesHindi = commonService.getString(row[index++]);
			headerNotesGujarati = commonService.getString(row[index++]);
			footerNotesEnglish = commonService.getString(row[index++]);
			footerNotesHindi = commonService.getString(row[index++]);
			footerNotesGujarati = commonService.getString(row[index++]);

			dtos.add(dto);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("data", dtos);
		response.put("item_font_size", itemFontSize);
		response.put("category_font_size", categoryFontSize);
		response.put("event_id", event_id);
		response.put("eventNo", eventNo);
		response.put("event_function_id", event_function_id);
		response.put("is_counter_item", isCounterNamePlate);
		response.put("is_standy_item", isStandyNamePlate);
		response.put("is_table_menu_item", isTableMenuNamePlate);

		response.put("headerNotesEnglish", headerNotesEnglish);
		response.put("headerNotesHindi", headerNotesHindi);
		response.put("headerNotesGujarati", headerNotesGujarati);
		response.put("footerNotesEnglish", footerNotesEnglish);
		response.put("footerNotesHindi", footerNotesHindi);
		response.put("footerNotesGujarati", footerNotesGujarati);

		return response;
	}

	@Override
	public Map<String, Object> getNameplateItemsWithCategory(Long userId, Long eventId, Long eventFunctionId,
			Long catId, Integer lang, Integer isCounterNamePlate, Integer isStandyNamePlate,
			Integer isTableMenuNamePlate) {
		List<Object[]> resulls = nameplateRepository.findAllNamePlateItemsWithCategory(eventId, eventFunctionId, catId,
				lang, userId, isCounterNamePlate, isStandyNamePlate, isTableMenuNamePlate);
		List<NameplateReportDataResponseDto> dtos = new ArrayList<>();

		Integer itemFontSize = 0;
		Integer categoryFontSize = 0;
		Long event_id = Long.valueOf(0);
		Long event_function_id = Long.valueOf(0);
		String eventNo = "";

		for (Object[] row : resulls) {
			int index = 0;
			NameplateReportDataResponseDto dto = new NameplateReportDataResponseDto();
			dto.setId(commonService.getLong(row[index++]));
			dto.setMenuCategoryId(commonService.getLong(row[index++]));
			dto.setCategoryName(commonService.getString(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setItemCount(commonService.getBigDecimal(row[index++]));

			itemFontSize = commonService.getInteger(row[index++]);
			categoryFontSize = commonService.getInteger(row[index++]);
			event_id = commonService.getLong(row[index++]);
			event_function_id = commonService.getLong(row[index++]);

			dto.setSequence(commonService.getBigDecimal(row[index++]));

			eventNo = commonService.getString(row[index++]);
			dtos.add(dto);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("data", dtos);
		response.put("item_font_size", itemFontSize);
		response.put("category_font_size", categoryFontSize);
		response.put("event_id", event_id);
		response.put("eventNo", eventNo);
		response.put("event_function_id", event_function_id);

		return response;
	}

	@Override
	public Map<String, Object> getNameplateItemsWithCategory(Long userId, Long eventId, Long eventFunctionId,
			Long catId, int lang, String defaultLanguage, String preferedLanguage, Integer isCounterNamePlate,
			Integer isStandyNamePlate, Integer isTableMenuNamePlate) {
		List<Object[]> resulls = nameplateRepository.findAllNamePlateItemsWithCategory(eventId, eventFunctionId, catId,
				defaultLanguage, preferedLanguage, userId, isCounterNamePlate, isStandyNamePlate, isTableMenuNamePlate);
		List<NameplateReportDataResponseDto> dtos = new ArrayList<>();

		Integer itemFontSize = 0;
		Integer categoryFontSize = 0;
		Long event_id = Long.valueOf(0);
		Long event_function_id = Long.valueOf(0);
		String eventNo = "";

		for (Object[] row : resulls) {
			int index = 0;
			NameplateReportDataResponseDto dto = new NameplateReportDataResponseDto();
			dto.setId(commonService.getLong(row[index++]));
			dto.setMenuCategoryId(commonService.getLong(row[index++]));
			dto.setCategoryName(commonService.getString(row[index++]));
			dto.setCategoryNamePref(commonService.getString(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemName(commonService.getString(row[index++]));
			dto.setItemNamePref(commonService.getString(row[index++]));
			dto.setItemCount(commonService.getBigDecimal(row[index++]));

			itemFontSize = commonService.getInteger(row[index++]);
			categoryFontSize = commonService.getInteger(row[index++]);
			event_id = commonService.getLong(row[index++]);
			event_function_id = commonService.getLong(row[index++]);

			dto.setSequence(commonService.getBigDecimal(row[index++]));

			eventNo = commonService.getString(row[index++]);
			dtos.add(dto);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("data", dtos);
		response.put("item_font_size", itemFontSize);
		response.put("category_font_size", categoryFontSize);
		response.put("event_id", event_id);
		response.put("eventNo", eventNo);
		response.put("event_function_id", event_function_id);

		return response;
	}

	@Override
	public Map<String, Object> getNameplateCategoryOfItems(Long userId, Long eventId, Long eventFunctionId,
			Integer lang, Integer isCounterNamePlate, Integer isStandyNamePlate, Integer isTableMenuNamePlate) {

		List<Object[]> resulls = nameplateRepository.findNamePlateCategory(eventId, eventFunctionId, lang, userId,
				isCounterNamePlate, isStandyNamePlate, isTableMenuNamePlate);
		List<NameplateReportDataResponseDto> dtos = new ArrayList<>();

		Integer itemFontSize = 0;
		Integer categoryFontSize = 0;
		Long event_id = Long.valueOf(0);
		Long event_function_id = Long.valueOf(0);
		String eventNo = "";

		String headerNotesEnglish = "";
		String headerNotesHindi = "";
		String headerNotesGujarati = "";
		String footerNotesEnglish = "";
		String footerNotesHindi = "";
		String footerNotesGujarati = "";

		for (Object[] row : resulls) {
			int index = 0;
			NameplateReportDataResponseDto dto = new NameplateReportDataResponseDto();
			dto.setMenuCategoryId(commonService.getLong(row[index++]));
			dto.setCategoryName(commonService.getString(row[index++]));

			categoryFontSize = commonService.getInteger(row[index++]);
			event_id = commonService.getLong(row[index++]);
			event_function_id = commonService.getLong(row[index++]);
			eventNo = commonService.getString(row[index++]);

			dto.setSequence(commonService.getBigDecimal(row[index++]));
			headerNotesEnglish = commonService.getString(row[index++]);
			headerNotesHindi = commonService.getString(row[index++]);
			headerNotesGujarati = commonService.getString(row[index++]);
			footerNotesEnglish = commonService.getString(row[index++]);
			footerNotesHindi = commonService.getString(row[index++]);
			footerNotesGujarati = commonService.getString(row[index++]);

			if (categoryFontSize != null) {
				dto.setCategoryFontSize(BigDecimal.valueOf(categoryFontSize));
			} else {
				dto.setCategoryFontSize(BigDecimal.ZERO);
			}
			dto.setEventId(event_id);
			dto.setEventFunctionId(event_function_id);

			dtos.add(dto);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("data", dtos);
		response.put("item_font_size", itemFontSize);
		response.put("category_font_size", categoryFontSize);
		response.put("event_id", event_id);
		response.put("eventNo", eventNo);
		response.put("event_function_id", event_function_id);

		response.put("headerNotesEnglish", headerNotesEnglish);
		response.put("headerNotesHindi", headerNotesHindi);
		response.put("headerNotesGujarati", headerNotesGujarati);
		response.put("footerNotesEnglish", footerNotesEnglish);
		response.put("footerNotesHindi", footerNotesHindi);
		response.put("footerNotesGujarati", footerNotesGujarati);

		return response;
	}

}
