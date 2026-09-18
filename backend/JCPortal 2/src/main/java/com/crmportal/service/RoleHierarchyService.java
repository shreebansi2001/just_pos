package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.RoleHierarchyRequestDto;
import com.crmportal.response.dto.RoleHierarchyResponseDto;
import com.crmportal.response.dto.RoleTreeResponseDto;

@Service
public interface RoleHierarchyService {

	Boolean addOrUpdate(List<RoleHierarchyRequestDto> requests);

	Boolean deleteById(Long hierarchyId);

	List<RoleHierarchyResponseDto> getChildren(Long roleId, Long userId);

	List<RoleHierarchyResponseDto> getHierarchyList(Long userId);

	RoleTreeResponseDto getTree(Long roleId, Long userId);

}
