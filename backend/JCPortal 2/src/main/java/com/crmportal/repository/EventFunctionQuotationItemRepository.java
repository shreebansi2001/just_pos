package com.crmportal.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventFunctionQuotationItemEntity;

@Repository
public interface EventFunctionQuotationItemRepository extends JpaRepository<EventFunctionQuotationItemEntity, Long> {

	EventFunctionQuotationItemEntity findByIdAndIsDeleteFalse(Long id);

	List<EventFunctionQuotationItemEntity> findAllByEventFunctionQuotationAndIsDeleteFalse(
			EventFunctionQuotationEntity eventFunctionQuotationEntity);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM quotation_items WHERE quotation_item_id IN (:deletedEventFunctionItemIds)", nativeQuery = true)
	void deleteAllByItemIds(@Param("deletedEventFunctionItemIds") List<Long> deletedEventFunctionItemIds);

	@Modifying
	@Query("DELETE FROM EventFunctionQuotationItemEntity e " + "WHERE e.eventFunctionQuotation = :quotation "
			+ "AND e.eventFunctionId IN :eventFunctionIds " + "AND e.isAddons = true")
	void deleteByQuotationAndEventFunctionIdsAndIsAddonsTrue(@Param("quotation") EventFunctionQuotationEntity quotation,
			@Param("eventFunctionIds") List<Long> eventFunctionIds);

	@Modifying
	@Query("UPDATE EventFunctionQuotationItemEntity e " + "SET "
			+ "e.pax = CASE WHEN :isUpdatePax = true THEN :pax ELSE e.pax END, "
			+ "e.ratePerPlate = CASE WHEN :isUpdateRate = true THEN :rate ELSE e.ratePerPlate END, " + "e.amount = "
			+ "(CASE WHEN :isUpdatePax = true THEN :pax ELSE e.pax END) * "
			+ "(CASE WHEN :isUpdateRate = true THEN :rate ELSE e.ratePerPlate END), "
			+ "e.updatedAt = CURRENT_TIMESTAMP " + "WHERE e.eventFunctionId = :eventFunctionId")
	void updatePaxAndRate(@Param("eventFunctionId") Long eventFunctionId, @Param("isUpdatePax") Boolean isUpdatePax,
			@Param("isUpdateRate") Boolean isUpdateRate, @Param("pax") Integer pax, @Param("rate") BigDecimal rate);

	@Modifying
	@Transactional
	@Query("DELETE FROM EventFunctionQuotationItemEntity e " + "WHERE e.eventFunctionQuotation.id = :quotationId "
			+ "AND e.isEventFunction = false " + "AND e.defaultFunctionId IS NOT NULL")
	void deleteExtraFunction(@Param("quotationId") Long quotationId);

	@Modifying
	@Query("UPDATE EventFunctionQuotationItemEntity e "
	        + "SET "
	        + "e.ratePerPlate = :price, "
	        + "e.amount = e.pax * :price, "
	        + "e.customPackageId = :customPackageId, "
	        + "e.customPackageName = :nameEnglish, "
	        + "e.customPackagePrice = CASE "
	        + "    WHEN :customPackageId IS NULL THEN NULL "
	        + "    ELSE :price "
	        + "END, "
	        + "e.updatedAt = CURRENT_TIMESTAMP "
	        + "WHERE e.eventFunctionId = :eventFunctionId")
	void updatePackage(
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("customPackageId") Long customPackageId,
	        @Param("nameEnglish") String nameEnglish,
	        @Param("price") BigDecimal price);

	Optional<EventFunctionQuotationItemEntity> findFirstByEventFunctionQuotationAndEventFunctionIdAndItemIdAndMenuCatIdAndIsAddonsTrueAndIsDeleteFalse(
			EventFunctionQuotationEntity quotationEntity, Long eventFunctionId, Long itemId, Long menuCatId);

	@Modifying
	@Query("UPDATE EventFunctionQuotationItemEntity e "
	        + "SET "
	        + "e.functionDate = :dtoStartDateTime , "
	        + "e.updatedAt = CURRENT_TIMESTAMP "
	        + "WHERE e.eventFunctionId = :eventFunctionId")
	void updateFunctionDate(LocalDateTime dtoStartDateTime,@Param("eventFunctionId") Long eventFunctionId);
	
	@Query(" "
			+ " SELECT "
			+ " 	qi "
			+ " FROM EventFunctionQuotationItemEntity qi "
	        + " WHERE qi.eventFunctionQuotation.id = :quotationId "
	        + " AND qi.isDelete = FALSE "
	        + " AND qi.defaultFunctionId IS NOT NULL")
	List<EventFunctionQuotationItemEntity> getAllDefaultFunctionsByQuotation(@Param("quotationId") Long quotationId);

}
