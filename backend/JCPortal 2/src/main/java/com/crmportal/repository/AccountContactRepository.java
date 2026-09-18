package com.crmportal.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.AccountContactEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.response.dto.AllVendorsPaymentResponseDto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Repository
public interface AccountContactRepository extends JpaRepository<AccountContactEntity, Long>{

	Optional<AccountContactEntity> findByNameAndUserIdAndIsDeleteFalse(String name, Long userId);
	
	Optional<AccountContactEntity> findByIdAndIsDeleteFalse(Long id);
	
	@Query(value =
	        " SELECT " +
	        " COALESCE( " +
	        "     CASE " +
	        "         WHEN EXISTS ( " +
	        "             SELECT 1 " +
	        "             FROM user_basic_details ubd " +
	        "             WHERE ubd.user_id = :userId " +
	        "             AND ubd.role_id IN (1,2) " +
	        "         ) " +
	        "         THEN CONCAT(COALESCE(u.first_name,''), ' ', COALESCE(u.last_name,'')) " +
	        "         ELSE COALESCE(ac.name,'') " +
	        "     END, '' " +
	        " ) AS name " +
	        " FROM account_contact ac " +
	        " LEFT JOIN users u ON ac.member_id = u.user_id " +
	        " WHERE ( " +
	        "     (ac.user_id = :userId " +
	        "      AND EXISTS ( " +
	        "          SELECT 1 " +
	        "          FROM user_basic_details ubd " +
	        "          WHERE ubd.user_id = :userId " +
	        "          AND ubd.role_id IN (1,2) " +
	        "      )) " +
	        " OR " +
	        "     (ac.member_id = :userId " +
	        "      AND NOT EXISTS ( " +
	        "          SELECT 1 " +
	        "          FROM user_basic_details ubd " +
	        "          WHERE ubd.user_id = :userId " +
	        "          AND ubd.role_id IN (1,2) " +
	        "      )) " +
	        " ) " +
	        " LIMIT 1",
	        nativeQuery = true)
	String getNameByUserId(@Param("userId") Long userId);
	
	@Query(value = 
	        " SELECT "
	        + " 	ac.account_contact_id,"
	        + " 	ac.name,"
	        + " 	ac.opening_balance,"
	        + "  	ac.current_balance,"
	        + " 	ac.opening_date,"
	        + " 	ac.entry_type,"
	        + " 	ac.user_id,"
	        + " 	ac.member_id,"
	        + " 	ac.is_delete,"
	        + " 	ac.created_at,"
	        + " 	ac.updated_at "
	        + " FROM account_contact ac "  
	        + " WHERE (( "
	        + "    ac.user_id = :userId "
	        + "    AND EXISTS (  "
	        + "        SELECT 1 FROM user_basic_details ubd "  
	        + "        WHERE ubd.user_id = :userId  "
	        + "        AND ubd.role_id IN (1, 2) "
	        + "    )  "
	        + " )  "
	        + " OR (  "
	        + "    ac.member_id = :userId "  
	        + "    AND NOT EXISTS (  "
	        + "        SELECT 1 FROM user_basic_details ubd "  
	        + "        WHERE ubd.user_id = :userId  "
	        + "        AND ubd.role_id IN (1, 2) "
	        + "    )  "
	        + " ))"
	        + " AND ac.is_delete = false ",
	        nativeQuery = true)
	List<Object[]> findAccountContactsByUser(@Param("userId") Long userId);
	
	@Query(value = ""
			+ " SELECT   "
			+ "    	ac.account_contact_id AS id,  "
			+ "    	ac.name COLLATE utf8_unicode_ci AS name_english,  "
			+ "    	CAST('' AS CHAR) COLLATE utf8_unicode_ci AS mobileno,  "
			+ "    	CAST('ACCOUNT CONTACT' AS CHAR) COLLATE utf8_unicode_ci AS TYPE  "
			+ "	FROM account_contact ac  "
			+ "	WHERE ac.user_id = :userId  "
			+ "	AND ac.is_delete = FALSE  "
			+ "  "
			+ "	UNION ALL  "
			+ "  "
			+ "	SELECT   "
			+ "		pm.party_id AS id,  "
			+ "		pm.name_english AS name_english,  "
			+ "		CAST(pm.mobileno AS CHAR) COLLATE utf8_unicode_ci AS mobileno,  "
			+ "		CAST('VENDOR' AS CHAR) COLLATE utf8_unicode_ci AS TYPE  "
			+ "	FROM contacttype ct  "
			+ "	INNER JOIN contact_category cc ON ct.contact_type_id = cc.contact_type_id "
			+ " 	AND cc.user_id = :userId "
			+ " 	AND cc.is_delete = FALSE  "
			+ "	INNER JOIN partymaster pm ON pm.contact_category_id = cc.contact_category_id  "
			+ "	WHERE "
			+ " 	:roleId != 1 "
			+ "		AND pm.user_id = :userId "
			+ "		AND pm.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getAllMemberData(@Param("userId") Long userId,  @Param("roleId") Long roleId);
	
	@Query("SELECT ac FROM AccountContactEntity ac " + "WHERE ac.id = :accountContactId " + "AND ac.isDelete = false "
			+ "AND ac.userId = :userId " + "AND ac.openingDate <= :startDate")
	AccountContactEntity getOpeningParty(@Param("accountContactId") Long accountContactId, @Param("userId") Long userId,
			@Param("startDate") LocalDate startDate);

	List<AccountContactEntity> findAllByIdInAndIsDeleteFalse(Set<Long> ids);
}
