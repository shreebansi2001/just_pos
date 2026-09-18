package com.crmportal.request.dto;

import lombok.Data;

@Data
public class RoomMasterRequestDto {

    private String nameEnglish;

    private String nameHindi;

    private String nameGujarati;

    private Integer price;

    private Boolean isActive;

    private Long userId;
}