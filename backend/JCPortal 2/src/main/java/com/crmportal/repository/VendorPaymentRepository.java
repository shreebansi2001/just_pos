package com.crmportal.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.response.dto.AllVendorsPaymentResponseDto;

@Repository
public interface VendorPaymentRepository extends JpaRepository<VendorPaymentEntity, Long> {

	@Query("SELECT new com.crmportal.response.dto.AllVendorsPaymentResponseDto(" + "CASE "
			+ " WHEN MAX(ma.chefLabour) = true THEN 'Chef' " + " WHEN MAX(ma.outside) = true THEN 'Outside' " + " ELSE '' END, "
			+ "p.id, " + "MAX(p.nameEnglish), " +
//		       "FUNCTION('DATE_FORMAT', MAX(e.eventStartDateTime), '%d/%m/%Y'), " +
//		       "FUNCTION('DATE_FORMAT', MAX(e.eventEndDateTime), '%d/%m/%Y'), " +
//		       "SUM(o.counterQuantity), " +
//		       "SUM(o.counterPrice), " +
//		       "SUM(o.helperQuantity), " +
//		       "SUM(o.helperPrice), " +
//		       "MAX(o.price), " +
//		       "CASE " +
//		       " WHEN ma.chefLabour = true THEN (SUM(o.counterQuantity) + SUM(o.helperQuantity)) " +
//		       " WHEN ma.outside = true THEN SUM(o.quantity) " +
//		       " ELSE 0 END, " +
			"SUM(o.totalPrice), "
			+ "COALESCE((SELECT SUM(vp.payAmount + vp.settlementAmount) FROM VendorPaymentEntity vp WHERE vp.vendorId = p.id AND vp.eventId = e.id AND vp.isDelete = false AND vp.isPayable = TRUE),0), "
			+ "(SUM(o.totalPrice) - COALESCE((SELECT SUM(vp.payAmount + vp.settlementAmount) FROM VendorPaymentEntity vp WHERE vp.vendorId = p.id AND vp.eventId = e.id AND vp.isDelete = false AND vp.isPayable = TRUE),0)) "
			+ ") " + "FROM MenuAllocationOrdersEntity o " + "JOIN o.menuAllocation ma " + "JOIN ma.event e "
			+ "JOIN o.party p " + "WHERE e.id = :eventId AND o.isDelete = false AND ma.inside = FALSE "
			+ "GROUP BY p.id")
	List<AllVendorsPaymentResponseDto> getVendorPaymentsByEvent(Long eventId);

	@Query(value = "SELECT invoice_code FROM vendor_payments WHERE user_id = :userId ORDER BY invoice_code DESC LIMIT 1", nativeQuery = true)
	String findTopByUserIdOrderByIdDesc(@Param("userId") Long userId);

	VendorPaymentEntity findByIdAndIsDeleteFalse(Long id);

	List<VendorPaymentEntity> findAllByEventIdAndVendorIdAndIsDeleteFalse(Long eventId, Long vendorId);

