package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionStaffTaskEntity;
import com.crmportal.entity.EventMasterEntity;

@Repository
public interface EventFunctionStaffTaskRepository extends JpaRepository<EventFunctionStaffTaskEntity, Long> {

	List<EventFunctionStaffTaskEntity> findByAssignmentIdAndIsDeleteFalse(Long id);

	List<EventFunctionStaffTaskEntity> findByAssignmentIdInAndIsDeleteFalse(List<Long> assignmentIds);

	void deleteAllByAssignmentId(Long id);

}
