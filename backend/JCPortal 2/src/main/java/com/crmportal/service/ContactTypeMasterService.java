package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ContactTypeMasterRequestDto;
import com.crmportal.response.dto.ContactTypeMasterResponseDto;

@Service
public interface ContactTypeMasterService {

	ContactTypeMasterResponseDto addOrUpdateContactTypeMaster(@Valid ContactTypeMasterRequestDto request, Long valueOf);

	List<ContactTypeMasterResponseDto> getAllContactTypeByUserId(Long userId, String contactTypeName, Boolean isActive);

	ContactTypeMasterResponseDto getContactTypeById(Long id);

	Boolean deleteContactTypeById(Long id);

	boolean updateContcatTypeStatus(Long id, Boolean isActive);

}
