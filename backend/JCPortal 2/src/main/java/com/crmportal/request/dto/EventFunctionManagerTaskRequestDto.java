package com.crmportal.request.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionManagerTaskRequestDto {

	private Long id;
	private Long managerTaskId;
	private Long managerId;
	private Long eventFunctionId;
	private Long eventId;
	private String remarks;
	private String latitude;
	private String longitude;
	private String status;
	private List<SpecialNotesImagesRequestDto> files;

}
