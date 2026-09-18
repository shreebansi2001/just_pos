package com.crmportal.service.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.RoleReportRightsEntity;
import com.crmportal.repository.AdminTemplateModuleRepository;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.RoleReportRightsRepository;
import com.crmportal.request.dto.SaveRoleReportRightsRequestDto;
import com.crmportal.request.dto.SaveRoleReportRightsRequestDto.ReportRightItemDto;

@Service
public class RoleReportRightsPersistService {

    @Autowired
    private RoleReportRightsRepository rightsRepository;

    @Autowired
    private RoleMasterRepository roleMasterRepository;

    @Autowired
    private AdminTemplateModuleRepository adminTemplateModuleRepository;

    /**
     * UPSERT strategy — does NOT wipe all rows.
     * For each report in the request:
     *   - If a row already exists for (role + adminTemplateModule) → update isAllowed
     *   - If no row exists → insert new one
     * This preserves all other previously saved reports for this role.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistRights(SaveRoleReportRightsRequestDto request) {

        if (request.getReports() == null || request.getReports().isEmpty()) return;

        RoleMasterEntity role = roleMasterRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException(
                        "Role not found: " + request.getRoleId()));

        // Load all existing rows for this role into a map for O(1) lookup
        // key = adminTemplateModule.id
        Map<Long, RoleReportRightsEntity> existingMap = new HashMap<>();
        rightsRepository.findByRoleIdAndIsAllRoles(request.getRoleId())
                .forEach(r -> existingMap.put(r.getAdminTemplateModule().getId(), r));

        List<RoleReportRightsEntity> toSave = new ArrayList<>();

        for (ReportRightItemDto item : request.getReports()) {

            boolean allowed = Boolean.TRUE.equals(item.getIsAllowed());

            RoleReportRightsEntity entry = existingMap.get(item.getAdminTemplateModuleId());

            if (entry != null) {
                // Row exists — just update isAllowed and clear soft-delete flag
                entry.setIsAllowed(allowed);
                entry.setIsDelete(false);
                entry.setUserId(request.getUserId());
            } else {
                // No row yet — create fresh
                AdminTemplateModuleEntity atm =
                        adminTemplateModuleRepository
                                .findById(item.getAdminTemplateModuleId())
                                .orElseThrow(() -> new RuntimeException(
                                        "AdminTemplateModule not found: "
                                                + item.getAdminTemplateModuleId()));
                entry = new RoleReportRightsEntity();
                entry.setRole(role);
                entry.setAdminTemplateModule(atm);
                entry.setUserId(request.getUserId());
                entry.setIsAllowed(allowed);
                entry.setIsDelete(false);
            }

            toSave.add(entry);
        }

        rightsRepository.saveAll(toSave);
        rightsRepository.flush();
    }
}