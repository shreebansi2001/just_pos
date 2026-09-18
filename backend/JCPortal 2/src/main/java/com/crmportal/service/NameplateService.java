package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.request.NameplateRequestDto;
import com.crmportal.response.dto.EventFunctionNameplateResponseDto;

@Service
public interface NameplateService {

	Map<String, Object> addNameplateItems(List<NameplateRequestDto> namePlateRequests, Integer itemFontSize,
			Integer categoryFontSize, Long eventId, Long eventFunctionId, Long userId, Integer isCounterItem,
			Integer isStandyItem, Integer isTableMenuItem, String headerNotesEnglish, String headerNotesHindi,
			String headerNotesGujarati, String footerNotesEnglish, String footerNotesHindi, String footerNotesGujarati);
	
	Map<String, Object> getNameplateItems(Long userId, Long eventId, Long eventFunctionId, Integer lang);

	Map<String, Object> getNameplateItemsWithCategory(Long userId, Long eventId, Long eventFunctionId, Long catId, 
			Integer lang, Integer isCounterNamePlate, Integer isStandyNamePlate, Integer isTableMenuNamePlate);

	Map<String, Object> getNameplateCategoryOfItems(Long userId, Long eventId, Long eventFunctionId, Integer lang, 
			Integer isCounterNamePlate, Integer isStandyNamePlate, Integer isTableMenuNamePlate);

	Map<String, Object> getNameplateItemsWithCategory(Long userid, Long eventId, Long eventFunctionId, Long catId, 
			int lang, String defaultLanguage, String preferedLanguage, Integer isCounterNamePlate, 
			Integer isStandyNamePlate, Integer isTableMenuNamePlate);

	Map<String, Object> getNameplateItemsWithNamePlateType(Long userId, Long eventId, Long eventFunctionId,
			Integer lang, Integer isCounterItem, Integer isStandyItem, Integer isTableMenuItem);

}
