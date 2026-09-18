package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.LaborHelperEntity;

@Repository
public interface LaborHelperRepository extends JpaRepository<LaborHelperEntity, Long> {

    List<LaborHelperEntity> findByIsDeleteFalse();

    Optional<LaborHelperEntity> findByIdAndIsDeleteFalse(Long id);

    List<LaborHelperEntity> findByContact_IdAndIsDeleteFalse(Long partyId);

    List<LaborHelperEntity> findByContactCategory_IdAndIsDeleteFalse(Long contactCategoryId);

    // Filters labor helpers where the associated party belongs to the given userId
    List<LaborHelperEntity> findByContact_User_IdAndIsDeleteFalse(Long userId);
}