package com.crmportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ConfigurationUtilEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface ConfigurationUtilEntityRepository extends JpaRepository<ConfigurationUtilEntity, Long> {

    Optional<ConfigurationUtilEntity> findById(Long id);

    Optional<ConfigurationUtilEntity> findByUser(Long user);

	Optional<ConfigurationUtilEntity> findByIdAndIsDeleteFalse(Long moduleRecordId);
}