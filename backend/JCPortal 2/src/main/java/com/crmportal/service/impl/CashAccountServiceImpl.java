package com.crmportal.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AccountEntryEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.OfficeExpensePayoutHistoryEntity;
import com.crmportal.entity.TripExpensePayoutHistoryEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.CashAccountMapper;
import com.crmportal.repository.AccountEntryRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.ContactTypeMasterRepository;
import com.crmportal.repository.OfficeExpensePayoutHistoryRepository;
import com.crmportal.repository.TripExpensePayoutHistoryRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.CashOpbRequestDto;
import com.crmportal.response.dto.CashOpbResponseDto;
import com.crmportal.service.CashAccountService;
import com.crmportal.utility.ConstantsPoc;

@Service
public class CashAccountServiceImpl implements CashAccountService {

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	ContactTypeMasterRepository contactTypeMasterRepository;

	@Autowired
	CashAccountMapper cashAccountMapper;
	
	@Autowired
	TripExpensePayoutHistoryRepository tripExpensePayoutHistoryRepository;
	
	@Autowired
	OfficeExpensePayoutHistoryRepository officeExpensePayoutRepository;
	
	@Autowired
	AccountEntryRepository accountEntryRepository;

	@Override
	public CashOpbResponseDto addOrUpdateCashAccount(@Valid CashOpbRequestDto request, Long id) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		ContactTypeMasterEntity contactType = contactTypeMasterRepository
				.getByIdAndIsDeleteFalse(request.getContactTypeId()).orElseThrow(
						() -> new RuntimeException("Contact type not found with id : " + request.getContactTypeId()));

		CashAccountEntity entity;
		
		Optional<CashAccountEntity> op = null;
		if (request.getIsPrimary()) {
			op = cashAccountRepository
					.findByUserIdAndIsPrimaryTrueAndIsDeleteFalse(request.getUserId());
		}
		
		if (id == -1) {
			if(op != null && op.isPresent()) {
				throw new RuntimeException("Primary cash account is alreay exist.");
			}
			entity = new CashAccountEntity();
			entity = cashAccountMapper.requestToEntity(request);
		} else {
			entity = cashAccountRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + id));

			if(op != null && op.isPresent()) {
				if(op.get().getId() != entity.getId()) {
					throw new RuntimeException("Primary cash account is alreay exist.");
				}else {
					entity.setIsPrimary(request.getIsPrimary());
				}
			}else {
				entity.setIsPrimary(request.getIsPrimary());
			}
			
			entity.setAccountName(request.getAccountName());
			entity.setOpeningBalance(request.getOpeningBalance());
			entity.setCurrentBalance(request.getCurrentBalance());
			entity.setDescription(request.getDescription());
		}
		entity.setContactType(contactType);

		entity = cashAccountRepository.save(entity);

		CashOpbResponseDto response = cashAccountMapper.entityToResponse(entity);

		return response;
	}

	@Override
	public List<CashOpbResponseDto> getAllByUserId(Long userId, Boolean isPrimary) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		Boolean isAdmin = false;
		if (userId == 1 || user.getClientId() == 0) {
			isAdmin = true;
		}

		List<CashAccountEntity> entities = cashAccountRepository.getAllByUserId(userId, isAdmin, isPrimary);
		List<CashOpbResponseDto> response = cashAccountMapper.entityToResponse(entities);
		return response;
	}

	@Override
	public CashOpbResponseDto getById(Long id) {
		CashAccountEntity entity = cashAccountRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + id));

		CashOpbResponseDto response = cashAccountMapper.entityToResponse(entity);

		return response;
	}
	
	@Override
	public Map<String, Object> deleteById(Long id) {

	    Map<String, Object> response = new LinkedHashMap<>();

	    CashAccountEntity entity = cashAccountRepository.findByIdAndIsDeleteFalse(id)
	            .orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + id));

	    List<TripExpensePayoutHistoryEntity> tripExpenses =
	            tripExpensePayoutHistoryRepository.findAllByCashTypeAndIsDeleteFalse(entity);

	    List<OfficeExpensePayoutHistoryEntity> officeExpenses =
	            officeExpensePayoutRepository.findAllByCashTypeAndIsDeleteFalse(entity);

	    List<AccountEntryEntity> accountEntries =
	            accountEntryRepository.findAllByCashTypeAndIsDeleteFalse(entity);

	    boolean isUsed = !tripExpenses.isEmpty() || !officeExpenses.isEmpty() || !accountEntries.isEmpty();

	    if (isUsed) {
	        response.put("success", false);
	        response.put("msg", "Cannot delete. This account is already used in transactions.");
	        response.put("isTripExpense", !tripExpenses.isEmpty());
	        response.put("isOfficeExpense", !officeExpenses.isEmpty());
	        response.put("isAccountEntry", !accountEntries.isEmpty());
	        return response;
	    }

	    entity.setIsDelete(true);
	    cashAccountRepository.save(entity);

	    response.put("success", true);
	    response.put("msg", ConstantsPoc.CASH_OPB_DELETE_SUCCESS);

	    return response;
	}

}
