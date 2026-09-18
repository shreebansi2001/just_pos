package com.crmportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CloseDateEntity;
import java.util.List;


@Repository
public interface CloseDateRepository extends JpaRepository<CloseDateEntity, Long>{

	Optional<CloseDateEntity> findByYearAndMonthAndUserId(Integer year, Integer month, Long userId);
	
	Optional<CloseDateEntity> findByMonthAndYearAndUserId(Integer month, Integer year, Long userId);
	
	List<CloseDateEntity> findAllByYearAndUserIdOrderByMonthAsc(Integer year, Long userId);
}
