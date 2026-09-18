package com.crmportal.service;

import java.util.List;

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
import com.crmportal.response.dto.PaymentOrderDto;
import com.crmportal.response.dto.PaymentResponseDto;

@Service
public interface PaymentInfoService {

	PaymentOrderDto getPaymentInfo(Long paymentId);

	PaymentInfo savePaymentResponse(PaymentResponseDto request, UserMasterEntity user, PlansEntity plan,
			UserPlansHistoryEntity entity, UpgradedModuleEntity moduleEntity, UserUpgradedModuleEntity userUpgraded,
			UserNotificationConfigEntity userNotification, UserAssigneAITemplateEntity userAiTemplate,
			AITemplateEntity aiTemplate);

	List<PaymentOrderDto> getAllPaymentInfo();

}
