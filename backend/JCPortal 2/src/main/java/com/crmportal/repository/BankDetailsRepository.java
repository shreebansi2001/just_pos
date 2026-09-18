package com.crmportal.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.AllVendorsPaymentResponseDto;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetailsEntity, Long> {

	Optional<BankDetailsEntity> findByIdAndIsDeleteFalse(Long id);

	List<BankDetailsEntity> findByUserIdAndIsDeleteFalse(Long userId);

	List<BankDetailsEntity> findByIsDeleteFalse();

	Optional<BankDetailsEntity> findByUserAndIsPrimaryTrueAndIsDeleteFalse(UserMasterEntity user);

	Optional<BankDetailsEntity> findByIdAndUserIdAndIsDeleteFalse(Long bankId, Long id);

	List<BankDetailsEntity> findAllByIdInAndIsDeleteFalse(Set<Long> ids);

}
