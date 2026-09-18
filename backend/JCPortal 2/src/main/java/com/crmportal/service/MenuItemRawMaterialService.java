package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UpdateItemRawMaterialWeightRequestDto;
import com.crmportal.request.dto.UpdateMisMatchedUnitsRequestDto;
import com.crmportal.response.dto.ItemRawMaterialsResponseDto;
import com.crmportal.response.dto.MenuItemCaptainReceipeResponseDto;
import com.crmportal.response.dto.MenuItemRawMaterialsResponseDto;
import com.crmportal.response.dto.WrongItemRawMaterialResponseDto;

@Service
public interface MenuItemRawMaterialService {

	List<MenuItemRawMaterialsResponseDto> getMenuItemRawMaterialByMenuId(Long menuItemId, Long userId, Boolean isSync);

	List<WrongItemRawMaterialResponseDto> getMismatchedUnitsByUserId(Long userId);

	Boolean updateMisMatchedUnits(List<UpdateMisMatchedUnitsRequestDto> request);

	Boolean syncAllItemRawMaterialRate(Long userId);

	List<ItemRawMaterialsResponseDto> getItemRawMaterialByRawMaterialId(Long rawMaterialId, Long userId);

	Boolean updateItemRawMaterialWeight(List<UpdateItemRawMaterialWeightRequestDto> request);

	Boolean syncAllCaptainReceipeRate(Long userId);

	List<MenuItemCaptainReceipeResponseDto> getMenuItemCaptainReceipeByMenuId(Long menuItemId, Long userId,
			Boolean isSync);

}
