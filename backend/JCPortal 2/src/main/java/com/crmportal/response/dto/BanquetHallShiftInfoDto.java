package com.crmportal.response.dto;

import java.time.LocalDate;

import com.crmportal.request.dto.BanquetHallShiftRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BanquetHallShiftInfoDto {
	private Long banquetHallId;
	private String banquetHallName;
	private Long shiftId;
	private String shiftName;
	private String shiftStartTime;
	private String shiftEndTime;
	private String bookingDate;

	public BanquetHallShiftInfoDto(Long banquetHallId, String banquetHallName, Long shiftId, String shiftName,
			String shiftStartTime, String shiftEndTime, LocalDate bookingDate) {

		this.banquetHallId = banquetHallId;
		this.banquetHallName = banquetHallName;
		this.shiftId = shiftId;
		this.shiftName = shiftName;
		this.shiftStartTime = shiftStartTime;
		this.shiftEndTime = shiftEndTime;
		this.bookingDate = bookingDate != null ? bookingDate.toString() : null;
	}
}
