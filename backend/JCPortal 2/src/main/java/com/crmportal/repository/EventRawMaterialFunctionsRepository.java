package com.crmportal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventRawMaterialEntity;
import com.crmportal.entity.EventRawMaterialFunctions;
import com.crmportal.response.dto.EventRawMaterialFunctionsDto;
import com.crmportal.response.dto.EventRawMaterialQtySumDto;
import com.crmportal.response.dto.RawMaterialCategoryRateResponseDto;

@Repository
public interface EventRawMaterialFunctionsRepository extends JpaRepository<EventRawMaterialFunctions, Long> {

	@Query("SELECT new com.crmportal.response.dto.EventRawMaterialFunctionsDto(i.function.id, i.eventFunction.id, i.function.nameEnglish, i.qty"
			+ ",i.itemName,COALESCE(s.id,0),COALESCE(s.nameEnglish,''), i.unit.id,i.unit.nameEnglish, i.place,i.price,i.functiondatetime,i.isExtraField, i.rawMaterialPrice, i.menuitemid)"
			+ " from #{#entityName} i LEFT JOIN i.supplier s where i.eventRawMaterial.id=:eventMaterialId")
	List<EventRawMaterialFunctionsDto> getEventRawMaterialFunctionsDetails(Long eventMaterialId);

//	@Modifying
//    @Transactional
//    @Query("DELETE FROM EventRawMaterialFunctions erf WHERE erf.event.id = :eventId AND erf.rawMaterialCat.id = :rawMaterialCatId")
//    void deleteByEventIdAndRawMaterialCat(@Param("eventId") Long eventId, @Param("rawMaterialCatId") Long rawMaterialCatId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM event_raw_material_functions " + "WHERE event_id = :eventId "
			+ "AND raw_material_cat_id = :rawMaterialCatId", nativeQuery = true)
	void deleteByEventIdAndRawMaterialCat(@Param("eventId") Long eventId,
			@Param("rawMaterialCatId") Long rawMaterialCatId);

	@Query("SELECT "
			+ "		ermf "
			+ "	FROM "
			+ "	EventRawMaterialFunctions ermf "
			+ "	WHERE ermf.event.id = :eventId "
			+ "	AND ermf.eventFunction.id IN (:eventFunctionIds) "
			+ " AND ermf.rawMaterialCat.id IN (:rawMaterialCatIds) "
			+ "	ORDER BY COALESCE(ermf.eventRawMaterial.rawMaterialCat.sequence, 9999) ASC, COALESCE(ermf.eventRawMaterial.rawMaterial.sequence, 9999) ASC ")
	List<EventRawMaterialFunctions> findByEventIdAndEventFunctionId(@Param("eventId") Long eventId,
			@Param("eventFunctionIds") List<Long> eventFunctionIds,
			@Param("rawMaterialCatIds") List<Long> rawMaterialCatIds);
	
//	@Query("SELECT "
//			+ "		ermf "
//			+ "	FROM "
//			+ "	EventRawMaterialFunctions ermf "
//			+ "	WHERE "
//			+ "	ermf.eventFunction.functionStartDateTime >= :startDate "
//			+ " AND ermf.eventFunction.functionEndDateTime <= :endDate "
//			+ " AND (:flag = FALSE OR ermf.rawMaterialCat.id IN (:rawMaterialCatIds))"
//			+ " AND ermf.event.user.id = :userId "
//			+ "	ORDER BY COALESCE(ermf.eventRawMaterial.rawMaterialCat.sequence, 9999) ASC, COALESCE(ermf.eventRawMaterial.rawMaterial.sequence, 9999) ASC ")
//	List<EventRawMaterialFunctions> dateWiseReportData(
//			@Param("rawMaterialCatIds") List<Long> rawMaterialCatIds,
//			@Param("startDate") LocalDateTime startDate,
//			@Param("endDate") LocalDateTime endDate,
//			@Param("flag") Boolean flag,
//			@Param("userId") Long userId);
	
