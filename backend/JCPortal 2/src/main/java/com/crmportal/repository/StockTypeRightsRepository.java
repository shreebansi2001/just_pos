package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.StockTypeRightsEntity;
import com.crmportal.response.dto.StockTypeRightsDTO;

@Repository
public interface StockTypeRightsRepository extends JpaRepository<StockTypeRightsEntity, Long> {

	void deleteAllByUserId(Long userId);

	@Query(value = "SELECT r.stock_type_id AS stockTypeId, " + "s.name_english AS nameEnglish "
			+ "FROM stocktype_rights r " + "INNER JOIN stock_type s ON s.stock_type_id = r.stock_type_id "
			+ "WHERE r.user_id = :userId " + "AND s.is_delete = FALSE " + "AND s.is_active = TRUE", nativeQuery = true)
	List<Object[]> getAllStockTypeRights(@Param("userId") Long userId);

}
