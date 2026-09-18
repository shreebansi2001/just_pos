package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.CustomPackageReportRequestDto;
import com.crmportal.request.dto.CustomPackageRequestDto;
import com.crmportal.response.dto.CustomPackageResponseDto;

@Service
public interface CustomPackageService {

	CustomPackageResponseDto addOrUpdateCustomPackage(@Valid CustomPackageRequestDto request, Long valueOf);

	List<CustomPackageResponseDto> getAllCustomPackageByUserId(Long userId, String packageName, Boolean isActive);

	Boolean deleteById(Long id);

	Boolean updateStatus(Long id, Boolean isActive);

	CustomPackageResponseDto getCustomPackageById(Long id);
	
	String generateCustomPackageReport(CustomPackageReportRequestDto request,
	        HttpServletRequest re);

}
