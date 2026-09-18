package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;

@Repository
public interface CashAccountRepository extends JpaRepository<CashAccountEntity, Long> {

	Optional<CashAccountEntity> findByIdAndIsDeleteFalse(Long id);
	
	Optional<CashAccountEntity> findByUserIdAndIsPrimaryTrueAndIsDeleteFalse(Long userId);
	
	@Query(value = 
			"SELECT ca.* "
			+ " FROM cash_account ca "
			+ " WHERE  "
			+ " ("
			+ " 	( "
			+ "      	:isAdmin = true AND ca.user_id = :userId "
			+ "  	) "
			+ "  	OR "
			+ "  	( "
			+ "      	:isAdmin = false AND ca.user_id = ( "
			+ "          	SELECT u1.user_id "
			+ "          	FROM users u1 "
			+ "          	INNER JOIN users u2  "
			+ "              	ON u2.client_id = u1.user_id "
			+ "          	WHERE u2.user_id = :userId "
			+ "          	AND u1.is_delete = FALSE "
			+ "          	AND u2.is_delete = FALSE "
			+ "      	)  "
			+ "   	) "
			+ " ) "
			+ " AND"
			+ " ( "
			+ "   :isPrimary IS NULL OR ca.is_primary = :isPrimary "
			+ " )"
			+ " AND ca.is_delete = FALSE ", nativeQuery = true)
	List<CashAccountEntity> getAllByUserId(@Param("userId") Long userId, @Param("isAdmin") Boolean isAdmin, @Param("isPrimary") Boolean isPrimary);
	
	List<CashAccountEntity> findAllByIdInAndIsDeleteFalse(Set<Long> ids);
}
