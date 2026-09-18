package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseOrderStoreDetailEntity;

@Repository
public interface PurchaseOrderStoreDetailRepository extends JpaRepository<PurchaseOrderStoreDetailEntity, Long> {

    List<PurchaseOrderStoreDetailEntity> findByPoId(Long poId);

    void deleteByPoId(Long poId);
    
    @Query("SELECT d FROM PurchaseOrderStoreDetailEntity d " +
    	       "JOIN d.po p " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND p.podate >= :fromDate " +
    	       "AND p.podate <= :toDate " +
    	       "AND p.isDelete = false " +
    	       "ORDER BY p.podate ASC")
    	List<PurchaseOrderStoreDetailEntity> findByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);
    
    @Query("SELECT COALESCE(SUM(d.qty), 0) FROM PurchaseOrderStoreDetailEntity d " +
    	       "JOIN d.po p " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND p.podate >= :fromDate " +
    	       "AND p.podate <= :toDate " +
    	       "AND p.isDelete = false")
    	double sumStoreIssueQtyByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);
    
    @Query(value = "SELECT COALESCE(SUM(d.qty), 0) " +
            "FROM purchaseorderstoredetails d " +
            "JOIN purchaseorderstore p ON d.storepo_id = p.storepo_id " +
            "WHERE d.raw_material_id = :rawMaterialId " +
            "AND p.podate >= :fromDate " +
            "AND p.podate <= :toDate " +
            "AND p.stock_type_id = :stockTypeId " +
            "AND p.is_delete = 0",
    nativeQuery = true)
double sumStoreIssueQtyByRawMaterialAndDateRangeAndStockType(
     @Param("rawMaterialId") Long rawMaterialId,
     @Param("fromDate") LocalDate fromDate,
     @Param("toDate") LocalDate toDate,
     @Param("stockTypeId") Long stockTypeId);
    
    
    List<PurchaseOrderStoreDetailEntity> findAllByPo_EventId(Long eventId);
}