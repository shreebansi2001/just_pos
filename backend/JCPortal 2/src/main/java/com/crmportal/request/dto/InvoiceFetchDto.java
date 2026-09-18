package com.crmportal.request.dto;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceFetchDto {

    @NotNull(message = "Total outstanding receivable cannot be null")
    @Min(value = 0, message = "Total outstanding receivable cannot be negative")
    private Integer totalOutstandingReceivable;

    @NotNull(message = "Due today cannot be null")
    @Min(value = 0, message = "Due today cannot be negative")
    private Integer dueToday;

    @NotNull(message = "Due within 30 days cannot be null")
    @Min(value = 0, message = "Due within 30 days cannot be negative")
    private Integer dueWithin30Days;

    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;

    @NotBlank(message = "Plan cannot be blank")
    private String plan;

    @NotNull(message = "Total paid cannot be null")
    @Min(value = 0, message = "Total paid cannot be negative")
    private Integer totalPaid;

    @NotNull(message = "Balance due cannot be null")
    @Min(value = 0, message = "Balance due cannot be negative")
    private Integer balanceDue;
}
