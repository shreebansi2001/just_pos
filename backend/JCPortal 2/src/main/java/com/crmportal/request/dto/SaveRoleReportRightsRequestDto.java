package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

/**
 * Payload for POST /v1/api/role-report-rights/save?userId=
 *
 * {
 *   "roleId"  : 1,
 *   "reports" : [
 *     { "adminTemplateModuleId": 40, "isAllowed": true  },
 *     { "adminTemplateModuleId": 42, "isAllowed": true  },
 *     { "adminTemplateModuleId": 43, "isAllowed": false }
 *   ]
 * }
 *
 * The frontend sends the id from admin_template_module for each report row.
 * userId is taken from @RequestParam — mandatory.
 */
@Data
public class SaveRoleReportRightsRequestDto {

    /** Role whose rights are being saved. */
    private Long roleId;

    /** Set by controller from @RequestParam — do not send in body. */
    private Long userId;

    /** Flat list of all report rows with their allow/deny flag. */
    private List<ReportRightItemDto> reports;

    @Data
    public static class ReportRightItemDto {
        /** admin_template_module.id */
        private Long    adminTemplateModuleId;
        private Boolean isAllowed;
    }
}