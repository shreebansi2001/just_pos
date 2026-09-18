package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventFunctionQuotationPaymentEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.request.dto.EventFunctionQuotationPaymentRequestDto;

@Repository
public interface EventFunctionQuotationPaymentRepository extends JpaRepository<EventFunctionQuotationPaymentEntity, Long>{

	EventFunctionQuotationPaymentEntity findByIdAndIsDeleteFalse(Long id);

	List<EventFunctionQuotationPaymentEntity> findAllByEventFunctionQuotationAndIsDeleteFalse(
			EventFunctionQuotationEntity eventFunctionQuotationEntity);

	@Query(value = "SELECT q.quotation_id, e.party_id, e.event_id, e.event_no,"
			+ " CASE WHEN q.billingname IS NULL OR LENGTH(TRIM(q.billingname)) = 0 THEN CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END ELSE q.billingname END AS party_name, "
			+ " CASE WHEN :lang = 0 THEN pm.address_english WHEN :lang = 1 THEN pm.address_hindi WHEN :lang = 2 THEN pm.address_gujarati END AS party_address,  "
			+ " pm.email, "
			+ " pm.mobileno, "
			+ " CASE WHEN q.gstnumber IS NULL OR LENGTH(TRIM(q.gstnumber)) = 0 THEN pm.gst ELSE q.gstnumber END AS gst, "
			 + " pm.pan AS pan, "
			+ " DATE_FORMAT(q.quotationdate, '%d/%m/%Y') AS duedate, "
			+ " q.quotation_code, "
			+ " CONCAT("
			+ " DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y'), "
			+ " ' To ', "
			+ " DATE_FORMAT(e.event_end_date_time, '%d/%m/%Y') "
			+ " ) AS event_date, "
			+ "q.sub_total, "
			+ "q.cash_payment, "
			+ "q.cheque_payment, "
			+ " CONCAT(q.cgst, '%') AS cgst, "
			+ " q.cgst_amnt, "
			+ " CONCAT(q.sgst, '%') AS sgst, "
			+ " q.sgst_amnt, "
			+ " CONCAT(q.igst, '%') AS igst, "
			+ " q.igst_amnt, "
			+ " q.discount, "
			+ " q.total_amount AS advance_paymant, "
			+ " q.remaining_amount, "
			+ " q.grand_total, "
			+ " ud.company_name, "
			+ " ud.country_code, "
			+ " ud.office_no, "
			+ " ud.company_email, "
			+ " ud.address, "
			+ " u.logo, "
			+ " bd.account_holder_name, "
			+ " bd.account_no, "
			+ " bd.bank_name, "
			+ " bd.ifsc_code, "
			+ " bd.upi_id, "
			+ " bd.branch_name,"
			+ " bd.qr_code_path,"
			+ " q.notes,DATE_FORMAT(e.inquiry_date, '%d/%m/%Y') as inquiry_date,e.banquet_hall_id,q.iron_service,q.pooja_rooms,q.venue_remark,q.venue_total, "
			+ " q.is_locked, "
			+ " et.name_english, "
			+ " CASE WHEN :lang = 0 THEN v.name_english WHEN :lang = 1 THEN v.name_hindi WHEN :lang = 2 THEN v.name_gujarati END AS venue, "
	        + " bhm.hall_name AS hall_name,"
	        + " ud.gst_number AS cmp_gst_number, "
	        + " q.created_at AS created_at, "
	        + " ud.pan_number AS pan_number,q.transportation, "
	        + " q.food_tax, "
	        + " q.food_tax_amount, "
	        + " q.food_tax_total_amount, "
	        + " q.service_tax, "
	        + " q.service_tax_amount, "
	        + " q.service_tax_total_amount, "
	        + " q.vat_tax, "
	        + " q.vat_tax_amount, "
	        + " q.vat_tax_total_amount, "
	        + " ud.fssai_number, "
	        + " ud.hsn_number, "
	        + " ud.cin_number, "
	        + " ud.fda_lincense, "
	        + " q.discount_percentage, e.prefix, e.venue as event_venue "
			+ " FROM events e "
			+ " LEFT JOIN partymaster pm ON e.party_id = pm.party_id "
			+ " LEFT JOIN venuemaster v ON v.venue_id = e.venue_id "
			+ " LEFT JOIN quotations q ON q.event_id = e.event_id AND q.is_decore = :isDecore "
			+ " LEFT JOIN users u ON u.user_id = e.user_id "
			+ " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " LEFT JOIN eventtype et ON e.event_type_id = et.event_type_id"
	        + " LEFT JOIN banquet_hall_master bhm ON bhm.id = e.banquet_hall_id "
			+ " LEFT JOIN bank_details bd ON (bd.user_id = u.user_id AND bd.is_primary = TRUE) "
			+ " WHERE e.event_id = :eventId AND e.user_id = :userid AND e.is_delete = FALSE ", 
			nativeQuery = true)
	Object getEventData(Long eventId, Long userid, int lang,Boolean isDecore);
	
