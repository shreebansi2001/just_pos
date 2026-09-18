package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.CityMasterRequestDto;
import com.crmportal.response.dto.CityMasterResponseDto;

@Service
public interface CityMasterService {

	CityMasterResponseDto addOrUpdateCityMaster(@Valid CityMasterRequestDto request, long parseLong);

	List<CityMasterResponseDto> getAllCityMaster();

	CityMasterResponseDto getCityMasterById(Long id);

	List<CityMasterResponseDto> getCityNameWithSearch(String cityName);

	Boolean deleteCityMasterById(Long id);

	List<CityMasterResponseDto> getCityMasterByStateId(Long stateId, String cityName);

}
