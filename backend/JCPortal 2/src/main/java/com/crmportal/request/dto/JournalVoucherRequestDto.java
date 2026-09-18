// JournalVoucherRequestDto.java
package com.crmportal.request.dto;
import java.util.List;
import lombok.Data;

@Data
public class JournalVoucherRequestDto {
    private Long   id;           // null or 0 = new
    private String voucherDate;  // dd/MM/yyyy
    private String narration;
    private Long   userId;
    private List<JournalVoucherDetailRequestDto> details;
}