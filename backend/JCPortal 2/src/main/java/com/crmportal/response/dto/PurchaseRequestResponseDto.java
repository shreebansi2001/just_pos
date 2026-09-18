package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestResponseDto {

	private Long id;
    private String requestCode;
    private String startDate;
    private String endDate;
    private String status;
    private String remarks;
    private Long userId;
    private String requestBy;
    private Boolean isDelete;
    private String createdAt;
    private String updatedAt;

    private List<PurchaseRequestDetailsResponseDto> requestDetails;
    
    private Long approvedBy;
    private String approvedByName;
}
