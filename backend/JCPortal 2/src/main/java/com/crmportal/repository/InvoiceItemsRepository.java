package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.crmportal.entity.InvoiceItemsEntity;
import com.crmportal.entity.InvoiceEntity;


@Service
public interface InvoiceItemsRepository extends JpaRepository<InvoiceItemsEntity, Long>{

	void deleteAllByInvoice(InvoiceEntity invoice);
	
	List<InvoiceItemsEntity> findByInvoiceAndIsDeleteFalse(InvoiceEntity invoice);
	
}
