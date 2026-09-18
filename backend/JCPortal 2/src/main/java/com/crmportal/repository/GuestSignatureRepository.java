package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.GuestSignatureEntity;
import com.crmportal.response.dto.GuestSignatureReportResponseDto;
import com.crmportal.response.dto.GuestSignatureResponseDto;

@Repository
public interface GuestSignatureRepository extends JpaRepository<GuestSignatureEntity, Long> {

    Optional<GuestSignatureEntity> findByIdAndIsDeleteFalse(Long id);

	List<GuestSignatureEntity> findByEventIdAndIsDeleteFalse(Long eventId);

	List<GuestSignatureEntity> findByEventIdAndEventFunctionIdAndIsDeleteFalse(Long eventId, Long eventFunctionId);

	@Query(value = " " +
	        "SELECT new com.crmportal.response.dto.GuestSignatureReportResponseDto( " +
	        "    gs.id, " +
	        "    gs.particulars, " +
	        "    gs.persons, " +
	        "    gs.extra, " +
	        "    ef.id, " +
	        "    fn.nameEnglish " +
	        ") " +
	        "FROM GuestSignatureEntity gs " +
	        "LEFT JOIN EventFunctionMasterEntity ef " +
	        "    ON ef.id = gs.eventFunctionId " +
	        "LEFT JOIN FunctionMasterEntity fn " +
	        "    ON fn.id = ef.function.id " +
	        "WHERE gs.eventId = :eventId " +
	        "AND gs.isDelete = false")
	List<GuestSignatureReportResponseDto> getGuestSignatures(
	        @Param("eventId") Long eventId);
}