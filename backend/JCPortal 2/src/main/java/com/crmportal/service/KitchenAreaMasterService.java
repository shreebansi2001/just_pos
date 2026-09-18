package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.KitchenAreaMasterRequestDto;
import com.crmportal.response.dto.KitchenAreaMasterResponseDto;

@Service
public interface KitchenAreaMasterService {

	KitchenAreaMasterResponseDto addOrUpdateKitchenAreaMaster(@Valid KitchenAreaMasterRequestDto request, long l);

	List<KitchenAreaMasterResponseDto> getAllKitchenAreasByUserId(Long userId, String kitchenAreaName);

	KitchenAreaMasterResponseDto getKitchenAreaById(Long id);

	Boolean deleteKitchenAreaById(Long id);

	boolean updateKitchenAreaStatus(Long id, Boolean isActive);

}
