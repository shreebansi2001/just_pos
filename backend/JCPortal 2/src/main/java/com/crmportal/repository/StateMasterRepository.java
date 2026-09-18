package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CountryMasterEntity;
import com.crmportal.entity.StateMasterEntity;

@Repository
public interface StateMasterRepository extends JpaRepository<StateMasterEntity, Long>{
	@Query(value = "SELECT s.* FROM states s WHERE s.is_delete = false AND s.name = :name", nativeQuery = true )
	Optional<StateMasterEntity> findByName(String name);

	@Query(value = "SELECT s.* FROM states s WHERE s.is_delete = false AND LOWER(s.name) LIKE CONCAT('%', LOWER(:stateName), '%')", nativeQuery = true )
	List<StateMasterEntity> getAllStateNameWithSearch(String stateName);

	@Query(value = "SELECT s.* FROM states s WHERE s.is_delete = false AND s.state_id = :id", nativeQuery = true )
	Optional<StateMasterEntity> getStateById(long id);

	List<StateMasterEntity> findAllByIsDeleteFalse();

	Optional<StateMasterEntity> findByNameAndIsDeleteFalse(String name);

	Optional<StateMasterEntity> findByIdAndIsDeleteFalse(long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<StateMasterEntity> findByCountryIdAndIsDeleteFalse(Long id);

	List<StateMasterEntity> findByNameContainingIgnoreCaseAndIsDeleteFalse(String stateName);

	List<StateMasterEntity> findByCountryAndIsDeleteFalse(CountryMasterEntity country);

	List<StateMasterEntity> findByNameContainingIgnoreCaseAndCountryAndIsDeleteFalse(String stateName,
			CountryMasterEntity country);


}
