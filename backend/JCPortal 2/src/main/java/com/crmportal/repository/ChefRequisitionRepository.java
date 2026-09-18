package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.ChefRequisitionEntity;

@Repository
public interface ChefRequisitionRepository extends JpaRepository<ChefRequisitionEntity, Long> {

    List<ChefRequisitionEntity> findByUserIdAndIsDeleteFalse(Long userId);
    
    @Query("SELECT p FROM ChefRequisitionEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.isDelete = false " +
		       "ORDER BY p.createdAt DESC")
		List<ChefRequisitionEntity> getByUserDesc(@Param("userId") Long userId);

    List<ChefRequisitionEntity> findByIdAndIsDeleteFalse(Long id);

    @Query("SELECT MAX(c.crcode) FROM ChefRequisitionEntity c WHERE c.crcode LIKE CONCAT(:prefix, '%')")
    String findMaxCrcodeByPrefix(@Param("prefix") String prefix);
    
    @Query("SELECT c.id, c.crcode FROM ChefRequisitionEntity c WHERE c.user.id = :userId AND c.isDelete = false AND c.status = 'COMPLETED' ORDER BY c.createdAt DESC")
    List<Object[]> findAllCrcodesByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM ChefRequisitionEntity c WHERE c.crcode = :crcode AND c.user.id = :userId AND c.isDelete = false")
    Optional<ChefRequisitionEntity> findByCrcodeAndUserIdAndIsDeleteFalse(
            @Param("crcode") String crcode,
            @Param("userId") Long userId);
    
    Optional<ChefRequisitionEntity> findByCrcodeAndIsDeleteFalse(String crcode);
}