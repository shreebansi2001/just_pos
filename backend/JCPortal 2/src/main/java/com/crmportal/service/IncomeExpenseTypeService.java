package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.IncomeExpenseTypeRequestDto;
import com.crmportal.response.dto.IncomeExpenseTypeResponseDto;

@Service
public interface IncomeExpenseTypeService {

	IncomeExpenseTypeResponseDto addUpdateIncomeExpenseType(@Valid IncomeExpenseTypeRequestDto request);

	List<IncomeExpenseTypeResponseDto> getAllByUserId(Long userId, String type);

	IncomeExpenseTypeResponseDto getById(Long id);

	Boolean deleteById(Long id);

}
