package com.crmportal.request.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventRoomMasterRequestDto {

	private Long eventId;

	private Long roomId;

	private Integer price;

	private Integer qty;

	private Integer total;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDate bookingdate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDate bookingcheckoutdate;

}
