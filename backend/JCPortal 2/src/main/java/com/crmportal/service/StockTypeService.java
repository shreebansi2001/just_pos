package com.crmportal.service;


import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.StockTypeRequestDto;
import com.crmportal.response.dto.StockTypeResponseDto;
import com.crmportal.response.dto.StockTypeRightsDTO;

@Service
public interface StockTypeService {

	StockTypeResponseDto addOrUpdateStockType(@Valid StockTypeRequestDto request, Long id);

	List<StockTypeResponseDto> getAllStockTypeByUserId(Long userId, Boolean isActive, Integer mainType);

	StockTypeResponseDto getStockTypeById(Long id);

	Boolean deleteStockTypeById(Long id);

	boolean updateStockTypeStatus(Long id, Boolean isActive);

	Boolean stockTypeRights(Long userId, List<Long> stockTypeIds);

	List<StockTypeRightsDTO> getAllStockTypeRights(Long userId);
}