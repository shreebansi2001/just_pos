package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.CrockeryCutleryRequestDto;
import com.crmportal.response.dto.CrockeryCutleryResponseDto;

@Service
public interface CrockeryCutleryService {

	List<CrockeryCutleryResponseDto> getByRawMaterialCatId(Long rawMaterialCatId, Long userId);

	List<CrockeryCutleryResponseDto> addUpdateCrockeryCutlery(List<CrockeryCutleryRequestDto> crockeryCutleries);

}
