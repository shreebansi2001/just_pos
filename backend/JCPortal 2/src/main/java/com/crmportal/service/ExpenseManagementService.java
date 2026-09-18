package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.request.dto.ExpenseManagementRequestDto;
import com.crmportal.response.dto.ExpenseManagementResponseDto;

@Service
public interface ExpenseManagementService {

	Map<String, Object> getExpensesUserType(String userType, Long eventId, Long userId);

	ExpenseManagementResponseDto getExpensesById(Long expenseId);

	Boolean deleteExpensesById(Long expenseId);

	ExpenseManagementResponseDto addOrUpdateExpenseMaster(ExpenseManagementRequestDto request);

	List<ExpenseManagementResponseDto> getAllExpenses(Long eventId, Long userId);

}
