package com.crmportal.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ExtraChargesResponseDto {

    private Long eventId;
    private Long eventFunctionId;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    private List<ExtraChargesHeadingResponseDto> headings;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getEventFunctionId() { return eventFunctionId; }
    public void setEventFunctionId(Long eventFunctionId) { this.eventFunctionId = eventFunctionId; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public List<ExtraChargesHeadingResponseDto> getHeadings() { return headings; }
    public void setHeadings(List<ExtraChargesHeadingResponseDto> headings) { this.headings = headings; }


    // ─── Heading DTO ────────────────────────────────────────────────────────

    public static class ExtraChargesHeadingResponseDto {

        private Long id;
        private String headingName;
        private String headingNameHindi;
        private String headingNameGujarati;
        private String subHeadingName;
        private String subHeadingNameHindi;
        private String subHeadingNameGujarati;
        private BigDecimal headingTotal = BigDecimal.ZERO;
        private List<ExtraChargesRowResponseDto> rows;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getHeadingName() { return headingName; }
        public void setHeadingName(String headingName) { this.headingName = headingName; }

        public BigDecimal getHeadingTotal() { return headingTotal; }
        public void setHeadingTotal(BigDecimal headingTotal) { this.headingTotal = headingTotal; }

        public List<ExtraChargesRowResponseDto> getRows() { return rows; }
        public void setRows(List<ExtraChargesRowResponseDto> rows) { this.rows = rows; }
		public String getSubHeadingName() {
			return subHeadingName;
		}
		public void setSubHeadingName(String subHeadingName) {
			this.subHeadingName = subHeadingName;
		}
		public String getHeadingNameHindi() {
			return headingNameHindi;
		}
		public void setHeadingNameHindi(String headingNameHindi) {
			this.headingNameHindi = headingNameHindi;
		}
		public String getHeadingNameGujarati() {
			return headingNameGujarati;
		}
		public void setHeadingNameGujarati(String headingNameGujarati) {
			this.headingNameGujarati = headingNameGujarati;
		}
		public String getSubHeadingNameHindi() {
			return subHeadingNameHindi;
		}
		public void setSubHeadingNameHindi(String subHeadingNameHindi) {
			this.subHeadingNameHindi = subHeadingNameHindi;
		}
		public String getSubHeadingNameGujarati() {
			return subHeadingNameGujarati;
		}
		public void setSubHeadingNameGujarati(String subHeadingNameGujarati) {
			this.subHeadingNameGujarati = subHeadingNameGujarati;
		}
        
        
    }


    // ─── Row DTO ────────────────────────────────────────────────────────────

    public static class ExtraChargesRowResponseDto {

        private Long id;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        private LocalDate chargeDate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        private LocalTime chargeStartTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        private LocalTime chargeEndTime;

        private String session;
        private Integer personItem;
        private BigDecimal rate;
        private BigDecimal total;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public LocalDate getChargeDate() { return chargeDate; }
        public void setChargeDate(LocalDate chargeDate) { this.chargeDate = chargeDate; }

        public LocalTime getChargeStartTime() { return chargeStartTime; }
        public void setChargeStartTime(LocalTime chargeStartTime) { this.chargeStartTime = chargeStartTime; }

        public LocalTime getChargeEndTime() { return chargeEndTime; }
        public void setChargeEndTime(LocalTime chargeEndTime) { this.chargeEndTime = chargeEndTime; }

        public String getSession() { return session; }
        public void setSession(String session) { this.session = session; }

        public Integer getPersonItem() { return personItem; }
        public void setPersonItem(Integer personItem) { this.personItem = personItem; }

        public BigDecimal getRate() { return rate; }
        public void setRate(BigDecimal rate) { this.rate = rate; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }
}