package com.crmportal.service;

import java.util.List;
import java.util.Map;

import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;

public interface StoreOrderingTicketService {

    // Generate SOT from event raw material
    SotResponseDto generateSot(GenerateSotRequestDto request);

    // Accept SOT — saves accepted qty, adds to stock_ledger as STORE_ISSUE
    SotResponseDto acceptSot(AcceptSotRequestDto request);

    // Generate Auto/Manual PO agency wise
    List<SotManualPoResponseDto> generateManualPo(Long sotId, Long userId);

    // Get Manual PO list by SOT
    List<SotManualPoResponseDto> getManualPoBySot(Long userId);

    // Get Manual PO details by PO id
    SotManualPoResponseDto getManualPoById(Long sotPoId);

    // Generate Purchase Invoice — creates purchase in purchase module
    void generatePurchaseInvoice(GeneratePurchaseInvoiceRequestDto request);

    // Return SOT — saves return qty, adds to stock_ledger as STORE_ISSUE_RETURN
    SotResponseDto returnSot(ReturnSotRequestDto request);

    // Get all SOTs
    List<SotListResponseDto> getAll(Long userId);

    // Get SOT by id
    SotResponseDto getById(Long sotId);

    // Get SOTs by event
    List<SotResponseDto> getByEvent(Long eventId);

    // Delete SOT
    void deleteSot(Long sotId);

    // Store Report — get SOT details for report
    SotResponseDto getStoreReport(Long sotId);
    
    void deleteManualPo(Long sotPoId);
    
    SotResponseDto updateSotDetails(UpdateSotDetailsRequestDto request);
    
    byte[] generatePdfReport(Long sotId, Long userId, Integer isCompanyDetails);
    
    // Get info fields from sot_manual_po row — shown in Info modal. 
    SotPoInfoResponseDto getManualPoInfo(Long sotPoId);
 
    // Save (update) info fields on sot_manual_po row — Save Changes in modal.
    SotPoInfoResponseDto saveManualPoInfo(SotPoInfoRequestDto request);
 
    // Generate PDF report for a Manual PO — triggered by Print button.
    byte[] generateManualPoPdf(Long sotPoId, Long userId, Integer isCompanyDetails);
    
    SotManualPoResponseDto addOrUpdateManualPo(SotManualPoRequestDto request);

	boolean checkSotExists(Long eventId, Long userId);

	List<MultipleSotResponseDto> getMultipleSotData(List<Long> sotIds, Long userId);

	List<SotResponseDto> addUpdateMultipleSotData(List<MultipleSotResponseDto> request);

	Boolean deleteSotDetail(List<Long> sotDetailIds);

}