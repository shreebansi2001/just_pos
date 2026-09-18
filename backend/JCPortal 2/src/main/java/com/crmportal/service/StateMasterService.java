package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.StateMasterRequestDto;
import com.crmportal.response.dto.StateMasterResponseDto;

@Service
public interface StateMasterService {

	StateMasterResponseDto addOrUpdateStateMaster(StateMasterRequestDto request, long id);

	List<StateMasterResponseDto> getAllStateMaster(String stateName);

	StateMasterResponseDto getStateMasterById(Long id);

	List<StateMasterResponseDto> getStateNameWithSearch(String stateName);

	Boolean deleteStateMasterById(Long id);

	List<StateMasterResponseDto> getStateMasterByCountryId(Long countryId, String stateName);

}
