package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventRawMaterialEntity;
import com.crmportal.entity.EventRawMaterialFunctions;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventRawMaterialFunctionsRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.request.dto.EventRawMaterialRequest;
import com.crmportal.request.dto.MenuPreparationRequestDto;
import com.crmportal.response.dto.EventRawMaterialFunctionDeleteDto;
import com.crmportal.response.dto.EventRawMaterialFunctionsDto;
import com.crmportal.response.dto.EventRawMaterialQtySumDto;
import com.crmportal.response.dto.EventRawMaterialResponse;
import com.crmportal.response.dto.MenuPreparationDetailsResponseDto;
import com.crmportal.utility.RoundOffUtility;

@Service
public class MenuPreparationHelperService {
	
	@Autowired
	EventRawMaterialHelperService eventRawMaterialHelperService;
	
	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;
	
	@Autowired
	RoundOffUtility roundOffUtility;
	
	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;
	
	@Autowired
	PartyMasterRepository partyMasterRepository;
	
	@Transactional
    public Map<String, List<Long>> manageEventRawMaterial(
            Long eventId,
            Long eventFunctionId,
            MenuPreparationRequestDto request
    ) {
		
		List<Long> uniqueMenuIdFromRequestList = getUniqueMenuItemIdsFromRequest(request);
		
		List<Long> requestList = Optional.ofNullable(uniqueMenuIdFromRequestList).orElse(Collections.emptyList());
		
		List<Long> dbList = Optional
                .ofNullable(menuPreparationDetailsRepository
                        .findDistinctMenuItemIdsByEventAndEventFunction(
                                eventId, eventFunctionId))
                .orElse(Collections.emptyList());

        Set<Long> dbSet = new HashSet<>(dbList);
        Set<Long> requestSet = new HashSet<>(requestList);
        
        List<Long> tobeDeletedMenuItemIdList = dbSet.stream()
                .filter(id -> !requestSet.contains(id))
                .collect(Collectors.toList());

        List<Long> tobeAddedMenuItemIdList = requestSet.stream()
                .filter(id -> !dbSet.contains(id))
                .collect(Collectors.toList());
        
       
        System.err.println("tobeDeletedMenuItemIdList : "+tobeDeletedMenuItemIdList);
        System.err.println("tobeAddedMenuItemIdList : "+tobeAddedMenuItemIdList);
       
        Map<String, List<Long>> manageData = new HashMap<>();
        manageData.put("delete", tobeDeletedMenuItemIdList);
        manageData.put("add", tobeAddedMenuItemIdList);
        return manageData;
        
    }
	
	public void addDeleteEventRawMaterial(Long eventId,
            Long eventFunctionId, Map<String, List<Long>> manageData) {
		 List<Long> tobeDeletedMenuItemIdList = manageData.get("delete");

	        List<Long> tobeAddedMenuItemIdList = manageData.get("add");
	        
	        if(tobeDeletedMenuItemIdList.size() != 0) {
	        	eventRawMaterialHelperService.deleteEventRawMaterialFunctions(eventId, eventFunctionId, tobeDeletedMenuItemIdList);
	        }
	        
	        if(tobeAddedMenuItemIdList.size() != 0) {
	        	eventRawMaterialHelperService.addEventRawMaterialFunctionsByMenuItemId(eventId, eventFunctionId, tobeAddedMenuItemIdList, false);
	        }
		
	}
	
	public static List<Long> getUniqueMenuItemIdsFromRequest(MenuPreparationRequestDto requestDto) {

	    if (requestDto == null 
	            || requestDto.getSelectedMenuPreparation() == null
	            || requestDto.getSelectedMenuPreparation().isEmpty()) {
	        return Collections.emptyList();
	    }

	    return requestDto.getSelectedMenuPreparation()
	            .stream()
	            .filter(Objects::nonNull)
	            .flatMap(category -> {
	                if (category.getSelectedMenuPreparationItems() == null) {
	                    return Collections.<MenuPreparationDetailsResponseDto>emptyList().stream();
	                }
	                return category.getSelectedMenuPreparationItems().stream();
	            })
	            .filter(Objects::nonNull)
	            .map(MenuPreparationDetailsResponseDto::getMenuItemId)
	            .filter(Objects::nonNull)
	            .distinct()
	            .collect(Collectors.toList());
	}	
}
