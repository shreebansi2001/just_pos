package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserOfferEntity;

@Repository
public interface UserOfferRepository extends JpaRepository<UserOfferEntity, Long> {

	boolean existsByIdAndIsDeleteFalse(Long id);

	Optional<UserOfferEntity> findByIdAndIsDeleteFalse(Long id);

	Optional<UserOfferEntity> findByIdAndIsDeleteFalseAndIsActiveTrue(Long id);

	List<UserOfferEntity> findAllByUserIdAndIsDeleteFalseAndIsActiveTrue(Long id);

}
