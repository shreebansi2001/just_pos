package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ExtraPaymentRequestDto;
import com.crmportal.response.dto.ExtraPaymentMasterResponseDto;

@Service
public interface ExtraPaymentMasterService {

	Boolean addOrUpdate(@Valid ExtraPaymentRequestDto request);

	List<ExtraPaymentMasterResponseDto> getAll();

	ExtraPaymentMasterResponseDto getById(Long id);

	Boolean deleteById(Long id);

}
