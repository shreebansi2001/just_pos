package com.crmportal.request.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class AssignDbRequest {

	@NotBlank(message = "User ID cannot be blank")
	private String userId;

	@NotBlank(message = "DB Planning ID cannot be blank")
	private String dbPlanningId;
}
