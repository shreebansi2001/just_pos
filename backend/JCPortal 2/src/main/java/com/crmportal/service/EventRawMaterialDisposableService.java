package com.crmportal.service;

import java.util.List;

import com.crmportal.request.dto.EventRawMaterialDisposableRequestDto;
import com.crmportal.response.dto.EventRawMaterialDisposableResponseDto;

public interface EventRawMaterialDisposableService {
	EventRawMaterialDisposableResponseDto getOrLoad(Long eventId, Long userId, Long rawMatCatId,Long qty, Integer isAllItems);

	EventRawMaterialDisposableResponseDto save(EventRawMaterialDisposableRequestDto request);
	
	byte[] generatePdfReport(Long eventId, Long userId, Integer isCompanyDetails, Integer isWithPrice, Integer lang,
			Integer isAllItems, Long rawCategoryId, Integer isWithImage, Integer isTwoColumns);
}