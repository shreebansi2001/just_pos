package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.SpecialNotesRequestDto;
import com.crmportal.response.dto.SpecialNotesResponseDto;

@Service
public interface SpecialNotesService {

	Boolean addOrUpdate(SpecialNotesRequestDto request);

	List<SpecialNotesResponseDto> getSpecialNotesByEventFunction(Long eventFunctionId, Long managerId, Long userId);

	Boolean deleteSpecialNotesImage(Long id);

}
 