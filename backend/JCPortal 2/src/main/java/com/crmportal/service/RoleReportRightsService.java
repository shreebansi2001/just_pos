package com.crmportal.service;

import com.crmportal.request.dto.SaveRoleReportRightsRequestDto;
import com.crmportal.response.dto.RoleReportRightsResponseDto;

public interface RoleReportRightsService {

    /**
     * Fetches report rights for a role scoped to a userId's admin_template_module rows.
     * Used in the Report Rights modal UI.
     */
    RoleReportRightsResponseDto getReportRights(Long roleId, Long userId);

    /**
     * Fetches report rights for a role across ALL admin_template_module rows.
     * Used in login API — role-level access, not user-scoped.
     */
    RoleReportRightsResponseDto getReportRightsByRole(Long roleId);

    /**
     * Saves (upserts) report rights for a role.
     */
    RoleReportRightsResponseDto saveReportRights(SaveRoleReportRightsRequestDto request);
    
    RoleReportRightsResponseDto getReportRightsByRoleAndOwner(Long roleId, Long ownerId);
}