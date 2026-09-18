package com.crmportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.crmportal.request.dto.StoreManageRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryChangeResponseDto;
import com.crmportal.response.dto.StoreManageCategoryDto;
import com.crmportal.response.dto.StoreManageResponseDto;


public interface StoreManageService {

    // Load items with closing stock for today's manage screen
    
	Page<StoreManageCategoryDto> loadTodayItems(Long userId, Pageable pageable, Long categoryId, String itemName, Long stockTypeId);

    // Save — creates/updates store manage entry
    StoreManageResponseDto save(StoreManageRequestDto request);

    // Get all entries for a user
    List<StoreManageResponseDto> getAll(Long userId);

    // Get by id
    StoreManageResponseDto getById(Long id);
}