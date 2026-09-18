package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ManagerTaskMasterEntity;
import com.crmportal.enums.TaskType;

@Repository
public interface ManagerTaskMasterRepository extends JpaRepository<ManagerTaskMasterEntity, Long> {

	Optional<ManagerTaskMasterEntity> findByIdAndIsDeleteFalse(Long id);

	List<ManagerTaskMasterEntity> findAllByUserIdAndIsDeleteFalse(Long userId);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<ManagerTaskMasterEntity> findAllByUserIdAndIsDeleteFalseAndType(Long userId, TaskType taskType);

}
