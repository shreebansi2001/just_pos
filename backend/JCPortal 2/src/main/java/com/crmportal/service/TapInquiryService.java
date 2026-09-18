package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.TapInquiryRequestDto;
import com.crmportal.response.dto.TapInquiryResponseDto;

@Service
public interface TapInquiryService {

	Boolean addTapInquiry(TapInquiryRequestDto request);

	List<TapInquiryResponseDto> getAllInquiry();

}
