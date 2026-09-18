package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.BankDetailsMapper;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.BankDetailsRequestDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.service.BankDetailsService;
import com.crmportal.service.CommonService;
import com.crmportal.service.UserFileService;

import java.util.Optional;

import javax.management.RuntimeErrorException;

@Service
public class BankDetailsServiceImpl implements BankDetailsService {

	@Autowired
	BankDetailsMapper bankDetailsMapper;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UserFileService userFileService;

	@Autowired
	Environment environment;

	@Override
	public BankDetailsResponseDto addUpdateBankDetails(BankDetailsRequestDto request) {
		try {
			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

			BankDetailsEntity entity = new BankDetailsEntity();

			if (request.getId() == -1) {
				if (request.getIsPrimary()) {
					Optional<BankDetailsEntity> isQuotationEntity = bankDetailsRepository
							.findByUserAndIsPrimaryTrueAndIsDeleteFalse(user);
					if (isQuotationEntity.isPresent()) {
						throw new RuntimeException("Quotation / Invoice Bank Detail is already present.");
					}
				}

				entity = bankDetailsMapper.requestToEntity(request);
			} else {
				Optional<BankDetailsEntity> optEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(request.getId());
				if (!optEntity.isPresent()) {
					throw new RuntimeException("Bank detail not found with id : " + request.getId());
				}

				entity = bankDetailsMapper.updateEntityFromRequest(optEntity.get(), request);
				entity.setUpdatedAt(commonService.getCurrentDateTime());
			}
			entity.setUser(user);
			entity = bankDetailsRepository.save(entity);

			BankDetailsResponseDto responseDto = bankDetailsMapper.entityToResponse(entity);

			Map<String, Object> data = new HashMap<>();
			if (request.getQrCodeImage() != null) {
				data = userFileService.storeFile(user.getId(), ModuleName.QRCODE.toString(),
						entity.getId(), FileType.OTHER.toString(), request.getQrCodeImage());
				responseDto.setQrCodePath(data.get("fullPath").toString());
			}


			return responseDto;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	@Override
	public List<BankDetailsResponseDto> getBankDetailsByUserId(Long userId) {

		Long adminId;
		
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		if (userId == 1 || user.getClientId() == 0) {
			adminId = userId;
		}else {
			adminId = user.getClientId();
		}
		
		List<BankDetailsEntity> entities = bankDetailsRepository.findByUserIdAndIsDeleteFalse(adminId);
		if (entities.isEmpty()) {
			throw new RuntimeException("Bank detail not found with user id : " + userId);
		}

		List<BankDetailsResponseDto> responseDtos = new ArrayList<>();
		for (BankDetailsEntity entity : entities) {
			BankDetailsResponseDto dto = bankDetailsMapper.entityToResponse(entity);
			dto.setQrCodePath(environment.getProperty("app.image.url") + entity.getQrCode());
			dto.setUserId(entity.getUser().getId());
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	@Override
	public List<BankDetailsResponseDto> getBankDetails(Long id) {

		List<BankDetailsResponseDto> responseDtos = new ArrayList<>();

		if (id != null) {
			Optional<BankDetailsEntity> optEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(id);
			if (!optEntity.isPresent()) {
				throw new RuntimeException("Bank detail not found with id : " + id);
			}
			BankDetailsEntity bankDetails = optEntity.get();
			BankDetailsResponseDto responseDto = bankDetailsMapper.entityToResponse(bankDetails);
			responseDto.setQrCodePath(environment.getProperty("app.image.url") + bankDetails.getQrCode());
			responseDto.setUserId(optEntity.get().getUser().getId());
			responseDtos.add(responseDto);
		} else {
			List<BankDetailsEntity> entities = bankDetailsRepository.findByIsDeleteFalse();
			for (BankDetailsEntity bankDetailsEntity : entities) {
				BankDetailsResponseDto responseDto = bankDetailsMapper.entityToResponse(bankDetailsEntity);
				responseDto.setQrCodePath(environment.getProperty("app.image.url") + bankDetailsEntity.getQrCode());
				responseDto.setUserId(bankDetailsEntity.getUser().getId());
			}
		}

		return responseDtos;
	}

	@Override
	public Boolean deleteBankAccountById(Long bankAccountId) {
		BankDetailsEntity bankDetails = bankDetailsRepository.findByIdAndIsDeleteFalse(bankAccountId)
				.orElseThrow(() -> new RuntimeException("Bank details not found with id : " + bankAccountId));
		
		bankDetails.setIsDelete(true);
		
		bankDetails = bankDetailsRepository.save(bankDetails);
		
		return true;
	}
}