//	@Query("SELECT "
//			+ "		ermf "
//			+ "	FROM "
//			+ "	EventRawMaterialEntity ermf "
//			+ "	WHERE "
//			+ "	ermf.event.eventStartDateTime >= :startDate "
//			+ " AND ermf.event.eventEndDateTime <= :endDate "
//			+ " AND (:flag = FALSE OR ermf.rawMaterialCat.id IN (:rawMaterialCatIds))"
//			+ " AND ermf.event.user.id = :userId "
//			+ "	ORDER BY COALESCE(ermf.rawMaterialCat.sequence, 9999) ASC, COALESCE(ermf.rawMaterial.sequence, 9999) ASC ")
//	List<EventRawMaterialEntity> dateWiseReportData(
//			@Param("rawMaterialCatIds") List<Long> rawMaterialCatIds,
//			@Param("startDate") LocalDateTime startDate,
//			@Param("endDate") LocalDateTime endDate,
//			@Param("flag") Boolean flag,
//			@Param("userId") Long userId);
	
	@Query(""
			+ " SELECT DISTINCT "
			+ " 	ermf "
			+ " FROM EventRawMaterialEntity ermf "
			+ " JOIN EventRawMaterialFunctions ermfFunc " 
			+ " ON ermfFunc.eventRawMaterial.id = ermf.id "
			+ " WHERE ermfFunc.eventFunction.functionStartDateTime >= :startDate "
			+ " AND ermfFunc.eventFunction.functionEndDateTime <= :endDate "
			+ " AND (:flag = FALSE OR ermf.rawMaterialCat.id IN (:rawMaterialCatIds)) "
			+ " AND ermf.event.user.id = :userId "
			+ " ORDER BY COALESCE(ermf.rawMaterialCat.sequence, 9999) ASC, "
			+ " COALESCE(ermf.rawMaterial.sequence, 9999) ASC")
	List<EventRawMaterialEntity> dateWiseReportData(
			@Param("rawMaterialCatIds") List<Long> rawMaterialCatIds,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			@Param("flag") Boolean flag,
			@Param("userId") Long userId);

	@Query("SELECT ermf FROM EventRawMaterialFunctions ermf  " + " JOIN FETCH ermf.rawMaterialCat cat "
			+ " JOIN FETCH cat.rawMaterialCatType cattype "
			+ " WHERE ermf.event.id = :eventId AND cat.rawMaterialCatType.id = 2 "
			+ " AND ermf.eventFunction.id = :eventFunctionId")
	List<EventRawMaterialFunctions> findAllCrockeryByEventIdAndEventFunctionId(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Modifying
	@Transactional
	@Query("DELETE FROM EventRawMaterialFunctions f WHERE f.eventRawMaterial.id = :eventRawMaterialId")
	void deleteByEventRawMaterialId(@Param("eventRawMaterialId") Long eventRawMaterialId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM event_raw_material_functions " + "WHERE event_id = :eventId ", nativeQuery = true)
	void deleteByEventId(@Param("eventId") Long eventId);

	@Query(value = "SELECT " + "    ermf.event_raw_material_function_id AS eventRawMaterialFunctionId, "
			+ "    ermf.event_raw_material_id AS eventRawMaterialId " + "FROM event_raw_material_functions ermf "
			+ "WHERE ermf.event_id = :eventId " + "  AND ermf.event_function_id = :eventFunctionId "
			+ "  AND ermf.menuitemid = :menuItemId", nativeQuery = true)
	List<Object[]> findForDelete(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId,
			@Param("menuItemId") Long menuItemId);

	@Query(value = "SELECT " + "    ermf.event_raw_material_function_id AS eventRawMaterialFunctionId, "
			+ "    ermf.event_raw_material_id AS eventRawMaterialId " + "FROM event_raw_material_functions ermf "
			+ "WHERE ermf.event_id = :eventId " + "  AND ermf.event_function_id = :eventFunctionId "
			+ "  AND ermf.menuitemid IN (:menuItemIds)", nativeQuery = true)
	List<Object[]> findForDelete(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId,
			@Param("menuItemIds") List<Long> menuItemIds);

	@Query("SELECT new com.crmportal.response.dto.EventRawMaterialQtySumDto(" + "   f.eventRawMaterial.id, "
			+ "   COALESCE(SUM(f.qty), 0) " + ") " + "FROM EventRawMaterialFunctions f "
			+ "WHERE f.eventRawMaterial.id IN :eventRawMaterialIds " + "GROUP BY f.eventRawMaterial.id")
	List<EventRawMaterialQtySumDto> sumQtyByEventRawMaterialIds(
			@Param("eventRawMaterialIds") Set<Long> eventRawMaterialIds);

	List<EventRawMaterialFunctions> findByEventRawMaterial_Id(Long eventRawMaterialId);

	@Query("SELECT DISTINCT e.eventRawMaterial.id " + "FROM EventRawMaterialFunctions e "
			+ "WHERE e.event.id = :eventId " + "AND e.eventFunction.id = :eventFunctionId "
			+ "AND e.eventRawMaterial IS NOT NULL")
	List<Long> findDistinctEventRawMaterialIdsByEventIdAndEventFunctionId(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Modifying
	@Transactional
	@Query("DELETE FROM EventRawMaterialFunctions e " + "WHERE e.event.id = :eventId "
			+ "AND e.eventFunction.id = :eventFunctionId")
	int deleteByEventIdAndEventFunctionId(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	void deleteAllByEvent(EventMasterEntity eventEntity);

	@Query("SELECT new com.crmportal.response.dto.RawMaterialCategoryRateResponseDto( "
	        + " rmc.id, "
	        + " rmc.nameEnglish, "
	        + " rmc.nameHindi, "
	        + " rmc.nameGujarati, "
	        + " COALESCE(SUM(m.price), 0) "
	        + ") "
	        + " FROM EventRawMaterialFunctions m "
	        + " JOIN m.rawMaterialCat rmc "
	        + " WHERE m.event.id = :eventId "
	        + " AND (:eventFunctionId = -1l OR m.eventFunction.id = :eventFunctionId) "
	        + " GROUP BY rmc.id, rmc.nameEnglish, rmc.nameHindi, rmc.nameGujarati")
	List<RawMaterialCategoryRateResponseDto> getCategoryWiseTotalRate(
	        @Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT new com.crmportal.response.dto.RawMaterialCategoryRateResponseDto( "
	        + " rmc.id, "
	        + " rmc.nameEnglish, "
	        + " rmc.nameHindi, "
	        + " rmc.nameGujarati, "
	        + " COALESCE(SUM(m.totalprice), 0) "
	        + ") "
	        + " FROM EventRawMaterialEntity m "
	        + " JOIN m.rawMaterialCat rmc "
	        + " WHERE m.event.id = :eventId "
	        + " GROUP BY rmc.id, rmc.nameEnglish, rmc.nameHindi, rmc.nameGujarati")
	List<RawMaterialCategoryRateResponseDto> getCategoryWiseTotalRateFromEventRaw(Long eventId);

	
}
