package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.InquiryRequestDto;
import com.crmportal.response.dto.InquiryResponseDto;

@Service
public interface InquiryService {

	InquiryResponseDto addOrUpdateInquiry(InquiryRequestDto request);

	Boolean deleteInquiryById(Long id, Long userId);

	List<InquiryResponseDto> getAllInquiry(Long userId, String startDate, String endDate);

	List<InquiryResponseDto> getInquiryById(Long userId, Long id);

}
