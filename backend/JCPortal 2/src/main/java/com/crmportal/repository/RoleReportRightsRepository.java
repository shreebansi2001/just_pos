package com.crmportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.RoleReportRightsEntity;

@Repository
public interface RoleReportRightsRepository
        extends JpaRepository<RoleReportRightsEntity, Long> {

    /**
     * Used by GET — only active allowed rows shown in response.
     */
    List<RoleReportRightsEntity> findByRoleIdAndIsDeleteFalse(Long roleId);

    /**
     * Used by UPSERT — fetch ALL rows for this role (any isAllowed, any isDelete)
     * so we can update in-place instead of re-inserting.
     */
    @Query("SELECT r FROM RoleReportRightsEntity r WHERE r.role.id = :roleId")
    List<RoleReportRightsEntity> findByRoleIdAndIsAllRoles(@Param("roleId") Long roleId);

    /** Check if a specific report is allowed for a role (access control check). */
    @Query("SELECT COUNT(r) > 0 FROM RoleReportRightsEntity r " +
           "WHERE r.role.id = :roleId " +
           "AND r.adminTemplateModule.id = :adminTemplateModuleId " +
           "AND r.isAllowed = true " +
           "AND r.isDelete = false")
    boolean isReportAllowed(
            @Param("roleId")                Long roleId,
            @Param("adminTemplateModuleId") Long adminTemplateModuleId);
}