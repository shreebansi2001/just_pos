package com.crmportal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.InvoicePaymentHistoryEntity;
import com.crmportal.enums.PaymentMode;
import com.crmportal.response.dto.IncomeListResponseDto;


@Repository
public interface InvoicePaymentHistoryRepository extends JpaRepository<InvoicePaymentHistoryEntity, Long>{

	Optional<InvoicePaymentHistoryEntity> findByInvoicePaymentIdAndIsDeleteFalse(Long id);
	
	List<InvoicePaymentHistoryEntity> findAllByInvoiceAndIsDeleteFalse(InvoiceEntity invoice);
	
	List<InvoicePaymentHistoryEntity> findAllByInvoice_InvoiceIdAndIsDeleteFalse(Long invoiceId);
	
	@Query(value = 
			" SELECT iph "
		    + " FROM InvoicePaymentHistoryEntity iph "
		    + " WHERE iph.invoice.id = :invoiceId "
		    + " AND iph.invoicePaymentId = ( "
		    + "  	SELECT MAX(iph2.invoicePaymentId) "
		    + "     	FROM InvoicePaymentHistoryEntity iph2 "
		    + "     	WHERE iph2.invoice.id = :invoiceId "
		    + " )")
	Optional<InvoicePaymentHistoryEntity> findLatestByInvoiceId(Long invoiceId);
		    
	@Query("SELECT COALESCE(SUM(p.amount),0) FROM InvoicePaymentHistoryEntity p " +
		       "WHERE p.invoice = :invoice AND p.isDelete = false ")
	BigDecimal getTotalPaidAmountByInvoice(@Param("invoice") InvoiceEntity invoice);
	
	@Query("SELECT COALESCE(SUM(p.amount),0) FROM InvoicePaymentHistoryEntity p " +
		       "WHERE p.invoice.id = :invoiceId AND p.isDelete = false ")
	BigDecimal getTotalPaidAmountByInvoiceId(@Param("invoiceId") Long invoiceId);
	
	@Query("SELECT COALESCE(SUM(p.amount), 0) FROM InvoicePaymentHistoryEntity p "
			+ " WHERE p.invoice.isDelete = FALSE AND p.isDelete = false "
			+ " AND (:paymentMode IS NULL OR p.paymentMode = :paymentMode)"
			+ " AND (:bankAccountId IS NULL OR p.bankAccount.id = :bankAccountId) "
			+ " AND ((:stDate IS NULL OR p.invoice.createdAt >= :stDate) "
		    + " AND (:eDate IS NULL OR p.invoice.createdAt <= :eDate)) ")
	BigDecimal getTotalPaidAmount(@Param("stDate") LocalDateTime startDate, @Param("eDate") LocalDateTime endDate, 
			@Param("paymentMode") PaymentMode paymentMode,
			@Param("bankAccountId") Long bankAccountId);
	
	@Query("SELECT p.status FROM InvoicePaymentHistoryEntity p "
			+ " WHERE p.invoice.invoiceId = :invoiceId and p.isDelete = false "
			+ " Order By p.createdAt DESC ")
	List<String> getInvoiceStatus(@Param("invoiceId") Long invoiceId);
	
	@Query("SELECT new com.crmportal.response.dto.IncomeListResponseDto( " +
		       "i.billingName, " +
		       "i.invoiceId, " +
		       "i.invoiceCode, " +
		       "SUM(p.amount), " +
		       "FUNCTION('DATE_FORMAT', i.invoiceDate, '%d/%m/%Y'), " +
		       "p.paymentMode ) " +
		       "FROM InvoicePaymentHistoryEntity p " +
		       "JOIN p.invoice i " +
		       "WHERE p.isDelete = false AND i.isDelete = false " +
		       "AND p.paymentDate BETWEEN :startDate AND :endDate " +
		       "AND (:paymentVia IS NULL OR p.paymentMode = :paymentVia) " +
		       "AND (:bankId IS NULL OR p.bankAccount.id = :bankId) " +
		       "GROUP BY i.invoiceId, i.billingName, i.invoiceCode,i.invoiceDate, p.paymentMode " +
		       "ORDER BY i.invoiceId DESC")
	List<IncomeListResponseDto> getIncomeList(
		        LocalDate startDate,
		        LocalDate endDate,
		        PaymentMode paymentVia,
		        Long bankId);
	
