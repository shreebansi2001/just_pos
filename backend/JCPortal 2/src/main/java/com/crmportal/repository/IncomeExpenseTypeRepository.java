package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.IncomeExpenseTypeEntity;

@Repository
public interface IncomeExpenseTypeRepository extends JpaRepository<IncomeExpenseTypeEntity, Long>{

	Optional<IncomeExpenseTypeEntity> findByTypeIdAndIsDeleteFalse(Long typeId);
	
	Optional<IncomeExpenseTypeEntity> findByNameAndUserIdAndIsDeleteFalse(String name, Long userId);
	
	List<IncomeExpenseTypeEntity> findAllByUserIdAndIsDeleteFalse(Long userId);
	
	List<IncomeExpenseTypeEntity> findAllByTypeAndUserIdAndIsDeleteFalse(String type, Long userId);
	
	@Query("SELECT t "
			+ " FROM IncomeExpenseTypeEntity t "
			+ " WHERE t.userId = :userId "
			+ " AND (:type IS NULL OR UPPER(t.type) = :type) "
			+ " AND t.isDelete = false ")
	List<IncomeExpenseTypeEntity> getAllByUserId(@Param("type") String type, @Param("userId") Long userId);
}