	@Query(value = "SELECT q.invoice_id, e.party_id, e.event_id, e.event_no,"
	        + " CASE WHEN q.billingname IS NULL OR LENGTH(TRIM(q.billingname)) = 0 "
	        + " THEN CASE WHEN :lang = 0 THEN pm.name_english "
	        + "           WHEN :lang = 1 THEN pm.name_hindi "
	        + "           WHEN :lang = 2 THEN pm.name_gujarati END "
	        + " ELSE q.billingname END AS party_name, "
	        + " q.billingaddress, "
	        + " pm.email, "
	        + " pm.mobileno, "
	        + " CASE WHEN q.gstnumber IS NULL OR LENGTH(TRIM(q.gstnumber)) = 0 THEN pm.gst ELSE q.gstnumber END AS gst, "
	        + " pm.pan AS pan, "
	        + " DATE_FORMAT(q.duedate, '%d/%m/%Y') AS duedate, "
	        + " q.invoice_code, "
	        + " CONCAT(DATE_FORMAT(e.event_start_date_time,'%d/%m/%Y'),' To ',DATE_FORMAT(e.event_end_date_time,'%d/%m/%Y')) AS event_date, "
	        + " q.sub_total, "
	        + " q.cash_payment, "
	        + " q.cheque_payment, "
	        + " CONCAT(q.cgst,'%') AS cgst, "
	        + " q.cgst_amnt, "
	        + " CONCAT(q.sgst,'%') AS sgst, "
	        + " q.sgst_amnt, "
	        + " CONCAT(q.igst,'%') AS igst, "
	        + " q.igst_amnt, "
	        + " q.discount, "
	        + " q.total_amount AS advance_payment, "
	        + " q.remaining_amount, "
	        + " q.grand_total, "
	        + " ud.company_name, "
	        + " ud.country_code, "
	        + " ud.office_no, "
	        + " ud.company_email, "
	        + " ud.address, "
	        + " u.logo, "
	        + " bd.account_holder_name, "
	        + " bd.account_no, "
	        + " bd.bank_name, "
	        + " bd.ifsc_code, "
	        + " bd.upi_id, "
	        + " bd.branch_name, "
	        + " bd.qr_code_path, "
	        + " q.notes, "
	        + " DATE_FORMAT(e.inquiry_date,'%d/%m/%Y') AS inquiry_date, "
	        + " e.banquet_hall_id, "

	        // Dummy values to match quotation query
	        + " '' AS iron_service, "
	        + " '' AS pooja_rooms, "
	        + " '' AS venue_remark, "
	        + " 0 AS venue_total, "
	        + " FALSE AS is_locked, "

	        // Common fields
	        + " et.name_english AS event_name, "
	        + " CASE WHEN :lang = 0 THEN v.name_english "
	        + "      WHEN :lang = 1 THEN v.name_hindi "
	        + "      WHEN :lang = 2 THEN v.name_gujarati END AS venue, "
	        + " bhm.hall_name AS hall_name, "

	        + " ud.gst_number AS cmp_gst_number, "
	        + " q.created_at AS created_at, "
	        + " q.shipaddress AS ship_address, "
	        + " ud.pan_number AS pan_number, "
	        + " 0 AS transportation, "
	        + " q.food_tax, "
	        + " q.food_tax_amount, "
	        + " q.food_tax_total_amount, "
	        + " q.service_tax, "
	        + " q.service_tax_amount, "
	        + " q.service_tax_total_amount, "
	        + " q.vat_tax, "
	        + " q.vat_tax_amount, "
	        + " q.vat_tax_total_amount, "
	        + " ud.fssai_number, "
	        + " ud.hsn_number, "
	        + " ud.cin_number, "
	        + " ud.fda_lincense,"
	        + " q.discount_percentage, e.prefix, e.venue as event_venue "
	        + " FROM events e "                                                                                                                                                                                                                                                                                                                                                                                                    
	        + " LEFT JOIN partymaster pm ON e.party_id = pm.party_id "
	        + " LEFT JOIN event_invoice q ON q.event_id = e.event_id "
	        + " LEFT JOIN venuemaster v ON v.venue_id = e.venue_id "
	        + " LEFT JOIN eventtype et ON et.event_type_id = e.event_type_id "
	        + " LEFT JOIN banquet_hall_master bhm ON bhm.id = e.banquet_hall_id "
	        + " LEFT JOIN users u ON u.user_id = e.user_id "
	        + " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
	        + " LEFT JOIN bank_details bd ON (bd.user_id = u.user_id AND bd.is_primary = TRUE) "
	        + " WHERE e.event_id = :eventId "
	        + " AND e.user_id = :userid "
	        + " AND e.is_delete = FALSE",
	        nativeQuery = true)
	Object getEventDataInvoice(Long eventId, Long userid, int lang);

