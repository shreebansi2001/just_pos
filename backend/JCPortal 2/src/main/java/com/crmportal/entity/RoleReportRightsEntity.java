package com.crmportal.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.*;

/**
 * One row = one report (admin_template_module entry) allowed/denied for a role.
 * Table: role_report_rights
 */
@Entity
@Table(
    name = "role_report_rights",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_role_template_module",
            columnNames = {"role_id", "admin_template_module_id"}
        )
    }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleReportRightsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** FK → roles.role_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleMasterEntity role;

    /** FK → admin_template_module.id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_template_module_id", nullable = false)
    private AdminTemplateModuleEntity adminTemplateModule;

    /** Mandatory — who saved this permission. */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** true = this report is accessible for this role. */
    @Column(name = "is_allowed", nullable = false)
    private Boolean isAllowed = true;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
}