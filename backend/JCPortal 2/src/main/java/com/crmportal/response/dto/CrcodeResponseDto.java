// CrcodeResponseDto.java
package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrcodeResponseDto {
    private Long id;
    private String crcode;
}