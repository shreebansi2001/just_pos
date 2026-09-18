package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventInvoiceEntity;
import com.crmportal.entity.EventInvoiceFunctionItemEntity;

@Repository
public interface EventInvoiceFunctionItemRepository extends JpaRepository<EventInvoiceFunctionItemEntity, Long>{

	EventInvoiceFunctionItemEntity findByIdAndIsDeleteFalse(Long id);

	List<EventInvoiceFunctionItemEntity> findAllByEventInvoiceAndIsDeleteFalse(
			EventInvoiceEntity eventInvoiceEntity);
	
	void deleteAllByEventInvoice(EventInvoiceEntity eventInvoice);

}
