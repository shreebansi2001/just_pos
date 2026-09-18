package com.crmportal.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crmportal.response.dto.RoleReportRightsResponseDto;
import com.crmportal.service.RoleReportRightsService;

/**
 * Reusable helper to fetch role report rights.
 * Inject this anywhere you need to attach report rights to a response.
 */
@Component
public class RoleReportRightsHelper {

    @Autowired
    private RoleReportRightsService roleReportRightsService;

    /**
     * For LOGIN API — fetches rights role-wise (all atm rows, no userId filter).
     * Returns null silently on error so login is never broken.
     */
    public RoleReportRightsResponseDto getForRoleOnly(Long roleId) {
        if (roleId == null) return null;
        try {
            return roleReportRightsService.getReportRightsByRole(roleId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * For MODAL UI — fetches rights scoped to a specific user's atm rows.
     * Returns null silently on error.
     */
    public RoleReportRightsResponseDto getForRole(Long roleId, Long userId) {
        if (roleId == null || userId == null) return null;
        try {
            return roleReportRightsService.getReportRights(roleId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public RoleReportRightsResponseDto getForRoleWithOwner(Long roleId, Long ownerId) {
        if (roleId == null || ownerId == null) return null;
        try {
            return roleReportRightsService.getReportRightsByRoleAndOwner(roleId, ownerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}