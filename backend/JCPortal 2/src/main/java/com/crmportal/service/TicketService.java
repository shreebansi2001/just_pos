package com.crmportal.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.TicketRequestDTO;
import com.crmportal.response.dto.TicketResponseDto;

public interface TicketService {

	TicketResponseDto addOrUpdateTicket(TicketRequestDTO dto, Long id,MultipartFile file);

	TicketResponseDto getTicketById(Long id);

    List<TicketResponseDto> getAllTickets();

    List<TicketResponseDto> getAllTicketsByUserId(Long userId);
    
    boolean deleteTicket(Long id);
}
