package com.crmportal.repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface EventFunctionQuotationRepository extends JpaRepository<EventFunctionQuotationEntity, Long> {

	EventFunctionQuotationEntity findByIdAndIsDeleteFalse(long id);

	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndIsDecore(UserMasterEntity userMaster,Boolean isDecore);

	@Query("SELECT e FROM EventFunctionQuotationEntity e " + "WHERE e.user = :user " + "AND e.isDelete = false "
			+ "AND e.event.eventStartDateTime BETWEEN :startDate AND :endDate")
	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndCreatedAtBetween(
			@Param("user") UserMasterEntity user, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query(value = "SELECT quotation_code FROM quotations WHERE user_id = :userId ORDER BY quotation_id DESC LIMIT 1", nativeQuery = true)
	String findLastQuotationNo(Long userId);

	@Query("SELECT COALESCE(SUM(q.grandTotal), 0) FROM EventFunctionQuotationEntity q WHERE q.event.user.id = :userId AND q.isDelete = false")
	BigInteger getTotalGrandTotalByUserId(Long userId);

	@Query("SELECT COALESCE(SUM(q.grandTotal), 0) FROM EventFunctionQuotationEntity q WHERE q.event.user.id = :userId AND q.createdAt BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	BigInteger getTotalGrandTotalQuotation(@Param("userId") Long userId,
			@Param("givenDateTime") LocalDateTime givenDateTime);

	@Query("SELECT COALESCE(SUM(q.remainingAmount), 0) FROM EventFunctionQuotationEntity q WHERE q.event.user.id = :userId AND q.createdAt BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	BigInteger getTotalRemainingTotalQuotation(@Param("userId") Long userId,
			@Param("givenDateTime") LocalDateTime givenDateTime);

	@Query(value = "SELECT COUNT(DISTINCT e.event_id) " + "FROM quotations q "
			+ "JOIN events e ON q.event_id = e.event_id " + "WHERE q.is_delete = false " + "AND e.is_delete = false "
			+  "AND ( :userId = -1 OR q.user_id = :userId)", nativeQuery = true)
	Integer getQuotationCount(Long userId);

	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndIsDecore(UserMasterEntity userMaster,Integer status,Boolean isDecore);

	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_Venue_IdAndIsDecore(
			UserMasterEntity userMaster, int i, Long id,Boolean isDecore);

	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_BanquetHall_IdAndIsDecore(
			UserMasterEntity userMaster, int i, Long id,Boolean isDecore);

	EventFunctionQuotationEntity findByEventAndIsDeleteFalseAndIsDecore(EventMasterEntity eventMaster,
			Boolean isDecore);

	EventFunctionQuotationEntity findByEventAndIsDeleteFalse(EventMasterEntity eventEntity);

	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndIsDecore(
			UserMasterEntity userMaster, int i, LocalDateTime start, LocalDateTime end, Boolean isDecore);

	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndEvent_Venue_IdAndIsDecore(
			UserMasterEntity userMaster, int i, LocalDateTime start, LocalDateTime end, Long id, Boolean isDecore);

	List<EventFunctionQuotationEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndEvent_BanquetHall_IdAndIsDecore(
			UserMasterEntity userMaster, int i, LocalDateTime start, LocalDateTime end, Long id, Boolean isDecore);

}
