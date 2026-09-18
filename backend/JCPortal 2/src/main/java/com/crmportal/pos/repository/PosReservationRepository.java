package com.crmportal.pos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosReservationEntity;

@Repository
public interface PosReservationRepository extends JpaRepository<PosReservationEntity, Long> {
    Optional<PosReservationEntity> findByResCode(String resCode);
    List<PosReservationEntity> findByResDate(String resDate);
    List<PosReservationEntity> findByResDateAndStatus(String resDate, String status);
}
