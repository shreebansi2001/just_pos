package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.GeneralFixRequestDto;
import com.crmportal.response.dto.GeneralFixRawResponseDto;

@Service
public interface EventFunctionGeneralFixService {

	Boolean addUpdate(GeneralFixRequestDto request);

	GeneralFixRawResponseDto getAllGeneralFixRaw(Long eventId, List<Long> eventFunctionIds, List<Long> rawCatIds);

}
