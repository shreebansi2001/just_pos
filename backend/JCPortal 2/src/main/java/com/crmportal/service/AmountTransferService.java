package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.AmountTransferRequestDto;
import com.crmportal.response.dto.AmountTransferResponseDto;

@Service
public interface AmountTransferService {

	AmountTransferResponseDto addUpdateAmountTransfer(AmountTransferRequestDto request);

	List<AmountTransferResponseDto> getAllTransfer(String startDate, String endDate, Long userId);

	AmountTransferResponseDto getTransferById(Long id, Long userId);

	Boolean deleteTransferById(Long id);
}
