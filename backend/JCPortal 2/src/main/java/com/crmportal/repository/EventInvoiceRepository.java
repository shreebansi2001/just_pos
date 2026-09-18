package com.crmportal.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventInvoiceEntity;
import com.crmportal.entity.EventLaborEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface EventInvoiceRepository extends JpaRepository<EventInvoiceEntity, Long> {

	EventInvoiceEntity findByIdAndIsDeleteFalse(long id);

	EventInvoiceEntity findByEventAndIsDeleteFalse(EventMasterEntity eventMaster);

	void deleteByEvent(EventMasterEntity event);

	List<EventInvoiceEntity> findAllByUserAndIsDeleteFalseAndEvent_Status(UserMasterEntity userMaster,Integer status);
	
	@Query("SELECT ei "
			+ " FROM EventInvoiceEntity ei "
			+ " WHERE ei.user.id = :userId "
			+ " AND (:startDate IS NULL OR ei.createdAt >= :startDate) "
			+ " AND (:endDate IS NULL OR ei.createdAt <= :endDate)"
			+ " AND ei.isDelete = FALSE ")
	List<EventInvoiceEntity> getAllByUserId(Long userId, LocalDateTime startDate, LocalDateTime endDate);

	@Query("SELECT e FROM EventInvoiceEntity e " + "WHERE e.user = :user " + "AND e.isDelete = false AND e.event.status = 1 "
			+ "AND e.event.eventStartDateTime BETWEEN :startDate AND :endDate")
	List<EventInvoiceEntity> findAllByUserAndIsDeleteFalseAndCreatedAtBetween(@Param("user") UserMasterEntity user,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query(value = "SELECT invoice_code FROM event_invoice WHERE (:userId IS NULL OR user_id = :userId) AND invoice_code LIKE 'INV-%' ORDER BY invoice_id DESC LIMIT 1", nativeQuery = true)
	String findLastInvoiceNo(Long userId);

	List<EventInvoiceEntity> findByEvent_Id(Long eventId);

	boolean existsByInvoiceCode(String invoiceCode);

	@Query("SELECT count(*) FROM EventInvoiceEntity e " + "WHERE e.user.id = :userId " + "AND e.isDelete = false ")
	long countByEvent_User_IdAndIsDeleteFalse(Long userId);

	@Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM EventInvoiceEntity i WHERE i.event.user.id = :userId AND i.isDelete = false")
	BigDecimal getTotalAmountByUserId(Long userId);

	@Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM EventInvoiceEntity i WHERE i.event.user.id = :userId AND i.createdAt BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	BigInteger getTotalGrandTotal(Long userId, @Param("givenDateTime") LocalDateTime givenDateTime);

	@Query("SELECT COALESCE(SUM(i.remainingAmount), 0) FROM EventInvoiceEntity i WHERE i.event.user.id = :userId AND i.createdAt BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	BigInteger getTotalRemainingTotal(Long userId, @Param("givenDateTime") LocalDateTime givenDateTime);

	boolean existsByEvent(EventMasterEntity event);

	@Query(value = "SELECT COUNT(DISTINCT e.event_id) " + "FROM event_invoice inv "
			+ "JOIN events e ON inv.event_id = e.event_id " + "WHERE inv.is_delete = false " + "AND e.is_delete = false "
			+ "AND (:userId = -1 OR inv.user_id = :userId)", nativeQuery = true)
	Integer getInvoiceCount(Long userId);

	List<EventInvoiceEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_Venue_Id(UserMasterEntity userMaster,
			int i, Long id);

	List<EventInvoiceEntity> findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_BanquetHall_Id(
			UserMasterEntity userMaster, int i, Long id);

	@Query("SELECT e FROM EventInvoiceEntity e " + "WHERE e.user = :user " + "AND e.isDelete = false AND e.event.status = 1 AND e.event.venue.id = :id "
			+ "AND e.event.eventStartDateTime BETWEEN :startDate AND :endDate")
	List<EventInvoiceEntity> findAllByUserAndIsDeleteFalseAndCreatedAtBetweenAndEvent_Venue_Id(
			@Param("user") UserMasterEntity user,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("id") Long id);

	@Query("SELECT e FROM EventInvoiceEntity e " + "WHERE e.user = :user " + "AND e.isDelete = false AND e.event.status = 1 AND e.event.banquetHall.id = :id "
			+ "AND e.event.eventStartDateTime BETWEEN :startDate AND :endDate")
	List<EventInvoiceEntity> findAllByUserAndIsDeleteFalseAndCreatedAtBetweenAndEvent_BanquetHall_Id(
			@Param("user") UserMasterEntity user,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("id") Long id);

}
