package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionRawmaterialPermissionEntity;

@Repository
public interface EventFunctionRawMaterialPermissionRepository
		extends JpaRepository<EventFunctionRawmaterialPermissionEntity, Long> {

	void deleteAllByEventIdAndEventFunctionIdAndType(Long eventId, Long eventFunctionId, String string);

	@Query(value = ""
	        + " SELECT "
	        + "   p.event_id AS eventId, "
	        + "   GROUP_CONCAT(DISTINCT p.eventfunction_id) AS eventFunctionIds, "
	        + "   MAX(p.type) AS type, "
	        + "   r.raw_material_id AS rawMaterialId, "
	        + "   r.name_english AS nameEnglish, "
	        + "   r.name_hindi AS nameHindi, "
	        + "   r.name_gujarati AS nameGujarati "
	        + " FROM eventfunction_rawmaterial_permission p "
	        + " INNER JOIN rawmaterial r "
	        + " ON p.raw_material_id = r.raw_material_id "
	        + " WHERE p.event_id = :eventId "
	        + " AND p.user_id = :userId "
	        + " AND (:eventFunctionId = -1 OR p.eventfunction_id = :eventFunctionId) "
	        + " GROUP BY p.event_id, r.raw_material_id, r.name_english, r.name_hindi, r.name_gujarati "
	        , nativeQuery = true)
	List<Object[]> findPermissionRawMaterials(
	        @Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("userId") Long userId);


}
