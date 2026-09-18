package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserRightsMasterEntity;
import com.crmportal.entity.UserRightsModuleEntity;
import com.crmportal.entity.UserRightsPagesEntity;
import com.crmportal.repository.ModuleRightsRepository;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserRightsMasterRepository;
import com.crmportal.repository.UserRightsPagesRepository;
import com.crmportal.request.dto.UserRightsMasterRequestDto;
import com.crmportal.request.dto.UserRightsPagesRequestDto;
import com.crmportal.response.dto.ModuleWiseUserRightsPagesResponseDto;
import com.crmportal.response.dto.UserRightsMasterResponseDto;
import com.crmportal.response.dto.UserRightsPageWithModuleResponseDto;
import com.crmportal.response.dto.UserRightsPagesResponseDto;
import com.crmportal.response.dto.UserRightsRequest;
import com.crmportal.service.CommonService;
import com.crmportal.service.UserRightsService;

@Service
public class UserRightsServiceImpl implements UserRightsService {

	@Autowired
	private UserRightsPagesRepository userRightsPagesRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	private UserRightsMasterRepository userRightsMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	RoleMasterRepository roleMasterRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	ModuleRightsRepository moduleRightsRepository;

	public List<ModuleWiseUserRightsPagesResponseDto> getActivePages(Boolean isAdminPages, Boolean isCombine) {

		List<UserRightsPagesEntity> entityList = new ArrayList<>();

		if (isCombine) {
			entityList = userRightsPagesRepository.findAllByIsDeleteFalseAndIsActiveTrue();
		} else {
			entityList = userRightsPagesRepository.findAllByIsDeleteFalseAndIsActiveTrue(isAdminPages);
		}

		Map<Long, List<UserRightsPagesEntity>> groupedByModule = entityList.stream()
				.collect(Collectors.groupingBy(UserRightsPagesEntity::getModuleId));

		return groupedByModule.entrySet().stream().map(entry -> {

			Long moduleId = entry.getKey();
			List<UserRightsPagesResponseDto> pages = entry.getValue().stream()
					.map(e -> new UserRightsPagesResponseDto(e.getId(), e.getPagename())).collect(Collectors.toList());

			return new ModuleWiseUserRightsPagesResponseDto(moduleId, getModuleName(moduleId), pages);
		}).collect(Collectors.toList());
	}

	private String getModuleName(Long moduleId) {
		String moduleName = moduleRightsRepository.findByIdAndIsDeleteFalse(moduleId).get().getName();
		return moduleName;
	}

	public UserRightsPagesResponseDto addOrUpdateUserRightsPage(UserRightsPagesRequestDto request, Long id) {

		UserRightsPagesEntity entity;

		if (id == -1) {
			entity = new UserRightsPagesEntity();
		} else {
			entity = userRightsPagesRepository.findById(id).orElse(null);
			if (entity == null) {
				throw new RuntimeException("Page not found!");
			}
		}

		entity.setPagename(request.getPagename());
		entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
		entity.setIsDelete(false);
		entity.setModuleId(request.getModuleId());

		UserRightsPagesEntity saved = userRightsPagesRepository.save(entity);

		return new UserRightsPagesResponseDto(saved.getId(), saved.getPagename());
	}

	public void updateUserRights(UserRightsRequest request) {

		Long roleId = request.getRoleId();

		RoleMasterEntity role = roleMasterRepository.findByIdAndIsDeleteFalse(roleId)
				.orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

		List<UserRightsMasterRequestDto> list = request.getRightsList();

		if (list == null || list.isEmpty()) {
			throw new RuntimeException("No rights provided!");
		}

		// Step 1: Delete all old rights
		userRightsMasterRepository.deleteAllByRoleid(roleId);

		// Step 2: Insert new rights
		List<UserRightsMasterEntity> entities = new ArrayList<>();

		for (UserRightsMasterRequestDto dto : list) {

			UserRightsMasterEntity entity = new UserRightsMasterEntity();
			entity.setAddaccess(dto.getAdd());
			entity.setEditaccess(dto.getEdit());
			entity.setDeleteaccess(dto.getDelete());
			entity.setViewaccess(dto.getView());
			entity.setPageid(dto.getPageid());
			entity.setRoleid(roleId);
			entity.setModuleId(dto.getModuleId());
			entities.add(entity);
		}

		userRightsMasterRepository.saveAll(entities);
	}

	/**
	 * Get rights for user
	 */
	public List<UserRightsPageWithModuleResponseDto> getRightsByRoleId(Long roleId) {

		return getUserRightsByRoleId(roleId);
	}

	public List<UserRightsPageWithModuleResponseDto> getUserRightsByRoleId(Long roleId) {
		System.err.println("RoleID" + roleId);
		List<UserRightsMasterEntity> list = userRightsMasterRepository.findAllByRoleid(roleId);
		System.err.println("list:-" + list.size());
		// Group by moduleId
		Map<Long, List<UserRightsMasterEntity>> groupedByModule = list.stream()
				.collect(Collectors.groupingBy(UserRightsMasterEntity::getModuleId));

		List<UserRightsPageWithModuleResponseDto> finalResponse = new ArrayList<>();

		for (Map.Entry<Long, List<UserRightsMasterEntity>> entry : groupedByModule.entrySet()) {

			Long moduleId = entry.getKey();
			List<UserRightsMasterEntity> rightsList = entry.getValue();
			System.err.println("moduleId:-" + moduleId);
			// Fetch module name (assuming you have module repository)
			String moduleName = moduleRightsRepository.findById(moduleId).map(UserRightsModuleEntity::getName)
					.orElse("Unknown Module");
			System.err.println("rightsList:-" + rightsList);
			// Convert each right inside module to DTO
			List<UserRightsMasterResponseDto> pageRightsList = rightsList.stream().map(e -> {

				System.out.println("e.getId():-" + e.getId() + " e.getPageid():-" + e.getPageid());

				return new UserRightsMasterResponseDto(e.getId(), e.getPageid(),
						userRightsPagesRepository.findById(e.getPageid()).get().getPagename(), e.getAddaccess(),
						e.getEditaccess(), e.getDeleteaccess(), e.getViewaccess());
			}).collect(Collectors.toList());

			System.err.println("outttt");

			// Add final module + pages structure
			finalResponse.add(new UserRightsPageWithModuleResponseDto(moduleId, moduleName, pageRightsList));
		}

		return finalResponse;
	}

	public UserRightsResponse getRightsByUserId(Long userId) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		UserBasicDetailsMasterEntity basicDetails = userBasicDetailsMasterRepository.findByUserAndIsDeleteFalse(user)
				.orElseThrow(() -> new RuntimeException("User basic details not found"));

		if (basicDetails.getRole() == null) {
			return null;
		}

		List<UserRightsPageWithModuleResponseDto> dtoList = getUserRightsByRoleId(basicDetails.getRole().getId());

		UserRightsResponse res = new UserRightsResponse(basicDetails.getRole().getId(),
				basicDetails.getRole().getName(), dtoList);

		return res;
	}
	
	@Override
	@Transactional
	public void deleteUserRightsPage(Long pageId) {

	    UserRightsPagesEntity entity = userRightsPagesRepository.findById(pageId)
	            .orElseThrow(() -> new RuntimeException("User Rights Page not found with id: " + pageId));

	    if (Boolean.TRUE.equals(entity.getIsDelete())) {
	        throw new RuntimeException("User Rights Page already deleted");
	    }

	    entity.setIsDelete(true);
	    entity.setIsActive(false);

	    userRightsPagesRepository.save(entity);

	    userRightsMasterRepository.deleteAllByPageid(pageId);
	}
}
