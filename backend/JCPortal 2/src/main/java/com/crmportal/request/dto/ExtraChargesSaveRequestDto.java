package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ExtraChargesSaveRequestDto {

	@NotNull(message = "eventId is required")
	private Long eventId;

	@NotNull(message = "eventFunctionId is required")
	private Long eventFunctionId;

	@NotNull(message = "userId is required")
	private Long userId;

	@Valid
	@NotNull(message = "headings list is required")
	private List<ExtraChargesHeadingRequestDto> headings;

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getEventFunctionId() {
		return eventFunctionId;
	}

	public void setEventFunctionId(Long eventFunctionId) {
		this.eventFunctionId = eventFunctionId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<ExtraChargesHeadingRequestDto> getHeadings() {
		return headings;
	}

	public void setHeadings(List<ExtraChargesHeadingRequestDto> headings) {
		this.headings = headings;
	}

	// ─── Heading DTO ────────────────────────────────────────────────────────

	public static class ExtraChargesHeadingRequestDto {

		private Long id;

		@NotBlank(message = "headingName is required")
		private String headingName;
		
		private String headingNameHindi;
		
		private String headingNameGujarati;
		
	    private String subHeadingName;
	    
	    private String subHeadingNameHindi;
	    
	    private String subHeadingNameGujarati;

		@Valid
		private List<ExtraChargesRowRequestDto> rows;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getHeadingName() {
			return headingName;
		}

		public void setHeadingName(String headingName) {
			this.headingName = headingName;
		}

		public List<ExtraChargesRowRequestDto> getRows() {
			return rows;
		}

		public void setRows(List<ExtraChargesRowRequestDto> rows) {
			this.rows = rows;
		}

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

	public static class ExtraChargesRowRequestDto {

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

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public LocalDate getChargeDate() {
			return chargeDate;
		}

		public void setChargeDate(LocalDate chargeDate) {
			this.chargeDate = chargeDate;
		}

		public LocalTime getChargeStartTime() {
			return chargeStartTime;
		}

		public void setChargeStartTime(LocalTime chargeStartTime) {
			this.chargeStartTime = chargeStartTime;
		}

		public LocalTime getChargeEndTime() {
			return chargeEndTime;
		}

		public void setChargeEndTime(LocalTime chargeEndTime) {
			this.chargeEndTime = chargeEndTime;
		}

		public String getSession() {
			return session;
		}

		public void setSession(String session) {
			this.session = session;
		}

		public Integer getPersonItem() {
			return personItem;
		}

		public void setPersonItem(Integer personItem) {
			this.personItem = personItem;
		}

		public BigDecimal getRate() {
			return rate;
		}

		public void setRate(BigDecimal rate) {
			this.rate = rate;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public void setTotal(BigDecimal total) {
			this.total = total;
		}
	}
}