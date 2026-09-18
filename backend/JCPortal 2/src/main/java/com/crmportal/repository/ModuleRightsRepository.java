package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserRightsModuleEntity;

@Repository
public interface ModuleRightsRepository extends JpaRepository<UserRightsModuleEntity, Long>{

	Optional<UserRightsModuleEntity> findByNameAndIsDeleteFalse(String name);

	Optional<UserRightsModuleEntity> findByIdAndIsDeleteFalse(Long id);

	List<UserRightsModuleEntity> findAllByIsDeleteFalse();

	boolean existsByIdAndIsDeleteFalse(Long id);

}
