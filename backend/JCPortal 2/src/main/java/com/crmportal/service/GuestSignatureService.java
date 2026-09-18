package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.GuestSignatureRequestDto;
import com.crmportal.response.dto.GuestSignatureResponseDto;

@Service
public interface GuestSignatureService {

	List<GuestSignatureResponseDto> addOrUpdateGuestSignature(@Valid List<GuestSignatureRequestDto> request, Long eventId, Long eventFunctionId);

	List<GuestSignatureResponseDto> getAllByEventAndEventFunction(Long eventId, Long eventFunctionId);

}
