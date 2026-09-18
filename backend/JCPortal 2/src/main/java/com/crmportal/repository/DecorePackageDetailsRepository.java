package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DecorePackageDetailsEntity;
import com.crmportal.entity.DecorePackageEntity;

@Repository
public interface DecorePackageDetailsRepository extends JpaRepository<DecorePackageDetailsEntity, Long> {

	List<DecorePackageDetailsEntity> findByDecorePackageAndIsDeleteFalse(DecorePackageEntity entity);

	List<DecorePackageDetailsEntity> findByDecorePackage(DecorePackageEntity entity);

	void deleteByDecorePackage(DecorePackageEntity entity);
}