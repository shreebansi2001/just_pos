package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.TicketCommentEntity;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketCommentEntity, Long> {

    List<TicketCommentEntity> findByTicketidOrderByCreatedAtDesc(Long ticketId);
}