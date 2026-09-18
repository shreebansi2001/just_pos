package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class StoreManageRequestDto {
    private Long   userId;
    private String manageDate;  // dd/MM/yyyy — null = today
    private List<StoreManageItemRequestDto> items;
    private Long stockTypeId; 
}