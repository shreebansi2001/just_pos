package com.crmportal.request.dto;

import java.util.List;

import lombok.Data;

@Data
public class EventRawMaterialAppRequest {
	Long eventId;
	Long rawMaterialCategoryId;
	List<EventRawMaterialAppRequestDetails> eventRawMaterial;
}
