package com.crmportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crmportal.entity.UserOtpEntity;

public interface UserOtpRepository extends JpaRepository<UserOtpEntity, Long> {

	@Modifying
    @Query("DELETE FROM UserOtpEntity u WHERE u.email = :email AND u.isUsed = TRUE")
    void deleteByEmailAndIsUsedTrue(String email);
    
    @Modifying
    @Query("DELETE FROM UserOtpEntity u WHERE u.email = :email AND u.otp = :otp")
	void deleteByEmailAndOtp(@Param("email") String email, String otp);

	Optional<UserOtpEntity> findByEmailAndOtpAndIsUsedFalse(String emailId, String otp);

}
