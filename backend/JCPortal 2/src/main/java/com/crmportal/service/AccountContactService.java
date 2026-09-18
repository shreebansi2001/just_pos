package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.AccountContactRequestDto;
import com.crmportal.response.dto.AccountContactResponseDto;

@Service
public interface AccountContactService {

	AccountContactResponseDto addOrUpdateAccountContact(@Valid AccountContactRequestDto request);

	List<AccountContactResponseDto> getAllAccountContactByUserId(Long userId);

	AccountContactResponseDto getAccountContactById(Long accountContactId);

	Boolean deleteAccountContactById(Long accountContactId);

}
