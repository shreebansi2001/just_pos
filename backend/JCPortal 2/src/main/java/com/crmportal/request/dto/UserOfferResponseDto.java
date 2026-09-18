package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOfferResponseDto {

	private Long id;
	private Long offerId;
	private String offerExpireDate;
	private Boolean isActive;
	private Boolean isDelete;
	private String createdAt;
}
