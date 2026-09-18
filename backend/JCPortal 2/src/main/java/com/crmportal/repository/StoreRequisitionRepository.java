package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.StoreRequisitionEntity;

@Repository
public interface StoreRequisitionRepository extends JpaRepository<StoreRequisitionEntity, Long> {

    List<StoreRequisitionEntity> findByUserIdAndIsDeleteFalse(Long userId);
    
    @Query("SELECT p FROM StoreRequisitionEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.isDelete = false " +
		       "ORDER BY p.createdAt DESC")
		List<StoreRequisitionEntity> getByUserDesc(@Param("userId") Long userId);

    List<StoreRequisitionEntity> findByIdAndIsDeleteFalse(Long id);

    @Query("SELECT MAX(c.srcode) FROM StoreRequisitionEntity c WHERE c.srcode LIKE CONCAT(:prefix, '%')")
    String findMaxSrcodeByPrefix(@Param("prefix") String prefix);
    
    @Query("SELECT c.id, c.srcode FROM StoreRequisitionEntity c WHERE c.user.id = :userId AND c.isDelete = false AND c.status = 'COMPLETED' ORDER BY c.createdAt DESC")
    List<Object[]> findAllSrcodesByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM StoreRequisitionEntity c WHERE c.srcode = :crcode AND c.user.id = :userId AND c.isDelete = false")
    Optional<StoreRequisitionEntity> findBySrcodeAndUserIdAndIsDeleteFalse(
            @Param("crcode") String crcode,
            @Param("userId") Long userId);
    
    Optional<StoreRequisitionEntity> findBySrcodeAndIsDeleteFalse(String crcode);
}