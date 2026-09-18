package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.DefaultExtraQuotationFunctionRequestDto;
import com.crmportal.response.dto.DefaultExtraQuotationFunctionResponseDto;

@Service
public interface DefaultExtraQuotationFunctionService {

	Boolean addOrUpdate(DefaultExtraQuotationFunctionRequestDto request);

	List<DefaultExtraQuotationFunctionResponseDto> getAll(Long userId, Boolean isActive);

	Boolean deleteById(Long id);

}
