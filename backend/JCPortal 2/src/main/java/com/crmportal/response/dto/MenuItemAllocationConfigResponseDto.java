package com.crmportal.response.dto;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.crmportal.request.dto.ChefLabourItemRequestDto;
import com.crmportal.request.dto.InsideItemRequestDto;
import com.crmportal.request.dto.OutsideItemRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemAllocationConfigResponseDto {

	private Long id;
	private String godownLocation;
	private Boolean selectOutsideAgency;
	private Boolean selectInsideAgency;
	private Boolean selectChefLabourAgency;
	private String remarks;
	private MenuItemPartyMasterResponseDto party;
	private ChefLabourItemResponseDto chefLabourItem;
	private OutsideItemResponseDto outsideItem;
	private InsideItemResponseDto insideItem;
}
