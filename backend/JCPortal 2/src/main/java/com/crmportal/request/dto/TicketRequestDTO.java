package com.crmportal.request.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class TicketRequestDTO {
	
	private Long interactionid;
	
	private String interactionname;
	private String interactiontype;

	private String ticketfrom;

    private String expactedclosedate; // dd/MM/yyyy
    private String actualclosedate;   // dd/MM/yyyy

    private String usermsg;
    private String clientmsg;

    private Long assigntouserid;
    private String assigntoname;

    private String status;

    private Long userid;
    
}
