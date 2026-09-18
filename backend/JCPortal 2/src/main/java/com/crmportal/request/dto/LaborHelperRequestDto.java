package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class LaborHelperRequestDto {
    private Long id;
    private Long partyId;
    private Long contactCategoryId;
    private String name;
    private String phonenumber;
    private String aadharcard;
    private String pancard;
    private Long userId;
    
    // List of files for upload
    private List<FileWithIdRequestDto> files;
}
