package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.NamePlateTableMenuWithBgRequestDto;

@Service
public interface NamePlateTableMenuWithBgService {

	Map<String, Object> addOrUpdateNamePlate(List<NamePlateTableMenuWithBgRequestDto> request,
			Long eventId, Long eventFunctionId, Long userId, Integer catFontSize, Integer itemFontSize,
			String headerNotesEnglish, String headerNotesHindi, String headerNotesGujarati,
			String footerNotesEnglish, String footerNotesHindi, String footerNotesGujarati);

	Map<String, Object> getNameplateItems(Long userId, Long eventId, Long eventFunctionId, Integer lang);

}
