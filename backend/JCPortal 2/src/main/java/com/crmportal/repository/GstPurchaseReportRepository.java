package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseOrderDetailEntity;

@Repository
public interface GstPurchaseReportRepository extends JpaRepository<PurchaseOrderDetailEntity, Long> {

	@Query("SELECT pod FROM PurchaseOrderDetailEntity pod " +
		       "JOIN FETCH pod.po po " +
		       "JOIN FETCH po.supplier s " +
		       "JOIN FETCH pod.rawMaterial rm " +
		       "WHERE po.isDelete = false " +
		       "AND po.podate BETWEEN :fromDate AND :toDate " +
		       "AND (:gstType IS NULL OR :gstType = '' " +
		       "     OR (:gstType = 'WITH_GST' AND (" +
		       "         COALESCE(pod.cgst, 0) > 0 " +
		       "         OR COALESCE(pod.sgst, 0) > 0 " +
		       "         OR COALESCE(pod.igst, 0) > 0" +
		       "     )) " +
		       "     OR (:gstType = 'WITHOUT_GST' AND " +
		       "         COALESCE(pod.cgst, 0) = 0 " +
		       "         AND COALESCE(pod.sgst, 0) = 0 " +
		       "         AND COALESCE(pod.igst, 0) = 0" +
		       "     )) " +
		       "ORDER BY po.podate ASC, po.id ASC")
	List<PurchaseOrderDetailEntity> findPurchaseGstReport(
	        @Param("fromDate") LocalDate fromDate,
	        @Param("toDate") LocalDate toDate,
	        @Param("gstType") String gstType);
}