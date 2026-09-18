package com.crmportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserAboutUsEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface UserAboutUsRepository extends JpaRepository<UserAboutUsEntity, Long>{

	void deleteByUser(UserMasterEntity user);

	UserAboutUsEntity findByUser(UserMasterEntity user);

}