	List<VendorPaymentEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity entity);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<VendorPaymentEntity> findAllByEventIdAndVendorIdAndIsDeleteFalseAndIsPayable(Long eventId, Long vendorId,
			Boolean isPayable);

	List<VendorPaymentEntity> findAllByUserAndIsDeleteFalseAndIsPayable(UserMasterEntity entity, Boolean isPayable);

	@Query(value = "SELECT SUM(total_amount) FROM ( " + "SELECT COALESCE(SUM(m.total_price),0) AS total_amount "
			+ "FROM eventfunction_menuallocation_order m " + "JOIN eventfunction_menuallocation ma "
			+ "ON ma.menu_allocation_id = m.menu_allocation_id " + "JOIN events e ON ma.event_id = e.event_id "
			+ "WHERE e.user_id = :userId " + "AND m.is_delete = false " +

			"UNION ALL " +

			"SELECT COALESCE(SUM(l.totalprice),0) " + "FROM event_labor l "
			+ "JOIN events e ON e.event_id = l.event_id " + "WHERE e.user_id = :userId " + ") t", nativeQuery = true)
	BigDecimal getTotalPayableAmountByUser(Long userId);

	@Query("SELECT new com.crmportal.response.dto.AllVendorsPaymentResponseDto(" + "MAX(c.nameEnglish), " + "p.id, "
			+ "MAX(p.nameEnglish), " + "SUM(o.totalprice), "
			+ "COALESCE((SELECT SUM(vp.payAmount + vp.settlementAmount) FROM VendorPaymentEntity vp WHERE vp.vendorId = p.id AND vp.eventId = e.id AND vp.isDelete = false AND vp.isPayable = TRUE),0), "
			+ "(SUM(o.totalprice) - COALESCE((SELECT SUM(vp.payAmount + vp.settlementAmount) FROM VendorPaymentEntity vp WHERE vp.vendorId = p.id AND vp.eventId = e.id AND vp.isDelete = false AND vp.isPayable = TRUE),0)) "
			+ ") " + "FROM EventLaborEntity o " + "JOIN o.event e " + "JOIN o.contact p " + "JOIN o.contactCategory c "
			+ "WHERE e.id = :eventId " + "GROUP BY p.id")
	List<AllVendorsPaymentResponseDto> getLabourVendorPaymentsByEvent(Long eventId);

	@Query(value = "SELECT t.event_id, t.name_english, " +
	        "       (SUM(t.total_amount) - COALESCE(vp.total_paid, 0)) AS remaining_amount, t.event_date " +

	        "FROM ( " +

	        // CHEF/OUTSIDE/INSIDE
	        "   SELECT e.event_id, et.name_english, SUM(m.total_price) AS total_amount, DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y') as event_date  " +
	        "   FROM eventfunction_menuallocation_order m " +
	        "   JOIN eventfunction_menuallocation ma ON ma.menu_allocation_id = m.menu_allocation_id " +
	        "   JOIN events e ON ma.event_id = e.event_id " +
	        "   JOIN eventtype et ON e.event_type_id = et.event_type_id " +
	        "   WHERE e.user_id = :userId AND m.party_id = :partyId AND m.is_delete = false AND e.status = 1 " +
	        "   GROUP BY e.event_id, et.name_english " +

	        "   UNION ALL " +

	        // LABOR
	        "   SELECT e.event_id, et.name_english, SUM(l.totalprice) AS total_amount, DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y') as event_date " +
	        "   FROM event_labor l " +
	        "   JOIN events e ON e.event_id = l.event_id " +
	        "   JOIN eventtype et ON e.event_type_id = et.event_type_id " +
	        "   WHERE e.user_id = :userId AND l.party_id = :partyId AND e.status = 1 " +
	        "   GROUP BY e.event_id, et.name_english " +

	        "   UNION ALL " +

	        // PURCHASE ORDER
	        "   SELECT -1 AS event_id, 'Purchase Order' AS name_english, SUM(po.finalamount) AS total_amount, DATE_FORMAT(CURDATE(), '%d/%m/%Y') AS event_date  " +
	        "   FROM purchaseorder po " +
	        "   WHERE po.user_id = :userId AND po.supplier_id = :partyId AND po.is_delete = FALSE " +
	        "   HAVING SUM(po.finalamount) IS NOT NULL " +

	        "   UNION ALL " +

	        // (Invoice > Quotation > 0)
	        "   SELECT e.event_id, et.name_english, " +
	        "   CASE " +
	        "       WHEN EXISTS (SELECT 1 FROM event_invoice ei WHERE ei.event_id = e.event_id AND ei.is_delete = FALSE) " +
	        "       THEN (SELECT SUM(IFNULL(ei.grand_total,0)) FROM event_invoice ei WHERE ei.event_id = e.event_id AND ei.is_delete = FALSE) " +

	        "       WHEN EXISTS (SELECT 1 FROM quotations q WHERE q.event_id = e.event_id AND q.is_delete = FALSE) " +
	        "       THEN (SELECT SUM(IFNULL(q.grand_total,0)) FROM quotations q WHERE q.event_id = e.event_id AND q.is_delete = FALSE) " +

	        "       ELSE 0 " +
	        "   END AS total_amount, DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y') as event_date " +

	        "   FROM events e " +
	        "   JOIN eventtype et ON e.event_type_id = et.event_type_id " +
	        "   WHERE e.user_id = :userId AND e.party_id = :partyId AND e.status = 1 " +

	        ") t " +

	        "LEFT JOIN ( " +
	        "   SELECT v.event_id, v.vendor_id, SUM(COALESCE(v.pay_amount,0) + COALESCE(v.received_amount, 0)) AS total_paid, DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y') as event_date " +
	        "   FROM vendor_payments v " +
	        " 	LEFT JOIN events e ON v.event_id = e.event_id "+
	        "   WHERE v.is_delete = false " +
	        "   GROUP BY v.event_id, v.vendor_id " +
	        ") vp ON vp.event_id = t.event_id AND vp.vendor_id = :partyId " +

	        "GROUP BY t.event_id, t.name_english, vp.total_paid, t.event_date",
	        nativeQuery = true)
	List<Object[]> getAllEventByParty(Long partyId, Long userId);

	@Query(value =
	        "SELECT DATE(ledger.txn_date) AS txn_date, " +
	        "       ledger.invoice_no, " +
	        "       ledger.account_name, " +
	        "       ledger.type, " +
	        "       SUM(ledger.credit) AS credit, " +
	        "       SUM(ledger.debit) AS debit, " +
	        "       ledger.remarks " +
	        "FROM ( " +

	        // =========================
	        // EVENT / EP
	        // =========================
	        "    SELECT DATE(e.event_start_date_time) AS txn_date, " +
	        "           CONVERT( " +
	        "               CASE " +
	        "                   WHEN :type = 'invoice' THEN ei.invoice_code " +
	        "                   WHEN :type = 'quotation' THEN q.quotation_code " +
	        "                   ELSE COALESCE(ei.invoice_code, q.quotation_code) " +
	        "               END USING utf8 " +
	        "           ) COLLATE utf8_unicode_ci AS invoice_no, " +
	        "           CONVERT( " +
	        "               CASE " +
	        "                   WHEN :type = 'invoice' THEN ei.billingname " +
	        "                   WHEN :type = 'quotation' THEN q.billingname " +
	        "                   ELSE COALESCE(ei.billingname, q.billingname) " +
	        "               END USING utf8 " +
	        "           ) COLLATE utf8_unicode_ci AS account_name, " +
	        "           CONVERT('EP' USING utf8) COLLATE utf8_unicode_ci AS type, " +
	        "           0 AS credit, " +
	        "           SUM( CASE " +
	        "               WHEN :type = 'invoice' THEN IFNULL(ei.grand_total, 0) " +
	        "               WHEN :type = 'quotation' THEN IFNULL(q.grand_total, 0) " +
	        "               ELSE " +
	        "                   CASE " +
	        "                       WHEN ei.event_id IS NOT NULL THEN IFNULL(ei.grand_total, 0) " +
	        "                       WHEN q.event_id IS NOT NULL THEN IFNULL(q.grand_total, 0) " +
	        "                       ELSE 0 " +
	        "                   END " +
	        "           END) AS debit, " +
	        "           1 AS category_order, " +
	        "           CASE " +
	        "               WHEN :type = 'invoice' THEN ei.notes " +
	        "               WHEN :type = 'quotation' THEN q.notes " +
	        "               ELSE COALESCE(ei.notes, q.notes) " +
	        "           END AS remarks " +
	        "    FROM events e " +
	        "    LEFT JOIN event_invoice ei ON ei.event_id = e.event_id " +
	        "        AND ei.is_delete = FALSE " +
	        "        AND (:type IN ('both', 'invoice')) " +
	        "    LEFT JOIN quotations q ON q.event_id = e.event_id " +
	        "        AND q.is_delete = FALSE " +
	        "        AND (:type IN ('both', 'quotation')) " +
	        "    WHERE e.user_id = :userId " +
	        "      AND e.party_id = :partyId " +
	        "      AND e.status = 1 " +
	        "      AND e.is_delete = FALSE " +
	        "      AND DATE(e.event_start_date_time) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "      AND (ei.event_id IS NOT NULL OR q.event_id IS NOT NULL) " +
	        "    GROUP BY DATE(e.event_start_date_time), invoice_no, account_name, remarks " +

	        "    UNION ALL " +

	        // =========================
	        // EVENT PAYMENT / RECEIVABLE
	        // =========================
	        "    SELECT base.txn_date, " +
	        "           CONVERT(base.invoice_no USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT(base.account_name USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('EP' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           0, " +
	        "           (base.total_amount - IFNULL(vp.received_amount, 0)), " +
	        "           6, " +
	        "           CONVERT(COALESCE(vp.remarks,base.remarks) USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM ( " +
	        "        SELECT DATE(e.created_at) AS txn_date, " +
	        "               CONVERT( " +
	        "                   CASE " +
	        "                       WHEN :type = 'invoice' THEN ei.invoice_code " +
	        "                       WHEN :type = 'quotation' THEN q.quotation_code " +
	        "                       ELSE COALESCE(ei.invoice_code, q.quotation_code) " +
	        "                   END USING utf8 " +
	        "               ) COLLATE utf8_unicode_ci AS invoice_no, " +
	        "               CONVERT( " +
	        "                   CASE " +
	        "                       WHEN :type = 'invoice' THEN ei.billingname " +
	        "                       WHEN :type = 'quotation' THEN q.billingname " +
	        "                       ELSE COALESCE(ei.billingname, q.billingname) " +
	        "                   END USING utf8 " +
	        "               ) COLLATE utf8_unicode_ci AS account_name, " +
	        "               e.user_id, " +
	        "             SUM( CASE " +
	        "                   WHEN :type = 'invoice' THEN IFNULL(ei.grand_total, 0) " +
	        "                   WHEN :type = 'quotation' THEN IFNULL(q.grand_total, 0) " +
	        "                   ELSE " +
	        "                       CASE " +
	        "                           WHEN ei.event_id IS NOT NULL THEN IFNULL(ei.grand_total, 0) " +
	        "                           WHEN q.event_id IS NOT NULL THEN IFNULL(q.grand_total, 0) " +
	        "                           ELSE 0 " +
	        "                       END " +
	        "               END) AS total_amount, " +
	        "               CASE " +
	        "                   WHEN :type = 'invoice' THEN ei.notes " +
	        "                   WHEN :type = 'quotation' THEN q.notes " +
	        "                   ELSE COALESCE(ei.notes, q.notes) " +
	        "               END AS remarks " +
	        "        FROM events e " +
	        "        LEFT JOIN event_invoice ei ON ei.event_id = e.event_id " +
	        "            AND ei.is_delete = FALSE " +
	        "            AND (:type IN ('both', 'invoice')) " +
	        "        LEFT JOIN quotations q ON q.event_id = e.event_id " +
	        "            AND q.is_delete = FALSE " +
	        "            AND (:type IN ('both', 'quotation')) " +
	        "        WHERE e.user_id = :userId " +
	        "          AND e.party_id = :partyId " +
	        "          AND e.is_delete = FALSE " +
	        "          AND e.status = 1 " +
	        "          AND DATE(e.created_at) BETWEEN " +
	        "              STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "              STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "        GROUP BY DATE(e.created_at), invoice_no, account_name, e.user_id, remarks " +
	        "    ) base " +
	        "    LEFT JOIN ( " +
	        "        SELECT DATE(created_at) AS txn_date, " +
	        "               vendor_id, " +
	        "               SUM(IFNULL(received_amount, 0)) AS received_amount, " +
	        "               remarks " +
	        "        FROM vendor_payments " +
	        "        WHERE is_delete = 0 " +
	        "          AND vendor_id = :partyId " +
	        "          AND DATE(created_at) BETWEEN " +
	        "              STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "              STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "        GROUP BY DATE(created_at), vendor_id, remarks " +
	        "    ) vp ON vp.vendor_id = base.user_id " +
	        "        AND vp.txn_date = base.txn_date " +
	        "    WHERE IFNULL(vp.received_amount, 0) > 0 " +

	        "    UNION ALL " +

	        // =========================
	        // OPENING BALANCE
	        // =========================
	        "    SELECT pm.opb_date, " +
	        "           CONVERT('OPB' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('Opening Balance' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('OPB/PREV' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           0, IFNULL(pm.opb, 0), 2, " +
	        "           CONVERT('' USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM partymaster pm " +
	        "    WHERE pm.party_id = :partyId " +
	        "      AND pm.is_delete = 0 " +
	        "      AND pm.user_id = :userId " +
	        "      AND pm.opb_date IS NOT NULL " +
	        "      AND IFNULL(pm.opb, 0) <> 0 " +
	        "      AND DATE(pm.opb_date) BETWEEN " +
	        "          DATE_ADD(STR_TO_DATE(:startDate,'%d/%m/%Y'), INTERVAL 1 DAY) " +
	        "          AND STR_TO_DATE(:endDate,'%d/%m/%Y') " +

	        "    UNION ALL " +

	        // =========================
	        // MENU ALLOCATION
	        // =========================
	        "    SELECT DATE(e.event_start_date_time), " +
	        "           CONVERT(CONCAT(e.event_no,' (',DATE_FORMAT(e.event_start_date_time,'%d/%m/%Y'),')') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT(IFNULL(pm.name_english,'Unknown') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('EP' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           SUM(IFNULL(mo.total_price, 0)), 0, 3, " +
	        "           CONVERT(COALESCE(mo.remarks,'') USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM eventfunction_menuallocation em " +
	        "    JOIN events e ON em.event_id = e.event_id " +
	        "        AND e.is_delete = FALSE " +
	        "    JOIN eventfunction_menuallocation_order mo " +
	        "        ON em.menu_allocation_id = mo.menu_allocation_id " +
	        "    LEFT JOIN partymaster pm ON pm.party_id = mo.party_id " +
	        "    WHERE mo.party_id = :partyId " +
	        "      AND mo.is_delete = 0 " +
	        "      AND DATE(e.event_start_date_time) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "    GROUP BY DATE(e.event_start_date_time), e.event_no, " +
	        "             e.event_start_date_time, pm.party_id, " +
	        "             pm.name_english, mo.remarks " +

	        "    UNION ALL " +

	        // =========================
	        // PAYMENTS
	        // =========================
	        "    SELECT vp.payment_date, " +
	        "           CONVERT(IFNULL(vp.invoice_code, CONCAT('PAY-', vp.vendor_pay_id)) USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('Payment' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT(CASE " +
	        "               WHEN IFNULL(vp.received_amount,0) > 0 AND IFNULL(vp.bank_id,0) > 0 THEN 'BR' " +
	        "               WHEN IFNULL(vp.received_amount,0) > 0 THEN 'CR' " +
	        "               WHEN IFNULL(vp.pay_amount,0) > 0 AND IFNULL(vp.bank_id,0) > 0 THEN 'BP' " +
	        "               ELSE 'CP' " +
	        "           END USING utf8) COLLATE utf8_unicode_ci, " +
	        "           IFNULL(vp.received_amount, 0), " +
	        "           IFNULL(vp.pay_amount, 0), " +
	        "           4, " +
	        "           CONVERT(COALESCE(vp.remarks,'') USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM vendor_payments vp " +
	        "    WHERE vp.vendor_id = :partyId " +
	        "      AND vp.user_id = :userId " +
	        "      AND vp.is_delete = 0 " +
	        "      AND DATE(vp.payment_date) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	       
	        "    UNION ALL " +

	        // =========================
	        // SETTLEMENT
	        // =========================
	        "    SELECT vp.payment_date, " +
	        "           CONVERT(CONCAT(IFNULL(vp.invoice_code, CONCAT('PAY-', vp.vendor_pay_id)), '-SETTLEMENT') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('Settlement' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('STL' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           0, " +
	        "           IFNULL(vp.settlement_amount, 0), " +
	        "           5, " +
	        "           CONVERT(COALESCE(vp.remarks,'') USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM vendor_payments vp " +
	        "    WHERE vp.vendor_id = :partyId " +
	        "      AND vp.user_id = :userId " +
	        "      AND vp.is_delete = 0 " +
	        "      AND IFNULL(vp.settlement_amount, 0) > 0 " +
	        "      AND DATE(vp.payment_date) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +

	        "    UNION ALL " +

	        // =========================
	        // EVENT LABOR
	        // =========================
	        "    SELECT DATE(e.event_start_date_time), " +
	        "           CONVERT(CONCAT(e.event_no,' (',DATE_FORMAT(e.event_start_date_time,'%d/%m/%Y'),')') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT(IFNULL(pm.name_english,'Unknown') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('EP' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           SUM(IFNULL(el.totalprice, 0)), " +
	        "           0, " +
	        "           3, " +
	        "           CONVERT(MAX(IFNULL(el.notes_english,'')) USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM event_labor el " +
	        "    JOIN events e ON el.event_id = e.event_id " +
	        "        AND e.is_delete = FALSE " +
	        "    LEFT JOIN partymaster pm ON pm.party_id = el.party_id " +
	        "    WHERE el.party_id = :partyId " +
	        "      AND DATE(el.labordatetime) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "    GROUP BY DATE(e.event_start_date_time), e.event_no, " +
	        "             e.event_start_date_time, pm.party_id, " +
	        "             pm.name_english " +

	        "    UNION ALL " +

	        // =========================
	        // PURCHASE
	        // =========================
	        "    SELECT DATE(po.podate), " +
	        "           CONVERT(po.billno USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT(IFNULL(pm.name_english,'Unknown') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('PO' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           SUM(IFNULL(po.finalamount, 0)), " +
	        "           0, " +
	        "           3, " +
	        "           CONVERT(po.remarks USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM purchaseorder po " +
	        "    LEFT JOIN partymaster pm ON pm.party_id = po.supplier_id " +
	        "    WHERE po.supplier_id = :partyId " +
	        "      AND po.is_delete = FALSE " +
	        "      AND DATE(po.podate) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "      AND (po.billno IS NOT NULL AND LENGTH(TRIM(po.billno)) != 0) " +
	        "    GROUP BY DATE(po.podate), po.billno, pm.party_id, " +
	        "             pm.name_english, po.remarks " +

	        "    UNION ALL " +

	        // =========================
	        // PURCHASE RETURN
	        // =========================
	        "    SELECT DATE(po.returndate), " +
	        "           CONVERT(po.billno USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT(IFNULL(pm.name_english,'Unknown') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('PO' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           0, " +
	        "           SUM(IFNULL(po.finalamount, 0)), " +
	        "           4, " +
	        "           CONVERT(po.remarks USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM purchaseorderreturn po " +
	        "    LEFT JOIN partymaster pm ON pm.party_id = po.supplier_id " +
	        "    LEFT JOIN purchaseorder p ON p.po_id = po.po_id " +
	        "        AND (p.billno IS NOT NULL AND LENGTH(TRIM(p.billno)) != 0) " +
	        "    WHERE po.supplier_id = :partyId " +
	        "      AND po.is_delete = FALSE " +
	        "      AND DATE(po.returndate) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "    GROUP BY DATE(po.returndate), po.billno, pm.party_id, " +
	        "             pm.name_english, po.remarks " +

	        "    UNION ALL " +

	        // =========================
	        // JOURNAL VOUCHER
	        // =========================
	        "    SELECT DATE(jv.voucher_date), " +
	        "           CONVERT(jv.voucher_no USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT(IFNULL(pm.name_english,'Journal Voucher') USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CONVERT('JV' USING utf8) COLLATE utf8_unicode_ci, " +
	        "           SUM(CASE " +
	        "               WHEN jvd.credit_debit='CR' THEN IFNULL(jvd.amount,0) " +
	        "               ELSE 0 " +
	        "           END) AS credit, " +
	        "           SUM(CASE " +
	        "               WHEN jvd.credit_debit='DR' THEN IFNULL(jvd.amount,0) " +
	        "               ELSE 0 " +
	        "           END) AS debit, " +
	        "           7, " +
	        "           CONVERT(COALESCE(jvd.particular,jv.narration,'') USING utf8) COLLATE utf8_unicode_ci " +
	        "    FROM journal_voucher jv " +
	        "    INNER JOIN journal_voucher_detail jvd " +
	        "        ON jvd.voucher_id = jv.id " +
	        "        AND jvd.is_delete = 0 " +
	        "    LEFT JOIN partymaster pm ON pm.party_id = jvd.party_id " +
	        "    WHERE jv.user_id = :userId " +
	        "      AND jvd.party_id = :partyId " +
	        "      AND jv.is_delete = 0 " +
	        "      AND DATE(jv.voucher_date) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        "    GROUP BY DATE(jv.voucher_date), jv.voucher_no, " +
	        "             pm.party_id, pm.name_english, " +
	        "             jvd.particular, jv.narration " +

			" " + 

	        "    UNION ALL " +

	        // =========================
	        // SECURITY DEPOSIT
	        // =========================

	        "    SELECT DATE(s.payment_date_time), " +
	        "           CONVERT(CONCAT('SD-', s.event_quotation_security_deposit_id) USING utf8) " +
	        "               COLLATE utf8_unicode_ci, " +
	        "           CONVERT(IFNULL(pm.name_english, 'Security Deposit') USING utf8) " +
	        "               COLLATE utf8_unicode_ci, " +
	        "           CONVERT(CASE " +
	        "               WHEN s.entry_type = 'RECEIPT' " +
	        "                    AND IFNULL(s.bank_account_id, 0) > 0 THEN 'BR' " +
	        "               WHEN s.entry_type = 'RECEIPT' THEN 'CR' " +
	        "               WHEN s.entry_type = 'PAYMENT' " +
	        "                    AND IFNULL(s.bank_account_id, 0) > 0 THEN 'BP' " +
	        "               ELSE 'CP' " +
	        "           END USING utf8) COLLATE utf8_unicode_ci, " +
	        "           CASE " +
	        "               WHEN s.entry_type = 'RECEIPT' " +
	        "                    THEN IFNULL(s.amount, 0) " +
	        "               ELSE 0 " +
	        "           END AS credit, " +
	        "           CASE " +
	        "               WHEN s.entry_type = 'PAYMENT' " +
	        "                    THEN IFNULL(s.amount, 0) " +
	        "               ELSE 0 " +
	        "           END AS debit, " +
	        "           8, " +
	        "           CONVERT(COALESCE(s.description, '') USING utf8) " +
	        "               COLLATE utf8_unicode_ci " +
	        "    FROM event_quotation_security_deposit s " +
	        "    LEFT JOIN events e ON e.event_id = s.event_id " +
	        "        AND e.is_delete = FALSE " +
	        "    LEFT JOIN partymaster pm ON pm.party_id = e.party_id " +
	        "    WHERE s.is_delete = FALSE " +
	        "      AND e.user_id = :userId " +
	        "      AND e.party_id = :partyId " +
	        "      AND DATE(s.payment_date_time) BETWEEN " +
	        "          STR_TO_DATE(:startDate,'%d/%m/%Y') AND " +
	        "          STR_TO_DATE(:endDate,'%d/%m/%Y') " +
	        
	        ") ledger " +

	        "GROUP BY DATE(ledger.txn_date), " +
	        "         ledger.invoice_no, " +
	        "         ledger.account_name, " +
	        "         ledger.type, " +
	        "         ledger.category_order, " +
	        "         ledger.remarks " +

	        "ORDER BY DATE(ledger.txn_date), ledger.category_order",

	        nativeQuery = true)
	List<Object[]> getGroupedLedger(
	        @Param("partyId") Long partyId,
	        @Param("userId") Long userId,
	        @Param("startDate") String startDate,
	        @Param("endDate") String endDate,
	        @Param("type") String type);
		
	List<VendorPaymentEntity> findAllByVendorIdAndIsDeleteFalse(Long vendorId);

	List<VendorPaymentEntity> findAllByEventIdAndIsDeleteFalse(Long eventId);
	
	@Query(" SELECT vp "
			+ " FROM VendorPaymentEntity vp "
			+ " WHERE vp.user.id = :userId "
			+ " AND vp.receivedAmount != 0 "
			+ " AND (:startDate IS NULL OR vp.paymentDate >= :startDate) "
			+ " AND (:endDate IS NULL OR vp.paymentDate <= :endDate) "
			+ " AND vp.isDelete = FALSE ")
	List<VendorPaymentEntity> getReceivedPaymentList(
			@Param("userId") Long userId, 
			@Param("startDate") LocalDate startDate, 
			@Param("endDate") LocalDate endDate
	);

	
	@Query(value =
	        "SELECT COALESCE(SUM( " +
	        "    CASE " +
	        "        WHEN EXISTS ( " +
	        "            SELECT 1 " +
	        "            FROM event_invoice ei " +
	        "            WHERE ei.event_id = e.event_id " +
	        "              AND ei.is_delete = FALSE " +
	        "        ) " +
	        "        THEN ( " +
	        "            SELECT COALESCE(SUM(ei2.grand_total), 0) " +
	        "            FROM event_invoice ei2 " +
	        "            WHERE ei2.event_id = e.event_id " +
	        "              AND ei2.is_delete = FALSE " +
	        "        ) " +
	        "        ELSE ( " +
	        "            SELECT COALESCE(SUM(q.grand_total), 0) " +
	        "            FROM quotations q " +
	        "            WHERE q.event_id = e.event_id " +
	        "              AND q.is_delete = FALSE " +
	        "        ) " +
	        "    END " +
	        "), 0) " +
	        "FROM events e " +
	        "WHERE e.user_id = :userId " +
	        "  AND e.is_delete = FALSE",
	        nativeQuery = true)
	BigInteger getTotalReceiveAmountByUser(@Param("userId") Long userId);

	VendorPaymentEntity findByInvoiceCodeAndVendorIdAndIsDeleteFalse(String vendorCode, Long id);

}
