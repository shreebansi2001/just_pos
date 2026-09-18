package com.crmportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crmportal.entity.RefundDetailsEntity;
import com.crmportal.repository.RefundDetailsRepository;
import com.crmportal.service.RefundDetailsService;

@Service
public class RefundDetailsServiceImpl implements RefundDetailsService {

	@Autowired
	RefundDetailsRepository refundDetailsRepository;

	@Override
	public Boolean deleteRefundDetails(Long id) {
		Boolean isSuccess = false;
		
		RefundDetailsEntity refundDetailsEntity = refundDetailsRepository.findByIdAndIsDeleteFalse(id);
		if(refundDetailsEntity == null) {
			return isSuccess;
		}
		refundDetailsEntity.setIsDelete(false);
		refundDetailsRepository.save(refundDetailsEntity);
		isSuccess = false;
		
		return null;
	}

}
