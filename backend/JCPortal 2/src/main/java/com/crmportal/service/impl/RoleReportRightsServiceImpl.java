package com.crmportal.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.repository.AdminTemplateModuleRepository;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.RoleReportRightsRepository;
import com.crmportal.request.dto.SaveRoleReportRightsRequestDto;
import com.crmportal.response.dto.RoleReportRightsResponseDto;
import com.crmportal.response.dto.RoleReportRightsResponseDto.*;
import com.crmportal.service.RoleReportRightsService;

@Service
public class RoleReportRightsServiceImpl implements RoleReportRightsService {

    @Autowired
    private RoleReportRightsRepository rightsRepository;

    @Autowired
    private RoleMasterRepository roleMasterRepository;

    @Autowired
    private AdminTemplateModuleRepository adminTemplateModuleRepository;

    @Autowired
    private RoleReportRightsPersistService persistService;

    // =========================================================================
    // GET — fetches all admin_template_module rows for the given userId
    //       (used in Report Rights modal — shows what reports exist for that user)
    // =========================================================================
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public RoleReportRightsResponseDto getReportRights(Long roleId, Long userId) {

        RoleMasterEntity role = roleMasterRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

        // Fetch atm rows scoped to this userId (for the modal — shows user's templates)
        List<AdminTemplateModuleEntity> atmList =
                adminTemplateModuleRepository
                        .findByUserIdAndIsDeleteFalseAndIsActiveTrue(userId);

        return buildResponse(role, atmList, roleId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public RoleReportRightsResponseDto getReportRightsByRoleAndOwner(Long roleId, Long ownerId) {

        RoleMasterEntity role = roleMasterRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

        // ATM rows belong to the admin/owner, NOT the individual user logging in
        List<AdminTemplateModuleEntity> atmList =
                adminTemplateModuleRepository.findByUserIdAndIsDeleteFalseAndIsActiveTrue(ownerId);

        return buildResponse(role, atmList, roleId);
    }

 // =========================================================================
 // GET BY ROLE — fetches ATM rows belonging to the admin/owner user
//                (used in login API — role-level access control)
 // =========================================================================
 @Override
 @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
 public RoleReportRightsResponseDto getReportRightsByRole(Long roleId) {

     RoleMasterEntity role = roleMasterRepository.findById(roleId)
             .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

     // Fetch ATM rows for the super-admin/owner (clientId = -1 or id = 1)
     // Adjust this query to match whichever user "owns" the master template set
     List<AdminTemplateModuleEntity> atmList =
             adminTemplateModuleRepository.findByUserIdAndIsDeleteFalseAndIsActiveTrue(1L); // or the admin userId

     return buildResponse(role, atmList, roleId);
 }

    // =========================================================================
    // SAVE
    // =========================================================================
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public RoleReportRightsResponseDto saveReportRights(SaveRoleReportRightsRequestDto request) {

        if (request.getRoleId() == null) throw new RuntimeException("roleId is required");
        if (request.getUserId() == null) throw new RuntimeException("userId is required");

        roleMasterRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRoleId()));

        persistService.persistRights(request);

        // Return modal view (userId-scoped) after save
        return getReportRights(request.getRoleId(), request.getUserId());
    }

    // =========================================================================
    // Shared builder — used by both GET methods
    // =========================================================================
    private RoleReportRightsResponseDto buildResponse(RoleMasterEntity role,
                                                       List<AdminTemplateModuleEntity> atmList,
                                                       Long roleId) {
        if (atmList.isEmpty()) {
            RoleReportRightsResponseDto empty = new RoleReportRightsResponseDto();
            empty.setRoleId(roleId);
            empty.setRoleName(role.getName());
            empty.setModules(Collections.emptyList());
            return empty;
        }

        // Allowed atm ids for this role
        Set<Long> allowedAtmIds = rightsRepository
                .findByRoleIdAndIsDeleteFalse(roleId)
                .stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsAllowed()))
                .map(r -> r.getAdminTemplateModule().getId())
                .collect(Collectors.toSet());

        // Group atm rows by templateModuleMaster
        Map<Long, List<AdminTemplateModuleEntity>> groupedByModule = atmList.stream()
                .filter(atm -> atm.getTemplateModuleMaster() != null
                            && atm.getTemplateMaster()       != null)
                .collect(Collectors.groupingBy(
                        atm -> atm.getTemplateModuleMaster().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<ModuleDto> moduleDtos = new ArrayList<>();

        for (Map.Entry<Long, List<AdminTemplateModuleEntity>> entry
                : groupedByModule.entrySet()) {

            List<AdminTemplateModuleEntity> group = entry.getValue();
            AdminTemplateModuleEntity first = group.get(0);

            ModuleDto moduleDto = new ModuleDto();
            moduleDto.setTemplateModuleMasterId(first.getTemplateModuleMaster().getId());
            moduleDto.setTemplateModuleMasterName(
                    first.getTemplateModuleMaster().getNameEnglish());

            List<ReportDto> reportDtos = new ArrayList<>();
            int selectedCount = 0;

            for (AdminTemplateModuleEntity atm : group) {
                ReportDto rd = new ReportDto();
                rd.setAdminTemplateModuleId(atm.getId());
                rd.setTemplateMasterId(atm.getTemplateMaster().getId());
                rd.setTemplateMasterName(atm.getTemplateMaster().getName());
                boolean allowed = allowedAtmIds.contains(atm.getId());
                rd.setIsAllowed(allowed);
                if (allowed) selectedCount++;
                reportDtos.add(rd);
            }

            moduleDto.setTotalReports(group.size());
            moduleDto.setTotalSelected(selectedCount);
            moduleDto.setReports(reportDtos);
            moduleDtos.add(moduleDto);
        }

        RoleReportRightsResponseDto response = new RoleReportRightsResponseDto();
        response.setRoleId(roleId);
        response.setRoleName(role.getName());
        response.setModules(moduleDtos);
        return response;
    }
}