package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResponseDto {

    private Long id;

    private String ticketcode;
    private Long ticketcodecounter;

    private Long interactionid;
    private String interactionname;
    private String interactiontype;

    private String ticketfrom;

    private String actualclosedate;      // dd/MM/yyyy
    private String expactedclosedate;    // dd/MM/yyyy

    private String usermsg;
    private String clientmsg;

    private Long assigntouserid;
    private String assigntoname;

    private Boolean isdelete;

    private String createdAt;            // dd/MM/yyyy HH:mm a

    private Long userid;
    private String username;             // firstname + lastname
    private String documentpath;
    
    private String status;
    
    
}