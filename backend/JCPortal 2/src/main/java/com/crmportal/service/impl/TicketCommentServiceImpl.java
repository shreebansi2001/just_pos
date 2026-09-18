package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.TicketCommentEntity;
import com.crmportal.entity.TicketEntity;
import com.crmportal.repository.TicketCommentRepository;
import com.crmportal.repository.TicketRepository;
import com.crmportal.request.dto.TicketCommentRequestDTO;
import com.crmportal.response.dto.TicketCommentResponseDTO;
import com.crmportal.service.TicketCommentService;

@Service
public class TicketCommentServiceImpl implements TicketCommentService {

    @Autowired
    private TicketCommentRepository ticketCommentRepository;
    
    @Autowired
    private TicketRepository ticketRepository;

    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    @Override
    public TicketCommentResponseDTO addOrUpdateComment(TicketCommentRequestDTO dto, Long id) {

        TicketCommentEntity entity;
        
        TicketEntity ticket = ticketRepository.findByIdAndIsDeleteFalse(dto.getTicketId())
				.orElseThrow(() -> new RuntimeException(
						"Ticket is not exists : " + dto.getTicketId()));

        if (id != 0) { 
            // Update case
            Optional<TicketCommentEntity> opt = ticketCommentRepository.findById(id);
            if (!opt.isPresent()) {
                throw new RuntimeException("Comment not found for update");
            }
            entity = opt.get();
            entity.setComment(dto.getComment());
            entity.setCommentby(dto.getCommentBy());

        } else {
            // Add case
            entity = new TicketCommentEntity();
            entity.setTicketid(dto.getTicketId());
            entity.setComment(dto.getComment());
            entity.setCommentby(dto.getCommentBy());
        }

        TicketCommentEntity saved = ticketCommentRepository.save(entity);
        return convertToResponse(saved);
    }

    @Override
    public boolean deleteComment(Long id) {
        if (!ticketCommentRepository.existsById(id)) {
            return false;
        }
        ticketCommentRepository.deleteById(id);
        return true;
    }

    @Override
    public List<TicketCommentResponseDTO> getCommentsByTicketId(Long ticketId) {
        List<TicketCommentEntity> list = 
                ticketCommentRepository.findByTicketidOrderByCreatedAtDesc(ticketId);

        List<TicketCommentResponseDTO> response = new ArrayList<>();

        for (TicketCommentEntity e : list) {
            response.add(convertToResponse(e));
        }

        return response;
    }

    private TicketCommentResponseDTO convertToResponse(TicketCommentEntity e) {
        TicketCommentResponseDTO dto = new TicketCommentResponseDTO();
        dto.setTicketId(e.getTicketid());
        dto.setComment(e.getComment());
        dto.setCommentBy(e.getCommentby());
        dto.setTicketCommentId(e.getId());
        dto.setCreatedAt(e.getCreatedAt() != null ? e.getCreatedAt().format(FORMATTER) : null);
        return dto;
    }
}