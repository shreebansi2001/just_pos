package com.crmportal.service;

import java.util.List;

import com.crmportal.request.dto.TicketCommentRequestDTO;
import com.crmportal.response.dto.TicketCommentResponseDTO;

public interface TicketCommentService {

    TicketCommentResponseDTO addOrUpdateComment(TicketCommentRequestDTO dto, Long id);

    boolean deleteComment(Long id);

    List<TicketCommentResponseDTO> getCommentsByTicketId(Long ticketId);
}