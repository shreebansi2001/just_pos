// JournalVoucherDetailRequestDto.java
package com.crmportal.request.dto;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class JournalVoucherDetailRequestDto {
    private Long   partyId;
    private BigDecimal amount;
    private String creditDebit;  // "CR" or "DR"
    private String particular;
}