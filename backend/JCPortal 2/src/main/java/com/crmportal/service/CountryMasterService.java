package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.CountryMasterRequestDto;
import com.crmportal.response.dto.CountryMasterResponseDto;

@Service
public interface CountryMasterService {

	CountryMasterResponseDto addOrUpdateCountryMaster(@Valid CountryMasterRequestDto request, long parseLong);

	List<CountryMasterResponseDto> getAllCountryMaster(String countryName);

	CountryMasterResponseDto getCountryMasterById(Long id);

	List<CountryMasterResponseDto> getCountryNameWithSearch(String countryName);

	Boolean deleteCountryMasterById(Long id);

}
