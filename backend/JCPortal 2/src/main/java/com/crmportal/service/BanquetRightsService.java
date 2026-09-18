package com.crmportal.service;

import java.util.List;

import com.crmportal.request.dto.BanquetRightsRequestDto;
import com.crmportal.response.dto.BanquetRightsResponseDto;

public interface BanquetRightsService {

	List<BanquetRightsResponseDto> add(List<BanquetRightsRequestDto> request, Long memberId);

	List<BanquetRightsResponseDto> getByUser(Long userId, Long memberId);

	Boolean delete(Long id);
}