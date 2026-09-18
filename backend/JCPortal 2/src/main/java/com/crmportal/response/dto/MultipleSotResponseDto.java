package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleSotResponseDto {

	private Long userId;
	
	private List<MultipleSotDetailsResponseDto> details;
}
