package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ContactCategoryMasterRequestDto;
import com.crmportal.response.dto.ContactCategoryMasterResponseDto;

@Service
public interface ContactCategoryMasterService {

	ContactCategoryMasterResponseDto addOrUpdateContactCategoryMaster(@Valid ContactCategoryMasterRequestDto request,
			long parseLong);

	ContactCategoryMasterResponseDto getContactCategoryById(Long id);

	Boolean deleteContactCategoryById(Long id);

	List<ContactCategoryMasterResponseDto> getContactCategoryWithSearch(String categoryName);

	List<ContactCategoryMasterResponseDto> getAllContactCategory(String categoryName, Long userId);

	boolean updateContcatCategoryStatus(Long id, Boolean isActive);

	List<ContactCategoryMasterResponseDto> getAllContactCategoryByCatAndUserId(String categoryName, Long userId,
			Long conCatId);

}
