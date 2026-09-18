package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.StockTypeEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface StockTypeRepository extends JpaRepository<StockTypeEntity, Long> {

	Optional<StockTypeEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<StockTypeEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<StockTypeEntity> findAllByUserAndIsDeleteFalseAndIsActive(UserMasterEntity user, Boolean isActive);

	List<StockTypeEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<StockTypeEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(
			String nameEnglish, UserMasterEntity user, Boolean isActive);

	StockTypeEntity findByIdAndIsDeleteFalse(Long id);

	StockTypeEntity findByIdAndUserAndIsDeleteFalse(Long id, UserMasterEntity user);

	boolean existsByIdAndIsDeleteFalse(Long id);
	
	@Query("SELECT s FROM StockTypeEntity s WHERE s.user = :user AND s.isDelete = false AND (:isActive IS NULL OR s.isActive = :isActive) AND (:mainType IS NULL OR s.mainType = :mainType)")
	List<StockTypeEntity> findStockTypes(UserMasterEntity user,Boolean isActive,Integer mainType);
}