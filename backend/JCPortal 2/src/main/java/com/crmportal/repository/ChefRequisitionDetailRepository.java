package com.crmportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.ChefRequisitionDetailEntity;

@Repository
public interface ChefRequisitionDetailRepository extends JpaRepository<ChefRequisitionDetailEntity, Long> {

    List<ChefRequisitionDetailEntity> findByChefRequisitionId(Long crId);

    void deleteByChefRequisitionId(Long crId);
}