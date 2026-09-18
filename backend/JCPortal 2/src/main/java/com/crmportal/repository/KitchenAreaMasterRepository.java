package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.KitchenAreaMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface KitchenAreaMasterRepository extends JpaRepository<KitchenAreaMasterEntity, Long>{
	    Optional<KitchenAreaMasterEntity> findByIdAndIsDeleteFalse(Long id);
	    List<KitchenAreaMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);
	    List<KitchenAreaMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String name, UserMasterEntity user);
	    Optional<KitchenAreaMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String name, UserMasterEntity user);

}
