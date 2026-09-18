package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.AmountTransferEntity;

@Repository
public interface AmountTransferRepository extends JpaRepository<AmountTransferEntity, Long> {

	Optional<AmountTransferEntity> findByIdAndIsDeleteFalse(Long id);
	
	Optional<AmountTransferEntity> findByIdAndUserIdAndIsDeleteFalse(Long id, Long userId);
	
	@Query(
		    "SELECT at "
		    + "FROM AmountTransferEntity at "
		    + "WHERE at.isDelete = false "
		    + "AND (:startDate IS NULL OR at.date >= :startDate) "
		    + "AND (:endDate IS NULL OR at.date <= :endDate) "
		    + "AND at.userId = :userId "
		    + "ORDER BY at.date DESC"
		)
	List<AmountTransferEntity> getAllTransfers(
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate,
	        @Param("userId") Long userId
	);
	
}
