package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialPartyDTO {

    private Long partyId;
    private String partyName;
    private String contactCategoryName;
    private String mobileNo;
}
