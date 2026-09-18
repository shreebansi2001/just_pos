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
import com.crmportal.response.dto.CurrentMonthPaidInvoicesResponseDto;

@Repository
public interface InvoiceEntityRepository extends JpaRepository<InvoiceEntity, Long> {

	@Query(value = "SELECT i.id, i.party_name,i.plan_name, i.grand_total, i.user_id FROM invoice_master i WHERE i.is_delete = false ", nativeQuery = true)
	List<Object[]> getAllAdminInvoice();

	@Query(value = "SELECT COALESCE(sum(i.total_amount),0) FROM invoice_master i WHERE i.is_delete = false ", nativeQuery = true)
	BigDecimal getTotalAmt();
	
	Optional<InvoiceEntity> findByInvoiceIdAndIsDeleteFalse(Long invoiceId);

	List<InvoiceEntity> findAllByIsDeleteFalse();
	
	@Query(value = "SELECT invoice_code FROM invoice_master " +
		       "WHERE is_delete = false " +
		       "ORDER BY invoice_id DESC LIMIT 1",
		       nativeQuery = true)
	String getLastInvoiceNo();
	
	Optional<InvoiceEntity> findByInvoiceCodeAndIsDeleteFalse(String invoiceCode);
	
	@Query("SELECT i FROM InvoiceEntity i " +
		       " WHERE (:stDate IS NULL OR i.createdAt >= :stDate) " +
		       " AND (:eDate IS NULL OR i.createdAt <= :eDate) " +
		       " AND i.isDelete = FALSE " +
		       " ORDER BY i.invoiceCode ASC  ")
	List<InvoiceEntity> getAllInvoiceBetweenStartDateAndEndDate(
		        @Param("stDate") LocalDateTime startDate,
		        @Param("eDate") LocalDateTime endDate);
	
	@Query(value = ""
			+ " SELECT "
			+ " 	im.invoice_id,"
			+ " 	im.invoice_code,"
			+ " 	im.billing_name,"
			+ " 	im.billing_address,"
			+ " 	im.total_amount,"
			+ " 	u.user_id,"
			+ " 	CONCAT(u.first_name, ' ', u.last_name) AS name,"
			+ " 	iph.invoice_payment_id,"
			+ " 	iph.amount,"
			+ " 	iph.payment_date,"
			+ " 	iph.payment_mode,"
			+ " 	iph.bank_account_id,"
			+ " 	iph.cash_type_id,"
			+ " 	iph.status,"
			+ " 	im.invoice_date "
			+ " FROM invoice_master im"
			+ " LEFT JOIN invoice_payment_history iph ON im.invoice_id = iph.invoice_id AND iph.is_delete = false "
			+ " INNER JOIN users u ON im.customer_id = u.user_id "
			+ " WHERE :userId = 1 "
			+ "	AND im.invoice_date <= STR_TO_DATE(:date, '%d/%m/%Y')"
			+ " AND im.is_delete = false "
			+ " "
			+ " UNION ALL "
			+ " "
			+ " SELECT "
			+ " 	ei.invoice_id,"
			+ " 	ei.invoice_code,"
			+ " 	ei.billingname,"
			+ " 	ei.billingaddress,"
			+ " 	ei.total_amount,"
			+ " 	pm.party_id,"
			+ " 	pm.name_english AS name,"
			+ " 	si.id,"
			+ " 	si.total_amount,"
			+ " 	si.payment_date,"
			+ " 	si.payment_mode,"
			+ " 	si.bank_id,"
			+ " 	si.cash_id,"
			+ " 	si.status,"
			+ " 	DATE(ei.created_at) AS invoice_date "
			+ " FROM event_invoice ei "
			+ " LEFT JOIN sales_invoice si ON si.invoice_no = ei.invoice_code AND si.is_delete = false "
			+ " LEFT JOIN `events` e ON e.event_id = ei.event_id AND e.is_delete = FALSE "
			+ " LEFT JOIN partymaster pm ON e.party_id = pm.party_id "
			+ " WHERE (:userId != 1 AND ei.user_id = :userId) "
			+ "	AND ei.created_at < DATE_ADD(STR_TO_DATE(:date, '%d/%m/%Y'), INTERVAL 1 DAY) "
			+ " AND ei.is_delete = false ", nativeQuery = true)
	List<Object[]> getAllPrevInvoiceDetails(@Param("date") String date, @Param("userId") Long userId);
	
