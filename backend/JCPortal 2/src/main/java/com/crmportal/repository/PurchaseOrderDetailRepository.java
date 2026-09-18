package com.crmportal.repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseOrderDetailEntity;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetailEntity, Long> {

    List<PurchaseOrderDetailEntity> findByPoId(Long poId);
    
    @Query("SELECT d FROM PurchaseOrderDetailEntity d " +
    	       "JOIN d.po p " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND p.podate >= :fromDate " +
    	       "AND p.podate <= :toDate " +
    	       "AND p.isDelete = false " +
    	       "ORDER BY p.podate ASC")
    	List<PurchaseOrderDetailEntity> findByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);
    
    @Query("SELECT COALESCE(SUM(d.qty), 0) FROM PurchaseOrderDetailEntity d " +
    	       "JOIN d.po p " +
    	       "WHERE d.rawMaterial.id = :rawMaterialId " +
    	       "AND p.podate >= :fromDate " +
    	       "AND p.podate <= :toDate " +
    	       "AND p.isDelete = false")
    	double sumPurchaseQtyByRawMaterialAndDateRange(
    	        @Param("rawMaterialId") Long rawMaterialId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate);
    
    // Latest single price — user-wise
    @Query(value =
            "SELECT pod.price FROM purchaseorderdetails pod " +
            "WHERE pod.raw_material_id = :rmId " +
            "AND pod.userid = :userId " +
            "AND pod.price > 0 " +
            "ORDER BY pod.podetail_id DESC LIMIT 1",
            nativeQuery = true)
    Double findLatestPriceByRawMaterialIdAndUserId(
            @Param("rmId") Long rmId,
            @Param("userId") Long userId);

    // Sum of all purchase prices
    @Query("SELECT COALESCE(SUM(pod.price), 0) " +
           "FROM PurchaseOrderDetailEntity pod " +
           "WHERE pod.rawMaterial.id = :rmId " +
           "AND pod.userid = :userId " +
           "AND pod.price > 0")
    Double sumPriceByRawMaterialIdAndUserId(
            @Param("rmId") Long rmId,
            @Param("userId") Long userId);

    // Count of records
    @Query("SELECT COUNT(pod) " +
           "FROM PurchaseOrderDetailEntity pod " +
           "WHERE pod.rawMaterial.id = :rmId " +
           "AND pod.userid = :userId " +
           "AND pod.price > 0")
    Long countByRawMaterialIdAndUserIdAndPriceGreaterThanZero(
            @Param("rmId") Long rmId,
            @Param("userId") Long userId);
    
    @Query(" " +
    	   " SELECT pod " +
	       " FROM PurchaseOrderDetailEntity pod " +
	       " WHERE pod.po.user.id = :userId " +
	       " AND pod.po.isDelete = false " +
	       " AND pod.po.podate >= :startDate " +
	       " AND pod.po.podate <= :endDate " +
	       " ORDER BY pod.po.podate ASC ")
	List<PurchaseOrderDetailEntity> findPurchaseReportData(
	        @Param("userId") Long userId,
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate);
}