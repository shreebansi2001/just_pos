package com.crmportal.response.dto;

import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuAllocationFunctionFullResponseDto {

	private EventFunctionMasterResponseDto eventFunction;
	private List<EventFunctionMenuAllocationResponseDto> menuAllocation;
	private List<SelectedMenuItemForMenuAllocationResponseDto> selectedItemDetails;
	private BigInteger totalChefPrice;
	private BigInteger totalOutSidePrice;
}
