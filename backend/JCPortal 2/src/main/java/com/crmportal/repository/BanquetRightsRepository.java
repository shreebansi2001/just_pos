package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crmportal.entity.BanquetRightsEntity;

public interface BanquetRightsRepository
        extends JpaRepository<BanquetRightsEntity, Long> {

    List<BanquetRightsEntity>
    findByUserIdAndIsDeleteFalse(Long userId);

    Optional<BanquetRightsEntity>
    findByUserIdAndBanquetHallIdAndIsDeleteFalse(
            Long userId,
            Long banquetHallId);

	void deleteAllByUserId(Long userId);
}