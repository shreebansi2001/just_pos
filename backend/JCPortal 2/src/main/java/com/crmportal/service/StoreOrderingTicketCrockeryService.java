package com.crmportal.service;

import java.util.List;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;

public interface StoreOrderingTicketCrockeryService {

    // Generate SOT from event raw material
    SotResponseDto generateSot(GenerateSotRequestDto request);

    // Accept SOT — saves accepted qty, adds to stock_ledger as STORE_ISSUE
    SotResponseDto acceptSot(AcceptSotRequestDto request);

    // Return SOT — saves return qty, adds to stock_ledger as STORE_ISSUE_RETURN
    SotResponseDto returnSot(ReturnSotRequestDto request);

    // Get all SOTs
    List<SotResponseDto> getAll(Long userId);

    // Get SOT by id
    SotResponseDto getById(Long sotId);

    // Get SOTs by event
    List<SotResponseDto> getByEvent(Long eventId);

    // Delete SOT
    void deleteSot(Long sotId);

    // Store Report — get SOT details for report
    SotResponseDto getStoreReport(Long sotId);
    
    SotResponseDto updateSotDetails(UpdateSotDetailsRequestDto request);
    
    byte[] generatePdfReport(Long sotId, Long userId, Integer isCompanyDetails);
    
    boolean checkSotExists(Long eventId, Long userId);
    
    // Generate Auto/Manual PO agency wise
    List<SotManualPoResponseDto> generateManualPo(Long sotId, Long userId);

}