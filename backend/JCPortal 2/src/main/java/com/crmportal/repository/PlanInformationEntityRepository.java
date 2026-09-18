package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.PlanInformationEntity;

@Repository
public interface PlanInformationEntityRepository extends JpaRepository<PlanInformationEntity, Long>{

	List<PlanInformationEntity> findAllByInvoice(InvoiceEntity invoiceEntity);

}
