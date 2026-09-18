package com.crmportal.pos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosInvoiceEntity;

@Repository
public interface PosInvoiceRepository extends JpaRepository<PosInvoiceEntity, Long> {
    Optional<PosInvoiceEntity> findByInvoiceCode(String invoiceCode);
    List<PosInvoiceEntity> findByOrderId(Long orderId);
    List<PosInvoiceEntity> findAllByOrderByCreatedAtDesc();
}
