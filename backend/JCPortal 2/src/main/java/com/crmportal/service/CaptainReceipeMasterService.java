package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.CaptainReceipeMasterRequestDto;
import com.crmportal.response.dto.CaptainReceipeMasterResponseDto;

@Service
public interface CaptainReceipeMasterService {

	CaptainReceipeMasterResponseDto addOrUpdateCaptainReceipeMaster(@Valid CaptainReceipeMasterRequestDto request);

	List<CaptainReceipeMasterResponseDto> getAllCaptainReceipeByUserId(Long userId, Boolean status);

	CaptainReceipeMasterResponseDto getCaptainReceipeById(Long id, Boolean isSync);

	Boolean deleteCaptainReceipeById(Long id);

	Boolean updateCaptainReceipeStatusById(Long id, Boolean status);

	Boolean syncAllCaptainReceipeRawMaterial(Long userId);

}
