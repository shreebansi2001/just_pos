package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionQuotationEntity;

@Repository
public interface GstSalesReportRepository extends JpaRepository<EventFunctionQuotationEntity, Long> {

    /**
     * Quotations where the event does NOT have any invoice in event_invoice table,
     * filtered by event_start_date_time from the events table.
     */
	@Query("SELECT q FROM EventFunctionQuotationEntity q " +
		       "JOIN q.event e " +
		       "WHERE q.isDelete = false " +
		       "AND e.isDelete = false " +
		       "AND q.user.id = :userId " +
		       "AND e.eventStartDateTime BETWEEN :fromDate AND :toDate " +
		       "AND q.subTotal > 0 " +
		       "AND (:gstType IS NULL OR :gstType = '' " +
		       "     OR (:gstType = 'WITH_GST' AND (COALESCE(q.cgstAmnt, 0) > 0 OR COALESCE(q.sgstAmnt, 0) > 0 OR COALESCE(q.igstAmnt, 0) > 0)) " +
		       "     OR (:gstType = 'WITHOUT_GST' AND COALESCE(q.cgstAmnt, 0) = 0 AND COALESCE(q.sgstAmnt, 0) = 0 AND COALESCE(q.igstAmnt, 0) = 0)) " +
		       "AND NOT EXISTS (" +
		       "    SELECT 1 FROM EventInvoiceEntity inv " +
		       "    WHERE inv.event.id = e.id " +
		       "    AND inv.isDelete = false" +
		       ") " +
		       "ORDER BY e.eventStartDateTime ASC")
		List<EventFunctionQuotationEntity> findQuotationsWithoutInvoice(
		        @Param("fromDate") LocalDateTime fromDate,
		        @Param("toDate") LocalDateTime toDate,
		        @Param("userId") Long userId,
		        @Param("gstType") String gstType);
}