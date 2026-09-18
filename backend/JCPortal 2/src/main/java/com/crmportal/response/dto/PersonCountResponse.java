package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonCountResponse {

	private Integer countIn;

    private Integer countOut;

    private Integer totalIn;

    private Integer totalOut;
    
}
