package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PosKotEntity;

@Repository
public interface PosKotRepository extends JpaRepository<PosKotEntity, Long> {
	Optional<PosKotEntity> findByKotCode(String kotCode);

	List<PosKotEntity> findByOrderId(Long orderId);

	List<PosKotEntity> findByStatusNot(String status);

	// User-wise queries
	List<PosKotEntity> findByUserId(Long userId);

	List<PosKotEntity> findByUserIdAndOrderId(Long userId, Long orderId);

	List<PosKotEntity> findByUserIdAndStatusNot(Long userId, String status);
}
