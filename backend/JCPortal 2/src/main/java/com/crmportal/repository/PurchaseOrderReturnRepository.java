package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseOrderEntity;
import com.crmportal.entity.PurchaseOrderReturnEntity;

@Repository
public interface PurchaseOrderReturnRepository extends JpaRepository<PurchaseOrderReturnEntity, Long> {
	
	@Query("SELECT p FROM PurchaseOrderReturnEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.isDelete = false " +
		       "ORDER BY p.createdAt DESC")
		List<PurchaseOrderReturnEntity> getByUserDesc(@Param("userId") Long userId);

    List<PurchaseOrderReturnEntity> findByUserIdAndIsDeleteFalse(Long userId);

    List<PurchaseOrderReturnEntity> findByPurchaseOrderIdAndIsDeleteFalse(Long poId);

    List<PurchaseOrderReturnEntity> findByIdAndIsDeleteFalse(Long id);

    @Query("SELECT MAX(p.porcode) FROM PurchaseOrderReturnEntity p " +
    	       "WHERE p.user.id = :userId " +
    	       "AND p.porcode LIKE CONCAT(:prefix, '%')")
    	String findMaxPorcodeByPrefix(
    	        @Param("userId") Long userId,
    	        @Param("prefix") String prefix);
    
 // Check if any return exists for this PO
    boolean existsByPurchaseOrderIdAndIsDeleteFalse(Long poId);
}