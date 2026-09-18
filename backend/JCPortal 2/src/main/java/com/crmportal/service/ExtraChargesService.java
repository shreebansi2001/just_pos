package com.crmportal.service;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ExtraChargesSaveRequestDto;
import com.crmportal.response.dto.ExtraChargesResponseDto;

@Service
public interface ExtraChargesService {

    ExtraChargesResponseDto saveOrUpdateExtraCharges(ExtraChargesSaveRequestDto request);

    ExtraChargesResponseDto getExtraCharges(Long eventId, Long eventFunctionId, Long userId);

    Boolean deleteHeading(Long headingId);

    Boolean deleteRow(Long rowId);
}