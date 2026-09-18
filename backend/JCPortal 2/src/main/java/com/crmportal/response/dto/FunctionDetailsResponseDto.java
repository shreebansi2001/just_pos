package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDetailsResponseDto {

	private EventFunctionMasterResponseDto eventFunction;
	private List<MenuAllocationItemsResponseDto> allocationItems;
}
