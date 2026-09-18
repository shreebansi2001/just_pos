package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExtraChargesHeadingEntity;
import com.crmportal.entity.ExtraChargesRowEntity;

@Repository
public interface ExtraChargesRowRepository extends JpaRepository<ExtraChargesRowEntity, Long> {

    List<ExtraChargesRowEntity> findAllByHeadingAndIsDeleteFalse(ExtraChargesHeadingEntity heading);

    void deleteAllByHeading(ExtraChargesHeadingEntity heading);
}