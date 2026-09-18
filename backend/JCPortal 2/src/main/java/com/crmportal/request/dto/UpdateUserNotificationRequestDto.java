package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserNotificationRequestDto {

	private Long id;
	private String key1;
	private String key2;
	private String url;

}
