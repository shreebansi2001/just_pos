package com.crmportal.service;

import java.util.List;
import com.crmportal.request.dto.StoreRequisitionRequestDto;
import com.crmportal.response.dto.StoreRequisitionResponseDto;
import com.crmportal.response.dto.CrcodeResponseDto;

public interface StoreRequisitionService {

    StoreRequisitionResponseDto addOrUpdate(StoreRequisitionRequestDto request);

    // Update status — when COMPLETED save to stock_ledger
    StoreRequisitionResponseDto updateStatus(Long crId, String status);

    List<StoreRequisitionResponseDto> getByUser(Long userId);

    StoreRequisitionResponseDto getBySrId(Long crId);

    void deleteBySrId(Long crId);
    
    byte[] generatePdfReport(Long crId, Long userId, Integer isCompanyDetails, Integer lang);
    
    List<CrcodeResponseDto> getAllSrcodes(Long userId);
    
    StoreRequisitionResponseDto getBySrcode(String crcode);
}