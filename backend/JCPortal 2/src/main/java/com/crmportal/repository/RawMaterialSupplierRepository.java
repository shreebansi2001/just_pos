package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.RawMaterialSupplierEntity;

@Repository
public interface RawMaterialSupplierRepository extends JpaRepository<RawMaterialSupplierEntity, Long>{

	RawMaterialSupplierEntity findByIdAndIsDeleteFalse(Long id);

	List<RawMaterialSupplierEntity> findAllByRawMaterialAndIsDeleteFalse(RawMaterialMasterEntity entity);

	boolean existsByIdAndIsDeleteFalse(Long id);

}
