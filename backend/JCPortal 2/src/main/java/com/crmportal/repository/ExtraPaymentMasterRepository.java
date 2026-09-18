package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExtraPaymentEntity;

@Repository
public interface ExtraPaymentMasterRepository extends JpaRepository<ExtraPaymentEntity, Long>{

	ExtraPaymentEntity findByIdAndIsDeleteFalse(Long id);

	List<ExtraPaymentEntity> findAllByIsDeleteFalse();

	boolean existsByIdAndIsDeleteFalse(Long id);

}
