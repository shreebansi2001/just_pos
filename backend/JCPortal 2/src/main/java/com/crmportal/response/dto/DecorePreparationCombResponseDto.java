package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePreparationCombResponseDto {

	private DecorePreparationResponseDto decorePreparation;

	private List<DecorePreparationItemResponseDto> decorePreparationItems;

	private List<DecorePreparationSelectedItemDetailsResponseDto> selectedDecorePreparationItems;

	private List<CustomPackageDetailsResponseDto> customPackageDetails;
}