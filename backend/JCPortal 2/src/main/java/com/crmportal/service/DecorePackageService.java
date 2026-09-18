package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.DecorePackageRequestDto;
import com.crmportal.response.dto.DecorePackageResponseDto;

@Service
public interface DecorePackageService {

	DecorePackageResponseDto addOrUpdateDecorePackage(@Valid DecorePackageRequestDto request);

	List<DecorePackageResponseDto> getAllByUserId(Long userId, String name, Boolean isActive);

	DecorePackageResponseDto getById(Long id);

	Boolean deleteById(Long id);

	Boolean updateStatus(Long id, Boolean isActive);

}
