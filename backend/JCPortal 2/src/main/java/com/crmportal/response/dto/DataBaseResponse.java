package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBaseResponse {

	private Long id;
	private String dbName;
	private String state;
	private String uuid;
	private String instructions;
	private Boolean isPublished;
	private Long userId;
	private String userName;
	private Long parentDbId;
	private String parentDbName;
	
}
