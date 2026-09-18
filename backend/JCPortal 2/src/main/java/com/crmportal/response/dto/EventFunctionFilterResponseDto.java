package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionFilterResponseDto {

    private Long eventFunctionId;
    private Long eventId;
    private Long userId;
    private Long partyId;
    private Long functionId;

    private Integer price;

    private String partyNameEnglish;
    private String partyNameHindi;
    private String partyNameGujarati;

    private String functionNameEnglish;
    private String functionNameHindi;
    private String functionNameGujarati;

    private String eventDateTime;
    private String functionStartDateTime;
    private String functionEndDateTime;
}