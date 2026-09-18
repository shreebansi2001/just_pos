package com.crmportal.service;

import java.util.List;
import com.crmportal.request.dto.JournalVoucherRequestDto;
import com.crmportal.response.dto.JournalVoucherResponseDto;

public interface JournalVoucherService {
    JournalVoucherResponseDto addOrUpdate(JournalVoucherRequestDto request);
    List<JournalVoucherResponseDto> getByUser(Long userId);
    JournalVoucherResponseDto getById(Long id);
    Boolean delete(Long id);
    byte[] generatePdfReport(Long voucherId, Long userId, Integer isCompanyDetails);
}