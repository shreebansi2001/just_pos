package com.crmportal.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GstReportResponseDTO {

    private String type;        // "SALES" or "PURCHASE"
    private String fromDate;
    private String toDate;
    private List<?> data;
}