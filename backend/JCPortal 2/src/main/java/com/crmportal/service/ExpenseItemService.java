package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ExpenseItemRequestDto;
import com.crmportal.response.dto.ExpenseItemResponseDto;

@Service
public interface ExpenseItemService {

	ExpenseItemResponseDto addOrUpdateExpenseItem(ExpenseItemRequestDto request, Long expenseItemId);

	Boolean deleteExpenseItem(Long expenseItemId);

	List<ExpenseItemResponseDto> getExpenseItemByExpenseAndEvent(Long expenseId, Long eventId);

}
