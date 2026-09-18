package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserGodownEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface UserGodownRepository extends JpaRepository<UserGodownEntity, Long> {

	List<UserGodownEntity> findByUserIdAndIsDeleteFalse(Long userId);

	Optional<UserGodownEntity> findByIdAndIsDeleteFalse(Long id);

}
