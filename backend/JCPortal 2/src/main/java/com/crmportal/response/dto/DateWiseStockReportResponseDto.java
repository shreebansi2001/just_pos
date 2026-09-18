package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class DateWiseStockReportResponseDto {
    private String fromDate;
    private String toDate;
    private String categoryName;
    private String stockType;
    private long totalItems;
    private int totalPages;
    private List<DateWiseStockRowDto> items;
	/*
	 * private long totalRecords; private int totalPages; private int currentPage;
	 * private int pageSize;
	 */
}