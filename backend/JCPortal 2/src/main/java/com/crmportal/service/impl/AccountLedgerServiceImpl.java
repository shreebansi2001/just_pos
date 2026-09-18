package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.repository.AccountEntryRepository;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.AccountLedgerDashboardResponseDto;
import com.crmportal.response.dto.AccountLedgerResponseDto;
import com.crmportal.service.AccountLedgerService;

@Service
public class AccountLedgerServiceImpl implements AccountLedgerService {

	@Autowired
	AccountEntryRepository accountEntryRepository;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Override
	public AccountLedgerDashboardResponseDto getAccountLedger(String startDate, String endDate, AccountType accountType,
			PaymentMode paymentMode, Long cashAccountId, Long bankAccountId, Long userId) {
		userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		boolean hasCash = cashAccountId != null && (cashAccountId > 0 || cashAccountId == -1);
		boolean hasBank = bankAccountId != null && (bankAccountId > 0 || bankAccountId == -1);

		if (hasCash == hasBank) {
		    throw new RuntimeException(
		            "Provide exactly one of cash or bank.");
		}

		if (hasCash && cashAccountId != -1) {
			cashAccountRepository.findByIdAndIsDeleteFalse(cashAccountId)
					.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + cashAccountId));
		}
		if (hasBank && bankAccountId != -1) {
			bankDetailsRepository.findByIdAndIsDeleteFalse(bankAccountId)
					.orElseThrow(() -> new RuntimeException("Bank account not found with id : " + bankAccountId));
		}
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<Object[]> result = accountEntryRepository.getAccountLedgerWithOpb(
				LocalDate.parse(startDate, dateFormatter).toString(), LocalDate.parse(endDate, dateFormatter).toString(), accountType != null ? accountType.name() : null,
				paymentMode != null ? paymentMode.name() : null, cashAccountId, bankAccountId, userId);

		return mapToLedgerDashboard(result);
	}

	public static AccountLedgerDashboardResponseDto mapToLedgerDashboard(List<Object[]> result) {

	    List<AccountLedgerResponseDto> transactions = new ArrayList<>();

	    BigDecimal totalDebit = BigDecimal.ZERO;
	    BigDecimal totalCredit = BigDecimal.ZERO;
	    BigDecimal runningBalance = BigDecimal.ZERO;
	    BigDecimal openingBalance = BigDecimal.ZERO;

	    Long count = Long.valueOf(0);
	    for (Object[] row : result) {
	    	count++;
	    	
	        BigDecimal debit = getBigDecimal(row[3]);
	        BigDecimal credit = getBigDecimal(row[4]);

	        runningBalance = runningBalance.add(debit).subtract(credit);
	        
	        if(count == Long.valueOf(1)) {
	        	openingBalance = runningBalance;
	        }
	        
	        AccountLedgerResponseDto dto = new AccountLedgerResponseDto(
	                getLocalDate(row[0]),
	                getString(row[1]),
	                getString(row[2]),
	                debit,
	                credit,
	                runningBalance
	        );

	        transactions.add(dto);

	        totalDebit = totalDebit.add(debit);
	        totalCredit = totalCredit.add(credit);
	    }

	    return new AccountLedgerDashboardResponseDto(
	            totalDebit,
	            totalCredit,
	            runningBalance, // final balance
	            openingBalance,
	            transactions
	    );
	}

	// ---------- Helper methods ----------

	private static String getLocalDate(Object obj) {
	    if (obj == null) return null;

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    LocalDate date = null;

	    if (obj instanceof java.sql.Date) {
	        date = ((java.sql.Date) obj).toLocalDate();
	    } 
	    else if (obj instanceof Timestamp) {
	        date = ((Timestamp) obj).toLocalDateTime().toLocalDate();
	    } 
	    else if (obj instanceof LocalDate) {
	        date = (LocalDate) obj;
	    } 
	    else if (obj instanceof byte[]) {
	        String str = new String((byte[]) obj);
	        date = LocalDate.parse(str);
	    }

	    return date != null ? date.format(formatter) : null;
	}

	private static String getString(Object obj) {
		return obj != null ? obj.toString() : null;
	}

	private static BigDecimal getBigDecimal(Object obj) {
		return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
	}
}
