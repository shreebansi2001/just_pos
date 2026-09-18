package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.StoreIssueReturnEntity;

@Repository
public interface StoreIssueReturnRepository extends JpaRepository<StoreIssueReturnEntity, Long> {

    List<StoreIssueReturnEntity> findByUserIdAndIsDeleteFalse(Long userId);
    
    @Query("SELECT p FROM StoreIssueReturnEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.isDelete = false " +
		       "ORDER BY p.createdAt DESC")
		List<StoreIssueReturnEntity> getByUserDesc(@Param("userId") Long userId);

    List<StoreIssueReturnEntity> findByIdAndIsDeleteFalse(Long id);

    // All returns made against one specific store issue
    List<StoreIssueReturnEntity> findByStoreIssueIdAndUserIdAndIsDeleteFalse(Long storeIssueId, Long userId);

    @Query("SELECT MAX(s.sircode) FROM StoreIssueReturnEntity s " +
    	       "WHERE s.user.id = :userId " +
    	       "AND s.sircode LIKE CONCAT(:prefix, '%')")
    	String findMaxSircodeByPrefix(
    	        @Param("userId") Long userId,
    	        @Param("prefix") String prefix);
    
 // Check if any return exists for this store issue
    boolean existsByStoreIssueIdAndIsDeleteFalse(Long storeIssueId);
}