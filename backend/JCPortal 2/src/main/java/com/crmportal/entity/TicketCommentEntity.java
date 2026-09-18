package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket_comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketCommentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ticket_comment_id")
	private Long id;
	
	@Column(name = "ticketid")
	private Long ticketid;

	@Column(name = "comment", columnDefinition = "VARCHAR(1000) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String comment;
	
	@Column(name = "commentby", columnDefinition = "VARCHAR(80)")
	private String commentby;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
}
