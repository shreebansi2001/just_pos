package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventLabourChecklistEntity;

@Repository
public interface EventLabourCheckListRepository extends JpaRepository<EventLabourChecklistEntity, Long> {

	Optional<EventLabourChecklistEntity> findByIdAndIsDeleteFalse(Long id);

	@Query(value =
	        "SELECT " +
	        "    elc.eventlabour_checklist_id AS id, " +
	        "    elc.event_id, " +
	        "    elc.event_function_id, " +
	        "    cc.contact_category_id, " +
	        "    cc.name_english AS contact_category_name, " +
	        "    pm.party_id, " +
	        "    pm.name_english AS vendor_name, " +
	        "    sm.shift_id, " +
	        "    sm.name_english AS shift_name, " +
	        "    elc.total_qty, " +
	        "    elc.in_qty, " +
	        "    elc.is_status, " +
	        "    elc.in_time, " +
	        "    elc.out_time, " +
	        "    elc.labordatetime, " +
	        "    elc.is_editable " +
	        "FROM eventlabour_checklist elc " +
	        "LEFT JOIN contact_category cc ON cc.contact_category_id = elc.contact_category_id " +
	        "LEFT JOIN partymaster pm ON pm.party_id = elc.party_id " +
	        "LEFT JOIN shift sm ON sm.shift_id = elc.shift_id " +
	        "WHERE elc.is_delete = 0 " +
	        "AND elc.event_id = :eventId " +
	        "AND elc.event_function_id = :eventFunctionId " +
	        "AND elc.user_id = :userId",
	        nativeQuery = true)
	List<Object[]> getChecklistData(Long eventId, Long eventFunctionId, Long userId);

	@Query(value =
	        "SELECT " +
	        "    el.event_id, " +
	        "    el.event_function_id, " +
	        "    cc.contact_category_id, " +
	        "    cc.name_english AS contact_category_name, " +
	        "    pm.party_id, " +
	        "    pm.name_english AS vendor_name, " +
	        "    sm.shift_id, " +
	        "    COALESCE(sm.name_english, el.laborshift) AS shift_name, " +
	        "    CAST(el.qty AS SIGNED) AS total_qty, " +
	        "    el.labordatetime " +
	        "FROM event_labor el " +
	        "LEFT JOIN contact_category cc ON cc.contact_category_id = el.contact_category_id " +
	        "LEFT JOIN partymaster pm ON pm.party_id = el.party_id " +
	        "LEFT JOIN shift sm ON sm.name_english = el.laborshift " +
	        "WHERE el.event_id = :eventId " +
	        "AND el.event_function_id = :eventFunctionId AND sm.user_id = :userId",
	        nativeQuery = true)
	List<Object[]> getEventLaborData(Long eventId, Long eventFunctionId,Long userId);

}
