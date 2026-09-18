package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventQuotationSecurityDepositEntity;

@Repository
public interface EventQuotationSecurityDepositRepository extends JpaRepository<EventQuotationSecurityDepositEntity, Long> {

	Optional<EventQuotationSecurityDepositEntity> findByIdAndIsDeleteFalse(Long id);
	
	List<EventQuotationSecurityDepositEntity> findAllByEventIdAndIsDeleteFalse(Long eventId);

}
