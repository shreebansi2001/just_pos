package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExtraEventRawMaterialEntity;

@Repository
public interface EventExtraRawMaterialRepository extends JpaRepository<ExtraEventRawMaterialEntity, Long> {

	List<ExtraEventRawMaterialEntity> findAllByEventIdAndRawmaterialCatId(Long eventId, Long rawMateriaCatlId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM event_extra_rawmaterial "
	             + "WHERE event_function_id = :eventFunctionId "
	             + "AND rawmaterial_cat_id = :rawMaterialCategoryId",
	       nativeQuery = true)
	void deleteByEventFunctionIdAndRawMaterialCatId(Long eventFunctionId, Long rawMaterialCategoryId);
	
	@Query("SELECT eerm FROM ExtraEventRawMaterialEntity eerm "
			+ " WHERE eerm.eventId = :eventId "
			+ " AND eerm.isDelete = false "
			+ " AND eerm.rawmaterialCatId IN (:rawMaterialCatIds)")
	List<ExtraEventRawMaterialEntity> findExtraRawMaterials(
			@Param("eventId") Long eventId,
			@Param("rawMaterialCatIds") List<Long> rawMaterialCatIds);

	@Query("SELECT e " +
		       "FROM ExtraEventRawMaterialEntity e, EventMasterEntity em " +
		       "WHERE e.eventId = em.id " +
		       "AND em.eventStartDateTime >= :startDate " +
		       "AND em.eventEndDateTime <= :endDate " +
		       "AND (:flag = FALSE OR e.rawmaterialCatId IN :rawMaterialCatIds) " +
		       "AND em.user.id = :userId " +
		       "ORDER BY e.rawmaterialCatId ASC")
		List<ExtraEventRawMaterialEntity> dateWiseExtraRaw(
		        @Param("rawMaterialCatIds") List<Long> rawMaterialCatIds,
		        @Param("startDate") LocalDateTime startDate,
		        @Param("endDate") LocalDateTime endDate,
		        @Param("flag") Boolean flag,
		        @Param("userId") Long userId);
}
