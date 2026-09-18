package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueDetailsResponseDto {

	private Long rawCatId;
	private String rawCatName;
	
	private List<StoreIssueItemsResponseDto> items;
}
