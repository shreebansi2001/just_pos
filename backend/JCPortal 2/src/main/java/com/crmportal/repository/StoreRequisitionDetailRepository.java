package com.crmportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.StoreRequisitionDetailEntity;

@Repository
public interface StoreRequisitionDetailRepository extends JpaRepository<StoreRequisitionDetailEntity, Long> {

    List<StoreRequisitionDetailEntity> findByStoreRequisitionId(Long crId);

    void deleteByStoreRequisitionId(Long crId);
}