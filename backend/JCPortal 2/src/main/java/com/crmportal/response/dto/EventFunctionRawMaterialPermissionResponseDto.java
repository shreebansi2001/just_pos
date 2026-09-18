package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionRawMaterialPermissionResponseDto {
	private Long eventId;
	private Long eventFunctionId;
	private List<EventFunctionRawMaterialDto> permissables;
	private List<EventFunctionRawMaterialDto> notPermissables;
}
