// Response
package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class StoreManageResponseDto {
    private Long   id;
    private String voucherNo;
    private String manageDate;
    private Long   userId;
    private String createdAt;
    private List<StoreManageCategoryDto> categories;
    private Long stockTypeId;
    private String stockTypeName;
}