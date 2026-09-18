package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CountryMasterEntity;

@Repository
public interface CountryMasterRepository extends JpaRepository<CountryMasterEntity, Long>{

	Optional<CountryMasterEntity> findByIdAndIsDeleteFalse(Long countryId);
	 
	boolean existsByIdAndIsDeleteFalse(Long id);

	Optional<CountryMasterEntity> findByNameAndIsDeleteFalse(String name);

	List<CountryMasterEntity> findAllByIsDeleteFalse();

	@Query(value = "SELECT c.* FROM countries c WHERE c.is_delete = false AND LOWER(c.name) LIKE CONCAT('%', LOWER(:countryName), '%')", nativeQuery = true )
	List<CountryMasterEntity> getAllCountryNameWithSearch(String countryName);

	Optional<CountryMasterEntity> findByCodeAndIsDeleteFalse(String code);

	List<CountryMasterEntity> findByNameContainingIgnoreCaseAndIsDeleteFalse(String countryName);

}
