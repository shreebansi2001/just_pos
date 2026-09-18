package com.crmportal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserRightsMasterEntity;

@Repository
public interface UserRightsMasterRepository extends JpaRepository<UserRightsMasterEntity, Long> {

	List<UserRightsMasterEntity> findAllByRoleid(Long roleid);

	@Modifying
	@Transactional
    void deleteAllByRoleid(Long roleid);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM UserRightsMasterEntity u WHERE u.pageid = :pageId")
    void deleteAllByPageid(@Param("pageId") Long pageId);
}
