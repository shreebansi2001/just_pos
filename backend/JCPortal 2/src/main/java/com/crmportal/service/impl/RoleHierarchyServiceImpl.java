package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.RoleHierarchyEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.RoleHierarchyRepository;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.RoleHierarchyRequestDto;
import com.crmportal.response.dto.RoleHierarchyResponseDto;
import com.crmportal.response.dto.RoleTreeResponseDto;
import com.crmportal.service.RoleHierarchyService;

@Service
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

	@Autowired
	RoleHierarchyRepository roleHierarchyRepository;

	@Autowired
	RoleMasterRepository roleRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Override
	@Transactional
	public Boolean addOrUpdate(List<RoleHierarchyRequestDto> requests) {

		for (RoleHierarchyRequestDto request : requests) {

			RoleMasterEntity parentRole = roleRepository.findByIdAndIsDeleteFalse(request.getParentRoleId())
					.orElseThrow(() -> new RuntimeException("Parent role not found"));

			RoleMasterEntity childRole = roleRepository.findByIdAndIsDeleteFalse(request.getChildRoleId())
					.orElseThrow(() -> new RuntimeException("Child role not found"));

			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));

			validateRequest(parentRole, childRole, user, request.getHierarchyId());

			RoleHierarchyEntity entity;

			if (request.getHierarchyId() != null && request.getHierarchyId() != 0 && request.getHierarchyId() != -1) {

				entity = roleHierarchyRepository.findById(request.getHierarchyId())
						.orElseThrow(() -> new RuntimeException("Hierarchy not found"));

				entity.setUpdatedAt(LocalDateTime.now());

			} else {

				entity = new RoleHierarchyEntity();
				entity.setIsDelete(false);
			}

			entity.setParentRole(parentRole);
			entity.setChildRole(childRole);
			entity.setUser(user);

			roleHierarchyRepository.save(entity);
		}

		return true;
	}

	private void validateRequest(RoleMasterEntity parentRole, RoleMasterEntity childRole, UserMasterEntity user,
			Long hierarchyId) {

		if (parentRole.getId().equals(childRole.getId())) {
			throw new RuntimeException("Parent role and child role cannot be same");
		}

		if (hierarchyId == null) {

			boolean exists = roleHierarchyRepository.existsByParentRoleAndChildRoleAndUserAndIsDeleteFalse(parentRole,
					childRole, user);

			if (exists) {
				throw new RuntimeException("Hierarchy already exists");
			}

		} else {

			boolean exists = roleHierarchyRepository.existsByParentRoleAndChildRoleAndUserAndIsDeleteFalseAndIdNot(
					parentRole, childRole, user, hierarchyId);

			if (exists) {
				throw new RuntimeException("Hierarchy already exists");
			}
		}

		if (isCircularHierarchy(parentRole, childRole, user, hierarchyId)) {

			throw new RuntimeException("Circular hierarchy not allowed");
		}
	}

	private boolean isCircularHierarchy(RoleMasterEntity parentRole, RoleMasterEntity childRole, UserMasterEntity user,
			Long hierarchyId) {

		return hasDescendant(childRole, parentRole, user, hierarchyId);
	}

	private boolean hasDescendant(RoleMasterEntity currentRole, RoleMasterEntity targetRole, UserMasterEntity user,
			Long hierarchyId) {

		List<RoleHierarchyEntity> children = roleHierarchyRepository
				.findByParentRoleAndIsDeleteFalseAndUser(currentRole, user);

		for (RoleHierarchyEntity child : children) {

			if (hierarchyId != null && child.getId().equals(hierarchyId)) {
				continue;
			}

			RoleMasterEntity childRole = child.getChildRole();

			if (childRole.getId().equals(targetRole.getId())) {

				return true;
			}

			if (hasDescendant(childRole, targetRole, user, hierarchyId)) {

				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean deleteById(Long hierarchyId) {
		RoleHierarchyEntity entity = roleHierarchyRepository.findById(hierarchyId)
				.orElseThrow(() -> new RuntimeException("Hierarchy not found"));

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		roleHierarchyRepository.save(entity);

		return true;
	}

	@Override
	public List<RoleHierarchyResponseDto> getChildren(Long roleId, Long userId) {

		RoleMasterEntity role = roleRepository.findByIdAndIsDeleteFalse(roleId)
				.orElseThrow(() -> new RuntimeException("Role not found"));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		return roleHierarchyRepository.findByParentRoleAndIsDeleteFalseAndUser(role, user).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	private RoleHierarchyResponseDto convertToDto(RoleHierarchyEntity entity) {

		RoleHierarchyResponseDto dto = new RoleHierarchyResponseDto();

		dto.setHierarchyId(entity.getId());

		dto.setParentRoleId(entity.getParentRole().getId());
		dto.setParentRoleName(entity.getParentRole().getName());

		dto.setChildRoleId(entity.getChildRole().getId());
		dto.setChildRoleName(entity.getChildRole().getName());

		dto.setUserId(entity.getUser().getId());

		return dto;
	}

	@Override
	public List<RoleHierarchyResponseDto> getHierarchyList(Long userId) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<RoleHierarchyEntity> hierarchies = roleHierarchyRepository.findByUserAndIsDeleteFalse(user);

		return hierarchies.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Override
	public RoleTreeResponseDto getTree(Long roleId, Long userId) {

		RoleMasterEntity role = roleRepository.findByIdAndIsDeleteFalse(roleId)
				.orElseThrow(() -> new RuntimeException("Role not found"));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		return buildTree(role, user);

	}

	private RoleTreeResponseDto buildTree(RoleMasterEntity role, UserMasterEntity user) {

		RoleTreeResponseDto dto = new RoleTreeResponseDto();

		dto.setRoleId(role.getId());
		dto.setRoleName(role.getName());

		List<RoleTreeResponseDto> children = roleHierarchyRepository.findByParentRoleAndIsDeleteFalseAndUser(role, user)
				.stream().map(x -> buildTree(x.getChildRole(), user)).collect(Collectors.toList());

		dto.setChildren(children);

		return dto;
	}
}