	@Query(value =
	        " SELECT "
	        + "    i.invoice_id AS invoice_id, "
	        + "    i.customer_id AS customer_id, "
	        + "    CONCAT(c.first_name, ' ', c.last_name) AS customer_name, "
	        + "    i.sales_person_id AS sales_person_id, "
	        + "    CONCAT(s.first_name, ' ', s.last_name) AS sales_person_name, "
	        + "    i.billing_name AS billing_name, "
	        + "    i.billing_address AS billing_address, "
	        + "    i.shipping_address AS shipping_address, "
	        + "    i.gst_number AS gst_number, "
	        + "    i.invoice_code AS invoice_code, "
	        + "    i.invoice_date AS invoice_date, "
	        + "    i.terms AS terms, "
	        + "    i.due_date AS due_date, "
	        + "    i.customer_notes AS cutomer_notes, "
	        + "    i.sub_total AS sub_total, "
	        + "    i.discount_per AS discount_per, "
	        + "    i.discount_amount AS discount_amount, "
	        + "    i.tax_type AS tax_type, "
	        + "    i.gst_percent AS gst_percent, "
	        + "    i.gst_amount AS gst_amount, "
	        + "    i.adjust_amount AS adjust_amount, "
	        + "    i.total_amount AS total_amount, "
	        + "    i.tnc AS tnc, "
	        + "    i.doc_path AS doc_path, "
	        + "    i.created_at AS created_at, "
	        + "    i.updated_at AS updated_at, "
	        + "    i.is_delete AS is_delete, "
	        + "    it.invoice_item_id AS invoice_item_id, "
	        + "    it.plan_history_id AS plan_history_id, "
	        + "    it.item_name AS item_name, "
	        + "    it.qty AS qty, "
	        + "    it.rate AS rate, "
	        + "    it.amount AS item_amount, "
	        + "    it.description AS description, "
	        + "    it.created_at AS item_created_at, "
	        + "    it.updated_at AS item_updated_at, "
	        + "    it.hsn_code AS hsn_code, "
	        + "    it.tax_percent AS item_tax_percent, "
	        + "    ph.invoice_payment_id AS invoice_payment_id, "
	        + "    ph.transaction_id AS transaction_id, "
	        + "    ph.payment_date AS payment_date, "
	        + "    ph.cheque_no AS cheque_no, "
	        + "    ph.amount AS paid_amount, "
	        + "    ph.due_amount AS due_amount, "
	        + "    ph.payment_mode AS payment_mode, "
	        + "    ph.status AS status, "
	        + "    ph.is_delete AS is_delete_payment, "
	        + "    ph.created_at AS payment_created_date, "
	        + "    ph.updated_at AS payment_updated_date, "
	        + "    bd.id, "
	        + "    bd.bank_name, "
	        + "    bd.branch_name, "
	        + "    bd.account_holder_name, "
	        + "    bd.account_no, "
	        + "    bd.ifsc_code, "
	        + "    bd.is_primary, "
	        + "    bd.upi_id, "
	        + "    bd.user_id,"
	        + "    it.tax_amount AS item_tax_amount "
	        + " FROM invoice_master i "
	        + " LEFT JOIN invoice_items it "
	        + "       ON it.invoice_id = i.invoice_id "
	        + "      AND it.is_delete = FALSE "
	        + " LEFT JOIN invoice_payment_history ph "
	        + "       ON ph.invoice_id = i.invoice_id "
	        + "      AND ph.is_delete = FALSE "
	        + " LEFT JOIN users c "
	        + "       ON c.user_id = i.customer_id "
	        + " LEFT JOIN users s "
	        + "       ON s.user_id = i.sales_person_id "
	        + " LEFT JOIN bank_details bd "
	        + " 	  ON bd.id = ph.bank_account_id "
	        + " WHERE i.is_delete = FALSE "
	        + " AND (:customerId IS NULL OR :customerId = -1 OR i.customer_id = :customerId) "
	        + " AND ((:startDate IS NULL OR i.invoice_date >= :startDate) "
	        + " AND (:endDate IS NULL OR i.invoice_date <= :endDate)) "
	        + " AND ( "
	        + "    :planId = -1  "
	        + "    OR EXISTS ( "
	        + "        SELECT 1 "
	        + "        FROM invoice_items it2 "
	        + "        JOIN user_plan_histories uph  "
	        + "             ON uph.plan_history_id = it2.plan_history_id "
	        + "        WHERE it2.invoice_id = i.invoice_id "
	        + "          AND it2.is_delete = FALSE "
	        + "          AND uph.plan_id = :planId "
	        + "    ) "
	        + " ) "
	        + " ORDER BY i.invoice_code ASC ",
	        nativeQuery = true)
	List<Object[]> getAllInvoiceFullData(
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate,
	        @Param("planId") Long planId,
	        @Param("customerId") Long customerId);
	
	
//	@Query("SELECT DISTINCT i "
//			+ " FROM InvoicePaymentHistoryEntity ph "
//			+ " JOIN ph.invoice i "
//			+ " WHERE (:startDate IS NULL OR ph.paymentDate >= :startDate)"
//			+ " AND (:endDate IS NULL OR ph.paymentDate <= :endDate) "
//			+ " AND ph.isDelete = FALSE "
//			+ " AND i.isDelete = FALSE ")
//	List<InvoiceEntity> getAllInvoicePaidThisMonth(
//			@Param("startDate") LocalDate startDate,
//			@Param("endDate") LocalDate endDate
//	);
	
	@Query("SELECT new com.crmportal.response.dto.CurrentMonthPaidInvoicesResponseDto( "
			+ " i, "
			+ " COALESCE(SUM(ph.amount), 0) "
			+ ") "
			+ " FROM InvoicePaymentHistoryEntity ph "
			+ " JOIN ph.invoice i "
			+ " WHERE (:startDate IS NULL OR ph.paymentDate >= :startDate) "
			+ " AND (:endDate IS NULL OR ph.paymentDate <= :endDate) "
			+ " AND ph.isDelete = FALSE "
			+ " AND i.isDelete = FALSE "
			+ " GROUP BY i ")
	List<CurrentMonthPaidInvoicesResponseDto> getPaidInvoicesWithAmount(
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
	
}
