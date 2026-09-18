package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseRequestEntity;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequestEntity, Long> {

	@Query(""
			+ " SELECT "
			+ " 	COUNT(p) "
			+ " FROM PurchaseRequestEntity p " 
			+ " WHERE p.adminId = :userId "
			+ " AND p.requestCode LIKE CONCAT(:prefix, '%') "
			+ " AND p.isDelete = FALSE ")
	Long countByUserIdAndPrefix(@Param("userId") Long userId, @Param("prefix") String prefix);

	List<PurchaseRequestEntity> findByUserIdAndIsDeleteFalse(Long userId);

	Optional<PurchaseRequestEntity> findByIdAndIsDeleteFalse(Long purchaseApproveRequestId);

	@Query(" "
			+ " SELECT "
			+ " 	p "
			+ " FROM PurchaseRequestEntity p "
	        + " WHERE p.userId = :userId "
	        + " AND p.isDelete = FALSE "
	        + " AND LOWER(p.status) = LOWER(:status)")
	Page<PurchaseRequestEntity> findByUserIdAndIsDeleteFalseAndStatusIgnoreCase(Long userId, String status,
			Pageable pageable);

	@Query(" " +
			" SELECT " +
			"  pr " +
			" FROM PurchaseRequestEntity pr " +
		    "  WHERE pr.isDelete = false " +
		    "  AND (" +
		    "    pr.userId = :userId " +
		    "    OR pr.userId IN ( " +
		    "        SELECT u.id FROM UserMasterEntity u " +
		    "        WHERE u.clientId = :userId" +
		    "    ) " +
		    " )" +
		    " AND ( " +
		    "  	:status = 'ALL' " +
		    " 	OR pr.status = :status " +
		    " )"
		   )
		List<PurchaseRequestEntity> findByAllPurchaseRequest(@Param("userId") Long userId, @Param("status") String status);

	boolean existsByRequestCodeAndAdminIdAndIsDeleteFalse(String requestCode, Long adminId);

	boolean existsByRequestCodeAndIdNotAndAdminIdAndIsDeleteFalse(String requestCode, Long id, Long adminId);
}