package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AITemplateEntity;
import com.crmportal.entity.PaymentInfo;
import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.UpgradedModuleEntity;
import com.crmportal.entity.UserAssigneAITemplateEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserNotificationConfigEntity;
import com.crmportal.entity.UserPlansHistoryEntity;
import com.crmportal.entity.UserUpgradedModuleEntity;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.UserPlansHistoryRepository;
import com.crmportal.response.dto.PaymentOrderDto;
import com.crmportal.response.dto.PaymentResponseDto;
import com.crmportal.service.PaymentInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {

	@Autowired
	private PaymentInfoRepository paymentInfoRepository;

	@Autowired
	UserPlansHistoryRepository userPlansHistoryRepository;

	@Transactional
	@Override
	public PaymentInfo savePaymentResponse(PaymentResponseDto request, UserMasterEntity user, PlansEntity plan,
			UserPlansHistoryEntity userPlanEntity, UpgradedModuleEntity moduleEntity,
			UserUpgradedModuleEntity userUpgradedModule, UserNotificationConfigEntity userNotification,
			UserAssigneAITemplateEntity userAiTemplate, AITemplateEntity aiTemplate) {
		PaymentInfo p = new PaymentInfo();

		if (plan != null && userPlanEntity != null) {
			UserPlansHistoryEntity historyResponseDto = userPlansHistoryRepository.findByUserAndIsActiveTrue(user)
					.orElseThrow(() -> new RuntimeException("User Active Plan not found"));
			p.setUserPlanHist(historyResponseDto);
			p.setPaidamount(userPlanEntity.getTotalPrice().floatValue());
			p.setAmount(userPlanEntity.getTotalPrice().floatValue());
			p.setOdId(plan.getId() + "");
		} else if (userUpgradedModule != null && moduleEntity != null) {
			p.setUserPlanHist(null);
			p.setPaidamount(userUpgradedModule.getPayAmount().floatValue());
			p.setAmount(userUpgradedModule.getPayAmount().floatValue());
			p.setOdId(moduleEntity.getId() + "");
			p.setUserUpgradedModuleId(userUpgradedModule.getId());
		} else if (userNotification != null && moduleEntity != null) {
			p.setUserPlanHist(null);
			p.setPaidamount(userNotification.getPayAmount().floatValue());
			p.setAmount(userNotification.getPayAmount().floatValue());
			p.setOdId(moduleEntity.getId() + "");
			p.setUserNotificationId(userNotification.getId());
		} else if (userAiTemplate != null && aiTemplate != null) {
			p.setUserPlanHist(null);
			p.setPaidamount(userAiTemplate.getPayAmount().floatValue());
			p.setAmount(userAiTemplate.getPayAmount().floatValue());
			p.setOdId(aiTemplate.getId() + "");
			p.setUserNotificationId(userAiTemplate.getId());
		}

		p.setPayid(request.getPayid());
		p.setPaysignature(request.getPaysignature());
		p.setPaymentresponse(request.getPaymentresponse());
		p.setInternalorderid(request.getPaymentorderid());
		p.setSurcharge(0.0f);
		p.setUser(user);
		p.setPaymentdone(true);
		p.setPaymentType("Online");
		p = paymentInfoRepository.save(p);

		return p;

	}

	@Override
	public PaymentOrderDto getPaymentInfo(Long paymentId) {
		try {
			Optional<PaymentInfo> p = paymentInfoRepository.findById(paymentId);
			if (!p.isPresent()) {
				return null;
			}
			return paymentInfoRepository.getPaymentResponse(paymentId);
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return new PaymentOrderDto(false);
		}

	}

	@Override
	public List<PaymentOrderDto> getAllPaymentInfo() {
		try {
			List<PaymentOrderDto> p = paymentInfoRepository.getAllPaymentDetails();
			return p;
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return new ArrayList<>();
		}
	}

}
