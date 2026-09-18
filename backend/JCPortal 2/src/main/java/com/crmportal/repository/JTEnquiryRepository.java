package com.crmportal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.JTEnquiryEntity;
import com.crmportal.enums.JTEnquiryServiceType;

@Repository
public interface JTEnquiryRepository extends JpaRepository<JTEnquiryEntity, Long> {

	@Query(value = "SELECT e "
			+ " FROM JTEnquiryEntity e "
			+ " WHERE (:startDate IS NULL OR e.createdAt >= :startDate)"
			+ " AND (:endDate IS NULL OR e.createdAt <= :endDate) "
			+ " AND e.type = :type ")
	List<JTEnquiryEntity> getAllEnquiry(
			@Param("startDate") LocalDateTime startDate, 
			@Param("endDate") LocalDateTime endDate, 
			@Param("type") JTEnquiryServiceType type
	);
}
