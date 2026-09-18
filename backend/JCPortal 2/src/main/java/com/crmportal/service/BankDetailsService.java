package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.BankDetailsRequestDto;
import com.crmportal.response.dto.BankDetailsResponseDto;

@Service
public interface BankDetailsService {

	BankDetailsResponseDto addUpdateBankDetails(BankDetailsRequestDto request);

	List<BankDetailsResponseDto> getBankDetailsByUserId(Long userId);

	List<BankDetailsResponseDto> getBankDetails(Long id);

	Boolean deleteBankAccountById(Long bankAccountId);

}
