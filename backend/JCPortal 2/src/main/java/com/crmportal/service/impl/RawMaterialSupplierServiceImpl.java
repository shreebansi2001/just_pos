package com.crmportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.RawMaterialSupplierEntity;
import com.crmportal.mapper.RawMaterialSupplierMapper;
import com.crmportal.repository.RawMaterialSupplierRepository;
import com.crmportal.service.RawMaterialSupplierService;

@Service
public class RawMaterialSupplierServiceImpl implements RawMaterialSupplierService {

	@Autowired
	RawMaterialSupplierMapper rawMaterialSupplierMapper;
	
	@Autowired
	RawMaterialSupplierRepository rawMaterialSupplierRepository;
	
	@Override
	public Boolean deleteRawMaterialSupplierById(Long id) {

		if(rawMaterialSupplierRepository.existsByIdAndIsDeleteFalse(id)) {
			RawMaterialSupplierEntity rawMaterialSupplierEntity = rawMaterialSupplierRepository.findByIdAndIsDeleteFalse(id);
			rawMaterialSupplierEntity.setIsDelete(true);
			rawMaterialSupplierRepository.save(rawMaterialSupplierEntity);
			return true;
		}else {
			return false;
		}
	}

}
