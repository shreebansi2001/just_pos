package com.crmportal.response.dto;

import java.util.List;

import lombok.Data;

@Data
public class PlansResponseDto {
	
	private Long id;
	private String name;
	private Double price;
	private String billingCycle;
	private String description;
	private Boolean isPopular;
	private String createdAt;
	private List<PlanFeatureResponseDto> features;
}
