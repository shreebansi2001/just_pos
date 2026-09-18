package com.crmportal.service;

import java.util.List;
import com.crmportal.request.dto.ChefRequisitionRequestDto;
import com.crmportal.response.dto.ChefRequisitionResponseDto;
import com.crmportal.response.dto.CrcodeResponseDto;

public interface ChefRequisitionService {

    ChefRequisitionResponseDto addOrUpdate(ChefRequisitionRequestDto request);

    // Update status — when COMPLETED save to stock_ledger
    ChefRequisitionResponseDto updateStatus(Long crId, String status);

    List<ChefRequisitionResponseDto> getByUser(Long userId);

    ChefRequisitionResponseDto getByCrId(Long crId);

    void deleteByCrId(Long crId);
    
    byte[] generatePdfReport(Long crId, Long userId, Integer isCompanyDetails, Integer lang);
    
    List<CrcodeResponseDto> getAllCrcodes(Long userId);
    
    ChefRequisitionResponseDto getByCrcode(String crcode);
}