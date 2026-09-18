package com.crmportal.request.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class EventRawMaterialRequest {
	Long eventId;
	Long eventFunctionId;
	Long rawMaterialCategoryId;
	List<EventRawMaterialDetailRequest> eventRawMaterial;
}
