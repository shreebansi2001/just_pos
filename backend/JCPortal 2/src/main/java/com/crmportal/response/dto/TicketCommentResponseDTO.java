package com.crmportal.response.dto;

import lombok.Data;

@Data
public class TicketCommentResponseDTO {
	private Long ticketCommentId;
    private Long ticketId;
    private String comment;
    private String commentBy;
    private String createdAt;  // formatted string
}