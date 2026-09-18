package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.FunctionMasterRequestDto;
import com.crmportal.request.dto.ShiftRequestDto;
import com.crmportal.response.dto.FunctionMasterResponseDto;
import com.crmportal.response.dto.ShiftResponseDto;

@Service
public interface ShiftService {

	ShiftResponseDto addOrUpdateShift(@Valid ShiftRequestDto request, long id);

	List<ShiftResponseDto> getAllShiftByUserId(Long userId, String shiftName);

	ShiftResponseDto getShiftById(Long id);

	Boolean deleteShiftById(Long id);

}
