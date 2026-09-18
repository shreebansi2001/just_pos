package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.HallPackageRateRequestDto;
import com.crmportal.response.dto.HallPackagePriceResponseDto;
import com.crmportal.response.dto.HallPackageRateResponseDto;

@Service
public interface HallPackageRateService {

	Boolean addOrUpdate(HallPackageRateRequestDto request);

	List<HallPackageRateResponseDto> getAll(Long userId, Boolean isActive, Long hallId, Long packageId);

	HallPackagePriceResponseDto getPrice(Long hallId, Long packageId, Integer functionPax);

	Boolean delete(Long id);

	Boolean updateStatus(Long id, Boolean isActive);

}