	@Query("SELECT COUNT(DISTINCT p.invoice.invoiceId) " +
		       "FROM InvoicePaymentHistoryEntity p " +
		       "WHERE p.isDelete = false AND p.invoice.isDelete = false " +
		       "AND (LOWER(p.status) = 'pending' OR p.status IS NULL) " +
		       "AND p.invoice.createdAt BETWEEN :startDate AND :endDate " +
		       "AND p.createdAt = ( " +
		       "   SELECT MAX(p2.createdAt) " +
		       "   FROM InvoicePaymentHistoryEntity p2 " +
		       "   WHERE p2.invoice = p.invoice " +
		       "   AND p2.isDelete = false " +
		       ")")
	Long countPendingInvoicesBetween(@Param("startDate") LocalDateTime startDate,
		                                 @Param("endDate") LocalDateTime endDate);
	
	@Query("SELECT SUM(ph.amount) "
			+ " FROM InvoicePaymentHistoryEntity ph "
			+ " WHERE (:startDate IS NULL OR ph.paymentDate >= :startDate)"
			+ " AND (:endDate IS NULL OR ph.paymentDate <= :endDate)"
			+ " AND ph.isDelete = FALSE "
			+ " AND ph.invoice.isDelete = FALSE ")
	BigDecimal totalPaidAmountThisMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	
//	@Query(value = ""
//	        + " SELECT "
//	        + "     COUNT(*) AS total_unpaid_invoice_count, "
//	        + " "
//	        + "     COALESCE(SUM(data.total_amount), 0) "
//	        + "         AS total_unpaid_invoice_amount, "
//	        + " "
//	        + "     COALESCE(SUM(data.total_paid), 0) "
//	        + "         AS total_unpaid_invoice_paid_amount, "
//	        + " "
//	        + "     COALESCE(SUM(data.remaining_amount), 0) "
//	        + "         AS total_unpaid_invoice_remaining_amount "
//	        + " FROM ( "
//	        + "     SELECT "
//	        + "         im.invoice_id, "
//	        + "         im.total_amount, "
//	        + "         COALESCE(SUM(ip.amount), 0) AS total_paid, "
//	        + "         ( "
//	        + "             im.total_amount - COALESCE(SUM(ip.amount), 0) "
//	        + "         ) AS remaining_amount "
//	        + "     FROM invoice_master im "
//	        + "     LEFT JOIN invoice_payment_history ip "
//	        + "         ON ip.invoice_id = im.invoice_id "
//	        + "         AND ip.is_delete = FALSE "
//	        + "     LEFT JOIN ( "
//	        + "         SELECT "
//	        + "             ip1.invoice_id, "
//	        + "             ip1.status "
//	        + "         FROM invoice_payment_history ip1 "
//	        + "         INNER JOIN ( "
//	        + "             SELECT "
//	        + "                 invoice_id, "
//	        + "                 MAX(invoice_payment_id) AS max_payment_id "
//	        + "             FROM invoice_payment_history "
//	        + "             WHERE is_delete = FALSE "
//	        + " 		 	AND payment_date < STR_TO_DATE(:date, '%d/%m/%Y') "
//	        + "             GROUP BY invoice_id "
//	        + "         ) latest "
//	        + "             ON latest.max_payment_id = ip1.invoice_payment_id "
//	        + "     ) latest_status "
//	        + "         ON latest_status.invoice_id = im.invoice_id "
//	        + "     WHERE im.is_delete = FALSE "
//	        + "         AND im.invoice_date < STR_TO_DATE(:date, '%d/%m/%Y') "
//	        + "         AND ( "
//	        + "  		 	latest_status.status IS NULL "
//	        + "             OR latest_status.status = 'UNPAID' "
//	        + "             OR latest_status.status = 'PENDING' "
//	        + "         ) "
//	        + "     GROUP BY im.invoice_id, im.total_amount "
//	        + " ) data ",
//	        nativeQuery = true)
//	List<Object[]> getPrevAllMonthData(@Param("date") String date);
	
