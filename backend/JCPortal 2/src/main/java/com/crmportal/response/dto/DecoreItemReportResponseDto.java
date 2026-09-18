package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoreItemReportResponseDto {

	private Long id;
    private String nameEnglish;
    private String nameHindi;
    private String nameGujarati;
    private String slogan;
    private String imagePath;
    private String decoreItemNotes;
    private String partyName;
    private String subItem;
    private String subItemHindi;
    private String subItemGujarati;
    private Integer itemQty;
    private BigDecimal price;
    private Integer itemSpace;
    private String vendorName;
}
