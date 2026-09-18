package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class TicketCommentRequestDTO {
	
    @NotNull(message = "Ticket ID is required")
    private Long ticketId;

    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    @NotBlank(message = "Comment by is required")
    @Size(max = 80, message = "CommentBy cannot exceed 80 characters")
    private String commentBy;
}
