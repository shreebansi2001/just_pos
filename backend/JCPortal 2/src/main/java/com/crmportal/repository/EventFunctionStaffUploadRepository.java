package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionStaffUploadEntity;
import com.crmportal.entity.EventMasterEntity;

@Repository
public interface EventFunctionStaffUploadRepository extends JpaRepository<EventFunctionStaffUploadEntity, Long> {

	List<EventFunctionStaffUploadEntity> findByAssignmentId(Long id);

	List<EventFunctionStaffUploadEntity> findByAssignmentIdIn(List<Long> assignmentIds);

	void deleteAllByAssignmentId(Long assignmentId);

}
