package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ticket_id")
	private Long id;
	
	@Column(name = "ticketcode", columnDefinition = "VARCHAR(50)")
	private String ticketcode;
	
	@Column(name = "ticketcodecounter")
	private Long ticketcodecounter;

	@Column(name = "interactionid")
	private Long interactionid;
	
	@Column(name = "interactionname", columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String interactionname;

	@Column(name = "interactiontype", columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String interactiontype;


	@Column(name = "ticketfrom", columnDefinition = "VARCHAR(50)")
	private String ticketfrom;
	
	@Column(name = "actualclosedate", nullable = true)
	private LocalDate actualclosedate;
	
	@Column(name = "expactedclosedate", nullable = true)
	private LocalDate expactedclosedate;

	@Column(name = "usermsg", columnDefinition = "VARCHAR(1000) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String usermsg;
	
	@Column(name = "clientmsg", columnDefinition = "VARCHAR(1000) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String clientmsg;
	
	@Column(name = "assigntouserid")
	private Long assigntouserid = 0l;
	
	@Column(name = "assigntoname", columnDefinition = "VARCHAR(80)")
	private String assigntoname;
	
	@Column(name = "status", columnDefinition = "VARCHAR(30)")
	private String status;
	
	@Column(name = "documentpath", columnDefinition = "VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String documentpath;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "userid")
	private Long userid = 0l;

}
