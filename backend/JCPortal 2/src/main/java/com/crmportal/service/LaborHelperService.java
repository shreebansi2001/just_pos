package com.crmportal.service;

import java.util.List;

import com.crmportal.request.dto.LaborHelperRequestDto;
import com.crmportal.response.dto.EventLaborHelperSelectionResponseDto;
import com.crmportal.response.dto.LaborHelperResponseDto;

public interface LaborHelperService {

    LaborHelperResponseDto addOrUpdateLaborHelper(LaborHelperRequestDto request, Long id);

    List<LaborHelperResponseDto> getAllLaborHelpersByUserId(Long userId);

    List<LaborHelperResponseDto> getAllLaborHelpersByPartyId(Long partyId);

    List<LaborHelperResponseDto> getAllLaborHelpersByContactCategoryId(Long contactCategoryId);

    LaborHelperResponseDto getLaborHelperById(Long id);

    boolean deleteLaborHelperById(Long id);
    
    List<EventLaborHelperSelectionResponseDto> getAllLaborHelpersByPartyAndEventDetails(
            Long partyId, Long eventId, Long eventFunctionId);
    
    byte[] generateLaborHelperReport(Long poId, Long userId);
}