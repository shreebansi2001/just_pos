package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionReportResponseDto {

    private Long functionId;
    private String functionName;
    private String functionStartTimestamp;
    private String functionEndTimestamp;
    private String functionVenue;
    private String functionVenueHindi;
    private String functionVenueGujarati;
    private Integer pax;
    private Double rate;
    private Double defaultPrice;
    private Boolean isPackage;
    private BigDecimal packagePrice; 
    private String notesEnglish;
    private String notesHindi;
    private String notesGujarati;
    private String foodType;
    private String mainFunction;
    private String ratePostFix;
    private Long banquetHallId;
    private String banquetHallName;
    private String packageName;
    private BigDecimal packPrice;
    private List<MenuReportResponseDto> menuCategories;
    private List<DecoreReportResponseDto> decoreCategories;
}