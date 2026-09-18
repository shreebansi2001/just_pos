package com.crmportal.request.dto;

import lombok.Data;

@Data
public class UserRightsMasterRequestDto {

    private Boolean add;
    private Boolean edit;
    private Boolean delete;
    private Boolean view;
    private Long pageid;
    private Long moduleId;
}
