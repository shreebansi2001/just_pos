package com.crmportal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionGeneralFixEntity;

@Repository
public interface EventFunctionGeneralFixRepository extends JpaRepository<EventFunctionGeneralFixEntity, Long> {

	List<EventFunctionGeneralFixEntity> findByEventFunctionIdInAndRawCatIdIn(List<Long> functionIds,
			List<Long> rawCatIds);

	List<EventFunctionGeneralFixEntity> findByEventFunctionIdIn(List<Long> functionIds);

	List<EventFunctionGeneralFixEntity> findByEventGeneralfixIdIn(List<Long> generalFixIds);

	@Transactional
	@Modifying
	@Query("DELETE FROM EventFunctionGeneralFixEntity e "
	        + "WHERE e.eventGeneralfixId IN (:generalFixIds)")
	void deleteByEventGeneralfixIdIn(
	        @Param("generalFixIds") List<Long> generalFixIds);

}
