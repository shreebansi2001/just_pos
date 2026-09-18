package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.NamePlateTableMenuWithBgEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.NamePlateTableMenuWithBgMapper;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.NamePlateTableMenuWithBgRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.NamePlateTableMenuWithBgRequestDto;
import com.crmportal.response.dto.NamePlateTableMenuWithBgResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.NamePlateTableMenuWithBgService;

@Service
public class NamePlateTableMenuWithBgSericeImpl implements NamePlateTableMenuWithBgService{

	@Autowired
	NamePlateTableMenuWithBgRepository tableMenuWithBgRepository; 
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	NamePlateTableMenuWithBgMapper namePlateTableMenuWithBgMapper;
	
	@Autowired
	CommonService commonService;
	
	@Override
	public Map<String, Object> addOrUpdateNamePlate(List<NamePlateTableMenuWithBgRequestDto> request, Long eventId, Long eventFunctionId, 
			Long userId, Integer catFontSize, Integer itemFontSize,
			String headerNotesEnglish, String headerNotesHindi, String headerNotesGujarati,
			String footerNotesEnglish, String footerNotesHindi, String footerNotesGujarati) {
		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
		EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));
		
		List<NamePlateTableMenuWithBgResponseDto> responseDtos = new ArrayList<>();
		
		for (NamePlateTableMenuWithBgRequestDto dto : request) {
			if(dto.getId() == -1) {
				NamePlateTableMenuWithBgEntity entity = namePlateTableMenuWithBgMapper.requestToEntity(dto);
				entity.setIsChecked(dto.getIs_checked() == 1 ? true : false);
				entity.setCatFontSize(catFontSize);
				entity.setItemFontSize(itemFontSize);
				entity.setEvent(eventMasterEntity);
				entity.setEventFunctionId(eventFunctionId);
				entity.setUser(userMasterEntity);
				
				entity.setHeaderNotesEnglish(headerNotesEnglish);
				entity.setHeaderNotesHindi(headerNotesHindi);
				entity.setHeaderNotesGujarati(headerNotesGujarati);
				
				entity.setFooterNotesEnglish(footerNotesEnglish);
				entity.setFooterNotesHindi(footerNotesHindi);
				entity.setFooterNotesGujarati(footerNotesGujarati);
				
				entity = tableMenuWithBgRepository.save(entity);
				
				NamePlateTableMenuWithBgResponseDto responseDto = namePlateTableMenuWithBgMapper.entityToResponse(entity);
				
				responseDtos.add(responseDto);
			}else {
				NamePlateTableMenuWithBgEntity entity = tableMenuWithBgRepository.findByIdAndIsDeleteFalse(dto.getId())
						.orElseThrow(() -> new RuntimeException("Item not found with id : " + dto.getId()));
				
				entity = namePlateTableMenuWithBgMapper.updateEntityFromRequest(entity, dto);
				entity.setIsChecked(dto.getIs_checked() == 1 ? true : false);
				entity.setCatFontSize(catFontSize);
				entity.setItemFontSize(itemFontSize);
				entity.setEvent(eventMasterEntity);
				entity.setUpdatedAt(commonService.getCurrentDateTime());
				entity.setEventFunctionId(eventFunctionId);
				entity.setUser(userMasterEntity);
				
				entity.setHeaderNotesEnglish(headerNotesEnglish);
				entity.setHeaderNotesHindi(headerNotesHindi);
				entity.setHeaderNotesGujarati(headerNotesGujarati);
				
				entity.setFooterNotesEnglish(footerNotesEnglish);
				entity.setFooterNotesHindi(footerNotesHindi);
				entity.setFooterNotesGujarati(footerNotesGujarati);
				
				entity = tableMenuWithBgRepository.save(entity);
				NamePlateTableMenuWithBgResponseDto responseDto = namePlateTableMenuWithBgMapper.entityToResponse(entity);
								
				responseDtos.add(responseDto);
			}
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("data", responseDtos);
		response.put("event_id", eventId);
		response.put("event_function_id", eventFunctionId);
		response.put("category_font_size", catFontSize);
		response.put("item_font_size", itemFontSize);
		
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
		List<Object[]> results = tableMenuWithBgRepository.findAllNamePlateTableMenuWithBg(eventId, eventFunctionId, userId);
		List<NamePlateTableMenuWithBgResponseDto> dtos = new ArrayList<>();
		
		Long event_id = Long.valueOf(0);
		Long event_function_id = Long.valueOf(0);
		Integer catFontSize = 0;
		Integer itemFontSize = 0;
		String eventNo = "";
		
		String headerNotesEnglish = "";
		String headerNotesHindi = "";
		String headerNotesGujarati = "";
		String footerNotesEnglish = "";
		String footerNotesHindi = "";
		String footerNotesGujarati = "";
		
		for (Object[] row : results) {
			int index = 0;
			NamePlateTableMenuWithBgResponseDto dto = new NamePlateTableMenuWithBgResponseDto();
			dto.setId(commonService.getLong(row[index++]));
			dto.setMenuItemId(commonService.getLong(row[index++]));
			dto.setItemNameEnglish(commonService.getString(row[index++]));
			dto.setItemNameHindi(commonService.getString(row[index++]));
			dto.setItemNameGujarati(commonService.getString(row[index++]));
			dto.setItemCount(commonService.getBigDecimal(row[index++]));
			dto.setSequence(commonService.getBigDecimal(row[index++]));
			
			Object isChecked = row[index++];
			
			dto.setIs_checked(isChecked != null ? commonService.getBoolean(isChecked) == true ? 1 : 0 : 0);
			event_id = commonService.getLong(row[index++]);
			event_function_id = commonService.getLong(row[index++]);
			eventNo = commonService.getString(row[index++]);
			
			dto.setMenuCatId(commonService.getLong(row[index++]));
			dto.setCatNameEnglish(commonService.getString(row[index++]));
			dto.setCatNameGujarati(commonService.getString(row[index++]));
			dto.setCatNameHindi(commonService.getString(row[index++]));
			
			catFontSize = commonService.getInteger(row[index++]);
			itemFontSize = commonService.getInteger(row[index++]);

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
		response.put("event_id", event_id);
		response.put("eventNo", eventNo);
		response.put("event_function_id", event_function_id);
		response.put("category_font_size", catFontSize);
		response.put("item_font_size", itemFontSize);
		
		response.put("headerNotesEnglish", headerNotesEnglish);
		response.put("headerNotesHindi", headerNotesHindi);
		response.put("headerNotesGujarati", headerNotesGujarati);
		response.put("footerNotesEnglish", footerNotesEnglish);
		response.put("footerNotesHindi", footerNotesHindi);
		response.put("footerNotesGujarati", footerNotesGujarati);
		
		return response;
	}
	
}
