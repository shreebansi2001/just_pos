package com.crmportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExclusiveThemePaymentEntity;

@Repository
public interface ExclusiveThemePaymentRepository extends JpaRepository<ExclusiveThemePaymentEntity, Long> {

	Optional<ExclusiveThemePaymentEntity> findByIdAndIsDeleteFalse(Long id);

	Optional<ExclusiveThemePaymentEntity> findByAdminTemplateId(Long id);

}
