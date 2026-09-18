package com.crmportal.service;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ExclusivePaymentRequestDto;
import com.crmportal.request.dto.ExclusiveThemePaymentRequestDto;
import com.crmportal.response.dto.ExclusiveThemePaymentResponseDto;

@Service
public interface ExclusiveThemePaymentService {

	ExclusiveThemePaymentResponseDto addExclusiveThemePayment(ExclusivePaymentRequestDto request);

}
