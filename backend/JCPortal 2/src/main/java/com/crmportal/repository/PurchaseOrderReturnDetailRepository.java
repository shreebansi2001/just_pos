package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.PurchaseOrderReturnDetailEntity;

@Repository
public interface PurchaseOrderReturnDetailRepository extends JpaRepository<PurchaseOrderReturnDetailEntity, Long> {

    List<PurchaseOrderReturnDetailEntity> findByPurchaseOrderReturnId(Long porId);
    
    @Query("SELECT COALESCE(SUM(d.qty), 0) FROM PurchaseOrderReturnDetailEntity d " +
 	       "JOIN d.purchaseOrderReturn r " +
 	       "WHERE r.purchaseOrder.id = :poId " +
 	       "AND d.rawMaterial.id = :rawMaterialId " +
 	       "AND r.isDelete = false")
 	double sumReturnedQtyByPoAndRawMaterial(
 	        @Param("poId") Long poId,
 	        @Param("rawMaterialId") Long rawMaterialId);
    
    @Query("SELECT d FROM PurchaseOrderReturnDetailEntity d " +
    	       "JOIN d.purchaseOrderReturn r " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND r.returndate >= :fromDate " +
    	       "AND r.returndate <= :toDate " +
    	       "AND r.isDelete = false " +
    	       "ORDER BY r.returndate ASC")
    	List<PurchaseOrderReturnDetailEntity> findByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);
    
    @Query("SELECT COALESCE(SUM(d.qty), 0) FROM PurchaseOrderReturnDetailEntity d " +
    	       "JOIN d.purchaseOrderReturn r " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND r.returndate >= :fromDate " +
    	       "AND r.returndate <= :toDate " +
    	       "AND r.isDelete = false")
    	double sumPurchaseReturnQtyByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);
}