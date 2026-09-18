package com.crmportal.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.AllVendorsPaymentResponseDto;
import com.crmportal.entity.ContactTypeMasterEntity;


@Repository
public interface PartyMasterRepository extends JpaRepository<PartyMasterEntity, Long>{
	
	@Query("SELECT p.id FROM PartyMasterEntity p")
	List<Long> findAllIds();

	Optional<PartyMasterEntity> findByIdAndIsDeleteFalse(long id);

	List<PartyMasterEntity> findAllByIsDeleteFalse();

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<PartyMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity masterEntity);

	@Query("SELECT p FROM PartyMasterEntity p " +
		       "JOIN p.contact cc " +
		       "JOIN cc.contactType ct " +
		       "WHERE p.user = :masterEntity " +
		       "AND p.isDelete = false " +
		       "AND (LOWER(p.nameEnglish) LIKE LOWER(CONCAT('%', :name, '%')) " +
		       "OR LOWER(cc.nameEnglish) LIKE LOWER(CONCAT('%', :name, '%')) " +
		       "OR LOWER(ct.nameEnglish) LIKE LOWER(CONCAT('%', :name, '%'))) ORDER BY p.nameEnglish ASC ")
		List<PartyMasterEntity> findBySearchTextAndUserAndIsDeleteFalse(
				String name,
		        UserMasterEntity masterEntity);

	@Query("SELECT p FROM PartyMasterEntity p JOIN p.contact cc JOIN cc.contactType ct WHERE ct.id = :catTypeId AND p.user = :masterEntity AND p.isDelete = false ORDER BY p.nameEnglish ASC")
		List<PartyMasterEntity> findAllByUserAndIsDeleteFalseAndCatTypeId(UserMasterEntity masterEntity, Long catTypeId);

	@Query("SELECT p FROM PartyMasterEntity p JOIN p.contact cc JOIN cc.contactType ct WHERE ct.id = :catTypeId AND p.user = :masterEntity AND p.isDelete = false AND LOWER(p.nameEnglish) LIKE LOWER(CONCAT('%', :name, '%'))  ORDER BY p.nameEnglish ASC")
	List<PartyMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndCatTypeId(String name,
			UserMasterEntity masterEntity, Long catTypeId);

	@Query("SELECT p FROM PartyMasterEntity p WHERE p.contact.id = :id AND p.user = :masterEntity AND p.isDelete = false ORDER BY p.nameEnglish ASC")
	List<PartyMasterEntity> findAllByUserAndIsDeleteFalseAndContCatId(UserMasterEntity masterEntity, Long id);

	@Query("SELECT p FROM PartyMasterEntity p WHERE p.contact.id = :id AND p.user = :masterEntity AND p.isDelete = false AND LOWER(p.nameEnglish) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.nameEnglish ASC")
	List<PartyMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndContCatId(String name,
			UserMasterEntity masterEntity, Long id);

	boolean existsByMobilenoAndIsDeleteFalse(String mobileno);

	boolean existsByMobilenoAndIdNotAndIsDeleteFalse(String mobileno, Long id);

	boolean existsByMobilenoAndIsDeleteFalseAndUser(String mobileno, UserMasterEntity userMasterEntity);

	boolean existsByMobilenoAndIdNotAndIsDeleteFalseAndUser(String mobileno, Long id,
			UserMasterEntity userMasterEntity);
	
	boolean existsByContact_ContactTypeAndIsDeleteFalseAndUser(ContactTypeMasterEntity contactCategory, UserMasterEntity user);

	boolean existsByContact_ContactTypeAndIdNotAndIsDeleteFalseAndUser(ContactTypeMasterEntity contactCategory, Long id, UserMasterEntity user);
	
	@Query(value = "SELECT party_code FROM partymaster WHERE user_id = :userId AND party_code IS NOT NULL ORDER BY party_id DESC LIMIT 1", nativeQuery = true)
	Optional<String> findLastInsertedPartyCode(@Param("userId") Long userId);

	Optional<PartyMasterEntity> findByPartyCodeAndIsDeleteFalse(String code);

	List<PartyMasterEntity> findAllByIdInAndIsDeleteFalse(Set<Long> ids);

	@Query("SELECT p FROM PartyMasterEntity p " + "WHERE p.id = :partyId " + "AND p.isDelete = false "
			+ "AND p.user.id = :userId " + "AND p.opbDate <= :startDate")
	PartyMasterEntity getOpeningParty(@Param("partyId") Long partyId, @Param("userId") Long userId,
			@Param("startDate") LocalDate startDate);

	List<PartyMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String trim,
			UserMasterEntity user);
	
	// Filter by contact_type_id = 1 (party)
	@Query("SELECT p FROM PartyMasterEntity p " +
	       "JOIN p.contact cc " +
	       "JOIN cc.contactType ct " +
	       "WHERE p.user.id = :userId " +
	       "AND p.isDelete = false " +
	       "AND ct.id = 1")
	List<PartyMasterEntity> findAllPartyByUserId(@Param("userId") Long userId);

	// Filter by contact_type_id != 1 (vendors)
	@Query("SELECT p FROM PartyMasterEntity p " +
	       "JOIN p.contact cc " +
	       "JOIN cc.contactType ct " +
	       "WHERE p.user.id = :userId " +
	       "AND p.isDelete = false " +
	       "AND ct.id != 1")
	List<PartyMasterEntity> findAllVendorByUserId(@Param("userId") Long userId);

	boolean existsByContactAndIsDeleteFalse(ContactCategoryMasterEntity categoryMasterEntity);
	
	Optional<PartyMasterEntity> findByUserIdAndMobileno(
            Long userId,
            String contactNo);

	boolean existsByContact_ContactTypeAndMobilenoAndIdNotAndIsDeleteFalseAndUser(ContactTypeMasterEntity contactType,
			String mobileno, Long id, UserMasterEntity userMasterEntity);

	boolean existsByContact_ContactTypeAndMobilenoAndIsDeleteFalseAndUser(ContactTypeMasterEntity contactType,
			String mobileno, UserMasterEntity userMasterEntity);
	
	
}
