package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PosOrderActivityEntity;

@Repository
public interface PosOrderActivityRepository extends JpaRepository<PosOrderActivityEntity, Long> {
    List<PosOrderActivityEntity> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    List<PosOrderActivityEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
