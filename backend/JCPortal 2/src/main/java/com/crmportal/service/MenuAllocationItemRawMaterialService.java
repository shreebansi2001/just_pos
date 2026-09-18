package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFunctionMenuItemRawMaterialRequestDto;

@Service
public interface MenuAllocationItemRawMaterialService {

	Boolean addOrUpdateMenuItemRawMaterial(@Valid List<EventFunctionMenuItemRawMaterialRequestDto> requests);

	Boolean deleteMenuItemRawMaterialById(Long id, Long menuItemId, Long eventId, Long eventFunctionId);

}
