package com.crmportal.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ExpenseRequestDto;
import com.crmportal.request.dto.OfficeExpensePayoutRequestDto;
import com.crmportal.request.dto.OfficeExpenseRequestDto;
import com.crmportal.request.dto.TripExpensePayoutRequestDto;
import com.crmportal.response.dto.AllExpensesResponseDto;
import com.crmportal.response.dto.ExpenseResponseDto;
import com.crmportal.response.dto.MemberResponseDto;
import com.crmportal.response.dto.OfficeExpensePayoutResponseDto;
import com.crmportal.response.dto.OfficeExpenseResponseDto;
import com.crmportal.response.dto.TripExpensePayoutHistoryResponseDto;

@Service
public interface ExpenseService {

	ExpenseResponseDto addOrUpdateExpense(ExpenseRequestDto request);

	ExpenseResponseDto getByExpenseId(Long expenseId);

	List<ExpenseResponseDto> getAllTripExpense(Long userId);

//	Boolean payoutExpense(Long expenseId, String payoutType, BigDecimal payoutAmount);

	Boolean deleteExpense(Long expenseId);

	OfficeExpenseResponseDto addOrUpdateOfficeExpense(OfficeExpenseRequestDto request);

	Boolean deleteOfficeExpense(Long expenseId);

//	Boolean payoutOfficeExpense(Long expenseId, String payoutType, BigDecimal payoutAmount);

	List<OfficeExpenseResponseDto> getAllOfficeExpense(Long userId);

	OfficeExpenseResponseDto getOfficeExpenseByExpenseId(Long expenseId);

	Map<String, Object> getOfficeExpenseByExpenseType(Long incomeExpenseTypeId, Long userId, String startDate, String endDate, Long accountContactId);

	Map<String, Object> getTripExpenseByExpenseType(String expenseType, Long userId, String startDate, String endDate, Long accountContactId);

	Map<String, Object> addOrUpdateCloseDate(String startDate, String closeDate, Long userId);

	Map<String, Object> getCloseDate(Integer month, Integer year, Long userId);

	AllExpensesResponseDto getAllExpenses(Long userId, String startDate, String endDate, Long accountContactId);

	TripExpensePayoutHistoryResponseDto payoutExpense(TripExpensePayoutRequestDto payout);

	Boolean deleteTripPayout(Long payoutId);

	List<TripExpensePayoutHistoryResponseDto> getAllTripPayoutByExpenseId(Long expenseId);

	TripExpensePayoutHistoryResponseDto getTripPayoutByPayoutId(Long payoutId);

	OfficeExpensePayoutResponseDto payoutOfficeExpense(@Valid OfficeExpensePayoutRequestDto request);
	
	List<OfficeExpensePayoutResponseDto> getAllOfficePayoutByExpenseId(Long expenseId);
	
	OfficeExpensePayoutResponseDto getOfficePayoutByPayoutId(Long payoutId);

	Boolean deleteOfficePayout(Long payoutId);

	List<MemberResponseDto> getAllMembers(Long userId);

	Boolean deleteOfficeExpenseFile(Long id);

}
