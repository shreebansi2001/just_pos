package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventAdvancePaymentEntity;

@Repository
public interface EventAdvancePaymentRepository extends JpaRepository<EventAdvancePaymentEntity, Long> {

	List<EventAdvancePaymentEntity> findByEventIdAndIsDeleteFalseOrderByPaymentDateDesc(Long eventId);

	Optional<EventAdvancePaymentEntity> findByIdAndIsDeleteFalse(Long id);
	
	@Query(value =
			" SELECT "
			+ "     e.event_id AS eventId, "
			+ "     e.event_no AS eventNo, "
			+ "     et.name_english AS eventName, "
			+ "     DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y') AS eventDate, "
			+ "     ap.event_advance_payment_id AS advancePaymentId, "
			+ "     DATE_FORMAT(ap.payment_date, '%d/%m/%Y') AS paymentDate, "
			+ "     ap.amount AS amount, "
			+ "     ap.payment_mode AS paymentMode, "
			+ "     ap.entry_by AS entryBy, "
			+ "     CONCAT(um.first_name, ' ', um.last_name) AS entryByName, "
			+ "     ap.remark AS remark, "
			+ "     ap.reference_id AS referenceId, "
			+ "     ap.user_id AS userId, "
			+ "     ap.cash_id AS cashId, "
			+ "     ca.account_name AS cashName, "
			+ "     ap.bank_id AS bankId, "
			+ "     bd.bank_name AS bankName, "
			+ "		u.logo AS logo, "
			+ "		ubd.company_name AS company_name, "
			+ " 	ubd.company_email AS company_email, "
			+ "		ubd.office_no AS company_contact_no,  "
			+ "		ubd.address AS company_address, "
			+ "		COALESCE(vm.name_english, '') AS venue, "
			+ "		pm.name_english AS partyName, "
			+ "	  	CONCAT("
			+ "    		DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y %h:%i %p'),"
			+ "    		' - ',"
			+ "    		DATE_FORMAT(ef.function_end_date_time, '%d/%m/%Y %h:%i %p')"
			+ "		) AS shift_time "
			+ " FROM event_advance_payment ap "
			+ " INNER JOIN `events` e "
			+ "     ON e.event_id = ap.event_id"
			+ " INNER JOIN eventtype et "
			+ "		ON et.event_type_id = e.event_type_id "
			+ " LEFT JOIN users um "
			+ "     ON um.user_id = ap.entry_by "
			+ " LEFT JOIN cash_account ca "
			+ "     ON ca.id = ap.cash_id "
			+ " LEFT JOIN bank_details bd "
			+ "     ON bd.id = ap.bank_id "
			+ " LEFT JOIN users u"
			+ " 	ON u.user_id = ap.user_id "
			+ " LEFT JOIN user_basic_details ubd "
			+ "		ON ubd.user_id = u.user_id"
			+ " LEFT JOIN partymaster pm "
			+ " 	ON pm.party_id = e.party_id "
			+ " LEFT JOIN venuemaster vm "
	        + "     ON vm.venue_id = e.venue_id "
	        + " LEFT JOIN event_function ef "
	        + "    ON ef.event_function_id = ap.event_function_id"
	        + " LEFT JOIN banquet_hall_master bhm"
	        + "    ON bhm.id = ap.banquet_id "
			+ " WHERE ap.is_delete = FALSE "
			+ "   AND e.is_delete = FALSE "
			+ "	  AND ap.event_advance_payment_id = :advancePaymentId "
			+ " ORDER BY ap.payment_date DESC, ap.event_advance_payment_id DESC ",
			nativeQuery = true)
		Object[] getEventAdvancePaymentReport(
				@Param("advancePaymentId") Long advancePaymentId
		);

	@Query(value = ""
	        + "SELECT "
	        + "    GROUP_CONCAT(DISTINCT bhm.hall_name ORDER BY bhm.hall_name SEPARATOR ', ') AS hallNames,"
	        + "    GROUP_CONCAT(CONCAT(bsm.start_time, ' - ', bsm.end_time) "
	        + "                 ORDER BY bsm.start_time SEPARATOR ', ') AS shifts, "
	        + "    GROUP_CONCAT(DISTINCT bsm.shift_name SEPARATOR ', ') AS session "
	        + "FROM banquet_hall_shift_booking bsb "
	        + "LEFT JOIN banquet_shift_master bsm "
	        + "    ON bsm.id = bsb.shift_id "
	        + "LEFT JOIN banquet_hall_master bhm "
	        + "    ON bhm.id = bsb.hall_id "
	        + "WHERE bsb.event_function_id = :eventFunctionId "
	        + "  AND bsb.is_delete = FALSE",
	        nativeQuery = true)
	List<Object[]> findHallShiftsByEventFunctionId(@Param("eventFunctionId") Long eventFunctionId);
}
