package com.crmportal.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.SalesInvoiceEntity;
import com.crmportal.enums.PaymentMode;

@Repository
public interface SalesInvoiceRepository extends JpaRepository<SalesInvoiceEntity, Long> {

	Optional<SalesInvoiceEntity> findByIdAndIsDeleteFalse(Long id);

	List<SalesInvoiceEntity> findByUserIdAndEventIdAndIsDeleteFalse(Long userId, Long eventId);

	List<SalesInvoiceEntity> findByUserIdAndEventIdAndIsDeleteFalseOrderById(Long userId, Long eventId);

	List<SalesInvoiceEntity> findByEventIdAndIsDeleteFalseOrderById(Long eventId);

	List<SalesInvoiceEntity> findByUserIdAndEventIdInAndIsDeleteFalse(Long userId, List<Long> eventIds);
	
	@Query(value = " "
			+ " SELECT SUM(si.total_amount) "
			+ " FROM sales_invoice si "
			+ " INNER JOIN event_invoice ei ON ei.invoice_code = si.invoice_no "
			+ " WHERE si.is_delete = FALSE"
			+ " AND ei.is_delete = FALSE "
			+ " AND ei.user_id = :userId "
			+ " AND (:startDate IS NULL OR si.payment_date >= :startDate) "
			+ " AND (:endDate IS NULL OR si.payment_date <= :endDate) ", nativeQuery = true)
	BigDecimal getTotalPaidAmount(
			@Param("startDate") LocalDateTime startDate, 
			@Param("endDate") LocalDateTime endDate,
			@Param("userId") Long userId);
}
