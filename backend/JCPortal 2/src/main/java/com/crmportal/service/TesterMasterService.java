package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.TesterMasterRequestDto;
import com.crmportal.response.dto.TesterMasterResponseDto;

@Service
public interface TesterMasterService {

	TesterMasterResponseDto addOrUpdateTesterMaster(@Valid TesterMasterRequestDto request);

	List<TesterMasterResponseDto> getAllTesterMaster(Long userId);

	TesterMasterResponseDto getTesterMasterById(Long id);

	Boolean deleteTesterMasterById(Long id);

}
