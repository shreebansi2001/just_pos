package com.crmportal.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.request.dto.AccountEntryRequestDto;
import com.crmportal.response.dto.AccountEntryResponseDto;

@Service
public interface AccountEntryService {

	AccountEntryResponseDto addOrUpdateEntry(@Valid AccountEntryRequestDto request, Long id);

	List<AccountEntryResponseDto> getAllByType(EntryType entryType, AccountType accountType, PaymentMode paymentMode,
			Long cashTypeId, Long bankAccountId, String startDate, String endDate, Long userId);

	AccountEntryResponseDto getById(Long accountEntryId);

	Boolean deleteById(Long accountEntryId);

	String generateVoucherNo(EntryType entryType, AccountType accountType, Long userId);

}
