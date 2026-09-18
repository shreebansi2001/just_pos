package com.crmportal.response.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JTEnquiryResponseDto {

	private Long id;

    private String fullName;

    private String mobileNo;

    private String cmpName;

    private String city;

    private String notes;

	private Set<String> eventDates;
}