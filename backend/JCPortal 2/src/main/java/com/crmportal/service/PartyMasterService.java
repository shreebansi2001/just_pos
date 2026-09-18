package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.PartyMasterRequestDto;
import com.crmportal.response.dto.PartyMasterResponseDto;

@Service
public interface PartyMasterService {

	PartyMasterResponseDto addOrUpdatePartyMaster(@Valid PartyMasterRequestDto request, long l,MultipartFile file);

	List<PartyMasterResponseDto> getAllPartyMaster();

	PartyMasterResponseDto getPartyMasterById(Long id);

	Boolean deleteById(Long id);

	List<PartyMasterResponseDto> getAllPartyMasterByUserId(Long userId, String partyName);

	List<PartyMasterResponseDto> getPartyNameWithSearch(String partyName,Long userId);

	List<PartyMasterResponseDto> getAllPartyMasterByCatTypeId(Long catTypeId, String partyName, Long userId);

	List<PartyMasterResponseDto> getAllPartyMasterByContCatId(Long contCatId, String partyName, Long userId);
	
	byte[] generatePartyReportPdf(String type, Long userId);
	byte[] generatePartyReportExcel(String type, Long userId);
	
	Long getOrCreatePartyId(Long leadId);

}
