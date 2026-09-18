package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.DecorePreparationRequestDto;
import com.crmportal.request.dto.EventFunctionDecorItemImagesRequestDto;
import com.crmportal.response.dto.DecorePreparationCombResponseDto;
import com.crmportal.response.dto.DecorePreparationResponseDto;

@Service
public interface DecorePreparationService {

	DecorePreparationResponseDto addOrUpdateDecorePreparation(@Valid DecorePreparationRequestDto request);

	DecorePreparationCombResponseDto getDecorePreparationItems(Integer pageNo, Integer totalRecord,
			Long decoreCategoryId, Long eventFunctionId, String itemName, Long userId);

	Boolean deleteDecorePreparationItem(Long decorePreparationId, Long decoreCategoryId, Long itemId);

	Boolean copyEventFunctionDecore(Long oldEventFunctionId, Long activeEventFunctionId);

	List<String> uploadDecorImages(EventFunctionDecorItemImagesRequestDto request);

}
