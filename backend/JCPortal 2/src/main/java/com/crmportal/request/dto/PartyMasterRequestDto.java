package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyMasterRequestDto {

	@NotBlank(message = "Name (English) is required")
    private String nameEnglish;

    private String nameHindi;

    private String nameGujarati;

    private String addressEnglish;

    private String addressHindi;

    private String addressGujarati;

    @NotNull(message = "Contact Category ID is required")
    private Long contactCategoryId;

    private String email;

    private String mobileno;

    private String altMobileno;

    private String gst;

    private String bdate;

    private String document;
    
    private String partyCode;

    private Long userId;
    
    private BigDecimal opb;
    
    private String opbDate;
    
    private String type;
    
    private String pan;
    
    private BigDecimal price;
    
    private BigDecimal helperPrice = BigDecimal.ZERO;
    
    private BigDecimal counterPrice = BigDecimal.ZERO;    
}
