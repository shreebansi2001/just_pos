package com.crmportal.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.TicketEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.repository.TicketRepository;
import com.crmportal.request.dto.TicketRequestDTO;
import com.crmportal.response.dto.TicketResponseDto;
import com.crmportal.service.TicketService;
import com.crmportal.service.UserFileService;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    UserFileService userFileService;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter CREATED_AT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    @Override
    public TicketResponseDto addOrUpdateTicket(TicketRequestDTO request, Long id,MultipartFile file) {

        TicketEntity entity = null;

        if (id == -1) { // ADD
            entity = new TicketEntity();

            Long maxCounter = ticketRepository.findMaxTicketCounter();
            Long newCounter = (maxCounter == null ? 1 : maxCounter + 1);

            entity.setTicketcodecounter(newCounter);
            entity.setTicketcode("JC-000-" + newCounter);

        } else { // UPDATE
            entity = ticketRepository.findById(id).orElse(null);
            if (entity == null) {
                throw new RuntimeException("Ticket not found for update.");
            }
        }

        entity.setTicketfrom(request.getTicketfrom());
        entity.setClientmsg(request.getClientmsg());
        entity.setUsermsg(request.getUsermsg());
        entity.setStatus(request.getStatus());
        entity.setAssigntoname(request.getAssigntoname());
        entity.setAssigntouserid(request.getAssigntouserid());
        entity.setUserid(request.getUserid());
        entity.setIsDelete(false);
        entity.setInteractionid(request.getInteractionid());
        entity.setInteractionname(request.getInteractionname());
        entity.setInteractiontype(request.getInteractiontype());

        try {
            if (request.getExpactedclosedate() != null && !request.getExpactedclosedate().trim().isEmpty()) {
                entity.setExpactedclosedate(LocalDate.parse(request.getExpactedclosedate(), DATE_FORMAT));
            }
            if (request.getActualclosedate() != null && !request.getActualclosedate().trim().isEmpty()) {
                entity.setActualclosedate(LocalDate.parse(request.getActualclosedate(), DATE_FORMAT));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Invalid date format. Expected dd/MM/yyyy.");
        }

        TicketEntity saved = ticketRepository.save(entity);
        if(file != null && !file.isEmpty()) {
        	try {
        		userFileService.storeFile(request.getUserid(), ModuleName.TICKET.toString(),saved.getId(), FileType.IMAGE.toString(), file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        }
        
        return getTicketById(saved.getId());
    }

    private LocalDate parseDate(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(input, DATE_FORMAT);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<TicketResponseDto> getAllTickets() {

        List<Object[]> rows = ticketRepository.fetchAllTicketsOptimized();
        List<TicketResponseDto> list = new ArrayList<>();

        for (Object[] r : rows) {
            list.add(mapRowToDto(r));
        }
        return list;
    }


    // -----------------------------------------
    // GET BY USER ID (Optimized)
    // -----------------------------------------
    @Override
    public List<TicketResponseDto> getAllTicketsByUserId(Long userId) {

        List<Object[]> rows = ticketRepository.fetchTicketsByUserIdOptimized(userId);
        List<TicketResponseDto> list = new ArrayList<>();

        for (Object[] r : rows) {
            list.add(mapRowToDto(r));
        }
        return list;
    }


    // -----------------------------------------
    // GET BY ID (Fetch One)
    // -----------------------------------------
    @Override
    public TicketResponseDto getTicketById(Long id) {
        List<Object[]> list = ticketRepository.fetchAllTicketsOptimized();

        for (Object[] r : list) {
            if (r[0] != null && ((Long) r[0]).equals(id)) {
                return mapRowToDto(r);
            }
        }
        return null;
    }


    // -----------------------------------------
    // Helper: Convert Query Row → DTO
    // -----------------------------------------
    private TicketResponseDto mapRowToDto(Object[] r) {

        Long id = (Long) r[0];
        String ticketcode = (String) r[1];
        Long ticketcodecounter = (Long) r[2];

        Long interactionid = (Long) r[3];
        String interactionname = (String) r[4];
        String interactiontype = (String) r[5];

        String ticketfrom = (String) r[6];

        LocalDate actual = (LocalDate) r[7];
        LocalDate expacted = (LocalDate) r[8];

        String usermsg = (String) r[9];
        String clientmsg = (String) r[10];

        Long assigntouserid = (Long) r[11];
        String assigntoname = (String) r[12];

        Boolean isdelete = (Boolean) r[13];

        LocalDateTime created = (LocalDateTime) r[14];

        Long userid = (Long) r[15];
        String username = (String) r[16];
        String doc = (String) r[17];
        String status = (String) r[18];

        return new TicketResponseDto(
                id, ticketcode, ticketcodecounter,
                interactionid, interactionname, interactiontype,
                ticketfrom,
                actual != null ? DATE_FORMAT.format(actual) : "",
                expacted != null ? DATE_FORMAT.format(expacted) : "",
                usermsg, clientmsg,
                assigntouserid, assigntoname,
                isdelete,
                created != null ? CREATED_AT_FORMAT.format(created) : "",
                userid, username, doc, status
        );
    }
    
    @Override
    public boolean deleteTicket(Long id) {
        try {

            int updated = ticketRepository.softDeleteTicket(id);

            // If updated == 1 → soft delete successful
            return updated > 0;

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete ticket: " + e.getMessage());
        }
    }
}
