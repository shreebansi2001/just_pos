package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventOverViewResponseDto {

	private EventMenuAllocationOverviewResponseDto menuAllocationOverview;
	private List<EventRawMaterialOverviewResponseDto> rawMaterialOverview;
	private List<EventAgencyDistributionOverviewResponseDto> agencyDistributionOverview;

}
