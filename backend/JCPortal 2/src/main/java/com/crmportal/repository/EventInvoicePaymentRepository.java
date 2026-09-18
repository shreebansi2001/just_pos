package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventFunctionQuotationPaymentEntity;
import com.crmportal.entity.EventInvoiceEntity;
import com.crmportal.entity.EventInvoicePaymentEntity;

@Repository
public interface EventInvoicePaymentRepository extends JpaRepository<EventInvoicePaymentEntity, Long>{

	EventInvoicePaymentEntity findByIdAndIsDeleteFalse(Long id);

	List<EventInvoicePaymentEntity> findAllByEventInvoiceAndIsDeleteFalse(
			EventInvoiceEntity eventInvoiceEntity);

	void deleteAllByEventInvoice(EventInvoiceEntity eventInvoice);
	
}
