package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerTaskSummaryResponseDto {

	private Long totalTasks;
	private Long totalPending;
	private Long totalInProgress;
	private Long totalCompleted;
	private Long totalCancel;

	private Double pendingPercentage;
	private Double inProgressPercentage;
	private Double completedPercentage;
	private Double cancelPercentage;
}