package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CoupenEntity;

@Repository
public interface CoupenMasterRepository extends JpaRepository<CoupenEntity, Long> {

	Optional<CoupenEntity> findByIdAndIsDeleteFalse(Long id);

	List<CoupenEntity> findByIsDeleteFalse();

}
