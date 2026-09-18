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
@Table(name = "event_rooms")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRoomMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_room_id")
	private Long id;

	@Column(name = "bookingdate")
	private LocalDate bookingdate;
	
	@Column(name = "bookingcheckoutdate")
	private LocalDate bookingcheckoutdate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private RoomMaster room;

	@Column(name = "price")
	private int price=0;
	
	@Column(name = "qty")
	private int qty=1;
	
	@Column(name = "total")
	private int total=0;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
//	
//	@OneToMany(mappedBy = "eventFunction", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuPreparationEntity> menuPraparations = new ArrayList<>();

}
