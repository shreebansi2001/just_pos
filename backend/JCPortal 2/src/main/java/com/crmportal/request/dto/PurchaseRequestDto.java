package com.crmportal.request.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDto {

    private Long id; 

    @NotBlank(message = "Request code is mandatory")
    @Size(max = 20, message = "Request code cannot exceed 20 characters")
    private String requestCode;

    @NotNull(message = "Start date is mandatory")
    private String startDate;

    private String endDate;

    @NotBlank(message = "Status is mandatory")
    private String status;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    @NotNull(message = "User ID is mandatory")
    private Long userId; 

    private Long approvedBy;
    
    @NotEmpty(message = "At least one item must be added to the purchase request")
    @Valid 
    private List<PurchaseRequestDetailsDto> requestDetails;
}