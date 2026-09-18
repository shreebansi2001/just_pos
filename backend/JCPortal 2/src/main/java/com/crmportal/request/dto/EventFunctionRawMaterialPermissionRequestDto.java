package com.crmportal.request.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionRawMaterialPermissionRequestDto {
	private Long userId;
	private List<EventFunctionRawMaterialRequestDto> permissables = new ArrayList<>();
	private List<EventFunctionRawMaterialRequestDto> notPermissables = new ArrayList<>();
}
