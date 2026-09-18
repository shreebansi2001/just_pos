// JournalVoucherResponseDto.java
package com.crmportal.response.dto;
import java.util.List;
import lombok.Data;

@Data
public class JournalVoucherResponseDto {
    private Long   id;
    private String voucherNo;
    private String voucherDate;
    private String narration;
    private Long   userId;
    private String createdAt;
    private List<JournalVoucherDetailResponseDto> details;
}