package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CityMasterEntity;
import com.crmportal.entity.StateMasterEntity;

@Repository
public interface CityMasterRepository extends JpaRepository<CityMasterEntity, Long>{

	Optional<CityMasterEntity> findByNameAndStateIdAndIsDeleteFalse(String name, Long stateId);
	
	Optional<CityMasterEntity> findByIdAndIsDeleteFalse(long id);

	List<CityMasterEntity> findAllByIsDeleteFalse();

	List<CityMasterEntity> findByStateIdAndIsDeleteFalse(Long stateId);
	
	@Query(value = "SELECT c.* FROM cities c WHERE c.is_delete = false AND LOWER(c.name) LIKE CONCAT('%', LOWER(:cityName), '%')", nativeQuery = true )
	List<CityMasterEntity> getAllCityNameWithSearch(String cityName);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<CityMasterEntity> findByNameContainingIgnoreCaseAndIsDeleteFalse(String cityName);

	List<CityMasterEntity> findByStateAndIsDeleteFalse(StateMasterEntity state);

	List<CityMasterEntity> findByNameContainingIgnoreCaseAndStateAndIsDeleteFalse(String cityName,StateMasterEntity state);

}
