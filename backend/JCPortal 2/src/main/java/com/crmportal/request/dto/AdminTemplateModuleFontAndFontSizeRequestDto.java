package com.crmportal.request.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminTemplateModuleFontAndFontSizeRequestDto {

    @NotNull(message = "Admin template module id is required.")
    private Long adminTemplateModuleId;

    @NotNull(message = "Category font id is required.")
    private Long catFontId;
    
    @NotNull(message = "Item font id is required.")
    private Long itemFontId;
    
    @NotNull(message = "Slogan font id is required.")
    private Long sloganFontId;
    
    @NotNull(message = "Category font size is required.")
    private Integer catFontSize;
    
    @NotNull(message = "Item font size is required.")
    private Integer itemFontSize;
    
    @NotNull(message = "Slogan font size is required.")
    private Integer sloganFontSize;
}
