package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRightsMasterResponseDto {

    private Long id;
    private Long pageid;
    private String pageName;
    private Boolean add;
    private Boolean edit;
    private Boolean delete;
    private Boolean view;
}