	@Query(value = " "
			+ " SELECT "
			+ "    COUNT(*) AS total_unpaid_invoice_count, "
			+ "    COALESCE(SUM(data.opening_due_amount), 0) "
			+ "        AS total_opening_due_amount, "
			+ "    COALESCE(SUM(data.current_month_paid_amount), 0) "
			+ "        AS total_paid_amount_in_current_month, "
			+ "    COALESCE(SUM(data.current_unpaid_amount), 0) "
			+ "        AS total_current_unpaid_amount "
			+ "FROM ( "
			+ "    SELECT "
			+ "        im.invoice_id, "
			+ "        im.total_amount, "
			+ "        COALESCE(prev_payment.total_paid_before_opening, 0) "
			+ "            AS total_paid_before_opening, "
			+ "        COALESCE(current_payment.total_paid_in_month, 0) "
			+ "            AS current_month_paid_amount, "
			+ "        ( "
			+ "            im.total_amount "
			+ "            - COALESCE(prev_payment.total_paid_before_opening, 0) "
			+ "        ) AS opening_due_amount, "
			+ "        ( "
			+ "            im.total_amount "
			+ "            - COALESCE(prev_payment.total_paid_before_opening, 0) "
			+ "            - COALESCE(current_payment.total_paid_in_month, 0) "
			+ "        ) AS current_unpaid_amount, "
			+ "        latest_status.status "
			+ "    FROM invoice_master im "
			+ "    LEFT JOIN ( "
			+ "        SELECT "
			+ "            iph.invoice_id, "
			+ "            SUM(iph.amount) AS total_paid_before_opening "
			+ "        FROM invoice_payment_history iph "
			+ "        WHERE iph.is_delete = FALSE "
			+ "            AND iph.payment_date < STR_TO_DATE(:startDate, '%d/%m/%Y') "
			+ "        GROUP BY iph.invoice_id "
			+ "    ) prev_payment "
			+ "        ON prev_payment.invoice_id = im.invoice_id "
			+ "    LEFT JOIN ( "
			+ "        SELECT "
			+ "            iph.invoice_id, "
			+ "            SUM(iph.amount) AS total_paid_in_month "
			+ "        FROM invoice_payment_history iph "
			+ "        WHERE iph.is_delete = FALSE "
			+ "            AND iph.payment_date >= STR_TO_DATE(:startDate, '%d/%m/%Y') "
			+ "            AND iph.payment_date <= STR_TO_DATE(:endDate, '%d/%m/%Y') "
			+ "        GROUP BY iph.invoice_id "
			+ "    ) current_payment "
			+ "        ON current_payment.invoice_id = im.invoice_id "
			+ "    LEFT JOIN ( "
			+ "        SELECT "
			+ "            iph1.invoice_id, "
			+ "            iph1.status "
			+ "        FROM invoice_payment_history iph1 "
			+ "        INNER JOIN ( "
			+ "            SELECT "
			+ "                invoice_id, "
			+ "                MAX(invoice_payment_id) AS max_payment_id "
			+ "            FROM invoice_payment_history "
			+ "            WHERE is_delete = FALSE "
			+ "                AND payment_date < STR_TO_DATE(:startDate, '%d/%m/%Y') "
			+ "            GROUP BY invoice_id "
			+ "        ) latest "
			+ "            ON latest.max_payment_id = iph1.invoice_payment_id "
			+ "    ) latest_status "
			+ "        ON latest_status.invoice_id = im.invoice_id "
			+ "    WHERE im.is_delete = FALSE "
			+ "        AND im.invoice_date < STR_TO_DATE(:startDate, '%d/%m/%Y') "
			+ "        AND ( "
			+ "            latest_status.status IS NULL "
			+ "            OR latest_status.status = 'UNPAID' "
			+ "            OR latest_status.status = 'PENDING' "
			+ "        ) "
			+ " "
			+ "        AND ( "
			+ "            im.total_amount "
			+ "            - COALESCE(prev_payment.total_paid_before_opening, 0) "
			+ "        ) > 0 "
			+ " "
			+ ") AS data ", nativeQuery = true)
	List<Object[]> getPrevAllMonthData(@Param("startDate") String startDate, @Param("endDate") String endDate);
	
	@Query(value = " "
			+ " SELECT "
			+ " 	im.total_amount AS total_amount, "
			+ " 	payment.amount AS paid_amount, "
			+ " 	im.total_amount - payment.amount AS unpaid_amount "
			+ " FROM invoice_master im "
			+ " LEFT JOIN ("
			+ " 	SELECT "
			+ " 		iph.invoice_id,"
			+ " 		SUM(iph.amount) AS amount "
			+ " 	FROM invoice_payment_history iph"
			+ " 	WHERE iph.is_delete = FALSE "
			+ " 	GROUP BY iph.invoice_id "
			+ " ) AS payment ON payment.invoice_id = im.invoice_id "
			+ " WHERE im.is_delete = FALSE "
			+ " AND (:date IS NULL OR im.invoice_date < :date) ", nativeQuery = true)
	List<Object[]> getTotalData(@Param("date") LocalDate date);

//	List<InvoicePaymentHistoryEntity> findAllByInvoice_InvoiceIdAndIsDeleteFalseOrderByInvoicePaymentIdAsc(
//			Long invoiceId);
//
	List<InvoicePaymentHistoryEntity> findAllByInvoice_InvoiceIdAndIsDeleteFalseOrderByPaymentDateAsc(Long invoiceId);
}
