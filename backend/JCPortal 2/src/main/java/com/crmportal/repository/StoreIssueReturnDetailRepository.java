package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.StoreIssueReturnDetailEntity;

@Repository
public interface StoreIssueReturnDetailRepository extends JpaRepository<StoreIssueReturnDetailEntity, Long> {

    List<StoreIssueReturnDetailEntity> findByStoreIssueReturnId(Long sirId);

    void deleteByStoreIssueReturnId(Long sirId);
    
    @Query("SELECT COALESCE(SUM(d.qty), 0) FROM StoreIssueReturnDetailEntity d " +
    	       "JOIN d.storeIssueReturn r " +
    	       "WHERE r.storeIssue.id = :storeIssueId " +
    	       "AND d.rawMaterial.id = :rawMaterialId " +
    	       "AND r.isDelete = false")
    	double sumReturnedQtyByStoreIssueAndRawMaterial(
    	        @Param("storeIssueId") Long storeIssueId,
    	        @Param("rawMaterialId") Long rawMaterialId);
    
    @Query("SELECT d FROM StoreIssueReturnDetailEntity d " +
    	       "JOIN d.storeIssueReturn r " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND r.returndate >= :fromDate " +
    	       "AND r.returndate <= :toDate " +
    	       "AND r.isDelete = false " +
    	       "ORDER BY r.returndate ASC")
    	List<StoreIssueReturnDetailEntity> findByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);
    
    @Query("SELECT COALESCE(SUM(d.qty), 0) FROM StoreIssueReturnDetailEntity d " +
    	       "JOIN d.storeIssueReturn r " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND r.returndate >= :fromDate " +
    	       "AND r.returndate <= :toDate " +
    	       "AND r.isDelete = false")
    	double sumStoreReturnQtyByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);

	List<StoreIssueReturnDetailEntity> findAllByStoreIssueReturn_EventId(Long eventId);
}