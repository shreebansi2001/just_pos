// JournalVoucherDetailResponseDto.java
package com.crmportal.response.dto;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class JournalVoucherDetailResponseDto {
    private Long   id;
    private Long   partyId;
    private String partyName;
    private BigDecimal amount;
    private String creditDebit;
    private String particular;
}