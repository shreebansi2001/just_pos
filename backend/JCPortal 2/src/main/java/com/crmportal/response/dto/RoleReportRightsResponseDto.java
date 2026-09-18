package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class RoleReportRightsResponseDto {

    private Long   roleId;
    private String roleName;
    private List<ModuleDto> modules;
    @Data
    public static class ModuleDto {
        private Long   templateModuleMasterId;
        private String templateModuleMasterName;
        private int    totalReports;
        private int    totalSelected;
        private List<ReportDto> reports;
    }

    @Data
    public static class ReportDto {
        private Long    adminTemplateModuleId;
        private Long    templateMasterId;
        private String  templateMasterName;
        private Boolean isAllowed;
    }
}