	@Query(value = "SELECT qi.function_name, "
	        + " qi.pax, "
	        + " qi.extra_pax, "
	        + " qi.rate_per_plate, "
	        + " qi.amount, "
	        + " qi.extra_tax, "
	        + " qi.tax_rate, "
	        + " qi.function_date, "
	        + " qi.custom_package_name, "
	        + " qi.is_event_function, "
	        + " qi.event_function_id, "
	        + " qi.is_locked, "
	        + " qi.is_addons, "
	        + " qi.offered_rate, "
	        + " qi.default_function_id "
	        + " FROM quotation_items qi "
	        + " WHERE qi.quotation_id = :quotationId "
	        + " AND qi.is_delete = FALSE "
	        + " ORDER BY "
	        + " qi.is_event_function DESC, "
	        + " qi.function_date IS NULL ASC, "
	        + " qi.function_date ASC ",
	        nativeQuery = true)
	List<Object[]> getFunData(Long quotationId);
	
	@Query(value =
	        "SELECT " +
	        "    t.function_name, " +
	        "    t.pax, " +
	        "    t.extra_pax, " +
	        "    t.rate_per_plate, " +
	        "    t.amount, " +
	        "    t.extra_tax, " +
	        "    t.tax_rate, " +
	        "    t.function_date, " +
	        "    t.custom_package_name, " +
	        "    t.is_event_function, " +
	        "    t.event_function_id, " +
	        "    t.is_locked, " +
	        "    t.is_addons " +
	        "FROM ( " +
	        "    SELECT " +
	        "        qi.function_name, " +
	        "        qi.pax, " +
	        "        qi.extra_pax, " +
	        "        qi.rate_per_plate, " +
	        "        qi.amount, " +
	        "        qi.extra_tax, " +
	        "        qi.tax_rate, " +
	        "        qi.function_date, " +
	        "        qi.custom_package_name, " +
	        "        qi.is_event_function, " +
	        "        qi.event_function_id, " +
	        "        qi.is_locked, " +
	        "        qi.is_addons " +
	        "    FROM quotation_items qi " +
	        "    LEFT JOIN event_function ef " +
	        "        ON ef.event_function_id = qi.event_function_id " +
	        "    WHERE qi.quotation_id = :quotationId " +
	        "      AND qi.is_delete = FALSE " +
	        "      AND qi.is_addons = FALSE " +

	        "    UNION ALL " +

	        "    SELECT " +
	        "        CONCAT('EXTRA ITEM (', f.name_english, ')') AS function_name, " +
	        "        MAX(ef.pax) AS pax, " +
	        "        0 AS extra_pax, " +
	        "        SUM(qi.rate_per_plate) AS rate_per_plate, " +
	        "        SUM(qi.amount) AS amount, " +
	        "        SUM(qi.extra_tax) AS extra_tax, " +
	        "        MAX(qi.tax_rate) AS tax_rate, " +
	        "        MAX(qi.function_date) AS function_date, " +
	        "        '' AS custom_package_name, " +
	        "        TRUE AS is_event_function, " +
	        "        qi.event_function_id, " +
	        "        FALSE AS is_locked, " +
	        "        TRUE AS is_addons " +
	        "    FROM quotation_items qi " +
	        "    LEFT JOIN event_function ef " +
	        "        ON ef.event_function_id = qi.event_function_id " +
	        "    LEFT JOIN functions f " +
	        "        ON f.function_id = ef.function_master_id " +
	        "    WHERE qi.quotation_id = :quotationId " +
	        "      AND qi.is_delete = FALSE " +
	        "      AND qi.is_addons = TRUE " +
	        "    GROUP BY qi.event_function_id, f.name_english " +
	        ") t " +
	        "ORDER BY t.function_date",
	        nativeQuery = true)
	List<Object[]> getFunData1(@Param("quotationId") Long quotationId);

	@Query(value = "SELECT qi.function_name,  "
			+ " qi.pax, "
			+ " qi.rate_per_plate, "
			+ " qi.amount,"
			+ " qi.extra_pax,"
			+ " qi.function_date, qi.is_event_function, "
			+ " qi.offered_rate "
			+ " FROM event_invoice_items qi "
			+ " WHERE qi.invoice_id = :invoiceId AND qi.is_delete = FALSE ", 
			nativeQuery = true)
	List<Object[]> getFunDataInvoice(Long invoiceId);
	
	Optional<EventFunctionQuotationPaymentEntity> findByModuleIdAndModuleNameAndEventAndIsDeleteFalse(
			@Param("moduleId") Long moduleId,
			@Param("moduleName") String moduleName,
			@Param("event") EventMasterEntity event
	);

}
