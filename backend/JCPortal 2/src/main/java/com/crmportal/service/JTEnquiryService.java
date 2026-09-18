package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.enums.JTEnquiryServiceType;
import com.crmportal.request.dto.JTEnquiryRequestDto;
import com.crmportal.response.dto.JTEnquiryResponseDto;

@Service
public interface JTEnquiryService {

	JTEnquiryResponseDto addOrUpdateEnquiry(@Valid JTEnquiryRequestDto request);

	List<JTEnquiryResponseDto> getAllEnquiry(String startDate, String endDate, JTEnquiryServiceType type);

	JTEnquiryResponseDto getEnquiryById(Long id);

}
