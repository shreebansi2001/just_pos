package com.crmportal.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.crmportal.request.dto.FollowUpDetailsRequestDto;
import com.crmportal.request.dto.LeadMasterRequestDto;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.AssignMemberResponseDTO;
import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.response.dto.FollowUpResponseDto;

@Service
public interface LeadMasterService {

	LeadMasterResponseDto addOrUpdateLeadMaster(@Valid LeadMasterRequestDto request, Long id);

	LeadMasterResponseDto getLeadById(Long id);

	Boolean deleteLeadById(Long id);

	Map<String, Object> getAllLeades(Long assignId, Long userId);

	String generateNewLeadCode(Long userId);

	List<FollowUpDetailsResponseDto> getFilteredFolloUpDetails(String startDate, String endDate, Long leadId, Boolean isCreated);

	List<LeadMasterResponseDto> getLeadesByLeadType(String leadType, Long userId);

	List<LeadMasterResponseDto> getLeadesByLeadStatus(String leadStatus, Long userId);

	List<LeadMasterResponseDto> getLeadesByLeadAssignedId(Long leadAssignedId, Long userId);

	Boolean deleteFollowUpDetailsById(Long id);

	List<LeadMasterResponseDto> assignMultipleLeadToMember(List<Long> leadIds, Long memberId, String description, String closeDate);
	
	Map<String, Long> getCountLeadByLeadType(Long userId);

	AssignMemberResponseDTO getAllFollowUpByMemberId(Long memberId, Long userId);

	Map<String, Long> getCountLeadByLeadStatus(Long userId);

	Boolean changeLeadStage(Long leadId, String stageType, Long stageId, String remark,
			FollowUpDetailsRequestDto requestDto, Long assignId);

	Boolean addOrUpdateFollowUp(List<FollowUpDetailsRequestDto> followUpDetails);
	
	String changeLeadStatus(List<Long> leadIds,Long leadStatusId);
	
	List<LeadMasterResponseDto> searchLeads(Long statusId, String priority, Long sourceId,Long userId);
}
