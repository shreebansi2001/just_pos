package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventInvoiceEntity;

@Repository
public interface GstInvoiceReportRepository extends JpaRepository<EventInvoiceEntity, Long> {

    /**
     * Invoices filtered by event_start_date_time from the events table.
     */
	@Query("SELECT inv FROM EventInvoiceEntity inv " +
		       "JOIN inv.event e " +
		       "WHERE inv.isDelete = false " +
		       "AND e.isDelete = false " +
		       "AND inv.user.id = :userId " +
		       "AND e.eventStartDateTime BETWEEN :fromDate AND :toDate " +
		       "AND inv.subTotal > 0 " +
		       "AND (:gstType IS NULL OR :gstType = '' " +
		       "     OR (:gstType = 'WITH_GST' AND (COALESCE(inv.cgstAmnt, 0) > 0 OR COALESCE(inv.sgstAmnt, 0) > 0 OR COALESCE(inv.igstAmnt, 0) > 0)) " +
		       "     OR (:gstType = 'WITHOUT_GST' AND COALESCE(inv.cgstAmnt, 0) = 0 AND COALESCE(inv.sgstAmnt, 0) = 0 AND COALESCE(inv.igstAmnt, 0) = 0)) " +
		       "ORDER BY e.eventStartDateTime ASC")
	List<EventInvoiceEntity> findInvoicesWithinDateRange(
	        @Param("fromDate") LocalDateTime fromDate,
	        @Param("toDate") LocalDateTime toDate,
	        @Param("userId") Long userId,
	        @Param("gstType") String gstType);
}